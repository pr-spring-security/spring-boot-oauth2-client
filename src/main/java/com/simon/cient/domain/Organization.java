package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/9/13.
 */
@Document(collection = "organization")
public class Organization {
    @Id
    private String id;

    @Indexed(unique = true)
    @Field("org_name")
    private String orgName;

    @Field("establish_day")
    private String establishDay;

    @Field("work_area")
    private String workArea;

    @Field("org_scale")
    private String orgScale;

    private String icon;

    private String address;

    @Field("mobile_phone")
    private String mobilePhone;

    @Field("fixed_phone")
    private String fixedPhone;

    @Field("click_volume")
    private Integer clickVolume;

    private String intro;

    @Field("last_edit_time")
    private Long lastEditTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getEstablishDay() {
        return establishDay;
    }

    public void setEstablishDay(String establishDay) {
        this.establishDay = establishDay;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public String getOrgScale() {
        return orgScale;
    }

    public void setOrgScale(String orgScale) {
        this.orgScale = orgScale;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFixedPhone() {
        return fixedPhone;
    }

    public void setFixedPhone(String fixedPhone) {
        this.fixedPhone = fixedPhone;
    }

    public Integer getClickVolume() {
        return clickVolume;
    }

    public void setClickVolume(Integer clickVolume) {
        this.clickVolume = clickVolume;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public Long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
