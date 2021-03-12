package com.generate.core.snowflake.service;

import com.generate.core.segment.service.IdGenerateService;
import com.generate.core.snowflake.support.SnowflakeIdGenerate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SnowflakeServiceImpl implements IdGenerateService {

    @Autowired
    SnowflakeIdGenerate snowflakeIdGenerate;

    @Override
    public long getId(String key) {
        return snowflakeIdGenerate.getId(key);
    }
}
