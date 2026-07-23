class InvoicePaymentAdjustRes {
  String? invoicePamentAdjust;
  String? timestamp;
  int? status;

  InvoicePaymentAdjustRes(
      {this.invoicePamentAdjust, this.timestamp, this.status});

  InvoicePaymentAdjustRes.fromJson(Map<String, dynamic> json) {
    invoicePamentAdjust = json['InvoicePamentAdjust'];
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['InvoicePamentAdjust'] = this.invoicePamentAdjust;
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}
