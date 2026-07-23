import 'package:savbill/webservices/base_response.dart';

class PaymentListResponse extends BaseResponse {
  List<PaymentDetail>? creditDocumentPojoList;
  PageDetails? pageDetails;

  PaymentListResponse(
      {timestamp, status, message, this.creditDocumentPojoList,this.pageDetails, });

  PaymentListResponse.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    message = json['message'];
    if (json['creditDocumentPojoList'] != null) {
      creditDocumentPojoList = <PaymentDetail>[];
      json['creditDocumentPojoList'].forEach((v) {
        creditDocumentPojoList!.add(new PaymentDetail.fromJson(v));
      });
    }
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['message'] = this.message;
    if (this.creditDocumentPojoList != null) {
      data['creditDocumentPojoList'] =
          this.creditDocumentPojoList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PaymentDetail {
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? paymode;
  String? paymentdate;
  String? chequedate;
  String? paydetails1;
  String? paydetails2;
  String? paydetails3;
  String? paydetails4;
  double? amount;
  String? status;
  int? approverid;
  String? remarks;
  String? referenceno;
  dynamic nextTeamHierarchyMappingId;
  int? custId;
  bool? isDelete;
  String? chequeNo;
  String? bankName;
  String? customerName;
  int? serviceAreaId;
  int? invoiceId;
  bool? delete;
  String? paymentBy;
  String? receiptNo;
  String? filename;
  String? creditdocumentno;
  String? type;
  double? abbsAmount;
  double? tdsamount;
  String? onlinesource;
  dynamic invoiceNumber;
  double? unsettledAmount;
  String? paymentreferenceno;
  double? adjustedAmount;
  String? createdate;

  PaymentDetail(
      {this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.paymode,
      this.paymentdate,
      this.chequedate,
      this.paydetails1,
      this.paydetails2,
      this.paydetails3,
      this.paydetails4,
      this.amount,
      this.status,
      this.approverid,
      this.remarks,
      this.referenceno,
        this.nextTeamHierarchyMappingId,
      this.custId,
      this.isDelete,
      this.chequeNo,
      this.bankName,
      this.customerName,
      this.serviceAreaId,
      this.invoiceId,
      this.delete,
      this.paymentBy,
      this.receiptNo,
      this.filename,
      this.creditdocumentno,
      this.type,
      this.abbsAmount,
      this.tdsamount,
      this.onlinesource,
      this.invoiceNumber,
      this.unsettledAmount,
        this.paymentreferenceno,
        this.adjustedAmount,
        this.createdate,

      });

  PaymentDetail.fromJson(Map<String, dynamic> json) {
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    paymode = json['paymode'];
    paymentdate = json['paymentdate'];
    chequedate = json['chequedate'];
    paydetails1 = json['paydetails1'];
    paydetails2 = json['paydetails2'];
    paydetails3 = json['paydetails3'];
    paydetails4 = json['paydetails4'];
    amount = json['amount'];
    status = json['status'];
    approverid = json['approverid'];
    remarks = json['remarks'];
    referenceno = json['referenceno'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    custId = json['custId'];
    isDelete = json['isDelete'];
    chequeNo = json['chequeNo'];
    bankName = json['bankName'];
    customerName = json['customerName'];
    serviceAreaId = json['serviceAreaId'];
    invoiceId = json['invoiceId'];
    delete = json['delete'];
    paymentBy = json['paymentBy'];
    receiptNo = json['receiptNo'];
    filename = json['filename'];
    creditdocumentno = json['creditdocumentno'];
    type = json['type'];
    abbsAmount = json['abbsAmount'];
    tdsamount = json['tdsamount'];
    onlinesource = json['onlinesource'];
    invoiceNumber = json['invoiceNumber'];
    unsettledAmount = json['unsettledAmount'];
    paymentreferenceno = json['paymentreferenceno'];
    adjustedAmount = json['adjustedAmount'];
    createdate = json['createdate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['paymode'] = this.paymode;
    data['paymentdate'] = this.paymentdate;
    data['chequedate'] = this.chequedate;
    data['paydetails1'] = this.paydetails1;
    data['paydetails2'] = this.paydetails2;
    data['paydetails3'] = this.paydetails3;
    data['paydetails4'] = this.paydetails4;
    data['amount'] = this.amount;
    data['status'] = this.status;
    data['approverid'] = this.approverid;
    data['remarks'] = this.remarks;
    data['referenceno'] = this.referenceno;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['custId'] = this.custId;
    data['isDelete'] = this.isDelete;
    data['chequeNo'] = this.chequeNo;
    data['bankName'] = this.bankName;
    data['customerName'] = this.customerName;
    data['serviceAreaId'] = this.serviceAreaId;
    data['invoiceId'] = this.invoiceId;
    data['delete'] = this.delete;
    data['paymentBy'] = this.paymentBy;
    data['receiptNo'] = this.receiptNo;
    data['filename'] = this.filename;
    data['creditdocumentno'] = this.creditdocumentno;
    data['type'] = this.type;
    data['abbsAmount'] = this.abbsAmount;
    data['tdsamount'] = this.tdsamount;
    data['onlinesource'] = this.onlinesource;
    data['invoiceNumber'] = this.invoiceNumber;
    data['unsettledAmount'] = this.unsettledAmount;
    data['paymentreferenceno'] = this.paymentreferenceno;
    data['adjustedAmount'] = this.adjustedAmount;
    data['createdate'] = this.createdate;
    return data;
  }
}

class PageDetails {
  int? totalPages;
  int? totalRecords;
  int? totalRecordsPerPage;
  int? currentPageNumber;

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
