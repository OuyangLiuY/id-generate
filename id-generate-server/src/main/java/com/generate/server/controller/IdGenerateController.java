package com.generate.server.controller;

import com.generate.common.base.Constants;
import com.generate.common.base.R;
import com.generate.common.exception.BizException;
import com.generate.core.segment.service.IdGenerateService;
import com.generate.server.segment.bean.SegmentBean;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequestMapping(value = "generate/id")
public class IdGenerateController {

    @Autowired(required = false)
    @Qualifier("SegmentService")
    IdGenerateService segmentService;

    @Autowired(required = false)
    @Qualifier("SnowflakeService")
    IdGenerateService snowflakeService;

    @PostMapping(value = "api/segment")
    @ApiOperation(value = "segment生成分布式唯一id")
    public R<Object> getSegmentId(@Validated @RequestBody SegmentBean segmentBean) {
        if (snowflakeService == null) {
            throw new BizException("请通过id.generate.segment=true开启segment");
        }
        Integer size = segmentBean.getSize();
        if (size == null || size <= 0 || size >= 20) {
            size = 8;
        }
        long id = segmentService.getId(segmentBean.getSystemId() + Constants.SPLIT_CHAR + segmentBean.getBizTag());
        String ids = String.format("%0" + size + "d", id);
        String result = segmentBean.getBizTag() + Constants.ID_SEPARATOR + ids;
        return R.ok(result);
    }
    @PostMapping(value = "/api/snowflake")
    @ApiOperation(value = "雪花算法生成分布式唯一id")
    public R<String> getSnowflakeId() {
        if (snowflakeService == null) {
            throw new BizException("请通过id.generate.snowflake=true开启snowflake");
        }

        long id = snowflakeService.getId(null);
        log.info(" snowflake 拿到的id:{}", id);
        String ids = String.valueOf(id);
        return R.ok(ids);
    }
}
