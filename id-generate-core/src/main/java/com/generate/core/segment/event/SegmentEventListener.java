package com.generate.core.segment.event;


import com.generate.core.segment.bean.Segment;
import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.database.entity.LeafInfo;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SegmentEventListener {

    @Subscribe
    public void listener(SegmentEvent event) {
        log.info("deal fill event = {}", event);
        SegmentBuffer segmentBuffer = event.getSegmentBuffer();
        Throwable ex = null;
        Segment segment = null;
        try {
            LeafInfo leafInfo = event.getLeafService().getLeafInfo(event.getTag());
            log.info(" deal res leaf info = {}", leafInfo);
            segment = leafInfoToSegment(leafInfo);
        } catch (Exception e) {
            ex = e;
            log.error("throw get leaf info exception");
        }
        segmentBuffer.setNextSegment(segment);
        // complete change state
        segmentBuffer.fillComplete(ex);
    }

    private Segment leafInfoToSegment(LeafInfo leafInfo) {
        Segment segment = new Segment();
        segment.setCurId(leafInfo.getCurId());
        segment.setMinId(leafInfo.getCurId());
        segment.setMaxId(leafInfo.getMaxId());
        return segment;
    }
}
