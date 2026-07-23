class InwardMacSerialItemRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<InwardMacSerialDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InwardMacSerialItemRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InwardMacSerialItemRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <InwardMacSerialDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InwardMacSerialDataList.fromJson(v));
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

class InwardMacSerialDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  int? outwardId;
  String? macAddress;
  String? serialNumber;
  int? mvnoId;
  String? condition;
  int? productId;
  int? currentInwardId;
  int? ownerId;
  String? assetId;
  String? ownerType;
  int? warrantyPeriod;
  String? warranty;
  String? currentInwardType;
  String? itemStatus;
  dynamic remainingDays;
  bool? isDeleted;
  String? ownershipType;
  dynamic externalItemId;
  dynamic intransiantWarrenty;
  dynamic intransiantOwnership;
  dynamic intransiantWarrentyStatus;
  dynamic expireDate;
  dynamic intransiantexpireDate;
  dynamic productRefundAmount;
  bool? refundFlag;
  dynamic remarks;
  dynamic removeFrom;
  bool? deleteFlag;
  int? primaryKey;
  String? macAddressValue;
  String? serialNumberValue;
  bool? selected = false;

  InwardMacSerialDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.outwardId,
        this.macAddress,
        this.serialNumber,
        this.mvnoId,
        this.condition,
        this.productId,
        this.currentInwardId,
        this.ownerId,
        this.assetId,
        this.ownerType,
        this.warrantyPeriod,
        this.warranty,
        this.currentInwardType,
        this.itemStatus,
        this.remainingDays,
        this.isDeleted,
        this.ownershipType,
        this.externalItemId,
        this.intransiantWarrenty,
        this.intransiantOwnership,
        this.intransiantWarrentyStatus,
        this.expireDate,
        this.intransiantexpireDate,
        this.productRefundAmount,
        this.refundFlag,
        this.remarks,
        this.removeFrom,
        this.deleteFlag,
        this.primaryKey,
        this.macAddressValue,
        this.serialNumberValue,
      this.selected});

  InwardMacSerialDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    outwardId = json['outwardId'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    condition = json['condition'];
    productId = json['productId'];
    currentInwardId = json['currentInwardId'];
    ownerId = json['ownerId'];
    assetId = json['assetId'];
    ownerType = json['ownerType'];
    warrantyPeriod = json['warrantyPeriod'];
    warranty = json['warranty'];
    currentInwardType = json['currentInwardType'];
    itemStatus = json['itemStatus'];
    remainingDays = json['remainingDays'];
    isDeleted = json['isDeleted'];
    ownershipType = json['ownershipType'];
    externalItemId = json['externalItemId'];
    intransiantWarrenty = json['intransiantWarrenty'];
    intransiantOwnership = json['intransiantOwnership'];
    intransiantWarrentyStatus = json['intransiantWarrentyStatus'];
    expireDate = json['expireDate'];
    intransiantexpireDate = json['intransiantexpireDate'];
    productRefundAmount = json['productRefundAmount'];
    refundFlag = json['refundFlag'];
    remarks = json['remarks'];
    removeFrom = json['removeFrom'];
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
    data['name'] = this.name;
    data['outwardId'] = this.outwardId;
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['condition'] = this.condition;
    data['productId'] = this.productId;
    data['currentInwardId'] = this.currentInwardId;
    data['ownerId'] = this.ownerId;
    data['assetId'] = this.assetId;
    data['ownerType'] = this.ownerType;
    data['warrantyPeriod'] = this.warrantyPeriod;
    data['warranty'] = this.warranty;
    data['currentInwardType'] = this.currentInwardType;
    data['itemStatus'] = this.itemStatus;
    data['remainingDays'] = this.remainingDays;
    data['isDeleted'] = this.isDeleted;
    data['ownershipType'] = this.ownershipType;
    data['externalItemId'] = this.externalItemId;
    data['intransiantWarrenty'] = this.intransiantWarrenty;
    data['intransiantOwnership'] = this.intransiantOwnership;
    data['intransiantWarrentyStatus'] = this.intransiantWarrentyStatus;
    data['expireDate'] = this.expireDate;
    data['intransiantexpireDate'] = this.intransiantexpireDate;
    data['productRefundAmount'] = this.productRefundAmount;
    data['refundFlag'] = this.refundFlag;
    data['remarks'] = this.remarks;
    data['removeFrom'] = this.removeFrom;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
