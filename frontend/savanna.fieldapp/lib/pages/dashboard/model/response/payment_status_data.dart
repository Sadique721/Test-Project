class PaymentStatus {
  String? status;
  String? label;

  PaymentStatus({this.status,this.label});

  PaymentStatus.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    label = json['label'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    data['label'] = this.label;
    return data;
  }
}
