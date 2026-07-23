class ArtelPaymentReq {
  int? customerId;
  int? planId;
  String? amount;
  bool? isFromCaptive;
  String? merchantName;
  String? customerUserName;
  int? mvnoId;
  int? custServiceMappingId;
  String? mobileNumber;
  int? invoiceId;
  int? partnerId;
  String? accountNumber;

  ArtelPaymentReq(
      {this.customerId,
        this.planId,
        this.amount,
        this.isFromCaptive,
        this.merchantName,
        this.customerUserName,
        this.mvnoId,
        this.custServiceMappingId,
        this.mobileNumber,
        this.invoiceId,
        this.partnerId,
        this.accountNumber});

  ArtelPaymentReq.fromJson(Map<String, dynamic> json) {
    customerId = json['customerId'];
    planId = json['planId'];
    amount = json['amount'];
    isFromCaptive = json['isFromCaptive'];
    merchantName = json['merchantName'];
    customerUserName = json['customerUserName'];
    mvnoId = json['mvnoId'];
    custServiceMappingId = json['custServiceMappingId'];
    mobileNumber = json['mobileNumber'];
    invoiceId = json['invoiceId'];
    partnerId = json['partnerId'];
    accountNumber = json['accountNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerId'] = this.customerId;
    data['planId'] = this.planId;
    data['amount'] = this.amount;
    data['isFromCaptive'] = this.isFromCaptive;
    data['merchantName'] = this.merchantName;
    data['customerUserName'] = this.customerUserName;
    data['mvnoId'] = this.mvnoId;
    data['custServiceMappingId'] = this.custServiceMappingId;
    data['mobileNumber'] = this.mobileNumber;
    data['invoiceId'] = this.invoiceId;
    data['partnerId'] = this.partnerId;
    data['accountNumber'] = this.accountNumber;
    return data;
  }
}