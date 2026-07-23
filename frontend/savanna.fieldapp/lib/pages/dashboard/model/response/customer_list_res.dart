import 'package:savbill/webservices/base_response.dart';

class CustomerListRes {
  PageDetails? pageDetails;
  List<CustListDetails>? customerList;
  String? timestamp;
  int? status;

  CustomerListRes(
      {this.pageDetails, this.customerList, this.timestamp, this.status});

  CustomerListRes.fromJson(Map<String, dynamic> json) {
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['customerList'] != null) {
      customerList = <CustListDetails>[];
      json['customerList'].forEach((v) {
        customerList!.add(new CustListDetails.fromJson(v));
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

class CustListDetails {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  // NetworkDetails? networkDetails;
  String? acctno;
  double? outstanding;
  Null? previousCafApprover;
  int? nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  String? leadNo;
  int? leadId;
  Null? nextTeamHierarchyMapping;
  // ServiceArea? serviceArea;
  // List<CustAddressList>? custAddressList;
  String? customerAddress;
  Null? currentAssigneeParentId;
  dynamic connectionMode;

  CustListDetails(
      {this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        // this.networkDetails,
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
        // this.serviceArea,
        // this.custAddressList,
        this.customerAddress,
        this.currentAssigneeParentId,
        this.connectionMode});

  CustListDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    // networkDetails = json['networkDetails'] != null
    //     ? new NetworkDetails.fromJson(json['networkDetails'])
    //     : null;
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
    // serviceArea = json['serviceArea'] != null
    //     ? new ServiceArea.fromJson(json['serviceArea'])
    //     : null;
    // if (json['custAddressList'] != null) {
    //   custAddressList = <CustAddressList>[];
    //   json['custAddressList'].forEach((v) {
    //     custAddressList!.add(new CustAddressList.fromJson(v));
    //   });
    // }
    customerAddress = json['customerAddress'];
    currentAssigneeParentId = json['currentAssigneeParentId'];
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
    // if (this.networkDetails != null) {
    //   data['networkDetails'] = this.networkDetails!.toJson();
    // }
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
    // if (this.serviceArea != null) {
    //   data['serviceArea'] = this.serviceArea!.toJson();
    // }
    // if (this.custAddressList != null) {
    //   data['custAddressList'] =
    //       this.custAddressList!.map((v) => v.toJson()).toList();
    // }
    data['customerAddress'] = this.customerAddress;
    data['currentAssigneeParentId'] = this.currentAssigneeParentId;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}
