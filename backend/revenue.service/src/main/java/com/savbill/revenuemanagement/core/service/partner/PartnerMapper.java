package com.savbill.revenuemanagement.core.service.partner;


import com.savbill.revenuemanagement.core.entity.partner.Partner;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import org.mapstruct.Mapper;

@Mapper
public abstract class PartnerMapper implements IBaseMapper<PartnerPojo, Partner> {
}
