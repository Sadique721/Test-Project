import 'package:savbill/webservices/base_response.dart';

import '../../../dashboard/model/response/show_tat_details_res.dart';

class TatTaskListRes  extends BaseResponse{
  dynamic responseCode;
  String? responseMessage;
  dynamic data;
  List<TatTaskListDetails>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TatTaskListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TatTaskListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TatTaskListDetails>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TatTaskListDetails.fromJson(v));
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

class TatTaskListDetails {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  dynamic buId;
  int? slaTimep1;
  int? slaTimep2;
  int? slaTime3;
  String? sunitp1;
  String? sunitp2;
  String? sunitp3;
  int? rtime;
  String? runit;
  List<TatMatrixMappings>? tatMatrixMappings;
  int? identityKey;

  TatTaskListDetails(
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
        this.slaTimep1,
        this.slaTimep2,
        this.slaTime3,
        this.sunitp1,
        this.sunitp2,
        this.sunitp3,
        this.rtime,
        this.runit,
        this.tatMatrixMappings,
        this.identityKey});

  TatTaskListDetails.fromJson(Map<String, dynamic> json) {
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
    slaTimep1 = json['slaTimep1'];
    slaTimep2 = json['slaTimep2'];
    slaTime3 = json['slaTime3'];
    sunitp1 = json['sunitp1'];
    sunitp2 = json['sunitp2'];
    sunitp3 = json['sunitp3'];
    rtime = json['rtime'];
    runit = json['runit'];
    if (json['tatMatrixMappings'] != null) {
      tatMatrixMappings = <TatMatrixMappings>[];
      json['tatMatrixMappings'].forEach((v) {
        tatMatrixMappings!.add(new TatMatrixMappings.fromJson(v));
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
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    data['rtime'] = this.rtime;
    data['runit'] = this.runit;
    if (this.tatMatrixMappings != null) {
      data['tatMatrixMappings'] =
          this.tatMatrixMappings!.map((v) => v.toJson()).toList();
    }
    data['identityKey'] = this.identityKey;
    return data;
  }
}


