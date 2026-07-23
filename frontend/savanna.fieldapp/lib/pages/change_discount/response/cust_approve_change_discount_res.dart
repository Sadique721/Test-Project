class CustApproveChangeDiscountRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<DiscountDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustApproveChangeDiscountRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustApproveChangeDiscountRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <DiscountDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new DiscountDataList.fromJson(v));
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

class DiscountDataList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
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
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic serviceAreaId;
  dynamic businessunitid;
  int? parentStaffId;
  int? mvnoId;
  dynamic staffUserServiceMappingList;
  dynamic serviceAreaIdsList;
  dynamic businessUnitIdsList;
  // List<ServiceAreaNameList>? serviceAreaNameList;
  // List<BusinessUnitNameList>? businessUnitNameList;
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
  dynamic department;
  bool? selected;

  DiscountDataList(
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
        // this.serviceAreaNameList,
        // this.businessUnitNameList,
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

  DiscountDataList.fromJson(Map<String, dynamic> json) {
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
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <BusinessUnitNameList>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new BusinessUnitNameList.fromJson(v));
    //   });
    // }
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
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
    // }
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

class ServiceAreaNameList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  List<NetworkDevicesList>? networkDevicesList;
  int? mvnoId;
  String? latitude;
  String? longitude;
  dynamic areaId;
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
    if (json['networkDevicesList'] != null) {
      networkDevicesList = <NetworkDevicesList>[];
      json['networkDevicesList'].forEach((v) {
        networkDevicesList!.add(new NetworkDevicesList.fromJson(v));
      });
    }
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
    if (this.networkDevicesList != null) {
      data['networkDevicesList'] =
          this.networkDevicesList!.map((v) => v.toJson()).toList();
    }
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

class NetworkDevicesList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? devicetype;
  String? status;
  dynamic latitude;
  dynamic longitude;
  int? servicearea;
  bool? isDeleted;
  int? mvnoId;
  int? totalInPorts;
  int? availableInPorts;
  int? totalOutPorts;
  int? availableOutPorts;
  dynamic totalPorts;
  dynamic availablePorts;
  List<int>? serviceAreaNameList;
  dynamic inwardId;
  int? itemId;
  int? custInventoryId;
  dynamic inventorymappingId;
  dynamic productName;

  NetworkDevicesList(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.devicetype,
        this.status,
        this.latitude,
        this.longitude,
        this.servicearea,
        this.isDeleted,
        this.mvnoId,
        this.totalInPorts,
        this.availableInPorts,
        this.totalOutPorts,
        this.availableOutPorts,
        this.totalPorts,
        this.availablePorts,
        this.serviceAreaNameList,
        this.inwardId,
        this.itemId,
        this.custInventoryId,
        this.inventorymappingId,
        this.productName});

  NetworkDevicesList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    devicetype = json['devicetype'];
    status = json['status'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    servicearea = json['servicearea'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    totalInPorts = json['totalInPorts'];
    availableInPorts = json['availableInPorts'];
    totalOutPorts = json['totalOutPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalPorts = json['totalPorts'];
    availablePorts = json['availablePorts'];
    serviceAreaNameList = json['serviceAreaNameList'].cast<int>();
    inwardId = json['inwardId'];
    itemId = json['itemId'];
    custInventoryId = json['custInventoryId'];
    inventorymappingId = json['inventorymappingId'];
    productName = json['productName'];
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
    data['devicetype'] = this.devicetype;
    data['status'] = this.status;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['servicearea'] = this.servicearea;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['totalInPorts'] = this.totalInPorts;
    data['availableInPorts'] = this.availableInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalPorts'] = this.totalPorts;
    data['availablePorts'] = this.availablePorts;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    data['inwardId'] = this.inwardId;
    data['itemId'] = this.itemId;
    data['custInventoryId'] = this.custInventoryId;
    data['inventorymappingId'] = this.inventorymappingId;
    data['productName'] = this.productName;
    return data;
  }
}

class PincodeList {
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  List<AreaList>? areaList;
  int? mvnoId;
  String? createdate;
  String? updatedate;
  int? createdById;
  int? lastModifiedById;

  PincodeList(
      {this.id,
        this.pincode,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.areaList,
        this.mvnoId,
        this.createdate,
        this.updatedate,
        this.createdById,
        this.lastModifiedById});

  PincodeList.fromJson(Map<String, dynamic> json) {
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
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    return data;
  }
}

class AreaList {
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  int? mvnoId;
  String? createdate;
  String? updatedate;
  int? createdById;
  int? lastModifiedById;
  bool? deleteFlag;
  int? primaryKey;

  AreaList(
      {this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.mvnoId,
        this.createdate,
        this.updatedate,
        this.createdById,
        this.lastModifiedById,
        this.deleteFlag,
        this.primaryKey});

  AreaList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class BusinessUnitNameList {
  int? id;
  String? buname;
  String? bucode;
  String? status;
  String? planBindingType;
  bool? isDeleted;
  int? mvnoId;
  String? createdate;
  String? updatedate;
  int? createdById;
  int? lastModifiedById;
  bool? deleteFlag;
  int? primaryKey;

  BusinessUnitNameList(
      {this.id,
        this.buname,
        this.bucode,
        this.status,
        this.planBindingType,
        this.isDeleted,
        this.mvnoId,
        this.createdate,
        this.updatedate,
        this.createdById,
        this.lastModifiedById,
        this.deleteFlag,
        this.primaryKey});

  BusinessUnitNameList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    buname = json['buname'];
    bucode = json['bucode'];
    status = json['status'];
    planBindingType = json['planBindingType'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['buname'] = this.buname;
    data['bucode'] = this.bucode;
    data['status'] = this.status;
    data['planBindingType'] = this.planBindingType;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}



