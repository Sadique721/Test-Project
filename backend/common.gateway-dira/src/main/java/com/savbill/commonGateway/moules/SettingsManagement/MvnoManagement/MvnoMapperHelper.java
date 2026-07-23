package com.savbill.commonGateway.moules.SettingsManagement.MvnoManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
public class MvnoMapperHelper {

    @Autowired
    private MvnoRepository mvnoRepository;

    @Named("mvnoFromId")
    public Mvno mvnoFromId(Long mvnoId) {
        return mvnoId != null ? mvnoRepository.findById(mvnoId).orElse(null) : null;
    }
}
