import 'package:savbill/webservices/base_response.dart';

class PaymentConfigRes extends BaseResponse {
  List<ActivePaymentConfig>? activePaymentConfig;
  String? message;
  String? timestamp;
  int? status;

  PaymentConfigRes(
      {this.activePaymentConfig, this.message, this.timestamp, this.status});

  PaymentConfigRes.fromJson(Map<String, dynamic> json) {
    if (json['activePaymentConfig'] != null) {
      activePaymentConfig = <ActivePaymentConfig>[];
      json['activePaymentConfig'].forEach((v) {
        activePaymentConfig!.add(new ActivePaymentConfig.fromJson(v));
      });
    }
    message = json['message'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.activePaymentConfig != null) {
      data['activePaymentConfig'] =
          this.activePaymentConfig!.map((v) => v.toJson()).toList();
    }
    data['message'] = this.message;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ActivePaymentConfig {
  int? paymentConfigId;
  String? paymentConfigName;
  List<PaymentConfigMappingList>? paymentConfigMappingList;
  bool? isDelete;
  String? createDate;
  int? mvnoId;
  bool? isActive;
  String? paymentGatewayInfo;

  ActivePaymentConfig(
      {this.paymentConfigId,
        this.paymentConfigName,
        this.paymentConfigMappingList,
        this.isDelete,
        this.createDate,
        this.mvnoId,
        this.isActive,
        this.paymentGatewayInfo});

  ActivePaymentConfig.fromJson(Map<String, dynamic> json) {
    paymentConfigId = json['paymentConfigId'];
    paymentConfigName = json['paymentConfigName'];
    if (json['paymentConfigMappingList'] != null) {
      paymentConfigMappingList = <PaymentConfigMappingList>[];
      json['paymentConfigMappingList'].forEach((v) {
        paymentConfigMappingList!.add(new PaymentConfigMappingList.fromJson(v));
      });
    }
    isDelete = json['isDelete'];
    createDate = json['createDate'];
    mvnoId = json['mvnoId'];
    isActive = json['isActive'];
    paymentGatewayInfo = json['paymentGatewayInfo'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paymentConfigId'] = this.paymentConfigId;
    data['paymentConfigName'] = this.paymentConfigName;
    if (this.paymentConfigMappingList != null) {
      data['paymentConfigMappingList'] =
          this.paymentConfigMappingList!.map((v) => v.toJson()).toList();
    }
    data['isDelete'] = this.isDelete;
    data['createDate'] = this.createDate;
    data['mvnoId'] = this.mvnoId;
    data['isActive'] = this.isActive;
    data['paymentGatewayInfo'] = this.paymentGatewayInfo;
    return data;
  }
}

class PaymentConfigMappingList {
  int? paymentConfigMappingId;
  int? paymentConfigId;
  String? paymentParameterName;
  String? paymentParameterValue;
  dynamic paymentParameterDescription;
  String? parameterDisplayName;
  String? paymentParameterFor;

  PaymentConfigMappingList(
      {this.paymentConfigMappingId,
        this.paymentConfigId,
        this.paymentParameterName,
        this.paymentParameterValue,
        this.paymentParameterDescription,
        this.parameterDisplayName,
        this.paymentParameterFor});

  PaymentConfigMappingList.fromJson(Map<String, dynamic> json) {
    paymentConfigMappingId = json['paymentConfigMappingId'];
    paymentConfigId = json['paymentConfigId'];
    paymentParameterName = json['paymentParameterName'];
    paymentParameterValue = json['paymentParameterValue'];
    paymentParameterDescription = json['paymentParameterDescription'];
    parameterDisplayName = json['parameterDisplayName'];
    paymentParameterFor = json['paymentParameterFor'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paymentConfigMappingId'] = this.paymentConfigMappingId;
    data['paymentConfigId'] = this.paymentConfigId;
    data['paymentParameterName'] = this.paymentParameterName;
    data['paymentParameterValue'] = this.paymentParameterValue;
    data['paymentParameterDescription'] = this.paymentParameterDescription;
    data['parameterDisplayName'] = this.parameterDisplayName;
    data['paymentParameterFor'] = this.paymentParameterFor;
    return data;
  }
}
