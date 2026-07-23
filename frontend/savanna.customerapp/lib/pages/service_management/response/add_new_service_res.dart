import 'package:savbill/webservices/base_response.dart';

import '../request/add_service_req.dart';

class AddNewServiceRes extends BaseResponse {
  Customer? customer;
  String? timestamp;
  int? status;

  AddNewServiceRes({this.customer, this.timestamp, this.status});

  AddNewServiceRes.fromJson(Map<String, dynamic> json) {
    customer = json['customer'] != null
        ? new Customer.fromJson(json['customer'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customer != null) {
      data['customer'] = this.customer!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Customer {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  dynamic username;
  dynamic password;
  dynamic firstname;
  dynamic lastname;
  dynamic email;
  dynamic title;
  dynamic custname;
  dynamic contactperson;
  dynamic pan;
  dynamic gst;
  dynamic aadhar;
  String? status;
  int? failcount;
  dynamic acctno;
  String? custtype;
  dynamic phone;
  dynamic billday;
  int? partnerid;
  dynamic onuid;
  String? nextBillDate;
  String? lastBillDate;
  dynamic addresstype;
  dynamic address1;
  dynamic address2;
  dynamic city;
  dynamic state;
  dynamic country;
  dynamic pincode;
  dynamic area;
  dynamic outstanding;
  dynamic oldpassword1;
  dynamic newpassword;
  dynamic oldpassword2;
  dynamic oldpassword3;
  dynamic selfcarepwd;
  int? popid;
  dynamic oltid;
  dynamic oltName;
  dynamic masterdbid;
  dynamic masterdbName;
  dynamic splitterid;
  dynamic splitterName;
  dynamic lastPasswordChange;
  dynamic lastpasswordchangestring;
  dynamic framedIpBind;
  dynamic ipPoolNameBind;
  List<PlanMappingList>? planMappingList;
  // List<Null>? linkAcceptanceList;
  List<AddressList>? addressList;
  // List<Null>? radiusprofileIds;
  // List<Null>? debitDocList;
  // List<Null>? creditDocuments;
  // List<Null>? overChargeList;
  // List<Null>? custDocList;
  // List<Null>? indiChargeList;
  dynamic custLeger;
  // List<Null>? custMacMapppingList;
  // List<Null>? ledgerDtls;
  dynamic paymentDetails;
  dynamic flashMsg;
  bool? mactelflag;
  bool? isinvoicestop;
  bool? istrialplan;
  dynamic mobile;
  String? countryCode;
  String? cafno;
  dynamic altmobile;
  dynamic altphone;
  dynamic altemail;
  dynamic fax;
  dynamic resellerid;
  dynamic salesrepid;
  dynamic voicesrvtype;
  bool? voiceprovision;
  dynamic didno;
  dynamic childdidno;
  dynamic intercomno;
  dynamic intercomgrp;
  bool? onlinerenewalflag;
  bool? voipenableflag;
  bool? isorgcust;
  dynamic custcategory;
  double? walletbalance;
  dynamic networktype;
  dynamic defaultpoolid;
  int? serviceareaid;
  dynamic networkdevicesid;
  dynamic oltslotid;
  dynamic oltportid;
  dynamic strconntype;
  dynamic stroltname;
  dynamic strslotname;
  dynamic strportname;
  dynamic billentityname;
  dynamic addparam1;
  dynamic addparam2;
  dynamic addparam3;
  dynamic addparam4;
  dynamic purchaseorder;
  dynamic remarks;
  dynamic allowedIPAddress;
  String? firstActivationDate;
  bool? isDeleted;
  dynamic createDateString;
  dynamic updateDateString;
  dynamic latitude;
  dynamic longitude;
  dynamic url;
  dynamic gisCode;
  dynamic salesremark;
  dynamic servicetype;
  dynamic isCustCaf;
  dynamic nextTeamHierarchyMapping;
  dynamic serviceareaName;
  dynamic cafApproveStatus;
  int? mvnoId;
  dynamic tinNo;
  dynamic passportNo;
  String? dunningCategory;
  dynamic plangroupid;
  dynamic planGroupDTO;
  dynamic parentCustomerId;
  dynamic parentCustomerName;
  dynamic invoiceType;
  String? calendarType;
  double? discount;
  dynamic buId;
  int? custPackageId;
  dynamic partnerLedgerMappingId;
  dynamic planPurchaseType;
  dynamic leadSource;
  dynamic feasibilityRequired;
  dynamic branch;
  dynamic branchName;
  dynamic regionName;
  dynamic buVerticals;
  dynamic valleyType;
  dynamic customerArea;
  dynamic customerType;
  dynamic customerSubType;
  dynamic customerSector;
  dynamic customerSubSector;
  dynamic lcoId;
  dynamic isFromPwc;
  dynamic leadId;
  dynamic leadNo;
  dynamic oldDebitDocId;
  dynamic nasPort;
  dynamic framedIp;
  dynamic flatAmount;
  dynamic ezyBillCustomersId;
  dynamic ezyBillAccountNumber;
  dynamic creditDocumentId;
  dynamic isFromFlutterWave;
  dynamic paymentOwner;
  dynamic ezyBillStockId;
  dynamic feasibility;
  dynamic feasibilityRemark;
  dynamic custlabel;
  dynamic staffId;
  dynamic dunningSubSector;
  dynamic dunningSubType;
  dynamic dunningType;
  dynamic dunningSector;
  dynamic registrationDate;
  dynamic planName;
  dynamic billableCustomerId;
  dynamic currentAssigneeId;
  dynamic rejectReasonId;
  dynamic rejectSubReasonId;
  dynamic rejectReasonName;
  dynamic rejectSubReasonName;
  dynamic businessType;
  String? discountType;
  dynamic discountExpiryDate;
  dynamic paymentOwnerId;
  dynamic additionalemail;
  dynamic salesrepresentative;
  dynamic skypeidImid;
  dynamic organisation;
  dynamic rating;
  dynamic automaticnotification;
  dynamic locationlevel1;
  dynamic locationlevel2;
  dynamic locationlevel3;
  dynamic locationlevel4;
  dynamic ponumber;
  dynamic customerbillingid;
  dynamic businessunit;
  dynamic subbusinessunit;
  dynamic isDunningActivate;
  dynamic dunningActivateFor;
  dynamic lastDunningDate;
  dynamic isDunningEnable;
  dynamic dunningAction;
  dynamic isNotificationEnable;
  dynamic parentExperience;
  dynamic lastStatusChangeDate;
  dynamic popName;
  dynamic department;
  bool? hasChildCust;
  dynamic subscriptionMode;
  dynamic validFrom;
  dynamic validUpto;
  dynamic locations;
  dynamic voucherCode;
  dynamic cid;
  dynamic birthDate;
  dynamic customerLocations;
  dynamic parentQuotaType;
  dynamic asnnumber;
  dynamic bngrouterinterface;
  dynamic bngroutername;
  dynamic ipprefixes;
  dynamic ipv6Prefixes;
  dynamic lanip;
  dynamic lanipv6;
  dynamic llaccountid;
  dynamic llconnectiontype;
  dynamic llexpirydate;
  dynamic llmedium;
  dynamic llserviceid;
  dynamic macaddress;
  dynamic peerip;
  dynamic poolip;
  dynamic qos;
  dynamic rdexport;
  dynamic rdvalue;
  dynamic vlanid;
  dynamic vrfname;
  dynamic vsiid;
  dynamic vsiname;
  dynamic wanip;
  dynamic wanipv6;
  dynamic oldBNGRouterinterface;
  dynamic oldVSIName;
  dynamic oldWANIP;
  dynamic oldLLAccountid;

  Customer(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.username,
        this.password,
        this.firstname,
        this.lastname,
        this.email,
        this.title,
        this.custname,
        this.contactperson,
        this.pan,
        this.gst,
        this.aadhar,
        this.status,
        this.failcount,
        this.acctno,
        this.custtype,
        this.phone,
        this.billday,
        this.partnerid,
        this.onuid,
        this.nextBillDate,
        this.lastBillDate,
        this.addresstype,
        this.address1,
        this.address2,
        this.city,
        this.state,
        this.country,
        this.pincode,
        this.area,
        this.outstanding,
        this.oldpassword1,
        this.newpassword,
        this.oldpassword2,
        this.oldpassword3,
        this.selfcarepwd,
        this.popid,
        this.oltid,
        this.oltName,
        this.masterdbid,
        this.masterdbName,
        this.splitterid,
        this.splitterName,
        this.lastPasswordChange,
        this.lastpasswordchangestring,
        this.framedIpBind,
        this.ipPoolNameBind,
        this.planMappingList,
        // this.linkAcceptanceList,
        this.addressList,
        // this.radiusprofileIds,
        // this.debitDocList,
        // this.creditDocuments,
        // this.overChargeList,
        // this.custDocList,
        // this.indiChargeList,
        this.custLeger,
        // this.custMacMapppingList,
        // this.ledgerDtls,
        this.paymentDetails,
        this.flashMsg,
        this.mactelflag,
        this.isinvoicestop,
        this.istrialplan,
        this.mobile,
        this.countryCode,
        this.cafno,
        this.altmobile,
        this.altphone,
        this.altemail,
        this.fax,
        this.resellerid,
        this.salesrepid,
        this.voicesrvtype,
        this.voiceprovision,
        this.didno,
        this.childdidno,
        this.intercomno,
        this.intercomgrp,
        this.onlinerenewalflag,
        this.voipenableflag,
        this.isorgcust,
        this.custcategory,
        this.walletbalance,
        this.networktype,
        this.defaultpoolid,
        this.serviceareaid,
        this.networkdevicesid,
        this.oltslotid,
        this.oltportid,
        this.strconntype,
        this.stroltname,
        this.strslotname,
        this.strportname,
        this.billentityname,
        this.addparam1,
        this.addparam2,
        this.addparam3,
        this.addparam4,
        this.purchaseorder,
        this.remarks,
        this.allowedIPAddress,
        this.firstActivationDate,
        this.isDeleted,
        this.createDateString,
        this.updateDateString,
        this.latitude,
        this.longitude,
        this.url,
        this.gisCode,
        this.salesremark,
        this.servicetype,
        this.isCustCaf,
        this.nextTeamHierarchyMapping,
        this.serviceareaName,
        this.cafApproveStatus,
        this.mvnoId,
        this.tinNo,
        this.passportNo,
        this.dunningCategory,
        this.plangroupid,
        this.planGroupDTO,
        this.parentCustomerId,
        this.parentCustomerName,
        this.invoiceType,
        this.calendarType,
        this.discount,
        this.buId,
        this.custPackageId,
        this.partnerLedgerMappingId,
        this.planPurchaseType,
        this.leadSource,
        this.feasibilityRequired,
        this.branch,
        this.branchName,
        this.regionName,
        this.buVerticals,
        this.valleyType,
        this.customerArea,
        this.customerType,
        this.customerSubType,
        this.customerSector,
        this.customerSubSector,
        this.lcoId,
        this.isFromPwc,
        this.leadId,
        this.leadNo,
        this.oldDebitDocId,
        this.nasPort,
        this.framedIp,
        this.flatAmount,
        this.ezyBillCustomersId,
        this.ezyBillAccountNumber,
        this.creditDocumentId,
        this.isFromFlutterWave,
        this.paymentOwner,
        this.ezyBillStockId,
        this.feasibility,
        this.feasibilityRemark,
        this.custlabel,
        this.staffId,
        this.dunningSubSector,
        this.dunningSubType,
        this.dunningType,
        this.dunningSector,
        this.registrationDate,
        this.planName,
        this.billableCustomerId,
        this.currentAssigneeId,
        this.rejectReasonId,
        this.rejectSubReasonId,
        this.rejectReasonName,
        this.rejectSubReasonName,
        this.businessType,
        this.discountType,
        this.discountExpiryDate,
        this.paymentOwnerId,
        this.additionalemail,
        this.salesrepresentative,
        this.skypeidImid,
        this.organisation,
        this.rating,
        this.automaticnotification,
        this.locationlevel1,
        this.locationlevel2,
        this.locationlevel3,
        this.locationlevel4,
        this.ponumber,
        this.customerbillingid,
        this.businessunit,
        this.subbusinessunit,
        this.isDunningActivate,
        this.dunningActivateFor,
        this.lastDunningDate,
        this.isDunningEnable,
        this.dunningAction,
        this.isNotificationEnable,
        this.parentExperience,
        this.lastStatusChangeDate,
        this.popName,
        this.department,
        this.hasChildCust,
        this.subscriptionMode,
        this.validFrom,
        this.validUpto,
        this.locations,
        this.voucherCode,
        this.cid,
        this.birthDate,
        this.customerLocations,
        this.parentQuotaType,
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
        this.oldBNGRouterinterface,
        this.oldVSIName,
        this.oldWANIP,
        this.oldLLAccountid});

  Customer.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    username = json['username'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    email = json['email'];
    title = json['title'];
    custname = json['custname'];
    contactperson = json['contactperson'];
    pan = json['pan'];
    gst = json['gst'];
    aadhar = json['aadhar'];
    status = json['status'];
    failcount = json['failcount'];
    acctno = json['acctno'];
    custtype = json['custtype'];
    phone = json['phone'];
    billday = json['billday'];
    partnerid = json['partnerid'];
    onuid = json['onuid'];
    nextBillDate = json['nextBillDate'];
    lastBillDate = json['lastBillDate'];
    addresstype = json['addresstype'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    pincode = json['pincode'];
    area = json['area'];
    outstanding = json['outstanding'];
    oldpassword1 = json['oldpassword1'];
    newpassword = json['newpassword'];
    oldpassword2 = json['oldpassword2'];
    oldpassword3 = json['oldpassword3'];
    selfcarepwd = json['selfcarepwd'];
    popid = json['popid'];
    oltid = json['oltid'];
    oltName = json['oltName'];
    masterdbid = json['masterdbid'];
    masterdbName = json['masterdbName'];
    splitterid = json['splitterid'];
    splitterName = json['splitterName'];
    lastPasswordChange = json['last_password_change'];
    lastpasswordchangestring = json['lastpasswordchangestring'];
    framedIpBind = json['framedIpBind'];
    ipPoolNameBind = json['ipPoolNameBind'];
    if (json['planMappingList'] != null) {
      planMappingList = <PlanMappingList>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingList.fromJson(v));
      });
    }
    // if (json['linkAcceptanceList'] != null) {
    //   linkAcceptanceList = <Null>[];
    //   json['linkAcceptanceList'].forEach((v) {
    //     linkAcceptanceList!.add(new Null.fromJson(v));
    //   });
    // }
    if (json['addressList'] != null) {
      addressList = <AddressList>[];
      json['addressList'].forEach((v) {
        addressList!.add(new AddressList.fromJson(v));
      });
    }
    // if (json['radiusprofileIds'] != null) {
    //   radiusprofileIds = <Null>[];
    //   json['radiusprofileIds'].forEach((v) {
    //     radiusprofileIds!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['debitDocList'] != null) {
    //   debitDocList = <Null>[];
    //   json['debitDocList'].forEach((v) {
    //     debitDocList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['creditDocuments'] != null) {
    //   creditDocuments = <Null>[];
    //   json['creditDocuments'].forEach((v) {
    //     creditDocuments!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['overChargeList'] != null) {
    //   overChargeList = <Null>[];
    //   json['overChargeList'].forEach((v) {
    //     overChargeList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['custDocList'] != null) {
    //   custDocList = <Null>[];
    //   json['custDocList'].forEach((v) {
    //     custDocList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['indiChargeList'] != null) {
    //   indiChargeList = <Null>[];
    //   json['indiChargeList'].forEach((v) {
    //     indiChargeList!.add(new Null.fromJson(v));
    //   });
    // }
    custLeger = json['custLeger'];
    // if (json['custMacMapppingList'] != null) {
    //   custMacMapppingList = <Null>[];
    //   json['custMacMapppingList'].forEach((v) {
    //     custMacMapppingList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['ledgerDtls'] != null) {
    //   ledgerDtls = <Null>[];
    //   json['ledgerDtls'].forEach((v) {
    //     ledgerDtls!.add(new Null.fromJson(v));
    //   });
    // }
    paymentDetails = json['paymentDetails'];
    flashMsg = json['flashMsg'];
    mactelflag = json['mactelflag'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    mobile = json['mobile'];
    countryCode = json['countryCode'];
    cafno = json['cafno'];
    altmobile = json['altmobile'];
    altphone = json['altphone'];
    altemail = json['altemail'];
    fax = json['fax'];
    resellerid = json['resellerid'];
    salesrepid = json['salesrepid'];
    voicesrvtype = json['voicesrvtype'];
    voiceprovision = json['voiceprovision'];
    didno = json['didno'];
    childdidno = json['childdidno'];
    intercomno = json['intercomno'];
    intercomgrp = json['intercomgrp'];
    onlinerenewalflag = json['onlinerenewalflag'];
    voipenableflag = json['voipenableflag'];
    isorgcust = json['isorgcust'];
    custcategory = json['custcategory'];
    walletbalance = json['walletbalance'];
    networktype = json['networktype'];
    defaultpoolid = json['defaultpoolid'];
    serviceareaid = json['serviceareaid'];
    networkdevicesid = json['networkdevicesid'];
    oltslotid = json['oltslotid'];
    oltportid = json['oltportid'];
    strconntype = json['strconntype'];
    stroltname = json['stroltname'];
    strslotname = json['strslotname'];
    strportname = json['strportname'];
    billentityname = json['billentityname'];
    addparam1 = json['addparam1'];
    addparam2 = json['addparam2'];
    addparam3 = json['addparam3'];
    addparam4 = json['addparam4'];
    purchaseorder = json['purchaseorder'];
    remarks = json['remarks'];
    allowedIPAddress = json['allowedIPAddress'];
    firstActivationDate = json['firstActivationDate'];
    isDeleted = json['isDeleted'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    url = json['url'];
    gisCode = json['gis_code'];
    salesremark = json['salesremark'];
    servicetype = json['servicetype'];
    isCustCaf = json['isCustCaf'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    serviceareaName = json['serviceareaName'];
    cafApproveStatus = json['cafApproveStatus'];
    mvnoId = json['mvnoId'];
    tinNo = json['tinNo'];
    passportNo = json['passportNo'];
    dunningCategory = json['dunningCategory'];
    plangroupid = json['plangroupid'];
    planGroupDTO = json['planGroupDTO'];
    parentCustomerId = json['parentCustomerId'];
    parentCustomerName = json['parentCustomerName'];
    invoiceType = json['invoiceType'];
    calendarType = json['calendarType'];
    discount = json['discount'];
    buId = json['buId'];
    custPackageId = json['custPackageId'];
    partnerLedgerMappingId = json['partnerLedgerMappingId'];
    planPurchaseType = json['planPurchaseType'];
    leadSource = json['leadSource'];
    feasibilityRequired = json['feasibilityRequired'];
    branch = json['branch'];
    branchName = json['branchName'];
    regionName = json['regionName'];
    buVerticals = json['buVerticals'];
    valleyType = json['valleyType'];
    customerArea = json['customerArea'];
    customerType = json['customerType'];
    customerSubType = json['customerSubType'];
    customerSector = json['customerSector'];
    customerSubSector = json['customerSubSector'];
    lcoId = json['lcoId'];
    isFromPwc = json['is_from_pwc'];
    leadId = json['leadId'];
    leadNo = json['leadNo'];
    oldDebitDocId = json['oldDebitDocId'];
    nasPort = json['nasPort'];
    framedIp = json['framedIp'];
    flatAmount = json['flatAmount'];
    ezyBillCustomersId = json['ezyBillCustomersId'];
    ezyBillAccountNumber = json['ezyBillAccountNumber'];
    creditDocumentId = json['creditDocumentId'];
    isFromFlutterWave = json['isFromFlutterWave'];
    paymentOwner = json['paymentOwner'];
    ezyBillStockId = json['ezyBillStockId'];
    feasibility = json['feasibility'];
    feasibilityRemark = json['feasibilityRemark'];
    custlabel = json['custlabel'];
    staffId = json['staffId'];
    dunningSubSector = json['dunningSubSector'];
    dunningSubType = json['dunningSubType'];
    dunningType = json['dunningType'];
    dunningSector = json['dunningSector'];
    registrationDate = json['registrationDate'];
    planName = json['planName'];
    billableCustomerId = json['billableCustomerId'];
    currentAssigneeId = json['currentAssigneeId'];
    rejectReasonId = json['rejectReasonId'];
    rejectSubReasonId = json['rejectSubReasonId'];
    rejectReasonName = json['rejectReasonName'];
    rejectSubReasonName = json['rejectSubReasonName'];
    businessType = json['businessType'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    paymentOwnerId = json['paymentOwnerId'];
    additionalemail = json['additionalemail'];
    salesrepresentative = json['salesrepresentative'];
    skypeidImid = json['skypeid_imid'];
    organisation = json['organisation'];
    rating = json['rating'];
    automaticnotification = json['automaticnotification'];
    locationlevel1 = json['locationlevel1'];
    locationlevel2 = json['locationlevel2'];
    locationlevel3 = json['locationlevel3'];
    locationlevel4 = json['locationlevel4'];
    ponumber = json['ponumber'];
    customerbillingid = json['customerbillingid'];
    businessunit = json['businessunit'];
    subbusinessunit = json['subbusinessunit'];
    isDunningActivate = json['isDunningActivate'];
    dunningActivateFor = json['dunningActivateFor'];
    lastDunningDate = json['lastDunningDate'];
    isDunningEnable = json['isDunningEnable'];
    dunningAction = json['dunningAction'];
    isNotificationEnable = json['isNotificationEnable'];
    parentExperience = json['parentExperience'];
    lastStatusChangeDate = json['lastStatusChangeDate'];
    popName = json['popName'];
    department = json['department'];
    hasChildCust = json['hasChildCust'];
    subscriptionMode = json['subscriptionMode'];
    validFrom = json['validFrom'];
    validUpto = json['validUpto'];
    locations = json['locations'];
    voucherCode = json['voucherCode'];
    cid = json['cid'];
    birthDate = json['birthDate'];
    customerLocations = json['customerLocations'];
    parentQuotaType = json['parentQuotaType'];
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
    oldBNGRouterinterface = json['oldBNGRouterinterface'];
    oldVSIName = json['oldVSIName'];
    oldWANIP = json['oldWANIP'];
    oldLLAccountid = json['oldLLAccountid'];
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
    data['username'] = this.username;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['email'] = this.email;
    data['title'] = this.title;
    data['custname'] = this.custname;
    data['contactperson'] = this.contactperson;
    data['pan'] = this.pan;
    data['gst'] = this.gst;
    data['aadhar'] = this.aadhar;
    data['status'] = this.status;
    data['failcount'] = this.failcount;
    data['acctno'] = this.acctno;
    data['custtype'] = this.custtype;
    data['phone'] = this.phone;
    data['billday'] = this.billday;
    data['partnerid'] = this.partnerid;
    data['onuid'] = this.onuid;
    data['nextBillDate'] = this.nextBillDate;
    data['lastBillDate'] = this.lastBillDate;
    data['addresstype'] = this.addresstype;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['pincode'] = this.pincode;
    data['area'] = this.area;
    data['outstanding'] = this.outstanding;
    data['oldpassword1'] = this.oldpassword1;
    data['newpassword'] = this.newpassword;
    data['oldpassword2'] = this.oldpassword2;
    data['oldpassword3'] = this.oldpassword3;
    data['selfcarepwd'] = this.selfcarepwd;
    data['popid'] = this.popid;
    data['oltid'] = this.oltid;
    data['oltName'] = this.oltName;
    data['masterdbid'] = this.masterdbid;
    data['masterdbName'] = this.masterdbName;
    data['splitterid'] = this.splitterid;
    data['splitterName'] = this.splitterName;
    data['last_password_change'] = this.lastPasswordChange;
    data['lastpasswordchangestring'] = this.lastpasswordchangestring;
    data['framedIpBind'] = this.framedIpBind;
    data['ipPoolNameBind'] = this.ipPoolNameBind;
    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }
    // if (this.linkAcceptanceList != null) {
    //   data['linkAcceptanceList'] =
    //       this.linkAcceptanceList!.map((v) => v.toJson()).toList();
    // }
    if (this.addressList != null) {
      data['addressList'] = this.addressList!.map((v) => v.toJson()).toList();
    }
    // if (this.radiusprofileIds != null) {
    //   data['radiusprofileIds'] =
    //       this.radiusprofileIds!.map((v) => v.toJson()).toList();
    // }
    // if (this.debitDocList != null) {
    //   data['debitDocList'] = this.debitDocList!.map((v) => v.toJson()).toList();
    // }
    // if (this.creditDocuments != null) {
    //   data['creditDocuments'] =
    //       this.creditDocuments!.map((v) => v.toJson()).toList();
    // }
    // if (this.overChargeList != null) {
    //   data['overChargeList'] =
    //       this.overChargeList!.map((v) => v.toJson()).toList();
    // }
    // if (this.custDocList != null) {
    //   data['custDocList'] = this.custDocList!.map((v) => v.toJson()).toList();
    // }
    // if (this.indiChargeList != null) {
    //   data['indiChargeList'] =
    //       this.indiChargeList!.map((v) => v.toJson()).toList();
    // }
    data['custLeger'] = this.custLeger;
    // if (this.custMacMapppingList != null) {
    //   data['custMacMapppingList'] =
    //       this.custMacMapppingList!.map((v) => v.toJson()).toList();
    // }
    // if (this.ledgerDtls != null) {
    //   data['ledgerDtls'] = this.ledgerDtls!.map((v) => v.toJson()).toList();
    // }
    data['paymentDetails'] = this.paymentDetails;
    data['flashMsg'] = this.flashMsg;
    data['mactelflag'] = this.mactelflag;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['mobile'] = this.mobile;
    data['countryCode'] = this.countryCode;
    data['cafno'] = this.cafno;
    data['altmobile'] = this.altmobile;
    data['altphone'] = this.altphone;
    data['altemail'] = this.altemail;
    data['fax'] = this.fax;
    data['resellerid'] = this.resellerid;
    data['salesrepid'] = this.salesrepid;
    data['voicesrvtype'] = this.voicesrvtype;
    data['voiceprovision'] = this.voiceprovision;
    data['didno'] = this.didno;
    data['childdidno'] = this.childdidno;
    data['intercomno'] = this.intercomno;
    data['intercomgrp'] = this.intercomgrp;
    data['onlinerenewalflag'] = this.onlinerenewalflag;
    data['voipenableflag'] = this.voipenableflag;
    data['isorgcust'] = this.isorgcust;
    data['custcategory'] = this.custcategory;
    data['walletbalance'] = this.walletbalance;
    data['networktype'] = this.networktype;
    data['defaultpoolid'] = this.defaultpoolid;
    data['serviceareaid'] = this.serviceareaid;
    data['networkdevicesid'] = this.networkdevicesid;
    data['oltslotid'] = this.oltslotid;
    data['oltportid'] = this.oltportid;
    data['strconntype'] = this.strconntype;
    data['stroltname'] = this.stroltname;
    data['strslotname'] = this.strslotname;
    data['strportname'] = this.strportname;
    data['billentityname'] = this.billentityname;
    data['addparam1'] = this.addparam1;
    data['addparam2'] = this.addparam2;
    data['addparam3'] = this.addparam3;
    data['addparam4'] = this.addparam4;
    data['purchaseorder'] = this.purchaseorder;
    data['remarks'] = this.remarks;
    data['allowedIPAddress'] = this.allowedIPAddress;
    data['firstActivationDate'] = this.firstActivationDate;
    data['isDeleted'] = this.isDeleted;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['url'] = this.url;
    data['gis_code'] = this.gisCode;
    data['salesremark'] = this.salesremark;
    data['servicetype'] = this.servicetype;
    data['isCustCaf'] = this.isCustCaf;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['serviceareaName'] = this.serviceareaName;
    data['cafApproveStatus'] = this.cafApproveStatus;
    data['mvnoId'] = this.mvnoId;
    data['tinNo'] = this.tinNo;
    data['passportNo'] = this.passportNo;
    data['dunningCategory'] = this.dunningCategory;
    data['plangroupid'] = this.plangroupid;
    data['planGroupDTO'] = this.planGroupDTO;
    data['parentCustomerId'] = this.parentCustomerId;
    data['parentCustomerName'] = this.parentCustomerName;
    data['invoiceType'] = this.invoiceType;
    data['calendarType'] = this.calendarType;
    data['discount'] = this.discount;
    data['buId'] = this.buId;
    data['custPackageId'] = this.custPackageId;
    data['partnerLedgerMappingId'] = this.partnerLedgerMappingId;
    data['planPurchaseType'] = this.planPurchaseType;
    data['leadSource'] = this.leadSource;
    data['feasibilityRequired'] = this.feasibilityRequired;
    data['branch'] = this.branch;
    data['branchName'] = this.branchName;
    data['regionName'] = this.regionName;
    data['buVerticals'] = this.buVerticals;
    data['valleyType'] = this.valleyType;
    data['customerArea'] = this.customerArea;
    data['customerType'] = this.customerType;
    data['customerSubType'] = this.customerSubType;
    data['customerSector'] = this.customerSector;
    data['customerSubSector'] = this.customerSubSector;
    data['lcoId'] = this.lcoId;
    data['is_from_pwc'] = this.isFromPwc;
    data['leadId'] = this.leadId;
    data['leadNo'] = this.leadNo;
    data['oldDebitDocId'] = this.oldDebitDocId;
    data['nasPort'] = this.nasPort;
    data['framedIp'] = this.framedIp;
    data['flatAmount'] = this.flatAmount;
    data['ezyBillCustomersId'] = this.ezyBillCustomersId;
    data['ezyBillAccountNumber'] = this.ezyBillAccountNumber;
    data['creditDocumentId'] = this.creditDocumentId;
    data['isFromFlutterWave'] = this.isFromFlutterWave;
    data['paymentOwner'] = this.paymentOwner;
    data['ezyBillStockId'] = this.ezyBillStockId;
    data['feasibility'] = this.feasibility;
    data['feasibilityRemark'] = this.feasibilityRemark;
    data['custlabel'] = this.custlabel;
    data['staffId'] = this.staffId;
    data['dunningSubSector'] = this.dunningSubSector;
    data['dunningSubType'] = this.dunningSubType;
    data['dunningType'] = this.dunningType;
    data['dunningSector'] = this.dunningSector;
    data['registrationDate'] = this.registrationDate;
    data['planName'] = this.planName;
    data['billableCustomerId'] = this.billableCustomerId;
    data['currentAssigneeId'] = this.currentAssigneeId;
    data['rejectReasonId'] = this.rejectReasonId;
    data['rejectSubReasonId'] = this.rejectSubReasonId;
    data['rejectReasonName'] = this.rejectReasonName;
    data['rejectSubReasonName'] = this.rejectSubReasonName;
    data['businessType'] = this.businessType;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['additionalemail'] = this.additionalemail;
    data['salesrepresentative'] = this.salesrepresentative;
    data['skypeid_imid'] = this.skypeidImid;
    data['organisation'] = this.organisation;
    data['rating'] = this.rating;
    data['automaticnotification'] = this.automaticnotification;
    data['locationlevel1'] = this.locationlevel1;
    data['locationlevel2'] = this.locationlevel2;
    data['locationlevel3'] = this.locationlevel3;
    data['locationlevel4'] = this.locationlevel4;
    data['ponumber'] = this.ponumber;
    data['customerbillingid'] = this.customerbillingid;
    data['businessunit'] = this.businessunit;
    data['subbusinessunit'] = this.subbusinessunit;
    data['isDunningActivate'] = this.isDunningActivate;
    data['dunningActivateFor'] = this.dunningActivateFor;
    data['lastDunningDate'] = this.lastDunningDate;
    data['isDunningEnable'] = this.isDunningEnable;
    data['dunningAction'] = this.dunningAction;
    data['isNotificationEnable'] = this.isNotificationEnable;
    data['parentExperience'] = this.parentExperience;
    data['lastStatusChangeDate'] = this.lastStatusChangeDate;
    data['popName'] = this.popName;
    data['department'] = this.department;
    data['hasChildCust'] = this.hasChildCust;
    data['subscriptionMode'] = this.subscriptionMode;
    data['validFrom'] = this.validFrom;
    data['validUpto'] = this.validUpto;
    data['locations'] = this.locations;
    data['voucherCode'] = this.voucherCode;
    data['cid'] = this.cid;
    data['birthDate'] = this.birthDate;
    data['customerLocations'] = this.customerLocations;
    data['parentQuotaType'] = this.parentQuotaType;
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
    data['oldBNGRouterinterface'] = this.oldBNGRouterinterface;
    data['oldVSIName'] = this.oldVSIName;
    data['oldWANIP'] = this.oldWANIP;
    data['oldLLAccountid'] = this.oldLLAccountid;
    return data;
  }
}


class QuotaList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? planGroup;
  dynamic id;
  int? planId;
  String? quotaType;
  String? custQuotaType;
  double? totalQuota;
  double? usedQuota;
  String? quotaUnit;
  double? timeTotalQuota;
  double? timeQuotaUsed;
  dynamic timeQuotaUnit;
  bool? isDelete;
  double? totalQuotaKB;
  double? usedQuotaKB;
  double? timeUsedQuotaSec;
  double? timeTotalQuotaSec;
  dynamic didtotalquota;
  dynamic didusedquota;
  dynamic intercomtotalquota;
  dynamic intercomusedquota;
  dynamic didQuotaUnit;
  dynamic intercomQuotaUnit;
  String? planName;
  dynamic cprId;
  dynamic currentSessionUsageTime;
  dynamic currentSessionUsageVolume;
  dynamic lastQuotaReset;
  dynamic parentQuotaType;
  dynamic reservedQuotaInPer;
  dynamic totalReservedQuota;
  bool? chunkAvailable;

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
        this.custQuotaType,
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
        this.cprId,
        this.currentSessionUsageTime,
        this.currentSessionUsageVolume,
        this.lastQuotaReset,
        this.parentQuotaType,
        this.reservedQuotaInPer,
        this.totalReservedQuota,
        this.chunkAvailable});

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
    custQuotaType = json['custQuotaType'];
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
    currentSessionUsageTime = json['currentSessionUsageTime'];
    currentSessionUsageVolume = json['currentSessionUsageVolume'];
    lastQuotaReset = json['lastQuotaReset'];
    parentQuotaType = json['parentQuotaType'];
    reservedQuotaInPer = json['reservedQuotaInPer'];
    totalReservedQuota = json['totalReservedQuota'];
    chunkAvailable = json['chunkAvailable'];
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
    data['custQuotaType'] = this.custQuotaType;
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
    data['currentSessionUsageTime'] = this.currentSessionUsageTime;
    data['currentSessionUsageVolume'] = this.currentSessionUsageVolume;
    data['lastQuotaReset'] = this.lastQuotaReset;
    data['parentQuotaType'] = this.parentQuotaType;
    data['reservedQuotaInPer'] = this.reservedQuotaInPer;
    data['totalReservedQuota'] = this.totalReservedQuota;
    data['chunkAvailable'] = this.chunkAvailable;
    return data;
  }
}

class AddressList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? addressType;
  dynamic address1;
  dynamic address2;
  String? landmark;
  dynamic landmark1;
  int? areaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;
  dynamic customerId;
  String? fullAddress;
  bool? isDelete;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  dynamic status;
  String? version;
  dynamic shiftId;
  dynamic shiftedPartnerId;
  dynamic shiftedServiceAreaId;
  dynamic requestedByName;
  dynamic requestedDate;
  bool? delete;

  AddressList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.addressType,
        this.address1,
        this.address2,
        this.landmark,
        this.landmark1,
        this.areaId,
        this.pincodeId,
        this.cityId,
        this.stateId,
        this.countryId,
        this.customerId,
        this.fullAddress,
        this.isDelete,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.status,
        this.version,
        this.shiftId,
        this.shiftedPartnerId,
        this.shiftedServiceAreaId,
        this.requestedByName,
        this.requestedDate,
        this.delete});

  AddressList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    addressType = json['addressType'];
    address1 = json['address1'];
    address2 = json['address2'];
    landmark = json['landmark'];
    landmark1 = json['landmark1'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    customerId = json['customerId'];
    fullAddress = json['fullAddress'];
    isDelete = json['isDelete'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    status = json['status'];
    version = json['version'];
    shiftId = json['shiftId'];
    shiftedPartnerId = json['shiftedPartnerId'];
    shiftedServiceAreaId = json['shiftedServiceAreaId'];
    requestedByName = json['requestedByName'];
    requestedDate = json['requestedDate'];
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
    data['addressType'] = this.addressType;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['landmark'] = this.landmark;
    data['landmark1'] = this.landmark1;
    data['areaId'] = this.areaId;
    data['pincodeId'] = this.pincodeId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;
    data['customerId'] = this.customerId;
    data['fullAddress'] = this.fullAddress;
    data['isDelete'] = this.isDelete;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['status'] = this.status;
    data['version'] = this.version;
    data['shiftId'] = this.shiftId;
    data['shiftedPartnerId'] = this.shiftedPartnerId;
    data['shiftedServiceAreaId'] = this.shiftedServiceAreaId;
    data['requestedByName'] = this.requestedByName;
    data['requestedDate'] = this.requestedDate;
    data['delete'] = this.delete;
    return data;
  }
}
