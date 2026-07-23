import 'package:savbill/webservices/base_response.dart';

class BindPortDeviceRes extends BaseResponse {
  List<BindPortDeviceDetail>? dataList;

  BindPortDeviceRes({responseCode, responseMessage, this.dataList});

  BindPortDeviceRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <BindPortDeviceDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new BindPortDeviceDetail.fromJson(v));
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

class BindPortDeviceDetail {
  int? id;
  int? deviceId;
  String? deviceName;
  int? parentDeviceId;
  String? parentDeviceName;
  String? portType;
  String? inBind;
  String? outBind;

  BindPortDeviceDetail(
      {this.id,
      this.deviceId,
      this.deviceName,
      this.parentDeviceId,
      this.parentDeviceName,
      this.portType,
      this.inBind,
      this.outBind});

  BindPortDeviceDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    deviceId = json['deviceId'];
    deviceName = json['deviceName'];
    parentDeviceId = json['parentDeviceId'];
    parentDeviceName = json['parentDeviceName'];
    portType = json['portType'];
    inBind = json['inBind'];
    outBind = json['outBind'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['deviceId'] = this.deviceId;
    data['deviceName'] = this.deviceName;
    data['parentDeviceId'] = this.parentDeviceId;
    data['parentDeviceName'] = this.parentDeviceName;
    data['portType'] = this.portType;
    data['inBind'] = this.inBind;
    data['outBind'] = this.outBind;
    return data;
  }
}
