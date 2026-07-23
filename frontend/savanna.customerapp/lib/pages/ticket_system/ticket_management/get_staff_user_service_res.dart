/*class GetStaffUserServiceRes {
  int? responseCode;
  String? responseMessage;
  List<StaffUserServiceDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetStaffUserServiceRes(
      {this.responseCode,
        this.responseMessage,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetStaffUserServiceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <StaffUserServiceDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffUserServiceDataList.fromJson(v));
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

class StaffUserServiceDataList {
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
  // List<int>? roleIds;
  List<int>? teamIds;
  List<String>? teamNameList;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  dynamic servicearea;
  BusinessUnit? businessUnit;
  dynamic serviceAreaId;
  List<int>? serviceAreasId;
  dynamic businessunitid;
  List<int>? businessunitids;
  int? parentStaffId;
  int? mvnoId;
  List<StaffUserServiceMappingList>? staffUserServiceMappingList;
  List<int>? serviceAreaIdsList;
  List<int>? businessUnitIdsList;
  List<String>? serviceAreasNameList;
  List<String>? businessUnitNamesList;
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
  String? profileImage;
  int? displayId;
  String? displayName;

  StaffUserServiceDataList(
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
        this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
        this.businessUnitIdsList,
        this.serviceAreasNameList,
        this.businessUnitNamesList,
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
        this.displayName});

  StaffUserServiceDataList.fromJson(Map<String, dynamic> json) {
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
    businessUnit = json['businessUnit'] != null
        ? new BusinessUnit.fromJson(json['businessUnit'])
        : null;
    serviceAreaId = json['serviceAreaId'];
    serviceAreasId = json['serviceAreasId'].cast<int>();
    businessunitid = json['businessunitid'];
    businessunitids = json['businessunitids'].cast<int>();
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    if (json['staffUserServiceMappingList'] != null) {
      staffUserServiceMappingList = <StaffUserServiceMappingList>[];
      json['staffUserServiceMappingList'].forEach((v) {
        staffUserServiceMappingList!
            .add(new StaffUserServiceMappingList.fromJson(v));
      });
    }
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    businessUnitIdsList = json['businessUnitIdsList'].cast<int>();
    serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    businessUnitNamesList = json['businessUnitNamesList'].cast<String>();
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
    if (this.businessUnit != null) {
      data['businessUnit'] = this.businessUnit!.toJson();
    }
    data['serviceAreaId'] = this.serviceAreaId;
    data['serviceAreasId'] = this.serviceAreasId;
    data['businessunitid'] = this.businessunitid;
    data['businessunitids'] = this.businessunitids;
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    if (this.staffUserServiceMappingList != null) {
      data['staffUserServiceMappingList'] =
          this.staffUserServiceMappingList!.map((v) => v.toJson()).toList();
    }
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['businessUnitIdsList'] = this.businessUnitIdsList;

    data['serviceAreasNameList'] = this.serviceAreasNameList;

    data['businessUnitNamesList'] = this.businessUnitNamesList;
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
    return data;
  }
}

class BusinessUnit {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? buname;
  String? bucode;
  String? status;
  String? planBindingType;
  bool? isDeleted;
  int? mvnoId;
  List<InvestmentCodeid>? investmentCodeid;
  bool? deleteFlag;
  int? primaryKey;

  BusinessUnit(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.buname,
        this.bucode,
        this.status,
        this.planBindingType,
        this.isDeleted,
        this.mvnoId,
        this.investmentCodeid,
        this.deleteFlag,
        this.primaryKey});

  BusinessUnit.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    buname = json['buname'];
    bucode = json['bucode'];
    status = json['status'];
    planBindingType = json['planBindingType'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    if (json['investmentCodeid'] != null) {
      investmentCodeid = <InvestmentCodeid>[];
      json['investmentCodeid'].forEach((v) {
        investmentCodeid!.add(new InvestmentCodeid.fromJson(v));
      });
    }
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['buname'] = this.buname;
    data['bucode'] = this.bucode;
    data['status'] = this.status;
    data['planBindingType'] = this.planBindingType;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    if (this.investmentCodeid != null) {
      data['investmentCodeid'] =
          this.investmentCodeid!.map((v) => v.toJson()).toList();
    }
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class InvestmentCodeid {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? iccode;
  String? icname;
  bool? isDeleted;
  int? mvnoId;
  String? status;
  bool? deleteFlag;
  int? primaryKey;

  InvestmentCodeid(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.iccode,
        this.icname,
        this.isDeleted,
        this.mvnoId,
        this.status,
        this.deleteFlag,
        this.primaryKey});

  InvestmentCodeid.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    iccode = json['iccode'];
    icname = json['icname'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['iccode'] = this.iccode;
    data['icname'] = this.icname;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class StaffUserServiceMappingList {
  int? id;
  String? prefix;
  int? fromreceiptnumber;
  int? toreceiptnumber;
  bool? isActive;
  bool? isDeleted;
  int? stfmappingId;
  bool? deleteFlag;
  int? primaryKey;

  StaffUserServiceMappingList(
      {this.id,
        this.prefix,
        this.fromreceiptnumber,
        this.toreceiptnumber,
        this.isActive,
        this.isDeleted,
        this.stfmappingId,
        this.deleteFlag,
        this.primaryKey});

  StaffUserServiceMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    prefix = json['prefix'];
    fromreceiptnumber = json['fromreceiptnumber'];
    toreceiptnumber = json['toreceiptnumber'];
    isActive = json['isActive'];
    isDeleted = json['isDeleted'];
    stfmappingId = json['stfmappingId'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['prefix'] = this.prefix;
    data['fromreceiptnumber'] = this.fromreceiptnumber;
    data['toreceiptnumber'] = this.toreceiptnumber;
    data['isActive'] = this.isActive;
    data['isDeleted'] = this.isDeleted;
    data['stfmappingId'] = this.stfmappingId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}*/

import 'package:savbill/webservices/base_response.dart';

class GetStaffUserServiceRes extends BaseResponse {
  String? responseMessage;
  List<StaffUserServiceDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetStaffUserServiceRes(
      {this.responseMessage,
      this.dataList,
      this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  GetStaffUserServiceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <StaffUserServiceDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffUserServiceDataList.fromJson(v));
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

class StaffUserServiceDataList {
  int? id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  String? username;
  dynamic password;
  dynamic firstname;
  dynamic lastname;
  dynamic email;
  dynamic phone;
  dynamic countryCode;
  int? failcount;
  dynamic status;
  dynamic lastLoginTime;
  dynamic partnerid;
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
  List<StaffUserServiceMappingList>? staffUserServiceMappingList;
  List<int>? serviceAreaIdsList;
  List<int>? businessUnitIdsList;
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
  String? parentstaffname;
  dynamic hrmsId;
  dynamic profileImage;
  int? displayId;
  String? displayName;
  dynamic department;

  StaffUserServiceDataList(
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
      this.staffUserServiceMappingList,
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
      this.department});

  StaffUserServiceDataList.fromJson(Map<String, dynamic> json) {
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
    // teamIds = json['teamIds'].cast<int>();
    teamIds = (json['teamIds'] as List?)?.cast<int>() ?? [];
    // teamNameList = json['teamNameList'].cast<String>();
    teamNameList = (json['teamNameList'] as List?)?.cast<String>() ?? [];
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    serviceAreaId = json['serviceAreaId'];
    // serviceAreasId = json['serviceAreasId'].cast<int>();
    serviceAreasId = (json['serviceAreasId'] as List?)?.cast<int>() ?? [];
    businessunitid = json['businessunitid'];
    // businessunitids = json['businessunitids'].cast<int>();
    businessunitids = (json['businessunitids'] as List?)?.cast<int>() ?? [];
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    if (json['staffUserServiceMappingList'] != null) {
      staffUserServiceMappingList = <StaffUserServiceMappingList>[];
      json['staffUserServiceMappingList'].forEach((v) {
        staffUserServiceMappingList!
            .add(StaffUserServiceMappingList.fromJson(v));
      });
    }
    // serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    serviceAreaIdsList = (json['serviceAreaIdsList'] as List?)?.cast<int>() ?? [];
    // businessUnitIdsList = json['businessUnitIdsList'].cast<int>();
    businessUnitIdsList = (json['businessUnitIdsList'] as List?)?.cast<int>() ?? [];

    // serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    serviceAreasNameList = (json['serviceAreasNameList'] as List?)?.cast<String>() ?? [];
    // businessUnitNamesList = json['businessUnitNamesList'].cast<String>();
    businessUnitNamesList = (json['businessUnitNamesList'] as List?)?.cast<String>() ?? [];
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
    if (this.staffUserServiceMappingList != null) {
      data['staffUserServiceMappingList'] =
          this.staffUserServiceMappingList!.map((v) => v.toJson()).toList();
    }
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
    return data;
  }
}

class StaffUserServiceMappingList {
  int? id;
  String? prefix;
  int? fromreceiptnumber;
  int? toreceiptnumber;
  bool? isActive;
  bool? isDeleted;
  int? stfmappingId;
  bool? deleteFlag;
  int? primaryKey;

  StaffUserServiceMappingList(
      {this.id,
      this.prefix,
      this.fromreceiptnumber,
      this.toreceiptnumber,
      this.isActive,
      this.isDeleted,
      this.stfmappingId,
      this.deleteFlag,
      this.primaryKey});

  StaffUserServiceMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    prefix = json['prefix'];
    fromreceiptnumber = json['fromreceiptnumber'];
    toreceiptnumber = json['toreceiptnumber'];
    isActive = json['isActive'];
    isDeleted = json['isDeleted'];
    stfmappingId = json['stfmappingId'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['prefix'] = this.prefix;
    data['fromreceiptnumber'] = this.fromreceiptnumber;
    data['toreceiptnumber'] = this.toreceiptnumber;
    data['isActive'] = this.isActive;
    data['isDeleted'] = this.isDeleted;
    data['stfmappingId'] = this.stfmappingId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
