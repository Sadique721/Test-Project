import 'package:savbill/webservices/base_response.dart';

class NewServicesAreaRes  extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<NewServiceDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  NewServicesAreaRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  NewServicesAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <NewServiceDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new NewServiceDataList.fromJson(v));
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
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class NewServiceDataList {
  int? id;
  String? name;
  int? createdById;
  List<int>? pincodes;

  NewServiceDataList({this.id, this.name, this.createdById, this.pincodes});

  NewServiceDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    createdById = json['createdById'];
    pincodes = json['pincodes'].cast<int>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['createdById'] = this.createdById;
    data['pincodes'] = this.pincodes;
    return data;
  }
}
