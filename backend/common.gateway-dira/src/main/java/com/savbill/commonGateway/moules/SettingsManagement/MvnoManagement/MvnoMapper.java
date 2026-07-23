package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.CustAccountProfile;
import com.savbill.commonGateway.moules.SettingsManagement.CustAccountProfileManagement.CustAccountProfileRepository;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class MvnoMapper implements IBaseMapper<MvnoDTO, Mvno> {

    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;


    @Mapping(source = "profileId",target = "custAccountProfile")
    public abstract Mvno dtoToDomain(MvnoDTO data,@Context CycleAvoidingMappingContext context);


     CustAccountProfile mapper(Long profileId) {
        if (profileId == null) {
            return null;
        }
        return custAccountProfileRepository.findById(profileId).orElse(null);
    }



}