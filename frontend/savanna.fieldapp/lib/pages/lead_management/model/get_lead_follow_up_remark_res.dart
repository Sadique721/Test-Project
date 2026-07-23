import 'package:savbill/webservices/base_response.dart';

class GetLeadFollowUpRemarkRes extends BaseResponse {
  List<FollowUpRemarkList>? followUpRemarkList;
  String? timestamp;
  int? status;

  GetLeadFollowUpRemarkRes(
      {this.followUpRemarkList, this.timestamp, this.status});

  GetLeadFollowUpRemarkRes.fromJson(Map<String, dynamic> json) {
    if (json['followUpRemarkList'] != null) {
      followUpRemarkList = <FollowUpRemarkList>[];
      json['followUpRemarkList'].forEach((v) {
        followUpRemarkList!.add(new FollowUpRemarkList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.followUpRemarkList != null) {
      data['followUpRemarkList'] =
          this.followUpRemarkList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class FollowUpRemarkList {
  int? id;
  String? remark;
  String? createdOn;

  FollowUpRemarkList({this.id, this.remark, this.createdOn});

  FollowUpRemarkList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    remark = json['remark'];
    createdOn = json['createdOn'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['remark'] = this.remark;
    data['createdOn'] = this.createdOn;
    return data;
  }
}
