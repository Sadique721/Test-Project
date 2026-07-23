class DevicePortBindReq {
  int? deviceId;
  List<InOutPortDevices>? inPortDevices;
  List<InOutPortDevices>? outPortDevices;

  DevicePortBindReq({this.deviceId, this.inPortDevices, this.outPortDevices});

  DevicePortBindReq.fromJson(Map<String, dynamic> json) {
    deviceId = json['deviceId'];
    if (json['inPortDevices'] != null) {
      inPortDevices = <InOutPortDevices>[];
      json['inPortDevices'].forEach((v) {
        inPortDevices!.add(new InOutPortDevices.fromJson(v));
      });
    }
    if (json['outPortDevices'] != null) {
      outPortDevices = <InOutPortDevices>[];
      json['outPortDevices'].forEach((v) {
        outPortDevices!.add(new InOutPortDevices.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['deviceId'] = this.deviceId;
    if (this.inPortDevices != null) {
      data['inPortDevices'] =
          this.inPortDevices!.map((v) => v.toJson()).toList();
    }
    if (this.outPortDevices != null) {
      data['outPortDevices'] =
          this.outPortDevices!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class InOutPortDevices {
  String? inBind;
  String? outBind;
  int? parentDeviceId;
  bool? flag;

  InOutPortDevices({this.inBind, this.outBind, this.parentDeviceId, this.flag});

  InOutPortDevices.fromJson(Map<String, dynamic> json) {
    inBind = json['inBind'];
    outBind = json['outBind'];
    parentDeviceId = json['parentDeviceId'];
    flag = json['flag'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['inBind'] = this.inBind;
    data['outBind'] = this.outBind;
    data['parentDeviceId'] = this.parentDeviceId;
    data['flag'] = this.flag;
    return data;
  }
}
