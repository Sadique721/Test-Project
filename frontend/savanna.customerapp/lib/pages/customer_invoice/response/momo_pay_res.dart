
import '../../../webservices/base_response.dart';

class MomoPayRes extends BaseResponse {
  String? responseMessage;
  MomoPay? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  dynamic dataSet;

  MomoPayRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages,
        this.dataSet});

  MomoPayRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new MomoPay.fromJson(json['data']) : null;
    dataList = json['dataList'];
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    dataSet = json['dataSet'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    data['dataSet'] = this.dataSet;
    return data;
  }
}

class MomoPay {
  int? code;
  PayData? data;
  String? message;

  MomoPay({this.code, this.data, this.message});

  MomoPay.fromJson(Map<String, dynamic> json) {
    code = json['code'];
    data = json['data'] != null ? new PayData.fromJson(json['data']) : null;
    message = json['message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['code'] = this.code;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['message'] = this.message;
    return data;
  }
}

class PayData {
  String? customerUUID;
  String? customerUserName;
  String? orderId;
  String? merchantName;

  PayData(
      {this.customerUUID,
        this.customerUserName,
        this.orderId,
        this.merchantName});

  PayData.fromJson(Map<String, dynamic> json) {
    customerUUID = json['customerUUID'];
    customerUserName = json['customerUserName'];
    orderId = json['orderId'];
    merchantName = json['merchantName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerUUID'] = this.customerUUID;
    data['customerUserName'] = this.customerUserName;
    data['orderId'] = this.orderId;
    data['merchantName'] = this.merchantName;
    return data;
  }
}
