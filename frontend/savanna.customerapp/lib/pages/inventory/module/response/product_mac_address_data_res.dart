class ProductMacAddressDataRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ProductMacDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ProductMacAddressDataRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ProductMacAddressDataRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ProductMacDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ProductMacDataList.fromJson(v));
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

class ProductMacDataList {
  int? id;
  int? inwardId;
  int? outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  dynamic custInventoryMappingId;
  String? serialNumber;
  dynamic mvnoId;
  dynamic currentApproverId;
  dynamic previousApproverId;
  dynamic teamHierarchyMappingId;
  dynamic inwardIdOfOutward;
  int? isForwarded;
  dynamic remark;
  dynamic externalItemId;
  int? itemId;
  dynamic inventoryMappingId;
  dynamic bulkConsumptionId;
  dynamic itemRemaingDays;
  int? isReturned;
  int? productId;
  String? ownerShip;
  dynamic nonSerializedItemId;
  String? condition;
  String? productName;
  bool? hasMac;
  bool? hasSerial;
  int? identityKey;
  bool? selected = false;
  String? macAddressValue;
  String? serialNumberValue;

  ProductMacDataList(
      {this.id,
        this.inwardId,
        this.outwardId,
        this.status,
        this.macAddress,
        this.isDeleted,
        this.custInventoryMappingId,
        this.serialNumber,
        this.mvnoId,
        this.currentApproverId,
        this.previousApproverId,
        this.teamHierarchyMappingId,
        this.inwardIdOfOutward,
        this.isForwarded,
        this.remark,
        this.externalItemId,
        this.itemId,
        this.inventoryMappingId,
        this.bulkConsumptionId,
        this.itemRemaingDays,
        this.isReturned,
        this.productId,
        this.ownerShip,
        this.nonSerializedItemId,
        this.condition,
        this.productName,
        this.hasMac,
        this.hasSerial,
        this.identityKey,  this.selected,
      this.macAddressValue,this.serialNumberValue});

  ProductMacDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardId = json['inwardId'];
    outwardId = json['outwardId'];
    status = json['status'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    currentApproverId = json['currentApproverId'];
    previousApproverId = json['previousApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    inwardIdOfOutward = json['inwardIdOfOutward'];
    isForwarded = json['isForwarded'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    itemRemaingDays = json['itemRemaingDays'];
    isReturned = json['isReturned'];
    productId = json['productId'];
    ownerShip = json['ownerShip'];
    nonSerializedItemId = json['nonSerializedItemId'];
    condition = json['condition'];
    productName = json['productName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['currentApproverId'] = this.currentApproverId;
    data['previousApproverId'] = this.previousApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['inwardIdOfOutward'] = this.inwardIdOfOutward;
    data['isForwarded'] = this.isForwarded;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['itemRemaingDays'] = this.itemRemaingDays;
    data['isReturned'] = this.isReturned;
    data['productId'] = this.productId;
    data['ownerShip'] = this.ownerShip;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['condition'] = this.condition;
    data['productName'] = this.productName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
