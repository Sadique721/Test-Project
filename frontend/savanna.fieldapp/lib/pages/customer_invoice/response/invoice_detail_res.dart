import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/webservices/base_response.dart';

class InvoiceDetailRes extends BaseResponse {
  List<InvoiceDetail>? invoicesearchlist;
  PageDetails? pageDetails;

  InvoiceDetailRes({this.invoicesearchlist, timestamp, status, message});

  InvoiceDetailRes.fromJson(Map<String, dynamic> json) {
    if (json['invoicesearchlist'] != null) {
      invoicesearchlist = <InvoiceDetail>[];
      json['invoicesearchlist'].forEach((v) {
        invoicesearchlist!.add(new InvoiceDetail.fromJson(v));
      });
    }
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
    message = json['message'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.invoicesearchlist != null) {
      data['invoicesearchlist'] =
          this.invoicesearchlist!.map((v) => v.toJson()).toList();
    }
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['message'] = this.message;
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