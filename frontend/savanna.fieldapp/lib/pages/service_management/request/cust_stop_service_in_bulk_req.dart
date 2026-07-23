import 'package:savbill/pages/service_management/request/cust_service_hold_request.dart';
class CustStopServiceInBulkReq {
  int? custId;
  bool? serviceStopBulkFlag;
  List<DeactivatePlanReqModels>? deactivatePlanReqModels;

  CustStopServiceInBulkReq(
      {this.custId, this.serviceStopBulkFlag, this.deactivatePlanReqModels});

  CustStopServiceInBulkReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    serviceStopBulkFlag = json['serviceStopBulkFlag'];
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
    data['serviceStopBulkFlag'] = this.serviceStopBulkFlag;
    if (this.deactivatePlanReqModels != null) {
      data['deactivatePlanReqModels'] =
          this.deactivatePlanReqModels!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

