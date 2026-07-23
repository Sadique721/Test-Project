import 'package:savbill/webservices/base_response.dart';

/*class ViewInwardsListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<InwardsDetail>? dataList;

  ViewInwardsListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewInwardsListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <InwardsDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InwardsDetail.fromJson(v));
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

class InwardsDetail {
  int? id;
  String? inwardNumber;
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
  int? totalMacSerial;
  int? serviceAreaId;
  OutwardId? outwardId;
  int? identityKey;
  String? createdBy;
  String? approvalStatus;
  int? outTransitQty;
  int? rejectedQty;
  InwardsProductDetail? productId;



 // Null? serviceArea;
//  Null? outwardId;
//  Null? totalMacSerial;


  InwardsDetail(
      {this.id,
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
      this.totalMacSerial,
      this.serviceAreaId,
        this.outwardId,
      this.identityKey,
      this.createdBy,
      this.approvalStatus,
        this.outTransitQty,
        this.rejectedQty});

  InwardsDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    productId = json['productId'] != null
        ? new InwardsProductDetail.fromJson(json['productId'])
        : null;
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
    totalMacSerial = json['totalMacSerial'];
    serviceAreaId = json['serviceAreaId'];
    outwardId = json['outwardId'] != null
        ? new OutwardId.fromJson(json['outwardId'])
        : null;
    identityKey = json['identityKey'];
    createdBy = json['createdBy'];
    approvalStatus = json['approvalStatus'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardNumber'] = this.inwardNumber;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
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
    data['totalMacSerial'] = this.totalMacSerial;
    data['serviceAreaId'] = this.serviceAreaId;
    if (this.outwardId != null) {
      data['outwardId'] = this.outwardId!.toJson();
    }
    data['identityKey'] = this.identityKey;
    data['createdBy'] = this.createdBy;
    data['approvalStatus'] = this.approvalStatus;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    return data;
  }
}

class InwardsProductDetail {
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
  InwardsProductCategory? productCategory;

  InwardsProductDetail(
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
      this.productCategory,
      this.expiryTime,
      this.expiryTimeUnit,
      this.refundAmount,
      this.deleteFlag,
      this.primaryKey});

  InwardsProductDetail.fromJson(Map<String, dynamic> json) {
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
    productCategory = json['productCategory'] != null
        ? new InwardsProductCategory.fromJson(json['productCategory'])
        : null;
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
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refundAmount'] = this.refundAmount;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class InwardsProductCategory {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? unit;
  int? mvnoId;
  bool? hasMac;
  String? type;
  String? status;
  bool? isDeleted;
  bool? hasSerial;
  bool? deleteFlag;
  int? primaryKey;

  InwardsProductCategory(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.unit,
      this.mvnoId,
      this.hasMac,
      this.type,
      this.status,
      this.isDeleted,
      this.hasSerial,
      this.deleteFlag,
      this.primaryKey});

  InwardsProductCategory.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    mvnoId = json['mvnoId'];
    hasMac = json['hasMac'];
    type = json['type'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    hasSerial = json['hasSerial'];
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
    data['unit'] = this.unit;
    data['mvnoId'] = this.mvnoId;
    data['hasMac'] = this.hasMac;
    data['type'] = this.type;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['hasSerial'] = this.hasSerial;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class OutwardId {
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
  // ProductId? productId;
  Null? inwardId;
  int? mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  int? usedQty;
  int? unusedQty;
  Null? productName;
  Null? wareHouseName;
  Null? inwardNumber;
  Null? unit;
  String? sourceType;
  int? sourceId;
  String? destinationType;
  int? destinationId;
  int? inTransitQty;
  Null? serviceArea;
  int? outTransitQty;
  int? rejectedQty;
  String? approvalStatus;
  String? categoryType;
  Null? rmsOutwardId;
  Null? navOutwardId;
  Null? approvalRemark;
  Null? type;
  Null? requestInventoryId;
  Null? requestInventoryProductId;
  int? selectedItems;

  OutwardId(
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
        this.inwardId,
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
        this.inTransitQty,
        this.serviceArea,
        this.outTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.categoryType,
        this.rmsOutwardId,
        this.navOutwardId,
        this.approvalRemark,
        this.type,
        this.requestInventoryId,
        this.requestInventoryProductId,
        this.selectedItems});

  OutwardId.fromJson(Map<String, dynamic> json) {
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
    inwardId = json['inwardId'];
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
    serviceArea = json['serviceArea'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    categoryType = json['categoryType'];
    rmsOutwardId = json['rmsOutwardId'];
    navOutwardId = json['navOutwardId'];
    approvalRemark = json['approvalRemark'];
    type = json['type'];
    requestInventoryId = json['requestInventoryId'];
    requestInventoryProductId = json['requestInventoryProductId'];
    selectedItems = json['selectedItems'];
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
    data['inwardId'] = this.inwardId;
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
    data['serviceArea'] = this.serviceArea;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['categoryType'] = this.categoryType;
    data['rmsOutwardId'] = this.rmsOutwardId;
    data['navOutwardId'] = this.navOutwardId;
    data['approvalRemark'] = this.approvalRemark;
    data['type'] = this.type;
    data['requestInventoryId'] = this.requestInventoryId;
    data['requestInventoryProductId'] = this.requestInventoryProductId;
    data['selectedItems'] = this.selectedItems;
    return data;
  }
}*/




class ViewInwardsListRes extends BaseResponse{
  dynamic responseCode;
  String? responseMessage;
  List<InwardsDetail>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ViewInwardsListRes(
      {this.responseCode,
        this.responseMessage,
        this.dataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ViewInwardsListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <InwardsDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InwardsDetail.fromJson(v));
      });
    }
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class InwardsDetail {
  int? id;
  String? inwardNumber;
  InwardsProductDetail? productId;
  dynamic qty;
  dynamic usedQty;
  dynamic unusedQty;
  String? inwardDateTime;
  String? type;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  String? sourceType;
  dynamic sourceId;
  String? destinationType;
  dynamic destinationId;
  int? inTransitQty;
  dynamic specificationParametersDTOList;
  dynamic serviceAreaId;
  OutwardId? outwardId;
  dynamic outTransitQty;
  dynamic rejectedQty;
  String? approvalStatus;
  String? categoryType;
  dynamic rmsInwardId;
  dynamic navInwardId;
  int? totalMacSerial;
  String? createdBy;
  String? approvalRemark;
  dynamic assignNonSerializedItemQty;
  dynamic requestInventoryId;
  dynamic inventorySpecificationList;
  dynamic source;
  dynamic destination;
  int? identityKey;
  String? description;

  InwardsDetail(
      {this.id,
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
        this.specificationParametersDTOList,
        this.serviceAreaId,
        this.outwardId,
        this.outTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.categoryType,
        this.rmsInwardId,
        this.navInwardId,
        this.totalMacSerial,
        this.createdBy,
        this.approvalRemark,
        this.assignNonSerializedItemQty,
        this.requestInventoryId,
        this.inventorySpecificationList,
        this.source,
        this.destination,
        this.identityKey,
      this.description});

  InwardsDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    productId = json['productId'] != null
        ? new InwardsProductDetail.fromJson(json['productId'])
        : null;
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
    specificationParametersDTOList = json['specificationParametersDTOList'];
    serviceAreaId = json['serviceAreaId'];
    outwardId = json['outwardId'] != null
        ? new OutwardId.fromJson(json['outwardId'])
        : null;
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    categoryType = json['categoryType'];
    rmsInwardId = json['rmsInwardId'];
    navInwardId = json['navInwardId'];
    totalMacSerial = json['totalMacSerial'];
    createdBy = json['createdBy'];
    approvalRemark = json['approvalRemark'];
    assignNonSerializedItemQty = json['assignNonSerializedItemQty'];
    requestInventoryId = json['requestInventoryId'];
    inventorySpecificationList = json['inventorySpecificationList'];
    source = json['source'];
    destination = json['destination'];
    identityKey = json['identityKey'];
    description = json['description'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardNumber'] = this.inwardNumber;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
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
    data['specificationParametersDTOList'] =
        this.specificationParametersDTOList;
    data['serviceAreaId'] = this.serviceAreaId;
    if (this.outwardId != null) {
      data['outwardId'] = this.outwardId!.toJson();
    }
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['categoryType'] = this.categoryType;
    data['rmsInwardId'] = this.rmsInwardId;
    data['navInwardId'] = this.navInwardId;
    data['totalMacSerial'] = this.totalMacSerial;
    data['createdBy'] = this.createdBy;
    data['approvalRemark'] = this.approvalRemark;
    data['assignNonSerializedItemQty'] = this.assignNonSerializedItemQty;
    data['requestInventoryId'] = this.requestInventoryId;
    data['inventorySpecificationList'] = this.inventorySpecificationList;
    data['source'] = this.source;
    data['destination'] = this.destination;
    data['identityKey'] = this.identityKey;
    data['description'] = this.description;
    return data;
  }
}

class InwardsProductDetail {
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
  dynamic mvnoId;
  dynamic totalInPorts;
  dynamic availableInPorts;
  dynamic totalOutPorts;
  dynamic availableOutPorts;
  String? productId;
  String? navLedgerId;
  bool? isDeleted;
  InwardsProductCategory? productCategory;
  dynamic expiryTime;
  String? expiryTimeUnit;
  dynamic refurburshiedProductRefAmountInWarranty;
  dynamic refurburshiedProductRefAmountPostWarranty;
  dynamic newProductRefAmountInWarranty;
  dynamic newProductRefAmountPostWarranty;
  int? caseId;
  Vendor? vendor;
  NewProductCharge? newProductCharge;
  NewProductCharge? refurburshiedProductCharge;
  dynamic actualpricenewProduct;
  dynamic actualpricerefurbishedProduct;
  dynamic newProductAmount;
  dynamic refurburshiedProductAmount;
  dynamic newPrice;
  dynamic refurburshiedPrice;
  dynamic refurburshiedProductTax;
  dynamic newProductTax;
  dynamic refurburshiedProductTaxName;
  dynamic newProductTaxName;
  bool? deleteFlag;
  int? primaryKey;

  InwardsProductDetail(
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
        this.productId,
        this.navLedgerId,
        this.isDeleted,
        this.productCategory,
        this.expiryTime,
        this.expiryTimeUnit,
        this.refurburshiedProductRefAmountInWarranty,
        this.refurburshiedProductRefAmountPostWarranty,
        this.newProductRefAmountInWarranty,
        this.newProductRefAmountPostWarranty,
        this.caseId,
        this.vendor,
        this.newProductCharge,
        this.refurburshiedProductCharge,
        this.actualpricenewProduct,
        this.actualpricerefurbishedProduct,
        this.newProductAmount,
        this.refurburshiedProductAmount,
        this.newPrice,
        this.refurburshiedPrice,
        this.refurburshiedProductTax,
        this.newProductTax,
        this.refurburshiedProductTaxName,
        this.newProductTaxName,
        this.deleteFlag,
        this.primaryKey});

  InwardsProductDetail.fromJson(Map<String, dynamic> json) {
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
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];
    isDeleted = json['isDeleted'];
    productCategory = json['productCategory'] != null
        ? InwardsProductCategory.fromJson(json['productCategory'])
        : null;
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    caseId = json['caseId'];
    vendor =
    json['vendor'] != null ? new Vendor.fromJson(json['vendor']) : null;
    newProductCharge = json['newProductCharge'] != null
        ? new NewProductCharge.fromJson(json['newProductCharge'])
        : null;
    refurburshiedProductCharge = json['refurburshiedProductCharge'] != null
        ? new NewProductCharge.fromJson(json['refurburshiedProductCharge'])
        : null;
    actualpricenewProduct = json['actualpricenewProduct'];
    actualpricerefurbishedProduct = json['actualpricerefurbishedProduct'];
    newProductAmount = json['newProductAmount'];
    refurburshiedProductAmount = json['refurburshiedProductAmount'];
    newPrice = json['newPrice'];
    refurburshiedPrice = json['refurburshiedPrice'];
    refurburshiedProductTax = json['refurburshiedProductTax'];
    newProductTax = json['newProductTax'];
    refurburshiedProductTaxName = json['refurburshiedProductTaxName'];
    newProductTaxName = json['newProductTaxName'];
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
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;
    data['isDeleted'] = this.isDeleted;
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refurburshiedProductRefAmountInWarranty'] =
        this.refurburshiedProductRefAmountInWarranty;
    data['refurburshiedProductRefAmountPostWarranty'] =
        this.refurburshiedProductRefAmountPostWarranty;
    data['newProductRefAmountInWarranty'] = this.newProductRefAmountInWarranty;
    data['newProductRefAmountPostWarranty'] =
        this.newProductRefAmountPostWarranty;
    data['caseId'] = this.caseId;
    if (this.vendor != null) {
      data['vendor'] = this.vendor!.toJson();
    }
    if (this.newProductCharge != null) {
      data['newProductCharge'] = this.newProductCharge!.toJson();
    }
    if (this.refurburshiedProductCharge != null) {
      data['refurburshiedProductCharge'] =
          this.refurburshiedProductCharge!.toJson();
    }
    data['actualpricenewProduct'] = this.actualpricenewProduct;
    data['actualpricerefurbishedProduct'] = this.actualpricerefurbishedProduct;
    data['newProductAmount'] = this.newProductAmount;
    data['refurburshiedProductAmount'] = this.refurburshiedProductAmount;
    data['newPrice'] = this.newPrice;
    data['refurburshiedPrice'] = this.refurburshiedPrice;
    data['refurburshiedProductTax'] = this.refurburshiedProductTax;
    data['newProductTax'] = this.newProductTax;
    data['refurburshiedProductTaxName'] = this.refurburshiedProductTaxName;
    data['newProductTaxName'] = this.newProductTaxName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class InwardsProductCategory {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? unit;
  int? mvnoId;
  bool? hasMac;
  String? type;
  String? status;
  String? productId;
  bool? isDeleted;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory;
  bool? deleteFlag;
  int? primaryKey;

  InwardsProductCategory(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.unit,
        this.mvnoId,
        this.hasMac,
        this.type,
        this.status,
        this.productId,
        this.isDeleted,
        this.hasSerial,
        this.hasTrackable,
        this.hasPort,
        this.hasCas,
        this.dtvCategory,
        this.deleteFlag,
        this.primaryKey});

  InwardsProductCategory.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    mvnoId = json['mvnoId'];
    hasMac = json['hasMac'];
    type = json['type'];
    status = json['status'];
    productId = json['productId'];
    isDeleted = json['isDeleted'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    dtvCategory = json['dtvCategory'];
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
    data['unit'] = this.unit;
    data['mvnoId'] = this.mvnoId;
    data['hasMac'] = this.hasMac;
    data['type'] = this.type;
    data['status'] = this.status;
    data['productId'] = this.productId;
    data['isDeleted'] = this.isDeleted;
    data['hasSerial'] = this.hasSerial;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['dtvCategory'] = this.dtvCategory;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class Vendor {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  int? mvnoId;
  bool? deleted;
  bool? deleteFlag;
  dynamic primaryKey;

  Vendor(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.mvnoId,
        this.deleted,
        this.deleteFlag,
        this.primaryKey});

  Vendor.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    deleted = json['deleted'];
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
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['deleted'] = this.deleted;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class NewProductCharge {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  dynamic desc;
  String? chargetype;
  dynamic price;
  dynamic actualprice;
  dynamic taxId;
  bool? isDelete;
  String? chargecategory;
  dynamic mvnoId;
  dynamic buId;
  dynamic service;
  String? status;
  dynamic taxamount;
  bool? isinventorycharge;
  dynamic productId;
  bool? deleteFlag;
  dynamic primaryKey;

  NewProductCharge(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.desc,
        this.chargetype,
        this.price,
        this.actualprice,
        this.taxId,
        this.isDelete,
        this.chargecategory,
        this.mvnoId,
        this.buId,
        this.service,
        this.status,
        this.taxamount,
        this.isinventorycharge,
        this.productId,
        this.deleteFlag,
        this.primaryKey});

  NewProductCharge.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    actualprice = json['actualprice'];
    taxId = json['taxId'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    service = json['service'];
    status = json['status'];
    taxamount = json['taxamount'];
    isinventorycharge = json['isinventorycharge'];
    productId = json['productId'];
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
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['taxId'] = this.taxId;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['service'] = this.service;
    data['status'] = this.status;
    data['taxamount'] = this.taxamount;
    data['isinventorycharge'] = this.isinventorycharge;
    data['productId'] = this.productId;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class OutwardId {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? outwardNumber;
  dynamic qty;
  String? status;
  ProductId? productId;
  dynamic inwardId;
  dynamic mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  dynamic usedQty;
  dynamic unusedQty;
  dynamic productName;
  dynamic wareHouseName;
  dynamic inwardNumber;
  dynamic unit;
  String? sourceType;
  dynamic sourceId;
  String? destinationType;
  dynamic destinationId;
  dynamic inTransitQty;
  dynamic serviceArea;
  dynamic outTransitQty;
  dynamic rejectedQty;
  String? approvalStatus;
  String? categoryType;
  dynamic rmsOutwardId;
  dynamic navOutwardId;
  dynamic approvalRemark;
  dynamic type;
  dynamic requestInventoryId;
  dynamic requestInventoryProductId;
  dynamic selectedItems;

  OutwardId(
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
        this.productId,
        this.inwardId,
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
        this.inTransitQty,
        this.serviceArea,
        this.outTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.categoryType,
        this.rmsOutwardId,
        this.navOutwardId,
        this.approvalRemark,
        this.type,
        this.requestInventoryId,
        this.requestInventoryProductId,
        this.selectedItems});

  OutwardId.fromJson(Map<String, dynamic> json) {
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
    productId = json['productId'] != null
        ? new ProductId.fromJson(json['productId'])
        : null;
    inwardId = json['inwardId'];
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
    serviceArea = json['serviceArea'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    categoryType = json['categoryType'];
    rmsOutwardId = json['rmsOutwardId'];
    navOutwardId = json['navOutwardId'];
    approvalRemark = json['approvalRemark'];
    type = json['type'];
    requestInventoryId = json['requestInventoryId'];
    requestInventoryProductId = json['requestInventoryProductId'];
    selectedItems = json['selectedItems'];
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
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    data['inwardId'] = this.inwardId;
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
    data['serviceArea'] = this.serviceArea;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['categoryType'] = this.categoryType;
    data['rmsOutwardId'] = this.rmsOutwardId;
    data['navOutwardId'] = this.navOutwardId;
    data['approvalRemark'] = this.approvalRemark;
    data['type'] = this.type;
    data['requestInventoryId'] = this.requestInventoryId;
    data['requestInventoryProductId'] = this.requestInventoryProductId;
    data['selectedItems'] = this.selectedItems;
    return data;
  }
}

class ProductId {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? description;
  String? status;
  dynamic mvnoId;
  dynamic totalInPorts;
  dynamic availableInPorts;
  dynamic totalOutPorts;
  dynamic availableOutPorts;
  String? productId;
  String? navLedgerId;
  bool? isDeleted;
  ProductCategory? productCategory;
  dynamic expiryTime;
  String? expiryTimeUnit;
  dynamic refurburshiedProductRefAmountInWarranty;
  dynamic refurburshiedProductRefAmountPostWarranty;
  dynamic newProductRefAmountInWarranty;
  dynamic newProductRefAmountPostWarranty;
  dynamic caseId;
  Vendor? vendor;
  dynamic newProductCharge;
  dynamic refurburshiedProductCharge;
  dynamic actualpricenewProduct;
  dynamic actualpricerefurbishedProduct;
  dynamic newProductAmount;
  dynamic refurburshiedProductAmount;
  dynamic newPrice;
  dynamic refurburshiedPrice;
  dynamic refurburshiedProductTax;
  dynamic newProductTax;
  dynamic refurburshiedProductTaxName;
  dynamic newProductTaxName;
  bool? deleteFlag;
  dynamic primaryKey;

  ProductId(
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
        this.productId,
        this.navLedgerId,
        this.isDeleted,
        this.productCategory,
        this.expiryTime,
        this.expiryTimeUnit,
        this.refurburshiedProductRefAmountInWarranty,
        this.refurburshiedProductRefAmountPostWarranty,
        this.newProductRefAmountInWarranty,
        this.newProductRefAmountPostWarranty,
        this.caseId,
        this.vendor,
        this.newProductCharge,
        this.refurburshiedProductCharge,
        this.actualpricenewProduct,
        this.actualpricerefurbishedProduct,
        this.newProductAmount,
        this.refurburshiedProductAmount,
        this.newPrice,
        this.refurburshiedPrice,
        this.refurburshiedProductTax,
        this.newProductTax,
        this.refurburshiedProductTaxName,
        this.newProductTaxName,
        this.deleteFlag,
        this.primaryKey});

  ProductId.fromJson(Map<String, dynamic> json) {
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
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];
    isDeleted = json['isDeleted'];
    productCategory = json['productCategory'] != null
        ? new ProductCategory.fromJson(json['productCategory'])
        : null;
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    caseId = json['caseId'];
    vendor =
    json['vendor'] != null ? new Vendor.fromJson(json['vendor']) : null;
    newProductCharge = json['newProductCharge'];
    refurburshiedProductCharge = json['refurburshiedProductCharge'];
    actualpricenewProduct = json['actualpricenewProduct'];
    actualpricerefurbishedProduct = json['actualpricerefurbishedProduct'];
    newProductAmount = json['newProductAmount'];
    refurburshiedProductAmount = json['refurburshiedProductAmount'];
    newPrice = json['newPrice'];
    refurburshiedPrice = json['refurburshiedPrice'];
    refurburshiedProductTax = json['refurburshiedProductTax'];
    newProductTax = json['newProductTax'];
    refurburshiedProductTaxName = json['refurburshiedProductTaxName'];
    newProductTaxName = json['newProductTaxName'];
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
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;
    data['isDeleted'] = this.isDeleted;
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refurburshiedProductRefAmountInWarranty'] =
        this.refurburshiedProductRefAmountInWarranty;
    data['refurburshiedProductRefAmountPostWarranty'] =
        this.refurburshiedProductRefAmountPostWarranty;
    data['newProductRefAmountInWarranty'] = this.newProductRefAmountInWarranty;
    data['newProductRefAmountPostWarranty'] =
        this.newProductRefAmountPostWarranty;
    data['caseId'] = this.caseId;
    if (this.vendor != null) {
      data['vendor'] = this.vendor!.toJson();
    }
    data['newProductCharge'] = this.newProductCharge;
    data['refurburshiedProductCharge'] = this.refurburshiedProductCharge;
    data['actualpricenewProduct'] = this.actualpricenewProduct;
    data['actualpricerefurbishedProduct'] = this.actualpricerefurbishedProduct;
    data['newProductAmount'] = this.newProductAmount;
    data['refurburshiedProductAmount'] = this.refurburshiedProductAmount;
    data['newPrice'] = this.newPrice;
    data['refurburshiedPrice'] = this.refurburshiedPrice;
    data['refurburshiedProductTax'] = this.refurburshiedProductTax;
    data['newProductTax'] = this.newProductTax;
    data['refurburshiedProductTaxName'] = this.refurburshiedProductTaxName;
    data['newProductTaxName'] = this.newProductTaxName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class ProductCategory {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? unit;
  dynamic mvnoId;
  bool? hasMac;
  String? type;
  String? status;
  dynamic productId;
  bool? isDeleted;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory;
  bool? deleteFlag;
  dynamic primaryKey;

  ProductCategory(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.unit,
        this.mvnoId,
        this.hasMac,
        this.type,
        this.status,
        this.productId,
        this.isDeleted,
        this.hasSerial,
        this.hasTrackable,
        this.hasPort,
        this.hasCas,
        this.dtvCategory,
        this.deleteFlag,
        this.primaryKey});

  ProductCategory.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    mvnoId = json['mvnoId'];
    hasMac = json['hasMac'];
    type = json['type'];
    status = json['status'];
    productId = json['productId'];
    isDeleted = json['isDeleted'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    dtvCategory = json['dtvCategory'];
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
    data['unit'] = this.unit;
    data['mvnoId'] = this.mvnoId;
    data['hasMac'] = this.hasMac;
    data['type'] = this.type;
    data['status'] = this.status;
    data['productId'] = this.productId;
    data['isDeleted'] = this.isDeleted;
    data['hasSerial'] = this.hasSerial;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['dtvCategory'] = this.dtvCategory;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}




