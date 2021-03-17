package com.generate.core.snowflake.support;

import com.generate.common.base.Constants;
import com.generate.common.exception.BizException;
import com.generate.common.lifecycle.AbstractGenerateLifeCycle;
import com.generate.common.properties.SnowflakeProperties;
import com.generate.common.utils.GenerateUtils;
import com.generate.core.IdGenerate;
import com.generate.core.snowflake.bean.Snowflake;
import com.generate.core.snowflake.etcd.SnowflakeEtcdHolder;
import com.generate.core.snowflake.notify.NotifyService;
import com.google.common.base.Preconditions;
import io.etcd.jetcd.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.util.List;
import java.util.Random;


@Slf4j
@Component
public class SnowflakeIdGenerate extends AbstractGenerateLifeCycle implements IdGenerate {
    @Autowired
    Client client;

    @Autowired
    NotifyService notifyService;

    @Autowired
    SnowflakeProperties snowflakeProperties;

    private Snowflake snowflake;

    private static final Random random = new Random();

    @PostConstruct
    public void initEtcdHolder() {
        snowflake = new Snowflake();
        // 起始时间戳,默认值 2008-08-08 00:00:00
        snowflake.setEpoch(Constants.SNOWFLAKE_DEFAULT_EPOCH);
        String workIp = snowflakeProperties.getWorkIp();
        String netInterName = snowflakeProperties.getNetInterName();
        String workPort = snowflakeProperties.getWorkPort();
        if (StringUtils.isEmpty(workIp)) {
            workIp = getCurrIp(netInterName);
        }
        checkArgument(workIp, netInterName, workPort);
        // 初始化etcd 客户端，注册worker节点
        SnowflakeEtcdHolder holder = new SnowflakeEtcdHolder(workIp, workPort, client, this, snowflakeProperties);
        holder.init();
        snowflake.setWorkId(holder.getWorkId());
        log.info("init SnowflakeEtcdHolder success , etcd workerId is = {}", holder.getWorkId());
    }

    private String getCurrIp(String netInterName) {
        String ip = null;
        try {
            List<String> hostAddress = GenerateUtils.getHostAddress(netInterName);
            log.info("获取的网卡IP地址 = {}", hostAddress);
            ip = (hostAddress.isEmpty()) ? "" : hostAddress.get(0);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new BizException("获取当前网卡地址失败", e);
        }
        return ip;
    }

    private void checkArgument(String workIp, String netInterName, String workPort) {
        Preconditions.checkArgument(StringUtils.isNotBlank(workIp), "init workIp must not null");
        Preconditions.checkArgument(StringUtils.isNotBlank(netInterName), "init netInterfaceName must not null");
        Preconditions.checkArgument(StringUtils.isNotBlank(workPort), "init workPort must not null");
    }


    @Override
    public String getName() {
        return "snowflake id generate";
    }

    @Override
    public synchronized long getId(String bizTag) {
        long timestamp = System.currentTimeMillis();
        long lastTimeStamp = snowflake.getLastTimeStamp();
        // 当前时间小于上次服务器更新时间，说明发生了时钟回拨
        if (timestamp < lastTimeStamp) {
            //
            long offset = timestamp - lastTimeStamp;
            if (offset <= snowflakeProperties.getBlockBackThreshold()) {
                try {
                    wait(offset << 1);
                    timestamp = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new BizException("wait clock back too long error ", e);
                }
            } else {
                String msg = " 服务器节点发生回拨，workerId = " + snowflake.getWorkId();
                throw new BizException(msg);
            }
        }
        long sequence;
        // 当前毫秒内进行序列递增
        if (timestamp == lastTimeStamp) {
            sequence = (snowflake.getSequence() + 1) & snowflake.getSequenceMask();
            // 说明1毫秒内的序列已经使用完毕
            if (sequence == 0) {
                sequence = random.nextInt(100);
                timestamp = tilNextMills(lastTimeStamp);
            }
        } else {
            // 不再一个时间戳下，序列重置即可
            sequence = random.nextInt(100);
        }
        snowflake.setSequence(sequence);
        snowflake.setLastTimeStamp(timestamp);
        log.info("generate id is success ,workerId = {}, timestamp = {},sequence = {}", snowflake.getWorkId(), timestamp, sequence);
        return ((timestamp - snowflake.getEpoch()) << snowflake.getTimestampLeftShift())
                | (snowflake.getWorkId() << snowflake.getWorkIdShift())
                | sequence;
    }

    private long tilNextMills(long lastTimeStamp) {
        long currentStamp;
        do {
            currentStamp = System.currentTimeMillis();
        } while (currentStamp <= lastTimeStamp);
        return currentStamp;
    }


    @Override
    protected void doInit() {

    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }
}
