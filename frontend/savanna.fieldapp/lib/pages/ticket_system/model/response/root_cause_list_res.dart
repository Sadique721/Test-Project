import 'package:savbill/webservices/base_response.dart';

class RootCauseListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<RootCauseDetail>? dataList;

  RootCauseListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  RootCauseListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <RootCauseDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new RootCauseDetail.fromJson(v));
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

class RootCauseDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  int? buId;
  List<ResoSubCategoryMappingList>? resoSubCategoryMappingList;
  List<RootCauseResolutionMapping>? rootCauseResolutionMappingList;

  RootCauseDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.isDeleted,
      this.mvnoId,
      this.buId,
        this.resoSubCategoryMappingList,
      this.rootCauseResolutionMappingList});

  RootCauseDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];

    if (json['resoSubCategoryMappingList'] != null) {
      resoSubCategoryMappingList = <ResoSubCategoryMappingList>[];
      json['resoSubCategoryMappingList'].forEach((v) {
        resoSubCategoryMappingList!
            .add(new ResoSubCategoryMappingList.fromJson(v));
      });
    }
    if (json['rootCauseResolutionMappingList'] != null) {
      rootCauseResolutionMappingList = <RootCauseResolutionMapping>[];
      json['rootCauseResolutionMappingList'].forEach((v) {
        rootCauseResolutionMappingList!
            .add(new RootCauseResolutionMapping.fromJson(v));
      });
    }
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
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;

    if (this.resoSubCategoryMappingList != null) {
      data['resoSubCategoryMappingList'] =
          this.resoSubCategoryMappingList!.map((v) => v.toJson()).toList();
    }
    if (this.rootCauseResolutionMappingList != null) {
      data['rootCauseResolutionMappingList'] =
          this.rootCauseResolutionMappingList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ResoSubCategoryMappingList {
  // int? id;
  int? resId;
  int? subcateId;
  String? subCateName;


  ResoSubCategoryMappingList({/*this.id,*/ this.resId, this.subcateId,this.subCateName});

  ResoSubCategoryMappingList.fromJson(Map<String, dynamic> json) {
    // id = json['id'];
    resId = json['resId'];
    subcateId = json['subcateId'];
    // subCateName = json['subCateName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    // data['id'] = this.id;
    data['resId'] = this.resId;
    data['subcateId'] = this.subcateId;
    // data['subCateName'] = this.subCateName;
    return data;
  }
}

class RootCauseResolutionMapping {
  int? id;
  String? rootCauseReason;
  int? resolutionId;

  RootCauseResolutionMapping(
      {this.id, this.rootCauseReason, this.resolutionId});

  RootCauseResolutionMapping.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    rootCauseReason = json['rootCauseReason'];
    resolutionId = json['resolutionId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['rootCauseReason'] = this.rootCauseReason;
    data['resolutionId'] = this.resolutionId;
    return data;
  }
}
