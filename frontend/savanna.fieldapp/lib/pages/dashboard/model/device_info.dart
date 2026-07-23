class DeviceInfo {
  String? deviceId;
  String? deviceType;
  String? deviceOSV;
  String? deviceName;
  String? appVer;

  DeviceInfo({
    this.deviceId,
    this.deviceType,
    this.deviceOSV,
    this.deviceName,
    this.appVer,
  });

  DeviceInfo.fromJson(Map<String, dynamic> json) {
    deviceId = json['device_id'];
    deviceType = json['os_type'];
    deviceOSV = json['device_os_version'];
    deviceName = json['device_name'];
    appVer = json['app_version'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['device_id'] = this.deviceId;
    data['os_type'] = this.deviceType;
    data['device_os_version'] = this.deviceOSV;
    data['device_name'] = this.deviceName;
    data['app_version'] = this.appVer;
    return data;
  }
}
