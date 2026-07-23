import 'package:savbill/webservices/base_response.dart';

class GetCasePackageRes extends BaseResponse {
  Null? data;
  List<CASDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetCasePackageRes(
      {
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetCasePackageRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <CASDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CASDataList.fromJson(v));
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

class CASDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? casname;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? endpoint;
  int? buId;
  List<CasPackageMappings>? casPackageMappings;
  List<CasParameterMappings>? casParameterMappings;
  int? identityKey;

  CASDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.casname,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.endpoint,
        this.buId,
        this.casPackageMappings,
        this.casParameterMappings,
        this.identityKey});

  CASDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    casname = json['casname'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    endpoint = json['endpoint'];
    buId = json['buId'];
    if (json['casPackageMappings'] != null) {
      casPackageMappings = <CasPackageMappings>[];
      json['casPackageMappings'].forEach((v) {
        casPackageMappings!.add(new CasPackageMappings.fromJson(v));
      });
    }
    if (json['casParameterMappings'] != null) {
      casParameterMappings = <CasParameterMappings>[];
      json['casParameterMappings'].forEach((v) {
        casParameterMappings!.add(new CasParameterMappings.fromJson(v));
      });
    }
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
    data['casname'] = this.casname;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['endpoint'] = this.endpoint;
    data['buId'] = this.buId;
    if (this.casPackageMappings != null) {
      data['casPackageMappings'] =
          this.casPackageMappings!.map((v) => v.toJson()).toList();
    }
    if (this.casParameterMappings != null) {
      data['casParameterMappings'] =
          this.casParameterMappings!.map((v) => v.toJson()).toList();
    }
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class CasPackageMappings {
  int? id;
  String? packageName;
  int? packageId;
  int? casMasterId;
  bool? isDeleted;

  CasPackageMappings(
      {this.id,
        this.packageName,
        this.packageId,
        this.casMasterId,
        this.isDeleted});

  CasPackageMappings.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    packageName = json['packageName'];
    packageId = json['packageId'];
    casMasterId = json['casMasterId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['packageName'] = this.packageName;
    data['packageId'] = this.packageId;
    data['casMasterId'] = this.casMasterId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}

class CasParameterMappings {
  int? id;
  String? paramName;
  String? paramValue;
  int? casId;
  bool? isDeleted;

  CasParameterMappings(
      {this.id, this.paramName, this.paramValue, this.casId, this.isDeleted});

  CasParameterMappings.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    paramName = json['paramName'];
    paramValue = json['paramValue'];
    casId = json['casId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['paramName'] = this.paramName;
    data['paramValue'] = this.paramValue;
    data['casId'] = this.casId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}
