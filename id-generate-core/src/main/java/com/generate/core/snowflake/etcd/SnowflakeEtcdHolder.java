package com.generate.core.snowflake.etcd;

import com.generate.common.exception.BizException;
import com.generate.common.lifecycle.AbstractGenerateLifeCycle;
import com.generate.common.properties.SnowflakeProperties;
import com.generate.core.snowflake.bean.EndPointData;
import com.generate.core.snowflake.support.SnowflakeIdGenerate;
import com.google.gson.Gson;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SnowflakeEtcdHolder extends AbstractGenerateLifeCycle {

    private final String ip;
    private final String port;
    /**
     * 机器id
     */
    private long workId;
    private String etcdAddrNode;
    private final String listenAddress;
    private final String rootNode = "root";
    private Client client;
    private final SnowflakeIdGenerate snowflakeIdGenerate;
    private final SnowflakeProperties snowflakeProperties;

    public SnowflakeEtcdHolder(String ip, String port, long workId,
                               SnowflakeIdGenerate snowflakeIdGenerate,
                               SnowflakeProperties snowflakeProperties) {
        this.ip = ip;
        this.port = port;
        this.workId = workId;
        this.listenAddress = ip + ":" + port;
        this.snowflakeIdGenerate = snowflakeIdGenerate;
        this.snowflakeProperties = snowflakeProperties;
    }

    @Override
    protected void doInit() {
        KV client = this.client.getKVClient();
        try {
            ByteSequence rootNode = ByteSequence.from(snowflakeProperties.getPathForever(), StandardCharsets.UTF_8);
            GetResponse response = client.get(rootNode, GetOption.newBuilder().withPrefix(rootNode).withKeysOnly(true).build()).get();
            if(response == null || response.getCount() == 0){
                String etcdNode = createNode(this.client);
            }
        } catch (Exception e) {

        }
    }

    private String createNode(Client client) throws ExecutionException, InterruptedException {
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from(snowflakeProperties.getPathForever(), StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(rootNode, StandardCharsets.UTF_8);
        PutResponse response = kvClient.put(key, value, PutOption.newBuilder().withPrevKV().build()).get();
        KeyValue prevKv = response.getPrevKv();
        long version = prevKv.getVersion();
        this.workId = version;
        // 持久节点
        String uniqueForeverPath = snowflakeProperties.getPathForever() + "/" + listenAddress+"-"+version;
        ByteSequence uniqueForeverKey = ByteSequence.from(uniqueForeverPath, StandardCharsets.UTF_8);
        ByteSequence uniqueForeverValue = ByteSequence.from(buildEndPointJsonData(), StandardCharsets.UTF_8);
        PutResponse uniqueResponse = kvClient.put(uniqueForeverKey, uniqueForeverValue).get();
        log.info("create etcd forever success ... response is = {}",uniqueResponse);
        log.info("create etcd forever success... path is ={}", uniqueForeverPath);
        // 临时节点
        String uniqueTmpPath = snowflakeProperties.getPathForever() + "/" + listenAddress+"-"+version;
        createTmpNode(uniqueTmpPath,buildEndPointJsonData());
        return uniqueForeverPath;
    }

    private void createTmpNode(String tmpPath, String data) {
        // 创建临时节点续约，创建Lease客户端
        KV kvClient = client.getKVClient();
        Lease leaseClient = client.getLeaseClient();
        Long leaseId = null;
        try{
            long seconds = TimeUnit.MICROSECONDS.toSeconds(snowflakeProperties.getLeaseTtl());
            leaseId = leaseClient.grant(seconds).get().getID();
        }catch (Exception e){
            throw new BizException("create lease id failed", e);
        }

    }

    private String buildEndPointJsonData() {
        EndPointData data = new EndPointData(ip,port,System.currentTimeMillis(),workId);
        return new Gson().toJson(data);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    @Override
    public String getName() {
        return null;
    }

    class LeaseClientTask implements Runnable{
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

}


