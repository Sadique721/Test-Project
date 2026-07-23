import 'package:savbill/webservices/base_response.dart';

class AssignedInventoryDetailRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<AssignedInventoryDetail>? dataList;

  AssignedInventoryDetailRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  AssignedInventoryDetailRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <AssignedInventoryDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AssignedInventoryDetail.fromJson(v));
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

class AssignedInventoryDetail {
  int? id;
  String? inwardNumber;
  int? identityKey;
  int? qty;
  int? usedQty;
  int? unusedQty;
  String? inwardDateTime;
  String? type;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  String? sourceType;
  int? sourceId;
  String? destinationType;
  int? destinationId;
  int? inTransitQty;
  int? serviceAreaId;
  AssignedInventoryProduct? productId;
  AssignedInventoryOutward? outwardId;

  AssignedInventoryDetail(
      {this.id,
      this.inwardNumber,
      this.identityKey,
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
      this.serviceAreaId,
      this.productId,
      this.outwardId});

  AssignedInventoryDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    identityKey = json['identityKey'];
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
    serviceAreaId = json['serviceAreaId'];
    productId = json['productId'] != null
        ? new AssignedInventoryProduct.fromJson(json['productId'])
        : null;
    outwardId = json['outwardId'] != null
        ? new AssignedInventoryOutward.fromJson(json['outwardId'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardNumber'] = this.inwardNumber;
    data['identityKey'] = this.identityKey;
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
    data['serviceAreaId'] = this.serviceAreaId;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    if (this.outwardId != null) {
      data['outwardId'] = this.outwardId!.toJson();
    }
    return data;
  }
}

class AssignedInventoryProduct {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? description;
  String? status;
  int? mvnoId;
  int? totalInPorts;
  int? availableInPorts;
  int? totalOutPorts;
  int? availableOutPorts;
  bool? isDeleted;
  int? expiryTime;
  String? expiryTimeUnit;
  String? refundAmount;
  bool? deleteFlag;
  int? primaryKey;

  AssignedInventoryProduct(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.description,
      this.status,
      this.mvnoId,
      this.totalInPorts,
      this.availableInPorts,
      this.totalOutPorts,
      this.availableOutPorts,
      this.isDeleted,
      this.expiryTime,
      this.expiryTimeUnit,
      this.refundAmount,
      this.deleteFlag,
      this.primaryKey});

  AssignedInventoryProduct.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    totalInPorts = json['totalInPorts'];
    availableInPorts = json['availableInPorts'];
    totalOutPorts = json['totalOutPorts'];
    availableOutPorts = json['availableOutPorts'];
    isDeleted = json['isDeleted'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refundAmount = json['refundAmount'];
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
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['totalInPorts'] = this.totalInPorts;
    data['availableInPorts'] = this.availableInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['isDeleted'] = this.isDeleted;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refundAmount'] = this.refundAmount;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class AssignedInventoryOutward {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? outwardNumber;
  int? qty;
  String? status;
  int? mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  int? usedQty;
  int? unusedQty;
  String? productName;
  String? wareHouseName;
  String? inwardNumber;
  int? unit;
  String? sourceType;
  int? sourceId;
  String? destinationType;
  int? destinationId;
  int? inTransitQty;

  AssignedInventoryOutward(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.outwardNumber,
      this.qty,
      this.status,
      this.mvnoId,
      this.outwardDateTime,
      this.isDeleted,
      this.usedQty,
      this.unusedQty,
      this.productName,
      this.wareHouseName,
      this.inwardNumber,
      this.unit,
      this.sourceType,
      this.sourceId,
      this.destinationType,
      this.destinationId,
      this.inTransitQty});

  AssignedInventoryOutward.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    outwardNumber = json['outwardNumber'];
    qty = json['qty'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    outwardDateTime = json['outwardDateTime'];
    isDeleted = json['isDeleted'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    productName = json['productName'];
    wareHouseName = json['wareHouseName'];
    inwardNumber = json['inwardNumber'];
    unit = json['unit'];
    sourceType = json['sourceType'];
    sourceId = json['sourceId'];
    destinationType = json['destinationType'];
    destinationId = json['destinationId'];
    inTransitQty = json['inTransitQty'];
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
    data['outwardNumber'] = this.outwardNumber;
    data['qty'] = this.qty;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['outwardDateTime'] = this.outwardDateTime;
    data['isDeleted'] = this.isDeleted;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['productName'] = this.productName;
    data['wareHouseName'] = this.wareHouseName;
    data['inwardNumber'] = this.inwardNumber;
    data['unit'] = this.unit;
    data['sourceType'] = this.sourceType;
    data['sourceId'] = this.sourceId;
    data['destinationType'] = this.destinationType;
    data['destinationId'] = this.destinationId;
    data['inTransitQty'] = this.inTransitQty;
    return data;
  }
}
