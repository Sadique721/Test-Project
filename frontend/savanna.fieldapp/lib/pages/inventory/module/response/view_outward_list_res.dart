import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class ViewOutwardListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<OutwardDetail>? dataList;

  ViewOutwardListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewOutwardListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <OutwardDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new OutwardDetail.fromJson(v));
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

class OutwardDetail {
  int? id;
  String? outwardNumber;
  int? qty;
  String? status;
  int? wareHouseId;
  int? staffId;
  String? productCategory;
  int? customerId;
  int? mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  int? usedQty;
  int? unusedQty;
  String? productName;
  String? wareHouseName;
  String? inwardNumber;
  String? unit;
  String? sourceType;
  int? selectedItems;
  int? sourceId;
  String? destinationType;
  int? destinationId;
  int? inTransitQty;
  int? serviceAreaId;
  int? identityKey;
  String? createdBy;
  String? approvalStatus;
  InwardsProductDetail? productId;
  InwardsDetail? inwardId;


  OutwardDetail(
      {this.id,
      this.outwardNumber,
      this.qty,
      this.status,
      this.wareHouseId,
      this.staffId,
      this.productCategory,
      this.customerId,
      this.mvnoId,
      this.outwardDateTime,
      this.isDeleted,
      this.usedQty,
      this.unusedQty,
      this.productName,
      this.wareHouseName,
      this.inwardNumber,
      this.unit,
      this.sourceType,
      this.selectedItems,
      this.sourceId,
      this.destinationType,
      this.destinationId,
      this.inTransitQty,
      this.serviceAreaId,
      this.identityKey,
      this.productId,
      this.inwardId,
        this.createdBy,
        this.approvalStatus
      });

  OutwardDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    outwardNumber = json['outwardNumber'];
    qty = json['qty'];
    status = json['status'];
    wareHouseId = json['wareHouseId'];
    staffId = json['staffId'];
    productCategory = json['productCategory'];
    customerId = json['customerId'];
    mvnoId = json['mvnoId'];
    outwardDateTime = json['outwardDateTime'];
    isDeleted = json['isDeleted'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    productName = json['productName'];
    wareHouseName = json['wareHouseName'];
    inwardNumber = json['inwardNumber'];
    unit = json['unit'];
    sourceType = json['sourceType'];
    selectedItems = json['selectedItems'];
    sourceId = json['sourceId'];
    destinationType = json['destinationType'];
    destinationId = json['destinationId'];
    inTransitQty = json['inTransitQty'];
    serviceAreaId = json['serviceAreaId'];
    identityKey = json['identityKey'];
    productId = json['productId'] != null
        ? new InwardsProductDetail.fromJson(json['productId'])
        : null;
    inwardId = json['inwardId'] != null
        ? new InwardsDetail.fromJson(json['inwardId'])
        : null;
    createdBy = json['createdBy'];
    approvalStatus = json['approvalStatus'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['outwardNumber'] = this.outwardNumber;
    data['qty'] = this.qty;
    data['status'] = this.status;
    data['wareHouseId'] = this.wareHouseId;
    data['staffId'] = this.staffId;
    data['productCategory'] = this.productCategory;
    data['customerId'] = this.customerId;
    data['mvnoId'] = this.mvnoId;
    data['outwardDateTime'] = this.outwardDateTime;
    data['isDeleted'] = this.isDeleted;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['productName'] = this.productName;
    data['wareHouseName'] = this.wareHouseName;
    data['inwardNumber'] = this.inwardNumber;
    data['unit'] = this.unit;
    data['sourceType'] = this.sourceType;
    data['selectedItems'] = this.selectedItems;
    data['sourceId'] = this.sourceId;
    data['destinationType'] = this.destinationType;
    data['destinationId'] = this.destinationId;
    data['inTransitQty'] = this.inTransitQty;
    data['serviceAreaId'] = this.serviceAreaId;
    data['identityKey'] = this.identityKey;
    if (this.productId != null) {
      data['productId'] = this.productId!.toJson();
    }
    if (this.inwardId != null) {
      data['inwardId'] = this.inwardId!.toJson();
    }
    data['createdBy'] = this.createdBy;
    data['approvalStatus'] = this.approvalStatus;
    return data;
  }
}
