class SaveRequestInventoryRes {
  int? responseCode;
  String? responseMessage;
  Data? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  SaveRequestInventoryRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  SaveRequestInventoryRes.fromJson(Map<String, dynamic> json) {
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
  List<RequestInvenotryProductMapping>? requestInvenotryProductMappings;
  String? status;
  String? remarks;
  String? reason;
  String? requesterName;
  String? requestToName;
  String? inventoryRequestStatus;
  dynamic identityKey;
  int? mvnoId;
  String? onBehalfOf;

  Data(
      {this.id,
        this.requestInventoryName,
        this.requestNameId,
        this.requestToWarehouseId,
        this.requestInvenotryProductMappings,
        this.status,
        this.remarks,
        this.reason,
        this.requesterName,
        this.requestToName,
        this.inventoryRequestStatus,
        this.identityKey,
        this.mvnoId,
        this.onBehalfOf});

  Data.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    requestInventoryName = json['requestInventoryName'];
    requestNameId = json['requestNameId'];
    requestToWarehouseId = json['requestToWarehouseId'];
    if (json['requestInvenotryProductMappings'] != null) {
      requestInvenotryProductMappings = <RequestInvenotryProductMapping>[];
      json['requestInvenotryProductMappings'].forEach((v) {
        requestInvenotryProductMappings!
            .add(new RequestInvenotryProductMapping.fromJson(v));
      });
    }
    status = json['status'];
    remarks = json['remarks'];
    reason = json['reason'];
    requesterName = json['requesterName'];
    requestToName = json['requestToName'];
    inventoryRequestStatus = json['inventoryRequestStatus'];
    identityKey = json['identityKey'];
    mvnoId = json['mvnoId'];
    onBehalfOf = json['onBehalfOf'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['requestInventoryName'] = this.requestInventoryName;
    data['requestNameId'] = this.requestNameId;
    data['requestToWarehouseId'] = this.requestToWarehouseId;
    if (this.requestInvenotryProductMappings != null) {
      data['requestInvenotryProductMappings'] =
          this.requestInvenotryProductMappings!.map((v) => v.toJson()).toList();
    }
    data['status'] = this.status;
    data['remarks'] = this.remarks;
    data['reason'] = this.reason;
    data['requesterName'] = this.requesterName;
    data['requestToName'] = this.requestToName;
    data['inventoryRequestStatus'] = this.inventoryRequestStatus;
    data['identityKey'] = this.identityKey;
    data['mvnoId'] = this.mvnoId;
    data['onBehalfOf'] = this.onBehalfOf;
    return data;
  }
}

class RequestInvenotryProductMapping {
  int? id;
  int? inventoryRequestId;
  int? productCategoryId;
  String? productCategoryName;
  int? productId;
  String? productName;
  int? quantity;
  String? itemType;
  String? requestStatus;
  dynamic identityKey;
  int? mvnoId;
  bool? outWardCreated;

  RequestInvenotryProductMapping(
      {this.id,
        this.inventoryRequestId,
        this.productCategoryId,
        this.productCategoryName,
        this.productId,
        this.productName,
        this.quantity,
        this.itemType,
        this.requestStatus,
        this.identityKey,
        this.mvnoId,
        this.outWardCreated});

  RequestInvenotryProductMapping.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inventoryRequestId = json['inventoryRequestId'];
    productCategoryId = json['productCategoryId'];
    productCategoryName = json['productCategoryName'];
    productId = json['productId'];
    productName = json['productName'];
    quantity = json['quantity'];
    itemType = json['itemType'];
    requestStatus = json['requestStatus'];
    identityKey = json['identityKey'];
    mvnoId = json['mvnoId'];
    outWardCreated = json['outWardCreated'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inventoryRequestId'] = this.inventoryRequestId;
    data['productCategoryId'] = this.productCategoryId;
    data['productCategoryName'] = this.productCategoryName;
    data['productId'] = this.productId;
    data['productName'] = this.productName;
    data['quantity'] = this.quantity;
    data['itemType'] = this.itemType;
    data['requestStatus'] = this.requestStatus;
    data['identityKey'] = this.identityKey;
    data['mvnoId'] = this.mvnoId;
    data['outWardCreated'] = this.outWardCreated;
    return data;
  }
}
