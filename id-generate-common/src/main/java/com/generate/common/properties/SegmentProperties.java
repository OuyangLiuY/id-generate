package com.generate.common.properties;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "id-generate.segment")
public class SegmentProperties {

    private BigDecimal nextSegmentFetchPercent;
    private Integer segmentFetchSize;
    private Integer threadLocalFetchSize;
    private Boolean threadLocalCacheEnabled;
}
