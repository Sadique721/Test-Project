import 'package:savbill/webservices/base_response.dart';

class CreditInvoiceListRes extends BaseResponse{
  List<CreditInvoiceList>? invoiceList;
  String? timestamp;
  int? status;

  CreditInvoiceListRes({this.invoiceList, this.timestamp, this.status});

  CreditInvoiceListRes.fromJson(Map<String, dynamic> json) {
    if (json['invoiceList'] != null) {
      invoiceList = <CreditInvoiceList>[];
      json['invoiceList'].forEach((v) {
        invoiceList!.add(new CreditInvoiceList.fromJson(v));
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

class CreditInvoiceList {
  String? createdate;
  dynamic updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  dynamic docnumber;
  dynamic planId;
  dynamic billdate;
  dynamic startdate;
  dynamic endate;
  dynamic duedate;
  dynamic latepaymentdate;
  dynamic subtotal;
  dynamic tax;
  dynamic discount;
  dynamic totalamount;
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
  String? document;
  bool? isDelete;
  dynamic cstchargeid;
  int? custid;
  String? customerName;
  String? custType;
  String? paymentStatus;
  dynamic adjustedAmount;
  String? custRefName;
  String? refundAbleAmount;
  List<DebitDocumentTAXRels>? debitDocumentTAXRels;
  dynamic nextStaff;
  dynamic nextTeamHierarchyMappingId;
  dynamic status;
  List<DebitDocDetails>? debitDocDetails;
  dynamic isDirectChargeInvoice;
  dynamic lcoId;
  String? paymentowner;
  dynamic purchaseorder;
  String? billableToName;
  dynamic debitDocumentInventoryRels;
  bool? selected;

  CreditInvoiceList(
      {this.createdate,
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
        this.custRefName,
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
      this.selected});

  CreditInvoiceList.fromJson(Map<String, dynamic> json) {
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
    custRefName = json['custRefName'];
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

    data['custRefName'] = this.custRefName;
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
    return data;
  }
}

class DebitDocumentTAXRels {
  dynamic debitdoctaxid;
  dynamic debitdocumentid;
  dynamic taxid;
  String? taxname;
  dynamic description;
  dynamic percentage;
  dynamic taxlevel;
  dynamic startdate;
  dynamic enddate;
  dynamic amount;
  dynamic chargeid;
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
  dynamic debitdocdetailid;
  dynamic debitdocumentid;
  dynamic chargeid;
  String? chargename;
  String? description;
  String? chargetype;
  String? chargecycle;
  dynamic subtotal;
  dynamic tax;
  dynamic discount;
  dynamic totalamount;
  dynamic startdate;
  dynamic enddate;
  dynamic prorationtype;
  dynamic noofcycle;
  dynamic planId;
  dynamic ledgerId;
  dynamic icCode;

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
        this.icCode});

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
    return data;
  }
}
