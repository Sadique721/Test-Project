class GetPromiseRemarkRes {
  int? responseCode;
  String? responseMessage;
  PromiseData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetPromiseRemarkRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetPromiseRemarkRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new PromiseData.fromJson(json['data']) : null;
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

class PromiseData {
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
  double? outstanding;
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

  PromiseData(
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
        this.password});

  PromiseData.fromJson(Map<String, dynamic> json) {
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
  Null? qosPolicyName;
  Null? qosPolicyId;
  int? custId;
  int? validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  String? timeTotalQuota;
  String? timeUsedQuota;
  Null? timeQuotaUnit;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  int? maxsession;
  String? service;
  String? planstage;
  Null? childValidity;
  Null? plangroupid;
  Null? planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  Null? nickname;
  String? connectionNo;
  String? createbyname;
  String? createdate;
  Null? remarks;
  String? stopServiceDate;

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
        this.remarks,
        this.stopServiceDate});

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
    remarks = json['remarks'];
    stopServiceDate = json['stopServiceDate'];
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
    data['remarks'] = this.remarks;
    data['stopServiceDate'] = this.stopServiceDate;
    return data;
  }
}
