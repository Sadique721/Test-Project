import 'package:savbill/webservices/base_response.dart';

class BulkConsumptionMappingRes extends BaseResponse {
  List<BulkConsumptionMappingDetail>? dataList;

  BulkConsumptionMappingRes({responseCode, responseMessage, this.dataList});

  BulkConsumptionMappingRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BulkConsumptionMappingDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BulkConsumptionMappingDetail.fromJson(v));
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

class BulkConsumptionMappingDetail {
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
  String? macAddress;
  bool? isDeleted;
  int? custInventoryMappingId;
  String? serialNumber;
  int? currentApproveId;
  int? previousApproveId;
  int? teamHierarchyMappingId;
  int? usedCount;
  int? inwardIdOfOutward;
  int? isForwarded;
  int? isReturned;
  String? remark;
  int? externalItemId;
  int? itemId;
  int? inventoryMappingId;
  int? bulkConsumptionId;
  String? itemStatus;
  bool? deleteFlag;
  int? primaryKey;

  BulkConsumptionMappingDetail(
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
      this.itemStatus,
      this.deleteFlag,
      this.primaryKey});

  BulkConsumptionMappingDetail.fromJson(Map<String, dynamic> json) {
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
    data['itemStatus'] = this.itemStatus;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
