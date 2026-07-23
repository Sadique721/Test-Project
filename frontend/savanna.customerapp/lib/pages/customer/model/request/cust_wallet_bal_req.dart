class CustomerWalletReq {
  String? cREATEDATE;
  String? eNDDATE;
  String? amount;
  String? balAmount;
  int? custId;
  String? description;
  String? id;
  String? refNo;
  String? transcategory;
  String? transtype;

  CustomerWalletReq(
      {this.cREATEDATE,
      this.eNDDATE,
      this.amount,
      this.balAmount,
      this.custId,
      this.description,
      this.id,
      this.refNo,
      this.transcategory,
      this.transtype});

  CustomerWalletReq.fromJson(Map<String, dynamic> json) {
    cREATEDATE = json['CREATE_DATE'];
    eNDDATE = json['END_DATE'];
    amount = json['amount'];
    balAmount = json['balAmount'];
    custId = json['custId'];
    description = json['description'];
    id = json['id'];
    refNo = json['refNo'];
    transcategory = json['transcategory'];
    transtype = json['transtype'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['CREATE_DATE'] = this.cREATEDATE;
    data['END_DATE'] = this.eNDDATE;
    data['amount'] = this.amount;
    data['balAmount'] = this.balAmount;
    data['custId'] = this.custId;
    data['description'] = this.description;
    data['id'] = this.id;
    data['refNo'] = this.refNo;
    data['transcategory'] = this.transcategory;
    data['transtype'] = this.transtype;
    return data;
  }
}
