package com.savbill.revenuemanagement.rabbitmq.messages;

import com.savbill.revenuemanagement.core.MvnoDiscountManagement.MvnoDiscountDTO;
import lombok.Data;

import java.util.Date;

@Data
public class MvnoDiscountMessage {
    private String messageId;
    private String message;
    private Date messageDate;
    private String sourceName;
    private String operation;
    private MvnoDiscountDTO mvnoDiscountDTO;
}
