package com.savbill.radius.helper;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerQuotaDataForReset {
    private Integer cqdId; //quotadtlsid
    private Double usedQuota = 0.0; //usedquota
    private Double usedQuotaKB = 0.0; //usedquotakb
    private Double timeQuotaUsed = 0.0; // timequotaused
    private Double timeUsedQuotaSec = 0.0; //timeusedquotasec
    private boolean isChunkAvailable; //is_chunk_available
    private Double reservedQuotaInPer; //reserved_quota_in_per
    private Boolean skipQuotaUpdate; //skip_quota_update
    private LocalDateTime lastQuotaReset; //last_quota_reset
}
