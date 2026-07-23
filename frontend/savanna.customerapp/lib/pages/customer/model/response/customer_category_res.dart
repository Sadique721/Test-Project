import 'package:savbill/webservices/base_response.dart';

class CustomerCategoryRes extends BaseResponse {

  List<CustomerCategoryDetail>? dataList;

  CustomerCategoryRes({responseCode, responseMessage, this.dataList});

  CustomerCategoryRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CustomerCategoryDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerCategoryDetail.fromJson(v));
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

class CustomerCategoryDetail {
  int? id;
  String? name;
  String? value;
  int? mvnoId;

  CustomerCategoryDetail({this.id, this.name, this.value, this.mvnoId});

  CustomerCategoryDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    value = json['value'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['value'] = this.value;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
