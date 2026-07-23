import 'package:savbill/webservices/base_response.dart';

class CustomerTitleRes extends BaseResponse {
  List<CustomerTitle>? dataList;

  CustomerTitleRes({responseCode, responseMessage, this.dataList});

  CustomerTitleRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CustomerTitle>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerTitle.fromJson(v));
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

class CustomerTitle {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  int? mvnoId;

  CustomerTitle(
      {this.id, this.text, this.value, this.type, this.status, this.mvnoId});

  CustomerTitle.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}


class CustomerFeasibilityRes extends BaseResponse {
  List<CustomerFeasibility>? dataList;

  CustomerFeasibilityRes({responseCode, responseMessage, this.dataList});

  CustomerFeasibilityRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CustomerFeasibility>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerFeasibility.fromJson(v));
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


class CustomerFeasibility {
  int? id;
  String? text;
  String? value;
  String? type;
  String? status;
  int? mvnoId;

  CustomerFeasibility(
      {this.id, this.text, this.value, this.type, this.status, this.mvnoId});

  CustomerFeasibility.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    text = json['text'];
    value = json['value'];
    type = json['type'];
    status = json['status'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['text'] = this.text;
    data['value'] = this.value;
    data['type'] = this.type;
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
