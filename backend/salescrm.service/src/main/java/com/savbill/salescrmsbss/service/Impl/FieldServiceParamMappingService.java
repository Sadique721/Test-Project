package com.savbill.salescrmsbss.service.Impl;

import java.util.ArrayList;
import java.util.List;

//import com.savbill.apigw.core.service.ExBaseAbstractService2;
//import com.savbill.apigw.modules.ServiceParameterMapping.Service.ServiceParamMappingService;
//import com.savbill.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
//import com.savbill.apigw.modules.fieldMapping.FieldmappingMapper;
//import com.savbill.apigw.modules.fieldMapping.FieldsBuidMappingRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.entity.FieldServiceParamMapping;
import com.savbill.salescrmsbss.repository.FieldServiceParamMappingRepository;
import com.savbill.salescrmsbss.repository.FieldsBuidMappingRepo;

@Service
public class FieldServiceParamMappingService {
//		extends ExBaseAbstractService2<FieldServiceParamMappingDto, FieldServiceParamMapping, Long> {
//	public FieldServiceParamMappingService(FieldServiceParamMappingRepository repository,
//			FieldServiceParamMappingMapper mapper) {
//		super(repository, mapper);
//	}

	@Autowired
	FieldsBuidMappingRepo fieldsBuidMappingRepo;
	
	@Autowired
	FieldServiceParamMappingService serviceParamMappingService;
	@Autowired
	private FieldServiceParamMappingRepository fieldServiceParamMappingRepository;
	private static final Logger logger = LoggerFactory.getLogger(FieldServiceParamMappingService.class);

//	@Override
	public String getModuleNameForLog() {
		return "[ServiceParamMappingService]";
	}

	public List<FieldServiceParamMapping> getserviceParam(List<Long> serviceparamIdList) {
		String SUBMODULE = getModuleNameForLog() + " [getserviceParam()] ";
		logger.info(getModuleNameForLog() + "--" + " Fteching serviceParam .Data[" + SUBMODULE.toString() + "]");
		try {
			List<FieldServiceParamMapping> list = new ArrayList<>();
			list = fieldServiceParamMappingRepository.findAllByserviceParamIdIn(serviceparamIdList);
			return list;
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

	public List<FieldServiceParamMapping> findAll() {
		String SUBMODULE = getModuleNameForLog() + " [findAll()] ";
		logger.info(getModuleNameForLog() + "--" + " findAll .Data[" + SUBMODULE.toString() + "]");
		try {
			return fieldServiceParamMappingRepository.findAll();
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

}
