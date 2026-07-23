import 'package:savbill/webservices/base_response.dart';

class SimpleDataRes extends BaseResponse {
  String? responseMessage;
  ServiceAreaData? data;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  SimpleDataRes({
    this.responseMessage,
    this.data,
    this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages,
  });

  SimpleDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];

    data = json['data'] != null
        ? ServiceAreaData.fromJson(json['data'])
        : null;

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

/* ---------------------------------------------------------
   ServiceAreaData MODEL INSIDE SimpleDataRes
---------------------------------------------------------- */

class ServiceAreaData {
  String? serviceAreaName;
  int? serviceAreaId;

  ServiceAreaData({
    this.serviceAreaName,
    this.serviceAreaId,
  });

  ServiceAreaData.fromJson(Map<String, dynamic> json) {
    serviceAreaName = json['serviceAreaName'];
    serviceAreaId = json['serviceAreaId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> map = {};
    map['serviceAreaName'] = serviceAreaName;
    map['serviceAreaId'] = serviceAreaId;
    return map;
  }
}
