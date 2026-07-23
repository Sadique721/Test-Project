package com.savbill.cpm.modules.Mvno.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.modules.custAccountProfile.CustAccountProfile;
import com.savbill.cpm.modules.custAccountProfile.CustAccountProfileRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Mvno.domain.Mvno;
import com.savbill.cpm.modules.Mvno.model.MvnoDTO;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class MvnoMapper implements IBaseMapper<MvnoDTO, Mvno> {

    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;

    @Mapping(source = "profileId", target = "custAccountProfile")
    public abstract Mvno dtoToDomain(MvnoDTO data,@Context CycleAvoidingMappingContext context);

   protected CustAccountProfile customMapper (Long profileId) {
        if (profileId == null) {
            return null;
        }
        return custAccountProfileRepository.findById(profileId).orElse(null);
    }

}
