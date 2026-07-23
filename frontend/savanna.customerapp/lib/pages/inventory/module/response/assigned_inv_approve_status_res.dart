class AssignedInvApproveStatusRes {
  int? responseCode;
  String? responseMessage;
  Data? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  AssignedInvApproveStatusRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  AssignedInvApproveStatusRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new Data.fromJson(json['data']) : null;
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

class Data {
  int? id;
  String? requestInventoryName;
  int? requestNameId;
  int? requestToWarehouseId;
  String? status;
  String? reason;
  dynamic requesterName;
  dynamic requestToName;
  dynamic mvnoId;
  int? identityKey;
  String? onBehalfOf;

  Data(
      {this.id,
        this.requestInventoryName,
        this.requestNameId,
        this.requestToWarehouseId,
        this.status,
        this.reason,
        this.requesterName,
        this.requestToName,
        this.mvnoId,
        this.identityKey,
        this.onBehalfOf});

  Data.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    requestInventoryName = json['requestInventoryName'];
    requestNameId = json['requestNameId'];
    requestToWarehouseId = json['requestToWarehouseId'];
    status = json['status'];
    reason = json['reason'];
    requesterName = json['requesterName'];
    requestToName = json['requestToName'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
    onBehalfOf = json['onBehalfOf'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['requestInventoryName'] = this.requestInventoryName;
    data['requestNameId'] = this.requestNameId;
    data['requestToWarehouseId'] = this.requestToWarehouseId;
    data['status'] = this.status;
    data['reason'] = this.reason;
    data['requesterName'] = this.requesterName;
    data['requestToName'] = this.requestToName;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['onBehalfOf'] = this.onBehalfOf;
    return data;
  }
}
