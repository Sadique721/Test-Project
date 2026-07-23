import 'package:savbill/webservices/base_response.dart';

class TicketFollowupFindAllResponse extends BaseResponse {
  String? responseMessage;
  Null? data;
  List<TicketFollowUpFindAllDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TicketFollowupFindAllResponse(
      {        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TicketFollowupFindAllResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TicketFollowUpFindAllDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(TicketFollowUpFindAllDataList.fromJson(v));
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

class TicketFollowUpFindAllDataList {
  int? id;
  String? followUpName;
  String? followUpDatetime;
  String? remarks;
  String? status;
  bool? isMissed;
  bool? isSend;
  int? caseId;
  String? caseNumber;
  dynamic createdBy;
  int? staffUserId;
  String? staffUserName;
  int? mvnoId;
  int? identityKey;
  dynamic buId;

  TicketFollowUpFindAllDataList(
      {this.id,
        this.followUpName,
        this.followUpDatetime,
        this.remarks,
        this.status,
        this.isMissed,
        this.isSend,
        this.caseId,
        this.caseNumber,
        this.createdBy,
        this.staffUserId,
        this.staffUserName,
        this.mvnoId,
        this.identityKey,
        this.buId});

  TicketFollowUpFindAllDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    followUpName = json['followUpName'];
    followUpDatetime = json['followUpDatetime'];
    remarks = json['remarks'];
    status = json['status'];
    isMissed = json['isMissed'];
    isSend = json['isSend'];
    caseId = json['caseId'];
    caseNumber = json['caseNumber'];
    createdBy = json['createdBy'];
    staffUserId = json['staffUserId'];
    staffUserName = json['staffUserName'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['followUpName'] = this.followUpName;
    data['followUpDatetime'] = this.followUpDatetime;
    data['remarks'] = this.remarks;
    data['status'] = this.status;
    data['isMissed'] = this.isMissed;
    data['isSend'] = this.isSend;
    data['caseId'] = this.caseId;
    data['caseNumber'] = this.caseNumber;
    data['createdBy'] = this.createdBy;
    data['staffUserId'] = this.staffUserId;
    data['staffUserName'] = this.staffUserName;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['buId'] = this.buId;
    return data;
  }
}
