class InventoryOwnershipStatusChangeReq {
  String? ownershipType;
  String? remarks;
  int? id;

  InventoryOwnershipStatusChangeReq(
      {this.remarks, this.id, this.ownershipType});

  InventoryOwnershipStatusChangeReq.fromJson(Map<String, dynamic> json) {
    remarks = json['remarks'];
    id = json['id'];
    ownershipType = json['ownershipType'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['remarks'] = this.remarks;
    data['id'] = this.id;
    data['ownershipType'] = this.ownershipType;
    return data;
  }
}
