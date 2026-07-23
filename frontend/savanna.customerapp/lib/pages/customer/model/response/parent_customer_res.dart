import 'package:savbill/webservices/base_response.dart';
class ParentCustomerRes extends BaseResponse {
  PageDetails? pageDetails;
  List<ParentCustomerDetail>? parentCustomerList;
  String? eRROR;


  ParentCustomerRes(
      {this.pageDetails, this.parentCustomerList, timestamp, status, eRROR});

  ParentCustomerRes.fromJson(Map<String, dynamic> json) {
    pageDetails = json['pageDetails'] != null
        ? new PageDetails.fromJson(json['pageDetails'])
        : null;
    if (json['parentCustomerList'] != null) {
      parentCustomerList = <ParentCustomerDetail>[];
      json['parentCustomerList'].forEach((v) {
        parentCustomerList!.add(new ParentCustomerDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
    eRROR = json['ERROR'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.pageDetails != null) {
      data['pageDetails'] = this.pageDetails!.toJson();
    }
    if (this.parentCustomerList != null) {
      data['parentCustomerList'] =
          this.parentCustomerList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    data['ERROR'] = this.eRROR;
    return data;
  }
}

/*class ParentCustomerDetail {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? acctno;
  double? outstanding;
  Null? previousCafApprover;
  Null? nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  Null? leadNo;
  Null? leadId;
  Null? nextTeamHierarchyMapping;
  String? customerAddress;
  Null? currentAssigneeParentId;
  Null? connectionMode;

  ParentCustomerDetail(
      {
        this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        this.networkDetails,
        this.acctno,
        this.outstanding,
        this.previousCafApprover,
        this.nextCafApprover,
        this.status,
        this.custtype,
        this.calendarType,
        this.isinvoicestop,
        this.istrialplan,
        this.leadNo,
        this.leadId,
        this.nextTeamHierarchyMapping,
        this.customerAddress,
        this.currentAssigneeParentId,
        this.connectionMode});

  ParentCustomerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    acctno = json['acctno'];
    outstanding = json['outstanding'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    status = json['status'];
    custtype = json['custtype'];
    calendarType = json['calendarType'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    customerAddress = json['customerAddress'];
    currentAssigneeParentId = json['currentAssigneeParentId'];
    connectionMode = json['connectionMode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['mobile'] = this.mobile;
    data['email'] = this.email;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['acctno'] = this.acctno;
    data['outstanding'] = this.outstanding;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['status'] = this.status;
    data['custtype'] = this.custtype;
    data['calendarType'] = this.calendarType;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    data['customerAddress'] = this.customerAddress;
    data['currentAssigneeParentId'] = this.currentAssigneeParentId;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}*/

/*class ParentCustomerDetail {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? acctno;
  dynamic outstanding;
  Null? previousCafApprover;
  dynamic nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  Null? leadNo;
  Null? leadId;
  Null? nextTeamHierarchyMapping;
  // ServiceArea? serviceArea;
  // List<CustAddressList>? custAddressList;
  String? customerAddress;
  Null? currentAssigneeParentId;
  Null? connectionMode;

  ParentCustomerDetail(
      {this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        this.networkDetails,
        this.acctno,
        this.outstanding,
        this.previousCafApprover,
        this.nextCafApprover,
        this.status,
        this.custtype,
        this.calendarType,
        this.isinvoicestop,
        this.istrialplan,
        this.leadNo,
        this.leadId,
        this.nextTeamHierarchyMapping,
        // this.serviceArea,
        // this.custAddressList,
        this.customerAddress,
        this.currentAssigneeParentId,
        this.connectionMode});

  ParentCustomerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    acctno = json['acctno'];
    outstanding = json['outstanding'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    status = json['status'];
    custtype = json['custtype'];
    calendarType = json['calendarType'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];

    customerAddress = json['customerAddress'];
    currentAssigneeParentId = json['currentAssigneeParentId'];
    connectionMode = json['connectionMode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['mobile'] = this.mobile;
    data['email'] = this.email;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['acctno'] = this.acctno;
    data['outstanding'] = this.outstanding;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['status'] = this.status;
    data['custtype'] = this.custtype;
    data['calendarType'] = this.calendarType;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    // if (this.serviceArea != null) {
    //   data['serviceArea'] = this.serviceArea!.toJson();
    // }
    // if (this.custAddressList != null) {
    //   data['custAddressList'] =
    //       this.custAddressList!.map((v) => v.toJson()).toList();
    // }
    data['customerAddress'] = this.customerAddress;
    data['currentAssigneeParentId'] = this.currentAssigneeParentId;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}*/

class PageDetails {
  int? totalPages;
  int? totalRecords;
  int? totalRecordsPerPage;
  int? currentPageNumber;

  PageDetails(
      {this.totalPages,
        this.totalRecords,
        this.totalRecordsPerPage,
        this.currentPageNumber});

  PageDetails.fromJson(Map<String, dynamic> json) {
    totalPages = json['totalPages'];
    totalRecords = json['totalRecords'];
    totalRecordsPerPage = json['totalRecordsPerPage'];
    currentPageNumber = json['currentPageNumber'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['totalPages'] = this.totalPages;
    data['totalRecords'] = this.totalRecords;
    data['totalRecordsPerPage'] = this.totalRecordsPerPage;
    data['currentPageNumber'] = this.currentPageNumber;
    return data;
  }
}

class ParentCustomerDetail {
  int? id;
  String? name;
  String? username;
  String? mobile;
  String? email;
  bool? connectivity;
  NetworkDetails? networkDetails;
  String? acctno;
  dynamic outstanding;
  dynamic previousCafApprover;
  dynamic nextCafApprover;
  String? status;
  String? custtype;
  String? calendarType;
  bool? isinvoicestop;
  bool? istrialplan;
  dynamic leadNo;
  dynamic leadId;
  dynamic nextTeamHierarchyMapping;
  // ServiceArea? serviceArea;
  // List<CustAddressList>? custAddressList;
  String? customerAddress;
  dynamic currentAssigneeParentId;
  dynamic connectionMode;

  ParentCustomerDetail(
      {this.id,
        this.name,
        this.username,
        this.mobile,
        this.email,
        this.connectivity,
        this.networkDetails,
        this.acctno,
        this.outstanding,
        this.previousCafApprover,
        this.nextCafApprover,
        this.status,
        this.custtype,
        this.calendarType,
        this.isinvoicestop,
        this.istrialplan,
        this.leadNo,
        this.leadId,
        this.nextTeamHierarchyMapping,
        // this.serviceArea,
        // this.custAddressList,
        this.customerAddress,
        this.currentAssigneeParentId,
        this.connectionMode});

  ParentCustomerDetail.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    username = json['username'];
    mobile = json['mobile'];
    email = json['email'];
    connectivity = json['connectivity'];
    networkDetails = json['networkDetails'] != null
        ? new NetworkDetails.fromJson(json['networkDetails'])
        : null;
    acctno = json['acctno'];
    outstanding = json['outstanding'];
    previousCafApprover = json['previousCafApprover'];
    nextCafApprover = json['nextCafApprover'];
    status = json['status'];
    custtype = json['custtype'];
    calendarType = json['calendarType'];
    isinvoicestop = json['isinvoicestop'];
    istrialplan = json['istrialplan'];
    leadNo = json['leadNo'];
    leadId = json['leadId'];
    nextTeamHierarchyMapping = json['nextTeamHierarchyMapping'];
    // serviceArea = json['serviceArea'] != null
    //     ? new ServiceArea.fromJson(json['serviceArea'])
    //     : null;
    // if (json['custAddressList'] != null) {
    //   custAddressList = <CustAddressList>[];
    //   json['custAddressList'].forEach((v) {
    //     custAddressList!.add(new CustAddressList.fromJson(v));
    //   });
    // }
    customerAddress = json['customerAddress'];
    currentAssigneeParentId = json['currentAssigneeParentId'];
    connectionMode = json['connectionMode'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['username'] = this.username;
    data['mobile'] = this.mobile;
    data['email'] = this.email;
    data['connectivity'] = this.connectivity;
    if (this.networkDetails != null) {
      data['networkDetails'] = this.networkDetails!.toJson();
    }
    data['acctno'] = this.acctno;
    data['outstanding'] = this.outstanding;
    data['previousCafApprover'] = this.previousCafApprover;
    data['nextCafApprover'] = this.nextCafApprover;
    data['status'] = this.status;
    data['custtype'] = this.custtype;
    data['calendarType'] = this.calendarType;
    data['isinvoicestop'] = this.isinvoicestop;
    data['istrialplan'] = this.istrialplan;
    data['leadNo'] = this.leadNo;
    data['leadId'] = this.leadId;
    data['nextTeamHierarchyMapping'] = this.nextTeamHierarchyMapping;
    // if (this.serviceArea != null) {
    //   data['serviceArea'] = this.serviceArea!.toJson();
    // }
    // if (this.custAddressList != null) {
    //   data['custAddressList'] =
    //       this.custAddressList!.map((v) => v.toJson()).toList();
    // }
    data['customerAddress'] = this.customerAddress;
    data['currentAssigneeParentId'] = this.currentAssigneeParentId;
    data['connectionMode'] = this.connectionMode;
    return data;
  }
}

class NetworkDetails {
  dynamic networkdeviceid;
  int? serviceareaid;
  dynamic slotid;
  dynamic portid;
  String? networkdevicename;
  String? serviceareaname;
  String? slotname;
  String? portname;

  NetworkDetails(
      {this.networkdeviceid,
        this.serviceareaid,
        this.slotid,
        this.portid,
        this.networkdevicename,
        this.serviceareaname,
        this.slotname,
        this.portname});

  NetworkDetails.fromJson(Map<String, dynamic> json) {
    networkdeviceid = json['networkdeviceid'];
    serviceareaid = json['serviceareaid'];
    slotid = json['slotid'];
    portid = json['portid'];
    networkdevicename = json['networkdevicename'];
    serviceareaname = json['serviceareaname'];
    slotname = json['slotname'];
    portname = json['portname'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['networkdeviceid'] = this.networkdeviceid;
    data['serviceareaid'] = this.serviceareaid;
    data['slotid'] = this.slotid;
    data['portid'] = this.portid;
    data['networkdevicename'] = this.networkdevicename;
    data['serviceareaname'] = this.serviceareaname;
    data['slotname'] = this.slotname;
    data['portname'] = this.portname;
    return data;
  }
}

/*class ServiceArea {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;
  String? latitude;
  String? longitude;
  Null? areaId;
  List<PincodeList>? pincodeList;
  int? cityid;

  ServiceArea(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.status,
        this.isDeleted,
        this.mvnoId,
        this.latitude,
        this.longitude,
        this.areaId,
        this.pincodeList,
        this.cityid});

  ServiceArea.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    latitude = json['latitude'];
    longitude = json['longitude'];
    areaId = json['areaId'];
    if (json['pincodeList'] != null) {
      pincodeList = <PincodeList>[];
      json['pincodeList'].forEach((v) {
        pincodeList!.add(new PincodeList.fromJson(v));
      });
    }
    cityid = json['cityid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['latitude'] = this.latitude;
    data['longitude'] = this.longitude;
    data['areaId'] = this.areaId;
    if (this.pincodeList != null) {
      data['pincodeList'] = this.pincodeList!.map((v) => v.toJson()).toList();
    }
    data['cityid'] = this.cityid;
    return data;
  }
}

class PincodeList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? pincode;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  List<AreaList>? areaList;
  int? mvnoId;

  PincodeList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.pincode,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.areaList,
        this.mvnoId});

  PincodeList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    pincode = json['pincode'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    if (json['areaList'] != null) {
      areaList = <AreaList>[];
      json['areaList'].forEach((v) {
        areaList!.add(new AreaList.fromJson(v));
      });
    }
    mvnoId = json['mvnoId'];
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
    data['pincode'] = this.pincode;
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    if (this.areaList != null) {
      data['areaList'] = this.areaList!.map((v) => v.toJson()).toList();
    }
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class AreaList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? countryId;
  int? cityId;
  int? stateId;
  int? mvnoId;
  int? primaryKey;
  bool? deleteFlag;

  AreaList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.countryId,
        this.cityId,
        this.stateId,
        this.mvnoId,
        this.primaryKey,
        this.deleteFlag});

  AreaList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    isDeleted = json['isDeleted'];
    countryId = json['countryId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    mvnoId = json['mvnoId'];
    primaryKey = json['primaryKey'];
    deleteFlag = json['deleteFlag'];
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
    data['status'] = this.status;
    data['isDeleted'] = this.isDeleted;
    data['countryId'] = this.countryId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['mvnoId'] = this.mvnoId;
    data['primaryKey'] = this.primaryKey;
    data['deleteFlag'] = this.deleteFlag;
    return data;
  }
}

class CustAddressList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? addressType;
  Null? address1;
  Null? address2;
  String? landmark;
  Null? landmark1;
  AreaList? area;
  int? areaId;
  PincodeList? pincode;
  int? pincodeId;
  City? city;
  int? cityId;
  State? state;
  int? stateId;
  Country? country;
  int? countryId;
  String? fullAddress;
  Null? nextTeamHierarchyMappingId;
  Null? nextStaff;
  Null? status;
  String? version;
  Null? shiftId;
  Null? shiftedPartnerId;
  Null? shitedServiceAreaId;
  Null? requestedByName;
  Null? requestedDate;
  bool? isDelete;

  CustAddressList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.addressType,
        this.address1,
        this.address2,
        this.landmark,
        this.landmark1,
        this.area,
        this.areaId,
        this.pincode,
        this.pincodeId,
        this.city,
        this.cityId,
        this.state,
        this.stateId,
        this.country,
        this.countryId,
        this.fullAddress,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.status,
        this.version,
        this.shiftId,
        this.shiftedPartnerId,
        this.shitedServiceAreaId,
        this.requestedByName,
        this.requestedDate,
        this.isDelete});

  CustAddressList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    addressType = json['addressType'];
    address1 = json['address1'];
    address2 = json['address2'];
    landmark = json['landmark'];
    landmark1 = json['landmark1'];
    area = json['area'] != null ? new AreaList.fromJson(json['area']) : null;
    areaId = json['areaId'];
    pincode = json['pincode'] != null
        ? new PincodeList.fromJson(json['pincode'])
        : null;
    pincodeId = json['pincodeId'];
    city = json['city'] != null ? new City.fromJson(json['city']) : null;
    cityId = json['cityId'];
    state = json['state'] != null ? new State.fromJson(json['state']) : null;
    stateId = json['stateId'];
    country =
    json['country'] != null ? new Country.fromJson(json['country']) : null;
    countryId = json['countryId'];
    fullAddress = json['fullAddress'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    status = json['status'];
    version = json['version'];
    shiftId = json['shiftId'];
    shiftedPartnerId = json['shiftedPartnerId'];
    shitedServiceAreaId = json['shitedServiceAreaId'];
    requestedByName = json['requestedByName'];
    requestedDate = json['requestedDate'];
    isDelete = json['isDelete'];
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
    data['addressType'] = this.addressType;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['landmark'] = this.landmark;
    data['landmark1'] = this.landmark1;
    if (this.area != null) {
      data['area'] = this.area!.toJson();
    }
    data['areaId'] = this.areaId;
    if (this.pincode != null) {
      data['pincode'] = this.pincode!.toJson();
    }
    data['pincodeId'] = this.pincodeId;
    if (this.city != null) {
      data['city'] = this.city!.toJson();
    }
    data['cityId'] = this.cityId;
    if (this.state != null) {
      data['state'] = this.state!.toJson();
    }
    data['stateId'] = this.stateId;
    if (this.country != null) {
      data['country'] = this.country!.toJson();
    }
    data['countryId'] = this.countryId;
    data['fullAddress'] = this.fullAddress;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['status'] = this.status;
    data['version'] = this.version;
    data['shiftId'] = this.shiftId;
    data['shiftedPartnerId'] = this.shiftedPartnerId;
    data['shitedServiceAreaId'] = this.shitedServiceAreaId;
    data['requestedByName'] = this.requestedByName;
    data['requestedDate'] = this.requestedDate;
    data['isDelete'] = this.isDelete;
    return data;
  }
}

class City {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  int? countryId;
  bool? isDelete;
  int? mvnoId;

  City(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.countryId,
        this.isDelete,
        this.mvnoId});

  City.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    countryId = json['countryId'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
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
    data['status'] = this.status;
    data['countryId'] = this.countryId;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class State {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  bool? isDeleted;
  int? mvnoId;

  State(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.name,
        this.status,
        this.isDeleted,
        this.mvnoId});

  State.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
        isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
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
    data['status'] = this.status;
        data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}

class Country {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? mvnoId;
  int? id;
  String? name;
  String? status;
  bool? isDelete;

  Country(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.mvnoId,
        this.id,
        this.name,
        this.status,
        this.isDelete});

  Country.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    mvnoId = json['mvnoId'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
        isDelete = json['isDelete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['mvnoId'] = this.mvnoId;
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
        data['isDelete'] = this.isDelete;
    return data;
  }
}*/





