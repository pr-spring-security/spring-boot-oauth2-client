package com.simon.cient.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by simon on 2016/8/31.
 */
public class UserUtil {
    private static UserUtil userUtil;
    private UserUtil(){

    }
    public static synchronized UserUtil getInstance(){
        if (null==userUtil){
            userUtil = new UserUtil();
        }
        return userUtil;
    }
    @Autowired
    JdbcTemplate jdbcTemplate;

    public String getUsernameByAccessToken(String access_token){
        return jdbcTemplate.queryForObject("SELECT user_name FROM oauth_access_token" +
                " WHERE encode(token, 'escape') LIKE ?", new Object[]{access_token}, String.class);
    }
}
