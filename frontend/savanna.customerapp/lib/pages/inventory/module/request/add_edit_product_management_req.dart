class AddEditProductManagementReq {
  int? id;
  String? name;
  String? productId;
  String? navLedgerId;
  String? description;
  int? productCategory;
  String? status;
  Null? newPrice;
  double? actualpricenewProduct;
  double? actualpricerefurbishedProduct;
  Null? newProductCharge;
  int? newProductTax;
  double? newProductRefAmountInWarranty;
  double? newProductRefAmountPostWarranty;
  Null? refurburshiedPrice;
  Null? refurburshiedProductCharge;
  int? refurburshiedProductTax;
  double? refurburshiedProductRefAmountInWarranty;
  double? refurburshiedProductRefAmountPostWarranty;
  int? expiryTime;
  String? expiryTimeUnit;
  int? totalInPorts;
  int? totalOutPorts;
  int? availableInPorts;
  int? availableOutPorts;
  int? caseId;
  int? vendorId;
  bool? hasAssetConsider = false;
  bool? hasOEMConsider = false;

  AddEditProductManagementReq(
      {this.id,
        this.name,
        this.productId,
        this.navLedgerId,
        this.description,
        this.productCategory,
        this.status,
        this.newPrice,
        this.actualpricenewProduct,
        this.actualpricerefurbishedProduct,
        this.newProductCharge,
        this.newProductTax,
        this.newProductRefAmountInWarranty,
        this.newProductRefAmountPostWarranty,
        this.refurburshiedPrice,
        this.refurburshiedProductCharge,
        this.refurburshiedProductTax,
        this.refurburshiedProductRefAmountInWarranty,
        this.refurburshiedProductRefAmountPostWarranty,
        this.expiryTime,
        this.expiryTimeUnit,
        this.totalInPorts,
        this.totalOutPorts,
        this.availableInPorts,
        this.availableOutPorts,
        this.caseId,
        this.vendorId,
      this.hasAssetConsider,
      this.hasOEMConsider});

  AddEditProductManagementReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];
    description = json['description'];
    productCategory = json['productCategory'];
    status = json['status'];
    newPrice = json['newPrice'];
    actualpricenewProduct = json['actualpricenewProduct'];
    actualpricerefurbishedProduct = json['actualpricerefurbishedProduct'];
    newProductCharge = json['newProductCharge'];
    newProductTax = json['newProductTax'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    refurburshiedPrice = json['refurburshiedPrice'];
    refurburshiedProductCharge = json['refurburshiedProductCharge'];
    refurburshiedProductTax = json['refurburshiedProductTax'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    totalInPorts = json['totalInPorts'];
    totalOutPorts = json['totalOutPorts'];
    availableInPorts = json['availableInPorts'];
    availableOutPorts = json['availableOutPorts'];
    caseId = json['caseId'];
    vendorId = json['vendorId'];
    hasAssetConsider = json['hasAssetConsider'];
    hasOEMConsider = json['hasOEMConsider'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;
    data['description'] = this.description;
    data['productCategory'] = this.productCategory;
    data['status'] = this.status;
    data['newPrice'] = this.newPrice;
    data['actualpricenewProduct'] = this.actualpricenewProduct;
    data['actualpricerefurbishedProduct'] = this.actualpricerefurbishedProduct;
    data['newProductCharge'] = this.newProductCharge;
    data['newProductTax'] = this.newProductTax;
    data['newProductRefAmountInWarranty'] = this.newProductRefAmountInWarranty;
    data['newProductRefAmountPostWarranty'] =
        this.newProductRefAmountPostWarranty;
    data['refurburshiedPrice'] = this.refurburshiedPrice;
    data['refurburshiedProductCharge'] = this.refurburshiedProductCharge;
    data['refurburshiedProductTax'] = this.refurburshiedProductTax;
    data['refurburshiedProductRefAmountInWarranty'] =
        this.refurburshiedProductRefAmountInWarranty;
    data['refurburshiedProductRefAmountPostWarranty'] =
        this.refurburshiedProductRefAmountPostWarranty;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['totalInPorts'] = this.totalInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['availableInPorts'] = this.availableInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['caseId'] = this.caseId;
    data['vendorId'] = this.vendorId;
    data['hasAssetConsider'] = this.hasAssetConsider;
    data['hasOEMConsider'] = this.hasOEMConsider;
    return data;
  }
}
