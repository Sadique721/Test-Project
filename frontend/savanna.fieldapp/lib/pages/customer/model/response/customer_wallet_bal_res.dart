import 'package:savbill/webservices/base_response.dart';

class CustomerWalletRes extends BaseResponse {
  double? customerWalletDetails;

  CustomerWalletRes({this.customerWalletDetails, timestamp, status});

  CustomerWalletRes.fromJson(Map<String, dynamic> json) {
    customerWalletDetails = json['customerWalletDetails'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerWalletDetails'] = this.customerWalletDetails;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
