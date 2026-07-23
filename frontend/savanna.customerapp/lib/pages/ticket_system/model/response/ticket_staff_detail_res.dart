import 'package:savbill/webservices/base_response.dart';

class TicketStaffDetailRes extends BaseResponse {
  TicketStaffDetail? staff;

  TicketStaffDetailRes({timestamp, status, this.staff});

  TicketStaffDetailRes.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    status = json['status'];
    staff = json['Staff'] != null
        ? new TicketStaffDetail.fromJson(json['Staff'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    if (this.staff != null) {
      data['Staff'] = this.staff!.toJson();
    }
    return data;
  }
}

class TicketStaffDetail {
  int? id;
  String? createdate;
  String? updatedate;
  String? username;
  String? firstname;
  String? lastname;
  String? email;
  String? phone;
  String? countryCode;
  String? status;
  int? partnerid;
  bool? isDelete;
  String? fullName;
  bool? sysstaff;
  int? serviceAreaId;
  int? parentStaffId;
  int? mvnoId;
  List<String>? serviceAreasNameList;
  List<String>? roleName;
  String? regDate;
  String? partnerName;
  String? parentstaffname;

  TicketStaffDetail(
      {this.id,
      this.createdate,
      this.updatedate,
      this.username,
      this.firstname,
      this.lastname,
      this.email,
      this.phone,
      this.countryCode,
      this.status,
      this.partnerid,
      this.isDelete,
      this.fullName,
      this.sysstaff,
      this.serviceAreaId,
      this.parentStaffId,
      this.mvnoId,
      this.serviceAreasNameList,
      this.roleName,
      this.regDate,
      this.partnerName,
      this.parentstaffname});

  TicketStaffDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    username = json['username'];
    firstname = json['firstname'];
    lastname = json['lastname'];
    email = json['email'];
    phone = json['phone'];
    countryCode = json['countryCode'];
    status = json['status'];
    partnerid = json['partnerid'];
    isDelete = json['isDelete'];
    fullName = json['fullName'];
    sysstaff = json['sysstaff'];
    serviceAreaId = json['serviceAreaId'];
    parentStaffId = json['parentStaffId'];
    mvnoId = json['mvnoId'];
    serviceAreasNameList = json['serviceAreasNameList'].cast<String>();
    roleName = json['roleName'].cast<String>();
    regDate = json['regDate'];
    partnerName = json['partnerName'];
    parentstaffname = json['parentstaffname'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['username'] = this.username;
    data['firstname'] = this.firstname;
    data['lastname'] = this.lastname;
    data['email'] = this.email;
    data['phone'] = this.phone;
    data['countryCode'] = this.countryCode;
    data['status'] = this.status;
    data['partnerid'] = this.partnerid;
    data['isDelete'] = this.isDelete;
    data['fullName'] = this.fullName;
    data['sysstaff'] = this.sysstaff;
    data['serviceAreaId'] = this.serviceAreaId;
    data['parentStaffId'] = this.parentStaffId;
    data['mvnoId'] = this.mvnoId;
    data['serviceAreasNameList'] = this.serviceAreasNameList;
    data['roleName'] = this.roleName;
    data['regDate'] = this.regDate;
    data['partnerName'] = this.partnerName;
    data['parentstaffname'] = this.parentstaffname;
    return data;
  }
}
