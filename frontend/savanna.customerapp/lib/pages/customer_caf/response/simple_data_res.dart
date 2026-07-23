import 'package:savbill/webservices/base_response.dart';

class SimpleDataRes extends BaseResponse{
  String? responseMessage;
  dynamic data;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  SimpleDataRes(
      {
        this.responseMessage,
        this.data,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  SimpleDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
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
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}
