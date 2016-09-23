package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.domain.MobileDevice;
import com.simon.cient.domain.MobileDeviceRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/9/23.
 */
@Api(description = "移动设备")
@RestController
@RequestMapping("/api/mobileDevices")
public class MobileDeviceController {
    @Autowired
    private MobileDeviceRepository mobileDeviceRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "绑定该设备到账号", notes = "MobileDevice不要传id和userId；只有用户当前登录的设备要和极光推送做长连接")
    @RequestMapping(method = RequestMethod.POST)
    @ApiImplicitParams({
        @ApiImplicitParam(name = "access_token", value = "access token", required = true, dataType = "string")
    })
    private Map<String, Object> bindDeviceWithAccount(@RequestParam String access_token, @RequestBody MobileDevice mobileDevice){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser currentUser = appUserRepository.findByPhone(phone);

        try{
            MobileDevice mobileDeviceOld = mobileDeviceRepository.findByUserId(currentUser.getId());
            if (null!=mobileDeviceOld){
                mobileDevice.setId(mobileDeviceOld.getId());
            }

            mobileDevice.setUserId(currentUser.getId());
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "绑定设备成功");
            responseMap.put(ServerContext.DATA, mobileDeviceRepository.save(mobileDevice));
        }catch (Exception e){
            logger.error("绑定设备失败", e);
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "绑定设备失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "获取绑定设备信息")
    @RequestMapping(value = "", method = RequestMethod.GET)
    private Map<String, Object> getBindInfo(@RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser currentUser = appUserRepository.findByPhone(phone);

        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取设备绑定信息成功");
            responseMap.put(ServerContext.DATA, mobileDeviceRepository.findByUserId(currentUser.getId()));
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "该账号未绑定任何设备");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    /*@ApiOperation(value = "绑定新设备到账号")
    @RequestMapping(method = RequestMethod.PATCH)
    private Map<String, Object> bindNewDevice(){

    }*/

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
