/*class ActiveStaffUserListRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<StaffUserDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ActiveStaffUserListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ActiveStaffUserListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <StaffUserDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffUserDataList.fromJson(v));
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

class StaffUserDataList {
  int? id;
  String? createdate;
  String? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  String? username;
  Null? password;
  String? firstname;
  String? lastname;
  Null? email;
  Null? phone;
  Null? countryCode;
  int? failcount;
  String? status;
  Null? lastLoginTime;
  int? partnerid;
  Null? newpassword;
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
  Null? businessunitid;
  List<int>? businessunitids;
  Null? parentStaffId;
  dynamic mvnoId;
  List<int>? serviceAreaIdsList;
  List<int>? businessUnitIdsList;
  List<String>? serviceAreasNameList;
  List<String>? businessUnitNamesList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  String? branchName;
  // List<dynamic>? roleName;
  dynamic regDate;
  String? partnerName;
  dynamic updatedatestring;
  dynamic branchId;
  dynamic parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  int? displayId;
  String? displayName;
  Null? department;
  int? identityKey;

  StaffUserDataList(
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
        this.businessUnitIdsList,
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

  StaffUserDataList.fromJson(Map<String, dynamic> json) {
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
    businessUnitIdsList = json['businessUnitIdsList'].cast<int>();
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
    data['businessUnitIdsList'] = this.businessUnitIdsList;
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
}*/



class ActiveStaffUserListRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<StaffUserDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ActiveStaffUserListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ActiveStaffUserListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <StaffUserDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffUserDataList.fromJson(v));
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

class StaffUserDataList {
  int? id;
  Null? createdate;
  Null? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  String? username;
  Null? password;
  String? firstname;
  String? lastname;
  Null? email;
  Null? phone;
  Null? countryCode;
  int? failcount;
  String? status;
  Null? lastLoginTime;
  int? partnerid;
  Null? newpassword;
  Null? roleIds;
  // List<Null>? teamIds;
  // List<Null>? teamNameList;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  Null? servicearea;
  Null? businessUnit;
  Null? serviceAreaId;
  // List<Null>? serviceAreasId;
  Null? businessunitid;
  // List<Null>? businessunitids;
  Null? parentStaffId;
  Null? mvnoId;
  // List<Null>? serviceAreaIdsList;
  Null? businessUnitIdsList;
  // List<Null>? serviceAreaNameList;
  // List<Null>? serviceAreasNameList;
  // List<Null>? businessUnitNameList;
  // List<Null>? businessUnitNamesList;
  Null? totalCollected;
  Null? totalTransferred;
  Null? availableAmount;
  Null? lcoId;
  String? branchName;
  Null? roleName;
  Null? regDate;
  String? partnerName;
  Null? updatedatestring;
  Null? branchId;
  Null? parentstaffname;
  Null? hrmsId;
  Null? profileImage;
  int? displayId;
  String? displayName;
  Null? department;
  int? identityKey;

  StaffUserDataList(
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
        // this.teamIds,
        // this.teamNameList,
        this.isDelete,
        this.fullName,
        this.sysstaff,
        this.servicearea,
        this.businessUnit,
        this.serviceAreaId,
        // this.serviceAreasId,
        this.businessunitid,
        // this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
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

  StaffUserDataList.fromJson(Map<String, dynamic> json) {
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
    roleIds = json['roleIds'];
    // if (json['teamIds'] != null) {
    //   teamIds = <Null>[];
    //   json['teamIds'].forEach((v) {
    //     teamIds!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['teamNameList'] != null) {
    //   teamNameList = <Null>[];
    //   json['teamNameList'].forEach((v) {
    //     teamNameList!.add(new Null.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
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
    // if (json['serviceAreaIdsList'] != null) {
    //   serviceAreaIdsList = <Null>[];
    //   json['serviceAreaIdsList'].forEach((v) {
    //     serviceAreaIdsList!.add(new Null.fromJson(v));
    //   });
    // }
    businessUnitIdsList = json['businessUnitIdsList'];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <Null>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['serviceAreasNameList'] != null) {
    //   serviceAreasNameList = <Null>[];
    //   json['serviceAreasNameList'].forEach((v) {
    //     serviceAreasNameList!.add(new Null.fromJson(v));
    //   });
    // }
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
    branchName = json['branchName'];
    roleName = json['roleName'];
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
    // if (this.teamIds != null) {
    //   data['teamIds'] = this.teamIds!.map((v) => v.toJson()).toList();
    // }
    // if (this.teamNameList != null) {
    //   data['teamNameList'] = this.teamNameList!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['servicearea'] = this.servicearea;
    data['businessUnit'] = this.businessUnit;
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
    // if (this.serviceAreaIdsList != null) {
    //   data['serviceAreaIdsList'] =
    //       this.serviceAreaIdsList!.map((v) => v.toJson()).toList();
    // }
    data['businessUnitIdsList'] = this.businessUnitIdsList;
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    // if (this.serviceAreasNameList != null) {
    //   data['serviceAreasNameList'] =
    //       this.serviceAreasNameList!.map((v) => v.toJson()).toList();
    // }
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
