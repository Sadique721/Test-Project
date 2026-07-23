package com.savbill.taskmanagement.core.modules.Template.mapper;


import com.savbill.taskmanagement.core.mapper.IBaseMapper;
import com.savbill.taskmanagement.core.modules.Template.domain.Template;
import com.savbill.taskmanagement.core.modules.Template.model.TemplateDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TemplateMapper extends IBaseMapper<TemplateDTO, Template> {
}
