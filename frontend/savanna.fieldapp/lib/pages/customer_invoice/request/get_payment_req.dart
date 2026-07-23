class GetPaymentStatusReq {
  String? orderId;
  String? status;

  GetPaymentStatusReq({this.orderId, this.status});

  GetPaymentStatusReq.fromJson(Map<String, dynamic> json) {
    orderId = json['orderId'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['orderId'] = this.orderId;
    data['status'] = this.status;
    return data;
  }
}
