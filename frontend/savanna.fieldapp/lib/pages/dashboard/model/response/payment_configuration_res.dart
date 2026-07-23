import 'package:savbill/webservices/base_response.dart';

class PaymentConfigurationRes extends BaseResponse {
  ConfigurationDetail? data;

  PaymentConfigurationRes({responseCode, responseMessage, this.data});

  PaymentConfigurationRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null
        ? new ConfigurationDetail.fromJson(json['data'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    return data;
  }
}

class ConfigurationDetail {
  int? id;
  String? name;
  String? value;
  int? mvnoId;
  int? displayId;
  String? displayName;

  ConfigurationDetail({this.id, this.name, this.value, this.mvnoId,this.displayId,this.displayName});

  ConfigurationDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    value = json['value'];
    mvnoId = json['mvnoId'];
    displayId = json['displayId'];
    displayName = json['displayName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['value'] = this.value;
    data['mvnoId'] = this.mvnoId;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
