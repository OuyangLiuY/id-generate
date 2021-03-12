package com.generate.core.snowflake.notify;

import com.generate.core.snowflake.bean.NotifyContext;

public interface NotifyService {
    <T> void notify(NotifyContext<T> context);
}
