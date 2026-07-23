import 'package:savbill/webservices/base_response.dart';

class LeadSourceBranchCRMRes extends BaseResponse {
  List<BranchList>? branchList;
  String? timestamp;
  int? status;

  LeadSourceBranchCRMRes({this.branchList, this.timestamp, this.status});

  LeadSourceBranchCRMRes.fromJson(Map<String, dynamic> json) {
    if (json['branchList'] != null) {
      branchList = <BranchList>[];
      json['branchList'].forEach((v) {
        branchList!.add(new BranchList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.branchList != null) {
      data['branchList'] = this.branchList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class BranchList {
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;

  BranchList({this.id, this.name, this.status, this.isDeleted, this.mvnoId});

  BranchList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
