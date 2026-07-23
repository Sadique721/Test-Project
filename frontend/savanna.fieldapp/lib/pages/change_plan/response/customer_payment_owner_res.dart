import 'package:savbill/webservices/base_response.dart';

class CustomerPaymentOwnerRes extends BaseResponse {
  PageDetails? pageDetails;
  List<StaffUserlist>? staffUserlist;
  String? timestamp;
  int? status;

  CustomerPaymentOwnerRes(
      {this.pageDetails, this.staffUserlist, this.timestamp, this.status});

  CustomerPaymentOwnerRes.fromJson(Map<String, dynamic> json) {
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['staffUserlist'] != null) {
      staffUserlist = <StaffUserlist>[];
      json['staffUserlist'].forEach((v) {
        staffUserlist!.add(new StaffUserlist.fromJson(v));
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
    if (this.staffUserlist != null) {
      data['staffUserlist'] =
          this.staffUserlist!.map((v) => v.toJson()).toList();
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

class StaffUserlist {
  int? id;
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? username;
  dynamic password;
  String? firstname;
  String? lastname;
  String? email;
  String? phone;
  String? countryCode;
  int? failcount;
  String? status;
  dynamic lastLoginTime;
  int? partnerid;
  dynamic newpassword;
  List<int>? roleIds;
  List<int>? teamIds;
  List<String>? teamNameList;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic tacacsAccessLevelGroup;
  dynamic serviceAreaId;
  List<int>? serviceAreasId;
  dynamic businessunitid;
  int? parentStaffId;
  int? mvnoId;
  List<int>? serviceAreaIdsList;
  
  List<String>? serviceAreasNameList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  String? branchName;
  List<String>? roleName;
  String? regDate;
  String? partnerName;
  dynamic updatedatestring;
  dynamic branchId;
  String? parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  int? displayId;
  String? displayName;
  dynamic department;
  int? identityKey;

  StaffUserlist(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.username,
        this.password,
        this.firstname,
        this.lastname,
        this.email,
        this.phone,
        this.countryCode,
        this.failcount,
        this.status,
        this.lastLoginTime,
        this.partnerid,
        this.newpassword,
        this.roleIds,
        this.teamIds,
        this.teamNameList,
        this.isDelete,
        this.fullName,
        this.sysstaff,
        this.servicearea,
        this.businessUnit,
        this.tacacsAccessLevelGroup,
        this.serviceAreaId,
        this.serviceAreasId,
        this.businessunitid,
        this.parentStaffId,
        this.mvnoId,
        this.serviceAreaIdsList,
        this.serviceAreasNameList,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.branchName,
        this.roleName,
        this.regDate,
        this.partnerName,
        this.updatedatestring,
        this.branchId,
        this.parentstaffname,
        this.hrmsId,
        this.profileImage,
        this.displayId,
        this.displayName,
        this.department,
        this.identityKey});

  StaffUserlist.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    username = json['username'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    email = json['email'];
    phone = json['phone'];
    countryCode = json['countryCode'];
    failcount = json['failcount'];
    status = json['status'];
    lastLoginTime = json['last_login_time'];
    partnerid = json['partnerid'];
    newpassword = json['newpassword'];
    roleIds = json['roleIds'].cast<int>();
    teamIds = json['teamIds'].cast<int>();
    teamNameList = json['teamNameList'].cast<String>();
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    tacacsAccessLevelGroup = json['tacacsAccessLevelGroup'];
    serviceAreaId = json['serviceAreaId'];
    serviceAreasId = json['serviceAreasId'].cast<int>();
    businessunitid = json['businessunitid'];
    
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    
    serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    
    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    branchName = json['branchName'];
    
    roleName = json['roleName'].cast<String>();
    regDate = json['regDate'];
    partnerName = json['partnerName'];
    updatedatestring = json['updatedatestring'];
    branchId = json['branchId'];
    parentstaffname = json['parentstaffname'];
    hrmsId = json['hrmsId'];
    profileImage = json['profileImage'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    department = json['department'];
    identityKey = json['identityKey'];
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
    data['username'] = this.username;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['email'] = this.email;
    data['phone'] = this.phone;
    data['countryCode'] = this.countryCode;
    data['failcount'] = this.failcount;
    data['status'] = this.status;
    data['last_login_time'] = this.lastLoginTime;
    data['partnerid'] = this.partnerid;
    data['newpassword'] = this.newpassword;
    data['roleIds'] = this.roleIds;
    data['teamIds'] = this.teamIds;
    data['teamNameList'] = this.teamNameList;
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['servicearea'] = this.servicearea;
    data['businessUnit'] = this.businessUnit;
    data['tacacsAccessLevelGroup'] = this.tacacsAccessLevelGroup;
    data['serviceAreaId'] = this.serviceAreaId;
    data['serviceAreasId'] = this.serviceAreasId;
    data['businessunitid'] = this.businessunitid;
    
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
   
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    
    data['serviceAreasNameList'] = this.serviceAreasNameList;
    
    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['branchName'] = this.branchName;
   
    data['roleName'] = this.roleName;
    data['regDate'] = this.regDate;
    data['partnerName'] = this.partnerName;
    data['updatedatestring'] = this.updatedatestring;
    data['branchId'] = this.branchId;
    data['parentstaffname'] = this.parentstaffname;
    data['hrmsId'] = this.hrmsId;
    data['profileImage'] = this.profileImage;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['department'] = this.department;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
