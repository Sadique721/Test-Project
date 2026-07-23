class InventoryItemTypeReq {
  String? condition;
  String? remarks;
  String? otherreason;
  int? itemId;
  String? filename;

  InventoryItemTypeReq(
      {this.condition,
        this.remarks,
        this.otherreason,
        this.itemId,
        this.filename});

  InventoryItemTypeReq.fromJson(Map<String, dynamic> json) {
    condition = json['condition'];
    remarks = json['remarks'];
    otherreason = json['otherreason'];
    itemId = json['itemId'];
    filename = json['filename'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['condition'] = this.condition;
    data['remarks'] = this.remarks;
    data['otherreason'] = this.otherreason;
    data['itemId'] = this.itemId;
    data['filename'] = this.filename;
    return data;
  }
}