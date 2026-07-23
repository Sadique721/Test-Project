package com.savbill.cpm.modules.Template.mapper;

import org.mapstruct.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Template.domain.Template;
import com.savbill.cpm.modules.Template.model.TemplateDTO;

@Mapper
public interface TemplateMapper extends IBaseMapper<TemplateDTO, Template> {
}
