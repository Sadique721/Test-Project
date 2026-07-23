import 'package:savbill/webservices/base_response.dart';

class CustomerCafDropDownRes extends BaseResponse {
  String? responseMessage;
  dynamic data;
  List<CustomerCafDropDownStaffList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustomerCafDropDownRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustomerCafDropDownRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <CustomerCafDropDownStaffList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerCafDropDownStaffList.fromJson(v));
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

class CustomerCafDropDownStaffList {
  int? id;
  String? username;
  String? firstname;
  String? lastname;
  String? fullName;
  dynamic phone;

  CustomerCafDropDownStaffList(
      {this.id,
        this.username,
        this.firstname,
        this.lastname,
        this.fullName,
        this.phone});

  CustomerCafDropDownStaffList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    username = json['username'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    fullName = json['fullName'];
    phone = json['phone'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['username'] = this.username;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['fullName'] = this.fullName;
    data['phone'] = this.phone;
    return data;
  }
}
