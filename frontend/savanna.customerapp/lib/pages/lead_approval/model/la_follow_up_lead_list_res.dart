import 'package:savbill/webservices/base_response.dart';

class LAFollowUpLeadListRes extends BaseResponse{
  FollowUpListData? followUpList;
  String? timestamp;
  int? status;

  LAFollowUpLeadListRes({this.followUpList, this.timestamp, this.status});

  LAFollowUpLeadListRes.fromJson(Map<String, dynamic> json) {
    followUpList = json['followUpList'] != null
        ? new FollowUpListData.fromJson(json['followUpList'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.followUpList != null) {
      data['followUpList'] = this.followUpList!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class FollowUpListData {
  List<FollowUpList>? content;
  Pageable? pageable;
  int? totalPages;
  int? totalElements;
  bool? last;
  bool? first;
  Sort? sort;
  int? size;
  int? number;
  int? numberOfElements;
  bool? empty;

  FollowUpListData(
      {this.content,
        this.pageable,
        this.totalPages,
        this.totalElements,
        this.last,
        this.first,
        this.sort,
        this.size,
        this.number,
        this.numberOfElements,
        this.empty});

  FollowUpListData.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <FollowUpList>[];
      json['content'].forEach((v) {
        content!.add(new FollowUpList.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? new Pageable.fromJson(json['pageable'])
        : null;
    totalPages = json['totalPages'];
    totalElements = json['totalElements'];
    last = json['last'];
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
    data['totalPages'] = this.totalPages;
    data['totalElements'] = this.totalElements;
    data['last'] = this.last;
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

class FollowUpList {
  int? id;
  String? followUpName;
  String? followUpDatetime;
  String? remarks;
  String? status;
  bool? isMissed;
  bool? isSend;
  int? leadMasterId;
  String? leadMasterName;
  int? createdBy;
  String? staffName;

  FollowUpList(
      {this.id,
        this.followUpName,
        this.followUpDatetime,
        this.remarks,
        this.status,
        this.isMissed,
        this.isSend,
        this.leadMasterId,
        this.leadMasterName,
        this.createdBy,
        this.staffName});

  FollowUpList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    followUpName = json['followUpName'];
    followUpDatetime = json['followUpDatetime'];
    remarks = json['remarks'];
    status = json['status'];
    isMissed = json['isMissed'];
    isSend = json['isSend'];
    leadMasterId = json['leadMasterId'];
    leadMasterName = json['leadMasterName'];
    createdBy = json['createdBy'];
    staffName = json['staffName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['followUpName'] = this.followUpName;
    data['followUpDatetime'] = this.followUpDatetime;
    data['remarks'] = this.remarks;
    data['status'] = this.status;
    data['isMissed'] = this.isMissed;
    data['isSend'] = this.isSend;
    data['leadMasterId'] = this.leadMasterId;
    data['leadMasterName'] = this.leadMasterName;
    data['createdBy'] = this.createdBy;
    data['staffName'] = this.staffName;
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
