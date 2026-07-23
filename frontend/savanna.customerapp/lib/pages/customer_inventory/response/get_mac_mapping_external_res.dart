/*class MACMappingExternalRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<MACMappingExternalData>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  MACMappingExternalRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  MACMappingExternalRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <MACMappingExternalData>[];
      json['dataList'].forEach((v) {
        dataList!.add(new MACMappingExternalData.fromJson(v));
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

class MACMappingExternalData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  Null? inwardId;
  Null? outwardId;
  String? status;
  Null? macAddress;
  bool? isDeleted;
  Null? custInventoryMappingId;
  String? serialNumber;
  Null? currentApproveId;
  Null? previousApproveId;
  Null? teamHierarchyMappingId;
  Null? usedCount;
  Null? inwardIdOfOutward;
  int? isForwarded;
  int? isReturned;
  Null? remark;
  int? externalItemId;
  int? itemId;
  Null? inventoryMappingId;
  Null? bulkConsumptionId;
  Null? nonSerializedItemId;
  Null? itemStatus;
  bool? deleteFlag;
  int? primaryKey;
  bool? selected = false;
  String? serialNumberValue;

  MACMappingExternalData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.inwardId,
        this.outwardId,
        this.status,
        this.macAddress,
        this.isDeleted,
        this.custInventoryMappingId,
        this.serialNumber,
        this.currentApproveId,
        this.previousApproveId,
        this.teamHierarchyMappingId,
        this.usedCount,
        this.inwardIdOfOutward,
        this.isForwarded,
        this.isReturned,
        this.remark,
        this.externalItemId,
        this.itemId,
        this.inventoryMappingId,
        this.bulkConsumptionId,
        this.nonSerializedItemId,
        this.itemStatus,
        this.deleteFlag,
        this.primaryKey,
        this.selected,
      this.serialNumberValue});

  MACMappingExternalData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    inwardId = json['inwardId'];
    outwardId = json['outwardId'];
    status = json['status'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    currentApproveId = json['currentApproveId'];
    previousApproveId = json['previousApproveId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    usedCount = json['usedCount'];
    inwardIdOfOutward = json['inwardIdOfOutward'];
    isForwarded = json['isForwarded'];
    isReturned = json['isReturned'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    nonSerializedItemId = json['nonSerializedItemId'];
    itemStatus = json['itemStatus'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['currentApproveId'] = this.currentApproveId;
    data['previousApproveId'] = this.previousApproveId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['usedCount'] = this.usedCount;
    data['inwardIdOfOutward'] = this.inwardIdOfOutward;
    data['isForwarded'] = this.isForwarded;
    data['isReturned'] = this.isReturned;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['itemStatus'] = this.itemStatus;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}*/


class MACMappingExternalRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<MACMappingExternalData>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  MACMappingExternalRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  MACMappingExternalRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <MACMappingExternalData>[];
      json['dataList'].forEach((v) {
        dataList!.add(new MACMappingExternalData.fromJson(v));
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

class MACMappingExternalData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  Null? inwardId;
  Null? outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  Null? custInventoryMappingId;
  String? serialNumber;
  Null? mvnoId;
  Null? currentApproveId;
  Null? previousApproveId;
  Null? teamHierarchyMappingId;
  Null? usedCount;
  Null? inwardIdOfOutward;
  int? isForwarded;
  int? isReturned;
  Null? remark;
  int? externalItemId;
  int? itemId;
  Null? inventoryMappingId;
  Null? bulkConsumptionId;
  Null? nonSerializedItemId;
  Null? itemStatus;
  bool? deleteFlag;
  int? primaryKey;
  bool? selected = false;
  String? serialNumberValue;

  MACMappingExternalData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.inwardId,
        this.outwardId,
        this.status,
        this.macAddress,
        this.isDeleted,
        this.custInventoryMappingId,
        this.serialNumber,
        this.mvnoId,
        this.currentApproveId,
        this.previousApproveId,
        this.teamHierarchyMappingId,
        this.usedCount,
        this.inwardIdOfOutward,
        this.isForwarded,
        this.isReturned,
        this.remark,
        this.externalItemId,
        this.itemId,
        this.inventoryMappingId,
        this.bulkConsumptionId,
        this.nonSerializedItemId,
        this.itemStatus,
        this.deleteFlag,
        this.primaryKey,
      this.selected,
        this.serialNumberValue
      });

  MACMappingExternalData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    inwardId = json['inwardId'];
    outwardId = json['outwardId'];
    status = json['status'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    currentApproveId = json['currentApproveId'];
    previousApproveId = json['previousApproveId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    usedCount = json['usedCount'];
    inwardIdOfOutward = json['inwardIdOfOutward'];
    isForwarded = json['isForwarded'];
    isReturned = json['isReturned'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    nonSerializedItemId = json['nonSerializedItemId'];
    itemStatus = json['itemStatus'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['currentApproveId'] = this.currentApproveId;
    data['previousApproveId'] = this.previousApproveId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['usedCount'] = this.usedCount;
    data['inwardIdOfOutward'] = this.inwardIdOfOutward;
    data['isForwarded'] = this.isForwarded;
    data['isReturned'] = this.isReturned;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['itemStatus'] = this.itemStatus;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
