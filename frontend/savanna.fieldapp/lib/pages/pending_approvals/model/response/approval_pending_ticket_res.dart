import 'package:savbill/webservices/base_response.dart';

class ApprovalPendingTicketRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ApprovalPendingTicket>? dataList;

  ApprovalPendingTicketRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ApprovalPendingTicketRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ApprovalPendingTicket>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApprovalPendingTicket.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
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

class ApprovalPendingTicket {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? caseId;
  String? caseTitle;
  String? caseType;
  String? caseNumber;
  String? caseFor;
  String? caseOrigin;
  String? caseStatus;
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
  String? oltName;
  String? slotName;
  String? portName;
  String? serviceAreaName;
  int? serviceAreaId;
  String? mobile;
  String? userName;
  String? currentAssigneeName;
  String? finalResolvedByName;
  String? finalClosedByName;
  String? finalResolutionName;
  String? customerName;
  String? createDateString;
  String? updateDateString;
  int? partnerid;
  String? partnerName;
  int? mvnoId;
  int? ticketReasonCategoryId;
  int? reasonSubCategoryId;
  int? groupReasonId;
  int? tatMappingId;
  int? buId;
  String? caseReasonCategory;
  String? caseReasonSubCategory;
  String? caseReason;
  int? teamHierarchyMappingId;
  String? department;

  ApprovalPendingTicket(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.caseId,
      this.caseTitle,
      this.caseType,
      this.caseNumber,
      this.caseFor,
      this.caseOrigin,
      this.caseStatus,
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
      this.oltName,
      this.slotName,
      this.portName,
      this.serviceAreaName,
      this.serviceAreaId,
      this.mobile,
      this.userName,
      this.currentAssigneeName,
      this.finalResolvedByName,
      this.finalClosedByName,
      this.finalResolutionName,
      this.customerName,
      this.createDateString,
      this.updateDateString,
      this.partnerid,
      this.partnerName,
      this.mvnoId,
      this.ticketReasonCategoryId,
      this.reasonSubCategoryId,
      this.groupReasonId,
      this.tatMappingId,
      this.buId,
      this.caseReasonCategory,
      this.caseReasonSubCategory,
      this.caseReason,
      this.teamHierarchyMappingId,
      this.department});

  ApprovalPendingTicket.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    caseId = json['caseId'];
    caseTitle = json['caseTitle'];
    caseType = json['caseType'];
    caseNumber = json['caseNumber'];
    caseFor = json['caseFor'];
    caseOrigin = json['caseOrigin'];
    caseStatus = json['caseStatus'];
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
    oltName = json['oltName'];
    slotName = json['slotName'];
    portName = json['portName'];
    serviceAreaName = json['serviceAreaName'];
    serviceAreaId = json['serviceAreaId'];
    mobile = json['mobile'];
    userName = json['userName'];
    currentAssigneeName = json['currentAssigneeName'];
    finalResolvedByName = json['finalResolvedByName'];
    finalClosedByName = json['finalClosedByName'];
    finalResolutionName = json['finalResolutionName'];
    customerName = json['customerName'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    partnerid = json['partnerid'];
    partnerName = json['partnerName'];
    mvnoId = json['mvnoId'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    reasonSubCategoryId = json['reasonSubCategoryId'];
    groupReasonId = json['groupReasonId'];
    tatMappingId = json['tatMappingId'];
    buId = json['buId'];
    caseReasonCategory = json['caseReasonCategory'];
    caseReasonSubCategory = json['caseReasonSubCategory'];
    caseReason = json['caseReason'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    department = json['department'];
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
    data['caseTitle'] = this.caseTitle;
    data['caseType'] = this.caseType;
    data['caseNumber'] = this.caseNumber;
    data['caseFor'] = this.caseFor;
    data['caseOrigin'] = this.caseOrigin;
    data['caseStatus'] = this.caseStatus;
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
    data['oltName'] = this.oltName;
    data['slotName'] = this.slotName;
    data['portName'] = this.portName;
    data['serviceAreaName'] = this.serviceAreaName;
    data['serviceAreaId'] = this.serviceAreaId;
    data['mobile'] = this.mobile;
    data['userName'] = this.userName;
    data['currentAssigneeName'] = this.currentAssigneeName;
    data['finalResolvedByName'] = this.finalResolvedByName;
    data['finalClosedByName'] = this.finalClosedByName;
    data['finalResolutionName'] = this.finalResolutionName;
    data['customerName'] = this.customerName;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['partnerid'] = this.partnerid;
    data['partnerName'] = this.partnerName;
    data['mvnoId'] = this.mvnoId;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['reasonSubCategoryId'] = this.reasonSubCategoryId;
    data['groupReasonId'] = this.groupReasonId;
    data['tatMappingId'] = this.tatMappingId;
    data['buId'] = this.buId;
    data['caseReasonCategory'] = this.caseReasonCategory;
    data['caseReasonSubCategory'] = this.caseReasonSubCategory;
    data['caseReason'] = this.caseReason;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['department'] = this.department;
    return data;
  }
}
