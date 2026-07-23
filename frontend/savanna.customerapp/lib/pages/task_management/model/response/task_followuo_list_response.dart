import 'package:savbill/webservices/base_response.dart';

class TaskFollowupListResponse extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<TaskFollowUpDetail>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TaskFollowupListResponse(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TaskFollowupListResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TaskFollowUpDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TaskFollowUpDetail.fromJson(v));
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

class TaskFollowUpDetail {
  int? id;
  String? remark;
  bool? isDelete;
  int? caseId;
  int? staffId;
  dynamic custId;
  String? remarkDate;
  String? remarkType;
  bool? isFromCustomer;
  String? caseTitle;
  String? staffUserName;
  dynamic customersName;
  bool? deleteFlag;
  int? primaryKey;

  TaskFollowUpDetail(
      {this.id,
        this.remark,
        this.isDelete,
        this.caseId,
        this.staffId,
        this.custId,
        this.remarkDate,
        this.remarkType,
        this.isFromCustomer,
        this.caseTitle,
        this.staffUserName,
        this.customersName,
        this.deleteFlag,
        this.primaryKey});

  TaskFollowUpDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    remark = json['remark'];
    isDelete = json['isDelete'];
    caseId = json['caseId'];
    staffId = json['staffId'];
    custId = json['custId'];
    remarkDate = json['remarkDate'];
    remarkType = json['remarkType'];
    isFromCustomer = json['isFromCustomer'];
    caseTitle = json['caseTitle'];
    staffUserName = json['staffUserName'];
    customersName = json['customersName'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['remark'] = this.remark;
    data['isDelete'] = this.isDelete;
    data['caseId'] = this.caseId;
    data['staffId'] = this.staffId;
    data['custId'] = this.custId;
    data['remarkDate'] = this.remarkDate;
    data['remarkType'] = this.remarkType;
    data['isFromCustomer'] = this.isFromCustomer;
    data['caseTitle'] = this.caseTitle;
    data['staffUserName'] = this.staffUserName;
    data['customersName'] = this.customersName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
