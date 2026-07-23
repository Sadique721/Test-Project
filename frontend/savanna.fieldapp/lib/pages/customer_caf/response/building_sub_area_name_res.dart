import 'package:savbill/webservices/base_response.dart';

class BuildingSubAreaNameRes extends BaseResponse{
  // int? responseCode;
  // String? responseMessage;
  BuildingSubAreaNameData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  BuildingSubAreaNameRes(
      {
        // this.responseCode,
        // this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  BuildingSubAreaNameRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new BuildingSubAreaNameData.fromJson(json['data']) : null;
    dataList = json['dataList'];
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
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class BuildingSubAreaNameData {
  String? name;
  String? buildingName;

  BuildingSubAreaNameData({this.name, this.buildingName});

  BuildingSubAreaNameData.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    buildingName = json['building_name'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['building_name'] = this.buildingName;
    return data;
  }
}
