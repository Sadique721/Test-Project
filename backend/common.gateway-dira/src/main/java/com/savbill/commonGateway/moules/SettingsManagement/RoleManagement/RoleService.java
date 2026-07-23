package com.savbill.commonGateway.moules.SettingsManagement.RoleManagement;

import com.savbill.commonGateway.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.savbill.commonGateway.common.CommonUtils;
import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.constants.AclConstants;
import com.savbill.commonGateway.constants.Constants;
import com.savbill.commonGateway.constants.SearchConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.core.dto.GenericDataDTO;
import com.savbill.commonGateway.core.dto.GenericSearchModel;
import com.savbill.commonGateway.core.exceptions.CustomValidationException;
import com.savbill.commonGateway.core.service.ExBaseAbstractService;
import com.savbill.commonGateway.core.utillity.log.ApplicationLogger;
import com.savbill.commonGateway.kafka.KafkaConstant;
import com.savbill.commonGateway.kafka.KafkaMessageData;
import com.savbill.commonGateway.kafka.KafkaMessageSender;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole.StaffAccessibleRoleMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.moules.acl.domain.CustomACLEntry;
import com.savbill.commonGateway.moules.acl.domain.RoleACLEntry;
import com.savbill.commonGateway.moules.acl.model.AclMenuStructureDTO;
import com.savbill.commonGateway.moules.acl.model.CustomACLEntryDTO;
import com.savbill.commonGateway.moules.acl.model.RoleACLEntryDTO;
import com.savbill.commonGateway.moules.acl.repository.RoleAclRepository;
import com.savbill.commonGateway.moules.acl.service.AclService;
import com.savbill.commonGateway.moules.acl.service.CustomACLService;
import com.savbill.commonGateway.rabbitmq.MessageSender;
import com.savbill.commonGateway.rabbitmq.messages.CommonRoleMessage;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import com.savbill.commonGateway.spring.MessagesPropertyConfig;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService extends ExBaseAbstractService<RoleDTO, Role, Long> {

    @Autowired
    private RoleRepository entityRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CustomACLService customACLService;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private RoleAclRepository roleAclRepository;

    @Autowired
    private AclService aclService;
    @Autowired
    private StaffRoleRelRepo staffRoleRelRepo;

    @Autowired
    private StaffAccessibleRoleMappingRepository staffAccessibleRoleMappingRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    StaffUserRepository staffUserRepository;

    public RoleService(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleService]";
    }

    @Override
    public List<RoleDTO> getAllEntities() throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        //if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
        roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatus(CommonConstants.ACTIVE_STATUS));
        //} else {
        //    roleDTOS = convertResponseModelIntoPojo(entityRepository.findByStatusAndIdIn(CommonConstants.ACTIVE_STATUS, CommonUtils.getPartnerStaffRoleIdList().stream().map(Long::valueOf).collect(Collectors.toList())));
        //}
        if(getLoggedInUser().getLco())
            return roleDTOS.stream().filter(data->data.getLcoId()!=null && data.getLcoId()==getLoggedInUser().getPartnerId()).filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
        else if (getMvnoIdFromCurrentStaff() == 1)
            return roleDTOS.stream().filter(roleDTO-> roleDTO.getLcoId()==null  && (roleDTO.getMvnoId() == 1)).collect(Collectors.toList());
        else
            return roleDTOS.stream().filter(data->data.getLcoId()==null).filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }
    public List<RoleDTO> getEntities(String productType) throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        QRole qRole = QRole.role;
        BooleanExpression expression = qRole.isNotNull().and(qRole.product.equalsIgnoreCase(productType)).and(qRole.isDelete.eq(false));
        List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(getLoggedInUserId()).orElse(null);
        StaffUser staffUser= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);

        if(assignableRoleIds!=null && assignableRoleIds.size()>0){
            if(getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment()!=null) {
                List<Role> assignableRoles = entityRepository.findByDepartmentIdWithoutPagination(staffUser.getDepartment());
                roleDTOS=convertRoleModelListToRolePojoList(assignableRoles);
            }else {
                Integer lcoId = getLoggedInUser().getLco() ? getLoggedInUser().getPartnerId() : null;
                if (lcoId != null) {
                    List<Role> assignableRoles = entityRepository.findRolesByRoleIdsAndLcoId(assignableRoleIds, lcoId, productType);
                    roleDTOS = convertRoleModelListToRolePojoList(assignableRoles);
                }
                List<Role> assignableRoles = entityRepository.findRolesByRoleIds(assignableRoleIds, productType);
                roleDTOS = convertRoleModelListToRolePojoList(assignableRoles);
            }
        }else {
            if (getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment() != null) {
                List<Role> assignableRoles = entityRepository.findByDepartmentIdWithoutPagination(staffUser.getDepartment());
                roleDTOS = convertRoleModelListToRolePojoList(assignableRoles);
            } else {
                if (getMvnoIdFromCurrentStaff() != null && getMvnoIdFromCurrentStaff() != 1)
                    expression = expression.and(qRole.mvnoId.in(Arrays.asList(1, getMvnoIdFromCurrentStaff())));
                if (getLoggedInUser().getLco())
                    expression = expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
                List<Role> roles = (List<Role>) entityRepository.findAll(expression);
                List<Role> activeRoles = roles.stream().sorted(Comparator.comparing(Role::getId).reversed()).filter(role -> role.getStatus().equalsIgnoreCase("Active")).collect(Collectors.toList());
                roleDTOS = convertRoleModelListToRolePojoList(activeRoles);//convertResponseModelIntoPojo(entityRepository.findByStatus(CommonConstants.ACTIVE_STATUS));
            }
        }
        return roleDTOS;
    }

    public List<RoleDTO> getRolesByLoggedInId(String productType) throws Exception {
        List<Role> roles = new ArrayList<>();
        List<RoleDTO> roleDTOS = new ArrayList<>();
        QRole qRole = QRole.role;
        BooleanExpression expression = qRole.isNotNull().and(qRole.product.equalsIgnoreCase(productType)).and(qRole.isDelete.eq(false));
        StaffUser staffUser= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);
        List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(getLoggedInUserId()).orElse(null);
        if(assignableRoleIds!=null && assignableRoleIds.size()>0){
            List<Role> assignableRoles = entityRepository.findRolesByRoleIds(assignableRoleIds,productType);
            roleDTOS = convertRoleModelListToRolePojoList(assignableRoles);
        }else {
            if (getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment() != null) {
                List<Role> assignableRoles = entityRepository.findByDepartmentIdWithoutPagination(staffUser.getDepartment());
                roleDTOS = convertRoleModelListToRolePojoList(assignableRoles);
            } else {

                if (getMvnoIdFromCurrentStaff() != null && getMvnoIdFromCurrentStaff() != 1)
                    expression = expression.and(qRole.mvnoId.in(Arrays.asList(1, getMvnoIdFromCurrentStaff())));
                if (getLoggedInUser().getLco())
                    expression = expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
                roles = (List<Role>) entityRepository.findAll(expression);
                List<Role> activeRoles = roles.stream().filter(role -> role.getStatus().equalsIgnoreCase("Active")).sorted(Comparator.comparing(Role::getId).reversed()).collect(Collectors.toList());
                roleDTOS = convertRoleModelListToRolePojoList(activeRoles);//convertRes
            }
        }
        return roleDTOS;
    }

    public List<RoleDTO> getAllByIdIn(List<Long> idList) throws Exception {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if (getLoggedInUserPartnerId() == CommonConstants.DEFAULT_PARTNER_ID) {
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(idList));
        } else {
            List<Long> idlist=CommonUtils.getPartnerStaffRoleIdList().stream().map(id->id.longValue()).collect(Collectors.toList());
            idList.addAll(idlist);
            roleDTOS = convertResponseModelIntoPojo(entityRepository.findAllById(idList));
        }
        return roleDTOS.stream().filter(roleDTO -> roleDTO.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 || roleDTO.getMvnoId() == getMvnoIdFromCurrentStaff().intValue()).collect(Collectors.toList());
    }

    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Role> paginationList = getRepository().findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public RoleDTO getEntityById(Long aLong) throws Exception {
        RoleDTO roleDTO = convertRoleModelToRolePojo(entityRepository.findById(aLong).get());
        List<AclMenuStructureDTO> aclMenuStructureDTOS = aclService.createAclMenuStructure("iwf");
        List<Integer> menuIds = roleDTO.getAclMenu().stream().map(RoleACLEntryDTO::getMenuid).collect(Collectors.toList());
        roleDTO.setAclMenus(updateAclMenuStructureDto(aclMenuStructureDTOS, menuIds));
        return roleDTO;
    }

    public RoleDTO getProductEntityById(Long aLong, String productName) throws Exception {
        RoleDTO roleDTO = convertRoleModelToRolePojo(entityRepository.findById(aLong).get());
        List<Integer> menuIds = roleAclRepository.findMenoIdFromRole(getLoggedInUser().getRoleIds().get(0));//roleDTO.getAclMenu().stream().map(RoleACLEntryDTO::getMenuid).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(menuIds)) {
            List<AclMenuStructureDTO> aclMenuStructureDTOS = aclService.createAclMenuStructure(productName, menuIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
            if(!CollectionUtils.isEmpty(aclMenuStructureDTOS)) {
                roleDTO.setAclMenus(updateAclMenuStructureDto(aclMenuStructureDTOS, roleDTO.getAclMenu().stream().map(RoleACLEntryDTO::getMenuid).collect(Collectors.toList())));
            }
        }
        return roleDTO;
    }


    public RoleDTO updateRole(RoleDTO roleDTO) throws Exception {
//        List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleandProduct(roleDTO.getId(),roleDTO.getProduct());
//        roleAclRepository.deleteInBatch(roleACLEntries);
        long start1 = System.currentTimeMillis();
        roleAclRepository.deleteByRoleAndProduct(roleDTO.getId(), roleDTO.getProduct());
        long start2 = System.currentTimeMillis();
        long firstTime= start2-start1;
        System.out.println("=============== Time Taken in reoleACLRepository:"+ firstTime+"=================");
        Role obj = convertRolePojoToRoleModel(roleDTO);
        long start3 = System.currentTimeMillis();
        obj = saveRole(obj);
        long start4 = System.currentTimeMillis();
        long ThirdTime= start4-start3;
        System.out.println("=============== Time Taken in saveRole:"+ ThirdTime+"=================");
        roleDTO = convertRoleModelToRolePojo(obj);
        return roleDTO;
    }

    public List<AclMenuStructureDTO> updateAclMenuStructureDto(List<AclMenuStructureDTO> aclMenuStructureDTOS, List<Integer> menuIds) {
        for (AclMenuStructureDTO aclMenuStructureDTO: aclMenuStructureDTOS) {
            if(menuIds.contains(aclMenuStructureDTO.getData().getId().intValue())) {
                aclMenuStructureDTO.getData().setIsSelected(true);
            } else {
                aclMenuStructureDTO.getData().setIsSelected(false);
            }
            List<AclMenuStructureDTO> child = aclMenuStructureDTO.getChildren();
            if(!CollectionUtils.isEmpty(child)) {
                child = updateAclMenuStructureDto(child, menuIds);
                aclMenuStructureDTO.setChildren(child);
            }
        }
        return aclMenuStructureDTOS;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
    @Override
    public RoleDTO saveEntity(RoleDTO pojo) {
        Role obj = convertRolePojoToRoleModel(pojo);
        obj = saveRole(obj);
        pojo = convertRoleModelToRolePojo(obj);
        return pojo;
    }
    public Role getRolebyId(Long id ){
        return entityRepository.getOne(id);
    }

    public RoleDTO saveRoleWithAcl(RoleDTO pojo) {
        Role obj = convertRolePojoToRoleModel(pojo);
        obj = saveRole(obj);
        pojo = convertRoleModelToRolePojo(obj);
        return pojo;
    }


    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_ADD + "\")")
    public Role saveRole(Role role) {
        role.setMvnoId(getMvnoIdFromCurrentStaff());
        Role save = entityRepository.save(role);
        new Thread(() -> {
            //run background code here
            customACLService.updateCache(role.getId());
        }).start();
        return save;
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_EDIT + "\")")
    @Override
    public RoleDTO updateEntity(RoleDTO entity) throws Exception {
        return saveEntity(entity);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_DELETE + "\")")
    @Override
    public void deleteEntity(RoleDTO entity) throws Exception {
        if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.getMvnoId()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        entity.setIsDelete(true);
        List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(entity.getId());
        roleAclRepository.deleteAll(roleACLEntries);
        entityRepository.save(convertRolePojoToRoleModel(entity));
    }
    @Override
    public boolean deleteVerification(Integer id)throws Exception{
        boolean flag = false;
        Integer count = staffRoleRelRepo.countByRoleId(Long.valueOf(id));
        if(count==0){
            flag=true;
        }
        return flag;
    }
    public RoleDTO deleteRole(Long id) throws Exception {
        Optional<Role> entity = entityRepository.findById(id);
        if(entity == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff().intValue() == entity.get().getMvnoId()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        entity.get().setIsDelete(true);
        List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(entity.get().getId());
        roleAclRepository.deleteAll(roleACLEntries);
        entityRepository.save(entity.get());
        customACLService.updateCache(id);
        return convertRoleModelToRolePojo(entity.get());
    }

    public Role convertRolePojoToRoleModel(RoleDTO roleDTO) {
        Role role;
        if (roleDTO != null) {
            role = new Role();
            if (roleDTO.getId() != null) {
                role.setId(roleDTO.getId());
            }
            role.setRolename(roleDTO.getRolename());
            role.setStatus(roleDTO.getStatus());
            role.setProduct(roleDTO.getProduct());
            role.setCreatedate(roleDTO.getCreatedate());
            role.setLcoId(roleDTO.getLcoId());
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

//                role.setAclEntry(aclEntryList);

            }
            if(!CollectionUtils.isEmpty(roleDTO.getAclMenu())) {
                List<RoleACLEntry> aclEntry = roleDTO.getAclMenu().stream().map(aclEntryDTO -> new RoleACLEntry(role, aclEntryDTO.getCode(), aclEntryDTO.getMenuid(),aclEntryDTO.getId(),roleDTO.getProduct())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(aclEntry))
                    role.setRoleAclEntry(aclEntry);

            }

        } else {
            role = null;
        }
        return role;
    }

    public List<RoleDTO> convertRoleModelListToRolePojoList(List<Role> roles) {
        List<RoleDTO> list = new ArrayList<>();
        for (Role role: roles) {
            RoleDTO pojo = null;
            if (role != null) {
                pojo = new RoleDTO();
                pojo.setId(role.getId());
                pojo.setRolename(role.getRolename());
                pojo.setStatus(role.getStatus());
                pojo.setProduct(role.getProduct());
                pojo.setAssignableRoleId(role.getId());
                pojo.setAssignableRoleName(role.getRolename());
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
                list.add(pojo);
            }
        }
        return list;
    }

    public RoleDTO convertRoleModelToRolePojo(Role role) {
        RoleDTO pojo = null;
        if (role != null) {
            pojo = new RoleDTO();
            pojo.setId(role.getId());
            pojo.setRolename(role.getRolename());
            pojo.setStatus(role.getStatus());
            pojo.setProduct(role.getProduct());
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

            if(!CollectionUtils.isEmpty(role.getRoleAclEntry())) {
                List<RoleACLEntryDTO> roleACLEntryDTOS = role.getRoleAclEntry().stream().map(roleACLEntry ->
                        new RoleACLEntryDTO(roleACLEntry.getId(), roleACLEntry.getCode(), roleACLEntry.getMenuid(),roleACLEntry.getRole().getId().intValue(),roleACLEntry.getProduct())).collect(Collectors.toList());
                pojo.setAclMenu(roleACLEntryDTOS);
            }
        }
        return pojo;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_ROLE_ALL + "\",\"" + AclConstants.OPERATION_ROLE_VIEW + "\")")
    public List<RoleDTO> convertResponseModelIntoPojo(List<Role> roleList) {
        List<RoleDTO> pojoListRes = new ArrayList<>();
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                if (role.getIsDelete() == false) pojoListRes.add(convertRoleModelToRolePojo(role));
                else continue;
            }
        }
        return pojoListRes;
    }

    public void validateRequest(RoleDTO pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook) throws Exception {
        Sheet sheet = workbook.createSheet("Role");
        createExcel(workbook, sheet, RoleDTO.class, null);
    }

    @Override
    public void pdfGenerate(Document doc) throws Exception {
        createPDF(doc, RoleDTO.class, null);
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }

    public GenericDataDTO getRoleByName(String name, PageRequest pageRequest) {
        String SUBMODULE = getModuleNameForLog() + " [getRoleByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QRole qRole = QRole.role;
            BooleanExpression booleanExpression = qRole.isNotNull().and(qRole.isDelete.eq(false)).and(qRole.rolename.likeIgnoreCase("%" + name + "%"));
            if(getLoggedInUser().getLco())
                booleanExpression=booleanExpression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression=booleanExpression.and(qRole.lcoId.isNull());

            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qRole.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
                // booleanExpression = booleanExpression.or(qRole.mvnoId.eq(1));
            }
            Page<Role> roleList = entityRepository.findAll(booleanExpression, pageRequest);
            if (0 < roleList.getSize()) {
                makeGenericResponse(genericDataDTO, roleList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public GenericDataDTO getRoleByRoleName(String name, PageRequest pageRequest, String productType) {
        String SUBMODULE = getModuleNameForLog() + " [getRoleByName()] ";
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            QRole qRole = QRole.role;
            BooleanExpression booleanExpression = qRole.isNotNull().and(qRole.isDelete.eq(false)).and(qRole.rolename.likeIgnoreCase("%" + name + "%"));
            if(getLoggedInUser().getLco())
                booleanExpression=booleanExpression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression=booleanExpression.and(qRole.lcoId.isNull());
            if(productType!= null){
                booleanExpression = booleanExpression.and(qRole.product.containsIgnoreCase(productType));
            }

            if (getMvnoIdFromCurrentStaff() != 1) {
                booleanExpression = booleanExpression.and(qRole.mvnoId.in(getMvnoIdFromCurrentStaff(), 1));
            }
            Page<Role> roleList = entityRepository.findAll(booleanExpression, pageRequest);
            if (0 < roleList.getSize()) {
                makeGenericResponse(genericDataDTO, roleList);
            }
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Role> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(this::convertRoleModelToRolePojo).collect(Collectors.toList()));
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
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "rolename", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getRoleByName(searchModel.getFilterValue(), pageRequest);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }
    public GenericDataDTO searchByProduct(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder, String productType) {
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "rolename", sortOrder);
            if (null != filterList && 0 < filterList.size()) {
                for (GenericSearchModel searchModel : filterList) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getRoleByRoleName(searchModel.getFilterValue(), pageRequest,  productType);
                    }
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
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
            if(getMvnoIdFromCurrentStaff() == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
            if (count >= 1) {
                Integer countEdit;
                if(getMvnoIdFromCurrentStaff() == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Role> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        QRole qRole=QRole.role;
        BooleanExpression expression=qRole.isNotNull();
        if(getLoggedInUser().getLco())
            expression=expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
        else
            expression=expression.and(qRole.lcoId.isNull());
        expression=expression.and(qRole.isDelete.eq(false));

        if(getMvnoIdFromCurrentStaff() == 1)
            paginationList = entityRepository.findAll(expression,pageRequest);
        else {
            expression=expression.and(qRole.mvnoId.in(1,getMvnoIdFromCurrentStaff()));
            paginationList = entityRepository.findAll(expression,pageRequest);
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
         return genericDataDTO;
    }

    public GenericDataDTO getpagination(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String productType, boolean isloggedInUser) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Role> paginationList = null;
        PageRequest pageRequest = generatePageRequest(page, size, sortBy, sortOrder);
        QRole qRole=QRole.role;
        BooleanExpression expression=qRole.isNotNull();
        List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(getLoggedInUserId()).orElse(null);
        StaffUser staffUser= staffUserRepository.findStaffUserStaffById(getLoggedInUserId()).orElse(null);
        if(getMvnoIdFromCurrentStaff() == 1 && staffUser.getDepartment()!=null) {
            paginationList = entityRepository.findByDepartmentId(staffUser.getDepartment(), pageRequest);
        }else {
            if (assignableRoleIds != null && assignableRoleIds.size() > 0) {
                pageRequest = generatePageRequest(page, size, "roleid", sortOrder);
                Integer lcoId = getLoggedInUser().getLco() ? getLoggedInUser().getPartnerId() : null;
                if (lcoId != null) {
                    paginationList = entityRepository.findRolesByRoleIdsAndLcoIdPaginationWise(pageRequest, assignableRoleIds, lcoId, productType);
                }
                paginationList = entityRepository.findRolesByRoleIdsPaginationWise(pageRequest, assignableRoleIds, productType);
            } else {
                if (getLoggedInUser().getLco())
                    expression = expression.and(qRole.lcoId.eq(getLoggedInUser().getPartnerId()));
                else
                    expression = expression.and(qRole.lcoId.isNull());
                expression = expression.and(qRole.isDelete.eq(false));

                if (productType != null) {
                    expression = expression.and(qRole.product.containsIgnoreCase(productType));
                }
//        if(isloggedInUser) {
//            expression = expression.and(qRole.createdById.eq(getLoggedInUserId()));
//        }

                if (getMvnoIdFromCurrentStaff() == 1)
                    paginationList = entityRepository.findAll(expression, pageRequest);
                else {
                    expression = expression.and(qRole.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
                    paginationList = entityRepository.findAll(expression, pageRequest);
                }
            }
        }
        if (null != paginationList && 0 < paginationList.getContent().size()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    public Role sharedData(RoleDTO role) {
        Role roleEntity = convertRolePojoToRoleModel(role);
        if (roleEntity.getCreatedById() != null) {
            roleEntity.setCreatedById(roleEntity.getCreatedById());
        } else {
            roleEntity.setCreatedById(getLoggedInUserId());
        }
        if (roleEntity.getLastModifiedById() != null) {
            roleEntity.setLastModifiedById(roleEntity.getLastModifiedById());
        } else {
            roleEntity.setLastModifiedById(getLoggedInUserId());
        }
        Role role1 = new Role();
        role1.setId(role.getId());
        List<CustomACLEntry> customACLEntries = new ArrayList<>();
        if(role.getAclMenu() != null && role.getAclMenu().size() > 0) {
//            List<RoleACLEntryDTO> roleACLEntries = new ArrayList<RoleACLEntryDTO>();
//            roleACLEntries.addAll(role.getAclMenu());
//            roleEntity.setAclEntryDTOList(roleACLEntries);
        }

        return roleEntity;
    }


    public void sendToAllMicroservices(RoleDTO roleDTO) {
        try{
            CommonRoleMessage message = new CommonRoleMessage(roleDTO);
            message.setAclMenus(roleDTO.getAclMenu());
            System.out.println(message);
            //messageSender.send(message, SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_REVENUE);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_CMS);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_INVENTORY);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_PARTNER);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_CRM);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_TICKET);
            //messageSender.send(message,SharedDataConstants.QUEUE_SEND_CREATE_DATA_ROLE_RADIUS);
            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName(), KafkaConstant.CREATE_DATA_ROLE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

