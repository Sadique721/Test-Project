class CaseAssignReq {
  //caseUpdate
  int? assignee;
  String? remark;
  String? status;
  String? remarkType;
  String? helperName;
  String? nextFollowupDate;
  String? nextFollowupTime;
  int? finalResolutionId;
  int? rootCauseReasonId;
  int? ticketId;

  CaseAssignReq(
      {this.assignee,
      this.remark,
      this.status,
      this.remarkType,
      this.helperName,
      this.nextFollowupDate,
      this.nextFollowupTime,
      this.finalResolutionId,
      this.rootCauseReasonId,
      this.ticketId});

  CaseAssignReq.fromJson(Map<String, dynamic> json) {
    assignee = json['assignee'];
    remark = json['remark'];
    status = json['status'];
    remarkType = json['remarkType'];
    helperName = json['helperName'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    finalResolutionId = json['finalResolutionId'];
    rootCauseReasonId = json['rootCauseReasonId'];
    ticketId = json['ticketId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['assignee'] = this.assignee;
    data['remark'] = this.remark;
    data['status'] = this.status;
    data['remarkType'] = this.remarkType;
    data['helperName'] = this.helperName;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['finalResolutionId'] = this.finalResolutionId;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['ticketId'] = this.ticketId;
    return data;
  }
}



class CaseAssignNewReq {
  int? ticketId;
  String? status;
  String? remark;
  String? remarkType;
  String? helperName;
  String? nextFollowupDate;
  String? nextFollowupTime;
  String? callStatus;
  String? isClosed;
  dynamic caseFeedbackRel;
  String? deacivateReason;

  CaseAssignNewReq(
      {this.ticketId,
        this.status,
        this.remark,
        this.remarkType,
        this.helperName,
        this.nextFollowupDate,
        this.nextFollowupTime,
        this.callStatus,
        this.isClosed,
        this.caseFeedbackRel,
        this.deacivateReason});

  CaseAssignNewReq.fromJson(Map<String, dynamic> json) {
    ticketId = json['ticketId'];
    status = json['status'];
    remark = json['remark'];
    remarkType = json['remarkType'];
    helperName = json['helperName'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    callStatus = json['call_status'];
    isClosed = json['is_closed'];
    caseFeedbackRel = json['caseFeedbackRel'];
    deacivateReason = json['deacivate_reason'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['ticketId'] = this.ticketId;
    data['status'] = this.status;
    data['remark'] = this.remark;
    data['remarkType'] = this.remarkType;
    data['helperName'] = this.helperName;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['call_status'] = this.callStatus;
    data['is_closed'] = this.isClosed;
    data['caseFeedbackRel'] = this.caseFeedbackRel;
    data['deacivate_reason'] = this.deacivateReason;
    return data;
  }
}

