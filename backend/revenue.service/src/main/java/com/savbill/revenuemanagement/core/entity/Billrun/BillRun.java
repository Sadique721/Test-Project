package com.savbill.revenuemanagement.core.entity.Billrun;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "TBLMBILLRUN")
@NoArgsConstructor
public class BillRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billrunid", nullable = false, length = 40)
    private Integer id;

    @CreationTimestamp
    @Column(name = "billruncreatedate", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @Column(name = "billrundate", nullable = false, updatable = false)
    private LocalDateTime rundate;

    @Column(name = "billruncount", nullable = false, length = 40)
    private Integer billruncount;

    @Column(name = "amount", nullable = false, length = 40)
    private Double amount;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @Column(name = "billruncompletedate", nullable = false, updatable = false)
    private LocalDateTime billrunfinishdate;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "type", nullable = false, length = 40)
    private String type;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name = "lcoid")
    private Integer lcoId;

    @Column(name = "SUCCESSCOUNT")
    private Integer successCount;

    @Column(name = "failcount")
    private Integer failCount;

    public BillRun(Integer billruncount, Double amount, String status, LocalDateTime billrunfinishdate,
                   Boolean isDelete, Integer mvnoId, String type, Long buId, Integer lcoId, Integer successCount, Integer failCount) {
        this.createdate = LocalDateTime.now();
        this.rundate = LocalDateTime.now();
        this.billruncount = billruncount;
        this.amount = amount;
        this.status = status;
        this.billrunfinishdate = billrunfinishdate;
        this.isDelete = isDelete;
        this.mvnoId = mvnoId;
        this.type = type;
        this.buId = buId;
        this.lcoId = lcoId;
        this.successCount = successCount;
        this.failCount = failCount;
    }
}

