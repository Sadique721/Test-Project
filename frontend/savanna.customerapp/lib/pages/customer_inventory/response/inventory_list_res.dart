import 'package:savbill/webservices/base_response.dart';

class InventoryListRes extends BaseResponse {
  List<InventoryDetail>? dataList;
  int? currentPageNumber;
  int? totalPages;

  InventoryListRes({responseCode, responseMessage, this.dataList,this.currentPageNumber, this.totalPages});

  InventoryListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <InventoryDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InventoryDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class InventoryDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  num? qty;
  int? productId;
  int? outwardId;
  String? assignedDateTime;
  int? mvnoId;
  bool? isDeleted;
  String? status;
  String? outwardNumber;
  String? productName;
  String? customerName;
  int? custId;
  int? outId;
  bool? hasMac;
  bool? hasSerial;
  bool? deleteFlag;
  String? primaryKey;

  InventoryDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.qty,
      this.productId,
      this.outwardId,
      this.assignedDateTime,
      this.mvnoId,
      this.isDeleted,
      this.status,
      this.outwardNumber,
      this.productName,
      this.customerName,
      this.custId,
      this.outId,
      this.hasMac,
      this.hasSerial,
      this.deleteFlag,
      this.primaryKey});

  InventoryDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    outwardId = json['outwardId'];
    assignedDateTime = json['assignedDateTime'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    status = json['status'];
    outwardNumber = json['outwardNumber'];
    productName = json['productName'];
    customerName = json['customerName'];
    custId = json['custId'];
    outId = json['outId'];
    hasMac = json['hasMac'];
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
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['outwardId'] = this.outwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['status'] = this.status;
    data['outwardNumber'] = this.outwardNumber;
    data['productName'] = this.productName;
    data['customerName'] = this.customerName;
    data['custId'] = this.custId;
    data['outId'] = this.outId;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
