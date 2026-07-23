class ChangeCustomerStatusReq {
  int? id;
  String? rf;
  String? status;
  String? remark;

  ChangeCustomerStatusReq({this.id, this.rf, this.status,this.remark});

  ChangeCustomerStatusReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    rf = json['rf'];
    status = json['status'];
    remark = json['remark'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['rf'] = this.rf;
    data['status'] = this.status;
    data['remark'] = this.remark;
    return data;
  }
}
