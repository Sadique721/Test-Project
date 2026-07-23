import 'package:savbill/webservices/base_response.dart';

class LeadFesibilityRes  extends BaseResponse{
  List<String>? feasibility;
  String? timestamp;
  int? status;

  LeadFesibilityRes({this.feasibility, this.timestamp, this.status});

  LeadFesibilityRes.fromJson(Map<String, dynamic> json) {
    feasibility = json['feasibility'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['feasibility'] = this.feasibility;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
