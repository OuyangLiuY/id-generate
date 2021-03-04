package com.generate.core.segment.buffer;

import com.generate.core.segment.Segment;
import lombok.Data;

@Data
public class SegmentBuffer {

    public static final int NORMAL = 0;
    private volatile int state = NORMAL;

    private volatile Segment curSegment;
    private volatile Segment nextSegment;



}
