package com.savbill.cpm.mapper.postpaid;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.model.radius.CustReplyItem;
import com.savbill.cpm.pojo.api.CustReplyItemPojo;

@Mapper
public interface CustReplyItemMapper extends IBaseMapper<CustReplyItemPojo, CustReplyItem> {
}
