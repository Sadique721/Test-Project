import 'package:savbill/webservices/base_response.dart';

class AllRejectedReasonLeadRes extends BaseResponse{
  AllRejectReasonList? rejectReasonList;
  String? timestamp;
  int? status;

  AllRejectedReasonLeadRes(
      {this.rejectReasonList, this.timestamp, this.status});

  AllRejectedReasonLeadRes.fromJson(Map<String, dynamic> json) {
    rejectReasonList = json['rejectReasonList'] != null
        ? new AllRejectReasonList.fromJson(json['rejectReasonList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.rejectReasonList != null) {
      data['rejectReasonList'] = this.rejectReasonList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class AllRejectReasonList {
  List<RejectedContent>? content;
  Pageable? pageable;
  int? totalElements;
  int? totalPages;
  bool? last;
  bool? first;
  Sort? sort;
  int? numberOfElements;
  int? size;
  int? number;
  bool? empty;

  AllRejectReasonList(
      {this.content,
        this.pageable,
        this.totalElements,
        this.totalPages,
        this.last,
        this.first,
        this.sort,
        this.numberOfElements,
        this.size,
        this.number,
        this.empty});

  AllRejectReasonList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <RejectedContent>[];
      json['content'].forEach((v) {
        content!.add(new RejectedContent.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    totalElements = json['totalElements'];
    totalPages = json['totalPages'];
    last = json['last'];
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
    data['totalElements'] = this.totalElements;
    data['totalPages'] = this.totalPages;
    data['last'] = this.last;
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

class RejectedContent {
  int? id;
  String? name;
  String? status;
  List<RejectSubReasonDtoList>? rejectSubReasonDtoList;
  dynamic rejectSubReasonDeletedIds;
  bool? isDelete;

  RejectedContent(
      {this.id,
        this.name,
        this.status,
        this.rejectSubReasonDtoList,
        this.rejectSubReasonDeletedIds,
        this.isDelete});

  RejectedContent.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    if (json['rejectSubReasonDtoList'] != null) {
      rejectSubReasonDtoList = <RejectSubReasonDtoList>[];
      json['rejectSubReasonDtoList'].forEach((v) {
        rejectSubReasonDtoList!.add(new RejectSubReasonDtoList.fromJson(v));
      });
    }
    rejectSubReasonDeletedIds = json['rejectSubReasonDeletedIds'];
    isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    if (this.rejectSubReasonDtoList != null) {
      data['rejectSubReasonDtoList'] =
          this.rejectSubReasonDtoList!.map((v) => v.toJson()).toList();
    }
    data['rejectSubReasonDeletedIds'] = this.rejectSubReasonDeletedIds;
    data['isDelete'] = this.isDelete;
    return data;
  }
}

class RejectSubReasonDtoList {
  int? id;
  String? name;
  int? rejectReasonId;
  bool? isDelete;

  RejectSubReasonDtoList(
      {this.id, this.name, this.rejectReasonId, this.isDelete});

  RejectSubReasonDtoList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    rejectReasonId = json['rejectReasonId'];
    isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['rejectReasonId'] = this.rejectReasonId;
    data['isDelete'] = this.isDelete;
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
  bool? sorted;
  bool? unsorted;
  bool? empty;

  Sort({this.sorted, this.unsorted, this.empty});

  Sort.fromJson(Map<String, dynamic> json) {
    sorted = json['sorted'];
    unsorted = json['unsorted'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['sorted'] = this.sorted;
    data['unsorted'] = this.unsorted;
    data['empty'] = this.empty;
    return data;
  }
}
