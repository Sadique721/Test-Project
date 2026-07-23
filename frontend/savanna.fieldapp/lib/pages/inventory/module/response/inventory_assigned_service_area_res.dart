class InventoryAssignedServiceAreaRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<AssignedServiceAreaDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryAssignedServiceAreaRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InventoryAssignedServiceAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <AssignedServiceAreaDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AssignedServiceAreaDataList.fromJson(v));
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

class AssignedServiceAreaDataList {
  int? id;
  int? qty;
  int? productId;
  String? ownerType;
  int? ownerId;
  int? staffId;
  dynamic inwardId;
  String? assignedDateTime;
  bool? isDeleted;
  int? mvnoId;
  String? approvalStatus;
  String? expiryDateTime;
  String? inwardNumber;
  String? productName;
  dynamic customerName;
  bool? hasMac;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  int? nextApproverId;
  dynamic teamHierarchyMappingId;
  String? assigneeName;
  List<InOutWardMACMapping>? inOutWardMACMapping;
  int? previousApproveId;
  String? approvalRemark;
  dynamic popName;
  String? serviceAreaName;
  int? identityKey;

  AssignedServiceAreaDataList(
      {this.id,
        this.qty,
        this.productId,
        this.ownerType,
        this.ownerId,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.isDeleted,
        this.mvnoId,
        this.approvalStatus,
        this.expiryDateTime,
        this.inwardNumber,
        this.productName,
        this.customerName,
        this.hasMac,
        this.hasSerial,
        this.hasTrackable,
        this.hasPort,
        this.nextApproverId,
        this.teamHierarchyMappingId,
        this.assigneeName,
        this.inOutWardMACMapping,
        this.previousApproveId,
        this.approvalRemark,
        this.popName,
        this.serviceAreaName,
        this.identityKey});

  AssignedServiceAreaDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    ownerType = json['ownerType'];
    ownerId = json['ownerId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    approvalStatus = json['approvalStatus'];
    expiryDateTime = json['expiryDateTime'];
    inwardNumber = json['inwardNumber'];
    productName = json['productName'];
    customerName = json['customerName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    nextApproverId = json['nextApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    assigneeName = json['assigneeName'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <InOutWardMACMapping>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(new InOutWardMACMapping.fromJson(v));
      });
    }
    previousApproveId = json['previousApproveId'];
    approvalRemark = json['approvalRemark'];
    popName = json['popName'];
    serviceAreaName = json['serviceAreaName'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['ownerType'] = this.ownerType;
    data['ownerId'] = this.ownerId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['approvalStatus'] = this.approvalStatus;
    data['expiryDateTime'] = this.expiryDateTime;
    data['inwardNumber'] = this.inwardNumber;
    data['productName'] = this.productName;
    data['customerName'] = this.customerName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['nextApproverId'] = this.nextApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['assigneeName'] = this.assigneeName;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
    data['previousApproveId'] = this.previousApproveId;
    data['approvalRemark'] = this.approvalRemark;
    data['popName'] = this.popName;
    data['serviceAreaName'] = this.serviceAreaName;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class InOutWardMACMapping {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? inwardId;
  int? outwardId;
  String? status;
  dynamic macAddress;
  bool? isDeleted;
  dynamic custInventoryMappingId;
  String? serialNumber;
  dynamic mvnoId;
  dynamic currentApproveId;
  dynamic previousApproveId;
  dynamic teamHierarchyMappingId;
  dynamic usedCount;
  dynamic inwardIdOfOutward;
  int? isForwarded;
  int? isReturned;
  dynamic remark;
  dynamic externalItemId;
  int? itemId;
  int? inventoryMappingId;
  dynamic bulkConsumptionId;
  dynamic nonSerializedItemId;
  dynamic itemStatus;
  bool? deleteFlag;
  int? primaryKey;

  InOutWardMACMapping(
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
        this.primaryKey});

  InOutWardMACMapping.fromJson(Map<String, dynamic> json) {
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
