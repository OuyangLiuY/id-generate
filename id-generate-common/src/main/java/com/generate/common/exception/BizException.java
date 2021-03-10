package com.generate.common.exception;

import com.generate.common.base.ResultState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BizException extends RuntimeException{

    private String msg;
    private int code;


    public BizException() {
    }

    public BizException(String message,Throwable cause) {
        super(cause);
        this.msg = message;
        this.code = ResultState.FAILED.getCode();
    }

    public BizException(ResultState state, Throwable cause) {
        super(cause);
        this.msg = state.getMsg();
        this.code = state.getCode();
    }
    public BizException(String msg) {
        this.msg = msg;
        this.code = ResultState.FAILED.getCode();
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

    }
}
