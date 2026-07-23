import 'package:savbill/pages/inventory/module/response/get_all_serialized_item_base_on_product_res.dart';

class AddEditBulkConsumptionReq {
  int? id;
  int? productId;
  List<int>? itemListLongId;
  String? bulkConsumptionName;
  List<SerializedItemBaseDataList>? inOutWardMACMappings;
  bool? isDeleted;
  int? qty;
  int? ownerId;
  String? ownerType;
  int? nonSerializedQty;
  String? itemType;


  AddEditBulkConsumptionReq(
      {this.id,
      this.productId,
      this.itemListLongId,
      this.bulkConsumptionName,
        this.inOutWardMACMappings,
      this.isDeleted,
      this.qty,
      this.ownerId,
      this.ownerType,
      this.nonSerializedQty,
      this.itemType
      });

  AddEditBulkConsumptionReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    itemListLongId = json['itemListLongId'].cast<int>();
    bulkConsumptionName = json['bulkConsumptionName'];
    if (json['inOutWardMACMappings'] != null) {
      inOutWardMACMappings = <SerializedItemBaseDataList>[];
      json['inOutWardMACMappings'].forEach((v) {
        inOutWardMACMappings!.add(SerializedItemBaseDataList.fromJson(v));
      });
    }
    isDeleted = json['isDeleted'];
    qty = json['qty'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    nonSerializedQty = json['nonSerializedQty'];
    itemType = json['itemType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['itemListLongId'] = this.itemListLongId;
    data['bulkConsumptionName'] = this.bulkConsumptionName;
    if (this.inOutWardMACMappings != null) {
      data['inOutWardMACMappings'] = this.inOutWardMACMappings!.map((v) => v.toJson()).toList();
    }
    data['isDeleted'] = this.isDeleted;
    data['qty'] = this.qty;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['nonSerializedQty'] = this.nonSerializedQty;
    data['itemType'] = this.itemType;
    return data;
  }
}
