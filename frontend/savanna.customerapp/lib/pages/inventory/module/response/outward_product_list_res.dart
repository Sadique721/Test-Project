import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/webservices/base_response.dart';

class OutwardProductListRes extends BaseResponse{

  List<OutwardProductDetail>? dataList;

  OutwardProductListRes(
      {responseCode, responseMessage, this.dataList});

  OutwardProductListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <OutwardProductDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new OutwardProductDetail.fromJson(v));
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

class OutwardProductDetail {
  int? id;
  String? name;
  String? description;
  String? status;
  int? mvnoId;
  bool? isDeleted;
  int? chargeId;
  int? expiryTime;
  String? expiryTimeUnit;
  String? refundAmount;
  int? availableInPorts;
  int? totalInPorts;
  int? availableOutPorts;
  int? totalOutPorts;
  InwardsProductCategory? productCategory;

  OutwardProductDetail(
      {this.id,
        this.name,
        this.description,
        this.status,
        this.mvnoId,
        this.isDeleted,
        this.chargeId,
        this.expiryTime,
        this.expiryTimeUnit,
        this.refundAmount,
        this.availableInPorts,
        this.totalInPorts,
        this.availableOutPorts,
        this.totalOutPorts,
        this.productCategory});

  OutwardProductDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    chargeId = json['chargeId'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refundAmount = json['refundAmount'];
    availableInPorts = json['availableInPorts'];
    totalInPorts = json['totalInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalOutPorts = json['totalOutPorts'];
    productCategory = json['productCategory'] != null
        ? new InwardsProductCategory.fromJson(json['productCategory'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    data['chargeId'] = this.chargeId;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refundAmount'] = this.refundAmount;
    data['availableInPorts'] = this.availableInPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    if (this.productCategory != null) {
      data['productCategory'] = this.productCategory!.toJson();
    }
    return data;
  }
}