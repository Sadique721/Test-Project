import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/webservices/base_response.dart';

class CustomerPaymentListRes extends BaseResponse {
  List<PaymentDetail>? dataList;

  CustomerPaymentListRes({responseCode, responseMessage, this.dataList});

  CustomerPaymentListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <PaymentDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PaymentDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
