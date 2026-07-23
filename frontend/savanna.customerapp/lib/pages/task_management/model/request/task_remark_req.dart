class TaskRemarkReq {
  String? remarkType;
  bool? isFromCustomer;
  String? remark;
  dynamic custId;
  int? caseId;
  String? remarkDate;
  int? staffId;

  TaskRemarkReq(
      {this.remarkType,
        this.isFromCustomer,
        this.remark,
        this.custId,
        this.caseId,
        this.remarkDate,
        this.staffId});

  TaskRemarkReq.fromJson(Map<String, dynamic> json) {
    remarkType = json['remarkType'];
    isFromCustomer = json['isFromCustomer'];
    remark = json['remark'];
    custId = json['custId'];
    caseId = json['caseId'];
    remarkDate = json['remarkDate'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['remarkType'] = this.remarkType;
    data['isFromCustomer'] = this.isFromCustomer;
    data['remark'] = this.remark;
    data['custId'] = this.custId;
    data['caseId'] = this.caseId;
    data['remarkDate'] = this.remarkDate;
    data['staffId'] = this.staffId;
    return data;
  }
}
