package com.savbill.ticketmanagement.core.modules.BusinessVerticals.Service;

import com.savbill.ticketmanagement.core.modules.BusinessVerticals.DTO.BusinessVerticalsDTO;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.Mapper.BusinessVerticalsMpper;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.Respository.BusinessVerticalsMappingRepository;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.Respository.BusinessVerticalsRepository;
import com.savbill.ticketmanagement.core.modules.BusinessVerticals.domain.BusinessVerticals;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.SaveBusinessVerticalSharedDataMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdateBusinessVerticalSharedDataMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessVerticalsService extends ExBaseAbstractService<BusinessVerticalsDTO, BusinessVerticals, Long> {

    public BusinessVerticalsService(BusinessVerticalsRepository repository, BusinessVerticalsMpper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[BusinessVerticalsService]";
    }

    @Autowired
    BusinessVerticalsRepository repository;

    @Autowired
    BusinessVerticalsMappingRepository businessVerticalsMappingRepository;


    @Autowired
    BusinessVerticalsMpper mapper;

    private final Logger log = Logger.getLogger(BusinessVerticalsService.class);
//    @Override
//    public boolean duplicateVerifyAtSave(String vname) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (vname != null) {
//            vname = vname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(vname);
//            else count = repository.duplicateVerifyAtSave(vname, mvnoIds);
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    public BusinessVerticals getById(Long id) {
//        return repository.findById(id).get();
//    }
//
//    public boolean duplicateVerifyAtEdit(String vname, Long id) throws Exception {
//        boolean flag = false;
//        List mvnoIds = Arrays.asList(getMvnoIdFromCurrentStaff(), 1);
//        if (vname != null) {
//            vname = vname.trim();
//            Integer count;
//            if (getMvnoIdFromCurrentStaff() == 1) count = repository.duplicateVerifyAtSave(vname);
//            else count = repository.duplicateVerifyAtSave(vname, mvnoIds);
//            if (count >= 1) {
//                Integer countEdit;
//                if (getMvnoIdFromCurrentStaff() == 1)
//                    countEdit = repository.duplicateVerifyAtEdit(vname, id);
//                else countEdit = repository.duplicateVerifyAtEdit(vname, id, mvnoIds);
//                if (countEdit == 1) {
//                    flag = true;
//                }
//            } else {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getBusinessVerticalsByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    public GenericDataDTO getBusinessVerticalsByName(String vname, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getPolicyByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QBusinessVerticals qBusinessVerticals = QBusinessVerticals.businessVerticals;
//            Page<BusinessVerticals> businessVerticalsList = null;
//            BooleanExpression booleanExpression = qBusinessVerticals.isNotNull()
//                    .and(qBusinessVerticals.isDeleted.eq(false))
//                    .and(qBusinessVerticals.vname.likeIgnoreCase("%" + vname + "%").or(qBusinessVerticals.status.containsIgnoreCase(vname)));
//            if(getMvnoIdFromCurrentStaff() == 1) {
//                businessVerticalsList = repository.findAll(booleanExpression, pageRequest);
//            }else {
//                booleanExpression = booleanExpression.and(qBusinessVerticals.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//                businessVerticalsList = repository.findAll(booleanExpression, pageRequest);
//            }
//            if (null != businessVerticalsList && 0 < businessVerticalsList.getSize()) {
//                makeGenericResponse(genericDataDTO, businessVerticalsList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<BusinessVerticals> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, "createdate", sortOrder);
//        if (getMvnoIdFromCurrentStaff() == 1)
//            paginationList = repository.findAll(pageRequest);
//        else
//            paginationList = repository.findAll(pageRequest,Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    public void deleteBusinessVerticalMapping(Long id) {
//        QBusinessVerticalsMapping qBusinessVerticalsMapping = QBusinessVerticalsMapping.businessVerticalsMapping;
//        BooleanExpression booleanExpression = qBusinessVerticalsMapping.isDeleted.eq(false).and(qBusinessVerticalsMapping.businessVerticals.id.eq(id));
//        List<BusinessVerticalsMapping> businessVerticalsMappings = IterableUtils.toList(businessVerticalsMappingRepository.findAll(booleanExpression));
//        for (int i=0; i<businessVerticalsMappings.size(); i++) {
//            businessVerticalsMappings.get(i).setIsDeleted(true);
//            businessVerticalsMappingRepository.saveAll(businessVerticalsMappings);
//        }
//    }
//
//    public List<BusinessVerticalsDTO> getAllVerticalsByRegion(List<Long> regionId) {
//        QBusinessVerticalsMapping qBusinessVerticalsMapping = QBusinessVerticalsMapping.businessVerticalsMapping;
//        BooleanExpression exp = qBusinessVerticalsMapping.isNotNull().and(qBusinessVerticalsMapping.isDeleted.eq(false));
//        exp = exp.and(qBusinessVerticalsMapping.region.id.in(regionId));
//       List<BusinessVerticalsMapping> businessVerticalsMappings = (List<BusinessVerticalsMapping>) businessVerticalsMappingRepository.findAll(exp);
//        List<Long> result = new ArrayList<>();
//        for (int i = 0; i < businessVerticalsMappings.size(); i++) {
//            result.add(businessVerticalsMappings.get(i).getBusinessVerticals().getId());
//        }
//        List<BusinessVerticals> businessVerticalsList = repository.findAllByIdIn(result);
//        return businessVerticalsList.stream().map(x->mapper.domainToDTO(x,new CycleAvoidingMappingContext())).collect(Collectors.toList())
//                .stream().filter(y->y.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)).collect(Collectors.toList())
//                .stream().filter(z->z.getIsDeleted().equals(false)).collect(Collectors.toList());
//    }

    public void saveBusinessVertical(SaveBusinessVerticalSharedDataMessage message){
    try {
    // Create a new business vertical object
        BusinessVerticals businessVertical = new BusinessVerticals();

        // Set values from the message
        businessVertical.setId(message.getId());
        businessVertical.setVname(message.getVname());
        businessVertical.setBuregionidList(message.getBuregionidList());
        businessVertical.setStatus(message.getStatus());
        businessVertical.setIsDeleted(message.getIsDeleted());
        businessVertical.setMvnoId(message.getMvnoId());

        // Save the business vertical using the repository
        repository.save(businessVertical);
    }catch (Exception e){
        e.getMessage();
    }

    }

    public void updateBusinessVertical(UpdateBusinessVerticalSharedDataMessage message){
        try {
            // Create a new business vertical object
            BusinessVerticals businessVertical = repository.findById(message.getId())
                    .orElse(null);

            // Assign values from the message to the created object
            businessVertical.setId(message.getId());
            businessVertical.setVname(message.getVname());
            businessVertical.setBuregionidList(message.getBuregionidList());
            businessVertical.setStatus(message.getStatus());
            businessVertical.setIsDeleted(message.getIsDeleted());
            businessVertical.setMvnoId(message.getMvnoId());

            // Save the business vertical using the repository
            repository.save(businessVertical);
        }catch (Exception e){
            e.getMessage();
        }

    }

    public void saveBusinessVerticalData(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recordInJson = (String) records.value();
        try {
            SaveBusinessVerticalSharedDataMessage message = objectMapper.readValue(recordInJson, SaveBusinessVerticalSharedDataMessage.class);
            BusinessVerticals businessVertical = new BusinessVerticals();
            businessVertical.setId(message.getId());
            businessVertical.setVname(message.getVname());
            businessVertical.setBuregionidList(message.getBuregionidList());
            businessVertical.setStatus(message.getStatus());
            businessVertical.setIsDeleted(message.getIsDeleted());
            businessVertical.setMvnoId(message.getMvnoId());
            repository.save(businessVertical);

        }catch (Exception e){
            log.error("Error message : "+e.getMessage());
        }
    }

    public void updateBusinessVerticalDate(ConsumerRecord<String, Object> records) {
        ObjectMapper objectMapper = new ObjectMapper();
        String recordInJson = (String) records.value();
        try {
            UpdateBusinessVerticalSharedDataMessage message = objectMapper.readValue(recordInJson, UpdateBusinessVerticalSharedDataMessage.class);
            BusinessVerticals businessVertical = repository.findById(message.getId()).orElse(null);
            if(businessVertical!=null){
                businessVertical.setId(message.getId());
                businessVertical.setVname(message.getVname());
                businessVertical.setBuregionidList(message.getBuregionidList());
                businessVertical.setStatus(message.getStatus());
                businessVertical.setIsDeleted(message.getIsDeleted());
                businessVertical.setMvnoId(message.getMvnoId());

                // Save the business vertical using the repository
                repository.save(businessVertical);
            }else {
                log.info("Data not found ");
            }
        }catch (Exception e){
            log.error("Error message :" + e.getMessage());
        }
    }
}
