class AddInwardMacMapReq {
  int? id;
  int? inwardId;
  String? macAddress;
  String? serialNumber;
  int? outwardId;
  String? status;

  AddInwardMacMapReq(
      {this.id,
      this.inwardId,
      this.macAddress,
      this.serialNumber,
      this.outwardId,
      this.status});

  AddInwardMacMapReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardId = json['inwardId'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    outwardId = json['outwardId'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardId'] = this.inwardId;
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    return data;
  }
}
