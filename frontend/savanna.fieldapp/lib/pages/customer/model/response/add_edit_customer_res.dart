import 'package:savbill/webservices/base_response.dart';

class AddEditCustomerRes extends BaseResponse {
  String? eRROR;

  AddEditCustomerRes({this.eRROR, timestamp, status});

  AddEditCustomerRes.fromJson(Map<String, dynamic> json) {
    eRROR = json['ERROR'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['ERROR'] = this.eRROR;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
