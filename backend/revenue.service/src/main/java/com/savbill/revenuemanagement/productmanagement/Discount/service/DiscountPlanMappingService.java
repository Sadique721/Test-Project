package com.savbill.revenuemanagement.productmanagement.Discount.service;


import com.savbill.revenuemanagement.core.service.AbstractService;
import com.savbill.revenuemanagement.productmanagement.Discount.domain.DiscountMapping;
import com.savbill.revenuemanagement.productmanagement.Discount.dto.DiscountMappingPojo;
import com.savbill.revenuemanagement.productmanagement.Discount.mapper.DiscountPlanMappingMapper;
import com.savbill.revenuemanagement.productmanagement.Discount.repocitory.DiscountMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DiscountPlanMappingService extends AbstractService<DiscountMapping, DiscountMappingPojo
        , Integer> {

    @Autowired
    private DiscountMappingRepository entityRepository;
    @Autowired
    private DiscountPlanMappingMapper mapper;

    @Override
    protected JpaRepository<DiscountMapping, Integer> getRepository() {
        return entityRepository;
    }



}
