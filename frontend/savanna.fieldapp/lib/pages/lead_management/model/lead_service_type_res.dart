import 'package:savbill/webservices/base_response.dart';

class LeadServiceTypeRes  extends BaseResponse{
  List<String>? servicerTypeList;
  String? timestamp;
  int? status;

  LeadServiceTypeRes({this.servicerTypeList, this.timestamp, this.status});

  LeadServiceTypeRes.fromJson(Map<String, dynamic> json) {
    servicerTypeList = json['servicerTypeList'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['servicerTypeList'] = this.servicerTypeList;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
