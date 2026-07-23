class GetNonTrackableProductQtyRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<NonTrackableProductDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetNonTrackableProductQtyRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetNonTrackableProductQtyRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <NonTrackableProductDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add( NonTrackableProductDataList.fromJson(v));
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

class NonTrackableProductDataList {
  dynamic id;
  dynamic productId;
  dynamic ownerId;
  String? ownerType;
  dynamic quantity;
  dynamic usedQty;
  dynamic unusedQty;
  dynamic inTransitQty;
  dynamic  boundQty;
  dynamic  mvnoId;
  String? productName;
  int? identityKey;

  NonTrackableProductDataList(
      {this.id,
        this.productId,
        this.ownerId,
        this.ownerType,
        this.quantity,
        this.usedQty,
        this.unusedQty,
        this.inTransitQty,
        this.boundQty,
        this.mvnoId,
        this.productName,
        this.identityKey});

  NonTrackableProductDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    quantity = json['quantity'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inTransitQty = json['inTransitQty'];
    boundQty = json['boundQty'];
    mvnoId = json['mvnoId'];
    productName = json['productName'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['quantity'] = this.quantity;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inTransitQty'] = this.inTransitQty;
    data['boundQty'] = this.boundQty;
    data['mvnoId'] = this.mvnoId;
    data['productName'] = this.productName;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
