package com.savbill.taskmanagement.core.modules.tasks.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.TatQueryFieldMapping;
import com.savbill.taskmanagement.core.modules.tasks.model.TatQueryFieldMappingDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TatQueryFieldMapper extends IBaseMapper<TatQueryFieldMappingDTO, TatQueryFieldMapping> {
}
