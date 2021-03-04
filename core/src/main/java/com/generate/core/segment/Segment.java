package com.generate.core.segment;

import com.generate.common.exception.BizException;
import lombok.Data;
import sun.misc.Unsafe;

@Data
public class Segment {
    private long curId;
    private long minId;
    private long maxId;
    private static final long CUR_ID_OFFSET;
    private static final Unsafe UNSAFE;
    static {
        try {
            UNSAFE = Unsafe.getUnsafe();
            CUR_ID_OFFSET = UNSAFE.objectFieldOffset(Segment.class.getDeclaredField("curId"));
        }catch (Exception e){
            throw new BizException(e.getMessage(),e);
        }
    }

    public IdWrapper getNextId(int num){
        IdWrapper wrapper = null;
        for (;;){
            long curId = this.curId;
            long nextId = curId + num;
            if(curId >= maxId){
                break;
            }
            if(maxId >= nextId && compareAndSwapCurId(curId,nextId)){
                wrapper = new IdWrapper();
                wrapper.setCurId(curId);
                wrapper.setMinId(curId);
                wrapper.setMaxId(nextId);
                break;
            }
            long diff = maxId - curId;
            if(nextId > maxId && diff > 0 && compareAndSwapCurId(curId,maxId)){
                wrapper = new IdWrapper();
                wrapper.setCurId(curId);
                wrapper.setMinId(curId);
                wrapper.setMaxId(maxId);
                break;
            }
        }
        return wrapper;
    }

    private boolean compareAndSwapCurId(long curId, long nextId) {
        return UNSAFE.compareAndSwapLong(this,CUR_ID_OFFSET,curId,nextId);
    }
}
