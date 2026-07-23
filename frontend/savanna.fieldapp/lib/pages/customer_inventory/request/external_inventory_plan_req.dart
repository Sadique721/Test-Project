import 'package:savbill/pages/customer_inventory/response/get_mac_mapping_external_res.dart';

class ExternalInventoryPlanReq {
  dynamic id;
  String? qty;
  int? productId;
  int? customerId;
  int? serviceId;
  dynamic inventoryType;
  String? staffId;
  dynamic inwardId;
  String? assignedDateTime;
  String? status;
  dynamic mvnoId;
  int? externalItemId;
  int? itemId;
  String? connectionNo;
  List<MACMappingExternalData>? inOutWardMACMapping;

  ExternalInventoryPlanReq(
      {this.id,
        this.qty,
        this.productId,
        this.customerId,
        this.serviceId,
        this.inventoryType,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.status,
        this.mvnoId,
        this.externalItemId,
        this.itemId,
        this.connectionNo,
        this.inOutWardMACMapping});

  ExternalInventoryPlanReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    customerId = json['customerId'];
    serviceId = json['serviceId'];
    inventoryType = json['inventoryType'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    connectionNo = json['connectionNo'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <MACMappingExternalData>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(new MACMappingExternalData.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['customerId'] = this.customerId;
    data['serviceId'] = this.serviceId;
    data['inventoryType'] = this.inventoryType;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['connectionNo'] = this.connectionNo;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

