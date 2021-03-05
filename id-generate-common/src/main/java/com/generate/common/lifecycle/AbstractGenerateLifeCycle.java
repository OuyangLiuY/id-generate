package com.generate.common.lifecycle;

public abstract class AbstractGenerateLifeCycle implements GenerateLifeCycle{

    protected GenerateState state = GenerateState.DEFAULT_STATE;

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }

    @Override
    public GenerateState getState() {
        return this.state;
    }
}
