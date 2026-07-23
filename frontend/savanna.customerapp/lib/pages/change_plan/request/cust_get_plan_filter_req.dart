class CustGetPlanByFiltersReq {
  String? changePlanType;
  int? custId;
  int? serviceId;
  int? customerServiceMappingID;

  CustGetPlanByFiltersReq(
      {this.changePlanType,
        this.custId,
        this.serviceId,
        this.customerServiceMappingID});

  CustGetPlanByFiltersReq.fromJson(Map<String, dynamic> json) {
    changePlanType = json['changePlanType'];
    custId = json['custId'];
    serviceId = json['serviceId'];
    customerServiceMappingID = json['customerServiceMappingID'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['changePlanType'] = this.changePlanType;
    data['custId'] = this.custId;
    data['serviceId'] = this.serviceId;
    data['customerServiceMappingID'] = this.customerServiceMappingID;
    return data;
  }
}
