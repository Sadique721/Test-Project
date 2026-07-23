class CustomerLedgerReq {
  String? cREATEDATE;
  String? eNDDATE;
  String? id;
  String? amount;
  String? balAmount;
  int? custId;
  String? description;
  String? refNo;
  String? transcategory;
  String? transtype;

  CustomerLedgerReq(
      {this.cREATEDATE,
        this.eNDDATE,
        this.id,
        this.amount,
        this.balAmount,
        this.custId,
        this.description,
        this.refNo,
        this.transcategory,
        this.transtype});

  CustomerLedgerReq.fromJson(Map<String, dynamic> json) {
    cREATEDATE = json['CREATE_DATE'];
    eNDDATE = json['END_DATE'];
    id = json['id'];
    amount = json['amount'];
    balAmount = json['balAmount'];
    custId = json['custId'];
    description = json['description'];
    refNo = json['refNo'];
    transcategory = json['transcategory'];
    transtype = json['transtype'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['CREATE_DATE'] = this.cREATEDATE;
    data['END_DATE'] = this.eNDDATE;
    data['id'] = this.id;
    data['amount'] = this.amount;
    data['balAmount'] = this.balAmount;
    data['custId'] = this.custId;
    data['description'] = this.description;
    data['refNo'] = this.refNo;
    data['transcategory'] = this.transcategory;
    data['transtype'] = this.transtype;
    return data;
  }
}