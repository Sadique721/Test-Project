import 'package:savbill/webservices/base_response.dart';

class PAInvoiceRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<PAInvoice>? dataList;

  PAInvoiceRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  PAInvoiceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <PAInvoice>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PAInvoice.fromJson(v));
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

class PAInvoice {
  String? createdate;
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
  String? dueinwords;
  int? billrunid;
  String? billrunstatus;
  String? document;
  bool? isDelete;
  int? custid;
  String? customerName;
  String? custType;
  String? custRefName;
  String? refundAbleAmount;
  int? nextStaff;
  int? nextTeamHierarchyMappingId;

  PAInvoice(
      {this.createdate,
      this.id,
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
      this.dueinwords,
      this.billrunid,
      this.billrunstatus,
      this.document,
      this.isDelete,
      this.custid,
      this.customerName,
      this.custType,
      this.custRefName,
      this.refundAbleAmount,
      this.nextStaff,
      this.nextTeamHierarchyMappingId});

  PAInvoice.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
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
    dueinwords = json['dueinwords'];
    billrunid = json['billrunid'];
    billrunstatus = json['billrunstatus'];
    document = json['document'];
    isDelete = json['isDelete'];
    custid = json['custid'];
    customerName = json['customerName'];
    custType = json['custType'];
    custRefName = json['custRefName'];
    refundAbleAmount = json['refundAbleAmount'];
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
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
    data['dueinwords'] = this.dueinwords;
    data['billrunid'] = this.billrunid;
    data['billrunstatus'] = this.billrunstatus;
    data['document'] = this.document;
    data['isDelete'] = this.isDelete;
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['custType'] = this.custType;
    data['custRefName'] = this.custRefName;
    data['refundAbleAmount'] = this.refundAbleAmount;
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    return data;
  }
}
