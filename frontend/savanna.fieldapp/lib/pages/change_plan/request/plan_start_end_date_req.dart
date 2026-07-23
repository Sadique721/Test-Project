class PlanStartEndDateReq {
  List<PlanStartEndDateDetailReq>? changePlanRequestDTOList;

  PlanStartEndDateReq({this.changePlanRequestDTOList});

  PlanStartEndDateReq.fromJson(Map<String, dynamic> json) {
    if (json['changePlanRequestDTOList'] != null) {
      changePlanRequestDTOList = <PlanStartEndDateDetailReq>[];
      json['changePlanRequestDTOList'].forEach((v) {
        changePlanRequestDTOList!
            .add(new PlanStartEndDateDetailReq.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.changePlanRequestDTOList != null) {
      data['changePlanRequestDTOList'] =
          this.changePlanRequestDTOList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PlanStartEndDateDetailReq {
  String? purchaseType;
  int? planId;
  String? isPaymentReceived;
  String? remarks;
  String? addonStartDate;
  bool? isAdvRenewal;
  int? custId;
  bool? isRefund;
  int? discount;

  PlanStartEndDateDetailReq(
      {this.purchaseType,
      this.planId,
      this.isPaymentReceived,
      this.remarks,
      this.addonStartDate,
      this.isAdvRenewal,
      this.custId,
      this.isRefund,
      this.discount});

  PlanStartEndDateDetailReq.fromJson(Map<String, dynamic> json) {
    purchaseType = json['purchaseType'];
    planId = json['planId'];
    isPaymentReceived = json['isPaymentReceived'];
    remarks = json['remarks'];
    addonStartDate = json['addonStartDate'];
    isAdvRenewal = json['isAdvRenewal'];
    custId = json['custId'];
    isRefund = json['isRefund'];
    discount = json['discount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['purchaseType'] = this.purchaseType;
    data['planId'] = this.planId;
    data['isPaymentReceived'] = this.isPaymentReceived;
    data['remarks'] = this.remarks;
    data['addonStartDate'] = this.addonStartDate;
    data['isAdvRenewal'] = this.isAdvRenewal;
    data['custId'] = this.custId;
    data['isRefund'] = this.isRefund;
    data['discount'] = this.discount;
    return data;
  }
}
