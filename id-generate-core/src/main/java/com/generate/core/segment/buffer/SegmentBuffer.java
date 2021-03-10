package com.generate.core.segment.buffer;

import com.generate.common.exception.BizException;
import com.generate.core.segment.bean.IdWrapper;
import com.generate.core.segment.LeafService;
import com.generate.core.segment.bean.Segment;
import com.generate.core.segment.event.SegmentEvent;
import com.generate.core.segment.event.SegmentEventBus;
import com.generate.core.segment.policy.FetchPolicy;
import lombok.extern.slf4j.Slf4j;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Slf4j
public class SegmentBuffer {

    public static final int NORMAL = 0;
    public static final int FILLING_NEXT_BUFFER = 1;
    public static final int FILLED_NEXT_BUFFER = 2;
    public static final int CHANGE_NEXT_BUFFER = 3;
    private volatile int state = NORMAL;

    private volatile Segment curSegment;
    private volatile Segment nextSegment;

    private final LeafService leafService;
    private static final long STATE_OFFSET;
    private static final Unsafe UNSAFE;
    private final SegmentEventBus segmentEventBus;
    private final FetchPolicy fetchPolicy;
    private final String tag;
    private Throwable ex;
    private static final String THE_SAFE = "theSafe";

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
            STATE_OFFSET = UNSAFE.objectFieldOffset(SegmentBuffer.class.getDeclaredField("state"));
        } catch (Exception e) {
            throw new BizException(e.getMessage(), e);
        }
    }

    public SegmentBuffer(LeafService leafService, SegmentEventBus segmentEventBus, FetchPolicy fetchPolicy, String tag) {
        this.leafService = leafService;
        this.segmentEventBus = segmentEventBus;
        this.fetchPolicy = fetchPolicy;
        this.tag = tag;
        // init segment
        this.curSegment = new Segment();
    }

    public IdWrapper nextId(int num) {
        checkSegment();
        checkException();
        IdWrapper idWrapper = null;
        for (; ; ) {
            idWrapper = curSegment.getNextId(num);
            if (idWrapper == null) {
                checkSegment();
                for (; ; ) {
                    checkException();
                    if (this.state == NORMAL) {
                        break;
                    }
                    // 准备好
                    if (state == FILLED_NEXT_BUFFER
                            && nextSegment != null
                            && compareAndSwapState(FILLED_NEXT_BUFFER, CHANGE_NEXT_BUFFER)) {
                        log.info("{} next segment is ok", tag);
                        changeSegment();
                        break;
                    }
                    synchronized (this) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                break;
            }
        }
        return idWrapper;
    }

    private void checkException() {
        if(ex != null){
            if(ex instanceof BizException){
                throw (BizException)ex;
            }else {
                throw new BizException("check exception tag = " + tag ,ex);
            }
        }

    }

    private void checkSegment() {
        if (curSegment.getUsedPercent().compareTo(fetchPolicy.nextSegmentFetchPercent(this.tag)) >= 0
                && nextSegment == null
                && compareAndSwapState(NORMAL, FILLING_NEXT_BUFFER)) {
            log.info("start fill next segment , tag = {}", tag);
            fillNextSegmentEvent();
        }

    }

    private void fillNextSegmentEvent() {
        SegmentEvent event = new SegmentEvent(this,tag,leafService,fetchPolicy.segmentFetchSize(tag));
        segmentEventBus.post(event);
    }

    private void changeSegment() {
        curSegment = nextSegment;
        nextSegment = null;
        state = NORMAL;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public void fillComplete(Throwable ex){
        this.ex = ex;
        state = FILLED_NEXT_BUFFER;
        synchronized (this){
            this.notify();
        }
    }

    public void setNextSegment(Segment segment) {
        this.nextSegment = segment;
    }

    private boolean compareAndSwapState(int curState, int newState) {
        return UNSAFE.compareAndSwapInt(this, STATE_OFFSET, curState, newState);
    }
}
