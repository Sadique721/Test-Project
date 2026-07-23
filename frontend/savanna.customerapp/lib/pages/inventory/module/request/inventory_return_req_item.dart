class InventoryReturnReqItem {
  String? remarks;
  int? id;

  InventoryReturnReqItem({this.remarks, this.id});

  InventoryReturnReqItem.fromJson(Map<String, dynamic> json) {
    remarks = json['remarks'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['remarks'] = this.remarks;
    data['id'] = this.id;
    return data;
  }
}
