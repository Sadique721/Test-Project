import 'package:savbill/webservices/base_response.dart';

class ActivePlanListRes {
  dynamic responseCode;
  String? responseMessage;

  // Null? data;
  List<ActivePlanListDataList>? dataList;

  // Null? excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  ActivePlanListRes(
      {this.responseCode,
      this.responseMessage,
      // this.data,
      this.dataList,
      // this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  ActivePlanListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    // data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ActivePlanListDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ActivePlanListDataList.fromJson(v));
      });
    }
    // excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    // data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ActivePlanListDataList {
  dynamic planId;
  String? planName;
  int? serviceId;
  dynamic planmapid;
  dynamic custPlanMapppingId;
  dynamic qosPolicyName;
  dynamic qosPolicyId;
  dynamic custId;
  dynamic validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  String? timeTotalQuota;
  String? timeUsedQuota;
  dynamic timeQuotaUnit;
  dynamic billablecust;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  dynamic maxsession;
  String? service;
  String? planstage;
  dynamic childValidity;
  dynamic plangroupid;
  String? planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  dynamic nickname;
  String? connectionNo;
  dynamic createbyname;
  String? createdate;
  bool? isdeleteforVoid;
  dynamic remarks;
  dynamic isinvoicestopinpackrel;
  String? stopServiceDate;
  String? billTo;
  dynamic discount;
  String? custPlanCategory;
  String? discountType;
  bool? isDtv;
  bool? isQosv;
  dynamic invoiceType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  dynamic customerServiceMappingId;
  String? serviceEndDate;
  dynamic serviceStartDate;
  dynamic serviceStopDate;
  dynamic discountExpiryDate;
  dynamic promiseToPayStartDate;
  dynamic promiseToPayEndDate;
  dynamic promiseToPayDays;
  bool? isPromiseToPayTaken;
  dynamic promiseToPayCount;
  String? dbStartDate;
  String? dbEndDate;
  String? dbExpiryDate;
  String? custServMappingStatus;
  bool? isChildExists;
  bool? isHold;
  bool? isVoid;
  dynamic remainingDays;
  dynamic extendValidityremarks;
  dynamic serviceHoldBy;
  dynamic serviceStartBy;
  dynamic serviceHoldRemarks;
  dynamic serviceStartRemarks;
  dynamic serviceHoldDate;
  dynamic serviceResumeDate;
  dynamic serviceResumeBy;
  dynamic serviceResumeRemarks;
  dynamic debitdocid;
  List<CustomerInventorySerialnumberDtos>? customerInventorySerialnumberDtos;
  dynamic promiseToPayRemarks;
  dynamic qosSpeed;
  dynamic isServiceThroughLead;
  double? offerPrice;
  bool? isAllowOverUsage;
  double? totalReserve;
  bool? isChunkAvailable;
  dynamic renewalId;
  dynamic isPromiseTopay;

  ActivePlanListDataList(
      {this.planId,
      this.planName,
      this.serviceId,
      this.planmapid,
      this.custPlanMapppingId,
      this.qosPolicyName,
      this.qosPolicyId,
      this.custId,
      this.validity,
      this.quotaType,
      this.volTotalQuota,
      this.volUsedQuota,
      this.volQuotaUnit,
      this.timeTotalQuota,
      this.timeUsedQuota,
      this.timeQuotaUnit,
      this.billablecust,
      this.startDate,
      this.endDate,
      this.expiryDate,
      this.startDateString,
      this.expiryDateString,
      this.plangroup,
      this.maxsession,
      this.service,
      this.planstage,
      this.childValidity,
      this.plangroupid,
      this.planGroupName,
      this.istrialplan,
      this.custPlanStatus,
      this.isinvoicestop,
      this.nickname,
      this.connectionNo,
      this.createbyname,
      this.createdate,
      this.isdeleteforVoid,
      this.remarks,
      this.isinvoicestopinpackrel,
      this.stopServiceDate,
      this.billTo,
      this.discount,
      this.custPlanCategory,
      this.discountType,
      this.isDtv,
      this.isQosv,
      this.invoiceType,
      this.nextTeamHierarchyMappingId,
      this.nextStaff,
      this.customerServiceMappingId,
      this.serviceEndDate,
      this.serviceStartDate,
      this.serviceStopDate,
      this.discountExpiryDate,
      this.promiseToPayStartDate,
      this.promiseToPayEndDate,
      this.promiseToPayDays,
      this.isPromiseToPayTaken,
      this.promiseToPayCount,
      this.dbStartDate,
      this.dbEndDate,
      this.dbExpiryDate,
      this.custServMappingStatus,
      this.isChildExists,
      this.isHold,
      this.isVoid,
      this.remainingDays,
      this.extendValidityremarks,
      this.serviceHoldBy,
      this.serviceStartBy,
      this.serviceHoldRemarks,
      this.serviceStartRemarks,
      this.serviceHoldDate,
      this.serviceResumeDate,
      this.serviceResumeBy,
      this.serviceResumeRemarks,
      this.debitdocid,
      this.customerInventorySerialnumberDtos,
      this.promiseToPayRemarks,
      this.qosSpeed,
      this.isServiceThroughLead,
      this.offerPrice,
      this.isAllowOverUsage,
      this.isChunkAvailable,
      this.isPromiseTopay,
      this.renewalId,
      this.totalReserve});

  ActivePlanListDataList.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    planName = json['planName'];
    serviceId = json['serviceId'];
    planmapid = json['planmapid'];
    custPlanMapppingId = json['custPlanMapppingId'];
    qosPolicyName = json['qosPolicyName'];
    qosPolicyId = json['qosPolicyId'];
    custId = json['custId'];
    validity = json['validity'];
    quotaType = json['quotaType'];
    volTotalQuota = json['volTotalQuota'];
    volUsedQuota = json['volUsedQuota'];
    volQuotaUnit = json['volQuotaUnit'];
    timeTotalQuota = json['timeTotalQuota'];
    timeUsedQuota = json['timeUsedQuota'];
    timeQuotaUnit = json['timeQuotaUnit'];
    billablecust = json['billablecust'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    expiryDate = json['expiryDate'];
    startDateString = json['startDateString'];
    expiryDateString = json['expiryDateString'];
    plangroup = json['plangroup'];
    maxsession = json['maxsession'];
    service = json['service'];
    planstage = json['planstage'];
    childValidity = json['childValidity'];
    plangroupid = json['plangroupid'];
    planGroupName = json['planGroupName'];
    istrialplan = json['istrialplan'];
    custPlanStatus = json['custPlanStatus'];
    isinvoicestop = json['isinvoicestop'];
    nickname = json['nickname'];
    connectionNo = json['connection_no'];
    createbyname = json['createbyname'];
    createdate = json['createdate'];
    isdeleteforVoid = json['isdeleteforVoid'];
    remarks = json['remarks'];
    isinvoicestopinpackrel = json['isinvoicestopinpackrel'];
    stopServiceDate = json['stopServiceDate'];
    billTo = json['billTo'];
    discount = json['discount'];
    custPlanCategory = json['custPlanCategory'];
    discountType = json['discountType'];
    isDtv = json['is_dtv'];
    isQosv = json['is_qosv'];
    invoiceType = json['invoiceType'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    customerServiceMappingId = json['customerServiceMappingId'];
    serviceEndDate = json['serviceEndDate'];
    serviceStartDate = json['serviceStartDate'];
    serviceStopDate = json['serviceStopDate'];
    discountExpiryDate = json['discountExpiryDate'];
    promiseToPayStartDate = json['promiseToPayStartDate'];
    promiseToPayEndDate = json['promiseToPayEndDate'];
    promiseToPayDays = json['promiseToPayDays'];
    isPromiseToPayTaken = json['isPromiseToPayTaken'];
    promiseToPayCount = json['promiseToPayCount'];
    dbStartDate = json['dbStartDate'];
    dbEndDate = json['dbEndDate'];
    dbExpiryDate = json['dbExpiryDate'];
    custServMappingStatus = json['custServMappingStatus'];
    isChildExists = json['isChildExists'];
    isHold = json['isHold'];
    isVoid = json['isVoid'];
    remainingDays = json['remainingDays'];
    extendValidityremarks = json['extendValidityremarks'];
    serviceHoldBy = json['serviceHoldBy'];
    serviceStartBy = json['serviceStartBy'];
    serviceHoldRemarks = json['serviceHoldRemarks'];
    serviceStartRemarks = json['serviceStartRemarks'];
    serviceHoldDate = json['serviceHoldDate'];
    serviceResumeDate = json['serviceResumeDate'];
    serviceResumeBy = json['serviceResumeBy'];
    serviceResumeRemarks = json['serviceResumeRemarks'];
    debitdocid = json['debitdocid'];
    if (json['customerInventorySerialnumberDtos'] != null) {
      customerInventorySerialnumberDtos = <CustomerInventorySerialnumberDtos>[];
      json['customerInventorySerialnumberDtos'].forEach((v) {
        customerInventorySerialnumberDtos!
            .add(new CustomerInventorySerialnumberDtos.fromJson(v));
      });
    }
    promiseToPayRemarks = json['promiseToPayRemarks'];
    qosSpeed = json['qosSpeed'];
    isServiceThroughLead = json['isServiceThroughLead'];
    offerPrice = json['offerPrice'];
    isAllowOverUsage = json['isAllowOverUsage'];
    isChunkAvailable = json['isChunkAvailable'];
    renewalId = json['renewalId'];
    isPromiseTopay = json['isPromiseTopay'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['planName'] = this.planName;
    data['serviceId'] = this.serviceId;
    data['planmapid'] = this.planmapid;
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['qosPolicyName'] = this.qosPolicyName;
    data['qosPolicyId'] = this.qosPolicyId;
    data['custId'] = this.custId;
    data['validity'] = this.validity;
    data['quotaType'] = this.quotaType;
    data['volTotalQuota'] = this.volTotalQuota;
    data['volUsedQuota'] = this.volUsedQuota;
    data['volQuotaUnit'] = this.volQuotaUnit;
    data['timeTotalQuota'] = this.timeTotalQuota;
    data['timeUsedQuota'] = this.timeUsedQuota;
    data['timeQuotaUnit'] = this.timeQuotaUnit;
    data['billablecust'] = this.billablecust;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['expiryDate'] = this.expiryDate;
    data['startDateString'] = this.startDateString;
    data['expiryDateString'] = this.expiryDateString;
    data['plangroup'] = this.plangroup;
    data['maxsession'] = this.maxsession;
    data['service'] = this.service;
    data['planstage'] = this.planstage;
    data['childValidity'] = this.childValidity;
    data['plangroupid'] = this.plangroupid;
    data['planGroupName'] = this.planGroupName;
    data['istrialplan'] = this.istrialplan;
    data['custPlanStatus'] = this.custPlanStatus;
    data['isinvoicestop'] = this.isinvoicestop;
    data['nickname'] = this.nickname;
    data['connection_no'] = this.connectionNo;
    data['createbyname'] = this.createbyname;
    data['createdate'] = this.createdate;
    data['isdeleteforVoid'] = this.isdeleteforVoid;
    data['remarks'] = this.remarks;
    data['isinvoicestopinpackrel'] = this.isinvoicestopinpackrel;
    data['stopServiceDate'] = this.stopServiceDate;
    data['billTo'] = this.billTo;
    data['discount'] = this.discount;
    data['custPlanCategory'] = this.custPlanCategory;
    data['discountType'] = this.discountType;
    data['is_dtv'] = this.isDtv;
    data['is_qosv'] = this.isQosv;
    data['invoiceType'] = this.invoiceType;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['customerServiceMappingId'] = this.customerServiceMappingId;
    data['serviceEndDate'] = this.serviceEndDate;
    data['serviceStartDate'] = this.serviceStartDate;
    data['serviceStopDate'] = this.serviceStopDate;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['promiseToPayStartDate'] = this.promiseToPayStartDate;
    data['promiseToPayEndDate'] = this.promiseToPayEndDate;
    data['promiseToPayDays'] = this.promiseToPayDays;
    data['isPromiseToPayTaken'] = this.isPromiseToPayTaken;
    data['promiseToPayCount'] = this.promiseToPayCount;
    data['dbStartDate'] = this.dbStartDate;
    data['dbEndDate'] = this.dbEndDate;
    data['dbExpiryDate'] = this.dbExpiryDate;
    data['custServMappingStatus'] = this.custServMappingStatus;
    data['isChildExists'] = this.isChildExists;
    data['isHold'] = this.isHold;
    data['isVoid'] = this.isVoid;
    data['remainingDays'] = this.remainingDays;
    data['extendValidityremarks'] = this.extendValidityremarks;
    data['serviceHoldBy'] = this.serviceHoldBy;
    data['serviceStartBy'] = this.serviceStartBy;
    data['serviceHoldRemarks'] = this.serviceHoldRemarks;
    data['serviceStartRemarks'] = this.serviceStartRemarks;
    data['serviceHoldDate'] = this.serviceHoldDate;
    data['serviceResumeDate'] = this.serviceResumeDate;
    data['serviceResumeBy'] = this.serviceResumeBy;
    data['serviceResumeRemarks'] = this.serviceResumeRemarks;
    data['debitdocid'] = this.debitdocid;
    if (this.customerInventorySerialnumberDtos != null) {
      data['customerInventorySerialnumberDtos'] = this
          .customerInventorySerialnumberDtos!
          .map((v) => v.toJson())
          .toList();
    }
    data['promiseToPayRemarks'] = this.promiseToPayRemarks;
    data['qosSpeed'] = this.qosSpeed;
    data['isServiceThroughLead'] = this.isServiceThroughLead;
    data['offerPrice'] = this.offerPrice;
    data['isAllowOverUsage'] = this.isAllowOverUsage;
    data['totalReserve'] = this.totalReserve;
    data['isChunkAvailable'] = this.isChunkAvailable;
    data['renewalId'] = this.renewalId;
    data['isPromiseTopay'] = this.isPromiseTopay;
    return data;
  }
}

class CustomerInventorySerialnumberDtos {
  dynamic id;
  dynamic productName;
  dynamic customerId;
  dynamic serialNumber;
  dynamic itemId;
  dynamic connectionNo;
  dynamic custInventoryMappingId;
  dynamic dtvCategory;
  bool? primary;
  dynamic mvnoId;
  dynamic identityKey;

  CustomerInventorySerialnumberDtos(
      {this.id,
      this.productName,
      this.customerId,
      this.serialNumber,
      this.itemId,
      this.connectionNo,
      this.custInventoryMappingId,
      this.dtvCategory,
      this.primary,
      this.mvnoId,
      this.identityKey});

  CustomerInventorySerialnumberDtos.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productName = json['productName'];
    customerId = json['customerId'];
    serialNumber = json['serialNumber'];
    itemId = json['itemId'];
    connectionNo = json['connectionNo'];
    custInventoryMappingId = json['custInventoryMappingId'];
    dtvCategory = json['dtvCategory'];
    primary = json['primary'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productName'] = this.productName;
    data['customerId'] = this.customerId;
    data['serialNumber'] = this.serialNumber;
    data['itemId'] = this.itemId;
    data['connectionNo'] = this.connectionNo;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['dtvCategory'] = this.dtvCategory;
    data['primary'] = this.primary;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
