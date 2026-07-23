package com.savbill.salescrmsbss.service.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.savbill.salescrmsbss.controller.FieldMappingController;
import com.savbill.salescrmsbss.entity.CustomerAddress;
import com.savbill.salescrmsbss.entity.FieldServiceParamMapping;
import com.savbill.salescrmsbss.entity.Fields;
import com.savbill.salescrmsbss.entity.FieldsBuidMapping;
import com.savbill.salescrmsbss.entity.QFields;
import com.savbill.salescrmsbss.entity.QFieldsBuidMapping;
import com.savbill.salescrmsbss.entity.QScreenFieldMapping;
import com.savbill.salescrmsbss.entity.ScreenFieldMapping;
import com.savbill.salescrmsbss.entity.Screens;
import com.savbill.salescrmsbss.entity.ServiceParamMapping;
import com.savbill.salescrmsbss.entity.pojo.CommonListDTO;
import com.savbill.salescrmsbss.entity.pojo.CustomerAddressPojo;
import com.savbill.salescrmsbss.entity.pojo.FieldsDTO;
import com.savbill.salescrmsbss.entity.pojo.FieldsDetailsDTO;
import com.savbill.salescrmsbss.entity.pojo.FielmappingDto;
import com.savbill.salescrmsbss.entity.pojo.ModuleWiseFieldsDto;
import com.savbill.salescrmsbss.entity.pojo.ScreenFieldMappingDto;
import com.savbill.salescrmsbss.entity.pojo.ServiceParamMappingDTO;
import com.savbill.salescrmsbss.exceptions.CustomValidationException;
import com.savbill.salescrmsbss.repository.CustomerAddressRepository;
import com.savbill.salescrmsbss.repository.FieldRepo;
import com.savbill.salescrmsbss.repository.FieldServiceParamMappingRepository;
import com.savbill.salescrmsbss.repository.FieldsBuidMappingRepo;
import com.savbill.salescrmsbss.repository.ScreenFieldMappingRepository;
import com.savbill.salescrmsbss.repository.ScreenRepository;
import com.savbill.salescrmsbss.repository.ServiceParamMappingRepository;
import com.savbill.salescrmsbss.utils.CommonConstants;
import com.savbill.salescrmsbss.utils.TypeConstants;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class FieldmappingServiceImpl {

	private static final Logger logger = LoggerFactory.getLogger(FieldMappingController.class);

	@Autowired
	private ScreenRepository screenRepository;
//	@Autowired
//	private FieldServiceParamMappingService fieldServiceParamMappingService;

	@Autowired
	private FieldServiceParamMappingRepository fieldServiceParamMappingRepository;
	@Autowired
	private FieldsBuidMappingRepo fieldsBuidMappingRepo;

	@Autowired
	private FieldRepo fieldRepo;

//	@Autowired
//	private BusinessUnitService businessUnitService;

	@Autowired
	private ServiceParamMappingRepository serviceParamMappingRepository;

	@Autowired
	private ServiceParamMappingService serviceParamMappingService;

	@Autowired
	private ScreenFieldMappingRepository screenFieldMappingRepository;

	@Autowired
	private CommonListService commonListService;

	@Autowired
	private CustomerAddressRepository customerAddressRepository;

	public List<FielmappingDto> getTemplate(Long screenid) {
		String SUBMODULE = getModuleNameForLog() + " [getTemplate()] ";
		List<ScreenFieldMapping> screenFieldMappings = new ArrayList<>();
		logger.info(getModuleNameForLog() + "--" + "Fetching TEMPLATE .Data[" + SUBMODULE.toString() + "]");
		try {
			List<FielmappingDto> fielmappingDtos = new ArrayList<>();
			screenFieldMappings = screenFieldMappingRepository.getFields(screenid);
			if (screenFieldMappings.get(0).getScreens().getScreenname().equalsIgnoreCase("plan")) {
				List<FieldServiceParamMapping> plan = fieldServiceParamMappingRepository.findAll();
				int temp = 0;
				for (ScreenFieldMapping screenFieldMapping : screenFieldMappings) {
					FielmappingDto fielmappingDto = new FielmappingDto();
					fielmappingDto.setDataType(screenFieldMapping.getFields().getDataType());
					fielmappingDto.setFieldName(screenFieldMapping.getFields().getName());
					fielmappingDto.setScreen(screenFieldMapping.getScreens().getId());
					fielmappingDto.setScreenName(screenFieldMapping.getScreens().getScreenname());
					fielmappingDto.setFieldId(screenFieldMapping.getFields().getId());
					fielmappingDto.setId(screenFieldMapping.getId());
					for (int i = temp; i < plan.size();) {
						fielmappingDto.setModule(plan.get(i).getModule());
						temp++;
						break;
					}
					fielmappingDtos.add(fielmappingDto);
				}
			} else {
				List<FieldsBuidMapping> buidMappings = fieldsBuidMappingRepo
						.findAllByScreen(screenFieldMappings.get(0).getScreens().getScreenname());
				int temp = 0;
				for (ScreenFieldMapping screenFieldMapping : screenFieldMappings) {
					FielmappingDto fielmappingDto = new FielmappingDto();
					fielmappingDto.setDataType(screenFieldMapping.getFields().getDataType());
					fielmappingDto.setFieldName(screenFieldMapping.getFields().getName());
					fielmappingDto.setScreen(screenFieldMapping.getScreens().getId());
					fielmappingDto.setScreenName(screenFieldMapping.getScreens().getScreenname());
					fielmappingDto.setFieldId(screenFieldMapping.getFields().getId());
					fielmappingDto.setId(screenFieldMapping.getId());
					for (int j = temp; j < buidMappings.size();) {
						fielmappingDto.setModule(buidMappings.get(j).getModule());
						temp++;
						break;
					}
					fielmappingDtos.add(fielmappingDto);
				}
			}
			return fielmappingDtos;
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "--" + exception.getMessage() + "Error : " + exception.getMessage(),
					exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

	public List<Fields> getFields(Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
		logger.info(getModuleNameForLog() + "--" + "Fetching Fields .Data[" + SUBMODULE.toString() + "]");
		try {
			if (buId != null) {
//                String buType = businessUnitService.getById(buids.get(0)).getPlanBindingType();
				QFields qFields = QFields.fields;
				BooleanExpression booleanExpression = qFields.isNotNull();
//                booleanExpression = booleanExpression.and(qFields.bu_type.equalsIgnoreCase(buType));
				return (List<Fields>) fieldRepo.findAll(booleanExpression);
			} else
				throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
						CommonConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

//	@Override
	public String getModuleNameForLog() {
		return "[FieldmappingService]";
	}

	public FielmappingDto saveEntity(FielmappingDto entity) throws Exception {
//		String SUBMODULE = getModuleNameForLog() + " [saveEntity()] ";
		logger.info(getModuleNameForLog() + "--" + " saveEntity .Data[" + entity.toString() + "]");
		try {
			return domainToDTOFielmappingDto(fieldsBuidMappingRepo.save(dtoToDomainFieldsBuidMapping(entity)));
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

	public List<FielmappingDto> saveEntityList(List<FielmappingDto> entity, Long buId) throws Exception {
//		String SUBMODULE = getModuleNameForLog() + " [getFields()] ";
		logger.info(getModuleNameForLog() + "--" + "Save Entity List .DataList[" + entity.toString() + "]");
		if (buId == null)
			throw new RuntimeException("BuId Not found while adding template!");
		try {
			for (FielmappingDto checkDto : entity) {
				if (checkDto.getModule() == null)
					throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Module Name is Mandatory",
							null);
			}
			List<FielmappingDto> fielmappingDtos = new ArrayList<>();
			Set<FieldsBuidMapping> fieldsBuidMappings = fieldsBuidMappingRepo.getAll();

			if (buId != null) {
				entity.stream().forEach(fielmappingDto -> {
					fielmappingDto.setBuid(buId);
				});
			} else {
				throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(),
						CommonConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
			}
			List<FieldsBuidMapping> listOne = fieldsBuidMappings.stream()
					.sorted(Comparator.comparing(FieldsBuidMapping::getFieldId).reversed())
					.collect(Collectors.toList());
			List<FielmappingDto> listTwo = entity.stream()
					.sorted(Comparator.comparing(FielmappingDto::getFieldId).reversed()).collect(Collectors.toList());
			List<FielmappingDto> buidMappingList = domainToDTOFielmappingDto(listOne);
			List<FielmappingDto> result = Stream
					.concat(buidMappingList.stream().filter(
							dto1 -> listTwo.stream().noneMatch(dto2 -> dto2.getFieldId().equals(dto1.getFieldId()))),
							listTwo.stream()
									.filter(dto2 -> buidMappingList.stream()
											.noneMatch(dto1 -> dto1.getFieldId().equals(dto2.getFieldId()))))
					.collect(Collectors.toList());

			result.stream().forEach(fielmappingDto -> {
				if (fielmappingDto.getId() != null && (buId != null && buId == fielmappingDto.getBuid())) {
					fieldsBuidMappingRepo.deleteById(fielmappingDto.getId());
				}
			});
			Set<FieldsBuidMapping> oldFieldsBuidMappings = new HashSet<>();
			List<FielmappingDto> listOneList = new ArrayList<>();
			List<FieldsBuidMapping> checkBuidMapping = buId != null ? fieldsBuidMappingRepo.findAllByBuid(buId)
					: null;
			// save Template
			if (checkBuidMapping != null && checkBuidMapping.size() == 0) {
				for (FieldsBuidMapping fieldsBuidMapping : fieldsBuidMappings) {
					FieldsBuidMapping mappings = new FieldsBuidMapping(fieldsBuidMapping);
//                    mappings.setBuid(buids.get(0));
					oldFieldsBuidMappings.add(mappings);
				}
				listOneList = entity.stream().filter(two -> oldFieldsBuidMappings.stream()
						.anyMatch(one -> (one.getBuid() == (two.getBuid())) && one.getScreen() == two.getScreen()
								&& one.getModule().equalsIgnoreCase(two.getModule())
								&& one.getIsMandatory() == two.getIsMandatory()))
						.collect(Collectors.toList());
				entity.removeAll(listOneList);
			}
			// edit / update field Template
			else if (checkBuidMapping != null && checkBuidMapping.size() != 0) {
				for (FieldsBuidMapping fieldsBuidMapping : fieldsBuidMappings) {
					FieldsBuidMapping mappings = new FieldsBuidMapping(fieldsBuidMapping);
					mappings.setBuid(buId);
					oldFieldsBuidMappings.add(mappings);
				}
				listOneList = entity.stream().filter(two -> oldFieldsBuidMappings.stream()
						.anyMatch(one -> (one.getBuid() == (two.getBuid())) && one.getScreen() == two.getScreen()
								&& one.getModule().equalsIgnoreCase(two.getModule())
								&& one.getIsMandatory() == two.getIsMandatory() && two.getDefaultMandatory()))
						.collect(Collectors.toList());
				entity.removeAll(listOneList);
				List<FielmappingDto> finalOne = domainToDTOFielmappingDto(fieldsBuidMappingRepo.findAllByBuid(buId));
				List<FielmappingDto> listTwoList = entity.stream()
						.filter(two -> finalOne.stream()
								.anyMatch(one -> (one.getBuid() == (two.getBuid()))
										&& one.getScreen() == (two.getScreen()) && one.getModule() == (two.getModule())
										&& one.getIsMandatory() == two.getIsMandatory()))
						.collect(Collectors.toList());
				entity.removeAll(listTwoList);
				// update isMandatory and moduleName
				List<FielmappingDto> DtoList = entity.stream().filter(
						two -> checkBuidMapping.stream().anyMatch(one -> (two.getFieldId().equals(one.getFieldId()))))
						.collect(Collectors.toList());
				List<FielmappingDto> updateEntity = new ArrayList<>();
				if (DtoList.size() > 0) {
					for (FielmappingDto fielmappingDto : DtoList) {
						for (FieldsBuidMapping fieldsBuidMapping : checkBuidMapping) {
							if (fielmappingDto.getFieldId().equals(fieldsBuidMapping.getFieldId())) {
								fielmappingDto.setId(fieldsBuidMapping.getId());
							}
						}
						updateEntity.add(fielmappingDto);

					}
					entity = entity.stream().map(fielmappingDto -> {
						updateEntity.stream()
								.filter(updateDto -> updateDto.getId() != null
										&& updateDto.getFieldId() == fielmappingDto.getFieldId())
								.findFirst().ifPresent(updateDto -> fielmappingDto.setId(updateDto.getId()));
						return fielmappingDto;
					}).collect(Collectors.toList());
				}
			}
			if (entity.size() != 0) {
				Set<FielmappingDto> fielmappingDtoList = new HashSet<>(entity);
				List<FielmappingDto> list = new ArrayList<>(fielmappingDtoList);
				List<FieldsBuidMapping> fieldsBuidMappingList = dtoToDomainFieldsBuidMappingList(list);
				fieldsBuidMappingList = fieldsBuidMappingRepo.saveAll(fieldsBuidMappingList);
				fielmappingDtos = domainToDTOFielmappingDto(fieldsBuidMappingList);
			}

			return fielmappingDtos;
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

	public List<FieldsBuidMapping> getbutypes(Long id) {
		List<FieldsBuidMapping> list = new ArrayList<>();
		String SUBMODULE = getModuleNameForLog() + " [getbutypes()] ";
		logger.info(getModuleNameForLog() + "--" + " Fetching BU Types .DataList[" + SUBMODULE.toString() + "]");
		try {
			list = fieldsBuidMappingRepo.findAllByBuid(id);
			return list;
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

//	@Override
//	public Object updateEntity(Object entity) throws Exception {
//		return null;
//	}
//
//	@Override
//	public void deleteEntity(Object entity) throws Exception {
//
//	}

	public List<FieldsDetailsDTO> getPlanFieldsByServiceId(Long serviceId) {
		String SUBMODULE = getModuleNameForLog() + " [getPlanFieldsByServiceId()] ";
		logger.info(
				getModuleNameForLog() + "--" + "Fetching PlanFieldsByServiceId .Data[" + SUBMODULE.toString() + "]");
		try {
			List<FieldServiceParamMapping> list = new ArrayList<>();
			List<ServiceParamMappingDTO> serviceParamMappingList = serviceParamMappingService
					.getParamsByServiceId(serviceId);
			List<Long> serviceparamIdList = serviceParamMappingList.stream()
					.map(ServiceParamMappingDTO::getServiceParamId).collect(Collectors.toList());
			List<FieldServiceParamMapping> byServiceParameterIdIn = fieldServiceParamMappingRepository
					.findAllByServiceParameterIdIn(serviceparamIdList);
			List<Fields> fieldsList = byServiceParameterIdIn.stream().map(FieldServiceParamMapping::getFields)
					.collect(Collectors.toList());
			List<Long> fieldsIdList = fieldsList.stream().map(Fields::getId).collect(Collectors.toList());
			List<ScreenFieldMapping> screenFieldMappingsList = screenFieldMappingRepository
					.findAllByFieldsIdInAndScreensId(fieldsIdList, 3L);

			byServiceParameterIdIn.sort(Comparator.comparing(mapping -> mapping.getFields().getId()));
			screenFieldMappingsList.sort(Comparator.comparing(mapping -> mapping.getFields().getId()));

			for (int i = 0; i < byServiceParameterIdIn.size(); i++) {
				FieldServiceParamMapping fieldServiceParamMapping = byServiceParameterIdIn.get(i);
				ServiceParamMappingDTO serviceParamMappingDTO = domainToDTOServiceParamMappingDTO(
						serviceParamMappingRepository.findByServiceidAndServiceParamId(serviceId,
								fieldServiceParamMapping.getServiceParameter().getId()));
				fieldServiceParamMapping.getFields().setDefaultValue(serviceParamMappingDTO.getValue());
				fieldServiceParamMapping.getFields().setMandatoryFlag(serviceParamMappingDTO.getIsMandatory());
			}

			list.addAll(byServiceParameterIdIn);

			// Process the first list using Stream API
			List<FieldsDetailsDTO> listFieldsDetailsDTO = list.stream().map(fieldServiceParamMapping -> {
				FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO();
				fieldsDetailsDTO.setId(fieldServiceParamMapping.getFields().getId());
				fieldsDetailsDTO.setFieldname(fieldServiceParamMapping.getFields().getFieldname());
				fieldsDetailsDTO.setName(fieldServiceParamMapping.getFields().getName());
				fieldsDetailsDTO.setDataType(fieldServiceParamMapping.getFields().getDataType());
				fieldsDetailsDTO.setModule(fieldServiceParamMapping.getModule());
				fieldsDetailsDTO.setIsMandatory(fieldServiceParamMapping.getIs_mandatory());
				fieldsDetailsDTO.setDefaultValue(fieldServiceParamMapping.getFields().getDefaultValue());
				fieldsDetailsDTO.setMandatoryFlag(fieldServiceParamMapping.getFields().getMandatoryFlag());
				return fieldsDetailsDTO;
			}).collect(Collectors.toList());

			// Process the second list using Stream API
			List<FieldsDetailsDTO> screenFieldsDetailsDTO = screenFieldMappingsList.stream().map(screenFieldMapping -> {
				FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO();
				fieldsDetailsDTO.setFieldType(screenFieldMapping.getFieldType());
				fieldsDetailsDTO.setEndpoint(screenFieldMapping.getEndpoint());
				fieldsDetailsDTO.setBackendrequired(screenFieldMapping.getBackendrequired());
				fieldsDetailsDTO.setDependantfieldName(screenFieldMapping.getDependantfieldName());
				fieldsDetailsDTO.setIsdependant(screenFieldMapping.getIsdependant());
				fieldsDetailsDTO.setIsdostrequest(screenFieldMapping.getIspostrequest());
				fieldsDetailsDTO.setRegex(screenFieldMapping.getRegex());
				return fieldsDetailsDTO;
			}).collect(Collectors.toList());

			List<FieldsDetailsDTO> finalfieldsDetailsDTOList = new ArrayList<>();

			for (int i = 0; i < listFieldsDetailsDTO.size(); i++) {
				FieldsDetailsDTO combinedList = new FieldsDetailsDTO();

				// 1st list(listFieldsDetailsDTO)
				combinedList.setId(listFieldsDetailsDTO.get(i).getId());
				combinedList.setFieldname(listFieldsDetailsDTO.get(i).getFieldname());
				combinedList.setName(listFieldsDetailsDTO.get(i).getName());
				combinedList.setDataType(listFieldsDetailsDTO.get(i).getDataType());
				combinedList.setModule(listFieldsDetailsDTO.get(i).getModule());
				combinedList.setIsMandatory(listFieldsDetailsDTO.get(i).getIsMandatory());
				combinedList.setDefaultValue(listFieldsDetailsDTO.get(i).getDefaultValue());
				combinedList.setMandatoryFlag(listFieldsDetailsDTO.get(i).getMandatoryFlag());

				// 2nd list(screenFieldsDetailsDTO)
				if (i < screenFieldsDetailsDTO.size()) {
					combinedList.setFieldType(screenFieldsDetailsDTO.get(i).getFieldType());
					combinedList.setEndpoint(screenFieldsDetailsDTO.get(i).getEndpoint());
					combinedList.setBackendrequired(screenFieldsDetailsDTO.get(i).getBackendrequired());
					combinedList.setDependantfieldName(screenFieldsDetailsDTO.get(i).getDependantfieldName());
					combinedList.setIsdependant(screenFieldsDetailsDTO.get(i).getIsdependant());
					combinedList.setIsdostrequest(screenFieldsDetailsDTO.get(i).getIsdostrequest());
					combinedList.setRegex(screenFieldsDetailsDTO.get(i).getRegex());
				}

				finalfieldsDetailsDTOList.add(combinedList);
			}

			finalfieldsDetailsDTOList.forEach(fieldsDetailsDTO -> {
				if (fieldsDetailsDTO.getMandatoryFlag() != null && fieldsDetailsDTO.getMandatoryFlag())
					fieldsDetailsDTO.setMandatoryFlag(true);
				else
					fieldsDetailsDTO.setMandatoryFlag(false);
			});
			return finalfieldsDetailsDTOList;

		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
	}

	public List<FieldsDetailsDTO> getAvailableAndBoundedFields(String screen, Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
		logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString()
				+ "]");
		List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
		List<FieldsDetailsDTO> finalDTOlist = new ArrayList<>();
		List<Screens> screensList = screenRepository.findIdByScreenname(screen);
		Long screenid = screensList.get(0).getId();
		try {
			if (buId != null) {
				QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
				BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);
				booleanExpression = booleanExpression.and(qScreenFieldMapping.isTempDisplay.eq(1));
				List<Fields> fields = new ArrayList<>();
				List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository
						.findAll(booleanExpression);
				List<Long> screenFieldIdsList = fieldsList.stream().map(ScreenFieldMapping::getFields)
						.collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
				screenFieldIdsList.stream().forEach(aLong -> {
					Fields lists = fieldRepo.findById(aLong).get();
					fields.add(lists);
				});
				List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
				QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
				BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull()
						.and(qFieldsBuidMapping.screen.eq(screenid)).and(qFieldsBuidMapping.buid.eq(buId))
						.and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

				List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo
						.findAll(booleanExpression1);
				List<Fields> boundedFields = fields.stream().filter(
						two -> fieldsBuidMappingList.stream().anyMatch(one -> (one.getFieldId().equals(two.getId()))))
						.collect(Collectors.toList());

				List<FieldsDTO> boundedFieldsDtos = domainToDTOFieldsDTO(boundedFields);
				List<ScreenFieldMapping> mappings = screenFieldMappingRepository.findAllByScreen(screenid).stream()
						.filter(item -> item.getIsTempDisplay() == 1).collect(Collectors.toList());
				List<ScreenFieldMappingDto> list2 = mappings.stream()
						.map(screenFieldMapping -> domainToDTOScreenFieldMapping(screenFieldMapping))
						.collect(Collectors.toList());
				for (int i = 0; i < mappings.size(); i++) {
					if (mappings.get(i).getId().equals(list2.get(i).getId())) {
						ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
						screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
						screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
						screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
						screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
						screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
						screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
						screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
						screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
						screenFieldMappingDto.setIsTempDisplay(
								mappings.get(i).getIsTempDisplay() != null && mappings.get(i).getIsTempDisplay() == 1
										? true
										: false);
						// screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
						list2.set(i, screenFieldMappingDto);
					}
				}

				// Set child of direct field if any
				List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields)
						.collect(Collectors.toList());
				List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
				List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream()
						.map(fields1 -> domainToDTOFields(fields1)).collect(Collectors.toList());

				for (int i = 0; i < nullparentFields.size(); i++) {
					List<ScreenFieldMapping> list = new ArrayList<>();
					List<ScreenFieldMapping> parentfields = screenFieldMappingRepository
							.findAllByParentfields(nullparentFields.get(i).getId(), screenid);
					list.addAll(parentfields);
					// Get fields from list of screen field mapping
					List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields)
							.collect(Collectors.toList()).stream().map(fields1 -> domainToDTOFields(fields1))
							.collect(Collectors.toList());
					if (parentfields.size() > 0) {
						for (int x = 0; x < parentfields.size(); x++) {
							FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
							list1.set(x, fieldsDTO);
						}
					}
					nullparentFields.get(i).setChild(list1);
				}

				for (FieldsDTO fieldsDTOOne : nullparentFields) {
					FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
					for (ScreenFieldMappingDto fieldMappingDto : list2) {
						if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())) {
							fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
							fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
							fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
							fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
							fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
							fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
							fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
							fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
							fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
							fieldsDetailsDTO.setIsTempDisplay(fieldMappingDto.getIsTempDisplay());
						}
					}
					for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
						if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
							fieldsDetailsDTO.setIsBounded(true);
							fieldsDetailsDTO.setIsTempDisplay(true);
						}
					}
					finalFieldsDtoList.add(fieldsDetailsDTO);
				}
				Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
				finalFieldsDtoList.forEach(p1 -> {
					Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
							.filter(p2 -> p2.getFieldId().equals(p1.getId())).findFirst();
					matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
					matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
					matchingFields.ifPresent(p2 -> p1.setIsTempDisplay(p1.getIsTempDisplay()));
				});
				finalFieldsDtoList.stream().filter(item -> (item.getIsTempDisplay() == true));
				List<FieldsBuidMapping> checkMandatory = fieldsBuidMappingRepo.findAllByNullBuids();
				if (checkMandatory.size() == 8) {
					finalDTOlist.addAll(getCustomerMandatoryFields(finalFieldsDtoList, screenid, buId));

				} else {
					finalDTOlist.addAll(finalFieldsDtoList);
				}
			} else
				throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
						CommonConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
		return finalDTOlist;
	}

	public List<FieldsDetailsDTO> getCustomerTemplate(String screen, Long buId) {
		return getAvailableAndBoundedFields("lead", buId).stream()
				.filter(fieldsDetailsDTO -> fieldsDetailsDTO.getIsBounded()).collect(Collectors.toList());
	}

	public List<ModuleWiseFieldsDto> getModuleWiseFields(String screen, Long buId) {

//		String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
		logger.info(
				getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + screen.toString() + "]");

		List<ModuleWiseFieldsDto> finalModuleWiseDto = new ArrayList<>();

		try {

			List<FieldsDetailsDTO> list1 = getAvailableAndBoundedFieldsFoModuleWise(screen, buId);

			List<CommonListDTO> commonListList;
			String screenType = "";
			if (screen.equalsIgnoreCase("customer"))
				screenType = TypeConstants.CUSTOMER_SCREEN;
			else if (screen.equalsIgnoreCase("plan"))
				screenType = TypeConstants.PLAN_SCREEN;
			else if (screen.equalsIgnoreCase("lead"))
				screenType = TypeConstants.LEAD_SCREEN;

			commonListList = commonListService.getCommonListByType(screenType);

			Collections.sort(commonListList, Comparator.comparing(CommonListDTO::getId));
			List<String> module = commonListList.stream().map(CommonListDTO::getText).collect(Collectors.toList());

			List<FieldsDetailsDTO> list2 = list1.stream()
					.filter(fieldsDetailsDTO -> fieldsDetailsDTO.getModule() != null).collect(Collectors.toList());

			module.stream().forEach(m -> {
				ModuleWiseFieldsDto moduleWiseFieldsDto1 = new ModuleWiseFieldsDto();
				List<FieldsDetailsDTO> fieldsDetailsDTOS = new ArrayList<>();
				list2.stream().forEach(fieldsDetailsDTO -> {
					if (!fieldsDetailsDTO.getModule().equalsIgnoreCase(CommonConstants.PERMANENT_ADDRESS_DETAILS)
							&& !fieldsDetailsDTO.getModule().equalsIgnoreCase(CommonConstants.PAYMENT_ADDRESS_DETAILS)
							&& !fieldsDetailsDTO.getModule()
									.equalsIgnoreCase(CommonConstants.PRESENT_ADDRESS_DETAILS)) {
						if (fieldsDetailsDTO.getModule().equalsIgnoreCase(m))
							fieldsDetailsDTOS.add(fieldsDetailsDTO);
					}
				});
				moduleWiseFieldsDto1.setModuleName(m);
				moduleWiseFieldsDto1.setFields(fieldsDetailsDTOS);
				finalModuleWiseDto.add(moduleWiseFieldsDto1);
			});
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
		List<ModuleWiseFieldsDto> finalList = new ArrayList<>();

		FieldsDTO fieldsDTO = domainToDTOFields(fieldRepo.findByFieldname("addressList"));
		FieldsDTO parentField = new FieldsDTO(
				screenFieldMappingRepository.findByFieldsIdAndScreensId(fieldsDTO.getId(), 1L));// fieldsDTO.getScreen()
		List<ScreenFieldMapping> screenFieldMappingList = screenFieldMappingRepository
				.findAllByParentfields(parentField.getId(), 1L);
		List<Long> fieldIdsList = screenFieldMappingList.stream().map(ScreenFieldMapping::getFields)
				.collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
		List<FieldsDTO> fieldsList = fieldRepo.findAllById(fieldIdsList).stream()
				.map(fields -> domainToDTOFields(fields)).collect(Collectors.toList());
		for (int i = 0; i < screenFieldMappingList.size(); i++) {
			FieldsDTO fieldsDTO1 = new FieldsDTO(screenFieldMappingList.get(i));
			fieldsList.set(i, fieldsDTO1);
		}
		parentField.setChild(fieldsList);

		for (ModuleWiseFieldsDto moduleWiseFieldsDto : finalModuleWiseDto) {
			if (moduleWiseFieldsDto.getModuleName().equalsIgnoreCase(CommonConstants.PRESENT_ADDRESS_DETAILS)) {
				parentField.setFieldname("presentAddress");
				moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
			} else if (moduleWiseFieldsDto.getModuleName().equalsIgnoreCase(CommonConstants.PAYMENT_ADDRESS_DETAILS)) {
				parentField.setFieldname("paymentAddress");
				moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
			} else if (moduleWiseFieldsDto.getModuleName()
					.equalsIgnoreCase(CommonConstants.PERMANENT_ADDRESS_DETAILS)) {
				parentField.setFieldname("permanentAddress");
				moduleWiseFieldsDto.getFields().add(new FieldsDetailsDTO(parentField));
			}
			finalList.add(moduleWiseFieldsDto);
		}
		return finalList;
	}

	public List<FieldsDetailsDTO> getCustomerMandatoryFields(List<FieldsDetailsDTO> fieldsDetailsDTOS, Long screenid,
			Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
		logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString()
				+ "]");
		List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
		try {
			if (buId != null) {
				QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
				BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);
				booleanExpression = booleanExpression.and(qScreenFieldMapping.isTempDisplay.eq(1));
				List<Fields> fields = new ArrayList<>();
				List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository
						.findAll(booleanExpression);
				List<Long> screenFieldIdsList = fieldsList.stream().filter(item -> item.getIsTempDisplay() == 1)
						.map(ScreenFieldMapping::getFields).collect(Collectors.toList()).stream().map(Fields::getId)
						.collect(Collectors.toList());
				screenFieldIdsList.stream().forEach(aLong -> {
					Fields lists = fieldRepo.findById(aLong).get();
					fields.add(lists);
				});
				List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
				QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
				BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull()
						.and(qFieldsBuidMapping.screen.eq(screenid)).and(qFieldsBuidMapping.buid.isNull())
						.and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

				List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo
						.findAll(booleanExpression1);
				List<Fields> boundedFields = fields.stream().filter(
						two -> fieldsBuidMappingList.stream().anyMatch(one -> (one.getFieldId().equals(two.getId()))))
						.collect(Collectors.toList());

				List<FieldsDTO> boundedFieldsDtos = domainToDTOFieldsDTO(boundedFields);
				List<Long> fIdsList = boundedFieldsDtos.stream().map(FieldsDTO::getId).collect(Collectors.toList());
				List<ScreenFieldMapping> mappings = screenFieldMappingRepository
						.findAllByFieldsIdInAndScreensId(fIdsList, screenid);
				List<ScreenFieldMappingDto> list2 = mappings.stream()
						.map(screenFieldMapping -> domainToDTOScreenFieldMapping(screenFieldMapping))
						.collect(Collectors.toList());
				for (int i = 0; i < mappings.size(); i++) {
					if (mappings.get(i).getId().equals(list2.get(i).getId())) {
						ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
						screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
						screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
						screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
						screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
						screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
						screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
						screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
						screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
						screenFieldMappingDto.setIsTempDisplay(
								mappings.get(i).getIsTempDisplay() != null && mappings.get(i).getIsTempDisplay() == 1
										? true
										: false);
						// screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
						list2.set(i, screenFieldMappingDto);
					}
				}

				// Set child of direct field if any
				List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields)
						.collect(Collectors.toList());
				List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
				List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream()
						.map(fields1 -> domainToDTOFields(fields1)).collect(Collectors.toList());

				for (int i = 0; i < nullparentFields.size(); i++) {
					List<ScreenFieldMapping> list = new ArrayList<>();
					List<ScreenFieldMapping> parentfields = screenFieldMappingRepository
							.findAllByParentfields(nullparentFields.get(i).getId(), screenid);
					list.addAll(parentfields);
					// Get fields from list of screen field mapping
					List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields)
							.collect(Collectors.toList()).stream().map(fields1 -> domainToDTOFields(fields1))
							.collect(Collectors.toList());
					if (parentfields.size() > 0) {
						for (int x = 0; x < parentfields.size(); x++) {
							FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
							list1.set(x, fieldsDTO);
						}
					}
					nullparentFields.get(i).setChild(list1);
				}

				for (FieldsDTO fieldsDTOOne : nullparentFields) {
					FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
					for (ScreenFieldMappingDto fieldMappingDto : list2) {
						if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())) {
							fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
							fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
							fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
							fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
							fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
							fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
							fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
							fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
							fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
							fieldsDetailsDTO.setIsTempDisplay(fieldMappingDto.getIsTempDisplay());
						}
					}
					for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
						if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
							fieldsDetailsDTO.setIsBounded(true);
							fieldsDetailsDTO.setDefaultMandatory(true);
							fieldsDetailsDTO.setIsTempDisplay(true);
						}
					}
					finalFieldsDtoList.add(fieldsDetailsDTO);
				}
				Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
				finalFieldsDtoList.forEach(p1 -> {
					Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
							.filter(p2 -> p2.getFieldId().equals(p1.getId())).findFirst();
					matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
					matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
					matchingFields.ifPresent(p2 -> p1.setIsTempDisplay(p1.getIsTempDisplay()));
				});
				finalFieldsDtoList.stream().filter(item -> (item.getIsTempDisplay() == true));
			} else
				throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
						CommonConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}

//        fieldsDetailsDTOS.addAll(finalFieldsDtoList);
		fieldsDetailsDTOS.forEach(p1 -> {
			Optional<FieldsDetailsDTO> matchingFields = finalFieldsDtoList.stream()
					.filter(p2 -> p2.getId().equals(p1.getId())).findFirst();
			matchingFields.ifPresent(p2 -> p1.setIsBounded(p2.getIsBounded()));
			matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
			matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
			matchingFields.ifPresent(p2 -> p1.setScreen(p2.getScreen()));
			matchingFields.ifPresent(p2 -> p1.setDefaultMandatory(p2.getDefaultMandatory()));
			matchingFields.ifPresent(p2 -> p1.setFieldname(p2.getFieldname()));
			matchingFields.ifPresent(p2 -> p1.setIsTempDisplay(p2.getIsTempDisplay()));
		});
		return fieldsDetailsDTOS;
	}

	public List<FieldsDetailsDTO> getAvailableAndBoundedFieldsFoModuleWise(String screen, Long buId) {
		String SUBMODULE = getModuleNameForLog() + " [getAvailableAndBoundedFields()] ";
		logger.info(getModuleNameForLog() + "--" + "Fetching AvailableAndBoundedFields .Data[" + SUBMODULE.toString()
				+ "]");
		List<FieldsDetailsDTO> finalFieldsDtoList = new ArrayList<>();
		List<Screens> screensList = screenRepository.findIdByScreenname(screen);
		Long screenid = screensList.get(0).getId();
		try {
			if (buId != null) {
				QScreenFieldMapping qScreenFieldMapping = QScreenFieldMapping.screenFieldMapping;
				BooleanExpression booleanExpression = qScreenFieldMapping.screens.id.eq(screenid);

				List<Fields> fields = new ArrayList<>();
				List<ScreenFieldMapping> fieldsList = (List<ScreenFieldMapping>) screenFieldMappingRepository
						.findAll(booleanExpression);
				List<Long> screenFieldIdsList = fieldsList.stream().map(ScreenFieldMapping::getFields)
						.collect(Collectors.toList()).stream().map(Fields::getId).collect(Collectors.toList());
				screenFieldIdsList.stream().forEach(aLong -> {
					Fields lists = fieldRepo.findById(aLong).get();
					fields.add(lists);
				});
				List<Long> fieldIdsList = fields.stream().map(Fields::getId).collect(Collectors.toList());
				QFieldsBuidMapping qFieldsBuidMapping = QFieldsBuidMapping.fieldsBuidMapping;
				BooleanExpression booleanExpression1 = qFieldsBuidMapping.isNotNull()
						.and(qFieldsBuidMapping.screen.eq(screenid)).and(qFieldsBuidMapping.buid.eq(buId))
						.and(qFieldsBuidMapping.fieldId.in(fieldIdsList));

				List<FieldsBuidMapping> fieldsBuidMappingList = (List<FieldsBuidMapping>) fieldsBuidMappingRepo
						.findAll(booleanExpression1);
				List<Fields> boundedFields = fields.stream().filter(
						two -> fieldsBuidMappingList.stream().anyMatch(one -> (one.getFieldId().equals(two.getId()))))
						.collect(Collectors.toList());

				List<FieldsDTO> boundedFieldsDtos = domainToDTOFieldsDTO(boundedFields);
				List<ScreenFieldMapping> mappings = screenFieldMappingRepository.findAllByScreen(screenid);
				List<ScreenFieldMappingDto> list2 = mappings.stream()
						.map(screenFieldMapping -> domainToDTOScreenFieldMapping(screenFieldMapping))
						.collect(Collectors.toList());
				for (int i = 0; i < mappings.size(); i++) {
					if (mappings.get(i).getId().equals(list2.get(i).getId())) {
						ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();
						screenFieldMappingDto.setFieldid(mappings.get(i).getFields().getId());
						screenFieldMappingDto.setIndexing(mappings.get(i).getIndexing());
						screenFieldMappingDto.setFieldType(mappings.get(i).getFieldType());
						screenFieldMappingDto.setBackendrequired(mappings.get(i).getBackendrequired());
						screenFieldMappingDto.setDependantfieldName(mappings.get(i).getDependantfieldName());
						screenFieldMappingDto.setEndpoint(mappings.get(i).getEndpoint());
						screenFieldMappingDto.setIsdependant(mappings.get(i).getIsdependant());
						screenFieldMappingDto.setIsdostrequest(mappings.get(i).getIspostrequest());
						// screenFieldMappingDto.setChild(mappings.get(i).getParentfields().getId());
						list2.set(i, screenFieldMappingDto);
					}
				}

				// Set child of direct field if any
				List<Fields> fieldsTest = mappings.stream().map(ScreenFieldMapping::getFields)
						.collect(Collectors.toList());
				List<Long> fieldIds = fieldsTest.stream().map(Fields::getId).collect(Collectors.toList());
				List<FieldsDTO> nullparentFields = fieldRepo.findAllById(fieldIds).stream()
						.map(fields1 -> domainToDTOFields(fields1)).collect(Collectors.toList());

				for (int i = 0; i < nullparentFields.size(); i++) {
					List<ScreenFieldMapping> list = new ArrayList<>();
					List<ScreenFieldMapping> parentfields = screenFieldMappingRepository
							.findAllByParentfields(nullparentFields.get(i).getId(), screenid);
					list.addAll(parentfields);
					// Get fields from list of screen field mapping
					List<FieldsDTO> list1 = list.stream().map(ScreenFieldMapping::getFields)
							.collect(Collectors.toList()).stream().map(fields1 -> domainToDTOFields(fields1))
							.collect(Collectors.toList());
					if (parentfields.size() > 0) {
						for (int x = 0; x < parentfields.size(); x++) {
							FieldsDTO fieldsDTO = new FieldsDTO(parentfields.get(x));
							list1.set(x, fieldsDTO);
						}
					}
					nullparentFields.get(i).setChild(list1);
				}

				for (FieldsDTO fieldsDTOOne : nullparentFields) {
					FieldsDetailsDTO fieldsDetailsDTO = new FieldsDetailsDTO(fieldsDTOOne);
					for (ScreenFieldMappingDto fieldMappingDto : list2) {
						if (fieldsDTOOne.getId().equals(fieldMappingDto.getFieldid())) {
							fieldsDetailsDTO.setFieldname(fieldsDTOOne.getFieldname());
							fieldsDetailsDTO.setIndexing(fieldMappingDto.getIndexing());
							fieldsDetailsDTO.setFieldType(fieldMappingDto.getFieldType());
							fieldsDetailsDTO.setEndpoint(fieldMappingDto.getEndpoint());
							fieldsDetailsDTO.setDependantfieldName(fieldMappingDto.getDependantfieldName());
							fieldsDetailsDTO.setBackendrequired(fieldMappingDto.getBackendrequired());
							fieldsDetailsDTO.setIsdependant(fieldMappingDto.getIsdependant());
							fieldsDetailsDTO.setIsdostrequest(fieldMappingDto.getIsdostrequest());
							fieldsDetailsDTO.setRegex(fieldMappingDto.getRegex());
						}
					}
					for (FieldsDTO fieldsDTOTwo : boundedFieldsDtos) {
						if (fieldsDTOOne.getFieldname().equals(fieldsDTOTwo.getFieldname())) {
							fieldsDetailsDTO.setIsBounded(true);
						}
					}
					finalFieldsDtoList.add(fieldsDetailsDTO);
				}
				Collections.sort(finalFieldsDtoList, Comparator.comparing(FieldsDetailsDTO::getIndexing));
				finalFieldsDtoList.forEach(p1 -> {
					Optional<FieldsBuidMapping> matchingFields = fieldsBuidMappingList.stream()
							.filter(p2 -> p2.getFieldId().equals(p1.getId())).findFirst();
					matchingFields.ifPresent(p2 -> p1.setModule(p2.getModule()));
					matchingFields.ifPresent(p2 -> p1.setIsMandatory(p2.getIsMandatory()));
				});

			} else
				throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
						CommonConstants.SAVE_RESTRICTED_TO_STAFF_WITH_MULTIPLE, null);
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
		return finalFieldsDtoList;
	}

	public CustomerAddressPojo getPresentAddressByCustomerId(Integer customerId) {
		String SUBMODULE = getModuleNameForLog() + " [getPresentAddressByCustomerId()] ";
		logger.info(getModuleNameForLog() + "--" + "Fetching PresentAddress By CustomerId .Data[" + SUBMODULE.toString()
				+ "]");
		CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
		try {
			CustomerAddress presentcustomerAddress = customerAddressRepository.findByAddressTypeAndCustomerId("Present",
					customerId);
			customerAddressPojo = domainToDTOCustomerAddressPojo(presentcustomerAddress);
		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
		return customerAddressPojo;
	}

	public List<FieldsDTO> getFieldDetailsByParam(Long paramId) {
		String SUBMODULE = getModuleNameForLog() + " [getFieldDetailsByParam()] ";
		logger.info(
				getModuleNameForLog() + "--" + "Fetching FieldDetails By Param .Data[" + SUBMODULE.toString() + "]");
		List<FieldsDTO> fieldsDTOList = new ArrayList<>();
		try {
			List<FieldServiceParamMapping> fieldServiceParamMappings = fieldServiceParamMappingRepository
					.findAllByServiceParameterIdIn(Arrays.asList(paramId));
			List<Fields> fieldsList = fieldServiceParamMappings.stream().map(FieldServiceParamMapping::getFields)
					.collect(Collectors.toList());
			List<Long> fieldsIdList = fieldsList.stream().map(Fields::getId).collect(Collectors.toList());
			List<ScreenFieldMapping> screenFieldMappingsList = screenFieldMappingRepository
					.findAllByFieldsIdInAndScreensId(fieldsIdList, 3L);

			for (ScreenFieldMapping screenFieldMapping : screenFieldMappingsList)
				fieldsDTOList.add(new FieldsDTO(screenFieldMapping));

		} catch (Exception exception) {
			logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
			throw new RuntimeException(exception.getMessage());
		}
		return fieldsDTOList;
	}

	public ScreenFieldMapping dtoToDomainScreenFieldMapping(ScreenFieldMappingDto dtoData) {
		if (dtoData == null) {
			return null;
		}

		ScreenFieldMapping screenFieldMapping = new ScreenFieldMapping();

		screenFieldMapping.setId(dtoData.getId());
		screenFieldMapping.setIndexing(dtoData.getIndexing());
		screenFieldMapping.setFieldType(dtoData.getFieldType());
		screenFieldMapping.setEndpoint(dtoData.getEndpoint());
		screenFieldMapping.setDependantfieldName(dtoData.getDependantfieldName());
		screenFieldMapping.setBackendrequired(dtoData.getBackendrequired());
		screenFieldMapping.setIsdependant(dtoData.getIsdependant());
		screenFieldMapping.setRegex(dtoData.getRegex());
		screenFieldMapping
				.setIsTempDisplay(dtoData.getIsTempDisplay() != null && dtoData.getIsTempDisplay() == true ? 1 : 0);
		return screenFieldMapping;
	}

	public ScreenFieldMappingDto domainToDTOScreenFieldMapping(ScreenFieldMapping data) {

		if (data == null) {
			return null;
		}
		ScreenFieldMappingDto screenFieldMappingDto = new ScreenFieldMappingDto();

		screenFieldMappingDto.setId(data.getId());
		screenFieldMappingDto.setIndexing(data.getIndexing());
		screenFieldMappingDto.setFieldType(data.getFieldType());
		screenFieldMappingDto.setEndpoint(data.getEndpoint());
		screenFieldMappingDto.setDependantfieldName(data.getDependantfieldName());
		screenFieldMappingDto.setBackendrequired(data.getBackendrequired());
		screenFieldMappingDto.setIsdependant(data.getIsdependant());
		screenFieldMappingDto.setRegex(data.getRegex());
		screenFieldMappingDto
				.setIsTempDisplay(data.getIsTempDisplay() != null && data.getIsTempDisplay() == 1 ? true : false);
		return screenFieldMappingDto;
	}

	public FielmappingDto domainToDTOFielmappingDto(FieldsBuidMapping data) {

		if (data == null) {
			return null;
		}

		FielmappingDto fielmappingDto = new FielmappingDto();

		fielmappingDto.setId(data.getId());
		fielmappingDto.setFieldId(data.getFieldId());
		fielmappingDto.setBuid(data.getBuid());
		fielmappingDto.setIsMandatory(data.getIsMandatory());
		fielmappingDto.setScreen(data.getScreen());
		fielmappingDto.setModule(data.getModule());
		fielmappingDto.setIsDeleted(data.getIsDeleted());
		fielmappingDto.setFieldName(data.getFieldName());
		fielmappingDto.setDataType(data.getDataType());
		fielmappingDto.setDefaultMandatory(data.getDefaultMandatory());

		return fielmappingDto;
	}

	public FieldsBuidMapping dtoToDomainFieldsBuidMapping(FielmappingDto dtoData) {

		if (dtoData == null) {
			return null;
		}

		FieldsBuidMapping fieldsBuidMapping = new FieldsBuidMapping();

		fieldsBuidMapping.setId(dtoData.getId());
		fieldsBuidMapping.setFieldId(dtoData.getFieldId());
		fieldsBuidMapping.setBuid(dtoData.getBuid());
		fieldsBuidMapping.setIsMandatory(dtoData.getIsMandatory());
		fieldsBuidMapping.setScreen(dtoData.getScreen());
		fieldsBuidMapping.setModule(dtoData.getModule());
		fieldsBuidMapping.setIsDeleted(dtoData.getIsDeleted());
		fieldsBuidMapping.setDataType(dtoData.getDataType());
		fieldsBuidMapping.setFieldName(dtoData.getFieldName());
		fieldsBuidMapping.setDefaultMandatory(dtoData.getDefaultMandatory());

		return fieldsBuidMapping;
	}

	public List<FieldsBuidMapping> dtoToDomainFieldsBuidMappingList(List<FielmappingDto> data) {

		if (data == null) {
			return null;
		}

		List<FieldsBuidMapping> list = new ArrayList<FieldsBuidMapping>(data.size());

		for (FielmappingDto fielmappingDto : data) {
			list.add(dtoToDomainFieldsBuidMapping(fielmappingDto));
		}

		return list;
	}

	public List<FielmappingDto> domainToDTOFielmappingDto(List<FieldsBuidMapping> data) {

		if (data == null) {
			return null;
		}

		List<FielmappingDto> list = new ArrayList<FielmappingDto>(data.size());

		for (FieldsBuidMapping fieldsBuidMapping : data) {
			list.add(domainToDTOFielmappingDto(fieldsBuidMapping));
		}

		return list;
	}

	public ServiceParamMappingDTO domainToDTOServiceParamMappingDTO(ServiceParamMapping data) {

		if (data == null) {
			return null;
		}

		ServiceParamMappingDTO serviceParamMappingDTO = new ServiceParamMappingDTO();

		serviceParamMappingDTO.setId(data.getId());
		serviceParamMappingDTO.setServiceid(data.getServiceid());
		serviceParamMappingDTO.setServiceParamId(data.getServiceParamId());
		serviceParamMappingDTO.setValue(data.getValue());
		serviceParamMappingDTO.setIsMandatory(data.getIsMandatory());

		return serviceParamMappingDTO;
	}

	public ServiceParamMapping dtoToDomainServiceParamMapping(ServiceParamMappingDTO dtoData) {

		if (dtoData == null) {
			return null;
		}

		ServiceParamMapping serviceParamMapping = new ServiceParamMapping();

		serviceParamMapping.setBuId(dtoData.getBuId());
		serviceParamMapping.setId(dtoData.getId());
		serviceParamMapping.setServiceid(dtoData.getServiceid());
		serviceParamMapping.setServiceParamId(dtoData.getServiceParamId());
		serviceParamMapping.setValue(dtoData.getValue());
		serviceParamMapping.setIsMandatory(dtoData.getIsMandatory());

		return serviceParamMapping;
	}

	public List<ServiceParamMappingDTO> domainToDTOServiceParamMapping(List<ServiceParamMapping> data) {

		if (data == null) {
			return null;
		}

		List<ServiceParamMappingDTO> list = new ArrayList<ServiceParamMappingDTO>(data.size());

		for (ServiceParamMapping serviceParamMapping : data) {
			list.add(domainToDTOServiceParamMappingDTO(serviceParamMapping));
		}

		return list;
	}

	public List<ServiceParamMapping> dtoToDomainServiceParamMapping(List<ServiceParamMappingDTO> data) {
		if (data == null) {
			return null;
		}
		List<ServiceParamMapping> list = new ArrayList<ServiceParamMapping>(data.size());

		for (ServiceParamMappingDTO serviceParamMappingDTO : data) {
			list.add(dtoToDomainServiceParamMapping(serviceParamMappingDTO));
		}
		return list;
	}

	public List<FieldsDTO> domainToDTOFieldsDTO(List<Fields> data) {

		if (data == null) {
			return null;
		}

		List<FieldsDTO> list = new ArrayList<FieldsDTO>(data.size());

		for (Fields fields : data) {
			list.add(domainToDTOFields(fields));
		}

		return list;
	}

	public List<Fields> dtoToDomainFields(List<FieldsDTO> data) {

		if (data == null) {
			return null;
		}

		List<Fields> list = new ArrayList<Fields>(data.size());
		for (FieldsDTO fieldsDTO : data) {
			list.add(dtoToDomainFields(fieldsDTO));
		}

		return list;
	}

	public FieldsDTO domainToDTOFields(Fields data) {

		if (data == null) {
			return null;
		}

		FieldsDTO fieldsDTO = new FieldsDTO();

		fieldsDTO.setId(data.getId());
		fieldsDTO.setFieldname(data.getFieldname());
		fieldsDTO.setName(data.getName());
		fieldsDTO.setDataType(data.getDataType());

		return fieldsDTO;
	}

	public Fields dtoToDomainFields(FieldsDTO dtoData) {
		if (dtoData == null) {
			return null;
		}

		Fields fields = new Fields();

		fields.setId(dtoData.getId());
		fields.setFieldname(dtoData.getFieldname());
		fields.setName(dtoData.getName());
		fields.setDataType(dtoData.getDataType());

		return fields;
	}

	public CustomerAddress dtoToDomainCustomerAddress(CustomerAddressPojo dto) {

		if (dto == null) {
			return null;
		}

		CustomerAddress customerAddress = new CustomerAddress();
		customerAddress.setId(dto.getId());
		customerAddress.setAddressType(dto.getAddressType());
		customerAddress.setAddress1(dto.getAddress1());
		customerAddress.setAddress2(dto.getAddress2());
		customerAddress.setLandmark(dto.getLandmark());
		customerAddress.setAreaId(dto.getAreaId());
		customerAddress.setPincodeId(dto.getPincodeId());
		customerAddress.setCityId(dto.getCityId());
		customerAddress.setStateId(dto.getStateId());
		customerAddress.setCountryId(dto.getCountryId());
		customerAddress.setFullAddress(dto.getFullAddress());
		customerAddress.setIsDelete(dto.getIsDelete());
		return customerAddress;
	}

	public CustomerAddressPojo domainToDTOCustomerAddressPojo(CustomerAddress domain) {

		if (domain == null) {
			return null;
		}

		CustomerAddressPojo customerAddressPojo = new CustomerAddressPojo();
		customerAddressPojo.setId(domain.getId());
		customerAddressPojo.setAddressType(domain.getAddressType());
		customerAddressPojo.setAddress1(domain.getAddress1());
		customerAddressPojo.setAddress2(domain.getAddress2());
		customerAddressPojo.setAreaId(domain.getAreaId());
		customerAddressPojo.setPincodeId(domain.getPincodeId());
		customerAddressPojo.setCityId(domain.getCityId());
		customerAddressPojo.setStateId(domain.getStateId());
		customerAddressPojo.setCountryId(domain.getCountryId());
		customerAddressPojo.setFullAddress(domain.getFullAddress());
		customerAddressPojo.setLandmark(domain.getLandmark());
		customerAddressPojo.setIsDelete(domain.getIsDelete());
		return customerAddressPojo;
	}
}
