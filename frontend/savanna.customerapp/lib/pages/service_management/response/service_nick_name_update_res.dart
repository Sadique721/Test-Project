import 'package:savbill/webservices/base_response.dart';

class ServiceNickNameUpdateRes extends BaseResponse {
  ServiceNickName? nickName;
  String? timestamp;
  int? status;

  ServiceNickNameUpdateRes({this.nickName, this.timestamp, this.status});

  ServiceNickNameUpdateRes.fromJson(Map<String, dynamic> json) {
    nickName = json['NickName'] != null
        ? new ServiceNickName.fromJson(json['NickName'])
        : null;
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.nickName != null) {
      data['NickName'] = this.nickName!.toJson();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class ServiceNickName {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  int? custId;
  int? serviceId;
  Null? leaseCircuitId;
  String? connectionNo;
  String? nickName;
  Null? stopServiceDate;
  bool? isDeleted;
  Null? purchaseorder;
  Null? invoiceformat;
  String? invoiceType;
  String? leaseCircuitName;
  Null? circuitStatus;
  Null? cafNo;
  Null? uploadCAF;
  Null? customerName;
  Null? accountNumber;
  Null? typeOfLink;
  Null? linkInstallationDate;
  Null? linkAcceptanceDate;
  Null? purchaseOrderDate;
  Null? partner;
  Null? expiryDate;
  Null? distance;
  Null? distanceUnit;
  Null? bandwidth;
  Null? uploadQOS;
  Null? downloadQOS;
  Null? linkRouterLocation;
  Null? linkPortType;
  Null? linkRouterIp;
  Null? linkPortOnRouter;
  Null? bandwidthType;
  Null? linkRouterName;
  Null? circuitBillingId;
  Null? pop;
  Null? associatedLevel;
  Null? locationLevel1;
  Null? locationLevel2;
  Null? locationLevel3;
  Null? locationLevel4;
  Null? baseStationId1;
  Null? baseStationId2;
  Null? terminationAddress;
  Null? note;
  Null? contactPerson;
  Null? contactPerson1;
  Null? mobileNumber;
  Null? mobileNumber1;
  Null? landlineNumber;
  Null? landlineNumber1;
  Null? emailId;
  Null? emailId1;
  Null? otcChargesFile;
  Null? serviceChargerFile;
  Null? staticOrPooledIP;
  Null? chargeTypeFile;
  Null? billingCycle;
  Null? billingType;
  Null? billable;
  Null? billingGroup;
  Null? payable;
  Null? enableProcessing;
  Null? deposite;
  Null? poNumber;
  Null? billRemark;
  Null? fullName;
  Null? organisation;
  Null? address1;
  Null? address2;
  Null? city;
  Null? zipcode;
  Null? state;
  Null? country;
  String? status;
  bool? isDelete;
  Null? mvnoId;
  Null? buId;
  String? discountType;
  double? discount;
  Null? discountExpiryDate;
  Null? serviceAreaType;
  String? newDiscountType;
  double? newDiscount;
  Null? newDiscountExpiryDate;
  Null? remarks;
  Null? nextTeamHierarchyMappingId;
  Null? nextStaff;
  Null? branch;
  Null? connectionType;
  Null? serviceName;
  Null? serviceHoldDate;
  Null? serviceHoldBy;
  Null? serviceHoldRemarks;
  Null? serviceResumeBy;
  Null? serviceResumeRemarks;
  Null? serviceResumeDate;
  Null? stopServiceRemark;
  Null? discountFlowInProcess;
  Null? oldDiscount;
  Null? vlanid;

  ServiceNickName(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.custId,
        this.serviceId,
        this.leaseCircuitId,
        this.connectionNo,
        this.nickName,
        this.stopServiceDate,
        this.isDeleted,
        this.purchaseorder,
        this.invoiceformat,
        this.invoiceType,
        this.leaseCircuitName,
        this.circuitStatus,
        this.cafNo,
        this.uploadCAF,
        this.customerName,
        this.accountNumber,
        this.typeOfLink,
        this.linkInstallationDate,
        this.linkAcceptanceDate,
        this.purchaseOrderDate,
        this.partner,
        this.expiryDate,
        this.distance,
        this.distanceUnit,
        this.bandwidth,
        this.uploadQOS,
        this.downloadQOS,
        this.linkRouterLocation,
        this.linkPortType,
        this.linkRouterIp,
        this.linkPortOnRouter,
        this.bandwidthType,
        this.linkRouterName,
        this.circuitBillingId,
        this.pop,
        this.associatedLevel,
        this.locationLevel1,
        this.locationLevel2,
        this.locationLevel3,
        this.locationLevel4,
        this.baseStationId1,
        this.baseStationId2,
        this.terminationAddress,
        this.note,
        this.contactPerson,
        this.contactPerson1,
        this.mobileNumber,
        this.mobileNumber1,
        this.landlineNumber,
        this.landlineNumber1,
        this.emailId,
        this.emailId1,
        this.otcChargesFile,
        this.serviceChargerFile,
        this.staticOrPooledIP,
        this.chargeTypeFile,
        this.billingCycle,
        this.billingType,
        this.billable,
        this.billingGroup,
        this.payable,
        this.enableProcessing,
        this.deposite,
        this.poNumber,
        this.billRemark,
        this.fullName,
        this.organisation,
        this.address1,
        this.address2,
        this.city,
        this.zipcode,
        this.state,
        this.country,
        this.status,
        this.isDelete,
        this.mvnoId,
        this.buId,
        this.discountType,
        this.discount,
        this.discountExpiryDate,
        this.serviceAreaType,
        this.newDiscountType,
        this.newDiscount,
        this.newDiscountExpiryDate,
        this.remarks,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.branch,
        this.connectionType,
        this.serviceName,
        this.serviceHoldDate,
        this.serviceHoldBy,
        this.serviceHoldRemarks,
        this.serviceResumeBy,
        this.serviceResumeRemarks,
        this.serviceResumeDate,
        this.stopServiceRemark,
        this.discountFlowInProcess,
        this.oldDiscount,
        this.vlanid});

  ServiceNickName.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    custId = json['custId'];
    serviceId = json['serviceId'];
    leaseCircuitId = json['leaseCircuitId'];
    connectionNo = json['connectionNo'];
    nickName = json['nickName'];
    stopServiceDate = json['stopServiceDate'];
    isDeleted = json['isDeleted'];
    purchaseorder = json['purchaseorder'];
    invoiceformat = json['invoiceformat'];
    invoiceType = json['invoiceType'];
    leaseCircuitName = json['leaseCircuitName'];
    circuitStatus = json['circuitStatus'];
    cafNo = json['cafNo'];
    uploadCAF = json['uploadCAF'];
    customerName = json['customerName'];
    accountNumber = json['accountNumber'];
    typeOfLink = json['typeOfLink'];
    linkInstallationDate = json['linkInstallationDate'];
    linkAcceptanceDate = json['linkAcceptanceDate'];
    purchaseOrderDate = json['purchaseOrderDate'];
    partner = json['partner'];
    expiryDate = json['expiryDate'];
    distance = json['distance'];
    distanceUnit = json['distanceUnit'];
    bandwidth = json['bandwidth'];
    uploadQOS = json['uploadQOS'];
    downloadQOS = json['downloadQOS'];
    linkRouterLocation = json['linkRouterLocation'];
    linkPortType = json['linkPortType'];
    linkRouterIp = json['linkRouterIp'];
    linkPortOnRouter = json['linkPortOnRouter'];
    bandwidthType = json['bandwidthType'];
    linkRouterName = json['linkRouterName'];
    circuitBillingId = json['circuitBillingId'];
    pop = json['pop'];
    associatedLevel = json['associatedLevel'];
    locationLevel1 = json['locationLevel1'];
    locationLevel2 = json['locationLevel2'];
    locationLevel3 = json['locationLevel3'];
    locationLevel4 = json['locationLevel4'];
    baseStationId1 = json['baseStationId1'];
    baseStationId2 = json['baseStationId2'];
    terminationAddress = json['terminationAddress'];
    note = json['note'];
    contactPerson = json['contactPerson'];
    contactPerson1 = json['contactPerson1'];
    mobileNumber = json['mobileNumber'];
    mobileNumber1 = json['mobileNumber1'];
    landlineNumber = json['landlineNumber'];
    landlineNumber1 = json['landlineNumber1'];
    emailId = json['emailId'];
    emailId1 = json['emailId1'];
    otcChargesFile = json['otcChargesFile'];
    serviceChargerFile = json['serviceChargerFile'];
    staticOrPooledIP = json['staticOrPooledIP'];
    chargeTypeFile = json['chargeTypeFile'];
    billingCycle = json['billingCycle'];
    billingType = json['billingType'];
    billable = json['billable'];
    billingGroup = json['billingGroup'];
    payable = json['payable'];
    enableProcessing = json['enableProcessing'];
    deposite = json['deposite'];
    poNumber = json['poNumber'];
    billRemark = json['billRemark'];
    fullName = json['fullName'];
    organisation = json['organisation'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    zipcode = json['zipcode'];
    state = json['state'];
    country = json['country'];
    status = json['status'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    discountType = json['discountType'];
    discount = json['discount'];
    discountExpiryDate = json['discountExpiryDate'];
    serviceAreaType = json['serviceAreaType'];
    newDiscountType = json['newDiscountType'];
    newDiscount = json['newDiscount'];
    newDiscountExpiryDate = json['newDiscountExpiryDate'];
    remarks = json['remarks'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    branch = json['branch'];
    connectionType = json['connectionType'];
    serviceName = json['serviceName'];
    serviceHoldDate = json['serviceHoldDate'];
    serviceHoldBy = json['serviceHoldBy'];
    serviceHoldRemarks = json['serviceHoldRemarks'];
    serviceResumeBy = json['serviceResumeBy'];
    serviceResumeRemarks = json['serviceResumeRemarks'];
    serviceResumeDate = json['serviceResumeDate'];
    stopServiceRemark = json['stopServiceRemark'];
    discountFlowInProcess = json['discountFlowInProcess'];
    oldDiscount = json['old_discount'];
    vlanid = json['vlanid'];
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
    data['custId'] = this.custId;
    data['serviceId'] = this.serviceId;
    data['leaseCircuitId'] = this.leaseCircuitId;
    data['connectionNo'] = this.connectionNo;
    data['nickName'] = this.nickName;
    data['stopServiceDate'] = this.stopServiceDate;
    data['isDeleted'] = this.isDeleted;
    data['purchaseorder'] = this.purchaseorder;
    data['invoiceformat'] = this.invoiceformat;
    data['invoiceType'] = this.invoiceType;
    data['leaseCircuitName'] = this.leaseCircuitName;
    data['circuitStatus'] = this.circuitStatus;
    data['cafNo'] = this.cafNo;
    data['uploadCAF'] = this.uploadCAF;
    data['customerName'] = this.customerName;
    data['accountNumber'] = this.accountNumber;
    data['typeOfLink'] = this.typeOfLink;
    data['linkInstallationDate'] = this.linkInstallationDate;
    data['linkAcceptanceDate'] = this.linkAcceptanceDate;
    data['purchaseOrderDate'] = this.purchaseOrderDate;
    data['partner'] = this.partner;
    data['expiryDate'] = this.expiryDate;
    data['distance'] = this.distance;
    data['distanceUnit'] = this.distanceUnit;
    data['bandwidth'] = this.bandwidth;
    data['uploadQOS'] = this.uploadQOS;
    data['downloadQOS'] = this.downloadQOS;
    data['linkRouterLocation'] = this.linkRouterLocation;
    data['linkPortType'] = this.linkPortType;
    data['linkRouterIp'] = this.linkRouterIp;
    data['linkPortOnRouter'] = this.linkPortOnRouter;
    data['bandwidthType'] = this.bandwidthType;
    data['linkRouterName'] = this.linkRouterName;
    data['circuitBillingId'] = this.circuitBillingId;
    data['pop'] = this.pop;
    data['associatedLevel'] = this.associatedLevel;
    data['locationLevel1'] = this.locationLevel1;
    data['locationLevel2'] = this.locationLevel2;
    data['locationLevel3'] = this.locationLevel3;
    data['locationLevel4'] = this.locationLevel4;
    data['baseStationId1'] = this.baseStationId1;
    data['baseStationId2'] = this.baseStationId2;
    data['terminationAddress'] = this.terminationAddress;
    data['note'] = this.note;
    data['contactPerson'] = this.contactPerson;
    data['contactPerson1'] = this.contactPerson1;
    data['mobileNumber'] = this.mobileNumber;
    data['mobileNumber1'] = this.mobileNumber1;
    data['landlineNumber'] = this.landlineNumber;
    data['landlineNumber1'] = this.landlineNumber1;
    data['emailId'] = this.emailId;
    data['emailId1'] = this.emailId1;
    data['otcChargesFile'] = this.otcChargesFile;
    data['serviceChargerFile'] = this.serviceChargerFile;
    data['staticOrPooledIP'] = this.staticOrPooledIP;
    data['chargeTypeFile'] = this.chargeTypeFile;
    data['billingCycle'] = this.billingCycle;
    data['billingType'] = this.billingType;
    data['billable'] = this.billable;
    data['billingGroup'] = this.billingGroup;
    data['payable'] = this.payable;
    data['enableProcessing'] = this.enableProcessing;
    data['deposite'] = this.deposite;
    data['poNumber'] = this.poNumber;
    data['billRemark'] = this.billRemark;
    data['fullName'] = this.fullName;
    data['organisation'] = this.organisation;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['zipcode'] = this.zipcode;
    data['state'] = this.state;
    data['country'] = this.country;
    data['status'] = this.status;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['discountType'] = this.discountType;
    data['discount'] = this.discount;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['serviceAreaType'] = this.serviceAreaType;
    data['newDiscountType'] = this.newDiscountType;
    data['newDiscount'] = this.newDiscount;
    data['newDiscountExpiryDate'] = this.newDiscountExpiryDate;
    data['remarks'] = this.remarks;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['branch'] = this.branch;
    data['connectionType'] = this.connectionType;
    data['serviceName'] = this.serviceName;
    data['serviceHoldDate'] = this.serviceHoldDate;
    data['serviceHoldBy'] = this.serviceHoldBy;
    data['serviceHoldRemarks'] = this.serviceHoldRemarks;
    data['serviceResumeBy'] = this.serviceResumeBy;
    data['serviceResumeRemarks'] = this.serviceResumeRemarks;
    data['serviceResumeDate'] = this.serviceResumeDate;
    data['stopServiceRemark'] = this.stopServiceRemark;
    data['discountFlowInProcess'] = this.discountFlowInProcess;
    data['old_discount'] = this.oldDiscount;
    data['vlanid'] = this.vlanid;
    return data;
  }
}
