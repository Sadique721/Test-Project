import 'package:savbill/webservices/base_response.dart';

class PaymentInvoiceRes extends BaseResponse {
  List<PaymentInvoice>? invoicelist;

  PaymentInvoiceRes({this.invoicelist, timestamp, status});

  PaymentInvoiceRes.fromJson(Map<String, dynamic> json) {
    if (json['Invoicelist'] != null) {
      invoicelist = <PaymentInvoice>[];
      json['Invoicelist'].forEach((v) {
        invoicelist!.add(new PaymentInvoice.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.invoicelist != null) {
      data['Invoicelist'] = this.invoicelist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PaymentInvoice {
  String? billdate;
  String? docnumber;
  num? totalamount;
  num? adjustedAmount;

  PaymentInvoice(
      {this.billdate, this.docnumber, this.totalamount, this.adjustedAmount});

  PaymentInvoice.fromJson(Map<String, dynamic> json) {
    billdate = json['billdate'];
    docnumber = json['docnumber'];
    totalamount = json['totalamount'];
    adjustedAmount = json['adjustedAmount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['billdate'] = this.billdate;
    data['docnumber'] = this.docnumber;
    data['totalamount'] = this.totalamount;
    data['adjustedAmount'] = this.adjustedAmount;
    return data;
  }
}
