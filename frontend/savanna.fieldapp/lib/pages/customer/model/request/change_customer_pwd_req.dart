class ChangeCustomerPasswordReq {
  int? custId;
  String? newpassword;
  String? password;
  String? remarks;
  String? selfcarepwd;

  ChangeCustomerPasswordReq({this.custId,
    this.newpassword,
    this.password,
    this.remarks,
    this.selfcarepwd});

  ChangeCustomerPasswordReq.fromJson(Map<String, dynamic> json) {
    custId = json['custId'];
    newpassword = json['newpassword'];
    password = json['password'];
    remarks = json['remarks'];
    selfcarepwd = json['selfcarepwd'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['custId'] = this.custId;
    data['newpassword'] = this.newpassword;
    data['password'] = this.password;
    data['remarks'] = this.remarks;
    data['selfcarepwd'] = this.selfcarepwd;
    return data;
  }
}
