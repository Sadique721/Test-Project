package com.savbill.revenuemanagement.core.entity.staff;

import com.savbill.revenuemanagement.core.acl.domain.CustomACLEntry;
import com.savbill.revenuemanagement.core.entity.role.domain.Role;
import com.savbill.revenuemanagement.core.entity.role.domain.RoleACLEntry;
import com.savbill.revenuemanagement.core.entity.role.model.RoleACLEntryDTO;
import com.savbill.revenuemanagement.core.entity.role.repository.RoleAclRepository;
import com.savbill.revenuemanagement.core.entity.role.repository.RoleRepository;
import com.savbill.revenuemanagement.core.entity.role.service.CustomACLService;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.CommonRoleMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.SaveRoleSharedDataMessage;
import com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.UpdateRoleSharedDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CustomACLService customACLService;
    @Autowired
    RoleAclRepository roleAclRepository;

    public void createNewRole(SaveRoleSharedDataMessage message) {
        Role roles=new Role();
        roles.setId(message.getId());
        roles.setSysRole(message.getSysRole());
        roles.setRolename(message.getRolename());
        roles.setCreatedate(LocalDateTime.now());
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
//        roles.setAclEntry(customACLEntryList);
        roles.setIsDelete(message.getIsDelete());
        roles.setLcoId(message.getLcoId());
        roles.setMvnoId(message.getMvnoId());

        roles.setCreatedById(message.getCreatedById());
        //roles.setCreatedByName(mess);
        roles.setStatus(message.getStatus());
        roleRepository.save(roles);
    }

    public void updateRoles(UpdateRoleSharedDataMessage message) {
        Role roles=roleRepository.findById(message.getId()).orElse(null);
        if(roles!=null){
            roles.setSysRole(message.getSysRole());
            roles.setRolename(message.getRolename());
            roles.setCreatedate(LocalDateTime.now());
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
//            roles.setAclEntry(customACLEntryList);
            roles.setIsDelete(message.getIsDelete());
            roles.setLcoId(message.getLcoId());
            roles.setMvnoId(message.getMvnoId());
            roles.setCreatedById(message.getCreatedById());
            //roles.setCreatedByName(mess);
            roles.setStatus(message.getStatus());
            roleRepository.save(roles);
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
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(role.get().getId());
                roleAclRepository.deleteAll(roleACLEntries);
                roleRepository.save(role.get());
                customACLService.updateCache(role.get().getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
