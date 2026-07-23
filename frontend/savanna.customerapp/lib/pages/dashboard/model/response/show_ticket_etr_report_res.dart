import 'package:savbill/webservices/base_response.dart';

class ShowTicketETRReportRes extends BaseResponse {
  dynamic data;
  List<ShowTicketETRReportDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ShowTicketETRReportRes(
      {this.data,
      this.dataList,
      this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  ShowTicketETRReportRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ShowTicketETRReportDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ShowTicketETRReportDataList.fromJson(v));
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

class ShowTicketETRReportDataList {
  int? id;
  int? custId;
  String? custUserName;
  int? staffId;
  String? staffPersonName;
  String? notificationSentDate;
  String? notificationSentTime;
  String? notificationMessage;
  String? notificationMode;
  String? messageMode;
  String? notificationStatus;
  int? caseId;
  String? caseNumber;

  ShowTicketETRReportDataList(
      {this.id,
      this.custId,
      this.custUserName,
      this.staffId,
      this.staffPersonName,
      this.notificationSentDate,
      this.notificationSentTime,
      this.notificationMessage,
      this.notificationMode,
      this.messageMode,
      this.notificationStatus,
      this.caseId,
      this.caseNumber});

  ShowTicketETRReportDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    custUserName = json['custUserName'];
    staffId = json['staffId'];
    staffPersonName = json['staffPersonName'];
    notificationSentDate = json['notificationSentDate'];
    notificationSentTime = json['notificationSentTime'];
    notificationMessage = json['notificationMessage'];
    notificationMode = json['notificationMode'];
    messageMode = json['messageMode'];
    notificationStatus = json['notificationStatus'];
    caseId = json['caseId'];
    caseNumber = json['caseNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['custUserName'] = this.custUserName;
    data['staffId'] = this.staffId;
    data['staffPersonName'] = this.staffPersonName;
    data['notificationSentDate'] = this.notificationSentDate;
    data['notificationSentTime'] = this.notificationSentTime;
    data['notificationMessage'] = this.notificationMessage;
    data['notificationMode'] = this.notificationMode;
    data['messageMode'] = this.messageMode;
    data['notificationStatus'] = this.notificationStatus;
    data['caseId'] = this.caseId;
    data['caseNumber'] = this.caseNumber;
    return data;
  }
}
