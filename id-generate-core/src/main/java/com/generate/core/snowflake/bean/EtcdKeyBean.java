package com.generate.core.snowflake.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class EtcdKeyBean  implements Serializable {
    private Integer id;
    /**
     * 组id
     */
    private Integer groupId;

    /**
     * 系统Id
     */
    private String systemId;

    /**
     * 业务键
     */
    private String bizTag;

    /**
     * 步长
     */
    private Integer step;

    /**
     * 描述
     */
    private String description;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 目前最大id
     */
    private Integer maxId;

    /**
     * 是否可以使用
     */
    private Boolean status;

    /**
     * 填充0的长度
     */
    private Integer fillZero;
}
