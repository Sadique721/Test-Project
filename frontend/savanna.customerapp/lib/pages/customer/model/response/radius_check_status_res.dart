import 'package:savbill/webservices/base_response.dart';

class RadiusCheckStatusRes extends BaseResponse{
  List<String>? liveusers;
  String? timestamp;
  int? status;

  RadiusCheckStatusRes({this.liveusers, this.timestamp, this.status});

  RadiusCheckStatusRes.fromJson(Map<String, dynamic> json) {
    liveusers = json['liveusers'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['liveusers'] = this.liveusers;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
