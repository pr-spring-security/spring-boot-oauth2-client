package com.simon.cient.controller;

import com.simon.cient.domain.jdbc.OauthUser;
import com.simon.cient.util.ServerContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by simon on 2016/8/16.
 */
@Api
@RestController
@RequestMapping("/api/oauthUser")
public class OauthUserController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @ApiOperation(value = "获取用户信息", notes = "this is notes", httpMethod = "GET")
    @RequestMapping(method = RequestMethod.GET)
    private Map<String, Object> get(@RequestParam String username) {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            OauthUser oauthUser = findOauthUserByUsername(username);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "");
            responseMap.put(ServerContext.DATA, oauthUser);
        } catch (DataAccessException e) {
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "there is no user named " + username);
            responseMap.put(ServerContext.DATA, "");
        }
        return responseMap;
    }

    @ApiOperation(value = "用户注册", notes = "this is notes", httpMethod = "POST")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        //判断username是否存在
        try {
            int result1 = jdbcTemplate.update("INSERT INTO users (username,password,enabled) VALUES (?, ?, ?)",
                    username, password, true);
            int result2 = jdbcTemplate.update("INSERT INTO authorities (username, authority) VALUES (?, ?)",
                    username, "ROLE_APP");
            if (result1 > 0 & result2 > 0) {
                responseMap.put(ServerContext.STATUS_CODE, 201);//201 (Created)
                responseMap.put(ServerContext.MSG, "register success");
                responseMap.put(ServerContext.DATA, "");
            }
        } catch (DataAccessException e) {
            responseMap.put(ServerContext.STATUS_CODE, 409);
            responseMap.put(ServerContext.MSG, "user exists");
            responseMap.put(ServerContext.DATA, "");
        }

        return responseMap;
    }

    public OauthUser findOauthUserByUsername(String username) {
        return jdbcTemplate.queryForObject(
                "SELECT username,password,enabled FROM users where username=?",
                new Object[]{username}, new RowMapper<OauthUser>() {
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
