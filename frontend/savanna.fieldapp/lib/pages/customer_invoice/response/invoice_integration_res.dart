import 'package:savbill/webservices/base_response.dart';

class InvoiceIntegrationRes extends BaseResponse{
  String? message;
  List<IntegrationDataList>? integrationDataListDetail;
  String? timestamp;
  int? status;

  InvoiceIntegrationRes(
      {this.message,
        this.integrationDataListDetail,
        this.timestamp,
        this.status});

  InvoiceIntegrationRes.fromJson(Map<String, dynamic> json) {
    message = json['message'];
    if (json['thirdPartyIntegrationMenuData'] != null) {
      integrationDataListDetail = <IntegrationDataList>[];
      json['thirdPartyIntegrationMenuData'].forEach((v) {
        integrationDataListDetail!
            .add(new IntegrationDataList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['message'] = this.message;
    if (this.integrationDataListDetail != null) {
      data['thirdPartyIntegrationMenuData'] =
          this.integrationDataListDetail!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class IntegrationDataList {
  int? id;
  String? name;
  String? eventName;
  String? clientName;
  List<ThirdPartyIntegrationMenuMappings>? thirdPartyIntegrationMenuMappings;
  String? status;
  int? mvnoId;
  bool? delete;
  dynamic identityKey;

  IntegrationDataList(
      {this.id,
        this.name,
        this.eventName,
        this.clientName,
        this.thirdPartyIntegrationMenuMappings,
        this.status,
        this.mvnoId,
        this.delete,
        this.identityKey});

  IntegrationDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    eventName = json['eventName'];
    clientName = json['clientName'];
    if (json['thirdPartyIntegrationMenuMappings'] != null) {
      thirdPartyIntegrationMenuMappings = <ThirdPartyIntegrationMenuMappings>[];
      json['thirdPartyIntegrationMenuMappings'].forEach((v) {
        thirdPartyIntegrationMenuMappings!
            .add(new ThirdPartyIntegrationMenuMappings.fromJson(v));
      });
    }
    status = json['status'];
    mvnoId = json['mvnoId'];
    delete = json['delete'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['eventName'] = this.eventName;
    data['clientName'] = this.clientName;
    if (this.thirdPartyIntegrationMenuMappings != null) {
      data['thirdPartyIntegrationMenuMappings'] = this
          .thirdPartyIntegrationMenuMappings!
          .map((v) => v.toJson())
          .toList();
    }
    data['status'] = this.status;
    data['mvnoId'] = this.mvnoId;
    data['delete'] = this.delete;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class ThirdPartyIntegrationMenuMappings {
  int? integrationMenuMappingId;
  int? thirdPartyIntegrationMenuId;
  String? thirdPartyParameterName;
  String? thirdPartyParameterValue;
  dynamic thirdPartyParamDesc;

  ThirdPartyIntegrationMenuMappings(
      {this.integrationMenuMappingId,
        this.thirdPartyIntegrationMenuId,
        this.thirdPartyParameterName,
        this.thirdPartyParameterValue,
        this.thirdPartyParamDesc});

  ThirdPartyIntegrationMenuMappings.fromJson(Map<String, dynamic> json) {
    integrationMenuMappingId = json['integrationMenuMappingId'];
    thirdPartyIntegrationMenuId = json['thirdPartyIntegrationMenuId'];
    thirdPartyParameterName = json['thirdPartyParameterName'];
    thirdPartyParameterValue = json['thirdPartyParameterValue'];
    thirdPartyParamDesc = json['thirdPartyParamDesc'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['integrationMenuMappingId'] = this.integrationMenuMappingId;
    data['thirdPartyIntegrationMenuId'] = this.thirdPartyIntegrationMenuId;
    data['thirdPartyParameterName'] = this.thirdPartyParameterName;
    data['thirdPartyParameterValue'] = this.thirdPartyParameterValue;
    data['thirdPartyParamDesc'] = this.thirdPartyParamDesc;
    return data;
  }
}
