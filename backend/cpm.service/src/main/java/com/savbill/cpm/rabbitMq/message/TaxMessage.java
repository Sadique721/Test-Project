package com.savbill.cpm.rabbitMq.message;

import com.savbill.cpm.model.postpaid.Tax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxMessage {


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

    public TaxMessage(Tax obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.desc = obj.getDesc();
        this.taxtype = obj.getTaxtype();
        this.status = obj.getStatus();
        this.mvnoId = obj.getMvnoId();
        this.buId = obj.getBuId();
        this.isDelete = obj.getIsDelete();
    }

}
