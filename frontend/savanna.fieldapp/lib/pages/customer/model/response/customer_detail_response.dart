import 'package:savbill/pages/customer/model/response/cust_address_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_charge_details.dart';
import 'package:savbill/pages/customer/model/response/cust_mac_mappping_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_payment_detail.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/webservices/base_response.dart';

class CustomerDetailResponse extends BaseResponse {
  CustomerDetail? data;

  CustomerDetailResponse({responseCode, responseMessage, this.data});

  CustomerDetailResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    status = json['status'];
    message = json['message'];
    timestamp = json['timestamp'];
    data = json['customerList'] != null
        ? CustomerDetail.fromJson(json['customerList'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['responseCode'] = responseCode;
    data['responseMessage'] = responseMessage;
    data['status'] = status;
    data['message'] = message;
    data['timestamp'] = timestamp;
    if (this.data != null) {
      data['customerList'] = this.data!.toJson();
    }
    return data;
  }
}

class CustomerDetail {
  dynamic id;
  dynamic ipPoolNameBind;
  dynamic framedIpv6Address;
  dynamic maxconcurrentsession;
  dynamic framedIPNetmask;
  dynamic framedroute;
  dynamic framedIPv6Prefix;
  dynamic gatewayIP;
  dynamic primaryDNS;
  dynamic primaryIPv6DNS;
  dynamic secondaryDNS;
  dynamic secondaryIPv6DNS;
  dynamic delegatedprefix;
  dynamic nasPortId;
  dynamic nasIpAddress;
  dynamic staticOrPooledIP;
  dynamic mac_auth_enable;
  dynamic nextStaff;
  String? parentQuotaType;
  String? birthDate;
  int? ezyBillServiceId;
  String? createdByName;
  String? activationByName;
  String? feasibility;
  dynamic mac_provision;
  String? expiryDate;
  int? walletBalUsed;
  int? currentAssigneeId;
  String? blockNo;
  dynamic nearestMacRetentionDate;
  String? nextQuotaResetDate;
  String? name;
  String? username;
  String? cafno;
  String? aadhar;
  dynamic contactperson;
  String? mobile;
  String? phone;
  dynamic email;
  String? currentAssigneeName;
  String? altemail;
  String? altmobile;
  String? altphone;
  dynamic currentStaff;
  String? fax;
  String? gst;
  String? pan;
  String? tinNo;
  String? address;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? onuid;
  String? stroltname;
  String? strslotname;
  String? strportname;
  String? strconntype;
  num? defaultpoolid;
  String? defaultpool;
  int? popid;
  dynamic oltid;
  dynamic buId;
  String? oltName;
  String? popName;
  String? masterdbName;
  String? splitterName;
  String? nextBillDate;
  dynamic nextfollowupdate;
  dynamic nextfollowuptime;
  dynamic serviceArea;
  // ServiceArea? serviceArea;

  // bool? onlinerenewalflag;
  // bool? voipenableflag;
  // bool? mactelflag;
  String? acctno;
  String? partnerName;
  int? salesRepId;
  String? salesRepName;
  String? ipAddress;
  String? ipPurDate;
  String? ipExpDate;
  String? voicesrvtype;
  String? didno;
  String? intercomgrp;
  num? outstanding;
  String? childdidno;
  String? intercomno;
  String? remarks;
  String? status;
  dynamic requestFor;
  String? latitude;
  String? longitude;
  String? url;
  String? gisCode;
  String? salesremark;
  String? servicetype;
  int? serviceAreaId;
  int? createdById;
  String? serviceareaName;
  String? custtype;
  dynamic connectionMode;
  dynamic password;
  String? firstname;
  String? lastname;
  String? title;
  String? custname;
  String? calendarType;
  String? countryCode;
  String? dunningActivateFor;
  String? remainTime;
  String? valleyType;
  String? customerArea;
  String? customerType;
  String? customerSubType;
  String? customerSector;
  String? customerSubSector;
  bool? isDunningEnable;
  bool? isNotificationEnable;
  int? failcount;
  int? billday;
  int? partnerid;
  dynamic partnerPaymentId;
  String? addresstype;
  String? address1;
  String? address2;
  int? city;
  int? state;
  int? country;
  int? pincode;
  int? area;
  String? oldpassword1;
  String? firstActivationDate;
  String? parentCustomerName;
  String? newpassword;
  String? oldpassword2;
  String? oldpassword3;
  String? selfcarepwd;
  String? lastPasswordChange;
  String? lastpasswordchangestring;
  String? flashMsg;
  int? resellerid;

  //bool? voiceprovision;
  String? custcategory;
  num? walletbalance;
  String? networktype;
  num? networkdevicesid;
  num? oltslotid;
  num? oltportid;
  String? oldBNGRouterinterface;
  String? oldVSIName;
  String? asnnumber;
  String? bngrouterinterface;
  String? bngroutername;
  String? ipprefixes;
  String? ipv6Prefixes;
  String? lanip;
  String? lanipv6;
  String? llaccountid;
  String? llconnectiontype;
  String? llexpirydate;
  String? llmedium;
  String? llserviceid;
  String? macaddress;
  String? peerip;
  String? poolip;
  String? qos;
  String? rdexport;
  String? rdvalue;
  String? vlanid;
  String? vrfname;
  String? vsiid;
  String? vsiname;
  String? wanip;
  String? wanipv6;
  String? oldWANIP;
  String? oldLLAccountid;

  // bool? isDeleted;
  String? billentityname;
  String? addparam1;
  String? addparam2;
  String? addparam3;
  String? addparam4;
  String? purchaseorder;
  String? allowedIPAddress;
  int? parentCustomersId;
  String? createDateString;
  String? updateDateString;
  String? isCustCaf;
  int? previousCafApprover;
  int? nextCafApprover;
  String? cafApproveStatus;
  int? mvnoId;
  String? mvnoName;
  String? passportNo;
  String? dunningCategory;
  CustPaymentDetail? paymentDetails;
  List<CustAddressDetail>? addressList;
  List<CustMacMapppingDetail>? custMacMapppingList;
  List<CustChargeDetails>? overChargeList;
  List<CustChargeDetails>? indiChargeList;
  List<PlanMappingDetail>? planMappingList;

  String? planPurchaseType;
  String? invoiceType;
  int? parentCustomerId;
  int? planGroupId;
  num? discount;
  int? branch;
  String? branchName;
  String? framedIp;
  String? nasPort;
  bool? isInvoiceToOrg;
  bool? istrialplan;
  dynamic? locations;
  String? areaName;

  CustomerDetail(
      {this.networkDetails,
      this.locations,
        this.nearestMacRetentionDate,
        this.blockNo,
        this.walletBalUsed,
        this.currentAssigneeId,
        this.expiryDate,
        this.mac_provision,
        this.parentQuotaType,
        this.birthDate,
        this.ezyBillServiceId,
        this.createdByName,
        this.activationByName,
        this.feasibility,
        this.nextQuotaResetDate,
        this.ipPoolNameBind,
        this.framedIpv6Address,
        this.maxconcurrentsession,
        this.framedIPNetmask,
        this.framedroute,
        this.framedIPv6Prefix,
        this.gatewayIP,
        this.primaryDNS,
        this.primaryIPv6DNS,
        this.secondaryDNS,
        this.secondaryIPv6DNS,
        this.delegatedprefix,
        this.nasPortId,
        this.nasIpAddress,
        this.staticOrPooledIP,
        this.mac_auth_enable,
        this.nextStaff,
        this.buId,

      this.id,
      this.name,
      this.username,
      this.cafno,
      this.aadhar,
      this.contactperson,
      this.mobile,
      this.phone,
      this.email,
      this.currentAssigneeName,
      this.altemail,
      this.altmobile,
      this.altphone,
      this.currentStaff,
      this.fax,
      this.gst,
      this.pan,
      this.address,
      this.connectivity,
      this.acctno,
      this.status,
      this.requestFor,
      this.custtype,
      this.outstanding,
      this.connectionMode,
      this.onuid,
      this.stroltname,
      this.strslotname,
      this.strportname,
      this.strconntype,
      this.defaultpoolid,
      this.defaultpool,
      this.popid,
      this.oltid,
      this.oltName,
      this.popName,
      this.masterdbName,
      this.splitterName,
      this.nextBillDate,
      this.nextfollowupdate,
      this.nextfollowuptime,
      this.serviceArea,
      //    this.onlinerenewalflag,
      // this.voipenableflag,
      // this.mactelflag,
      this.partnerName,
      this.salesRepId,
      this.salesRepName,
      this.tinNo,
      this.ipAddress,
      this.ipPurDate,
      this.ipExpDate,
      this.voicesrvtype,
      this.didno,
      this.intercomgrp,
      this.childdidno,
      this.intercomno,
      this.remarks,
      this.latitude,
      this.longitude,
      this.url,
      this.gisCode,
      this.salesremark,
      this.servicetype,
      this.serviceareaName,
      this.serviceAreaId,
      this.createdById,
      this.password,
      this.firstname,
      this.lastname,
      this.title,
      this.custname,
      this.calendarType,
      this.countryCode,
      this.dunningActivateFor,
      this.remainTime,
      this.valleyType,
      this.customerType,
      this.customerSubType,
      this.customerSector,
      this.customerSubSector,
      this.customerArea,
      this.isDunningEnable,
      this.isNotificationEnable,
      this.failcount,
      this.billday,
      this.partnerid,
      this.partnerPaymentId,
      this.addresstype,
      this.address1,
      this.address2,
      this.city,
      this.state,
      this.country,
      this.pincode,
      this.area,
      this.oldpassword1,
      this.firstActivationDate,
      this.parentCustomerName,
      this.newpassword,
      this.oldpassword2,
      this.oldpassword3,
      this.selfcarepwd,
      this.lastPasswordChange,
      this.lastpasswordchangestring,
      this.flashMsg,
      this.resellerid,
      //   this.voiceprovision,
      this.custcategory,
      this.walletbalance,
      this.networktype,
      this.networkdevicesid,
      this.oltslotid,
      this.oltportid,
      this.oldBNGRouterinterface,
      this.oldVSIName,
      this.asnnumber,
      this.bngrouterinterface,
      this.bngroutername,
      this.ipprefixes,
      this.ipv6Prefixes,
      this.lanip,
      this.lanipv6,
      this.llaccountid,
      this.llconnectiontype,
      this.llexpirydate,
      this.llmedium,
      this.llserviceid,
      this.macaddress,
      this.peerip,
      this.poolip,
      this.qos,
      this.rdexport,
      this.rdvalue,
      this.vlanid,
      this.vrfname,
      this.vsiid,
      this.vsiname,
      this.wanip,
      this.wanipv6,
      this.oldWANIP,
      this.oldLLAccountid,
      // this.isDeleted,
      this.billentityname,
      this.addparam1,
      this.addparam2,
      this.addparam3,
      this.addparam4,
      this.purchaseorder,
      this.allowedIPAddress,
      this.parentCustomersId,
      this.createDateString,
      this.updateDateString,
      this.isCustCaf,
      this.previousCafApprover,
      this.nextCafApprover,
      this.cafApproveStatus,
      this.mvnoId,
      this.mvnoName,
      this.passportNo,
      this.dunningCategory,
      this.paymentDetails,
      this.addressList,
      this.custMacMapppingList,
      this.indiChargeList,
      this.overChargeList,
      this.planMappingList,
      this.planPurchaseType,
      this.invoiceType,
      this.parentCustomerId,
      this.planGroupId,
      this.discount,
      this.branch,
      this.branchName,
      this.nasPort,
      this.framedIp,
        this.isInvoiceToOrg,
        this.istrialplan,
      this.areaName,
      });

  CustomerDetail.fromJson(Map<String, dynamic> json) {
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    id = json['id'];
    ipPoolNameBind = json['ipPoolNameBind'];
    framedIpv6Address = json['framedIpv6Address'];
    maxconcurrentsession = json['maxconcurrentsession'];
    framedIPNetmask = json['framedIPNetmask'];
    framedroute = json['framedroute'];
    framedIPv6Prefix = json['framedIPv6Prefix'];
    gatewayIP = json['gatewayIP'];
    primaryDNS = json['primaryDNS'];
    primaryIPv6DNS = json['primaryIPv6DNS'];
    secondaryDNS = json['secondaryDNS'];
    secondaryIPv6DNS = json['secondaryIPv6DNS'];
    delegatedprefix = json['delegatedprefix'];
    nasPortId = json['nasPortId'];
    nasIpAddress = json['nasIpAddress'];
    staticOrPooledIP = json['staticOrPooledIP'];
    mac_auth_enable = json['mac_auth_enable'];
    nextStaff = json['nextStaff'];
    buId = json['buId'];
    locations = json['locations'];
    name = json['name'];
    nextQuotaResetDate = json['nextQuotaResetDate'];
    nearestMacRetentionDate = json['nearestMacRetentionDate'];
    blockNo = json['blockNo'];
    walletBalUsed = json['walletBalUsed'];
    currentAssigneeId = json['currentAssigneeId'];
    expiryDate = json['expiryDate'];
    mac_provision = json['mac_provision'];
    parentQuotaType = json['parentQuotaType'];
    birthDate = json['birthDate'];
    ezyBillServiceId = json['ezyBillServiceId'];
    createdByName = json['createdByName'];
    activationByName = json['activationByName'];
    feasibility = json['feasibility'];
    username = json['username'];
    cafno = json['cafno'];
    aadhar = json['aadhar'];
    contactperson = json['contactperson'];
    mobile = json['mobile'];
    phone = json['phone'];
    email = json['email'];
    currentAssigneeName = json['currentAssigneeName'];
    altemail = json['altemail'];
    altmobile = json['altmobile'];
    altphone = json['altphone'];
    currentStaff = json['currentStaff'];
    fax = json['fax'];
    gst = json['gst'];
    pan = json['pan'];
    tinNo = json['tinNo'];
    address = json['address'];
    connectivity = json['connectivity'];
    acctno = json['acctno'];
    status = json['status'];
    requestFor = json['requestFor'];
    custtype = json['custtype'];
    outstanding = json['outstanding'];
    connectionMode = json['connectionMode'];
    onuid = json['onuid'];
    stroltname = json['stroltname'];
    strslotname = json['strslotname'];
    strportname = json['strportname'];
    strconntype = json['strconntype'];
    defaultpoolid = json['defaultpoolid'];
    defaultpool = json['defaultpool'];
    popid = json['popid'];
    oltid = json['oltid'];
    createdById = json['createdById'];
    popName = json['popName'];
    oltName = json['oltName'];
    masterdbName = json['masterdbName'];
    splitterName = json['splitterName'];
    nextBillDate = json['nextBillDate'];
    nextfollowupdate = json['nextfollowupcdate'];
    nextfollowuptime = json['nextfollowuptime'];
    serviceArea = json['serviceArea'];
    // serviceArea = json['serviceArea'] != null
    //     ? ServiceArea.fromJson(json['serviceArea'])
    //     : null;
    //  onlinerenewalflag = json['onlinerenewalflag'];
    // voipenableflag = json['voipenableflag'];
    // mactelflag = json['mactelflag'];
    partnerName = json['partnerName'];
    salesRepId = json['salesRepId'];
    salesRepName = json['salesRepName'];
    ipAddress = json['ipAddress'];
    ipPurDate = json['ipPurDate'];
    ipExpDate = json['ipExpDate'];
    voicesrvtype = json['voicesrvtype'];
    didno = json['didno'];
    intercomgrp = json['intercomgrp'];
    childdidno = json['childdidno'];
    intercomno = json['intercomno'];
    remarks = json['remarks'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    url = json['url'];
    gisCode = json['gis_code'];
    salesremark = json['salesremark'];
    servicetype = json['servicetype'];
    serviceareaName = json['serviceareaName'];
    serviceAreaId = json['serviceareaid'];
    createdById = json['createdById'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    title = json['title'];
    custname = json['custname'];
    calendarType = json['calendarType'];
    countryCode = json['countryCode'];
    dunningActivateFor = json['dunningActivateFor'];
    remainTime = json['remainTime'];

    valleyType = json['valleyType'];
    customerArea = json['customerArea'];
    customerType = json['customerType'];
    customerSubType = json['customerSubType'];
    customerSector = json['customerSector'];
    customerSubSector = json['customerSubSector'];
    isDunningEnable = json['isDunningEnable'];
    isNotificationEnable = json['isNotificationEnable'];

    failcount = json['failcount'];
    billday = json['billday'];
    partnerid = json['partnerid'];
    partnerPaymentId = json['partnerPaymentId'];
    addresstype = json['addresstype'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    pincode = json['pincode'];
    area = json['area'];
    oldpassword1 = json['oldpassword1'];
    parentCustomerName = json['parentCustomerName'];
    firstActivationDate = json['firstActivationDate'];
    newpassword = json['newpassword'];
    oldpassword2 = json['oldpassword2'];
    oldpassword3 = json['oldpassword3'];
    selfcarepwd = json['selfcarepwd'];
    lastPasswordChange = json['last_password_change'];
    lastpasswordchangestring = json['lastpasswordchangestring'];
    flashMsg = json['flashMsg'];
    resellerid = json['resellerid'];
    //voiceprovision = json['voiceprovision'];
    custcategory = json['custcategory'];
    walletbalance = json['walletbalance'];
    networktype = json['networktype'];
    networkdevicesid = json['networkdevicesid'];
    oltslotid = json['oltslotid'];
    oltportid = json['oltportid'];
    oldBNGRouterinterface = json['oldBNGRouterinterface'];
    oldVSIName = json['oldVSIName'];
    asnnumber = json['asnnumber'];
    bngrouterinterface = json['bngrouterinterface'];
    bngroutername = json['bngroutername'];
    ipprefixes = json['ipprefixes'];
    ipv6Prefixes = json['ipv6Prefixes'];
    lanip = json['lanip'];
    lanipv6 = json['lanipv6'];
    llaccountid = json['llaccountid'];
    llconnectiontype = json['llconnectiontype'];
    llexpirydate = json['llexpirydate'];
    llmedium = json['llmedium'];
    llserviceid = json['llserviceid'];
    macaddress = json['macaddress'];
    peerip = json['peerip'];
    poolip = json['poolip'];
    qos = json['qos'];
    rdexport = json['rdexport'];
    rdvalue = json['rdvalue'];
    vlanid = json['vlanid'];
    vrfname = json['vrfname'];
    vsiid = json['vsiid'];
    vsiname = json['vsiname'];
    wanip = json['wanip'];
    wanipv6 = json['wanipv6'];
    oldWANIP = json['oldWANIP'];
    oldLLAccountid = json['oldLLAccountid'];
    //isDeleted = json['isDeleted'];
    billentityname = json['billentityname'];
    addparam1 = json['addparam1'];
    addparam2 = json['addparam2'];
    addparam3 = json['addparam3'];
    addparam4 = json['addparam4'];
    purchaseorder = json['purchaseorder'];
    allowedIPAddress = json['allowedIPAddress'];
    parentCustomersId = json['parentCustomersId'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    isCustCaf = json['isCustCaf'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    cafApproveStatus = json['cafApproveStatus'];
    mvnoId = json['mvnoId'];
    mvnoName = json['mvnoName'];
    passportNo = json['passportNo'];
    dunningCategory = json['dunningCategory'];
    paymentDetails = json['paymentDetails'] != null
        ? new CustPaymentDetail.fromJson(json['paymentDetails'])
        : null;
    if (json['addressList'] != null) {
      addressList = <CustAddressDetail>[];
      json['addressList'].forEach((v) {
        addressList!.add(new CustAddressDetail.fromJson(v));
      });
    }
    if (json['custMacMapppingList'] != null) {
      custMacMapppingList = <CustMacMapppingDetail>[];
      json['custMacMapppingList'].forEach((v) {
        custMacMapppingList!.add(new CustMacMapppingDetail.fromJson(v));
      });
    }

    if (json['overChargeList'] != null) {
      overChargeList = <CustChargeDetails>[];
      json['overChargeList'].forEach((v) {
        overChargeList!.add(new CustChargeDetails.fromJson(v));
      });
    }

    if (json['indiChargeList'] != null) {
      indiChargeList = <CustChargeDetails>[];
      json['indiChargeList'].forEach((v) {
        indiChargeList!.add(new CustChargeDetails.fromJson(v));
      });
    }

    if (json['planMappingList'] != null) {
      planMappingList = <PlanMappingDetail>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingDetail.fromJson(v));
      });
    }
    planPurchaseType = json['planPurchaseType'];
    invoiceType = json['invoiceType'];
    parentCustomerId = json['parentCustomerId'];
    planGroupId = json['plangroupid'];
    discount = json['discount'];
    branch = json['branch'];
    branchName = json['branchName'];
    framedIp = json['framedIp'];
    nasPort = json['nasPort'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    istrialplan = json['istrialplan'];
    areaName = json['areaName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['id'] = this.id;
    data['framedIpv6Address'] = this.framedIpv6Address;
    data['ipPoolNameBind'] = this.ipPoolNameBind;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['framedIPNetmask'] = this.framedIPNetmask;
    data['framedroute'] = this.framedIPv6Prefix;
    data['gatewayIP'] = this.gatewayIP;
    data['primaryDNS'] = this.primaryDNS;
    data['primaryIPv6DNS'] = this.id;
    data['secondaryDNS'] = this.secondaryDNS;
    data['secondaryIPv6DNS'] = this.secondaryIPv6DNS;
    data['delegatedprefix'] = this.delegatedprefix;
    data['nasPortId'] = this.nasPortId;
    data['nasIpAddress'] = this.nasIpAddress;
    data['staticOrPooledIP'] = this.staticOrPooledIP;
    data['mac_auth_enable'] = this.mac_auth_enable;
    data['nextStaff'] = this.nextStaff;
    data['buId'] = this.buId;

    data['locations'] = this.locations;
    data['nextQuotaResetDate'] = this.nextQuotaResetDate;
    data['nearestMacRetentionDate'] = this.nearestMacRetentionDate;
    data['blockNo'] = this.blockNo;
    data['walletBalUsed'] = this.walletBalUsed;
    data['currentAssigneeId'] = this.currentAssigneeId;
    data['expiryDate'] = this.expiryDate;
    data['mac_provision'] = this.mac_provision;
    data['parentQuotaType'] = this.parentQuotaType;
    data['birthDate'] = this.birthDate;
    data['ezyBillServiceId'] = this.ezyBillServiceId;
    data['activationByName'] = this.activationByName;
    data['createdByName'] = this.createdByName;
    data['feasibility'] = this.feasibility;
    data['name'] = this.name;
    data['username'] = this.username;
    data['cafno'] = this.cafno;
    data['aadhar'] = this.aadhar;
    data['contactperson'] = this.contactperson;
    data['mobile'] = this.mobile;
    data['phone'] = this.phone;
    data['email'] = this.email;
    data['currentAssigneeName'] = this.currentAssigneeName;
    data['altemail'] = this.altemail;
    data['altmobile'] = this.altmobile;
    data['altphone'] = this.altphone;
    data['currentStaff'] = this.currentStaff;
    data['fax'] = this.fax;
    data['gst'] = this.gst;
    data['pan'] = this.pan;
    data['tinNo'] = this.tinNo;
    data['address'] = this.address;
    data['connectivity'] = this.connectivity;
    data['acctno'] = this.acctno;
    data['status'] = this.status;
    data['outstanding'] = this.outstanding;
    data['connectionMode'] = this.connectionMode;
    data['onuid'] = this.onuid;
    data['stroltname'] = this.stroltname;
    data['strslotname'] = this.strslotname;
    data['strportname'] = this.strportname;
    data['strconntype'] = this.strconntype;
    data['defaultpoolid'] = this.defaultpoolid;
    data['defaultpool'] = this.defaultpool;
    data['popid'] = this.popid;
    data['oltid'] = this.oltid;
    data['buId'] = this.buId;
    data['oltName'] = this.oltName;
    data['popName'] = this.popName;
    data['splitterName'] = this.splitterName;
    data['masterdbName'] = this.masterdbName;
    data['nextBillDate'] = this.nextBillDate;
    data['nextfollowupdate'] = this.nextfollowupdate;
    data['nextfollowuptime'] = this.nextfollowuptime;
    data['serviceArea'] = this.serviceArea;
    // if (this.serviceArea != null) {
    //   data['serviceArea'] = this.serviceArea!.toJson();
    // }
    // data['onlinerenewalflag'] = this.onlinerenewalflag;
    // data['voipenableflag'] = this.voipenableflag;
    // data['mactelflag'] = this.mactelflag;
    data['partnerName'] = this.partnerName;
    data['salesRepId'] = this.salesRepId;
    data['salesRepName'] = this.salesRepName;
    data['ipAddress'] = this.ipAddress;
    data['ipPurDate'] = this.ipPurDate;
    data['ipExpDate'] = this.ipExpDate;
    data['voicesrvtype'] = this.voicesrvtype;
    data['didno'] = this.didno;
    data['intercomgrp'] = this.intercomgrp;
    data['childdidno'] = this.childdidno;
    data['intercomno'] = this.intercomno;
    data['remarks'] = this.remarks;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['url'] = this.url;
    data['gis_code'] = this.gisCode;
    data['salesremark'] = this.salesremark;
    data['servicetype'] = this.servicetype;
    data['serviceareaName'] = this.serviceareaName;
    data['serviceareaid'] = this.serviceAreaId;
    data['createdById'] = this.createdById;
    data['custtype'] = this.custtype;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['title'] = this.title;
    data['custname'] = this.custname;
    data['calendarType'] = this.calendarType;
    data['countryCode'] = this.countryCode;
    data['dunningActivateFor'] = this.dunningActivateFor;
    data['remainTime'] = this.remainTime;

    data['valleyType'] = this.valleyType;
    data['customerArea'] = this.customerArea;
    data['customerType'] = this.customerType;
    data['customerSubType'] = this.customerSubType;
    data['customerSector'] = this.customerSector;
    data['customerSubSector'] = this.customerSubSector;
    data['isDunningEnable'] = this.isDunningEnable;
    data['isNotificationEnable'] = this.isNotificationEnable;

    data['failcount'] = this.failcount;
    data['billday'] = this.billday;
    data['partnerid'] = this.partnerid;
    data['partnerPaymentId'] = this.partnerPaymentId;
    data['addresstype'] = this.addresstype;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['pincode'] = this.pincode;
    data['area'] = this.area;
    data['oldpassword1'] = this.oldpassword1;
    data['firstActivationDate'] = this.firstActivationDate;
    data['parentCustomerName'] = this.parentCustomerName;
    data['newpassword'] = this.newpassword;
    data['oldpassword2'] = this.oldpassword2;
    data['oldpassword3'] = this.oldpassword3;
    data['selfcarepwd'] = this.selfcarepwd;
    data['last_password_change'] = this.lastPasswordChange;
    data['lastpasswordchangestring'] = this.lastpasswordchangestring;
    data['flashMsg'] = this.flashMsg;
    data['resellerid'] = this.resellerid;
    // data['voiceprovision'] = this.voiceprovision;
    data['custcategory'] = this.custcategory;
    data['walletbalance'] = this.walletbalance;
    data['networktype'] = this.networktype;
    data['networkdevicesid'] = this.networkdevicesid;
    data['oltslotid'] = this.oltslotid;
    data['oltportid'] = this.oltportid;
    data['oldBNGRouterinterface'] = this.oldBNGRouterinterface;
    data['oldVSIName'] = this.oldVSIName;
    data['asnnumber'] = this.asnnumber;
    data['bngrouterinterface'] = this.bngrouterinterface;
    data['bngroutername'] = this.bngroutername;
    data['ipprefixes'] = this.ipprefixes;
    data['ipv6Prefixes'] = this.ipv6Prefixes;
    data['lanip'] = this.lanip;
    data['lanipv6'] = this.lanipv6;
    data['llaccountid'] = this.llaccountid;
    data['llconnectiontype'] = this.llconnectiontype;
    data['llexpirydate'] = this.llexpirydate;
    data['llmedium'] = this.llmedium;
    data['llserviceid'] = this.llserviceid;
    data['macaddress'] = this.macaddress;
    data['peerip'] = this.peerip;
    data['poolip'] = this.poolip;
    data['qos'] = this.qos;
    data['rdexport'] = this.rdexport;
    data['rdvalue'] = this.rdvalue;
    data['vlanid'] = this.vlanid;
    data['vrfname'] = this.vrfname;
    data['vsiid'] = this.vsiid;
    data['vsiname'] = this.vsiname;
    data['wanip'] = this.wanip;
    data['wanipv6'] = this.wanipv6;
    data['oldWANIP'] = this.oldWANIP;
    data['oldLLAccountid'] = this.oldLLAccountid;
    //data['isDeleted'] = this.isDeleted;
    data['billentityname'] = this.billentityname;
    data['addparam1'] = this.addparam1;
    data['addparam2'] = this.addparam2;
    data['addparam3'] = this.addparam3;
    data['addparam4'] = this.addparam4;
    data['purchaseorder'] = this.purchaseorder;
    data['allowedIPAddress'] = this.allowedIPAddress;
    data['parentCustomersId'] = this.parentCustomersId;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['isCustCaf'] = this.isCustCaf;
    data['mvnoName'] = this.mvnoName;
    if (this.paymentDetails != null) {
      data['paymentDetails'] = this.paymentDetails!.toJson();
    }
    if (this.addressList != null) {
      data['addressList'] = this.addressList!.map((v) => v.toJson()).toList();
    }

    if (this.custMacMapppingList != null) {
      data['custMacMapppingList'] =
          this.custMacMapppingList!.map((v) => v.toJson()).toList();
    }

    if (this.overChargeList != null) {
      data['overChargeList'] =
          this.overChargeList!.map((v) => v.toJson()).toList();
    }

    if (this.indiChargeList != null) {
      data['indiChargeList'] =
          this.indiChargeList!.map((v) => v.toJson()).toList();
    }

    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }

    data['planPurchaseType'] = this.planPurchaseType;
    data['invoiceType'] = this.invoiceType;
    data['parentCustomerId'] = this.parentCustomerId;
    data['plangroupid'] = this.planGroupId;
    data['discount'] = this.discount;
    data['branch'] = this.branch;
    data['branchName'] = this.branchName;
    data['framedIp'] = this.framedIp;
    data['nasPort'] = this.nasPort;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['istrialplan'] = this.istrialplan;
    data['areaName'] = this.areaName;
    return data;
  }
}

class NetworkDetails {
  int? networkdeviceid;
  int? serviceareaid;
  int? slotid;
  int? portid;
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

class ServiceArea {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;

  // List<Null>? networkDevicesList;
  int? mvnoId;
  String? latitude;
  String? longitude;
  Null? areaId;
  List<PincodeList>? pincodeList;
  int? cityid;

  ServiceArea(
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
      // this.networkDevicesList,
      this.mvnoId,
      this.latitude,
      this.longitude,
      this.areaId,
      this.pincodeList,
      this.cityid});

  ServiceArea.fromJson(Map<String, dynamic> json) {
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
    // if (json['networkDevicesList'] != null) {
    //   networkDevicesList = <Null>[];
    //   json['networkDevicesList'].forEach((v) {
    //     networkDevicesList!.add(new Null.fromJson(v));
    //   });
    // }
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
    // if (this.networkDevicesList != null) {
    //   data['networkDevicesList'] =
    //       this.networkDevicesList!.map((v) => v.toJson()).toList();
    // }
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

class PincodeList {
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  List<AreaList>? areaList;
  int? mvnoId;
  String? createdate;
  String? updatedate;
  int? createdById;
  int? lastModifiedById;
  String? createdByName;
  String? lastModifiedByName;

  PincodeList(
      {this.id,
      this.pincode,
      this.status,
      this.isDeleted,
      this.countryId,
      this.cityId,
      this.stateId,
      this.areaList,
      this.mvnoId,
      this.createdate,
      this.updatedate,
      this.createdById,
      this.lastModifiedById,
      this.createdByName,
      this.lastModifiedByName});

  PincodeList.fromJson(Map<String, dynamic> json) {
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
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    return data;
  }
}

class AreaList {
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  int? mvnoId;
  String? createdate;
  String? updatedate;
  int? createdById;
  int? lastModifiedById;
  String? createdByName;
  String? lastModifiedByName;
  int? primaryKey;
  bool? deleteFlag;

  AreaList(
      {this.id,
      this.name,
      this.status,
      this.isDeleted,
      this.countryId,
      this.cityId,
      this.stateId,
      this.mvnoId,
      this.createdate,
      this.updatedate,
      this.createdById,
      this.lastModifiedById,
      this.createdByName,
      this.lastModifiedByName,
      this.primaryKey,
      this.deleteFlag});

  AreaList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

/*class CustomerDetail {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? acctno;
  double? outstanding;
  Null? previousCafApprover;
  int? nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  String? leadNo;
  int? leadId;
  Null? nextTeamHierarchyMapping;
  ServiceArea? serviceArea;
  List<CustAddressDetail>? custAddressList;
  String? customerAddress;
  Null? currentAssigneeParentId;
  Null? connectionMode;

  CustomerDetail(
      {this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        this.networkDetails,
        this.acctno,
        this.outstanding,
        this.previousCafApprover,
        this.nextCafApprover,
        this.status,
        this.custtype,
        this.calendarType,
        this.isinvoicestop,
        this.istrialplan,
        this.leadNo,
        this.leadId,
        this.nextTeamHierarchyMapping,
        this.serviceArea,
        this.custAddressList,
        this.customerAddress,
        this.currentAssigneeParentId,
        this.connectionMode});

  CustomerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    acctno = json['acctno'];
    outstanding = json['outstanding'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    status = json['status'];
    custtype = json['custtype'];
    calendarType = json['calendarType'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    serviceArea = json['serviceArea'] != null
        ? new ServiceArea.fromJson(json['serviceArea'])
        : null;
    if (json['custAddressList'] != null) {
      custAddressList = <CustAddressDetail>[];
      json['custAddressList'].forEach((v) {
        custAddressList!.add(new CustAddressDetail.fromJson(v));
      });
    }
    customerAddress = json['customerAddress'];
    currentAssigneeParentId = json['currentAssigneeParentId'];
    connectionMode = json['connectionMode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['mobile'] = this.mobile;
    data['email'] = this.email;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['acctno'] = this.acctno;
    data['outstanding'] = this.outstanding;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['status'] = this.status;
    data['custtype'] = this.custtype;
    data['calendarType'] = this.calendarType;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    if (this.serviceArea != null) {
      data['serviceArea'] = this.serviceArea!.toJson();
    }
    if (this.custAddressList != null) {
      data['custAddressList'] =
          this.custAddressList!.map((v) => v.toJson()).toList();
    }
    data['customerAddress'] = this.customerAddress;
    data['currentAssigneeParentId'] = this.currentAssigneeParentId;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}*/
