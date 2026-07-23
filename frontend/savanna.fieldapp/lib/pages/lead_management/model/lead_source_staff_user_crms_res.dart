import 'package:savbill/webservices/base_response.dart';

class LeadSourceStaffUserCRMRes  extends BaseResponse{
  List<StaffUserList>? staffUserList;
  String? timestamp;
  int? status;

  LeadSourceStaffUserCRMRes({this.staffUserList});

  LeadSourceStaffUserCRMRes.fromJson(Map<String, dynamic> json) {
    if (json['staffUserList'] != null) {
      staffUserList = <StaffUserList>[];
      json['staffUserList'].forEach((v) {
        staffUserList!.add(new StaffUserList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.staffUserList != null) {
      data['staffUserList'] =
          this.staffUserList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class StaffUserList {
  int? id;
  String? username;
  String? password;
  String? firstname;
  String? lastname;
  String? email;
  String? phone;
  int? failcount;
  String? status;
  dynamic countryCode;
  dynamic lastLoginTime;
  String? createdate;
  String? updatedate;
  int? partnerid;
  // List<Null>? roles;
  // List<Null>? businessUnitNameList;
  dynamic otp;
  dynamic otpvalidate;
  bool? isDelete;
  bool? sysstaff;
  dynamic serviceareaId;
  dynamic businessunitid;
  dynamic staffUserparentId;
  int? mvnoId;
  dynamic branchId;

  StaffUserList(
      {this.id,
        this.username,
        this.password,
        this.firstname,
        this.lastname,
        this.email,
        this.phone,
        this.failcount,
        this.status,
        this.countryCode,
        this.lastLoginTime,
        this.createdate,
        this.updatedate,
        this.partnerid,
        // this.roles,
        // this.businessUnitNameList,
        this.otp,
        this.otpvalidate,
        this.isDelete,
        this.sysstaff,
        this.serviceareaId,
        this.businessunitid,
        this.staffUserparentId,
        this.mvnoId,
        this.branchId});

  StaffUserList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    username = json['username'];
    password = json['password'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    email = json['email'];
    phone = json['phone'];
    failcount = json['failcount'];
    status = json['status'];
    countryCode = json['countryCode'];
    lastLoginTime = json['last_login_time'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    partnerid = json['partnerid'];
    // if (json['roles'] != null) {
    //   roles = <Null>[];
    //   json['roles'].forEach((v) {
    //     roles!.add(new Null.fromJson(v));
    //   });
    // }
    // if (json['businessUnitNameList'] != null) {
    //   businessUnitNameList = <Null>[];
    //   json['businessUnitNameList'].forEach((v) {
    //     businessUnitNameList!.add(new Null.fromJson(v));
    //   });
    // }
    otp = json['otp'];
    otpvalidate = json['otpvalidate'];
    isDelete = json['isDelete'];
    sysstaff = json['sysstaff'];
    serviceareaId = json['serviceareaId'];
    businessunitid = json['businessunitid'];
    staffUserparentId = json['staffUserparentId'];
    mvnoId = json['mvnoId'];
    branchId = json['branchId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['username'] = this.username;
    data['password'] = this.password;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['email'] = this.email;
    data['phone'] = this.phone;
    data['failcount'] = this.failcount;
    data['status'] = this.status;
    data['countryCode'] = this.countryCode;
    data['last_login_time'] = this.lastLoginTime;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['partnerid'] = this.partnerid;
    // if (this.roles != null) {
    //   data['roles'] = this.roles!.map((v) => v.toJson()).toList();
    // }
    // if (this.businessUnitNameList != null) {
    //   data['businessUnitNameList'] =
    //       this.businessUnitNameList!.map((v) => v.toJson()).toList();
    // }
    data['otp'] = this.otp;
    data['otpvalidate'] = this.otpvalidate;
    data['isDelete'] = this.isDelete;
    data['sysstaff'] = this.sysstaff;
    data['serviceareaId'] = this.serviceareaId;
    data['businessunitid'] = this.businessunitid;
    data['staffUserparentId'] = this.staffUserparentId;
    data['mvnoId'] = this.mvnoId;
    data['branchId'] = this.branchId;
    return data;
  }
}
