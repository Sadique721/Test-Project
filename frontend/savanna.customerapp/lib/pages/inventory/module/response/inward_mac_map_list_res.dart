import 'package:savbill/webservices/base_response.dart';

class InwardMacMapListRes extends BaseResponse {
  List<InwardMacMapDetail>? dataList;

  InwardMacMapListRes({responseCode, responseMessage, this.dataList});

  InwardMacMapListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <InwardMacMapDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InwardMacMapDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class InwardMacMapDetail {
  int? id;
  int? inwardId;
  int? outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  int? custInventoryMappingId;
  String? serialNumber;
  int? mvnoId;
  int? currentApproverId;
  int? previousApproverId;
  int? teamHierarchyMappingId;
  int? isForwarded;
  String? remark;
  int? externalItemId;

  int? itemId;

  num? isReturned;
  num? nonSerializedItemId;

  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? currentApproveId;
  int? inwardIdOfOutward;
  int? inventoryMappingId;
  int? bulkConsumptionId;
  int? itemRemaingDays;
  String? condition;
  String? productName;
  int? productId;
  bool? hasMac;
  bool? hasSerial;
  String? ownerShip;
  int? previousApproveId;
  int? usedCount;
  bool? deleteFlag;
  int? primaryKey;
  String? itemStatus;


  int? identityKey;
  bool? selected = false;

  InwardMacMapDetail(
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
      this.mvnoId,
      this.currentApproverId,
      this.previousApproverId,
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
      this.deleteFlag,
      this.primaryKey,
      this.isReturned,
      this.nonSerializedItemId,
      this.remark,
      this.externalItemId,
      this.itemId,
      this.inventoryMappingId,
      this.bulkConsumptionId,
      this.itemRemaingDays,
      this.itemStatus,
      this.condition,
      this.productName,
      this.productId,
      this.hasMac,
      this.hasSerial,
      this.ownerShip,
      this.identityKey,
      this.selected});

  InwardMacMapDetail.fromJson(Map<String, dynamic> json) {
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
    mvnoId = json['mvnoId'];
    currentApproverId = json['currentApproverId'];
    previousApproverId = json['previousApproverId'];
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
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
    isReturned = json['isReturned'];
    nonSerializedItemId = json['nonSerializedItemId'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    itemRemaingDays = json['itemRemaingDays'];
    itemStatus = json['itemStatus'];
    condition = json['condition'];
    productName = json['productName'];
    productId = json['productId'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    ownerShip = json['ownerShip'];
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
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['previousApproverId'] = this.previousApproverId;
    data['currentApproverId'] = this.currentApproverId;
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
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    data['isReturned'] = this.isReturned;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['itemRemaingDays'] = this.itemRemaingDays;
    data['itemStatus'] = this.itemStatus;
    data['condition'] = this.condition;
    data['productName'] = this.productName;
    data['productId'] = this.productId;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['ownerShip'] = this.ownerShip;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
