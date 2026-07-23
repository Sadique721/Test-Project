import 'package:savbill/webservices/base_response.dart';

class GetTicketFollowUpRemarkRes  extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<TicketFollowUpRemarkDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetTicketFollowUpRemarkRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetTicketFollowUpRemarkRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TicketFollowUpRemarkDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(TicketFollowUpRemarkDataList.fromJson(v));
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

class TicketFollowUpRemarkDataList {
  int? id;
  String? remark;
  int? ticketFollowUpId;
  String? ticketFollowUpName;
  String? createdOn;
  Null? mvnoId;
  int? identityKey;
  Null? buId;

  TicketFollowUpRemarkDataList(
      {this.id,
        this.remark,
        this.ticketFollowUpId,
        this.ticketFollowUpName,
        this.createdOn,
        this.mvnoId,
        this.identityKey,
        this.buId});

  TicketFollowUpRemarkDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    remark = json['remark'];
    ticketFollowUpId = json['ticketFollowUpId'];
    ticketFollowUpName = json['ticketFollowUpName'];
    createdOn = json['createdOn'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['remark'] = this.remark;
    data['ticketFollowUpId'] = this.ticketFollowUpId;
    data['ticketFollowUpName'] = this.ticketFollowUpName;
    data['createdOn'] = this.createdOn;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['buId'] = this.buId;
    return data;
  }
}
