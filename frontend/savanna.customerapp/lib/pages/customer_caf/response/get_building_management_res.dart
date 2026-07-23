import 'package:savbill/webservices/base_response.dart';

class GetBuildingManagementRes extends BaseResponse {
  // int? responseCode;
  // dynamic responseMessage;
  dynamic data;
  List<BuildingManagementDataList>? buildingNumberList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetBuildingManagementRes(
      {
        // this.responseCode,
        // this.responseMessage,
        this.data,
        this.buildingNumberList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetBuildingManagementRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      buildingNumberList = <BuildingManagementDataList>[];
      json['dataList'].forEach((v) {
        buildingNumberList!.add(new BuildingManagementDataList.fromJson(v));
      });
    }
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
    data['data'] = this.data;
    if (this.buildingNumberList != null) {
      data['dataList'] = this.buildingNumberList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}


class BuildingManagementDataList {
  int? buildingMgmtId;
  String? buildingName;

  BuildingManagementDataList({this.buildingMgmtId, this.buildingName});

  BuildingManagementDataList.fromJson(Map<String, dynamic> json) {
    buildingMgmtId = json['buildingMgmtId'];
    buildingName = json['buildingName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['buildingMgmtId'] = this.buildingMgmtId;
    data['buildingName'] = this.buildingName;
    return data;
  }
}



