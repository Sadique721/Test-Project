package com.savbill.revenuemanagement.core.entity.role.service;



import com.savbill.revenuemanagement.core.acl.domain.CustomACLEntry;
import com.savbill.revenuemanagement.core.acl.model.CustomACLEntryDTO;
import com.savbill.revenuemanagement.core.entity.role.domain.Role;
import com.savbill.revenuemanagement.core.entity.role.mapper.RoleMapper;
import com.savbill.revenuemanagement.core.entity.role.model.RoleDTO;
import com.savbill.revenuemanagement.core.entity.role.repository.RoleRepository;
import com.savbill.revenuemanagement.core.exceptions.CustomValidationException;
import com.savbill.revenuemanagement.core.service.ExBaseAbstractService;
import com.savbill.revenuemanagement.core.utillity.log.ApplicationLogger;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveRoleSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService extends ExBaseAbstractService<RoleDTO, Role, Long> {

    @Autowired
    private RoleRepository entityRepository;
//
//    @Autowired
//    private MessagesPropertyConfig messagesProperty;
//
//    @Autowired
//    private CustomACLService customACLService;
//
    public RoleService(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
    }
//
    @Override
    public String getModuleNameForLog() {
        return "[RoleService]";
    }



    public void saveRoleEntity(SaveRoleSharedDataMessage message) throws Exception {
        try {
            Role role = new Role();
            List<CustomACLEntry> customACLEntryList = new ArrayList<>();
            role.setId(message.getId());
            role.setRolename(message.getRolename());
            role.setStatus(message.getStatus());
            role.setSysRole(message.getSysRole());
            for (CustomACLEntry item : message.getAclEntry()) {
                CustomACLEntry customACLEntry = new CustomACLEntry();
                customACLEntry.setId(item.getId());
                customACLEntry.setClassid(item.getClassid());
                customACLEntry.setRoleid(item.getRole());
                customACLEntry.setPermit(item.getPermit());
                customACLEntryList.add(customACLEntry);
            }
//            role.setAclEntry(customACLEntryList);
            role.setIsDelete(message.getIsDelete());
            role.setMvnoId(message.getMvnoId());
            role.setLcoId(message.getLcoId());
            entityRepository.save(role);
            ApplicationLogger.logger.info("Role created successfully with name " + message.getRolename());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create role with name " + message.getRolename(), e.getMessage());
        }
    }

    public void updateRoleEntity(SaveRoleSharedDataMessage message) throws Exception {
        try {
            Role role = entityRepository.findById(message.getId()).orElse(null);


            role.setId(message.getId());
            role.setRolename(message.getRolename());
            role.setStatus(message.getStatus());
            role.setSysRole(message.getSysRole());

            List<CustomACLEntry> customACLEntryList = new ArrayList<>();
            for (CustomACLEntry item : message.getAclEntry()) {

                CustomACLEntry customACLEntry = new CustomACLEntry();
                customACLEntry.setId(item.getId());
                customACLEntry.setClassid(item.getClassid());
                if (message.getIsDelete().equals(false)) {
                    customACLEntry.setRoleid(item.getRole());
                } else if (message.getIsDelete().equals(true)) {
                    customACLEntry.setRoleid(null);
                }
                customACLEntry.setPermit(item.getPermit());
                customACLEntryList.add(customACLEntry);
            }
//            role.setAclEntry(customACLEntryList);
            role.setIsDelete(message.getIsDelete());
            role.setMvnoId(message.getMvnoId());
            role.setLcoId(message.getLcoId());
            entityRepository.save(role);
            ApplicationLogger.logger.info("Role updated successfully with name " + message.getRolename());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to update role with name " + message.getRolename(), e.getMessage());
        }
    }
//
//    @Override
//    public List<RoleDTO> getAllEntities() throws Exception {
//        List<RoleDTO> roleDTOS = new ArrayList<>();
//        //if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//        roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatus(CommonConstants.ACTIVE_STATUS));
//        //} else {
//        //    roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatusAndIdIn(CommonConstants.ACTIVE_STATUS, CommonUtils.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
//        //}
//        if(getLoggedInUser().getLco())
//            return roleDTOS.stream().filter(data->data.getLcoId()!=null && data.getLcoId()==getLoggedInUser().getPartnerId()).filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//        else
//            return roleDTOS.stream().filter(data->data.getLcoId()==null).filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//    }
//
//    public List<RoleDTO> getAllByIdIn(List<Long> idList) throws Exception {
//        List<RoleDTO> roleDTOS = new ArrayList<>();
//        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
//            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(idList));
//        } else {
//            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(CommonUtils.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
//        }
//        return roleDTOS.stream().filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
//    }
//
//    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Role> paginationList = getRepository().findAll(pageRequest);
//        if (null != paginationList && 0 < paginationList.getSize()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
//
//    @Override
//    public RoleDTO getEntityById(Long aLong) throws Exception {
//        return convertRoleModelToRolePojo(entityRepository.findById(aLong).get());
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
//    @Override
//    public RoleDTO saveEntity(RoleDTO pojo) {
//        Role obj = convertRolePojoToRoleModel(pojo);
//        obj = saveRole(obj);
//        pojo = convertRoleModelToRolePojo(obj);
//        return pojo;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
//    public Role saveRole(Role role) {
//        role.setMvnoId(getMvnoIdFromCurrentStaff());
//        Role save = null;
//
//            if (role.getAclEntry() != null) {
//                for (CustomACLEntry item : role.getAclEntry()) {
//                    item.setRole(role);
//                }
//                save = entityRepository.save(role);
//                customACLService.reloadCache();
//            } else {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), Constants.NO_ROLE_SELECTED, null);
//            }
//
//        /*for (CustomACLEntry item : role.getAclEntry()) {
//            item.setRole(role);
//        }*/
//        //Role save = entityRepository.save(role);
//        //customACLService.reloadCache();
//        return save;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
//    @Override
//    public RoleDTO updateEntity(RoleDTO entity) throws Exception {
//        return saveEntity(entity);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
//    @Override
//    public void deleteEntity(RoleDTO entity) throws Exception {
//        if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId()))
//            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
//        entity.setIsDelete(true);
//        entityRepository.save(convertRolePojoToRoleModel(entity));
//    }
//
    public Role convertRolePojoToRoleModel(RoleDTO roleDTO) {
        Role role = null;
        if (roleDTO != null) {
            role = new Role();
            if (roleDTO.getId() != null) {
                role.setId(roleDTO.getId());
            }
            role.setRolename(roleDTO.getRolename());
            role.setStatus(roleDTO.getStatus());
            role.setCreatedate(roleDTO.getCreatedate());
//            role.setAclEntry(null);
            role.setIsDelete(roleDTO.getDelete());
            role.setSysRole(roleDTO.getSysRole());
            if (roleDTO.getMvnoId() != null) {
                role.setMvnoId(roleDTO.getMvnoId());
            }
            if (roleDTO.getAclEntryPojoList() != null && roleDTO.getAclEntryPojoList().size() > 0) {
                List<CustomACLEntry> aclEntryList = new ArrayList<>();
                CustomACLEntry aclEntry = null;
                for (CustomACLEntryDTO aclEntryPojo : roleDTO.getAclEntryPojoList()) {
                    aclEntry = new CustomACLEntry();
                    if (aclEntryPojo.getId() != null) {
                        aclEntry.setId(aclEntryPojo.getId());
                    }
                    aclEntry.setClassid(aclEntryPojo.getClassid());
                    aclEntry.setPermit(aclEntryPojo.getPermit());
                    if (aclEntryPojo.getRoleId() != null) {
                        //aclEntry.setRole(entityRepository.getOne(aclEntryPojo.getRoleId()));
                    }
                    aclEntryList.add(aclEntry);
                }
                role.setLcoId(roleDTO.getLcoId());
//                role.setAclEntry(aclEntryList);
            }
        }
        return role;
    }
//
    public RoleDTO convertRoleModelToRolePojo(Role role) {
        RoleDTO pojo = null;
        if (role != null) {
            pojo = new RoleDTO();
            pojo.setId(role.getId());
            pojo.setRolename(role.getRolename());
            pojo.setStatus(role.getStatus());
            pojo.setCreatedate(role.getCreatedate());
            pojo.setDelete(role.getIsDelete());
            pojo.setSysRole(role.getSysRole());
            pojo.setCreatedate(role.getCreatedate());
            pojo.setCreatedById(role.getCreatedById());
            pojo.setCreatedByName(role.getCreatedByName());
            pojo.setLastModifiedById(role.getLastModifiedById());
            pojo.setUpdatedate(role.getUpdatedate());
            pojo.setLastModifiedByName(role.getLastModifiedByName());
            if (role.getMvnoId() != null) {
                pojo.setMvnoId(role.getMvnoId());
            }
//            if (role.getAclEntry() != null && role.getAclEntry().size() > 0) {
//                List<CustomACLEntryDTO> custAclList = new ArrayList<CustomACLEntryDTO>();
//                CustomACLEntryDTO customACLEntryDTO = null;
//                for (CustomACLEntry customACLEntry : role.getAclEntry()) {
//                    customACLEntryDTO = new CustomACLEntryDTO();
//                    if (customACLEntry.getId() != null) {
//                        customACLEntryDTO.setId(customACLEntry.getId());
//                    }
//                    customACLEntryDTO.setClassid(customACLEntry.getClassid());
//                    customACLEntryDTO.setPermit(customACLEntry.getPermit());
////                    if (customACLEntry.getRole()!= null) {
////                        customACLEntryDTO.setRoleId(Math.toIntExact(customACLEntry.getRole().getId()));
////                    }
//                    custAclList.add(customACLEntryDTO);
//                }
//                pojo.setLcoId(role.getLcoId());
//                pojo.setAclEntryPojoList(custAclList);
//            }
        }
        return pojo;
    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
//    public List<RoleDTO> convertResponseModelIntoPojo(List<Role> roleList) {
//        List<RoleDTO> pojoListRes = new ArrayList<>();
//        if (roleList != null && roleList.size() > 0) {
//            for (Role role : roleList) {
//                if (role.getIsDelete() == false) pojoListRes.add(convertRoleModelToRolePojo(role));
//                else continue;
//            }
//        }
//        return pojoListRes;
//    }
//
//    public void validateRequest(RoleDTO pojo, Integer operation) {
//
//        if (pojo == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
//        }
//        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
//            if (pojo.getId() != null) {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
//            }
//        }
//        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
//        }
//        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
//        }
//    }
//
//    @Override
//    public void excelGenerate(Workbook workbook) throws Exception {
//        Sheet sheet = workbook.createSheet("Role");
//        createExcel(workbook, sheet, RoleDTO.class, null);
//    }
//
//    @Override
//    public void pdfGenerate(Document doc) throws Exception {
//        createPDF(doc, RoleDTO.class, null);
//    }
//
//    public int getLoggedInUserId() {
//        int loggedInUserId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
//            }
//        } catch (Exception e) {
//            loggedInUserId = -1;
//        }
//        return loggedInUserId;
//    }
//
//    public int getLoggedInUserPartnerId() {
//        int partnerId = -1;
//        try {
//            SecurityContext securityContext = SecurityContextHolder.getContext();
//            if (null != securityContext.getAuthentication()) {
//                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
//            }
//        } catch (Exception e) {
//            partnerId = -1;
//        }
//        return partnerId;
//    }
//
//    public GenericDataDTO getRoleByName(String name, PageRequest pageRequest) {
//        String SUBMODULE = getModuleNameForLog() + " [getRoleByName()] ";
//        try {
//            GenericDataDTO genericDataDTO = new GenericDataDTO();
//            QRole qRole = QRole.role;
//            BooleanExpression booleanExpression = qRole.isNotNull().and(qRole.isDelete.eq(false)).and(qRole.rolename.likeIgnoreCase("%" + name + "%"));
//            if(getLoggedInUser().getLco())
//                booleanExpression=booleanExpression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
//            else
//                booleanExpression=booleanExpression.and(qRole.lcoId.isNull());
//
//            if (getMvnoIdFromCurrentStaff() != 1) {
//                booleanExpression = booleanExpression.and(qRole.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
//               // booleanExpression = booleanExpression.or(qRole.mvnoId.eq(1));
//            }
//            Page<Role> roleList = entityRepository.findAll(booleanExpression, pageRequest);
//            if (0 < roleList.getSize()) {
//                makeGenericResponse(genericDataDTO, roleList);
//            }
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//    @Override
//    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Role> paginationList) {
//        genericDataDTO.setDataList(paginationList.getContent().stream().map(this::convertRoleModelToRolePojo).collect(Collectors.toList()));
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
//        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
//        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
//        genericDataDTO.setTotalPages(paginationList.getTotalPages());
//        return genericDataDTO;
//    }
//
//    @Override
//    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        try {
//            PageRequest pageRequest = generatePageRequest(page, pageSize, "rolename", sortOrder);
//            if (null != filterList && 0 < filterList.size()) {
//                for (GenericSearchModel searchModel : filterList) {
//                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
//                        return getRoleByName(searchModel.getFilterValue(), pageRequest);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//        }
//        return null;
//    }
//
//	@Override
//    public boolean duplicateVerifyAtSave(String name) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count == 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//
//    public boolean duplicateVerifyAtEdit(String name, Long id) throws Exception {
//        boolean flag = false;
//        if (name != null) {
//        	name = name.trim();
//            Integer count;
//            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
//            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//            if (count >= 1) {
//                Integer countEdit;
//                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
//                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
//    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        Page<Role> paginationList = null;
//        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
//        QRole qRole=QRole.role;
//        BooleanExpression expression=qRole.isNotNull();
//        if(getLoggedInUser().getLco())
//            expression=expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
//        else
//            expression=expression.and(qRole.lcoId.isNull());
//            expression=expression.and(qRole.isDelete.eq(false));
//
//        if(getMvnoIdFromCurrentStaff() == 1)
//            paginationList = entityRepository.findAll(expression,pageRequest);
//        else {
//            expression=expression.and(qRole.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
//            paginationList = entityRepository.findAll(expression,pageRequest);
//        }
//        if (null != paginationList && 0 < paginationList.getContent().size()) {
//            makeGenericResponse(genericDataDTO, paginationList);
//        }
//        return genericDataDTO;
//    }
}
