package com.simon.cient.util;

import java.util.LinkedHashMap;

/**
 * Created by simon on 2016/8/18.
 */
public class ResponseMap{
    private LinkedHashMap<String, Object> resultMap;
    private Integer resultCode;
    private String message;
    private Object data;

    {
        resultMap = new LinkedHashMap<>();
    }

    public ResponseMap() {
    }

    public ResponseMap(Integer resultCode, String message, Object data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    public LinkedHashMap<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(LinkedHashMap<String, Object> resultMap) {
        this.resultMap = resultMap;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
