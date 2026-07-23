class AddEditProductReq {
  int? id;
  String? name;
  String? description;
  int? productCategory;
  String? status;
  int? chargeId;
  int? expiryTime;
  String? expiryTimeUnit;
  String? refundAmount;
  int? totalInPorts;
  int? totalOutPorts;
  int? availableInPorts;
  int? availableOutPorts;
  String? productId;
  String? navLedgerId;

  AddEditProductReq(
      {this.id,
      this.name,
      this.description,
      this.productCategory,
      this.status,
      this.chargeId,
      this.expiryTime,
      this.expiryTimeUnit,
      this.refundAmount,
      this.totalInPorts,
      this.totalOutPorts,
      this.availableInPorts,
      this.availableOutPorts,
      this.productId,
      this.navLedgerId});

  AddEditProductReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    description = json['description'];
    productCategory = json['productCategory'];
    status = json['status'];
    chargeId = json['chargeId'];
    expiryTime = json['expiryTime'];
    expiryTimeUnit = json['expiryTimeUnit'];
    refundAmount = json['refundAmount'];
    totalInPorts = json['totalInPorts'];
    totalOutPorts = json['totalOutPorts'];
    availableInPorts = json['availableInPorts'];
    availableOutPorts = json['availableOutPorts'];
    productId = json['productId'];
    navLedgerId = json['navLedgerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['description'] = this.description;
    data['productCategory'] = this.productCategory;
    data['status'] = this.status;
    data['chargeId'] = this.chargeId;
    data['expiryTime'] = this.expiryTime;
    data['expiryTimeUnit'] = this.expiryTimeUnit;
    data['refundAmount'] = this.refundAmount;
    data['totalInPorts'] = this.totalInPorts;
    data['totalOutPorts'] = this.totalOutPorts;
    data['availableInPorts'] = this.availableInPorts;
    data['availableOutPorts'] = this.availableOutPorts;
    data['productId'] = this.productId;
    data['navLedgerId'] = this.navLedgerId;
    return data;
  }
}
