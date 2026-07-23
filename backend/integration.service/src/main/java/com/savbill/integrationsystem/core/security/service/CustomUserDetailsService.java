package com.savbill.integrationsystem.core.security.service;


import com.savbill.integrationsystem.billgen.repository.StaffUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("customUserDetailService")
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private StaffUserRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }

//    @Autowired
//    private MvnoRepository mvnoRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        // TODO Auto-generated method stub
//
//        LoggedInUser user = null;
//        Long serviceAreaId = null;
//        Integer mvnoId= null;
//        Boolean isLco=false;
//        try {
//            logger.info("LoadUserByUserName called");
//            Optional<StaffUser> staffUser = staffRepository.findByUsername(username);
//            StaffUser sfUser = staffUser.get();
//            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//            StringBuilder roleList = new StringBuilder();
//            int i = 0;
//            /*for (Role role : sfUser.getRoles()) {
//                authorities.add(new SimpleGrantedAuthority(role.getRolename()));
//                if (i != 0)
//                    roleList.append(",");
//                roleList.append(role.getId());
//                i++;
//            }*/
//            String mvnoName=null;
//            if(sfUser.getMvnoId()!=null)
//            {
//                mvnoName= mvnoRepository.findMvnoNameById(Long.valueOf(sfUser.getMvnoId()));
//            }
//            user = new LoggedInUser(username, sfUser.getPassword(), true, true, true, true, authorities,
//                    sfUser.getFirstname(), sfUser.getLastname(), sfUser.getLast_login_time(), sfUser.getId(), sfUser.getPartnerid(), roleList.toString(), serviceAreaId, mvnoId, null, sfUser.getId(), null, isLco , null, new ArrayList<Long>(),mvnoName);
//
//        } catch (Exception e) {
//            logger.error("Unable to login with username  "+username+" :  response: {  error : {};exception :{}}", APIConstants.FAIL,e.getStackTrace());
//            e.printStackTrace();
//            user = null;
//        }
//        return user;
//    }
}
