class PaymentApproveRejectReq {
  int? customerid;
  int? idlist;
  String? paymode;
  String? paystatus;
  String? paytodate;
  String? remarks;
  String? referenceno;

  PaymentApproveRejectReq(
      {this.customerid,
      this.idlist,
      this.paymode,
      this.paystatus,
      this.paytodate,
      this.remarks,
      this.referenceno});

  PaymentApproveRejectReq.fromJson(Map<String, dynamic> json) {
    customerid = json['customerid'];
    idlist = json['idlist'];
    paymode = json['paymode'];
    paystatus = json['paystatus'];
    paytodate = json['paytodate'];
    remarks = json['remarks'];
    referenceno= json['referenceno'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['customerid'] = this.customerid;
    data['idlist'] = this.idlist;
    data['paymode'] = this.paymode;
    data['paystatus'] = this.paystatus;
    data['paytodate'] = this.paytodate;
    data['remarks'] = this.remarks;
    data['referenceno'] = this.referenceno;
    return data;
  }
}
