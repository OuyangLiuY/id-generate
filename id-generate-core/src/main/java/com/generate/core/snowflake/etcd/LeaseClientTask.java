package com.generate.core.snowflake.etcd;

import io.etcd.jetcd.Lease;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeaseClientTask implements Runnable {
    private final Lease lease;
    private final Long leaseId;

    public LeaseClientTask(Lease lease, Long leaseId) {
        this.lease = lease;
        this.leaseId = leaseId;
    }

    @Override
    public void run() {
        log.info("续约任务线程开始执行...");
        lease.keepAliveOnce(leaseId);
    }
}
