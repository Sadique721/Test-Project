import 'package:savbill/webservices/base_response.dart';

class PincodeDataRes extends BaseResponse {
  String? responseMessage;
  dynamic? data;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  PincodeDataRes({
    this.responseMessage,
    this.data,
    this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages,
  });

  PincodeDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> map = {};
    map['responseCode'] = responseCode;
    map['responseMessage'] = responseMessage;
    map['data'] = data?.toJson();
    map['totalRecords'] = totalRecords;
    map['pageRecords'] = pageRecords;
    map['currentPageNumber'] = currentPageNumber;
    map['totalPages'] = totalPages;
    return map;
  }
}
