package com.generate.common.exception;

import com.generate.common.base.ResultState;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LockException extends RuntimeException{

    private String msg;
    private int code;


    public LockException() {
    }

    public LockException(String message, Throwable cause) {
        super(cause);
        this.msg = message;
        this.code = ResultState.FAILED.getCode();
    }

    public LockException(ResultState state, Throwable cause) {
        super(cause);
        this.msg = state.getMsg();
        this.code = state.getCode();
    }
    public LockException(String msg) {
        this.msg = msg;
        this.code = ResultState.FAILED.getCode();
    }

    public LockException(Throwable cause) {
        super(cause);
    }

    public LockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

    }
}
