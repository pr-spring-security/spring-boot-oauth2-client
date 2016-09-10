package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/personInfo", method = RequestMethod.GET)
    private Map<String, Object> getPersonInfo(@RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            String phone = getPhoneByAccessToken(access_token);
            AppUser appUser = appUserRepository.findByPhone(phone);
            PersonInfo personInfo = new PersonInfo();
            personInfo.setAppUser(appUser);

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
    @RequestMapping(value = "/{username}",method = RequestMethod.GET)
    private Map<String, Object> get(@PathVariable("username")String username){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, appUserRepository.findByUsername(username));
        }catch (DataRetrievalFailureException e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, e.getMessage());
//            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }


    @ApiOperation(value = "添加用户信息")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(AppUser appUser){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        try{
            appUserRepository.insert(appUser);
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "");
//            responseMap.put(ServerContext.DATA, "");
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE, 409);
            responseMap.put(ServerContext.MSG, e.getMessage());
//            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    @ApiOperation(value = "修改用户信息", notes = "修改成功，在data里返回了修改后的用户信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    private Map<String, Object> patch(@PathVariable("id")String id, AppUser appUser){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        try{
            appUser.setId(id);
            appUserRepository.save(appUser);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            dataMap.put(ServerContext.USER_INFO, appUserRepository.findOne(id));
            responseMap.put(ServerContext.DATA, dataMap);
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE, 400);
            responseMap.put(ServerContext.MSG, e.getMessage());
//            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
