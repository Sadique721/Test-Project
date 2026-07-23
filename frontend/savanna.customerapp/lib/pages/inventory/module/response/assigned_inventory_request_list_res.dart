class AssignedInventoryRequestListRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<AssignedInventoryDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  AssignedInventoryRequestListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  AssignedInventoryRequestListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <AssignedInventoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AssignedInventoryDataList.fromJson(v));
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

class AssignedInventoryDataList {
  int? id;
  String? requestInventoryName;
  int? requestNameId;
  int? requestToWarehouseId;
  String? status;
  String? reason;
  List<InventoryRequestProductMappings>? requestInvenotryProductMappings;
  String? requesterName;
  String? requestToName;
  Null? mvnoId;
  int? identityKey;
  String? onBehalfOf;

  AssignedInventoryDataList(
      {this.id,
        this.requestInventoryName,
        this.requestNameId,
        this.requestToWarehouseId,
        this.status,
        this.reason,
        this.requestInvenotryProductMappings,
        this.requesterName,
        this.requestToName,
        this.mvnoId,
        this.identityKey,
        this.onBehalfOf});

  AssignedInventoryDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    requestInventoryName = json['requestInventoryName'];
    requestNameId = json['requestNameId'];
    requestToWarehouseId = json['requestToWarehouseId'];
    status = json['status'];
    reason = json['reason'];
    if (json['requestInvenotryProductMappings'] != null) {
      requestInvenotryProductMappings = <InventoryRequestProductMappings>[];
      json['requestInvenotryProductMappings'].forEach((v) {
        requestInvenotryProductMappings!
            .add(new InventoryRequestProductMappings.fromJson(v));
      });
    }
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
    if (this.requestInvenotryProductMappings != null) {
      data['requestInvenotryProductMappings'] =
          this.requestInvenotryProductMappings!.map((v) => v.toJson()).toList();
    }
    data['requesterName'] = this.requesterName;
    data['requestToName'] = this.requestToName;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['onBehalfOf'] = this.onBehalfOf;
    return data;
  }
}


class InventoryRequestProductMappings {
  int? id;
  int? inventoryRequestId;
  int? productCategoryId;
  String? productCategoryName;
  int? productId;
  String? productName;
  int? quantity;
  String? itemType;
  String? requestStatus;
  Null? mvnoId;
  int? identityKey;
  bool? outWardCreated;

  InventoryRequestProductMappings(
      {this.id,
        this.inventoryRequestId,
        this.productCategoryId,
        this.productCategoryName,
        this.productId,
        this.productName,
        this.quantity,
        this.itemType,
        this.requestStatus,
        this.mvnoId,
        this.identityKey,
        this.outWardCreated});

  InventoryRequestProductMappings.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inventoryRequestId = json['inventoryRequestId'];
    productCategoryId = json['productCategoryId'];
    productCategoryName = json['productCategoryName'];
    productId = json['productId'];
    productName = json['productName'];
    quantity = json['quantity'];
    itemType = json['itemType'];
    requestStatus = json['requestStatus'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
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
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    data['outWardCreated'] = this.outWardCreated;
    return data;
  }
}
