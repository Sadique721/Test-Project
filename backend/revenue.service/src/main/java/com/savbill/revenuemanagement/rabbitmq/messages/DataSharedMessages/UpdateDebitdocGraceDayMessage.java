package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDebitdocGraceDayMessage {
    private Integer debitDocId;
    private Integer debitDocGraceDays;
}
