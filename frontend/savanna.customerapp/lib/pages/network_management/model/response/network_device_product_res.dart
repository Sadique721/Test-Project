import 'package:savbill/webservices/base_response.dart';

class NetworkDeviceProductRes extends BaseResponse {
  List<NetworkDeviceProduct>? dataList;

  NetworkDeviceProductRes({responseCode, responseMessage, this.dataList});

  NetworkDeviceProductRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <NetworkDeviceProduct>[];
      json['dataList'].forEach((v) {
        dataList!.add(new NetworkDeviceProduct.fromJson(v));
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

class NetworkDeviceProduct {
  int? id;
  String? name;
  String? description;
  String? status;
  int? mvnoId;

  NetworkDeviceProduct(
      {this.id, this.name, this.description, this.status, this.mvnoId});

  NetworkDeviceProduct.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    status = json['status'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
