package com.generate.core.snowflake.notify;

import com.generate.common.exception.BizException;
import com.generate.core.snowflake.bean.NotifyContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultNotifyService implements NotifyService {

    @Override
    public <T> void notify(NotifyContext<T> context) {
        log.info("Id 生成器发生错误，错误信息={}", context);
        throw new BizException("服务节点发生时钟回拨");
    }
}
