package com.savbill.revenuemanagement.core.MvnoDiscountManagement.MvnoDicountImpl;

import com.savbill.revenuemanagement.core.Mvno.repository.MvnoRepository;
import com.savbill.revenuemanagement.core.MvnoDiscountManagement.*;
import com.savbill.revenuemanagement.core.MvnoDiscountManagement.*;
import com.savbill.revenuemanagement.core.mapper.common.CycleAvoidingMappingContext;
import com.savbill.revenuemanagement.rabbitmq.messages.MvnoDiscountMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class MvnoDicountServiceImpl implements MvnoDiscountService {

    @Autowired
    private MvnoDiscountMappingRepository discountMappingRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private MvnoDiscountMapper mvnoDiscountMapper;

    @Override
    public MvnoDiscountDTO saveMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO) {
        try {
            List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountDTO.getMvnoDiscountMappings();
            if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                List<MvnoDiscountMapping> mvnoDiscountMappings = new ArrayList<>();//mvnoDiscountMapper.dtoToDomain(mvnoDiscountMappingDTOS, new CycleAvoidingMappingContext());
                for (MvnoDiscountMappingDTO discountMapping: mvnoDiscountMappingDTOS) {
                    discountMapping.setMvnoId(mvnoDiscountDTO.getMvnoId());
                    mvnoDiscountMappings.add(mvnoDiscountMapper.dtoToDomain(discountMapping, new CycleAvoidingMappingContext()));
                }
                discountMappingRepository.saveAll(mvnoDiscountMappings);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable To save Mvno Discount");
        }
        return mvnoDiscountDTO;
    }

    @Override
    public void saveMvnoDiscountFromMessageReceiver(MvnoDiscountMessage mvnoDiscountMessage) {
        if(mvnoDiscountMessage.getOperation().equalsIgnoreCase("save")) {
            saveMvnoDiscount(mvnoDiscountMessage.getMvnoDiscountDTO());
        } else {
            updateMvnoDiscount(mvnoDiscountMessage.getMvnoDiscountDTO());
        }
    }

    @Override
    public MvnoDiscountDTO updateMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO) {
        try {
            if(deleteAllMvnoDiscountByMvnoId(mvnoDiscountDTO.getMvnoId())) {
                List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountDTO.getMvnoDiscountMappings();
                if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                    saveMvnoDiscount(mvnoDiscountDTO);
                }
            } else {
                throw new RuntimeException("Unable To Update Mvno Discount");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable To Update Mvno Discount, Please retry after some time..!");
        }
        return mvnoDiscountDTO;
    }

    @Override
    public boolean deleteAllMvnoDiscountByMvnoId(Long mvnoId) {
        try {
            discountMappingRepository.deleteAllByMvnoId(mvnoId);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}
