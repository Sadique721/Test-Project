package com.savbill.inventorymanagement.modules.Role;

import com.savbill.inventorymanagement.core.exceptions.CustomValidationException;
import com.savbill.inventorymanagement.core.service.ExBaseAbstractService;
import com.savbill.inventorymanagement.modules.acl.domain.CustomACLEntry;
import com.savbill.inventorymanagement.modules.acl.domain.RoleACLEntry;
import com.savbill.inventorymanagement.modules.acl.model.RoleACLEntryDTO;
import com.savbill.inventorymanagement.modules.acl.repository.CustomACLEntryRepository;
import com.savbill.inventorymanagement.modules.acl.repository.RoleAclRepository;
import com.savbill.inventorymanagement.modules.acl.services.CustomACLService;
import com.savbill.inventorymanagement.rabbitmq.CommonRoleMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.SaveRoleSharedDataMessage;
import com.savbill.inventorymanagement.rabbitmq.SharedMessages.UpdateRoleSharedDataMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService extends ExBaseAbstractService<RoleDTO, Role, Long> {
    public RoleService(RoleRepository repository, RoleMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[RoleService]";
    }

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomACLEntryRepository customACLEntryRepository;

    @Autowired
    private CustomACLService customACLService;

    @Autowired
    private RoleAclRepository roleAclRepository;

    private static final Logger logger = Logger.getLogger(RoleService.class);

    public void saveRoleEntity(SaveRoleSharedDataMessage message) throws Exception {
        try {
            Role role = new Role();
            List<CustomACLEntry> customACLEntryList = new ArrayList<>();
            role.setId(message.getId());
            role.setRolename(message.getRolename());
            role.setStatus(message.getStatus());
            role.setSysRole(message.getSysRole());
            role.setCreatedById(message.getCreatedById());
            role.setLastModifiedById(message.getLastModifiedById());
//            for (CustomACLEntry item : message.getAclEntry()) {
//                CustomACLEntry customACLEntry = new CustomACLEntry();
//                customACLEntry.setId(item.getId());
//                customACLEntry.setClassid(item.getClassid());
//                customACLEntry.setRoleid(message.getId());
//                customACLEntry.setPermit(item.getPermit());
//                customACLEntryList.add(customACLEntry);
//            }
//            role.setAclEntry(customACLEntryList);
            role.setIsDelete(message.getIsDelete());
            role.setMvnoId(message.getMvnoId());
            role.setLcoId(message.getLcoId());
            roleRepository.save(role);
            logger.info("Role created successfully with name " + message.getRolename());
        } catch (CustomValidationException e) {
            logger.error("Unable to create role with name " + message.getRolename() + " , Error: " + e.getMessage());
        }
    }

    public void updateRoleEntity(UpdateRoleSharedDataMessage message) throws Exception {
        try {
            Role role = roleRepository.findById(message.getId()).orElse(null);
            if (role != null) {
                List<CustomACLEntry> customACLEntryList = new ArrayList<>();
                role.setId(message.getId());
                role.setRolename(message.getRolename());
                role.setStatus(message.getStatus());
                role.setSysRole(message.getSysRole());
                role.setCreatedById(message.getCreatedById());
                role.setLastModifiedById(message.getLastModifiedById());
//                for (CustomACLEntry item : message.getAclEntry()) {
//                    CustomACLEntry customACLEntry = new CustomACLEntry();
//                    customACLEntry.setId(item.getId());
//                    customACLEntry.setClassid(item.getClassid());
//                    if (message.getIsDelete().equals(false)) {
//                        customACLEntry.setRoleid(message.getId());
//                    } else if (message.getIsDelete().equals(true)) {
//                        customACLEntry.setRoleid(null);
//                    }
//                    customACLEntry.setPermit(item.getPermit());
//                    customACLEntryList.add(customACLEntry);
//                }
//                role.setAclEntry(customACLEntryList);
                role.setIsDelete(message.getIsDelete());
                role.setMvnoId(message.getMvnoId());
                role.setLcoId(message.getLcoId());
                roleRepository.save(role);
                logger.info("Role updated successfully with name " + message.getRolename());
            } else {
                Role role1 = new Role();
                List<CustomACLEntry> customACLEntryList = new ArrayList<>();
                role1.setId(message.getId());
                role1.setRolename(message.getRolename());
                role1.setStatus(message.getStatus());
                role1.setSysRole(message.getSysRole());
                role1.setCreatedById(message.getCreatedById());
                role1.setLastModifiedById(message.getLastModifiedById());
//                for (CustomACLEntry item : message.getAclEntry()) {
//                    CustomACLEntry customACLEntry = new CustomACLEntry();
//                    customACLEntry.setId(item.getId());
//                    customACLEntry.setClassid(item.getClassid());
//                    if (message.getIsDelete().equals(false)) {
//                        customACLEntry.setRoleid(message.getId());
//                    } else if (message.getIsDelete().equals(true)) {
//                        customACLEntry.setRoleid(null);
//                    }
//                    customACLEntry.setPermit(item.getPermit());
//                    customACLEntryList.add(customACLEntry);
//                }
//                role1.setAclEntry(customACLEntryList);
                role1.setIsDelete(message.getIsDelete());
                role1.setMvnoId(message.getMvnoId());
                role1.setLcoId(message.getLcoId());
                roleRepository.save(role1);
                logger.info("Role updated successfully with name " + message.getRolename());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update role with name " + message.getRolename() + " , Error: " + e.getMessage());
        }
    }

    public void saveRoleWithNewACL(SaveRoleSharedDataMessage message) {
        Role obj = convertSaveMessageToModel(message);
        if (obj != null) {
            saveRole(obj);
        }
    }

    public void updateRoleWithNewACl(UpdateRoleSharedDataMessage message) {
        List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleId(message.getId());
        roleAclRepository.deleteAll(roleACLEntries);
        Role obj = convertUpdateMessageToModel(message);
        if (obj != null) {
            saveRole(obj);
        }
    }
    public Role saveRole(Role role) {
        Role save = null;
        save = roleRepository.save(role);
        customACLService.updateCache(role.getId());
        return save;
    }
    public Role convertSaveMessageToModel(SaveRoleSharedDataMessage message) {
        Role role;
        try {
            if (message != null) {
                role = new Role();
                if (message.getId() != null) {
                    role.setId(message.getId());
                }
                role.setRolename(message.getRolename());
                role.setStatus(message.getStatus());
//                role.setAclEntry(null);
                role.setIsDelete(message.getIsDelete());
                role.setSysRole(message.getSysRole());
                if (message.getMvnoId() != null) {
                    role.setMvnoId(message.getMvnoId());
                }
                if (message.getLcoId() != null) {
                    role.setLcoId(message.getLcoId());
                }
//                if(!CollectionUtils.isEmpty(message.getAclEntry())) {
//    //                List<RoleACLEntry> aclEntry = message.getAclMenu().stream().map(aclEntryDTO -> new RoleACLEntry(role, aclEntryDTO.getCode(), aclEntryDTO.getMenuid())).collect(Collectors.toList());
//                    if(!CollectionUtils.isEmpty(message.getAclEntry()))
//                        role.setRoleAclEntry(message.getAclEntry());
//                }
            } else {
                role = null;
            }
            return role;
        } catch (CustomValidationException e) {
            logger.error("Unable to convert save role message to model with name " + message.getRolename() + " , Error: " + e.getMessage());
        }
        return null;
    }

    public Role convertUpdateMessageToModel(UpdateRoleSharedDataMessage message) {
        Role role;
        try {
            if (message != null) {
                role = new Role();
                if (message.getId() != null) {
                    role.setId(message.getId());
                }
                role.setRolename(message.getRolename());
                role.setStatus(message.getStatus());
//                role.setAclEntry(null);
                role.setIsDelete(message.getIsDelete());
                role.setSysRole(message.getSysRole());
                if (message.getMvnoId() != null) {
                    role.setMvnoId(message.getMvnoId());
                }
                if (message.getLcoId() != null) {
                    role.setLcoId(message.getLcoId());
                }
//                if(!CollectionUtils.isEmpty(message.getAclEntry())) {
//                    //                List<RoleACLEntry> aclEntry = message.getAclMenu().stream().map(aclEntryDTO -> new RoleACLEntry(role, aclEntryDTO.getCode(), aclEntryDTO.getMenuid())).collect(Collectors.toList());
//                    if(!CollectionUtils.isEmpty(message.getAclEntry()))
//                        role.setRoleAclEntry(message.getAclEntry());
//                }
            } else {
                role = null;
            }
            return role;
        } catch (CustomValidationException e) {
            logger.error("Unable to convert save role message to model with name " + message.getRolename() + " , Error: " + e.getMessage());
        }
        return null;
    }

    public void saveRoles(CommonRoleMessage message) {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Role roles= null;
            Optional<Role> role = roleRepository.findById(message.getId());
            if (role.isPresent()){
                roles = role.get();
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleId(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
            }else {
                roles = new Role();
                roles.setCreatedate(LocalDateTime.now());
            }
            roles.setId(message.getId());
            roles.setSysRole(message.getSysRole());
            roles.setRolename(message.getRolename());
            roles.setUpdatedate(LocalDateTime.now());
            List<RoleACLEntry> aclEntryList = new ArrayList<>();
            if (message.getAclMenus()!=null && message.getAclMenus().size()>0) {
                for (RoleACLEntryDTO item : message.getAclMenus()) {
                    RoleACLEntry roleACLEntry = new RoleACLEntry(roles,item.getCode(), item.getMenuid(),item.getId());
                    aclEntryList.add(roleACLEntry);
                }
            }
//            roles.setRoleAclEntry(aclEntryList);
            roles.setIsDelete(message.getIsDelete());
            roles.setLcoId(message.getLcoId());
            roles.setMvnoId(message.getMvnoId());
            roles.setCreatedById(message.getCreatedById());
            roles.setCreatedByName(message.getCreatedByName());
            roles.setLastModifiedByName(message.getLastModifiedByName());
            roles.setLastModifiedById(message.getLastModifiedById());
            roles.setStatus(message.getStatus());
            roleRepository.save(roles);
            if(!CollectionUtils.isEmpty(aclEntryList))
                roleAclRepository.saveAll(aclEntryList);
            customACLService.updateCache(roles.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRole(CommonRoleMessage message) {
        try{
            Optional<Role> role = roleRepository.findById(message.getId());
            if (role.isPresent()){
                role.get().setIsDelete(true);
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRoleId(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
                roleRepository.save(role.get());
                customACLService.updateCache(role.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
