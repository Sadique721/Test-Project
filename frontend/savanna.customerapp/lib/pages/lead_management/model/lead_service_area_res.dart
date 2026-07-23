import 'package:savbill/webservices/base_response.dart';

class LeadServiceAreaRes  extends BaseResponse{
  String? responseMessage;
  LeadServiceAeraData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  LeadServiceAreaRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  LeadServiceAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new LeadServiceAeraData.fromJson(json['data']) : null;
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

class LeadServiceAeraData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? siteName;
  String? status;
  bool? isDeleted;
  dynamic latitude;
  dynamic longitude;
  dynamic areaid;
  int? mvnoId;
  List<int>? pincodes;
  int? cityid;
  // List<dynamic>? polyGoneList;
  int? displayId;
  String? displayName;
  dynamic radius;
  double? radiusDis;
  String? serviceAreaType;
  dynamic blockNo;
  dynamic mvnoIds;
  dynamic mvnoLists;

  LeadServiceAeraData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.siteName,
        this.status,
        this.isDeleted,
        this.latitude,
        this.longitude,
        this.areaid,
        this.mvnoId,
        this.pincodes,
        this.cityid,
        // this.polyGoneList,
        this.displayId,
        this.displayName,
        this.radius,
        this.radiusDis,
        this.serviceAreaType,
        this.blockNo,
        this.mvnoIds,
        this.mvnoLists});

  LeadServiceAeraData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    siteName = json['siteName'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaid = json['areaid'];
    mvnoId = json['mvnoId'];
    pincodes = json['pincodes'].cast<int>();
    cityid = json['cityid'];
    // if (json['polyGoneList'] != null) {
    //   polyGoneList = <dynamic>[];
    //   json['polyGoneList'].forEach((v) {
    //     polyGoneList!.add(dynamic.fromJson(v));
    //   });
    // }
    displayId = json['displayId'];
    displayName = json['displayName'];
    radius = json['radius'];
    radiusDis = json['radiusDis'];
    serviceAreaType = json['serviceAreaType'];
    blockNo = json['blockNo'];
    mvnoIds = json['mvnoIds'];
    mvnoLists = json['mvnoLists'];
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
    data['siteName'] = this.siteName;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaid'] = this.areaid;
    data['mvnoId'] = this.mvnoId;
    data['pincodes'] = this.pincodes;
    data['cityid'] = this.cityid;
    // if (this.polyGoneList != null) {
    //   data['polyGoneList'] = this.polyGoneList!.map((v) => v.toJson()).toList();
    // }
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['radius'] = this.radius;
    data['radiusDis'] = this.radiusDis;
    data['serviceAreaType'] = this.serviceAreaType;
    data['blockNo'] = this.blockNo;
    data['mvnoIds'] = this.mvnoIds;
    data['mvnoLists'] = this.mvnoLists;
    return data;
  }
}
