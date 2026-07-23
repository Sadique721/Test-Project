import 'package:savbill/webservices/base_response.dart';

class ApprovalPendingPlanGroupRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ApprovalPendingPlanGroup>? dataList;

  ApprovalPendingPlanGroupRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ApprovalPendingPlanGroupRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ApprovalPendingPlanGroup>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApprovalPendingPlanGroup.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ApprovalPendingPlanGroup {
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
  num? dbr;
  int? buId;
  String? planGroupType;
  String? category;
  int? nextTeamHierarchyMappingId;
  int? nextStaff;
  bool? allowDiscount;

  ApprovalPendingPlanGroup(
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
      this.planGroupType,
      this.category,
      this.nextTeamHierarchyMappingId,
      this.nextStaff,
      this.allowDiscount});

  ApprovalPendingPlanGroup.fromJson(Map<String, dynamic> json) {
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
    buId = json['buId'];
    planGroupType = json['planGroupType'];
    category = json['category'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    allowDiscount = json['allowDiscount'];
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
    data['planGroupType'] = this.planGroupType;
    data['category'] = this.category;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['allowDiscount'] = this.allowDiscount;
    return data;
  }
}
