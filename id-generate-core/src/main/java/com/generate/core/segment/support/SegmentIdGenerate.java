package com.generate.core.segment.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.generate.common.lifecycle.AbstractGenerateLifeCycle;
import com.generate.core.segment.IdGenerate;
import com.generate.core.segment.LeafService;
import com.generate.core.segment.bean.IdWrapper;
import com.generate.core.segment.buffer.SegmentBuffer;
import com.generate.core.segment.cache.EnhanceThreadLocal;
import com.generate.core.segment.event.SegmentEventBus;
import com.generate.core.segment.policy.FetchPolicy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SegmentIdGenerate extends AbstractGenerateLifeCycle implements IdGenerate {
    private final EnhanceThreadLocal threadLocal = new EnhanceThreadLocal();
    private final ConcurrentHashMap<String, SegmentBuffer> segmentBufferMap = new ConcurrentHashMap<>(256);
    private final ConcurrentHashSet<Thread> threadHashSet = new ConcurrentHashSet<>(256);
    private final FetchPolicy fetchPolicy;
    private final LeafService leafService;
    private final SegmentEventBus segmentEventBus;

    public SegmentIdGenerate(FetchPolicy fetchPolicy, LeafService leafService, SegmentEventBus segmentEventBus) {
        this.fetchPolicy = fetchPolicy;
        this.leafService = leafService;
        this.segmentEventBus = segmentEventBus;
    }

    @Override
    public String getName() {
        return "segment id generate";
    }

    @Override
    public long getId(String bizTag) {
        return fetchPolicy.threadLocalCacheEnabled() ? getIdForThreadLocal(bizTag) : getIdForSegment(bizTag);
    }

    private long getIdForSegment(String bizTag) {
        try {
            SegmentBuffer segmentBuffer = segmentBufferMap.get(bizTag);
            if (segmentBuffer == null) {
                synchronized (this) {
                    if (segmentBufferMap.get(bizTag) == null) {
                        log.info("");
                        segmentBuffer = new SegmentBuffer(leafService, segmentEventBus, fetchPolicy, bizTag);
                        segmentBufferMap.put(bizTag, segmentBuffer);
                    }
                }
            }
            assert segmentBuffer != null;
            IdWrapper idWrapper = segmentBuffer.nextId(1);
            return idWrapper.getCurId();
        } catch (Exception e) {
            e.printStackTrace();
            segmentBufferMap.remove(bizTag);
            log.error("获取segment id 异常 e = {}" , e.getMessage());
            throw e;
        }
    }

    private long getIdForThreadLocal(String bizTag) {
        Map<String, IdWrapper> wrapperMap = threadLocal.get();
        IdWrapper idWrapper = wrapperMap.get(bizTag);
        if(isFilled(idWrapper)){
            log.info("threadLocal 无缓存，开始填充...");
            SegmentBuffer segmentBuffer = segmentBufferMap.get(bizTag);
            if(segmentBuffer == null){
                if (segmentBufferMap.get(bizTag) == null) {
                    log.info("");
                    segmentBuffer = new SegmentBuffer(leafService, segmentEventBus, fetchPolicy, bizTag);
                    segmentBufferMap.put(bizTag, segmentBuffer);
                }
            }
            boolean flag = true;
            try {
                assert segmentBuffer != null;
                idWrapper = segmentBuffer.nextId(fetchPolicy.segmentFetchSize(bizTag));
            }catch (Exception e){
                e.printStackTrace();
                segmentBufferMap.remove(bizTag);
                flag = false;
                throw e;
            }finally {
                if(flag){
                    threadHashSet.add(Thread.currentThread());
                    wrapperMap.put(bizTag,idWrapper);
                }
            }
        }
        long curId = idWrapper.getCurId();
        idWrapper.setCurId(curId + 1);
        return curId;
    }

    private boolean isFilled(IdWrapper idWrapper) {
        return idWrapper == null || idWrapper.getCurId() > idWrapper.getMaxId() - 1;
    }

    @Override
    protected void doInit() {

    }

    @Override
    protected void doStart() {

    }

    @Override
    protected void doStop() {

    }
}
