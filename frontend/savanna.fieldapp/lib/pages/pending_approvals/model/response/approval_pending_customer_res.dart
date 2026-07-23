import 'package:savbill/webservices/base_response.dart';

class ApprovalPendingCustomerRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ApprovalPendingCustomer>? dataList;

  ApprovalPendingCustomerRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ApprovalPendingCustomerRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ApprovalPendingCustomer>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApprovalPendingCustomer.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ApprovalPendingCustomer {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? username;
  String? password;
  String? firstname;
  String? lastname;
  String? email;
  String? title;
  String? custname;
  String? contactperson;
  String? status;
  String? acctno;
  String? custtype;
  String? phone;
  int? partnerid;
  dynamic onuid;
  String? nextBillDate;
  dynamic lastBillDate;
  dynamic addresstype;
  dynamic address1;
  dynamic address2;
  String? selfcarepwd;
  String? mobile;
  String? countryCode;
  String? cafno;
  dynamic altmobile;
  dynamic altphone;
  dynamic altemail;
  int? serviceareaid;
  String? serviceareaName;
  int? mvnoId;
  dynamic plangroupid;


  ApprovalPendingCustomer(
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
      this.title,
      this.custname,
      this.contactperson,
      this.status,
      this.acctno,
      this.custtype,
      this.phone,
      this.partnerid,
      this.onuid,
      this.nextBillDate,
      this.lastBillDate,
      this.addresstype,
      this.address1,
      this.address2,
      this.selfcarepwd,
      this.mobile,
      this.countryCode,
      this.cafno,
      this.altmobile,
      this.altphone,
      this.altemail,
      this.serviceareaid,
      this.serviceareaName,
      this.mvnoId,
      this.plangroupid});

  ApprovalPendingCustomer.fromJson(Map<String, dynamic> json) {
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
    title = json['title'];
    custname = json['custname'];
    contactperson = json['contactperson'];
    status = json['status'];
    acctno = json['acctno'];
    custtype = json['custtype'];
    phone = json['phone'];
    partnerid = json['partnerid'];
    onuid = json['onuid'];
    nextBillDate = json['nextBillDate'];
    lastBillDate = json['lastBillDate'];
    addresstype = json['addresstype'];
    address1 = json['address1'];
    address2 = json['address2'];
    selfcarepwd = json['selfcarepwd'];
    mobile = json['mobile'];
    countryCode = json['countryCode'];
    cafno = json['cafno'];
    altmobile = json['altmobile'];
    altphone = json['altphone'];
    altemail = json['altemail'];
    serviceareaid = json['serviceareaid'];
    serviceareaName = json['serviceareaName'];
    mvnoId = json['mvnoId'];
    plangroupid = json['plangroupid'];
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
    data['title'] = this.title;
    data['custname'] = this.custname;
    data['contactperson'] = this.contactperson;
    data['status'] = this.status;
    data['acctno'] = this.acctno;
    data['custtype'] = this.custtype;
    data['phone'] = this.phone;
    data['partnerid'] = this.partnerid;
    data['onuid'] = this.onuid;
    data['nextBillDate'] = this.nextBillDate;
    data['lastBillDate'] = this.lastBillDate;
    data['addresstype'] = this.addresstype;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['selfcarepwd'] = this.selfcarepwd;
    data['mobile'] = this.mobile;
    data['countryCode'] = this.countryCode;
    data['cafno'] = this.cafno;
    data['altmobile'] = this.altmobile;
    data['altphone'] = this.altphone;
    data['altemail'] = this.altemail;
    data['serviceareaid'] = this.serviceareaid;
    data['serviceareaName'] = this.serviceareaName;
    data['mvnoId'] = this.mvnoId;
    data['plangroupid'] = this.plangroupid;
    return data;
  }
}
