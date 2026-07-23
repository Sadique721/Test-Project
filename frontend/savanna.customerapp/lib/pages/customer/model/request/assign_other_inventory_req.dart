import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';

class AssignOtherInventoryReq {
  int? id;
  int? qty;
  int? productId;
  int? customerId;
  int? staffId;
  String? assignedDateTime;
  int? mvnoId;
  String? status;
  int? serviceId;
  int? custPackId;
  int? inwardId;
  int? externalItemId;
  int? itemId;
  String? itemAssemblyId;
  bool? itemAssemblyflag;
  String? itemTypeFlag;
  int? inventoryType;
  String? nonSerializedQty;
  String? connectionNo;
  List<ProductMacDataList>? inOutWardMACMapping;
  String? itemAssemblyStatus;

  AssignOtherInventoryReq(
      {this.id,
        this.qty,
        this.productId,
        this.serviceId,
        this.custPackId,
        this.customerId,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.mvnoId,
        this.status,
        this.externalItemId,
        this.itemId,
        this.itemAssemblyId,
        this.itemAssemblyflag,
        this.itemTypeFlag,
        this.inventoryType,
        this.nonSerializedQty,
        this.connectionNo,
        this.inOutWardMACMapping,
        this.itemAssemblyStatus});

  AssignOtherInventoryReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    serviceId = json['serviceId'];
    custPackId = json['custPackId'];
    customerId = json['customerId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    itemAssemblyId = json['itemAssemblyId'];
    itemAssemblyflag = json['itemAssemblyflag'];
    itemTypeFlag = json['itemTypeFlag'];
    inventoryType = json['inventoryType'];
    nonSerializedQty = json['nonSerializedQty'];
    connectionNo = json['connectionNo'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <ProductMacDataList>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(ProductMacDataList.fromJson(v));
      });
    }
    itemAssemblyStatus = json['itemAssemblyStatus'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['serviceId'] = this.serviceId;
    data['custPackId'] = this.custPackId;
    data['customerId'] = this.customerId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['itemAssemblyId'] = this.itemAssemblyId;
    data['itemAssemblyflag'] = this.itemAssemblyflag;
    data['itemTypeFlag'] = this.itemTypeFlag;
    data['inventoryType'] = this.inventoryType;
    data['nonSerializedQty'] = this.nonSerializedQty;
    data['connectionNo'] = this.connectionNo;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
    data['itemAssemblyStatus'] = this.itemAssemblyStatus;
    return data;
  }
}
