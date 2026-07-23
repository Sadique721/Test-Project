class NetworkInwardProductRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<NetworkInwardProudctDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  NetworkInwardProductRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  NetworkInwardProductRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <NetworkInwardProudctDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new NetworkInwardProudctDataList.fromJson(v));
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

class NetworkInwardProudctDataList {
  dynamic createdate;
  dynamic updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? inwardNumber;
  dynamic productId;
  dynamic qty;
  dynamic usedQty;
  int? unusedQty;
  dynamic inwardDateTime;
  dynamic type;
  dynamic status;
  int? mvnoId;
  bool? isDeleted;
  dynamic sourceType;
  dynamic sourceId;
  dynamic destinationType;
  dynamic destinationId;
  dynamic inTransitQty;
  dynamic serviceArea;
  dynamic outwardId;
  dynamic outTransitQty;
  dynamic rejectedQty;
  dynamic approvalStatus;
  dynamic categoryType;
  dynamic rmsInwardId;
  dynamic navInwardId;
  dynamic totalMacSerial;
  dynamic approvalRemark;
  dynamic assignNonSerializedItemQty;
  dynamic requestInventoryId;
  bool? deleteFlag;
  int? primaryKey;

  NetworkInwardProudctDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.inwardNumber,
        this.productId,
        this.qty,
        this.usedQty,
        this.unusedQty,
        this.inwardDateTime,
        this.type,
        this.status,
        this.mvnoId,
        this.isDeleted,
        this.sourceType,
        this.sourceId,
        this.destinationType,
        this.destinationId,
        this.inTransitQty,
        this.serviceArea,
        this.outwardId,
        this.outTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.categoryType,
        this.rmsInwardId,
        this.navInwardId,
        this.totalMacSerial,
        this.approvalRemark,
        this.assignNonSerializedItemQty,
        this.requestInventoryId,
        this.deleteFlag,
        this.primaryKey});

  NetworkInwardProudctDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    productId = json['productId'];
    qty = json['qty'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inwardDateTime = json['inwardDateTime'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    sourceType = json['sourceType'];
    sourceId = json['sourceId'];
    destinationType = json['destinationType'];
    destinationId = json['destinationId'];
    inTransitQty = json['inTransitQty'];
    serviceArea = json['serviceArea'];
    outwardId = json['outwardId'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    categoryType = json['categoryType'];
    rmsInwardId = json['rmsInwardId'];
    navInwardId = json['navInwardId'];
    totalMacSerial = json['totalMacSerial'];
    approvalRemark = json['approvalRemark'];
    assignNonSerializedItemQty = json['assignNonSerializedItemQty'];
    requestInventoryId = json['requestInventoryId'];
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
    data['inwardNumber'] = this.inwardNumber;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inwardDateTime'] = this.inwardDateTime;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['sourceType'] = this.sourceType;
    data['sourceId'] = this.sourceId;
    data['destinationType'] = this.destinationType;
    data['destinationId'] = this.destinationId;
    data['inTransitQty'] = this.inTransitQty;
    data['serviceArea'] = this.serviceArea;
    data['outwardId'] = this.outwardId;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['categoryType'] = this.categoryType;
    data['rmsInwardId'] = this.rmsInwardId;
    data['navInwardId'] = this.navInwardId;
    data['totalMacSerial'] = this.totalMacSerial;
    data['approvalRemark'] = this.approvalRemark;
    data['assignNonSerializedItemQty'] = this.assignNonSerializedItemQty;
    data['requestInventoryId'] = this.requestInventoryId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
