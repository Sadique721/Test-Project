class AddEditInwards {
  int? id;
  int? productId;
  num? qty;
  String? inwardDateTime;
  int? destinationId;
  String? destinationType;
  String? type;
  String? status;
  String? inwardNumber;
  int? inTransitQty;
  int? mvnoId;
  int? usedQty;
  int? unusedQty;
  int? outTransitQty;
  int? rejectedQty;
  int? totalMacSerial;
  String? description;
  String? startDateTime;
  String? expiryDateTime;
  List<int>?specificationParametersDTOList;

  AddEditInwards({
    this.id,
    this.productId,
    this.qty,
    this.inwardDateTime,
    this.destinationId,
    this.destinationType,
    this.type,
    this.status,
    this.inwardNumber,
    this.inTransitQty,
    this.mvnoId,
    this.usedQty,
    this.unusedQty,
    this.outTransitQty,
    this.rejectedQty,
    this.totalMacSerial,
    this.description,
    this.startDateTime,
    this.expiryDateTime,
    this.specificationParametersDTOList,
  });

  AddEditInwards.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    qty = json['qty'];
    inwardDateTime = json['inwardDateTime'];
    destinationId = json['destinationId'];
    destinationType = json['destinationType'];
    type = json['type'];
    status = json['status'];
    inwardNumber = json['inwardNumber'];
    inTransitQty = json['inTransitQty'];
    mvnoId = json['mvnoId'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    totalMacSerial = json['totalMacSerial'];
    description = json['description'];
    startDateTime = json['startDateTime'];
    expiryDateTime = json['expiryDateTime'];
    specificationParametersDTOList = json['specificationParametersDTOList'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['inwardDateTime'] = this.inwardDateTime;
    data['destinationId'] = this.destinationId;
    data['destinationType'] = this.destinationType;
    data['type'] = this.type;
    data['status'] = this.status;
    data['inwardNumber'] = this.inwardNumber;
    data['inTransitQty'] = this.inTransitQty;
    data['mvnoId'] = this.mvnoId;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['totalMacSerial'] = this.totalMacSerial;
    data['description'] = this.description;
    data['startDateTime'] = this.startDateTime;
    data['expiryDateTime'] = this.expiryDateTime;
    data['specificationParametersDTOList'] = specificationParametersDTOList;
    return data;
  }
}
