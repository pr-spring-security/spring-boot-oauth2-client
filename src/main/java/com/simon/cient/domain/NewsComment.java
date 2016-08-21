package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/8/17.
 */
@Document(collection = "news_comment")
public class NewsComment {
    @Id
    private String id;

    @Field("news_id")
    private String newsId;

    private String username;

    private String content;

    @Field("comment_time")
    private Long commentTime;

    @Field("point_praise")
    private Integer pointPraise;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Long commentTime) {
        this.commentTime = commentTime;
    }

    public Integer getPointPraise() {
        return pointPraise;
    }

    public void setPointPraise(Integer pointPraise) {
        this.pointPraise = pointPraise;
    }
}
