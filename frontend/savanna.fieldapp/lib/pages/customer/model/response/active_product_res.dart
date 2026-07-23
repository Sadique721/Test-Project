import 'package:savbill/webservices/base_response.dart';

class ActiveProductRes extends BaseResponse {
  List<ProductDetail>? dataList;

  ActiveProductRes({responseCode, responseMessage, this.dataList});

  ActiveProductRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <ProductDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ProductDetail.fromJson(v));
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

class ProductDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? unit;
  String? description;
  String? status;
  int? mvnoId;
  bool? hasMac;
  bool? isDeleted;
  bool? hasSerial;
  bool? deleteFlag;
  int? primaryKey;

  ProductDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.unit,
      this.description,
      this.status,
      this.mvnoId,
      this.hasMac,
      this.isDeleted,
      this.hasSerial,
      this.deleteFlag,
      this.primaryKey});

  ProductDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    unit = json['unit'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    hasMac = json['hasMac'];
    isDeleted = json['isDeleted'];
    hasSerial = json['hasSerial'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['unit'] = this.unit;
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['hasMac'] = this.hasMac;
    data['isDeleted'] = this.isDeleted;
    data['hasSerial'] = this.hasSerial;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
