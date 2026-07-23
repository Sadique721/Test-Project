class ChangePlanRequest {
  String? purchaseType;
  String? planId;
  String? isPaymentReceived;
  String? remarks;
  String? addonStartDate;
  bool? isAdvRenewal;
  int? custId;
  bool? isRefund;

  ChangePlanRequest(
      {this.purchaseType,
        this.planId,
        this.isPaymentReceived,
        this.remarks,
        this.addonStartDate,
        this.isAdvRenewal,
        this.custId,
        this.isRefund});

  ChangePlanRequest.fromJson(Map<String, dynamic> json) {
    purchaseType = json['purchaseType'];
    planId = json['planId'];
    isPaymentReceived = json['isPaymentReceived'];
    remarks = json['remarks'];
    addonStartDate = json['addonStartDate'];
    isAdvRenewal = json['isAdvRenewal'];
    custId = json['custId'];
    isRefund = json['isRefund'];
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
    return data;
  }
}