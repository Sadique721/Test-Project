package com.savbill.integrationsystem.rabbitmq;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.PostLoad;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
public class StaffUserMessage {
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
    private String last_login_time;

    private Integer partnerid;

    private String newpassword;

  //  private List<Integer> roleIds;

  //  private Set<Long> teamIds = new HashSet<>();
  // private List<String> teamNameList = new ArrayList<>();

    private Boolean isDelete = false;
    private String fullName;

    private Boolean sysstaff = false;

 //   private ServiceArea servicearea;
//    private BusinessUnit businessUnit;

    private Long serviceAreaId;

 //   private List<Integer> serviceAreasId= new ArrayList<>();

    private Long businessunitid;

 //   private List<Integer> businessunitids= new ArrayList<>();

    private Integer parentStaffId;

    private Integer mvnoId;

//    private List<StaffUserServiceMapping>staffUserServiceMappingList;
//    private List<Long> serviceAreaIdsList;
//    private List<Long> businessUnitIdsList;
//    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
//    private List<String> serviceAreasNameList = new ArrayList<>();
//    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
//    private List<String> businessUnitNamesList = new ArrayList<>();
    private Double totalCollected;
    private Double totalTransferred;
    private Double availableAmount;

    String branchName;
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

//    private List<String> roleName;
    private String regDate;
    private String partnerName;
    private String updatedatestring;
    private Integer branchId;
    private String parentstaffname;
    private String hrmsId;
//    private byte[] profileImage;
    }


