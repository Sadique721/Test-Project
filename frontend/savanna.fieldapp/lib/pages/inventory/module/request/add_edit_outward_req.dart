class AddEditOutwardReq {
  int? id;
  int? productId;
  num? qty;
  String? outwardDateTime;
  int? sourceId;
  String? sourceType;
  String? status;
  String? outwardNumber;
  int? destinationId;
  String? destinationType;
  int? mvnoId;
  int? inwardId;
  num? usedQty;
  num? unusedQty;
  num? inTransitQty;
  num? outTransitQty;
  num? rejectedQty;

  AddEditOutwardReq(
      {this.id,
      this.productId,
      this.qty,
      this.outwardDateTime,
      this.sourceId,
      this.sourceType,
      this.status,
      this.outwardNumber,
      this.destinationId,
      this.destinationType,
      this.mvnoId,
      this.inwardId,
      this.usedQty,
      this.unusedQty,
      this.inTransitQty,
      this.outTransitQty,
      this.rejectedQty});

  AddEditOutwardReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    qty = json['qty'];
    outwardDateTime = json['outwardDateTime'];
    sourceId = json['sourceId'];
    sourceType = json['sourceType'];
    status = json['status'];
    outwardNumber = json['outwardNumber'];
    destinationId = json['destinationId'];
    destinationType = json['destinationType'];
    mvnoId = json['mvnoId'];
    inwardId = json['inwardId'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inTransitQty = json['inTransitQty'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['outwardDateTime'] = this.outwardDateTime;
    data['sourceId'] = this.sourceId;
    data['sourceType'] = this.sourceType;
    data['status'] = this.status;
    data['outwardNumber'] = this.outwardNumber;
    data['destinationId'] = this.destinationId;
    data['destinationType'] = this.destinationType;
    data['mvnoId'] = this.mvnoId;
    data['inwardId'] = this.inwardId;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inTransitQty'] = this.inTransitQty;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    return data;
  }
}
