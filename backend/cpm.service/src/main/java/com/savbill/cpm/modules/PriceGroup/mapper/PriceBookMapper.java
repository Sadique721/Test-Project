package com.savbill.cpm.modules.PriceGroup.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.PriceGroup.domain.PriceBook;
import com.savbill.cpm.modules.PriceGroup.model.PriceBookDTO;
import com.savbill.cpm.modules.PriceGroup.repository.PriceBookRepository;

@Mapper
public abstract class PriceBookMapper implements IBaseMapper<PriceBookDTO, PriceBook> {

    @Autowired
    private PriceBookRepository priceBookRepository;

    @Override
    @Mapping(source = "priceBook.validfrom", target = "validFromString", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "priceBook.validto", target = "validToString", dateFormat = "dd-MM-yyyy")
    //@Mapping(source = "priceBook.priceBookSlabDetailsList", target = "priceBookSlabDetailsList")
    public abstract PriceBookDTO domainToDTO(PriceBook priceBook, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PriceBook dtoToDomain(PriceBookDTO dtoData, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMapping(@MappingTarget PriceBookDTO priceBookDTO, PriceBook priceBook) {
        try {
            if (priceBook.getId() != null) {
                priceBookDTO.setNoPartnerAssociate(priceBookRepository.countPartnerByPriceBook(priceBook.getId().intValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
