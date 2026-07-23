import 'package:savbill/webservices/base_response.dart';

class CustomerInvoiceDetailRes extends BaseResponse{
  List<DebitDocumentTAXReels>? debitDocumentTAXRels;
  List<DebitDocDetail>? debitDocDetails;
  List<DebitDocumentTAXRelDtos>? debitDocumentTAXRelDtos;
  InvoiceDetails? invoiceDetails;
  String? timestamp;
  int? status;

  CustomerInvoiceDetailRes(
      {this.debitDocumentTAXRels,
        this.debitDocDetails,
        this.debitDocumentTAXRelDtos,
        this.invoiceDetails,
        this.timestamp,
        this.status});

  CustomerInvoiceDetailRes.fromJson(Map<String, dynamic> json) {
    if (json['debitDocumentTAXRels'] != null) {
      debitDocumentTAXRels = <DebitDocumentTAXReels>[];
      json['debitDocumentTAXRels'].forEach((v) {
        debitDocumentTAXRels!.add(new DebitDocumentTAXReels.fromJson(v));
      });
    }
    if (json['debitDocDetails'] != null) {
      debitDocDetails = <DebitDocDetail>[];
      json['debitDocDetails'].forEach((v) {
        debitDocDetails!.add(new DebitDocDetail.fromJson(v));
      });
    }
    if (json['debitDocumentTAXRelDtos'] != null) {
      debitDocumentTAXRelDtos = <DebitDocumentTAXRelDtos>[];
      json['debitDocumentTAXRelDtos'].forEach((v) {
        debitDocumentTAXRelDtos!.add(new DebitDocumentTAXRelDtos.fromJson(v));
      });
    }
    invoiceDetails = json['invoiceDetails'] != null
        ? new InvoiceDetails.fromJson(json['invoiceDetails'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.debitDocumentTAXRels != null) {
      data['debitDocumentTAXRels'] =
          this.debitDocumentTAXRels!.map((v) => v.toJson()).toList();
    }
    if (this.debitDocDetails != null) {
      data['debitDocDetails'] =
          this.debitDocDetails!.map((v) => v.toJson()).toList();
    }
    if (this.debitDocumentTAXRelDtos != null) {
      data['debitDocumentTAXRelDtos'] =
          this.debitDocumentTAXRelDtos!.map((v) => v.toJson()).toList();
    }
    if (this.invoiceDetails != null) {
      data['invoiceDetails'] = this.invoiceDetails!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class DebitDocumentTAXReels {
  int? debitdoctaxid;
  int? debitdocumentid;
  int? taxid;
  dynamic taxname;
  dynamic description;
  double? percentage;
  double? taxlevel;
  dynamic startdate;
  dynamic enddate;
  double? amount;
  int? chargeid;
  dynamic taxLedgerId;
  int? documentDetailId;
  dynamic chargeAmount;
  dynamic planName;
  dynamic discount;
  dynamic discountAmount;
  dynamic taxTypeTiers;

  DebitDocumentTAXReels(
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
        this.taxLedgerId,
        this.documentDetailId,
        this.chargeAmount,
        this.planName,
        this.discount,
        this.discountAmount,
        this.taxTypeTiers});

  DebitDocumentTAXReels.fromJson(Map<String, dynamic> json) {
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
    documentDetailId = json['documentDetailId'];
    chargeAmount = json['chargeAmount'];
    planName = json['planName'];
    discount = json['discount'];
    discountAmount = json['discountAmount'];
    taxTypeTiers = json['taxTypeTiers'];
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
    data['documentDetailId'] = this.documentDetailId;
    data['chargeAmount'] = this.chargeAmount;
    data['planName'] = this.planName;
    data['discount'] = this.discount;
    data['discountAmount'] = this.discountAmount;
    data['taxTypeTiers'] = this.taxTypeTiers;
    return data;
  }
}

class DebitDocDetail {
  int? debitdocdetailid;
  int? debitdocumentid;
  int? chargeid;
  dynamic chargename;
  dynamic description;
  dynamic chargetype;
  dynamic chargecycle;
  double? subtotal;
  double? tax;
  double? discount;
  double? totalamount;
  dynamic startdate;
  dynamic enddate;
  dynamic prorationtype;
  int? noofcycle;
  dynamic planId;
  dynamic ledgerId;
  dynamic icCode;
  dynamic pushableLedgerId;
  int? custServiceId;
  int? serviceId;
  double? discountPercentage;
  dynamic offerPrice;
  int? mvnodebitdocumentid;

  DebitDocDetail(
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
        this.offerPrice,
        this.mvnodebitdocumentid});

  DebitDocDetail.fromJson(Map<String, dynamic> json) {
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
    mvnodebitdocumentid = json['mvnodebitdocumentid'];
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
    data['mvnodebitdocumentid'] = this.mvnodebitdocumentid;
    return data;
  }
}

class DebitDocumentTAXRelDtos {
  dynamic taxname;
  double? percentage;
  double? amount;
  int? chargeId;

  DebitDocumentTAXRelDtos(
      {this.taxname, this.percentage, this.amount, this.chargeId});

  DebitDocumentTAXRelDtos.fromJson(Map<String, dynamic> json) {
    taxname = json['taxname'];
    percentage = json['percentage'];
    amount = json['amount'];
    chargeId = json['chargeId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['taxname'] = this.taxname;
    data['percentage'] = this.percentage;
    data['amount'] = this.amount;
    data['chargeId'] = this.chargeId;
    return data;
  }
}

class InvoiceDetails {
  Null? remarks;
  dynamic lastModifiedByName;
  dynamic operationType;
  int? custid;
  dynamic customerName;
  dynamic paymentStatus;
  dynamic adjustedAmount;
  dynamic nextStaff;
  dynamic nextTeamHierarchyMappingId;
  dynamic billrunstatus;
  dynamic createdate;
  double? totalamount;
  dynamic docnumber;
  dynamic billdate;
  dynamic createdByName;
  dynamic custType;
  dynamic billableToName;
  dynamic billrunid;
  dynamic amountinwords;
  double? discount;
  dynamic latepaymentdate;
  dynamic startdate;
  dynamic endate;
  int? id;
  double? tax;
  String? status;
  dynamic custRefName;
  // List<int>? creditDocId;
  dynamic ispromiseToPayInOldCPR;
  int? promiseToPayHoldDays;
  dynamic promiseStartDate;
  dynamic promiseEndDate;
  dynamic mvnoName;

  InvoiceDetails(
      {this.remarks,
        this.lastModifiedByName,
        this.operationType,
        this.custid,
        this.customerName,
        this.paymentStatus,
        this.adjustedAmount,
        this.nextStaff,
        this.nextTeamHierarchyMappingId,
        this.billrunstatus,
        this.createdate,
        this.totalamount,
        this.docnumber,
        this.billdate,
        this.createdByName,
        this.custType,
        this.billableToName,
        this.billrunid,
        this.amountinwords,
        this.discount,
        this.latepaymentdate,
        this.startdate,
        this.endate,
        this.id,
        this.tax,
        this.status,
        this.custRefName,
        // this.creditDocId,
        this.ispromiseToPayInOldCPR,
        this.promiseToPayHoldDays,
        this.promiseStartDate,
        this.promiseEndDate,
        this.mvnoName});

  InvoiceDetails.fromJson(Map<String, dynamic> json) {
    remarks = json['remarks'];
    lastModifiedByName = json['lastModifiedByName'];
    operationType = json['operationType'];
    custid = json['custid'];
    customerName = json['customerName'];
    paymentStatus = json['paymentStatus'];
    adjustedAmount = json['adjustedAmount'];
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    billrunstatus = json['billrunstatus'];
    createdate = json['createdate'];
    totalamount = json['totalamount'];
    docnumber = json['docnumber'];
    billdate = json['billdate'];
    createdByName = json['createdByName'];
    custType = json['custType'];
    billableToName = json['billableToName'];
    billrunid = json['billrunid'];
    amountinwords = json['amountinwords'];
    discount = json['discount'];
    latepaymentdate = json['latepaymentdate'];
    startdate = json['startdate'];
    endate = json['endate'];
    id = json['id'];
    tax = json['tax'];
    status = json['status'];
    custRefName = json['custRefName'];
    // creditDocId = json['creditDocId'].cast<int>();
    ispromiseToPayInOldCPR = json['ispromiseToPayInOldCPR'];
    promiseToPayHoldDays = json['promiseToPayHoldDays'];
    promiseStartDate = json['promiseStartDate'];
    promiseEndDate = json['promiseEndDate'];
    mvnoName = json['mvnoName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['remarks'] = this.remarks;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['operationType'] = this.operationType;
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['paymentStatus'] = this.paymentStatus;
    data['adjustedAmount'] = this.adjustedAmount;
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['billrunstatus'] = this.billrunstatus;
    data['createdate'] = this.createdate;
    data['totalamount'] = this.totalamount;
    data['docnumber'] = this.docnumber;
    data['billdate'] = this.billdate;
    data['createdByName'] = this.createdByName;
    data['custType'] = this.custType;
    data['billableToName'] = this.billableToName;
    data['billrunid'] = this.billrunid;
    data['amountinwords'] = this.amountinwords;
    data['discount'] = this.discount;
    data['latepaymentdate'] = this.latepaymentdate;
    data['startdate'] = this.startdate;
    data['endate'] = this.endate;
    data['id'] = this.id;
    data['tax'] = this.tax;
    data['status'] = this.status;
    data['custRefName'] = this.custRefName;
    // data['creditDocId'] = this.creditDocId;
    data['ispromiseToPayInOldCPR'] = this.ispromiseToPayInOldCPR;
    data['promiseToPayHoldDays'] = this.promiseToPayHoldDays;
    data['promiseStartDate'] = this.promiseStartDate;
    data['promiseEndDate'] = this.promiseEndDate;
    data['mvnoName'] = this.mvnoName;
    return data;
  }
}
