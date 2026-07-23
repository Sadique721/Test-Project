class AllPlanInventoryPlanIdRes {
  int? responseCode;
  String? responseMessage;
  List<AllPlanInventoryDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  AllPlanInventoryPlanIdRes(
      {this.responseCode,
        this.responseMessage,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  AllPlanInventoryPlanIdRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <AllPlanInventoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AllPlanInventoryDataList.fromJson(v));
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

class AllPlanInventoryDataList {
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
  int? productQuantity;
  dynamic mvnoId;
  int? identityKey;

  AllPlanInventoryDataList(
      {this.id,
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
        this.productQuantity,
        this.mvnoId,
        this.identityKey});

  AllPlanInventoryDataList.fromJson(Map<String, dynamic> json) {
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
    productQuantity = json['productQuantity'];
    mvnoId = json['mvnoId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['productQuantity'] = this.productQuantity;
    data['mvnoId'] = this.mvnoId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
