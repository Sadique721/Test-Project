package com.savbill.revenuemanagement.core.mapper.invoice;

import com.savbill.revenuemanagement.core.dto.invoice.DebitDocumentDto;
import com.savbill.revenuemanagement.core.entity.debitdoc.DebitDocument;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.core.mapper.common.IBaseMapper;
import com.savbill.revenuemanagement.core.mapper.customer.CustomerMapper;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public abstract class DebitDocumentMapper implements IBaseMapper<DebitDocumentDto, DebitDocument> {

    @Override
    public abstract DebitDocumentDto domainToDTO(DebitDocument debitDocument, CycleAvoidingMappingContext context);

    @Override
    public abstract DebitDocument dtoToDomain(DebitDocumentDto dtoData, CycleAvoidingMappingContext context);
}
