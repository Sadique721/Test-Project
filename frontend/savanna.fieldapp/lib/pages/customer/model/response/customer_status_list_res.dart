import 'package:savbill/webservices/base_response.dart';

class CustomerStatusListRes extends BaseResponse {
  List<CustomerStatusDetail>? dataList;

  CustomerStatusListRes({responseCode, responseMessage, this.dataList});

  CustomerStatusListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CustomerStatusDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(CustomerStatusDetail.fromJson(v));
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

class CustomerStatusDetail {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  int? mvnoId;
  int? displayId;
  String? displayName;

  CustomerStatusDetail(
      {this.id, this.text, this.value, this.type, this.status, this.mvnoId,this.displayId,this.displayName});

  CustomerStatusDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
    displayId = json['displayId'];
    displayName = json['displayName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
