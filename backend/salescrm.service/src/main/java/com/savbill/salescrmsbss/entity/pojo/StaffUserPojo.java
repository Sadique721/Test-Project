package com.savbill.salescrmsbss.entity.pojo;

import com.savbill.salescrmsbss.entity.BusinessUnit;
import com.savbill.salescrmsbss.entity.ServiceArea;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.PostLoad;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class StaffUserPojo {

    private Integer id;

    @NotNull
    private String username;

    private String password;

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String email;

    @NotNull
    private String phone;

    // added country code
    private String countryCode;

    private Integer failcount = 0;

    @NotNull
    private String status;

    @CreationTimestamp
    private LocalDateTime last_login_time;

    private Integer partnerid;

    private String newpassword;

    private List<Integer> roleIds;

    private Set<Long> teamIds = new HashSet<>();

    private List<String> teamNameList = new ArrayList<>();

    private Boolean isDelete = false;

    private String fullName;

    private Boolean sysstaff = false;

    private ServiceArea servicearea;

    private BusinessUnit businessUnit;

    private Long serviceAreaId;

    private Long businessunitid;

    private Integer parentStaffId;

    private Integer mvnoId;

    private List<Long> serviceAreaIdsList;

    private List<Long> businessUnitIdsList;

    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();


    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != this.getFirstname() && !this.getFirstname().isEmpty()) {
                fullName = this.getFirstname() + " ";
            }
            if (null != this.getLastname() && !this.getLastname().isEmpty()) {
                fullName += this.getLastname() + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> roleName;
    private String regDate;
    private String partnerName;

    private String updatedatestring;

    private Integer branchId;
}
