class ChangeInwardStatusReq {
  int? id;
  int? productId;
  String? approvalStatus;
  String? approvalRemark;

  ChangeInwardStatusReq({this.id, this.productId,this.approvalStatus, this.approvalRemark});

  ChangeInwardStatusReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    approvalStatus = json['approvalStatus'];
    approvalRemark = json['approvalRemark'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['approvalStatus'] = this.approvalStatus;
    data['approvalRemark'] = this.approvalRemark;
    return data;
  }
}
