class DeactivatePlanReq {
  int? custId;
  List<DeactivatePlanReqDetail>? deactivatePlanReqModels;
  bool? planGroupChange;
  bool? planGroupFullyChanged;

  DeactivatePlanReq(
      {this.custId,
      this.deactivatePlanReqModels,
      this.planGroupChange,
      this.planGroupFullyChanged});

  DeactivatePlanReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    if (json['deactivatePlanReqModels'] != null) {
      deactivatePlanReqModels = <DeactivatePlanReqDetail>[];
      json['deactivatePlanReqModels'].forEach((v) {
        deactivatePlanReqModels!.add(new DeactivatePlanReqDetail.fromJson(v));
      });
    }
    planGroupChange = json['planGroupChange'];
    planGroupFullyChanged = json['planGroupFullyChanged'];
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
    return data;
  }
}

class DeactivatePlanReqDetail {
  int? newPlanGroupId;
  int? newPlanId;
  int? planGroupId;
  int? planId;

  DeactivatePlanReqDetail(
      {this.newPlanGroupId, this.newPlanId, this.planGroupId, this.planId});

  DeactivatePlanReqDetail.fromJson(Map<String, dynamic> json) {
    newPlanGroupId = json['newPlanGroupId'];
    newPlanId = json['newPlanId'];
    planGroupId = json['planGroupId'];
    planId = json['planId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['newPlanGroupId'] = this.newPlanGroupId;
    data['newPlanId'] = this.newPlanId;
    data['planGroupId'] = this.planGroupId;
    data['planId'] = this.planId;
    return data;
  }
}
