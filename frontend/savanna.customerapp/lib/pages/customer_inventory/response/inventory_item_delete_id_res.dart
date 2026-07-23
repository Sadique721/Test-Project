import 'package:savbill/webservices/base_response.dart';

class InventoryItemDeleteIdRes extends BaseResponse{
  // int? responseCode;
  String? responseMessage;
  InventoryItemData? data;
  dynamic dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryItemDeleteIdRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InventoryItemDeleteIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? InventoryItemData.fromJson(json['data']) : null;
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

class InventoryItemData {
  int? id;
  String? name;
  String? macAddress;
  String? serialNumber;
  int? mvnoId;
  String? condition;
  bool? isDeleted;
  int? currentInwardId;
  String? currentInwardType;
  int? productId;
  int? ownerId;
  String? ownerType;
  String? warranty;
  int? warrantyPeriod;
  dynamic currentInwardNumber;
  dynamic ownerName;
  dynamic productName;
  String? itemStatus;
  String? ownershipType;
  dynamic remarks;
  dynamic externalItemId;
  dynamic remainingDays;
  dynamic productRefundAmount;
  dynamic filename;
  dynamic refundFlag;
  dynamic itemConditionId;
  dynamic expireDate;

  InventoryItemData(
      {this.id,
        this.name,
        this.macAddress,
        this.serialNumber,
        this.mvnoId,
        this.condition,
        this.isDeleted,
        this.currentInwardId,
        this.currentInwardType,
        this.productId,
        this.ownerId,
        this.ownerType,
        this.warranty,
        this.warrantyPeriod,
        this.currentInwardNumber,
        this.ownerName,
        this.productName,
        this.itemStatus,
        this.ownershipType,
        this.remarks,
        this.externalItemId,
        this.remainingDays,
        this.productRefundAmount,
        this.filename,
        this.refundFlag,
        this.itemConditionId,
        this.expireDate});

  InventoryItemData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    condition = json['condition'];
    isDeleted = json['isDeleted'];
    currentInwardId = json['currentInwardId'];
    currentInwardType = json['currentInwardType'];
    productId = json['productId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    warranty = json['warranty'];
    warrantyPeriod = json['warrantyPeriod'];
    currentInwardNumber = json['currentInwardNumber'];
    ownerName = json['ownerName'];
    productName = json['productName'];
    itemStatus = json['itemStatus'];
    ownershipType = json['ownershipType'];
    remarks = json['remarks'];
    externalItemId = json['externalItemId'];
    remainingDays = json['remainingDays'];
    productRefundAmount = json['productRefundAmount'];
    filename = json['filename'];
    refundFlag = json['refundFlag'];
    itemConditionId = json['itemConditionId'];
    expireDate = json['expireDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['condition'] = this.condition;
    data['isDeleted'] = this.isDeleted;
    data['currentInwardId'] = this.currentInwardId;
    data['currentInwardType'] = this.currentInwardType;
    data['productId'] = this.productId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['warranty'] = this.warranty;
    data['warrantyPeriod'] = this.warrantyPeriod;
    data['currentInwardNumber'] = this.currentInwardNumber;
    data['ownerName'] = this.ownerName;
    data['productName'] = this.productName;
    data['itemStatus'] = this.itemStatus;
    data['ownershipType'] = this.ownershipType;
    data['remarks'] = this.remarks;
    data['externalItemId'] = this.externalItemId;
    data['remainingDays'] = this.remainingDays;
    data['productRefundAmount'] = this.productRefundAmount;
    data['filename'] = this.filename;
    data['refundFlag'] = this.refundFlag;
    data['itemConditionId'] = this.itemConditionId;
    data['expireDate'] = this.expireDate;
    return data;
  }
}
