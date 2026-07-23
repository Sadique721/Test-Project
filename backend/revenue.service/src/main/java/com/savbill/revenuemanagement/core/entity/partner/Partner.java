package com.savbill.revenuemanagement.core.entity.partner;


import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.savbill.revenuemanagement.core.data.IBaseData;
import com.savbill.revenuemanagement.core.dto.common.Auditable;
import com.savbill.revenuemanagement.core.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@Table(name = "tblpartners")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class Partner extends Auditable implements IBaseData {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTNERID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "PARTNERNAME", nullable = false, length = 40)
    private String name;

    @Column(name = "partner_code")
    private String prcode;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "COMM_TYPE", nullable = false, length = 40)
    private String commtype;

    @Column(name = "COMM_REL_VALUE", length = 40)
    private Double commrelvalue;

    @Column(name = "balance", length = 40)
    private Double balance;

    @Column(name = "COMM_DUE_DAY", length = 40)
    private Integer commdueday;

    @Column(name = "NEXTBILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextbilldate;

    @Column(name = "LASTBILLDATE", nullable = false, length = 40)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastbilldate;

    @Column(name = "taxid", nullable = false, length = 40)
    private Integer taxid;

    @Column(name = "addresstype", nullable = false, length = 40)
    private String addresstype;

    @Column(name = "address1", nullable = false, length = 40)
    private String address1;

    @Column(name = "address2", nullable = false, length = 40)
    private String address2;

    @Column(name = "credit", nullable = false, length = 40)
    private Double credit;

    @Column(name = "city", nullable = false, length = 40)
    private Integer city;

    @Column(name = "state", nullable = false, length = 40)
    private Integer state;

    @Column(name = "country", nullable = false, length = 40)
    private Integer country;

    @Column(name = "pincode", nullable = false, length = 40)
    private String pincode;

    @Column(name = "mobile", nullable = false, length = 40)
    private String mobile;


    private String countryCode;

    @Column(name = "email", nullable = false, length = 40)
    private String email;

    @Column(name = "partner_type", nullable = false, length = 40)
    private String partnerType;

    @Column(name = "contact_person_name", nullable = false, length = 40)
    private String cpName;

    @Column(name = "company_name", nullable = false, length = 40)
    private String cname;

    @Column(name = "pan_details", nullable = false, length = 40)
    private String panName;


    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "parentpartnerid")
    private Partner parentPartner;

    @ManyToOne()
    @JoinColumn(name = "pricebookid")
    private PriceBook priceBookId;


    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "commission_share_type", nullable = false, length = 40)
    private String commissionShareType;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name= "new_customer_count")
    private Long newCustomerCount = 0L;

    @Column(name= "renew_customer_count")
    private Long renewCustomerCount = 0L;

    @Column(name= "total_customer_count")
    private Long totalCustomerCount = 0L;

    @Column(name = "calendartype", nullable = false, length = 100,columnDefinition = "varchar(100) default 'English'")
    private String calendarType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Column(name = "reset_date")
    private LocalDate resetDate;

    @Column(name= "credit_consume")
    private Double creditConsume = 0d;

    @Column(name = "region")
    private Long region ;

    @Column(name = "branch")
    private Long branch ;

    @Column(name = "bussiness_vertical")
    private Long bussinessvertical ;

    @Column(name = "dunning_activate_for")
    private String dunningActivateFor;

    @Column(name = "last_dunning_date")
    private LocalDateTime lastDunningDate;

    @Column(name = "is_dunning_enable")
    private Boolean isDunningEnable;

    @Column(name = "dunning_action")
    private String dunningAction;

    @Column(name = "commission_interval")
    private String commissionInterval;

    @Override
    public String toString() {
        return "Partner []";
    }

    public Partner(Integer id) {
        this.id = id;
    }

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

}
