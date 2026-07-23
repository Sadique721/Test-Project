import 'package:savbill/webservices/base_response.dart';

class CafRemarkFollowUpRes  extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<RemarkFollowUpDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CafRemarkFollowUpRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CafRemarkFollowUpRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <RemarkFollowUpDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new RemarkFollowUpDataList.fromJson(v));
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

class RemarkFollowUpDataList {
  int? id;
  String? remark;
  int? cafFollowUpId;
  String? cafFollowUpName;
  String? createdOn;
  Null? mvnoId;
  int? identityKey;

  RemarkFollowUpDataList(
      {this.id,
        this.remark,
        this.cafFollowUpId,
        this.cafFollowUpName,
        this.createdOn,
        this.mvnoId,
        this.identityKey});

  RemarkFollowUpDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    remark = json['remark'];
    cafFollowUpId = json['cafFollowUpId'];
    cafFollowUpName = json['cafFollowUpName'];
    createdOn = json['createdOn'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['remark'] = this.remark;
    data['cafFollowUpId'] = this.cafFollowUpId;
    data['cafFollowUpName'] = this.cafFollowUpName;
    data['createdOn'] = this.createdOn;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
