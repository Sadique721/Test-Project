class RecordPaymentReq {
  num? amount;
  String? chequedate;
  String? chequeno;
  int? customerid;
  List<int>? invoiceId;
  String? paymentdate;
  String? onlinesource;
  List<PaymentListPojos>? paymentListPojos;
  String? paymode;
  String? referenceno;
  String? payReferenceNo;
  String? remark;
  String? type;
String? destinationBank;
  String? bankManagement;
  String? paytype;
  String? bank;
  String? branch;
  num? barteramount;
  String? reciptNo;
  num? tdsAmount;
  num? abbsAmount;
  String? filename;

  RecordPaymentReq(
      {this.amount,
      this.chequedate,
      this.chequeno,
      this.customerid,
      this.invoiceId,
      this.paymentdate,
      this.onlinesource,
      this.paymentListPojos,
      this.paymode,
      this.referenceno,
        this.payReferenceNo,
      this.remark,
      this.type,
      this.destinationBank,
      this.bankManagement,
      this.paytype,
      this.bank,
      this.branch,
        this.barteramount,
        this.reciptNo,
        this.tdsAmount,
        this.abbsAmount,
        this.filename
      });

  RecordPaymentReq.fromJson(Map<String, dynamic> json) {
    amount = json['amount'];
    chequedate = json['chequedate'];
    chequeno = json['chequeno'];
    customerid = json['customerid'];
    invoiceId = json['invoiceId'].cast<int>();
    paymentdate = json['paymentdate'];
    onlinesource = json['onlinesource'];
    if (json['paymentListPojos'] != null) {
      paymentListPojos = <PaymentListPojos>[];
      json['paymentListPojos'].forEach((v) {
        paymentListPojos!.add(new PaymentListPojos.fromJson(v));
      });
    }
    paymode = json['paymode'];
    referenceno = json['referenceno'];
    payReferenceNo = json['paymentreferenceno'];
    remark = json['remark'];
    type = json['type'];
    destinationBank = json['destinationBank'];
    bankManagement = json['bankManagement'];
    paytype = json['paytype'];
    bank = json['bank'];
    branch = json['branch'];
    barteramount = json['barteramount'];
    reciptNo = json['reciptNo'];
    tdsAmount = json['tdsAmount'];
    abbsAmount = json['abbsAmount'];
    filename = json['filename'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['amount'] = this.amount;
    data['chequedate'] = this.chequedate;
    data['chequeno'] = this.chequeno;
    data['customerid'] = this.customerid;
    data['invoiceId'] = this.invoiceId;
    data['paymentdate'] = this.paymentdate;
    data['onlinesource'] = this.onlinesource;
    if (this.paymentListPojos != null) {
      data['paymentListPojos'] =
          this.paymentListPojos!.map((v) => v.toJson()).toList();
    }
    data['paymode'] = this.paymode;
    data['referenceno'] = this.referenceno;
    data['paymentreferenceno'] = this.payReferenceNo;
    data['remark'] = this.remark;
    data['type'] = this.type;
    data['destinationBank'] = this.destinationBank;
    data['bankManagement'] = this.bankManagement;
    data['paytype'] = this.paytype;
    data['bank'] = this.bank;
    data['branch'] = this.branch;
    data['barteramount'] = this.barteramount;
    data['reciptNo'] = this.reciptNo;
    data['tdsAmount'] = this.tdsAmount;
    data['abbsAmount'] = this.abbsAmount;
    data['filename'] = this.filename;
    return data;
  }
}
class PaymentListPojos {
  dynamic abbsAmountAgainstInvoice;
  String? amountAgainstInvoice;
  int? invoiceId;
  dynamic tdsAmountAgainstInvoice;

  PaymentListPojos(
      {this.abbsAmountAgainstInvoice,
        this.amountAgainstInvoice,
        this.invoiceId,
        this.tdsAmountAgainstInvoice});

  PaymentListPojos.fromJson(Map<String, dynamic> json) {
    abbsAmountAgainstInvoice = json['abbsAmountAgainstInvoice'];
    amountAgainstInvoice = json['amountAgainstInvoice'];
    invoiceId = json['invoiceId'];
    tdsAmountAgainstInvoice = json['tdsAmountAgainstInvoice'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['abbsAmountAgainstInvoice'] = this.abbsAmountAgainstInvoice;
    data['amountAgainstInvoice'] = this.amountAgainstInvoice;
    data['invoiceId'] = this.invoiceId;
    data['tdsAmountAgainstInvoice'] = this.tdsAmountAgainstInvoice;
    return data;
  }
}