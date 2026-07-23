import 'package:savbill/webservices/base_response.dart';

/*
class StaffsByServiceAreaIdRes extends BaseResponse {
  dynamic responseCode;
  String? responseMessage;
  dynamic data;
  List<StaffsByServiceAreaData>? dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  StaffsByServiceAreaIdRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  StaffsByServiceAreaIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <StaffsByServiceAreaData>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffsByServiceAreaData.fromJson(v));
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

class StaffsByServiceAreaData {
  int? id;
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
  dynamic lastLogdynamicime;
  dynamic partnerid;
  dynamic newpassword;
  List<dynamic>? roleIds;
  bool? isDelete;
  dynamic fullName;
  bool? sysstaff;
  dynamic servicearea;
  dynamic businessUnit;
  dynamic serviceAreaId;
  dynamic businessunitid;
  dynamic parentStaffId;
  dynamic mvnoId;
  dynamic staffUserServiceMappingList;
  dynamic serviceAreaIdsList;
  dynamic businessUnitIdsList;
  // List<ServiceAreaNameList>? serviceAreaNameList;
  // List<BusinessUnitNameList>? businessUnitNameList;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  dynamic branchName;
  List<String>? roleName;
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

  StaffsByServiceAreaData(
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
        this.department});

  StaffsByServiceAreaData.fromJson(Map<String, dynamic> json) {
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

    partnerid = json['partnerid'];
    newpassword = json['newpassword'];
    roleIds = json['roleIds'].cast<dynamic>();
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
    //
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
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
    return data;
  }
}

class ServiceAreaNameList {
  dynamic id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic name;
  dynamic status;
  bool? isDeleted;
  List<NetworkDevicesList>? networkDevicesList;
  dynamic mvnoId;
  dynamic latitude;
  dynamic longitude;
  dynamic areaId;
  List<PincodeList>? pincodeList;
  dynamic cityid;

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
  dynamic id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic name;
  dynamic devicetype;
  dynamic status;
  dynamic latitude;
  dynamic longitude;
  dynamic servicearea;
  bool? isDeleted;
  dynamic mvnoId;
  dynamic totalInPorts;
  dynamic availableInPorts;
  dynamic totalOutPorts;
  dynamic availableOutPorts;
  dynamic totalPorts;
  dynamic availablePorts;
  List<dynamic>? serviceAreaNameList;
  dynamic inwardId;
  dynamic itemId;
  dynamic custInventoryId;
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
    serviceAreaNameList = json['serviceAreaNameList'].cast<dynamic>();
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
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic pincode;
  dynamic status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  List<AreaList>? areaList;
  dynamic mvnoId;

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
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic name;
  dynamic status;
  bool? isDeleted;
  dynamic countryId;
  dynamic cityId;
  dynamic stateId;
  dynamic mvnoId;
  bool? deleteFlag;
  dynamic primaryKey;

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

class BusinessUnitNameList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic buname;
  dynamic bucode;
  dynamic status;
  dynamic planBindingType;
  bool? isDeleted;
  dynamic mvnoId;
  List<InvestmentCodeid>? investmentCodeid;
  bool? deleteFlag;
  dynamic primaryKey;

  BusinessUnitNameList(
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

  BusinessUnitNameList.fromJson(Map<String, dynamic> json) {
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
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic iccode;
  dynamic icname;
  bool? isDeleted;
  dynamic mvnoId;
  dynamic status;
  bool? deleteFlag;
  dynamic primaryKey;

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
*/



class StaffsByServiceAreaIdRes extends BaseResponse{
  // int? responseCode;
  String? responseMessage;
  dynamic data;
  List<StaffsByServiceAreaData>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  StaffsByServiceAreaIdRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  StaffsByServiceAreaIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <StaffsByServiceAreaData>[];
      json['dataList'].forEach((v) {
        dataList!.add(new StaffsByServiceAreaData.fromJson(v));
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

class StaffsByServiceAreaData {
  int? id;
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic username;
  dynamic password;
  String? firstname;
  dynamic lastname;
  dynamic email;
  dynamic phone;
  dynamic countryCode;
  int? failcount;
  dynamic status;
  dynamic lastLoginTime;
  dynamic partnerid;
  dynamic newpassword;
  dynamic roleIds;
  dynamic assignableRoleIds;
  // List<Null>? teamIds;
  // List<Null>? teamNameList;
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
  dynamic staffUserServiceMappingList;
  dynamic serviceAreaIdsList;
  dynamic businessUnitIdsList;
  // List<Null>? serviceAreaNameList;
  // List<Null>? serviceAreasNameList;
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
  dynamic roleName;
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
  int? identityKey;

  StaffsByServiceAreaData(
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
        this.assignableRoleIds,
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
        this.staffUserServiceMappingList,
        this.serviceAreaIdsList,
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

  StaffsByServiceAreaData.fromJson(Map<String, dynamic> json) {
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
    assignableRoleIds = json['assignableRoleIds'];
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
    staffUserServiceMappingList = json['staffUserServiceMappingList'];
    serviceAreaIdsList = json['serviceAreaIdsList'];
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
    data['assignableRoleIds'] = this.assignableRoleIds;
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
    data['staffUserServiceMappingList'] = this.staffUserServiceMappingList;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
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
