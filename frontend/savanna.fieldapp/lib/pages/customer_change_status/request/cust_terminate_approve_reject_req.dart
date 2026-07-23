class CustomerTerminateApproveRejectReq {
  int? id;
  String? status;
  String? remarks;

  CustomerTerminateApproveRejectReq({this.id, this.status, this.remarks});

  CustomerTerminateApproveRejectReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    status = json['status'];
    remarks = json['remarks'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['status'] = this.status;
    data['remarks'] = this.remarks;
    return data;
  }
}
