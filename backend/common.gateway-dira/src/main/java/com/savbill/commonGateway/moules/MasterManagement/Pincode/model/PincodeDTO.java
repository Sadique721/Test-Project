package com.savbill.commonGateway.moules.MasterManagement.Pincode.model;


import com.savbill.commonGateway.core.data.Auditable;
import com.savbill.commonGateway.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class PincodeDTO extends Auditable implements IBaseDto {

    private Long pincodeid;
    private String pincode;
    private String status;
    private Boolean isDeleted = false;
    private Integer countryId;

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    private Integer stateId;
    private Integer cityId;
    private String cityName;
    private String stateName;
    private String countryName;
    private String areas;

    private Long displayId;
    private String displayName;

//    @JsonManagedReference
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<AreaDTO> areaList = new ArrayList<>();
    private Integer mvnoId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return pincodeid;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}

    @Override
    public void setMvnoId(Integer mvnoId){
        this.mvnoId = mvnoId;
    }

}
