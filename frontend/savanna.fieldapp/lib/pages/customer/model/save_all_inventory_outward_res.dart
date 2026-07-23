class SaveAllInventoryOutwardRes {
  String? id;
  int? productId;
  String? qty;
  String? outwardDateTime;
  String? source;
  int? sourceId;
  String? sourceType;
  String? status;
  String? outwardNumber;
  int? destinationId;
  String? destinationType;
  Null? mvnoId;
  int? usedQty;
  int? unusedQty;
  int? inTransitQty;
  String? outTransitQty;
  int? rejectedQty;
  int? requestInventoryId;
  int? requestInventoryProductId;
  int? selectedItems;

  SaveAllInventoryOutwardRes(
      {this.id,
        this.productId,
        this.qty,
        this.outwardDateTime,
        this.source,
        this.sourceId,
        this.sourceType,
        this.status,
        this.outwardNumber,
        this.destinationId,
        this.destinationType,
        this.mvnoId,
        this.usedQty,
        this.unusedQty,
        this.inTransitQty,
        this.outTransitQty,
        this.rejectedQty,
        this.requestInventoryId,
        this.requestInventoryProductId,
        this.selectedItems});

  SaveAllInventoryOutwardRes.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    qty = json['qty'];
    outwardDateTime = json['outwardDateTime'];
    source = json['source'];
    sourceId = json['sourceId'];
    sourceType = json['sourceType'];
    status = json['status'];
    outwardNumber = json['outwardNumber'];
    destinationId = json['destinationId'];
    destinationType = json['destinationType'];
    mvnoId = json['mvnoId'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    inTransitQty = json['inTransitQty'];
    outTransitQty = json['outTransitQty'];
    rejectedQty = json['rejectedQty'];
    requestInventoryId = json['requestInventoryId'];
    requestInventoryProductId = json['requestInventoryProductId'];
    selectedItems = json['selectedItems'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['outwardDateTime'] = this.outwardDateTime;
    data['source'] = this.source;
    data['sourceId'] = this.sourceId;
    data['sourceType'] = this.sourceType;
    data['status'] = this.status;
    data['outwardNumber'] = this.outwardNumber;
    data['destinationId'] = this.destinationId;
    data['destinationType'] = this.destinationType;
    data['mvnoId'] = this.mvnoId;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['inTransitQty'] = this.inTransitQty;
    data['outTransitQty'] = this.outTransitQty;
    data['rejectedQty'] = this.rejectedQty;
    data['requestInventoryId'] = this.requestInventoryId;
    data['requestInventoryProductId'] = this.requestInventoryProductId;
    data['selectedItems'] = this.selectedItems;
    return data;
  }
}
