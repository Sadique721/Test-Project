class InventoryWorkFlowAuditRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<WorkFlowAuditDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryWorkFlowAuditRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InventoryWorkFlowAuditRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <WorkFlowAuditDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(WorkFlowAuditDataList.fromJson(v));
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

class WorkFlowAuditDataList {
  int? id;
  dynamic eventId;
  String? eventName;
  int? entityId;
  String? entityName;
  int? actionByStaffId;
  String? actionByName;
  String? action;
  String? actionDateTime;
  String? remark;

  WorkFlowAuditDataList(
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

  WorkFlowAuditDataList.fromJson(Map<String, dynamic> json) {
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