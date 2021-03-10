package com.generate.core.segment.database.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.generate.common.base.ResultWrapper;
import com.generate.core.segment.database.entity.LeafAlloc;

public interface LeafAllocService extends IService<LeafAlloc> {

    ResultWrapper<LeafAlloc> findBySystemIdAndBizTag(String systemId,String bizTag);
}
