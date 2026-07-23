import 'package:savbill/webservices/base_response.dart';

class ProductChargeListRes extends BaseResponse {
  List<ProductChargeDetail>? dataList;

  ProductChargeListRes({responseCode, responseMessage, this.dataList});

  ProductChargeListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ProductChargeDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ProductChargeDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ProductChargeDetail {
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
  num? price;
  int? taxid;
  String? taxName;
  int? discountid;
  num? dbr;
  num? actualprice;
  bool? isDelete;
  String? chargecategory;
  String? saccode;
  num? taxamount;
  int? mvnoId;
  int? buId;

  ProductChargeDetail(
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
      this.buId});

  ProductChargeDetail.fromJson(Map<String, dynamic> json) {
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
    return data;
  }
}
