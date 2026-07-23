import 'package:savbill/webservices/base_response.dart';

class ChildCustChangePlanRes extends BaseResponse {
  PageDetails? pageDetails;
  List<ChildCustList>? customerList;
  String? timestamp;
  int? status;

  ChildCustChangePlanRes(
      {this.pageDetails, this.customerList, this.timestamp, this.status});

  ChildCustChangePlanRes.fromJson(Map<String, dynamic> json) {
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['customerList'] != null) {
      customerList = <ChildCustList>[];
      json['customerList'].forEach((v) {
        customerList!.add(new ChildCustList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    if (this.customerList != null) {
      data['customerList'] = this.customerList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PageDetails {
  int? totalPages;
  int? totalRecords;
  int? totalRecordsPerPage;
  int? currentPageNumber;

  PageDetails(
      {this.totalPages,
        this.totalRecords,
        this.totalRecordsPerPage,
        this.currentPageNumber});

  PageDetails.fromJson(Map<String, dynamic> json) {
    totalPages = json['totalPages'];
    totalRecords = json['totalRecords'];
    totalRecordsPerPage = json['totalRecordsPerPage'];
    currentPageNumber = json['currentPageNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['totalPages'] = this.totalPages;
    data['totalRecords'] = this.totalRecords;
    data['totalRecordsPerPage'] = this.totalRecordsPerPage;
    data['currentPageNumber'] = this.currentPageNumber;
    return data;
  }
}

class ChildCustList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? username;
  String? password;
  String? firstname;
  String? lastname;
  String? email;
  String? title;
  String? custname;
  String? contactperson;
  String? pan;
  String? gst;
  String? aadhar;
  String? status;
  int? failcount;
  String? acctno;
  String? custtype;
  String? phone;
  dynamic billday;
  int? partnerid;
  dynamic onuid;
  String? nextBillDate;
  dynamic lastBillDate;
  dynamic addresstype;
  dynamic address1;
  dynamic address2;
  dynamic city;
  dynamic state;
  dynamic country;
  dynamic pincode;
  dynamic area;
  double? outstanding;
  dynamic oldpassword1;
  dynamic newpassword;
  dynamic oldpassword2;
  dynamic oldpassword3;
  String? selfcarepwd;
  int? popid;
  dynamic oltid;
  dynamic oltName;
  dynamic masterdbid;
  dynamic masterdbName;
  dynamic splitterid;
  dynamic splitterName;
  String? lastPasswordChange;
  String? lastpasswordchangestring;
  String? framedIpBind;
  String? ipPoolNameBind;
  List<PlanMappingList>? planMappingList;
  // List<Null>? linkAcceptanceList;
  List<AddressList>? addressList;
  // List<Null>? radiusprofileIds;
  List<DebitDocList>? debitDocList;
  // List<Null>? creditDocuments;
  // List<Null>? overChargeList;
  // List<Null>? custDocList;
  List<IndiChargeList>? indiChargeList;
  dynamic custLeger;
  // List<Null>? custMacMapppingList;
  // List<Null>? ledgerDtls;
  dynamic paymentDetails;
  dynamic flashMsg;
  bool? mactelflag;
  bool? isinvoicestop;
  bool? istrialplan;
  String? mobile;
  String? countryCode;
  String? cafno;
  dynamic ipv4;
  dynamic ipv6;
  dynamic vlan;
  dynamic altmobile;
  dynamic altphone;
  dynamic altemail;
  String? fax;
  dynamic resellerid;
  dynamic salesrepid;
  String? voicesrvtype;
  bool? voiceprovision;
  String? didno;
  dynamic childdidno;
  dynamic intercomno;
  dynamic intercomgrp;
  bool? onlinerenewalflag;
  bool? voipenableflag;
  bool? isorgcust;
  String? custcategory;
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
  String? vlanId;
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
  String? createDateString;
  String? updateDateString;
  String? latitude;
  String? longitude;
  dynamic url;
  dynamic gisCode;
  String? salesremark;
  String? servicetype;
  dynamic isCustCaf;
  dynamic nextTeamHierarchyMapping;
  String? serviceareaName;
  dynamic cafApproveStatus;
  int? mvnoId;
  String? tinNo;
  String? passportNo;
  String? dunningCategory;
  dynamic plangroupid;
  dynamic planGroupDTO;
  dynamic parentCustomerId;
  dynamic parentCustomerName;
  dynamic invoiceType;
  String? calendarType;
  double? discount;
  dynamic buId;
  dynamic custPackageId;
  dynamic partnerLedgerMappingId;
  String? planPurchaseType;
  dynamic leadSource;
  dynamic feasibilityRequired;
  int? branch;
  dynamic branchName;
  dynamic regionName;
  dynamic buVerticals;
  String? valleyType;
  String? customerArea;
  String? customerType;
  String? customerSubType;
  String? customerSector;
  String? customerSubSector;
  dynamic lcoId;
  bool? isFromPwc;
  dynamic leadId;
  dynamic leadNo;
  dynamic oldDebitDocId;
  String? nasPort;
  String? framedIp;
  dynamic flatAmount;
  dynamic ezyBillCustomersId;
  dynamic ezyBillAccountNumber;
  dynamic creditDocumentId;
  dynamic isFromFlutterWave;
  dynamic paymentOwner;
  dynamic ezyBillStockId;
  dynamic feasibility;
  dynamic feasibilityRemark;
  String? custlabel;
  dynamic staffId;
  dynamic dunningSubSector;
  dynamic dunningSubType;
  String? dunningType;
  String? dunningSector;
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
  bool? isDunningEnable;
  dynamic dunningAction;
  bool? isNotificationEnable;
  String? parentExperience;
  String? lastStatusChangeDate;
  dynamic popName;
  String? department;
  bool? hasChildCust;
  dynamic subscriptionMode;
  dynamic validFrom;
  dynamic validUpto;
  dynamic locations;
  dynamic voucherCode;
  dynamic cid;
  dynamic birthDate;
  // List<Null>? customerLocations;
  dynamic parentQuotaType;
  dynamic slaTime;
  dynamic slaUnit;
  dynamic nextfollowupdate;
  dynamic nextfollowuptime;
  dynamic refMvno;
  dynamic nasPortId;
  String? nasIpAddress;
  String? framedIpv6Address;
  dynamic maxconcurrentsession;
  // List<Null>? custIpMappingList;
  dynamic customerPaymentDto;
  dynamic referenceNo;
  int? earlybilldays;
  int? earlybillday;
  dynamic earlybilldate;
  bool? customerCreated;
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
  dynamic vrfname;
  dynamic vsiid;
  dynamic vsiname;
  dynamic wanip;
  dynamic wanipv6;
  dynamic oldBNGRouterinterface;
  dynamic oldVSIName;
  dynamic oldWANIP;
  dynamic oldLLAccountid;
  dynamic newPlanGroupId;
  dynamic isAddCharge;
  List<ServiceMappingData>? serviceMappingData;

  ChildCustList(
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
        this.debitDocList,
        // this.creditDocuments,
        // this.overChargeList,
        // this.custDocList,
        this.indiChargeList,
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
        this.ipv4,
        this.ipv6,
        this.vlan,
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
        this.vlanId,
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
        // this.customerLocations,
        this.parentQuotaType,
        this.slaTime,
        this.slaUnit,
        this.nextfollowupdate,
        this.nextfollowuptime,
        this.refMvno,
        this.nasPortId,
        this.nasIpAddress,
        this.framedIpv6Address,
        this.maxconcurrentsession,
        // this.custIpMappingList,
        this.customerPaymentDto,
        this.referenceNo,
        this.earlybilldays,
        this.earlybillday,
        this.earlybilldate,
        this.customerCreated,
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
        this.vrfname,
        this.vsiid,
        this.vsiname,
        this.wanip,
        this.wanipv6,
        this.oldBNGRouterinterface,
        this.oldVSIName,
        this.oldWANIP,
        this.oldLLAccountid,
      this.newPlanGroupId,
      this.isAddCharge,
        this.serviceMappingData,
      });

  ChildCustList.fromJson(Map<String, dynamic> json) {
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
    if (json['debitDocList'] != null) {
      debitDocList = <DebitDocList>[];
      json['debitDocList'].forEach((v) {
        debitDocList!.add(new DebitDocList.fromJson(v));
      });
    }
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
    if (json['indiChargeList'] != null) {
      indiChargeList = <IndiChargeList>[];
      json['indiChargeList'].forEach((v) {
        indiChargeList!.add(new IndiChargeList.fromJson(v));
      });
    }
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
    ipv4 = json['ipv4'];
    ipv6 = json['ipv6'];
    vlan = json['vlan'];
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
    vlanId = json['vlan_id'];
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
    // if (json['customerLocations'] != null) {
    //   customerLocations = <Null>[];
    //   json['customerLocations'].forEach((v) {
    //     customerLocations!.add(new Null.fromJson(v));
    //   });
    // }
    parentQuotaType = json['parentQuotaType'];
    slaTime = json['slaTime'];
    slaUnit = json['slaUnit'];
    nextfollowupdate = json['nextfollowupdate'];
    nextfollowuptime = json['nextfollowuptime'];
    refMvno = json['refMvno'];
    nasPortId = json['nasPortId'];
    nasIpAddress = json['nasIpAddress'];
    framedIpv6Address = json['framedIpv6Address'];
    maxconcurrentsession = json['maxconcurrentsession'];
    // if (json['custIpMappingList'] != null) {
    //   custIpMappingList = <Null>[];
    //   json['custIpMappingList'].forEach((v) {
    //     custIpMappingList!.add(new Null.fromJson(v));
    //   });
    // }
    customerPaymentDto = json['customerPaymentDto'];
    referenceNo = json['referenceNo'];
    earlybilldays = json['earlybilldays'];
    earlybillday = json['earlybillday'];
    earlybilldate = json['earlybilldate'];
    customerCreated = json['customerCreated'];
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
    vrfname = json['vrfname'];
    vsiid = json['vsiid'];
    vsiname = json['vsiname'];
    wanip = json['wanip'];
    wanipv6 = json['wanipv6'];
    oldBNGRouterinterface = json['oldBNGRouterinterface'];
    oldVSIName = json['oldVSIName'];
    oldWANIP = json['oldWANIP'];
    oldLLAccountid = json['oldLLAccountid'];
    newPlanGroupId = json['newPlanGroupId'];
    isAddCharge = json['isAddCharge'];

    if (json['serviceMappingData'] != null) {
      serviceMappingData = <ServiceMappingData>[];
      json['serviceMappingData'].forEach((v) {
        serviceMappingData!.add(ServiceMappingData.fromJson(v));
      });
    }
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
    if (this.debitDocList != null) {
      data['debitDocList'] = this.debitDocList!.map((v) => v.toJson()).toList();
    }
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
    if (this.indiChargeList != null) {
      data['indiChargeList'] =
          this.indiChargeList!.map((v) => v.toJson()).toList();
    }
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
    data['ipv4'] = this.ipv4;
    data['ipv6'] = this.ipv6;
    data['vlan'] = this.vlan;
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
    data['vlan_id'] = this.vlanId;
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
    // if (this.customerLocations != null) {
    //   data['customerLocations'] =
    //       this.customerLocations!.map((v) => v.toJson()).toList();
    // }
    data['parentQuotaType'] = this.parentQuotaType;
    data['slaTime'] = this.slaTime;
    data['slaUnit'] = this.slaUnit;
    data['nextfollowupdate'] = this.nextfollowupdate;
    data['nextfollowuptime'] = this.nextfollowuptime;
    data['refMvno'] = this.refMvno;
    data['nasPortId'] = this.nasPortId;
    data['nasIpAddress'] = this.nasIpAddress;
    data['framedIpv6Address'] = this.framedIpv6Address;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    // if (this.custIpMappingList != null) {
    //   data['custIpMappingList'] =
    //       this.custIpMappingList!.map((v) => v.toJson()).toList();
    // }
    data['customerPaymentDto'] = this.customerPaymentDto;
    data['referenceNo'] = this.referenceNo;
    data['earlybilldays'] = this.earlybilldays;
    data['earlybillday'] = this.earlybillday;
    data['earlybilldate'] = this.earlybilldate;
    data['customerCreated'] = this.customerCreated;
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
    data['vrfname'] = this.vrfname;
    data['vsiid'] = this.vsiid;
    data['vsiname'] = this.vsiname;
    data['wanip'] = this.wanip;
    data['wanipv6'] = this.wanipv6;
    data['oldBNGRouterinterface'] = this.oldBNGRouterinterface;
    data['oldVSIName'] = this.oldVSIName;
    data['oldWANIP'] = this.oldWANIP;
    data['oldLLAccountid'] = this.oldLLAccountid;
    data['newPlanGroupId'] = this.newPlanGroupId;
    data['isAddCharge'] = this.isAddCharge;
    if (serviceMappingData != null) {
      data['serviceMappingData'] = this.serviceMappingData!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PlanMappingList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? planId;
  dynamic postpaidPlanPojo;
  dynamic custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  dynamic startDateString;
  dynamic endDateString;
  dynamic expiryDateString;
  dynamic status;
  dynamic qospolicyId;
  dynamic uploadqos;
  dynamic downloadqos;
  dynamic uploadts;
  dynamic downloadts;
  List<QuotaList>? quotaList;
  String? service;
  bool? isDelete;
  double? offerPrice;
  double? taxAmount;
  dynamic creditdocid;
  double? walletBalUsed;
  String? purchaseType;
  dynamic onlinePurchaseId;
  String? purchaseFrom;
  int? debitdocid;
  dynamic validity;
  dynamic planName;
  double? discount;
  dynamic plangroupid;
  int? planValidityDays;
  bool? isInvoiceToOrg;
  String? billTo;
  dynamic newAmount;
  dynamic renewalId;
  dynamic custRefId;
  dynamic custRefName;
  dynamic expiry;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  int? graceDays;
  int? custServiceMappingId;
  dynamic plangroup;
  int? serviceId;
  dynamic ezyBillServiceId;
  dynamic oldDiscount;
  dynamic remarks;
  String? invoiceType;
  dynamic traildebitdocid;
  double? isTrialValidityDays;
  int? trialPlanValidityCount;
  dynamic ezBillPackageId;
  dynamic casId;
  dynamic invoiceformat;
  dynamic billableCustomerId;
  dynamic unitsOfValidity;
  dynamic extendValidityremarks;
  dynamic linkAcceptanceDTO;
  dynamic extendDate;
  String? discountType;
  dynamic discountExpiryDate;
  dynamic startServiceDate;
  dynamic cprIdForPromiseToPay;
  bool? isHold;
  dynamic isVoid;
  dynamic isContainsCustomerInvoice;
  dynamic customerCpr;
  String? serialNumber;
  dynamic voucherId;
  bool? serviceThroughLead;

  PlanMappingList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.planId,
        this.postpaidPlanPojo,
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
        this.quotaList,
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
        this.linkAcceptanceDTO,
        this.extendDate,
        this.discountType,
        this.discountExpiryDate,
        this.startServiceDate,
        this.cprIdForPromiseToPay,
        this.isHold,
        this.isVoid,
        this.isContainsCustomerInvoice,
        this.customerCpr,
        this.serialNumber,
        this.voucherId,
        this.serviceThroughLead});

  PlanMappingList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planId = json['planId'];
    postpaidPlanPojo = json['postpaidPlanPojo'];
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
    if (json['quotaList'] != null) {
      quotaList = <QuotaList>[];
      json['quotaList'].forEach((v) {
        quotaList!.add(new QuotaList.fromJson(v));
      });
    }
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
    linkAcceptanceDTO = json['linkAcceptanceDTO'];
    extendDate = json['extendDate'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    startServiceDate = json['startServiceDate'];
    cprIdForPromiseToPay = json['cprIdForPromiseToPay'];
    isHold = json['isHold'];
    isVoid = json['isVoid'];
    isContainsCustomerInvoice = json['isContainsCustomerInvoice'];
    customerCpr = json['customerCpr'];
    serialNumber = json['serialNumber'];
    voucherId = json['voucherId'];
    serviceThroughLead = json['serviceThroughLead'];
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
    data['postpaidPlanPojo'] = this.postpaidPlanPojo;
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
    if (this.quotaList != null) {
      data['quotaList'] = this.quotaList!.map((v) => v.toJson()).toList();
    }
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
    data['linkAcceptanceDTO'] = this.linkAcceptanceDTO;
    data['extendDate'] = this.extendDate;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['startServiceDate'] = this.startServiceDate;
    data['cprIdForPromiseToPay'] = this.cprIdForPromiseToPay;
    data['isHold'] = this.isHold;
    data['isVoid'] = this.isVoid;
    data['isContainsCustomerInvoice'] = this.isContainsCustomerInvoice;
    data['customerCpr'] = this.customerCpr;
    data['serialNumber'] = this.serialNumber;
    data['voucherId'] = this.voucherId;
    data['serviceThroughLead'] = this.serviceThroughLead;
    return data;
  }
}

class QuotaList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  dynamic planGroup;
  int? id;
  int? planId;
  String? quotaType;
  dynamic custQuotaType;
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
  dynamic planName;
  dynamic cprId;
  double? currentSessionUsageTime;
  double? currentSessionUsageVolume;
  String? lastQuotaReset;
  String? parentQuotaType;
  dynamic reservedQuotaInPer;
  dynamic totalReservedQuota;
  dynamic upstreamprofileuid;
  dynamic downstreamprofileuid;
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
        this.upstreamprofileuid,
        this.downstreamprofileuid,
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
    upstreamprofileuid = json['upstreamprofileuid'];
    downstreamprofileuid = json['downstreamprofileuid'];
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
    data['upstreamprofileuid'] = this.upstreamprofileuid;
    data['downstreamprofileuid'] = this.downstreamprofileuid;
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
  String? landmark1;
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

class DebitDocList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? docnumber;
  int? planId;
  String? billdate;
  String? startdate;
  String? endate;
  String? duedate;
  String? latepaymentdate;
  double? subtotal;
  double? tax;
  double? discount;
  double? totalamount;
  double? previousbalance;
  double? latepaymentfee;
  double? currentpayment;
  double? currentdebit;
  dynamic currentcredit;
  double? totaldue;
  dynamic amountinwords;
  dynamic dueinwords;
  dynamic billrunid;
  String? billrunstatus;
  dynamic document;
  bool? isDelete;
  dynamic cstchargeid;
  dynamic custid;
  dynamic customerName;
  dynamic custType;
  String? paymentStatus;
  double? adjustedAmount;
  // List<Null>? creditDocumentList;
  dynamic custRefName;
  dynamic refundAbleAmount;
  // List<Null>? debitDocumentTAXRels;
  int? nextStaff;
  dynamic nextTeamHierarchyMappingId;
  String? status;
  dynamic debitDocDetails;
  bool? isDirectChargeInvoice;
  dynamic lcoId;
  dynamic paymentowner;
  dynamic purchaseorder;
  dynamic billableToName;
  // List<Null>? debitDocumentInventoryRels;
  dynamic isPromiseToPayInOldCPR;
  dynamic promiseToPayHoldDays;
  dynamic promiseStartDate;
  dynamic promiseEndDate;
  dynamic isCNEnable;
  dynamic invoiceCancelRemarks;
  double? pendingAmt;

  DebitDocList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.docnumber,
        this.planId,
        this.billdate,
        this.startdate,
        this.endate,
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
        this.custid,
        this.customerName,
        this.custType,
        this.paymentStatus,
        this.adjustedAmount,
        // this.creditDocumentList,
        this.custRefName,
        this.refundAbleAmount,
        // this.debitDocumentTAXRels,
        this.nextStaff,
        this.nextTeamHierarchyMappingId,
        this.status,
        this.debitDocDetails,
        this.isDirectChargeInvoice,
        this.lcoId,
        this.paymentowner,
        this.purchaseorder,
        this.billableToName,
        // this.debitDocumentInventoryRels,
        this.isPromiseToPayInOldCPR,
        this.promiseToPayHoldDays,
        this.promiseStartDate,
        this.promiseEndDate,
        this.isCNEnable,
        this.invoiceCancelRemarks,
        this.pendingAmt});

  DebitDocList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    docnumber = json['docnumber'];
    planId = json['planId'];
    billdate = json['billdate'];
    startdate = json['startdate'];
    endate = json['endate'];
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
    custid = json['custid'];
    customerName = json['customerName'];
    custType = json['custType'];
    paymentStatus = json['paymentStatus'];
    adjustedAmount = json['adjustedAmount'];
    // if (json['creditDocumentList'] != null) {
    //   creditDocumentList = <Null>[];
    //   json['creditDocumentList'].forEach((v) {
    //     creditDocumentList!.add(new Null.fromJson(v));
    //   });
    // }
    custRefName = json['custRefName'];
    refundAbleAmount = json['refundAbleAmount'];
    // if (json['debitDocumentTAXRels'] != null) {
    //   debitDocumentTAXRels = <Null>[];
    //   json['debitDocumentTAXRels'].forEach((v) {
    //     debitDocumentTAXRels!.add(new Null.fromJson(v));
    //   });
    // }
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    status = json['status'];
    debitDocDetails = json['debitDocDetails'];
    isDirectChargeInvoice = json['isDirectChargeInvoice'];
    lcoId = json['lcoId'];
    paymentowner = json['paymentowner'];
    purchaseorder = json['purchaseorder'];
    billableToName = json['billableToName'];
    // if (json['debitDocumentInventoryRels'] != null) {
    //   debitDocumentInventoryRels = <Null>[];
    //   json['debitDocumentInventoryRels'].forEach((v) {
    //     debitDocumentInventoryRels!.add(new Null.fromJson(v));
    //   });
    // }
    isPromiseToPayInOldCPR = json['isPromiseToPayInOldCPR'];
    promiseToPayHoldDays = json['promiseToPayHoldDays'];
    promiseStartDate = json['promiseStartDate'];
    promiseEndDate = json['promiseEndDate'];
    isCNEnable = json['isCNEnable'];
    invoiceCancelRemarks = json['invoiceCancelRemarks'];
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
    data['planId'] = this.planId;
    data['billdate'] = this.billdate;
    data['startdate'] = this.startdate;
    data['endate'] = this.endate;
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
    data['custid'] = this.custid;
    data['customerName'] = this.customerName;
    data['custType'] = this.custType;
    data['paymentStatus'] = this.paymentStatus;
    data['adjustedAmount'] = this.adjustedAmount;
    // if (this.creditDocumentList != null) {
    //   data['creditDocumentList'] =
    //       this.creditDocumentList!.map((v) => v.toJson()).toList();
    // }
    data['custRefName'] = this.custRefName;
    data['refundAbleAmount'] = this.refundAbleAmount;
    // if (this.debitDocumentTAXRels != null) {
    //   data['debitDocumentTAXRels'] =
    //       this.debitDocumentTAXRels!.map((v) => v.toJson()).toList();
    // }
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['status'] = this.status;
    data['debitDocDetails'] = this.debitDocDetails;
    data['isDirectChargeInvoice'] = this.isDirectChargeInvoice;
    data['lcoId'] = this.lcoId;
    data['paymentowner'] = this.paymentowner;
    data['purchaseorder'] = this.purchaseorder;
    data['billableToName'] = this.billableToName;
    // if (this.debitDocumentInventoryRels != null) {
    //   data['debitDocumentInventoryRels'] =
    //       this.debitDocumentInventoryRels!.map((v) => v.toJson()).toList();
    // }
    data['isPromiseToPayInOldCPR'] = this.isPromiseToPayInOldCPR;
    data['promiseToPayHoldDays'] = this.promiseToPayHoldDays;
    data['promiseStartDate'] = this.promiseStartDate;
    data['promiseEndDate'] = this.promiseEndDate;
    data['isCNEnable'] = this.isCNEnable;
    data['invoiceCancelRemarks'] = this.invoiceCancelRemarks;
    data['pendingAmt'] = this.pendingAmt;
    return data;
  }
}

class IndiChargeList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? planid;
  dynamic chargePojo;
  int? chargeid;
  dynamic chargeName;
  String? chargetype;
  int? validity;
  double? price;
  double? actualprice;
  dynamic remarks;
  String? chargeDate;
  dynamic chargeDateString;
  String? startdate;
  dynamic startdateString;
  String? enddate;
  dynamic expiry;
  dynamic enddateString;
  double? taxamount;
  bool? isReversed;
  dynamic revDate;
  dynamic revdateString;
  dynamic revAmt;
  dynamic revRemarks;
  bool? isUsed;
  dynamic purchaseEntityId;
  dynamic ippooldtlsid;
  dynamic debitdocid;
  dynamic createDateString;
  dynamic updateDateString;
  String? type;
  int? planValidity;
  String? unitsOfValidity;
  int? taxId;
  int? custPlanMapppingId;
  dynamic lastBillDate;
  String? nextBillDate;
  int? billingCycle;
  bool? isDeleted;
  dynamic dbr;
  dynamic custServiceMappingId;
  dynamic discount;
  bool? isInvoiceToOrg;
  String? billTo;
  double? newAmount;
  dynamic staticIPAdrress;
  String? connectionNo;
  dynamic isRenew;
  dynamic taxInPer;

  IndiChargeList({this.createdate,
    this.updatedate,
    this.createdByName,
    this.lastModifiedByName,
    this.createdById,
    this.lastModifiedById,
    this.id,
    this.planid,
    this.chargePojo,
    this.chargeid,
    this.chargeName,
    this.chargetype,
    this.validity,
    this.price,
    this.actualprice,
    this.remarks,
    this.chargeDate,
    this.chargeDateString,
    this.startdate,
    this.startdateString,
    this.enddate,
    this.expiry,
    this.enddateString,
    this.taxamount,
    this.isReversed,
    this.revDate,
    this.revdateString,
    this.revAmt,
    this.revRemarks,
    this.isUsed,
    this.purchaseEntityId,
    this.ippooldtlsid,
    this.debitdocid,
    this.createDateString,
    this.updateDateString,
    this.type,
    this.planValidity,
    this.unitsOfValidity,
    this.taxId,
    this.custPlanMapppingId,
    this.lastBillDate,
    this.nextBillDate,
    this.billingCycle,
    this.isDeleted,
    this.dbr,
    this.custServiceMappingId,
    this.discount,
    this.isInvoiceToOrg,
    this.billTo,
    this.newAmount,
    this.staticIPAdrress,
    this.connectionNo,
    this.isRenew,
    this.taxInPer});

  IndiChargeList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planid = json['planid'];
    chargePojo = json['chargePojo'];
    chargeid = json['chargeid'];
    chargeName = json['chargeName'];
    chargetype = json['chargetype'];
    validity = json['validity'];
    price = json['price'];
    actualprice = json['actualprice'];
    remarks = json['remarks'];
    chargeDate = json['charge_date'];
    chargeDateString = json['chargeDateString'];
    startdate = json['startdate'];
    startdateString = json['startdateString'];
    enddate = json['enddate'];
    expiry = json['expiry'];
    enddateString = json['enddateString'];
    taxamount = json['taxamount'];
    isReversed = json['is_reversed'];
    revDate = json['rev_date'];
    revdateString = json['revdateString'];
    revAmt = json['rev_amt'];
    revRemarks = json['rev_remarks'];
    isUsed = json['isUsed'];
    purchaseEntityId = json['purchaseEntityId'];
    ippooldtlsid = json['ippooldtlsid'];
    debitdocid = json['debitdocid'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    type = json['type'];
    planValidity = json['planValidity'];
    unitsOfValidity = json['unitsOfValidity'];
    taxId = json['taxId'];
    custPlanMapppingId = json['custPlanMapppingId'];
    lastBillDate = json['lastBillDate'];
    nextBillDate = json['nextBillDate'];
    billingCycle = json['billingCycle'];
    isDeleted = json['isDeleted'];
    dbr = json['dbr'];
    custServiceMappingId = json['custServiceMappingId'];
    discount = json['discount'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    billTo = json['billTo'];
    newAmount = json['newAmount'];
    staticIPAdrress = json['staticIPAdrress'];
    connectionNo = json['connection_no'];
    isRenew = json['isRenew'];
    taxInPer = json['taxInPer'];
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
    data['planid'] = this.planid;
    data['chargePojo'] = this.chargePojo;
    data['chargeid'] = this.chargeid;
    data['chargeName'] = this.chargeName;
    data['chargetype'] = this.chargetype;
    data['validity'] = this.validity;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['remarks'] = this.remarks;
    data['charge_date'] = this.chargeDate;
    data['chargeDateString'] = this.chargeDateString;
    data['startdate'] = this.startdate;
    data['startdateString'] = this.startdateString;
    data['enddate'] = this.enddate;
    data['expiry'] = this.expiry;
    data['enddateString'] = this.enddateString;
    data['taxamount'] = this.taxamount;
    data['is_reversed'] = this.isReversed;
    data['rev_date'] = this.revDate;
    data['revdateString'] = this.revdateString;
    data['rev_amt'] = this.revAmt;
    data['rev_remarks'] = this.revRemarks;
    data['isUsed'] = this.isUsed;
    data['purchaseEntityId'] = this.purchaseEntityId;
    data['ippooldtlsid'] = this.ippooldtlsid;
    data['debitdocid'] = this.debitdocid;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['type'] = this.type;
    data['planValidity'] = this.planValidity;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['taxId'] = this.taxId;
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['lastBillDate'] = this.lastBillDate;
    data['nextBillDate'] = this.nextBillDate;
    data['billingCycle'] = this.billingCycle;
    data['isDeleted'] = this.isDeleted;
    data['dbr'] = this.dbr;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['discount'] = this.discount;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['billTo'] = this.billTo;
    data['newAmount'] = this.newAmount;
    data['staticIPAdrress'] = this.staticIPAdrress;
    data['connection_no'] = this.connectionNo;
    data['isRenew'] = this.isRenew;
    data['taxInPer'] = this.taxInPer;
    return data;
  }
}


class ServiceMappingData {
  bool changeFlag;
  dynamic newPlanSelection;

  ServiceMappingData({required this.changeFlag, this.newPlanSelection});

  // Optionally, you can add a method to easily create instances from a Map (e.g., from JSON)
  factory ServiceMappingData.fromJson(Map<String, dynamic> json) {
    return ServiceMappingData(
      changeFlag: json['changeFlag'] ?? false,
      newPlanSelection: json['newPlanSelection'],
    );
  }

  // You can also add a method to convert an instance to a Map
  Map<String, dynamic> toJson() {
    return {
      'changeFlag': changeFlag,
      'newPlanSelection': newPlanSelection,
    };
  }
}