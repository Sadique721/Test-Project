import 'package:savbill/webservices/base_response.dart';

class CustomerBasicLeadDetailsRes  extends BaseResponse{
  CustomerBasicLeadCustomers? customers;
  String? timestamp;
  int? status;

  CustomerBasicLeadDetailsRes({this.customers, this.timestamp, this.status});

  CustomerBasicLeadDetailsRes.fromJson(Map<String, dynamic> json) {
    customers = json['customers'] != null
        ? new CustomerBasicLeadCustomers.fromJson(json['customers'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customers != null) {
      data['customers'] = this.customers!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustomerBasicLeadCustomers {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
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
  dynamic failcount;
  String? acctno;
  String? custtype;
  String? phone;
  Null? billday;
  dynamic partnerid;
  Null? onuid;
  String? nextBillDate;
  Null? lastBillDate;
  Null? addresstype;
  Null? address1;
  Null? address2;
  Null? city;
  Null? state;
  Null? country;
  Null? pincode;
  Null? area;
  dynamic outstanding;
  Null? oldpassword1;
  Null? newpassword;
  Null? oldpassword2;
  Null? oldpassword3;
  String? selfcarepwd;
  dynamic popid;
  dynamic branchId;
  Null? oltid;
  Null? oltName;
  Null? masterdbid;
  Null? masterdbName;
  Null? splitterid;
  Null? splitterName;
  String? lastPasswordChange;
  String? lastpasswordchangestring;
  Null? framedIpBind;
  Null? ipPoolNameBind;
  List<PlanMappingLeadList>? planMappingList;
  // List<Null>? linkAcceptanceList;
  List<AddressList>? addressList;
  // List<Null>? radiusprofileIds;
  List<DebitDocList>? debitDocList;
  List<CreditDocuments>? creditDocuments;
  // List<Null>? overChargeList;
  // List<Null>? custDocList;
  List<IndiChargeList>? indiChargeList;
  Null? custLeger;
  // List<Null>? custMacMapppingList;
  // List<Null>? ledgerDtls;
  Null? paymentDetails;
  Null? flashMsg;
  bool? mactelflag;
  bool? isinvoicestop;
  bool? istrialplan;
  String? mobile;
  String? countryCode;
  String? cafno;
  Null? ipv4;
  Null? ipv6;
  Null? vlan;
  Null? altmobile;
  Null? altphone;
  Null? altemail;
  Null? fax;
  Null? resellerid;
  Null? salesrepid;
  String? voicesrvtype;
  bool? voiceprovision;
  String? didno;
  Null? childdidno;
  Null? intercomno;
  Null? intercomgrp;
  bool? onlinerenewalflag;
  bool? voipenableflag;
  bool? isorgcust;
  Null? custcategory;
  dynamic walletbalance;
  Null? networktype;
  Null? defaultpoolid;
  dynamic serviceareaid;
  Null? networkdevicesid;
  Null? oltslotid;
  Null? oltportid;
  Null? strconntype;
  Null? stroltname;
  Null? strslotname;
  Null? strportname;
  Null? vlanId;
  Null? billentityname;
  Null? addparam1;
  Null? addparam2;
  Null? addparam3;
  Null? addparam4;
  Null? purchaseorder;
  Null? remarks;
  Null? allowedIPAddress;
  String? firstActivationDate;
  bool? isDeleted;
  String? createDateString;
  String? updateDateString;
  Null? latitude;
  Null? longitude;
  Null? url;
  Null? gisCode;
  String? salesremark;
  String? servicetype;
  Null? isCustCaf;
  Null? nextTeamHierarchyMapping;
  String? serviceareaName;
  String? cafApproveStatus;
  dynamic mvnoId;
  Null? tinNo;
  String? passportNo;
  Null? dunningCategory;
  Null? plangroupid;
  Null? planGroupDTO;
  Null? parentCustomerId;
  Null? parentCustomerName;
  Null? invoiceType;
  String? calendarType;
  dynamic discount;
  Null? buId;
  Null? custPackageId;
  Null? partnerLedgerMappingId;
  String? planPurchaseType;
  Null? leadSource;
  Null? feasibilityRequired;
  dynamic branch;
  String? branchName;
  String? regionName;
  String? buVerticals;
  Null? valleyType;
  Null? customerArea;
  Null? customerType;
  Null? customerSubType;
  Null? customerSector;
  Null? customerSubSector;
  Null? lcoId;
  bool? isFromPwc;
  Null? leadId;
  Null? leadNo;
  Null? oldDebitDocId;
  Null? nasPort;
  Null? framedIp;
  Null? flatAmount;
  Null? ezyBillCustomersId;
  Null? ezyBillAccountNumber;
  Null? creditDocumentId;
  Null? isFromFlutterWave;
  Null? paymentOwner;
  Null? ezyBillStockId;
  Null? feasibility;
  Null? feasibilityRemark;
  String? custlabel;
  Null? staffId;
  Null? dunningSubSector;
  Null? dunningSubType;
  Null? dunningType;
  Null? dunningSector;
  Null? registrationDate;
  Null? planName;
  Null? billableCustomerId;
  Null? currentAssigneeId;
  Null? rejectReasonId;
  Null? rejectSubReasonId;
  Null? rejectReasonName;
  Null? rejectSubReasonName;
  Null? businessType;
  String? discountType;
  Null? discountExpiryDate;
  Null? paymentOwnerId;
  Null? additionalemail;
  Null? salesrepresentative;
  Null? skypeidImid;
  Null? organisation;
  Null? rating;
  Null? automaticnotification;
  Null? locationlevel1;
  Null? locationlevel2;
  Null? locationlevel3;
  Null? locationlevel4;
  Null? ponumber;
  Null? customerbillingid;
  Null? businessunit;
  Null? subbusinessunit;
  Null? isDunningActivate;
  Null? dunningActivateFor;
  Null? lastDunningDate;
  bool? isDunningEnable;
  Null? dunningAction;
  bool? isNotificationEnable;
  Null? parentExperience;
  String? lastStatusChangeDate;
  Null? popName;
  Null? department;
  bool? hasChildCust;
  Null? subscriptionMode;
  Null? validFrom;
  Null? validUpto;
  Null? locations;
  Null? voucherCode;
  Null? cid;
  Null? birthDate;
  // List<Null>? customerLocations;
  String? parentQuotaType;
  Null? slaTime;
  Null? slaUnit;
  Null? nextfollowupdate;
  Null? nextfollowuptime;
  Null? refMvno;
  Null? nasPortId;
  Null? nasIpAddress;
  Null? framedIpv6Address;
  Null? maxconcurrentsession;
  // List<Null>? custIpMappingList;
  Null? customerPaymentDto;
  Null? referenceNo;
  Null? asnnumber;
  Null? bngrouterinterface;
  Null? bngroutername;
  Null? ipprefixes;
  Null? ipv6Prefixes;
  Null? lanip;
  Null? lanipv6;
  Null? llaccountid;
  Null? llconnectiontype;
  Null? llexpirydate;
  Null? llmedium;
  Null? llserviceid;
  String? macaddress;
  Null? peerip;
  Null? poolip;
  Null? qos;
  Null? rdexport;
  Null? rdvalue;
  Null? vrfname;
  Null? vsiid;
  Null? vsiname;
  Null? wanip;
  Null? wanipv6;
  Null? oldBNGRouterinterface;
  Null? oldVSIName;
  Null? oldWANIP;
  Null? oldLLAccountid;

  CustomerBasicLeadCustomers(
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
        this.branchId,
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
        this.creditDocuments,
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
        this.oldLLAccountid});

  CustomerBasicLeadCustomers.fromJson(Map<String, dynamic> json) {
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
    branchId = json['branchId'];
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
      planMappingList = <PlanMappingLeadList>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingLeadList.fromJson(v));
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
    if (json['creditDocuments'] != null) {
      creditDocuments = <CreditDocuments>[];
      json['creditDocuments'].forEach((v) {
        creditDocuments!.add(new CreditDocuments.fromJson(v));
      });
    }
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
    data['branchId'] = this.branchId;
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
    if (this.creditDocuments != null) {
      data['creditDocuments'] =
          this.creditDocuments!.map((v) => v.toJson()).toList();
    }
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
    return data;
  }
}

class PlanMappingLeadList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic planId;
  Null? postpaidPlanPojo;
  Null? custid;
  String? startDate;
  String? endDate;
  String? expiryDate;
  Null? startDateString;
  Null? endDateString;
  Null? expiryDateString;
  Null? status;
  dynamic qospolicyId;
  Null? uploadqos;
  Null? downloadqos;
  Null? uploadts;
  Null? downloadts;
  List<QuotaList>? quotaList;
  String? service;
  bool? isDelete;
  dynamic offerPrice;
  dynamic taxAmount;
  Null? creditdocid;
  dynamic walletBalUsed;
  String? purchaseType;
  Null? onlinePurchaseId;
  String? purchaseFrom;
  dynamic debitdocid;
  dynamic validity;
  String? planName;
  dynamic discount;
  Null? plangroupid;
  dynamic planValidityDays;
  bool? isInvoiceToOrg;
  String? billTo;
  Null? newAmount;
  Null? renewalId;
  Null? custRefId;
  Null? custRefName;
  Null? expiry;
  String? custPlanStatus;
  bool? isinvoicestop;
  bool? istrialplan;
  bool? isInvoiceCreated;
  dynamic graceDays;
  dynamic custServiceMappingId;
  String? plangroup;
  dynamic serviceId;
  Null? ezyBillServiceId;
  Null? oldDiscount;
  Null? remarks;
  Null? invoiceType;
  Null? traildebitdocid;
  dynamic isTrialValidityDays;
  dynamic trialPlanValidityCount;
  Null? ezBillPackageId;
  Null? casId;
  Null? invoiceformat;
  Null? billableCustomerId;
  String? unitsOfValidity;
  Null? extendValidityremarks;
  LinkAcceptanceDTO? linkAcceptanceDTO;
  Null? extendDate;
  String? discountType;
  Null? discountExpiryDate;
  Null? startServiceDate;
  Null? cprIdForPromiseToPay;
  bool? isHold;
  Null? isVoid;
  Null? isContainsCustomerInvoice;
  Null? customerCpr;
  Null? serialNumber;
  Null? voucherId;
  bool? serviceThroughLead;

  PlanMappingLeadList(
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

  PlanMappingLeadList.fromJson(Map<String, dynamic> json) {
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
    linkAcceptanceDTO = json['linkAcceptanceDTO'] != null
        ? new LinkAcceptanceDTO.fromJson(json['linkAcceptanceDTO'])
        : null;
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
    if (this.linkAcceptanceDTO != null) {
      data['linkAcceptanceDTO'] = this.linkAcceptanceDTO!.toJson();
    }
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
  dynamic createdById;
  dynamic lastModifiedById;
  Null? planGroup;
  dynamic id;
  dynamic planId;
  String? quotaType;
  Null? custQuotaType;
  dynamic totalQuota;
  dynamic usedQuota;
  String? quotaUnit;
  dynamic timeTotalQuota;
  dynamic timeQuotaUsed;
  Null? timeQuotaUnit;
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
  dynamic currentSessionUsageTime;
  dynamic currentSessionUsageVolume;
  String? lastQuotaReset;
  Null? parentQuotaType;
  Null? reservedQuotaInPer;
  Null? totalReservedQuota;
  Null? upstreamprofileuid;
  Null? downstreamprofileuid;
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

class AddressList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? addressType;
  Null? address1;
  Null? address2;
  String? landmark;
  Null? landmark1;
  dynamic areaId;
  dynamic pincodeId;
  dynamic cityId;
  dynamic stateId;
  dynamic countryId;
  Null? customerId;
  String? fullAddress;
  bool? isDelete;
  Null? nextTeamHierarchyMappingId;
  Null? nextStaff;
  Null? status;
  String? version;
  Null? shiftId;
  Null? shiftedPartnerId;
  Null? shiftedServiceAreaId;
  Null? requestedByName;
  Null? requestedDate;
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
  Null? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? docnumber;
  dynamic planId;
  String? billdate;
  String? startdate;
  String? endate;
  String? duedate;
  String? latepaymentdate;
  dynamic subtotal;
  dynamic tax;
  dynamic discount;
  dynamic totalamount;
  dynamic previousbalance;
  dynamic latepaymentfee;
  dynamic currentpayment;
  dynamic currentdebit;
  Null? currentcredit;
  dynamic totaldue;
  Null? amountinwords;
  Null? dueinwords;
  Null? billrunid;
  String? billrunstatus;
  Null? document;
  bool? isDelete;
  Null? cstchargeid;
  Null? custid;
  Null? customerName;
  Null? custType;
  String? paymentStatus;
  dynamic adjustedAmount;
  List<CreditDocumentList>? creditDocumentList;
  Null? custRefName;
  Null? refundAbleAmount;
  // List<Null>? debitDocumentTAXRels;
  dynamic nextStaff;
  Null? nextTeamHierarchyMappingId;
  String? status;
  Null? debitDocDetails;
  bool? isDirectChargeInvoice;
  Null? lcoId;
  Null? paymentowner;
  Null? purchaseorder;
  Null? billableToName;
  // List<Null>? debitDocumentInventoryRels;
  Null? isPromiseToPayInOldCPR;
  Null? promiseToPayHoldDays;
  Null? promiseStartDate;
  Null? promiseEndDate;
  Null? isCNEnable;
  Null? invoiceCancelRemarks;
  dynamic pendingAmt;

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
        this.creditDocumentList,
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
    if (json['creditDocumentList'] != null) {
      creditDocumentList = <CreditDocumentList>[];
      json['creditDocumentList'].forEach((v) {
        creditDocumentList!.add(new CreditDocumentList.fromJson(v));
      });
    }
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
    if (this.creditDocumentList != null) {
      data['creditDocumentList'] =
          this.creditDocumentList!.map((v) => v.toJson()).toList();
    }
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

class CreditDocumentList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  Null? lastModifiedByName;
  dynamic createdById;
  Null? lastModifiedById;
  dynamic id;
  String? paymode;
  String? paymentdate;
  Null? chequedate;
  Null? paydetails1;
  Null? paydetails2;
  Null? paydetails3;
  String? paydetails4;
  dynamic amount;
  String? status;
  dynamic approverid;
  String? remarks;
  String? referenceno;
  Null? xmldocument;
  bool? isDelete;
  Null? tdsflag;
  dynamic tdsamount;
  Null? isReversed;
  Null? resevrsedDate;
  Null? resverseDebitdocId;
  Null? tdsReceived;
  Null? tdsReceivedDate;
  Null? tdsCreditDocId;
  dynamic mvnoId;
  Null? buID;
  Null? lcoid;
  dynamic invoiceId;
  String? paytype;
  String? type;
  Null? nextTeamHierarchyMappingId;
  String? reciptNo;
  Null? paymentreferenceno;
  List<DebitDocumentList>? debitDocumentList;
  dynamic adjustedAmount;
  Null? bankManagement;
  Null? destinationBank;
  Null? filename;
  Null? uniquename;
  Null? barteramount;
  dynamic abbsAmount;
  Null? branchname;
  Null? onlinesource;
  Null? creditdocumentno;
  Null? ledgerId;
  Null? batchAssigned;
  Null? mvnoName;
  dynamic remainingAmount;
  Null? invoiceNumber;

  CreditDocumentList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.paymode,
        this.paymentdate,
        this.chequedate,
        this.paydetails1,
        this.paydetails2,
        this.paydetails3,
        this.paydetails4,
        this.amount,
        this.status,
        this.approverid,
        this.remarks,
        this.referenceno,
        this.xmldocument,
        this.isDelete,
        this.tdsflag,
        this.tdsamount,
        this.isReversed,
        this.resevrsedDate,
        this.resverseDebitdocId,
        this.tdsReceived,
        this.tdsReceivedDate,
        this.tdsCreditDocId,
        this.mvnoId,
        this.buID,
        this.lcoid,
        this.invoiceId,
        this.paytype,
        this.type,
        this.nextTeamHierarchyMappingId,
        this.reciptNo,
        this.paymentreferenceno,
        this.debitDocumentList,
        this.adjustedAmount,
        this.bankManagement,
        this.destinationBank,
        this.filename,
        this.uniquename,
        this.barteramount,
        this.abbsAmount,
        this.branchname,
        this.onlinesource,
        this.creditdocumentno,
        this.ledgerId,
        this.batchAssigned,
        this.mvnoName,
        this.remainingAmount,
        this.invoiceNumber});

  CreditDocumentList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    paymode = json['paymode'];
    paymentdate = json['paymentdate'];
    chequedate = json['chequedate'];
    paydetails1 = json['paydetails1'];
    paydetails2 = json['paydetails2'];
    paydetails3 = json['paydetails3'];
    paydetails4 = json['paydetails4'];
    amount = json['amount'];
    status = json['status'];
    approverid = json['approverid'];
    remarks = json['remarks'];
    referenceno = json['referenceno'];
    xmldocument = json['xmldocument'];
    isDelete = json['isDelete'];
    tdsflag = json['tdsflag'];
    tdsamount = json['tdsamount'];
    isReversed = json['is_reversed'];
    resevrsedDate = json['resevrsed_date'];
    resverseDebitdocId = json['resverse_debitdoc_id'];
    tdsReceived = json['tds_received'];
    tdsReceivedDate = json['tds_received_date'];
    tdsCreditDocId = json['tds_credit_doc_id'];
    mvnoId = json['mvnoId'];
    buID = json['buID'];
    lcoid = json['lcoid'];
    invoiceId = json['invoiceId'];
    paytype = json['paytype'];
    type = json['type'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    reciptNo = json['reciptNo'];
    paymentreferenceno = json['paymentreferenceno'];
    if (json['debitDocumentList'] != null) {
      debitDocumentList = <DebitDocumentList>[];
      json['debitDocumentList'].forEach((v) {
        debitDocumentList!.add(new DebitDocumentList.fromJson(v));
      });
    }
    adjustedAmount = json['adjustedAmount'];
    bankManagement = json['bankManagement'];
    destinationBank = json['destinationBank'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    barteramount = json['barteramount'];
    abbsAmount = json['abbsAmount'];
    branchname = json['branchname'];
    onlinesource = json['onlinesource'];
    creditdocumentno = json['creditdocumentno'];
    ledgerId = json['ledgerId'];
    batchAssigned = json['batchAssigned'];
    mvnoName = json['mvnoName'];
    remainingAmount = json['remainingAmount'];
    invoiceNumber = json['invoiceNumber'];
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
    data['paymode'] = this.paymode;
    data['paymentdate'] = this.paymentdate;
    data['chequedate'] = this.chequedate;
    data['paydetails1'] = this.paydetails1;
    data['paydetails2'] = this.paydetails2;
    data['paydetails3'] = this.paydetails3;
    data['paydetails4'] = this.paydetails4;
    data['amount'] = this.amount;
    data['status'] = this.status;
    data['approverid'] = this.approverid;
    data['remarks'] = this.remarks;
    data['referenceno'] = this.referenceno;
    data['xmldocument'] = this.xmldocument;
    data['isDelete'] = this.isDelete;
    data['tdsflag'] = this.tdsflag;
    data['tdsamount'] = this.tdsamount;
    data['is_reversed'] = this.isReversed;
    data['resevrsed_date'] = this.resevrsedDate;
    data['resverse_debitdoc_id'] = this.resverseDebitdocId;
    data['tds_received'] = this.tdsReceived;
    data['tds_received_date'] = this.tdsReceivedDate;
    data['tds_credit_doc_id'] = this.tdsCreditDocId;
    data['mvnoId'] = this.mvnoId;
    data['buID'] = this.buID;
    data['lcoid'] = this.lcoid;
    data['invoiceId'] = this.invoiceId;
    data['paytype'] = this.paytype;
    data['type'] = this.type;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['reciptNo'] = this.reciptNo;
    data['paymentreferenceno'] = this.paymentreferenceno;
    if (this.debitDocumentList != null) {
      data['debitDocumentList'] =
          this.debitDocumentList!.map((v) => v.toJson()).toList();
    }
    data['adjustedAmount'] = this.adjustedAmount;
    data['bankManagement'] = this.bankManagement;
    data['destinationBank'] = this.destinationBank;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['barteramount'] = this.barteramount;
    data['abbsAmount'] = this.abbsAmount;
    data['branchname'] = this.branchname;
    data['onlinesource'] = this.onlinesource;
    data['creditdocumentno'] = this.creditdocumentno;
    data['ledgerId'] = this.ledgerId;
    data['batchAssigned'] = this.batchAssigned;
    data['mvnoName'] = this.mvnoName;
    data['remainingAmount'] = this.remainingAmount;
    data['invoiceNumber'] = this.invoiceNumber;
    return data;
  }
}

class DebitDocumentList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  Null? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? docnumber;
  Null? operationType;
  PostpaidPlan? postpaidPlan;
  String? billdate;
  Null? localbilldate;
  String? startdate;
  String? localstartdate;
  String? endate;
  String? localenddate;
  String? duedate;
  String? latepaymentdate;
  dynamic subtotal;
  dynamic tax;
  dynamic discount;
  dynamic totalamount;
  dynamic previousbalance;
  dynamic latepaymentfee;
  dynamic currentpayment;
  dynamic currentdebit;
  Null? currentcredit;
  dynamic totaldue;
  Null? amountinwords;
  Null? dueinwords;
  Null? billrunid;
  String? billrunstatus;
  bool? isDelete;
  Null? cstchargeid;
  Null? isCreditReversal;
  Null? creditDocId;
  String? paymentStatus;
  dynamic adjustedAmount;
  Null? totalCustomerDiscount;
  Null? buId;
  Null? custRefName;
  Null? inventoryMappingId;
  // List<Null>? debitDocumentTAXRels;
  dynamic custpackrelid;
  dynamic nextStaff;
  Null? nextTeamHierarchyMappingId;
  String? status;
  bool? isDirectChargeInvoice;
  Null? lcoId;
  Null? paymentowner;
  Null? purchaseorder;
  // List<Null>? debitDocDetailsList;
  Null? billableToName;
  // List<Null>? debitDocumentInventoryRels;
  Null? staffid;
  Null? isPromiseToPayInOldCPR;
  Null? promiseToPayHoldDays;
  Null? promiseStartDate;
  Null? promiseEndDate;
  Null? isCNEnable;
  Null? invoiceCancelRemarks;
  Null? remarks;
  dynamic pendingAmt;
  Null? duedateString;
  Null? latepaymentdateString;

  DebitDocumentList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.docnumber,
        this.operationType,
        this.postpaidPlan,
        this.billdate,
        this.localbilldate,
        this.startdate,
        this.localstartdate,
        this.endate,
        this.localenddate,
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
        this.isDelete,
        this.cstchargeid,
        this.isCreditReversal,
        this.creditDocId,
        this.paymentStatus,
        this.adjustedAmount,
        this.totalCustomerDiscount,
        this.buId,
        this.custRefName,
        this.inventoryMappingId,
        // this.debitDocumentTAXRels,
        this.custpackrelid,
        this.nextStaff,
        this.nextTeamHierarchyMappingId,
        this.status,
        this.isDirectChargeInvoice,
        this.lcoId,
        this.paymentowner,
        this.purchaseorder,
        // this.debitDocDetailsList,
        this.billableToName,
        // this.debitDocumentInventoryRels,
        this.staffid,
        this.isPromiseToPayInOldCPR,
        this.promiseToPayHoldDays,
        this.promiseStartDate,
        this.promiseEndDate,
        this.isCNEnable,
        this.invoiceCancelRemarks,
        this.remarks,
        this.pendingAmt,
        this.duedateString,
        this.latepaymentdateString});

  DebitDocumentList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    docnumber = json['docnumber'];
    operationType = json['operationType'];
    postpaidPlan = json['postpaidPlan'] != null
        ? new PostpaidPlan.fromJson(json['postpaidPlan'])
        : null;
    billdate = json['billdate'];
    localbilldate = json['localbilldate'];
    startdate = json['startdate'];
    localstartdate = json['localstartdate'];
    endate = json['endate'];
    localenddate = json['localenddate'];
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
    isDelete = json['isDelete'];
    cstchargeid = json['cstchargeid'];
    isCreditReversal = json['is_credit_reversal'];
    creditDocId = json['credit_doc_id'];
    paymentStatus = json['paymentStatus'];
    adjustedAmount = json['adjustedAmount'];
    totalCustomerDiscount = json['totalCustomerDiscount'];
    buId = json['buId'];
    custRefName = json['custRefName'];
    inventoryMappingId = json['inventoryMappingId'];
    // if (json['debitDocumentTAXRels'] != null) {
    //   debitDocumentTAXRels = <Null>[];
    //   json['debitDocumentTAXRels'].forEach((v) {
    //     debitDocumentTAXRels!.add(new Null.fromJson(v));
    //   });
    // }
    custpackrelid = json['custpackrelid'];
    nextStaff = json['nextStaff'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    status = json['status'];
    isDirectChargeInvoice = json['isDirectChargeInvoice'];
    lcoId = json['lcoId'];
    paymentowner = json['paymentowner'];
    purchaseorder = json['purchaseorder'];
    // if (json['debitDocDetailsList'] != null) {
    //   debitDocDetailsList = <Null>[];
    //   json['debitDocDetailsList'].forEach((v) {
    //     debitDocDetailsList!.add(new Null.fromJson(v));
    //   });
    // }
    billableToName = json['billableToName'];
    // if (json['debitDocumentInventoryRels'] != null) {
    //   debitDocumentInventoryRels = <Null>[];
    //   json['debitDocumentInventoryRels'].forEach((v) {
    //     debitDocumentInventoryRels!.add(new Null.fromJson(v));
    //   });
    // }
    staffid = json['staffid'];
    isPromiseToPayInOldCPR = json['isPromiseToPayInOldCPR'];
    promiseToPayHoldDays = json['promiseToPayHoldDays'];
    promiseStartDate = json['promiseStartDate'];
    promiseEndDate = json['promiseEndDate'];
    isCNEnable = json['isCNEnable'];
    invoiceCancelRemarks = json['invoiceCancelRemarks'];
    remarks = json['remarks'];
    pendingAmt = json['pendingAmt'];
    duedateString = json['duedateString'];
    latepaymentdateString = json['latepaymentdateString'];
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
    data['operationType'] = this.operationType;
    if (this.postpaidPlan != null) {
      data['postpaidPlan'] = this.postpaidPlan!.toJson();
    }
    data['billdate'] = this.billdate;
    data['localbilldate'] = this.localbilldate;
    data['startdate'] = this.startdate;
    data['localstartdate'] = this.localstartdate;
    data['endate'] = this.endate;
    data['localenddate'] = this.localenddate;
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
    data['isDelete'] = this.isDelete;
    data['cstchargeid'] = this.cstchargeid;
    data['is_credit_reversal'] = this.isCreditReversal;
    data['credit_doc_id'] = this.creditDocId;
    data['paymentStatus'] = this.paymentStatus;
    data['adjustedAmount'] = this.adjustedAmount;
    data['totalCustomerDiscount'] = this.totalCustomerDiscount;
    data['buId'] = this.buId;
    data['custRefName'] = this.custRefName;
    data['inventoryMappingId'] = this.inventoryMappingId;
    // if (this.debitDocumentTAXRels != null) {
    //   data['debitDocumentTAXRels'] =
    //       this.debitDocumentTAXRels!.map((v) => v.toJson()).toList();
    // }
    data['custpackrelid'] = this.custpackrelid;
    data['nextStaff'] = this.nextStaff;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['status'] = this.status;
    data['isDirectChargeInvoice'] = this.isDirectChargeInvoice;
    data['lcoId'] = this.lcoId;
    data['paymentowner'] = this.paymentowner;
    data['purchaseorder'] = this.purchaseorder;
    // if (this.debitDocDetailsList != null) {
    //   data['debitDocDetailsList'] =
    //       this.debitDocDetailsList!.map((v) => v.toJson()).toList();
    // }
    data['billableToName'] = this.billableToName;
    // if (this.debitDocumentInventoryRels != null) {
    //   data['debitDocumentInventoryRels'] =
    //       this.debitDocumentInventoryRels!.map((v) => v.toJson()).toList();
    // }
    data['staffid'] = this.staffid;
    data['isPromiseToPayInOldCPR'] = this.isPromiseToPayInOldCPR;
    data['promiseToPayHoldDays'] = this.promiseToPayHoldDays;
    data['promiseStartDate'] = this.promiseStartDate;
    data['promiseEndDate'] = this.promiseEndDate;
    data['isCNEnable'] = this.isCNEnable;
    data['invoiceCancelRemarks'] = this.invoiceCancelRemarks;
    data['remarks'] = this.remarks;
    data['pendingAmt'] = this.pendingAmt;
    data['duedateString'] = this.duedateString;
    data['latepaymentdateString'] = this.latepaymentdateString;
    return data;
  }
}

class PostpaidPlan {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? displayName;
  String? code;
  String? desc;
  String? category;
  Null? maxChild;
  String? startDate;
  String? endDate;
  dynamic quota;
  String? quotaUnit;
  Null? uploadQOS;
  Null? downloadQOS;
  Null? uploadTs;
  Null? downloadTs;
  bool? allowOverUsage;
  String? status;
  String? planStatus;
  Null? childQuota;
  Null? childQuotaUnit;
  Null? slice;
  Null? sliceUnit;
  Null? attachedToAllHotSpots;
  Null? param1;
  Null? param2;
  Null? param3;
  dynamic mvnoId;
  Null? taxId;
  dynamic serviceId;
  Null? timebasepolicyId;
  String? plantype;
  double? dbr;
  String? mvnoName;
  List<ChargeList>? chargeList;
  String? planGroup;
  dynamic validity;
  Null? saccode;
  String? maxconcurrentsession;
  Null? quotaunittime;
  Null? quotatime;
  String? quotatype;
  dynamic offerprice;
  Null? quotadid;
  Null? quotaintercom;
  Null? quotaunitdid;
  Null? quotaunitintercom;
  Qospolicy? qospolicy;
  // List<Null>? radiusprofile;
  bool? isDelete;
  Null? dataCategory;
  dynamic taxamount;
  Null? serviceName;
  Null? timebasepolicyName;
  List<ServiceAreaNameList>? serviceAreaNameList;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  Null? buId;
  Null? nextTeamHierarchyMapping;
  Null? nextStaff;
  dynamic newOfferPrice;
  bool? allowdiscount;
  Null? productId;
  // List<Null>? productplanmappingList;
  bool? invoiceToOrg;
  bool? requiredApproval;
  // List<Null>? planCasMappingList;
  String? bandwidth;
  Null? linkType;
  Null? connectionType;
  Null? distance;
  Null? ram;
  Null? cpu;
  Null? storage;
  Null? storageType;
  Null? autoBackup;
  Null? cpanel;
  Null? location;
  Null? quantity;
  Null? packageType;
  Null? numberOfDays;
  Null? noOfUsers;
  Null? rackSpace;
  Null? rackUnit;
  Null? powerConsumption;
  Null? networkCard;
  Null? ipOrIpPool;
  Null? noOfLicense;
  Null? noOfEmailUserLicense;
  Null? noOfServerLicense;
  Null? noOfUserLicense;
  Null? noOfNodes;
  Null? eventPerSecond;
  Null? noOfAdditionalServer;
  Null? noOfAdditionalStorage;
  Null? additionalStorageType;
  Null? epsLicense;
  Null? noOfNodesLicense;
  Null? hardwareResource;
  Null? manPower;
  Null? noOfDomains;
  Null? securityModules;
  Null? hardwareOrServers;
  Null? country;
  Null? noOfVpn;
  Null? deviceThroughput;
  Null? retail;
  Null? businessType;
  bool? basePlan;
  Null? templateId;
  List<PlanQosMappingEntities>? planQosMappingEntities;
  Null? planQosMappingEntityList;
  Null? postPaidPlanServiceAreaMappingList;
  bool? isApprove;
  bool? useQuota;
  Null? chunk;
  Null? accessibility;

  PostpaidPlan(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.displayName,
        this.code,
        this.desc,
        this.category,
        this.maxChild,
        this.startDate,
        this.endDate,
        this.quota,
        this.quotaUnit,
        this.uploadQOS,
        this.downloadQOS,
        this.uploadTs,
        this.downloadTs,
        this.allowOverUsage,
        this.status,
        this.planStatus,
        this.childQuota,
        this.childQuotaUnit,
        this.slice,
        this.sliceUnit,
        this.attachedToAllHotSpots,
        this.param1,
        this.param2,
        this.param3,
        this.mvnoId,
        this.taxId,
        this.serviceId,
        this.timebasepolicyId,
        this.plantype,
        this.dbr,
        this.mvnoName,
        this.chargeList,
        this.planGroup,
        this.validity,
        this.saccode,
        this.maxconcurrentsession,
        this.quotaunittime,
        this.quotatime,
        this.quotatype,
        this.offerprice,
        this.quotadid,
        this.quotaintercom,
        this.quotaunitdid,
        this.quotaunitintercom,
        this.qospolicy,
        // this.radiusprofile,
        this.isDelete,
        this.dataCategory,
        this.taxamount,
        this.serviceName,
        this.timebasepolicyName,
        this.serviceAreaNameList,
        this.quotaResetInterval,
        this.mode,
        this.unitsOfValidity,
        this.buId,
        this.nextTeamHierarchyMapping,
        this.nextStaff,
        this.newOfferPrice,
        this.allowdiscount,
        this.productId,
        // this.productplanmappingList,
        this.invoiceToOrg,
        this.requiredApproval,
        // this.planCasMappingList,
        this.bandwidth,
        this.linkType,
        this.connectionType,
        this.distance,
        this.ram,
        this.cpu,
        this.storage,
        this.storageType,
        this.autoBackup,
        this.cpanel,
        this.location,
        this.quantity,
        this.packageType,
        this.numberOfDays,
        this.noOfUsers,
        this.rackSpace,
        this.rackUnit,
        this.powerConsumption,
        this.networkCard,
        this.ipOrIpPool,
        this.noOfLicense,
        this.noOfEmailUserLicense,
        this.noOfServerLicense,
        this.noOfUserLicense,
        this.noOfNodes,
        this.eventPerSecond,
        this.noOfAdditionalServer,
        this.noOfAdditionalStorage,
        this.additionalStorageType,
        this.epsLicense,
        this.noOfNodesLicense,
        this.hardwareResource,
        this.manPower,
        this.noOfDomains,
        this.securityModules,
        this.hardwareOrServers,
        this.country,
        this.noOfVpn,
        this.deviceThroughput,
        this.retail,
        this.businessType,
        this.basePlan,
        this.templateId,
        this.planQosMappingEntities,
        this.planQosMappingEntityList,
        this.postPaidPlanServiceAreaMappingList,
        this.isApprove,
        this.useQuota,
        this.chunk,
        this.accessibility});

  PostpaidPlan.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    displayName = json['displayName'];
    code = json['code'];
    desc = json['desc'];
    category = json['category'];
    maxChild = json['maxChild'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    quota = json['quota'];
    quotaUnit = json['quotaUnit'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    uploadTs = json['uploadTs'];
    downloadTs = json['downloadTs'];
    allowOverUsage = json['allowOverUsage'];
    status = json['status'];
    planStatus = json['planStatus'];
    childQuota = json['childQuota'];
    childQuotaUnit = json['childQuotaUnit'];
    slice = json['slice'];
    sliceUnit = json['sliceUnit'];
    attachedToAllHotSpots = json['attachedToAllHotSpots'];
    param1 = json['param1'];
    param2 = json['param2'];
    param3 = json['param3'];
    mvnoId = json['mvnoId'];
    taxId = json['taxId'];
    serviceId = json['serviceId'];
    timebasepolicyId = json['timebasepolicyId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    mvnoName = json['mvnoName'];
    if (json['chargeList'] != null) {
      chargeList = <ChargeList>[];
      json['chargeList'].forEach((v) {
        chargeList!.add(new ChargeList.fromJson(v));
      });
    }
    planGroup = json['planGroup'];
    validity = json['validity'];
    saccode = json['saccode'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    quotadid = json['quotadid'];
    quotaintercom = json['quotaintercom'];
    quotaunitdid = json['quotaunitdid'];
    quotaunitintercom = json['quotaunitintercom'];
    qospolicy = json['qospolicy'] != null
        ? new Qospolicy.fromJson(json['qospolicy'])
        : null;
    // if (json['radiusprofile'] != null) {
    //   radiusprofile = <Null>[];
    //   json['radiusprofile'].forEach((v) {
    //     radiusprofile!.add(new Null.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
    dataCategory = json['dataCategory'];
    taxamount = json['taxamount'];
    serviceName = json['serviceName'];
    timebasepolicyName = json['timebasepolicyName'];
    if (json['serviceAreaNameList'] != null) {
      serviceAreaNameList = <ServiceAreaNameList>[];
      json['serviceAreaNameList'].forEach((v) {
        serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
      });
    }
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    nextStaff = json['nextStaff'];
    newOfferPrice = json['newOfferPrice'];
    allowdiscount = json['allowdiscount'];
    productId = json['productId'];
    // if (json['productplanmappingList'] != null) {
    //   productplanmappingList = <Null>[];
    //   json['productplanmappingList'].forEach((v) {
    //     productplanmappingList!.add(new Null.fromJson(v));
    //   });
    // }
    invoiceToOrg = json['invoiceToOrg'];
    requiredApproval = json['requiredApproval'];
    // if (json['planCasMappingList'] != null) {
    //   planCasMappingList = <Null>[];
    //   json['planCasMappingList'].forEach((v) {
    //     planCasMappingList!.add(new Null.fromJson(v));
    //   });
    // }
    bandwidth = json['bandwidth'];
    linkType = json['link_type'];
    connectionType = json['connection_type'];
    distance = json['distance'];
    ram = json['ram'];
    cpu = json['cpu'];
    storage = json['storage'];
    storageType = json['storage_type'];
    autoBackup = json['auto_backup'];
    cpanel = json['cpanel'];
    location = json['location'];
    quantity = json['quantity'];
    packageType = json['package_type'];
    numberOfDays = json['number_of_days'];
    noOfUsers = json['no_of_users'];
    rackSpace = json['rack_space'];
    rackUnit = json['rack_unit'];
    powerConsumption = json['power_consumption'];
    networkCard = json['network_card'];
    ipOrIpPool = json['ip_or_ip_pool'];
    noOfLicense = json['no_of_license'];
    noOfEmailUserLicense = json['no_of_email_user_license'];
    noOfServerLicense = json['no_of_server_license'];
    noOfUserLicense = json['no_of_user_license'];
    noOfNodes = json['no_of_nodes'];
    eventPerSecond = json['event_per_second'];
    noOfAdditionalServer = json['no_of_additional_server'];
    noOfAdditionalStorage = json['no_of_additional_storage'];
    additionalStorageType = json['additional_storage_type'];
    epsLicense = json['eps_License'];
    noOfNodesLicense = json['no_of_nodes_license'];
    hardwareResource = json['hardware_resource'];
    manPower = json['man_power'];
    noOfDomains = json['no_of_domains'];
    securityModules = json['security_modules'];
    hardwareOrServers = json['hardware_or_servers'];
    country = json['country'];
    noOfVpn = json['no_of_vpn'];
    deviceThroughput = json['device_throughput'];
    retail = json['retail'];
    businessType = json['businessType'];
    basePlan = json['basePlan'];
    templateId = json['templateId'];
    if (json['planQosMappingEntities'] != null) {
      planQosMappingEntities = <PlanQosMappingEntities>[];
      json['planQosMappingEntities'].forEach((v) {
        planQosMappingEntities!.add(new PlanQosMappingEntities.fromJson(v));
      });
    }
    planQosMappingEntityList = json['planQosMappingEntityList'];
    postPaidPlanServiceAreaMappingList =
    json['postPaidPlanServiceAreaMappingList'];
    isApprove = json['isApprove'];
    useQuota = json['useQuota'];
    chunk = json['chunk'];
    accessibility = json['accessibility'];
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
    data['name'] = this.name;
    data['displayName'] = this.displayName;
    data['code'] = this.code;
    data['desc'] = this.desc;
    data['category'] = this.category;
    data['maxChild'] = this.maxChild;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['quota'] = this.quota;
    data['quotaUnit'] = this.quotaUnit;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['uploadTs'] = this.uploadTs;
    data['downloadTs'] = this.downloadTs;
    data['allowOverUsage'] = this.allowOverUsage;
    data['status'] = this.status;
    data['planStatus'] = this.planStatus;
    data['childQuota'] = this.childQuota;
    data['childQuotaUnit'] = this.childQuotaUnit;
    data['slice'] = this.slice;
    data['sliceUnit'] = this.sliceUnit;
    data['attachedToAllHotSpots'] = this.attachedToAllHotSpots;
    data['param1'] = this.param1;
    data['param2'] = this.param2;
    data['param3'] = this.param3;
    data['mvnoId'] = this.mvnoId;
    data['taxId'] = this.taxId;
    data['serviceId'] = this.serviceId;
    data['timebasepolicyId'] = this.timebasepolicyId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    data['mvnoName'] = this.mvnoName;
    if (this.chargeList != null) {
      data['chargeList'] = this.chargeList!.map((v) => v.toJson()).toList();
    }
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['saccode'] = this.saccode;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['quotadid'] = this.quotadid;
    data['quotaintercom'] = this.quotaintercom;
    data['quotaunitdid'] = this.quotaunitdid;
    data['quotaunitintercom'] = this.quotaunitintercom;
    if (this.qospolicy != null) {
      data['qospolicy'] = this.qospolicy!.toJson();
    }
    // if (this.radiusprofile != null) {
    //   data['radiusprofile'] =
    //       this.radiusprofile!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['dataCategory'] = this.dataCategory;
    data['taxamount'] = this.taxamount;
    data['serviceName'] = this.serviceName;
    data['timebasepolicyName'] = this.timebasepolicyName;
    if (this.serviceAreaNameList != null) {
      data['serviceAreaNameList'] =
          this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    }
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['nextStaff'] = this.nextStaff;
    data['newOfferPrice'] = this.newOfferPrice;
    data['allowdiscount'] = this.allowdiscount;
    data['productId'] = this.productId;
    // if (this.productplanmappingList != null) {
    //   data['productplanmappingList'] =
    //       this.productplanmappingList!.map((v) => v.toJson()).toList();
    // }
    data['invoiceToOrg'] = this.invoiceToOrg;
    data['requiredApproval'] = this.requiredApproval;
    // if (this.planCasMappingList != null) {
    //   data['planCasMappingList'] =
    //       this.planCasMappingList!.map((v) => v.toJson()).toList();
    // }
    data['bandwidth'] = this.bandwidth;
    data['link_type'] = this.linkType;
    data['connection_type'] = this.connectionType;
    data['distance'] = this.distance;
    data['ram'] = this.ram;
    data['cpu'] = this.cpu;
    data['storage'] = this.storage;
    data['storage_type'] = this.storageType;
    data['auto_backup'] = this.autoBackup;
    data['cpanel'] = this.cpanel;
    data['location'] = this.location;
    data['quantity'] = this.quantity;
    data['package_type'] = this.packageType;
    data['number_of_days'] = this.numberOfDays;
    data['no_of_users'] = this.noOfUsers;
    data['rack_space'] = this.rackSpace;
    data['rack_unit'] = this.rackUnit;
    data['power_consumption'] = this.powerConsumption;
    data['network_card'] = this.networkCard;
    data['ip_or_ip_pool'] = this.ipOrIpPool;
    data['no_of_license'] = this.noOfLicense;
    data['no_of_email_user_license'] = this.noOfEmailUserLicense;
    data['no_of_server_license'] = this.noOfServerLicense;
    data['no_of_user_license'] = this.noOfUserLicense;
    data['no_of_nodes'] = this.noOfNodes;
    data['event_per_second'] = this.eventPerSecond;
    data['no_of_additional_server'] = this.noOfAdditionalServer;
    data['no_of_additional_storage'] = this.noOfAdditionalStorage;
    data['additional_storage_type'] = this.additionalStorageType;
    data['eps_License'] = this.epsLicense;
    data['no_of_nodes_license'] = this.noOfNodesLicense;
    data['hardware_resource'] = this.hardwareResource;
    data['man_power'] = this.manPower;
    data['no_of_domains'] = this.noOfDomains;
    data['security_modules'] = this.securityModules;
    data['hardware_or_servers'] = this.hardwareOrServers;
    data['country'] = this.country;
    data['no_of_vpn'] = this.noOfVpn;
    data['device_throughput'] = this.deviceThroughput;
    data['retail'] = this.retail;
    data['businessType'] = this.businessType;
    data['basePlan'] = this.basePlan;
    data['templateId'] = this.templateId;
    if (this.planQosMappingEntities != null) {
      data['planQosMappingEntities'] =
          this.planQosMappingEntities!.map((v) => v.toJson()).toList();
    }
    data['planQosMappingEntityList'] = this.planQosMappingEntityList;
    data['postPaidPlanServiceAreaMappingList'] =
        this.postPaidPlanServiceAreaMappingList;
    data['isApprove'] = this.isApprove;
    data['useQuota'] = this.useQuota;
    data['chunk'] = this.chunk;
    data['accessibility'] = this.accessibility;
    return data;
  }
}

class ChargeList {
  dynamic id;
  Charge? charge;
  dynamic billingCycle;
  String? createdate;
  dynamic chargeprice;
  Null? chargeName;
  Null? planId;
  Null? chargeId;

  ChargeList(
      {this.id,
        this.charge,
        this.billingCycle,
        this.createdate,
        this.chargeprice,
        this.chargeName,
        this.planId,
        this.chargeId});

  ChargeList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    charge =
    json['charge'] != null ? new Charge.fromJson(json['charge']) : null;
    billingCycle = json['billingCycle'];
    createdate = json['createdate'];
    chargeprice = json['chargeprice'];
    chargeName = json['chargeName'];
    planId = json['planId'];
    chargeId = json['chargeId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    if (this.charge != null) {
      data['charge'] = this.charge!.toJson();
    }
    data['billingCycle'] = this.billingCycle;
    data['createdate'] = this.createdate;
    data['chargeprice'] = this.chargeprice;
    data['chargeName'] = this.chargeName;
    data['planId'] = this.planId;
    data['chargeId'] = this.chargeId;
    return data;
  }
}

class Charge {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? chargetype;
  dynamic price;
  dynamic actualprice;
  Tax? tax;
  Null? discountid;
  dynamic dbr;
  bool? isDelete;
  String? chargecategory;
  Null? saccode;
  List<ServiceList>? serviceList;
  dynamic mvnoId;
  Null? buId;
  Null? service;
  String? status;
  Null? ledgerId;
  bool? royaltyPayable;
  Null? taxamount;
  Null? businessType;
  Null? pushableLedgerId;
  bool? isinventorycharge;
  Null? productId;
  Null? inventoryChargeType;
  String? mvnoName;

  Charge(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.desc,
        this.chargetype,
        this.price,
        this.actualprice,
        this.tax,
        this.discountid,
        this.dbr,
        this.isDelete,
        this.chargecategory,
        this.saccode,
        this.serviceList,
        this.mvnoId,
        this.buId,
        this.service,
        this.status,
        this.ledgerId,
        this.royaltyPayable,
        this.taxamount,
        this.businessType,
        this.pushableLedgerId,
        this.isinventorycharge,
        this.productId,
        this.inventoryChargeType,
        this.mvnoName});

  Charge.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    actualprice = json['actualprice'];
    tax = json['tax'] != null ? new Tax.fromJson(json['tax']) : null;
    discountid = json['discountid'];
    dbr = json['dbr'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    if (json['serviceList'] != null) {
      serviceList = <ServiceList>[];
      json['serviceList'].forEach((v) {
        serviceList!.add(new ServiceList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    service = json['service'];
    status = json['status'];
    ledgerId = json['ledgerId'];
    royaltyPayable = json['royalty_payable'];
    taxamount = json['taxamount'];
    businessType = json['businessType'];
    pushableLedgerId = json['pushableLedgerId'];
    isinventorycharge = json['isinventorycharge'];
    productId = json['productId'];
    inventoryChargeType = json['inventoryChargeType'];
    mvnoName = json['mvnoName'];
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
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    if (this.tax != null) {
      data['tax'] = this.tax!.toJson();
    }
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    if (this.serviceList != null) {
      data['serviceList'] = this.serviceList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['service'] = this.service;
    data['status'] = this.status;
    data['ledgerId'] = this.ledgerId;
    data['royalty_payable'] = this.royaltyPayable;
    data['taxamount'] = this.taxamount;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    data['isinventorycharge'] = this.isinventorycharge;
    data['productId'] = this.productId;
    data['inventoryChargeType'] = this.inventoryChargeType;
    data['mvnoName'] = this.mvnoName;
    return data;
  }
}

class Tax {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
  String? taxtype;
  String? status;
  dynamic mvnoId;
  Null? buId;
  List<TieredList>? tieredList;
  // List<Null>? slabList;
  bool? isDelete;
  String? mvnoName;

  Tax(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.desc,
        this.taxtype,
        this.status,
        this.mvnoId,
        this.buId,
        this.tieredList,
        // this.slabList,
        this.isDelete,
        this.mvnoName});

  Tax.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    taxtype = json['taxtype'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    if (json['tieredList'] != null) {
      tieredList = <TieredList>[];
      json['tieredList'].forEach((v) {
        tieredList!.add(new TieredList.fromJson(v));
      });
    }
    // if (json['slabList'] != null) {
    //   slabList = <Null>[];
    //   json['slabList'].forEach((v) {
    //     slabList!.add(new Null.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
    mvnoName = json['mvnoName'];
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
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['taxtype'] = this.taxtype;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    if (this.tieredList != null) {
      data['tieredList'] = this.tieredList!.map((v) => v.toJson()).toList();
    }
    // if (this.slabList != null) {
    //   data['slabList'] = this.slabList!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['mvnoName'] = this.mvnoName;
    return data;
  }
}

class TieredList {
  dynamic id;
  String? name;
  String? taxGroup;
  dynamic rate;
  bool? isDelete;
  bool? beforeDiscount;
  String? taxLedgerId;

  TieredList(
      {this.id,
        this.name,
        this.taxGroup,
        this.rate,
        this.isDelete,
        this.beforeDiscount,
        this.taxLedgerId});

  TieredList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    taxGroup = json['taxGroup'];
    rate = json['rate'];
    isDelete = json['isDelete'];
    beforeDiscount = json['beforeDiscount'];
    taxLedgerId = json['taxLedgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['taxGroup'] = this.taxGroup;
    data['rate'] = this.rate;
    data['isDelete'] = this.isDelete;
    data['beforeDiscount'] = this.beforeDiscount;
    data['taxLedgerId'] = this.taxLedgerId;
    return data;
  }
}

class ServiceList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? serviceName;
  dynamic mvnoId;

  ServiceList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.serviceName,
        this.mvnoId});

  ServiceList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    serviceName = json['serviceName'];
    mvnoId = json['mvnoId'];
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
    data['serviceName'] = this.serviceName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class Qospolicy {
  String? createdate;
  String? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  dynamic id;
  String? name;
  String? description;
  String? basepolicyname;
  String? thpolicyname;
  String? baseparam1;
  String? baseparam2;
  String? baseparam3;
  String? thparam1;
  String? thparam2;
  String? thparam3;
  bool? isDeleted;
  dynamic mvnoId;
  Null? buId;
  Null? upstreamprofileuid;
  Null? downstreamprofileuid;
  Null? upstreamprofileName;
  Null? downstreamprofileName;
  String? type;
  String? qosspeed;
  List<QosPolicyGatewayMappingList>? qosPolicyGatewayMappingList;
  String? mvnoName;

  Qospolicy(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.description,
        this.basepolicyname,
        this.thpolicyname,
        this.baseparam1,
        this.baseparam2,
        this.baseparam3,
        this.thparam1,
        this.thparam2,
        this.thparam3,
        this.isDeleted,
        this.mvnoId,
        this.buId,
        this.upstreamprofileuid,
        this.downstreamprofileuid,
        this.upstreamprofileName,
        this.downstreamprofileName,
        this.type,
        this.qosspeed,
        this.qosPolicyGatewayMappingList,
        this.mvnoName});

  Qospolicy.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    description = json['description'];
    basepolicyname = json['basepolicyname'];
    thpolicyname = json['thpolicyname'];
    baseparam1 = json['baseparam1'];
    baseparam2 = json['baseparam2'];
    baseparam3 = json['baseparam3'];
    thparam1 = json['thparam1'];
    thparam2 = json['thparam2'];
    thparam3 = json['thparam3'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    upstreamprofileuid = json['upstreamprofileuid'];
    downstreamprofileuid = json['downstreamprofileuid'];
    upstreamprofileName = json['upstreamprofileName'];
    downstreamprofileName = json['downstreamprofileName'];
    type = json['type'];
    qosspeed = json['qosspeed'];
    if (json['qosPolicyGatewayMappingList'] != null) {
      qosPolicyGatewayMappingList = <QosPolicyGatewayMappingList>[];
      json['qosPolicyGatewayMappingList'].forEach((v) {
        qosPolicyGatewayMappingList!
            .add(new QosPolicyGatewayMappingList.fromJson(v));
      });
    }
    mvnoName = json['mvnoName'];
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
    data['name'] = this.name;
    data['description'] = this.description;
    data['basepolicyname'] = this.basepolicyname;
    data['thpolicyname'] = this.thpolicyname;
    data['baseparam1'] = this.baseparam1;
    data['baseparam2'] = this.baseparam2;
    data['baseparam3'] = this.baseparam3;
    data['thparam1'] = this.thparam1;
    data['thparam2'] = this.thparam2;
    data['thparam3'] = this.thparam3;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['upstreamprofileuid'] = this.upstreamprofileuid;
    data['downstreamprofileuid'] = this.downstreamprofileuid;
    data['upstreamprofileName'] = this.upstreamprofileName;
    data['downstreamprofileName'] = this.downstreamprofileName;
    data['type'] = this.type;
    data['qosspeed'] = this.qosspeed;
    if (this.qosPolicyGatewayMappingList != null) {
      data['qosPolicyGatewayMappingList'] =
          this.qosPolicyGatewayMappingList!.map((v) => v.toJson()).toList();
    }
    data['mvnoName'] = this.mvnoName;
    return data;
  }
}

class QosPolicyGatewayMappingList {
  dynamic id;
  String? gatewayName;
  String? downloadSpeed;
  String? uploadSpeed;
  String? baseDownloadSpeed;
  String? baseUploadSpeed;
  String? throttleDownloadSpeed;
  String? throttleUploadSpeed;
  dynamic qosPolicyId;

  QosPolicyGatewayMappingList(
      {this.id,
        this.gatewayName,
        this.downloadSpeed,
        this.uploadSpeed,
        this.baseDownloadSpeed,
        this.baseUploadSpeed,
        this.throttleDownloadSpeed,
        this.throttleUploadSpeed,
        this.qosPolicyId});

  QosPolicyGatewayMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    gatewayName = json['gatewayName'];
    downloadSpeed = json['downloadSpeed'];
    uploadSpeed = json['uploadSpeed'];
    baseDownloadSpeed = json['baseDownloadSpeed'];
    baseUploadSpeed = json['baseUploadSpeed'];
    throttleDownloadSpeed = json['throttleDownloadSpeed'];
    throttleUploadSpeed = json['throttleUploadSpeed'];
    qosPolicyId = json['qosPolicyId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['gatewayName'] = this.gatewayName;
    data['downloadSpeed'] = this.downloadSpeed;
    data['uploadSpeed'] = this.uploadSpeed;
    data['baseDownloadSpeed'] = this.baseDownloadSpeed;
    data['baseUploadSpeed'] = this.baseUploadSpeed;
    data['throttleDownloadSpeed'] = this.throttleDownloadSpeed;
    data['throttleUploadSpeed'] = this.throttleUploadSpeed;
    data['qosPolicyId'] = this.qosPolicyId;
    return data;
  }
}

class ServiceAreaNameList {
  dynamic id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  Null? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  // List<Null>? networkDevicesList;
  dynamic mvnoId;
  String? latitude;
  String? longitude;
  Null? areaId;
  List<PincodeList>? pincodeList;
  dynamic cityid;
  String? siteName;

  ServiceAreaNameList(
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
        this.cityid,
        this.siteName});

  ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
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
    siteName = json['siteName'];
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
    data['siteName'] = this.siteName;
    return data;
  }
}

class PincodeList {
  dynamic id;
  String? pincode;
  String? status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  List<AreaList>? areaList;
  dynamic mvnoId;
  String? createdate;
  String? updatedate;
  dynamic createdById;
  dynamic lastModifiedById;
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
  dynamic id;
  String? name;
  String? status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  dynamic mvnoId;
  String? createdate;
  String? updatedate;
  dynamic createdById;
  dynamic lastModifiedById;
  String? createdByName;
  String? lastModifiedByName;
  bool? deleteFlag;
  dynamic primaryKey;

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
        this.deleteFlag,
        this.primaryKey});

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
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class PlanQosMappingEntities {
  dynamic id;
  Qospolicy? qosPolicy;
  dynamic frompercentage;
  dynamic topercentage;
  Null? isdelete;

  PlanQosMappingEntities(
      {this.id,
        this.qosPolicy,
        this.frompercentage,
        this.topercentage,
        this.isdelete});

  PlanQosMappingEntities.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qosPolicy = json['qosPolicy'] != null
        ? new Qospolicy.fromJson(json['qosPolicy'])
        : null;
    frompercentage = json['frompercentage'];
    topercentage = json['topercentage'];
    isdelete = json['isdelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    if (this.qosPolicy != null) {
      data['qosPolicy'] = this.qosPolicy!.toJson();
    }
    data['frompercentage'] = this.frompercentage;
    data['topercentage'] = this.topercentage;
    data['isdelete'] = this.isdelete;
    return data;
  }
}

class CreditDocuments {
  String? createdate;
  String? updatedate;
  String? createdByName;
  Null? lastModifiedByName;
  dynamic createdById;
  Null? lastModifiedById;
  dynamic id;
  String? paymode;
  String? paymentdate;
  Null? chequedate;
  Null? paydetails1;
  Null? paydetails2;
  Null? paydetails3;
  String? paydetails4;
  dynamic amount;
  String? status;
  dynamic approverid;
  String? remarks;
  String? referenceno;
  Null? xmldocument;
  Null? custId;
  String? reciptNo;
  bool? isDelete;
  Null? chequeNo;
  Null? bankName;
  Null? destinationBank;
  Null? branch;
  Null? tdsflag;
  dynamic tdsamount;
  Null? isReversed;
  Null? resevrsedDate;
  Null? resverseDebitdocId;
  Null? tdsReceived;
  Null? tdsReceivedDate;
  Null? tdsCreditDocId;
  dynamic adjustedAmount;
  Null? customerName;
  Null? serviceAreaId;
  dynamic invoiceId;
  Null? invoiceNumber;
  String? type;
  String? paytype;
  Null? batchAssigned;
  Null? nextTeamHierarchyMappingId;
  Null? staff;
  Null? documentno;
  Null? buId;
  Null? creditdocumentno;
  Null? paymentreferenceno;
  dynamic mvnoId;
  Null? lcoId;
  dynamic abbsAmount;
  bool? delete;

  CreditDocuments(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.paymode,
        this.paymentdate,
        this.chequedate,
        this.paydetails1,
        this.paydetails2,
        this.paydetails3,
        this.paydetails4,
        this.amount,
        this.status,
        this.approverid,
        this.remarks,
        this.referenceno,
        this.xmldocument,
        this.custId,
        this.reciptNo,
        this.isDelete,
        this.chequeNo,
        this.bankName,
        this.destinationBank,
        this.branch,
        this.tdsflag,
        this.tdsamount,
        this.isReversed,
        this.resevrsedDate,
        this.resverseDebitdocId,
        this.tdsReceived,
        this.tdsReceivedDate,
        this.tdsCreditDocId,
        this.adjustedAmount,
        this.customerName,
        this.serviceAreaId,
        this.invoiceId,
        this.invoiceNumber,
        this.type,
        this.paytype,
        this.batchAssigned,
        this.nextTeamHierarchyMappingId,
        this.staff,
        this.documentno,
        this.buId,
        this.creditdocumentno,
        this.paymentreferenceno,
        this.mvnoId,
        this.lcoId,
        this.abbsAmount,
        this.delete});

  CreditDocuments.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    paymode = json['paymode'];
    paymentdate = json['paymentdate'];
    chequedate = json['chequedate'];
    paydetails1 = json['paydetails1'];
    paydetails2 = json['paydetails2'];
    paydetails3 = json['paydetails3'];
    paydetails4 = json['paydetails4'];
    amount = json['amount'];
    status = json['status'];
    approverid = json['approverid'];
    remarks = json['remarks'];
    referenceno = json['referenceno'];
    xmldocument = json['xmldocument'];
    custId = json['custId'];
    reciptNo = json['reciptNo'];
    isDelete = json['isDelete'];
    chequeNo = json['chequeNo'];
    bankName = json['bankName'];
    destinationBank = json['destinationBank'];
    branch = json['branch'];
    tdsflag = json['tdsflag'];
    tdsamount = json['tdsamount'];
    isReversed = json['is_reversed'];
    resevrsedDate = json['resevrsed_date'];
    resverseDebitdocId = json['resverse_debitdoc_id'];
    tdsReceived = json['tds_received'];
    tdsReceivedDate = json['tds_received_date'];
    tdsCreditDocId = json['tds_credit_doc_id'];
    adjustedAmount = json['adjustedAmount'];
    customerName = json['customerName'];
    serviceAreaId = json['serviceAreaId'];
    invoiceId = json['invoiceId'];
    invoiceNumber = json['invoiceNumber'];
    type = json['type'];
    paytype = json['paytype'];
    batchAssigned = json['batchAssigned'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    staff = json['staff'];
    documentno = json['documentno'];
    buId = json['buId'];
    creditdocumentno = json['creditdocumentno'];
    paymentreferenceno = json['paymentreferenceno'];
    mvnoId = json['mvnoId'];
    lcoId = json['lcoId'];
    abbsAmount = json['abbsAmount'];
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
    data['paymode'] = this.paymode;
    data['paymentdate'] = this.paymentdate;
    data['chequedate'] = this.chequedate;
    data['paydetails1'] = this.paydetails1;
    data['paydetails2'] = this.paydetails2;
    data['paydetails3'] = this.paydetails3;
    data['paydetails4'] = this.paydetails4;
    data['amount'] = this.amount;
    data['status'] = this.status;
    data['approverid'] = this.approverid;
    data['remarks'] = this.remarks;
    data['referenceno'] = this.referenceno;
    data['xmldocument'] = this.xmldocument;
    data['custId'] = this.custId;
    data['reciptNo'] = this.reciptNo;
    data['isDelete'] = this.isDelete;
    data['chequeNo'] = this.chequeNo;
    data['bankName'] = this.bankName;
    data['destinationBank'] = this.destinationBank;
    data['branch'] = this.branch;
    data['tdsflag'] = this.tdsflag;
    data['tdsamount'] = this.tdsamount;
    data['is_reversed'] = this.isReversed;
    data['resevrsed_date'] = this.resevrsedDate;
    data['resverse_debitdoc_id'] = this.resverseDebitdocId;
    data['tds_received'] = this.tdsReceived;
    data['tds_received_date'] = this.tdsReceivedDate;
    data['tds_credit_doc_id'] = this.tdsCreditDocId;
    data['adjustedAmount'] = this.adjustedAmount;
    data['customerName'] = this.customerName;
    data['serviceAreaId'] = this.serviceAreaId;
    data['invoiceId'] = this.invoiceId;
    data['invoiceNumber'] = this.invoiceNumber;
    data['type'] = this.type;
    data['paytype'] = this.paytype;
    data['batchAssigned'] = this.batchAssigned;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['staff'] = this.staff;
    data['documentno'] = this.documentno;
    data['buId'] = this.buId;
    data['creditdocumentno'] = this.creditdocumentno;
    data['paymentreferenceno'] = this.paymentreferenceno;
    data['mvnoId'] = this.mvnoId;
    data['lcoId'] = this.lcoId;
    data['abbsAmount'] = this.abbsAmount;
    data['delete'] = this.delete;
    return data;
  }
}

class IndiChargeList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic planid;
  Null? chargePojo;
  dynamic chargeid;
  Null? chargeName;
  String? chargetype;
  dynamic validity;
  dynamic price;
  dynamic actualprice;
  Null? remarks;
  String? chargeDate;
  Null? chargeDateString;
  String? startdate;
  Null? startdateString;
  String? enddate;
  Null? expiry;
  Null? enddateString;
  dynamic taxamount;
  bool? isReversed;
  Null? revDate;
  Null? revdateString;
  Null? revAmt;
  Null? revRemarks;
  bool? isUsed;
  Null? purchaseEntityId;
  Null? ippooldtlsid;
  dynamic debitdocid;
  Null? createDateString;
  Null? updateDateString;
  String? type;
  dynamic planValidity;
  String? unitsOfValidity;
  dynamic taxId;
  dynamic custPlanMapppingId;
  Null? lastBillDate;
  Null? nextBillDate;
  dynamic billingCycle;
  bool? isDeleted;
  double? dbr;
  Null? custServiceMappingId;
  Null? discount;
  bool? isInvoiceToOrg;
  String? billTo;
  dynamic newAmount;
  Null? staticIPAdrress;
  String? connectionNo;
  Null? isRenew;
  Null? taxInPer;

  IndiChargeList(
      {this.createdate,
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
