class CustPlanDetailResponse {
  int? responseCode;
  dynamic responseMessage;
  List<TrialPlanData>? data;
  List<CustPlanDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustPlanDetailResponse(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustPlanDetailResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['data'] != null) {
      data = <TrialPlanData>[];
      json['data'].forEach((v) {
        data!.add(TrialPlanData.fromJson(v));
      });
    }
    if (json['dataList'] != null) {
      dataList = <CustPlanDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(CustPlanDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['responseCode'] = responseCode;
    data['responseMessage'] = responseMessage;

    if (this.data != null) {
      data['data'] = this.data!.map((v) => v.toJson()).toList();
    }
    if (this.dataList != null) {
      data['dataList'] = dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = excelDataList;
    data['totalRecords'] = totalRecords;
    data['pageRecords'] = pageRecords;
    data['currentPageNumber'] = currentPageNumber;
    data['totalPages'] = totalPages;
    return data;
  }
}

class CustPlanDataList {
  int? planId;
  String? planName;
  int? serviceId;
  int? planmapid;
  int? custPlanMapppingId;
  dynamic qosPolicyName;
  dynamic qosPolicyId;
  int? custId;
  num? validity;
  dynamic quotaType;
  dynamic volTotalQuota;
  dynamic volUsedQuota;
  dynamic volQuotaUnit;
  dynamic timeTotalQuota;
  dynamic timeUsedQuota;
  dynamic timeQuotaUnit;
  dynamic billablecust;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  num? maxsession;
  String? service;
  String? planstage;
  int? childValidity;
  dynamic plangroupid;
  dynamic planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  dynamic nickname;
  String? connectionNo;
  String? createbyname;
  String? lastModifiedByName;
  String? createdate;
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
  String? invoiceType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  int? customerServiceMappingId;
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
  List<CustomerInventorySerialnumber>? customerInventorySerialnumberDtos;
  dynamic promiseToPayRemarks;
  dynamic qosSpeed;
  dynamic isServiceThroughLead;
  double? offerPrice;

  CustPlanDataList(
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
        this.lastModifiedByName,
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
        this.offerPrice});

  CustPlanDataList.fromJson(Map<String, dynamic> json) {
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
    lastModifiedByName = json['lastModifiedByName'];
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
      customerInventorySerialnumberDtos = <CustomerInventorySerialnumber>[];
      json['customerInventorySerialnumberDtos'].forEach((v) {
        customerInventorySerialnumberDtos!.add(new CustomerInventorySerialnumber.fromJson(v));
      });
    }
    promiseToPayRemarks = json['promiseToPayRemarks'];
    qosSpeed = json['qosSpeed'];
    isServiceThroughLead = json['isServiceThroughLead'];
    offerPrice = json["offerPrice"];
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
    data['lastModifiedByName'] = this.lastModifiedByName;
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
    return data;
  }
}

class TrialPlanData {
  int? planId;
  String? planName;
  int? serviceId;
  int? planmapid;
  int? custPlanMapppingId;
  String? qosPolicyName;
  dynamic qosPolicyId;
  int? custId;
  num? validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  String? timeTotalQuota;
  String? timeUsedQuota;
  String? timeQuotaUnit;
  dynamic billablecust;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  num? maxsession;
  String? service;
  String? planstage;
  num? childValidity;
  int? plangroupid;
  dynamic planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  dynamic nickname;
  String? connectionNo;
  String? createbyname;
  String? createdate;
  bool? isdeleteforVoid;
  dynamic remarks;
  bool? isinvoicestopinpackrel;
  dynamic stopServiceDate;
  String? billTo;
  num? discount;
  String? custPlanCategory;
  String? discountType;
  bool? isDtv;
  bool? isQosv;
  String? invoiceType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  int? customerServiceMappingId;
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
  dynamic promiseToPayRemarks;
  String? qosSpeed;

  TrialPlanData(
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
        this.promiseToPayRemarks,
        this.qosSpeed});

  TrialPlanData.fromJson(Map<String, dynamic> json) {
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
    promiseToPayRemarks = json['promiseToPayRemarks'];
    qosSpeed = json['qosSpeed'];
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
    data['promiseToPayRemarks'] = this.promiseToPayRemarks;
    data['qosSpeed'] = this.qosSpeed;
    return data;
  }
}

class CustomerInventorySerialnumber{

  String? productName;
  int? customerId;
  String? serialNumber;
  int? itemId;
  String? connectionNo;
  int? custInventoryMappingId;
  String? dtvCategory;
  bool? primary;
  bool? isPrimary;

  CustomerInventorySerialnumber(
      {this.productName,
        this.customerId,
        this.serialNumber,
        this.itemId,
        this.connectionNo,
        this.custInventoryMappingId,
        this.dtvCategory,
        this.primary,
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
    primary = json['primary'];
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
    data['primary'] = this.primary;
    return data;
  }
}
