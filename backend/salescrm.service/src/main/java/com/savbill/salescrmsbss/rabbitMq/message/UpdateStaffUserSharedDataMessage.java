package com.savbill.salescrmsbss.rabbitMq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.savbill.salescrmsbss.entity.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStaffUserSharedDataMessage {
    private Integer id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String status;
    private String last_login_time;
    private Integer partnerid;
    private Set<Role> roles = new HashSet<>();
    private Set<Teams> team = new HashSet<>();
    private Boolean isDelete = false;
    private ServiceArea servicearea;
    private BusinessUnit businessUnit;
    private Integer mvnoId;
    private Integer branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();

    private Integer parentStaffId;
    private String email;
    private String phone;
    private Integer lcoId;
    private String countryCode;
    private Integer createdById;
    private Integer lastModifiedById;
    private List<Teams> teamsList = new ArrayList<>();

    public  UpdateStaffUserSharedDataMessage(StaffUser staffUser){

        this.id = staffUser.getId();
        this.username = staffUser.getUsername();
        this.password = staffUser.getPassword();
        this.status = staffUser.getStatus();
        this.branchId = staffUser.getBranchId();
        this.mvnoId = staffUser.getMvnoId();
        this.last_login_time = String.valueOf(staffUser.getLast_login_time());
//        this.createdById = staffUser.getCreatedById();
//        this.lastModifiedById = staffUser.getLastModifiedById();
        this.businessUnitNameList = staffUser.getBusinessUnitNameList();
//        this.serviceAreaNameList = staffUser.getServiceAreaNameList();
        this.countryCode = staffUser.getCountryCode();
        this.phone = staffUser.getPhone();
        this.email =staffUser.getEmail();
        if(staffUser.getRoles()!=null){
            this.roles = staffUser.getRoles();
        }
        if(staffUser.getStaffUserparentId()!=null){
            this.parentStaffId = staffUser.getStaffUserparentId();
        }

        if(staffUser.getPartnerid()!=null){
            this.partnerid = staffUser.getPartnerid();
        }
        this.isDelete = staffUser.getIsDelete();
        this.mvnoId =staffUser.getMvnoId();
        this.firstname = staffUser.getFirstname();
        this.lastname = staffUser.getLastname();
        if(staffUser.getLast_login_time()!=null) {
            this.last_login_time = staffUser.getLast_login_time().toString();
        }
        this.status = staffUser.getStatus();

    }

    public UpdateStaffUserSharedDataMessage() {
    }

}
