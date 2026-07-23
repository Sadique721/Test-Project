class TaskCaseAssignReq {
  //caseUpdate
  int? assignee;
  String? remark;
  String? status;
  String? remarkType;
  int? ticketId;
  int? teamId;

  TaskCaseAssignReq(
      {this.assignee,
        this.remark,
        this.status,
        this.remarkType,
        this.ticketId,
        this.teamId,
      });

  TaskCaseAssignReq.fromJson(Map<String, dynamic> json) {
    assignee = json['assignee'];
    remark = json['remark'];
    status = json['status'];
    remarkType = json['remarkType'];
    ticketId = json['ticketId'];
    teamId = json['teamId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['assignee'] = this.assignee;
    data['remark'] = this.remark;
    data['status'] = this.status;
    data['remarkType'] = this.remarkType;
    data['ticketId'] = this.ticketId;
    data['teamId'] = this.teamId;
    return data;
  }
}