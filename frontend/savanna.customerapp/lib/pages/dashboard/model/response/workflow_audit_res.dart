import 'package:savbill/webservices/base_response.dart';

class WorkflowAuditRes extends BaseResponse{

  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<WorkflowAuditDetail>? dataList;

  WorkflowAuditRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  WorkflowAuditRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <WorkflowAuditDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new WorkflowAuditDetail.fromJson(v));
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

class WorkflowAuditDetail {
  int? id;
  int? eventId;
  String? eventName;
  int? entityId;
  String? entityName;
  int? actionByStaffId;
  String? actionByName;
  String? action;
  String? actionDateTime;
  String? remark;

  WorkflowAuditDetail(
      {this.id,
      this.eventId,
      this.eventName,
      this.entityId,
      this.entityName,
      this.actionByStaffId,
      this.actionByName,
      this.action,
      this.actionDateTime,
      this.remark});

  WorkflowAuditDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    eventId = json['eventId'];
    eventName = json['eventName'];
    entityId = json['entityId'];
    entityName = json['entityName'];
    actionByStaffId = json['actionByStaffId'];
    actionByName = json['actionByName'];
    action = json['action'];
    actionDateTime = json['actionDateTime'];
    remark = json['remark'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['eventId'] = this.eventId;
    data['eventName'] = this.eventName;
    data['entityId'] = this.entityId;
    data['entityName'] = this.entityName;
    data['actionByStaffId'] = this.actionByStaffId;
    data['actionByName'] = this.actionByName;
    data['action'] = this.action;
    data['actionDateTime'] = this.actionDateTime;
    data['remark'] = this.remark;
    return data;
  }
}
