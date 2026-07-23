import 'package:savbill/webservices/base_response.dart';

class ApprovalPendingPaymentRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ApprovalPendingPayment>? dataList;

  ApprovalPendingPaymentRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ApprovalPendingPaymentRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ApprovalPendingPayment>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApprovalPendingPayment.fromJson(v));
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

class ApprovalPendingPayment {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? paymode;
  String? paymentdate;
  num? amount;
  String? status;
  String? paymentBy;
  String? remarks;
  String? receiptNo;
  String? xmldocument;
  int? custId;
  bool? isDelete;
  String? type;
  int? nextTeamHierarchyMappingId;


  ApprovalPendingPayment(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.paymode,
      this.paymentdate,
      this.amount,
      this.status,
      this.paymentBy,
      this.remarks,
      this.receiptNo,
      this.xmldocument,
      this.custId,
      this.isDelete,
      this.type,
      this.nextTeamHierarchyMappingId});

  ApprovalPendingPayment.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    paymode = json['paymode'];
    paymentdate = json['paymentdate'];
    amount = json['amount'];
    status = json['status'];
    paymentBy = json['paymentBy'];
    remarks = json['remarks'];
    receiptNo = json['receiptNo'];
    xmldocument = json['xmldocument'];
    custId = json['custId'];
    isDelete = json['isDelete'];
    type = json['type'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
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
    data['paymode'] = this.paymode;
    data['paymentdate'] = this.paymentdate;
    data['amount'] = this.amount;
    data['status'] = this.status;
    data['paymentBy'] = this.paymentBy;
    data['remarks'] = this.remarks;
    data['receiptNo'] = this.receiptNo;
    data['xmldocument'] = this.xmldocument;
    data['custId'] = this.custId;
    data['isDelete'] = this.isDelete;
    data['type'] = this.type;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    return data;
  }
}
