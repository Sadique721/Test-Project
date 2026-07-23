import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PlanGroupDetailRes extends BaseResponse {
  List<PlanGroupMappingList>? planGroupMappingList;

  PlanGroupDetailRes({timestamp, status, this.planGroupMappingList});

  PlanGroupDetailRes.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    if (json['planGroupMappingList'] != null) {
      planGroupMappingList = <PlanGroupMappingList>[];
      json['planGroupMappingList'].forEach((v) {
        planGroupMappingList!.add(new PlanGroupMappingList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.planGroupMappingList != null) {
      data['planGroupMappingList'] =
          this.planGroupMappingList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PlanGroupMappingList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? planGroupMappingId;
  PostpaidPlanDetail? plan;

  PlanGroupMappingList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.planGroupMappingId,
      this.plan});

  PlanGroupMappingList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroupMappingId = json['planGroupMappingId'];
    plan = json['plan'] != null
        ? new PostpaidPlanDetail.fromJson(json['plan'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroupMappingId'] = this.planGroupMappingId;
    if (this.plan != null) {
      data['plan'] = this.plan!.toJson();
    }
    return data;
  }
}
