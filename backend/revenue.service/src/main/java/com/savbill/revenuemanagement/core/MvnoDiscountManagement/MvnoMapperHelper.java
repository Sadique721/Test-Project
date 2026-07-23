package com.savbill.revenuemanagement.core.MvnoDiscountManagement;

import com.savbill.revenuemanagement.core.Mvno.domain.Mvno;
import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
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
