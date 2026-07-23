class CustPaymentDetail {
  String? chequedate;
  String? paymentdate;
  String? chequeno;
  String? bank;
  int? customerid;
  String? paymode;
  num? amount;
  String? paymentreferenceno;
  String? remark;
  String? branch;
  int? invoiceId;
  String? type;

  CustPaymentDetail(
      {this.chequedate,
      this.paymentdate,
      this.chequeno,
      this.bank,
      this.customerid,
      this.paymode,
      this.amount,
      this.paymentreferenceno,
      this.remark,
      this.branch,
      this.invoiceId,
      this.type});

  CustPaymentDetail.fromJson(Map<String, dynamic> json) {
    chequedate = json['chequedate'];
    paymentdate = json['paymentdate'];
    chequeno = json['chequeno'];
    bank = json['bank'];
    customerid = json['customerid'];
    paymode = json['paymode'];
    amount = json['amount'];
    paymentreferenceno = json['paymentreferenceno'];
    remark = json['remark'];
    branch = json['branch'];
    invoiceId = json['invoiceId'];
    type = json['type'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['chequedate'] = this.chequedate;
    data['paymentdate'] = this.paymentdate;
    data['chequeno'] = this.chequeno;
    data['bank'] = this.bank;
    data['customerid'] = this.customerid;
    data['paymode'] = this.paymode;
    data['amount'] = this.amount;
    data['paymentreferenceno'] = this.paymentreferenceno;
    data['remark'] = this.remark;
    data['branch'] = this.branch;
    data['invoiceId'] = this.invoiceId;
    data['type'] = this.type;
    return data;
  }
}
