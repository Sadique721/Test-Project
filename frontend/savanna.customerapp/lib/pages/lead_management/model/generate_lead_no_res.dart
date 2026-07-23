import 'package:savbill/webservices/base_response.dart';

class GenerateLeadNoRes extends BaseResponse {
  String? leadNo;
  String? timestamp;
  int? status;

  GenerateLeadNoRes({this.leadNo, this.timestamp, this.status});

  GenerateLeadNoRes.fromJson(Map<String, dynamic> json) {
    leadNo = json['leadNo'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['leadNo'] = this.leadNo;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
