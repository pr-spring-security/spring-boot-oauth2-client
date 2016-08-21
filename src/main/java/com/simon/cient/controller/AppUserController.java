package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
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
            responseMap.put(ServerContext.DATA, "");
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
            responseMap.put(ServerContext.DATA, "");
        }catch (DataIntegrityViolationException e){
            responseMap.put(ServerContext.STATUS_CODE, 409);
            responseMap.put(ServerContext.MSG, e.getMessage());
            responseMap.put(ServerContext.DATA, "");
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
            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }
}
