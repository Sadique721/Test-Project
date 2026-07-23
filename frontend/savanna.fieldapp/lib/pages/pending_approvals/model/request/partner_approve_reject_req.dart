class PartnerApproveRejectReq {
  int? partnerPaymentId;
  String? nextStaffId;
  String? flag;
  String? remark;
  String? staffId;

  PartnerApproveRejectReq(
      {this.partnerPaymentId,
      this.nextStaffId,
      this.flag,
      this.remark,
      this.staffId});

  PartnerApproveRejectReq.fromJson(Map<String, dynamic> json) {
    partnerPaymentId = json['partnerPaymentId'];
    nextStaffId = json['nextStaffId'];
    flag = json['flag'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['partnerPaymentId'] = this.partnerPaymentId;
    data['nextStaffId'] = this.nextStaffId;
    data['flag'] = this.flag;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
