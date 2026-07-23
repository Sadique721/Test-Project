import 'package:savbill/pages/dashboard/model/response/show_tat_details_res.dart';
import 'package:savbill/webservices/base_response.dart';

class TatTicketListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<TatTicketDetail>? dataList;

  TatTicketListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  TatTicketListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <TatTicketDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TatTicketDetail.fromJson(v));
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

class TatTicketDetail {
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
  int? slaTimep1;
  int? slaTimep2;
  int? slaTime3;
  String? sunitp1;
  String? sunitp2;
  String? sunitp3;
  int? rtime;
  String? runit;
  int? identityKey;
  List<TatMatrixMappings>? tatMatrixMappings;


  TatTicketDetail(
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
      this.identityKey,
      this.tatMatrixMappings});

  TatTicketDetail.fromJson(Map<String, dynamic> json) {
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
    identityKey = json['identityKey'];
    if (json['tatMatrixMappings'] != null) {
      tatMatrixMappings = <TatMatrixMappings>[];
      json['tatMatrixMappings'].forEach((v) {
        tatMatrixMappings!.add(new TatMatrixMappings.fromJson(v));
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
    data['slaTimep1'] = this.slaTimep1;
    data['slaTimep2'] = this.slaTimep2;
    data['slaTime3'] = this.slaTime3;
    data['sunitp1'] = this.sunitp1;
    data['sunitp2'] = this.sunitp2;
    data['sunitp3'] = this.sunitp3;
    data['rtime'] = this.rtime;
    data['runit'] = this.runit;
    data['identityKey'] = this.identityKey;
    if (this.tatMatrixMappings != null) {
      data['tatMatrixMappings'] =
          this.tatMatrixMappings!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

// class TatMatrixMappings {
//   int? id;
//   int? orderNo;
//   String? level;
//   num? mtime1;
//   num? mtime2;
//   num? mtime3;
//   String? munit;
//   String? action;
//   int? tatMappingtId;
//   bool? isDeleted;
//
//   TatMatrixMappings(
//       {this.id,
//       this.orderNo,
//       this.level,
//       this.mtime1,
//       this.mtime2,
//       this.mtime3,
//       this.munit,
//       this.action,
//       this.tatMappingtId,
//       this.isDeleted});
//
//   TatMatrixMappings.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     orderNo = json['orderNo'];
//     level = json['level'];
//     mtime1 = json['mtime1'];
//     mtime2 = json['mtime2'];
//     mtime3 = json['mtime3'];
//     munit = json['munit'];
//     action = json['action'];
//     tatMappingtId = json['tatMappingtId'];
//     isDeleted = json['isDeleted'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['orderNo'] = this.orderNo;
//     data['level'] = this.level;
//     data['mtime1'] = this.mtime1;
//     data['mtime2'] = this.mtime2;
//     data['mtime3'] = this.mtime3;
//     data['munit'] = this.munit;
//     data['action'] = this.action;
//     data['tatMappingtId'] = this.tatMappingtId;
//     data['isDeleted'] = this.isDeleted;
//     return data;
//   }
// }
