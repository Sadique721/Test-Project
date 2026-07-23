package com.savbill.revenuemanagement.core.dto.invoice;

import com.savbill.revenuemanagement.productmanagement.Tax.dto.TaxTypeTierDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DebitDocumentTAXRelDto implements Serializable {

    private Integer debitdoctaxid;

    private Integer debitdocumentid;

    private Integer taxid;

    private String taxname;

    private String description;

    private Double percentage;

    private Double taxlevel;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime startdate;

    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime enddate;

    private Double amount;

    private Integer chargeid;

    private String taxLedgerId;

    private Double chargeAmount;

    private String planName;

    private List<TaxTypeTierDto> taxTypeTiers;
}
