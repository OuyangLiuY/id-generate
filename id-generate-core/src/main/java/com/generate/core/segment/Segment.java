package com.generate.core.segment;

import com.generate.common.exception.BizException;
import lombok.Data;
import sun.misc.Unsafe;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public BigDecimal getUsedPercent(){
        BigDecimal percent = null;
        if (maxId == 0){
            percent = new BigDecimal("1.00");

        }else {
            percent = new BigDecimal(curId - maxId).divide(new BigDecimal(maxId - minId),3, RoundingMode.UP);
        }
        return percent;
    }
    private boolean compareAndSwapCurId(long curId, long nextId) {
        return UNSAFE.compareAndSwapLong(this,CUR_ID_OFFSET,curId,nextId);
    }
}
