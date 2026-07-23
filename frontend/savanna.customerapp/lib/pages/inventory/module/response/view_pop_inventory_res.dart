import 'package:savbill/webservices/base_response.dart';

class ViewPopInventoryRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<PopInventoryDetail>? dataList;

  ViewPopInventoryRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewPopInventoryRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <PopInventoryDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new PopInventoryDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PopInventoryDetail {
  int? id;
  int? qty;
  int? productId;
  String? ownerType;
  int? ownerId;
  int? staffId;
  int? inwardId;
  String? assignedDateTime;
  bool? isDeleted;
  int? mvnoId;
  String? approvalStatus;
  String? expiryDateTime;
  String? inwardNumber;
  String? productName;
  String? customerName;
  bool? hasMac;
  bool? hasSerial;
  int? nextApproverId;
  int? teamHierarchyMappingId;
  String? assigneeName;
  int? previousApproveId;
  String? approvalRemark;
  int? identityKey;
  List<InOutWardMACMapping>? inOutWardMACMapping;
  bool? selected;

  PopInventoryDetail(
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
      this.nextApproverId,
      this.teamHierarchyMappingId,
      this.assigneeName,
      this.previousApproveId,
      this.approvalRemark,
      this.identityKey,
      this.inOutWardMACMapping,
      this.selected});

  PopInventoryDetail.fromJson(Map<String, dynamic> json) {
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
    nextApproverId = json['nextApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    assigneeName = json['assigneeName'];
    previousApproveId = json['previousApproveId'];
    approvalRemark = json['approvalRemark'];
    identityKey = json['identityKey'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <InOutWardMACMapping>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(new InOutWardMACMapping.fromJson(v));
      });
    }
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
    data['nextApproverId'] = this.nextApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['assigneeName'] = this.assigneeName;
    data['previousApproveId'] = this.previousApproveId;
    data['approvalRemark'] = this.approvalRemark;
    data['identityKey'] = this.identityKey;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
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
  bool? selected;


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
      this.primaryKey,
      this.selected});

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
