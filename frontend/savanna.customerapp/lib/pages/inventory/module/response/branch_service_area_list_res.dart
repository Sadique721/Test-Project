import 'package:savbill/webservices/base_response.dart';

class BranchServiceAreaListRes extends BaseResponse {
  List<BranchServiceAreaDetail>? dataList;

  BranchServiceAreaListRes({responseCode, responseMessage, this.dataList});

  BranchServiceAreaListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BranchServiceAreaDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BranchServiceAreaDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class BranchServiceAreaDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  String? branchCode;
  bool? isDeleted;
  int? mvnoId;
  bool? revenueSharing;
  String? dunningDays;
  bool? deleteFlag;
  int? primaryKey;

  BranchServiceAreaDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.branchCode,
      this.isDeleted,
      this.mvnoId,
      this.revenueSharing,
      this.dunningDays,
      this.deleteFlag,
      this.primaryKey});

  BranchServiceAreaDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    branchCode = json['branch_code'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    revenueSharing = json['revenue_sharing'];
    dunningDays = json['dunningDays'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['branch_code'] = this.branchCode;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['revenue_sharing'] = this.revenueSharing;
    data['dunningDays'] = this.dunningDays;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
