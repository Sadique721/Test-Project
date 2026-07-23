package com.savbill.partnermanagement.modules.PriceGroup.model;

import com.savbill.partnermanagement.core.data.Auditable;
import com.savbill.partnermanagement.core.dto.IBaseDto;
import com.savbill.partnermanagement.core.dto.IBaseDto2;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PriceBookDTO extends Auditable implements IBaseDto, IBaseDto2 {

    private Long id;
    @NotNull
    private String bookname;
    private LocalDateTime validfrom;
    private LocalDateTime validto;
    @NotNull
    private String status;
    private Boolean isDeleted = false;
    private String description;

    private String validFromString;
    private String validToString;

    private Integer noPartnerAssociate;
    
    @NotNull
    private String agrPercentage;
    
    @NotNull
    private String tdsPercentage;

    @JsonManagedReference
    private List<PriceBookPlanDetailDTO> priceBookPlanDetailList = new ArrayList<>();
    private Integer mvnoId;

    private Long buId;

    private String revenueType;

    private String commission_on;

    private Boolean isAllPlanSelected=false;

    private Boolean isAllPlanGroupSelected = false;

    private Integer revenueSharePercentage;

    private Integer partnerId;
    
    @JsonManagedReference
    private List<ServiceCommissionDTO> serviceCommissionList = new ArrayList<>();

    @JsonManagedReference
    private List<PriceBookSlabDetailsDTO> priceBookSlabDetailsList = new ArrayList<>();

    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }

    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
