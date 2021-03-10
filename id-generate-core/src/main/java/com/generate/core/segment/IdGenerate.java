package com.generate.core.segment;

import com.generate.common.lifecycle.GenerateLifeCycle;

public interface IdGenerate extends GenerateLifeCycle {

    long getId(String bizTag);
}
