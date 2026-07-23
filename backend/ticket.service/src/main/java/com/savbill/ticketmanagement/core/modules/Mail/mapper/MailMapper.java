package com.savbill.ticketmanagement.core.modules.Mail.mapper;

import com.savbill.ticketmanagement.core.mapper.CycleAvoidingMappingContext;
import com.savbill.ticketmanagement.core.mapper.IBaseMapper;
import com.savbill.ticketmanagement.core.modules.Mail.domain.Mail;
import com.savbill.ticketmanagement.core.modules.Mail.model.MailDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

@Mapper
public abstract class MailMapper implements IBaseMapper<MailDTO, Mail> {




    //@Mapping(source = "issueid",target = "issue")
    public abstract Mail dtoToDomain(MailDTO dtoData, @Context CycleAvoidingMappingContext context);

    //@Mapping(source = "issue",target = "issueid")
    public abstract MailDTO domainToDTO(Mail data, @Context CycleAvoidingMappingContext context);

//    Long fromIssueToIssueid(Issue entity) {
//        return entity == null ? null : entity.getId();
//    }
//
//    Issue fromIssueidToIssue(Integer id) {
//        if (id == null) {
//            return null;
//        }
//        Issue entity;
//        try {
//            entity = issueRepository.findById(id.longValue()).get();
//            entity.setId(id.longValue());
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }

}
