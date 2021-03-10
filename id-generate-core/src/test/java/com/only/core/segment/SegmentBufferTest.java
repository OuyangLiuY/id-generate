package com.only.core.segment;

import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.event.SegmentEventBus;
import com.generate.core.segment.policy.support.DefaultFetchPolicy;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SegmentBufferTest {
    private SegmentBuffer segmentBuffer;
    private String key = "test";
    @Before
    public void init(){
        segmentBuffer = new SegmentBuffer(new TestLeafService(),new SegmentEventBus(),new DefaultFetchPolicy(),key);
    }
    @Test
    public void testSingle(){
        for (int i = 0; i < 2000; i++) {
            System.out.println(segmentBuffer.nextId(10));
            System.out.println("处理完成了..." + i);
        }
    }

    @Test
    public void tesMulti() throws InterruptedException {
        ExecutorService executor = Executors.newCachedThreadPool();

        for (int i = 0; i < 20; i++) {
            int finalI = i;
            executor.execute(()->{
                System.out.println(segmentBuffer.nextId(10));
                System.out.println("处理完成了..." + finalI);
            });

        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
    }
}
