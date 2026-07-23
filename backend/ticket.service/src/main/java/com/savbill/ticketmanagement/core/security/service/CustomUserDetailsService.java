package com.savbill.ticketmanagement.core.security.service;

import com.savbill.ticketmanagement.core.modules.Mvno.repository.MvnoRepository;
import com.savbill.ticketmanagement.core.modules.role.domain.Role;
import com.savbill.ticketmanagement.core.modules.staffuser.domain.StaffUser;
import com.savbill.ticketmanagement.core.modules.staffuser.repository.StaffUserRepository;
import com.savbill.ticketmanagement.core.modules.utils.APIConstants;
import com.savbill.ticketmanagement.core.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("customUserDetailService")
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private StaffUserRepository staffRepository;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        LoggedInUser user = null;
        Long serviceAreaId = null;
        Integer mvnoId= null;
        Boolean isLco=false;
        try {
            logger.info("LoadUserByUserName called");
            Optional<StaffUser> staffUser = staffRepository.findByUsername(username);
            StaffUser sfUser = staffUser.get();
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            StringBuilder roleList = new StringBuilder();
            int i = 0;
            for (Role role : sfUser.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(role.getRolename()));
                if (i != 0)
                    roleList.append(",");
                roleList.append(role.getId());
                i++;
            }
            String mvnoNamre=null;
            if(staffUser.get().getMvnoId()!=null){
                mvnoNamre=mvnoRepository.findMvnoNameById(Long.valueOf(staffUser.get().getMvnoId()));
            }
            user = new LoggedInUser(username, sfUser.getPassword(), true, true, true, true, authorities,
                    sfUser.getFirstname(), sfUser.getLastname(), sfUser.getLast_login_time(), sfUser.getId(), sfUser.getPartnerid(), roleList.toString(), serviceAreaId, mvnoId, null, sfUser.getId(), null, isLco , null,null,mvnoNamre,null,null);

        } catch (Exception e) {
            logger.error("Unable to login with username  "+username+" :  response: {  error : {};exception :{}}", APIConstants.FAIL,e.getStackTrace());
            e.printStackTrace();
            user = null;
        }
        return user;
    }
}
