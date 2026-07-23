import 'package:savbill/webservices/base_response.dart';

class InventroyTaxesListRes  extends BaseResponse{
  List<Taxlist>? taxlist;
  String? timestamp;
  int? status;

  InventroyTaxesListRes({this.taxlist, this.timestamp, this.status});

  InventroyTaxesListRes.fromJson(Map<String, dynamic> json) {
    if (json['taxlist'] != null) {
      taxlist = <Taxlist>[];
      json['taxlist'].forEach((v) {
        taxlist!.add(new Taxlist.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.taxlist != null) {
      data['taxlist'] = this.taxlist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Taxlist {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? desc;
  String? taxtype;
  String? status;
  bool? isDelete;
  List<TieredList>? tieredList;
  int? mvnoId;
  Null? buId;

  Taxlist(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.desc,
      this.taxtype,
      this.status,
      this.isDelete,
      this.tieredList,
      this.mvnoId,
      this.buId});

  Taxlist.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    desc = json['desc'];
    taxtype = json['taxtype'];
    status = json['status'];
    isDelete = json['isDelete'];
    if (json['tieredList'] != null) {
      tieredList = <TieredList>[];
      json['tieredList'].forEach((v) {
        tieredList!.add(new TieredList.fromJson(v));
      });
    }
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
    data['taxtype'] = this.taxtype;
    data['status'] = this.status;
    data['isDelete'] = this.isDelete;
    if (this.tieredList != null) {
      data['tieredList'] = this.tieredList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    return data;
  }
}

class TieredList {
  int? id;
  String? name;
  String? taxGroup;
  double? rate;
  bool? isDelete;
  bool? beforeDiscount;
  String? ledgerId;

  TieredList(
      {this.id,
      this.name,
      this.taxGroup,
      this.rate,
      this.isDelete,
      this.beforeDiscount,
      this.ledgerId});

  TieredList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    taxGroup = json['taxGroup'];
    rate = json['rate'];
    isDelete = json['isDelete'];
    beforeDiscount = json['beforeDiscount'];
    ledgerId = json['ledgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['taxGroup'] = this.taxGroup;
    data['rate'] = this.rate;
    data['isDelete'] = this.isDelete;
    data['beforeDiscount'] = this.beforeDiscount;
    data['ledgerId'] = this.ledgerId;
    return data;
  }
}
