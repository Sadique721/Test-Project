import 'package:savbill/webservices/base_response.dart';

class PlanDetailResponse extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<PlanDetail>? dataList;
  List<PlanDetail>? data;

  PlanDetailResponse(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList,
      this.data});

  PlanDetailResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <PlanDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PlanDetail.fromJson(v));
      });
    }
    if (json['data'] != null) {
      data = <PlanDetail>[];
      json['data'].forEach((v) {
        data!.add(new PlanDetail.fromJson(v));
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
    if (this.data != null) {
      data['data'] = this.data!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PlanDetail {
  int? planId;
  String? planName;
  int? planmapid;
  int? serviceId;
  int? custPlanMapppingId;
  String? qosPolicyName;
  int? qosPolicyId;
  int? custId;
  num? validity;
  dynamic quotaType;
  dynamic volTotalQuota;
  dynamic volUsedQuota;
  dynamic volQuotaUnit = "";
  dynamic timeTotalQuota;
  dynamic timeUsedQuota;
  dynamic timeQuotaUnit;
  dynamic startDate;
  dynamic endDate;
  dynamic expiryDate;
  dynamic startDateString;
  dynamic expiryDateString;
  dynamic plangroup;
  num? maxsession;
  dynamic service;
  dynamic planstage;
  dynamic childValidity;
  dynamic plangroupid;
  dynamic planGroupName;
  bool? istrialplan;
  bool? isPromiseToPayTaken;
  dynamic custPlanStatus;
  bool? isinvoicestop;
  dynamic nickname;
  dynamic connectionNo;
  dynamic createbyname;
  dynamic createdate;
  bool? isdeleteforVoid;
  dynamic remarks;
  bool? isinvoicestopinpackrel;
  dynamic stopServiceDate;
  String? billTo;
  double? discount;
  String? custPlanCategory;
  String? discountType;
  bool? isDtv;
  bool? isQosv;
  dynamic invoiceType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  dynamic customerServiceMappingId;
  String? discountExpiryDate;
  List<CustomerInventorySerialnumber>?customerInventorySerialnumberDtos;
  dynamic promiseToPayStartDate;
  dynamic promiseToPayEndDate;
  dynamic promiseToPayDays;
  dynamic promiseToPayCount;
  dynamic dbStartDate;
  dynamic dbEndDate;
  dynamic dbExpiryDate;
  dynamic custServMappingStatus;
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
  int? debitdocid;
  dynamic promiseToPayRemarks;
  dynamic qosSpeed;

  PlanDetail(
      {this.planId,
      this.planName,
      this.planmapid,
        this.serviceId,
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
        this.isPromiseToPayTaken,
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
        this.discountExpiryDate,
      this.customerInventorySerialnumberDtos,
        this.promiseToPayStartDate,
        this.promiseToPayEndDate,
        this.promiseToPayDays,
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
        this.promiseToPayRemarks,
        this.qosSpeed});

  PlanDetail.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    planName = json['planName'];
    planmapid = json['planmapid'];
    serviceId = json['serviceId'];
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
    isPromiseToPayTaken = json['isPromiseToPayTaken'];
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
    discountExpiryDate = json['discountExpiryDate'];
    // customerInventorySerialnumberDtos = json['customerInventorySerialnumberDtos'];
    if (json['customerInventorySerialnumberDtos'] != null) {
      customerInventorySerialnumberDtos = <CustomerInventorySerialnumber>[];
      json['customerInventorySerialnumberDtos'].forEach((v) {
        customerInventorySerialnumberDtos!.add(new CustomerInventorySerialnumber.fromJson(v));
      });
    }
    promiseToPayStartDate = json['promiseToPayStartDate'];
    promiseToPayEndDate = json['promiseToPayEndDate'];
    promiseToPayDays = json['promiseToPayDays'];
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
    promiseToPayRemarks = json['promiseToPayRemarks'];
    qosSpeed = json['qosSpeed'];

  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['planName'] = this.planName;
    data['planmapid'] = this.planmapid;
    data['serviceId'] = this.serviceId;
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
    data['isPromiseToPayTaken'] = this.isPromiseToPayTaken;
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
    data['discountExpiryDate'] = this.discountExpiryDate;
    if (this.customerInventorySerialnumberDtos != null) {
      data['customerInventorySerialnumberDtos'] = this
          .customerInventorySerialnumberDtos!
          .map((v) => v.toJson())
          .toList();
    }
    data['promiseToPayStartDate'] = this.promiseToPayStartDate;
    data['promiseToPayEndDate'] = this.promiseToPayEndDate;
    data['promiseToPayDays'] = this.promiseToPayDays;
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
    data['promiseToPayRemarks'] = this.promiseToPayRemarks;
    data['qosSpeed'] = this.qosSpeed;
    return data;
  }
}

class CustomerInventorySerialnumber{

  String? productName;
  int? customerId;
  dynamic serialNumber;
  int? itemId;
  dynamic connectionNo;
  int? custInventoryMappingId;
  dynamic dtvCategory;
  bool? isPrimary;

  CustomerInventorySerialnumber(
  {this.productName,
  this.customerId,
  this.serialNumber,
  this.itemId,
  this.connectionNo,
  this.custInventoryMappingId,
  this.dtvCategory,
  this.isPrimary,
  });

  CustomerInventorySerialnumber.fromJson(Map<String, dynamic> json) {
    productName = json['productName'];
    customerId = json['customerId'];
    serialNumber = json['serialNumber'];
    itemId = json['itemId'];
    connectionNo = json['connectionNo'];
    custInventoryMappingId = json['custInventoryMappingId'];
    dtvCategory = json['dtvCategory'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['productName'] = this.productName;
    data['customerId'] = this.customerId;
    data['serialNumber'] = this.serialNumber;
    data['itemId'] = this.itemId;
    data['connectionNo'] = this.connectionNo;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['dtvCategory'] = this.dtvCategory;
    return data;
  }
}





