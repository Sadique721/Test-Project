import 'package:savbill/webservices/base_response.dart';

class ChkPrimeCustomerRes extends BaseResponse {
  bool? isCustomerPrime;

  ChkPrimeCustomerRes({this.isCustomerPrime, timestamp, status});

  ChkPrimeCustomerRes.fromJson(Map<String, dynamic> json) {
    isCustomerPrime = json['isCustomerPrime'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['isCustomerPrime'] = this.isCustomerPrime;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
