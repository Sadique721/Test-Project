class CustomerApproveRejectReq {
  int? custcafId;
  String? nextStaffId;
  String? flag;
  String? remark;
  String? staffId;

  CustomerApproveRejectReq(
      {this.custcafId, this.nextStaffId, this.flag, this.remark, this.staffId});

  CustomerApproveRejectReq.fromJson(Map<String, dynamic> json) {
    custcafId = json['custcafId'];
    nextStaffId = json['nextStaffId'];
    flag = json['flag'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custcafId'] = this.custcafId;
    data['nextStaffId'] = this.nextStaffId;
    data['flag'] = this.flag;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
