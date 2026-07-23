package com.savbill.salescrmsbss.rabbitMq.message;

import lombok.Data;

@Data
public class PlanGroupMsg {

    private Integer planGroupId;
	private String planGroupName;
	private String status;
	//private ServiceArea servicearea;
	//private Boolean isDelete;
    private String plantype;
	private String planMode;
	private Integer mvnoId;
	private Long buId;
	//private Double dbr;
	private String planGroupType;

	private String category;
	private String accessibility;
	private Boolean allowdiscount;
	private Double offerprice;
	private Boolean invoiceToOrg;
	private Boolean requiredApproval;

}
