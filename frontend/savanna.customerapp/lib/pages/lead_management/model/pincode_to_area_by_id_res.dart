import 'package:savbill/webservices/base_response.dart';

class PincodeToAreaByIdRes extends BaseResponse{
  String? responseMessage;
  PincodeToAreaByIdData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  PincodeToAreaByIdRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  PincodeToAreaByIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new PincodeToAreaByIdData.fromJson(json['data']) : null;
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

class PincodeToAreaByIdData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? pincodeid;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? stateId;
  int? cityId;
  String? cityName;
  String? stateName;
  String? countryName;
  String? areas;
  int? displayId;
  String? displayName;
  int? mvnoId;

  PincodeToAreaByIdData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.pincodeid,
        this.pincode,
        this.status,
        this.isDeleted,
        this.countryId,
        this.stateId,
        this.cityId,
        this.cityName,
        this.stateName,
        this.countryName,
        this.areas,
        this.displayId,
        this.displayName,
        this.mvnoId});

  PincodeToAreaByIdData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    pincodeid = json['pincodeid'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    stateId = json['stateId'];
    cityId = json['cityId'];
    cityName = json['cityName'];
    stateName = json['stateName'];
    countryName = json['countryName'];
    areas = json['areas'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['pincodeid'] = this.pincodeid;
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['stateId'] = this.stateId;
    data['cityId'] = this.cityId;
    data['cityName'] = this.cityName;
    data['stateName'] = this.stateName;
    data['countryName'] = this.countryName;
    data['areas'] = this.areas;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
