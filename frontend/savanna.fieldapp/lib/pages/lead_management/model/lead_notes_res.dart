import 'package:savbill/webservices/base_response.dart';

class LeadNotesRes extends BaseResponse{
  LeadNoteList? leadNoteList;
  String? timestamp;
  int? status;

  LeadNotesRes({this.leadNoteList, this.timestamp, this.status});

  LeadNotesRes.fromJson(Map<String, dynamic> json) {
    leadNoteList = json['leadNoteList'] != null
        ? LeadNoteList.fromJson(json['leadNoteList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.leadNoteList != null) {
      data['leadNoteList'] = this.leadNoteList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class LeadNoteList {
  List<LeadNotesContent>? content;
  Pageable? pageable;
  bool? last;
  int? totalPages;
  int? totalElements;
  bool? first;
  Sort? sort;
  int? size;
  int? number;
  int? numberOfElements;
  bool? empty;

  LeadNoteList(
      {this.content,
        this.pageable,
        this.last,
        this.totalPages,
        this.totalElements,
        this.first,
        this.sort,
        this.size,
        this.number,
        this.numberOfElements,
        this.empty});

  LeadNoteList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <LeadNotesContent>[];
      json['content'].forEach((v) {
        content!.add(new LeadNotesContent.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    last = json['last'];
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    first = json['first'];
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    size = json['size'];
    number = json['number'];
    numberOfElements = json['numberOfElements'];
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
    data['size'] = this.size;
    data['number'] = this.number;
    data['numberOfElements'] = this.numberOfElements;
    data['empty'] = this.empty;
    return data;
  }
}

class LeadNotesContent {
  int? id;
  String? notes;
  int? leadMasterId;
  String? createdOn;
  String? createdBy;
  String? createdByName;

  LeadNotesContent(
      {this.id,
        this.notes,
        this.leadMasterId,
        this.createdOn,
        this.createdBy,
        this.createdByName});

  LeadNotesContent.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    notes = json['notes'];
    leadMasterId = json['leadMasterId'];
    createdOn = json['createdOn'];
    createdBy = json['createdBy'];
    createdByName = json['createdByName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['notes'] = this.notes;
    data['leadMasterId'] = this.leadMasterId;
    data['createdOn'] = this.createdOn;
    data['createdBy'] = this.createdBy;
    data['createdByName'] = this.createdByName;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? offset;
  int? pageSize;
  int? pageNumber;
  bool? paged;
  bool? unpaged;

  Pageable(
      {this.sort,
        this.offset,
        this.pageSize,
        this.pageNumber,
        this.paged,
        this.unpaged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    offset = json['offset'];
    pageSize = json['pageSize'];
    pageNumber = json['pageNumber'];
    paged = json['paged'];
    unpaged = json['unpaged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['offset'] = this.offset;
    data['pageSize'] = this.pageSize;
    data['pageNumber'] = this.pageNumber;
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
