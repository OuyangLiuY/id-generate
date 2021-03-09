package com.only.core.segment;

import com.generate.core.segment.Segment;
import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.event.SegmentEventBus;
import com.generate.core.segment.policy.support.DefaultFetchPolicy;
import org.junit.Before;
import org.junit.Test;

public class SegmentBufferTest {
    private SegmentBuffer segmentBuffer;
    private String key = "test";
    @Before
    public void init(){
        segmentBuffer = new SegmentBuffer(new TestLeafService(),new SegmentEventBus(),new DefaultFetchPolicy(),key);
    }
    @Test
    public void testSingle(){
        for (int i = 0; i < 10; i++) {
            System.out.println("处理完成了..." + i);
        }
        System.out.println(segmentBuffer.nextId(10));
    }
}
