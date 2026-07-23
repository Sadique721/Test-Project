import 'package:savbill/webservices/base_response.dart';

class ChargeListRes extends BaseResponse {
  List<ChargeDetail>? chargelist;

  ChargeListRes({this.chargelist, timestamp, error, status});

  ChargeListRes.fromJson(Map<String, dynamic> json) {
    if (json['chargelist'] != null) {
      chargelist = <ChargeDetail>[];
      json['chargelist'].forEach((v) {
        chargelist!.add(new ChargeDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    error = json['error'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.chargelist != null) {
      data['chargelist'] = this.chargelist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['error'] = this.error;
    data['status'] = this.status;
    return data;
  }
}

class ChargeDetail {
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? desc;
  String? chargetype;
  num? price;
  int? taxid;
  String? taxName;
  num? actualprice;
  bool? isDelete;
  String? chargecategory;
  String? saccode;
  int? mvnoId;
  int? buId;
  int? discountid;
  num? dbr;
  num? taxamount;
  String? createdByName;
  String? lastModifiedByName;

  ChargeDetail(
      {this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.desc,
      this.chargetype,
      this.price,
      this.taxid,
      this.taxName,
      this.actualprice,
      this.isDelete,
      this.chargecategory,
      this.saccode,
      this.mvnoId,
      this.buId,
      this.discountid,
      this.dbr,
      this.taxamount,
      this.createdByName,
      this.lastModifiedByName});

  ChargeDetail.fromJson(Map<String, dynamic> json) {
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    chargetype = json['chargetype'];
    price = json['price'];
    taxid = json['taxid'];
    taxName = json['taxName'];
    actualprice = json['actualprice'];
    isDelete = json['isDelete'];
    chargecategory = json['chargecategory'];
    saccode = json['saccode'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    discountid = json['discountid'];
    dbr = json['dbr'];
    taxamount = json['taxamount'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['name'] = this.name;
    data['desc'] = this.desc;
    data['chargetype'] = this.chargetype;
    data['price'] = this.price;
    data['taxid'] = this.taxid;
    data['taxName'] = this.taxName;
    data['actualprice'] = this.actualprice;
    data['isDelete'] = this.isDelete;
    data['chargecategory'] = this.chargecategory;
    data['saccode'] = this.saccode;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['discountid'] = this.discountid;
    data['dbr'] = this.dbr;
    data['taxamount'] = this.taxamount;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    return data;
  }
}
