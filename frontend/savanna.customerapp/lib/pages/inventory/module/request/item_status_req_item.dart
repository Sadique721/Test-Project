class ItemStatusReqItem {
  String? itemStatus;
  int? id;

  ItemStatusReqItem({this.itemStatus, this.id});

  ItemStatusReqItem.fromJson(Map<String, dynamic> json) {
    itemStatus = json['itemStatus'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['itemStatus'] = this.itemStatus;
    data['id'] = this.id;
    return data;
  }
}
