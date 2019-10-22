package com.wext.common.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

    private static final long serialVersionUID = 7255335905681108209L;
    private boolean success;

    private String msg;

    private Object data;

    public BaseResponse(boolean success, String msg, Object data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public static BaseResponse successResponse(Object data) {
        return new BaseResponse(true, "success", data);
    }

    public static BaseResponse failResponse(String msg) {
        return new BaseResponse(false, msg, null);
    }
}
