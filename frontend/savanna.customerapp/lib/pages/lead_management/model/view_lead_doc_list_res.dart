import 'package:savbill/webservices/base_response.dart';

class ViewLeadDocListRes  extends BaseResponse{
  CustmerDocList? custmerDocList;
  String? timestamp;
  int? status;

  ViewLeadDocListRes({this.custmerDocList, this.timestamp, this.status});

  ViewLeadDocListRes.fromJson(Map<String, dynamic> json) {
    custmerDocList = json['custmerDocList'] != null
        ? new CustmerDocList.fromJson(json['custmerDocList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.custmerDocList != null) {
      data['custmerDocList'] = this.custmerDocList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustmerDocList {
  List<LeadDocContent>? content;
  Pageable? pageable;
  int? totalPages;
  int? totalElements;
  bool? last;
  bool? first;
  Sort? sort;
  int? size;
  int? number;
  int? numberOfElements;
  bool? empty;

  CustmerDocList(
      {this.content,
        this.pageable,
        this.totalPages,
        this.totalElements,
        this.last,
        this.first,
        this.sort,
        this.size,
        this.number,
        this.numberOfElements,
        this.empty});

  CustmerDocList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <LeadDocContent>[];
      json['content'].forEach((v) {
        content!.add(new LeadDocContent.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    last = json['last'];
    first = json['first'];
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    size = json['size'];
    number = json['number'];
    numberOfElements = json['numberOfElements'];
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
    data['totalPages'] = this.totalPages;
    data['totalElements'] = this.totalElements;
    data['last'] = this.last;
    data['first'] = this.first;
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['size'] = this.size;
    data['number'] = this.number;
    data['numberOfElements'] = this.numberOfElements;
    data['empty'] = this.empty;
    return data;
  }
}

class LeadDocContent {
  int? docId;
  LeadMaster? leadMaster;
  String? docType;
  String? docSubType;
  String? mode;
  String? remark;
  String? docStatus;
  dynamic filename;
  dynamic uniquename;
  bool? isDelete;
  String? startDate;
  String? endDate;
  String? documentNumber;
  dynamic staffId;

  LeadDocContent(
      {this.docId,
        this.leadMaster,
        this.docType,
        this.docSubType,
        this.mode,
        this.remark,
        this.docStatus,
        this.filename,
        this.uniquename,
        this.isDelete,
        this.startDate,
        this.endDate,
        this.documentNumber,
        this.staffId});

  LeadDocContent.fromJson(Map<String, dynamic> json) {
    docId = json['docId'];
    leadMaster = json['leadMaster'] != null
        ? new LeadMaster.fromJson(json['leadMaster'])
        : null;
    docType = json['docType'];
    docSubType = json['docSubType'];
    mode = json['mode'];
    remark = json['remark'];
    docStatus = json['docStatus'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    isDelete = json['isDelete'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    documentNumber = json['documentNumber'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['docId'] = this.docId;
    if (this.leadMaster != null) {
      data['leadMaster'] = this.leadMaster!.toJson();
    }
    data['docType'] = this.docType;
    data['docSubType'] = this.docSubType;
    data['mode'] = this.mode;
    data['remark'] = this.remark;
    data['docStatus'] = this.docStatus;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['isDelete'] = this.isDelete;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['documentNumber'] = this.documentNumber;
    data['staffId'] = this.staffId;
    return data;
  }
}

class LeadMaster {
  int? id;
  dynamic username;
  dynamic password;
  String? firstname;
  String? lastname;
  String? email;
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
  dynamic radiusprofileIds;
  dynamic flashMsg;
  bool? mactelflag;
  String? mobile;
  String? countryCode;
  dynamic cafno;
  dynamic altmobile;
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
  int? mvnoId;
  dynamic tinNo;
  String? passportNo;
  String? dunningCategory;
  dynamic plangroupid;
  dynamic parentCustomerId;
  dynamic parentCustomerName;
  dynamic invoiceType;
  String? calendarType;
  dynamic discount;
  dynamic buId;
  dynamic reasonToChangeServiceProvider;
  dynamic previousVendor;
  dynamic servicerType;
  String? leadStatus;
  String? createdOn;
  String? lastModifiedOn;
  String? createdBy;
  String? createdByName;
  dynamic lastModifiedBy;
  dynamic rejectedOn;
  dynamic rejectedBy;
  dynamic approvedOn;
  dynamic approvedBy;
  dynamic reOpenOn;
  dynamic reOpenBy;
  dynamic altmobile1;
  dynamic altmobile2;
  dynamic altmobile3;
  dynamic altmobile4;
  int? nextApproveStaffId;
  dynamic nextTeamMappingId;
  String? leadCategory;
  dynamic heardAboutSubisuFrom;
  dynamic leadAgentId;
  String? feasibility;
  String? feasibilityRemark;
  String? feasibilityRequired;
  dynamic rejectLeadTime;
  dynamic leadType;
  dynamic existingCustomerId;
  bool? noLeadFollowupSendNotification;
  bool? finalApproved;
  dynamic planType;
  String? leadNo;
  bool? presentCheckForPayment;
  bool? presentCheckForPermanent;
  dynamic leadCustomerCategory;
  String? leadCustomerType;
  dynamic leadCustomerSubType;
  String? leadCustomerSector;
  dynamic leadCustomerSubSector;
  dynamic valleyType;
  dynamic insideValley;
  dynamic outsideValley;
  dynamic competitorDuration;
  dynamic expiry;
  dynamic amount;
  dynamic feedback;
  String? gender;
  dynamic dateOfBirth;
  dynamic secondaryContactDetails;
  dynamic secondaryPhone;
  dynamic secondaryEmail;
  dynamic previousAmount;
  dynamic previousMonth;
  dynamic leadOriginType;
  dynamic requireServiceType;
  dynamic landlineNumber;
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
  dynamic parentExperience;
  dynamic locationlevel1;
  dynamic locationlevel2;
  dynamic locationlevel3;
  dynamic locationlevel4;
  dynamic skypeidImid;
  dynamic associatedLevel;
  dynamic organisation;
  dynamic nation;
  int? isLeadQuickInv;
  dynamic leadIdentity;
  dynamic leadDepartment;
  dynamic designation;
  dynamic nextfollowupdate;
  dynamic nextfollowuptime;
  dynamic isLeadFromCWSC;
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
  bool? deleted;

  LeadMaster(
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
        this.radiusprofileIds,
        this.flashMsg,
        this.mactelflag,
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
        this.mvnoId,
        this.tinNo,
        this.passportNo,
        this.dunningCategory,
        this.plangroupid,
        this.parentCustomerId,
        this.parentCustomerName,
        this.invoiceType,
        this.calendarType,
        this.discount,
        this.buId,
        this.reasonToChangeServiceProvider,
        this.previousVendor,
        this.servicerType,
        this.leadStatus,
        this.createdOn,
        this.lastModifiedOn,
        this.createdBy,
        this.createdByName,
        this.lastModifiedBy,
        this.rejectedOn,
        this.rejectedBy,
        this.approvedOn,
        this.approvedBy,
        this.reOpenOn,
        this.reOpenBy,
        this.altmobile1,
        this.altmobile2,
        this.altmobile3,
        this.altmobile4,
        this.nextApproveStaffId,
        this.nextTeamMappingId,
        this.leadCategory,
        this.heardAboutSubisuFrom,
        this.leadAgentId,
        this.feasibility,
        this.feasibilityRemark,
        this.feasibilityRequired,
        this.rejectLeadTime,
        this.leadType,
        this.existingCustomerId,
        this.noLeadFollowupSendNotification,
        this.finalApproved,
        this.planType,
        this.leadNo,
        this.presentCheckForPayment,
        this.presentCheckForPermanent,
        this.leadCustomerCategory,
        this.leadCustomerType,
        this.leadCustomerSubType,
        this.leadCustomerSector,
        this.leadCustomerSubSector,
        this.valleyType,
        this.insideValley,
        this.outsideValley,
        this.competitorDuration,
        this.expiry,
        this.amount,
        this.feedback,
        this.gender,
        this.dateOfBirth,
        this.secondaryContactDetails,
        this.secondaryPhone,
        this.secondaryEmail,
        this.previousAmount,
        this.previousMonth,
        this.leadOriginType,
        this.requireServiceType,
        this.landlineNumber,
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
        this.parentExperience,
        this.locationlevel1,
        this.locationlevel2,
        this.locationlevel3,
        this.locationlevel4,
        this.skypeidImid,
        this.associatedLevel,
        this.organisation,
        this.nation,
        this.isLeadQuickInv,
        this.leadIdentity,
        this.leadDepartment,
        this.designation,
        this.nextfollowupdate,
        this.nextfollowuptime,
        this.isLeadFromCWSC,
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
        this.deleted});

  LeadMaster.fromJson(Map<String, dynamic> json) {
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
    radiusprofileIds = json['radiusprofileIds'];
    flashMsg = json['flashMsg'];
    mactelflag = json['mactelflag'];
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
    mvnoId = json['mvnoId'];
    tinNo = json['tinNo'];
    passportNo = json['passportNo'];
    dunningCategory = json['dunningCategory'];
    plangroupid = json['plangroupid'];
    parentCustomerId = json['parentCustomerId'];
    parentCustomerName = json['parentCustomerName'];
    invoiceType = json['invoiceType'];
    calendarType = json['calendarType'];
    discount = json['discount'];
    buId = json['buId'];
    reasonToChangeServiceProvider = json['reasonToChangeServiceProvider'];
    previousVendor = json['previousVendor'];
    servicerType = json['servicerType'];
    leadStatus = json['leadStatus'];
    createdOn = json['createdOn'];
    lastModifiedOn = json['lastModifiedOn'];
    createdBy = json['createdBy'];
    createdByName = json['createdByName'];
    lastModifiedBy = json['lastModifiedBy'];
    rejectedOn = json['rejectedOn'];
    rejectedBy = json['rejectedBy'];
    approvedOn = json['approvedOn'];
    approvedBy = json['approvedBy'];
    reOpenOn = json['reOpenOn'];
    reOpenBy = json['reOpenBy'];
    altmobile1 = json['altmobile1'];
    altmobile2 = json['altmobile2'];
    altmobile3 = json['altmobile3'];
    altmobile4 = json['altmobile4'];
    nextApproveStaffId = json['nextApproveStaffId'];
    nextTeamMappingId = json['nextTeamMappingId'];
    leadCategory = json['leadCategory'];
    heardAboutSubisuFrom = json['heardAboutSubisuFrom'];
    leadAgentId = json['leadAgentId'];
    feasibility = json['feasibility'];
    feasibilityRemark = json['feasibilityRemark'];
    feasibilityRequired = json['feasibilityRequired'];
    rejectLeadTime = json['rejectLeadTime'];
    leadType = json['leadType'];
    existingCustomerId = json['existingCustomerId'];
    noLeadFollowupSendNotification = json['noLeadFollowupSendNotification'];
    finalApproved = json['finalApproved'];
    planType = json['planType'];
    leadNo = json['leadNo'];
    presentCheckForPayment = json['presentCheckForPayment'];
    presentCheckForPermanent = json['presentCheckForPermanent'];
    leadCustomerCategory = json['leadCustomerCategory'];
    leadCustomerType = json['leadCustomerType'];
    leadCustomerSubType = json['leadCustomerSubType'];
    leadCustomerSector = json['leadCustomerSector'];
    leadCustomerSubSector = json['leadCustomerSubSector'];
    valleyType = json['valleyType'];
    insideValley = json['insideValley'];
    outsideValley = json['outsideValley'];
    competitorDuration = json['competitorDuration'];
    expiry = json['expiry'];
    amount = json['amount'];
    feedback = json['feedback'];
    gender = json['gender'];
    dateOfBirth = json['dateOfBirth'];
    secondaryContactDetails = json['secondaryContactDetails'];
    secondaryPhone = json['secondaryPhone'];
    secondaryEmail = json['secondaryEmail'];
    previousAmount = json['previousAmount'];
    previousMonth = json['previousMonth'];
    leadOriginType = json['leadOriginType'];
    requireServiceType = json['requireServiceType'];
    landlineNumber = json['landlineNumber'];
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
    parentExperience = json['parentExperience'];
    locationlevel1 = json['locationlevel1'];
    locationlevel2 = json['locationlevel2'];
    locationlevel3 = json['locationlevel3'];
    locationlevel4 = json['locationlevel4'];
    skypeidImid = json['skypeid_imid'];
    associatedLevel = json['associatedLevel'];
    organisation = json['organisation'];
    nation = json['nation'];
    isLeadQuickInv = json['isLeadQuickInv'];
    leadIdentity = json['leadIdentity'];
    leadDepartment = json['leadDepartment'];
    designation = json['designation'];
    nextfollowupdate = json['nextfollowupdate'];
    nextfollowuptime = json['nextfollowuptime'];
    isLeadFromCWSC = json['isLeadFromCWSC'];
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
    deleted = json['deleted'];
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
    data['radiusprofileIds'] = this.radiusprofileIds;
    data['flashMsg'] = this.flashMsg;
    data['mactelflag'] = this.mactelflag;
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
    data['mvnoId'] = this.mvnoId;
    data['tinNo'] = this.tinNo;
    data['passportNo'] = this.passportNo;
    data['dunningCategory'] = this.dunningCategory;
    data['plangroupid'] = this.plangroupid;
    data['parentCustomerId'] = this.parentCustomerId;
    data['parentCustomerName'] = this.parentCustomerName;
    data['invoiceType'] = this.invoiceType;
    data['calendarType'] = this.calendarType;
    data['discount'] = this.discount;
    data['buId'] = this.buId;
    data['reasonToChangeServiceProvider'] = this.reasonToChangeServiceProvider;
    data['previousVendor'] = this.previousVendor;
    data['servicerType'] = this.servicerType;
    data['leadStatus'] = this.leadStatus;
    data['createdOn'] = this.createdOn;
    data['lastModifiedOn'] = this.lastModifiedOn;
    data['createdBy'] = this.createdBy;
    data['createdByName'] = this.createdByName;
    data['lastModifiedBy'] = this.lastModifiedBy;
    data['rejectedOn'] = this.rejectedOn;
    data['rejectedBy'] = this.rejectedBy;
    data['approvedOn'] = this.approvedOn;
    data['approvedBy'] = this.approvedBy;
    data['reOpenOn'] = this.reOpenOn;
    data['reOpenBy'] = this.reOpenBy;
    data['altmobile1'] = this.altmobile1;
    data['altmobile2'] = this.altmobile2;
    data['altmobile3'] = this.altmobile3;
    data['altmobile4'] = this.altmobile4;
    data['nextApproveStaffId'] = this.nextApproveStaffId;
    data['nextTeamMappingId'] = this.nextTeamMappingId;
    data['leadCategory'] = this.leadCategory;
    data['heardAboutSubisuFrom'] = this.heardAboutSubisuFrom;
    data['leadAgentId'] = this.leadAgentId;
    data['feasibility'] = this.feasibility;
    data['feasibilityRemark'] = this.feasibilityRemark;
    data['feasibilityRequired'] = this.feasibilityRequired;
    data['rejectLeadTime'] = this.rejectLeadTime;
    data['leadType'] = this.leadType;
    data['existingCustomerId'] = this.existingCustomerId;
    data['noLeadFollowupSendNotification'] =
        this.noLeadFollowupSendNotification;
    data['finalApproved'] = this.finalApproved;
    data['planType'] = this.planType;
    data['leadNo'] = this.leadNo;
    data['presentCheckForPayment'] = this.presentCheckForPayment;
    data['presentCheckForPermanent'] = this.presentCheckForPermanent;
    data['leadCustomerCategory'] = this.leadCustomerCategory;
    data['leadCustomerType'] = this.leadCustomerType;
    data['leadCustomerSubType'] = this.leadCustomerSubType;
    data['leadCustomerSector'] = this.leadCustomerSector;
    data['leadCustomerSubSector'] = this.leadCustomerSubSector;
    data['valleyType'] = this.valleyType;
    data['insideValley'] = this.insideValley;
    data['outsideValley'] = this.outsideValley;
    data['competitorDuration'] = this.competitorDuration;
    data['expiry'] = this.expiry;
    data['amount'] = this.amount;
    data['feedback'] = this.feedback;
    data['gender'] = this.gender;
    data['dateOfBirth'] = this.dateOfBirth;
    data['secondaryContactDetails'] = this.secondaryContactDetails;
    data['secondaryPhone'] = this.secondaryPhone;
    data['secondaryEmail'] = this.secondaryEmail;
    data['previousAmount'] = this.previousAmount;
    data['previousMonth'] = this.previousMonth;
    data['leadOriginType'] = this.leadOriginType;
    data['requireServiceType'] = this.requireServiceType;
    data['landlineNumber'] = this.landlineNumber;
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
    data['parentExperience'] = this.parentExperience;
    data['locationlevel1'] = this.locationlevel1;
    data['locationlevel2'] = this.locationlevel2;
    data['locationlevel3'] = this.locationlevel3;
    data['locationlevel4'] = this.locationlevel4;
    data['skypeid_imid'] = this.skypeidImid;
    data['associatedLevel'] = this.associatedLevel;
    data['organisation'] = this.organisation;
    data['nation'] = this.nation;
    data['isLeadQuickInv'] = this.isLeadQuickInv;
    data['leadIdentity'] = this.leadIdentity;
    data['leadDepartment'] = this.leadDepartment;
    data['designation'] = this.designation;
    data['nextfollowupdate'] = this.nextfollowupdate;
    data['nextfollowuptime'] = this.nextfollowuptime;
    data['isLeadFromCWSC'] = this.isLeadFromCWSC;
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
    data['deleted'] = this.deleted;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? offset;
  int? pageNumber;
  int? pageSize;
  bool? unpaged;
  bool? paged;

  Pageable(
      {this.sort,
        this.offset,
        this.pageNumber,
        this.pageSize,
        this.unpaged,
        this.paged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    offset = json['offset'];
    pageNumber = json['pageNumber'];
    pageSize = json['pageSize'];
    unpaged = json['unpaged'];
    paged = json['paged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['offset'] = this.offset;
    data['pageNumber'] = this.pageNumber;
    data['pageSize'] = this.pageSize;
    data['unpaged'] = this.unpaged;
    data['paged'] = this.paged;
    return data;
  }
}

class Sort {
  bool? sorted;
  bool? unsorted;
  bool? empty;

  Sort({this.sorted, this.unsorted, this.empty});

  Sort.fromJson(Map<String, dynamic> json) {
    sorted = json['sorted'];
    unsorted = json['unsorted'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['sorted'] = this.sorted;
    data['unsorted'] = this.unsorted;
    data['empty'] = this.empty;
    return data;
  }
}
