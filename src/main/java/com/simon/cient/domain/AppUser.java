package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/8/13.
 */
@Document(collection = "app_user")
public class AppUser {
    @Id
    private String id;
    @Indexed
    private String username;
    @Indexed
    private String phone;
    private Boolean sex;
    private Integer age;
    private String birth;

    @Field("person_brief")
    private String personBrief;

    @Field("head_photo")
    private String headPhoto;

    @Field("visit_card")
    private String visitCard;

    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPersonBrief() {
        return personBrief;
    }

    public void setPersonBrief(String personBrief) {
        this.personBrief = personBrief;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getVisitCard() {
        return visitCard;
    }

    public void setVisitCard(String visitCard) {
        this.visitCard = visitCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
