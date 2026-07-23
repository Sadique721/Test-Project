package com.savbill.radius.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblcustquotaresetdtls")
public class CustQuotaResetDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "cprid")
    private Long cprId;

    @Column(name = "custid")
    private Long custId;

    @Column(name = "totalquotaused")
    private Double totalQuotaUsed = 0.0;

    @Column(name = "quotaunit")
    private String quotaUnit;

    @Column(name = "totaltimequota")
    private Double totalTimeQuota = 0.0;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "createdate", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "lastmodifieddate")
    private LocalDateTime updatedate;

    @Override
    public String toString() {
        return "CustQuotaResetDetails{" +
                "id=" + id +
                ", cprId=" + cprId +
                ", custId=" + custId +
                ", totalQuotaUsed=" + totalQuotaUsed +
                ", quotaUnit='" + quotaUnit + '\'' +
                ", totalTimeQuota=" + totalTimeQuota +
                ", createdate=" + createdate +
                ", updatedate=" + updatedate +
                '}';
    }

}
