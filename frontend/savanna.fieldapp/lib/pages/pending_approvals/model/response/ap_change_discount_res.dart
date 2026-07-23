import 'package:savbill/webservices/base_response.dart';

class APChangeDiscountRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<APChangeDiscount>? dataList;

  APChangeDiscountRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  APChangeDiscountRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <APChangeDiscount>[];
      json['dataList'].forEach((v) {
        dataList!.add(new APChangeDiscount.fromJson(v));
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

class APChangeDiscount {
  int? id;
  int? planId;
  int? custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? status;
  int? qospolicyId;
  String? service;
  bool? isDelete;
  double? offerPrice;
  double? taxAmount;
  num? walletBalUsed;
  String? purchaseType;
  String? purchaseFrom;
  num? discount;
  bool? isInvoiceToOrg;
  String? billTo;
  num? newAmount;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  num? graceDays;

  APChangeDiscount(
      {this.id,
      this.planId,
      this.custid,
      this.startDate,
      this.endDate,
      this.expiryDate,
      this.status,
      this.qospolicyId,
      this.service,
      this.isDelete,
      this.offerPrice,
      this.taxAmount,
      this.walletBalUsed,
      this.purchaseType,
      this.purchaseFrom,
      this.discount,
      this.isInvoiceToOrg,
      this.billTo,
      this.newAmount,
      this.custPlanStatus,
      this.isinvoicestop,
      this.istrialplan,
      this.isInvoiceCreated,
      this.graceDays});

  APChangeDiscount.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    planId = json['planId'];
    custid = json['custid'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    expiryDate = json['expiryDate'];
    status = json['status'];
    qospolicyId = json['qospolicyId'];
    service = json['service'];
    isDelete = json['isDelete'];
    offerPrice = json['offerPrice'];
    taxAmount = json['taxAmount'];
    walletBalUsed = json['walletBalUsed'];
    purchaseType = json['purchaseType'];
    purchaseFrom = json['purchaseFrom'];
    discount = json['discount'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    billTo = json['billTo'];
    newAmount = json['newAmount'];
    custPlanStatus = json['custPlanStatus'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    isInvoiceCreated = json['isInvoiceCreated'];
    graceDays = json['graceDays'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['planId'] = this.planId;
    data['custid'] = this.custid;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['expiryDate'] = this.expiryDate;
    data['status'] = this.status;
    data['qospolicyId'] = this.qospolicyId;
    data['service'] = this.service;
    data['isDelete'] = this.isDelete;
    data['offerPrice'] = this.offerPrice;
    data['taxAmount'] = this.taxAmount;
    data['walletBalUsed'] = this.walletBalUsed;
    data['purchaseType'] = this.purchaseType;
    data['purchaseFrom'] = this.purchaseFrom;
    data['discount'] = this.discount;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['billTo'] = this.billTo;
    data['newAmount'] = this.newAmount;
    data['custPlanStatus'] = this.custPlanStatus;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['isInvoiceCreated'] = this.isInvoiceCreated;
    data['graceDays'] = this.graceDays;
    return data;
  }
}
