class getSubCategoryReasonsRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<DataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  getSubCategoryReasonsRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  getSubCategoryReasonsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <DataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new DataList.fromJson(v));
      });
    }
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
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class DataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? subCategoryName;

  int? mvnoId;
  bool? isDeleted;
  String? status;
  List<TicketSubCategoryReasonCategoryMappingList>?
  ticketSubCategoryReasonCategoryMappingList;
  Null? buId;
  Null? lcoId;
  int? identityKey;

  DataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.subCategoryName,

        this.mvnoId,
        this.isDeleted,
        this.status,
        this.ticketSubCategoryReasonCategoryMappingList,
        this.buId,
        this.lcoId,
        this.identityKey});

  DataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    subCategoryName = json['subCategoryName'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    status = json['status'];
    if (json['ticketSubCategoryReasonCategoryMappingList'] != null) {
      ticketSubCategoryReasonCategoryMappingList =
      <TicketSubCategoryReasonCategoryMappingList>[];
      json['ticketSubCategoryReasonCategoryMappingList'].forEach((v) {
        ticketSubCategoryReasonCategoryMappingList!
            .add(new TicketSubCategoryReasonCategoryMappingList.fromJson(v));
      });
    }
    buId = json['buId'];
    lcoId = json['lcoId'];
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
    data['subCategoryName'] = this.subCategoryName;

    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['status'] = this.status;
    if (this.ticketSubCategoryReasonCategoryMappingList != null) {
      data['ticketSubCategoryReasonCategoryMappingList'] = this
          .ticketSubCategoryReasonCategoryMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    data['buId'] = this.buId;
    data['lcoId'] = this.lcoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class TicketSubCategoryReasonCategoryMappingList {
  int? id;
  int? ticketReasonCategoryId;
  int? ticketReasonSubCategoryId;

  TicketSubCategoryReasonCategoryMappingList(
      {this.id, this.ticketReasonCategoryId, this.ticketReasonSubCategoryId});

  TicketSubCategoryReasonCategoryMappingList.fromJson(
      Map<String, dynamic> json) {
    id = json['id'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    ticketReasonSubCategoryId = json['ticketReasonSubCategoryId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['ticketReasonSubCategoryId'] = this.ticketReasonSubCategoryId;
    return data;
  }
}
