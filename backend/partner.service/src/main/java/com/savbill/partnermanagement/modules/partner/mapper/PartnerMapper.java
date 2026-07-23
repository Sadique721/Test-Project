package com.savbill.partnermanagement.modules.partner.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.partner.dto.PartnerPojo;
import com.savbill.partnermanagement.modules.partner.entity.Partner;
import org.mapstruct.Mapper;

@Mapper
public abstract class PartnerMapper implements IBaseMapper<PartnerPojo, Partner> {
}
