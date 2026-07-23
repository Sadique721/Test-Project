class PaymentOwnerResp {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<PaymentOwnerDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  PaymentOwnerResp(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  PaymentOwnerResp.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <PaymentOwnerDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PaymentOwnerDataList.fromJson(v));
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

class PaymentOwnerDataList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  dynamic username;
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
  List<int>? roles;
  dynamic otp;
  dynamic otpvalidate;
  List<Null>? team;
  bool? isDelete;
  bool? sysstaff;
  String? fullName;
  List<Null>? staffUserChildList;
  dynamic mvnoId;
  dynamic branchId;
  List<Null>? serviceAreaNameList;
  List<Null>? businessUnitNameList;
  dynamic staffUserServiceMappings;
  dynamic totalCollected;
  dynamic totalTransferred;
  dynamic availableAmount;
  dynamic lcoId;
  dynamic hrmsId;
  dynamic profileImage;

  PaymentOwnerDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
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
        this.roles,
        this.otp,
        this.otpvalidate,
        this.team,
        this.isDelete,
        this.sysstaff,
        this.fullName,
        this.staffUserChildList,
        this.mvnoId,
        this.branchId,
        this.serviceAreaNameList,
        this.businessUnitNameList,
        this.staffUserServiceMappings,
        this.totalCollected,
        this.totalTransferred,
        this.availableAmount,
        this.lcoId,
        this.hrmsId,
        this.profileImage});

  PaymentOwnerDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
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
    // if (json['roles'] != null) {
    //   roles = <Null>[];
    //   json['roles'].forEach((v) {
    //     roles!.add(new Null.fromJson(v));
    //   });
    // }
    otp = json['otp'];
    otpvalidate = json['otpvalidate'];
    // if (json['team'] != null) {
    //   team = <Null>[];
    //   json['team'].forEach((v) {
    //     team!.add(Null.fromJson(v));
    //   });
    // }
    isDelete = json['isDelete'];
    sysstaff = json['sysstaff'];
    fullName = json['fullName'];
    // if (json['staffUserChildList'] != null) {
    //   staffUserChildList = <Null>[];
    //   json['staffUserChildList'].forEach((v) {
    //     staffUserChildList!.add(new Null.fromJson(v));
    //   });
    // }
    mvnoId = json['mvnoId'];
    branchId = json['branchId'];
    // if (json['serviceAreaNameList'] != null) {
    //   serviceAreaNameList = <Null>[];
    //   json['serviceAreaNameList'].forEach((v) {
    //     serviceAreaNameList!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <Null>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new Null.fromJson(v));
    //   });
    // }
    staffUserServiceMappings = json['staffUserServiceMappings'];
    totalCollected = json['totalCollected'];
    totalTransferred = json['totalTransferred'];
    availableAmount = json['availableAmount'];
    lcoId = json['lcoId'];
    hrmsId = json['hrmsId'];
    profileImage = json['profileImage'];
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
    // if (this.roles != null) {
    //   data['roles'] = this.roles!.map((v) => v.toJson()).toList();
    // }
    data['otp'] = this.otp;
    data['otpvalidate'] = this.otpvalidate;
    // if (this.team != null) {
    //   data['team'] = this.team!.map((v) => v.toJson()).toList();
    // }
    data['isDelete'] = this.isDelete;
    data['sysstaff'] = this.sysstaff;
    data['fullName'] = this.fullName;
    // if (this.staffUserChildList != null) {
    //   data['staffUserChildList'] =
    //       this.staffUserChildList!.map((v) => v.toJson()).toList();
    // }
    data['mvnoId'] = this.mvnoId;
    data['branchId'] = this.branchId;
    // if (this.serviceAreaNameList != null) {
    //   data['serviceAreaNameList'] =
    //       this.serviceAreaNameList!.map((v) => v.toJson()).toList();
    // }
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
    // }
    data['staffUserServiceMappings'] = this.staffUserServiceMappings;
    data['totalCollected'] = this.totalCollected;
    data['totalTransferred'] = this.totalTransferred;
    data['availableAmount'] = this.availableAmount;
    data['lcoId'] = this.lcoId;
    data['hrmsId'] = this.hrmsId;
    data['profileImage'] = this.profileImage;
    return data;
  }
}
