import 'package:savbill/webservices/base_response.dart';

class ExternalItemDetailsRes extends BaseResponse{
  // int? responseCode;
  String? responseMessage;
  ExternalItemDetailsData? data;
  // dynamic dataList;
  // dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ExternalItemDetailsRes(
      {
        // this.responseCode,
        this.responseMessage,
        this.data,
        // this.dataList,
        // this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ExternalItemDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new ExternalItemDetailsData.fromJson(json['data']) : null;
    // dataList = json['dataList'];
    // excelDataList = json['excelDataList'];
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
    // data['dataList'] = this.dataList;
    // data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ExternalItemDetailsData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  ProductId? productId;
  int? qty;
  int? usedQty;
  int? unusedQty;
  String? ownershipType;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  ServiceAreaId? serviceAreaId;
  int? inTransitQty;
  int? rejectedQty;
  String? approvalStatus;
  String? externalItemGroupNumber;
  int? totalMacSerial;
  String? approvalRemark;
  String? ownerName;
  int? ownerId;
  int? identityKey;

  ExternalItemDetailsData(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
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
        this.externalItemGroupNumber,
        this.totalMacSerial,
        this.approvalRemark,
        this.ownerName,
        this.ownerId,
        this.identityKey});

  ExternalItemDetailsData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    productId = json['productId'] != null
        ? new ProductId.fromJson(json['productId'])
        : null;
    qty = json['qty'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    ownershipType = json['ownershipType'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    serviceAreaId = json['serviceAreaId'] != null
        ? new ServiceAreaId.fromJson(json['serviceAreaId'])
        : null;
    inTransitQty = json['inTransitQty'];
    rejectedQty = json['rejectedQty'];
    approvalStatus = json['approvalStatus'];
    externalItemGroupNumber = json['externalItemGroupNumber'];
    totalMacSerial = json['totalMacSerial'];
    approvalRemark = json['approvalRemark'];
    ownerName = json['ownerName'];
    ownerId = json['ownerId'];
    identityKey = json['identityKey'];
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
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    data['qty'] = this.qty;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['ownershipType'] = this.ownershipType;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    if (this.serviceAreaId != null) {
      data['serviceAreaId'] = this.serviceAreaId!.toJson();
    }
    data['inTransitQty'] = this.inTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['approvalStatus'] = this.approvalStatus;
    data['externalItemGroupNumber'] = this.externalItemGroupNumber;
    data['totalMacSerial'] = this.totalMacSerial;
    data['approvalRemark'] = this.approvalRemark;
    data['ownerName'] = this.ownerName;
    data['ownerId'] = this.ownerId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class ProductId {
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
  String? productId;
  dynamic navLedgerId;
  bool? hasOEMConsider;
  dynamic hasAssetConsider;
  bool? isDeleted;
  // ProductCategory? productCategory;
  int? expiryTime;
  String? expiryTimeUnit;
  double? refurburshiedProductRefAmountInWarranty;
  double? refurburshiedProductRefAmountPostWarranty;
  double? newProductRefAmountInWarranty;
  double? newProductRefAmountPostWarranty;
  dynamic caseId;
  // Vendor? vendor;
  // NewProductCharge? newProductCharge;
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
  dynamic specificationParametersDTOList;
  bool? deleteFlag;
  int? primaryKey;

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
        // this.productCategory,
        this.expiryTime,
        this.expiryTimeUnit,
        this.refurburshiedProductRefAmountInWarranty,
        this.refurburshiedProductRefAmountPostWarranty,
        this.newProductRefAmountInWarranty,
        this.newProductRefAmountPostWarranty,
        this.caseId,
        // this.vendor,
        // this.newProductCharge,
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
    // productCategory = json['productCategory'] != null
    //     ? new ProductCategory.fromJson(json['productCategory'])
    //     : null;
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    caseId = json['caseId'];
    // vendor =
    // json['vendor'] != null ? new Vendor.fromJson(json['vendor']) : null;
    // newProductCharge = json['newProductCharge'] != null
    //     ? new NewProductCharge.fromJson(json['newProductCharge'])
    //     : null;
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
    // if (this.productCategory != null) {
    //   data['productCategory'] = this.productCategory!.toJson();
    // }
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
    // if (this.vendor != null) {
    //   data['vendor'] = this.vendor!.toJson();
    // }
    // if (this.newProductCharge != null) {
    //   data['newProductCharge'] = this.newProductCharge!.toJson();
    // }
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
    data['specificationParametersDTOList'] =
        this.specificationParametersDTOList;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
class ServiceAreaId {
  int? id;
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? latitude;
  String? longitude;
  dynamic areaId;
  // List<PincodeList>? pincodeList;
  int? cityid;

  ServiceAreaId(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.latitude,
        this.longitude,
        this.areaId,
        // this.pincodeList,
        this.cityid});

  ServiceAreaId.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    // if (json['pincodeList'] != null) {
    //   pincodeList = <PincodeList>[];
    //   json['pincodeList'].forEach((v) {
    //     pincodeList!.add(new PincodeList.fromJson(v));
    //   });
    // }
    cityid = json['cityid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    // if (this.pincodeList != null) {
    //   data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
    // }
    data['cityid'] = this.cityid;
    return data;
  }
}


