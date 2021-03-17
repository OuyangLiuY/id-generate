package com.generate.core.snowflake.etcd;

import cn.hutool.http.HttpUtil;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Data
public class SnowflakeEtcdHolder extends AbstractGenerateLifeCycle {

    private static final String SERVER_TIME_API = "/generator/id/api/time";
    private final String ip;
    private final String port;
    /**
     * 机器id
     */
    private long workId;
    private String etcdAddrNode;
    private long lastUpdateTime;
    private final String listenAddress;
    private final String rootNode = "root";
    private Client client;
    private final SnowflakeIdGenerate snowflakeIdGenerate;
    private final SnowflakeProperties snowflakeProperties;

    public SnowflakeEtcdHolder(String ip, String port, Client client,
                               SnowflakeIdGenerate snowflakeIdGenerate,
                               SnowflakeProperties snowflakeProperties) {
        this.ip = ip;
        this.port = port;
        this.client = client;
        this.listenAddress = ip + ":" + port;
        this.snowflakeIdGenerate = snowflakeIdGenerate;
        this.snowflakeProperties = snowflakeProperties;
    }

    @Override
    protected void doInit() {
        KV kvClient = this.client.getKVClient();
        try {
            ByteSequence rootNode = ByteSequence.from(snowflakeProperties.getPathForever(), StandardCharsets.UTF_8);
            GetResponse response = kvClient.get(rootNode, GetOption.newBuilder().withPrefix(rootNode).withKeysOnly(true).build()).get();
            if (response == null || response.getCount() == 0) {
                String etcdNode = createNode(this.client);
                updateLocalWorkId(workId);
                etcdAddrNode = etcdNode;
                // 定时上报本机时间给 forever 节点
                scheduledReportEndPointsData(kvClient, etcdAddrNode);
            }else {
                Map<String, Long> workIdMap = new HashMap<>();
                Map<String, String> etcdKeyMap = new HashMap<>();
                // 存在根节点，检查是否存在属于自己的根节点
                List<KeyValue> keyValues = response.getKvs();
                for (KeyValue keyValue : keyValues) {
                    String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                    // 如果key为持久节根点 PATH_FOREVER
                    if (!snowflakeProperties.getPathForever().equals(key)) {
                        String[] split = key.split("-");
                        workIdMap.put(split[0], Long.parseLong(split[1]));
                        etcdKeyMap.put(split[0], key);
                    }
                }
                String key = snowflakeProperties.getPathForever() + "/" + listenAddress;
                Long tempWorkId = workIdMap.get(key);
                // 存在自己的节点
                if (tempWorkId != null) {
                    etcdAddrNode = etcdKeyMap.get(key);
                    workId = tempWorkId;
                    // 检查当前时间是否 大于 节点最近的上报时间
                    if (!checkInitTimeStamp(kvClient, etcdAddrNode)) {
                        throw new BizException("check init timestamp error,forever node timestamp gt this node time");
                    }
                    String tempNodePath = etcdAddrNode.replace(snowflakeProperties.getPathForever(), snowflakeProperties.getPathTemp());
                    // 重新创建临时节点
                    createTmpNode(tempNodePath, buildEndPointJsonData());
                    // 开启定时上报节点
                    scheduledReportEndPointsData(kvClient, etcdAddrNode);
                    updateLocalWorkId(workId);
                    log.info("there node has been found on  forever node, this endpoint ip-{} port-{} workerId-{} childnode and start success", ip, port, workId);
                } else {
                    // 创建新节点
                    String newNode = createNode(this.client);
                    etcdAddrNode = newNode;
                    String[] nodeKey = newNode.split("-");
                    workId = Integer.parseInt(nodeKey[1]);
                    // 开启定时上报节点
                    scheduledReportEndPointsData(kvClient, etcdAddrNode);
                    updateLocalWorkId(workId);
                    log.info("node can not find node on forever node,that endpoint ip-{} port-{} workid-{},create own node on forever node and start success", ip, port, workId);
                }
                // 检查当前机器节点与其他服务节点的时间是否同步，存在时间偏差，校验每个服务节点的时间一致
                checkServersClockBack(kvClient);
            }
        } catch (Exception e) {
            log.error("初始化ETCD节点服务出错", e);
            try {
                // 从本地机器文件系统加载workId信息
                Properties properties = new Properties();
                String path = getPropPath().replace("{port}", port);
                properties.load(new FileInputStream(path));
                String workId = properties.getProperty("workId");
                if (StringUtils.isNotEmpty(workId)) {
                    this.workId = Integer.parseInt(workId);
                } else {
                    throw new BizException("读取当前服务器的workId配置文件信息缺失");
                }
                log.warn("start node failed,use local node properties file workId-{}", workId);
            } catch (Exception e1) {
                throw new BizException("读取当前服务器的配置文件出错", e1);
            }
        }
    }

    /**
     * 获取 ETCD的所有临时节点(所有运行中的snowflake节点)的服务 IP：Port，
     * 然后通过RPC请求得到所有节点的系统时间，计算 sum(time)/nodeSize
     * 若 abs( 系统时间 -sum(time)/nodeSize ) < 阈值，认为当前系统时间准确，正常启动服务，
     * 检查ID服务器节点之间的时间差别，如果存在时钟不同步，进行抛出异常
     *
     * @param kvClient
     */
    private void checkServersClockBack(KV kvClient) throws Exception {
        ByteSequence rootNode = ByteSequence.from(snowflakeProperties.getPathTemp(), StandardCharsets.UTF_8);
        GetResponse rootResponse = null;
        try {
            // 带前缀匹配 /snowflake/bx/temp*
            rootResponse = kvClient.get(rootNode, GetOption.newBuilder()
                    .withPrefix(rootNode)
                    .build()).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new BizException("检查在线服务器节点的时间同步，查询ETCD服务节点出错", e);
        }

        long count = rootResponse.getCount();
        if (count == 0) {
            return;
        }
        List<KeyValue> keyValues = rootResponse.getKvs();
        Gson gson = new Gson();

        List<String> urls = keyValues.stream().filter(kv -> {
            String key = kv.getKey().toString(StandardCharsets.UTF_8);
            // 排除临时节点根节点 PATH_TEMP、排除当前节点路径
            return !(snowflakeProperties.getPathTemp().equals(key) || key.contains(ip + ":" + port));
        }).map(kv -> {
            String value = kv.getValue().toString(StandardCharsets.UTF_8);
            EndPointData endPointData = gson.fromJson(value, EndPointData.class);
            String url = "http://" + endPointData.getIp() + ":" + endPointData.getPort() + SERVER_TIME_API;
            return url;
        }).collect(Collectors.toList());

        log.info("在线节点信息: " + urls);

        if (urls == null || urls.size() == 0) {
            return;
        }

        // 服务器之间的时间误差,并行流处理
        double differTime = urls.parallelStream().map(url -> {
            // 系统当前时间
            long start = System.currentTimeMillis();
            String serverTime = HttpUtil.get(url);
            long endTime = System.currentTimeMillis();
            log.info("当前处理线程:{},请求的服务器URL:{},发起请求之前服务器系统时间:{},对方服务器的系统时间:{},请求完成后的系统时间:{}",
                    Thread.currentThread().getName(), url, start, serverTime, endTime);
            // 单程请求消耗时间
            long oneWayCost = (endTime - start) / 2;
            if (serverTime.startsWith("<Long>")) {
                serverTime = serverTime.substring(6);
                serverTime = serverTime.substring(0, serverTime.length() - 7);
            }
            return Long.parseLong(serverTime) - oneWayCost - start;
        }).collect(Collectors.toList()).stream().mapToLong(v -> v.longValue()).average().getAsDouble();

        // 时间差绝对值
        Double absSubTime = Math.abs(differTime);
        log.info("当前服务器系统时间与其他线上ID服务器的系统均值时间差: " + absSubTime + "ms");

        // 如果大于阈值
        if (absSubTime.longValue() > snowflakeProperties.getBlockBackThreshold()) {
            //snowflakeIdGenerate.getNotifyService().notify(NotifyContext.builder().data(snowFlakeIdGenerator).msg("服务器节点之间存在时钟不同步问题，请检查重试,节点信息:" + gson.toJson(urls)).build());
        }
    }

    /**
     * 检查当前节点时间戳是否 大于 节点最后一次上报时间
     *
     * @param kvClient
     * @param etcdAddressNode
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private boolean checkInitTimeStamp(KV kvClient, String etcdAddressNode) throws ExecutionException, InterruptedException {
        GetResponse response = kvClient.get(ByteSequence.from(etcdAddressNode, StandardCharsets.UTF_8)).get();
        KeyValue keyValue = response.getKvs().get(0);
        ByteSequence value = keyValue.getValue();
        EndPointData endPointReportData = parseEndPointReportData(value.toString(StandardCharsets.UTF_8));
        // 当前节点时间 大于或等于 最后一次上报时间
        return System.currentTimeMillis() >= endPointReportData.getTimestamp();
    }
    private EndPointData parseEndPointReportData(String json) {
        Gson gson = new Gson();
        return  gson.fromJson(json, EndPointData.class);
    }

    private void scheduledReportEndPointsData(KV client, String node) {
        //
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, (t) -> {
            Thread thread = new Thread(t, "scheduled-report-endpoints");
            thread.setDaemon(true);
            return thread;
        });
        executor.scheduleWithFixedDelay(() -> reportEndPointsData(client, node), 1L, snowflakeProperties.getSystemReportInterval(), TimeUnit.MILLISECONDS);
    }

    private void reportEndPointsData(KV client, String node) {
        if (System.currentTimeMillis() < lastUpdateTime) {
            return;
        }
        ByteSequence key = ByteSequence.from(node, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(buildEndPointJsonData(), StandardCharsets.UTF_8);
        client.put(key, value);
    }

    private void updateLocalWorkId(long workId) {
        File file = new File(getPropPath().replace("${port}", port));
        if (file.exists()) {
            try {
                FileUtils.writeStringToFile(file, "workId" + "=" + workId, false);
                log.info("update local file success , cache workId  is {}", workId);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("update local file failed, path = {}, workId = {}", file.getAbsolutePath(), workId);
            }
        } else {
            if (file.getParentFile().mkdirs()) {
                try {
                    if (file.createNewFile()) {
                        FileUtils.writeStringToFile(file, "workId" + "=" + workId, false);
                        log.info("create local file success , cache workId  is {}", workId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("create local file failed, path = {}, workId = {}", file.getAbsolutePath(), workId);
                }
            }
            log.error("mkdirs failed,path: {},workId:{}", file.getAbsolutePath(), workId);
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
        String uniqueForeverPath = snowflakeProperties.getPathForever() + "/" + listenAddress + "-" + version;
        ByteSequence uniqueForeverKey = ByteSequence.from(uniqueForeverPath, StandardCharsets.UTF_8);
        ByteSequence uniqueForeverValue = ByteSequence.from(buildEndPointJsonData(), StandardCharsets.UTF_8);
        PutResponse uniqueResponse = kvClient.put(uniqueForeverKey, uniqueForeverValue).get();
        log.info("create etcd forever success ... response is = {}", uniqueResponse);
        log.info("create etcd forever success... path is ={}", uniqueForeverPath);
        // 临时节点
        String uniqueTmpPath = snowflakeProperties.getPathForever() + "/" + listenAddress + "-" + version;
        createTmpNode(uniqueTmpPath, buildEndPointJsonData());
        return uniqueForeverPath;
    }

    private void createTmpNode(String tmpPath, String data) {
        // 创建临时节点续约，创建Lease客户端
        KV kvClient = client.getKVClient();
        Lease leaseClient = client.getLeaseClient();
        Long leaseId = null;
        try {
            long seconds = TimeUnit.MICROSECONDS.toSeconds(snowflakeProperties.getLeaseTtl());
            leaseId = leaseClient.grant(seconds).get().getID();
        } catch (Exception e) {
            throw new BizException("create lease id failed", e);
        }

    }

    private String buildEndPointJsonData() {
        EndPointData data = new EndPointData(ip, port, System.currentTimeMillis(), workId);
        return new Gson().toJson(data);
    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }

    public String getPropPath() {
        return System.getProperty("java.io.tmpdir") + snowflakeProperties.getGeneratorName()
                + File.separator + "conf" + File.separator + "{port}" + File.separator + "workerId.properties";
    }

    @Override
    public String getName() {
        return null;
    }

    class LeaseClientTask implements Runnable {
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


