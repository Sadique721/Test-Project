class CreateTaskRequest {
  String? caseForPartner;
  String? caseFor;
  String? caseOrigin;
  dynamic callStatus;
  dynamic caseReason;
  dynamic deacivateReason;
  String? department;
  dynamic isClosed;
  int? mvnoId;
  int? rootCauseReasonId;
  dynamic source;
  dynamic subSource;
  bool? isFromCalender;
  String? startDate;
  String? endDate;
  String? caseStatus;
  String? caseTitle;
  String? caseType;
  int? caseCategoryId;
  int? caseSubCategoryId;
  dynamic groupReasonId;
  String? priority;
  String? nextFollowupDate;
  String? nextFollowupTime;
  dynamic currentAssigneeId;
  int? teamId;
  dynamic file;
  String? firstRemark;
  dynamic helperName;
  String? serialNumber;
  dynamic staffId;
  dynamic caseId;
  dynamic isReassignTask;

  CreateTaskRequest(
      {this.caseForPartner,
        this.caseFor,
        this.caseOrigin,
        this.callStatus,
        this.caseReason,
        this.deacivateReason,
        this.department,
        this.isClosed,
        this.mvnoId,
        this.rootCauseReasonId,
        this.source,
        this.subSource,
        this.isFromCalender,
        this.startDate,
        this.endDate,
        this.caseStatus,
        this.caseTitle,
        this.caseType,
        this.caseCategoryId,
        this.caseSubCategoryId,
        this.groupReasonId,
        this.priority,
        this.nextFollowupDate,
        this.nextFollowupTime,
        this.currentAssigneeId,
        this.teamId,
        this.file,
        this.firstRemark,
        this.helperName,
        this.serialNumber,
        this.staffId,
        this.caseId,
        this.isReassignTask});

  CreateTaskRequest.fromJson(Map<String, dynamic> json) {
    caseForPartner = json['caseForPartner'];
    caseFor = json['caseFor'];
    caseOrigin = json['caseOrigin'];
    callStatus = json['call_status'];
    caseReason = json['caseReason'];
    deacivateReason = json['deacivate_reason'];
    department = json['department'];
    isClosed = json['is_closed'];
    mvnoId = json['mvnoId'];
    rootCauseReasonId = json['rootCauseReasonId'];
    source = json['source'];
    subSource = json['subSource'];
    isFromCalender = json['isFromCalender'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    caseStatus = json['caseStatus'];
    caseTitle = json['caseTitle'];
    caseType = json['caseType'];
    caseCategoryId = json['caseCategoryId'];
    caseSubCategoryId = json['caseSubCategoryId'];
    groupReasonId = json['groupReasonId'];
    priority = json['priority'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    currentAssigneeId = json['currentAssigneeId'];
    teamId = json['teamId'];
    file = json['file'];
    firstRemark = json['firstRemark'];
    helperName = json['helperName'];
    serialNumber = json['serialNumber'];
    staffId = json['staffId'];
    caseId = json['caseId'];
    isReassignTask = json['isReassignTask'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['caseForPartner'] = this.caseForPartner;
    data['caseFor'] = this.caseFor;
    data['caseOrigin'] = this.caseOrigin;
    data['call_status'] = this.callStatus;
    data['caseReason'] = this.caseReason;
    data['deacivate_reason'] = this.deacivateReason;
    data['department'] = this.department;
    data['is_closed'] = this.isClosed;
    data['mvnoId'] = this.mvnoId;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['source'] = this.source;
    data['subSource'] = this.subSource;
    data['isFromCalender'] = this.isFromCalender;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['caseStatus'] = this.caseStatus;
    data['caseTitle'] = this.caseTitle;
    data['caseType'] = this.caseType;
    data['caseCategoryId'] = this.caseCategoryId;
    data['caseSubCategoryId'] = this.caseSubCategoryId;
    data['groupReasonId'] = this.groupReasonId;
    data['priority'] = this.priority;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['currentAssigneeId'] = this.currentAssigneeId;
    data['teamId'] = this.teamId;
    data['file'] = this.file;
    data['firstRemark'] = this.firstRemark;
    data['helperName'] = this.helperName;
    data['serialNumber'] = this.serialNumber;
    data['staffId'] = this.staffId;
    data['caseId'] = this.caseId;
    data['isReassignTask'] = this.isReassignTask;
    return data;
  }
}
