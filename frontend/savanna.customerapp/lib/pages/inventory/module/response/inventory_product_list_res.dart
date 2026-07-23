import 'package:savbill/webservices/base_response.dart';

class InventoryProductListRes extends BaseResponse {
  List<ProductDetail>? dataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  InventoryProductListRes(
      {responseCode,
      responseMessage,
      this.dataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  InventoryProductListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ProductDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ProductDetail.fromJson(v));
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

class ProductDetail {
  int? id;
  String? name;
  String? description;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  int? chargeId;
  int? expiryTime;
  String? expiryTimeUnit;
  String? refundAmount;
  ProductCategory? productCategory;
  // int? productCategory;
  dynamic availableInPorts;
  dynamic totalInPorts;
  dynamic availableOutPorts;
  dynamic totalOutPorts;
  dynamic productId;
  dynamic navLedgerId;


  // //new response
  int? vendorId;

  dynamic newProductCharge;
  dynamic refurburshiedProductRefAmountPostWarranty;
  dynamic refurburshiedProductRefAmountInWarranty;
  dynamic newProductRefAmountInWarranty;
  dynamic newProductRefAmountPostWarranty;
  dynamic caseId;
  String? newProductTaxName;
  String? refurburshiedProductTaxName;

  dynamic actualpricenewProduct;
  dynamic actualpricerefurbishedProduct;

  ProductDetail(
      {this.id,
      this.name,
      this.description,
      this.status,
      this.mvnoId,
      this.isDeleted,
      this.chargeId,
      this.expiryTime,
      this.expiryTimeUnit,
      this.refundAmount,
      this.productCategory,
      this.availableInPorts,
      this.totalInPorts,
      this.availableOutPorts,
      this.totalOutPorts,
      this.productId,
      this.navLedgerId,

      //new response
        this.vendorId,
      this.newProductCharge,
      this.refurburshiedProductRefAmountInWarranty,
      this.refurburshiedProductRefAmountPostWarranty,
      this.newProductRefAmountInWarranty,
      this.newProductRefAmountPostWarranty,
      this.caseId,
        this.newProductTaxName,
        this.refurburshiedProductTaxName,
      this.actualpricenewProduct,
      this.actualpricerefurbishedProduct
      });

  ProductDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    chargeId = json['chargeId'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refundAmount = json['refundAmount'];
    // productCategory = json['productCategory'];
    productCategory = json['productCategory'] != null
        ? new ProductCategory.fromJson(json['productCategory'])
        : null;
    availableInPorts = json['availableInPorts'];
    totalInPorts = json['totalInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalOutPorts = json['totalOutPorts'];
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];

    //new response
    vendorId = json['vendorId'];
    newProductCharge = json['newProductCharge'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    caseId = json['caseId'];
    newProductTaxName = json['newProductTaxName'];
    refurburshiedProductTaxName = json['refurburshiedProductTaxName'];
    actualpricenewProduct = json['actualpricenewProduct'];
    actualpricerefurbishedProduct = json['actualpricerefurbishedProduct'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['chargeId'] = this.chargeId;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refundAmount'] = this.refundAmount;
    // data['productCategory'] = this.productCategory;
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    data['availableInPorts'] = this.availableInPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;


    //new response
    data['vendorId'] = this.vendorId;
    data['newProductCharge'] = this.newProductCharge;
    data['refurburshiedProductRefAmountInWarranty'] =
        this.refurburshiedProductRefAmountInWarranty;
    data['refurburshiedProductRefAmountPostWarranty'] =
        this.refurburshiedProductRefAmountPostWarranty;
    data['newProductRefAmountInWarranty'] = this.newProductRefAmountInWarranty;
    data['newProductRefAmountPostWarranty'] =
        this.newProductRefAmountPostWarranty;
    data['caseId'] = this.caseId;
    data['newProductTaxName'] = this.newProductTaxName;
    data['refurburshiedProductTaxName'] = this.refurburshiedProductTaxName;
    data['actualpricenewProduct'] = this.actualpricenewProduct;
    data['actualpricerefurbishedProduct'] = this.actualpricerefurbishedProduct;
    return data;
  }
}

class ProductCategory {
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
      this.isDeleted,
      this.hasSerial,
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
