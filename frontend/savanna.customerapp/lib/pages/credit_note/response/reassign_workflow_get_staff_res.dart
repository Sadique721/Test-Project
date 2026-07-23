class ReassignWorkflowGetStaffRes {
  int? responseCode;
  dynamic responseMessage;
  dynamic data;
  List<ReassignWorkflowList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ReassignWorkflowGetStaffRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ReassignWorkflowGetStaffRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ReassignWorkflowList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ReassignWorkflowList.fromJson(v));
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

class ReassignWorkflowList {
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
  dynamic email;
  dynamic phone;
  dynamic countryCode;
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
  dynamic serviceAreaId;
  List<int>? serviceAreasId;
  dynamic businessunitid;
  List<int>? businessunitids;
  dynamic parentStaffId;
  int? mvnoId;
  dynamic staffUserServiceMappingList;
  dynamic serviceAreaIdsList;
  dynamic businessUnitIdsList;
  // List<ServiceAreaNameList>? serviceAreaNameList;
  List<String>? roleName;
  String? regDate;
  String? partnerName;
  String? updatedatestring;
  int? branchId;
  int? displayId;
  String? displayName;
  bool? selected;

  ReassignWorkflowList(
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
        this.serviceAreaId,
        this.serviceAreasId,
        this.businessunitid,
        this.businessunitids,
        this.parentStaffId,
        this.mvnoId,
        this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
        this.businessUnitIdsList,
        // this.serviceAreaNameList,

        this.roleName,
        this.regDate,
        this.partnerName,
        this.updatedatestring,
        this.branchId,
        this.displayId,
        this.displayName,
      this.selected});

  ReassignWorkflowList.fromJson(Map<String, dynamic> json) {
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
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <ServiceAreaNameList>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
    //   });
    // }

    roleName = json['roleName'].cast<String>();
    regDate = json['regDate'];
    partnerName = json['partnerName'];
    updatedatestring = json['updatedatestring'];
    branchId = json['branchId'];
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
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    data['roleName'] = this.roleName;
    data['regDate'] = this.regDate;
    data['partnerName'] = this.partnerName;
    data['updatedatestring'] = this.updatedatestring;
    data['branchId'] = this.branchId;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}

class ServiceAreaNameList {
  int? id;
  String? createdate;
  String? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  int? createdById;
  Null? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  List<Null>? networkDevicesList;
  int? mvnoId;
  String? latitude;
  String? longitude;
  Null? areaId;
  List<PincodeList>? pincodeList;
  int? cityid;

  ServiceAreaNameList(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.status,
        this.isDeleted,
        this.networkDevicesList,
        this.mvnoId,
        this.latitude,
        this.longitude,
        this.areaId,
        this.pincodeList,
        this.cityid});

  ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    if (json['pincodeList'] != null) {
      pincodeList = <PincodeList>[];
      json['pincodeList'].forEach((v) {
        pincodeList!.add(new PincodeList.fromJson(v));
      });
    }
    cityid = json['cityid'];
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
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    if (this.pincodeList != null) {
      data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
    }
    data['cityid'] = this.cityid;
    return data;
  }
}

class PincodeList {
  String? createdate;
  String? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  List<AreaList>? areaList;
  int? mvnoId;

  PincodeList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.pincode,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.areaList,
        this.mvnoId});

  PincodeList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    if (json['areaList'] != null) {
      areaList = <AreaList>[];
      json['areaList'].forEach((v) {
        areaList!.add(new AreaList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
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
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class AreaList {
  String? createdate;
  String? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  int? mvnoId;
  bool? deleteFlag;
  int? primaryKey;

  AreaList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.mvnoId,
        this.deleteFlag,
        this.primaryKey});

  AreaList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
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
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
