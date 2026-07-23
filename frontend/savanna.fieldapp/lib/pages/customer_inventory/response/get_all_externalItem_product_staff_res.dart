class GetAllExternalItemProductRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<GetAllExternalItemProductDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllExternalItemProductRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllExternalItemProductRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <GetAllExternalItemProductDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new GetAllExternalItemProductDataList.fromJson(v));
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

class GetAllExternalItemProductDataList {
  Null? createdate;
  Null? updatedate;
  Null? createdByName;
  Null? lastModifiedByName;
  Null? createdById;
  Null? lastModifiedById;
  int? id;
  String? externalItemGroupNumber;
  Null? productId;
  Null? qty;
  Null? usedQty;
  int? unusedQty;
  Null? ownershipType;
  Null? status;
  int? mvnoId;
  bool? isDeleted;
  Null? serviceAreaId;
  Null? inTransitQty;
  Null? rejectedQty;
  Null? approvalStatus;
  Null? totalMacSerial;
  Null? approvalRemark;
  Null? ownerId;
  bool? deleteFlag;
  int? primaryKey;

  GetAllExternalItemProductDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.externalItemGroupNumber,
        this.productId,
        this.qty,
        this.usedQty,
        this.unusedQty,
        this.ownershipType,
        this.status,
        this.mvnoId,
        this.isDeleted,
        this.serviceAreaId,
        this.inTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.totalMacSerial,
        this.approvalRemark,
        this.ownerId,
        this.deleteFlag,
        this.primaryKey});

  GetAllExternalItemProductDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    externalItemGroupNumber = json['externalItemGroupNumber'];
    productId = json['productId'];
    qty = json['qty'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    ownershipType = json['ownershipType'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    serviceAreaId = json['serviceAreaId'];
    inTransitQty = json['inTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    totalMacSerial = json['totalMacSerial'];
    approvalRemark = json['approvalRemark'];
    ownerId = json['ownerId'];
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
    data['externalItemGroupNumber'] = this.externalItemGroupNumber;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['ownershipType'] = this.ownershipType;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['serviceAreaId'] = this.serviceAreaId;
    data['inTransitQty'] = this.inTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['totalMacSerial'] = this.totalMacSerial;
    data['approvalRemark'] = this.approvalRemark;
    data['ownerId'] = this.ownerId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
