class CustServiceAuditStatusResponse {
  int? responseCode;
  String? responseMessage;
  ServiceAuditStatusData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  CustServiceAuditStatusResponse(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  CustServiceAuditStatusResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new ServiceAuditStatusData.fromJson(json['data']) : null;
    dataList = json['dataList'];
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ServiceAuditStatusData {
  List<ServiceAuditStatusContent>? content;
  Pageable? pageable;
  bool? last;
  int? totalElements;
  int? totalPages;
  bool? first;
  Sort? sort;
  int? size;
  int? number;
  int? numberOfElements;
  bool? empty;

  ServiceAuditStatusData(
      {this.content,
        this.pageable,
        this.last,
        this.totalElements,
        this.totalPages,
        this.first,
        this.sort,
        this.size,
        this.number,
        this.numberOfElements,
        this.empty});

  ServiceAuditStatusData.fromJson(Map<String, dynamic> json) {
    if (json['content'] != null) {
      content = <ServiceAuditStatusContent>[];
      json['content'].forEach((v) {
        content!.add(new ServiceAuditStatusContent.fromJson(v));
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
    data['totalElements'] = this.totalElements;
    data['totalPages'] = this.totalPages;
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

class ServiceAuditStatusContent {
  int? id;
  String? serviceStopTime;
  Null? staffId;
  String? action;
  int? cprid;
  Null? reasonId;
  String? remarks;
  Null? reasonCategory;
  Null? servicestarttime;
  String? staffName;
  int? custServiceMappingId;
  String? reason;

  ServiceAuditStatusContent(
      {this.id,
        this.serviceStopTime,
        this.staffId,
        this.action,
        this.cprid,
        this.reasonId,
        this.remarks,
        this.reasonCategory,
        this.servicestarttime,
        this.staffName,
        this.custServiceMappingId,
        this.reason});

  ServiceAuditStatusContent.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    serviceStopTime = json['serviceStopTime'];
    staffId = json['staffId'];
    action = json['action'];
    cprid = json['cprid'];
    reasonId = json['reasonId'];
    remarks = json['remarks'];
    reasonCategory = json['reasonCategory'];
    servicestarttime = json['servicestarttime'];
    staffName = json['staffName'];
    custServiceMappingId = json['custServiceMappingId'];
    reason = json['reason'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['serviceStopTime'] = this.serviceStopTime;
    data['staffId'] = this.staffId;
    data['action'] = this.action;
    data['cprid'] = this.cprid;
    data['reasonId'] = this.reasonId;
    data['remarks'] = this.remarks;
    data['reasonCategory'] = this.reasonCategory;
    data['servicestarttime'] = this.servicestarttime;
    data['staffName'] = this.staffName;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['reason'] = this.reason;
    return data;
  }
}

class Pageable {
  Sort? sort;
  int? offset;
  int? pageSize;
  int? pageNumber;
  bool? unpaged;
  bool? paged;

  Pageable(
      {this.sort,
        this.offset,
        this.pageSize,
        this.pageNumber,
        this.unpaged,
        this.paged});

  Pageable.fromJson(Map<String, dynamic> json) {
    sort = json['sort'] != null ? new Sort.fromJson(json['sort']) : null;
    offset = json['offset'];
    pageSize = json['pageSize'];
    pageNumber = json['pageNumber'];
    unpaged = json['unpaged'];
    paged = json['paged'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.sort != null) {
      data['sort'] = this.sort!.toJson();
    }
    data['offset'] = this.offset;
    data['pageSize'] = this.pageSize;
    data['pageNumber'] = this.pageNumber;
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
