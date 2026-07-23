import 'package:savbill/webservices/base_response.dart';

class ApproveRejectCafCustomerRes extends BaseResponse {
  ApproveRejectCafResult? result;
  String? message;
  String? timestamp;
  int? status;

  ApproveRejectCafCustomerRes(
      {this.result, this.message, this.timestamp, this.status});

  ApproveRejectCafCustomerRes.fromJson(Map<String, dynamic> json) {
    result =
    json['result'] != null ? new ApproveRejectCafResult.fromJson(json['result']) : null;
    message = json['message'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.result != null) {
      data['result'] = this.result!.toJson();
    }
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ApproveRejectCafResult {
  int? responseCode;
  Null? responseMessage;
  Null? data;
  List<ApproveRejectCafDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ApproveRejectCafResult(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ApproveRejectCafResult.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ApproveRejectCafDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApproveRejectCafDataList.fromJson(v));
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

class ApproveRejectCafDataList {
  int? id;
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? username;
  String? password;
  String? firstname;
  String? lastname;
  String? email;
  String? phone;
  dynamic countryCode;
  int? failcount;
  String? status;
  dynamic lastLoginTime;
  int? partnerid;
  dynamic newpassword;
  List<int>? roleIds;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic serviceAreaId;
  dynamic businessunitid;
  dynamic parentStaffId;
  int? mvnoId;
  dynamic staffUserServiceMappingList;
  dynamic serviceAreaIdsList;
  dynamic businessUnitIdsList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  dynamic tacacsAccessLevelGroup;
  dynamic branchName;
  List<String>? roleName;
  String? regDate;
  String? partnerName;
  String? updatedatestring;
  dynamic branchId;
  dynamic parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  int? displayId;
  String? displayName;
  Null? department;
  bool? selected;

  ApproveRejectCafDataList(
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

        this.isDelete,
        this.fullName,
        this.sysstaff,
        this.servicearea,
        this.businessUnit,
        this.serviceAreaId,
        this.businessunitid,
        this.parentStaffId,
        this.mvnoId,
        this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
        this.businessUnitIdsList,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.tacacsAccessLevelGroup,
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
      this.selected});

  ApproveRejectCafDataList.fromJson(Map<String, dynamic> json) {
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

    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    serviceAreaId = json['serviceAreaId'];

    businessunitid = json['businessunitid'];

    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    staffUserServiceMappingList = json['staffUserServiceMappingList'];
    serviceAreaIdsList = json['serviceAreaIdsList'];
    businessUnitIdsList = json['businessUnitIdsList'];


    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    tacacsAccessLevelGroup = json['tacacsAccessLevelGroup'];
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

    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['servicearea'] = this.servicearea;
    data['businessUnit'] = this.businessUnit;
    data['serviceAreaId'] = this.serviceAreaId;

    data['businessunitid'] = this.businessunitid;

    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    data['staffUserServiceMappingList'] = this.staffUserServiceMappingList;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['businessUnitIdsList'] = this.businessUnitIdsList;

    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['tacacsAccessLevelGroup'] = this.tacacsAccessLevelGroup;
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
    return data;
  }
}
