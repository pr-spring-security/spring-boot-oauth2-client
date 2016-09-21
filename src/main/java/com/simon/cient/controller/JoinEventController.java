package com.simon.cient.controller;

import com.simon.cient.domain.*;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by simon on 2016/9/8.
 */
@Api(value = "参加活动", description = "参加活动")
@RequestMapping("/api/joinEvents")
@RestController
public class JoinEventController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private JoinEventRepository joinEventRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private OrgEventRepository orgEventRepository;

    @ApiOperation(value = "报名活动", notes = "status不需要传；status的3个状态：1，已报名；2，进行中；3，已完成")
    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String eventId, @RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser appUser = appUserRepository.findByPhone(phone);

        JoinEvent joinEventFind = joinEventRepository.findByEventIdAndPhone(eventId, appUser.getPhone());
        OrgEvent orgEvent = orgEventRepository.findById(eventId);

        if (null!=joinEventFind){
            responseMap.put(ServerContext.STATUS_CODE, 409);
            responseMap.put(ServerContext.MSG, "您已报名");
        }else{
            JoinEvent joinEvent = new JoinEvent();
            joinEvent.setEventId(eventId);
            joinEvent.setPhone(phone);
            joinEvent.setUsername(appUser.getUsername());
            joinEvent.setSignUpTime(System.currentTimeMillis());
            joinEvent.setStatus(1);//1，已报名

            orgEvent.setSignUpCount(orgEvent.getSignUpCount()+1);

            try{
                joinEventRepository.insert(joinEvent);
                orgEventRepository.save(orgEvent);
                responseMap.put(ServerContext.STATUS_CODE, 201);
                responseMap.put(ServerContext.MSG, "报名成功");
            }catch (Exception e){
                responseMap.put(ServerContext.STATUS_CODE, 404);
                responseMap.put(ServerContext.MSG, "报名失败");
                responseMap.put(ServerContext.DEV_MSG, e.getMessage());
            }
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
            joinEvent.setStatus(2);//2，进行中

            orgEvent.setSignInCount(orgEvent.getSignInCount()+1);

            joinEventRepository.save(joinEvent);
            orgEventRepository.save(orgEvent);

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
            joinEvent.setStatus(3);//3，已完成

            orgEvent.setSignOutCount(orgEvent.getSignOutCount()+1);

            joinEventRepository.save(joinEvent);
            orgEventRepository.save(orgEvent);

            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "签退成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "签退失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "取消参加")
    @RequestMapping(value = "/cancel", method = RequestMethod.PATCH)
    private Map<String, Object> cancel(@RequestParam String eventId, @RequestParam String access_token){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        OrgEvent orgEvent = orgEventRepository.findById(eventId);
        String phone = getPhoneByAccessToken(access_token);

        try {
            JoinEvent joinEvent = joinEventRepository.findByEventIdAndPhone(eventId, phone);

            orgEvent.setSignUpCount(orgEvent.getSignUpCount()-1);

            joinEventRepository.delete(joinEvent);
            orgEventRepository.save(orgEvent);

            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "取消参加成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "取消参加失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value="获取已报名、进行中、已完成的活动", notes = "status有3个状态：1，已报名；2，进行中；3，已完成")
    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET)
    private Map<String, Object> getSignedUp(@PathVariable("status") Integer status, @RequestParam String access_token, @RequestParam Integer limit, @RequestParam Integer offset){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);

        try {
            List<JoinEvent> joinEventList = joinEventRepository.findByPhoneAndStatus(phone, status, new PageRequest(offset/limit, limit, new Sort(Sort.Direction.ASC, "eventId")));
            for (JoinEvent joinEvent : joinEventList){
                joinEvent.setOrgEvent(orgEventRepository.findById(joinEvent.getEventId()));
            }
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "获取活动成功");
            responseMap.put(ServerContext.DATA, joinEventList);
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "获取活动失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
