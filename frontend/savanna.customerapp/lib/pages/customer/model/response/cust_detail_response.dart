import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/webservices/base_response.dart';

class CustDetailResponse extends BaseResponse {
  CustomerDetail? customers;

  CustDetailResponse({responseCode, responseMessage, this.customers});

  CustDetailResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    status = json['status'];
    message = json['message'];
    timestamp = json['timestamp'];
    customers = json['customers'] != null
        ? new CustomerDetail.fromJson(json['customers'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['status'] = this.status;
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    if (this.customers != null) {
      data['customers'] = this.customers!.toJson();
    }
    return data;
  }
}
