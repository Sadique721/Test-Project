package com.savbill.salescrmsbss.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.savbill.salescrmsbss.rabbitMq.message.PlanGroupMsg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmplangroup")
public class PlanGroup {
	@Id
    @Column(name = "plangroupid")
    private Integer planGroupId;
	
	@Column(name = "plangroupname", nullable = false, length = 40)
	private String planGroupName;
	
	@Column(name = "status", nullable = false, length = 40)
	private String status;
	
	@Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
	private Integer mvnoId;
	
	/*@ManyToOne
    @JoinColumn(name = "servicearea_id")
    private ServiceArea servicearea;*/

	@Column(name = "plantype", nullable = false, length = 40)
    private String plantype;

	@Column(name = "planmode", nullable = false, length = 50)
	private String planMode;
	
	/*@Column(name = "is_deleted",columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;;
	
	@Column(name = "dbr", nullable = true)
	private Double dbr;*/

	@Column(name = "BUID", nullable = false, length = 40, updatable = false)
	private Long buId;
	
	@Column(name = "plangrouptype", nullable = false, length = 100)
    private String planGroupType;

	@Column(name = "PLANCATEGORY", nullable = false, length = 40)
	private String category;

	@Column(name = "accessibility")
	private String accessibility;

	@Column(name = "allowdiscount")
	private boolean allowDiscount;

	@Column(name ="offerprice")
	private Double offerprice;

	@Column(name="invoicetoorg")
	private Boolean invoiceToOrg;

	@Column(name="requiredapproval")
	private Boolean requiredApproval;





	public PlanGroup(PlanGroupMsg message) {
		this.planGroupId = message.getPlanGroupId();
		this.planGroupName = message.getPlanGroupName();
		this.status = message.getStatus();
		this.mvnoId = message.getMvnoId();
		this.plantype = message.getPlantype();
		this.planMode = message.getPlanMode();
		//this.isDelete = message.getIsDelete();
		//this.dbr = message.getDbr();
		this.buId = message.getBuId();
		this.planGroupType = message.getPlanGroupType();
		/*if(message.getServicearea() != null) {
			this.servicearea = message.getServicearea();*/
		this.category = message.getCategory();
		this.accessibility = message.getAccessibility();
		this.allowDiscount = message.getAllowdiscount();
		this.offerprice = message.getOfferprice();
		this.invoiceToOrg = message.getInvoiceToOrg();
		this.requiredApproval = message.getRequiredApproval();
	}
}

