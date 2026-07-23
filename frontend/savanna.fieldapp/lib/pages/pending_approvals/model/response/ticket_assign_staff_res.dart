import 'package:savbill/webservices/base_response.dart';

class TicketAssignStaffRes extends BaseResponse {
  List<TicketAssignStaff>? dataList;

  TicketAssignStaffRes({responseCode, responseMessage, this.dataList});

  TicketAssignStaffRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TicketAssignStaff>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TicketAssignStaff.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class TicketAssignStaff {
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
  int? displayId;
  String? displayName;
  String? fullName;
  bool? isDelete;
  int? mvnoId;
  String? partnerName;
  int? partnerid;
  bool? selected;




  TicketAssignStaff(
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
        this.displayId,
        this.displayName,
        this.fullName,
        this.isDelete,
        this.mvnoId,
        this.partnerName,
        this.partnerid,
      this.selected
      });

  TicketAssignStaff.fromJson(Map<String, dynamic> json) {
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
    displayId = json['displayId'];
    displayName = json['displayName'];
    fullName = json['fullName'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    partnerName = json['partnerName'];
    partnerid = json['partnerid'];
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
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['partnerName'] = this.partnerName;
    data['partnerid'] = this.partnerid;
    return data;
  }
}
