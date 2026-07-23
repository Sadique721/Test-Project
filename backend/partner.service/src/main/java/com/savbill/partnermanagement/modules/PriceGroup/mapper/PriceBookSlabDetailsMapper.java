package com.savbill.partnermanagement.modules.PriceGroup.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import com.savbill.partnermanagement.modules.partner.entity.PriceBookSlabDetails;
import org.mapstruct.Mapper;

@Mapper
public interface PriceBookSlabDetailsMapper extends IBaseMapper<PriceBookSlabDetailsDTO, PriceBookSlabDetails> {
}
