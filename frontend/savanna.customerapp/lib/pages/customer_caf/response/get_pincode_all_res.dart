import 'package:savbill/webservices/base_response.dart';

class GetPinCodeAllRes extends BaseResponse{
  String? responseMessage;
  List<PinCodeAllDataList>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetPinCodeAllRes(
      {
        this.responseMessage,
        this.dataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetPinCodeAllRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <PinCodeAllDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PinCodeAllDataList.fromJson(v));
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

class PinCodeAllDataList {
  int? pincodeid;
  String? pincode;
  String? status;

  PinCodeAllDataList({this.pincodeid, this.pincode, this.status});

  PinCodeAllDataList.fromJson(Map<String, dynamic> json) {
    pincodeid = json['pincodeid'];
    pincode = json['pincode'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['pincodeid'] = this.pincodeid;
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    return data;
  }
}
