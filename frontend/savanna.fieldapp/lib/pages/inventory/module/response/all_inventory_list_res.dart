import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/ownership_res.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/webservices/base_response.dart';

class AllInventoryListRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<InventoryListDetail>? dataList;

  AllInventoryListRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  AllInventoryListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <InventoryListDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InventoryListDetail.fromJson(v));
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

class InventoryListDetail {
  int? id;
  String? name;
  String? mac;
  String? serialNumber;
  int? mvnoId;
  String? condition;
  bool? isDeleted;
  int? currentInwardId;
  String? currentInwardType;
  int? productId;
  int? ownerId;
  String? ownerType;
  String? warranty;
  int? warrantyPeriod;
  String? currentInwardNumber;
  String? ownerName;
  String? productName;
  String? itemStatus;
  String? ownershipType;
  String? remarks;
  int? externalItemId;
  String? remainingDays;
  String? filename;
  int? itemConditionId;
  bool? selected = false;
  // for change warranty status
  WarrantyStatusDetail? selectedWarranty;
  // for item status
  StatusDetail? selectedItemStatus;
  // for change ownership status
  OwnershipDetail? selectedOwnershipStatus;
  String? ownerShipRemarks;
  // for change item type
  String? changeTypeRemarks;
  ItemTypeDetail? selectedItemType;
  DropdownDetail? selectedRemarkType;
  bool? readOnly=true;

  InventoryListDetail(
      {this.id,
      this.name,
      this.mac,
      this.serialNumber,
      this.mvnoId,
      this.condition,
      this.isDeleted,
      this.currentInwardId,
      this.currentInwardType,
      this.productId,
      this.ownerId,
      this.ownerType,
      this.warranty,
      this.warrantyPeriod,
      this.currentInwardNumber,
      this.ownerName,
      this.productName,
      this.itemStatus,
      this.ownershipType,
      this.remarks,
      this.externalItemId,
      this.remainingDays,
      this.filename,
      this.itemConditionId,
      this.selected,
      this.selectedWarranty,
      this.selectedItemStatus,
      this.selectedOwnershipStatus,
      this.ownerShipRemarks,
      this.changeTypeRemarks,
      this.selectedItemType,
      this.selectedRemarkType,
      this.readOnly
      });

  InventoryListDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    mac = json['mac'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    condition = json['condition'];
    isDeleted = json['isDeleted'];
    currentInwardId = json['currentInwardId'];
    currentInwardType = json['currentInwardType'];
    productId = json['productId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    warranty = json['warranty'];
    warrantyPeriod = json['warrantyPeriod'];
    currentInwardNumber = json['currentInwardNumber'];
    ownerName = json['ownerName'];
    productName = json['productName'];
    itemStatus = json['itemStatus'];
    ownershipType = json['ownershipType'];
    remarks = json['remarks'];
    externalItemId = json['externalItemId'];
    remainingDays = json['remainingDays'];
    filename = json['filename'];
    itemConditionId = json['itemConditionId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['mac'] = this.mac;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['condition'] = this.condition;
    data['isDeleted'] = this.isDeleted;
    data['currentInwardId'] = this.currentInwardId;
    data['currentInwardType'] = this.currentInwardType;
    data['productId'] = this.productId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['warranty'] = this.warranty;
    data['warrantyPeriod'] = this.warrantyPeriod;
    data['currentInwardNumber'] = this.currentInwardNumber;
    data['ownerName'] = this.ownerName;
    data['productName'] = this.productName;
    data['itemStatus'] = this.itemStatus;
    data['ownershipType'] = this.ownershipType;
    data['remarks'] = this.remarks;
    data['externalItemId'] = this.externalItemId;
    data['remainingDays'] = this.remainingDays;
    data['filename'] = this.filename;
    data['itemConditionId'] = this.itemConditionId;
    return data;
  }
}
