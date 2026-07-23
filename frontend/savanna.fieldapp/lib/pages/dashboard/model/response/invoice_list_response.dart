import 'package:savbill/webservices/base_response.dart';

/*class InvoiceListResponse extends BaseResponse {
  List<InvoiceDetail>? invoiceList;

  InvoiceListResponse({timestamp, status, message, this.invoiceList});

  InvoiceListResponse.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    message = json['message'];
    if (json['invoiceList'] != null) {
      invoiceList = <InvoiceDetail>[];
      json['invoiceList'].forEach((v) {
        invoiceList!.add(new InvoiceDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['message'] = this.message;
    if (this.invoiceList != null) {
      data['invoiceList'] = this.invoiceList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class InvoiceDetail {
  int? id;
  String? docnumber;
  String? billdate;
  String? startdate;
  String? endate;
  String? duedate;
  String? latepaymentdate;
  num? subtotal;
  num? tax;
  num? discount;
  num? totalamount;
  num? previousbalance;
  num? latepaymentfee;
  num? currentdebit;
  num? currentcredit;
  num? totaldue;
  String? amountinwords;
  String? billableToName;
  String? createdByName;
  String? dueinwords;
  num? billrunid;
  String? billrunstatus;
  String? document;
  int? custid;
  String? customerName;
  String? custType;
  String? paymentStatus;
  String? paymentowner;

  int? planId;
  num? currentpayment;
  int? cstchargeid;
  num? adjustedAmount;
  bool? selected;

  InvoiceDetail(
      {this.id,
      this.docnumber,
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
      this.currentdebit,
      this.currentcredit,
      this.totaldue,
      this.amountinwords,
      this.billableToName,
      this.createdByName,
      this.dueinwords,
      this.billrunid,
      this.billrunstatus,
      this.document,
      this.custid,
      this.customerName,
      this.custType,
      this.paymentStatus,
      this.paymentowner,
      this.planId,
      this.currentpayment,
      this.adjustedAmount,
      this.cstchargeid,
      this.selected});

  InvoiceDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    docnumber = json['docnumber'];
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
    currentdebit = json['currentdebit'];
    currentcredit = json['currentcredit'];
    totaldue = json['totaldue'];
    amountinwords = json['amountinwords'];
    billableToName = json['billableToName'];
    createdByName = json['createdByName'];
    dueinwords = json['dueinwords'];
    billrunid = json['billrunid'];
    billrunstatus = json['billrunstatus'];
    document = json['document'];
    custid = json['custid'];
    customerName = json['customerName'];
    custType = json['custType'];
    paymentStatus = json['paymentStatus'];
    paymentowner = json['paymentowner'];

    planId = json['planId'];
    currentpayment = json['currentpayment'];
    adjustedAmount = json['adjustedAmount'];
    cstchargeid = json['cstchargeid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['docnumber'] = this.docnumber;
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
    data['currentdebit'] = this.currentdebit;
    data['currentcredit'] = this.currentcredit;
    data['totaldue'] = this.totaldue;
    data['amountinwords'] = this.amountinwords;
    data['billableToName'] = this.billableToName;
    data['createdByName'] = this.createdByName;
    data['dueinwords'] = this.dueinwords;
    data['billrunid'] = this.billrunid;
    data['billrunstatus'] = this.billrunstatus;
    data['document'] = this.document;
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['custType'] = this.custType;
    data['paymentStatus'] = this.paymentStatus;
    data['paymentowner'] = this.paymentowner;

    data['planId'] = this.planId;
    data['currentpayment'] = this.currentpayment;
    data['adjustedAmount'] = this.adjustedAmount;
    data['cstchargeid'] = this.cstchargeid;
    return data;
  }
}*/

class InvoiceListResponse extends BaseResponse {
  List<InvoiceDetail>? invoiceList;
  String? timestamp;
  int? status;

  InvoiceListResponse({this.invoiceList, this.timestamp, this.status});

  InvoiceListResponse.fromJson(Map<String, dynamic> json) {
    if (json['invoiceList'] != null) {
      invoiceList = <InvoiceDetail>[];
      json['invoiceList'].forEach((v) {
        invoiceList!.add(new InvoiceDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.invoiceList != null) {
      data['invoiceList'] = this.invoiceList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class InvoiceDetail {
  String? createdate;
  dynamic updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? docnumber;
  dynamic planId;
  String? billdate;
  String? startdate;
  String? endate;
  String? duedate;
  String? latepaymentdate;
  double? subtotal;
  double? tax;
  double? discount;
  double? totalamount;
  double? previousbalance;
  double? latepaymentfee;
  double? currentpayment;
  double? currentdebit;
  double? currentcredit;
  double? totaldue;
  String? amountinwords;
  String? dueinwords;
  dynamic billrunid;
  String? billrunstatus;
  String? document;
  bool? isDelete;
  dynamic cstchargeid;
  int? custid;
  String? customerName;
  String? custType;
  String? paymentStatus;
  double? adjustedAmount;

  // List<Null>? creditDocumentList;
  String? custRefName;

  List<dynamic>? creditDocId;
  dynamic refundAbleAmount;
  List<DebitDocumentTAXRels>? debitDocumentTAXRels;
  int? nextStaff;
  dynamic nextTeamHierarchyMappingId;
  String? status;
  List<DebitDocDetails>? debitDocDetails;
  dynamic isDirectChargeInvoice;
  dynamic lcoId;
  String? paymentowner;
  dynamic purchaseorder;
  dynamic billableToName;
  dynamic debitDocumentInventoryRels;
  bool? isPromiseToPayInOldCPR;
  int? promiseToPayHoldDays;
  dynamic promiseStartDate;
  dynamic promiseEndDate;
  bool? isCNEnable;
  dynamic invoiceCancelRemarks;
  double? pendingAmt;
  bool? selected;
  bool? tdsCheck = false;
  bool? abbsCheck = false;
  double? editTotalAmountValue;
  double? testamount;

  InvoiceDetail({
    this.createdate,
    this.updatedate,
    this.createdByName,
    this.lastModifiedByName,
    this.createdById,
    this.lastModifiedById,
    this.id,
    this.docnumber,
    this.planId,
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
    this.isDelete,
    this.cstchargeid,
    this.custid,
    this.customerName,
    this.custType,
    this.paymentStatus,
    this.adjustedAmount,
    // this.creditDocumentList,
    this.custRefName,
    this.creditDocId,
    this.refundAbleAmount,
    this.debitDocumentTAXRels,
    this.nextStaff,
    this.nextTeamHierarchyMappingId,
    this.status,
    this.debitDocDetails,
    this.isDirectChargeInvoice,
    this.lcoId,
    this.paymentowner,
    this.purchaseorder,
    this.billableToName,
    this.debitDocumentInventoryRels,
    this.isPromiseToPayInOldCPR,
    this.promiseToPayHoldDays,
    this.promiseStartDate,
    this.promiseEndDate,
    this.isCNEnable,
    this.invoiceCancelRemarks,
    this.pendingAmt,
    this.selected,
    this.tdsCheck,
    this.abbsCheck,
    this.editTotalAmountValue,
    this.testamount,
  });

  InvoiceDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    docnumber = json['docnumber'];
    planId = json['planId'];
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
    isDelete = json['isDelete'];
    cstchargeid = json['cstchargeid'];
    custid = json['custid'];
    customerName = json['customerName'];
    custType = json['custType'];
    paymentStatus = json['paymentStatus'];
    adjustedAmount = json['adjustedAmount'];
    // if (json['creditDocumentList'] != null) {
    //   creditDocumentList = <Null>[];
    //   json['creditDocumentList'].forEach((v) {
    //     creditDocumentList!.add(new Null.fromJson(v));
    //   });
    // }
    custRefName = json['custRefName'];

    if(json['creditDocId'] != null){
      creditDocId = json['creditDocId'].cast<dynamic>();
    }
    refundAbleAmount = json['refundAbleAmount'];
    if (json['debitDocumentTAXRels'] != null) {
      debitDocumentTAXRels = <DebitDocumentTAXRels>[];
      json['debitDocumentTAXRels'].forEach((v) {
        debitDocumentTAXRels!.add(new DebitDocumentTAXRels.fromJson(v));
      });
    }
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    status = json['status'];
    if (json['debitDocDetails'] != null) {
      debitDocDetails = <DebitDocDetails>[];
      json['debitDocDetails'].forEach((v) {
        debitDocDetails!.add(new DebitDocDetails.fromJson(v));
      });
    }
    isDirectChargeInvoice = json['isDirectChargeInvoice'];
    lcoId = json['lcoId'];
    paymentowner = json['paymentowner'];
    purchaseorder = json['purchaseorder'];
    billableToName = json['billableToName'];
    debitDocumentInventoryRels = json['debitDocumentInventoryRels'];
    isPromiseToPayInOldCPR = json['isPromiseToPayInOldCPR'];
    promiseToPayHoldDays = json['promiseToPayHoldDays'];
    promiseStartDate = json['promiseStartDate'];
    promiseEndDate = json['promiseEndDate'];
    isCNEnable = json['isCNEnable'];
    invoiceCancelRemarks = json['invoiceCancelRemarks'];
    pendingAmt = json['pendingAmt'];
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
    data['docnumber'] = this.docnumber;
    data['planId'] = this.planId;
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
    data['isDelete'] = this.isDelete;
    data['cstchargeid'] = this.cstchargeid;
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['custType'] = this.custType;
    data['paymentStatus'] = this.paymentStatus;
    data['adjustedAmount'] = this.adjustedAmount;
    // if (this.creditDocumentList != null) {
    //   data['creditDocumentList'] =
    //       this.creditDocumentList!.map((v) => v.toJson()).toList();
    // }
    data['custRefName'] = this.custRefName;
    if(this.creditDocId != null) {
      data['creditDocId'] = this.creditDocId;
    }
    data['refundAbleAmount'] = this.refundAbleAmount;
    if (this.debitDocumentTAXRels != null) {
      data['debitDocumentTAXRels'] =
          this.debitDocumentTAXRels!.map((v) => v.toJson()).toList();
    }
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['status'] = this.status;
    if (this.debitDocDetails != null) {
      data['debitDocDetails'] =
          this.debitDocDetails!.map((v) => v.toJson()).toList();
    }
    data['isDirectChargeInvoice'] = this.isDirectChargeInvoice;
    data['lcoId'] = this.lcoId;
    data['paymentowner'] = this.paymentowner;
    data['purchaseorder'] = this.purchaseorder;
    data['billableToName'] = this.billableToName;
    data['debitDocumentInventoryRels'] = this.debitDocumentInventoryRels;
    data['isPromiseToPayInOldCPR'] = this.isPromiseToPayInOldCPR;
    data['promiseToPayHoldDays'] = this.promiseToPayHoldDays;
    data['promiseStartDate'] = this.promiseStartDate;
    data['promiseEndDate'] = this.promiseEndDate;
    data['isCNEnable'] = this.isCNEnable;
    data['invoiceCancelRemarks'] = this.invoiceCancelRemarks;
    data['pendingAmt'] = this.pendingAmt;
    return data;
  }
}

class DebitDocumentTAXRels {
  dynamic debitdoctaxid;
  dynamic debitdocumentid;
  dynamic taxid;
  String? taxname;
  dynamic description;
  double? percentage;
  double? taxlevel;
  dynamic startdate;
  dynamic enddate;
  double? amount;
  String? chargeid;
  dynamic taxLedgerId;

  DebitDocumentTAXRels(
      {this.debitdoctaxid,
      this.debitdocumentid,
      this.taxid,
      this.taxname,
      this.description,
      this.percentage,
      this.taxlevel,
      this.startdate,
      this.enddate,
      this.amount,
      this.chargeid,
      this.taxLedgerId});

  DebitDocumentTAXRels.fromJson(Map<String, dynamic> json) {
    debitdoctaxid = json['debitdoctaxid'];
    debitdocumentid = json['debitdocumentid'];
    taxid = json['taxid'];
    taxname = json['taxname'];
    description = json['description'];
    percentage = json['percentage'];
    taxlevel = json['taxlevel'];
    startdate = json['startdate'];
    enddate = json['enddate'];
    amount = json['amount'];
    chargeid = json['chargeid'];
    taxLedgerId = json['taxLedgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['debitdoctaxid'] = this.debitdoctaxid;
    data['debitdocumentid'] = this.debitdocumentid;
    data['taxid'] = this.taxid;
    data['taxname'] = this.taxname;
    data['description'] = this.description;
    data['percentage'] = this.percentage;
    data['taxlevel'] = this.taxlevel;
    data['startdate'] = this.startdate;
    data['enddate'] = this.enddate;
    data['amount'] = this.amount;
    data['chargeid'] = this.chargeid;
    data['taxLedgerId'] = this.taxLedgerId;
    return data;
  }
}

class DebitDocDetails {
  int? debitdocdetailid;
  int? debitdocumentid;
  int? chargeid;
  String? chargename;
  String? description;
  String? chargetype;
  String? chargecycle;
  double? subtotal;
  double? tax;
  double? discount;
  double? totalamount;
  String? startdate;
  String? enddate;
  String? prorationtype;
  int? noofcycle;
  String? planId;
  dynamic ledgerId;
  String? icCode;
  dynamic pushableLedgerId;
  int? custServiceId;
  dynamic serviceId;
  double? discountPercentage;
  dynamic offerPrice;

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
      this.planId,
      this.ledgerId,
      this.icCode,
      this.pushableLedgerId,
      this.custServiceId,
      this.serviceId,
      this.discountPercentage,
      this.offerPrice});

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
    ledgerId = json['ledgerId'];
    icCode = json['icCode'];
    pushableLedgerId = json['pushableLedgerId'];
    custServiceId = json['custServiceId'];
    serviceId = json['serviceId'];
    discountPercentage = json['discountPercentage'];
    offerPrice = json['offerPrice'];
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
    data['ledgerId'] = this.ledgerId;
    data['icCode'] = this.icCode;
    data['pushableLedgerId'] = this.pushableLedgerId;
    data['custServiceId'] = this.custServiceId;
    data['serviceId'] = this.serviceId;
    data['discountPercentage'] = this.discountPercentage;
    data['offerPrice'] = this.offerPrice;
    return data;
  }
}
