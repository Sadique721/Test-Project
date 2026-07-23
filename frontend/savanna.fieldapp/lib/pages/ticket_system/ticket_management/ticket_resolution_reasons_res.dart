class TicketResolutionReasonsRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ResolutionReasonsDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TicketResolutionReasonsRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TicketResolutionReasonsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ResolutionReasonsDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ResolutionReasonsDataList.fromJson(v));
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

class ResolutionReasonsDataList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  List<ResoSubCategoryMappingList>? resoSubCategoryMappingList;
  int? mvnoId;
  int? buId;
  dynamic lcoId;
  List<RootCauseResolutionMappingList>? rootCauseResolutionMappingList;
  bool? deleteFlag;
  int? primaryKey;

  ResolutionReasonsDataList(
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
        this.resoSubCategoryMappingList,
        this.mvnoId,
        this.buId,
        this.lcoId,
        this.rootCauseResolutionMappingList,
        this.deleteFlag,
        this.primaryKey});

  ResolutionReasonsDataList.fromJson(Map<String, dynamic> json) {
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
    if (json['resoSubCategoryMappingList'] != null) {
      resoSubCategoryMappingList = <ResoSubCategoryMappingList>[];
      json['resoSubCategoryMappingList'].forEach((v) {
        resoSubCategoryMappingList!
            .add(new ResoSubCategoryMappingList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    lcoId = json['lcoId'];
    if (json['rootCauseResolutionMappingList'] != null) {
      rootCauseResolutionMappingList = <RootCauseResolutionMappingList>[];
      json['rootCauseResolutionMappingList'].forEach((v) {
        rootCauseResolutionMappingList!
            .add(new RootCauseResolutionMappingList.fromJson(v));
      });
    }
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    if (this.resoSubCategoryMappingList != null) {
      data['resoSubCategoryMappingList'] =
          this.resoSubCategoryMappingList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['lcoId'] = this.lcoId;
    if (this.rootCauseResolutionMappingList != null) {
      data['rootCauseResolutionMappingList'] =
          this.rootCauseResolutionMappingList!.map((v) => v.toJson()).toList();
    }
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class ResoSubCategoryMappingList {
  int? id;
  int? resId;
  int? subcateId;

  ResoSubCategoryMappingList({this.id, this.resId, this.subcateId});

  ResoSubCategoryMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    resId = json['resId'];
    subcateId = json['subcateId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['resId'] = this.resId;
    data['subcateId'] = this.subcateId;
    return data;
  }
}

class RootCauseResolutionMappingList {
  int? id;
  String? rootCauseReason;
  int? resolutionId;

  RootCauseResolutionMappingList(
      {this.id, this.rootCauseReason, this.resolutionId});

  RootCauseResolutionMappingList.fromJson(Map<String, dynamic> json) {
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