package com.savbill.integrationsystem.RestApiService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustQuotaDetailsDTO {
    private Integer id;
    private Long planId;
    private String quotaType;
    private Double totalQuota = 0.0;
    private Double usedQuota = 0.0;
    private String quotaUnit;
    private Double timeTotalQuota = 0.0;

    private Double timeQuotaUsed = 0.0;

    private String timeQuotaUnit;
    private Boolean isDelete;
    private Double totalQuotaKB = 0.0;
    private Double usedQuotaKB = 0.0;

    private Double timeUsedQuotaSec = 0.0;
    private Double timeTotalQuotaSec = 0.0;
    private Double currentSessionUsageTime = 0.0;
    private Double currentSessionUsageVolume = 0.0;
//    private CustPlanMapppingDTO custPlanMappping;
    private Integer custid;
    private Double didTotalQuota;
    private Double didUsedQuota;
    private Double intercomTotalQuota;
    private Double intercomUsedQuota;
    private String didQuotaUnit;
    private String intercomQuotaUnit;
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime createdate;
    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    private LocalDateTime updatedate;
    private String createdByName;
    private String lastModifiedByName;
    private Integer createdById;
    private Integer lastModifiedById;
    private String parentQuotaType;
    private boolean isChunkAvailable;
    private Double reservedQuotaInPer;
    private Double totalReservedQuota;
    private String usageQuotaType;
    private Boolean skipQuotaUpdate;
    private Long cprId;

}

