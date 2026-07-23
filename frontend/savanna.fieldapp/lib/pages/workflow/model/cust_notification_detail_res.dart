class CustNotificationDetailRes {
  CustomerNotificationHistory? customerNotificationHistory;
  String? timestamp;
  int? status;

  CustNotificationDetailRes(
      {this.customerNotificationHistory, this.timestamp, this.status});

  CustNotificationDetailRes.fromJson(Map<String, dynamic> json) {
    customerNotificationHistory = json['customerNotificationHistory'] != null
        ? CustomerNotificationHistory.fromJson(
        json['customerNotificationHistory'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    if (customerNotificationHistory != null) {
      data['customerNotificationHistory'] =
          customerNotificationHistory!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustomerNotificationHistory {
  List<NotificationContent>? content;
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

  CustomerNotificationHistory(
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

  CustomerNotificationHistory.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <NotificationContent>[];
      json['content'].forEach((v) {
        content!.add(new NotificationContent.fromJson(v));
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

class NotificationContent {
  int? id;
  String? eventName;
  dynamic action;
  dynamic staffid;
  dynamic custid;
  dynamic partnerid;
  String? username;
  String? message;
  String? messageDate;

  NotificationContent(
      {this.id,
        this.eventName,
        this.action,
        this.staffid,
        this.custid,
        this.partnerid,
        this.username,
        this.message,
        this.messageDate});

  NotificationContent.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    eventName = json['eventName'];
    action = json['action'];
    staffid = json['staffid'];
    custid = json['custid'];
    partnerid = json['partnerid'];
    username = json['username'];
    message = json['message'];
    messageDate = json['messageDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['eventName'] = this.eventName;
    data['action'] = this.action;
    data['staffid'] = this.staffid;
    data['custid'] = this.custid;
    data['partnerid'] = this.partnerid;
    data['username'] = this.username;
    data['message'] = this.message;
    data['messageDate'] = this.messageDate;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? pageNumber;
  int? pageSize;
  int? offset;
  bool? unpaged;
  bool? paged;

  Pageable(
      {this.sort,
        this.pageNumber,
        this.pageSize,
        this.offset,
        this.unpaged,
        this.paged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    pageNumber = json['pageNumber'];
    pageSize = json['pageSize'];
    offset = json['offset'];
    unpaged = json['unpaged'];
    paged = json['paged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['pageNumber'] = this.pageNumber;
    data['pageSize'] = this.pageSize;
    data['offset'] = this.offset;
    data['unpaged'] = this.unpaged;
    data['paged'] = this.paged;
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
