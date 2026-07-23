import 'package:savbill/webservices/base_response.dart';

class LeadTypeRes extends BaseResponse {
  List<String>? leadTypeList;
  String? timestamp;
  int? status;

  LeadTypeRes({this.leadTypeList, this.timestamp, this.status});

  LeadTypeRes.fromJson(Map<String, dynamic> json) {
    leadTypeList = json['leadTypeList'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['leadTypeList'] = this.leadTypeList;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
