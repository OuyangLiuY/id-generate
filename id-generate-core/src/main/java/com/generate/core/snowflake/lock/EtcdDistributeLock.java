package com.generate.core.snowflake.lock;

import com.generate.common.exception.LockException;
import com.generate.core.snowflake.etcd.LeaseClientTask;
import com.google.common.collect.Maps;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.lock.LockResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Slf4j
public class EtcdDistributeLock extends AbstractLock {

    private Client client;
    private Lease lease;
    private final Lock lock;
    private final String  lockKey;
    private String lockPath;
    private final long leaseTTl;
    private long initDelay = 0L;
    ScheduledExecutorService executor;
    private final ConcurrentMap<Thread,EtcdLockData> cache = Maps.newConcurrentMap();

    public EtcdDistributeLock(Client client, String lockKey, long leaseTTl, TimeUnit unit) {
        this.client = client;
        this.lock = client.getLockClient();
        this.lockKey = lockKey;
        this.leaseTTl = unit.toNanos(leaseTTl);
        executor = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        EtcdLockData etcdLockData = cache.get(currentThread);
        if(etcdLockData != null && etcdLockData.isLockSuccess()){
            // re enter 锁重入
            int count = etcdLockData.lockCount.incrementAndGet();
            if(count < 0){
                throw new LockException("超出可以重入次数错误");
            }
            return;
        }
        // 记录租约id
        long leaseId = 0L;
        try {
            leaseId = lease.grant(TimeUnit.NANOSECONDS.toNanos(leaseTTl)).get().getID();
            // 续约心跳周期
            long period = leaseTTl - leaseTTl / 5;
            executor.scheduleAtFixedRate(new LeaseClientTask(this.lease,leaseId),
                    initDelay,period,TimeUnit.NANOSECONDS);
            LockResponse lockResponse = lock.lock(ByteSequence.from(lockKey.getBytes(StandardCharsets.UTF_8)), leaseId).get();
            if(lockResponse != null){
                lockPath = lockResponse.getKey().toString(StandardCharsets.UTF_8);
            }
        } catch (InterruptedException  | ExecutionException e) {
            e.printStackTrace();
            log.error("获取锁失败");
        }
        // 获取锁成功，锁对象设置
        EtcdLockData newLockData = new EtcdLockData(currentThread, lockPath);
        newLockData.setLeaseId(leaseId);
        newLockData.setExecutor(executor);
        newLockData.setLockSuccess(true);
        newLockData.setLockKey(lockKey);
        cache.put(currentThread, newLockData);
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();
        EtcdLockData lockData = cache.get(currentThread);
        if (lockData == null){
            throw new IllegalMonitorStateException("You do not own the lock: " + lockKey);
        }
        int newLockCount = lockData.lockCount.decrementAndGet();
        if ( newLockCount > 0 ) {
            return;
        }
        if ( newLockCount < 0 ) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + lockKey);
        }
        try {
            // 释放锁
            if(lockPath != null){
                lock.unlock(ByteSequence.from(lockPath.getBytes())).get();
            }
            // 关闭定时任务
            lockData.getExecutor().shutdown();
            // 删除租约
            if (lockData.getLeaseId() != 0L) {
                lease.revoke(lockData.getLeaseId());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("解锁失败",e);
        }finally {
            // 移除当前线程资源
            cache.remove(currentThread);
        }
    }
}
