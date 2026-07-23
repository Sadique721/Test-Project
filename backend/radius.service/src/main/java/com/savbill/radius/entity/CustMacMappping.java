package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Data
@ToString
@Table(name = "tblcustmacmapping")
//@EntityListeners(AuditableListener.class)
public class CustMacMappping {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custmacmapid", nullable = false, length = 40)
    private Integer id;

    @Column(name = "macaddress", length = 100)
    private String macAddress;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "custid")
    private Customers customer;

//    @Column(name = "custid")
//    private Integer custid;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

//    @Autowired
//    private CustomersRepository customersRepository;

    public CustMacMappping() {
    }

    public CustMacMappping(CustMacMappping custMacMappping) {
        this.id = custMacMappping.getId();
        this.macAddress = custMacMappping.getMacAddress();
        this.customer = custMacMappping.getCustomer();
        this.isDeleted = custMacMappping.getIsDeleted();
    }

    public CustMacMappping(Map map) {
        if (map.get("id") != null)
            this.id = Integer.parseInt(map.get("id").toString());
        if (map.get("customer") != null)
            this.customer = new Customers((Map)map.get("customer"));
        if (map.get("isDelete") != null)
            this.isDeleted = Boolean.parseBoolean(map.get("isDelete").toString());
        if (map.get("macAddress") != null)
            this.macAddress = map.get("macAddress").toString();
    }

}
