import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/webservices/base_response.dart';

class PlanGroupRes extends BaseResponse {
  List<PlanGroupDetail>? planGroupList;

  PlanGroupRes({this.planGroupList, timestamp, error, status});

  PlanGroupRes.fromJson(Map<String, dynamic> json) {
    if (json['planGroupList'] != null) {
      planGroupList = <PlanGroupDetail>[];
      json['planGroupList'].forEach((v) {
        planGroupList!.add(new PlanGroupDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    error = json['error'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.planGroupList != null) {
      data['planGroupList'] =
          this.planGroupList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['error'] = this.error;
    data['status'] = this.status;
    return data;
  }
}

class PlanGroupDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? planGroupId;
  String? planGroupName;
  String? status;
  int? mvnoId;
  String? plantype;
  String? planMode;
  bool? isDelete;
  double? dbr;
  int? buId;
  String? category;
  List<PlanMappingGroupDetail>? planMappingList;
  String? planGroupType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  dynamic accessibility;
  bool? allowDiscount;
  double? offerprice;
  List<int>? servicearea;
  dynamic templateId;
  bool? invoiceToOrg;
  bool? requiredApproval;
  String? mvnoName;

  PlanGroupDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.planGroupId,
      this.planGroupName,
      this.status,
      this.mvnoId,
      this.plantype,
      this.planMode,
      this.isDelete,
      this.dbr,
      this.buId,
      this.planMappingList,
        this.category,
        this.planGroupType,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.accessibility,
        this.allowDiscount,
        this.offerprice,
        this.servicearea,
        this.templateId,
        this.invoiceToOrg,
        this.requiredApproval,
        this.mvnoName});

  PlanGroupDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroupId = json['planGroupId'];
    planGroupName = json['planGroupName'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    plantype = json['plantype'];
    planMode = json['planMode'];
    isDelete = json['isDelete'];
    dbr = json['dbr'];
    category = json['category'];
    buId = json['buId'];
    if (json['planMappingList'] != null) {
      planMappingList = <PlanMappingGroupDetail>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingGroupDetail.fromJson(v));
      });
    }

    planGroupType = json['planGroupType'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    accessibility = json['accessibility'];
    allowDiscount = json['allowDiscount'];
    offerprice = json['offerprice'];
    servicearea = json['servicearea'].cast<int>();
    templateId = json['templateId'];
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
    mvnoName = json['mvnoName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroupId'] = this.planGroupId;
    data['planGroupName'] = this.planGroupName;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['plantype'] = this.plantype;
    data['planMode'] = this.planMode;
    data['isDelete'] = this.isDelete;
    data['dbr'] = this.dbr;
    data['buId'] = this.buId;
    data['category'] = this.category;
    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }

    data['planGroupType'] = this.planGroupType;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['accessibility'] = this.accessibility;
    data['allowDiscount'] = this.allowDiscount;
    data['offerprice'] = this.offerprice;
    data['servicearea'] = this.servicearea;
    data['templateId'] = this.templateId;
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
    data['mvnoName'] = this.mvnoName;
    return data;
  }
}

class PlanMappingGroupDetail {
  int? createdById;
  int? lastModifiedById;
  int? planGroupMappingId;
  PostpaidPlanDetail? plan;
  String? service;
  bool? isDelete;
  int? mvnoId;
  num? validity;
  bool? inactive = false;

  PlanMappingGroupDetail(
      {this.createdById,
      this.lastModifiedById,
      this.planGroupMappingId,
      this.plan,
      this.service,
      this.isDelete,
      this.mvnoId,
      this.validity,
      this.inactive});

  PlanMappingGroupDetail.fromJson(Map<String, dynamic> json) {
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroupMappingId = json['planGroupMappingId'];
    plan = json['plan'] != null
        ? new PostpaidPlanDetail.fromJson(json['plan'])
        : null;
    service = json['service'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    validity = json['validity'];
    inactive = json['inactive'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroupMappingId'] = this.planGroupMappingId;
    if (this.plan != null) {
      data['plan'] = this.plan!.toJson();
    }
    data['service'] = this.service;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['validity'] = this.validity;
    data['inactive'] = this.inactive;
    return data;
  }
}
