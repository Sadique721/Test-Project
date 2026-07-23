class UpdateMacSerialReq {
  int? itemId;
  String? macAddress;
  String? serialNumber;

  UpdateMacSerialReq({this.itemId, this.macAddress, this.serialNumber});

  UpdateMacSerialReq.fromJson(Map<String, dynamic> json) {
    itemId = json['itemId'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['itemId'] = itemId;
    data['macAddress'] = macAddress;
    data['serialNumber'] = serialNumber;
    return data;
  }
}
