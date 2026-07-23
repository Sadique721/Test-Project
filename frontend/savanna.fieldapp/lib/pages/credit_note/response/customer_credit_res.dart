import '../../../webservices/base_response.dart';

class CustomerCreditNoteRes extends BaseResponse{
  PageDetails? pageDetails;
  List<CustomerCreditList>? customerList;
  String? timestamp;
  int? status;

  CustomerCreditNoteRes(
      {this.pageDetails, this.customerList, this.timestamp, this.status});

  CustomerCreditNoteRes.fromJson(Map<String, dynamic> json) {
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['customerList'] != null) {
      customerList = <CustomerCreditList>[];
      json['customerList'].forEach((v) {
        customerList!.add(new CustomerCreditList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    if (this.customerList != null) {
      data['customerList'] = this.customerList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PageDetails {
  int? totalPages;
  int? totalRecords;
  int? totalRecordsPerPage;
  int? currentPageNumber;

  PageDetails(
      {this.totalPages,
        this.totalRecords,
        this.totalRecordsPerPage,
        this.currentPageNumber});

  PageDetails.fromJson(Map<String, dynamic> json) {
    totalPages = json['totalPages'];
    totalRecords = json['totalRecords'];
    totalRecordsPerPage = json['totalRecordsPerPage'];
    currentPageNumber = json['currentPageNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['totalPages'] = this.totalPages;
    data['totalRecords'] = this.totalRecords;
    data['totalRecordsPerPage'] = this.totalRecordsPerPage;
    data['currentPageNumber'] = this.currentPageNumber;
    return data;
  }
}

class CustomerCreditList {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? acctno;
  double? outstanding;
  dynamic previousCafApprover;
  int? nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  dynamic leadNo;
  dynamic leadId;
  dynamic nextTeamHierarchyMapping;
  dynamic connectionMode;

  CustomerCreditList(
      {this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        this.networkDetails,
        this.acctno,
        this.outstanding,
        this.previousCafApprover,
        this.nextCafApprover,
        this.status,
        this.custtype,
        this.calendarType,
        this.isinvoicestop,
        this.istrialplan,
        this.leadNo,
        this.leadId,
        this.nextTeamHierarchyMapping,
        this.connectionMode});

  CustomerCreditList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    acctno = json['acctno'];
    outstanding = json['outstanding'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    status = json['status'];
    custtype = json['custtype'];
    calendarType = json['calendarType'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    connectionMode = json['connectionMode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['mobile'] = this.mobile;
    data['email'] = this.email;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['acctno'] = this.acctno;
    data['outstanding'] = this.outstanding;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['status'] = this.status;
    data['custtype'] = this.custtype;
    data['calendarType'] = this.calendarType;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}

class NetworkDetails {
  dynamic  networkdeviceid;
  dynamic serviceareaid;
  dynamic  slotid;
  dynamic  portid;
  String? networkdevicename;
  String? serviceareaname;
  String? slotname;
  String? portname;

  NetworkDetails(
      {this.networkdeviceid,
        this.serviceareaid,
        this.slotid,
        this.portid,
        this.networkdevicename,
        this.serviceareaname,
        this.slotname,
        this.portname});

  NetworkDetails.fromJson(Map<String, dynamic> json) {
    networkdeviceid = json['networkdeviceid'];
    serviceareaid = json['serviceareaid'];
    slotid = json['slotid'];
    portid = json['portid'];
    networkdevicename = json['networkdevicename'];
    serviceareaname = json['serviceareaname'];
    slotname = json['slotname'];
    portname = json['portname'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['networkdeviceid'] = this.networkdeviceid;
    data['serviceareaid'] = this.serviceareaid;
    data['slotid'] = this.slotid;
    data['portid'] = this.portid;
    data['networkdevicename'] = this.networkdevicename;
    data['serviceareaname'] = this.serviceareaname;
    data['slotname'] = this.slotname;
    data['portname'] = this.portname;
    return data;
  }
}
