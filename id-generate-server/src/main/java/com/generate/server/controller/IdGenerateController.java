package com.generate.server.controller;

import com.generate.common.base.Constants;
import com.generate.common.base.R;
import com.generate.core.segment.service.IdGenerateService;
import com.generate.server.segment.bean.SegmentBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "generate/id")
public class IdGenerateController {

    @Autowired
    IdGenerateService idGenerateService;

    @PostMapping(value = "api/segment")
    public R<Object> getSegmentId( @Validated  @RequestBody SegmentBean segmentBean){
        Integer size = segmentBean.getSize();
        if (size == null || size < 0 || size > 20) {
            size = 8;
        }
        long id = idGenerateService.getId(segmentBean.getBizTag());
        String ids = String.format("%0" + size + "d", id);
        String result = segmentBean.getBizTag() + Constants.ID_SEPARATOR + ids;
        return R.ok(result);
    }
}
