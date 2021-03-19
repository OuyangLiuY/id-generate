package com.generate.core.snowflake.lock;

public interface Lock {
    void lock();

    void unlock();
}
