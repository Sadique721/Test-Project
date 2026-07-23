import 'package:savbill/webservices/base_response.dart';

class RejectReasonCafRes extends BaseResponse {
  RejectReasonList? rejectReasonList;
  String? timestamp;
  int? status;

  RejectReasonCafRes({this.rejectReasonList, this.timestamp, this.status});

  RejectReasonCafRes.fromJson(Map<String, dynamic> json) {
    rejectReasonList = json['rejectReasonList'] != null
        ? new RejectReasonList.fromJson(json['rejectReasonList'])
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

class RejectReasonList {
  List<CloseCafContentList>? content;
  Pageable? pageable;
  bool? last;
  int? totalElements;
  int? totalPages;
  bool? first;
  Sort? sort;
  int? numberOfElements;
  int? size;
  int? number;
  bool? empty;

  RejectReasonList(
      {this.content,
        this.pageable,
        this.last,
        this.totalElements,
        this.totalPages,
        this.first,
        this.sort,
        this.numberOfElements,
        this.size,
        this.number,
        this.empty});

  RejectReasonList.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <CloseCafContentList>[];
      json['content'].forEach((v) {
        content!.add(new CloseCafContentList.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    last = json['last'];
    totalElements = json['totalElements'];
    totalPages = json['totalPages'];
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
    data['totalElements'] = this.totalElements;
    data['totalPages'] = this.totalPages;
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

class CloseCafContentList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  List<RejectSubReasonDtoList>? rejectSubReasonDtoList;
  Null? rejectSubReasonDeletedIds;
  bool? isDelete;
  Null? mvnoId;
  Null? buId;
  int? identityKey;

  CloseCafContentList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.rejectSubReasonDtoList,
        this.rejectSubReasonDeletedIds,
        this.isDelete,
        this.mvnoId,
        this.buId,
        this.identityKey});

  CloseCafContentList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
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
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    identityKey = json['identityKey'];
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
    data['name'] = this.name;
    data['status'] = this.status;
    if (this.rejectSubReasonDtoList != null) {
      data['rejectSubReasonDtoList'] =
          this.rejectSubReasonDtoList!.map((v) => v.toJson()).toList();
    }
    data['rejectSubReasonDeletedIds'] = this.rejectSubReasonDeletedIds;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class RejectSubReasonDtoList {
  int? id;
  String? name;
  int? rejectReasonId;
  bool? isDelete;
  dynamic mvnoId;
  dynamic identityKey;
  dynamic buId;

  RejectSubReasonDtoList(
      {this.id,
        this.name,
        this.rejectReasonId,
        this.isDelete,
        this.mvnoId,
        this.identityKey,
        this.buId});

  RejectSubReasonDtoList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    rejectReasonId = json['rejectReasonId'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
    buId = json['buId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['rejectReasonId'] = this.rejectReasonId;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['buId'] = this.buId;
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
