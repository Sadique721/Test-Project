/*class PlanMappingDetail {
  dynamic id;
  dynamic planId;
  String? planName;
  String? service;
  String? billTo;

  PlanMappingDetail({this.id, this.planName, this.service,this.planId
  ,this.billTo});

  PlanMappingDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    planId = json['planId'];
    planName = json['planName'];
    service = json['service'];
    billTo = json['billTo'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['planId'] = this.planId;
    data['planName'] = this.planName;
    data['service'] = this.service;
    data['billTo'] = this.billTo;
    return data;
  }
}*/

class PlanMappingDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic planId;
  dynamic custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? endDateString;
  String? expiryDateString;
  String? status;
  dynamic qospolicyId;
  dynamic uploadqos;
  dynamic downloadqos;
  dynamic uploadts;
  dynamic downloadts;
  // List<QuotaList>? quotaList;
  String? service;
  bool? isDelete;
  dynamic offerPrice;
  dynamic taxAmount;
  dynamic creditdocid;
  dynamic walletBalUsed;
  dynamic purchaseType;
  dynamic onlinePurchaseId;
  String? purchaseFrom;
  dynamic debitdocid;
  dynamic validity;
  String? planName;
  dynamic discount;
  dynamic plangroupid;
  dynamic planValidityDays;
  bool? isInvoiceToOrg;
  String? billTo;
  dynamic newAmount;
  dynamic renewalId;
  dynamic custRefId;
  String? custRefName;
  dynamic expiry;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  dynamic graceDays;
  dynamic custServiceMappingId;
  String? plangroup;
  dynamic serviceId;
  dynamic ezyBillServiceId;
  dynamic oldDiscount;
  String? remarks;
  String? invoiceType;
  dynamic traildebitdocid;
  dynamic isTrialValidityDays;
  dynamic trialPlanValidityCount;
  dynamic ezBillPackageId;
  dynamic casId;
  dynamic invoiceformat;
  dynamic billableCustomerId;
  String? unitsOfValidity;
  dynamic extendValidityremarks;
  // LinkAcceptanceDTO? linkAcceptanceDTO;
  dynamic extendDate;
  String? discountType;
  dynamic discountExpiryDate;
  dynamic startServiceDate;
  dynamic cprIdForPromiseToPay;
  bool? isHold;
  dynamic isVoid;
  dynamic isContainsCustomerInvoice;
  dynamic customerCpr;

  PlanMappingDetail(
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
      // this.quotaList,
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
      // this.linkAcceptanceDTO,
      this.extendDate,
      this.discountType,
      this.discountExpiryDate,
      this.startServiceDate,
      this.cprIdForPromiseToPay,
      this.isHold,
      this.isVoid,
      this.isContainsCustomerInvoice,
      this.customerCpr});

  PlanMappingDetail.fromJson(Map<String, dynamic> json) {
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
    // if (json['quotaList'] != null) {
    //   quotaList = <QuotaList>[];
    //   json['quotaList'].forEach((v) {
    //     quotaList!.add(new QuotaList.fromJson(v));
    //   });
    // }
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
    // linkAcceptanceDTO = json['linkAcceptanceDTO'] != null
    //     ? new LinkAcceptanceDTO.fromJson(json['linkAcceptanceDTO'])
    //     : null;
    extendDate = json['extendDate'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    startServiceDate = json['startServiceDate'];
    cprIdForPromiseToPay = json['cprIdForPromiseToPay'];
    isHold = json['isHold'];
    isVoid = json['isVoid'];
    isContainsCustomerInvoice = json['isContainsCustomerInvoice'];
    customerCpr = json['customerCpr'];
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
    // if (this.quotaList != null) {
    //   data['quotaList'] = this.quotaList!.map((v) => v.toJson()).toList();
    // }
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
    // if (this.linkAcceptanceDTO != null) {
    //   data['linkAcceptanceDTO'] = this.linkAcceptanceDTO!.toJson();
    // }
    data['extendDate'] = this.extendDate;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['startServiceDate'] = this.startServiceDate;
    data['cprIdForPromiseToPay'] = this.cprIdForPromiseToPay;
    data['isHold'] = this.isHold;
    data['isVoid'] = this.isVoid;
    data['isContainsCustomerInvoice'] = this.isContainsCustomerInvoice;
    data['customerCpr'] = this.customerCpr;
    return data;
  }
}

class QuotaList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  Null? planGroup;
  dynamic id;
  dynamic planId;
  String? quotaType;
  dynamic totalQuota;
  dynamic usedQuota;
  String? quotaUnit;
  dynamic timeTotalQuota;
  dynamic timeQuotaUsed;
  String? timeQuotaUnit;
  bool? isDelete;
  dynamic totalQuotaKB;
  dynamic usedQuotaKB;
  dynamic timeUsedQuotaSec;
  dynamic timeTotalQuotaSec;
  Null? didtotalquota;
  Null? didusedquota;
  Null? intercomtotalquota;
  Null? intercomusedquota;
  Null? didQuotaUnit;
  Null? intercomQuotaUnit;
  Null? planName;
  Null? cprId;

  QuotaList(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.planGroup,
      this.id,
      this.planId,
      this.quotaType,
      this.totalQuota,
      this.usedQuota,
      this.quotaUnit,
      this.timeTotalQuota,
      this.timeQuotaUsed,
      this.timeQuotaUnit,
      this.isDelete,
      this.totalQuotaKB,
      this.usedQuotaKB,
      this.timeUsedQuotaSec,
      this.timeTotalQuotaSec,
      this.didtotalquota,
      this.didusedquota,
      this.intercomtotalquota,
      this.intercomusedquota,
      this.didQuotaUnit,
      this.intercomQuotaUnit,
      this.planName,
      this.cprId});

  QuotaList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    planGroup = json['planGroup'];
    id = json['id'];
    planId = json['planId'];
    quotaType = json['quotaType'];
    totalQuota = json['totalQuota'];
    usedQuota = json['usedQuota'];
    quotaUnit = json['quotaUnit'];
    timeTotalQuota = json['timeTotalQuota'];
    timeQuotaUsed = json['timeQuotaUsed'];
    timeQuotaUnit = json['timeQuotaUnit'];
    isDelete = json['isDelete'];
    totalQuotaKB = json['totalQuotaKB'];
    usedQuotaKB = json['usedQuotaKB'];
    timeUsedQuotaSec = json['timeUsedQuotaSec'];
    timeTotalQuotaSec = json['timeTotalQuotaSec'];
    didtotalquota = json['didtotalquota'];
    didusedquota = json['didusedquota'];
    intercomtotalquota = json['intercomtotalquota'];
    intercomusedquota = json['intercomusedquota'];
    didQuotaUnit = json['didQuotaUnit'];
    intercomQuotaUnit = json['intercomQuotaUnit'];
    planName = json['planName'];
    cprId = json['cprId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['planGroup'] = this.planGroup;
    data['id'] = this.id;
    data['planId'] = this.planId;
    data['quotaType'] = this.quotaType;
    data['totalQuota'] = this.totalQuota;
    data['usedQuota'] = this.usedQuota;
    data['quotaUnit'] = this.quotaUnit;
    data['timeTotalQuota'] = this.timeTotalQuota;
    data['timeQuotaUsed'] = this.timeQuotaUsed;
    data['timeQuotaUnit'] = this.timeQuotaUnit;
    data['isDelete'] = this.isDelete;
    data['totalQuotaKB'] = this.totalQuotaKB;
    data['usedQuotaKB'] = this.usedQuotaKB;
    data['timeUsedQuotaSec'] = this.timeUsedQuotaSec;
    data['timeTotalQuotaSec'] = this.timeTotalQuotaSec;
    data['didtotalquota'] = this.didtotalquota;
    data['didusedquota'] = this.didusedquota;
    data['intercomtotalquota'] = this.intercomtotalquota;
    data['intercomusedquota'] = this.intercomusedquota;
    data['didQuotaUnit'] = this.didQuotaUnit;
    data['intercomQuotaUnit'] = this.intercomQuotaUnit;
    data['planName'] = this.planName;
    data['cprId'] = this.cprId;
    return data;
  }
}

class LinkAcceptanceDTO {
  Null? id;
  String? circuitName;
  Null? circuitStatus;
  Null? cafNo;
  Null? uploadCAF;
  Null? customerName;
  Null? accountNumber;
  Null? typeOfLink;
  Null? planService;
  Null? linkInstallationDate;
  Null? linkAcceptanceDate;
  Null? purchaseOrderDate;
  Null? partner;
  Null? expiryDate;
  Null? distance;
  Null? distanceUnit;
  Null? bandwidth;
  Null? uploadQOS;
  Null? downloadQOS;
  Null? linkRouterLocation;
  Null? linkPortType;
  Null? linkRouterIP;
  Null? linkPortOnRouter;
  Null? bandwidthType;
  Null? linkRouterName;
  Null? circuitBillingId;
  Null? pop;
  Null? associatedLevel;
  Null? locationLevel1;
  Null? locationLevel2;
  Null? locationLevel3;
  Null? locationLevel4;
  Null? baseStationId1;
  Null? baseStationId2;
  Null? terminationAddress;
  Null? note;
  Null? contactPerson;
  Null? contactPerson1;
  Null? mobileNumber;
  Null? mobileNumber1;
  Null? landLineNumber;
  Null? landLineNumber1;
  Null? emailId;
  Null? emailId1;
  Null? remarks;
  Null? otcChargesFile;
  Null? serviceChargerFile;
  Null? staticOrPooledIP;
  Null? chargeTypeFile;
  Null? billingCycle;
  Null? billingType;
  Null? billable;
  Null? billingGroup;
  Null? payable;
  Null? enableProcessing;
  Null? deposite;
  Null? poNumber;
  Null? billRemark;
  Null? fullName;
  Null? organisation;
  Null? address1;
  Null? address2;
  Null? city;
  Null? zipCode;
  Null? state;
  Null? country;
  bool? isDeleted;
  String? status;
  Null? custId;
  Null? serviceAreaType;
  Null? branch;
  Null? connectionType;
  Null? vlanid;

  LinkAcceptanceDTO(
      {this.id,
      this.circuitName,
      this.circuitStatus,
      this.cafNo,
      this.uploadCAF,
      this.customerName,
      this.accountNumber,
      this.typeOfLink,
      this.planService,
      this.linkInstallationDate,
      this.linkAcceptanceDate,
      this.purchaseOrderDate,
      this.partner,
      this.expiryDate,
      this.distance,
      this.distanceUnit,
      this.bandwidth,
      this.uploadQOS,
      this.downloadQOS,
      this.linkRouterLocation,
      this.linkPortType,
      this.linkRouterIP,
      this.linkPortOnRouter,
      this.bandwidthType,
      this.linkRouterName,
      this.circuitBillingId,
      this.pop,
      this.associatedLevel,
      this.locationLevel1,
      this.locationLevel2,
      this.locationLevel3,
      this.locationLevel4,
      this.baseStationId1,
      this.baseStationId2,
      this.terminationAddress,
      this.note,
      this.contactPerson,
      this.contactPerson1,
      this.mobileNumber,
      this.mobileNumber1,
      this.landLineNumber,
      this.landLineNumber1,
      this.emailId,
      this.emailId1,
      this.remarks,
      this.otcChargesFile,
      this.serviceChargerFile,
      this.staticOrPooledIP,
      this.chargeTypeFile,
      this.billingCycle,
      this.billingType,
      this.billable,
      this.billingGroup,
      this.payable,
      this.enableProcessing,
      this.deposite,
      this.poNumber,
      this.billRemark,
      this.fullName,
      this.organisation,
      this.address1,
      this.address2,
      this.city,
      this.zipCode,
      this.state,
      this.country,
      this.isDeleted,
      this.status,
      this.custId,
      this.serviceAreaType,
      this.branch,
      this.connectionType,
      this.vlanid});

  LinkAcceptanceDTO.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    circuitName = json['circuitName'];
    circuitStatus = json['circuitStatus'];
    cafNo = json['cafNo'];
    uploadCAF = json['uploadCAF'];
    customerName = json['customerName'];
    accountNumber = json['accountNumber'];
    typeOfLink = json['typeOfLink'];
    planService = json['planService'];
    linkInstallationDate = json['linkInstallationDate'];
    linkAcceptanceDate = json['linkAcceptanceDate'];
    purchaseOrderDate = json['purchaseOrderDate'];
    partner = json['partner'];
    expiryDate = json['expiryDate'];
    distance = json['distance'];
    distanceUnit = json['distanceUnit'];
    bandwidth = json['bandwidth'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    linkRouterLocation = json['linkRouterLocation'];
    linkPortType = json['linkPortType'];
    linkRouterIP = json['linkRouterIP'];
    linkPortOnRouter = json['linkPortOnRouter'];
    bandwidthType = json['bandwidthType'];
    linkRouterName = json['linkRouterName'];
    circuitBillingId = json['circuitBillingId'];
    pop = json['pop'];
    associatedLevel = json['associatedLevel'];
    locationLevel1 = json['locationLevel1'];
    locationLevel2 = json['locationLevel2'];
    locationLevel3 = json['locationLevel3'];
    locationLevel4 = json['locationLevel4'];
    baseStationId1 = json['baseStationId1'];
    baseStationId2 = json['baseStationId2'];
    terminationAddress = json['terminationAddress'];
    note = json['note'];
    contactPerson = json['contactPerson'];
    contactPerson1 = json['contactPerson1'];
    mobileNumber = json['mobileNumber'];
    mobileNumber1 = json['mobileNumber1'];
    landLineNumber = json['landLineNumber'];
    landLineNumber1 = json['landLineNumber1'];
    emailId = json['emailId'];
    emailId1 = json['emailId1'];
    remarks = json['remarks'];
    otcChargesFile = json['otcChargesFile'];
    serviceChargerFile = json['serviceChargerFile'];
    staticOrPooledIP = json['staticOrPooledIP'];
    chargeTypeFile = json['chargeTypeFile'];
    billingCycle = json['billingCycle'];
    billingType = json['billingType'];
    billable = json['billable'];
    billingGroup = json['billingGroup'];
    payable = json['payable'];
    enableProcessing = json['enableProcessing'];
    deposite = json['deposite'];
    poNumber = json['poNumber'];
    billRemark = json['billRemark'];
    fullName = json['fullName'];
    organisation = json['organisation'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    zipCode = json['zipCode'];
    state = json['state'];
    country = json['country'];
    isDeleted = json['isDeleted'];
    status = json['status'];
    custId = json['custId'];
    serviceAreaType = json['serviceAreaType'];
    branch = json['branch'];
    connectionType = json['connectionType'];
    vlanid = json['vlanid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['circuitName'] = this.circuitName;
    data['circuitStatus'] = this.circuitStatus;
    data['cafNo'] = this.cafNo;
    data['uploadCAF'] = this.uploadCAF;
    data['customerName'] = this.customerName;
    data['accountNumber'] = this.accountNumber;
    data['typeOfLink'] = this.typeOfLink;
    data['planService'] = this.planService;
    data['linkInstallationDate'] = this.linkInstallationDate;
    data['linkAcceptanceDate'] = this.linkAcceptanceDate;
    data['purchaseOrderDate'] = this.purchaseOrderDate;
    data['partner'] = this.partner;
    data['expiryDate'] = this.expiryDate;
    data['distance'] = this.distance;
    data['distanceUnit'] = this.distanceUnit;
    data['bandwidth'] = this.bandwidth;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['linkRouterLocation'] = this.linkRouterLocation;
    data['linkPortType'] = this.linkPortType;
    data['linkRouterIP'] = this.linkRouterIP;
    data['linkPortOnRouter'] = this.linkPortOnRouter;
    data['bandwidthType'] = this.bandwidthType;
    data['linkRouterName'] = this.linkRouterName;
    data['circuitBillingId'] = this.circuitBillingId;
    data['pop'] = this.pop;
    data['associatedLevel'] = this.associatedLevel;
    data['locationLevel1'] = this.locationLevel1;
    data['locationLevel2'] = this.locationLevel2;
    data['locationLevel3'] = this.locationLevel3;
    data['locationLevel4'] = this.locationLevel4;
    data['baseStationId1'] = this.baseStationId1;
    data['baseStationId2'] = this.baseStationId2;
    data['terminationAddress'] = this.terminationAddress;
    data['note'] = this.note;
    data['contactPerson'] = this.contactPerson;
    data['contactPerson1'] = this.contactPerson1;
    data['mobileNumber'] = this.mobileNumber;
    data['mobileNumber1'] = this.mobileNumber1;
    data['landLineNumber'] = this.landLineNumber;
    data['landLineNumber1'] = this.landLineNumber1;
    data['emailId'] = this.emailId;
    data['emailId1'] = this.emailId1;
    data['remarks'] = this.remarks;
    data['otcChargesFile'] = this.otcChargesFile;
    data['serviceChargerFile'] = this.serviceChargerFile;
    data['staticOrPooledIP'] = this.staticOrPooledIP;
    data['chargeTypeFile'] = this.chargeTypeFile;
    data['billingCycle'] = this.billingCycle;
    data['billingType'] = this.billingType;
    data['billable'] = this.billable;
    data['billingGroup'] = this.billingGroup;
    data['payable'] = this.payable;
    data['enableProcessing'] = this.enableProcessing;
    data['deposite'] = this.deposite;
    data['poNumber'] = this.poNumber;
    data['billRemark'] = this.billRemark;
    data['fullName'] = this.fullName;
    data['organisation'] = this.organisation;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['zipCode'] = this.zipCode;
    data['state'] = this.state;
    data['country'] = this.country;
    data['isDeleted'] = this.isDeleted;
    data['status'] = this.status;
    data['custId'] = this.custId;
    data['serviceAreaType'] = this.serviceAreaType;
    data['branch'] = this.branch;
    data['connectionType'] = this.connectionType;
    data['vlanid'] = this.vlanid;
    return data;
  }
}
