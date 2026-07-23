package com.savbill.inventorymanagement.modules.Services;

import com.savbill.inventorymanagement.core.dto.IBaseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ServicesDTO implements IBaseDto {
        private Long id;
        private String serviceName;
        private Integer mvnoId;
        @JsonIgnore
        @Override
        public Long getIdentityKey() {
                return id;
        }

		@Override
		public Integer getMvnoId() {
			// TODO Auto-generated method stub
			return mvnoId;
		}

//    @Override
//    public Long getBuId() {
//        return null;
//    }
}
