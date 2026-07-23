package com.savbill.salescrmsbss.security.service;

import com.savbill.salescrmsbss.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.salescrmsbss.entity.QStaffUser;
import com.savbill.salescrmsbss.entity.Role;
import com.savbill.salescrmsbss.entity.StaffUser;
import com.savbill.salescrmsbss.repository.MvnoRepository;
import com.savbill.salescrmsbss.repository.StaffUserRepository;
import com.savbill.salescrmsbss.security.dto.LoggedInUser;

import com.savbill.salescrmsbss.security.dto.LoggedInUserDto;
import com.savbill.salescrmsbss.utils.APIConstants;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("customUserDetailService")
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private StaffUserRepository staffRepository;

    @Autowired
    private StaffRoleRelRepo staffRolRelRepo;

    @PersistenceContext
    private EntityManager entityManager;

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
            QStaffUser qStaffUser = QStaffUser.staffUser;
            BooleanExpression exp = qStaffUser.isNotNull().and(qStaffUser.username.equalsIgnoreCase(username))
                    .and(qStaffUser.status.equalsIgnoreCase("Active")).and(qStaffUser.isDelete.ne(true));
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
            List<LoggedInUserDto> queryResults = queryFactory
                    .select(Projections.constructor(
                            LoggedInUserDto.class,
                            qStaffUser.password,
                            qStaffUser.firstname,
                            qStaffUser.lastname,
                            qStaffUser.last_login_time,
                            qStaffUser.id,
                            qStaffUser.partnerid,
                            qStaffUser.mvnoId
                    ))
                    .from(qStaffUser)
                    .where(exp)
                    .fetch();

            if (queryResults!=null && queryResults.size() > 0) {
                List<Long> roleIds = staffRolRelRepo.findRoleIdByStaffId(Long.valueOf(queryResults.get(0).getStaffId()));

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
                String mvnoName=null;
                if(sfUser.getMvnoId()!=null)
                {
                    mvnoName= mvnoRepository.findMvnoNameById(Long.valueOf(sfUser.getMvnoId()));
                }
                user = new LoggedInUser(username, sfUser.getPassword(), true, true, true, true, authorities, sfUser.getFirstname(), sfUser.getLastname(), sfUser.getLast_login_time(), sfUser.getId(), sfUser.getPartnerid(), roleList.toString(), serviceAreaId, mvnoId, null, sfUser.getId(), null, isLco , null,roleIds,mvnoName,null,null);
            }
        } catch (Exception e) {
            logger.error("Unable to login with username  "+username+" :  response: {  error : {};exception :{}}", APIConstants.FAIL,e.getStackTrace());
            e.printStackTrace();
            user = null;
        }
        return user;
    }
}
