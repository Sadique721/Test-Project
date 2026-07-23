import 'package:savbill/webservices/base_response.dart';

class DocApproveRejectAssignStaffRes extends BaseResponse {
  DocApproveRejectAssignStaffData? data;
  List<DocApproveRejectAssignStaffDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  DocApproveRejectAssignStaffRes(
      {this.data,
      this.dataList,
      this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  DocApproveRejectAssignStaffRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null
        ? new DocApproveRejectAssignStaffData.fromJson(json['data'])
        : null;
    if (json['dataList'] != null) {
      dataList = <DocApproveRejectAssignStaffDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(DocApproveRejectAssignStaffDataList.fromJson(v));
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
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
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

class DocApproveRejectAssignStaffData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? docId;
  int? custId;
  String? docType;
  String? docSubType;
  String? remark;
  String? mode;
  String? docStatus;
  String? filename;
  String? uniquename;
  bool? isDelete;
  dynamic documentNumber;
  String? startDate;
  String? endDate;
  dynamic nextTeamHierarchyMappingId;
  int? nextStaff;
  dynamic mvnoId;
  dynamic leadId;
  dynamic startDateAsString;
  dynamic endDateAsString;

  DocApproveRejectAssignStaffData(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.docId,
      this.custId,
      this.docType,
      this.docSubType,
      this.remark,
      this.mode,
      this.docStatus,
      this.filename,
      this.uniquename,
      this.isDelete,
      this.documentNumber,
      this.startDate,
      this.endDate,
      this.nextTeamHierarchyMappingId,
      this.nextStaff,
      this.mvnoId,
      this.leadId,
      this.startDateAsString,
      this.endDateAsString});

  DocApproveRejectAssignStaffData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    docId = json['docId'];
    custId = json['custId'];
    docType = json['docType'];
    docSubType = json['docSubType'];
    remark = json['remark'];
    mode = json['mode'];
    docStatus = json['docStatus'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    isDelete = json['isDelete'];
    documentNumber = json['documentNumber'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    mvnoId = json['mvnoId'];
    leadId = json['leadId'];
    startDateAsString = json['startDateAsString'];
    endDateAsString = json['endDateAsString'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['docId'] = this.docId;
    data['custId'] = this.custId;
    data['docType'] = this.docType;
    data['docSubType'] = this.docSubType;
    data['remark'] = this.remark;
    data['mode'] = this.mode;
    data['docStatus'] = this.docStatus;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['isDelete'] = this.isDelete;
    data['documentNumber'] = this.documentNumber;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['mvnoId'] = this.mvnoId;
    data['leadId'] = this.leadId;
    data['startDateAsString'] = this.startDateAsString;
    data['endDateAsString'] = this.endDateAsString;
    return data;
  }
}

class DocApproveRejectAssignStaffDataList {
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

  Null? businessunitid;

  // List<Null>? businessunitids;
  int? parentStaffId;
  int? mvnoId;

  // Null? staffUserServiceMappingList;
  // Null? serviceAreaIdsList;
  // Null? businessUnitIdsList;
  // List<ServiceAreaNameList>? serviceAreaNameList;

  // List<Null>? serviceAreasNameList;
  // List<BusinessUnitNameList>? businessUnitNameList;

  // List<Null>? businessUnitNamesList;
  // Null? totalCollected;
  // Null? totalTransferred;
  // Null? availableAmount;
  // Null? lcoId;
  // Null? tacacsAccessLevelGroup;
  // Null? branchName;
  List<String>? roleName;
  String? regDate;
  String? partnerName;
  String? updatedatestring;
  int? branchId;
  Null? parentstaffname;
  Null? hrmsId;
  Null? profileImage;
  int? displayId;
  String? displayName;
  Null? department;
  bool? selected;

  DocApproveRejectAssignStaffDataList(
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
      // this.servicearea,
      // this.businessUnit,
      // this.serviceAreaId,
      // this.serviceAreasId,
      this.businessunitid,
      // this.businessunitids,
      this.parentStaffId,
      this.mvnoId,
      // this.staffUserServiceMappingList,
      // this.serviceAreaIdsList,
      // this.businessUnitIdsList,
      // this.serviceAreaNameList,
      // this.serviceAreasNameList,
      // this.businessUnitNameList,
      // this.businessUnitNamesList,
      // this.totalCollected,
      // this.totalTransferred,
      // this.availableAmount,
      // this.lcoId,
      // this.tacacsAccessLevelGroup,
      // this.branchName,
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

  DocApproveRejectAssignStaffDataList.fromJson(Map<String, dynamic> json) {
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
    // servicearea = json['servicearea'];
    // businessUnit = json['businessUnit'];
    // serviceAreaId = json['serviceAreaId'];
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
    // staffUserServiceMappingList = json['staffUserServiceMappingList'];
    // serviceAreaIdsList = json['serviceAreaIdsList'];
    // businessUnitIdsList = json['businessUnitIdsList'];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <ServiceAreaNameList>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new ServiceAreaNameList.fromJson(v));
    //   });
    // }
    // if (json['serviceAreasNameList'] != null) {
    //   serviceAreasNameList = <Null>[];
    //   json['serviceAreasNameList'].forEach((v) {
    //     serviceAreasNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <BusinessUnitNameList>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new BusinessUnitNameList.fromJson(v));
    //   });
    // }
    // if (json['businessUnitNamesList'] != null) {
    //   businessUnitNamesList = <Null>[];
    //   json['businessUnitNamesList'].forEach((v) {
    //     businessUnitNamesList!.add(new Null.fromJson(v));
    //   });
    // }
    // totalCollected = json['totalCollected'];
    // totalTransferred = json['totalTransferred'];
    // availableAmount = json['availableAmount'];
    // lcoId = json['lcoId'];
    // tacacsAccessLevelGroup = json['tacacsAccessLevelGroup'];
    // branchName = json['branchName'];
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
    // if (this.teamIds != null) {
    //   data['teamIds'] = this.teamIds!.map((v) => v.toJson()).toList();
    // }
    // if (this.teamNameList != null) {
    //   data['teamNameList'] = this.teamNameList!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    // data['servicearea'] = this.servicearea;
    // data['businessUnit'] = this.businessUnit;
    // data['serviceAreaId'] = this.serviceAreaId;
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
    // data['staffUserServiceMappingList'] = this.staffUserServiceMappingList;
    // data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    // data['businessUnitIdsList'] = this.businessUnitIdsList;
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
    // data['totalCollected'] = this.totalCollected;
    // data['totalTransferred'] = this.totalTransferred;
    // data['availableAmount'] = this.availableAmount;
    // data['lcoId'] = this.lcoId;
    // data['tacacsAccessLevelGroup'] = this.tacacsAccessLevelGroup;
    // data['branchName'] = this.branchName;
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

// class ServiceAreaNameList {
//   int? id;
//   String? createdate;
//   String? updatedate;
//   String? createdByName;
//   String? lastModifiedByName;
//   int? createdById;
//   Null? lastModifiedById;
//   String? name;
//   String? status;
//   bool? isDeleted;
//
//   // List<Null>? networkDevicesList;
//   int? mvnoId;
//   String? latitude;
//   String? longitude;
//   Null? areaId;
//   List<PincodeList>? pincodeList;
//   int? cityid;
//
//   ServiceAreaNameList(
//       {this.id,
//       this.createdate,
//       this.updatedate,
//       this.createdByName,
//       this.lastModifiedByName,
//       this.createdById,
//       this.lastModifiedById,
//       this.name,
//       this.status,
//       this.isDeleted,
//       // this.networkDevicesList,
//       this.mvnoId,
//       this.latitude,
//       this.longitude,
//       this.areaId,
//       this.pincodeList,
//       this.cityid});
//
//   ServiceAreaNameList.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     name = json['name'];
//     status = json['status'];
//     isDeleted = json['isDeleted'];
//     // if (json['networkDevicesList'] != null) {
//     //   networkDevicesList = <Null>[];
//     //   json['networkDevicesList'].forEach((v) {
//     //     networkDevicesList!.add(new Null.fromJson(v));
//     //   });
//     // }
//     mvnoId = json['mvnoId'];
//     latitude = json['latitude'];
//     longitude = json['longitude'];
//     areaId = json['areaId'];
//     if (json['pincodeList'] != null) {
//       pincodeList = <PincodeList>[];
//       json['pincodeList'].forEach((v) {
//         pincodeList!.add(new PincodeList.fromJson(v));
//       });
//     }
//     cityid = json['cityid'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['name'] = this.name;
//     data['status'] = this.status;
//     data['isDeleted'] = this.isDeleted;
//     // if (this.networkDevicesList != null) {
//     //   data['networkDevicesList'] =
//     //       this.networkDevicesList!.map((v) => v.toJson()).toList();
//     // }
//     data['mvnoId'] = this.mvnoId;
//     data['latitude'] = this.latitude;
//     data['longitude'] = this.longitude;
//     data['areaId'] = this.areaId;
//     if (this.pincodeList != null) {
//       data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
//     }
//     data['cityid'] = this.cityid;
//     return data;
//   }
// }
//
// class PincodeList {
//   int? id;
//   String? pincode;
//   String? status;
//   bool? isDeleted;
//   int? countryId;
//   int? cityId;
//   int? stateId;
//   List<AreaList>? areaList;
//   int? mvnoId;
//   String? createdate;
//   String? updatedate;
//   int? createdById;
//   int? lastModifiedById;
//   String? createdByName;
//   String? lastModifiedByName;
//
//   PincodeList(
//       {this.id,
//       this.pincode,
//       this.status,
//       this.isDeleted,
//       this.countryId,
//       this.cityId,
//       this.stateId,
//       this.areaList,
//       this.mvnoId,
//       this.createdate,
//       this.updatedate,
//       this.createdById,
//       this.lastModifiedById,
//       this.createdByName,
//       this.lastModifiedByName});
//
//   PincodeList.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     pincode = json['pincode'];
//     status = json['status'];
//     isDeleted = json['isDeleted'];
//     countryId = json['countryId'];
//     cityId = json['cityId'];
//     stateId = json['stateId'];
//     if (json['areaList'] != null) {
//       areaList = <AreaList>[];
//       json['areaList'].forEach((v) {
//         areaList!.add(new AreaList.fromJson(v));
//       });
//     }
//     mvnoId = json['mvnoId'];
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['pincode'] = this.pincode;
//     data['status'] = this.status;
//     data['isDeleted'] = this.isDeleted;
//     data['countryId'] = this.countryId;
//     data['cityId'] = this.cityId;
//     data['stateId'] = this.stateId;
//     if (this.areaList != null) {
//       data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
//     }
//     data['mvnoId'] = this.mvnoId;
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     return data;
//   }
// }
//
// class AreaList {
//   int? id;
//   String? name;
//   String? status;
//   bool? isDeleted;
//   int? countryId;
//   int? cityId;
//   int? stateId;
//   int? mvnoId;
//   String? createdate;
//   String? updatedate;
//   int? createdById;
//   int? lastModifiedById;
//   String? createdByName;
//   String? lastModifiedByName;
//   int? primaryKey;
//   bool? deleteFlag;
//
//   AreaList(
//       {this.id,
//       this.name,
//       this.status,
//       this.isDeleted,
//       this.countryId,
//       this.cityId,
//       this.stateId,
//       this.mvnoId,
//       this.createdate,
//       this.updatedate,
//       this.createdById,
//       this.lastModifiedById,
//       this.createdByName,
//       this.lastModifiedByName,
//       this.primaryKey,
//       this.deleteFlag});
//
//   AreaList.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     name = json['name'];
//     status = json['status'];
//     isDeleted = json['isDeleted'];
//     countryId = json['countryId'];
//     cityId = json['cityId'];
//     stateId = json['stateId'];
//     mvnoId = json['mvnoId'];
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     primaryKey = json['primaryKey'];
//     deleteFlag = json['deleteFlag'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['name'] = this.name;
//     data['status'] = this.status;
//     data['isDeleted'] = this.isDeleted;
//     data['countryId'] = this.countryId;
//     data['cityId'] = this.cityId;
//     data['stateId'] = this.stateId;
//     data['mvnoId'] = this.mvnoId;
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['primaryKey'] = this.primaryKey;
//     data['deleteFlag'] = this.deleteFlag;
//     return data;
//   }
// }
//
// class BusinessUnitNameList {
//   int? id;
//   String? buname;
//   String? bucode;
//   String? status;
//   String? planBindingType;
//   bool? isDeleted;
//   int? mvnoId;
//
//   // List<Null>? investmentCodeid;
//   String? createdate;
//   String? updatedate;
//   int? createdById;
//   int? lastModifiedById;
//   String? createdByName;
//   String? lastModifiedByName;
//   int? primaryKey;
//   bool? deleteFlag;
//
//   BusinessUnitNameList(
//       {this.id,
//       this.buname,
//       this.bucode,
//       this.status,
//       this.planBindingType,
//       this.isDeleted,
//       this.mvnoId,
//       // this.investmentCodeid,
//       this.createdate,
//       this.updatedate,
//       this.createdById,
//       this.lastModifiedById,
//       this.createdByName,
//       this.lastModifiedByName,
//       this.primaryKey,
//       this.deleteFlag});
//
//   BusinessUnitNameList.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     buname = json['buname'];
//     bucode = json['bucode'];
//     status = json['status'];
//     planBindingType = json['planBindingType'];
//     isDeleted = json['isDeleted'];
//     mvnoId = json['mvnoId'];
//     // if (json['investmentCodeid'] != null) {
//     //   investmentCodeid = <Null>[];
//     //   json['investmentCodeid'].forEach((v) {
//     //     investmentCodeid!.add(new Null.fromJson(v));
//     //   });
//     // }
//     createdate = json['createdate'];
//     updatedate = json['updatedate'];
//     createdById = json['createdById'];
//     lastModifiedById = json['lastModifiedById'];
//     createdByName = json['createdByName'];
//     lastModifiedByName = json['lastModifiedByName'];
//     primaryKey = json['primaryKey'];
//     deleteFlag = json['deleteFlag'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['buname'] = this.buname;
//     data['bucode'] = this.bucode;
//     data['status'] = this.status;
//     data['planBindingType'] = this.planBindingType;
//     data['isDeleted'] = this.isDeleted;
//     data['mvnoId'] = this.mvnoId;
//     // if (this.investmentCodeid != null) {
//     //   data['investmentCodeid'] =
//     //       this.investmentCodeid!.map((v) => v.toJson()).toList();
//     // }
//     data['createdate'] = this.createdate;
//     data['updatedate'] = this.updatedate;
//     data['createdById'] = this.createdById;
//     data['lastModifiedById'] = this.lastModifiedById;
//     data['createdByName'] = this.createdByName;
//     data['lastModifiedByName'] = this.lastModifiedByName;
//     data['primaryKey'] = this.primaryKey;
//     data['deleteFlag'] = this.deleteFlag;
//     return data;
//   }
// }
