package com.simon.cient;

import com.simon.cient.domain.AppUser;
import com.simon.cient.domain.AppUserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by simon on 2016/10/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AppUserTests {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    public void contextLoads() {
    }

    public void getTest(){
        AppUser appUser = new AppUser();
        appUser.setUsername("starchild6745");
        appUser.setPhone("18550046745");
        System.out.println(appUserRepository.save(appUser).toString());
    }
}
