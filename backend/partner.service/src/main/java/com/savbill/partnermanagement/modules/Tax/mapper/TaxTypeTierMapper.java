package com.savbill.partnermanagement.modules.Tax.mapper;

import com.savbill.partnermanagement.core.mapper.IBaseMapper;
import com.savbill.partnermanagement.modules.Tax.domain.TaxTypeTier;
import com.savbill.partnermanagement.modules.Tax.dto.TaxTypeTierPojo;
import org.mapstruct.Mapper;

@Mapper
public interface TaxTypeTierMapper extends IBaseMapper<TaxTypeTierPojo, TaxTypeTier> {
}
