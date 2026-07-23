
import '../../../webservices/base_response.dart';

class ArtelPaymentRes extends BaseResponse{
  String? responseMessage;
  ArtelPaymentData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  dynamic dataSet;

  ArtelPaymentRes(
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

  ArtelPaymentRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new ArtelPaymentData.fromJson(json['data']) : null;
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

class ArtelPaymentData {
  ArtelPayData? data;
  Status? status;

  ArtelPaymentData({this.data, this.status});

  ArtelPaymentData.fromJson(Map<String, dynamic> json) {
    data = json['data'] != null ? new ArtelPayData.fromJson(json['data']) : null;
    status =
    json['status'] != null ? new Status.fromJson(json['status']) : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    if (this.status != null) {
      data['status'] = this.status!.toJson();
    }
    return data;
  }
}

class ArtelPayData {
  Transaction? transaction;

  ArtelPayData({this.transaction});

  ArtelPayData.fromJson(Map<String, dynamic> json) {
    transaction = json['transaction'] != null
        ? new Transaction.fromJson(json['transaction'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.transaction != null) {
      data['transaction'] = this.transaction!.toJson();
    }
    return data;
  }
}

class Transaction {
  String? id;
  String? status;

  Transaction({this.id, this.status});

  Transaction.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['status'] = this.status;
    return data;
  }
}

class Status {
  String? responseCode;
  String? code;
  bool? success;
  String? resultCode;
  String? message;

  Status(
      {this.responseCode,
        this.code,
        this.success,
        this.resultCode,
        this.message});

  Status.fromJson(Map<String, dynamic> json) {
    responseCode = json['response_code'];
    code = json['code'];
    success = json['success'];
    resultCode = json['result_code'];
    message = json['message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['response_code'] = this.responseCode;
    data['code'] = this.code;
    data['success'] = this.success;
    data['result_code'] = this.resultCode;
    data['message'] = this.message;
    return data;
  }
}
