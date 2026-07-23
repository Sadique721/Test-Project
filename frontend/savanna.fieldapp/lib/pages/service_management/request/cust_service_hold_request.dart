class CustomerServiceHoldReq {
  int? custId;
  List<DeactivatePlanReqModels>? deactivatePlanReqModels;

  CustomerServiceHoldReq({this.custId, this.deactivatePlanReqModels});

  CustomerServiceHoldReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    if (json['deactivatePlanReqModels'] != null) {
      deactivatePlanReqModels = <DeactivatePlanReqModels>[];
      json['deactivatePlanReqModels'].forEach((v) {
        deactivatePlanReqModels!.add(new DeactivatePlanReqModels.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    if (this.deactivatePlanReqModels != null) {
      data['deactivatePlanReqModels'] =
          this.deactivatePlanReqModels!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class DeactivatePlanReqModels {
  int? custServiceMappingId;
  String? remarks;
  String? reasonId;

  DeactivatePlanReqModels(
      {this.custServiceMappingId, this.remarks, this.reasonId});

  DeactivatePlanReqModels.fromJson(Map<String, dynamic> json) {
    custServiceMappingId = json['custServiceMappingId'];
    remarks = json['remarks'];
    reasonId = json['reasonId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['remarks'] = this.remarks;
    data['reasonId'] = this.reasonId;
    return data;
  }
}
