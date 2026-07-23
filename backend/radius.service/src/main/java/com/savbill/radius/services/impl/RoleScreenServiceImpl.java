package com.savbill.radius.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.savbill.radius.entity.QRoleScreens;
import com.savbill.radius.entity.RoleScreens;
import com.savbill.radius.repository.RoleScreensRepository;
import com.savbill.radius.services.RoleScreenService;
import com.savbill.radius.utils.ValidateCrudTransactionData;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class RoleScreenServiceImpl implements RoleScreenService {
    
    @Autowired
    private RoleScreensRepository roleScreensRepository;
    
    @Override
    public List<RoleScreens> getScreensByRole(Long id, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(id))
                throw new IllegalArgumentException("Please enter valid proxy server id.");
            QRoleScreens qRoleScreens = QRoleScreens.roleScreens;
            BooleanExpression boolExp = qRoleScreens.isNotNull();
            if(mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qRoleScreens.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
            boolExp = boolExp.and(qRoleScreens.roleId.eq(id));

            return  (List<RoleScreens>) roleScreensRepository.findAll(boolExp);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<RoleScreens> getScreensByRoles(List<Long> ids, Long mvnoId) {
        try {
            QRoleScreens qRoleScreens = QRoleScreens.roleScreens;
            BooleanExpression boolExp = qRoleScreens.isNotNull();
            if(mvnoId == null || mvnoId != 1)
                boolExp = boolExp.and(qRoleScreens.mvnoId.in(mvnoId, 1));
            boolExp = boolExp.and(qRoleScreens.roleId.in(ids));

            return  (List<RoleScreens>) roleScreensRepository.findAll(boolExp);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
