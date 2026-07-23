package com.savbill.cpm.modules.ServiceArea.mapper;

import com.savbill.cpm.core.mapper.CycleAvoidingMappingContext;
import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.Pincode.domain.Pincode;
import com.savbill.cpm.modules.Pincode.service.PincodeService;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceArea;
import com.savbill.cpm.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaDTO;
import com.savbill.cpm.modules.ServiceArea.model.ServiceAreaPincodeRelDTO;
import com.savbill.cpm.modules.ServiceArea.service.ServiceAreaService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract class ServiceAreaPincodeRelMapper implements IBaseMapper<ServiceAreaPincodeRelDTO, ServiceAreaPincodeRel> {
        @Autowired
        private ServiceAreaMapper serviceAreaMapper;
        @Autowired
        private ServiceAreaService serviceAreaService;
        @Autowired
        private PincodeService pincodeService;

        @Override
        @Mapping(source = "dtoData.pincodeId", target = "pincodeData")
        @Mapping(source = "dtoData.serviceAreaId", target = "serviceArea")
        public abstract ServiceAreaPincodeRel dtoToDomain(ServiceAreaPincodeRelDTO dtoData, @Context CycleAvoidingMappingContext context);

        @Override
        @Mapping(source = "data.pincodeData", target = "pincodeId")
        @Mapping(source = "data.serviceArea", target = "serviceAreaId")
        public abstract ServiceAreaPincodeRelDTO domainToDTO(ServiceAreaPincodeRel data, @Context CycleAvoidingMappingContext context);

        Long fromServiceAreaToId(ServiceArea entity) {
                return entity == null ? null : entity.getId();
        }

        ServiceArea fromServiceAreaIdToServiceArea(Long entityId) {
                if (entityId == null) {
                        return null;
                }
                ServiceArea entity;
                try {
                        ServiceAreaDTO entityDTO = serviceAreaService.getEntityById(entityId, false);
                        entity = serviceAreaMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
                        entity.setId(entityId);
                } catch (Exception e) {
                        e.printStackTrace();
                        entity = null;
                }
                return entity;
        }

        protected Pincode fromPincodeIdToPincode(Integer pincodeId) {
                if (pincodeId == null) {
                        return null;
                }
                Pincode pincode;
                try {
                        pincode = pincodeService.getMapper().dtoToDomain(pincodeService.getEntityById(pincodeId.longValue()), new CycleAvoidingMappingContext());
                        pincode.setId(pincodeId.longValue());
                } catch (Exception e) {
                        e.printStackTrace();
                        pincode = null;
                }
                return pincode;
        }

        Integer fromPincodeToPincodeId(Pincode entity) {
                return entity == null ? null : entity.getId().intValue();
        }

        }
