package com.generate.common.lifecycle;


import com.generate.common.enums.GenerateState;

public interface GenerateLifeCycle {

    void init();

    String getName();

    void stop();

    void start();

    GenerateState getState();
}
