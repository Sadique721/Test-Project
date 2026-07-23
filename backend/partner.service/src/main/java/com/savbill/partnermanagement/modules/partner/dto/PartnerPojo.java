package com.savbill.partnermanagement.modules.partner.dto;


import com.savbill.partnermanagement.core.data.Auditable;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PartnerPojo extends Auditable {

    private Integer id;
    @NotNull
    private String name;
    @NotNull
    private String status;
    @NotNull
    @ApiModelProperty(notes = "Possible values: PERCUSTFLAT, PERCUSTPERC, PRICEBOOK")
    private String commtype;
    private Double commrelvalue = 0.0;
    private Double balance = 0.0;
    @NotNull
    private Integer commdueday;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextbilldate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastbilldate;
    private Integer taxid;
    private Double credit;
    @NotNull
    @ApiModelProperty(notes = "Possible values: home, office, other")
    private String addresstype;
    @NotNull
    private String address1;
    @NotNull
    private String address2;
    @NotNull
    private Integer city;
    @NotNull
    private Integer state;
    @NotNull
    private Integer country;
    @NotNull
    private String pincode;
    @NotNull
    private String mobile;
    private String countryCode;
    private String prcode;
    private String partnerType;
    @NotNull
    private String email;
    private List<Long> serviceAreaIds = new ArrayList<>();
    private Integer parentpartnerid;
    private Boolean isDelete = false;
    private List<String> serviceAreaNameList = new ArrayList<>();
    private String cityName;
    private String countryName;
    private String stateName;
    private String taxName;
    private String parentPartnerName;
    private Long pricebookId;
    private String pricebookname;
    private String cpName;
    private String cname;
    private String panName;
    private Double outcomeBalance = 0.0;
    private Long totalCustomerCount;
    private Long renewCustomerCount;
    private Long newCustomerCount;
    private String calendarType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ResetDate;
    @ApiModelProperty(notes = "Possible values: Balance,Revenue")
    private String commissionShareType;
    private Integer mvnoId;
    private Long buId;
    private Double creditConsume;
    private Integer displayId;
    private String displayName;
    private Long region ;
    private Long branch ;
    private Long bussinessvertical ;
    private Boolean isShitPartner=false;
    private String commissionInterval;
    private Boolean isVisibleToIsp = false;
    @Transient
    private Long serviceAreaId;
    @Transient
    private String serviceAreaName;


    public Long getBuId() {
        return buId;
    }
    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @Override
    public String toString() {
        return "PartnerPojo [id=" + id + ", name=" + name + ", status=" + status + ", commtype=" + commtype
                + ", commrelvalue=" + commrelvalue + ", commdueday=" + commdueday + ", nextbilldate=" + nextbilldate
                + ", lastbilldate=" + lastbilldate + ", taxid=" + taxid + ", addresstype=" + addresstype + ", address1="
                + address1 + ", address2=" + address2 + ", city=" + city + ", state=" + state + ", country=" + country
                + ", pincode=" + pincode + ", mobile=" + mobile + ", email=" + email + "]";
    }

    public PartnerPojo(){}

    public PartnerPojo(Integer id, String name, String status, String commtype, Double commrelvalue, Double balance,
                       Integer commdueday, LocalDate nextbilldate, LocalDate lastbilldate, Integer taxid, Double credit,
                       String addresstype, String address1, String address2, Integer city, Integer state, Integer country,
                       String pincode, String mobile, String countryCode, String prcode, String partnerType, String email,
                       Integer parentPartnerId, Boolean isDelete, Long priceBookId, String calendarType, String commissionShareType,
                       Integer mvnoId, Long buId, Double creditConsume, Integer displayId, String displayName,
                       Long region, Long branch, Long bussinessvertical, String commissionInterval, Boolean isVisibleToIsp) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.commtype = commtype;
        this.commrelvalue = commrelvalue;
        this.balance = balance;
        this.commdueday = commdueday;
        this.nextbilldate = nextbilldate;
        this.lastbilldate = lastbilldate;
        this.taxid = taxid;
        this.credit = credit;
        this.addresstype = addresstype;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.prcode = prcode;
        this.partnerType = partnerType;
        this.email = email;
        this.parentpartnerid = parentPartnerId;
        this.isDelete = isDelete;
        this.pricebookId = priceBookId;
        this.calendarType = calendarType;
        this.commissionShareType = commissionShareType;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.creditConsume = creditConsume;
        this.displayId = displayId;
        this.displayName = displayName;
        this.region = region;
        this.branch = branch;
        this.bussinessvertical = bussinessvertical;
        this.commissionInterval = commissionInterval;
        this.isVisibleToIsp = isVisibleToIsp;
    }

    public PartnerPojo(Integer id, String name, String status, String commtype, Double commrelvalue, Double balance,
                       Integer commdueday, LocalDate nextbilldate, LocalDate lastbilldate, Integer taxid, Double credit,
                       String addresstype, String address1, String address2, Integer city, Integer state, Integer country,
                       String pincode, String mobile, String countryCode, String prcode, String partnerType, String email,
                       Integer parentPartnerId, Boolean isDelete, Long priceBookId, String calendarType, String commissionShareType,
                       Integer mvnoId, Long buId, Double creditConsume, Integer displayId, String displayName,
                       Long region, Long branch, Long bussinessvertical, String commissionInterval, Boolean isVisibleToIsp,
                       LocalDateTime createdate, LocalDateTime updatedate, String cityname, String countryName, String stateName,
                       String taxName, String parentPartnerName, Double outcomeBalance, Long totalCustomerCount, Long renewCustomerCount,
                       Long newCustomerCount, String pricebookname, Long serviceAreaId, String serviceAreaName) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.commtype = commtype;
        this.commrelvalue = commrelvalue;
        this.balance = balance;
        this.commdueday = commdueday;
        this.nextbilldate = nextbilldate;
        this.lastbilldate = lastbilldate;
        this.taxid = taxid;
        this.credit = credit;
        this.addresstype = addresstype;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.prcode = prcode;
        this.partnerType = partnerType;
        this.email = email;
        this.parentpartnerid = parentPartnerId;
        this.isDelete = isDelete;
        this.pricebookId = priceBookId;
        this.calendarType = calendarType;
        this.commissionShareType = commissionShareType;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.creditConsume = creditConsume;
        this.displayId = displayId;
        this.displayName = displayName;
        this.region = region;
        this.branch = branch;
        this.bussinessvertical = bussinessvertical;
        this.commissionInterval = commissionInterval;
        this.isVisibleToIsp = isVisibleToIsp;
        this.createdate = createdate;
        this.updatedate = updatedate;
        this.cityName = cityname;
        this.countryName = countryName;
        this.stateName = stateName;
        this.taxName = taxName;
        this.parentPartnerName = parentPartnerName;
        this.outcomeBalance = outcomeBalance;
        this.totalCustomerCount = totalCustomerCount;
        this.renewCustomerCount = renewCustomerCount;
        this.newCustomerCount = newCustomerCount;
        this.pricebookname = pricebookname;
        this.serviceAreaId = serviceAreaId;
        this.serviceAreaName = serviceAreaName;
    }


}
