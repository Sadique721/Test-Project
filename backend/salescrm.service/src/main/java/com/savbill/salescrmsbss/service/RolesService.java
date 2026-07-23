package com.savbill.salescrmsbss.service;

import com.savbill.salescrmsbss.entity.Role;
import com.savbill.salescrmsbss.entity.RoleACLEntry;
import com.savbill.salescrmsbss.entity.pojo.RoleACLEntryDTO;
import com.savbill.salescrmsbss.rabbitMq.message.CommonRoleMessage;
import com.savbill.salescrmsbss.repository.RoleAclRepository;
import com.savbill.salescrmsbss.repository.RoleRepository;
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
//            roles.setLcoId(message.getLcoId());
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
