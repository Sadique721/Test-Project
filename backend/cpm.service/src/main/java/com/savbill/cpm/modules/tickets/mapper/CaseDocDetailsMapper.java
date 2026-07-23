package com.savbill.cpm.modules.tickets.mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.tickets.domain.CaseDocDetails;
import com.savbill.cpm.modules.tickets.model.CaseDocDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseDocDetailsMapper implements IBaseMapper<CaseDocDetailsDTO, CaseDocDetails> {
}
