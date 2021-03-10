package com.generate.common.enums;

public enum GenerateState {
    STOP_STATE("stop",3),
    START_STATE("start",2),
    INIT_STATE("init",1),
    DEFAULT_STATE("default",0);
    private final int code;
    private final String value;

    GenerateState(String value,int code){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
    public int getCode(){
        return this.code;
    }
}
