class CustChargeDetails {
  int? id;
  int? planid;
  int? chargeid;
  String? chargeName;
  String? chargetype;
  num? validity;
  num? price;
  num? actualprice;
  String? chargeDateString;

  CustChargeDetails(
      {this.id,
      this.planid,
      this.chargeid,
      this.chargeName,
      this.chargetype,
      this.validity,
      this.price,
      this.actualprice,
      this.chargeDateString});

  CustChargeDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    planid = json['planid'];
    chargeid = json['chargeid'];
    chargeName = json['chargeName'];
    chargetype = json['chargetype'];
    validity = json['validity'];
    price = json['price'];
    actualprice = json['actualprice'];
    chargeDateString = json['chargeDateString'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['planid'] = this.planid;
    data['chargeid'] = this.chargeid;
    data['chargeName'] = this.chargeName;
    data['chargetype'] = this.chargetype;
    data['validity'] = this.validity;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['chargeDateString'] = this.chargeDateString;
    return data;
  }
}
