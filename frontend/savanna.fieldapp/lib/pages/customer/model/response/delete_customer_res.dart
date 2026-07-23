import 'package:savbill/webservices/base_response.dart';

class DeleteCustomerRes extends BaseResponse {
  String? eRROR;

  DeleteCustomerRes({message, timestamp, status, this.eRROR});

  DeleteCustomerRes.fromJson(Map<String, dynamic> json) {
    message = json['message'];
    timestamp = json['timestamp'];
    status = json['status'];
    eRROR = json['ERROR'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['ERROR'] = this.eRROR;
    return data;
  }
}
