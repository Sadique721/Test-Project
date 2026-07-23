import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PaymentChangeStatusRes extends BaseResponse {
  String? timestamp;
  int? status;
  PaymentChangeStatusData? payment;

  PaymentChangeStatusRes({this.timestamp, this.status, this.payment});

  PaymentChangeStatusRes.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    payment = json['payment'] != null
        ? new PaymentChangeStatusData.fromJson(json['payment'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.payment != null) {
      data['payment'] = this.payment!.toJson();
    }
    return data;
  }
}

class PaymentChangeStatusData {
  List<TicketAssignStaff>? dataList;

  PaymentChangeStatusData({this.dataList});

  PaymentChangeStatusData.fromJson(Map<String, dynamic> json) {
    //  responseCode = json['responseCode'];
    // responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TicketAssignStaff>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TicketAssignStaff.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    //data['responseCode'] = this.responseCode;
    //data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
