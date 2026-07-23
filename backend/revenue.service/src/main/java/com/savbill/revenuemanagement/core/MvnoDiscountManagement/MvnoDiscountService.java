package com.savbill.revenuemanagement.core.MvnoDiscountManagement;

import com.savbill.revenuemanagement.rabbitmq.messages.MvnoDiscountMessage;

public interface MvnoDiscountService {

    MvnoDiscountDTO saveMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO);
    void saveMvnoDiscountFromMessageReceiver(MvnoDiscountMessage mvnoDiscountMessage);

    MvnoDiscountDTO updateMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO);

    boolean deleteAllMvnoDiscountByMvnoId(Long mvnoId);
}
