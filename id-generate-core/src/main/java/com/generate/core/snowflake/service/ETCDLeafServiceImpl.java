package com.generate.core.snowflake.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.generate.common.base.Constants;
import com.generate.common.exception.BizException;
import com.generate.core.LeafService;
import com.generate.core.segment.database.entity.LeafInfo;
import com.generate.core.snowflake.bean.EtcdKeyBean;
import com.generate.core.snowflake.lock.AbstractLock;
import com.generate.core.snowflake.lock.EtcdDistributeLock;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ETCDLeafServiceImpl implements LeafService {


    @Autowired
    Client client;

    @Override
    public LeafInfo getLeafInfo(String key) {
        if (StringUtils.isBlank(key)) {
            throw new BizException("tag不能为空");
        }
        LeafInfo leafInfo = new LeafInfo();
        KV kvClient = this.client.getKVClient();
        ByteSequence seqKey = ByteSequence.from(Constants.getETCDKeyBySystemId(key), StandardCharsets.UTF_8);
        AbstractLock lock = new EtcdDistributeLock(client,key,20, TimeUnit.SECONDS);
        try {
            lock.lock();
            GetResponse response = kvClient.get(seqKey).get();
            long count = response.getCount();
            if(count == 0){
                throw new BizException("etcd不存在此tag:["+key+"]的数据");
            }
            List<KeyValue> values = response.getKvs();
            if(values.isEmpty()){
                log.info("不存在此tag的节点数据：" + key);
                throw new BizException("etcd不存在此tag:["+key+"]节点数据");
            }
            KeyValue value = values.get(0);
            EtcdKeyBean etcdKeyBean = JSONUtil.toBean(JSONUtil.parseObj(value), EtcdKeyBean.class);
            log.info("获取节点数据 info = {}",etcdKeyBean);
            if(!etcdKeyBean.getStatus()){
                throw new BizException("此tag:{" + key + "}的数据不可用");
            }
            String bizTag = etcdKeyBean.getBizTag();
            Integer maxId = etcdKeyBean.getMaxId();
            Integer step = etcdKeyBean.getStep();

            int newMaxId = step + maxId;
            etcdKeyBean.setMaxId(newMaxId);
            etcdKeyBean.setUpdateTime(System.currentTimeMillis());
            ByteSequence updateValue = ByteSequence.from(JSONUtil.toJsonStr(etcdKeyBean), StandardCharsets.UTF_8);
            log.info("更新etcd 节点的值为：{}",etcdKeyBean);
            kvClient.put(seqKey,updateValue, PutOption.newBuilder().withPrevKV().build()).get();

            leafInfo.setTag(bizTag);
            leafInfo.setCurId(maxId);
            leafInfo.setMaxId(newMaxId);
            leafInfo.setUpdateTime(new Date(etcdKeyBean.getUpdateTime()));
            leafInfo.setDescription(etcdKeyBean.getDescription());
        }catch (Exception e){
            e.printStackTrace();
            throw new BizException("填充异常 : {" + key +"}");
        }finally {
            lock.unlock();
        }
        return leafInfo;
    }
}
