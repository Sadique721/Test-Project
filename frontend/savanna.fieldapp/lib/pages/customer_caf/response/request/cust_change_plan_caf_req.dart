class CustChangePlanCafReq {
  int? custId;
  List<CustDeactivatePlanReqModel>? deactivatePlanReqModels;
  bool? planGroupChange;
  bool? planGroupFullyChanged;
  String? paymentOwner;
  int? paymentOwnerId;
  int? billableCustomerId;
  String? changePlanBillingCycle;

  CustChangePlanCafReq(
      {this.custId,
        this.deactivatePlanReqModels,
        this.planGroupChange,
        this.planGroupFullyChanged,
        this.paymentOwner,
        this.paymentOwnerId,
        this.billableCustomerId,
        this.changePlanBillingCycle});

  CustChangePlanCafReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    if (json['deactivatePlanReqModels'] != null) {
      deactivatePlanReqModels = <CustDeactivatePlanReqModel>[];
      json['deactivatePlanReqModels'].forEach((v) {
        deactivatePlanReqModels!.add(new CustDeactivatePlanReqModel.fromJson(v));
      });
    }
    planGroupChange = json['planGroupChange'];
    planGroupFullyChanged = json['planGroupFullyChanged'];
    paymentOwner = json['paymentOwner'];
    paymentOwnerId = json['paymentOwnerId'];
    billableCustomerId = json['billableCustomerId'];
    changePlanBillingCycle = json['changePlanBillingCycle'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    if (this.deactivatePlanReqModels != null) {
      data['deactivatePlanReqModels'] =
          this.deactivatePlanReqModels!.map((v) => v.toJson()).toList();
    }
    data['planGroupChange'] = this.planGroupChange;
    data['planGroupFullyChanged'] = this.planGroupFullyChanged;
    data['paymentOwner'] = this.paymentOwner;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['billableCustomerId'] = this.billableCustomerId;
    data['changePlanBillingCycle'] = this.changePlanBillingCycle;
    return data;
  }
}

class CustDeactivatePlanReqModel {
  String? newPlanGroupId;
  int? newPlanId;
  String? planGroupId;
  int? planId;
  int? custServiceMappingId;
  String? discount;

  CustDeactivatePlanReqModel(
      {this.newPlanGroupId,
        this.newPlanId,
        this.planGroupId,
        this.planId,
        this.custServiceMappingId,
        this.discount});

  CustDeactivatePlanReqModel.fromJson(Map<String, dynamic> json) {
    newPlanGroupId = json['newPlanGroupId'];
    newPlanId = json['newPlanId'];
    planGroupId = json['planGroupId'];
    planId = json['planId'];
    custServiceMappingId = json['custServiceMappingId'];
    discount = json['discount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['newPlanGroupId'] = this.newPlanGroupId;
    data['newPlanId'] = this.newPlanId;
    data['planGroupId'] = this.planGroupId;
    data['planId'] = this.planId;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['discount'] = this.discount;
    return data;
  }
}
