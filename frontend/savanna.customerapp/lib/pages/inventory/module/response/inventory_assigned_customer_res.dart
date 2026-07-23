import 'package:savbill/webservices/base_response.dart';

class InventoryAssignedCustomerRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<InventoryAssignedCustomerDetail>? dataList;

  InventoryAssignedCustomerRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  InventoryAssignedCustomerRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <InventoryAssignedCustomerDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InventoryAssignedCustomerDetail.fromJson(v));
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

class InventoryAssignedCustomerDetail {
  int? id;
  int? qty;
  int? productId;
  int? customerId;
  int? staffId;
  int? inwardId;
  String? assignedDateTime;
  bool? isDeleted;
  int? mvnoId;
  String? status;
  String? expiryDateTime;
  String? outwardNumber;
  String? productName;
  String? customerName;
  bool? hasMac;
  bool? hasSerial;
  int? nextApproverId;
  int? teamHierarchyMappingId;
  String? assigneeName;
  int? previousApproveId;
  int? identityKey;

  InventoryAssignedCustomerDetail(
      {this.id,
      this.qty,
      this.productId,
      this.customerId,
      this.staffId,
      this.inwardId,
      this.assignedDateTime,
      this.isDeleted,
      this.mvnoId,
      this.status,
      this.expiryDateTime,
      this.outwardNumber,
      this.productName,
      this.customerName,
      this.hasMac,
      this.hasSerial,
      this.nextApproverId,
      this.teamHierarchyMappingId,
      this.assigneeName,
      this.previousApproveId,
      this.identityKey});

  InventoryAssignedCustomerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    customerId = json['customerId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    expiryDateTime = json['expiryDateTime'];
    outwardNumber = json['outwardNumber'];
    productName = json['productName'];
    customerName = json['customerName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    nextApproverId = json['nextApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    assigneeName = json['assigneeName'];
    previousApproveId = json['previousApproveId'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['customerId'] = this.customerId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['expiryDateTime'] = this.expiryDateTime;
    data['outwardNumber'] = this.outwardNumber;
    data['productName'] = this.productName;
    data['customerName'] = this.customerName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['nextApproverId'] = this.nextApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['assigneeName'] = this.assigneeName;
    data['previousApproveId'] = this.previousApproveId;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
