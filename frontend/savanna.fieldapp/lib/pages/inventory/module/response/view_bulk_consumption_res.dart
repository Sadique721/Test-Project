import 'package:savbill/webservices/base_response.dart';

class ViewBulkConsumptionRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<BulkConsumptionDetail>? dataList;

  ViewBulkConsumptionRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewBulkConsumptionRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <BulkConsumptionDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BulkConsumptionDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class BulkConsumptionDetail {
  int? id;
  String? bulkConsumptionName;
  int? mvnoId;
  bool? isDeleted;
  int? productId;
  String? productName;
  int? inwardId;
  String? inwardNumber;
  String? approvalStatus;
  String? approvalRemark;
  List<int>? itemListLongId;
  int? qty;
  String? itemType;
  int? ownerId;
  String? ownerType;


  BulkConsumptionDetail(
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
      this.itemListLongId,
      this.qty,
      this.ownerType,
      this.ownerId,
      this.itemType});

  BulkConsumptionDetail.fromJson(Map<String, dynamic> json) {
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
    itemListLongId = json['itemListLongId'].cast<int>();
    qty = json['qty'];
    itemType = json['itemType'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
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
    data['itemListLongId'] = this.itemListLongId;
    data['qty'] = this.qty;
    data['itemType'] = this.itemType;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;

    return data;
  }
}
