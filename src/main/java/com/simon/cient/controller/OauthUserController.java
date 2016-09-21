package com.simon.cient.controller;

import com.simon.cient.domain.*;
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
@Api(value="登录注册", description = "登录注册")
@RestController
@RequestMapping("/api/oauthUser")
public class OauthUserController {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    VeriCodeRepository veriCodeRepository;

    @ApiOperation(value = "登录", notes = "this is notes", httpMethod = "GET")
    @RequestMapping(value = "/{phone}/{password}", method = RequestMethod.GET)
    private Map<String, Object> get(@PathVariable("phone")String phone,
                                    @PathVariable("password")String password) {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try {
            OauthUser oauthUser = findOauthUserByUsername(phone,password);
            if (null!=oauthUser){
                responseMap.put(ServerContext.STATUS_CODE, 200);
                responseMap.put(ServerContext.MSG, "登录成功");
                responseMap.put(ServerContext.DATA, appUserRepository.findByPhone(phone));
            }else{
                responseMap.put(ServerContext.STATUS_CODE, 404);
                responseMap.put(ServerContext.MSG, "用户名或者密码错误");
            }

        } catch (Exception e) {
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "用户名或者密码错误");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }
        return responseMap;
    }

    @ApiOperation(value = "注册", notes = "注册成功返回appUser对象，包含自动生成的username", httpMethod = "POST")
    @RequestMapping(method = RequestMethod.POST)
    private Map<String, Object> post(@RequestParam Integer code, @RequestParam String phone, @RequestParam String password) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        VeriCode veriCode = veriCodeRepository.findByPhoneAndCode(phone, code);
        if (null!=veriCode){
            //判断username是否存在
            try {
                int result1 = jdbcTemplate.update("INSERT INTO users (username,password,enabled) VALUES (?, ?, ?)",
                        phone, password, true);
                int result2 = jdbcTemplate.update("INSERT INTO authorities (username, authority) VALUES (?, ?)",
                        phone, "ROLE_APP");

                AppUser appUser = new AppUser();
                //String name = "sc"+Long.toString(System.currentTimeMillis()/1000, 26);
                String name = "starchild"+phone.substring(phone.length()-4);
                appUser.setUsername(name);
                appUser.setPhone(phone);

                if (result1 > 0 && result2 > 0 && null!=appUserRepository.insert(appUser)) {
                    responseMap.put(ServerContext.STATUS_CODE, 201);//201 (Created)
                    responseMap.put(ServerContext.MSG, "注册成功");
                    responseMap.put(ServerContext.DATA, appUserRepository.findByUsername(name));
                }
            } catch (DataIntegrityViolationException e) {
                responseMap.put(ServerContext.STATUS_CODE, 409);
                responseMap.put(ServerContext.MSG, "用户名已存在");
                responseMap.put(ServerContext.DEV_MSG, e.getMessage());
            }
        }else{
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "验证码错误或者过期");
        }

        return responseMap;
    }

    @ApiOperation(value = "更新密码（使用旧密码）", notes = "目前密码是明文存储，正式发布前需要做加密")
    @RequestMapping(value = "/updatePassword/{oldPassword}/{newPassword}", method = RequestMethod.PATCH)
    private Map<String, Object> updatePassword(@RequestParam String access_token, @PathVariable String oldPassword,@PathVariable String newPassword){
        Map<String, Object> responseMap = new LinkedHashMap<>();
        String phone = getPhoneByAccessToken(access_token);

        try{
            this.jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ? AND password = ?", newPassword, phone, oldPassword);
            responseMap.put(ServerContext.STATUS_CODE, 200);
            responseMap.put(ServerContext.MSG, "更新密码成功");
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "更新密码失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
        }

        return responseMap;
    }

    @ApiOperation(value = "更新密码（使用手机验证码）",notes = "此处还需要传一次验证码，防止有人破解app后知道更新密码api，直接更新其他用户密码")
    @RequestMapping(value = "/updatePwdWithoutOldPwd", method = RequestMethod.PATCH)
    private Map<String, Object> updatePwdWithoutOldPwd(@RequestParam String phone, @RequestParam Integer code, @RequestParam String newPwd){
        Map<String, Object> responseMap = new LinkedHashMap<>();

        try{
            VeriCode veriCode = veriCodeRepository.findByPhoneAndCode(phone, code);
            if (null!=veriCode){
                this.jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?", newPwd, phone);
                responseMap.put(ServerContext.STATUS_CODE, 200);
                responseMap.put(ServerContext.MSG, "更新密码成功");
            }else{
                responseMap.put(ServerContext.STATUS_CODE, 404);
                responseMap.put(ServerContext.MSG, "验证码过期，更新密码失败");
            }
        }catch (Exception e){
            responseMap.put(ServerContext.STATUS_CODE, 404);
            responseMap.put(ServerContext.MSG, "更新密码失败");
            responseMap.put(ServerContext.DEV_MSG, e.getMessage());
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

    private String getPhoneByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE CONCAT('%', ?)", new Object[]{access_token}, String.class);
    }
}
