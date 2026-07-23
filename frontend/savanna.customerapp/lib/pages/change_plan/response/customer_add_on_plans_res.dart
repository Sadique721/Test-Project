import 'package:savbill/webservices/base_response.dart';

class CustomerAddOnPlanRes extends BaseResponse {
  // int? responseCode;
  String? responseMessage;
  Data? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustomerAddOnPlanRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustomerAddOnPlanRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new Data.fromJson(json['data']) : null;
    dataList = json['dataList'];
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class Data {
  int? id;
  String? name;
  String? username;
  String? cafno;
  String? aadhar;
  String? contactperson;
  String? mobile;
  String? phone;
  String? email;
  String? altemail;
  String? altmobile;
  String? altphone;
  String? fax;
  String? gst;
  String? pan;
  String? address;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? onuid;
  String? stroltname;
  String? strslotname;
  String? strportname;
  String? strconntype;
  dynamic defaultpoolid;
  String? defaultpool;
  String? onlinerenewalflag;
  String? voipenableflag;
  String? mactelflag;
  // List<Null>? macAddressModelList;
  String? acctno;
  int? partnerId;
  String? partnerName;
  dynamic salesRepId;
  dynamic salesRepName;
  List<PlanList>? planList;
  dynamic caseCount;
  dynamic expiryDate;
  dynamic maxSession;
  String? ipAddress;
  String? ipPurDate;
  String? ipExpDate;
  String? voicesrvtype;
  String? didno;
  String? intercomgrp;
  double? outstanding;
  String? childdidno;
  String? intercomno;
  dynamic remarks;
  String? status;
  String? latitude;
  String? longitude;
  String? url;
  String? gisCode;
  String? salesremark;
  String? servicetype;
  dynamic previousCafApprover;
  dynamic nextCafApprover;
  dynamic serviceareaName;
  String? custtype;
  String? passportNo;
  dynamic custPackagId;
  String? password;
  dynamic nextTeamHierarchyMapping;
  dynamic custChargeOverride;

  Data(
      {this.id,
        this.name,
        this.username,
        this.cafno,
        this.aadhar,
        this.contactperson,
        this.mobile,
        this.phone,
        this.email,
        this.altemail,
        this.altmobile,
        this.altphone,
        this.fax,
        this.gst,
        this.pan,
        this.address,
        this.connectivity,
        this.networkDetails,
        this.onuid,
        this.stroltname,
        this.strslotname,
        this.strportname,
        this.strconntype,
        this.defaultpoolid,
        this.defaultpool,
        this.onlinerenewalflag,
        this.voipenableflag,
        this.mactelflag,
        // this.macAddressModelList,
        this.acctno,
        this.partnerId,
        this.partnerName,
        this.salesRepId,
        this.salesRepName,
        this.planList,
        this.caseCount,
        this.expiryDate,
        this.maxSession,
        this.ipAddress,
        this.ipPurDate,
        this.ipExpDate,
        this.voicesrvtype,
        this.didno,
        this.intercomgrp,
        this.outstanding,
        this.childdidno,
        this.intercomno,
        this.remarks,
        this.status,
        this.latitude,
        this.longitude,
        this.url,
        this.gisCode,
        this.salesremark,
        this.servicetype,
        this.previousCafApprover,
        this.nextCafApprover,
        this.serviceareaName,
        this.custtype,
        this.passportNo,
        this.custPackagId,
        this.password,
        this.nextTeamHierarchyMapping,
        this.custChargeOverride});

  Data.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    cafno = json['cafno'];
    aadhar = json['aadhar'];
    contactperson = json['contactperson'];
    mobile = json['mobile'];
    phone = json['phone'];
    email = json['email'];
    altemail = json['altemail'];
    altmobile = json['altmobile'];
    altphone = json['altphone'];
    fax = json['fax'];
    gst = json['gst'];
    pan = json['pan'];
    address = json['address'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    onuid = json['onuid'];
    stroltname = json['stroltname'];
    strslotname = json['strslotname'];
    strportname = json['strportname'];
    strconntype = json['strconntype'];
    defaultpoolid = json['defaultpoolid'];
    defaultpool = json['defaultpool'];
    onlinerenewalflag = json['onlinerenewalflag'];
    voipenableflag = json['voipenableflag'];
    mactelflag = json['mactelflag'];
    // if (json['macAddressModelList'] != null) {
    //   macAddressModelList = <Null>[];
    //   json['macAddressModelList'].forEach((v) {
    //     macAddressModelList!.add(new Null.fromJson(v));
    //   });
    // }
    acctno = json['acctno'];
    partnerId = json['partnerId'];
    partnerName = json['partnerName'];
    salesRepId = json['salesRepId'];
    salesRepName = json['salesRepName'];
    if (json['planList'] != null) {
      planList = <PlanList>[];
      json['planList'].forEach((v) {
        planList!.add(new PlanList.fromJson(v));
      });
    }
    caseCount = json['caseCount'];
    expiryDate = json['expiryDate'];
    maxSession = json['maxSession'];
    ipAddress = json['ipAddress'];
    ipPurDate = json['ipPurDate'];
    ipExpDate = json['ipExpDate'];
    voicesrvtype = json['voicesrvtype'];
    didno = json['didno'];
    intercomgrp = json['intercomgrp'];
    outstanding = json['outstanding'];
    childdidno = json['childdidno'];
    intercomno = json['intercomno'];
    remarks = json['remarks'];
    status = json['status'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    url = json['url'];
    gisCode = json['gis_code'];
    salesremark = json['salesremark'];
    servicetype = json['servicetype'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    serviceareaName = json['serviceareaName'];
    custtype = json['custtype'];
    passportNo = json['passportNo'];
    custPackagId = json['custPackagId'];
    password = json['password'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    custChargeOverride = json['custChargeOverride'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['cafno'] = this.cafno;
    data['aadhar'] = this.aadhar;
    data['contactperson'] = this.contactperson;
    data['mobile'] = this.mobile;
    data['phone'] = this.phone;
    data['email'] = this.email;
    data['altemail'] = this.altemail;
    data['altmobile'] = this.altmobile;
    data['altphone'] = this.altphone;
    data['fax'] = this.fax;
    data['gst'] = this.gst;
    data['pan'] = this.pan;
    data['address'] = this.address;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['onuid'] = this.onuid;
    data['stroltname'] = this.stroltname;
    data['strslotname'] = this.strslotname;
    data['strportname'] = this.strportname;
    data['strconntype'] = this.strconntype;
    data['defaultpoolid'] = this.defaultpoolid;
    data['defaultpool'] = this.defaultpool;
    data['onlinerenewalflag'] = this.onlinerenewalflag;
    data['voipenableflag'] = this.voipenableflag;
    data['mactelflag'] = this.mactelflag;
    // if (this.macAddressModelList != null) {
    //   data['macAddressModelList'] =
    //       this.macAddressModelList!.map((v) => v.toJson()).toList();
    // }
    data['acctno'] = this.acctno;
    data['partnerId'] = this.partnerId;
    data['partnerName'] = this.partnerName;
    data['salesRepId'] = this.salesRepId;
    data['salesRepName'] = this.salesRepName;
    if (this.planList != null) {
      data['planList'] = this.planList!.map((v) => v.toJson()).toList();
    }
    data['caseCount'] = this.caseCount;
    data['expiryDate'] = this.expiryDate;
    data['maxSession'] = this.maxSession;
    data['ipAddress'] = this.ipAddress;
    data['ipPurDate'] = this.ipPurDate;
    data['ipExpDate'] = this.ipExpDate;
    data['voicesrvtype'] = this.voicesrvtype;
    data['didno'] = this.didno;
    data['intercomgrp'] = this.intercomgrp;
    data['outstanding'] = this.outstanding;
    data['childdidno'] = this.childdidno;
    data['intercomno'] = this.intercomno;
    data['remarks'] = this.remarks;
    data['status'] = this.status;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['url'] = this.url;
    data['gis_code'] = this.gisCode;
    data['salesremark'] = this.salesremark;
    data['servicetype'] = this.servicetype;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['serviceareaName'] = this.serviceareaName;
    data['custtype'] = this.custtype;
    data['passportNo'] = this.passportNo;
    data['custPackagId'] = this.custPackagId;
    data['password'] = this.password;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['custChargeOverride'] = this.custChargeOverride;
    return data;
  }
}

class NetworkDetails {
  dynamic networkdeviceid;
  int? serviceareaid;
  dynamic slotid;
  dynamic portid;
  String? networkdevicename;
  String? serviceareaname;
  String? slotname;
  String? portname;

  NetworkDetails(
      {this.networkdeviceid,
        this.serviceareaid,
        this.slotid,
        this.portid,
        this.networkdevicename,
        this.serviceareaname,
        this.slotname,
        this.portname});

  NetworkDetails.fromJson(Map<String, dynamic> json) {
    networkdeviceid = json['networkdeviceid'];
    serviceareaid = json['serviceareaid'];
    slotid = json['slotid'];
    portid = json['portid'];
    networkdevicename = json['networkdevicename'];
    serviceareaname = json['serviceareaname'];
    slotname = json['slotname'];
    portname = json['portname'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['networkdeviceid'] = this.networkdeviceid;
    data['serviceareaid'] = this.serviceareaid;
    data['slotid'] = this.slotid;
    data['portid'] = this.portid;
    data['networkdevicename'] = this.networkdevicename;
    data['serviceareaname'] = this.serviceareaname;
    data['slotname'] = this.slotname;
    data['portname'] = this.portname;
    return data;
  }
}

class PlanList {
  int? planId;
  String? planName;
  int? serviceId;
  int? planmapid;
  int? custPlanMapppingId;
  String? qosPolicyName;
  int? qosPolicyId;
  int? custId;
  double? validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  dynamic timeTotalQuota;
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
  int? childValidity;
  dynamic plangroupid;
  dynamic planGroupName;
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
  String? discountExpiryDate;
  dynamic promiseToPayStartDate;
  dynamic promiseToPayEndDate;
  int? promiseToPayDays;
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
  int? debitdocid;
  // List<Null>? customerInventorySerialnumberDtos;
  dynamic promiseToPayRemarks;
  String? qosSpeed;
  dynamic isServiceThroughLead;
  double? offerPrice;
  bool? isAllowOverUsage;
  double? totalReserve;
  bool? isChunkAvailable;
  int? renewalId;
  dynamic isPromiseTopay;

  PlanList(
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
        // this.customerInventorySerialnumberDtos,
        this.promiseToPayRemarks,
        this.qosSpeed,
        this.isServiceThroughLead,
        this.offerPrice,
        this.isAllowOverUsage,
        this.totalReserve,
        this.isChunkAvailable,
        this.renewalId,
        this.isPromiseTopay});

  PlanList.fromJson(Map<String, dynamic> json) {
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
    // if (json['customerInventorySerialnumberDtos'] != null) {
    //   customerInventorySerialnumberDtos = <Null>[];
    //   json['customerInventorySerialnumberDtos'].forEach((v) {
    //     customerInventorySerialnumberDtos!.add(new Null.fromJson(v));
    //   });
    // }
    promiseToPayRemarks = json['promiseToPayRemarks'];
    qosSpeed = json['qosSpeed'];
    isServiceThroughLead = json['isServiceThroughLead'];
    offerPrice = json['offerPrice'];
    isAllowOverUsage = json['isAllowOverUsage'];
    totalReserve = json['totalReserve'];
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
    // if (this.customerInventorySerialnumberDtos != null) {
    //   data['customerInventorySerialnumberDtos'] = this
    //       .customerInventorySerialnumberDtos!
    //       .map((v) => v.toJson())
    //       .toList();
    // }
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
