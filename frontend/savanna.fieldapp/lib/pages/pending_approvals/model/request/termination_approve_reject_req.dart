class TerminationApproveRejectReq {
  int? id;
  String? status;

  TerminationApproveRejectReq({this.id, this.status});

  TerminationApproveRejectReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['status'] = this.status;
    return data;
  }
}