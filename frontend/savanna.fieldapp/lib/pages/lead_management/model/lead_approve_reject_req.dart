class LeadApproveRejectReq {
  bool? approveRequest;
  int? buId;
  int? currentLoggedInStaffId;
  String? firstname;
  int? id;
  int? mvnoId;
  String? remark;
  int? serviceareaid;
  String? flag;
  int? nextTeamMappingId;
  String? status;
  String? teamName;
  String? username;
  dynamic rejectedReasonMasterId;

  LeadApproveRejectReq(
      {this.approveRequest,
        this.buId,
        this.currentLoggedInStaffId,
        this.firstname,
        this.id,
        this.mvnoId,
        this.remark,
        this.serviceareaid,
        this.flag,
        this.nextTeamMappingId,
        this.status,
        this.teamName,
        this.username,
        this.rejectedReasonMasterId});

  LeadApproveRejectReq.fromJson(Map<String, dynamic> json) {
    approveRequest = json['approveRequest'];
    buId = json['buId'];
    currentLoggedInStaffId = json['currentLoggedInStaffId'];
    firstname = json['firstname'];
    id = json['id'];
    mvnoId = json['mvnoId'];
    remark = json['remark'];
    serviceareaid = json['serviceareaid'];
    flag = json['flag'];
    nextTeamMappingId = json['nextTeamMappingId'];
    status = json['status'];
    teamName = json['teamName'];
    username = json['username'];
    rejectedReasonMasterId = json['rejectedReasonMasterId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['approveRequest'] = this.approveRequest;
    data['buId'] = this.buId;
    data['currentLoggedInStaffId'] = this.currentLoggedInStaffId;
    data['firstname'] = this.firstname;
    data['id'] = this.id;
    data['mvnoId'] = this.mvnoId;
    data['remark'] = this.remark;
    data['serviceareaid'] = this.serviceareaid;
    data['flag'] = this.flag;
    data['nextTeamMappingId'] = this.nextTeamMappingId;
    data['status'] = this.status;
    data['teamName'] = this.teamName;
    data['username'] = this.username;
    data['rejectedReasonMasterId'] = this.rejectedReasonMasterId;
    return data;
  }
}
