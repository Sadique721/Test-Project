import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/webservices/base_response.dart';

class CustomerListResponse extends BaseResponse {
  PageDetails? pageDetails;
  List<CustomerDetail>? customerList;

  CustomerListResponse(
      {timestamp, status, message, this.pageDetails, this.customerList});

  CustomerListResponse.fromJson(Map<String, dynamic> json) {
    timestamp = json['timestamp'];
    message = json['message'];
    status = json['status'];
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['customerList'] != null) {
      customerList = <CustomerDetail>[];
      json['customerList'].forEach((v) {
        customerList!.add(new CustomerDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['message'] = this.message;
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    if (this.customerList != null) {
      data['customerList'] = this.customerList!.map((v) => v.toJson()).toList();
    }
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

