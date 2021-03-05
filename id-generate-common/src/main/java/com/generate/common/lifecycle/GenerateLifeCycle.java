package com.generate.common.lifecycle;


public interface GenerateLifeCycle {

    void init();

    String getName();

    void stop();

    void start();

    enum GenerateState{
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
    }

    GenerateState getState();
}
