import 'package:savbill/webservices/base_response.dart';

class BuildingReferenceRes extends BaseResponse {
  String? responseMessage;
  List<BuildingReferenceDataList>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  BuildingReferenceRes(
      {
        this.responseMessage,
        this.dataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  BuildingReferenceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BuildingReferenceDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BuildingReferenceDataList.fromJson(v));
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

class BuildingReferenceDataList {
  int? id;
  String? name;
  String? mappingFrom;
  int? mvnoId;

  BuildingReferenceDataList({this.id, this.name, this.mappingFrom, this.mvnoId});

  BuildingReferenceDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    mappingFrom = json['mappingFrom'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['mappingFrom'] = this.mappingFrom;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
