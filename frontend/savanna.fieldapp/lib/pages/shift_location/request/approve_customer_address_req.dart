class ApproveCustomerAddressReq {
  int? addressId;
  String? flag;
  int? nextStaffId;
  String? remark;
  String? staffId;

  ApproveCustomerAddressReq(
      {this.addressId, this.flag, this.nextStaffId, this.remark, this.staffId});

  ApproveCustomerAddressReq.fromJson(Map<String, dynamic> json) {
    addressId = json['addressId'];
    flag = json['flag'];
    nextStaffId = json['nextStaffId'];
    remark = json['remark'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['addressId'] = this.addressId;
    data['flag'] = this.flag;
    data['nextStaffId'] = this.nextStaffId;
    data['remark'] = this.remark;
    data['staffId'] = this.staffId;
    return data;
  }
}
