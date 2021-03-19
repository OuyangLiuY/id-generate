package com.generate.core.snowflake.lock;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public  abstract class AbstractLock implements Lock {
    @Override
    public void lock() {
        log.info("AbstractLock lock ...");
    }

    @Override
    public void unlock() {
        log.info("AbstractLock unlock ...");
    }
}
