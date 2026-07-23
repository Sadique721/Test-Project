class ReplacementMacAddressListRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ReplacementMacAddressList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ReplacementMacAddressListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ReplacementMacAddressListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ReplacementMacAddressList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ReplacementMacAddressList.fromJson(v));
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

class ReplacementMacAddressList {
  int? id;
  int? inwardId;
  int? outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  dynamic custInventoryMappingId;
  String? serialNumber;
  dynamic  mvnoId;
  dynamic  currentApproverId;
  dynamic  previousApproverId;
  dynamic  teamHierarchyMappingId;
  dynamic  inwardIdOfOutward;
  dynamic isForwarded;
  dynamic  remark;
  dynamic  externalItemId;
  dynamic itemId;
  dynamic  inventoryMappingId;
  dynamic  bulkConsumptionId;
  dynamic  itemRemaingDays;
  dynamic isReturned;
  dynamic nonSerializedItemId;
  String? condition;
  String? productName;
  bool? hasMac;
  bool? hasSerial;
  int? identityKey;
  bool? selected = false;
  dynamic macAddressValue;
  dynamic serialNumberValue;

  ReplacementMacAddressList(
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
        this.nonSerializedItemId,
        this.condition,
        this.productName,
        this.hasMac,
        this.hasSerial,
        this.identityKey,
        this.selected,
      this.macAddressValue,
      this.serialNumberValue});

  ReplacementMacAddressList.fromJson(Map<String, dynamic> json) {
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
    nonSerializedItemId = json['nonSerializedItemId'];
    condition = json['condition'];
    productName = json['productName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['condition'] = this.condition;
    data['productName'] = this.productName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
