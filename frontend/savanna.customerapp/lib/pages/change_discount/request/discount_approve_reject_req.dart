class DiscountApproveRejectReq {
  int? custPackageId;
  String? flag;
  int? nextStaffId;
  String? remark;
  String? staffId;

  DiscountApproveRejectReq(
      {this.custPackageId,
        this.flag,
        this.nextStaffId,
        this.remark,
        this.staffId});

  DiscountApproveRejectReq.fromJson(Map<String, dynamic> json) {
    custPackageId = json['custPackageId'];
    flag = json['flag'];
    nextStaffId = json['nextStaffId'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custPackageId'] = this.custPackageId;
    data['flag'] = this.flag;
    data['nextStaffId'] = this.nextStaffId;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
