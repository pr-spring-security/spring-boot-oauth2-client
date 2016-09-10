package com.simon.cient.domain;

/**
 * Created by simon on 2016/9/11.
 */
public class PersonInfo {
    private AppUser appUser;
    private Integer signUpCount;
    private Integer joinCount;
    private Integer volHour;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Integer getSignUpCount() {
        return signUpCount;
    }

    public void setSignUpCount(Integer signUpCount) {
        this.signUpCount = signUpCount;
    }

    public Integer getJoinCount() {
        return joinCount;
    }

    public void setJoinCount(Integer joinCount) {
        this.joinCount = joinCount;
    }

    public Integer getVolHour() {
        return volHour;
    }

    public void setVolHour(Integer volHour) {
        this.volHour = volHour;
    }
}
