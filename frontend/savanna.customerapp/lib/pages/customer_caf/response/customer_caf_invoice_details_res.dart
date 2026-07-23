import 'package:savbill/webservices/base_response.dart';

class CustomerCafInvoiceDetailRes extends BaseResponse {
  List<Invoicesearchlist>? invoicesearchlist;
  PageDetails? pageDetails;
  String? timestamp;
  int? status;

  CustomerCafInvoiceDetailRes(
      {this.invoicesearchlist, this.pageDetails, this.timestamp, this.status});

  CustomerCafInvoiceDetailRes.fromJson(Map<String, dynamic> json) {
    if (json['invoicesearchlist'] != null) {
      invoicesearchlist = <Invoicesearchlist>[];
      json['invoicesearchlist'].forEach((v) {
        invoicesearchlist!.add(new Invoicesearchlist.fromJson(v));
      });
    }
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.invoicesearchlist != null) {
      data['invoicesearchlist'] =
          this.invoicesearchlist!.map((v) => v.toJson()).toList();
    }
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Invoicesearchlist {
  String? createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic errCode;
  dynamic errMessage;
  dynamic id;
  String? docnumber;
  // CustomerPojo? customerPojo;
  String? billdate;
  String? startdate;
  String? endate;
  String? duedate;
  dynamic latepaymentdate;
  double? subtotal;
  double? tax;
  dynamic discount;
  double? totalamount;
  dynamic previousbalance;
  dynamic latepaymentfee;
  dynamic currentpayment;
  dynamic currentdebit;
  dynamic currentcredit;
  dynamic totaldue;
  String? amountinwords;
  String? dueinwords;
  dynamic billrunid;
  String? billrunstatus;
  dynamic document;
  dynamic custid;
  String? customerName;
  String? custType;
  String? paymentStatus;
  dynamic billableToName;
  double? adjustedAmount;
  List<DebitDocDetails>? debitDocDetails;
  String? referenceNo;
  dynamic purchaseorderId;
  dynamic delete;

  Invoicesearchlist(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.errCode,
        this.errMessage,
        this.id,
        this.docnumber,
        // this.customerPojo,
        this.billdate,
        this.startdate,
        this.endate,
        this.duedate,
        this.latepaymentdate,
        this.subtotal,
        this.tax,
        this.discount,
        this.totalamount,
        this.previousbalance,
        this.latepaymentfee,
        this.currentpayment,
        this.currentdebit,
        this.currentcredit,
        this.totaldue,
        this.amountinwords,
        this.dueinwords,
        this.billrunid,
        this.billrunstatus,
        this.document,
        this.custid,
        this.customerName,
        this.custType,
        this.paymentStatus,
        this.billableToName,
        this.adjustedAmount,
        this.debitDocDetails,
        this.referenceNo,
        this.purchaseorderId,
        this.delete});

  Invoicesearchlist.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    errCode = json['errCode'];
    errMessage = json['errMessage'];
    id = json['id'];
    docnumber = json['docnumber'];
    // customerPojo = json['customerPojo'] != null
    //     ? new CustomerPojo.fromJson(json['customerPojo'])
    //     : null;
    billdate = json['billdate'];
    startdate = json['startdate'];
    endate = json['endate'];
    duedate = json['duedate'];
    latepaymentdate = json['latepaymentdate'];
    subtotal = json['subtotal'];
    tax = json['tax'];
    discount = json['discount'];
    totalamount = json['totalamount'];
    previousbalance = json['previousbalance'];
    latepaymentfee = json['latepaymentfee'];
    currentpayment = json['currentpayment'];
    currentdebit = json['currentdebit'];
    currentcredit = json['currentcredit'];
    totaldue = json['totaldue'];
    amountinwords = json['amountinwords'];
    dueinwords = json['dueinwords'];
    billrunid = json['billrunid'];
    billrunstatus = json['billrunstatus'];
    document = json['document'];
    custid = json['custid'];
    customerName = json['customerName'];
    custType = json['custType'];
    paymentStatus = json['paymentStatus'];
    billableToName = json['billableToName'];
    adjustedAmount = json['adjustedAmount'];
    if (json['debitDocDetails'] != null) {
      debitDocDetails = <DebitDocDetails>[];
      json['debitDocDetails'].forEach((v) {
        debitDocDetails!.add(new DebitDocDetails.fromJson(v));
      });
    }
    referenceNo = json['referenceNo'];
    purchaseorderId = json['purchaseorder_id'];
    delete = json['delete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['errCode'] = this.errCode;
    data['errMessage'] = this.errMessage;
    data['id'] = this.id;
    data['docnumber'] = this.docnumber;
    // if (this.customerPojo != null) {
    //   data['customerPojo'] = this.customerPojo!.toJson();
    // }
    data['billdate'] = this.billdate;
    data['startdate'] = this.startdate;
    data['endate'] = this.endate;
    data['duedate'] = this.duedate;
    data['latepaymentdate'] = this.latepaymentdate;
    data['subtotal'] = this.subtotal;
    data['tax'] = this.tax;
    data['discount'] = this.discount;
    data['totalamount'] = this.totalamount;
    data['previousbalance'] = this.previousbalance;
    data['latepaymentfee'] = this.latepaymentfee;
    data['currentpayment'] = this.currentpayment;
    data['currentdebit'] = this.currentdebit;
    data['currentcredit'] = this.currentcredit;
    data['totaldue'] = this.totaldue;
    data['amountinwords'] = this.amountinwords;
    data['dueinwords'] = this.dueinwords;
    data['billrunid'] = this.billrunid;
    data['billrunstatus'] = this.billrunstatus;
    data['document'] = this.document;
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['custType'] = this.custType;
    data['paymentStatus'] = this.paymentStatus;
    data['billableToName'] = this.billableToName;
    data['adjustedAmount'] = this.adjustedAmount;
    if (this.debitDocDetails != null) {
      data['debitDocDetails'] =
          this.debitDocDetails!.map((v) => v.toJson()).toList();
    }
    data['referenceNo'] = this.referenceNo;
    data['purchaseorder_id'] = this.purchaseorderId;
    data['delete'] = this.delete;
    return data;
  }
}


class PlanMappingList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic planId;
  dynamic custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  dynamic startDateString;
  dynamic endDateString;
  dynamic expiryDateString;
  String? status;
  dynamic qospolicyId;
  dynamic uploadqos;
  dynamic downloadqos;
  dynamic uploadts;
  dynamic downloadts;
  dynamic service;
  bool? isDelete;
  dynamic offerPrice;
  dynamic taxAmount;
  dynamic creditdocid;
  dynamic walletBalUsed;
  dynamic purchaseType;
  dynamic onlinePurchaseId;
  dynamic purchaseFrom;
  dynamic debitdocid;
  dynamic validity;
  dynamic planName;
  dynamic discount;
  dynamic plangroupid;
  dynamic planValidityDays;
  bool? isInvoiceToOrg;
  String? billTo;
  dynamic newAmount;
  dynamic renewalId;
  dynamic custRefId;
  dynamic serialNumber;
  dynamic custRefName;
  dynamic expiry;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  dynamic graceDays;
  dynamic custServiceMappingId;
  dynamic plangroup;
  dynamic serviceId;
  dynamic ezyBillServiceId;
  dynamic oldDiscount;
  dynamic remarks;
  dynamic invoiceType;
  dynamic traildebitdocid;
  dynamic isTrialValidityDays;
  dynamic trialPlanValidityCount;
  dynamic ezBillPackageId;
  dynamic casId;
  dynamic invoiceformat;
  dynamic billableCustomerId;
  dynamic unitsOfValidity;
  dynamic extendValidityremarks;
  dynamic extendDate;
  String? discountType;
  dynamic discountExpiryDate;
  dynamic startServiceDate;
  dynamic cprIdForPromiseToPay;
  bool? isHold;
  bool? isVoid;
  dynamic isContainsCustomerInvoice;
  dynamic customerCpr;
  dynamic serviceParamName;

  PlanMappingList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.planId,
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
        this.serialNumber,
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
        this.extendDate,
        this.discountType,
        this.discountExpiryDate,
        this.startServiceDate,
        this.cprIdForPromiseToPay,
        this.isHold,
        this.isVoid,
        this.isContainsCustomerInvoice,
        this.customerCpr,
        this.serviceParamName});

  PlanMappingList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planId = json['planId'];
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
    serialNumber = json['serialNumber'];
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
    extendDate = json['extendDate'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    startServiceDate = json['startServiceDate'];
    cprIdForPromiseToPay = json['cprIdForPromiseToPay'];
    isHold = json['isHold'];
    isVoid = json['isVoid'];
    isContainsCustomerInvoice = json['isContainsCustomerInvoice'];
    customerCpr = json['customerCpr'];
    serviceParamName = json['serviceParamName'];
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
    data['serialNumber'] = this.serialNumber;
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
    data['extendDate'] = this.extendDate;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['startServiceDate'] = this.startServiceDate;
    data['cprIdForPromiseToPay'] = this.cprIdForPromiseToPay;
    data['isHold'] = this.isHold;
    data['isVoid'] = this.isVoid;
    data['isContainsCustomerInvoice'] = this.isContainsCustomerInvoice;
    data['customerCpr'] = this.customerCpr;
    data['serviceParamName'] = this.serviceParamName;
    return data;
  }
}

class DebitDocDetails {
  dynamic debitdocdetailid;
  dynamic debitdocumentid;
  dynamic chargeid;
  String? chargename;
  String? description;
  String? chargetype;
  String? chargecycle;
  double? subtotal;
  double? tax;
  dynamic discount;
  double? totalamount;
  String? startdate;
  String? enddate;
  String? prorationtype;
  dynamic noofcycle;
  dynamic planId;

  DebitDocDetails(
      {this.debitdocdetailid,
        this.debitdocumentid,
        this.chargeid,
        this.chargename,
        this.description,
        this.chargetype,
        this.chargecycle,
        this.subtotal,
        this.tax,
        this.discount,
        this.totalamount,
        this.startdate,
        this.enddate,
        this.prorationtype,
        this.noofcycle,
        this.planId});

  DebitDocDetails.fromJson(Map<String, dynamic> json) {
    debitdocdetailid = json['debitdocdetailid'];
    debitdocumentid = json['debitdocumentid'];
    chargeid = json['chargeid'];
    chargename = json['chargename'];
    description = json['description'];
    chargetype = json['chargetype'];
    chargecycle = json['chargecycle'];
    subtotal = json['subtotal'];
    tax = json['tax'];
    discount = json['discount'];
    totalamount = json['totalamount'];
    startdate = json['startdate'];
    enddate = json['enddate'];
    prorationtype = json['prorationtype'];
    noofcycle = json['noofcycle'];
    planId = json['planId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['debitdocdetailid'] = this.debitdocdetailid;
    data['debitdocumentid'] = this.debitdocumentid;
    data['chargeid'] = this.chargeid;
    data['chargename'] = this.chargename;
    data['description'] = this.description;
    data['chargetype'] = this.chargetype;
    data['chargecycle'] = this.chargecycle;
    data['subtotal'] = this.subtotal;
    data['tax'] = this.tax;
    data['discount'] = this.discount;
    data['totalamount'] = this.totalamount;
    data['startdate'] = this.startdate;
    data['enddate'] = this.enddate;
    data['prorationtype'] = this.prorationtype;
    data['noofcycle'] = this.noofcycle;
    data['planId'] = this.planId;
    return data;
  }
}

class PageDetails {
  dynamic totalPages;
  dynamic totalRecords;
  dynamic totalRecordsPerPage;
  dynamic currentPageNumber;

  PageDetails(
      {this.totalPages,
        this.totalRecords,
        this.totalRecordsPerPage,
        this.currentPageNumber});

  PageDetails.fromJson(Map<String, dynamic> json) {
    totalPages = json['totalPages'];
    totalRecords = json['totalRecords'];
    totalRecordsPerPage = json['totalRecordsPerPage'];
    currentPageNumber = json['currentPageNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['totalPages'] = this.totalPages;
    data['totalRecords'] = this.totalRecords;
    data['totalRecordsPerPage'] = this.totalRecordsPerPage;
    data['currentPageNumber'] = this.currentPageNumber;
    return data;
  }
}
