import 'package:savbill/webservices/base_response.dart';

class LeadFollowUpListRes extends BaseResponse{
  List<FollowUpList>? followUpList;
  String? timestamp;
  int? status;

  LeadFollowUpListRes({this.followUpList, this.timestamp, this.status});

  LeadFollowUpListRes.fromJson(Map<String, dynamic> json) {
    if (json['followUpList'] != null) {
      followUpList = <FollowUpList>[];
      json['followUpList'].forEach((v) {
        followUpList!.add(new FollowUpList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.followUpList != null) {
      data['followUpList'] = this.followUpList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class FollowUpList {
  int? id;
  String? followUpName;
  String? followUpDatetime;
  String? remarks;
  String? status;
  bool? isMissed;
  bool? isSend;
  int? leadMasterId;
  String? leadMasterName;
  int? createdBy;
  String? staffName;

  FollowUpList(
      {this.id,
        this.followUpName,
        this.followUpDatetime,
        this.remarks,
        this.status,
        this.isMissed,
        this.isSend,
        this.leadMasterId,
        this.leadMasterName,
        this.createdBy,
        this.staffName});

  FollowUpList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    followUpName = json['followUpName'];
    followUpDatetime = json['followUpDatetime'];
    remarks = json['remarks'];
    status = json['status'];
    isMissed = json['isMissed'];
    isSend = json['isSend'];
    leadMasterId = json['leadMasterId'];
    leadMasterName = json['leadMasterName'];
    createdBy = json['createdBy'];
    staffName = json['staffName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['followUpName'] = this.followUpName;
    data['followUpDatetime'] = this.followUpDatetime;
    data['remarks'] = this.remarks;
    data['status'] = this.status;
    data['isMissed'] = this.isMissed;
    data['isSend'] = this.isSend;
    data['leadMasterId'] = this.leadMasterId;
    data['leadMasterName'] = this.leadMasterName;
    data['createdBy'] = this.createdBy;
    data['staffName'] = this.staffName;
    return data;
  }
}
