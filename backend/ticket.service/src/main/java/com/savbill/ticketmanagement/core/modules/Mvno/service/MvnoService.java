package com.savbill.ticketmanagement.core.modules.Mvno.service;


import com.savbill.ticketmanagement.core.exceptions.CustomValidationException;
import com.savbill.ticketmanagement.core.modules.Mvno.domain.Mvno;
import com.savbill.ticketmanagement.core.modules.Mvno.mapper.MvnoMapper;
import com.savbill.ticketmanagement.core.modules.Mvno.model.MvnoDTO;
import com.savbill.ticketmanagement.core.modules.Mvno.repository.MvnoRepository;
import com.savbill.ticketmanagement.core.service.ExBaseAbstractService;
import com.savbill.ticketmanagement.core.utillity.log.ApplicationLogger;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.SaveMvnoSharedDataMessage;
import com.savbill.ticketmanagement.rabbitmq.messages.DataShareMessage.UpdateMvnoSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MvnoService extends ExBaseAbstractService<MvnoDTO, Mvno,Long> {


    @Autowired
    MvnoRepository mvnoRepository;
    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoService]";
    }

@Transactional
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setId(mvnoSharedDataMessage.getId());
            mvno.setName(mvnoSharedDataMessage.getName());
            mvno.setUsername(mvnoSharedDataMessage.getUsername());
            mvno.setPassword(mvnoSharedDataMessage.getPassword());
            mvno.setSuffix(mvnoSharedDataMessage.getSuffix());
            mvno.setDescription(mvnoSharedDataMessage.getDescription());
            mvno.setEmail(mvnoSharedDataMessage.getEmail());
            mvno.setPhone(mvnoSharedDataMessage.getPhone());
            mvno.setStatus(mvnoSharedDataMessage.getStatus());
            mvno.setLogfile(mvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(mvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(mvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(mvnoSharedDataMessage.getIsDelete());
            mvnoRepository.save(mvno);
            ApplicationLogger.logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
           ApplicationLogger.logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }
@Transactional
    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            mvno.setId(updateMvnoSharedDataMessage.getId());
            mvno.setName(updateMvnoSharedDataMessage.getName());
            mvno.setUsername(updateMvnoSharedDataMessage.getUsername());
            mvno.setPassword(updateMvnoSharedDataMessage.getPassword());
            mvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
            mvno.setDescription(updateMvnoSharedDataMessage.getDescription());
            mvno.setEmail(updateMvnoSharedDataMessage.getEmail());
            mvno.setPhone(updateMvnoSharedDataMessage.getPhone());
            mvno.setStatus(updateMvnoSharedDataMessage.getStatus());
            mvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
            mvnoRepository.save(mvno);
            ApplicationLogger.logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }
//
//    @Autowired
//    private StaffUserService staffUserService;
//    @Autowired
//    private StaffUserMapper staffUserMapper;
//    @Autowired
//    private MvnoMapper mapper;
//    @Autowired
//    private MvnoRepository mvnoRepository;
//    @Autowired
//    private ClientServiceSrv clientServiceSrv;
//    @Autowired
//    private ServiceAreaService serviceAreaService;
//    @Autowired
//    private ServiceAreaMapper serviceAreaMapper;
//
//    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[MvnoService]";
//    }
//
//    @Override
//    @Transactional
//    public MvnoDTO saveEntity(MvnoDTO entity) throws Exception {
//        MvnoDTO mvno = super.saveEntity(entity);
//        entity.setId(mvno.getId());
//        staffUserService.saveWithMvno(mvnoToStaff(entity));
//        return mvno;
//    }
//
//    @Override
//    @Transactional
//    public MvnoDTO updateEntity(MvnoDTO entity) throws Exception {
//        MvnoDTO mvnoDTO = super.updateEntity(entity);
//        entity.setId(mvnoDTO.getId());
//        staffUserService.saveWithMvno(mvnoToStaff(entity));
//        return mvnoDTO;
//    }
//
//    private StaffUserPojo mvnoToStaff(MvnoDTO mvno) {
//        StaffUserPojo staffPojo = new StaffUserPojo();
//        try {
//            StaffUser staff = staffUserService.getByUserName(mvno.getUsername());
//            if (staff != null)
//                staff.setId(staff.getId());
//            staffPojo = staffUserMapper.domainToDTO(staff, new CycleAvoidingMappingContext());
//            staffPojo.setUsername(mvno.getUsername());
//            staffPojo.setPassword(mvno.getPassword());
//            staffPojo.setFirstname(mvno.getName());
//            staffPojo.setLastname(mvno.getName());
//            staffPojo.setEmail(mvno.getEmail());
//            staffPojo.setPhone(mvno.getPhone());
//            staffPojo.setStatus(mvno.getStatus().toUpperCase());
//            List<Integer> roles = new ArrayList();
//            roles.add(1);
////            staffPojo.setServiceAreaNameList(serviceAreaService.getAllEntities().stream().map(serviceAreaDTO -> serviceAreaMapper.dtoToDomain(serviceAreaDTO, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//            staffPojo.setRoleIds(roles);
//            staffPojo.setIsDelete(mvno.getIsDelete());
//            staffPojo.setMvnoId(mvno.getId().intValue());
//            staffPojo.setPartnerid(1);
//
//        } catch(Exception e) {
//            throw new RuntimeException(e.getMessage());
//        }
//        return staffPojo;
//    }
//
//    @Override
//    public List<MvnoDTO> getAllEntities() throws Exception {
//        try {
//            return mvnoRepository.findAll().stream().filter(data -> !data.getDeleteFlag()).map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Override
//    public MvnoDTO getEntityById(Long id) throws Exception {
//        try {
//            Mvno domain = (null == mvnoRepository.findById(id)) ? null : mvnoRepository.findById(id).get();
//            if (null == domain || domain.getDeleteFlag()) {
//                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
//            }
//            MvnoDTO dto = mapper.domainToDTO(mvnoRepository.findById(id).get(), new CycleAvoidingMappingContext());
//            if (dto != null)
//                return dto;
//            return null;
//        } catch (Exception ex) {
//            if (ex instanceof NoSuchElementException) {
//                throw new DataNotFoundException();
//            }
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    @Override
//    public void deleteEntity(MvnoDTO entity) throws Exception {
//        Mvno entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
//        ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
//        try {
//            if (entityDomain.getDeleteFlag()) {
//                throw new DataNotFoundException();
//            }
//            if(entity == null)
//                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//            entityDomain.setDeleteFlag(true);
//            mvnoRepository.save(entityDomain);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    //Pagination
//    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Mvno> paginationList = getRepository().findAll(pageRequest);
//        if (null != paginationList && 0 < paginationList.getSize()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
//        try {
//            return getListByPagination(generatePageRequest(page, size, sortBy, sortOrder));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }
//
//    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Mvno> paginationList) {
//        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        return genericDataDTO;
//    }
public void updateMvnoIsp(Long oldMvnoId , Long newMvnoId) {
    try {
        Mvno oldMvnoEntity = mvnoRepository.getOne(oldMvnoId);
        Mvno newMvnoEntity = mvnoRepository.getOne(newMvnoId);
        if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
            mvnoRepository.updatesMvnoidIsp(oldMvnoId , newMvnoId);
            ApplicationLogger.logger.info("MVNO updated successfully " + oldMvnoId +" to "+newMvnoId);
        } else {
            ApplicationLogger.logger.error("Unable to update MVNO ID "+ oldMvnoId);
        }
    } catch (Exception e) {
        ApplicationLogger.logger.error("Unexpected error while updating MVNO ID "+ oldMvnoId+ e);
    }
}
}
