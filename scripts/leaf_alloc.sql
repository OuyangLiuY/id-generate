DROP TABLE IF EXISTS `leaf_alloc`;

CREATE TABLE `leaf_alloc`
(
    `id` int(20)  unsigned NOT NULL AUTO_INCREMENT COMMENT 'id'
        primary key,
    `system_id`      varchar(20)   NOT NULL ,
    `group_id`      int(11)   NOT NULL ,
    `biz_tag`     varchar(128) NOT NULL DEFAULT '',
    `max_id`      bigint(20)   NOT NULL DEFAULT '1',
    `fill_zero`      int(4)   NOT NULL DEFAULT '1',
    `step`        int(11)      NOT NULL,
    `description` varchar(255)   DEFAULT NULL,
    `enable_flag`   tinyint(1) unsigned NOT NULL COMMENT '状态：0停用，1启用',
    `update_time` timestamp (0) NOT NULL DEFAULT  CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_systemid_groupid_biztag` (`system_id`,`group_id`,`biz_tag`)
) ENGINE = InnoDB  charset = utf8mb4;