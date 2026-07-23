class ExternalLiteMacMappingReq {
  int? id;
  int? externalItemId;
  String? macAddress;
  String? serialNumber;
  String? status;

  ExternalLiteMacMappingReq(
      {this.id,
        this.externalItemId,
        this.macAddress,
        this.serialNumber,
        this.status});

  ExternalLiteMacMappingReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    externalItemId = json['externalItemId'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['externalItemId'] = this.externalItemId;
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['status'] = this.status;
    return data;
  }
}