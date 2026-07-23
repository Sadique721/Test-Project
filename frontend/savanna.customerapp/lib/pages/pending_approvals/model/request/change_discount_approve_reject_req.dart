class ChangeDiscountApproveRejectReq {
  int? custPackageId;
  String? flag;
  int? nextStaffId;
  int? planId;
  String? remark;
  String? staffId;

  ChangeDiscountApproveRejectReq(
      {this.custPackageId,
      this.flag,
      this.nextStaffId,
      this.planId,
      this.remark,
      this.staffId});

  ChangeDiscountApproveRejectReq.fromJson(Map<String, dynamic> json) {
    custPackageId = json['custPackageId'];
    flag = json['flag'];
    nextStaffId = json['nextStaffId'];
    planId = json['planId'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custPackageId'] = this.custPackageId;
    data['flag'] = this.flag;
    data['nextStaffId'] = this.nextStaffId;
    data['planId'] = this.planId;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
