package com.savbill.cpm.modules.StaffUserService.Mapper;

import com.savbill.cpm.core.mapper.IBaseMapper;
import com.savbill.cpm.modules.StaffUserService.domain.StaffUserServiceMapping1;
import com.savbill.cpm.modules.StaffUserService.model.StaffUserServiceDTO;
import org.mapstruct.Mapper;

@Mapper
public interface StaffUserServiceMapper extends IBaseMapper<StaffUserServiceDTO, StaffUserServiceMapping1> {

}
