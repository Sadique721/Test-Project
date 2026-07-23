import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class TaskAddEditCategoryRes extends BaseResponse {
  String? responseMessage;
  TaskCategoryDataListDetails? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TaskAddEditCategoryRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TaskAddEditCategoryRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new TaskCategoryDataListDetails.fromJson(json['data']) : null;
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

class TaskCategoryDataListDetails {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? categoryId;
  String? categoryName;
  int? mvnoId;
  dynamic buId;
  String? status;
  List<CaseCategoryTatMappingList>? caseCategoryTatMappingList;
  bool? isDeleted;
  bool? isDefaultCaseCategory;
  dynamic lcoId;
  int? identityKey;

  TaskCategoryDataListDetails(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.categoryId,
        this.categoryName,
        this.mvnoId,
        this.buId,
        this.status,
        this.caseCategoryTatMappingList,
        this.isDeleted,
        this.isDefaultCaseCategory,
        this.lcoId,
        this.identityKey});

  TaskCategoryDataListDetails.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    categoryId = json['categoryId'];
    categoryName = json['categoryName'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    status = json['status'];
    if (json['caseCategoryTatMappingList'] != null) {
      caseCategoryTatMappingList = <CaseCategoryTatMappingList>[];
      json['caseCategoryTatMappingList'].forEach((v) {
        caseCategoryTatMappingList!
            .add(new CaseCategoryTatMappingList.fromJson(v));
      });
    }
    isDeleted = json['isDeleted'];
    isDefaultCaseCategory = json['isDefaultCaseCategory'];
    lcoId = json['lcoId'];
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
    data['categoryId'] = this.categoryId;
    data['categoryName'] = this.categoryName;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['status'] = this.status;
    if (this.caseCategoryTatMappingList != null) {
      data['caseCategoryTatMappingList'] =
          this.caseCategoryTatMappingList!.map((v) => v.toJson()).toList();
    }
    data['isDeleted'] = this.isDeleted;
    data['isDefaultCaseCategory'] = this.isDefaultCaseCategory;
    data['lcoId'] = this.lcoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class TicketTatMatrix {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  dynamic name;
  dynamic status;
  dynamic slaTimep1;
  dynamic slaTimep2;
  dynamic slaTime3;
  dynamic sunitp1;
  dynamic sunitp2;
  dynamic sunitp3;
  dynamic tatMatrixMappings;
  dynamic buId;
  dynamic mvnoId;
  dynamic rtime;
  dynamic runit;
  bool? isDeleted;
  bool? isDefault;
  bool? deleteFlag;
  int? primaryKey;

  TicketTatMatrix(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.slaTimep1,
        this.slaTimep2,
        this.slaTime3,
        this.sunitp1,
        this.sunitp2,
        this.sunitp3,
        this.tatMatrixMappings,
        this.buId,
        this.mvnoId,
        this.rtime,
        this.runit,
        this.isDeleted,
        this.isDefault,
        this.deleteFlag,
        this.primaryKey});

  TicketTatMatrix.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    slaTimep1 = json['slaTimep1'];
    slaTimep2 = json['slaTimep2'];
    slaTime3 = json['slaTime3'];
    sunitp1 = json['sunitp1'];
    sunitp2 = json['sunitp2'];
    sunitp3 = json['sunitp3'];
    tatMatrixMappings = json['tatMatrixMappings'];
    buId = json['buId'];
    mvnoId = json['mvnoId'];
    rtime = json['rtime'];
    runit = json['runit'];
    isDeleted = json['isDeleted'];
    isDefault = json['isDefault'];
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
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    data['tatMatrixMappings'] = this.tatMatrixMappings;
    data['buId'] = this.buId;
    data['mvnoId'] = this.mvnoId;
    data['rtime'] = this.rtime;
    data['runit'] = this.runit;
    data['isDeleted'] = this.isDeleted;
    data['isDefault'] = this.isDefault;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
