class CaseFollowupReq {
  int? caseId;
  int? custId;
  String? remark;
  String? remarkDate;
  int? staffId;

  CaseFollowupReq(
      {this.caseId, this.custId, this.remark, this.remarkDate, this.staffId});

  CaseFollowupReq.fromJson(Map<String, dynamic> json) {
    caseId = json['caseId'];
    custId = json['custId'];
    remark = json['remark'];
    remarkDate = json['remarkDate'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['caseId'] = this.caseId;
    data['custId'] = this.custId;
    data['remark'] = this.remark;
    data['remarkDate'] = this.remarkDate;
    data['staffId'] = this.staffId;
    return data;
  }
}