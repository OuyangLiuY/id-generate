package com.generate.common.properties;


import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "id-generate.snowflake")
public class SnowflakeProperties {
    private String workIp;
    private String workPort;
    private String generatorName;
    private Long blockBackThreshold;
    private String etcdPoints;
    private Long keepAliveInterval;
    private Long leaseTtl;
    private Long systemReportInterval;

    /**
     * ETCD 节点根节点
     */
    private String rootEtcdPath;
    /**
     * 保存所有服务器节点数据的持久节点路径
     */
    private String pathForever;
    /**
     * 保持服务器节点信息的临时节点路径
     */
    private String pathTemp;
    /**
     * 指定获取启动服务IP的的网卡
     */
    private String netInterName;
}
