import 'package:savbill/webservices/base_response.dart';

class CustomerExistRes extends BaseResponse {
  bool? isAlreadyExists =false;

  CustomerExistRes({this.isAlreadyExists, timestamp, status});

  CustomerExistRes.fromJson(Map<String, dynamic> json) {
    isAlreadyExists = json['isAlreadyExists'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['isAlreadyExists'] = this.isAlreadyExists;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
