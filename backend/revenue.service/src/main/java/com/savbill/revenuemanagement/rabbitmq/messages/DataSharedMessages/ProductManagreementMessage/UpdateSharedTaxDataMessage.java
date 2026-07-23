package com.savbill.revenuemanagement.rabbitmq.messages.DataSharedMessages.ProductManagreementMessage;

import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeSlab;
import com.savbill.revenuemanagement.productmanagement.Tax.domain.TaxTypeTier;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UpdateSharedTaxDataMessage {
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String desc;

    @NotNull
    private String taxtype;

    private String status;
    private Integer mvnoId;
    private Long buId;
    // private String ledgerId;
    private Boolean isDelete = false;
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    private List<TaxTypeTier> tieredList = new ArrayList<>();
}
