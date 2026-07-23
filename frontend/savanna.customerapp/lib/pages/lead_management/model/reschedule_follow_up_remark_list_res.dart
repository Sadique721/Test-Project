import 'package:savbill/webservices/base_response.dart';

class RescheduleFollowupRemarkListRes extends BaseResponse{
  List<String>? rescheduleFollowupRemarkList;
  String? timestamp;
  int? status;

  RescheduleFollowupRemarkListRes(
      {this.rescheduleFollowupRemarkList, this.timestamp, this.status});

  RescheduleFollowupRemarkListRes.fromJson(Map<String, dynamic> json) {
    rescheduleFollowupRemarkList =
        json['rescheduleFollowupRemarkList'].cast<String>();
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['rescheduleFollowupRemarkList'] = this.rescheduleFollowupRemarkList;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
