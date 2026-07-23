package com.savbill.integrationsystem.billgen.model;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DebitDocDetailsDTO {

	private Integer debitdocdetailid;
	private Integer debitdocumentid;
	private Integer chargeid;
	private String chargename;
	private String description;
	private String chargetype;
	private String chargecycle;
	private Double subtotal;
	private Integer tax;
	private Integer discount;
	private Double totalamount;
	private LocalDateTime startdate;
	private LocalDateTime enddate;
	private String prorationtype;
	private Integer noofcycle;
	private String planId;
	private String ledgerId;
	private String icCode;
	private String pushableLedgerId;
}
