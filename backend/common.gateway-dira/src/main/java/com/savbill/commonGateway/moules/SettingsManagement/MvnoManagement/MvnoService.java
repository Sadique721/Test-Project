package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import com.savbill.commonGateway.common.repository.ClientServiceRepository;
import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.exceptions.DataNotFoundException;
import com.savbill.commonGateway.core.mapper.CycleAvoidingMappingContext;
import com.savbill.commonGateway.core.repository.CustomRepository;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.exceptions.AlreadyExistException;
import com.savbill.commonGateway.moules.Customers.repository.CustomerRepository;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.mapper.ServiceAreaMapper;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.model.ServiceAreaDTO;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.service.ServiceAreaService;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordPolicy;
import com.savbill.commonGateway.moules.SettingsManagement.PasswordPolicy.PasswordRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffPasswordHistory.PasswordHistoryRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRel;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.*;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.*;
import com.savbill.commonGateway.rabbitmq.CustomMessage;
import com.savbill.commonGateway.rabbitmq.messages.MvnoStatusMessage;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MvnoService extends ExBaseAbstractService<MvnoDTO, Mvno, Long> {
    @Autowired
    private StaffUserMapper staffUserMapper;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private MvnoMapper mapper;
    @Autowired
    private MvnoRepository mvnoRepository;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private StaffRoleRelRepo staffRoleRelRepo;
    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    PasswordHistoryRepository passwordHistoryRepository;
    @Autowired
    PasswordRepository passwordRepository;

    @Autowired
    CustomRepository customRepository;
    @Autowired
    ClientServiceRepository entityRepository;

    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoService]";
    }

    private static String MODULE = " [MvnoService] ";

    @Override
    @Transactional
    public MvnoDTO saveEntity(MvnoDTO entity) throws Exception {

        MvnoDTO mvno = super.saveEntity(entity);
        mvno.setProfileId(entity.getProfileId());
        entity.setId(mvno.getId());
        //staffUserService.saveWithMvno(mvnoToStaff(entity));
        return mvno;
    }

//    @Transactional
//    public void saveStaffUserForNewMvno(MvnoDTO entity,Long id) throws Exception {
//        entity.setId(id);
//        staffUserService.saveWithMvno(mvnoToStaff(entity));
//    }

    @Override
    @Transactional
    public MvnoDTO updateEntity(MvnoDTO entity) throws Exception {
        MvnoDTO mvnoDTO = super.updateEntity(entity);
        entity.setId(mvnoDTO.getId());
        mvnoDTO.setProfileId(entity.getProfileId());
        staffUserService.updateWithMvno(mvnoToStaff(entity));
        return mvnoDTO;
    }

    public StaffUserPojo mvnoToStaff(MvnoDTO mvno) {
        StaffUserPojo staffPojo = new StaffUserPojo();
        try {
            StaffUser staff = staffUserService.getByUserName(mvno.getUsername());
            if (staff != null) {
                staff.setId(staff.getId());
                staffPojo = staffUserMapper.domainToDTO(staff, new CycleAvoidingMappingContext());
            }
            staffPojo.setUsername(mvno.getUsername());
            staffPojo.setPassword(mvno.getPassword());
            staffPojo.setFirstname(mvno.getName());
            staffPojo.setLastname(mvno.getName());
            staffPojo.setEmail(mvno.getEmail());
            staffPojo.setFullName(mvno.getFullName());
            staffPojo.setPhone(mvno.getPhone());
            staffPojo.setEventName(mvno.getEventName());
            staffPojo.setEventId(mvno.getEventId());
            staffPojo.setStatus(mvno.getStatus().toUpperCase());
            List<Long> roles = new ArrayList<>();
            roles.add(mvno.getRoleId());
//            staffPojo.setServiceAreaNameList(serviceAreaService.getAllEntities().stream().map(serviceAreaDTO -> serviceAreaMapper.dtoToDomain(serviceAreaDTO, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            staffPojo.setRoleIds(roles);
            staffPojo.setIsDelete(mvno.getIsDelete());
            staffPojo.setMvnoId(mvno.getId().intValue());
            staffPojo.setPartnerid(1);

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return staffPojo;
    }

    @Override
    public List<MvnoDTO> getAllEntities() throws Exception {
        try {
            return mvnoRepository.findAll().stream().filter(data -> !data.getDeleteFlag()).map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public MvnoDTO getEntityById(Long id) throws Exception {
        try {
            Mvno domain = (null == mvnoRepository.findById(id)) ? null : mvnoRepository.findById(id).get();
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            MvnoDTO dto = mapper.domainToDTO(mvnoRepository.findById(id).get(), new CycleAvoidingMappingContext());
//            Set MVNO Role
            Integer mvnoId = Math.toIntExact(id);
            StaffUser staffUser = staffUserRepository.findByUsernameAndIsDeleteIsFalseAndMvnoId(dto.getUsername(), mvnoId);
            if (staffUser != null) {
                Long staffId = Long.valueOf(staffUser.getId());
                StaffRoleRel staffRoleRel = staffRoleRelRepo.findByStaffId(staffId);
                if (staffRoleRel != null) {
                    dto.setRoleId(staffRoleRel.getRoleId());
                }
                if (dto.getCustAccountProfile() != null) {
                    dto.setProfileId(dto.getCustAccountProfile().getId());
                }

            }
            if (dto != null)
                return dto;
            return null;
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEntity(MvnoDTO entity) throws Exception {
        Mvno entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                throw new DataNotFoundException();
            }
            if (entity == null)
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            entityDomain.setDeleteFlag(true);
            mvnoRepository.save(entityDomain);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    //Pagination
    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Mvno> paginationList = getRepository().findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        try {
            return getListByPagination(generatePageRequest(page, size, sortBy, sortOrder));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Mvno> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        String SUBMODULE = MODULE + " [(search())] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterValue() != "") {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getMVNOList(searchModel.getFilterValue(), pageRequest);
                    }
                } else {
                    return this.getListByPageAndSizeAndSortByAndOrderBy(page, pageSize, sortBy, sortOrder, filterList);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    public GenericDataDTO getMVNOList(String name, PageRequest pageRequest) {
        String SUBMODULE = MODULE + " [getMVNOList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Page<Mvno> mvnoList;
            mvnoList = mvnoRepository.findAllByNameContainingIgnoreCaseAndIsDeleteIsFalse(name, pageRequest);
            if (null != mvnoList && 0 < mvnoList.getSize()) {
                makeGenericResponse(genericDataDTO, mvnoList);
            }
            if (mvnoList.getTotalElements() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Data Not Found.");
            }
        } catch (CustomValidationException ex) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    public List<MvnoDTO> getListOfMvnoByLatAndLon(Double lat, Double longt, Integer mvnoId) {
        Long startTimeMvno = System.currentTimeMillis() % 1000;
        System.out.println("********** Start Of fetch SA from Mvno in Method by lat and long time in millisecond: " + startTimeMvno);
        List<ServiceAreaDTO> serviceAreaDTOS = serviceAreaService.getServiceAreaIdByLatAndLong(lat, longt, mvnoId);
        Long fetchTimeSA = System.currentTimeMillis() % 1000;
        System.out.println("********** Start Of fetch SA from Mvno in Method by lat and long time in millisecond: " + fetchTimeSA + " diff from SA start and End: " + (startTimeMvno - fetchTimeSA));
        try {
            if (!CollectionUtils.isEmpty(serviceAreaDTOS)) {
                Long filterTimeMvno = System.currentTimeMillis() % 1000;
                System.out.println("********** Start Of Mvno from SA in Method by lat and long time in millisecond: " + filterTimeMvno);
                Set<Long> mvnoIds = serviceAreaDTOS.stream().filter(serviceAreaDTO -> serviceAreaDTO.getMvnoId() != null).mapToLong(ServiceAreaDTO::getMvnoId).boxed().collect(Collectors.toSet());
                Long filterendTimeMvno = System.currentTimeMillis() % 1000;
                System.out.println("********** Start Of Mvno from SA in Method by lat and long time in millisecond: " + filterendTimeMvno + " difference: " + (filterendTimeMvno - filterTimeMvno));

                QMvno qMvno = QMvno.mvno;
                BooleanExpression expression = qMvno.isNotNull();
                if (mvnoId == null || mvnoId == 1)
                    expression = expression.and(qMvno.isDelete.eq(false)).and(qMvno.id.in(mvnoIds)).and(qMvno.status.equalsIgnoreCase("Active"));
                else
                    expression = expression.and(qMvno.isDelete.eq(false)).and(qMvno.id.eq(Long.valueOf(mvnoId))).and(qMvno.status.equalsIgnoreCase("Active"));

                Long fetchMvnoFromDB = System.currentTimeMillis() % 1000;
                System.out.println("********** Start Of Mvno from SA in Method by lat and long time in millisecond: " + fetchMvnoFromDB);
                List<Mvno> list = (List<Mvno>) mvnoRepository.findAll(expression);
                Long fetchEndMvnoFromDB = System.currentTimeMillis() % 1000;
                System.out.println("********** Start Of Mvno from SA in Method by lat and long time in millisecond: " + fetchEndMvnoFromDB + " diff: " + (fetchEndMvnoFromDB - fetchMvnoFromDB));

                return mapper.domainToDTO(list, new CycleAvoidingMappingContext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            count = mvnoRepository.countByNameAndIsDeleteIsFalse(name);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyusernameAtSave(String username) throws Exception {
        boolean flag = false;
        if (username != null) {
            username = username.trim();
            Integer count;
            count = mvnoRepository.countByUsernameAndIsDeleteIsFalse(username);
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            count = mvnoRepository.countByNameAndIsDeleteIsFalse(name);
            if (count >= 1) {
                Integer countEdit;
                countEdit = mvnoRepository.countByNameAndIsDeleteIsFalseAndId(name, id);
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    public Mvno getMvnoById(Long id) {
        return mvnoRepository.findById(id).get();
    }

    public void updateMvnoRefForInvoice(Long mvnoId, Integer custInvoiceRefId) {
        Mvno mvno = getMvnoById(mvnoId);
        mvno.setCustInvoiceRefId(custInvoiceRefId);
        mvnoRepository.save(mvno);
    }


    public void UpdateMvnoStatus(MvnoStatusMessage mvnoStatusMessage) {
        try {
            if (!mvnoStatusMessage.getObjectList().isEmpty()) {
                Boolean flag = customRepository.updateMvnoStatus(mvnoStatusMessage.getObjectList(), mvnoStatusMessage.getStatus(), mvnoStatusMessage.getMvnoDeactivationFlag());
                List<Integer> longList = mvnoStatusMessage.getObjectList().stream().map(aLong -> aLong.intValue()).collect(Collectors.toList());
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }


    public void reActivateAllStaffAndCustomers(Integer MVNOID) {
        List<Integer> custIdList = customerRepository.findCustomerIdsbyMvnoDeactivationFlag(MVNOID);
        List<Integer> staffIdList = staffUserRepository.findStaffidByMvnoDeativationFlag(MVNOID);
        if (!custIdList.isEmpty()) {
            customRepository.updateMvnoStatusForCustomer(custIdList, CommonConstants.ACTIVE_STATUS, null);
        }
        if (!staffIdList.isEmpty()) {
            customRepository.updateMvnoStatusForStaff(staffIdList, CommonConstants.ACTIVE_STATUS, null);
        }
    }

    public void updateMvnoIdIsptoIsp(Integer oldMvno, Integer newMvno) {
        Optional<Mvno> oldMvnoEntity = mvnoRepository.findActiveById(oldMvno.longValue());
        Optional<Mvno> newMvnoEntity = mvnoRepository.findActiveById(newMvno.longValue());
        if (oldMvnoEntity.isPresent() && newMvnoEntity.isPresent()) {
            mvnoRepository.UpdateMvnoidISP(oldMvno, newMvno);
        } else {
            throw new CustomValidationException(401, "Unable to update Mvno ", null);
        }
    }

    public GenericDataDTO getMvnoAndIds() {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        List<MvnoNameAndIdDTO> mvnoDTOList = mvnoRepository.findMvnoNameAndIdsForListing();
        if (!mvnoDTOList.isEmpty()) {
            genericDataDTO.setDataList(mvnoDTOList);
        } else {
            genericDataDTO.setDataList(new ArrayList<>());
        }
        return genericDataDTO;
    }

    public String getMobileNumber(Integer mvno, String mobileNumber) {
        return entityRepository.getValuesByMvnoId(mvno, mobileNumber);
    }

    public void validateAndEncodePassword(MvnoDTO entityDTO) throws CustomValidationException, AlreadyExistException {
        if (entityDTO.getPassword() != null) {
            Long passwordPolicyId = entityDTO.getPasswordPolicyId();

            PasswordPolicy passwordPolicy = passwordRepository.findById(passwordPolicyId).get();

            if (passwordPolicy == null) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "No password policy found for the given MVNO ID", null);
            }

            // Validate minimum length
            if (entityDTO.getPassword().length() < passwordPolicy.getMin_length()) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Password is too short. Minimum length is " + passwordPolicy.getMin_length(), null);
            }

            // Validate maximum length
            if (entityDTO.getPassword().length() > passwordPolicy.getMax_length()) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Password is too long. Maximum length is " + passwordPolicy.getMax_length(), null);
            }

            // Validate pattern
            String passwordPattern = passwordPolicy.getPattern();
            if (passwordPattern != null && !passwordPattern.isEmpty()) {
                if (!entityDTO.getPassword().matches(passwordPattern)) {
                    throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), passwordPolicy.getPattern_description(), null);
                }
            }

            // Encode the password after all validations pass
//            PasswordEncoder encoder = new BCryptPasswordEncoder();
//            entityDTO.setPassword(encoder.encode(entityDTO.getPassword()));

            entityDTO.setPassword(entityDTO.getPassword());
        }
    }

    public List<Mvno> updateMvnoProfile(Long id) {
        List<Mvno> mvnoList = new ArrayList<>();
        try {
            mvnoList = mvnoRepository.findAllByCustAccountProfileId(id);
            if (mvnoList != null) {
                /** set default profile to Mvno list when exist profile get Inactive */
                for (Mvno mvno : mvnoList) {
                    mvno.setCustAccountProfile(mapper.mapper(Long.valueOf(1)));
                }
                mvnoRepository.saveAll(mvnoList);
                return mvnoList;
            }
        }catch (Exception e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + e.getMessage() + "Error while Update Default Profile in MvnoList. " + e.getMessage(), e);
        }
        return mvnoList;
    }


    public void saveMvnoCustRef(CustomMessage message){
        try{
            if(message.getCustomerData().get("mvnoId")!=null){
                Double mvnoId  = Double.valueOf(message.getCustomerData().get("mvnoId").toString());
                Mvno mvno = mvnoRepository.findById(mvnoId.longValue()).orElse(null);
                if (mvno!=null){
                    Double custId = Double.valueOf(message.getCustomerData().get("custid").toString());
                    mvno.setCustInvoiceRefId(custId.intValue());
                    mvnoRepository.save(mvno);
                }
            }
        }catch (Exception e){
            ApplicationLogger.logger.error("Unable to save mvno cust ref into mvno table");
        }
    }
}
