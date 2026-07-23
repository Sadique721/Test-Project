package com.savbill.cpm.modules.PriceGroup.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.savbill.cpm.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PriceBookSlabDetailsMapper extends IBaseMapper<PriceBookSlabDetailsDTO, PriceBookSlabDetails> {
}
