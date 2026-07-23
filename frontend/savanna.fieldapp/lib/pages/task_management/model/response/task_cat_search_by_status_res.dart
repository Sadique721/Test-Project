import 'package:savbill/webservices/base_response.dart';

import '../../../dashboard/model/response/show_tat_details_res.dart';

class TaskCatSearchByStatusRes extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<TaskCatSearchByDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TaskCatSearchByStatusRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TaskCatSearchByStatusRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TaskCatSearchByDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TaskCatSearchByDataList.fromJson(v));
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

class TaskCatSearchByDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  int? caseCategoryId;
  String? status;
  int? slaTimep1;
  int? slaTimep2;
  int? slaTime3;
  String? sunitp1;
  String? sunitp2;
  String? sunitp3;
  List<TatMatrixMappings>? tatMatrixMappings;
  dynamic buId;
  int? mvnoId;
  int? rtime;
  String? runit;
  bool? isDeleted;
  bool? isDefault;
  bool? deleteFlag;
  int? primaryKey;
  int? orderId;
  int? tatForTicketID;

  TaskCatSearchByDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.caseCategoryId,
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
        this.primaryKey,this.orderId,this.tatForTicketID});

  TaskCatSearchByDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    caseCategoryId = json['caseCategoryId'];
    status = json['status'];
    slaTimep1 = json['slaTimep1'];
    slaTimep2 = json['slaTimep2'];
    slaTime3 = json['slaTime3'];
    sunitp1 = json['sunitp1'];
    sunitp2 = json['sunitp2'];
    sunitp3 = json['sunitp3'];
    if (json['tatMatrixMappings'] != null) {
      tatMatrixMappings = <TatMatrixMappings>[];
      json['tatMatrixMappings'].forEach((v) {
        tatMatrixMappings!.add(new TatMatrixMappings.fromJson(v));
      });
    }
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
    data['caseCategoryId'] = this.caseCategoryId;
    data['status'] = this.status;
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    if (this.tatMatrixMappings != null) {
      data['tatMatrixMappings'] =
          this.tatMatrixMappings!.map((v) => v.toJson()).toList();
    }
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

