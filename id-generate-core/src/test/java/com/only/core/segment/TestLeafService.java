package com.only.core.segment;

import com.generate.core.LeafService;
import com.generate.core.segment.database.entity.LeafInfo;

import java.util.HashMap;

public class TestLeafService implements LeafService {
    HashMap<String, Long> map = new HashMap<>(8);
    @Override
    public synchronized LeafInfo getLeafInfo(String tag) {
        LeafInfo leafInfo = new LeafInfo();
        map.putIfAbsent(tag, 0L);
        leafInfo.setCurId(map.get(tag));
        map.put(tag, map.get(tag));
        leafInfo.setMaxId(2000);
        System.out.println("获取 leafInfo" + leafInfo.toString());
        return leafInfo;
    }
}
