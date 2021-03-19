package com.generate.core.snowflake.lock;

import lombok.Data;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class EtcdLockData {
    private String lockKey;
    private boolean lockSuccess;
    private long leaseId;
    private ScheduledExecutorService executor;
    private Thread owningThread;
    private String lockPath;
    final AtomicInteger lockCount = new AtomicInteger(1);

    public EtcdLockData (){
    }

    public EtcdLockData(Thread owningThread, String lockPath)
    {
        this.owningThread = owningThread;
        this.lockPath = lockPath;
    }
}
