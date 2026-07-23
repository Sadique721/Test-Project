class BulkConsApproveRejectReq {
  int? id;
  String? approvalStatus;
  String? approvalRemark;

  BulkConsApproveRejectReq({this.id, this.approvalStatus, this.approvalRemark});

  BulkConsApproveRejectReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    approvalStatus = json['approvalStatus'];
    approvalRemark = json['approvalRemark'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['approvalStatus'] = this.approvalStatus;
    data['approvalRemark'] = this.approvalRemark;
    return data;
  }
}
