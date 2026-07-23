class CustDunningDetailRes {
  CustomerDunningHistory? customerDunningHistory;
  String? timestamp;
  int? status;

  CustDunningDetailRes(
      {this.customerDunningHistory, this.timestamp, this.status});

  CustDunningDetailRes.fromJson(Map<String, dynamic> json) {
    customerDunningHistory = json['customerDunningHistory'] != null
        ? new CustomerDunningHistory.fromJson(json['customerDunningHistory'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.customerDunningHistory != null) {
      data['customerDunningHistory'] = this.customerDunningHistory!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustomerDunningHistory {
  List<DunningContent>? content;
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

  CustomerDunningHistory(
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

  CustomerDunningHistory.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <DunningContent>[];
      json['content'].forEach((v) {
        content!.add(DunningContent.fromJson(v));
      });
    }
    pageable = json['pageable'] != null
        ? Pageable.fromJson(json['pageable'])
        : null;
    last = json['last'];
    totalElements = json['totalElements'];
    totalPages = json['totalPages'];
    first = json['first'];
    sort = json['sort'] != null ? Sort.fromJson(json['sort']) : null;
    numberOfElements = json['numberOfElements'];
    size = json['size'];
    number = json['number'];
    empty = json['empty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    if (content != null) {
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

class DunningContent {
  int? id;
  String? eventName;
  String? action;
  int? staffid;
  int? custid;
  int? partnerid;
  String? dunningMessageDate;
  String? dunningMessage;

  DunningContent(
      {this.id,
        this.eventName,
        this.action,
        this.staffid,
        this.custid,
        this.partnerid,
        this.dunningMessageDate,
        this.dunningMessage});

  DunningContent.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    eventName = json['eventName'];
    action = json['action'];
    staffid = json['staffid'];
    custid = json['custid'];
    partnerid = json['partnerid'];
    dunningMessageDate = json['dunningMessageDate'];
    dunningMessage = json['dunningMessage'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['eventName'] = this.eventName;
    data['action'] = this.action;
    data['staffid'] = this.staffid;
    data['custid'] = this.custid;
    data['partnerid'] = this.partnerid;
    data['dunningMessageDate'] = this.dunningMessageDate;
    data['dunningMessage'] = this.dunningMessage;
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
