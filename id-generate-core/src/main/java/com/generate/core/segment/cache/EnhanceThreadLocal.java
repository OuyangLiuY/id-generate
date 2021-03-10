package com.generate.core.segment.cache;

import com.generate.core.segment.bean.IdWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * cached by thread local
 */
public class EnhanceThreadLocal {

    private static final Map<Thread, Map<String, IdWrapper>> idCache= new HashMap<>(128);

    public Map<String, IdWrapper> get(){
        Thread thread = Thread.currentThread();
        return idCache.computeIfAbsent(thread, k -> new HashMap<>(256));
    }

    public Map<String, IdWrapper> getValue(Thread thread){
        return idCache.get(thread);
    }
}
