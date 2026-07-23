class SerialNumberReq {
  dynamic serialNumber;
  dynamic custPlanMapppingId;
  dynamic connectionNo;

  SerialNumberReq(
      {this.serialNumber, this.custPlanMapppingId, this.connectionNo});

  SerialNumberReq.fromJson(Map<String, dynamic> json) {
    serialNumber = json['serialNumber'];
    custPlanMapppingId = json['custPlanMapppingId'];
    connectionNo = json['connection_no'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['serialNumber'] = this.serialNumber;
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['connection_no'] = this.connectionNo;
    return data;
  }
}