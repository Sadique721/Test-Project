import 'package:savbill/webservices/base_response.dart';

/*
class GetActiveStaffUserRes extends BaseResponse {
  List<ActiveStaffUserList>? staffUserlist;
  dynamic timestamp;
  dynamic status;

  GetActiveStaffUserRes({this.staffUserlist, this.timestamp, this.status});

  GetActiveStaffUserRes.fromJson(Map<String, dynamic> json) {
    if (json['staffUserlist'] != null) {
      staffUserlist = <ActiveStaffUserList>[];
      json['staffUserlist'].forEach((v) {
        staffUserlist!.add(new ActiveStaffUserList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.staffUserlist != null) {
      data['staffUserlist'] =
          this.staffUserlist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ActiveStaffUserList {
  dynamic id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic username;
  dynamic password;
  dynamic firstname;
  dynamic lastname;
  dynamic email;
  dynamic phone;
  dynamic countryCode;
  dynamic failcount;
  dynamic status;
  dynamic lastLoginTime;
  dynamic partnerid;
  dynamic newpassword;
  // dynamic roleIds;
  List<int>? teamIds;
  List<String>? teamNameList;
  bool? isDelete;
  dynamic fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic tacacsAccessLevelGroup;
  dynamic serviceAreaId;
  List<int>? serviceAreasId;
  dynamic businessunitid;
  List<int>? businessunitids;
  dynamic parentStaffId;
  dynamic mvnoId;
  // List<Null>? staffUserServiceMappingList;
  List<int>? serviceAreaIdsList;
  List<int>? businessUnitIdsList;
  // List<Null>? serviceAreaNameList;
  List<String>? serviceAreasNameList;
  // List<Null>? businessUnitNameList;
  List<String>? businessUnitNamesList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  dynamic branchName;
  // List<Null>? staffUserLocationMappingDtos;
  // List<dynamic>? roleName;
  dynamic regDate;
  dynamic partnerName;
  dynamic updatedatestring;
  dynamic branchId;
  dynamic parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  dynamic displayId;
  dynamic displayName;
  dynamic department;
  dynamic identityKey;

  ActiveStaffUserList(
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
        this.tacacsAccessLevelGroup,
        this.serviceAreaId,
        this.serviceAreasId,
        this.businessunitid,
        this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
        // this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
        this.businessUnitIdsList,
        // this.serviceAreaNameList,
        this.serviceAreasNameList,
        // this.businessUnitNameList,
        this.businessUnitNamesList,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.branchName,
        // this.staffUserLocationMappingDtos,
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

  ActiveStaffUserList.fromJson(Map<String, dynamic> json) {
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
    // roleIds = json['roleIds'].cast<dynamic>();
    teamIds = json['teamIds'].cast<int>();
    teamNameList = json['teamNameList'].cast<String>();
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    tacacsAccessLevelGroup = json['tacacsAccessLevelGroup'];
    serviceAreaId = json['serviceAreaId'];
    // serviceAreasId = json['serviceAreasId'].cast<int>();
    serviceAreasId = (json['serviceAreasId'] as List?)?.cast<int>() ?? [];
    businessunitid = json['businessunitid'];
    // businessunitids = json['businessunitids'].cast<int>();
    businessunitids = (json['businessunitids'] as List?)?.cast<int>() ?? [];
    parentStaffId = json['parentStaffId']??0;
    mvnoId = json['mvnoId'];
    // if (json['staffUserServiceMappingList'] != null) {
    //   staffUserServiceMappingList = <Null>[];
    //   json['staffUserServiceMappingList'].forEach((v) {
    //     staffUserServiceMappingList!.add(new Null.fromJson(v));
    //   });
    // }
    // serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    serviceAreaIdsList = (json['serviceAreaIdsList'] as List?)?.cast<int>() ?? [];
    // businessUnitIdsList = json['businessUnitIdsList'].cast<int>();
    businessUnitIdsList = (json['businessUnitIdsList'] as List?)?.cast<int>() ?? [];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <Null>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    serviceAreasNameList = (json['serviceAreasNameList'] as List?)?.cast<String>() ?? [];
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <Null>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // businessUnitNamesList = json['businessUnitNamesList'].cast<String>();
    businessUnitNamesList = (json['businessUnitNamesList'] as List?)?.cast<String>() ?? [];
    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    branchName = json['branchName'];
    // if (json['staffUserLocationMappingDtos'] != null) {
    //   staffUserLocationMappingDtos = <Null>[];
    //   json['staffUserLocationMappingDtos'].forEach((v) {
    //     staffUserLocationMappingDtos!.add(new Null.fromJson(v));
    //   });
    // }
    // roleName = json['roleName'].cast<dynamic>();
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
    data['tacacsAccessLevelGroup'] = this.tacacsAccessLevelGroup;
    data['serviceAreaId'] = this.serviceAreaId;
    data['serviceAreasId'] = this.serviceAreasId;
    data['businessunitid'] = this.businessunitid;
    data['businessunitids'] = this.businessunitids;
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    // if (this.staffUserServiceMappingList != null) {
    //   data['staffUserServiceMappingList'] =
    //       this.staffUserServiceMappingList!.map((v) => v.toJson()).toList();
    // }
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['businessUnitIdsList'] = this.businessUnitIdsList;
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    data['serviceAreasNameList'] = this.serviceAreasNameList;
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
    // }
    data['businessUnitNamesList'] = this.businessUnitNamesList;
    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['branchName'] = this.branchName;
    // if (this.staffUserLocationMappingDtos != null) {
    //   data['staffUserLocationMappingDtos'] =
    //       this.staffUserLocationMappingDtos!.map((v) => v.toJson()).toList();
    // }
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

*/


class GetActiveStaffUserRes extends BaseResponse{
  // dynamic responseCode;
  // dynamic responseMessage;
  dynamic data;
  List<ActiveStaffUserList>? dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  GetActiveStaffUserRes(
      {
        // this.responseCode,
        // this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetActiveStaffUserRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ActiveStaffUserList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ActiveStaffUserList.fromJson(v));
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

class ActiveStaffUserList {
  dynamic id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic username;
  dynamic password;
  dynamic firstname;
  dynamic lastname;
  dynamic email;
  dynamic phone;
  dynamic countryCode;
  dynamic failcount;
  dynamic status;
  dynamic lastLoginTime;
  dynamic partnerid;
  dynamic newpassword;
  // List<int>? roleIds;
  // List<int>? assignableRoleIds;
  // List<int>? teamIds;
  // List<String>? teamNameList;
  bool? isDelete;
  dynamic fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic tacacsAccessLevelGroup;
  dynamic serviceAreaId;
  // List<Null>? serviceAreasId;
  dynamic businessunitid;
  // List<Null>? businessunitids;
  dynamic parentStaffId;
  dynamic mvnoId;
  // List<Null>? staffUserServiceMappingList;
  // List<int>? serviceAreaIdsList;
  dynamic businessUnitIdsList;
  // List<Null>? serviceAreaNameList;
  // List<String>? serviceAreasNameList;
  // List<Null>? businessUnitNameList;
  // List<Null>? businessUnitNamesList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  dynamic uuid;
  dynamic eventName;
  dynamic eventId;
  bool? isNotificationRequired;
  dynamic passwordDate;
  bool? isPasswordExpired;
  dynamic branchName;
  // List<Null>? staffUserLocationMappingDtos;
  // List<String>? roleName;
  dynamic regDate;
  dynamic partnerName;
  dynamic updatedatestring;
  dynamic branchId;
  dynamic parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  dynamic displayId;
  dynamic displayName;
  dynamic department;
  dynamic identityKey;

  ActiveStaffUserList(
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
        // this.assignableRoleIds,
        // this.teamIds,
        // this.teamNameList,
        this.isDelete,
        this.fullName,
        this.sysstaff,
        this.servicearea,
        this.businessUnit,
        this.tacacsAccessLevelGroup,
        this.serviceAreaId,
        // this.serviceAreasId,
        this.businessunitid,
        // this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
        // this.staffUserServiceMappingList,
        // this.serviceAreaIdsList,
        this.businessUnitIdsList,
        // this.serviceAreaNameList,
        // this.serviceAreasNameList,
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

  ActiveStaffUserList.fromJson(Map<String, dynamic> json) {
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
    // assignableRoleIds = json['assignableRoleIds'].cast<int>();
    // teamIds = json['teamIds'].cast<int>();
    // teamNameList = json['teamNameList'].cast<String>();
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    tacacsAccessLevelGroup = json['tacacsAccessLevelGroup'];
    serviceAreaId = json['serviceAreaId'];
    // if (json['serviceAreasId'] != null) {
    //   serviceAreasId = <Null>[];
    //   json['serviceAreasId'].forEach((v) {
    //     serviceAreasId!.add(new Null.fromJson(v));
    //   });
    // }
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
    // serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    businessUnitIdsList = json['businessUnitIdsList'];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <Null>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
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
    // data['assignableRoleIds'] = this.assignableRoleIds;
    // data['teamIds'] = this.teamIds;
    // data['teamNameList'] = this.teamNameList;
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['servicearea'] = this.servicearea;
    data['businessUnit'] = this.businessUnit;
    data['tacacsAccessLevelGroup'] = this.tacacsAccessLevelGroup;
    data['serviceAreaId'] = this.serviceAreaId;
    // if (this.serviceAreasId != null) {
    //   data['serviceAreasId'] =
    //       this.serviceAreasId!.map((v) => v.toJson()).toList();
    // }
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
    // data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['businessUnitIdsList'] = this.businessUnitIdsList;
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    // data['serviceAreasNameList'] = this.serviceAreasNameList;
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
