class GetAllServicesByServiceAreaRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<ServicesByServiceAreaDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllServicesByServiceAreaRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllServicesByServiceAreaRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ServicesByServiceAreaDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ServicesByServiceAreaDataList.fromJson(v));
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

class   ServicesByServiceAreaDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  dynamic icname;
  dynamic iccode;
  int? mvnoId;
  dynamic buId;
  bool? isQoSV;
  dynamic expiry;
  dynamic ledgerId;
  bool? isDtv;
  dynamic investmentid;
  List<ServiceParamMappingList>? serviceParamMappingList;
  bool? feasibility;
  bool? poc;
  bool? installation;
  bool? provisioning;
  bool? isPriceEditable;
  dynamic feasibilityTeamId;
  dynamic pocTeamId;
  dynamic installationTeamId;
  dynamic provisioningTeamId;

  ServicesByServiceAreaDataList(
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
        this.serviceParamMappingList,
        this.feasibility,
        this.poc,
        this.installation,
        this.provisioning,
        this.isPriceEditable,
        this.feasibilityTeamId,
        this.pocTeamId,
        this.installationTeamId,
        this.provisioningTeamId});

  ServicesByServiceAreaDataList.fromJson(Map<String, dynamic> json) {
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
    if (json['serviceParamMappingList'] != null) {
      serviceParamMappingList = <ServiceParamMappingList>[];
      json['serviceParamMappingList'].forEach((v) {
        serviceParamMappingList!.add(new ServiceParamMappingList.fromJson(v));
      });
    }
    feasibility = json['feasibility'];
    poc = json['poc'];
    installation = json['installation'];
    provisioning = json['provisioning'];
    isPriceEditable = json['isPriceEditable'];
    feasibilityTeamId = json['feasibilityTeamId'];
    pocTeamId = json['pocTeamId'];
    installationTeamId = json['installationTeamId'];
    provisioningTeamId = json['provisioningTeamId'];
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

    if (this.serviceParamMappingList != null) {
      data['serviceParamMappingList'] =
          this.serviceParamMappingList!.map((v) => v.toJson()).toList();
    }
    data['feasibility'] = this.feasibility;
    data['poc'] = this.poc;
    data['installation'] = this.installation;
    data['provisioning'] = this.provisioning;
    data['isPriceEditable'] = this.isPriceEditable;
    data['feasibilityTeamId'] = this.feasibilityTeamId;
    data['pocTeamId'] = this.pocTeamId;
    data['installationTeamId'] = this.installationTeamId;
    data['provisioningTeamId'] = this.provisioningTeamId;
    return data;
  }
}

class ServiceParamMappingList {
  int? id;
  int? serviceid;
  int? serviceParamId;
  String? value;
  dynamic isMandatory;
  dynamic serviceParamName;

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
