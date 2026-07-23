import 'package:savbill/webservices/base_response.dart';

class ChangeCustomerStatusRes extends BaseResponse {
  String? customer;

  ChangeCustomerStatusRes({this.customer, timestamp, status});

  ChangeCustomerStatusRes.fromJson(Map<String, dynamic> json) {
    customer = json['customer'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customer'] = this.customer;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
