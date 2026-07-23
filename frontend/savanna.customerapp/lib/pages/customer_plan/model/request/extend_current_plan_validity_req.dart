class ExtendCurrentPlanValidityReq {
  List<ExtendPlanValidity>? extendPlanValidity;

  ExtendCurrentPlanValidityReq({this.extendPlanValidity});

  ExtendCurrentPlanValidityReq.fromJson(Map<String, dynamic> json) {
    if (json['extendPlanValidity'] != null) {
      extendPlanValidity = <ExtendPlanValidity>[];
      json['extendPlanValidity'].forEach((v) {
        extendPlanValidity!.add(new ExtendPlanValidity.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.extendPlanValidity != null) {
      data['extendPlanValidity'] =
          this.extendPlanValidity!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ExtendPlanValidity {
  int? custPlanMapppingId;
  bool? extentionforChild;
  String? downStartDate;
  String? downEndDate;
  String? extendValidityRemarks;
  bool? planGroup;
  int? planGroupId;

  ExtendPlanValidity(
      {this.custPlanMapppingId,
        this.extentionforChild,
        this.downStartDate,
        this.downEndDate,
        this.extendValidityRemarks,
        this.planGroup,
        this.planGroupId});

  ExtendPlanValidity.fromJson(Map<String, dynamic> json) {
    custPlanMapppingId = json['custPlanMapppingId'];
    extentionforChild = json['extentionforChild'];
    downStartDate = json['downStartDate'];
    downEndDate = json['downEndDate'];
    extendValidityRemarks = json['extend_validity_remarks'];
    planGroup = json['planGroup'];
    planGroupId = json['planGroupId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['extentionforChild'] = this.extentionforChild;
    data['downStartDate'] = this.downStartDate;
    data['downEndDate'] = this.downEndDate;
    data['extend_validity_remarks'] = this.extendValidityRemarks;
    data['planGroup'] = this.planGroup;
    data['planGroupId'] = this.planGroupId;
    return data;
  }
}



