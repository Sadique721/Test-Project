class CancelTrailPlanReq {
  String? billingStartFrom;
  int? cprId;
  int? custId;
  String? extendDays;
  int? planGroupId;
  int? planId;
  String? remarks;
  int? custpackageid;
  String? planCancelRemarks;

  CancelTrailPlanReq({
    this.billingStartFrom,
    this.cprId,
    this.custId,
    this.extendDays,
    this.planGroupId,
    this.planId,
    this.remarks,
    this.custpackageid,
    this.planCancelRemarks,
  });

  CancelTrailPlanReq.fromJson(Map<String, dynamic> json) {
    billingStartFrom = json['billingStartFrom'];
    cprId = json['cprId'];
    custId = json['custId'];
    extendDays = json['extendDays'];
    planGroupId = json['planGroupId'];
    planId = json['planId'];
    remarks = json['remarks'];
    custpackageid = json['custpackageid'];
    planCancelRemarks = json['planCancelRemarks'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['billingStartFrom'] = this.billingStartFrom;
    data['cprId'] = this.cprId;
    data['custId'] = this.custId;
    data['extendDays'] = this.extendDays;
    data['planGroupId'] = this.planGroupId;
    data['planId'] = this.planId;
    data['remarks'] = this.remarks;
    data['custpackageid'] = this.custpackageid;
    data['planCancelRemarks'] = this.planCancelRemarks;
    return data;
  }
}
