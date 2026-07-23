import 'package:savbill/webservices/base_response.dart';

class OutwardsDetailsRes extends BaseResponse{
  String? responseMessage;
  OutWardsData? data;
  dynamic dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  OutwardsDetailsRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  OutwardsDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? OutWardsData.fromJson(json['data']) : null;
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

class OutWardsData {
  dynamic id;
  String? outwardNumber;
  dynamic qty;
  String? status;
  ProductId? productId;
  dynamic wareHouseId;
  dynamic staffId;
  dynamic productCategory;
  dynamic customerId;
  dynamic mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  dynamic inwardId;
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
  dynamic serviceAreaId;
  dynamic outTransitQty;
  dynamic rejectedQty;
  String? approvalStatus;
  String? categoryType;
  dynamic rmsOutwardId;
  dynamic navOutwardId;
  dynamic type;
  String? createdBy;
  String? approvalRemark;
  dynamic description;
  dynamic outwardsInwardId;
  dynamic requestInventoryId;
  dynamic requestInventoryProductId;
  dynamic selectedItems;
  String? source;
  String? destination;
  dynamic identityKey;

  OutWardsData(
      {this.id,
        this.outwardNumber,
        this.qty,
        this.status,
        this.productId,
        this.wareHouseId,
        this.staffId,
        this.productCategory,
        this.customerId,
        this.mvnoId,
        this.outwardDateTime,
        this.isDeleted,
        this.inwardId,
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
        this.serviceAreaId,
        this.outTransitQty,
        this.rejectedQty,
        this.approvalStatus,
        this.categoryType,
        this.rmsOutwardId,
        this.navOutwardId,
        this.type,
        this.createdBy,
        this.approvalRemark,
        this.description,
        this.outwardsInwardId,
        this.requestInventoryId,
        this.requestInventoryProductId,
        this.selectedItems,
        this.source,
        this.destination,
        this.identityKey});

  OutWardsData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    outwardNumber = json['outwardNumber'];
    qty = json['qty'];
    status = json['status'];
    productId = json['productId'] != null
        ? new ProductId.fromJson(json['productId'])
        : null;
    wareHouseId = json['wareHouseId'];
    staffId = json['staffId'];
    productCategory = json['productCategory'];
    customerId = json['customerId'];
    mvnoId = json['mvnoId'];
    outwardDateTime = json['outwardDateTime'];
    isDeleted = json['isDeleted'];
    inwardId = json['inwardId'];
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
    serviceAreaId = json['serviceAreaId'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    categoryType = json['categoryType'];
    rmsOutwardId = json['rmsOutwardId'];
    navOutwardId = json['navOutwardId'];
    type = json['type'];
    createdBy = json['createdBy'];
    approvalRemark = json['approvalRemark'];
    description = json['description'];
    outwardsInwardId = json['outwardsInwardId'];
    requestInventoryId = json['requestInventoryId'];
    requestInventoryProductId = json['requestInventoryProductId'];
    selectedItems = json['selectedItems'];
    source = json['source'];
    destination = json['destination'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['outwardNumber'] = this.outwardNumber;
    data['qty'] = this.qty;
    data['status'] = this.status;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    data['wareHouseId'] = this.wareHouseId;
    data['staffId'] = this.staffId;
    data['productCategory'] = this.productCategory;
    data['customerId'] = this.customerId;
    data['mvnoId'] = this.mvnoId;
    data['outwardDateTime'] = this.outwardDateTime;
    data['isDeleted'] = this.isDeleted;
    data['inwardId'] = this.inwardId;
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
    data['serviceAreaId'] = this.serviceAreaId;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['categoryType'] = this.categoryType;
    data['rmsOutwardId'] = this.rmsOutwardId;
    data['navOutwardId'] = this.navOutwardId;
    data['type'] = this.type;
    data['createdBy'] = this.createdBy;
    data['approvalRemark'] = this.approvalRemark;
    data['description'] = this.description;
    data['outwardsInwardId'] = this.outwardsInwardId;
    data['requestInventoryId'] = this.requestInventoryId;
    data['requestInventoryProductId'] = this.requestInventoryProductId;
    data['selectedItems'] = this.selectedItems;
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
  String? productId;
  bool? isDeleted;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory;
  String? deviceType;
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
        this.deviceType,
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
    deviceType = json['deviceType'];
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
    data['deviceType'] = this.deviceType;
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
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  String? name;
  String? status;
  dynamic mvnoId;
  bool? deleteFlag;
  bool? deleted;
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
        this.deleteFlag,
        this.deleted,
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
    deleteFlag = json['deleteFlag'];
    deleted = json['deleted'];
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
    data['deleteFlag'] = this.deleteFlag;
    data['deleted'] = this.deleted;
    data['primaryKey'] = this.primaryKey;
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
  bool? deleteFlag;
  dynamic primaryKey;

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
        this.deleteFlag,
        this.primaryKey});

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
