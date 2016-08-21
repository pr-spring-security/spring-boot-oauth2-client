package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/8/17.
 */
@Document(collection = "app_news")
public class AppNews {
    @Id
    private String id;

    private String editor;

    @Field("last_edit_time")
    private Long lastEditTime;

    private String title;

    @Field("pic_desc")
    private String picDesc;

    private String content;

    @Field("point_praise")
    private Integer pointPraise;

    @Field("share")
    private Integer share;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public Long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicDesc() {
        return picDesc;
    }

    public void setPicDesc(String picDesc) {
        this.picDesc = picDesc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPointPraise() {
        return pointPraise;
    }

    public void setPointPraise(Integer pointPraise) {
        this.pointPraise = pointPraise;
    }

    public Integer getShare() {
        return share;
    }

    public void setShare(Integer share) {
        this.share = share;
    }
}
