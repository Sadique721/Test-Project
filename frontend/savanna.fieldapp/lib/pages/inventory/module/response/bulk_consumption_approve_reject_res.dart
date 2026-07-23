class BulkConApproveRejectRes {
  int? responseCode;
  String? responseMessage;
  BulkConsumptionData? data;
  Null? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  BulkConApproveRejectRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  BulkConApproveRejectRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? BulkConsumptionData.fromJson(json['data']) : null;
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

class BulkConsumptionData {
  int? id;
  String? bulkConsumptionName;
  int? mvnoId;
  bool? isDeleted;
  int? productId;
  String? productName;
  dynamic inwardId;
  dynamic inwardNumber;
  String? approvalStatus;
  String? approvalRemark;
  int? qty;
  String? itemType;
  int? ownerId;
  String? ownerType;
  List<int>? itemListLongId;

  BulkConsumptionData(
      {this.id,
        this.bulkConsumptionName,
        this.mvnoId,
        this.isDeleted,
        this.productId,
        this.productName,
        this.inwardId,
        this.inwardNumber,
        this.approvalStatus,
        this.approvalRemark,
        this.qty,
        this.itemType,
        this.ownerId,
        this.ownerType,
        this.itemListLongId,
      });

  BulkConsumptionData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    bulkConsumptionName = json['bulkConsumptionName'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    productId = json['productId'];
    productName = json['productName'];
    inwardId = json['inwardId'];
    inwardNumber = json['inwardNumber'];
    approvalStatus = json['approvalStatus'];
    approvalRemark = json['approvalRemark'];
    qty = json['qty'];
    itemType = json['itemType'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    itemListLongId = json['itemListLongId'].cast<int>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['bulkConsumptionName'] = this.bulkConsumptionName;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['productId'] = this.productId;
    data['productName'] = this.productName;
    data['inwardId'] = this.inwardId;
    data['inwardNumber'] = this.inwardNumber;
    data['approvalStatus'] = this.approvalStatus;
    data['approvalRemark'] = this.approvalRemark;
    data['qty'] = this.qty;
    data['itemType'] = this.itemType;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['itemListLongId'] = this.itemListLongId;
    return data;
  }
}
