class CustomerDocApproveRejectReq {
  String? nextStaffId;
  String? flag;
  String? remark;
  String? staffId;

  CustomerDocApproveRejectReq(
      {this.nextStaffId, this.flag, this.remark, this.staffId});

  CustomerDocApproveRejectReq.fromJson(Map<String, dynamic> json) {
    nextStaffId = json['nextStaffId'];
    flag = json['flag'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['nextStaffId'] = this.nextStaffId;
    data['flag'] = this.flag;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
