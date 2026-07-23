import 'package:savbill/util/strings.dart';

import 'package:savbill/util/strings.dart';

import 'package:savbill/util/strings.dart';

import '../../service_management/request/add_service_req.dart';

class LeadSaveReq {
  String? aadhar;
  List<LeadAddressList>? addressList;
  String? altmobile1;
  String? altmobile2;
  String? altmobile3;
  String? altmobile4;
  String? amount;
  dynamic assigneeName;
  dynamic billableCustomerId;
  String? billTo;
  int? branchId;
  dynamic branch;
  String? cafno;
  dynamic leadOriginTypes;
  String? calendarType;
  String? competitorDuration;
  String? contactperson;
  String? countryCode;
  String? custlabel;
  String? dunningCategory;
  dynamic customerId;
  String? custtype;
  int? billday;
  dynamic dateOfBirth;
  String? didno;
  dynamic discount;
  dynamic discountExpiryDate;
  dynamic discountType;
  String? durationUnits;
  String? email;
  dynamic existingCustomerId;
  String? expiry;
  int? failcount;
  String? feasibility;
  dynamic feasibilityRemark;
  String? feedback;
  String? firstname;
  double? flatAmount;
  String? gender;
  String? gst;
  String? heardAboutSubisuFrom;
  dynamic id;
  String? insideValley;
  String? isCustCaf;
  bool? isInvoiceToOrg;
  bool? isLeadQuickInv;
  bool? istrialplan;
  String? landlineNumber;
  String? lastname;
  String? latitude;
  dynamic leadAgentId;
  dynamic leadBranchId;
  String? leadCategory;
  dynamic leadCustomerCategory;
  int? leadCustomerId;
  String? leadCustomerSector;
  String? leadCustomerSubSector;
  String? leadCustomerType;
  String? department;
  dynamic leadNo;
  int? leadId;
  String? leadOriginType;
  int? leadPartnerId;
  dynamic leadServiceAreaId;
  int? leadSourceId;
  dynamic leadSourceName;
  dynamic leadStaffId;
  String? leadStatus;
  dynamic leadSubSourceId;
  dynamic leadSubSourceName;
  String? leadType;
  dynamic leadvariety;
  String? longitude;
  String? mobile;
  dynamic nextApproveStaffId;
  dynamic nextTeamMappingId;
  String? outsideValley;
  List<dynamic>? overChargeList;
  List<dynamic>? custMacMapppingList;
  String? pan;
  int? parentCustomerId;
  int? partnerid;
  int? blockNo;
  String? passportNo;
  String? password;
  String? loginPassword;
  // dynamic paymentDetails;
  PaymentDetails? paymentDetails;
  String? phone;
  dynamic plangroupid;
  List<LeadSavePlanMappingList>? planMappingList;
  dynamic planType;
  dynamic popManagementId;
  bool? presentCheckForPayment;
  bool? presentCheckForPermanent;
  double? previousAmount;
  String? previousMonth;
  int? renewPlanLimit;
  bool? isCredentialMatchWithAccountNo;
  dynamic previousVendor;
  dynamic rejectReasonId;
  dynamic rejectReasonName;
  dynamic rejectSubReasonId;
  dynamic rejectSubReasonName;
  String? requireServiceType;
  String? salesremark;
  dynamic secondaryContactDetails;
  String? secondaryEmail;
  String? secondaryPhone;
  int? serviceareaid;
  String? servicerType;
  String? servicetype;
  dynamic status;
  String? tinNo;
  String? title;
  String? username;
  String? loginUsername;
  String? valleyType;
  String? voicesrvtype;
  String? leadIdentity;
  String? leadDepartment;
  bool? isLeadFromCWSC;
  int? approveMvnoId;
  int? approveStaffId;
  int? approveCurrentLoggedInStaffId;
  String? approveStatus;
  int? approverNextLeadApprover;
  String? approveFirstname;
  String? approveUsername;
  int? approveServiceareaid;
  // int? leadStatus;
  // int? customerId;
  bool? isCustomerCafeIsUpdated;
  // String? assigneeName;


  LeadSaveReq(
      {this.aadhar,
      this.addressList,
      this.altmobile1,
      this.altmobile2,
      this.altmobile3,
      this.altmobile4,
      this.amount,
      this.assigneeName,
      this.billableCustomerId,
      this.billTo,
      this.branchId,
      this.branch,
      this.cafno,
      this.leadOriginTypes,
      this.calendarType,
      this.competitorDuration,
      this.contactperson,
      this.countryCode,
      this.custlabel,
      this.dunningCategory,
      this.customerId,
      this.custtype,
      this.billday,
      this.dateOfBirth,
      this.didno,
      this.discount,
      this.discountExpiryDate,
      this.discountType,
      this.durationUnits,
      this.email,
      this.existingCustomerId,
      this.expiry,
      this.failcount,
      this.feasibility,
      this.feasibilityRemark,
      this.feedback,
      this.firstname,
      this.flatAmount,
      this.gender,
      this.gst,
      this.heardAboutSubisuFrom,
      this.id,
      this.insideValley,
      this.isCustCaf,
      this.isInvoiceToOrg,
      this.isLeadQuickInv,
      this.istrialplan,
      this.landlineNumber,
      this.lastname,
      this.latitude,
      this.leadAgentId,
      this.leadBranchId,
      this.leadCategory,
      this.leadCustomerCategory,
      this.leadCustomerId,
      this.leadCustomerSector,
      this.leadCustomerSubSector,
      this.leadCustomerType,
      this.department,
      this.leadNo,
      this.leadId,
      this.leadOriginType,
      this.leadPartnerId,
      this.leadServiceAreaId,
      this.leadSourceId,
      this.leadSourceName,
      this.leadStaffId,
      this.leadStatus,
      this.leadSubSourceId,
      this.leadSubSourceName,
      this.leadType,
      this.leadvariety,
      this.longitude,
      this.mobile,
      this.nextApproveStaffId,
      this.nextTeamMappingId,
      this.outsideValley,
        this.overChargeList,
        this.custMacMapppingList,
      this.pan,
      this.parentCustomerId,
      this.partnerid,
      this.blockNo,
      this.passportNo,
      this.password,
      this.loginPassword,

      this.paymentDetails,
      this.phone,
      this.plangroupid,
      this.planMappingList,
      this.planType,
      this.popManagementId,
      this.presentCheckForPayment,
      this.presentCheckForPermanent,
      this.previousAmount,
      this.previousMonth,
      this.renewPlanLimit,
      this.isCredentialMatchWithAccountNo,
      this.previousVendor,
      this.rejectReasonId,
      this.rejectReasonName,
      this.rejectSubReasonId,
      this.rejectSubReasonName,
      this.requireServiceType,
      this.salesremark,
      this.secondaryContactDetails,
      this.secondaryEmail,
      this.secondaryPhone,
      this.serviceareaid,
      this.servicerType,
      this.servicetype,
      this.status,
      this.tinNo,
      this.title,
      this.username,
      this.loginUsername,
      this.valleyType,
      this.voicesrvtype,
      this.leadIdentity,
      this.leadDepartment,
      this.isLeadFromCWSC,
      this.approveMvnoId,
      this.approveStaffId,
      this.approveCurrentLoggedInStaffId,
      this.approveStatus,
      this.approverNextLeadApprover,
      this.approveFirstname,
      this.approveUsername,
      this.approveServiceareaid,
      // this.leadStatus,
      // this.customerId,
      this.isCustomerCafeIsUpdated,
      // this.assigneeName,
  });

  LeadSaveReq.fromJson(Map<String, dynamic> json) {
    aadhar = json['aadhar'];
    if (json['addressList'] != null) {
      addressList = <LeadAddressList>[];
      json['addressList'].forEach((v) {
        addressList!.add(new LeadAddressList.fromJson(v));
      });
    }
    altmobile1 = json['altmobile1'];
    altmobile2 = json['altmobile2'];
    altmobile3 = json['altmobile3'];
    altmobile4 = json['altmobile4'];
    amount = json['amount'];
    assigneeName = json['assigneeName'];
    billableCustomerId = json['billableCustomerId'];
    billTo = json['billTo'];
    branchId = json['branchId'];
    branch = json['branch'];
    cafno = json['cafno'];
    leadOriginTypes = json['leadOriginTypes'];
    calendarType = json['calendarType'];
    competitorDuration = json['competitorDuration'];
    contactperson = json['contactperson'];
    countryCode = json['countryCode'];
    custlabel = json['custlabel'];
    dunningCategory = json['dunningCategory'];
    customerId = json['customerId'];
    custtype = json['custtype'];
    billday = json['billday'];
    dateOfBirth = json['dateOfBirth'];
    didno = json['didno'];
    discount = json['discount'];
    discountExpiryDate = json['discountExpiryDate'];
    discountType = json['discountType'];
    durationUnits = json['durationUnits'];
    email = json['email'];
    existingCustomerId = json['existingCustomerId'];
    expiry = json['expiry'];
    failcount = json['failcount'];
    feasibility = json['feasibility'];
    feasibilityRemark = json['feasibilityRemark'];
    feedback = json['feedback'];
    firstname = json['firstname'];
    flatAmount = json['flatAmount'];
    gender = json['gender'];
    gst = json['gst'];
    heardAboutSubisuFrom = json['heardAboutSubisuFrom'];
    id = json['id'];
    insideValley = json['insideValley'];
    isCustCaf = json['isCustCaf'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    isLeadQuickInv = json['isLeadQuickInv'];
    istrialplan = json['istrialplan'];
    landlineNumber = json['landlineNumber'];
    lastname = json['lastname'];
    latitude = json['latitude'];
    leadAgentId = json['leadAgentId'];
    leadBranchId = json['leadBranchId'];
    leadCategory = json['leadCategory'];
    leadCustomerCategory = json['leadCustomerCategory'];
    leadCustomerId = json['leadCustomerId'];
    leadCustomerSector = json['leadCustomerSector'];
    leadCustomerSubSector = json['leadCustomerSubSector'];
    leadCustomerType = json['leadCustomerType'];
    department = json['department'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    leadOriginType = json['leadOriginType'];
    leadPartnerId = json['leadPartnerId'];
    leadServiceAreaId = json['leadServiceAreaId'];
    leadSourceId = json['leadSourceId'];
    leadSourceName = json['leadSourceName'];
    leadStaffId = json['leadStaffId'];
    leadStatus = json['leadStatus'];
    leadSubSourceId = json['leadSubSourceId'];
    leadSubSourceName = json['leadSubSourceName'];
    leadType = json['leadType'];
    leadvariety = json['leadvariety'];
    longitude = json['longitude'];
    mobile = json['mobile'];
    nextApproveStaffId = json['nextApproveStaffId'];
    nextTeamMappingId = json['nextTeamMappingId'];
    outsideValley = json['outsideValley'];


    // overChargeList = json['overChargeList'];
    // if (json['overChargeList'] != null) {
    //   overChargeList = <dynamic>[];
    //   json['overChargeList'].forEach((v) {
    //     overChargeList!.add(New String[].fromJson(v));
    //   });
    // }

    overChargeList = List<dynamic>.from(json["overChargeList"].map((x) => x));
    custMacMapppingList = List<dynamic>.from(json["custMacMapppingList"].map((x) => x));
    pan = json['pan'];
    parentCustomerId = json['parentCustomerId'];
    partnerid = json['partnerid'];
    blockNo = json['blockNo'];
    passportNo = json['passportNo'];
    password = json['password'];
    loginPassword = json['loginPassword'];
    paymentDetails = json['paymentDetails'] != null
        ? new PaymentDetails.fromJson(json['paymentDetails'])
        : null;
    // paymentDetails = json['paymentDetails'];
    phone = json['phone'];
    plangroupid = json['plangroupid'];
    if (json['planMappingList'] != null) {
      planMappingList = <LeadSavePlanMappingList>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new LeadSavePlanMappingList.fromJson(v));
      });
    }
    planType = json['planType'];
    popManagementId = json['popManagementId'];
    presentCheckForPayment = json['presentCheckForPayment'];
    presentCheckForPermanent = json['presentCheckForPermanent'];
    previousAmount = json['previousAmount'];
    previousMonth = json['previousMonth'];
    renewPlanLimit = json['renewPlanLimit'];
    isCredentialMatchWithAccountNo = json['isCredentialMatchWithAccountNo'];
    previousVendor = json['previousVendor'];
    rejectReasonId = json['rejectReasonId'];
    rejectReasonName = json['rejectReasonName'];
    rejectSubReasonId = json['rejectSubReasonId'];
    rejectSubReasonName = json['rejectSubReasonName'];
    requireServiceType = json['requireServiceType'];
    salesremark = json['salesremark'];
    secondaryContactDetails = json['secondaryContactDetails'];
    secondaryEmail = json['secondaryEmail'];
    secondaryPhone = json['secondaryPhone'];
    serviceareaid = json['serviceareaid'];
    servicerType = json['servicerType'];
    servicetype = json['servicetype'];
    status = json['status'];
    tinNo = json['tinNo'];
    title = json['title'];
    username = json['username'];
    loginUsername = json['loginUsername'];
    valleyType = json['valleyType'];
    voicesrvtype = json['voicesrvtype'];
    leadIdentity = json['leadIdentity'];
    leadDepartment = json['leadDepartment'];
    isLeadFromCWSC = json['isLeadFromCWSC'];
    approveMvnoId = json['approveMvnoId'];
    approveStaffId = json['approveStaffId'];
    approveCurrentLoggedInStaffId = json['approveCurrentLoggedInStaffId'];
    approveStatus = json['approveStatus'];
    approverNextLeadApprover = json['approverNextLeadApprover'];
    approveFirstname = json['approveFirstname'];
    approveUsername = json['approveUsername'];
    approveServiceareaid = json['approveServiceareaid'];
    // this.leadStatus,
    // this.customerId,
    isCustomerCafeIsUpdated = json['isCustomerCafeIsUpdated'];
    // this.assigneeName,
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['aadhar'] = this.aadhar;
    if (this.addressList != null) {
      data['addressList'] = this.addressList!.map((v) => v.toJson()).toList();
    }
    data['altmobile1'] = this.altmobile1;
    data['altmobile2'] = this.altmobile2;
    data['altmobile3'] = this.altmobile3;
    data['altmobile4'] = this.altmobile4;
    data['amount'] = this.amount;
    data['assigneeName'] = this.assigneeName;
    data['billableCustomerId'] = this.billableCustomerId;
    data['billTo'] = this.billTo;
    data['branchId'] = this.branchId;
    data['branch'] = this.branch;
    data['cafno'] = this.cafno;
    data['leadOriginTypes'] = this.leadOriginTypes;
    data['calendarType'] = this.calendarType;
    data['competitorDuration'] = this.competitorDuration;
    data['contactperson'] = this.contactperson;
    data['countryCode'] = this.countryCode;
    data['custlabel'] = this.custlabel;
    data['dunningCategory'] = this.dunningCategory;
    data['customerId'] = this.customerId;
    data['custtype'] = this.custtype;
    data['billday'] = this.billday;
    data['dateOfBirth'] = this.dateOfBirth;
    data['didno'] = this.didno;
    data['discount'] = this.discount;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['discountType'] = this.discountType;
    data['durationUnits'] = this.durationUnits;
    data['email'] = this.email;
    data['existingCustomerId'] = this.existingCustomerId;
    data['expiry'] = this.expiry;
    data['failcount'] = this.failcount;
    data['feasibility'] = this.feasibility;
    data['feasibilityRemark'] = this.feasibilityRemark;
    data['feedback'] = this.feedback;
    data['firstname'] = this.firstname;
    data['flatAmount'] = this.flatAmount;
    data['gender'] = this.gender;
    data['gst'] = this.gst;
    data['heardAboutSubisuFrom'] = this.heardAboutSubisuFrom;
    data['id'] = this.id;
    data['insideValley'] = this.insideValley;
    data['isCustCaf'] = this.isCustCaf;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['isLeadQuickInv'] = this.isLeadQuickInv;
    data['istrialplan'] = this.istrialplan;
    data['landlineNumber'] = this.landlineNumber;
    data['lastname'] = this.lastname;
    data['latitude'] = this.latitude;
    data['leadAgentId'] = this.leadAgentId;
    data['leadBranchId'] = this.leadBranchId;
    data['leadCategory'] = this.leadCategory;
    data['leadCustomerCategory'] = this.leadCustomerCategory;
    data['leadCustomerId'] = this.leadCustomerId;
    data['leadCustomerSector'] = this.leadCustomerSector;
    data['leadCustomerSubSector'] = this.leadCustomerSubSector;
    data['leadCustomerType'] = this.leadCustomerType;
    data['department'] = this.department;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['leadOriginType'] = this.leadOriginType;
    data['leadPartnerId'] = this.leadPartnerId;
    data['leadServiceAreaId'] = this.leadServiceAreaId;
    data['leadSourceId'] = this.leadSourceId;
    data['leadSourceName'] = this.leadSourceName;
    data['leadStaffId'] = this.leadStaffId;
    data['leadStatus'] = this.leadStatus;
    data['leadSubSourceId'] = this.leadSubSourceId;
    data['leadSubSourceName'] = this.leadSubSourceName;
    data['leadType'] = this.leadType;
    data['leadvariety'] = this.leadvariety;
    data['longitude'] = this.longitude;
    data['mobile'] = this.mobile;
    data['nextApproveStaffId'] = this.nextApproveStaffId;
    data['nextTeamMappingId'] = this.nextTeamMappingId;
    data['outsideValley'] = this.outsideValley;

    if (this.overChargeList != null) {
      data['overChargeList'] = this.overChargeList!.map((v) => v.toJson()).toList();
    }
    if (this.custMacMapppingList != null) {
      data['custMacMapppingList'] = this.custMacMapppingList!.map((v) => v.toJson()).toList();
    }

    data['pan'] = this.pan;
    data['parentCustomerId'] = this.parentCustomerId;
    data['partnerid'] = this.partnerid;
    data['blockNo'] = this.blockNo;
    data['passportNo'] = this.passportNo;
    data['password'] = this.password;
    data['loginPassword'] = this.loginPassword;

    if (this.paymentDetails != null) {
      data['paymentDetails'] = this.paymentDetails!.toJson();
    }

    // data['paymentDetails'] = this.paymentDetails;
    data['phone'] = this.phone;
    data['plangroupid'] = this.plangroupid;
    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }
    data['planType'] = this.planType;
    data['popManagementId'] = this.popManagementId;
    data['presentCheckForPayment'] = this.presentCheckForPayment;
    data['presentCheckForPermanent'] = this.presentCheckForPermanent;
    data['previousAmount'] = this.previousAmount;
    data['previousMonth'] = this.previousMonth;
    data['renewPlanLimit'] = this.renewPlanLimit;
    data['isCredentialMatchWithAccountNo'] = this.isCredentialMatchWithAccountNo;
    data['previousVendor'] = this.previousVendor;
    data['rejectReasonId'] = this.rejectReasonId;
    data['rejectReasonName'] = this.rejectReasonName;
    data['rejectSubReasonId'] = this.rejectSubReasonId;
    data['rejectSubReasonName'] = this.rejectSubReasonName;
    data['requireServiceType'] = this.requireServiceType;
    data['salesremark'] = this.salesremark;
    data['secondaryContactDetails'] = this.secondaryContactDetails;
    data['secondaryEmail'] = this.secondaryEmail;
    data['secondaryPhone'] = this.secondaryPhone;
    data['serviceareaid'] = this.serviceareaid;
    data['servicerType'] = this.servicerType;
    data['servicetype'] = this.servicetype;
    data['status'] = this.status;
    data['tinNo'] = this.tinNo;
    data['title'] = this.title;
    data['username'] = this.username;
    data['loginUsername'] = this.loginUsername;
    data['valleyType'] = this.valleyType;
    data['voicesrvtype'] = this.voicesrvtype;
    data['leadIdentity'] = this.leadIdentity;
    data['leadDepartment'] = this.leadDepartment;
    data['isLeadFromCWSC'] = this.isLeadFromCWSC;

    data['approveMvnoId'] = this.approveMvnoId;
    data['approveStaffId'] = this.approveStaffId;
    data['approveCurrentLoggedInStaffId'] = this.approveCurrentLoggedInStaffId;
    data['approveStatus'] = this.approveStatus;
    data['approverNextLeadApprover'] = this.approverNextLeadApprover;
    data['approveFirstname'] = this.approveFirstname;
    data['approveUsername'] = this.approveUsername;
    data['approveServiceareaid'] = this.approveServiceareaid;
    data['isCustomerCafeIsUpdated'] = this.isCustomerCafeIsUpdated;
    return data;
  }
}

class LeadAddressList {
  String? addressType;
  String? landmark;
  int? areaId;
  int? pincodeId;
  int? building_mgmt_id;
  String? buildingNumber;
  int? subareaId;
  int? cityId;
  int? stateId;
  int? countryId;
  String? streetName;
  String? houseNo;

  LeadAddressList(
      {this.addressType,
      this.landmark,
      this.areaId,
      this.pincodeId,
        this.building_mgmt_id,
        this.buildingNumber,
        this.subareaId,
      this.cityId,
      this.stateId,
      this.countryId,
      this.streetName,
      this.houseNo});

  LeadAddressList.fromJson(Map<String, dynamic> json) {
    addressType = json['addressType'];
    landmark = json['landmark'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    building_mgmt_id = json['building_mgmt_id'];
    buildingNumber = json['buildingNumber'];
    subareaId = json['subareaId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    streetName = json['streetName'];
    houseNo = json['houseNo'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['addressType'] = this.addressType;
    data['landmark'] = this.landmark;
    data['areaId'] = this.areaId;
    data['pincodeId'] = this.pincodeId;
    data['building_mgmt_id'] = this.building_mgmt_id;
    data['buildingNumber'] = this.buildingNumber;
    data['subareaId'] = this.subareaId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;
    data['streetName'] = this.streetName;
    data['houseNo'] = this.houseNo;
    return data;
  }
}

class LeadSavePlanMappingList {
  int? planId;
  dynamic service;
  double? validity;
  double? discount;
  String? billTo;
  int? billableCustomerId;
  double? offerPrice;
  double? newAmount;
  bool? isInvoiceToOrg;
  bool? istrialplan;
  String? discountType;
  String? discountExpiryDate;

  LeadSavePlanMappingList(
      {this.planId,
      this.service,
      this.validity,
      this.discount,
      this.billTo,
      this.billableCustomerId,
      this.offerPrice,
      this.newAmount,
      this.isInvoiceToOrg,
      this.istrialplan,
      this.discountType,
      this.discountExpiryDate});

  LeadSavePlanMappingList.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    service = json['service'];
    validity = json['validity'];
    discount = json['discount'];
    billTo = json['billTo'];
    billableCustomerId = json['billableCustomerId'];
    offerPrice = json['offerPrice'];
    newAmount = json['newAmount'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    istrialplan = json['istrialplan'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['service'] = this.service;
    data['validity'] = this.validity;
    data['discount'] = this.discount;
    data['billTo'] = this.billTo;
    data['billableCustomerId'] = this.billableCustomerId;
    data['offerPrice'] = this.offerPrice;
    data['newAmount'] = this.newAmount;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['istrialplan'] = this.istrialplan;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    return data;
  }
}

class PaymentDetails {
  int? amount;
  String? paymentdate;
  String? paymode;
  String? referenceno;

  PaymentDetails(
      {this.amount, this.paymentdate, this.paymode, this.referenceno});

  PaymentDetails.fromJson(Map<String, dynamic> json) {
    amount = json['amount'];
    paymentdate = json['paymentdate'];
    paymode = json['paymode'];
    referenceno = json['referenceno'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['amount'] = this.amount;
    data['paymentdate'] = this.paymentdate;
    data['paymode'] = this.paymode;
    data['referenceno'] = this.referenceno;
    return data;
  }
}
