class GetAllProductByCategroyIdRes {
  dynamic id;
  String? name;
  String? description;
  String? status;
  dynamic mvnoId;
  bool? isDeleted;
  dynamic expiryTime;
  String? expiryTimeUnit;
  dynamic refurburshiedProductCharge;
  ProductCategory? productCategory;
  dynamic availableInPorts;
  dynamic totalInPorts;
  dynamic availableOutPorts;
  dynamic totalOutPorts;
  dynamic productId;
  dynamic navLedgerId;
  dynamic newProductCharge;
  dynamic refurburshiedProductRefAmountInWarranty;
  dynamic refurburshiedProductRefAmountPostWarranty;
  dynamic newProductRefAmountInWarranty;
  dynamic newProductRefAmountPostWarranty;
  dynamic caseId;

  GetAllProductByCategroyIdRes(
      {this.id,
        this.name,
        this.description,
        this.status,
        this.mvnoId,
        this.isDeleted,
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
        this.caseId});

  GetAllProductByCategroyIdRes.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refurburshiedProductCharge = json['refurburshiedProductCharge'];
    productCategory = json['productCategory'] != null
        ? new ProductCategory.fromJson(json['productCategory'])
        : null;
    availableInPorts = json['availableInPorts'];
    totalInPorts = json['totalInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalOutPorts = json['totalOutPorts'];
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];
    newProductCharge = json['newProductCharge'];
    refurburshiedProductRefAmountInWarranty =
    json['refurburshiedProductRefAmountInWarranty'];
    refurburshiedProductRefAmountPostWarranty =
    json['refurburshiedProductRefAmountPostWarranty'];
    newProductRefAmountInWarranty = json['newProductRefAmountInWarranty'];
    newProductRefAmountPostWarranty = json['newProductRefAmountPostWarranty'];
    caseId = json['caseId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refurburshiedProductCharge'] = this.refurburshiedProductCharge;
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    data['availableInPorts'] = this.availableInPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;
    data['newProductCharge'] = this.newProductCharge;
    data['refurburshiedProductRefAmountInWarranty'] =
        this.refurburshiedProductRefAmountInWarranty;
    data['refurburshiedProductRefAmountPostWarranty'] =
        this.refurburshiedProductRefAmountPostWarranty;
    data['newProductRefAmountInWarranty'] = this.newProductRefAmountInWarranty;
    data['newProductRefAmountPostWarranty'] =
        this.newProductRefAmountPostWarranty;
    data['caseId'] = this.caseId;
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
  String? dtvCategory;
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
