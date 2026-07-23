import 'package:savbill/webservices/base_response.dart';

class LeadOriginTypeRes  extends BaseResponse{
  List<String>? leadOriginTypeList;
  String? timestamp;
  int? status;

  LeadOriginTypeRes({this.leadOriginTypeList, this.timestamp, this.status});

  LeadOriginTypeRes.fromJson(Map<String, dynamic> json) {
    leadOriginTypeList = json['leadOriginTypeList'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['leadOriginTypeList'] = this.leadOriginTypeList;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
