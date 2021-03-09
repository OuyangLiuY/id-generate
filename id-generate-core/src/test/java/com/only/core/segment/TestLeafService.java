package com.only.core.segment;

import com.generate.core.segment.LeafService;
import com.generate.core.segment.entity.LeafInfo;

import java.util.HashMap;

public class TestLeafService implements LeafService {
    HashMap<String, Long> map = new HashMap<>(8);
    @Override
    public synchronized LeafInfo getLeafInfo(String tag) {
        LeafInfo leafInfo = new LeafInfo();
        map.putIfAbsent(tag, 0L);
        leafInfo.setCurId(map.get(tag));
        map.put(tag, map.get(tag));
        leafInfo.setMaxId(map.get(tag));
        return leafInfo;
    }
}
