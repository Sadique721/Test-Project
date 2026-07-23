class ReplaceInventoryReq {
  int? oldMacMappingId;
  int? newMacMappingId;

  ReplaceInventoryReq({this.oldMacMappingId, this.newMacMappingId});

  ReplaceInventoryReq.fromJson(Map<String, dynamic> json) {
    oldMacMappingId = json['oldMacMappingId'];
    newMacMappingId = json['newMacMappingId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['oldMacMappingId'] = this.oldMacMappingId;
    data['newMacMappingId'] = this.newMacMappingId;
    return data;
  }
}