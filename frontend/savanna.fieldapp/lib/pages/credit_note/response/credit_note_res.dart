class CreditNoteResponse {
  List<CreditNoteDetailsList>? creditDocumentPojoList;
  PageDetails? pageDetails;
  String? timestamp;
  String? message;
  int? status;

  CreditNoteResponse(
      {this.creditDocumentPojoList, this.pageDetails,this.timestamp, this.status,this.message});

  CreditNoteResponse.fromJson(Map<String, dynamic> json) {
    if (json['creditDocumentPojoList'] != null) {
      creditDocumentPojoList = <CreditNoteDetailsList>[];
      json['creditDocumentPojoList'].forEach((v) {
        creditDocumentPojoList!.add(CreditNoteDetailsList.fromJson(v));
      });
    }
    pageDetails = json['pageDetails'] != null
        ? PageDetails.fromJson(json['pageDetails'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
    message = json['message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.creditDocumentPojoList != null) {
      data['creditDocumentPojoList'] =
          this.creditDocumentPojoList!.map((v) => v.toJson()).toList();
    }
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    data['timestamp'] = timestamp;
    data['status'] = status;
    data['message'] = message;
    return data;
  }
}

class CreditNoteDetailsList {
  int? id;
  String? customerName;
  double? amount;
  String? documentno;
  String? invoiceNumber;
  dynamic paydetails2;
  double? tdsamount;
  double? abbsAmount;
  String? referenceno;
  String? paymode;
  String? type;
  String? paymentdate;
  String? status;
  int? approverid;
  dynamic nextTeamHierarchyMappingId;
  String? createbyname;
  String? remarks;
  int? custId;

  CreditNoteDetailsList(
      {this.id,
        this.customerName,
        this.amount,
        this.documentno,
        this.invoiceNumber,
        this.paydetails2,
        this.tdsamount,
        this.abbsAmount,
        this.referenceno,
        this.paymode,
        this.type,
        this.paymentdate,
        this.status,
        this.approverid,
        this.nextTeamHierarchyMappingId,
        this.createbyname,
        this.remarks,
        this.custId});

  CreditNoteDetailsList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    customerName = json['customerName'];
    amount = json['amount'];
    documentno = json['documentno'];
    invoiceNumber = json['invoiceNumber'];
    paydetails2 = json['paydetails2'];
    tdsamount = json['tdsamount'];
    abbsAmount = json['abbsAmount'];
    referenceno = json['referenceno'];
    paymode = json['paymode'];
    type = json['type'];
    paymentdate = json['paymentdate'];
    status = json['status'];
    approverid = json['approverid'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    createbyname = json['createbyname'];
    remarks = json['remarks'];
    custId = json['custId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['customerName'] = this.customerName;
    data['amount'] = this.amount;
    data['documentno'] = this.documentno;
    data['invoiceNumber'] = this.invoiceNumber;
    data['paydetails2'] = this.paydetails2;
    data['tdsamount'] = this.tdsamount;
    data['abbsAmount'] = this.abbsAmount;
    data['referenceno'] = this.referenceno;
    data['paymode'] = this.paymode;
    data['type'] = this.type;
    data['paymentdate'] = this.paymentdate;
    data['status'] = this.status;
    data['approverid'] = this.approverid;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['createbyname'] = this.createbyname;
    data['remarks'] = this.remarks;
    data['custId'] = this.custId;
    return data;
  }
}

/*class CreditNoteDetailsList {
  dynamic createdate;
  dynamic updatedate;
  String? createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? paymode;
  String? paymentdate;
  dynamic chequedate;
  dynamic paydetails1;
  dynamic paydetails2;
  dynamic paydetails3;
  String? paydetails4;
  double? amount;
  String? status;
  int? approverid;
  String? remarks;
  String? referenceno;
  dynamic xmldocument;
  int? custId;
  dynamic reciptNo;
  bool? isDelete;
  dynamic chequeNo;
  dynamic bankName;
  dynamic destinationBank;
  dynamic branch;
  dynamic tdsflag;
  dynamic tdsamount;
  dynamic isReversed;
  dynamic resevrsedDate;
  dynamic resverseDebitdocId;
  dynamic tdsReceived;
  dynamic tdsReceivedDate;
  dynamic tdsCreditDocId;
  dynamic adjustedAmount;
  String? customerName;
  int? serviceAreaId;
  int? invoiceId;
  String? invoiceNumber;
  String? type;
  String? paytype;
  bool? batchAssigned;
  dynamic nextTeamHierarchyMappingId;
  dynamic staff;
  String? documentno;
  dynamic creditdocumentno;
  dynamic paymentreferenceno;
  dynamic mvnoId;
  dynamic lcoId;
  dynamic abbsAmount;
  bool? delete;

  CreditNoteDetailsList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
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
        this.xmldocument,
        this.custId,
        this.reciptNo,
        this.isDelete,
        this.chequeNo,
        this.bankName,
        this.destinationBank,
        this.branch,
        this.tdsflag,
        this.tdsamount,
        this.isReversed,
        this.resevrsedDate,
        this.resverseDebitdocId,
        this.tdsReceived,
        this.tdsReceivedDate,
        this.tdsCreditDocId,
        this.adjustedAmount,
        this.customerName,
        this.serviceAreaId,
        this.invoiceId,
        this.invoiceNumber,
        this.type,
        this.paytype,
        this.batchAssigned,
        this.nextTeamHierarchyMappingId,
        this.staff,
        this.documentno,
        this.creditdocumentno,
        this.paymentreferenceno,
        this.mvnoId,
        this.lcoId,
        this.abbsAmount,
        this.delete});

  CreditNoteDetailsList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
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
    xmldocument = json['xmldocument'];
    custId = json['custId'];
    reciptNo = json['reciptNo'];
    isDelete = json['isDelete'];
    chequeNo = json['chequeNo'];
    bankName = json['bankName'];
    destinationBank = json['destinationBank'];
    branch = json['branch'];
    tdsflag = json['tdsflag'];
    tdsamount = json['tdsamount'];
    isReversed = json['is_reversed'];
    resevrsedDate = json['resevrsed_date'];
    resverseDebitdocId = json['resverse_debitdoc_id'];
    tdsReceived = json['tds_received'];
    tdsReceivedDate = json['tds_received_date'];
    tdsCreditDocId = json['tds_credit_doc_id'];
    adjustedAmount = json['adjustedAmount'];
    customerName = json['customerName'];
    serviceAreaId = json['serviceAreaId'];
    invoiceId = json['invoiceId'];
    invoiceNumber = json['invoiceNumber'];
    type = json['type'];
    paytype = json['paytype'];
    batchAssigned = json['batchAssigned'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    staff = json['staff'];
    documentno = json['documentno'];
    creditdocumentno = json['creditdocumentno'];
    paymentreferenceno = json['paymentreferenceno'];
    mvnoId = json['mvnoId'];
    lcoId = json['lcoId'];
    abbsAmount = json['abbsAmount'];
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
    data['xmldocument'] = this.xmldocument;
    data['custId'] = this.custId;
    data['reciptNo'] = this.reciptNo;
    data['isDelete'] = this.isDelete;
    data['chequeNo'] = this.chequeNo;
    data['bankName'] = this.bankName;
    data['destinationBank'] = this.destinationBank;
    data['branch'] = this.branch;
    data['tdsflag'] = this.tdsflag;
    data['tdsamount'] = this.tdsamount;
    data['is_reversed'] = this.isReversed;
    data['resevrsed_date'] = this.resevrsedDate;
    data['resverse_debitdoc_id'] = this.resverseDebitdocId;
    data['tds_received'] = this.tdsReceived;
    data['tds_received_date'] = this.tdsReceivedDate;
    data['tds_credit_doc_id'] = this.tdsCreditDocId;
    data['adjustedAmount'] = this.adjustedAmount;
    data['customerName'] = this.customerName;
    data['serviceAreaId'] = this.serviceAreaId;
    data['invoiceId'] = this.invoiceId;
    data['invoiceNumber'] = this.invoiceNumber;
    data['type'] = this.type;
    data['paytype'] = this.paytype;
    data['batchAssigned'] = this.batchAssigned;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['staff'] = this.staff;
    data['documentno'] = this.documentno;
    data['creditdocumentno'] = this.creditdocumentno;
    data['paymentreferenceno'] = this.paymentreferenceno;
    data['mvnoId'] = this.mvnoId;
    data['lcoId'] = this.lcoId;
    data['abbsAmount'] = this.abbsAmount;
    data['delete'] = this.delete;
    return data;
  }
}*/


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