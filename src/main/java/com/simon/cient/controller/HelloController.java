package com.simon.cient.controller;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.PlatformNotification;
import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static cn.jpush.api.push.model.notification.PlatformNotification.ALERT;

/**
 * Created by simon on 2016/8/13.
 */
@RestController
@RequestMapping("api/hello")
@Api
public class HelloController {

    @Autowired
    private AppUserRepository appUserRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "说hello", notes = "...")
    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String get() {
        return "hello";
    }

    @RequestMapping(value = "admin/hello", method = RequestMethod.GET)
    public String admin() {
        return "hello,admin";
    }

    @ApiOperation(value = "get user", notes = "...")
    //"#oauth2.throwOnError(#oauth2.hasScope('read') or (#oauth2.hasScope('other') and hasRole('ROLE_USER'))"
    //http://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/expression/OAuth2SecurityExpressionMethods.html
    @PreAuthorize("#oauth2.isUser()")
    //@Secured({"ROLE_ADMIN"})
    //@RolesAllowed({"ROLE_ADMIN"})
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "user/{username}", method = RequestMethod.GET)
    public AppUser get(@PathVariable("username") String username) {
        return appUserRepository.findByUsername(username);
    }

    @RequestMapping(value = "/jpush", method = RequestMethod.GET)
    private void jPushDemo(){
        JPushClient jpushClient = new JPushClient(ServerContext.JIGUANG_MASTER_SECRET, ServerContext.JIGUANG_APP_KEY);

        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_all_all_alert();

        try{
            PushResult result = jpushClient.sendPush(payload);
            System.out.println("Got result - " + result);

        }catch(APIConnectionException e){
            // Connection error, should retry later
            System.out.println("Connection error, should retry later");

        }catch(APIRequestException e){
            // Should review the error, and fix the request
            System.out.println("Should review the error, and fix the request");
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
        }
    }


    public static PushPayload buildPushObject_all_all_alert() {
        return PushPayload.alertAll(PlatformNotification.ALERT);//通知栏通知
//        return PushPayload.messageAll(PlatformNotification.ALERT);//app内通知

    }

    /*public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
                        .addAudienceTarget(AudienceTarget.alias("alias1", "alias2"))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }*/

}
