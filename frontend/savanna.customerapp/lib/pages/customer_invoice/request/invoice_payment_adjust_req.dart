class InvoicePaymentAdjustReq {
  int? invoiceId;
  List<CreditDocumentList>? creditDocumentList;

  InvoicePaymentAdjustReq({this.invoiceId, this.creditDocumentList});

  InvoicePaymentAdjustReq.fromJson(Map<String, dynamic> json) {
    invoiceId = json['invoiceId'];
    if (json['creditDocumentList'] != null) {
      creditDocumentList = <CreditDocumentList>[];
      json['creditDocumentList'].forEach((v) {
        creditDocumentList!.add(new CreditDocumentList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['invoiceId'] = this.invoiceId;
    if (this.creditDocumentList != null) {
      data['creditDocumentList'] =
          this.creditDocumentList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class CreditDocumentList {
  dynamic id;
  dynamic amount;

  CreditDocumentList({this.id, this.amount});

  CreditDocumentList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    amount = json['amount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['amount'] = this.amount;
    return data;
  }
}
