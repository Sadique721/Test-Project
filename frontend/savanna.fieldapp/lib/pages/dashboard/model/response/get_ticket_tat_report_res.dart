import 'package:savbill/webservices/base_response.dart';

class GetTicketTATReportRes  extends BaseResponse{
  dynamic data;
  List<GetTicketTATReportDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetTicketTATReportRes(
      {
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetTicketTATReportRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <GetTicketTATReportDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(GetTicketTATReportDataList.fromJson(v));
      });
    }else{
      dataList = [];
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
    if (this.dataList != null && this.dataList!.isNotEmpty) {
      // data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
      data['dataList'] = dataList?.map((v) => v.toJson()).toList() ?? [];
    }else{
      data['dataList']  = [];
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class GetTicketTATReportDataList {
  int? id;
  int? caseId;
  String? caseStatus;
  String? tatAction;
  int? tatTime;
  String? tatUnit;
  int? slaTime;
  String? slaUnit;
  String? tatStartTime;
  String? tatMessage;
  int? assignStaffId;
  int? assignStaffParentId;
  String? caseLevel;
  String? notificationFor;
  String? isTatBreached;
  String? isSlaBreached;
  String? messageStatus;
  String? messageMode;
  String? staffName;
  String? parentStaffName;

  GetTicketTATReportDataList(
      {this.id,
        this.caseId,
        this.caseStatus,
        this.tatAction,
        this.tatTime,
        this.tatUnit,
        this.slaTime,
        this.slaUnit,
        this.tatStartTime,
        this.tatMessage,
        this.assignStaffId,
        this.assignStaffParentId,
        this.caseLevel,
        this.notificationFor,
        this.isTatBreached,
        this.isSlaBreached,
        this.messageStatus,
        this.messageMode,
        this.staffName,
        this.parentStaffName});

  GetTicketTATReportDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    caseId = json['caseId'];
    caseStatus = json['caseStatus'];
    tatAction = json['tatAction'];
    tatTime = json['tatTime'];
    tatUnit = json['tatUnit'];
    slaTime = json['slaTime'];
    slaUnit = json['slaUnit'];
    tatStartTime = json['tatStartTime'];
    tatMessage = json['tatMessage'];
    assignStaffId = json['assignStaffId'];
    assignStaffParentId = json['assignStaffParentId'];
    caseLevel = json['caseLevel'];
    notificationFor = json['notificationFor'];
    isTatBreached = json['isTatBreached'];
    isSlaBreached = json['isSlaBreached'];
    messageStatus = json['messageStatus'];
    messageMode = json['messageMode'];
    staffName = json['staffName'];
    parentStaffName = json['parentStaffName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['caseId'] = this.caseId;
    data['caseStatus'] = this.caseStatus;
    data['tatAction'] = this.tatAction;
    data['tatTime'] = this.tatTime;
    data['tatUnit'] = this.tatUnit;
    data['slaTime'] = this.slaTime;
    data['slaUnit'] = this.slaUnit;
    data['tatStartTime'] = this.tatStartTime;
    data['tatMessage'] = this.tatMessage;
    data['assignStaffId'] = this.assignStaffId;
    data['assignStaffParentId'] = this.assignStaffParentId;
    data['caseLevel'] = this.caseLevel;
    data['notificationFor'] = this.notificationFor;
    data['isTatBreached'] = this.isTatBreached;
    data['isSlaBreached'] = this.isSlaBreached;
    data['messageStatus'] = this.messageStatus;
    data['messageMode'] = this.messageMode;
    data['staffName'] = this.staffName;
    data['parentStaffName'] = this.parentStaffName;
    return data;
  }
}
