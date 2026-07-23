import 'package:savbill/webservices/base_response.dart';

class ViewTicketResponse extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<TicketDetail>? dataList;

  ViewTicketResponse(
      {status,
      message,
      timestamp,
      responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ViewTicketResponse.fromJson(Map<String, dynamic> json) {
    status = json['status'];
    message = json['message'];
    timestamp = json['timestamp'];
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <TicketDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TicketDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['status'] = this.status;
    data['message'] = this.message;
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['timestamp'] = this.timestamp;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class TicketDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? caseId;
  int? caseReasonId;
  String? caseReasonName;
  String? caseTitle;
  String? caseType;
  String? caseNumber;
  int? caseSlaTime;
  String? caseSlaUnit;
  String? caseFor;
  String? caseOrigin;
  String? caseStatus;
  dynamic status;
  int? caseOrder;
  String? priority;
  int? customersId;
  String? caseForPartner;
  String? nextFollowupDate;
  String? nextFollowupTime;
  String? caseStartedOn;
  String? caseStartedOnString;
  String? firstAssignedOn;
  String? firstAssignedOnString;
  bool? isDelete;
  int? currentAssigneeId;
  int? finalResolutionId;
  int? finalResolvedById;
  dynamic finalClosedById;
  String? finalResolutionDate;
  String? finalClosedDate;
  List<CaseUpdateList>? caseUpdateList;
  String? firstRemark;
  String? oltName;
  String? caseCategoryName;
  String? slotName;
  String? portName;
  String? serviceAreaName;
  int? serviceAreaId;
  String? mobile;
  String? reason;
  String? userName;
  String? currentAssigneeName;
  String? finalResolvedByName;
  String? finalClosedByName;
  String? finalResolutionName;
  String? customerName;
  String? finalClosedByDateString;
  String? finalResolutionDateString;
  String? createDateString;
  String? updateDateString;
  int? partnerid;
  String? partnerName;
  int? mvnoId;
  int? rating;
  int? caseCategoryId;
  String? customerFeedback;

  int? ticketReasonCategoryId;
  int? reasonSubCategoryId;
  int? groupReasonId;
  int? tatMappingId;
  int? buId;
  String? caseReasonCategory;
  String? caseReasonSubCategory;
  String? caseReason;
  int? rootCauseReasonId;
  String? subSource;
  String? source;
  String? ticketClassification;
  int? teamHierarchyMappingId;
  String? department;
  String? customerAdditionalMobileNumber;
  String? customerAdditionalEmail;
  int? parentTicketId;
  String? helperName;
  String? email;

  //String? file;
  List<TicketAssignStaffMappings>? ticketAssignStaffMappings;
  bool? selected = false;
  List<TicketAttachments>? caseDocDetails;

  TicketDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.caseId,
      this.caseReasonId,
      this.caseReasonName,
      this.caseTitle,
      this.caseType,
      this.caseNumber,
      this.caseSlaTime,
      this.caseSlaUnit,
      this.caseFor,
      this.caseOrigin,
      this.caseStatus,
      this.status,
      this.caseOrder,
      this.priority,
      this.customersId,
      this.caseForPartner,
      this.nextFollowupDate,
      this.nextFollowupTime,
      this.caseStartedOn,
      this.caseStartedOnString,
      this.firstAssignedOn,
      this.firstAssignedOnString,
      this.isDelete,
      this.currentAssigneeId,
      this.finalResolutionId,
      this.finalResolvedById,
      this.finalClosedById,
      this.finalResolutionDate,
      this.finalClosedDate,
      this.caseUpdateList,
      this.firstRemark,
      this.oltName,
      this.caseCategoryName,
      this.slotName,
      this.portName,
      this.serviceAreaName,
      this.serviceAreaId,
      this.mobile,
      this.reason,
      this.userName,
      this.currentAssigneeName,
      this.finalResolvedByName,
      this.finalClosedByName,
      this.finalResolutionName,
      this.customerName,
      this.finalClosedByDateString,
      this.finalResolutionDateString,
      this.createDateString,
      this.updateDateString,
      this.partnerid,
      this.partnerName,
      this.mvnoId,
      this.rating,
      this.caseCategoryId,
      this.customerFeedback,
      this.ticketReasonCategoryId,
      this.reasonSubCategoryId,
      this.groupReasonId,
      this.tatMappingId,
      this.buId,
      this.caseReasonCategory,
      this.caseReasonSubCategory,
      this.caseReason,
      this.rootCauseReasonId,
      this.subSource,
      this.source,
      this.ticketClassification,
      this.teamHierarchyMappingId,
      this.department,
      this.customerAdditionalMobileNumber,
      this.customerAdditionalEmail,
      this.parentTicketId,
      this.helperName,
      this.email,
      // this.file,
      this.ticketAssignStaffMappings,
      this.caseDocDetails,
      this.selected});

  TicketDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    caseId = json['caseId'];
    caseReasonId = json['caseReasonId'];
    caseReasonName = json['caseReasonName'];
    caseTitle = json['caseTitle'];
    caseType = json['caseType'];
    caseNumber = json['caseNumber'];
    caseSlaTime = json['caseSlaTime'];
    caseSlaUnit = json['caseSlaUnit'];
    caseFor = json['caseFor'];
    caseOrigin = json['caseOrigin'];
    caseStatus = json['caseStatus'];
    status = json['status'];
    caseOrder = json['case_order'];
    priority = json['priority'];
    customersId = json['customersId'];
    caseForPartner = json['caseForPartner'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    caseStartedOn = json['caseStartedOn'];
    caseStartedOnString = json['caseStartedOnString'];
    firstAssignedOn = json['firstAssignedOn'];
    firstAssignedOnString = json['firstAssignedOnString'];
    isDelete = json['isDelete'];
    currentAssigneeId = json['currentAssigneeId'];
    finalResolutionId = json['finalResolutionId'];
    finalResolvedById = json['finalResolvedById'];
    finalClosedById = json['finalClosedById'];
    finalResolutionDate = json['finalResolutionDate'];
    finalClosedDate = json['finalClosedDate'];
    if (json['caseUpdateList'] != null) {
      caseUpdateList = <CaseUpdateList>[];
      json['caseUpdateList'].forEach((v) {
        caseUpdateList!.add(new CaseUpdateList.fromJson(v));
      });
    }
    firstRemark = json['firstRemark'];
    oltName = json['oltName'];
    caseCategoryName = json['caseCategoryName'];
    slotName = json['slotName'];
    portName = json['portName'];
    serviceAreaName = json['serviceAreaName'];
    serviceAreaId = json['serviceAreaId'];
    mobile = json['mobile'];
    reason = json['reason'];
    userName = json['userName'];
    currentAssigneeName = json['currentAssigneeName'];
    finalResolvedByName = json['finalResolvedByName'];
    finalClosedByName = json['finalClosedByName'];
    finalResolutionName = json['finalResolutionName'];
    customerName = json['customerName'];
    finalClosedByDateString = json['finalClosedByDateString'];
    finalResolutionDateString = json['finalResolutionDateString'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    partnerid = json['partnerid'];
    partnerName = json['partnerName'];
    mvnoId = json['mvnoId'];
    rating = json['rating'];
    caseCategoryId = json['caseCategoryId'];
    customerFeedback = json['customerFeedback'];

    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    reasonSubCategoryId = json['reasonSubCategoryId'];
    groupReasonId = json['groupReasonId'];
    tatMappingId = json['tatMappingId'];
    buId = json['buId'];
    caseReasonCategory = json['caseReasonCategory'];
    caseReasonSubCategory = json['caseReasonSubCategory'];
    caseReason = json['caseReason'];
    rootCauseReasonId = json['rootCauseReasonId'];
    subSource = json['subSource'];
    source = json['source'];
    ticketClassification = json['ticketClassification'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    department = json['department'];
    customerAdditionalMobileNumber = json['customerAdditionalMobileNumber'];
    customerAdditionalEmail = json['customerAdditionalEmail'];
    parentTicketId = json['parentTicketId'];
    helperName = json['helperName'];
    email = json['email'];
    // file = json['file'];
    if (json['ticketAssignStaffMappings'] != null) {
      ticketAssignStaffMappings = <TicketAssignStaffMappings>[];
      json['ticketAssignStaffMappings'].forEach((v) {
        ticketAssignStaffMappings!
            .add(new TicketAssignStaffMappings.fromJson(v));
      });
    }
    if (json['caseDocDetails'] != null) {
      caseDocDetails = <TicketAttachments>[];
      json['caseDocDetails'].forEach((v) {
        caseDocDetails!.add(new TicketAttachments.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['caseId'] = this.caseId;
    data['caseReasonId'] = this.caseReasonId;
    data['caseReasonName'] = this.caseReasonName;
    data['caseTitle'] = this.caseTitle;
    data['caseType'] = this.caseType;
    data['caseNumber'] = this.caseNumber;
    data['caseSlaTime'] = this.caseSlaTime;
    data['caseSlaUnit'] = this.caseSlaUnit;
    data['caseFor'] = this.caseFor;
    data['caseOrigin'] = this.caseOrigin;
    data['caseStatus'] = this.caseStatus;
    data['status'] = this.status;
    data['case_order'] = this.caseOrder;
    data['priority'] = this.priority;
    data['customersId'] = this.customersId;
    data['caseForPartner'] = this.caseForPartner;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['caseStartedOn'] = this.caseStartedOn;
    data['caseStartedOnString'] = this.caseStartedOnString;
    data['firstAssignedOn'] = this.firstAssignedOn;
    data['firstAssignedOnString'] = this.firstAssignedOnString;
    data['isDelete'] = this.isDelete;
    data['currentAssigneeId'] = this.currentAssigneeId;
    data['finalResolutionId'] = this.finalResolutionId;
    data['finalResolvedById'] = this.finalResolvedById;
    data['finalClosedById'] = this.finalClosedById;
    data['finalResolutionDate'] = this.finalResolutionDate;
    data['finalClosedDate'] = this.finalClosedDate;
    if (this.caseUpdateList != null) {
      data['caseUpdateList'] =
          this.caseUpdateList!.map((v) => v.toJson()).toList();
    }
    data['firstRemark'] = this.firstRemark;
    data['oltName'] = this.oltName;
    data['caseCategoryName'] = this.caseCategoryName;
    data['slotName'] = this.slotName;
    data['portName'] = this.portName;
    data['serviceAreaName'] = this.serviceAreaName;
    data['serviceAreaId'] = this.serviceAreaId;
    data['mobile'] = this.mobile;
    data['reason'] = this.reason;
    data['userName'] = this.userName;
    data['currentAssigneeName'] = this.currentAssigneeName;
    data['finalResolvedByName'] = this.finalResolvedByName;
    data['finalClosedByName'] = this.finalClosedByName;
    data['finalResolutionName'] = this.finalResolutionName;
    data['customerName'] = this.customerName;
    data['finalClosedByDateString'] = this.finalClosedByDateString;
    data['finalResolutionDateString'] = this.finalResolutionDateString;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['partnerid'] = this.partnerid;
    data['partnerName'] = this.partnerName;
    data['mvnoId'] = this.mvnoId;
    data['rating'] = this.rating;
    data['caseCategoryId'] = this.caseCategoryId;
    data['customerFeedback'] = this.customerFeedback;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['reasonSubCategoryId'] = this.reasonSubCategoryId;
    data['groupReasonId'] = this.groupReasonId;
    data['tatMappingId'] = this.tatMappingId;
    data['buId'] = this.buId;
    data['caseReasonCategory'] = this.caseReasonCategory;
    data['caseReasonSubCategory'] = this.caseReasonSubCategory;
    data['caseReason'] = this.caseReason;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['subSource'] = this.subSource;
    data['source'] = this.source;
    data['ticketClassification'] = this.ticketClassification;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['department'] = this.department;
    data['customerAdditionalMobileNumber'] =
        this.customerAdditionalMobileNumber;
    data['customerAdditionalEmail'] = this.customerAdditionalEmail;
    data['parentTicketId'] = this.parentTicketId;
    data['helperName'] = this.helperName;
    data['email'] = this.email;
    // data['file'] = this.file;
    if (this.ticketAssignStaffMappings != null) {
      data['ticketAssignStaffMappings'] =
          this.ticketAssignStaffMappings!.map((v) => v.toJson()).toList();
    }
    if (this.caseDocDetails != null) {
      data['caseDocDetails'] =
          this.caseDocDetails!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class CaseUpdateList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? ticketId;
  int? resolutionId;
  String? remarkType;
  String? remark;
  int? reasonId;
  String? commentBy;
  bool? isDeleted;
  String? nextFollowupDate;
  String? nextFollowupTime;
  String? createby;
  String? updateby;
  String? createDateString;
  String? updateDateString;
  List<CaseHistoryDetails>? updateDetails = [];

  String? status;
  String? caseType;
  String? assignee;
  String? priority;
  String? filename;
  int? mvnoId;

  CaseUpdateList({
    this.createdate,
    this.updatedate,
    this.createdByName,
    this.lastModifiedByName,
    this.createdById,
    this.lastModifiedById,
    this.id,
    this.ticketId,
    this.resolutionId,
    this.remarkType,
    this.remark,
    this.reasonId,
    this.commentBy,
    this.isDeleted,
    this.nextFollowupDate,
    this.nextFollowupTime,
    this.createby,
    this.updateby,
    this.createDateString,
    this.updateDateString,
    this.updateDetails,
    this.status,
    this.caseType,
    this.assignee,
    this.priority,
    this.filename,
  });

  CaseUpdateList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    ticketId = json['ticketId'];
    resolutionId = json['resolutionId'];
    remarkType = json['remarkType'];
    remark = json['remark'];
    reasonId = json['reasonId'];
    commentBy = json['commentBy'];
    isDeleted = json['isDeleted'];
    nextFollowupDate = json['nextFollowupDate'];
    nextFollowupTime = json['nextFollowupTime'];
    createby = json['createby'];
    updateby = json['updateby'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    status = json['status'];
    caseType = json['caseType'];
    assignee = json['assignee'];
    priority = json['priority'];
    filename = json['filename'];
    if (json['updateDetails'] != null) {
      updateDetails = <CaseHistoryDetails>[];
      json['updateDetails'].forEach((v) {
        updateDetails!.add(new CaseHistoryDetails.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['ticketId'] = this.ticketId;
    data['resolutionId'] = this.resolutionId;
    data['remarkType'] = this.remarkType;
    data['remark'] = this.remark;
    data['reasonId'] = this.reasonId;
    data['commentBy'] = this.commentBy;
    data['isDeleted'] = this.isDeleted;
    data['nextFollowupDate'] = this.nextFollowupDate;
    data['nextFollowupTime'] = this.nextFollowupTime;
    data['createby'] = this.createby;
    data['updateby'] = this.updateby;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['status'] = this.status;
    data['caseType'] = this.caseType;
    data['assignee'] = this.assignee;
    data['priority'] = this.priority;
    data['filename'] = this.filename;
    if (this.updateDetails != null) {
      data['updateDetails'] =
          this.updateDetails!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class CaseHistoryDetails {
  int? id;
  String? operation;
  String? entitytype;
  String? oldvalue;
  String? newvalue;
  bool? isDeleted;
  String? filename;
  int? resolutionId;
  String? remarktype;
  int? mvnoId;

  CaseHistoryDetails(
      {this.id,
      this.operation,
      this.entitytype,
      this.oldvalue,
      this.newvalue,
      this.filename,
      this.resolutionId,
      this.remarktype,
      this.isDeleted,
      this.mvnoId});

  CaseHistoryDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    operation = json['operation'];
    entitytype = json['entitytype'];
    oldvalue = json['oldvalue'];
    newvalue = json['newvalue'];
    filename = json['filename'];
    resolutionId = json['resolutionId'];
    remarktype = json['remarktype'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['operation'] = this.operation;
    data['entitytype'] = this.entitytype;
    data['oldvalue'] = this.oldvalue;
    data['newvalue'] = this.newvalue;
    data['filename'] = this.filename;
    data['resolutionId'] = this.resolutionId;
    data['remarktype'] = this.remarktype;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class TicketAssignStaffMappings {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? ticketId;
  int? staffId;

  TicketAssignStaffMappings(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.ticketId,
      this.staffId});

  TicketAssignStaffMappings.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    ticketId = json['ticketId'];
    staffId = json['staffId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['id'] = this.id;
    data['ticketId'] = this.ticketId;
    data['staffId'] = this.staffId;
    return data;
  }
}

class TicketAttachments {
  String? filename;
  String? docStatus;
  String? createdByName;
  String? createdate;
  int? createdById;
  int? lastModifiedById;
  int? docId;
  int? ticketId;
  String? uniquename;
  bool? isDelete;

  TicketAttachments(
      {this.filename,
      this.docStatus,
      this.createdByName,
      this.createdate,
      this.createdById,
      this.lastModifiedById,
      this.docId,
      this.ticketId,
      this.uniquename,
      this.isDelete});

  TicketAttachments.fromJson(Map<String, dynamic> json) {
    filename = json['filename'];
    docStatus = json['docStatus'];
    createdByName = json['createdByName'];
    createdate = json['createdate'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    docId = json['docId'];
    ticketId = json['ticketId'];
    uniquename = json['uniquename'];
    isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['filename'] = this.filename;
    data['docStatus'] = this.docStatus;
    data['createdByName'] = this.createdByName;
    data['createdate'] = this.createdate;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['docId'] = this.docId;
    data['ticketId'] = this.ticketId;
    data['uniquename'] = this.uniquename;
    data['isDelete'] = this.isDelete;
    return data;
  }
}
