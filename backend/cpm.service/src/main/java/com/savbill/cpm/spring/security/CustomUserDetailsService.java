package com.savbill.cpm.spring.security;

import com.savbill.cpm.model.common.QStaffUser;
import com.savbill.cpm.model.postpaid.Partner;
import com.savbill.cpm.modules.Mvno.repository.MvnoRepository;
import com.savbill.cpm.modules.Teams.repository.TeamsRepository;
import com.savbill.cpm.modules.role.domain.Role;
import com.savbill.cpm.modules.role.repository.RoleRepository;
import com.savbill.cpm.repository.common.StaffRolRelRepo;
import com.savbill.cpm.repository.common.StaffUserBusinessUnitMappingRepository;
import com.savbill.cpm.repository.common.StaffUserServiceAreaMappingRepository;
import com.savbill.cpm.repository.postpaid.PartnerRepository;
import com.savbill.cpm.service.common.StaffUserService;
import com.savbill.cpm.spring.LoggedInUser;
import com.savbill.cpm.utils.APIConstants;
import com.savbill.cpm.utils.CommonConstants;
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
    private  StaffRolRelRepo staffRolRelRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    MvnoRepository mvnoRepository;


    public void setStaffUserService(StaffUserService staffUserService) {
        this.staffUserService = staffUserService;
    }

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
            BooleanExpression exp = qStaffUser.isNotNull().and(qStaffUser.username.equalsIgnoreCase(username));
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

            List<Integer> serviceAreaIdList=staffUserServiceAreaMappingRepository.findServiceAreaByStaffId(queryResults.get(0).getStaffId());

            if (queryResults!=null) {

                List<Long> buIds = staffUserBusinessUnitMappingRepository.findBuidByStaffId(queryResults.get(0).getStaffId());

                List<Long> roleIds = staffRolRelRepo.findRoleIdByStaffId(queryResults.get(0).getStaffId().longValue());

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

                List<Long> teamids = teamsRepository.findAllByStaff(queryResults.get(0).getStaffId());
                List<String> teamsName = new ArrayList<>();
                if (!teamids.isEmpty()) {
                    teamsName = teamsRepository.findRolenameByrolrids(teamids);
                }
                String mvnoName=null;
                if( queryResults.get(0).getMvnoId()!=null){
                    mvnoName=mvnoRepository.findMvnoNameById(Long.valueOf(queryResults.get(0).getMvnoId()));
                }

                user = new LoggedInUser(username, queryResults.get(0).getPassword(), true, true, true, true, authorities,
                        queryResults.get(0).getFirstName(), queryResults.get(0).getLastName(), queryResults.get(0).getLastLoginTime(), queryResults.get(0).getStaffId(), queryResults.get(0).getPartnerId(), roleList.toString(), serviceAreaId, queryResults.get(0).getMvnoId(), serviceAreaIdList, queryResults.get(0).getStaffId(), buIds, isLco , teamsName, new ArrayList<Long>(),mvnoName, new ArrayList<Long>(), null,null);
            } else {
                logger.error("Unable to login with username  "+username+" :  request: { From : {}}; Response : {{}};Error :{}", APIConstants.FAIL, new UsernameNotFoundException("User not found."));
                throw new UsernameNotFoundException("User not found.");
            }
        } catch (Exception e) {
            logger.error("Unable to login with username  "+username+" :  response: {  error : {};exception :{}}",APIConstants.FAIL,e.getStackTrace());
            e.printStackTrace();
            user = null;
        }
        return user;
     }

}
