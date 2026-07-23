class GetProductMappingDetailsRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ProductMappingDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetProductMappingDetailsRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetProductMappingDetailsRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ProductMappingDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ProductMappingDataList.fromJson(v));
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

class ProductMappingDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? planId;
  int? productCategoryId;
  String? productType;
  int? productId;
  String? revisedCharge;
  String? ownershipType;
  String? name;
  dynamic productCategoryName;
  dynamic productName;
  dynamic planName;
  bool? deleteFlag;
  int? primaryKey;

  ProductMappingDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.planId,
        this.productCategoryId,
        this.productType,
        this.productId,
        this.revisedCharge,
        this.ownershipType,
        this.name,
        this.productCategoryName,
        this.productName,
        this.planName,
        this.deleteFlag,
        this.primaryKey});

  ProductMappingDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    planId = json['planId'];
    productCategoryId = json['productCategoryId'];
    productType = json['product_type'];
    productId = json['productId'];
    revisedCharge = json['revisedCharge'];
    ownershipType = json['ownershipType'];
    name = json['name'];
    productCategoryName = json['productCategoryName'];
    productName = json['productName'];
    planName = json['planName'];
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
    data['planId'] = this.planId;
    data['productCategoryId'] = this.productCategoryId;
    data['product_type'] = this.productType;
    data['productId'] = this.productId;
    data['revisedCharge'] = this.revisedCharge;
    data['ownershipType'] = this.ownershipType;
    data['name'] = this.name;
    data['productCategoryName'] = this.productCategoryName;
    data['productName'] = this.productName;
    data['planName'] = this.planName;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
