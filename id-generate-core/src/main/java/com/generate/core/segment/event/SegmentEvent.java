package com.generate.core.segment.event;

import com.generate.core.LeafService;
import com.generate.core.segment.buffer.SegmentBuffer;
import lombok.Data;

@Data
public class SegmentEvent {
    private SegmentBuffer segmentBuffer;
    private String tag;
    private LeafService leafService;
    private int num;

    public SegmentEvent(SegmentBuffer segmentBuffer, String tag, LeafService leafService, int num) {
        this.segmentBuffer = segmentBuffer;
        this.tag = tag;
        this.leafService = leafService;
        this.num = num;
    }
}
