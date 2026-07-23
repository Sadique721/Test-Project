package com.savbill.commonGateway.spring.security;


import com.savbill.commonGateway.constants.APIConstants;
import com.savbill.commonGateway.core.constants.CommonConstants;
import com.savbill.commonGateway.exceptions.PasswordExpiryException;
import com.savbill.commonGateway.moules.PartnerManagement.Partner;
import com.savbill.commonGateway.moules.PartnerManagement.PartnerRepository;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.Role;
import com.savbill.commonGateway.moules.SettingsManagement.RoleManagement.RoleRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffRoleMapping.StaffRoleRelRepo;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserAccessibleRole.StaffAccessibleRoleMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserBusinessUnitMapping.StaffUserBusinessUnitMappingRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.QStaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.commonGateway.moules.TeamsManagement.Teams.TeamsRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
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
    private StaffUserService staffUserService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    @Autowired
    private TeamsRepository teamsRepository;

    @Autowired
    private StaffUserBusinessUnitMappingRepository staffUserBusinessUnitMappingRepository;


    @Autowired
    private  StaffRoleRelRepo    staffRolRelRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    MvnoRepository mvnoRepository;
    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    StaffAccessibleRoleMappingRepository staffAccessibleRoleMappingRepository;

    public void setStaffUserService(StaffUserService staffUserService) {
        this.staffUserService = staffUserService;
    }

    public LoggedInUser getLoggedInUserRefreshDataWithStaffId(Integer staffId) {
        LoggedInUser user = null;
        Long serviceAreaId = null;
        Integer mvnoId= null;
        Boolean isLco=false;
        String mvnoName = null;
        Optional<StaffUser> staffUser = staffUserRepository.findStaffUserStaffById(staffId);
        if(staffUser.isPresent()) {
            String username = staffUser.get().getUsername();
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
                                qStaffUser.mvnoId,
                                qStaffUser.isPasswordExpired,
                                qStaffUser.username
                        ))
                        .from(qStaffUser)
                        .where(exp)
                        .fetch();


                if (queryResults != null && queryResults.size() > 0) {
                    if (queryResults.get(0).getIsPasswordExpired()) {
                        throw new RuntimeException("Password is Expired Please reset the Password");
                    }
                    List<Integer> serviceAreaIdList = staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(queryResults.get(0).getStaffId());

                    List<Long> buIds = staffUserBusinessUnitMappingRepository.findBuidByStaffId(queryResults.get(0).getStaffId());

                    List<Long> roleIds = staffRolRelRepo.findRoleIdByStaffId(Long.valueOf(queryResults.get(0).getStaffId()));

                    List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(queryResults.get(0).getStaffId()).orElse(null);
                    List<String> assignableRoleNames = roleRepository.findRoleNameByRoleIds(assignableRoleIds);
                    StringBuilder roleList = new StringBuilder();
                    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                    List<Role> roleNames = roleRepository.findRolenameByrolrids(roleIds);
                    int i = 0;
                    for (Role role : roleNames) {
                        authorities.add(new SimpleGrantedAuthority(role.getRolename()));
                        if (i != 0)
                            roleList.append(",");
                        roleList.append(role.getId());
                        i++;
                    }

                    if (queryResults.get(0).getPartnerId() != null && queryResults.get(0).getPartnerId() != 1) {
                        Partner partner = partnerRepository.findById(queryResults.get(0).getPartnerId()).get();
                        if (partner != null && partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                            isLco = true;
                    }

                    List<Long> teamids = teamsRepository.findAllByStaff(queryResults.get(0).getPartnerId());
                    List<String> teamsName = new ArrayList<>();
                    if (!teamids.isEmpty()) {
                        teamsName = teamsRepository.findRolenameByrolrids(teamids);
                    }
                    if (staffUser != null) {
                        mvnoName = mvnoRepository.findMvnoNameById(Long.valueOf(staffUser.get().getMvnoId()));
                    }
                    user = new LoggedInUser(queryResults.get(0).getUserName(), queryResults.get(0).getPassword(), true, true, true, true, authorities,
                            queryResults.get(0).getFirstName(), queryResults.get(0).getLastName(), queryResults.get(0).getLastLoginTime(), queryResults.get(0).getStaffId(), queryResults.get(0).getPartnerId(),
                            roleList.toString(), serviceAreaId, queryResults.get(0).getMvnoId(), serviceAreaIdList, queryResults.get(0).getStaffId(), buIds, isLco, teamsName, roleIds, mvnoName, teamids, assignableRoleIds, assignableRoleNames);
                } else {
                    logger.error("Unable to login with username  " + username + " :  request: { From : {}}; Response : {{}};Error :{}", APIConstants.FAIL, new UsernameNotFoundException("User not found."));
                    throw new UsernameNotFoundException("User not found.");
                }
            } catch (Exception e) {
                logger.error("Unable to login with username  " + username + " :  response: {  error : {};exception :{}}", APIConstants.FAIL, e.getStackTrace());
                e.printStackTrace();
                user = null;
            }
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub

        LoggedInUser user = null;
        Long serviceAreaId = null;
        Integer mvnoId= null;
        String mvnoName= null;
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
                            qStaffUser.mvnoId,
                            qStaffUser.isPasswordExpired,
                            qStaffUser.username
                    ))
                    .from(qStaffUser)
                    .where(exp)
                    .fetch();

            if (queryResults!=null && queryResults.size() > 0) {
                if(queryResults.get(0).getIsPasswordExpired()){
                    throw new PasswordExpiryException("Password is Expired Please reset the Password");
                }
                List<Integer> serviceAreaIdList=staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(queryResults.get(0).getStaffId());

                List<Long> buIds = staffUserBusinessUnitMappingRepository.findBuidByStaffId(queryResults.get(0).getStaffId());

                List<Long> roleIds = staffRolRelRepo.findRoleIdByStaffId(Long.valueOf(queryResults.get(0).getStaffId()));
                List<Long> assignableRoleIds = staffAccessibleRoleMappingRepository.findAccessibleRolesByStaffId(queryResults.get(0).getStaffId()).orElse(null);
                List<String> assignableRoleNames = roleRepository.findRoleNameByRoleIds(assignableRoleIds);
                StringBuilder roleList = new StringBuilder();
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                List<Role> roleNames = roleRepository.findRolenameByrolrids(roleIds);
                int i = 0;
                for (Role role : roleNames) {
                    authorities.add(new SimpleGrantedAuthority(role.getRolename()));
                    if (i != 0)
                        roleList.append(",");
                    roleList.append(role.getId());
                    i++;
                }

                if (queryResults.get(0).getPartnerId() != null && queryResults.get(0).getPartnerId() != 1) {
                    Integer partnerId = queryResults.get(0).getPartnerId();
                    Integer lcoCount = partnerRepository.getCountByIdAndPartnerType(partnerId, CommonConstants.PARTNER_TYPE_LCO);
                    if (lcoCount != null && lcoCount > 0) {
                        isLco = true;
                    }
                }

                List<Long> teamids = teamsRepository.findAllByStaff(queryResults.get(0).getStaffId());
                List<String> teamsName = new ArrayList<>();
                if (!teamids.isEmpty()) {
                    teamsName = teamsRepository.findRolenameByrolrids(teamids);
                }
                if(queryResults!=null){
                    mvnoName=mvnoRepository.findMvnoNameById(Long.valueOf(queryResults.get(0).getMvnoId()));
                }
                user = new LoggedInUser(queryResults.get(0).getUserName(), queryResults.get(0).getPassword(), true, true, true, true, authorities,
                        queryResults.get(0).getFirstName(), queryResults.get(0).getLastName(), queryResults.get(0).getLastLoginTime(),
                        queryResults.get(0).getStaffId(), queryResults.get(0).getPartnerId(), roleList.toString(), serviceAreaId,
                        queryResults.get(0).getMvnoId(), serviceAreaIdList, queryResults.get(0).getStaffId(), buIds, isLco , teamsName, roleIds,mvnoName,teamids,assignableRoleIds,assignableRoleNames);
            } else {
                logger.error("Unable to login with username  "+username+" :  request: { From : {}}; Response : {{}};Error :{}", APIConstants.FAIL, new UsernameNotFoundException("User not found."));
                throw new UsernameNotFoundException("User not found.");
            }
        } catch (Exception | PasswordExpiryException e) {
            logger.error("Unable to login with username  "+username+" :  response: {  error : {};exception :{}}",APIConstants.FAIL,e.getStackTrace());
            e.printStackTrace();
            user = null;
        }
        return user;
    }

}
