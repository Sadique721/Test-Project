package com.savbill.radius.helper;

import com.savbill.radius.kafka.CustomMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Customer Update Quota", description = "This is data transfer object for customer which is used to update customer data")
public class UpdateCustomerQuotaDto {
    @ApiModelProperty(notes = "Name of the user", required = true)
    private String userName;
    private Double usedQuota;
    private Double usedQuotaKB;
    private Double usedTimeQuota;
    private Double usedTimeQuotaSec;
    @ApiModelProperty(hidden = true)
    private Long mvnoId;
    private Integer custId;
    private Integer quotaDetailId;
    private boolean isChunkAvailable;
    private Integer reservedQuotaInPer;
    private boolean skipQuotaUpdate;
    private LocalDate nextQuotaReset;

    public UpdateCustomerQuotaDto(CustomMessage message) {

        Map<String, Object> map = message.getCustomerData();
        if (map.get("mvnoId") != null)
            this.setMvnoId(Long.parseLong(map.get("mvnoId").toString()));

        if (map.get("custId") != null)
            this.setCustId(Integer.parseInt(map.get("custId").toString()));

        if (map.get("quotaDetailId") != null)
            this.setQuotaDetailId(Integer.parseInt(map.get("quotaDetailId").toString()));

        if (map.get("userName") != null)
            this.setUserName(map.get("userName").toString());

        if (map.get("usedQuota") != null)
            this.setUsedQuota(Double.parseDouble(map.get("usedQuota").toString()));

        if (map.get("usedQuotaKB") != null)
            this.setUsedQuotaKB(Double.parseDouble(map.get("usedQuotaKB").toString()));

        if (map.get("usedTimeQuota") != null)
            this.setUsedTimeQuota(Double.parseDouble(map.get("usedTimeQuota").toString()));

        if (map.get("usedTimeQuotaSec") != null)
            this.setUsedTimeQuotaSec(Double.parseDouble(map.get("usedTimeQuotaSec").toString()));
        if (map.get("isChunkAvailable") != null)
            this.isChunkAvailable = Boolean.parseBoolean(map.get("isChunkAvailable").toString());
        if (map.get("reservedQuotaInPer") != null)
            this.reservedQuotaInPer = Integer.valueOf(map.get("reservedQuotaInPer").toString());
        if (map.get("skipQuotaUpdate") != null)
            this.skipQuotaUpdate = Boolean.parseBoolean(map.get("skipQuotaUpdate").toString());
        else
            this.skipQuotaUpdate = true;
    }
}
