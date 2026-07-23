import 'package:savbill/webservices/base_response.dart';

class GetPaymentStatusRes extends BaseResponse{
  String? responseMessage;
  GetPaymentStatusData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  dynamic dataSet;

  GetPaymentStatusRes(
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

  GetPaymentStatusRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new GetPaymentStatusData.fromJson(json['data']) : null;
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

class GetPaymentStatusData {
  String? istransactionsuccess;

  GetPaymentStatusData({this.istransactionsuccess});

  GetPaymentStatusData.fromJson(Map<String, dynamic> json) {
    istransactionsuccess = json['istransactionsuccess'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['istransactionsuccess'] = this.istransactionsuccess;
    return data;
  }
}
