package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2016/8/21.
 */
@Api(value="用户信息")
@RestController
@RequestMapping("/api/appUserInfo")
public class AppUserController {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private JoinEventRepository joinEventRepository;

    @Autowired
    private OrgEventRepository orgEventRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ResourceLoader resourceLoader;

    private static final String ROOT = "appUsers";

    @Autowired
    public AppUserController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @ApiOperation(value = "\"我的\"模块访问的接口")
    @RequestMapping(value = "/personInfo", method = RequestMethod.GET)
    private Map<String, Object> getPersonInfo(@RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            String phone = getPhoneByAccessToken(access_token);
            AppUser appUser = appUserRepository.findByPhone(phone);
            PersonInfo personInfo = new PersonInfo();
            personInfo.setAppUser(appUser);

            personInfo.setSignUpCount(joinEventRepository.countByPhone(phone));
            List<JoinEvent> joinEventList = joinEventRepository.getByPhoneAndStatus(phone, ServerContext.SIGN_OUT_STATUS);
            personInfo.setJoinCount(joinEventList.size());

            int volHour = 0;
            for(JoinEvent joinEvent : joinEventList){
                OrgEvent orgEvent = orgEventRepository.findById(joinEvent.getEventId());
                Long beginTime = orgEvent.getBeginTime();
                Long endTime = orgEvent.getEndTime();
                volHour+=(endTime-beginTime)/(1000*60&60);//java时间戳13位，计算到毫秒，这里是计算两个时间戳之间的小时差
            }
            personInfo.setVolHour(volHour);

            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取用户信息成功");
            responseMap.put(ServerContext.DATA, personInfo);

        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取用户信息失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value="获取用户信息")
    @RequestMapping(value = "",method = RequestMethod.GET)
    private Map<String, Object> get(@RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取用户信息成功");
            responseMap.put(ServerContext.DATA, appUserRepository.findByPhone(phone));
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取用户信息失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "更新头像")
    @RequestMapping(value = "/updateHeadPhoto", method = RequestMethod.PATCH)
    private Map<String, Object> updateHeadPhoto(@RequestParam String access_token, @RequestParam String photoBase64){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser appUser = appUserRepository.findByPhone(phone);
        String headPhotoUrl = appUser.getHeadPhoto();
        if (null==headPhotoUrl||"".equals(headPhotoUrl)){
            headPhotoUrl = ROOT+"/"+appUser.getPhone() + "/" + appUser.getPhone() + ".png";
            String headPhotoDir = ROOT + "/" + appUser.getPhone();
            try{
                if (!Files.exists(Paths.get(headPhotoUrl))){
                    if (!Files.exists(Paths.get(headPhotoDir))){
                        Files.createDirectories(Paths.get(headPhotoDir));
                    }
                    Files.createFile(Paths.get(headPhotoUrl));
                }
                Files.write(Paths.get(headPhotoUrl), photoBase64.getBytes());
                appUser.setHeadPhoto(headPhotoUrl);
                responseMap.put(ServerContext.STATUS_CODE, 200);
                responseMap.put(ServerContext.MSG, "更新头像成功");
                responseMap.put(ServerContext.DATA, appUserRepository.save(appUser));
            }catch (IOException e){
                responseMap.put(ServerContext.STATUS_CODE, 404);
                responseMap.put(ServerContext.MSG, "创建文件夹或者文件失败");
                responseMap.put(ServerContext.DEV_MSG, e.getMessage());
            }catch (Exception e){
                responseMap.put(ServerContext.STATUS_CODE, 500);
                responseMap.put(ServerContext.MSG, "未知错误");
                responseMap.put(ServerContext.DEV_MSG, e.getMessage());
            }
        }
        return responseMap;
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
