package com.generate.core;

import com.generate.common.lifecycle.GenerateLifeCycle;

public interface IdGenerate extends GenerateLifeCycle {

    long getId(String bizTag);
}
