class GetAllActiveProductsByProductCategoryRes {
  int? responseCode;
  String? responseMessage;
  dynamic data; // replaced Null?
  List<AllActiveProductsByProductData>? dataList;
  dynamic excelDataList; // replaced Null?
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllActiveProductsByProductCategoryRes({
    this.responseCode,
    this.responseMessage,
    this.data,
    this.dataList,
    this.excelDataList,
    this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages,
  });

  factory GetAllActiveProductsByProductCategoryRes.fromJson(
      Map<String, dynamic> json) {
    return GetAllActiveProductsByProductCategoryRes(
      responseCode: json['responseCode'],
      responseMessage: json['responseMessage'],
      data: json['data'],
      dataList: json['dataList'] != null
          ? (json['dataList'] as List)
          .map((e) => AllActiveProductsByProductData.fromJson(e))
          .toList()
          : null,
      excelDataList: json['excelDataList'],
      totalRecords: json['totalRecords'],
      pageRecords: json['pageRecords'],
      currentPageNumber: json['currentPageNumber'],
      totalPages: json['totalPages'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'responseCode': responseCode,
      'responseMessage': responseMessage,
      'data': data,
      'dataList': dataList?.map((e) => e.toJson()).toList(),
      'excelDataList': excelDataList,
      'totalRecords': totalRecords,
      'pageRecords': pageRecords,
      'currentPageNumber': currentPageNumber,
      'totalPages': totalPages,
    };
  }
}

class AllActiveProductsByProductData {
  int? id;
  String? name;
  String? description;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  bool? hasOEMConsider;
  int? expiryTime;
  String? expiryTimeUnit;
  dynamic refurburshiedProductCharge;
  ProductCategory? productCategory;
  int? availableInPorts;
  int? totalInPorts;
  int? availableOutPorts;
  int? totalOutPorts;
  String? productId;
  String? navLedgerId;
  dynamic newProductCharge;
  dynamic refurburshiedProductRefAmountInWarranty;
  dynamic refurburshiedProductRefAmountPostWarranty;
  dynamic newProductRefAmountInWarranty;
  dynamic newProductRefAmountPostWarranty;
  dynamic newProductAmount;
  dynamic refurburshiedProductAmount;
  dynamic caseId;
  int? vendorId;
  dynamic newPrice;
  dynamic refurburshiedPrice;
  int? refurburshiedProductTax;
  int? newProductTax;
  dynamic refurburshiedProductTaxName;
  dynamic newProductTaxName;
  dynamic actualpricenewProduct;
  dynamic actualpricerefurbishedProduct;

  AllActiveProductsByProductData({
    this.id,
    this.name,
    this.description,
    this.status,
    this.mvnoId,
    this.isDeleted,
    this.hasOEMConsider,
    this.expiryTime,
    this.expiryTimeUnit,
    this.refurburshiedProductCharge,
    this.productCategory,
    this.availableInPorts,
    this.totalInPorts,
    this.availableOutPorts,
    this.totalOutPorts,
    this.productId,
    this.navLedgerId,
    this.newProductCharge,
    this.refurburshiedProductRefAmountInWarranty,
    this.refurburshiedProductRefAmountPostWarranty,
    this.newProductRefAmountInWarranty,
    this.newProductRefAmountPostWarranty,
    this.newProductAmount,
    this.refurburshiedProductAmount,
    this.caseId,
    this.vendorId,
    this.newPrice,
    this.refurburshiedPrice,
    this.refurburshiedProductTax,
    this.newProductTax,
    this.refurburshiedProductTaxName,
    this.newProductTaxName,
    this.actualpricenewProduct,
    this.actualpricerefurbishedProduct,
  });

  factory AllActiveProductsByProductData.fromJson(Map<String, dynamic> json) {
    return AllActiveProductsByProductData(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      status: json['status'],
      mvnoId: json['mvnoId'],
      isDeleted: json['isDeleted'],
      hasOEMConsider: json['hasOEMConsider'],
      expiryTime: json['expiryTime'],
      expiryTimeUnit: json['expiryTimeUnit'],
      refurburshiedProductCharge: json['refurburshiedProductCharge'],
      productCategory: json['productCategory'] != null
          ? ProductCategory.fromJson(json['productCategory'])
          : null,
      availableInPorts: json['availableInPorts'],
      totalInPorts: json['totalInPorts'],
      availableOutPorts: json['availableOutPorts'],
      totalOutPorts: json['totalOutPorts'],
      productId: json['productId'],
      navLedgerId: json['navLedgerId'],
      newProductCharge: json['newProductCharge'],
      refurburshiedProductRefAmountInWarranty:
      json['refurburshiedProductRefAmountInWarranty'],
      refurburshiedProductRefAmountPostWarranty:
      json['refurburshiedProductRefAmountPostWarranty'],
      newProductRefAmountInWarranty: json['newProductRefAmountInWarranty'],
      newProductRefAmountPostWarranty: json['newProductRefAmountPostWarranty'],
      newProductAmount: json['newProductAmount'],
      refurburshiedProductAmount: json['refurburshiedProductAmount'],
      caseId: json['caseId'],
      vendorId: json['vendorId'],
      newPrice: json['newPrice'],
      refurburshiedPrice: json['refurburshiedPrice'],
      refurburshiedProductTax: json['refurburshiedProductTax'],
      newProductTax: json['newProductTax'],
      refurburshiedProductTaxName: json['refurburshiedProductTaxName'],
      newProductTaxName: json['newProductTaxName'],
      actualpricenewProduct: json['actualpricenewProduct'],
      actualpricerefurbishedProduct: json['actualpricerefurbishedProduct'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'status': status,
      'mvnoId': mvnoId,
      'isDeleted': isDeleted,
      'hasOEMConsider': hasOEMConsider,
      'expiryTime': expiryTime,
      'expiryTimeUnit': expiryTimeUnit,
      'refurburshiedProductCharge': refurburshiedProductCharge,
      'productCategory': productCategory?.toJson(),
      'availableInPorts': availableInPorts,
      'totalInPorts': totalInPorts,
      'availableOutPorts': availableOutPorts,
      'totalOutPorts': totalOutPorts,
      'productId': productId,
      'navLedgerId': navLedgerId,
      'newProductCharge': newProductCharge,
      'refurburshiedProductRefAmountInWarranty':
      refurburshiedProductRefAmountInWarranty,
      'refurburshiedProductRefAmountPostWarranty':
      refurburshiedProductRefAmountPostWarranty,
      'newProductRefAmountInWarranty': newProductRefAmountInWarranty,
      'newProductRefAmountPostWarranty': newProductRefAmountPostWarranty,
      'newProductAmount': newProductAmount,
      'refurburshiedProductAmount': refurburshiedProductAmount,
      'caseId': caseId,
      'vendorId': vendorId,
      'newPrice': newPrice,
      'refurburshiedPrice': refurburshiedPrice,
      'refurburshiedProductTax': refurburshiedProductTax,
      'newProductTax': newProductTax,
      'refurburshiedProductTaxName': refurburshiedProductTaxName,
      'newProductTaxName': newProductTaxName,
      'actualpricenewProduct': actualpricenewProduct,
      'actualpricerefurbishedProduct': actualpricerefurbishedProduct,
    };
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
  String? productId;
  bool? isDeleted;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic dtvCategory; // changed
  bool? deleteFlag;
  int? primaryKey;

  ProductCategory({
    this.createdate,
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
    this.primaryKey,
  });

  factory ProductCategory.fromJson(Map<String, dynamic> json) {
    return ProductCategory(
      createdate: json['createdate'],
      updatedate: json['updatedate'],
      createdByName: json['createdByName'],
      lastModifiedByName: json['lastModifiedByName'],
      createdById: json['createdById'],
      lastModifiedById: json['lastModifiedById'],
      id: json['id'],
      name: json['name'],
      unit: json['unit'],
      mvnoId: json['mvnoId'],
      hasMac: json['hasMac'],
      type: json['type'],
      status: json['status'],
      productId: json['productId'],
      isDeleted: json['isDeleted'],
      hasSerial: json['hasSerial'],
      hasTrackable: json['hasTrackable'],
      hasPort: json['hasPort'],
      hasCas: json['hasCas'],
      dtvCategory: json['dtvCategory'],
      deleteFlag: json['deleteFlag'],
      primaryKey: json['primaryKey'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'createdate': createdate,
      'updatedate': updatedate,
      'createdByName': createdByName,
      'lastModifiedByName': lastModifiedByName,
      'createdById': createdById,
      'lastModifiedById': lastModifiedById,
      'id': id,
      'name': name,
      'unit': unit,
      'mvnoId': mvnoId,
      'hasMac': hasMac,
      'type': type,
      'status': status,
      'productId': productId,
      'isDeleted': isDeleted,
      'hasSerial': hasSerial,
      'hasTrackable': hasTrackable,
      'hasPort': hasPort,
      'hasCas': hasCas,
      'dtvCategory': dtvCategory,
      'deleteFlag': deleteFlag,
      'primaryKey': primaryKey,
    };
  }
}
