package com.savbill.commonGateway.spring.security;

import com.savbill.commonGateway.common.service.ClientServiceSrv;
import com.savbill.commonGateway.constants.ClientServiceConstant;
import com.savbill.commonGateway.moules.Customers.Service.CustomersService;
import com.savbill.commonGateway.moules.Customers.domain.Customers;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement.MvnoRepository;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUser;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement.StaffUserService;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMapping;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserServiceAreaMapping.StaffUserServiceAreaMappingRepository;
import com.savbill.commonGateway.security.dto.LoggedInUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("customSubscriberDetailService")
public class CustomSubscriberDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomSubscriberDetailsService.class);

    @Autowired
    private CustomersService subscriberService;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private MvnoRepository mvnoRepository;

    private String subscriberRoleName;

    private Integer subscriberRoleId;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;

    public void setSubscriberService(CustomersService subscriberService) {
        this.subscriberService = subscriberService;
    }
    
    @Autowired
    private StaffUserService staffUserService;

    public void setStaffUserService(StaffUserService staffUserService) {
        this.staffUserService = staffUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        subscriberRoleId = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.SUBSCRIBER_ROLEID).get(0).getValue());
        subscriberRoleName = clientServiceSrv.getClientSrvByName(ClientServiceConstant.SUBSCRIBER_ROLENAME).get(0).getValue();
        LoggedInUser user = null;
        try {
            logger.info("LoadUserByUserName called");
            List<Customers> subscriberList = subscriberService.getActiveSubscriberFromUsername(username);
            List<StaffUser> staffList = staffUserService.getActiveStaffUserFromUsername(username);
            List<Integer>  ID=staffList.stream().map(StaffUser::getId).collect(Collectors.toList());
             List<StaffUserServiceAreaMapping> staffUserServiceAreaMappings=staffUserServiceAreaMappingRepository.findByStaffIdIn(ID);
             List<Integer> serviceAreaIdList= staffUserServiceAreaMappings.stream().map(StaffUserServiceAreaMapping :: getServiceId).collect(Collectors.toList());
            Long serviceAreaId = null;
            Integer mvnoId = null;
            String mvnoName = null;
            StaffUser sfUser = null;
            List<Long> buIds = null;
            if (staffList != null && staffList.size() > 0) {
            	sfUser = staffList.get(0);
                List<BusinessUnit> businessUnits = sfUser.getBusinessUnitNameList();
                buIds = businessUnits.stream().map(BusinessUnit :: getId).collect(Collectors.toList());
                if(sfUser != null && sfUser.getServicearea() != null) {
            		serviceAreaId = sfUser.getServicearea().getId();
            	}
            	if(sfUser != null && sfUser.getMvnoId() != null) {
            		mvnoId = sfUser.getMvnoId();
            	}
                if(mvnoId != null) {
                    mvnoName = mvnoRepository.findMvnoNameById(Long.valueOf(mvnoId));
                }
            }

            if (subscriberList != null && subscriberList.size() > 0) {
                Customers subscriber = subscriberList.get(0);
                subscriber = subscriberService.get(subscriber.getId());

                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                authorities.add(new SimpleGrantedAuthority(subscriberRoleName));

                user = new LoggedInUser(username, subscriber.getPassword(), true, true, true, true, authorities,
                        subscriber.getFirstname(), subscriber.getLastname(), LocalDateTime.now(), subscriber.getId(), subscriber.getParnterId(), subscriberRoleId.toString(),serviceAreaId,subscriber.getMvnoId(),serviceAreaIdList,
                        sfUser == null ? 0 : sfUser.getId(), buIds,false , new ArrayList<String>(), new ArrayList<Long>(), mvnoName,new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
            } else {
                throw new UsernameNotFoundException("Subscriber not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            user = null;
        }
        return user;
    }

}
