package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseDocDetails;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseDocDetailsDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseDocDetailsMapper implements IBaseMapper<CaseDocDetailsDTO, CaseDocDetails> {
}
