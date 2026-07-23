import 'package:savbill/webservices/base_response.dart';

class CustomerDocApprovalRes extends BaseResponse {
  String? responseMessage;
  dynamic data;
  List<CustDocApprovalDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustomerDocApprovalRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustomerDocApprovalRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <CustDocApprovalDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustDocApprovalDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class CustDocApprovalDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? docId;
  String? docType;
  String? docSubType;
  String? mode;
  String? remark;
  String? docStatus;
  String? filename;
  String? uniquename;
  bool? isDelete;
  dynamic startDate;
  dynamic endDate;
  dynamic nextTeamHierarchyMappingId;
  int? nextStaff;

  CustDocApprovalDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.docId,
        this.docType,
        this.docSubType,
        this.mode,
        this.remark,
        this.docStatus,
        this.filename,
        this.uniquename,
        this.isDelete,
        this.startDate,
        this.endDate,
        this.nextTeamHierarchyMappingId,
        this.nextStaff});

  CustDocApprovalDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    docId = json['docId'];
    docType = json['docType'];
    docSubType = json['docSubType'];
    mode = json['mode'];
    remark = json['remark'];
    docStatus = json['docStatus'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    isDelete = json['isDelete'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['docId'] = this.docId;
    data['docType'] = this.docType;
    data['docSubType'] = this.docSubType;
    data['mode'] = this.mode;
    data['remark'] = this.remark;
    data['docStatus'] = this.docStatus;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['isDelete'] = this.isDelete;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    return data;
  }
}
