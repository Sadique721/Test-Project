package com.savbill.commonGateway.moules.SettingsManagement.StaffUserManagement;

import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.savbill.commonGateway.moules.MasterManagement.BusinessUnit.domain.BusinessUnit;
import com.savbill.commonGateway.moules.MasterManagement.ServiceArea.domain.ServiceArea;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserLocationMapping.StaffUserLocationMappingDto;
import com.savbill.commonGateway.moules.SettingsManagement.StaffUserPlanServiceMapping.StaffUserPlanServiceMapping;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class StaffUserPojo extends Auditable implements IBaseDto {

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

    private List<Long> roleIds;

    private List<Long> assignableRoleIds;

    private Set<Long> teamIds = new HashSet<>();

    private List<String> teamNameList = new ArrayList<>();

    private Boolean isDelete = false;

    private String fullName;

    private Boolean sysstaff = false;

    private ServiceArea servicearea;

    private BusinessUnit businessUnit;

    private String tacacsAccessLevelGroup;

    private Long serviceAreaId;

    private List<Integer> serviceAreasId= new ArrayList<>();

    private Long businessunitid;

    private List<Integer> businessunitids= new ArrayList<>();

    private Integer parentStaffId;

    private Integer mvnoId;

    private List<StaffUserPlanServiceMapping>staffUserServiceMappingList;

    private List<Long> serviceAreaIdsList;

    private List<Long> businessUnitIdsList;

    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();

    private List<String> serviceAreasNameList = new ArrayList<>();

    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();

    private List<String> businessUnitNamesList = new ArrayList<>();

    private Double totalCollected;

    private Double totalTransferred;

    private Double availableAmount;

    private Integer lcoId;

    private String uuid;

    private String eventName;

    private Long eventId;

    private Boolean isNotificationRequired = false;

    private LocalDateTime passwordDate;

    private Boolean isPasswordExpired = false;

    String branchName;

    private List<StaffUserLocationMappingDto> staffUserLocationMappingDtos = new ArrayList<>();

    public StaffUserPojo(Integer id, String username,  String firstname, String lastname,
                         String email, String phone, String countryCode, String status,
                         Integer partnerid, Boolean isDelete, Boolean sysstaff,
                         String tacacsAccessLevelGroup, Integer mvnoId, Integer branchId, Double totalCollected,
                         Double totalTransferred, Double availableAmount, Integer lcoId, String hrmsId,
                         byte[] profileImage, Integer department, String uuid, Boolean isPasswordExpired, LocalDateTime passwordDate,String password, Integer parentStaffId) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.countryCode = countryCode;
        this.status = status;
        this.partnerid = partnerid;
        this.isDelete = isDelete;
        this.sysstaff = sysstaff;
        this.tacacsAccessLevelGroup = tacacsAccessLevelGroup;
        this.mvnoId = mvnoId;
        this.branchId = branchId;
        this.totalCollected = totalCollected;
        this.totalTransferred = totalTransferred;
        this.availableAmount = availableAmount;
        this.lcoId = lcoId;
        this.hrmsId = hrmsId;
        this.profileImage = profileImage;
        this.department = department;
        this.uuid = uuid;
        this.isPasswordExpired = isPasswordExpired;
        this.passwordDate = passwordDate;
        this.fullName = firstname+' '+lastname;
        this.password=password;
        this.parentStaffId = parentStaffId;
    }
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

    private String parentstaffname;

    private String hrmsId;

    @Lob
    private byte[] profileImage;

    private Integer displayId;
    private String displayName;
    private Integer department;

    public StaffUserPojo(Integer id , String username , String firstname,String lastname){
        this.id = id;
        this.username=username;
        this.firstname=firstname;
        this.fullName=firstname+" "+lastname;
        this.businessUnitIdsList = new ArrayList<>();
        this.serviceAreaIdsList = new ArrayList<>();
    }

    public StaffUserPojo(Integer id , String firstname){
        this.id = id;
        this.firstname=firstname;
    }

    @Override
    public Long getIdentityKey() {
        return Long.valueOf(id);
    }

    public StaffUserPojo(Integer id, String username, String password, String firstname, String lastname,
                        String email, String phone, String countryCode, Integer failcount, String status,
                         Integer partnerid, Boolean isDelete, Boolean sysstaff,
                        String tacacsAccessLevelGroup, Integer mvnoId, Integer branchId, Double totalCollected,
                        Double totalTransferred, Double availableAmount, Integer lcoId, String hrmsId,
                        byte[] profileImage, Integer department, String uuid, Boolean isPasswordExpired, LocalDateTime passwordDate,Integer parentId) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.countryCode = countryCode;
        this.status = status;
        this.partnerid = partnerid;
        this.isDelete = isDelete;
        this.sysstaff = sysstaff;
        this.tacacsAccessLevelGroup = tacacsAccessLevelGroup;
        this.mvnoId = mvnoId;
        this.branchId = branchId;
        this.totalCollected = totalCollected;
        this.totalTransferred = totalTransferred;
        this.availableAmount = availableAmount;
        this.lcoId = lcoId;
        this.hrmsId = hrmsId;
        this.profileImage = profileImage;
        this.department = department;
        this.uuid = uuid;
        this.isPasswordExpired = isPasswordExpired;
        this.passwordDate = passwordDate;
        this.parentStaffId= parentId;
        this.fullName = firstname+' '+lastname;
    }
}

