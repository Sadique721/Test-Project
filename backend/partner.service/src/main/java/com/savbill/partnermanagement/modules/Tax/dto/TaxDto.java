package com.savbill.partnermanagement.modules.Tax.dto;

import com.savbill.partnermanagement.core.data.Auditable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor

public class TaxDto extends Auditable {

    String invoiceId;

    String name;

    double percentage;

    double absoluteAmount;

    /* SLAB */
    double rangefrom;
    double rangeupto;

    int level;

    Date startDate;

    Date endDate;

    double taxAmount;

    String description;

    String tiertaxid;

    String slabtaxid;

    boolean beforetax;

    String chargeid;

    String ledgerId;

}
