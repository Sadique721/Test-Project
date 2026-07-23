

import '../../../service_management/request/add_service_req.dart';

class AddEditCustomerReq {
  String? username;
  String? password;
  String? firstname;
  String? lastname;
  String? email;
  String? title;
  String? pan;
  String? gst;
  String? aadhar;
  String? passportNo;
  String? tinNo;
  String? contactperson;
  int? failcount;
  String? custtype;
  String? custlabel;
  dynamic phone;
  dynamic mobile;
  dynamic altmobile;
  dynamic fax;
  String? birthDate;
  String? countryCode;
  String? customerType;
  String? customerSubType;
  List<CustomerLocations>? customerLocations;
  String? customerSector;
  String? customerSubSector;
  String? cafno;
  String? feasibilityRequired;
  String? voicesrvtype;
  String? didno;
  String? calendarType;
  int? partnerid;
  String? salesremark;
  int? renewPlanLimit;
  String? servicetype;
  int? serviceareaid;
  String? status;
  String? parentCustomerId;
  // String? parentExperience;
  String? parentQuotaType;
  String? latitude;
  String? longitude;
  String? billTo;
  dynamic billableCustomerId;
  bool? isInvoiceToOrg;
  bool? istrialplan;
  int? popid;
  dynamic staffId;
  dynamic discount;
  dynamic flatAmount;
  dynamic plangroupid;
  String? discountType;
  bool? isCredentialMatchWithAccountNo;
  String? discountExpiryDate;
  List<PlanMappingList>? planMappingList;
  List<AddressList>? addressList;
  List<OverChargesDetails>? overChargeList;
  List<CustMacMapppingList>? custMacMapppingList;

  int? branch;
  dynamic oltid;
  dynamic masterdbid;
  dynamic splitterid;
  String? nasPort;
  String? framedIp;
  String? framedIpBind;
  String? ipPoolNameBind;
  String? valleyType;
  String? vlan_id;
  String? customerArea;
  PaymentDetails? paymentDetails;
  String? isCustCaf;
  String? dunningCategory;
  String? earlybilldays;
  String? framedIpv6Address;
  String? maxconcurrentsession;
  String? nasIpAddress;
  int? billday;
  String? blockNO;
  String? department;
  String? invoiceType;
  String? planPurchaseType;
  // bool? isDunningEnable;
  // bool? isNotificationEnable;
  dynamic isParentLocation;
  dynamic locations;
  dynamic mac_provision;
  dynamic mac_auth_enable;
  dynamic macRetentionPeriod;
  dynamic macRetentionUnit;
  bool? skipQuotaUpdate;
  dynamic primaryDNS;
  dynamic primaryIPv6DNS;
  dynamic secondaryDNS;
  dynamic secondaryIPv6DNS;
  dynamic acctno;
  String? addparam1;
  String? addparam2;
  String? addparam3;
  String? addparam4;
  // String? locations;


  AddEditCustomerReq.fromJson(Map<String, dynamic> json) {
    username = json['username'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    email = json['email'];
    title = json['title'];
    pan = json['pan'];
    gst = json['gst'];
    aadhar = json['aadhar'];
    passportNo = json['passportNo'];
    tinNo = json['tinNo'];
    contactperson = json['contactperson'];
    failcount = json['failcount'];
    custtype = json['custtype'];
    custlabel = json['custlabel'];
    phone = json['phone'];
    mobile = json['mobile'];
    altmobile = json['altmobile'];
    fax = json['fax'];
    birthDate = json['birthDate'];
    countryCode = json['countryCode'];
    customerType = json['customerType'];
    customerSubType = json['customerSubType'];
    if (json['customerLocations'] != null) {
      customerLocations = <CustomerLocations>[];
      json['customerLocations'].forEach((v) {
        customerLocations!.add(new CustomerLocations.fromJson(v));
      });
    }
    customerSector = json['customerSector'];
    customerSubSector = json['customerSubSector'];
    cafno = json['cafno'];
    feasibilityRequired = json['feasibilityRequired'];
    voicesrvtype = json['voicesrvtype'];
    didno = json['didno'];
    calendarType = json['calendarType'];
    partnerid = json['partnerid'];
    salesremark = json['salesremark'];
    renewPlanLimit = json['renewPlanLimit'];
    servicetype = json['servicetype'];
    serviceareaid = json['serviceareaid'];
    status = json['status'];
    parentCustomerId = json['parentCustomerId'];
    // parentExperience = json['parentExperience'];
    parentQuotaType = json['parentQuotaType'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    billTo = json['billTo'];
    billableCustomerId = json['billableCustomerId'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    istrialplan = json['istrialplan'];
    popid = json['popid'];
    staffId = json['staffId'];
    discount = json['discount'];
    flatAmount = json['flatAmount'];
    plangroupid = json['plangroupid'];
    discountType = json['discountType'];
    isCredentialMatchWithAccountNo = json['isCredentialMatchWithAccountNo'];
    discountExpiryDate = json['discountExpiryDate'];

    if (json['planMappingList'] != null) {
      planMappingList = <PlanMappingList>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingList.fromJson(v));
      });
    }
    if (json['addressList'] != null) {
      addressList = <AddressList>[];
      json['addressList'].forEach((v) {
        addressList!.add(new AddressList.fromJson(v));
      });
    }
    if (json['overChargeList'] != null) {
      overChargeList = <OverChargesDetails>[];
      json['overChargeList'].forEach((v) {
        overChargeList!.add(new OverChargesDetails.fromJson(v));
      });
    }
    if (json['custMacMapppingList'] != null) {
      custMacMapppingList = <CustMacMapppingList>[];
      json['custMacMapppingList'].forEach((v) {
        custMacMapppingList!.add(new CustMacMapppingList.fromJson(v));
      });
    }
    branch = json['branch'];
    oltid = json['oltid'];
    masterdbid = json['masterdbid'];
    splitterid = json['splitterid'];
    nasPort = json['nasPort'];
    framedIp = json['framedIp'];
    framedIpBind = json['framedIpBind'];
    ipPoolNameBind = json['ipPoolNameBind'];
    valleyType = json['valleyType'];
    vlan_id = json['vlan_id'];
    customerArea = json['customerArea'];
    paymentDetails = json['paymentDetails'] != null
        ? new PaymentDetails.fromJson(json['paymentDetails'])
        : null;
    isCustCaf = json['isCustCaf'];
    dunningCategory = json['dunningCategory'];
    earlybilldays = json['earlybilldays'];
    framedIpv6Address = json['framedIpv6Address'];
    maxconcurrentsession = json['maxconcurrentsession'];
    nasIpAddress = json['nasIpAddress'];
    billday = json['billday'];
    blockNO = json['blockNO'];
    department = json['department'];
    invoiceType = json['invoiceType'];
    planPurchaseType = json['planPurchaseType'];
    isParentLocation = json['isParentLocation'];
    // locations = json['locations'];
    locations = json['locations'].cast<dynamic>();
    skipQuotaUpdate = json['skipQuotaUpdate'];
    mac_auth_enable = json['mac_auth_enable'];
    mac_provision = json['mac_provision'];
    macRetentionPeriod = json['macRetentionPeriod'];
    macRetentionUnit = json['macRetentionUnit'];
    primaryDNS = json['primaryDNS'];
    primaryIPv6DNS = json['primaryIPv6DNS'];
    secondaryDNS = json['secondaryDNS'];
    secondaryIPv6DNS = json['secondaryIPv6DNS'];
    acctno = json['acctno'];
    addparam1 = json['addparam1'];
    addparam2 = json['addparam2'];
    addparam3 = json['addparam3'];
    addparam4 = json['addparam4'];
  }

  AddEditCustomerReq({
    this.username,
    this.password,
    this.firstname,
    this.lastname,
    this.email,
    this.title,
    this.pan,
    this.gst,
    this.aadhar,
    this.passportNo,
    this.tinNo,
    this.contactperson,
    this.failcount,
    this.custtype,
    this.custlabel,
    this.phone,
    this.mobile,
    this.altmobile,
    this.fax,
    this.birthDate,
    this.countryCode,
    this.customerType,
    this.customerSubType,
    this.customerLocations,
    this.customerSector,
    this.customerSubSector,
    this.feasibilityRequired,
    this.cafno,
    this.voicesrvtype,
    this.didno,
    this.calendarType,
    this.partnerid,
    this.salesremark,
    this.renewPlanLimit,
    this.servicetype,
    this.serviceareaid,
    this.status,
    this.parentCustomerId,
    // this.parentExperience,
    this.parentQuotaType,
    this.latitude,
    this.longitude,
    this.billTo,
    this.billableCustomerId,
    this.isInvoiceToOrg,
    this.istrialplan,
    this.popid,
    this.staffId,
    this.discount,
    this.flatAmount,
    this.plangroupid,
    this.discountType,
    this.isCredentialMatchWithAccountNo,
    this.discountExpiryDate,
    this.planMappingList,
    this.addressList,
    this.overChargeList,
    this.custMacMapppingList,
    this.branch,
    this.oltid,
    this.masterdbid,
    this.splitterid,
    this.nasPort,
    this.framedIp,
    this.framedIpBind,
    this.ipPoolNameBind,
    this.valleyType,
    this.vlan_id,
    this.customerArea,
    this.paymentDetails,
    this.isCustCaf,
    this.dunningCategory,
    this.earlybilldays,
    this.framedIpv6Address,
    this.maxconcurrentsession,
    this.nasIpAddress,
    this.billday,
    this.blockNO,
    this.department,
    this.invoiceType,
    this.planPurchaseType,
    // this.isDunningEnable,
    // this.isNotificationEnable
    this.isParentLocation,
    this.locations,
    this.mac_auth_enable,
    this.mac_provision,
    this.macRetentionPeriod,
    this.macRetentionUnit,
    this.skipQuotaUpdate,
    this.primaryDNS,
    this.primaryIPv6DNS,
    this.secondaryDNS,
    this.secondaryIPv6DNS,
    this.acctno,
    this.addparam1,
    this.addparam2,
    this.addparam3,
    this.addparam4,
  });

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['username'] = this.username;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['email'] = this.email;
    data['title'] = this.title;
    data['pan'] = this.pan;
    data['gst'] = this.gst;
    data['aadhar'] = this.aadhar;
    data['passportNo'] = this.passportNo;
    data['tinNo'] = this.tinNo;
    data['contactperson'] = this.contactperson;
    data['failcount'] = this.failcount;
    data['custtype'] = this.custtype;
    data['custlabel'] = this.custlabel;
    data['phone'] = this.phone;
    data['mobile'] = this.mobile;
    data['altmobile'] = this.altmobile;
    data['fax'] = this.fax;
    data['birthDate'] = this.birthDate;
    data['countryCode'] = this.countryCode;
    data['customerType'] = this.customerType;
    data['customerSubType'] = this.customerSubType;
    if (this.customerLocations != null) {
      data['customerLocations'] =
          this.customerLocations!.map((v) => v.toJson()).toList();
    }
    data['customerSector'] = this.customerSector;
    data['customerSubSector'] = this.customerSubSector;
    data['cafno'] = this.cafno;
    data['feasibilityRequired'] = this.feasibilityRequired;
    data['voicesrvtype'] = this.voicesrvtype;
    data['didno'] = this.didno;
    data['calendarType'] = this.calendarType;
    data['partnerid'] = this.partnerid;
    data['salesremark'] = this.salesremark;
    data['renewPlanLimit'] = this.renewPlanLimit;
    data['servicetype'] = this.servicetype;
    data['serviceareaid'] = this.serviceareaid;
    data['status'] = this.status;
    data['parentCustomerId'] = this.parentCustomerId;
    // data['parentExperience'] = this.parentExperience;
    data['parentQuotaType'] = this.parentQuotaType;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['billTo'] = this.billTo;
    data['billableCustomerId'] = this.billableCustomerId;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['istrialplan'] = this.istrialplan;
    data['popid'] = this.popid;
    data['staffId'] = this.staffId;
    data['discount'] = this.discount;
    data['flatAmount'] = this.flatAmount;
    data['plangroupid'] = this.plangroupid;
    data['discountType'] = this.discountType;
    data['isCredentialMatchWithAccountNo'] = this.isCredentialMatchWithAccountNo;
    data['discountExpiryDate'] = this.discountExpiryDate;
    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }
    if (this.addressList != null) {
      data['addressList'] = this.addressList!.map((v) => v.toJson()).toList();
    }
    if (this.overChargeList != null) {
      data['overChargeList'] =
          this.overChargeList!.map((v) => v.toJson()).toList();
    }
    if (this.custMacMapppingList != null) {
      data['custMacMapppingList'] =
          this.custMacMapppingList!.map((v) => v.toJson()).toList();
    }

    data['branch'] = this.branch;
    data['oltid'] = this.oltid;
    data['masterdbid'] = this.masterdbid;
    data['splitterid'] = this.splitterid;
    data['nasPort'] = this.nasPort;
    data['framedIp'] = this.framedIp;
    data['framedIpBind'] = this.framedIpBind;
    data['ipPoolNameBind'] = this.ipPoolNameBind;
    data['valleyType'] = this.valleyType;
    data['vlan_id'] = this.vlan_id;
    data['customerArea'] = this.customerArea;
    if (this.paymentDetails != null) {
      data['paymentDetails'] = this.paymentDetails!.toJson();
    }
    data['isCustCaf'] = this.isCustCaf;
    data['dunningCategory'] = this.dunningCategory;
    data['earlybilldays'] = this.earlybilldays;
    data['framedIpv6Address'] = this.framedIpv6Address;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['nasIpAddress'] = this.nasIpAddress;
    data['billday'] = this.billday;
    data['blockNO'] = this.blockNO;
    data['department'] = this.department;
    data['invoiceType'] = this.invoiceType;
    data['planPurchaseType'] = this.planPurchaseType;
    // data['isDunningEnable'] = this.isDunningEnable;
    // data['isNotificationEnable'] = this.isNotificationEnable;
    data['locations'] = this.locations;
    data['isParentLocation'] = this.isParentLocation;
    data['mac_auth_enable'] = this.mac_auth_enable;
    data['mac_provision'] = this.mac_provision;
    data['macRetentionPeriod'] = this.macRetentionPeriod;
    data['macRetentionUnit'] = this.macRetentionUnit;
    data['skipQuotaUpdate'] = this.skipQuotaUpdate;
    data['primaryDNS'] = this.primaryDNS;
    data['primaryIPv6DNS'] = this.primaryIPv6DNS;
    data['secondaryDNS'] = this.secondaryDNS;
    data['secondaryIPv6DNS'] = this.secondaryIPv6DNS;
    data['acctno'] = this.acctno;
    data['addparam1'] = this.addparam1;
    data['addparam2'] = this.addparam2;
    data['addparam3'] = this.addparam3;
    data['addparam4'] = this.addparam4;
    return data;
  }
}

// class PlanMappingList {
//   int? planId;
//   String? service;
//   dynamic validity;
//   dynamic discount;
//   String? billTo;
//   dynamic billableCustomerId;
//   dynamic newAmount;
//   String? invoiceType;
//   dynamic offerPrice;
//   bool? isInvoiceToOrg;
//   bool? istrialplan;
//   String? discountType;
//   String? discountExpiryDate;
//
//   PlanMappingList(
//       {this.planId,
//         this.service,
//         this.validity,
//         this.discount,
//         this.billTo,
//         this.billableCustomerId,
//         this.newAmount,
//         this.invoiceType,
//         this.offerPrice,
//         this.isInvoiceToOrg,
//         this.istrialplan,
//         this.discountType,
//         this.discountExpiryDate});
//
//   PlanMappingList.fromJson(Map<String, dynamic> json) {
//     planId = json['planId'];
//     service = json['service'];
//     validity = json['validity'];
//     discount = json['discount'];
//     billTo = json['billTo'];
//     billableCustomerId = json['billableCustomerId'];
//     newAmount = json['newAmount'];
//     invoiceType = json['invoiceType'];
//     offerPrice = json['offerPrice'];
//     isInvoiceToOrg = json['isInvoiceToOrg'];
//     istrialplan = json['istrialplan'];
//     discountType = json['discountType'];
//     discountExpiryDate = json['discountExpiryDate'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['planId'] = this.planId;
//     data['service'] = this.service;
//     data['validity'] = this.validity;
//     data['discount'] = this.discount;
//     data['billTo'] = this.billTo;
//     data['billableCustomerId'] = this.billableCustomerId;
//     data['newAmount'] = this.newAmount;
//     data['invoiceType'] = this.invoiceType;
//     data['offerPrice'] = this.offerPrice;
//     data['isInvoiceToOrg'] = this.isInvoiceToOrg;
//     data['istrialplan'] = this.istrialplan;
//     data['discountType'] = this.discountType;
//     data['discountExpiryDate'] = this.discountExpiryDate;
//     return data;
//   }
// }

// class PlanMappingList {
//   String? createdate;
//   String? updatedate;
//   String? createdByName;
//   String? lastModifiedByName;
//   int? createdById;
//   int? lastModifiedById;
//   int? id;
//   int? planId;
//   dynamic postpaidPlanPojo;
//   dynamic custid;
//   String? startDate;
//   String? endDate;
//   String? expiryDate;
//   dynamic startDateString;
//   dynamic endDateString;
//   dynamic expiryDateString;
//   dynamic status;
//   int? qospolicyId;
//   dynamic uploadqos;
//   dynamic downloadqos;
//   dynamic uploadts;
//   dynamic downloadts;
//   // List<QuotaList>? quotaList;
//   String? service;
//   bool? isDelete;
//   dynamic offerPrice;
//   dynamic taxAmount;
//   dynamic creditdocid;
//   dynamic walletBalUsed;
//   String? purchaseType;
//   dynamic onlinePurchaseId;
//   String? purchaseFrom;
//   int? debitdocid;
//   dynamic validity;
//   String? planName;
//   dynamic discount;
//   dynamic plangroupid;
//   int? planValidityDays;
//   bool? isInvoiceToOrg;
//   String? billTo;
//   dynamic newAmount;
//   dynamic renewalId;
//   dynamic custRefId;
//   dynamic custRefName;
//   dynamic expiry;
//   String? custPlanStatus;
//   bool? isinvoicestop;
//   bool? istrialplan;
//   bool? isInvoiceCreated;
//   int? graceDays;
//   int? custServiceMappingId;
//   String? plangroup;
//   int? serviceId;
//   dynamic ezyBillServiceId;
//   dynamic oldDiscount;
//   dynamic remarks;
//   String? invoiceType;
//   dynamic traildebitdocid;
//   dynamic isTrialValidityDays;
//   int? trialPlanValidityCount;
//   dynamic ezBillPackageId;
//   dynamic casId;
//   dynamic invoiceformat;
//   dynamic billableCustomerId;
//   String? unitsOfValidity;
//   dynamic extendValidityremarks;
//   LinkAcceptanceDTO? linkAcceptanceDTO;
//   dynamic extendDate;
//   String? discountType;
//   dynamic discountExpiryDate;
//   dynamic startServiceDate;
//   dynamic cprIdForPromiseToPay;
//   bool? isHold;
//   dynamic isVoid;
//   dynamic isContainsCustomerInvoice;
//   dynamic customerCpr;
//   dynamic serialNumber;
//   dynamic voucherId;
//   bool? serviceThroughLead;
//
//   PlanMappingList(
//       {this.createdate,
//         this.updatedate,
//         this.createdByName,
//         this.lastModifiedByName,
//         this.createdById,
//         this.lastModifiedById,
//         this.id,
//         this.planId,
//         this.postpaidPlanPojo,
//         this.custid,
//         this.startDate,
//         this.endDate,
//         this.expiryDate,
//         this.startDateString,
//         this.endDateString,
//         this.expiryDateString,
//         this.status,
//         this.qospolicyId,
//         this.uploadqos,
//         this.downloadqos,
//         this.uploadts,
//         this.downloadts,
//         // this.quotaList,
//         this.service,
//         this.isDelete,
//         this.offerPrice,
//         this.taxAmount,
//         this.creditdocid,
//         this.walletBalUsed,
//         this.purchaseType,
//         this.onlinePurchaseId,
//         this.purchaseFrom,
//         this.debitdocid,
//         this.validity,
//         this.planName,
//         this.discount,
//         this.plangroupid,
//         this.planValidityDays,
//         this.isInvoiceToOrg,
//         this.billTo,
//         this.newAmount,
//         this.renewalId,
//         this.custRefId,
//         this.custRefName,
//         this.expiry,
//         this.custPlanStatus,
//         this.isinvoicestop,
//         this.istrialplan,
//         this.isInvoiceCreated,
//         this.graceDays,
//         this.custServiceMappingId,
//         this.plangroup,
//         this.serviceId,
//         this.ezyBillServiceId,
//         this.oldDiscount,
//         this.remarks,
//         this.invoiceType,
//         this.traildebitdocid,
//         this.isTrialValidityDays,
//         this.trialPlanValidityCount,
//         this.ezBillPackageId,
//         this.casId,
//         this.invoiceformat,
//         this.billableCustomerId,
//         this.unitsOfValidity,
//         this.extendValidityremarks,
//         this.linkAcceptanceDTO,
//         this.extendDate,
//         this.discountType,
//         this.discountExpiryDate,
//         this.startServiceDate,
//         this.cprIdForPromiseToPay,
//         this.isHold,
//         this.isVoid,
//         this.isContainsCustomerInvoice,
//         this.customerCpr,
//         this.serialNumber,
//         this.voucherId,
//         this.serviceThroughLead});
//
//   PlanMappingList.fromJson(Map<String, dynamic> json) {
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     id = json['id'];
//     planId = json['planId'];
//     postpaidPlanPojo = json['postpaidPlanPojo'];
//     custid = json['custid'];
//     startDate = json['startDate'];
//     endDate = json['endDate'];
//     expiryDate = json['expiryDate'];
//     startDateString = json['startDateString'];
//     endDateString = json['endDateString'];
//     expiryDateString = json['expiryDateString'];
//     status = json['status'];
//     qospolicyId = json['qospolicyId'];
//     uploadqos = json['uploadqos'];
//     downloadqos = json['downloadqos'];
//     uploadts = json['uploadts'];
//     downloadts = json['downloadts'];
//     // if (json['quotaList'] != null) {
//     //   quotaList = <QuotaList>[];
//     //   json['quotaList'].forEach((v) {
//     //     quotaList!.add(new QuotaList.fromJson(v));
//     //   });
//     // }
//     service = json['service'];
//     isDelete = json['isDelete'];
//     offerPrice = json['offerPrice'];
//     taxAmount = json['taxAmount'];
//     creditdocid = json['creditdocid'];
//     walletBalUsed = json['walletBalUsed'];
//     purchaseType = json['purchaseType'];
//     onlinePurchaseId = json['onlinePurchaseId'];
//     purchaseFrom = json['purchaseFrom'];
//     debitdocid = json['debitdocid'];
//     validity = json['validity'];
//     planName = json['planName'];
//     discount = json['discount'];
//     plangroupid = json['plangroupid'];
//     planValidityDays = json['planValidityDays'];
//     isInvoiceToOrg = json['isInvoiceToOrg'];
//     billTo = json['billTo'];
//     newAmount = json['newAmount'];
//     renewalId = json['renewalId'];
//     custRefId = json['custRefId'];
//     custRefName = json['custRefName'];
//     expiry = json['expiry'];
//     custPlanStatus = json['custPlanStatus'];
//     isinvoicestop = json['isinvoicestop'];
//     istrialplan = json['istrialplan'];
//     isInvoiceCreated = json['isInvoiceCreated'];
//     graceDays = json['graceDays'];
//     custServiceMappingId = json['custServiceMappingId'];
//     plangroup = json['plangroup'];
//     serviceId = json['serviceId'];
//     ezyBillServiceId = json['ezyBillServiceId'];
//     oldDiscount = json['oldDiscount'];
//     remarks = json['remarks'];
//     invoiceType = json['invoiceType'];
//     traildebitdocid = json['traildebitdocid'];
//     isTrialValidityDays = json['isTrialValidityDays'];
//     trialPlanValidityCount = json['trialPlanValidityCount'];
//     ezBillPackageId = json['ezBillPackageId'];
//     casId = json['casId'];
//     invoiceformat = json['invoiceformat'];
//     billableCustomerId = json['billableCustomerId'];
//     unitsOfValidity = json['unitsOfValidity'];
//     extendValidityremarks = json['extendValidityremarks'];
//     linkAcceptanceDTO = json['linkAcceptanceDTO'] != null
//         ? new LinkAcceptanceDTO.fromJson(json['linkAcceptanceDTO'])
//         : null;
//     extendDate = json['extendDate'];
//     discountType = json['discountType'];
//     discountExpiryDate = json['discountExpiryDate'];
//     startServiceDate = json['startServiceDate'];
//     cprIdForPromiseToPay = json['cprIdForPromiseToPay'];
//     isHold = json['isHold'];
//     isVoid = json['isVoid'];
//     isContainsCustomerInvoice = json['isContainsCustomerInvoice'];
//     customerCpr = json['customerCpr'];
//     serialNumber = json['serialNumber'];
//     voucherId = json['voucherId'];
//     serviceThroughLead = json['serviceThroughLead'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['id'] = this.id;
//     data['planId'] = this.planId;
//     data['postpaidPlanPojo'] = this.postpaidPlanPojo;
//     data['custid'] = this.custid;
//     data['startDate'] = this.startDate;
//     data['endDate'] = this.endDate;
//     data['expiryDate'] = this.expiryDate;
//     data['startDateString'] = this.startDateString;
//     data['endDateString'] = this.endDateString;
//     data['expiryDateString'] = this.expiryDateString;
//     data['status'] = this.status;
//     data['qospolicyId'] = this.qospolicyId;
//     data['uploadqos'] = this.uploadqos;
//     data['downloadqos'] = this.downloadqos;
//     data['uploadts'] = this.uploadts;
//     data['downloadts'] = this.downloadts;
//     // if (this.quotaList != null) {
//     //   data['quotaList'] = this.quotaList!.map((v) => v.toJson()).toList();
//     // }
//     data['service'] = this.service;
//     data['isDelete'] = this.isDelete;
//     data['offerPrice'] = this.offerPrice;
//     data['taxAmount'] = this.taxAmount;
//     data['creditdocid'] = this.creditdocid;
//     data['walletBalUsed'] = this.walletBalUsed;
//     data['purchaseType'] = this.purchaseType;
//     data['onlinePurchaseId'] = this.onlinePurchaseId;
//     data['purchaseFrom'] = this.purchaseFrom;
//     data['debitdocid'] = this.debitdocid;
//     data['validity'] = this.validity;
//     data['planName'] = this.planName;
//     data['discount'] = this.discount;
//     data['plangroupid'] = this.plangroupid;
//     data['planValidityDays'] = this.planValidityDays;
//     data['isInvoiceToOrg'] = this.isInvoiceToOrg;
//     data['billTo'] = this.billTo;
//     data['newAmount'] = this.newAmount;
//     data['renewalId'] = this.renewalId;
//     data['custRefId'] = this.custRefId;
//     data['custRefName'] = this.custRefName;
//     data['expiry'] = this.expiry;
//     data['custPlanStatus'] = this.custPlanStatus;
//     data['isinvoicestop'] = this.isinvoicestop;
//     data['istrialplan'] = this.istrialplan;
//     data['isInvoiceCreated'] = this.isInvoiceCreated;
//     data['graceDays'] = this.graceDays;
//     data['custServiceMappingId'] = this.custServiceMappingId;
//     data['plangroup'] = this.plangroup;
//     data['serviceId'] = this.serviceId;
//     data['ezyBillServiceId'] = this.ezyBillServiceId;
//     data['oldDiscount'] = this.oldDiscount;
//     data['remarks'] = this.remarks;
//     data['invoiceType'] = this.invoiceType;
//     data['traildebitdocid'] = this.traildebitdocid;
//     data['isTrialValidityDays'] = this.isTrialValidityDays;
//     data['trialPlanValidityCount'] = this.trialPlanValidityCount;
//     data['ezBillPackageId'] = this.ezBillPackageId;
//     data['casId'] = this.casId;
//     data['invoiceformat'] = this.invoiceformat;
//     data['billableCustomerId'] = this.billableCustomerId;
//     data['unitsOfValidity'] = this.unitsOfValidity;
//     data['extendValidityremarks'] = this.extendValidityremarks;
//     if (this.linkAcceptanceDTO != null) {
//       data['linkAcceptanceDTO'] = this.linkAcceptanceDTO!.toJson();
//     }
//     data['extendDate'] = this.extendDate;
//     data['discountType'] = this.discountType;
//     data['discountExpiryDate'] = this.discountExpiryDate;
//     data['startServiceDate'] = this.startServiceDate;
//     data['cprIdForPromiseToPay'] = this.cprIdForPromiseToPay;
//     data['isHold'] = this.isHold;
//     data['isVoid'] = this.isVoid;
//     data['isContainsCustomerInvoice'] = this.isContainsCustomerInvoice;
//     data['customerCpr'] = this.customerCpr;
//     data['serialNumber'] = this.serialNumber;
//     data['voucherId'] = this.voucherId;
//     data['serviceThroughLead'] = this.serviceThroughLead;
//     return data;
//   }
// }

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
  dynamic totalQuota;
  dynamic usedQuota;
  String? quotaUnit;
  dynamic timeTotalQuota;
  dynamic timeQuotaUsed;
  dynamic timeQuotaUnit;
  bool? isDelete;
  dynamic totalQuotaKB;
  dynamic usedQuotaKB;
  dynamic timeUsedQuotaSec;
  dynamic timeTotalQuotaSec;
  dynamic didtotalquota;
  dynamic didusedquota;
  dynamic intercomtotalquota;
  dynamic intercomusedquota;
  dynamic didQuotaUnit;
  dynamic intercomQuotaUnit;
  dynamic planName;
  dynamic cprId;
  dynamic currentSessionUsageTime;
  dynamic currentSessionUsageVolume;
  String? lastQuotaReset;
  dynamic parentQuotaType;
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

class LinkAcceptanceDTO {
  dynamic id;
  String? circuitName;
  dynamic circuitStatus;
  dynamic cafNo;
  dynamic uploadCAF;
  dynamic customerName;
  dynamic accountNumber;
  dynamic typeOfLink;
  dynamic planService;
  dynamic linkInstallationDate;
  dynamic linkAcceptanceDate;
  dynamic purchaseOrderDate;
  dynamic partner;
  dynamic expiryDate;
  dynamic distance;
  dynamic distanceUnit;
  dynamic bandwidth;
  dynamic uploadQOS;
  dynamic downloadQOS;
  dynamic linkRouterLocation;
  dynamic linkPortType;
  dynamic linkRouterIP;
  dynamic linkPortOnRouter;
  dynamic bandwidthType;
  dynamic linkRouterName;
  dynamic circuitBillingId;
  dynamic pop;
  dynamic associatedLevel;
  dynamic locationLevel1;
  dynamic locationLevel2;
  dynamic locationLevel3;
  dynamic locationLevel4;
  dynamic baseStationId1;
  dynamic baseStationId2;
  dynamic terminationAddress;
  dynamic note;
  dynamic contactPerson;
  dynamic contactPerson1;
  dynamic mobileNumber;
  dynamic mobileNumber1;
  dynamic landLineNumber;
  dynamic landLineNumber1;
  dynamic emailId;
  dynamic emailId1;
  dynamic remarks;
  dynamic otcChargesFile;
  dynamic serviceChargerFile;
  dynamic staticOrPooledIP;
  dynamic chargeTypeFile;
  dynamic billingCycle;
  dynamic billingType;
  dynamic billable;
  dynamic billingGroup;
  dynamic payable;
  dynamic enableProcessing;
  dynamic deposite;
  dynamic poNumber;
  dynamic billRemark;
  dynamic fullName;
  dynamic organisation;
  dynamic address1;
  dynamic address2;
  dynamic city;
  dynamic zipCode;
  dynamic state;
  dynamic country;
  bool? isDeleted;
  String? status;
  dynamic custId;
  dynamic serviceAreaType;
  dynamic branch;
  dynamic connectionType;
  dynamic vlanid;

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

// class AddressList {
//   String? addressType;
//   String? landmark;
//   int? areaId;
//   int? pincodeId;
//   int? cityId;
//   int? stateId;
//   int? countryId;
//   String? landmark1;
//   String? version;
//
//   AddressList(
//       {this.addressType,
//         this.landmark,
//         this.areaId,
//         this.pincodeId,
//         this.cityId,
//         this.stateId,
//         this.countryId,
//         this.landmark1,
//         this.version});
//
//   AddressList.fromJson(Map<String, dynamic> json) {
//     addressType = json['addressType'];
//     landmark = json['landmark'];
//     areaId = json['areaId'];
//     pincodeId = json['pincodeId'];
//     cityId = json['cityId'];
//     stateId = json['stateId'];
//     countryId = json['countryId'];
//     landmark1 = json['landmark1'];
//     version = json['version'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['addressType'] = this.addressType;
//     data['landmark'] = this.landmark;
//     data['areaId'] = this.areaId;
//     data['pincodeId'] = this.pincodeId;
//     data['cityId'] = this.cityId;
//     data['stateId'] = this.stateId;
//     data['countryId'] = this.countryId;
//     data['landmark1'] = this.landmark1;
//     data['version'] = this.version;
//     return data;
//   }
// }

class OverChargesDetails {
  dynamic type;
  int? chargeid;
  num? validity;
  num? price;
  num? actualprice;
  dynamic chargeDate;
  int? planid;
  dynamic unitsOfValidity;
  dynamic billingCycle;
  int? id;

  OverChargesDetails(
      {this.type,
      this.chargeid,
      this.validity,
      this.price,
      this.actualprice,
      this.chargeDate,
      this.planid,
      this.unitsOfValidity,
      this.billingCycle,
      this.id});

  OverChargesDetails.fromJson(Map<String, dynamic> json) {
    type = json['type'];
    chargeid = json['chargeid'];
    validity = json['validity'];
    price = json['price'];
    actualprice = json['actualprice'];
    chargeDate = json['charge_date'];
    planid = json['planid'];
    unitsOfValidity = json['unitsOfValidity'];
    billingCycle = json['billingCycle'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['type'] = this.type;
    data['chargeid'] = this.chargeid;
    data['validity'] = this.validity;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['charge_date'] = this.chargeDate;
    data['planid'] = this.planid;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['billingCycle'] = this.billingCycle;
    data['id'] = this.id;
    return data;
  }
}

class CustMacMapppingList {
  dynamic macAddress;

  CustMacMapppingList({this.macAddress});

  CustMacMapppingList.fromJson(Map<String, dynamic> json) {
    macAddress = json['macAddress'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['macAddress'] = this.macAddress;
    return data;
  }
}

class PaymentDetails {
  dynamic amount;
  String? paymode;
  String? referenceno;
  String? paymentdate;

  PaymentDetails(
      {this.amount, this.paymode, this.referenceno, this.paymentdate});

  PaymentDetails.fromJson(Map<String, dynamic> json) {
    amount = json['amount'];
    paymode = json['paymode'];
    referenceno = json['referenceno'];
    paymentdate = json['paymentdate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['amount'] = this.amount;
    data['paymode'] = this.paymode;
    data['referenceno'] = this.referenceno;
    data['paymentdate'] = this.paymentdate;
    return data;
  }
}

class CustomerLocations {
  int? locationId;
  String? mac;
  String? isParentLocation;

  CustomerLocations({this.locationId, this.mac, this.isParentLocation});

  CustomerLocations.fromJson(Map<String, dynamic> json) {
    locationId = json['locationId'];
    mac = json['mac'];
    isParentLocation = json['isParentLocation'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['locationId'] = this.locationId;
    data['mac'] = this.mac;
    data['isParentLocation'] = this.isParentLocation;
    return data;
  }
}
