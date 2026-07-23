class InvoicePaymentListRes {
  List<InvoicePaymentList>? paymentlist;
  String? timestamp;
  dynamic status;

  InvoicePaymentListRes({this.paymentlist, this.timestamp, this.status});

  InvoicePaymentListRes.fromJson(Map<String, dynamic> json) {
    if (json['Paymentlist'] != null) {
      paymentlist = <InvoicePaymentList>[];
      json['Paymentlist'].forEach((v) {
        paymentlist!.add(new InvoicePaymentList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.paymentlist != null) {
      data['Paymentlist'] = this.paymentlist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class InvoicePaymentList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? paymode;
  String? paymentdate;
  Null? chequedate;
  Null? paydetails1;
  Null? paydetails2;
  Null? paydetails3;
  String? paydetails4;
  dynamic amount;
  String? status;
  dynamic approverid;
  String? remarks;
  String? referenceno;
  String? xmldocument;
  bool? isDelete;
  dynamic tdsflag;
  dynamic tdsamount;
  dynamic isReversed;
  dynamic resevrsedDate;
  dynamic resverseDebitdocId;
  dynamic tdsReceived;
  dynamic tdsReceivedDate;
  dynamic tdsCreditDocId;
  dynamic mvnoId;
  dynamic buID;
  dynamic lcoid;
  dynamic invoiceId;
  String? paytype;
  String? type;
  dynamic nextTeamHierarchyMappingId;
  String? reciptNo;
  dynamic paymentreferenceno;
  List<DebitDocumentList>? debitDocumentList;
  double? adjustedAmount;
  dynamic bankManagement;
  dynamic destinationBank;
  dynamic filename;
  dynamic uniquename;
  dynamic barteramount;
  double? abbsAmount;
  dynamic branchname;
  String? onlinesource;
  String? creditdocumentno;
  dynamic ledgerId;
  dynamic remainingAmount;
  dynamic invoiceNumber;
  bool? isSelected;

  InvoicePaymentList(
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
      this.isDelete,
      this.tdsflag,
      this.tdsamount,
      this.isReversed,
      this.resevrsedDate,
      this.resverseDebitdocId,
      this.tdsReceived,
      this.tdsReceivedDate,
      this.tdsCreditDocId,
      this.mvnoId,
      this.buID,
      this.lcoid,
      this.invoiceId,
      this.paytype,
      this.type,
      this.nextTeamHierarchyMappingId,
      this.reciptNo,
      this.paymentreferenceno,
      this.debitDocumentList,
      this.adjustedAmount,
      this.bankManagement,
      this.destinationBank,
      this.filename,
      this.uniquename,
      this.barteramount,
      this.abbsAmount,
      this.branchname,
      this.onlinesource,
      this.creditdocumentno,
      this.ledgerId,
      this.remainingAmount,
      this.invoiceNumber,
      this.isSelected});

  InvoicePaymentList.fromJson(Map<String, dynamic> json) {
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
    isDelete = json['isDelete'];
    tdsflag = json['tdsflag'];
    tdsamount = json['tdsamount'];
    isReversed = json['is_reversed'];
    resevrsedDate = json['resevrsed_date'];
    resverseDebitdocId = json['resverse_debitdoc_id'];
    tdsReceived = json['tds_received'];
    tdsReceivedDate = json['tds_received_date'];
    tdsCreditDocId = json['tds_credit_doc_id'];
    mvnoId = json['mvnoId'];
    buID = json['buID'];
    lcoid = json['lcoid'];
    invoiceId = json['invoiceId'];
    paytype = json['paytype'];
    type = json['type'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    reciptNo = json['reciptNo'];
    paymentreferenceno = json['paymentreferenceno'];
    if (json['debitDocumentList'] != null) {
      debitDocumentList = <DebitDocumentList>[];
      json['debitDocumentList'].forEach((v) {
        debitDocumentList!.add(new DebitDocumentList.fromJson(v));
      });
    }
    adjustedAmount = json['adjustedAmount'];
    bankManagement = json['bankManagement'];
    destinationBank = json['destinationBank'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    barteramount = json['barteramount'];
    abbsAmount = json['abbsAmount'];
    branchname = json['branchname'];
    onlinesource = json['onlinesource'];
    creditdocumentno = json['creditdocumentno'];
    ledgerId = json['ledgerId'];
    remainingAmount = json['remainingAmount'];
    invoiceNumber = json['invoiceNumber'];
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
    data['isDelete'] = this.isDelete;
    data['tdsflag'] = this.tdsflag;
    data['tdsamount'] = this.tdsamount;
    data['is_reversed'] = this.isReversed;
    data['resevrsed_date'] = this.resevrsedDate;
    data['resverse_debitdoc_id'] = this.resverseDebitdocId;
    data['tds_received'] = this.tdsReceived;
    data['tds_received_date'] = this.tdsReceivedDate;
    data['tds_credit_doc_id'] = this.tdsCreditDocId;
    data['mvnoId'] = this.mvnoId;
    data['buID'] = this.buID;
    data['lcoid'] = this.lcoid;
    data['invoiceId'] = this.invoiceId;
    data['paytype'] = this.paytype;
    data['type'] = this.type;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['reciptNo'] = this.reciptNo;
    data['paymentreferenceno'] = this.paymentreferenceno;
    if (this.debitDocumentList != null) {
      data['debitDocumentList'] =
          this.debitDocumentList!.map((v) => v.toJson()).toList();
    }
    data['adjustedAmount'] = this.adjustedAmount;
    data['bankManagement'] = this.bankManagement;
    data['destinationBank'] = this.destinationBank;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['barteramount'] = this.barteramount;
    data['abbsAmount'] = this.abbsAmount;
    data['branchname'] = this.branchname;
    data['onlinesource'] = this.onlinesource;
    data['creditdocumentno'] = this.creditdocumentno;
    data['ledgerId'] = this.ledgerId;
    data['remainingAmount'] = this.remainingAmount;
    data['invoiceNumber'] = this.invoiceNumber;
    return data;
  }
}

class DebitDocumentList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  dynamic id;
  String? docnumber;

  // PostpaidPlan? postpaidPlan;
  String? billdate;
  String? localbilldate;
  String? startdate;
  String? localstartdate;
  String? endate;
  String? localenddate;
  String? duedate;
  String? latepaymentdate;
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
  dynamic isCreditReversal;
  dynamic creditDocId;
  String? paymentStatus;
  dynamic adjustedAmount;
  dynamic totalCustomerDiscount;
  dynamic buId;
  dynamic custRefName;
  dynamic inventoryMappingId;
  List<DebitDocumentTAXRels>? debitDocumentTAXRels;
  dynamic custpackrelid;
  dynamic nextStaff;
  dynamic nextTeamHierarchyMappingId;
  dynamic status;
  bool? isDirectChargeInvoice;
  dynamic lcoId;
  String? paymentowner;
  dynamic purchaseorder;
  List<DebitDocDetailsList>? debitDocDetailsList;
  String? billableToName;
  dynamic staffid;
  bool? isPromiseToPayInOldCPR;
  String? promiseToPayHoldDays;
  dynamic promiseStartDate;
  dynamic promiseEndDate;
  bool? isCNEnable;
  dynamic invoiceCancelRemarks;
  dynamic remarks;
  dynamic pendingAmt;

  DebitDocumentList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.docnumber,
      // this.postpaidPlan,
      this.billdate,
      this.localbilldate,
      this.startdate,
      this.localstartdate,
      this.endate,
      this.localenddate,
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
      this.isCreditReversal,
      this.creditDocId,
      this.paymentStatus,
      this.adjustedAmount,
      this.totalCustomerDiscount,
      this.buId,
      this.custRefName,
      this.inventoryMappingId,
      this.debitDocumentTAXRels,
      this.custpackrelid,
      this.nextStaff,
      this.nextTeamHierarchyMappingId,
      this.status,
      this.isDirectChargeInvoice,
      this.lcoId,
      this.paymentowner,
      this.purchaseorder,
      this.debitDocDetailsList,
      this.billableToName,
      this.staffid,
      this.isPromiseToPayInOldCPR,
      this.promiseToPayHoldDays,
      this.promiseStartDate,
      this.promiseEndDate,
      this.isCNEnable,
      this.invoiceCancelRemarks,
      this.remarks,
      this.pendingAmt});

  DebitDocumentList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    docnumber = json['docnumber'];
    // postpaidPlan = json['postpaidPlan'] != null
    //     ? new PostpaidPlan.fromJson(json['postpaidPlan'])
    //     : null;
    billdate = json['billdate'];
    localbilldate = json['localbilldate'];
    startdate = json['startdate'];
    localstartdate = json['localstartdate'];
    endate = json['endate'];
    localenddate = json['localenddate'];
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
    isCreditReversal = json['is_credit_reversal'];
    creditDocId = json['credit_doc_id'];
    paymentStatus = json['paymentStatus'];
    adjustedAmount = json['adjustedAmount'];
    totalCustomerDiscount = json['totalCustomerDiscount'];
    buId = json['buId'];
    custRefName = json['custRefName'];
    inventoryMappingId = json['inventoryMappingId'];
    if (json['debitDocumentTAXRels'] != null) {
      debitDocumentTAXRels = <DebitDocumentTAXRels>[];
      json['debitDocumentTAXRels'].forEach((v) {
        debitDocumentTAXRels!.add(new DebitDocumentTAXRels.fromJson(v));
      });
    }
    custpackrelid = json['custpackrelid'];
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    status = json['status'];
    isDirectChargeInvoice = json['isDirectChargeInvoice'];
    lcoId = json['lcoId'];
    paymentowner = json['paymentowner'];
    purchaseorder = json['purchaseorder'];
    if (json['debitDocDetailsList'] != null) {
      debitDocDetailsList = <DebitDocDetailsList>[];
      json['debitDocDetailsList'].forEach((v) {
        debitDocDetailsList!.add(new DebitDocDetailsList.fromJson(v));
      });
    }
    billableToName = json['billableToName'];
    staffid = json['staffid'];
    isPromiseToPayInOldCPR = json['isPromiseToPayInOldCPR'];
    promiseToPayHoldDays = json['promiseToPayHoldDays'];
    promiseStartDate = json['promiseStartDate'];
    promiseEndDate = json['promiseEndDate'];
    isCNEnable = json['isCNEnable'];
    invoiceCancelRemarks = json['invoiceCancelRemarks'];
    remarks = json['remarks'];
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
    // if (this.postpaidPlan != null) {
    //   data['postpaidPlan'] = this.postpaidPlan!.toJson();
    // }
    data['billdate'] = this.billdate;
    data['localbilldate'] = this.localbilldate;
    data['startdate'] = this.startdate;
    data['localstartdate'] = this.localstartdate;
    data['endate'] = this.endate;
    data['localenddate'] = this.localenddate;
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
    data['is_credit_reversal'] = this.isCreditReversal;
    data['credit_doc_id'] = this.creditDocId;
    data['paymentStatus'] = this.paymentStatus;
    data['adjustedAmount'] = this.adjustedAmount;
    data['totalCustomerDiscount'] = this.totalCustomerDiscount;
    data['buId'] = this.buId;
    data['custRefName'] = this.custRefName;
    data['inventoryMappingId'] = this.inventoryMappingId;
    if (this.debitDocumentTAXRels != null) {
      data['debitDocumentTAXRels'] =
          this.debitDocumentTAXRels!.map((v) => v.toJson()).toList();
    }
    data['custpackrelid'] = this.custpackrelid;
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['status'] = this.status;
    data['isDirectChargeInvoice'] = this.isDirectChargeInvoice;
    data['lcoId'] = this.lcoId;
    data['paymentowner'] = this.paymentowner;
    data['purchaseorder'] = this.purchaseorder;
    if (this.debitDocDetailsList != null) {
      data['debitDocDetailsList'] =
          this.debitDocDetailsList!.map((v) => v.toJson()).toList();
    }
    data['billableToName'] = this.billableToName;
    data['staffid'] = this.staffid;
    data['isPromiseToPayInOldCPR'] = this.isPromiseToPayInOldCPR;
    data['promiseToPayHoldDays'] = this.promiseToPayHoldDays;
    data['promiseStartDate'] = this.promiseStartDate;
    data['promiseEndDate'] = this.promiseEndDate;
    data['isCNEnable'] = this.isCNEnable;
    data['invoiceCancelRemarks'] = this.invoiceCancelRemarks;
    data['remarks'] = this.remarks;
    data['pendingAmt'] = this.pendingAmt;
    return data;
  }
}

class PostpaidPlan {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? displayName;
  String? code;
  String? desc;
  String? category;
  dynamic maxChild;
  String? startDate;
  String? endDate;
  dynamic quota;
  String? quotaUnit;
  dynamic uploadQOS;
  dynamic downloadQOS;
  dynamic uploadTs;
  dynamic downloadTs;
  bool? allowOverUsage;
  String? status;
  String? planStatus;
  dynamic childQuota;
  dynamic childQuotaUnit;
  dynamic slice;
  dynamic sliceUnit;
  dynamic attachedToAllHotSpots;
  dynamic param1;
  dynamic param2;
  dynamic param3;
  dynamic mvnoId;
  dynamic taxId;
  dynamic serviceId;
  dynamic timebasepolicyId;
  String? plantype;
  dynamic dbr;
  List<ChargeList>? chargeList;
  String? planGroup;
  dynamic validity;
  dynamic saccode;
  String? maxconcurrentsession;
  dynamic quotaunittime;
  dynamic quotatime;
  String? quotatype;
  dynamic offerprice;
  dynamic quotadid;
  dynamic quotaintercom;
  dynamic quotaunitdid;
  dynamic quotaunitintercom;
  dynamic qospolicy;
  bool? isDelete;
  dynamic dataCategory;
  dynamic taxamount;
  dynamic serviceName;
  dynamic timebasepolicyName;
  List<ServiceAreaNameList>? serviceAreaNameList;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  dynamic buId;
  dynamic nextTeamHierarchyMapping;
  dynamic nextStaff;
  dynamic newOfferPrice;
  bool? allowdiscount;
  dynamic productId;
  bool? invoiceToOrg;
  bool? requiredApproval;
  dynamic bandwidth;
  dynamic linkType;
  dynamic connectionType;
  dynamic distance;
  dynamic ram;
  dynamic cpu;
  dynamic storage;
  dynamic storageType;
  dynamic autoBackup;
  dynamic cpanel;
  dynamic location;
  dynamic quantity;
  dynamic packageType;
  dynamic numberOfDays;
  dynamic noOfUsers;
  dynamic rackSpace;
  dynamic rackUnit;
  dynamic powerConsumption;
  dynamic networkCard;
  dynamic ipOrIpPool;
  dynamic noOfLicense;
  dynamic noOfEmailUserLicense;
  dynamic noOfServerLicense;
  dynamic noOfUserLicense;
  dynamic noOfNodes;
  dynamic eventPerSecond;
  dynamic noOfAdditionalServer;
  dynamic noOfAdditionalStorage;
  dynamic additionalStorageType;
  dynamic epsLicense;
  dynamic noOfNodesLicense;
  dynamic hardwareResource;
  dynamic manPower;
  dynamic noOfDomains;
  dynamic securityModules;
  dynamic hardwareOrServers;
  dynamic country;
  dynamic noOfVpn;
  dynamic deviceThroughput;
  dynamic retail;
  String? businessType;
  bool? basePlan;
  dynamic templateId;
  dynamic planQosMappingEntityList;
  dynamic accessibility;

  PostpaidPlan(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.displayName,
      this.code,
      this.desc,
      this.category,
      this.maxChild,
      this.startDate,
      this.endDate,
      this.quota,
      this.quotaUnit,
      this.uploadQOS,
      this.downloadQOS,
      this.uploadTs,
      this.downloadTs,
      this.allowOverUsage,
      this.status,
      this.planStatus,
      this.childQuota,
      this.childQuotaUnit,
      this.slice,
      this.sliceUnit,
      this.attachedToAllHotSpots,
      this.param1,
      this.param2,
      this.param3,
      this.mvnoId,
      this.taxId,
      this.serviceId,
      this.timebasepolicyId,
      this.plantype,
      this.dbr,
      this.chargeList,
      this.planGroup,
      this.validity,
      this.saccode,
      this.maxconcurrentsession,
      this.quotaunittime,
      this.quotatime,
      this.quotatype,
      this.offerprice,
      this.quotadid,
      this.quotaintercom,
      this.quotaunitdid,
      this.quotaunitintercom,
      this.qospolicy,
      this.isDelete,
      this.dataCategory,
      this.taxamount,
      this.serviceName,
      this.timebasepolicyName,
      this.serviceAreaNameList,
      this.quotaResetInterval,
      this.mode,
      this.unitsOfValidity,
      this.buId,
      this.nextTeamHierarchyMapping,
      this.nextStaff,
      this.newOfferPrice,
      this.allowdiscount,
      this.productId,
      this.invoiceToOrg,
      this.requiredApproval,
      this.bandwidth,
      this.linkType,
      this.connectionType,
      this.distance,
      this.ram,
      this.cpu,
      this.storage,
      this.storageType,
      this.autoBackup,
      this.cpanel,
      this.location,
      this.quantity,
      this.packageType,
      this.numberOfDays,
      this.noOfUsers,
      this.rackSpace,
      this.rackUnit,
      this.powerConsumption,
      this.networkCard,
      this.ipOrIpPool,
      this.noOfLicense,
      this.noOfEmailUserLicense,
      this.noOfServerLicense,
      this.noOfUserLicense,
      this.noOfNodes,
      this.eventPerSecond,
      this.noOfAdditionalServer,
      this.noOfAdditionalStorage,
      this.additionalStorageType,
      this.epsLicense,
      this.noOfNodesLicense,
      this.hardwareResource,
      this.manPower,
      this.noOfDomains,
      this.securityModules,
      this.hardwareOrServers,
      this.country,
      this.noOfVpn,
      this.deviceThroughput,
      this.retail,
      this.businessType,
      this.basePlan,
      this.templateId,
      this.planQosMappingEntityList,
      this.accessibility});

  PostpaidPlan.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    displayName = json['displayName'];
    code = json['code'];
    desc = json['desc'];
    category = json['category'];
    maxChild = json['maxChild'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    quota = json['quota'];
    quotaUnit = json['quotaUnit'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    uploadTs = json['uploadTs'];
    downloadTs = json['downloadTs'];
    allowOverUsage = json['allowOverUsage'];
    status = json['status'];
    planStatus = json['planStatus'];
    childQuota = json['childQuota'];
    childQuotaUnit = json['childQuotaUnit'];
    slice = json['slice'];
    sliceUnit = json['sliceUnit'];
    attachedToAllHotSpots = json['attachedToAllHotSpots'];
    param1 = json['param1'];
    param2 = json['param2'];
    param3 = json['param3'];
    mvnoId = json['mvnoId'];
    taxId = json['taxId'];
    serviceId = json['serviceId'];
    timebasepolicyId = json['timebasepolicyId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    if (json['chargeList'] != null) {
      chargeList = <ChargeList>[];
      json['chargeList'].forEach((v) {
        chargeList!.add(new ChargeList.fromJson(v));
      });
    }
    planGroup = json['planGroup'];
    validity = json['validity'];
    saccode = json['saccode'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    quotadid = json['quotadid'];
    quotaintercom = json['quotaintercom'];
    quotaunitdid = json['quotaunitdid'];
    quotaunitintercom = json['quotaunitintercom'];
    qospolicy = json['qospolicy'];
    isDelete = json['isDelete'];
    dataCategory = json['dataCategory'];
    taxamount = json['taxamount'];
    serviceName = json['serviceName'];
    timebasepolicyName = json['timebasepolicyName'];
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <ServiceAreaNameList>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
      });
    }
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    nextStaff = json['nextStaff'];
    newOfferPrice = json['newOfferPrice'];
    allowdiscount = json['allowdiscount'];
    productId = json['productId'];
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
    bandwidth = json['bandwidth'];
    linkType = json['link_type'];
    connectionType = json['connection_type'];
    distance = json['distance'];
    ram = json['ram'];
    cpu = json['cpu'];
    storage = json['storage'];
    storageType = json['storage_type'];
    autoBackup = json['auto_backup'];
    cpanel = json['cpanel'];
    location = json['location'];
    quantity = json['quantity'];
    packageType = json['package_type'];
    numberOfDays = json['number_of_days'];
    noOfUsers = json['no_of_users'];
    rackSpace = json['rack_space'];
    rackUnit = json['rack_unit'];
    powerConsumption = json['power_consumption'];
    networkCard = json['network_card'];
    ipOrIpPool = json['ip_or_ip_pool'];
    noOfLicense = json['no_of_license'];
    noOfEmailUserLicense = json['no_of_email_user_license'];
    noOfServerLicense = json['no_of_server_license'];
    noOfUserLicense = json['no_of_user_license'];
    noOfNodes = json['no_of_nodes'];
    eventPerSecond = json['event_per_second'];
    noOfAdditionalServer = json['no_of_additional_server'];
    noOfAdditionalStorage = json['no_of_additional_storage'];
    additionalStorageType = json['additional_storage_type'];
    epsLicense = json['eps_License'];
    noOfNodesLicense = json['no_of_nodes_license'];
    hardwareResource = json['hardware_resource'];
    manPower = json['man_power'];
    noOfDomains = json['no_of_domains'];
    securityModules = json['security_modules'];
    hardwareOrServers = json['hardware_or_servers'];
    country = json['country'];
    noOfVpn = json['no_of_vpn'];
    deviceThroughput = json['device_throughput'];
    retail = json['retail'];
    businessType = json['businessType'];
    basePlan = json['basePlan'];
    templateId = json['templateId'];
    planQosMappingEntityList = json['planQosMappingEntityList'];
    accessibility = json['accessibility'];
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
    data['name'] = this.name;
    data['displayName'] = this.displayName;
    data['code'] = this.code;
    data['desc'] = this.desc;
    data['category'] = this.category;
    data['maxChild'] = this.maxChild;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['quota'] = this.quota;
    data['quotaUnit'] = this.quotaUnit;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['uploadTs'] = this.uploadTs;
    data['downloadTs'] = this.downloadTs;
    data['allowOverUsage'] = this.allowOverUsage;
    data['status'] = this.status;
    data['planStatus'] = this.planStatus;
    data['childQuota'] = this.childQuota;
    data['childQuotaUnit'] = this.childQuotaUnit;
    data['slice'] = this.slice;
    data['sliceUnit'] = this.sliceUnit;
    data['attachedToAllHotSpots'] = this.attachedToAllHotSpots;
    data['param1'] = this.param1;
    data['param2'] = this.param2;
    data['param3'] = this.param3;
    data['mvnoId'] = this.mvnoId;
    data['taxId'] = this.taxId;
    data['serviceId'] = this.serviceId;
    data['timebasepolicyId'] = this.timebasepolicyId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    if (this.chargeList != null) {
      data['chargeList'] = this.chargeList!.map((v) => v.toJson()).toList();
    }
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['saccode'] = this.saccode;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['quotadid'] = this.quotadid;
    data['quotaintercom'] = this.quotaintercom;
    data['quotaunitdid'] = this.quotaunitdid;
    data['quotaunitintercom'] = this.quotaunitintercom;
    data['qospolicy'] = this.qospolicy;
    data['isDelete'] = this.isDelete;
    data['dataCategory'] = this.dataCategory;
    data['taxamount'] = this.taxamount;
    data['serviceName'] = this.serviceName;
    data['timebasepolicyName'] = this.timebasepolicyName;
    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['nextStaff'] = this.nextStaff;
    data['newOfferPrice'] = this.newOfferPrice;
    data['allowdiscount'] = this.allowdiscount;
    data['productId'] = this.productId;
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
    data['bandwidth'] = this.bandwidth;
    data['link_type'] = this.linkType;
    data['connection_type'] = this.connectionType;
    data['distance'] = this.distance;
    data['ram'] = this.ram;
    data['cpu'] = this.cpu;
    data['storage'] = this.storage;
    data['storage_type'] = this.storageType;
    data['auto_backup'] = this.autoBackup;
    data['cpanel'] = this.cpanel;
    data['location'] = this.location;
    data['quantity'] = this.quantity;
    data['package_type'] = this.packageType;
    data['number_of_days'] = this.numberOfDays;
    data['no_of_users'] = this.noOfUsers;
    data['rack_space'] = this.rackSpace;
    data['rack_unit'] = this.rackUnit;
    data['power_consumption'] = this.powerConsumption;
    data['network_card'] = this.networkCard;
    data['ip_or_ip_pool'] = this.ipOrIpPool;
    data['no_of_license'] = this.noOfLicense;
    data['no_of_email_user_license'] = this.noOfEmailUserLicense;
    data['no_of_server_license'] = this.noOfServerLicense;
    data['no_of_user_license'] = this.noOfUserLicense;
    data['no_of_nodes'] = this.noOfNodes;
    data['event_per_second'] = this.eventPerSecond;
    data['no_of_additional_server'] = this.noOfAdditionalServer;
    data['no_of_additional_storage'] = this.noOfAdditionalStorage;
    data['additional_storage_type'] = this.additionalStorageType;
    data['eps_License'] = this.epsLicense;
    data['no_of_nodes_license'] = this.noOfNodesLicense;
    data['hardware_resource'] = this.hardwareResource;
    data['man_power'] = this.manPower;
    data['no_of_domains'] = this.noOfDomains;
    data['security_modules'] = this.securityModules;
    data['hardware_or_servers'] = this.hardwareOrServers;
    data['country'] = this.country;
    data['no_of_vpn'] = this.noOfVpn;
    data['device_throughput'] = this.deviceThroughput;
    data['retail'] = this.retail;
    data['businessType'] = this.businessType;
    data['basePlan'] = this.basePlan;
    data['templateId'] = this.templateId;
    data['planQosMappingEntityList'] = this.planQosMappingEntityList;
    data['accessibility'] = this.accessibility;
    return data;
  }
}

class ChargeList {
  dynamic id;
  Charge? charge;
  dynamic billingCycle;
  String? createdate;
  dynamic chargeprice;
  dynamic chargeName;

  ChargeList(
      {this.id,
      this.charge,
      this.billingCycle,
      this.createdate,
      this.chargeprice,
      this.chargeName});

  ChargeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    charge =
        json['charge'] != null ? new Charge.fromJson(json['charge']) : null;
    billingCycle = json['billingCycle'];
    createdate = json['createdate'];
    chargeprice = json['chargeprice'];
    chargeName = json['chargeName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    if (this.charge != null) {
      data['charge'] = this.charge!.toJson();
    }
    data['billingCycle'] = this.billingCycle;
    data['createdate'] = this.createdate;
    data['chargeprice'] = this.chargeprice;
    data['chargeName'] = this.chargeName;
    return data;
  }
}

class Charge {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? chargetype;
  dynamic price;
  dynamic actualprice;
  Tax? tax;
  dynamic discountid;
  dynamic dbr;
  bool? isDelete;
  String? chargecategory;
  dynamic saccode;
  List<ServiceList>? serviceList;
  dynamic mvnoId;
  dynamic buId;
  dynamic service;
  String? status;
  dynamic ledgerId;
  bool? royaltyPayable;
  dynamic taxamount;
  String? businessType;
  dynamic pushableLedgerId;

  Charge(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.desc,
      this.chargetype,
      this.price,
      this.actualprice,
      this.tax,
      this.discountid,
      this.dbr,
      this.isDelete,
      this.chargecategory,
      this.saccode,
      this.serviceList,
      this.mvnoId,
      this.buId,
      this.service,
      this.status,
      this.ledgerId,
      this.royaltyPayable,
      this.taxamount,
      this.businessType,
      this.pushableLedgerId});

  Charge.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    actualprice = json['actualprice'];
    tax = json['tax'] != null ? new Tax.fromJson(json['tax']) : null;
    discountid = json['discountid'];
    dbr = json['dbr'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    if (json['serviceList'] != null) {
      serviceList = <ServiceList>[];
      json['serviceList'].forEach((v) {
        serviceList!.add(new ServiceList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    service = json['service'];
    status = json['status'];
    ledgerId = json['ledgerId'];
    royaltyPayable = json['royalty_payable'];
    taxamount = json['taxamount'];
    businessType = json['businessType'];
    pushableLedgerId = json['pushableLedgerId'];
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
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    if (this.tax != null) {
      data['tax'] = this.tax!.toJson();
    }
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    if (this.serviceList != null) {
      data['serviceList'] = this.serviceList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['service'] = this.service;
    data['status'] = this.status;
    data['ledgerId'] = this.ledgerId;
    data['royalty_payable'] = this.royaltyPayable;
    data['taxamount'] = this.taxamount;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    return data;
  }
}

class Tax {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? taxtype;
  String? status;
  dynamic mvnoId;
  dynamic buId;
  List<TieredList>? tieredList;
  bool? isDelete;

  Tax(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.desc,
      this.taxtype,
      this.status,
      this.mvnoId,
      this.buId,
      this.tieredList,
      this.isDelete});

  Tax.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    taxtype = json['taxtype'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    if (json['tieredList'] != null) {
      tieredList = <TieredList>[];
      json['tieredList'].forEach((v) {
        tieredList!.add(new TieredList.fromJson(v));
      });
    }
    isDelete = json['isDelete'];
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
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['taxtype'] = this.taxtype;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    if (this.tieredList != null) {
      data['tieredList'] = this.tieredList!.map((v) => v.toJson()).toList();
    }
    data['isDelete'] = this.isDelete;
    return data;
  }
}

class TieredList {
  dynamic id;
  String? name;
  String? taxGroup;
  dynamic rate;
  bool? isDelete;
  bool? beforeDiscount;
  String? taxLedgerId;

  TieredList(
      {this.id,
      this.name,
      this.taxGroup,
      this.rate,
      this.isDelete,
      this.beforeDiscount,
      this.taxLedgerId});

  TieredList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    taxGroup = json['taxGroup'];
    rate = json['rate'];
    isDelete = json['isDelete'];
    beforeDiscount = json['beforeDiscount'];
    taxLedgerId = json['taxLedgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['taxGroup'] = this.taxGroup;
    data['rate'] = this.rate;
    data['isDelete'] = this.isDelete;
    data['beforeDiscount'] = this.beforeDiscount;
    data['taxLedgerId'] = this.taxLedgerId;
    return data;
  }
}

class ServiceList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? serviceName;
  dynamic mvnoId;

  ServiceList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.serviceName,
      this.mvnoId});

  ServiceList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    serviceName = json['serviceName'];
    mvnoId = json['mvnoId'];
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
    data['serviceName'] = this.serviceName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class ServiceAreaNameList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  List<NetworkDevicesList>? networkDevicesList;
  dynamic mvnoId;
  dynamic latitude;
  dynamic longitude;
  dynamic areaId;
  List<PincodeList>? pincodeList;
  dynamic cityid;

  ServiceAreaNameList(
      {this.id,
      this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.name,
      this.status,
      this.isDeleted,
      this.networkDevicesList,
      this.mvnoId,
      this.latitude,
      this.longitude,
      this.areaId,
      this.pincodeList,
      this.cityid});

  ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    if (json['networkDevicesList'] != null) {
      networkDevicesList = <NetworkDevicesList>[];
      json['networkDevicesList'].forEach((v) {
        networkDevicesList!.add(new NetworkDevicesList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    if (json['pincodeList'] != null) {
      pincodeList = <PincodeList>[];
      json['pincodeList'].forEach((v) {
        pincodeList!.add(new PincodeList.fromJson(v));
      });
    }
    cityid = json['cityid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    if (this.networkDevicesList != null) {
      data['networkDevicesList'] =
          this.networkDevicesList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    if (this.pincodeList != null) {
      data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
    }
    data['cityid'] = this.cityid;
    return data;
  }
}

class NetworkDevicesList {
  dynamic id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? name;
  String? devicetype;
  String? status;
  dynamic latitude;
  dynamic longitude;
  dynamic servicearea;
  bool? isDeleted;
  dynamic mvnoId;
  dynamic totalInPorts;
  dynamic availableInPorts;
  dynamic totalOutPorts;
  dynamic availableOutPorts;
  dynamic totalPorts;
  dynamic availablePorts;
  List<int>? serviceAreaNameList;
  dynamic inwardId;
  dynamic itemId;
  dynamic custInventoryId;
  dynamic inventorymappingId;
  dynamic productName;

  NetworkDevicesList(
      {this.id,
      this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.name,
      this.devicetype,
      this.status,
      this.latitude,
      this.longitude,
      this.servicearea,
      this.isDeleted,
      this.mvnoId,
      this.totalInPorts,
      this.availableInPorts,
      this.totalOutPorts,
      this.availableOutPorts,
      this.totalPorts,
      this.availablePorts,
      this.serviceAreaNameList,
      this.inwardId,
      this.itemId,
      this.custInventoryId,
      this.inventorymappingId,
      this.productName});

  NetworkDevicesList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    devicetype = json['devicetype'];
    status = json['status'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    servicearea = json['servicearea'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    totalInPorts = json['totalInPorts'];
    availableInPorts = json['availableInPorts'];
    totalOutPorts = json['totalOutPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalPorts = json['totalPorts'];
    availablePorts = json['availablePorts'];
    serviceAreaNameList = json['serviceAreaNameList'].cast<int>();
    inwardId = json['inwardId'];
    itemId = json['itemId'];
    custInventoryId = json['custInventoryId'];
    inventorymappingId = json['inventorymappingId'];
    productName = json['productName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['devicetype'] = this.devicetype;
    data['status'] = this.status;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['servicearea'] = this.servicearea;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['totalInPorts'] = this.totalInPorts;
    data['availableInPorts'] = this.availableInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalPorts'] = this.totalPorts;
    data['availablePorts'] = this.availablePorts;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    data['inwardId'] = this.inwardId;
    data['itemId'] = this.itemId;
    data['custInventoryId'] = this.custInventoryId;
    data['inventorymappingId'] = this.inventorymappingId;
    data['productName'] = this.productName;
    return data;
  }
}

class PincodeList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic pincode;
  String? status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  List<AreaList>? areaList;
  dynamic mvnoId;

  PincodeList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.pincode,
      this.status,
      this.isDeleted,
      this.countryId,
      this.cityId,
      this.stateId,
      this.areaList,
      this.mvnoId});

  PincodeList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    if (json['areaList'] != null) {
      areaList = <AreaList>[];
      json['areaList'].forEach((v) {
        areaList!.add(new AreaList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
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
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class AreaList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  dynamic mvnoId;
  dynamic primaryKey;
  bool? deleteFlag;

  AreaList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.isDeleted,
      this.countryId,
      this.cityId,
      this.stateId,
      this.mvnoId,
      this.primaryKey,
      this.deleteFlag});

  AreaList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class DebitDocumentTAXRels {
  int? debitdoctaxid;
  int? debitdocumentid;
  int? taxid;
  String? taxname;
  String? description;
  dynamic percentage;
  dynamic taxlevel;
  String? startdate;
  String? enddate;
  dynamic amount;
  dynamic chargeid;
  String? taxLedgerId;

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

class DebitDocDetailsList {
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
  String? startdate;
  String? enddate;
  String? prorationtype;
  dynamic noofcycle;
  String? planId;
  dynamic ledgerId;
  String? icCode;
  dynamic pushableLedgerId;
  dynamic custServiceId;
  dynamic serviceId;
  dynamic discountPercentage;
  dynamic offerPrice;

  DebitDocDetailsList(
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

  DebitDocDetailsList.fromJson(Map<String, dynamic> json) {
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
