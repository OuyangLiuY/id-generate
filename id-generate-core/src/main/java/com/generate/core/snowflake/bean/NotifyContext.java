package com.generate.core.snowflake.bean;

import lombok.Data;

@Data
public class NotifyContext<T> {
    private String msg;
    private int code;
    private T data;
}
