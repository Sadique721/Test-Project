package com.savbill.radius.services;
import com.savbill.radius.dto.RoleACLEntryDTO;
import com.savbill.radius.entity.Role;
import com.savbill.radius.entity.RoleACLEntry;
import com.savbill.radius.kafka.message.CommonRoleMessage;
import com.savbill.radius.repository.RoleAclRepository;
import com.savbill.radius.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RolesService {
    @Autowired
    RoleRepository roleRepository;

    //@Autowired
    //CustomACLService customACLService;
    @Autowired
    RoleAclRepository roleAclRepository;


    public void saveRole(CommonRoleMessage message) {
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

            Role roles= null;
            Optional<Role> role = roleRepository.findById(message.getId());
            if (role.isPresent()){
                roles = role.get();
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(role.get().getRoleId());
                roleAclRepository.deleteAll(roleACLEntries);
            }else {
                roles = new Role();
            }
            roles.setRoleId(message.getId());
            roles.setName(message.getRolename());
            List<RoleACLEntry> aclEntryList = new ArrayList<>();
            if (message.getAclMenus()!=null && message.getAclMenus().size()>0) {
                for (RoleACLEntryDTO item : message.getAclMenus()) {
                    RoleACLEntry roleACLEntry = new RoleACLEntry(roles,item.getCode(), item.getMenuid(),item.getId());
                    aclEntryList.add(roleACLEntry);
                }
            }

            roles.setRoleAclEntry(aclEntryList);
            roles.setIsDelete(message.getIsDelete());
            roles.setMvnoId(message.getMvnoId().longValue());
            roleRepository.save(roles);
            //customACLService.reloadCache();
            //configService.reloadCache();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRole(CommonRoleMessage message) {
        try{
            Optional<Role> role = roleRepository.findById(message.getId());
            if (role.isPresent()){
                role.get().setIsDelete(true);
                List<RoleACLEntry> roleACLEntries = roleAclRepository.findAllByRole(role.get().getRoleId());
                roleAclRepository.deleteAll(roleACLEntries);
                roleRepository.save(role.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
