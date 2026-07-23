package com.savbill.partnermanagement.modules.Role;

import com.savbill.partnermanagement.core.exceptions.CustomValidationException;
import com.savbill.partnermanagement.core.service.ExBaseAbstractService;
import com.savbill.partnermanagement.modules.acl.domain.CustomACLEntry;
import com.savbill.partnermanagement.modules.acl.repository.CustomACLEntryRepository;
import com.savbill.partnermanagement.modules.acl.service.CustomACLService;
import com.savbill.partnermanagement.rabbitmq.setting.CommonRoleMessage;
import com.savbill.partnermanagement.rabbitmq.setting.SaveRoleSharedDataMessage;
import com.savbill.partnermanagement.rabbitmq.setting.UpdateRoleSharedDataMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    CustomACLService customACLService;
    @Autowired
    RoleAclRepository roleAclRepository;

    @Autowired
    CustomACLEntryRepository customACLEntryRepository;

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public void saveRoleEntity(SaveRoleSharedDataMessage message) {
        try {
            Role role = new Role();
            List<CustomACLEntry> customACLEntryList = new ArrayList<>();
            role.setId(message.getId());
            role.setRolename(message.getRolename());
            role.setStatus(message.getStatus());
            role.setSysRole(message.getSysRole());
            role.setCreatedById(message.getCreatedById());
            role.setLastModifiedById(message.getLastModifiedById());
            for (CustomACLEntry item : message.getAclEntry()) {
                CustomACLEntry customACLEntry = new CustomACLEntry();
                customACLEntry.setId(item.getId());
                customACLEntry.setClassid(item.getClassid());
                customACLEntry.setRoleid(message.getId());
                customACLEntry.setPermit(item.getPermit());
                customACLEntryList.add(customACLEntry);
            }
//            role.setAclEntry(customACLEntryList);
            role.setIsDelete(message.getIsDelete());
            role.setMvnoId(message.getMvnoId());
            role.setLcoId(message.getLcoId());
            roleRepository.save(role);
            logger.info("Role created successfully with name " + message.getRolename());
        } catch (CustomValidationException e) {
            logger.error("Unable to create role with name " + message.getRolename(), e.getMessage());
        }
    }

    public void updateRoleEntity(UpdateRoleSharedDataMessage message)  {
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
                for (CustomACLEntry item : message.getAclEntry()) {
                    CustomACLEntry customACLEntry = new CustomACLEntry();
                    customACLEntry.setId(item.getId());
                    customACLEntry.setClassid(item.getClassid());
                    if (message.getIsDelete().equals(false)) {
                        customACLEntry.setRoleid(message.getId());
                    } else if (message.getIsDelete().equals(true)) {
                        customACLEntry.setRoleid(null);
                    }
                    customACLEntry.setPermit(item.getPermit());
                    customACLEntryList.add(customACLEntry);
                }
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
                for (CustomACLEntry item : message.getAclEntry()) {
                    CustomACLEntry customACLEntry = new CustomACLEntry();
                    customACLEntry.setId(item.getId());
                    customACLEntry.setClassid(item.getClassid());
                    if (message.getIsDelete().equals(false)) {
                        customACLEntry.setRoleid(message.getId());
                    } else if (message.getIsDelete().equals(true)) {
                        customACLEntry.setRoleid(null);
                    }
                    customACLEntry.setPermit(item.getPermit());
                    customACLEntryList.add(customACLEntry);
                }
//                role1.setAclEntry(customACLEntryList);
                role1.setIsDelete(message.getIsDelete());
                role1.setMvnoId(message.getMvnoId());
                role1.setLcoId(message.getLcoId());
                roleRepository.save(role1);
                logger.info("Role updated successfully with name " + message.getRolename());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update role with name " + message.getRolename(), e.getMessage());
        }
    }



    public void saveRole(CommonRoleMessage message) {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Role roles= null;
            Optional<Role> role = roleRepository.findById(message.getId());
            if (role.isPresent()){
                roles = role.get();
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
                logger.info("Existing role found. Old ACL entries removed for role ID: {}", roles.getId());

            }else {
                roles = new Role();
                roles.setCreatedate(LocalDateTime.now());
                logger.info("Creating new role with ID: {}", message.getId());
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
            roles.setRoleAclEntry(aclEntryList);
            roles.setIsDelete(message.getIsDelete());
            roles.setLcoId(message.getLcoId());
            roles.setMvnoId(message.getMvnoId());
            roles.setCreatedById(message.getCreatedById());
            roles.setCreatedByName(message.getCreatedByName());
            roles.setLastModifiedByName(message.getLastModifiedByName());
            roles.setLastModifiedById(message.getLastModifiedById());
            roles.setStatus(message.getStatus());
            roleRepository.save(roles);
//            customACLService.reloadCache();
            customACLService.updateCache(roles.getId());
            logger.info("Role saved successfully with ID: {}", roles.getId());

        } catch (Exception e) {
            logger.error("Exception occurred while saving role with ID: {}", message.getId(), e);
            throw new RuntimeException(e);
        }
    }

    public void deleteRole(CommonRoleMessage message) {
        try{
            logger.info("[deleteRole] Attempting to delete role with ID: {}", message.getId());
            Optional<Role> role = roleRepository.findById(message.getId());

            if (role.isPresent()){
                logger.debug("[deleteRole] Role found: {}", role.get().getRolename());
                role.get().setIsDelete(true);
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(role.get().getId());
                logger.debug("[deleteRole] Found {} ACL entries to delete for role ID {}", roleACLEntries.size(), role.get().getId());

                roleAclRepository.deleteAll(roleACLEntries);
                logger.info("[deleteRole] Deleted ACL entries for role ID {}", role.get().getId());

                roleRepository.save(role.get());
                logger.info("[deleteRole] Role marked as deleted and saved: {}", role.get().getRolename());

                customACLService.updateCache(role.get().getId());
                logger.info("[deleteRole] Cache updated for role ID: {}", role.get().getId());

            }
        } catch (Exception e) {
            logger.error("[deleteRole] Error while deleting role with ID: {}. Exception: {}", message.getId(), e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
