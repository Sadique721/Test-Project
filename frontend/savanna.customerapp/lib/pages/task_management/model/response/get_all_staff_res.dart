import 'package:savbill/webservices/base_response.dart';

class GetAllStaffRes extends BaseResponse{
  dynamic data;
  List<AllStaffDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllStaffRes(
      {
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllStaffRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <AllStaffDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AllStaffDataList.fromJson(v));
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

class AllStaffDataList {
  int? id;
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? username;
  String? password;
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
  // List<Null>? businessunitids;
  int? parentStaffId;
  int? mvnoId;
  // List<Null>? staffUserServiceMappingList;
  List<int>? serviceAreaIdsList;
  // List<Null>? businessUnitIdsList;
  // List<Null>? serviceAreaNameList;
  List<String>? serviceAreasNameList;
  // List<Null>? businessUnitNameList;
  // List<Null>? businessUnitNamesList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  String? uuid;
  dynamic eventName;
  dynamic eventId;
  bool? isNotificationRequired;
  String? passwordDate;
  bool? isPasswordExpired;
  String? branchName;
  // List<Null>? staffUserLocationMappingDtos;
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
  bool? selected = false;

  AllStaffDataList(
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
        // this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
        // this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
        // this.businessUnitIdsList,
        // this.serviceAreaNameList,
        this.serviceAreasNameList,
        // this.businessUnitNameList,
        // this.businessUnitNamesList,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.uuid,
        this.eventName,
        this.eventId,
        this.isNotificationRequired,
        this.passwordDate,
        this.isPasswordExpired,
        this.branchName,
        // this.staffUserLocationMappingDtos,
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
        this.identityKey,
        this.selected});

  AllStaffDataList.fromJson(Map<String, dynamic> json) {
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
    // if (json['businessunitids'] != null) {
    //   businessunitids = <Null>[];
    //   json['businessunitids'].forEach((v) {
    //     businessunitids!.add(new Null.fromJson(v));
    //   });
    // }
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    // if (json['staffUserServiceMappingList'] != null) {
    //   staffUserServiceMappingList = <Null>[];
    //   json['staffUserServiceMappingList'].forEach((v) {
    //     staffUserServiceMappingList!.add(new Null.fromJson(v));
    //   });
    // }
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    // if (json['businessUnitIdsList'] != null) {
    //   businessUnitIdsList = <Null>[];
    //   json['businessUnitIdsList'].forEach((v) {
    //     businessUnitIdsList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <Null>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new Null.fromJson(v));
    //   });
    // }
    serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <Null>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['businessUnitNamesList'] != null) {
    //   businessUnitNamesList = <Null>[];
    //   json['businessUnitNamesList'].forEach((v) {
    //     businessUnitNamesList!.add(new Null.fromJson(v));
    //   });
    // }
    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    uuid = json['uuid'];
    eventName = json['eventName'];
    eventId = json['eventId'];
    isNotificationRequired = json['isNotificationRequired'];
    passwordDate = json['passwordDate'];
    isPasswordExpired = json['isPasswordExpired'];
    branchName = json['branchName'];
    // if (json['staffUserLocationMappingDtos'] != null) {
    //   staffUserLocationMappingDtos = <Null>[];
    //   json['staffUserLocationMappingDtos'].forEach((v) {
    //     staffUserLocationMappingDtos!.add(new Null.fromJson(v));
    //   });
    // }
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
    // if (this.businessunitids != null) {
    //   data['businessunitids'] =
    //       this.businessunitids!.map((v) => v.toJson()).toList();
    // }
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    // if (this.staffUserServiceMappingList != null) {
    //   data['staffUserServiceMappingList'] =
    //       this.staffUserServiceMappingList!.map((v) => v.toJson()).toList();
    // }
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    // if (this.businessUnitIdsList != null) {
    //   data['businessUnitIdsList'] =
    //       this.businessUnitIdsList!.map((v) => v.toJson()).toList();
    // }
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    data['serviceAreasNameList'] = this.serviceAreasNameList;
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
    // }
    // if (this.businessUnitNamesList != null) {
    //   data['businessUnitNamesList'] =
    //       this.businessUnitNamesList!.map((v) => v.toJson()).toList();
    // }
    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['uuid'] = this.uuid;
    data['eventName'] = this.eventName;
    data['eventId'] = this.eventId;
    data['isNotificationRequired'] = this.isNotificationRequired;
    data['passwordDate'] = this.passwordDate;
    data['isPasswordExpired'] = this.isPasswordExpired;
    data['branchName'] = this.branchName;
    // if (this.staffUserLocationMappingDtos != null) {
    //   data['staffUserLocationMappingDtos'] =
    //       this.staffUserLocationMappingDtos!.map((v) => v.toJson()).toList();
    // }
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
