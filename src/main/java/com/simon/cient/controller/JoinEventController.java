package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/9/8.
 */
@Api("参加活动")
@RequestMapping("/api/joinEvents")
@RestController
public class JoinEventController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JoinEventRepository joinEventRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OrgEventRepository orgEventRepository;

    @ApiOperation(value = "报名活动", notes = "")
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String eventId, @RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser appUser = appUserRepository.findByPhone(phone);

        JoinEvent joinEvent = new JoinEvent();
        joinEvent.setEventId(eventId);
        joinEvent.setPhone(phone);
        joinEvent.setUsername(appUser.getUsername());
        joinEvent.setSignUpTime(System.currentTimeMillis());

        try{
            joinEventRepository.insert(joinEvent);
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "报名成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "报名失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "活动签到", notes = "签到失败的情况：未到签到时间，未报名")
    @RequestMapping(value = "/signIn", method = RequestMethod.PATCH)
    private Map<String, Object> signIn(@RequestParam String eventId, @RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        OrgEvent orgEvent = orgEventRepository.findById(eventId);
        String phone = getPhoneByAccessToken(access_token);

        try{
            JoinEvent joinEvent = joinEventRepository.findByEventIdAndPhone(eventId, phone);
            joinEvent.setSignInTime(System.currentTimeMillis());
            joinEventRepository.save(joinEvent);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "签到成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "签到失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "活动签退", notes = "签退失败的情况：未到签退时间")
    @RequestMapping(value = "/signOut", method = RequestMethod.PATCH)
    private Map<String, Object> signOut(@RequestParam String eventId, @RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        OrgEvent orgEvent = orgEventRepository.findById(eventId);
        String phone = getPhoneByAccessToken(access_token);

        try{
            JoinEvent joinEvent = joinEventRepository.findByEventIdAndPhone(eventId, phone);
            joinEvent.setSignOutTime(System.currentTimeMillis());
            joinEventRepository.save(joinEvent);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "签退成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "签退失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
