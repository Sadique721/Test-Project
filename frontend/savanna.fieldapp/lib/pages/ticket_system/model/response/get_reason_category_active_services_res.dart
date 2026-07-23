class GetReasonCategoryByActiveServicesRes {
  dynamic responseCode;
  String? responseMessage;
  dynamic data;
  List<ReasonCategoryDataList>? dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  GetReasonCategoryByActiveServicesRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetReasonCategoryByActiveServicesRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ReasonCategoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ReasonCategoryDataList.fromJson(v));
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

class ReasonCategoryDataList {
  dynamic id;
  String? categoryName;
  ReasonCategoryService? service;
  dynamic mvnoId;
  dynamic isDeleted;
  List<TicketReasonCategoryTATMappingList>? ticketReasonCategoryTATMappingList;
  dynamic status;
  dynamic buId;
  dynamic slaTimeP3;
  dynamic slaUnitP3;
  dynamic slaTimeP2;
  dynamic slaUnitP2;
  dynamic slaTimeP1;
  dynamic slaUnitP1;
  String? department;
  dynamic lcoId;
  dynamic isDefaultProblemDomain;
  dynamic identityKey;

  ReasonCategoryDataList(
      {this.id,
        this.categoryName,
        this.service,
        this.mvnoId,
        this.isDeleted,
        this.ticketReasonCategoryTATMappingList,
        this.status,
        this.buId,
        this.slaTimeP3,
        this.slaUnitP3,
        this.slaTimeP2,
        this.slaUnitP2,
        this.slaTimeP1,
        this.slaUnitP1,
        this.department,
        this.lcoId,
        this.isDefaultProblemDomain,
        this.identityKey});

  ReasonCategoryDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    categoryName = json['categoryName'];
    service =
    json['service'] != null ? ReasonCategoryService.fromJson(json['service']) : null;
    mvnoId = json['mvnoId'];
    isDeleted = json['isDeleted'];
    if (json['ticketReasonCategoryTATMappingList'] != null) {
      ticketReasonCategoryTATMappingList =
      <TicketReasonCategoryTATMappingList>[];
      json['ticketReasonCategoryTATMappingList'].forEach((v) {
        ticketReasonCategoryTATMappingList!
            .add(new TicketReasonCategoryTATMappingList.fromJson(v));
      });
    }
    status = json['status'];
    buId = json['buId'];
    slaTimeP3 = json['slaTimeP3'];
    slaUnitP3 = json['slaUnitP3'];
    slaTimeP2 = json['slaTimeP2'];
    slaUnitP2 = json['slaUnitP2'];
    slaTimeP1 = json['slaTimeP1'];
    slaUnitP1 = json['slaUnitP1'];
    department = json['department'];
    lcoId = json['lcoId'];
    isDefaultProblemDomain = json['isDefaultProblemDomain'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['categoryName'] = this.categoryName;
    if (this.service != null) {
      data['service'] = this.service!.toJson();
    }
    data['mvnoId'] = this.mvnoId;
    data['isDeleted'] = this.isDeleted;
    if (this.ticketReasonCategoryTATMappingList != null) {
      data['ticketReasonCategoryTATMappingList'] = this
          .ticketReasonCategoryTATMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    data['status'] = this.status;
    data['buId'] = this.buId;
    data['slaTimeP3'] = this.slaTimeP3;
    data['slaUnitP3'] = this.slaUnitP3;
    data['slaTimeP2'] = this.slaTimeP2;
    data['slaUnitP2'] = this.slaUnitP2;
    data['slaTimeP1'] = this.slaTimeP1;
    data['slaUnitP1'] = this.slaUnitP1;
    data['department'] = this.department;
    data['lcoId'] = this.lcoId;
    data['isDefaultProblemDomain'] = this.isDefaultProblemDomain;
    data['identityKey'] = this.identityKey;
    return data;
  }
}

class ReasonCategoryService {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  dynamic icname;
  dynamic iccode;
  dynamic mvnoId;
  dynamic buId;
  dynamic isQoSV;
  dynamic expiry;
  dynamic ledgerId;
  dynamic isDtv;
  dynamic investmentid;
  dynamic feasibility;
  dynamic poc;
  dynamic installation;
  dynamic provisioning;
  dynamic isPriceEditable;
  dynamic feasibilityTeamId;
  dynamic pocTeamId;
  dynamic installationTeamId;
  dynamic provisioningTeamId;
  List<ServiceParamMappingList>? serviceParamMappingList;
  bool? deleteFlag;
  dynamic primaryKey;

  ReasonCategoryService(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.icname,
        this.iccode,
        this.mvnoId,
        this.buId,
        this.isQoSV,
        this.expiry,
        this.ledgerId,
        this.isDtv,
        this.investmentid,
        this.feasibility,
        this.poc,
        this.installation,
        this.provisioning,
        this.isPriceEditable,
        this.feasibilityTeamId,
        this.pocTeamId,
        this.installationTeamId,
        this.provisioningTeamId,
        this.serviceParamMappingList,
        this.deleteFlag,
        this.primaryKey});

  ReasonCategoryService.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    icname = json['icname'];
    iccode = json['iccode'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    isQoSV = json['isQoSV'];
    expiry = json['expiry'];
    ledgerId = json['ledgerId'];
    isDtv = json['is_dtv'];
    investmentid = json['investmentid'];
    feasibility = json['feasibility'];
    poc = json['poc'];
    installation = json['installation'];
    provisioning = json['provisioning'];
    isPriceEditable = json['isPriceEditable'];
    feasibilityTeamId = json['feasibilityTeamId'];
    pocTeamId = json['pocTeamId'];
    installationTeamId = json['installationTeamId'];
    provisioningTeamId = json['provisioningTeamId'];
    if (json['serviceParamMappingList'] != null) {
      serviceParamMappingList = <ServiceParamMappingList>[];
      json['serviceParamMappingList'].forEach((v) {
        serviceParamMappingList!.add(new ServiceParamMappingList.fromJson(v));
      });
    }
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['name'] = this.name;
    data['icname'] = this.icname;
    data['iccode'] = this.iccode;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['isQoSV'] = this.isQoSV;
    data['expiry'] = this.expiry;
    data['ledgerId'] = this.ledgerId;
    data['is_dtv'] = this.isDtv;
    data['investmentid'] = this.investmentid;
    data['feasibility'] = this.feasibility;
    data['poc'] = this.poc;
    data['installation'] = this.installation;
    data['provisioning'] = this.provisioning;
    data['isPriceEditable'] = this.isPriceEditable;
    data['feasibilityTeamId'] = this.feasibilityTeamId;
    data['pocTeamId'] = this.pocTeamId;
    data['installationTeamId'] = this.installationTeamId;
    data['provisioningTeamId'] = this.provisioningTeamId;
    if (this.serviceParamMappingList != null) {
      data['serviceParamMappingList'] =
          this.serviceParamMappingList!.map((v) => v.toJson()).toList();
    }
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}

class TicketReasonCategoryTATMappingList {
  dynamic mappingId;
  dynamic action;
  dynamic timeUnit;
  dynamic time;
  dynamic ticketReasonCategoryId;
  dynamic orderNumber;
  dynamic escalatedTime;
  dynamic mediumTime;
  dynamic teamName;
  dynamic level;

  TicketReasonCategoryTATMappingList(
      {this.mappingId,
        this.action,
        this.timeUnit,
        this.time,
        this.ticketReasonCategoryId,
        this.orderNumber,
        this.escalatedTime,
        this.mediumTime,
        this.teamName,
        this.level});

  TicketReasonCategoryTATMappingList.fromJson(Map<String, dynamic> json) {
    mappingId = json['mappingId'];
    action = json['action'];
    timeUnit = json['timeUnit'];
    time = json['time'];
    ticketReasonCategoryId = json['ticketReasonCategoryId'];
    orderNumber = json['orderNumber'];
    escalatedTime = json['escalatedTime'];
    mediumTime = json['mediumTime'];
    teamName = json['teamName'];
    level = json['level'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['mappingId'] = this.mappingId;
    data['action'] = this.action;
    data['timeUnit'] = this.timeUnit;
    data['time'] = this.time;
    data['ticketReasonCategoryId'] = this.ticketReasonCategoryId;
    data['orderNumber'] = this.orderNumber;
    data['escalatedTime'] = this.escalatedTime;
    data['mediumTime'] = this.mediumTime;
    data['teamName'] = this.teamName;
    data['level'] = this.level;
    return data;
  }
}

class ServiceParamMappingList {
  int? id;
  int? serviceid;
  int? serviceParamId;
  String? value;
  dynamic isMandatory;
  String? serviceParamName;

  ServiceParamMappingList(
      {this.id,
        this.serviceid,
        this.serviceParamId,
        this.value,
        this.isMandatory,
        this.serviceParamName});

  ServiceParamMappingList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    serviceid = json['serviceid'];
    serviceParamId = json['serviceParamId'];
    value = json['value'];
    isMandatory = json['isMandatory'];
    serviceParamName = json['serviceParamName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['serviceid'] = this.serviceid;
    data['serviceParamId'] = this.serviceParamId;
    data['value'] = this.value;
    data['isMandatory'] = this.isMandatory;
    data['serviceParamName'] = this.serviceParamName;
    return data;
  }
}

