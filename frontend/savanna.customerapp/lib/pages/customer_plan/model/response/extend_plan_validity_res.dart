import 'package:savbill/webservices/base_response.dart';

class CurrentPlanExtendValidityRes extends BaseResponse {

  dynamic data;
  List<ExtendPlanValidityDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CurrentPlanExtendValidityRes(
      {
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CurrentPlanExtendValidityRes.fromJson(Map<String, dynamic> json) {
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ExtendPlanValidityDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ExtendPlanValidityDataList.fromJson(v));
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

class ExtendPlanValidityDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? planId;
  dynamic postpaidPlanPojo;
  dynamic custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  dynamic startDateString;
  dynamic endDateString;
  dynamic expiryDateString;
  dynamic status;
  dynamic qospolicyId;
  dynamic uploadqos;
  dynamic downloadqos;
  dynamic uploadts;
  dynamic downloadts;
  String? service;
  bool? isDelete;
  int? offerPrice;
  int? taxAmount;
  dynamic creditdocid;
  int? walletBalUsed;
  String? purchaseType;
  dynamic onlinePurchaseId;
  String? purchaseFrom;
  dynamic debitdocid;
  dynamic validity;
  dynamic planName;
  int? discount;
  dynamic plangroupid;
  int? planValidityDays;
  bool? isInvoiceToOrg;
  String? billTo;
  int? newAmount;
  dynamic renewalId;
  dynamic custRefId;
  dynamic custRefName;
  dynamic expiry;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  int? graceDays;
  int? custServiceMappingId;
  dynamic plangroup;
  dynamic serviceId;
  dynamic ezyBillServiceId;
  dynamic oldDiscount;
  dynamic remarks;
  dynamic invoiceType;
  dynamic traildebitdocid;
  int? isTrialValidityDays;
  int? trialPlanValidityCount;
  dynamic ezBillPackageId;
  dynamic casId;
  dynamic invoiceformat;
  dynamic billableCustomerId;
  dynamic unitsOfValidity;
  String? extendValidityremarks;
  dynamic linkAcceptanceDTO;
  dynamic extendDate;
  String? discountType;
  dynamic discountExpiryDate;
  dynamic startServiceDate;
  dynamic cprIdForPromiseToPay;
  bool? isHold;
  dynamic isVoid;
  dynamic isContainsCustomerInvoice;
  dynamic customerCpr;
  dynamic serialNumber;
  dynamic serivceId;

  ExtendPlanValidityDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.planId,
        this.postpaidPlanPojo,
        this.custid,
        this.startDate,
        this.endDate,
        this.expiryDate,
        this.startDateString,
        this.endDateString,
        this.expiryDateString,
        this.status,
        this.qospolicyId,
        this.uploadqos,
        this.downloadqos,
        this.uploadts,
        this.downloadts,
        this.service,
        this.isDelete,
        this.offerPrice,
        this.taxAmount,
        this.creditdocid,
        this.walletBalUsed,
        this.purchaseType,
        this.onlinePurchaseId,
        this.purchaseFrom,
        this.debitdocid,
        this.validity,
        this.planName,
        this.discount,
        this.plangroupid,
        this.planValidityDays,
        this.isInvoiceToOrg,
        this.billTo,
        this.newAmount,
        this.renewalId,
        this.custRefId,
        this.custRefName,
        this.expiry,
        this.custPlanStatus,
        this.isinvoicestop,
        this.istrialplan,
        this.isInvoiceCreated,
        this.graceDays,
        this.custServiceMappingId,
        this.plangroup,
        this.serviceId,
        this.ezyBillServiceId,
        this.oldDiscount,
        this.remarks,
        this.invoiceType,
        this.traildebitdocid,
        this.isTrialValidityDays,
        this.trialPlanValidityCount,
        this.ezBillPackageId,
        this.casId,
        this.invoiceformat,
        this.billableCustomerId,
        this.unitsOfValidity,
        this.extendValidityremarks,
        this.linkAcceptanceDTO,
        this.extendDate,
        this.discountType,
        this.discountExpiryDate,
        this.startServiceDate,
        this.cprIdForPromiseToPay,
        this.isHold,
        this.isVoid,
        this.isContainsCustomerInvoice,
        this.customerCpr,
        this.serialNumber,
        this.serivceId});

  ExtendPlanValidityDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planId = json['planId'];
    postpaidPlanPojo = json['postpaidPlanPojo'];
    custid = json['custid'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    expiryDate = json['expiryDate'];
    startDateString = json['startDateString'];
    endDateString = json['endDateString'];
    expiryDateString = json['expiryDateString'];
    status = json['status'];
    qospolicyId = json['qospolicyId'];
    uploadqos = json['uploadqos'];
    downloadqos = json['downloadqos'];
    uploadts = json['uploadts'];
    downloadts = json['downloadts'];

    service = json['service'];
    isDelete = json['isDelete'];
    offerPrice = json['offerPrice'];
    taxAmount = json['taxAmount'];
    creditdocid = json['creditdocid'];
    walletBalUsed = json['walletBalUsed'];
    purchaseType = json['purchaseType'];
    onlinePurchaseId = json['onlinePurchaseId'];
    purchaseFrom = json['purchaseFrom'];
    debitdocid = json['debitdocid'];
    validity = json['validity'];
    planName = json['planName'];
    discount = json['discount'];
    plangroupid = json['plangroupid'];
    planValidityDays = json['planValidityDays'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    billTo = json['billTo'];
    newAmount = json['newAmount'];
    renewalId = json['renewalId'];
    custRefId = json['custRefId'];
    custRefName = json['custRefName'];
    expiry = json['expiry'];
    custPlanStatus = json['custPlanStatus'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    isInvoiceCreated = json['isInvoiceCreated'];
    graceDays = json['graceDays'];
    custServiceMappingId = json['custServiceMappingId'];
    plangroup = json['plangroup'];
    serviceId = json['serviceId'];
    ezyBillServiceId = json['ezyBillServiceId'];
    oldDiscount = json['oldDiscount'];
    remarks = json['remarks'];
    invoiceType = json['invoiceType'];
    traildebitdocid = json['traildebitdocid'];
    isTrialValidityDays = json['isTrialValidityDays'];
    trialPlanValidityCount = json['trialPlanValidityCount'];
    ezBillPackageId = json['ezBillPackageId'];
    casId = json['casId'];
    invoiceformat = json['invoiceformat'];
    billableCustomerId = json['billableCustomerId'];
    unitsOfValidity = json['unitsOfValidity'];
    extendValidityremarks = json['extendValidityremarks'];
    linkAcceptanceDTO = json['linkAcceptanceDTO'];
    extendDate = json['extendDate'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    startServiceDate = json['startServiceDate'];
    cprIdForPromiseToPay = json['cprIdForPromiseToPay'];
    isHold = json['isHold'];
    isVoid = json['isVoid'];
    isContainsCustomerInvoice = json['isContainsCustomerInvoice'];
    customerCpr = json['customerCpr'];
    serialNumber = json['serialNumber'];
    serivceId = json['serivceId'];
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
    data['planId'] = this.planId;
    data['postpaidPlanPojo'] = this.postpaidPlanPojo;
    data['custid'] = this.custid;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['expiryDate'] = this.expiryDate;
    data['startDateString'] = this.startDateString;
    data['endDateString'] = this.endDateString;
    data['expiryDateString'] = this.expiryDateString;
    data['status'] = this.status;
    data['qospolicyId'] = this.qospolicyId;
    data['uploadqos'] = this.uploadqos;
    data['downloadqos'] = this.downloadqos;
    data['uploadts'] = this.uploadts;
    data['downloadts'] = this.downloadts;
    data['service'] = this.service;
    data['isDelete'] = this.isDelete;
    data['offerPrice'] = this.offerPrice;
    data['taxAmount'] = this.taxAmount;
    data['creditdocid'] = this.creditdocid;
    data['walletBalUsed'] = this.walletBalUsed;
    data['purchaseType'] = this.purchaseType;
    data['onlinePurchaseId'] = this.onlinePurchaseId;
    data['purchaseFrom'] = this.purchaseFrom;
    data['debitdocid'] = this.debitdocid;
    data['validity'] = this.validity;
    data['planName'] = this.planName;
    data['discount'] = this.discount;
    data['plangroupid'] = this.plangroupid;
    data['planValidityDays'] = this.planValidityDays;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['billTo'] = this.billTo;
    data['newAmount'] = this.newAmount;
    data['renewalId'] = this.renewalId;
    data['custRefId'] = this.custRefId;
    data['custRefName'] = this.custRefName;
    data['expiry'] = this.expiry;
    data['custPlanStatus'] = this.custPlanStatus;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['isInvoiceCreated'] = this.isInvoiceCreated;
    data['graceDays'] = this.graceDays;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['plangroup'] = this.plangroup;
    data['serviceId'] = this.serviceId;
    data['ezyBillServiceId'] = this.ezyBillServiceId;
    data['oldDiscount'] = this.oldDiscount;
    data['remarks'] = this.remarks;
    data['invoiceType'] = this.invoiceType;
    data['traildebitdocid'] = this.traildebitdocid;
    data['isTrialValidityDays'] = this.isTrialValidityDays;
    data['trialPlanValidityCount'] = this.trialPlanValidityCount;
    data['ezBillPackageId'] = this.ezBillPackageId;
    data['casId'] = this.casId;
    data['invoiceformat'] = this.invoiceformat;
    data['billableCustomerId'] = this.billableCustomerId;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['extendValidityremarks'] = this.extendValidityremarks;
    data['linkAcceptanceDTO'] = this.linkAcceptanceDTO;
    data['extendDate'] = this.extendDate;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['startServiceDate'] = this.startServiceDate;
    data['cprIdForPromiseToPay'] = this.cprIdForPromiseToPay;
    data['isHold'] = this.isHold;
    data['isVoid'] = this.isVoid;
    data['isContainsCustomerInvoice'] = this.isContainsCustomerInvoice;
    data['customerCpr'] = this.customerCpr;
    data['serialNumber'] = this.serialNumber;
    data['serivceId'] = this.serivceId;
    return data;
  }
}
