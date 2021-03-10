package com.generate.common.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultWrapper<T> implements Serializable {
    private boolean success = true;
    private int errorCode;
    private String errorMsg;
    private T data;

    public ResultWrapper() {
    }

    public ResultWrapper(boolean success) {
        this.success = success;
    }

    public ResultWrapper(int errorCode) {
        this.errorCode = errorCode;
    }

    public ResultWrapper(T data) {
        this.data = data;
    }

    public ResultWrapper(int errorCode, String errorMsg) {
        this.success = false;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    public ResultWrapper(boolean success, int errorCode, String errorMsg) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
    public ResultWrapper(int errorCode, String errorMsg, T data) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public ResultWrapper(boolean success, int errorCode, String errorMsg, T data) {
        this.success = success;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }
}
