package com.savbill.ticketmanagement.core.modules.Template.mapper;


import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Template.domain.Template;
import com.savbill.ticketmanagement.core.modules.Template.model.TemplateDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TemplateMapper extends IBaseMapper<TemplateDTO, Template> {
}
