import 'package:savbill/webservices/base_response.dart';

class LAAssignListRes  extends BaseResponse{
  LeadMasterList? leadMasterList;
  String? timestamp;
  int? status;

  LAAssignListRes({this.leadMasterList, this.timestamp, this.status});

  LAAssignListRes.fromJson(Map<String, dynamic> json) {
    leadMasterList = json['leadMasterList'] != null
        ? new LeadMasterList.fromJson(json['leadMasterList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.leadMasterList != null) {
      data['leadMasterList'] = this.leadMasterList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class LeadMasterList {
  List<LAAssignContent>? content;
  Pageable? pageable;
  bool? last;
  int? totalPages;
  int? totalElements;
  bool? first;
  Sort? sort;
  int? numberOfElements;
  int? size;
  int? number;
  bool? empty;

  LeadMasterList(
      {this.content,
        this.pageable,
        this.last,
        this.totalPages,
        this.totalElements,
        this.first,
        this.sort,
        this.numberOfElements,
        this.size,
        this.number,
        this.empty});

  LeadMasterList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <LAAssignContent>[];
      json['content'].forEach((v) {
        content!.add(new LAAssignContent.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    last = json['last'];
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    first = json['first'];
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    numberOfElements = json['numberOfElements'];
    size = json['size'];
    number = json['number'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.content != null) {
      data['content'] = this.content!.map((v) => v.toJson()).toList();
    }
    if (this.pageable != null) {
      data['pageable'] = this.pageable!.toJson();
    }
    data['last'] = this.last;
    data['totalPages'] = this.totalPages;
    data['totalElements'] = this.totalElements;
    data['first'] = this.first;
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['numberOfElements'] = this.numberOfElements;
    data['size'] = this.size;
    data['number'] = this.number;
    data['empty'] = this.empty;
    return data;
  }
}

class LAAssignContent {
  int? id;
  dynamic username;
  dynamic password;
  String? firstname;
  String? lastname;
  dynamic email;
  String? title;
  dynamic custname;
  dynamic contactperson;
  String? pan;
  String? gst;
  String? aadhar;
  dynamic status;
  dynamic cstatus;
  int? failcount;
  dynamic acctno;
  String? custtype;
  String? phone;
  dynamic billday;
  int? partnerid;
  dynamic onuid;
  dynamic nextBillDate;
  dynamic lastBillDate;
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
  String? lastPasswordChange;
  dynamic lastpasswordchangestring;

  dynamic custLeger;

  PaymentDetails? paymentDetails;
  dynamic flashMsg;
  bool? mactelflag;
  String? mobile;
  String? countryCode;
  dynamic cafno;
  dynamic altmobile;
  dynamic altmobile1;
  dynamic altmobile2;
  dynamic altmobile3;
  dynamic altmobile4;
  dynamic altphone;
  dynamic altemail;
  dynamic fax;
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
  dynamic firstActivationDate;
  dynamic createDateString;
  dynamic updateDateString;
  dynamic latitude;
  dynamic longitude;
  dynamic url;
  dynamic gisCode;
  String? salesremark;
  String? servicetype;
  String? isCustCaf;
  dynamic previousCafApprover;
  dynamic nextCafApprover;
  dynamic serviceareaName;
  dynamic cafApproveStatus;
  dynamic tinNo;
  String? passportNo;
  String? dunningCategory;
  dynamic plangroupid;
  dynamic parentCustomerId;
  dynamic parentCustomerName;
  dynamic invoiceType;
  String? calendarType;
  dynamic discount;
  int? leadSourceId;
  dynamic leadSubSourceId;
  dynamic rejectReasonId;
  dynamic rejectSubReasonId;
  dynamic rejectReasonName;
  dynamic rejectSubReasonName;
  String? leadSourceName;
  dynamic leadSubSourceName;
  dynamic reasonToChangeServiceProvider;
  dynamic previousVendor;
  dynamic servicerType;
  String? leadStatus;
  String? createdBy;
  dynamic createdByName;
  int? nextApproveStaffId;
  dynamic nextTeamMappingId;
  String? leadCategory;
  dynamic heardAboutSubisuFrom;
  dynamic leadPartnerId;
  dynamic leadPartnerName;
  dynamic leadCustomerId;
  dynamic leadCustomerName;
  dynamic leadStaffId;
  dynamic leadStaffName;
  dynamic leadBranchId;
  dynamic leadBranchName;
  dynamic leadAgentId;
  dynamic leadAgentName;
  dynamic leadServiceAreaId;
  dynamic leadServiceAreaName;
  String? feasibility;
  dynamic feasibilityRemark;
  String? feasibilityRequired;
  dynamic rejectLeadTime;
  bool? leadReopenAllow;
  String? leadType;
  dynamic existingCustomerId;
  dynamic approveBuId;
  dynamic approveCurrentLoggedInStaffId;
  dynamic approveFirstname;
  dynamic flag;
  int? mvnoId;
  dynamic buId;
  dynamic approveMvnoId;
  dynamic approverNextLeadApprover;
  dynamic approveRemark;
  dynamic approveServiceareaid;
  dynamic approveStaffId;
  dynamic approveStatus;
  dynamic approveUsername;
  bool? finalApproved;
  dynamic planType;
  String? assigneeName;
  dynamic isCustomerCafeIsUpdated;
  dynamic customerId;
  String? leadNo;
  bool? presentCheckForPayment;
  bool? presentCheckForPermanent;
  dynamic leadSourcePojo;
  dynamic lastModifiedBy;
  dynamic rejectedOn;
  dynamic approvedOn;
  dynamic reOpenOn;
  dynamic approvedBy;
  dynamic rejectedBy;
  dynamic reOpenBy;
  dynamic leadCustomerCategory;
  String? leadCustomerType;
  dynamic leadCustomerSubType;
  String? leadCustomerSector;
  dynamic leadCustomerSubSector;
  dynamic valleyType;
  dynamic insideValley;
  dynamic outsideValley;
  dynamic competitorDuration;
  dynamic parentExperience;
  dynamic expiry;
  dynamic amount;
  dynamic feedback;
  String? gender;
  int? branchId;
  String? branchName;
  dynamic popManagementId;
  dynamic popManagementName;
  dynamic dateOfBirth;
  dynamic secondaryContactDetails;
  dynamic secondaryPhone;
  dynamic secondaryEmail;
  dynamic previousAmount;
  dynamic previousMonth;
  dynamic leadOriginType;
  String? requireServiceType;
  dynamic landlineNumber;
  dynamic leadFollowUpCount;
  dynamic pcontactphno;
  dynamic scontactname;
  dynamic businessverticals;
  dynamic subbusinessverticals;
  dynamic connectiontype;
  dynamic linktype;
  dynamic circuitarea;
  dynamic closuredate;
  dynamic circuitid;
  dynamic circuitname;
  dynamic leadvariety;
  dynamic billableCustomerId;
  dynamic discountType;
  dynamic discountExpiryDate;
  dynamic cafConvertedDate;
  dynamic cafConvertedStaffId;
  dynamic cafCovertedStaffName;
  dynamic locationlevel1;
  dynamic locationlevel2;
  dynamic locationlevel3;
  dynamic locationlevel4;
  dynamic skypeidImid;
  dynamic organisation;
  dynamic associatedLevel;
  dynamic nation;
  bool? isLeadQuickInv;
  dynamic leadIdentity;
  dynamic leadDepartment;
  dynamic designation;
  dynamic nextfollowupdate;
  dynamic nextfollowuptime;
  dynamic mvnoName;
  dynamic isLeadFromCWSC;
  bool? deleted;
  dynamic oldBNGRouterinterface;
  dynamic oldVSIName;
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
  dynamic oldWANIP;
  dynamic oldLLAccountid;

  LAAssignContent(
      {this.id,
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
        this.cstatus,
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
        this.lastPasswordChange,
        this.lastpasswordchangestring,
        this.custLeger,
        this.paymentDetails,
        this.flashMsg,
        this.mactelflag,
        this.mobile,
        this.countryCode,
        this.cafno,
        this.altmobile,
        this.altmobile1,
        this.altmobile2,
        this.altmobile3,
        this.altmobile4,
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
        this.createDateString,
        this.updateDateString,
        this.latitude,
        this.longitude,
        this.url,
        this.gisCode,
        this.salesremark,
        this.servicetype,
        this.isCustCaf,
        this.previousCafApprover,
        this.nextCafApprover,
        this.serviceareaName,
        this.cafApproveStatus,
        this.tinNo,
        this.passportNo,
        this.dunningCategory,
        this.plangroupid,
        this.parentCustomerId,
        this.parentCustomerName,
        this.invoiceType,
        this.calendarType,
        this.discount,
        this.leadSourceId,
        this.leadSubSourceId,
        this.rejectReasonId,
        this.rejectSubReasonId,
        this.rejectReasonName,
        this.rejectSubReasonName,
        this.leadSourceName,
        this.leadSubSourceName,
        this.reasonToChangeServiceProvider,
        this.previousVendor,
        this.servicerType,
        this.leadStatus,
        this.createdBy,
        this.createdByName,
        this.nextApproveStaffId,
        this.nextTeamMappingId,
        this.leadCategory,
        this.heardAboutSubisuFrom,
        this.leadPartnerId,
        this.leadPartnerName,
        this.leadCustomerId,
        this.leadCustomerName,
        this.leadStaffId,
        this.leadStaffName,
        this.leadBranchId,
        this.leadBranchName,
        this.leadAgentId,
        this.leadAgentName,
        this.leadServiceAreaId,
        this.leadServiceAreaName,
        this.feasibility,
        this.feasibilityRemark,
        this.feasibilityRequired,
        this.rejectLeadTime,
        this.leadReopenAllow,
        this.leadType,
        this.existingCustomerId,
        this.approveBuId,
        this.approveCurrentLoggedInStaffId,
        this.approveFirstname,
        this.flag,
        this.mvnoId,
        this.buId,
        this.approveMvnoId,
        this.approverNextLeadApprover,
        this.approveRemark,
        this.approveServiceareaid,
        this.approveStaffId,
        this.approveStatus,
        this.approveUsername,
        this.finalApproved,
        this.planType,
        this.assigneeName,
        this.isCustomerCafeIsUpdated,
        this.customerId,
        this.leadNo,
        this.presentCheckForPayment,
        this.presentCheckForPermanent,
        this.leadSourcePojo,
        this.lastModifiedBy,
        this.rejectedOn,
        this.approvedOn,
        this.reOpenOn,
        this.approvedBy,
        this.rejectedBy,
        this.reOpenBy,
        this.leadCustomerCategory,
        this.leadCustomerType,
        this.leadCustomerSubType,
        this.leadCustomerSector,
        this.leadCustomerSubSector,
        this.valleyType,
        this.insideValley,
        this.outsideValley,
        this.competitorDuration,
        this.parentExperience,
        this.expiry,
        this.amount,
        this.feedback,
        this.gender,
        this.branchId,
        this.branchName,
        this.popManagementId,
        this.popManagementName,
        this.dateOfBirth,
        this.secondaryContactDetails,
        this.secondaryPhone,
        this.secondaryEmail,
        this.previousAmount,
        this.previousMonth,
        this.leadOriginType,
        this.requireServiceType,
        this.landlineNumber,
        this.leadFollowUpCount,
        this.pcontactphno,
        this.scontactname,
        this.businessverticals,
        this.subbusinessverticals,
        this.connectiontype,
        this.linktype,
        this.circuitarea,
        this.closuredate,
        this.circuitid,
        this.circuitname,
        this.leadvariety,
        this.billableCustomerId,
        this.discountType,
        this.discountExpiryDate,
        this.cafConvertedDate,
        this.cafConvertedStaffId,
        this.cafCovertedStaffName,
        this.locationlevel1,
        this.locationlevel2,
        this.locationlevel3,
        this.locationlevel4,
        this.skypeidImid,
        this.organisation,
        this.associatedLevel,
        this.nation,
        this.isLeadQuickInv,
        this.leadIdentity,
        this.leadDepartment,
        this.designation,
        this.nextfollowupdate,
        this.nextfollowuptime,
        this.mvnoName,
        this.isLeadFromCWSC,
        this.deleted,
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
        this.oldLLAccountid});

  LAAssignContent.fromJson(Map<String, dynamic> json) {
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
    cstatus = json['cstatus'];
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
    lastPasswordChange = json['last_password_change'];
    lastpasswordchangestring = json['lastpasswordchangestring'];
    custLeger = json['custLeger'];
    paymentDetails = json['paymentDetails'] != null
        ? new PaymentDetails.fromJson(json['paymentDetails'])
        : null;
    flashMsg = json['flashMsg'];
    mactelflag = json['mactelflag'];
    mobile = json['mobile'];
    countryCode = json['countryCode'];
    cafno = json['cafno'];
    altmobile = json['altmobile'];
    altmobile1 = json['altmobile1'];
    altmobile2 = json['altmobile2'];
    altmobile3 = json['altmobile3'];
    altmobile4 = json['altmobile4'];
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
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    url = json['url'];
    gisCode = json['gisCode'];
    salesremark = json['salesremark'];
    servicetype = json['servicetype'];
    isCustCaf = json['isCustCaf'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    serviceareaName = json['serviceareaName'];
    cafApproveStatus = json['cafApproveStatus'];
    tinNo = json['tinNo'];
    passportNo = json['passportNo'];
    dunningCategory = json['dunningCategory'];
    plangroupid = json['plangroupid'];
    parentCustomerId = json['parentCustomerId'];
    parentCustomerName = json['parentCustomerName'];
    invoiceType = json['invoiceType'];
    calendarType = json['calendarType'];
    discount = json['discount'];
    leadSourceId = json['leadSourceId'];
    leadSubSourceId = json['leadSubSourceId'];
    rejectReasonId = json['rejectReasonId'];
    rejectSubReasonId = json['rejectSubReasonId'];
    rejectReasonName = json['rejectReasonName'];
    rejectSubReasonName = json['rejectSubReasonName'];
    leadSourceName = json['leadSourceName'];
    leadSubSourceName = json['leadSubSourceName'];
    reasonToChangeServiceProvider = json['reasonToChangeServiceProvider'];
    previousVendor = json['previousVendor'];
    servicerType = json['servicerType'];
    leadStatus = json['leadStatus'];
    createdBy = json['createdBy'];
    createdByName = json['createdByName'];
    nextApproveStaffId = json['nextApproveStaffId'];
    nextTeamMappingId = json['nextTeamMappingId'];
    leadCategory = json['leadCategory'];
    heardAboutSubisuFrom = json['heardAboutSubisuFrom'];
    leadPartnerId = json['leadPartnerId'];
    leadPartnerName = json['leadPartnerName'];
    leadCustomerId = json['leadCustomerId'];
    leadCustomerName = json['leadCustomerName'];
    leadStaffId = json['leadStaffId'];
    leadStaffName = json['leadStaffName'];
    leadBranchId = json['leadBranchId'];
    leadBranchName = json['leadBranchName'];
    leadAgentId = json['leadAgentId'];
    leadAgentName = json['leadAgentName'];
    leadServiceAreaId = json['leadServiceAreaId'];
    leadServiceAreaName = json['leadServiceAreaName'];
    feasibility = json['feasibility'];
    feasibilityRemark = json['feasibilityRemark'];
    feasibilityRequired = json['feasibilityRequired'];
    rejectLeadTime = json['rejectLeadTime'];
    leadReopenAllow = json['leadReopenAllow'];
    leadType = json['leadType'];
    existingCustomerId = json['existingCustomerId'];
    approveBuId = json['approveBuId'];
    approveCurrentLoggedInStaffId = json['approveCurrentLoggedInStaffId'];
    approveFirstname = json['approveFirstname'];
    flag = json['flag'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    approveMvnoId = json['approveMvnoId'];
    approverNextLeadApprover = json['approverNextLeadApprover'];
    approveRemark = json['approveRemark'];
    approveServiceareaid = json['approveServiceareaid'];
    approveStaffId = json['approveStaffId'];
    approveStatus = json['approveStatus'];
    approveUsername = json['approveUsername'];
    finalApproved = json['finalApproved'];
    planType = json['planType'];
    assigneeName = json['assigneeName'];
    isCustomerCafeIsUpdated = json['isCustomerCafeIsUpdated'];
    customerId = json['customerId'];
    leadNo = json['leadNo'];
    presentCheckForPayment = json['presentCheckForPayment'];
    presentCheckForPermanent = json['presentCheckForPermanent'];
    leadSourcePojo = json['leadSourcePojo'];
    lastModifiedBy = json['lastModifiedBy'];
    rejectedOn = json['rejectedOn'];
    approvedOn = json['approvedOn'];
    reOpenOn = json['reOpenOn'];
    approvedBy = json['approvedBy'];
    rejectedBy = json['rejectedBy'];
    reOpenBy = json['reOpenBy'];
    leadCustomerCategory = json['leadCustomerCategory'];
    leadCustomerType = json['leadCustomerType'];
    leadCustomerSubType = json['leadCustomerSubType'];
    leadCustomerSector = json['leadCustomerSector'];
    leadCustomerSubSector = json['leadCustomerSubSector'];
    valleyType = json['valleyType'];
    insideValley = json['insideValley'];
    outsideValley = json['outsideValley'];
    competitorDuration = json['competitorDuration'];
    parentExperience = json['parentExperience'];
    expiry = json['expiry'];
    amount = json['amount'];
    feedback = json['feedback'];
    gender = json['gender'];
    branchId = json['branchId'];
    branchName = json['branchName'];
    popManagementId = json['popManagementId'];
    popManagementName = json['popManagementName'];
    dateOfBirth = json['dateOfBirth'];
    secondaryContactDetails = json['secondaryContactDetails'];
    secondaryPhone = json['secondaryPhone'];
    secondaryEmail = json['secondaryEmail'];
    previousAmount = json['previousAmount'];
    previousMonth = json['previousMonth'];
    leadOriginType = json['leadOriginType'];
    requireServiceType = json['requireServiceType'];
    landlineNumber = json['landlineNumber'];
    leadFollowUpCount = json['leadFollowUpCount'];
    pcontactphno = json['pcontactphno'];
    scontactname = json['scontactname'];
    businessverticals = json['businessverticals'];
    subbusinessverticals = json['subbusinessverticals'];
    connectiontype = json['connectiontype'];
    linktype = json['linktype'];
    circuitarea = json['circuitarea'];
    closuredate = json['closuredate'];
    circuitid = json['circuitid'];
    circuitname = json['circuitname'];
    leadvariety = json['leadvariety'];
    billableCustomerId = json['billableCustomerId'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    cafConvertedDate = json['cafConvertedDate'];
    cafConvertedStaffId = json['cafConvertedStaffId'];
    cafCovertedStaffName = json['cafCovertedStaffName'];
    locationlevel1 = json['locationlevel1'];
    locationlevel2 = json['locationlevel2'];
    locationlevel3 = json['locationlevel3'];
    locationlevel4 = json['locationlevel4'];
    skypeidImid = json['skypeid_imid'];
    organisation = json['organisation'];
    associatedLevel = json['associatedLevel'];
    nation = json['nation'];
    isLeadQuickInv = json['isLeadQuickInv'];
    leadIdentity = json['leadIdentity'];
    leadDepartment = json['leadDepartment'];
    designation = json['designation'];
    nextfollowupdate = json['nextfollowupdate'];
    nextfollowuptime = json['nextfollowuptime'];
    mvnoName = json['mvnoName'];
    isLeadFromCWSC = json['isLeadFromCWSC'];
    deleted = json['deleted'];
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
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['cstatus'] = this.cstatus;
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
    data['last_password_change'] = this.lastPasswordChange;
    data['lastpasswordchangestring'] = this.lastpasswordchangestring;
    data['custLeger'] = this.custLeger;
    if (this.paymentDetails != null) {
      data['paymentDetails'] = this.paymentDetails!.toJson();
    }
    data['flashMsg'] = this.flashMsg;
    data['mactelflag'] = this.mactelflag;
    data['mobile'] = this.mobile;
    data['countryCode'] = this.countryCode;
    data['cafno'] = this.cafno;
    data['altmobile'] = this.altmobile;
    data['altmobile1'] = this.altmobile1;
    data['altmobile2'] = this.altmobile2;
    data['altmobile3'] = this.altmobile3;
    data['altmobile4'] = this.altmobile4;
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
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['url'] = this.url;
    data['gisCode'] = this.gisCode;
    data['salesremark'] = this.salesremark;
    data['servicetype'] = this.servicetype;
    data['isCustCaf'] = this.isCustCaf;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['serviceareaName'] = this.serviceareaName;
    data['cafApproveStatus'] = this.cafApproveStatus;
    data['tinNo'] = this.tinNo;
    data['passportNo'] = this.passportNo;
    data['dunningCategory'] = this.dunningCategory;
    data['plangroupid'] = this.plangroupid;
    data['parentCustomerId'] = this.parentCustomerId;
    data['parentCustomerName'] = this.parentCustomerName;
    data['invoiceType'] = this.invoiceType;
    data['calendarType'] = this.calendarType;
    data['discount'] = this.discount;
    data['leadSourceId'] = this.leadSourceId;
    data['leadSubSourceId'] = this.leadSubSourceId;
    data['rejectReasonId'] = this.rejectReasonId;
    data['rejectSubReasonId'] = this.rejectSubReasonId;
    data['rejectReasonName'] = this.rejectReasonName;
    data['rejectSubReasonName'] = this.rejectSubReasonName;
    data['leadSourceName'] = this.leadSourceName;
    data['leadSubSourceName'] = this.leadSubSourceName;
    data['reasonToChangeServiceProvider'] = this.reasonToChangeServiceProvider;
    data['previousVendor'] = this.previousVendor;
    data['servicerType'] = this.servicerType;
    data['leadStatus'] = this.leadStatus;
    data['createdBy'] = this.createdBy;
    data['createdByName'] = this.createdByName;
    data['nextApproveStaffId'] = this.nextApproveStaffId;
    data['nextTeamMappingId'] = this.nextTeamMappingId;
    data['leadCategory'] = this.leadCategory;
    data['heardAboutSubisuFrom'] = this.heardAboutSubisuFrom;
    data['leadPartnerId'] = this.leadPartnerId;
    data['leadPartnerName'] = this.leadPartnerName;
    data['leadCustomerId'] = this.leadCustomerId;
    data['leadCustomerName'] = this.leadCustomerName;
    data['leadStaffId'] = this.leadStaffId;
    data['leadStaffName'] = this.leadStaffName;
    data['leadBranchId'] = this.leadBranchId;
    data['leadBranchName'] = this.leadBranchName;
    data['leadAgentId'] = this.leadAgentId;
    data['leadAgentName'] = this.leadAgentName;
    data['leadServiceAreaId'] = this.leadServiceAreaId;
    data['leadServiceAreaName'] = this.leadServiceAreaName;
    data['feasibility'] = this.feasibility;
    data['feasibilityRemark'] = this.feasibilityRemark;
    data['feasibilityRequired'] = this.feasibilityRequired;
    data['rejectLeadTime'] = this.rejectLeadTime;
    data['leadReopenAllow'] = this.leadReopenAllow;
    data['leadType'] = this.leadType;
    data['existingCustomerId'] = this.existingCustomerId;
    data['approveBuId'] = this.approveBuId;
    data['approveCurrentLoggedInStaffId'] = this.approveCurrentLoggedInStaffId;
    data['approveFirstname'] = this.approveFirstname;
    data['flag'] = this.flag;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['approveMvnoId'] = this.approveMvnoId;
    data['approverNextLeadApprover'] = this.approverNextLeadApprover;
    data['approveRemark'] = this.approveRemark;
    data['approveServiceareaid'] = this.approveServiceareaid;
    data['approveStaffId'] = this.approveStaffId;
    data['approveStatus'] = this.approveStatus;
    data['approveUsername'] = this.approveUsername;
    data['finalApproved'] = this.finalApproved;
    data['planType'] = this.planType;
    data['assigneeName'] = this.assigneeName;
    data['isCustomerCafeIsUpdated'] = this.isCustomerCafeIsUpdated;
    data['customerId'] = this.customerId;
    data['leadNo'] = this.leadNo;
    data['presentCheckForPayment'] = this.presentCheckForPayment;
    data['presentCheckForPermanent'] = this.presentCheckForPermanent;
    data['leadSourcePojo'] = this.leadSourcePojo;
    data['lastModifiedBy'] = this.lastModifiedBy;
    data['rejectedOn'] = this.rejectedOn;
    data['approvedOn'] = this.approvedOn;
    data['reOpenOn'] = this.reOpenOn;
    data['approvedBy'] = this.approvedBy;
    data['rejectedBy'] = this.rejectedBy;
    data['reOpenBy'] = this.reOpenBy;
    data['leadCustomerCategory'] = this.leadCustomerCategory;
    data['leadCustomerType'] = this.leadCustomerType;
    data['leadCustomerSubType'] = this.leadCustomerSubType;
    data['leadCustomerSector'] = this.leadCustomerSector;
    data['leadCustomerSubSector'] = this.leadCustomerSubSector;
    data['valleyType'] = this.valleyType;
    data['insideValley'] = this.insideValley;
    data['outsideValley'] = this.outsideValley;
    data['competitorDuration'] = this.competitorDuration;
    data['parentExperience'] = this.parentExperience;
    data['expiry'] = this.expiry;
    data['amount'] = this.amount;
    data['feedback'] = this.feedback;
    data['gender'] = this.gender;
    data['branchId'] = this.branchId;
    data['branchName'] = this.branchName;
    data['popManagementId'] = this.popManagementId;
    data['popManagementName'] = this.popManagementName;
    data['dateOfBirth'] = this.dateOfBirth;
    data['secondaryContactDetails'] = this.secondaryContactDetails;
    data['secondaryPhone'] = this.secondaryPhone;
    data['secondaryEmail'] = this.secondaryEmail;
    data['previousAmount'] = this.previousAmount;
    data['previousMonth'] = this.previousMonth;
    data['leadOriginType'] = this.leadOriginType;
    data['requireServiceType'] = this.requireServiceType;
    data['landlineNumber'] = this.landlineNumber;
    data['leadFollowUpCount'] = this.leadFollowUpCount;
    data['pcontactphno'] = this.pcontactphno;
    data['scontactname'] = this.scontactname;
    data['businessverticals'] = this.businessverticals;
    data['subbusinessverticals'] = this.subbusinessverticals;
    data['connectiontype'] = this.connectiontype;
    data['linktype'] = this.linktype;
    data['circuitarea'] = this.circuitarea;
    data['closuredate'] = this.closuredate;
    data['circuitid'] = this.circuitid;
    data['circuitname'] = this.circuitname;
    data['leadvariety'] = this.leadvariety;
    data['billableCustomerId'] = this.billableCustomerId;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['cafConvertedDate'] = this.cafConvertedDate;
    data['cafConvertedStaffId'] = this.cafConvertedStaffId;
    data['cafCovertedStaffName'] = this.cafCovertedStaffName;
    data['locationlevel1'] = this.locationlevel1;
    data['locationlevel2'] = this.locationlevel2;
    data['locationlevel3'] = this.locationlevel3;
    data['locationlevel4'] = this.locationlevel4;
    data['skypeid_imid'] = this.skypeidImid;
    data['organisation'] = this.organisation;
    data['associatedLevel'] = this.associatedLevel;
    data['nation'] = this.nation;
    data['isLeadQuickInv'] = this.isLeadQuickInv;
    data['leadIdentity'] = this.leadIdentity;
    data['leadDepartment'] = this.leadDepartment;
    data['designation'] = this.designation;
    data['nextfollowupdate'] = this.nextfollowupdate;
    data['nextfollowuptime'] = this.nextfollowuptime;
    data['mvnoName'] = this.mvnoName;
    data['isLeadFromCWSC'] = this.isLeadFromCWSC;
    data['deleted'] = this.deleted;
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
    return data;
  }
}

class PaymentDetails {
  int? id;
  dynamic referenceno;
  dynamic chequedate;
  dynamic paymentdate;
  dynamic chequeno;
  dynamic bank;
  dynamic customerid;
  dynamic paymode;
  double? amount;
  dynamic paymentreferenceno;
  dynamic remark;
  dynamic branch;
  dynamic invoiceId;
  dynamic type;
  dynamic paytype;
  dynamic mvnoId;
  dynamic buId;
  dynamic bankManagement;

  PaymentDetails(
      {this.id,
        this.referenceno,
        this.chequedate,
        this.paymentdate,
        this.chequeno,
        this.bank,
        this.customerid,
        this.paymode,
        this.amount,
        this.paymentreferenceno,
        this.remark,
        this.branch,
        this.invoiceId,
        this.type,
        this.paytype,
        this.mvnoId,
        this.buId,
        this.bankManagement});

  PaymentDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    referenceno = json['referenceno'];
    chequedate = json['chequedate'];
    paymentdate = json['paymentdate'];
    chequeno = json['chequeno'];
    bank = json['bank'];
    customerid = json['customerid'];
    paymode = json['paymode'];
    amount = json['amount'];
    paymentreferenceno = json['paymentreferenceno'];
    remark = json['remark'];
    branch = json['branch'];
    invoiceId = json['invoiceId'];
    type = json['type'];
    paytype = json['paytype'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    bankManagement = json['bankManagement'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['referenceno'] = this.referenceno;
    data['chequedate'] = this.chequedate;
    data['paymentdate'] = this.paymentdate;
    data['chequeno'] = this.chequeno;
    data['bank'] = this.bank;
    data['customerid'] = this.customerid;
    data['paymode'] = this.paymode;
    data['amount'] = this.amount;
    data['paymentreferenceno'] = this.paymentreferenceno;
    data['remark'] = this.remark;
    data['branch'] = this.branch;
    data['invoiceId'] = this.invoiceId;
    data['type'] = this.type;
    data['paytype'] = this.paytype;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['bankManagement'] = this.bankManagement;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? pageSize;
  int? pageNumber;
  int? offset;
  bool? paged;
  bool? unpaged;

  Pageable(
      {this.sort,
        this.pageSize,
        this.pageNumber,
        this.offset,
        this.paged,
        this.unpaged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    pageSize = json['pageSize'];
    pageNumber = json['pageNumber'];
    offset = json['offset'];
    paged = json['paged'];
    unpaged = json['unpaged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['pageSize'] = this.pageSize;
    data['pageNumber'] = this.pageNumber;
    data['offset'] = this.offset;
    data['paged'] = this.paged;
    data['unpaged'] = this.unpaged;
    return data;
  }
}

class Sort {
  bool? unsorted;
  bool? sorted;
  bool? empty;

  Sort({this.unsorted, this.sorted, this.empty});

  Sort.fromJson(Map<String, dynamic> json) {
    unsorted = json['unsorted'];
    sorted = json['sorted'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['unsorted'] = this.unsorted;
    data['sorted'] = this.sorted;
    data['empty'] = this.empty;
    return data;
  }
}
