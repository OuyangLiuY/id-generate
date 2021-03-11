package com.generate.server.config;

import com.generate.core.segment.event.SegmentEventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SegmentConfig {

    @Bean
    public SegmentEventBus segmentEventBus(){
        return new SegmentEventBus();
    }
}
