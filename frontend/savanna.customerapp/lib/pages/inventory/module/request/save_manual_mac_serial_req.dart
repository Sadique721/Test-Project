class SaveManualMacSerialReq {
  int? inwardId;
  List<MacSerialListDTOList>? macSerialListDTOList;

  SaveManualMacSerialReq({this.inwardId, this.macSerialListDTOList});

  SaveManualMacSerialReq.fromJson(Map<String, dynamic> json) {
    inwardId = json['inwardId'];
    if (json['macSerialListDTOList'] != null) {
      macSerialListDTOList = <MacSerialListDTOList>[];
      json['macSerialListDTOList'].forEach((v) {
        macSerialListDTOList!.add(new MacSerialListDTOList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['inwardId'] = this.inwardId;
    if (this.macSerialListDTOList != null) {
      data['macSerialListDTOList'] =
          this.macSerialListDTOList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class MacSerialListDTOList {
  String? macAddress;
  String? serialNumber;

  MacSerialListDTOList({this.macAddress, this.serialNumber});

  MacSerialListDTOList.fromJson(Map<String, dynamic> json) {
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    return data;
  }
}
