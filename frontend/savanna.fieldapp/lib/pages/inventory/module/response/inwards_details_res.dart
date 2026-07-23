import 'package:savbill/webservices/base_response.dart';

class InwardDetailsRes extends BaseResponse {
  String? responseMessage;
  InwardsDetailData? data;
  dynamic dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  InwardDetailsRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  InwardDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new InwardsDetailData.fromJson(json['data']) : null;
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

class InwardsDetailData {
  dynamic id;
  String? inwardNumber;
  ProductId? productId;
  dynamic qty;
  dynamic usedQty;
  dynamic unusedQty;
  String? inwardDateTime;
  dynamic type;
  String? status;
  dynamic mvnoId;
  bool? isDeleted;
  String? sourceType;
  dynamic sourceId;
  String? destinationType;
  dynamic destinationId;
  dynamic inTransitQty;
  dynamic specificationParametersDTOList;
  dynamic serviceAreaId;
  OutwardId? outwardId;
  dynamic outTransitQty;
  dynamic rejectedQty;
  String? approvalStatus;
  String? categoryType;
  dynamic rmsInwardId;
  dynamic navInwardId;
  dynamic totalMacSerial;
  String? createdBy;
  String? approvalRemark;
  dynamic description;
  dynamic assignNonSerializedItemQty;
  dynamic requestInventoryId;
  dynamic inventorySpecificationList;
  dynamic startDateTime;
  dynamic expiryDateTime;
  dynamic oemWarrantyRemainingDays;
  dynamic oemWarrantyStatus;
  List<ItemList>? itemList;
  String? source;
  String? destination;
  dynamic identityKey;

  InwardsDetailData(
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
        this.description,
        this.assignNonSerializedItemQty,
        this.requestInventoryId,
        this.inventorySpecificationList,
        this.startDateTime,
        this.expiryDateTime,
        this.oemWarrantyRemainingDays,
        this.oemWarrantyStatus,
        this.itemList,
        this.source,
        this.destination,
        this.identityKey});

  InwardsDetailData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardNumber = json['inwardNumber'];
    productId = json['productId'] != null
        ? new ProductId.fromJson(json['productId'])
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
    description = json['description'];
    assignNonSerializedItemQty = json['assignNonSerializedItemQty'];
    requestInventoryId = json['requestInventoryId'];
    inventorySpecificationList = json['inventorySpecificationList'];
    startDateTime = json['startDateTime'];
    expiryDateTime = json['expiryDateTime'];
    oemWarrantyRemainingDays = json['oemWarrantyRemainingDays'];
    oemWarrantyStatus = json['oemWarrantyStatus'];
    if (json['itemList'] != null) {
      itemList = <ItemList>[];
      json['itemList'].forEach((v) {
        itemList!.add(new ItemList.fromJson(v));
      });
    }
    source = json['source'];
    destination = json['destination'];
    identityKey = json['identityKey'];
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
    data['description'] = this.description;
    data['assignNonSerializedItemQty'] = this.assignNonSerializedItemQty;
    data['requestInventoryId'] = this.requestInventoryId;
    data['inventorySpecificationList'] = this.inventorySpecificationList;
    data['startDateTime'] = this.startDateTime;
    data['expiryDateTime'] = this.expiryDateTime;
    data['oemWarrantyRemainingDays'] = this.oemWarrantyRemainingDays;
    data['oemWarrantyStatus'] = this.oemWarrantyStatus;
    if (this.itemList != null) {
      data['itemList'] = this.itemList!.map((v) => v.toJson()).toList();
    }
    data['source'] = this.source;
    data['destination'] = this.destination;
    data['identityKey'] = this.identityKey;
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
  dynamic productId;
  dynamic navLedgerId;
  bool? hasOEMConsider;
  bool? hasAssetConsider;
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
  RefurburshiedProductCharge? refurburshiedProductCharge;
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
  dynamic specificationParametersDTOList;
  dynamic primaryKey;
  bool? deleteFlag;

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
        this.hasOEMConsider,
        this.hasAssetConsider,
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
        this.specificationParametersDTOList,
        this.primaryKey,
        this.deleteFlag});

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
    hasOEMConsider = json['hasOEMConsider'];
    hasAssetConsider = json['hasAssetConsider'];
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
    refurburshiedProductCharge = json['refurburshiedProductCharge'] != null
        ? new RefurburshiedProductCharge.fromJson(
        json['refurburshiedProductCharge'])
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
    specificationParametersDTOList = json['specificationParametersDTOList'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['hasOEMConsider'] = this.hasOEMConsider;
    data['hasAssetConsider'] = this.hasAssetConsider;
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
    data['specificationParametersDTOList'] =
        this.specificationParametersDTOList;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
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
  String? productId;
  bool? isDeleted;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory;
  String? deviceType;
  dynamic primaryKey;
  bool? deleteFlag;

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
        this.deviceType,
        this.primaryKey,
        this.deleteFlag});

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
    deviceType = json['deviceType'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['deviceType'] = this.deviceType;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class Vendor {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? status;
  dynamic mvnoId;
  dynamic primaryKey;
  bool? deleted;
  bool? deleteFlag;

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
        this.primaryKey,
        this.deleted,
        this.deleteFlag});

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
    primaryKey = json['primaryKey'];
    deleted = json['deleted'];
    deleteFlag = json['deleteFlag'];
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
    data['primaryKey'] = this.primaryKey;
    data['deleted'] = this.deleted;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class RefurburshiedProductCharge {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? desc;
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
  dynamic primaryKey;
  bool? deleteFlag;

  RefurburshiedProductCharge(
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
        this.primaryKey,
        this.deleteFlag});

  RefurburshiedProductCharge.fromJson(Map<String, dynamic> json) {
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
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
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
  String? approvalRemark;
  dynamic type;
  dynamic requestInventoryId;
  dynamic requestInventoryProductId;
  dynamic selectedItems;
  dynamic description;

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
        this.selectedItems,
        this.description});

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
    description = json['description'];
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
    data['description'] = this.description;
    return data;
  }
}

class ItemList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? macAddress;
  String? serialNumber;
  dynamic mvnoId;
  String? condition;
  dynamic productId;
  dynamic currentInwardId;
  dynamic ownerId;
  String? ownerType;
  dynamic warrantyPeriod;
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
  dynamic invenSpecId;
  String? assetId;
  dynamic oemStartDate;
  dynamic oemEndDate;
  dynamic oemWarrantyRemainingDays;
  dynamic oemWarrantyStatus;
  dynamic productRefundAmount;
  bool? refundFlag;
  dynamic remarks;
  dynamic removeFrom;
  dynamic primaryKey;
  bool? deleteFlag;

  ItemList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.macAddress,
        this.serialNumber,
        this.mvnoId,
        this.condition,
        this.productId,
        this.currentInwardId,
        this.ownerId,
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
        this.invenSpecId,
        this.assetId,
        this.oemStartDate,
        this.oemEndDate,
        this.oemWarrantyRemainingDays,
        this.oemWarrantyStatus,
        this.productRefundAmount,
        this.refundFlag,
        this.remarks,
        this.removeFrom,
        this.primaryKey,
        this.deleteFlag});

  ItemList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    condition = json['condition'];
    productId = json['productId'];
    currentInwardId = json['currentInwardId'];
    ownerId = json['ownerId'];
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
    invenSpecId = json['invenSpecId'];
    assetId = json['assetId'];
    oemStartDate = json['oemStartDate'];
    oemEndDate = json['oemEndDate'];
    oemWarrantyRemainingDays = json['oemWarrantyRemainingDays'];
    oemWarrantyStatus = json['oemWarrantyStatus'];
    productRefundAmount = json['productRefundAmount'];
    refundFlag = json['refundFlag'];
    remarks = json['remarks'];
    removeFrom = json['removeFrom'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['condition'] = this.condition;
    data['productId'] = this.productId;
    data['currentInwardId'] = this.currentInwardId;
    data['ownerId'] = this.ownerId;
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
    data['invenSpecId'] = this.invenSpecId;
    data['assetId'] = this.assetId;
    data['oemStartDate'] = this.oemStartDate;
    data['oemEndDate'] = this.oemEndDate;
    data['oemWarrantyRemainingDays'] = this.oemWarrantyRemainingDays;
    data['oemWarrantyStatus'] = this.oemWarrantyStatus;
    data['productRefundAmount'] = this.productRefundAmount;
    data['refundFlag'] = this.refundFlag;
    data['remarks'] = this.remarks;
    data['removeFrom'] = this.removeFrom;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}
