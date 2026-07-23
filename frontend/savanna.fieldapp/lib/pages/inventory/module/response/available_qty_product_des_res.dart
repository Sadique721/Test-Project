class AvailableQtyProductDesRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<AvailableQtyDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  AvailableQtyProductDesRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  AvailableQtyProductDesRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <AvailableQtyDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AvailableQtyDataList.fromJson(v));
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

class AvailableQtyDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? productId;
  int? ownerId;
  String? ownerType;
  int? quantity;
  int? usedQty;
  int? unusedQty;
  int? inTransitQty;
  Null? boundQty;
  Null? mvnoId;
  Null? wareHouseName;
  Null? productName;
  bool? deleteFlag;
  int? primaryKey;

  AvailableQtyDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.productId,
        this.ownerId,
        this.ownerType,
        this.quantity,
        this.usedQty,
        this.unusedQty,
        this.inTransitQty,
        this.boundQty,
        this.mvnoId,
        this.wareHouseName,
        this.productName,
        this.deleteFlag,
        this.primaryKey});

  AvailableQtyDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    productId = json['productId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    quantity = json['quantity'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inTransitQty = json['inTransitQty'];
    boundQty = json['boundQty'];
    mvnoId = json['mvnoId'];
    wareHouseName = json['wareHouseName'];
    productName = json['productName'];
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
    data['productId'] = this.productId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['quantity'] = this.quantity;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inTransitQty'] = this.inTransitQty;
    data['boundQty'] = this.boundQty;
    data['mvnoId'] = this.mvnoId;
    data['wareHouseName'] = this.wareHouseName;
    data['productName'] = this.productName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
