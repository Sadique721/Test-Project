import 'package:savbill/webservices/base_response.dart';

class RoleOpertaionRes extends BaseResponse{
  dynamic responseCode;
  String? responseMessage;
  // Null? data;
  List<RoleOperationDataList>? dataList;
  // Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  RoleOpertaionRes(
      {this.responseCode,
        this.responseMessage,
        // this.data,
        this.dataList,
        // this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  RoleOpertaionRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    // data = json['data'];
    if (json['dataList'] != null) {
      dataList = <RoleOperationDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new RoleOperationDataList.fromJson(v));
      });
    }
    // excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    // data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class RoleOperationDataList {
  int? roleid;
  List<Operations>? operations;

  RoleOperationDataList({this.roleid, this.operations});

  RoleOperationDataList.fromJson(Map<String, dynamic> json) {
    roleid = json['roleid'];
    if (json['operations'] != null) {
      operations = <Operations>[];
      json['operations'].forEach((v) {
        operations!.add(new Operations.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['roleid'] = this.roleid;
    if (this.operations != null) {
      data['operations'] = this.operations!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Operations {
  int? opid;
  int? classid;

  Operations({this.opid, this.classid});

  Operations.fromJson(Map<String, dynamic> json) {
    opid = json['opid'];
    classid = json['classid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['opid'] = this.opid;
    data['classid'] = this.classid;
    return data;
  }
}


