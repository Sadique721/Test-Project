class WarrantyStatusReqItem {
  String? warranty;
  int? id;

  WarrantyStatusReqItem({this.warranty, this.id});

  WarrantyStatusReqItem.fromJson(Map<String, dynamic> json) {
    warranty = json['warranty'];
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['warranty'] = this.warranty;
    data['id'] = this.id;
    return data;
  }
}
