class CustMacMapppingDetail {
  int? custid;
  int? id;
  String? macAddress;
  bool? isDeleted;

  CustMacMapppingDetail(
      {this.custid, this.id, this.macAddress, this.isDeleted});

  CustMacMapppingDetail.fromJson(Map<String, dynamic> json) {
    custid = json['custid'];
    id = json['id'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custid'] = this.custid;
    data['id'] = this.id;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}
