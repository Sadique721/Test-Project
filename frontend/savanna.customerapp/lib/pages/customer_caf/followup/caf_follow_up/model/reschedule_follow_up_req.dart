class RescheduleFollowUpReq {
  int? id;
  String? followUpName;
  String? followUpDatetime;
  String? remarks;
  dynamic isMissed;
  int? caseId;
  int? customersId;
  String? remarksTemp;
  int? staffUserId;
  int? mvnoId;
  bool? isSend;
  String? status;

  RescheduleFollowUpReq(
      {this.id,
        this.followUpName,
        this.followUpDatetime,
        this.remarks,
        this.isMissed,
        this.caseId,
        this.customersId,
        this.remarksTemp,
        this.staffUserId,
        this.mvnoId,
        this.isSend,
        this.status});

  RescheduleFollowUpReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    followUpName = json['followUpName'];
    followUpDatetime = json['followUpDatetime'];
    remarks = json['remarks'];
    isMissed = json['isMissed'];
    caseId = json['caseId'];
    customersId = json['customersId'];
    remarksTemp = json['remarksTemp'];
    staffUserId = json['staffUserId'];
    mvnoId = json['mvnoId'];
    isSend = json['isSend'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['followUpName'] = this.followUpName;
    data['followUpDatetime'] = this.followUpDatetime;
    data['remarks'] = this.remarks;
    data['isMissed'] = this.isMissed;
    data['caseId'] = this.caseId;
    data['customersId'] = this.customersId;
    data['remarksTemp'] = this.remarksTemp;
    data['staffUserId'] = this.staffUserId;
    data['mvnoId'] = this.mvnoId;
    data['isSend'] = this.isSend;
    data['status'] = this.status;
    return data;
  }
}
