import 'package:savbill/webservices/base_response.dart';

class OutwardRes extends BaseResponse {
  List<OutwardDetail>? dataList;

  OutwardRes({responseCode, responseMessage, this.dataList});

  OutwardRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <OutwardDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new OutwardDetail.fromJson(v));
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

class OutwardDetail {
  int? id;
  String? outwardNumber;
  int? qty;
  String? status;
  int? staffId;
  int? customerId;
  int? mvnoId;
  String? outwardDateTime;
  bool? isDeleted;
  int? usedQty;
  int? unusedQty;
  String? productName;
  String? wareHouseName;
  String? inwardNumber;
  String? unit;
  int? identityKey;

  OutwardDetail(
      {this.id,
      this.outwardNumber,
      this.qty,
      this.status,
      this.staffId,
      this.customerId,
      this.mvnoId,
      this.outwardDateTime,
      this.isDeleted,
      this.usedQty,
      this.unusedQty,
      this.productName,
      this.wareHouseName,
      this.inwardNumber,
      this.unit,
      this.identityKey});

  OutwardDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    outwardNumber = json['outwardNumber'];
    qty = json['qty'];
    status = json['status'];
    staffId = json['staffId'];
    customerId = json['customerId'];
    mvnoId = json['mvnoId'];
    outwardDateTime = json['outwardDateTime'];
    isDeleted = json['isDeleted'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    productName = json['productName'];
    wareHouseName = json['wareHouseName'];
    inwardNumber = json['inwardNumber'];
    unit = json['unit'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['outwardNumber'] = this.outwardNumber;
    data['qty'] = this.qty;
    data['status'] = this.status;
    data['staffId'] = this.staffId;
    data['customerId'] = this.customerId;
    data['mvnoId'] = this.mvnoId;
    data['outwardDateTime'] = this.outwardDateTime;
    data['isDeleted'] = this.isDeleted;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['productName'] = this.productName;
    data['wareHouseName'] = this.wareHouseName;
    data['inwardNumber'] = this.inwardNumber;
    data['unit'] = this.unit;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
