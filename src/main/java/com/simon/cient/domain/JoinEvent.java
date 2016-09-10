package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/9/7.
 */
@Document(collection = "join_event")
public class JoinEvent {
    @Id
    private String id;

    @Field("event_id")
    private String eventId;
    private String phone;
    private String username;

    @Field("sign_up_time")
    private Long signUpTime;//报名时间

    @Field("sign_in_time")
    private Long signInTime;//签到时间

    @Field("sign_out_time")
    private Long signOutTime;//签退时间

    @Field("status")
    private Integer status;//1，已报名；2，进行中；3，已完成

    @DBRef
    @Field(value = "org_event")
    private OrgEvent orgEvent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getSignUpTime() {
        return signUpTime;
    }

    public void setSignUpTime(Long signUpTime) {
        this.signUpTime = signUpTime;
    }

    public Long getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Long signInTime) {
        this.signInTime = signInTime;
    }

    public Long getSignOutTime() {
        return signOutTime;
    }

    public void setSignOutTime(Long signOutTime) {
        this.signOutTime = signOutTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public OrgEvent getOrgEvent() {
        return orgEvent;
    }

    public void setOrgEvent(OrgEvent orgEvent) {
        this.orgEvent = orgEvent;
    }
}
