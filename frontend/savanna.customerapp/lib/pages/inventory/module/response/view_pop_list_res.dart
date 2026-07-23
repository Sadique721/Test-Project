import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';

class ViewPopListRes {
  int? responseCode;
  String? responseMessage;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<PopDetail>? dataList;

  ViewPopListRes(
      {this.responseCode,
      this.responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewPopListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <PopDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PopDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PopDetail {
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
  List<String>? serviceAreaNameList;
  List<int>? serviceAreaIdsList;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  int? identityKey;

  PopDetail(
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
     this.serviceAreaNameList,
        this.serviceAreaIdsList,
      this.status,
      this.isDeleted,
      this.mvnoId,
      this.identityKey});

  PopDetail.fromJson(Map<String, dynamic> json) {
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
   /* if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <StaffServiceAreaDetail>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new StaffServiceAreaDetail.fromJson(v));
      });
    }*/
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
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
    /*if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }*/
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
