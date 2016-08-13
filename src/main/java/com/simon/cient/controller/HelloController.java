package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

/**
 * Created by simon on 2016/8/13.
 */
@RestController
@RequestMapping("api")
public class HelloController {

    @Autowired
    private AppUserRepository appUserRepository;
    @RequestMapping("hello")
    public String get(){
        return "hello";
    }

    @RequestMapping("admin/hello")
    public String admin(){
        return "hello,admin";
    }

    //"#oauth2.throwOnError(#oauth2.hasScope('read') or (#oauth2.hasScope('other') and hasRole('ROLE_USER'))"
    //http://docs.spring.io/spring-security/oauth/apidocs/org/springframework/security/oauth2/provider/expression/OAuth2SecurityExpressionMethods.html
    @PreAuthorize("#oauth2.isUser()")
    //@Secured({"ROLE_ADMIN"})
    //@RolesAllowed({"ROLE_ADMIN"})
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value="user/{username}",method = RequestMethod.GET)
    public AppUser get(@PathVariable("username") String username){
        return appUserRepository.findByUsername(username);
    }

}
