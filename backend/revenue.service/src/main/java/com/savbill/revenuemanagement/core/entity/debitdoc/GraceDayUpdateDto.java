package com.savbill.revenuemanagement.core.entity.debitdoc;

import lombok.Data;

@Data
public class GraceDayUpdateDto {
    private Integer debitDocId;
    private Integer debitDocGraceDays;
}
