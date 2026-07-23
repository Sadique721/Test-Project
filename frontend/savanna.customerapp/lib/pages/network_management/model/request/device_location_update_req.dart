class DeviceLocationUpdateReq {
  int? id;
  String? name;
  String? status;
  int? productId;
  String? staffId;
  int? inwardId;
  String? latitude;
  String? longitude;
  bool? isDeleted;
  String? devicetype;
  List<int>? serviceAreaIdsList;
  int? availableInPorts;
  int? availableOutPorts;
  int? totalInPorts;
  int? totalOutPorts;

  DeviceLocationUpdateReq(
      {this.id,
      this.name,
      this.status,
      this.productId,
      this.staffId,
      this.inwardId,
      this.latitude,
      this.longitude,
      this.isDeleted,
      this.devicetype,
      this.serviceAreaIdsList,
      this.availableInPorts,
      this.availableOutPorts,
      this.totalInPorts,
      this.totalOutPorts});

  DeviceLocationUpdateReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    productId = json['productId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    isDeleted = json['isDeleted'];
    devicetype = json['devicetype'];
    serviceAreaIdsList = json['serviceAreaIdsList'].cast<int>();
    availableInPorts = json['availableInPorts'];
    availableOutPorts = json['availableOutPorts'];
    totalInPorts = json['totalInPorts'];
    totalOutPorts = json['totalOutPorts'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    data['productId'] = this.productId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['isDeleted'] = this.isDeleted;
    data['devicetype'] = this.devicetype;
    data['serviceAreaIdsList'] = this.serviceAreaIdsList;
    data['availableInPorts'] = this.availableInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['totalInPorts'] = this.totalInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    return data;
  }
}
