import 'package:savbill/webservices/base_response.dart';

class LeadCustomerGenderRes extends BaseResponse {
  List<String>? leadCustomerGender;
  String? timestamp;
  int? status;

  LeadCustomerGenderRes({this.leadCustomerGender, this.timestamp, this.status});

  LeadCustomerGenderRes.fromJson(Map<String, dynamic> json) {
    leadCustomerGender = json['leadCustomerGender'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['leadCustomerGender'] = this.leadCustomerGender;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
