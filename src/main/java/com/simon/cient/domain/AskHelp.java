package com.simon.cient.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by simon on 2016/9/11.
 */
@Document(collection = "ask_help")
public class AskHelp {
    @Id
    private String id;

    @Field("publisher_id")
    private String publisherId;

    @DBRef
    @Field("publisher")
    private AppUser publisher;

    @Field("publish_time")
    private Long publishTime;

    private String content;

    @Field("auditor_id")
    private String auditorId;

    @DBRef
    private AppUser auditor;//审核人

    @Field("audit_time")
    private Long auditTime;//审核时间

    @Field("audit_result")
    private Boolean auditResult;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AppUser getPublisher() {
        return publisher;
    }

    public void setPublisher(AppUser publisher) {
        this.publisher = publisher;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AppUser getAuditor() {
        return auditor;
    }

    public void setAuditor(AppUser auditor) {
        this.auditor = auditor;
    }

    public Long getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Long auditTime) {
        this.auditTime = auditTime;
    }

    public Boolean getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(Boolean auditResult) {
        this.auditResult = auditResult;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getAuditorId() {
        return auditorId;
    }

    public void setAuditorId(String auditorId) {
        this.auditorId = auditorId;
    }
}
