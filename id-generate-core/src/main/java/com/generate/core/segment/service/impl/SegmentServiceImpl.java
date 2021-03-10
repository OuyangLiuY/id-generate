package com.generate.core.segment.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.generate.common.base.Constants;
import com.generate.common.base.ResultWrapper;
import com.generate.common.exception.BizException;
import com.generate.core.segment.LeafService;
import com.generate.core.segment.database.entity.LeafAlloc;
import com.generate.core.segment.database.entity.LeafInfo;
import com.generate.core.segment.database.service.LeafAllocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SegmentServiceImpl implements LeafService {

    @Autowired
    LeafAllocService leafAllocService;

    @Override
    public LeafInfo getLeafInfo(String key) {
        String[] split = key.split(Constants.SPLIT_CHAR);
        String sysId = split[0];
        String bizTag = split[1];
        ResultWrapper<LeafAlloc> result = leafAllocService.findBySystemIdAndBizTag(sysId, bizTag);
        if (!result.isSuccess()) {
            log.info("获取leaf alloc 出错");
        }
        LeafAlloc info = result.getData();
        if (info == null) {
            throw new BizException("不存在此tag:{" + key + "}的数据");
        }
        Boolean enableFlag = info.getEnableFlag();
        if (enableFlag == null || !enableFlag) {
            throw new BizException("此tag:{" + key + "}的数据已经被禁用");
        }
        log.info("tag:{},info:{}", key, info);
        //步长
        Integer step = info.getStep();
        Long maxId = info.getMaxId();
        Long newMaxId = step + maxId;
        info.setMaxId(newMaxId);
        LambdaUpdateWrapper<LeafAlloc> wrapper = new UpdateWrapper<LeafAlloc>().lambda()
                .eq(LeafAlloc::getSystemId, sysId)
                .eq(LeafAlloc::getBizTag, bizTag)
                .eq(LeafAlloc::getMaxId, maxId);
        boolean update = false;
        for (int i = Constants.MAX_TRIES; i > 0 && !update; i--) {
            update = leafAllocService.update(wrapper);
        }
        if (!update) {
            throw new BizException("更新失败");
        }

        return resultLeafInfo(info, maxId);
    }

    private LeafInfo resultLeafInfo(LeafAlloc info, Long oldMaxId) {
        LeafInfo leafInfo = new LeafInfo();
        leafInfo.setMaxId(info.getMaxId());
        leafInfo.setCurId(oldMaxId);
        leafInfo.setTag(info.getBizTag());
        leafInfo.setDescription(info.getDescription());
        return leafInfo;
    }
}
