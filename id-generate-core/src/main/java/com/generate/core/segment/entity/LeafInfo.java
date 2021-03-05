package com.generate.core.segment.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LeafInfo {
    /**
     * 业务标识
     */
    private String tag;
    /**
     * 当前id
     */
    private long curId;
    /**
     * 最大id
     */
    private long maxId;
    /**
     * 节点描述
     */
    private String description;
    /**
     * 更新时间
     */
    private Date updateTime;
}
