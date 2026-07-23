class SendPaymentLinkReq {
  int? custId;

  SendPaymentLinkReq({this.custId});

  SendPaymentLinkReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    return data;
  }
}