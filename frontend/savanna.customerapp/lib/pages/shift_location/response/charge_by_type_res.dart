import 'package:savbill/webservices/base_response.dart';

class ChargeByTypeRes extends BaseResponse {
  List<Chargelist>? chargelist;
  String? timestamp;
  int? status;

  ChargeByTypeRes({this.chargelist, this.timestamp, this.status});

  ChargeByTypeRes.fromJson(Map<String, dynamic> json) {
    if (json['chargelist'] != null) {
      chargelist = <Chargelist>[];
      json['chargelist'].forEach((v) {
        chargelist!.add(new Chargelist.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.chargelist != null) {
      data['chargelist'] = this.chargelist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Chargelist {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? desc;
  String? chargetype;
  double? price;
  int? taxid;
  String? taxName;
  Null? discountid;
  dynamic dbr;
  double? actualprice;
  bool? isDelete;
  String? chargecategory;
  String? saccode;
  double? taxamount;
  int? mvnoId;
  int? buId;
  String? status;
  String? ledgerId;
  bool? royaltyPayable;
  Null? serviceid;
  Null? services;
  int? displayId;
  String? displayName;
  String? businessType;
  String? pushableLedgerId;
  bool? isinventorycharge;
  Null? productId;
  Null? inventoryChargeType;

  Chargelist(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.desc,
        this.chargetype,
        this.price,
        this.taxid,
        this.taxName,
        this.discountid,
        this.dbr,
        this.actualprice,
        this.isDelete,
        this.chargecategory,
        this.saccode,
        this.taxamount,
        this.mvnoId,
        this.buId,
        this.status,
        this.ledgerId,
        this.royaltyPayable,
        this.serviceid,
        this.services,
        this.displayId,
        this.displayName,
        this.businessType,
        this.pushableLedgerId,
        this.isinventorycharge,
        this.productId,
        this.inventoryChargeType});

  Chargelist.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    taxid = json['taxid'];
    taxName = json['taxName'];
    discountid = json['discountid'];
    dbr = json['dbr'];
    actualprice = json['actualprice'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    taxamount = json['taxamount'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    status = json['status'];
    ledgerId = json['ledgerId'];
    royaltyPayable = json['royalty_payable'];
    serviceid = json['serviceid'];
    services = json['services'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    businessType = json['businessType'];
    pushableLedgerId = json['pushableLedgerId'];
    isinventorycharge = json['isinventorycharge'];
    productId = json['productId'];
    inventoryChargeType = json['inventoryChargeType'];
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
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['taxid'] = this.taxid;
    data['taxName'] = this.taxName;
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['actualprice'] = this.actualprice;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    data['taxamount'] = this.taxamount;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['status'] = this.status;
    data['ledgerId'] = this.ledgerId;
    data['royalty_payable'] = this.royaltyPayable;
    data['serviceid'] = this.serviceid;
    data['services'] = this.services;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['businessType'] = this.businessType;
    data['pushableLedgerId'] = this.pushableLedgerId;
    data['isinventorycharge'] = this.isinventorycharge;
    data['productId'] = this.productId;
    data['inventoryChargeType'] = this.inventoryChargeType;
    return data;
  }
}
