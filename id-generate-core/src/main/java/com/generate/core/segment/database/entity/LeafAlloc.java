package com.generate.core.segment.database.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "leaf_alloc")
public class LeafAlloc {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String systemId;

    private Integer groupId;

    private String bizTag;

    private Long maxId;

    private Integer fillZero;

    private Integer step;

    private Boolean enableFlag;

    private String description;

    private Date updateTime;
}