package com.simon.cient.controller;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import com.simon.cient.domain.jdbc.OauthUser;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/16.
 */
@Api(value="登录注册")
@RestController
@RequestMapping("/api/oauthUser")
public class OauthUserController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AppUserRepository appUserRepository;

    @ApiOperation(value = "登录", notes = "this is notes", httpMethod = "GET")
    @RequestMapping(value = "/{phone}/{password}", method = RequestMethod.GET)
    private Map<String, Object> get(@PathVariable("phone")String phone,
                                    @PathVariable("password")String password) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> dataMap = new LinkedHashMap<>();

        try {
            OauthUser oauthUser = findOauthUserByUsername(phone,password);
            AppUser appUser = appUserRepository.findByPhone(phone);
            dataMap.put(ServerContext.USER_INFO, appUser);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, dataMap);
        } catch (DataRetrievalFailureException e) {
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "username or password is incorrect.");
//            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    @ApiOperation(value = "注册", notes = "注册成功返回appUser对象，包含自动生成的username", httpMethod = "POST")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String phone, @RequestParam String password) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        //判断username是否存在
        try {
            int result1 = jdbcTemplate.update("INSERT INTO users (username,password,enabled) VALUES (?, ?, ?)",
                    phone, password, true);
            int result2 = jdbcTemplate.update("INSERT INTO authorities (username, authority) VALUES (?, ?)",
                    phone, "ROLE_APP");

            AppUser appUser = new AppUser();
            String name = "sc"+Long.toString(System.currentTimeMillis()/1000, 26);
            appUser.setUsername(name);
            appUser.setPhone(phone);

            if (result1 > 0 && result2 > 0 && null!=appUserRepository.insert(appUser)) {
                responseMap.put(ServerContext.STATUS_CODE, 201);//201 (Created)
                responseMap.put(ServerContext.MSG, "register success");
                responseMap.put(ServerContext.DATA, appUserRepository.findByUsername(name));
            }
        } catch (DataIntegrityViolationException e) {
            responseMap.put(ServerContext.STATUS_CODE, 409);
            responseMap.put(ServerContext.MSG, "user exists");
//            responseMap.put(ServerContext.DATA, "");
        }

        return responseMap;
    }

    public OauthUser findOauthUserByUsername(String username, String password) {
        return jdbcTemplate.queryForObject(
                "SELECT username,password,enabled FROM users where username=? AND password=?",
                new Object[]{username, password}, new RowMapper<OauthUser>() {
                    @Override
                    public OauthUser mapRow(ResultSet resultSet, int i) throws SQLException {
                        OauthUser oauthUser = new OauthUser();
                        oauthUser.setUsername(resultSet.getString("username"));
                        oauthUser.setPassword(resultSet.getString("password"));
                        oauthUser.setEnable(resultSet.getBoolean("enabled"));
                        return oauthUser;
                    }
                });
    }
}
