import 'package:savbill/webservices/base_response.dart';

class PopDetailsRes  extends BaseResponse{
  // int? responseCode;
  String? responseMessage;
  PopDetailData? data;
  // Null? dataList;
  // Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  PopDetailsRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        // this.dataList,
        // this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  PopDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new PopDetailData.fromJson(json['data']) : null;
    // dataList = json['dataList'];
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
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    // data['dataList'] = this.dataList;
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class PopDetailData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? latitude;
  String? longitude;
  List<int>? serviceAreaIdsList;
  List<String>? serviceAreaNameList;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  Null? popCode;
  int? displayId;
  String? displayName;
  int? identityKey;

  PopDetailData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.latitude,
        this.longitude,
        this.serviceAreaIdsList,
        this.serviceAreaNameList,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.popCode,
        this.displayId,
        this.displayName,
        this.identityKey});

  PopDetailData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    serviceAreaNameList = json['serviceAreaNameList'].cast<String>();
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    popCode = json['popCode'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['popCode'] = this.popCode;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
