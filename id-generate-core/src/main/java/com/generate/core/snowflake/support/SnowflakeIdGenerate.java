package com.generate.core.snowflake.support;

import com.generate.common.base.Constants;
import com.generate.common.lifecycle.AbstractGenerateLifeCycle;
import com.generate.common.properties.SnowflakeProperties;
import com.generate.core.IdGenerate;
import com.generate.core.snowflake.bean.Snowflake;
import com.generate.core.snowflake.notify.NotifyService;
import com.google.common.base.Preconditions;
import io.etcd.jetcd.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


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

    @PostConstruct
    public void initEtcdHolder(){
        snowflake  = new Snowflake();
        snowflake.setEpoch(Constants.SNOWFLAKE_DEFAULT_EPOCH);
        String workIp = snowflakeProperties.getWorkIp();
        String netInterName = snowflakeProperties.getNetInterName();
        String workPort = snowflakeProperties.getWorkPort();
        checkArgument(workIp,netInterName,workPort);
        
    }

    private void checkArgument(String workIp, String netInterName, String workPort) {
        Preconditions.checkArgument(StringUtils.isNotBlank(workIp),"init workIp must not null");
        Preconditions.checkArgument(StringUtils.isNotBlank(netInterName),"init netInterfaceName must not null");
        Preconditions.checkArgument(StringUtils.isNotBlank(workPort),"init workPort must not null");
    }


    @Override
    public String getName() {
        return "snowflake id generate";
    }

    @Override
    public long getId(String bizTag) {
        return 0;
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
