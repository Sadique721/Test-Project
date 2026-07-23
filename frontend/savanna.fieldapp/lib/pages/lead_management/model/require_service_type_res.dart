import 'package:savbill/webservices/base_response.dart';

class RequireServiceTypeRes  extends BaseResponse{
  List<String>? requireServiceTypeList;
  String? timestamp;
  int? status;

  RequireServiceTypeRes(
      {this.requireServiceTypeList, this.timestamp, this.status});

  RequireServiceTypeRes.fromJson(Map<String, dynamic> json) {
    requireServiceTypeList = json['requireServiceTypeList'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['requireServiceTypeList'] = this.requireServiceTypeList;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
