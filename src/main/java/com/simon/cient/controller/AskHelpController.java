package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.domain.AskHelp;
import com.simon.cient.domain.AskHelpRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/9/11.
 */
@Api("请求帮助")
@RestController
@RequestMapping("/api/askHelps")
public class AskHelpController {
    @Autowired
    private AskHelpRepository askHelpRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @ApiOperation(value = "发布求助信息", notes = "")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String access_token, @RequestParam String content){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser publisher = appUserRepository.findByPhone(phone);

        AskHelp askHelp = new AskHelp();
        askHelp.setPublisherId(publisher.getId());
        askHelp.setPublisher(publisher);
        askHelp.setPublishTime(System.currentTimeMillis());
        askHelp.setContent(content);

        try{
            responseMap.put(ServerContext.STATUS_CODE, 201);
            responseMap.put(ServerContext.MSG, "发布成功");
            responseMap.put(ServerContext.DATA, askHelpRepository.insert(askHelp));
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "发布失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "审核", notes = "access_token来自审核人")
    private Map<String, Object> auditHelpInfo(@RequestParam String access_token, @RequestParam String askHelpId, @RequestParam Boolean auditResult){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);
        AppUser auditor = appUserRepository.findByPhone(phone);

        AskHelp askHelp = askHelpRepository.findById(askHelpId);

        AppUser publisher = appUserRepository.findById(askHelp.getPublisherId());

        try{
            askHelp.setAuditorId(auditor.getId());
            askHelp.setAuditor(auditor);
            askHelp.setAuditTime(System.currentTimeMillis());
            askHelp.setAuditResult(auditResult);

            askHelpRepository.save(askHelp);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "审核完成");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "审核信息有误，未审核成功");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
