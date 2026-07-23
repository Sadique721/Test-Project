class PlanApproveRejectReq {
  int? planId;
  String? nextStaffId;
  String? flag;
  String? remark;
  String? staffId;

  PlanApproveRejectReq(
      {this.planId, this.nextStaffId, this.flag, this.remark, this.staffId});

  PlanApproveRejectReq.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    nextStaffId = json['nextStaffId'];
    flag = json['flag'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['nextStaffId'] = this.nextStaffId;
    data['flag'] = this.flag;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
