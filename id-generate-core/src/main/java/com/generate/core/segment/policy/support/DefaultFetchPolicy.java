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
        Boolean enabled = segmentProperties.getThreadLocalCacheEnabled();
        return enabled != null ? enabled : false;
    }

    @Override
    public int threadLocalFetchSize(String key) {
        Integer fetchSize = segmentProperties.getThreadLocalFetchSize();
        return fetchSize != null ? fetchSize : 10;
    }

    @Override
    public int segmentFetchSize(String key) {
        Integer fetchSize = segmentProperties.getSegmentFetchSize();
        return fetchSize != null ? fetchSize : 100;
    }

    @Override
    public BigDecimal nextSegmentFetchPercent(String key) {
        BigDecimal percent = segmentProperties.getNextSegmentFetchPercent();
        return percent != null ? percent : new BigDecimal("0.9");
    }
}
