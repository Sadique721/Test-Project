class ApproveInventoryReq {
  bool? approveReq;
  List<int>? requestApproveId;

  ApproveInventoryReq({this.approveReq,required this.requestApproveId});

  ApproveInventoryReq.fromJson(Map<String, dynamic> json) {
    approveReq = json['isApproveRequest'];
    requestApproveId = json['0'].cast<int>();
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['isApproveRequest'] = approveReq;
    data['0'] = requestApproveId;
    return data;
  }
}
