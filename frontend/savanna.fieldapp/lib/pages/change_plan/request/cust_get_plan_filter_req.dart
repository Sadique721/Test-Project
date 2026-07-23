class CustGetPlanByFiltersReq {
  String? changePlanType;
  int? custId;
  int? serviceId;
  int? customerServiceMappingID;
  String? plantype;
  int? currPlanId;

  CustGetPlanByFiltersReq(
      {this.changePlanType,
        this.custId,
        this.serviceId,
        this.customerServiceMappingID,
        this.plantype,
        this.currPlanId});

  CustGetPlanByFiltersReq.fromJson(Map<String, dynamic> json) {
    changePlanType = json['changePlanType'];
    custId = json['custId'];
    serviceId = json['serviceId'];
    customerServiceMappingID = json['customerServiceMappingID'];
    plantype = json['plantype'];
    currPlanId = json['currPlanId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['changePlanType'] = this.changePlanType;
    data['custId'] = this.custId;
    data['serviceId'] = this.serviceId;
    data['customerServiceMappingID'] = this.customerServiceMappingID;
    data['plantype'] = this.plantype;
    data['currPlanId'] = this.currPlanId;
    return data;
  }
}
