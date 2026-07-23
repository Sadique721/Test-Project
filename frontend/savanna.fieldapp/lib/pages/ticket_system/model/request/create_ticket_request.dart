class CreateTicketRequest {
  //int? ticketId;
  String? caseTitle;
  int? customersId;
  String? userName;
  String? serviceAreaName;
  String? caseType;
  int? ticketReasonCategoryId;
  int? reasonSubCategoryId;
  int? groupReasonId;
  String? priority;
  String? nextFollowupDate;
  String? nextFollowupTime;
  String? caseStatus;
  int? currentAssigneeId;
  int? finalResolutionId;
  String? firstRemark;
  int? rootCauseReasonId;
  String? source;
  String? ticketClassification;
  String? subSource;
  String? department;
  String? customerAdditionalMobileNumber;
  String? customerAdditionalEmail;
  String? file;
  String? helperName;
  String? caseForPartner;
  String? caseFor;
  String? caseOrigin;
  String? serialNumber;

  // String? assignee;
  // String? remarkType;
  //String? status;
  // String? attachment;
  // String? filename;

  // String? caseReasonId;
  List<TicketServicemappingList>? ticketServicemappingList;

  CreateTicketRequest({
    //this.ticketId,
    this.caseTitle,
    this.customersId,
    this.userName,
    this.serviceAreaName,
    this.caseType,
    this.ticketReasonCategoryId,
    this.reasonSubCategoryId,
    this.groupReasonId,
    this.priority,
    this.nextFollowupDate,
    this.nextFollowupTime,
    this.caseStatus,
    this.currentAssigneeId,
    this.finalResolutionId,
    this.firstRemark,
    this.rootCauseReasonId,
    this.source,
    this.ticketClassification,
    this.subSource,
    this.department,
    this.customerAdditionalMobileNumber,
    this.customerAdditionalEmail,
    this.file,
    this.helperName,
    this.caseForPartner,
    this.caseFor,
    this.caseOrigin,
    this.serialNumber,
    this.ticketServicemappingList,
    /* this.assignee,
      this.remarkType,
      this.status,
      this.attachment,
      this.filename*/
  });

  CreateTicketRequest.fromJson(Map<String, dynamic> json) {
    //ticketId = json['ticketId'];
    caseTitle = json['caseTitle'];
    customersId = json['customersId'];
    userName = json['userName'];
    serviceAreaName = json['serviceAreaName'];
    caseType = json['caseType'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    reasonSubCategoryId = json['reasonSubCategoryId'];
    groupReasonId = json['groupReasonId'];
    priority = json['priority'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    caseStatus = json['caseStatus'];
    currentAssigneeId = json['currentAssigneeId'];
    finalResolutionId = json['finalResolutionId'];
    firstRemark = json['firstRemark'];
    rootCauseReasonId = json['rootCauseReasonId'];
    source = json['source'];
    ticketClassification = json['ticketClassification'];
    subSource = json['subSource'];
    department = json['department'];
    customerAdditionalMobileNumber = json['customerAdditionalMobileNumber'];
    customerAdditionalEmail = json['customerAdditionalEmail'];
    file = json['file'];
    helperName = json['helperName'];
    caseForPartner = json['caseForPartner'];
    caseFor = json['caseFor'];
    caseOrigin = json['caseOrigin'];
    serialNumber = json['serialNumber'];
    if (json['ticketServicemappingList'] != null) {
      ticketServicemappingList = <TicketServicemappingList>[];
      json['ticketServicemappingList'].forEach((v) {
        ticketServicemappingList!.add(new TicketServicemappingList.fromJson(v));
      });
    }
    /* assignee = json['assignee'];
    remarkType = json['remarkType'];
    status = json['status'];
    attachment = json['attachment'];
    filename = json['filename'];*/
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    // data['ticketId'] = this.ticketId;
    data['caseTitle'] = this.caseTitle;
    data['customersId'] = this.customersId;
    data['userName'] = this.userName;
    data['serviceAreaName'] = this.serviceAreaName;
    data['caseType'] = this.caseType;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['reasonSubCategoryId'] = this.reasonSubCategoryId;
    data['groupReasonId'] = this.groupReasonId;
    data['priority'] = this.priority;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['caseStatus'] = this.caseStatus;
    data['currentAssigneeId'] = this.currentAssigneeId;
    data['finalResolutionId'] = this.finalResolutionId;
    data['firstRemark'] = this.firstRemark;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['source'] = this.source;
    data['ticketClassification'] = this.ticketClassification;
    data['subSource'] = this.subSource;
    data['department'] = this.department;
    data['customerAdditionalMobileNumber'] =
        this.customerAdditionalMobileNumber;
    data['customerAdditionalEmail'] = this.customerAdditionalEmail;
    data['file'] = this.file;
    data['helperName'] = this.helperName;
    data['caseForPartner'] = this.caseForPartner;
    data['caseFor'] = this.caseFor;
    data['caseOrigin'] = this.caseOrigin;
    data['serialNumber'] = this.serialNumber;
    if (this.ticketServicemappingList != null) {
      data['ticketServicemappingList'] =
          this.ticketServicemappingList!.map((v) => v.toJson()).toList();
    }
    /* data['assignee'] = this.assignee;
    data['remarkType'] = this.remarkType;
    data['status'] = this.status;
    data['attachment'] = this.attachment;
    data['filename'] = this.filename;*/
    return data;
  }
}

class TicketServicemappingList {
  int? serviceid;

  TicketServicemappingList({this.serviceid});

  TicketServicemappingList.fromJson(Map<String, dynamic> json) {
    serviceid = json['serviceid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['serviceid'] = this.serviceid;
    return data;
  }
}
