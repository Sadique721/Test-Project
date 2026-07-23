import 'package:savbill/webservices/base_response.dart';

class CustomerDocumentRes extends BaseResponse {
  List<DocumentDetail>? dataList;

  CustomerDocumentRes({responseCode, responseMessage, this.dataList});

  CustomerDocumentRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <DocumentDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new DocumentDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

// class DocumentDetail {
//   num? docId;
//   int? custId;
//   String? docType;
//   String? docSubType;
//   String? remark;
//   String? mode;
//   String? docStatus;
//   String? filename;
//   String? uniquename;
//   bool? isDelete;
//   String? documentNumber;
//   int? mvnoId;
//   int? nextStaff;
//
//   DocumentDetail(
//       {this.docId,
//       this.custId,
//       this.docType,
//       this.docSubType,
//       this.remark,
//       this.mode,
//       this.docStatus,
//       this.filename,
//       this.uniquename,
//       this.isDelete,
//       this.documentNumber,
//       this.mvnoId,
//       this.nextStaff});
//
//   DocumentDetail.fromJson(Map<String, dynamic> json) {
//     docId = json['docId'];
//     custId = json['custId'];
//     docType = json['docType'];
//     docSubType = json['docSubType'];
//     remark = json['remark'];
//     mode = json['mode'];
//     docStatus = json['docStatus'];
//     filename = json['filename'];
//     uniquename = json['uniquename'];
//     isDelete = json['isDelete'];
//     documentNumber = json['documentNumber'];
//     mvnoId = json['mvnoId'];
//     nextStaff = json['nextStaff'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['docId'] = this.docId;
//     data['custId'] = this.custId;
//     data['docType'] = this.docType;
//     data['docSubType'] = this.docSubType;
//     data['remark'] = this.remark;
//     data['mode'] = this.mode;
//     data['docStatus'] = this.docStatus;
//     data['filename'] = this.filename;
//     data['uniquename'] = this.uniquename;
//     data['isDelete'] = this.isDelete;
//     data['documentNumber'] = this.documentNumber;
//     data['mvnoId'] = this.mvnoId;
//     data['nextStaff'] = this.nextStaff;
//     return data;
//   }
// }
class DocumentDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? docId;
  int? custId;
  String? docType;
  String? docSubType;
  String? remark;
  String? mode;
  String? docStatus;
  String? filename;
  String? uniquename;
  bool? isDelete;
  String? documentNumber;
  String? startDate;
  String? endDate;
  int? nextTeamHierarchyMappingId;
  int? nextStaff;
  dynamic mvnoId;
  dynamic leadId;
  dynamic startDateAsString;
  dynamic endDateAsString;

  DocumentDetail(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.docId,
        this.custId,
        this.docType,
        this.docSubType,
        this.remark,
        this.mode,
        this.docStatus,
        this.filename,
        this.uniquename,
        this.isDelete,
        this.documentNumber,
        this.startDate,
        this.endDate,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.mvnoId,
        this.leadId,
        this.startDateAsString,
        this.endDateAsString});

  DocumentDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    docId = json['docId'];
    custId = json['custId'];
    docType = json['docType'];
    docSubType = json['docSubType'];
    remark = json['remark'];
    mode = json['mode'];
    docStatus = json['docStatus'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    isDelete = json['isDelete'];
    documentNumber = json['documentNumber'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    mvnoId = json['mvnoId'];
    leadId = json['leadId'];
    startDateAsString = json['startDateAsString'];
    endDateAsString = json['endDateAsString'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['docId'] = this.docId;
    data['custId'] = this.custId;
    data['docType'] = this.docType;
    data['docSubType'] = this.docSubType;
    data['remark'] = this.remark;
    data['mode'] = this.mode;
    data['docStatus'] = this.docStatus;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['isDelete'] = this.isDelete;
    data['documentNumber'] = this.documentNumber;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['mvnoId'] = this.mvnoId;
    data['leadId'] = this.leadId;
    data['startDateAsString'] = this.startDateAsString;
    data['endDateAsString'] = this.endDateAsString;
    return data;
  }
}
