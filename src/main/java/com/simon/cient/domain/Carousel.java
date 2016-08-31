package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/8/31.
 */
@Document(collection = "carousel")
public class Carousel {
    @Id
    private String id;

    @Field("img_desc")
    private String imgDesc;

    private String content;

    @Field("last_edit_time")
    private Long lastEditTime;

    private Boolean status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgDesc() {
        return imgDesc;
    }

    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
