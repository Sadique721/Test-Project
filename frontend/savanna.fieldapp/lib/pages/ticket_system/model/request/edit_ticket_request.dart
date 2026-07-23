class EditTicketRequest {
  int? ticketId;
  String? status;
  String? caseType;
  int? assignee;
  String? priority;
  String? attachment;
  String? filename;
  int? finalResolutionId;
  String? remark;
  String? remarkType;
  int? groupReasonId;
  int? reasonSubCategoryId;
  int? ticketReasonCategoryId;
  String? caseTitle;
  int? rootCauseReasonId;
  String? source;
  String? subSource;
  String? customerAdditionalMobileNumber;
  String? customerAdditionalEmail;
  String? helperName;

  EditTicketRequest(
      {this.ticketId,
      this.status,
      this.caseType,
      this.assignee,
      this.priority,
      this.attachment,
      this.filename,
      this.finalResolutionId,
      this.remarkType,
      this.groupReasonId,
      this.reasonSubCategoryId,
      this.ticketReasonCategoryId,
      this.caseTitle,
      this.rootCauseReasonId,
      this.source,
      this.subSource,
      this.customerAdditionalMobileNumber,
      this.customerAdditionalEmail,
      this.helperName,
      this.remark});

  EditTicketRequest.fromJson(Map<String, dynamic> json) {
    ticketId = json['ticketId'];
    status = json['status'];
    caseType = json['caseType'];
    assignee = json['assignee'];
    priority = json['priority'];
    attachment = json['attachment'];
    filename = json['filename'];
    finalResolutionId = json['finalResolutionId'];
    remark = json['remark'];
    remarkType = json['remarkType'];
    groupReasonId = json['groupReasonId'];
    reasonSubCategoryId = json['reasonSubCategoryId'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    caseTitle = json['caseTitle'];
    rootCauseReasonId = json['rootCauseReasonId'];
    source = json['source'];
    subSource = json['subSource'];
    customerAdditionalMobileNumber = json['customerAdditionalMobileNumber'];
    customerAdditionalEmail = json['customerAdditionalEmail'];
    helperName = json['helperName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['ticketId'] = this.ticketId;
    data['status'] = this.status;
    data['caseType'] = this.caseType;
    data['assignee'] = this.assignee;
    data['priority'] = this.priority;
    data['attachment'] = this.attachment;
    data['filename'] = this.filename;
    data['finalResolutionId'] = this.finalResolutionId;
    data['remark'] = this.remark;
    data['remarkType'] = this.remarkType;
    data['groupReasonId'] = this.groupReasonId;
    data['reasonSubCategoryId'] = this.reasonSubCategoryId;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['caseTitle'] = this.caseTitle;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['source'] = this.source;
    data['subSource'] = this.subSource;
    data['customerAdditionalMobileNumber'] =
        this.customerAdditionalMobileNumber;
    data['customerAdditionalEmail'] = this.customerAdditionalEmail;
    data['helperName'] = this.helperName;
    return data;
  }
}
