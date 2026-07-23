import 'package:savbill/webservices/base_response.dart';
class StaffUserListRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<StaffUserDetail>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  StaffUserListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  StaffUserListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <StaffUserDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffUserDetail.fromJson(v));
      });
    }
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
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class StaffUserDetail {
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
  dynamic email;
  dynamic phone;
  dynamic countryCode;
  int? failcount;
  String? status;
  dynamic lastLoginTime;
  int? partnerid;
  dynamic newpassword;
  // List<int>? roleIds;
  List<int>? teamIds;
  List<String>? teamNameList;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic serviceAreaId;
  List<int>? serviceAreasId;
  dynamic businessunitid;
  List<int>? businessunitids;
  int? parentStaffId;
  int? mvnoId;
  List<int>? serviceAreaIdsList;
  // List<int>? businessUnitIdsList;
  List<String>? serviceAreasNameList;
  List<String>? businessUnitNamesList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  String? branchName;
  // List<String>? roleName;
  String? regDate;
  String? partnerName;
  dynamic updatedatestring;
  dynamic branchId;
  dynamic parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  int? displayId;
  String? displayName;
  dynamic department;
  int? identityKey;

  StaffUserDetail(
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
        // this.roleIds,
        this.teamIds,
        this.teamNameList,
        this.isDelete,
        this.fullName,
        this.sysstaff,
        this.servicearea,
        this.businessUnit,
        this.serviceAreaId,
        this.serviceAreasId,
        this.businessunitid,
        this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
        this.serviceAreaIdsList,
        // this.businessUnitIdsList,
        this.serviceAreasNameList,
        this.businessUnitNamesList,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.branchName,
        // this.roleName,
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

  StaffUserDetail.fromJson(Map<String, dynamic> json) {
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
    // roleIds = json['roleIds'].cast<int>();
    teamIds = json['teamIds'].cast<int>();
    teamNameList = json['teamNameList'].cast<String>();
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    serviceAreaId = json['serviceAreaId'];
    serviceAreasId = json['serviceAreasId'].cast<int>();
    businessunitid = json['businessunitid'];
    businessunitids = json['businessunitids'].cast<int>();
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    // businessUnitIdsList = json['businessUnitIdsList'].cast<int>();

    serviceAreasNameList = json['serviceAreasNameList'].cast<String>();

    businessUnitNamesList = json['businessUnitNamesList'].cast<String>();
    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    branchName = json['branchName'];
    // roleName = json['roleName'].cast<String>();
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
    // data['roleIds'] = this.roleIds;
    data['teamIds'] = this.teamIds;
    data['teamNameList'] = this.teamNameList;
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['servicearea'] = this.servicearea;
    data['businessUnit'] = this.businessUnit;
    data['serviceAreaId'] = this.serviceAreaId;
    data['serviceAreasId'] = this.serviceAreasId;
    data['businessunitid'] = this.businessunitid;
    data['businessunitids'] = this.businessunitids;
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    // data['businessUnitIdsList'] = this.businessUnitIdsList;

    data['serviceAreasNameList'] = this.serviceAreasNameList;

    data['businessUnitNamesList'] = this.businessUnitNamesList;
    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['branchName'] = this.branchName;
    // data['roleName'] = this.roleName;
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
