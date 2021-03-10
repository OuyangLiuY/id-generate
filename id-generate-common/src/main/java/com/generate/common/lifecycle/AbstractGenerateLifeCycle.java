package com.generate.common.lifecycle;

import com.generate.common.enums.GenerateState;
import com.generate.common.exception.BizException;

public abstract class AbstractGenerateLifeCycle implements GenerateLifeCycle{

    protected GenerateState state = GenerateState.DEFAULT_STATE;
    public static final String MSG_FORMAT = "前一个状态必须为: %s,当前为: %s";
    @Override
    public void init() {
        GenerateState state = getState();
        if(state != GenerateState.DEFAULT_STATE){
            throw  new BizException(String.format(MSG_FORMAT,GenerateState.DEFAULT_STATE.getValue(),state.getValue()));
        }
        doInit();
        this.state = GenerateState.INIT_STATE;
    }

    @Override
    public void stop() {
        GenerateState state = getState();
        if(state != GenerateState.START_STATE){
            throw  new BizException(String.format(MSG_FORMAT,GenerateState.START_STATE.getValue(),state.getValue()));
        }
        doStop();
        this.state = GenerateState.STOP_STATE;
    }

    @Override
    public void start() {
        GenerateState state = getState();
        if(state != GenerateState.INIT_STATE){
            throw  new BizException(String.format(MSG_FORMAT,GenerateState.INIT_STATE.getValue(),state.getValue()));
        }
        doStart();
        this.state = GenerateState.START_STATE;
    }

    @Override
    public GenerateState getState() {
        return this.state;
    }

    protected abstract void doInit();
    protected abstract void doStart();
    protected abstract void doStop();
}
