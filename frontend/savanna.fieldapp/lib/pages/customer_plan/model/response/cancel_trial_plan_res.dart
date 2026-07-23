import 'package:savbill/webservices/base_response.dart';

class CancelTrailPlanRes extends BaseResponse {
  TrialPlanResponse? trialPlanResponse;
  String? timestamp;
  int? status;

  CancelTrailPlanRes({this.trialPlanResponse, this.timestamp, this.status});

  CancelTrailPlanRes.fromJson(Map<String, dynamic> json) {
    trialPlanResponse = json['trialPlanResponse'] != null
        ? TrialPlanResponse.fromJson(json['trialPlanResponse'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.trialPlanResponse != null) {
      data['trialPlanResponse'] = this.trialPlanResponse!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class TrialPlanResponse {
  CustomersBasicDetailsPojo? customersBasicDetailsPojo;
  Null? recordpaymentResponseDTO;
  Null? custpackagerelid;
  Null? remarks;

  TrialPlanResponse(
      {this.customersBasicDetailsPojo,
        this.recordpaymentResponseDTO,
        this.custpackagerelid,
        this.remarks});

  TrialPlanResponse.fromJson(Map<String, dynamic> json) {
    customersBasicDetailsPojo = json['customersBasicDetailsPojo'] != null
        ? new CustomersBasicDetailsPojo.fromJson(
        json['customersBasicDetailsPojo'])
        : null;
    recordpaymentResponseDTO = json['recordpaymentResponseDTO'];
    custpackagerelid = json['custpackagerelid'];
    remarks = json['remarks'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customersBasicDetailsPojo != null) {
      data['customersBasicDetailsPojo'] =
          this.customersBasicDetailsPojo!.toJson();
    }
    data['recordpaymentResponseDTO'] = this.recordpaymentResponseDTO;
    data['custpackagerelid'] = this.custpackagerelid;
    data['remarks'] = this.remarks;
    return data;
  }
}

class CustomersBasicDetailsPojo {
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
  Null? defaultpoolid;
  String? defaultpool;
  String? onlinerenewalflag;
  String? voipenableflag;
  String? mactelflag;
  List<Null>? macAddressModelList;
  String? acctno;
  int? partnerId;
  String? partnerName;
  Null? salesRepId;
  Null? salesRepName;
  List<PlanList>? planList;
  Null? caseCount;
  Null? expiryDate;
  Null? maxSession;
  String? ipAddress;
  String? ipPurDate;
  String? ipExpDate;
  String? voicesrvtype;
  String? didno;
  String? intercomgrp;
  int? outstanding;
  String? childdidno;
  String? intercomno;
  Null? remarks;
  String? status;
  String? latitude;
  String? longitude;
  String? url;
  String? gisCode;
  String? salesremark;
  String? servicetype;
  Null? previousCafApprover;
  Null? nextCafApprover;
  Null? serviceareaName;
  String? custtype;
  String? passportNo;
  Null? custPackagId;
  String? password;
  Null? nextTeamHierarchyMapping;

  CustomersBasicDetailsPojo(
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
        this.nextTeamHierarchyMapping});

  CustomersBasicDetailsPojo.fromJson(Map<String, dynamic> json) {
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
    return data;
  }
}

class NetworkDetails {
  Null? networkdeviceid;
  int? serviceareaid;
  Null? slotid;
  Null? portid;
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
  int? validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  String? timeTotalQuota;
  String? timeUsedQuota;
  String? timeQuotaUnit;
  Null? billablecust;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  int? maxsession;
  String? service;
  String? planstage;
  int? childValidity;
  Null? plangroupid;
  Null? planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  Null? nickname;
  String? connectionNo;
  String? createbyname;
  String? createdate;
  bool? isdeleteforVoid;
  Null? remarks;
  bool? isinvoicestopinpackrel;
  Null? stopServiceDate;
  String? billTo;
  int? discount;
  String? custPlanCategory;
  String? discountType;
  bool? isDtv;
  bool? isQosv;
  String? invoiceType;
  Null? nextTeamHierarchyMappingId;
  Null? nextStaff;
  int? customerServiceMappingId;
  String? serviceEndDate;
  Null? serviceStartDate;
  Null? serviceStopDate;
  Null? discountExpiryDate;
  Null? promiseToPayStartDate;
  Null? promiseToPayEndDate;
  Null? promiseToPayDays;
  bool? isPromiseToPayTaken;
  Null? promiseToPayCount;
  String? dbStartDate;
  String? dbEndDate;
  String? dbExpiryDate;
  String? custServMappingStatus;
  bool? isChildExists;
  bool? isHold;
  bool? isVoid;
  Null? remainingDays;
  Null? extendValidityremarks;
  Null? serviceHoldBy;
  Null? serviceStartBy;
  Null? serviceHoldRemarks;
  Null? serviceStartRemarks;
  Null? serviceHoldDate;
  Null? serviceResumeDate;
  Null? serviceResumeBy;
  Null? serviceResumeRemarks;
  Null? debitdocid;
  Null? promiseToPayRemarks;
  String? qosSpeed;

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
        this.promiseToPayRemarks,
        this.qosSpeed});

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
