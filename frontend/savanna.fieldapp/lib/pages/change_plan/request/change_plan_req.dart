class ChangePlanReq {
  List<ChangePlanRequestDTOList>? changePlanRequestDTOList;

  ChangePlanReq({this.changePlanRequestDTOList});

  ChangePlanReq.fromJson(Map<String, dynamic> json) {
    if (json['changePlanRequestDTOList'] != null) {
      changePlanRequestDTOList = <ChangePlanRequestDTOList>[];
      json['changePlanRequestDTOList'].forEach((v) {
        changePlanRequestDTOList!.add(new ChangePlanRequestDTOList.fromJson(v));
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

class ChangePlanRequestDTOList {
  String? purchaseType;
  int? planGroupId;
  int? planId;
  String? isPaymentReceived;
  String? remarks;
  String? paymentOwner;
  RecordPaymentDTO? recordPaymentDTO;
  String? addonStartDate;
  bool? isAdvRenewal;
  int? custId;

  bool? isRefund;
  num? discount;
  List<PlanBindWithOldPlans>? planBindWithOldPlans;
  List<int>? newPlanList;
  List<int>? planMappingList;
  int? custServiceMappingId;

  ChangePlanRequestDTOList(
      {this.purchaseType,
        this.planGroupId,
        this.planId,
        this.isPaymentReceived,
        this.remarks,
        this.paymentOwner,
        this.recordPaymentDTO,
        this.addonStartDate,
        this.isAdvRenewal,
        this.custId,
        this.isRefund,
        this.discount,
        this.planBindWithOldPlans,
        this.newPlanList,
        this.planMappingList,
        this.custServiceMappingId
      });

  ChangePlanRequestDTOList.fromJson(Map<String, dynamic> json) {
    purchaseType = json['purchaseType'];
    planGroupId = json['planGroupId'];
    planId = json['planId'];
    isPaymentReceived = json['isPaymentReceived'];
    remarks = json['remarks'];
    paymentOwner = json['paymentOwner'];
    recordPaymentDTO = json['recordPaymentDTO'] != null
        ? new RecordPaymentDTO.fromJson(json['recordPaymentDTO'])
        : null;
    addonStartDate = json['addonStartDate'];
    isAdvRenewal = json['isAdvRenewal'];
    custId = json['custId'];
    isRefund = json['isRefund'];
    discount = json['discount'];
    if (json['planBindWithOldPlans'] != null) {
      planBindWithOldPlans = <PlanBindWithOldPlans>[];
      json['planBindWithOldPlans'].forEach((v) {
        planBindWithOldPlans!.add(new PlanBindWithOldPlans.fromJson(v));
      });
    }
    newPlanList = json['newPlanList'].cast<int>();
    planMappingList = json['planMappingList'].cast<int>();
    custServiceMappingId = json['custServiceMappingId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['purchaseType'] = this.purchaseType;
    data['planGroupId'] = this.planGroupId;
    data['planId'] = this.planId;
    data['isPaymentReceived'] = this.isPaymentReceived;
    data['remarks'] = this.remarks;
    data['paymentOwner'] = this.paymentOwner;
    if (this.recordPaymentDTO != null) {
      data['recordPaymentDTO'] = this.recordPaymentDTO!.toJson();
    }
    data['addonStartDate'] = this.addonStartDate;
    data['isAdvRenewal'] = this.isAdvRenewal;
    data['custId'] = this.custId;
    data['isRefund'] = this.isRefund;
    data['discount'] = this.discount;
    if (this.planBindWithOldPlans != null) {
      data['planBindWithOldPlans'] =
          this.planBindWithOldPlans!.map((v) => v.toJson()).toList();
    }
    data['newPlanList'] = this.newPlanList;
    data['planMappingList'] = this.planMappingList;
    data['custServiceMappingId'] = this.custServiceMappingId;
    return data;
  }
}

class RecordPaymentDTO {
  String? paymentAmount;
  String? paymentDate;
  String? paymentMode;
  String? referenceNo;
  String? bankName;
  String? branch;
  String? remarks;
  bool? isTdsDeducted;
  int? custId;
  String? chequeNo;
  String? chequeDate;

  RecordPaymentDTO(
      {this.paymentAmount,
        this.paymentDate,
        this.paymentMode,
        this.referenceNo,
        this.bankName,
        this.branch,
        this.remarks,
        this.isTdsDeducted,
        this.custId,
        this.chequeNo,
        this.chequeDate});

  RecordPaymentDTO.fromJson(Map<String, dynamic> json) {
    paymentAmount = json['paymentAmount'];
    paymentDate = json['paymentDate'];
    paymentMode = json['paymentMode'];
    referenceNo = json['referenceNo'];
    bankName = json['bankName'];
    branch = json['branch'];
    remarks = json['remarks'];
    isTdsDeducted = json['isTdsDeducted'];
    custId = json['custId'];
    chequeNo = json['chequeNo'];
    chequeDate = json['chequeDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paymentAmount'] = this.paymentAmount;
    data['paymentDate'] = this.paymentDate;
    data['paymentMode'] = this.paymentMode;
    data['referenceNo'] = this.referenceNo;
    data['bankName'] = this.bankName;
    data['branch'] = this.branch;
    data['remarks'] = this.remarks;
    data['isTdsDeducted'] = this.isTdsDeducted;
    data['custId'] = this.custId;
    data['chequeNo'] = this.chequeNo;
    data['chequeDate'] = this.chequeDate;
    return data;
  }
}

class PlanBindWithOldPlans {
  int? newPlanId;
  int? oldPlanId;

  PlanBindWithOldPlans({this.newPlanId, this.oldPlanId});

  PlanBindWithOldPlans.fromJson(Map<String, dynamic> json) {
    newPlanId = json['newPlanId'];
    oldPlanId = json['oldPlanId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['newPlanId'] = this.newPlanId;
    data['oldPlanId'] = this.oldPlanId;
    return data;
  }
}