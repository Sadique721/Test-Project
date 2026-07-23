package com.savbill.integrationsystem.SendCreditNoteToGovernment.model;

import com.savbill.integrationsystem.core.dto.Auditable;
import com.savbill.integrationsystem.core.dto.IBaseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SendCreditNoteToGovernmentDto extends Auditable<Long> implements IBaseDto {

    private Long id;
    private String username;
    private String password;
    private String seller_pan;
    private String buyer_pan;
    private String fiscal_year;
    private String buyer_name;
    private String ref_invoice_number;
    private String credit_note_number;
    private String credit_note_date;
    private String reason_for_return;
    private Double total_sales;
    private Double taxable_sales_vat;
    private Double vat;
    private Double excisable_amount;
    private Double excise;
    private Double taxable_sales_hst;
    private Double hst;
    private Double amount_for_esf;
    private Double esf;
    private Double export_sales;
    private Double tax_exempted_sales;
    private Boolean isrealtime;
    private LocalDateTime datetimeclient;

    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Long getMvnoId() {
        return null;
    }

    @Override
    public void setMvnoId(Long mvnoId) {

    }
}
