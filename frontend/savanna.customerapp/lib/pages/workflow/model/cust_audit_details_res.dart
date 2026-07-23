import 'package:savbill/webservices/base_response.dart';

class CustAuditDetailRes {
  dynamic responseCode;
  String? responseMessage;
  List<AuditDetailList>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustAuditDetailRes(
      {
        this.responseCode,
        this.responseMessage,
        this.dataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustAuditDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <AuditDetailList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AuditDetailList.fromJson(v));
      });
    }
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class AuditDetailList {
  dynamic auditId;
  dynamic auditDate;
  dynamic userName;
  int? userId;
  dynamic employeeName;
  int? employeeId;
  dynamic module;
  dynamic operation;
  dynamic ipAddress;
  dynamic remark;
  int? entityRefId;
  int? partnerId;
  int? mvnoId;

  AuditDetailList(
      {
        this.auditId,
        this.auditDate,
        this.userName,
        this.userId,
        this.employeeName,
        this.employeeId,
        this.module,
        this.operation,
        this.ipAddress,
        this.remark,
        this.entityRefId,
        this.partnerId,
        this.mvnoId});

  AuditDetailList.fromJson(Map<String, dynamic> json) {
    auditId = json['auditId'];
    auditDate = json['auditDate'];
    userName = json['userName'];
    userId = json['userId'];
    employeeName = json['employeeName'];
    employeeId = json['employeeId'];
    module = json['module'];
    operation = json['operation'];
    ipAddress = json['ipAddress'];
    remark = json['remark'];
    entityRefId = json['entityRefId'];
    partnerId = json['partnerId'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['auditId'] = this.auditId;
    data['auditDate'] = this.auditDate;
    data['userName'] = this.userName;
    data['userId'] = this.userId;
    data['employeeName'] = this.employeeName;
    data['employeeId'] = this.employeeId;
    data['module'] = this.module;
    data['operation'] = this.operation;
    data['ipAddress'] = this.ipAddress;
    data['remark'] = this.remark;
    data['entityRefId'] = this.entityRefId;
    data['partnerId'] = this.partnerId;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
