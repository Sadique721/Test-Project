import 'package:savbill/webservices/base_response.dart';

class TaskSubCategoryDataRes extends BaseResponse {
  String? responseMessage;
  dynamic data;
  List<TaskSubCategoryDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TaskSubCategoryDataRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TaskSubCategoryDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TaskSubCategoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TaskSubCategoryDataList.fromJson(v));
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

class TaskSubCategoryDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? subCategoryId;
  String? subCategoryName;
  String? discription;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  dynamic buId;
  dynamic lcoId;
  List<CaseSubCategoryCategoryMappingList>? caseSubCategoryCategoryMappingList;
  dynamic isDefaultCaseSubCategory;
  bool? deleteFlag;
  int? primaryKey;

  TaskSubCategoryDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.subCategoryId,
        this.subCategoryName,
        this.discription,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.buId,
        this.lcoId,
        this.caseSubCategoryCategoryMappingList,
        this.isDefaultCaseSubCategory,
        this.deleteFlag,
        this.primaryKey});

  TaskSubCategoryDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    subCategoryId = json['subCategoryId'];
    subCategoryName = json['subCategoryName'];
    discription = json['discription'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    lcoId = json['lcoId'];
    if (json['caseSubCategoryCategoryMappingList'] != null) {
      caseSubCategoryCategoryMappingList =
      <CaseSubCategoryCategoryMappingList>[];
      json['caseSubCategoryCategoryMappingList'].forEach((v) {
        caseSubCategoryCategoryMappingList!
            .add(new CaseSubCategoryCategoryMappingList.fromJson(v));
      });
    }
    isDefaultCaseSubCategory = json['isDefaultCaseSubCategory'];
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
    data['subCategoryId'] = this.subCategoryId;
    data['subCategoryName'] = this.subCategoryName;
    data['discription'] = this.discription;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['lcoId'] = this.lcoId;
    if (this.caseSubCategoryCategoryMappingList != null) {
      data['caseSubCategoryCategoryMappingList'] = this
          .caseSubCategoryCategoryMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    data['isDefaultCaseSubCategory'] = this.isDefaultCaseSubCategory;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class CaseSubCategoryCategoryMappingList {
  int? id;
  int? caseCategoryId;
  int? caseSubCategoryId;

  CaseSubCategoryCategoryMappingList(
      {this.id, this.caseCategoryId, this.caseSubCategoryId});

  CaseSubCategoryCategoryMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    caseCategoryId = json['caseCategoryId'];
    caseSubCategoryId = json['caseSubCategoryId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['caseCategoryId'] = this.caseCategoryId;
    data['caseSubCategoryId'] = this.caseSubCategoryId;
    return data;
  }
}
