package com.savbill.taskmanagement.core.modules.tasks.mapper;

import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseCategory;

import com.savbill.taskmanagement.core.modules.tasks.model.CaseCategoryDTO;

import org.mapstruct.Mapper;

@Mapper
public abstract class CaseCategoryMapper implements IBaseMapper<CaseCategoryDTO, CaseCategory> {
}
