package com.generate.core.segment.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.generate.common.base.ResultWrapper;
import com.generate.core.segment.database.entity.LeafAlloc;
import com.generate.core.segment.database.mapper.LeafAllocMapper;
import com.generate.core.segment.database.service.LeafAllocService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeafAllocServiceImpl extends ServiceImpl<LeafAllocMapper, LeafAlloc> implements LeafAllocService {

    @Override
    public ResultWrapper<LeafAlloc> findBySystemIdAndBizTag(String systemId, String bizTag) {
        QueryWrapper<LeafAlloc> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LeafAlloc::getSystemId, systemId)
                .eq(LeafAlloc::getBizTag, bizTag);
        LeafAlloc res = this.getOne(queryWrapper);
        log.info("数据获取结果 res = {}", res);
        return new ResultWrapper<>(res);
    }
}
