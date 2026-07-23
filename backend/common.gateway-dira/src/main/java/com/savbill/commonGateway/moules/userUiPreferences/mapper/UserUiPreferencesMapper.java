package com.savbill.commonGateway.moules.userUiPreferences.mapper;

import com.savbill.commonGateway.core.mapper.IBaseMapper;
import com.savbill.commonGateway.moules.userUiPreferences.domain.UserUiPreferences;
import com.savbill.commonGateway.moules.userUiPreferences.model.UserUiPreferencesDTO;
import org.mapstruct.Mapper;

@Mapper
public abstract class UserUiPreferencesMapper implements IBaseMapper<UserUiPreferencesDTO, UserUiPreferences> {

}
