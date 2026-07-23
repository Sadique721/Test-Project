class ActiveStaffUserListRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<StaffUserDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ActiveStaffUserListRes({
    this.responseCode,
    this.responseMessage,
    this.data,
    this.dataList,
    this.excelDataList,
    this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages,
  });

  ActiveStaffUserListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];

    if (json['dataList'] is List) {
      dataList = (json['dataList'] as List)
          .map((e) => StaffUserDataList.fromJson(e))
          .toList();
    }

    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() => {
    'responseCode': responseCode,
    'responseMessage': responseMessage,
    'data': data,
    'dataList': dataList?.map((e) => e.toJson()).toList(),
    'excelDataList': excelDataList,
    'totalRecords': totalRecords,
    'pageRecords': pageRecords,
    'currentPageNumber': currentPageNumber,
    'totalPages': totalPages,
  };
}

class StaffUserDataList {
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
  dynamic regDate;
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

  StaffUserDataList({
    this.id,
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
  });

  StaffUserDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate']?.toString();
    updatedate = json['updatedate']?.toString();

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

    // ✅ Safe parsing of int list
    teamIds = (json['teamIds'] is List)
        ? List<int>.from(json['teamIds'])
        : [];

    // ✅ Safe parsing of string list
    teamNameList = (json['teamNameList'] is List)
        ? List<String>.from(json['teamNameList'])
        : [];

    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];

    servicearea = json['servicearea'];
    businessUnit = json['businessUnit'];
    serviceAreaId = json['serviceAreaId'];

    serviceAreasId = (json['serviceAreasId'] is List)
        ? List<int>.from(json['serviceAreasId'])
        : [];

    businessunitid = json['businessunitid'];

    businessunitids = (json['businessunitids'] is List)
        ? List<int>.from(json['businessunitids'])
        : [];

    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];

    serviceAreaIdsList = (json['serviceAreaIdsList'] is List)
        ? List<int>.from(json['serviceAreaIdsList'])
        : [];

    businessUnitIdsList = (json['businessUnitIdsList'] is List)
        ? List<int>.from(json['businessUnitIdsList'])
        : [];

    serviceAreasNameList = (json['serviceAreasNameList'] is List)
        ? List<String>.from(json['serviceAreasNameList'])
        : [];

    businessUnitNamesList = (json['businessUnitNamesList'] is List)
        ? List<String>.from(json['businessUnitNamesList'])
        : [];

    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];

    branchName = json['branchName'];
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

  Map<String, dynamic> toJson() => {
    'id': id,
    'createdate': createdate,
    'updatedate': updatedate,
    'createdByName': createdByName,
    'lastModifiedByName': lastModifiedByName,
    'createdById': createdById,
    'lastModifiedById': lastModifiedById,
    'username': username,
    'password': password,
    'firstname': firstname,
    'lastname': lastname,
    'email': email,
    'phone': phone,
    'countryCode': countryCode,
    'failcount': failcount,
    'status': status,
    'last_login_time': lastLoginTime,
    'partnerid': partnerid,
    'newpassword': newpassword,
    'teamIds': teamIds,
    'teamNameList': teamNameList,
    'isDelete': isDelete,
    'fullName': fullName,
    'sysstaff': sysstaff,
    'servicearea': servicearea,
    'businessUnit': businessUnit,
    'serviceAreaId': serviceAreaId,
    'serviceAreasId': serviceAreasId,
    'businessunitid': businessunitid,
    'businessunitids': businessunitids,
    'parentStaffId': parentStaffId,
    'mvnoId': mvnoId,
    'serviceAreaIdsList': serviceAreaIdsList,
    'businessUnitIdsList': businessUnitIdsList,
    'serviceAreasNameList': serviceAreasNameList,
    'businessUnitNamesList': businessUnitNamesList,
    'totalCollected': totalCollected,
    'totalTransferred': totalTransferred,
    'availableAmount': availableAmount,
    'lcoId': lcoId,
    'branchName': branchName,
    'regDate': regDate,
    'partnerName': partnerName,
    'updatedatestring': updatedatestring,
    'branchId': branchId,
    'parentstaffname': parentstaffname,
    'hrmsId': hrmsId,
    'profileImage': profileImage,
    'displayId': displayId,
    'displayName': displayName,
    'department': department,
    'identityKey': identityKey,
  };
}
