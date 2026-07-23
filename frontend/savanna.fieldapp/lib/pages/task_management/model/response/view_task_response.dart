import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/webservices/base_response.dart';

class ViewTaskResponse extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<ViewTaskDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ViewTaskResponse(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ViewTaskResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ViewTaskDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ViewTaskDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ViewTaskDataList {
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
  dynamic customersId;
  String? caseForPartner;
  dynamic caseForZone;
  dynamic nextFollowupDate;
  dynamic nextFollowupTime;
  String? caseStartedOn;
  String? caseStartedOnString;
  String? firstAssignedOn;
  String? firstAssignedOnString;
  bool? isDelete;
  int? currentAssigneeId;
  dynamic finalResolutionId;
  dynamic finalResolvedById;
  dynamic finalClosedById;
  dynamic finalResolutionDate;
  dynamic finalClosedDate;
  List<CaseUpdateList>? caseUpdateList;
  String? firstRemark;
  dynamic liveUserServiceAreaDetails;
  dynamic oltName;
  dynamic slotName;
  dynamic portName;
  dynamic serviceAreaName;
  dynamic serviceAreaId;
  dynamic mobile;
  dynamic userName;
  String? currentAssigneeName;
  dynamic finalResolvedByName;
  dynamic finalClosedByName;
  dynamic finalResolutionName;
  dynamic finalClosedByDateString;
  dynamic finalResolutionDateString;
  String? createDateString;
  String? updateDateString;
  int? partnerid;
  dynamic partnerName;
  int? mvnoId;
  dynamic rating;
  int? caseCategoryId;
  int? caseSubCategoryId;
  dynamic groupReasonId;
  dynamic tatMappingId;
  dynamic buId;
  dynamic caseCategoryName;
  dynamic caseSubCategoryName;
  dynamic caseReason;
  int? rootCauseReasonId;
  dynamic subSource;
  dynamic source;
  dynamic teamHierarchyMappingId;
  List<TicketAssignStaffMappings>? ticketAssignStaffMappings;
  String? department;
  dynamic email;
  dynamic parentTicketId;
  dynamic helperName;
  dynamic lcoId;
  dynamic messageId;
  dynamic remark;
  dynamic file;
  int? caseOrder;
  dynamic caseDocDetails;
  dynamic createdFrom;
  dynamic caseSlaTime;
  dynamic caseSlaUnit;
  dynamic parentId;
  dynamic callStatus;
  dynamic isClosed;
  dynamic deacivateReason;
  String? serialNumber;
  dynamic mvnoName;
  int? teamId;
  dynamic finalTaskCompletionRemark;
  dynamic startDate;
  dynamic endDate;
  bool? isFromCalender;
  dynamic teamName;
  dynamic assigneeName;
  dynamic customerName;
  bool? selected = false;

  ViewTaskDataList(
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
        this.caseForZone,
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
        this.liveUserServiceAreaDetails,
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
        this.finalClosedByDateString,
        this.finalResolutionDateString,
        this.createDateString,
        this.updateDateString,
        this.partnerid,
        this.partnerName,
        this.mvnoId,
        this.rating,
        this.caseCategoryId,
        this.caseSubCategoryId,
        this.groupReasonId,
        this.tatMappingId,
        this.buId,
        this.caseCategoryName,
        this.caseSubCategoryName,
        this.caseReason,
        this.rootCauseReasonId,
        this.subSource,
        this.source,
        this.teamHierarchyMappingId,
        this.ticketAssignStaffMappings,
        this.department,
        this.email,
        this.parentTicketId,
        this.helperName,
        this.lcoId,
        this.messageId,
        this.remark,
        this.file,
        this.caseOrder,
        this.caseDocDetails,
        this.createdFrom,
        this.caseSlaTime,
        this.caseSlaUnit,
        this.parentId,
        this.callStatus,
        this.isClosed,
        this.deacivateReason,
        this.serialNumber,
        this.mvnoName,
        this.teamId,
        this.finalTaskCompletionRemark,
        this.startDate,
        this.endDate,
        this.isFromCalender,
        this.teamName,
        this.assigneeName,
        this.customerName,
        this.selected});

  ViewTaskDataList.fromJson(Map<String, dynamic> json) {
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
    caseForZone = json['caseForZone'];
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
    liveUserServiceAreaDetails = json['liveUserServiceAreaDetails'];
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
    finalClosedByDateString = json['finalClosedByDateString'];
    finalResolutionDateString = json['finalResolutionDateString'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    partnerid = json['partnerid'];
    partnerName = json['partnerName'];
    mvnoId = json['mvnoId'];
    rating = json['rating'];
    caseCategoryId = json['caseCategoryId'];
    caseSubCategoryId = json['caseSubCategoryId'];
    groupReasonId = json['groupReasonId'];
    tatMappingId = json['tatMappingId'];
    buId = json['buId'];
    caseCategoryName = json['caseCategoryName'];
    caseSubCategoryName = json['caseSubCategoryName'];
    caseReason = json['caseReason'];
    rootCauseReasonId = json['rootCauseReasonId'];
    subSource = json['subSource'];
    source = json['source'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    if (json['ticketAssignStaffMappings'] != null) {
      ticketAssignStaffMappings = <TicketAssignStaffMappings>[];
      json['ticketAssignStaffMappings'].forEach((v) {
        ticketAssignStaffMappings!
            .add(new TicketAssignStaffMappings.fromJson(v));
      });
    }
    department = json['department'];
    email = json['email'];
    parentTicketId = json['parentTicketId'];
    helperName = json['helperName'];
    lcoId = json['lcoId'];
    messageId = json['messageId'];
    remark = json['remark'];
    file = json['file'];
    caseOrder = json['case_order'];
    caseDocDetails = json['caseDocDetails'];
    createdFrom = json['createdFrom'];
    caseSlaTime = json['caseSlaTime'];
    caseSlaUnit = json['caseSlaUnit'];
    parentId = json['parentId'];
    callStatus = json['call_status'];
    isClosed = json['is_closed'];
    deacivateReason = json['deacivate_reason'];
    serialNumber = json['serialNumber'];
    mvnoName = json['mvnoName'];
    teamId = json['teamId'];
    finalTaskCompletionRemark = json['finalTaskCompletionRemark'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    isFromCalender = json['isFromCalender'];
    teamName = json['teamName'];
    assigneeName = json['assigneeName'];
    customerName = json['customerName'];
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
    data['caseForZone'] = this.caseForZone;
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
    data['liveUserServiceAreaDetails'] = this.liveUserServiceAreaDetails;
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
    data['finalClosedByDateString'] = this.finalClosedByDateString;
    data['finalResolutionDateString'] = this.finalResolutionDateString;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['partnerid'] = this.partnerid;
    data['partnerName'] = this.partnerName;
    data['mvnoId'] = this.mvnoId;
    data['rating'] = this.rating;
    data['caseCategoryId'] = this.caseCategoryId;
    data['caseSubCategoryId'] = this.caseSubCategoryId;
    data['groupReasonId'] = this.groupReasonId;
    data['tatMappingId'] = this.tatMappingId;
    data['buId'] = this.buId;
    data['caseCategoryName'] = this.caseCategoryName;
    data['caseSubCategoryName'] = this.caseSubCategoryName;
    data['caseReason'] = this.caseReason;
    data['rootCauseReasonId'] = this.rootCauseReasonId;
    data['subSource'] = this.subSource;
    data['source'] = this.source;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    if (this.ticketAssignStaffMappings != null) {
      data['ticketAssignStaffMappings'] =
          this.ticketAssignStaffMappings!.map((v) => v.toJson()).toList();
    }
    data['department'] = this.department;
    data['email'] = this.email;
    data['parentTicketId'] = this.parentTicketId;
    data['helperName'] = this.helperName;
    data['lcoId'] = this.lcoId;
    data['messageId'] = this.messageId;
    data['remark'] = this.remark;
    data['file'] = this.file;
    data['case_order'] = this.caseOrder;
    data['caseDocDetails'] = this.caseDocDetails;
    data['createdFrom'] = this.createdFrom;
    data['caseSlaTime'] = this.caseSlaTime;
    data['caseSlaUnit'] = this.caseSlaUnit;
    data['parentId'] = this.parentId;
    data['call_status'] = this.callStatus;
    data['is_closed'] = this.isClosed;
    data['deacivate_reason'] = this.deacivateReason;
    data['serialNumber'] = this.serialNumber;
    data['mvnoName'] = this.mvnoName;
    data['teamId'] = this.teamId;
    data['finalTaskCompletionRemark'] = this.finalTaskCompletionRemark;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['isFromCalender'] = this.isFromCalender;
    data['teamName'] = this.teamName;
    data['assigneeName'] = this.assigneeName;
    data['customerName'] = this.customerName;
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
