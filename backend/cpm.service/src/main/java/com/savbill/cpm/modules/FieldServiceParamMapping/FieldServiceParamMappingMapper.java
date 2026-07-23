package com.savbill.cpm.modules.FieldServiceParamMapping;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.ServiceParameters.domain.ServiceParameter;
import com.savbill.cpm.modules.ServiceParameters.repository.ServcieParametersRepository;
import com.savbill.cpm.modules.fieldMapping.FieldRepo;
import com.savbill.cpm.modules.fieldMapping.Fields;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class FieldServiceParamMappingMapper implements IBaseMapper<FieldServiceParamMappingDto,FieldServiceParamMapping> {

    @Autowired
    ServcieParametersRepository servcieParametersRepository;

    @Autowired
    FieldRepo fieldRepo;

    @Mapping(source = "serviceParameter", target = "serviceparamid")
    @Mapping(source = "fields",target = "fieldid")
    public abstract FieldServiceParamMappingDto domainToDTO(FieldServiceParamMapping data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "dtoData.serviceparamid", target = "serviceParameter")
    @Mapping(source = "dtoData.fieldid",target = "fields")
    public abstract FieldServiceParamMapping dtoToDomain(FieldServiceParamMappingDto dtoData, @Context CycleAvoidingMappingContext context);

    Long fromServiceParameterToServiceparamid(ServiceParameter entity) {
        return entity == null ? null : entity.getId();
    }

    ServiceParameter fromServiceparamidToServiceParameter(Integer id) {
        if (id == null) {
            return null;
        }
        ServiceParameter entity;
        try {
            entity = servcieParametersRepository.findById(id.longValue()).get();
            entity.setId(id.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Long fromFieldsToFieldid(Fields entity) {
        return entity == null ? null : entity.getId();
    }

    Fields fromFieldidToFields(Integer id) {
        if (id == null) {
            return null;
        }
        Fields entity;
        try {
            entity = fieldRepo.findById(id.longValue()).get();
            entity.setId(id.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
}
