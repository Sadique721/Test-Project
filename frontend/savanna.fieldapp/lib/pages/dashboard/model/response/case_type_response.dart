import 'package:savbill/webservices/base_response.dart';

class CaseTypeResponse extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<CaseTypeDetail>? dataList;

  CaseTypeResponse(
      {responseCode,
      responseMessage,
      this.dataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  CaseTypeResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CaseTypeDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CaseTypeDetail.fromJson(v));
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

class CaseTypeDetail {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;

  CaseTypeDetail({this.id, this.text, this.value, this.type, this.status});

  CaseTypeDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    return data;
  }
}
