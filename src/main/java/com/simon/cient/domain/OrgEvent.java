package com.simon.cient.domain;

import io.swagger.models.auth.In;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/8/30.
 */
@Document(collection = "event")
public class OrgEvent {
    @Id
    private String id;
    private String publisher;

    @Field("publish_time")
    private Long publishTime;

    private String theme;
    private String place;

    @Field(value = "begin_time")
    private Long beginTime;

    @Field(value = "end_time")
    private Long endTime;

    private Long deadline;

    private String content;

    private String poster;

    @Field("sign_up_count")
    private Integer signUpCount;

    @Field("sign_in_count")
    private Integer signInCount;

    @Field("sign_out_count")
    private Integer signOutCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }


    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Long beginTime) {
        this.beginTime = beginTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getDeadline() {
        return deadline;
    }

    public void setDeadline(Long deadline) {
        this.deadline = deadline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public Integer getSignUpCount() {
        return signUpCount;
    }

    public void setSignUpCount(Integer signUpCount) {
        this.signUpCount = signUpCount;
    }

    public Integer getSignInCount() {
        return signInCount;
    }

    public void setSignInCount(Integer signInCount) {
        this.signInCount = signInCount;
    }

    public Integer getSignOutCount() {
        return signOutCount;
    }

    public void setSignOutCount(Integer signOutCount) {
        this.signOutCount = signOutCount;
    }
}
