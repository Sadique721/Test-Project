import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/webservices/base_response.dart';

class ViewLeadSearchRes extends BaseResponse {
  LeadMasterList? leadMasterList;
  String? timestamp;
  int? status;

  ViewLeadSearchRes({this.leadMasterList, this.timestamp, this.status});

  ViewLeadSearchRes.fromJson(Map<String, dynamic> json) {
    leadMasterList = json['leadMasterList'] != null
        ? new LeadMasterList.fromJson(json['leadMasterList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.leadMasterList != null) {
      data['leadMasterList'] = this.leadMasterList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class LeadMasterList {
  List<LeadMasterListData>? content;
  Pageable? pageable;
  bool? last;
  int? totalPages;
  int? totalElements;
  bool? first;
  Sort? sort;
  int? numberOfElements;
  int? size;
  int? number;
  bool? empty;

  LeadMasterList(
      {this.content,
        this.pageable,
        this.last,
        this.totalPages,
        this.totalElements,
        this.first,
        this.sort,
        this.numberOfElements,
        this.size,
        this.number,
        this.empty});

  LeadMasterList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <LeadMasterListData>[];
      json['content'].forEach((v) {
        content!.add(new LeadMasterListData.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? Pageable.fromJson(json['pageable'])
        : null;
    last = json['last'];
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    first = json['first'];
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    numberOfElements = json['numberOfElements'];
    size = json['size'];
    number = json['number'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.content != null) {
      data['content'] = this.content!.map((v) => v.toJson()).toList();
    }
    if (this.pageable != null) {
      data['pageable'] = this.pageable!.toJson();
    }
    data['last'] = this.last;
    data['totalPages'] = this.totalPages;
    data['totalElements'] = this.totalElements;
    data['first'] = this.first;
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['numberOfElements'] = this.numberOfElements;
    data['size'] = this.size;
    data['number'] = this.number;
    data['empty'] = this.empty;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? pageSize;
  int? pageNumber;
  int? offset;
  bool? paged;
  bool? unpaged;

  Pageable(
      {this.sort,
        this.pageSize,
        this.pageNumber,
        this.offset,
        this.paged,
        this.unpaged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    pageSize = json['pageSize'];
    pageNumber = json['pageNumber'];
    offset = json['offset'];
    paged = json['paged'];
    unpaged = json['unpaged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['pageSize'] = this.pageSize;
    data['pageNumber'] = this.pageNumber;
    data['offset'] = this.offset;
    data['paged'] = this.paged;
    data['unpaged'] = this.unpaged;
    return data;
  }
}

class Sort {
  bool? unsorted;
  bool? sorted;
  bool? empty;

  Sort({this.unsorted, this.sorted, this.empty});

  Sort.fromJson(Map<String, dynamic> json) {
    unsorted = json['unsorted'];
    sorted = json['sorted'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['unsorted'] = this.unsorted;
    data['sorted'] = this.sorted;
    data['empty'] = this.empty;
    return data;
  }
}
