import 'package:savbill/webservices/base_response.dart';

class DeActivatePlanCustRes extends BaseResponse{
  DeActivateResponse? deActivateResponse;
  String? timestamp;
  int? status;

  DeActivatePlanCustRes({this.deActivateResponse, this.timestamp, this.status});

  DeActivatePlanCustRes.fromJson(Map<String, dynamic> json) {
    deActivateResponse = json['deActivateResponse'] != null
        ? new DeActivateResponse.fromJson(json['deActivateResponse'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.deActivateResponse != null) {
      data['deActivateResponse'] = this.deActivateResponse!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class DeActivateResponse {
  List<DeactivatePlanReqDTOS>? deactivatePlanReqDTOS;
  dynamic deactivatePlanReqModels;
  dynamic recordPayment;
  dynamic custId;
  bool? serviceStopBulkFlag;

  DeActivateResponse(
      {this.deactivatePlanReqDTOS,
        this.deactivatePlanReqModels,
        this.recordPayment,
        this.custId,
        this.serviceStopBulkFlag});

  DeActivateResponse.fromJson(Map<String, dynamic> json) {
    if (json['deactivatePlanReqDTOS'] != null) {
      deactivatePlanReqDTOS = <DeactivatePlanReqDTOS>[];
      json['deactivatePlanReqDTOS'].forEach((v) {
        deactivatePlanReqDTOS!.add(new DeactivatePlanReqDTOS.fromJson(v));
      });
    }
    deactivatePlanReqModels = json['deactivatePlanReqModels'];
    recordPayment = json['recordPayment'];
    custId = json['custId'];
    serviceStopBulkFlag = json['serviceStopBulkFlag'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.deactivatePlanReqDTOS != null) {
      data['deactivatePlanReqDTOS'] =
          this.deactivatePlanReqDTOS!.map((v) => v.toJson()).toList();
    }
    data['deactivatePlanReqModels'] = this.deactivatePlanReqModels;
    data['recordPayment'] = this.recordPayment;
    data['custId'] = this.custId;
    data['serviceStopBulkFlag'] = this.serviceStopBulkFlag;
    return data;
  }
}

class DeactivatePlanReqDTOS {
  int? custId;
  List<DeactivatePlanReqModels>? deactivatePlanReqModels;
  String? paymentOwner;
  int? billableCustomerId;
  int? paymentOwnerId;
  bool? isParent;
  dynamic debitDocIds;
  dynamic recordPaymentDTO;
  dynamic changePlanDate;
  bool? planGroupChange;
  bool? planGroupFullyChanged;

  DeactivatePlanReqDTOS(
      {this.custId,
        this.deactivatePlanReqModels,
        this.paymentOwner,
        this.billableCustomerId,
        this.paymentOwnerId,
        this.isParent,
        this.debitDocIds,
        this.recordPaymentDTO,
        this.changePlanDate,
        this.planGroupChange,
        this.planGroupFullyChanged});

  DeactivatePlanReqDTOS.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    if (json['deactivatePlanReqModels'] != null) {
      deactivatePlanReqModels = <DeactivatePlanReqModels>[];
      json['deactivatePlanReqModels'].forEach((v) {
        deactivatePlanReqModels!.add(new DeactivatePlanReqModels.fromJson(v));
      });
    }
    paymentOwner = json['paymentOwner'];
    billableCustomerId = json['billableCustomerId'];
    paymentOwnerId = json['paymentOwnerId'];
    isParent = json['isParent'];
    debitDocIds = json['debitDocIds'];
    recordPaymentDTO = json['recordPaymentDTO'];
    changePlanDate = json['changePlanDate'];
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
    data['paymentOwner'] = this.paymentOwner;
    data['billableCustomerId'] = this.billableCustomerId;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['isParent'] = this.isParent;
    data['debitDocIds'] = this.debitDocIds;
    data['recordPaymentDTO'] = this.recordPaymentDTO;
    data['changePlanDate'] = this.changePlanDate;
    data['planGroupChange'] = this.planGroupChange;
    data['planGroupFullyChanged'] = this.planGroupFullyChanged;
    return data;
  }
}

class DeactivatePlanReqModels {
  Null? planId;
  int? newPlanId;
  Null? planGroupId;
  Null? newPlanGroupId;
  int? cprId;
  int? discount;
  int? custServiceMappingId;
  Null? newAmount;
  Null? creditDocumentId;
  Null? isFromFlutterWave;
  int? reasonId;
  Null? remarks;
  List<int>? debitDocIds;
  bool? billToOrg;

  DeactivatePlanReqModels(
      {this.planId,
        this.newPlanId,
        this.planGroupId,
        this.newPlanGroupId,
        this.cprId,
        this.discount,
        this.custServiceMappingId,
        this.newAmount,
        this.creditDocumentId,
        this.isFromFlutterWave,
        this.reasonId,
        this.remarks,
        this.debitDocIds,
        this.billToOrg});

  DeactivatePlanReqModels.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    newPlanId = json['newPlanId'];
    planGroupId = json['planGroupId'];
    newPlanGroupId = json['newPlanGroupId'];
    cprId = json['cprId'];
    discount = json['discount'];
    custServiceMappingId = json['custServiceMappingId'];
    newAmount = json['newAmount'];
    creditDocumentId = json['creditDocumentId'];
    isFromFlutterWave = json['isFromFlutterWave'];
    reasonId = json['reasonId'];
    remarks = json['remarks'];
    debitDocIds = (json['debitDocIds'] != null)
        ? json['debitDocIds'].cast<int>()
        : [];
    //debitDocIds = json['debitDocIds'].cast<int>();
    billToOrg = json['billToOrg'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['newPlanId'] = this.newPlanId;
    data['planGroupId'] = this.planGroupId;
    data['newPlanGroupId'] = this.newPlanGroupId;
    data['cprId'] = this.cprId;
    data['discount'] = this.discount;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['newAmount'] = this.newAmount;
    data['creditDocumentId'] = this.creditDocumentId;
    data['isFromFlutterWave'] = this.isFromFlutterWave;
    data['reasonId'] = this.reasonId;
    data['remarks'] = this.remarks;
    data['debitDocIds'] = this.debitDocIds;
    data['billToOrg'] = this.billToOrg;
    return data;
  }
}
