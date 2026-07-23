package com.savbill.taskmanagement.core.modules.tasks.mapper;

import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.tasks.domain.CaseSubCategory;
import com.savbill.taskmanagement.core.modules.tasks.model.CaseSubCategoryDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class CaseSubCategoryMapper implements IBaseMapper<CaseSubCategoryDTO, CaseSubCategory> {
}
