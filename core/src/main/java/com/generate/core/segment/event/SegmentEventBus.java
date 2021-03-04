package com.generate.core.segment.event;


import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SegmentEventBus {

    private final EventBus eventBus = new AsyncEventBus(
            "segment-event-bus",
            new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(50)));

    public SegmentEventBus() {
        eventBus.register(new SegmentEventListener());
    }

    // 注册到事件总线中
    public void register(SegmentEventListener listener) {
        eventBus.register(listener);
    }

    // 发送事件
    public void post(SegmentEvent event) {
        eventBus.post(event);
    }
}
