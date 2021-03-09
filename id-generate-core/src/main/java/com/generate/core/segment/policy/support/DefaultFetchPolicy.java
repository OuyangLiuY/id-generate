package com.generate.core.segment.policy.support;

import com.generate.common.properties.SegmentProperties;
import com.generate.core.segment.policy.FetchPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DefaultFetchPolicy implements FetchPolicy {

    @Autowired
    SegmentProperties segmentProperties;

    @Override
    public boolean threadLocalCacheEnabled() {
        return segmentProperties.getThreadLocalCacheEnabled();
    }

    @Override
    public int threadLocalFetchSize(String key) {
        return segmentProperties.getThreadLocalFetchSize();
    }

    @Override
    public int segmentFetchSize(String key) {
        //return segmentProperties.getSegmentFetchSize();
        return 100;
    }

    @Override
    public BigDecimal nextSegmentFetchPercent(String key) {
        //return segmentProperties.getNextSegmentFetchPercent();
        return new BigDecimal("0.9");
    }
}
