import '../../customer/model/response/cust_address_detail.dart';

class AddServiceReq {
  int? id;
  int? failcount;
  String? custtype;
  String? countryCode;
  String? cafno;
  String? calendarType;
  int? partnerid;
  int? serviceareaid;
  String? status;
  int? billableCustomerId;
  List<PlanMappingList>? planMappingList;
  List<CustAddressDetail>? addressList;
  Null? paymentDetails;
  String? dunningCategory;

  AddServiceReq(
      {this.id,
        this.failcount,
        this.custtype,
        this.countryCode,
        this.cafno,
        this.calendarType,
        this.partnerid,
        this.serviceareaid,
        this.status,
        this.billableCustomerId,
        this.planMappingList,
        this.addressList,
        this.paymentDetails,
        this.dunningCategory});

  AddServiceReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    failcount = json['failcount'];
    custtype = json['custtype'];
    countryCode = json['countryCode'];
    cafno = json['cafno'];
    calendarType = json['calendarType'];
    partnerid = json['partnerid'];
    serviceareaid = json['serviceareaid'];
    status = json['status'];
    billableCustomerId = json['billableCustomerId'];
    if (json['planMappingList'] != null) {
      planMappingList = <PlanMappingList>[];
      json['planMappingList'].forEach((v) {
        planMappingList!.add(new PlanMappingList.fromJson(v));
      });
    }
    if (json['addressList'] != null) {
      addressList = <CustAddressDetail>[];
      json['addressList'].forEach((v) {
        addressList!.add(new CustAddressDetail.fromJson(v));
      });
    }
    paymentDetails = json['paymentDetails'];
    dunningCategory = json['dunningCategory'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['failcount'] = this.failcount;
    data['custtype'] = this.custtype;
    data['countryCode'] = this.countryCode;
    data['cafno'] = this.cafno;
    data['calendarType'] = this.calendarType;
    data['partnerid'] = this.partnerid;
    data['serviceareaid'] = this.serviceareaid;
    data['status'] = this.status;
    data['billableCustomerId'] = this.billableCustomerId;
    if (this.planMappingList != null) {
      data['planMappingList'] =
          this.planMappingList!.map((v) => v.toJson()).toList();
    }
    if (this.addressList != null) {
      data['addressList'] = this.addressList!.map((v) => v.toJson()).toList();
    }
    data['paymentDetails'] = this.paymentDetails;
    data['dunningCategory'] = this.dunningCategory;
    return data;
  }
}

class PlanMappingList {
  double? discount;
  int? planId;
  String? service;
  int? serviceId;
  double? validity;
  String? validityUnit;
  bool? istrialplan;
  String? discountType;
  String? discountExpiryDate;
  String? invoiceType;
  String? serialNumber;
  String? planCategory;
  String? billTo;
  dynamic billableCustomerId;
  dynamic newAmount;
  double? offerPrice;
  String? planName;
  String? unitsOfValidity;
  bool? isInvoiceToOrg;

  PlanMappingList(
      {this.discount,
        this.planId,
        this.service,
        this.serviceId,
        this.validity,
        this.validityUnit,
        this.istrialplan,
        this.discountType,
        this.discountExpiryDate,
        this.invoiceType,
        this.serialNumber,
        this.planCategory,
        this.billTo,
        this.billableCustomerId,
        this.newAmount,
        this.offerPrice,
        this.planName,
        this.unitsOfValidity,
        this.isInvoiceToOrg});

  PlanMappingList.fromJson(Map<String, dynamic> json) {
    discount = json['discount'];
    planId = json['planId'];
    service = json['service'];
    serviceId = json['serviceId'];
    validity = json['validity'];
    validityUnit = json['validityUnit'];
    istrialplan = json['istrialplan'];
    discountType = json['discountType'];
    discountExpiryDate = json['discountExpiryDate'];
    invoiceType = json['invoiceType'];
    serialNumber = json['serialNumber'];
    planCategory = json['planCategory'];
    billTo = json['billTo'];
    billableCustomerId = json['billableCustomerId'];
    newAmount = json['newAmount'];
    offerPrice = json['offerPrice'];
    planName = json['planName'];
    unitsOfValidity = json['unitsOfValidity'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['discount'] = this.discount;
    data['planId'] = this.planId;
    data['service'] = this.service;
    data['serviceId'] = this.serviceId;
    data['validity'] = this.validity;
    data['validityUnit'] = this.validityUnit;
    data['istrialplan'] = this.istrialplan;
    data['discountType'] = this.discountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['invoiceType'] = this.invoiceType;
    data['serialNumber'] = this.serialNumber;
    data['planCategory'] = this.planCategory;
    data['billTo'] = this.billTo;
    data['billableCustomerId'] = this.billableCustomerId;
    data['newAmount'] = this.newAmount;
    data['offerPrice'] = this.offerPrice;
    data['planName'] = this.planName;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    return data;
  }
}

class AddressList {
  // int? id;
  String? addressType;
  String? landmark;
  String? landmark1;
  int? areaId;
  int? building_mgmt_id;
  String? buildingNumber;
  int? subareaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;
  String? fullAddress;
  // bool? isDelete;
  String? version;
  // bool? delete;

  AddressList(
      {
        // this.id,
        this.addressType,
        this.landmark,
        this.landmark1,
        this.areaId,
        this.building_mgmt_id,
        this.buildingNumber,
        this.subareaId,
        this.pincodeId,
        this.cityId,
        this.stateId,
        this.countryId,
        this.fullAddress,
        // this.isDelete,
        this.version,
        // this.delete
      });

  AddressList.fromJson(Map<String, dynamic> json) {

    // id = json['id'];
    addressType = json['addressType'];
    landmark = json['landmark'];
    landmark1 = json['landmark1'];
    areaId = json['areaId'];
    building_mgmt_id = json['building_mgmt_id'];
    buildingNumber = json['buildingNumber'];
    subareaId = json['subareaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    fullAddress = json['fullAddress'];
    // isDelete = json['isDelete'];
    version = json['version'];
    // delete = json['delete'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();

    // data['id'] = this.id;
    data['addressType'] = this.addressType;

    data['landmark'] = this.landmark;
    data['landmark1'] = this.landmark1;
    data['areaId'] = this.areaId;
    data['building_mgmt_id'] = this.building_mgmt_id;
    data['buildingNumber'] = this.buildingNumber;
    data['subareaId'] = this.subareaId;
    data['pincodeId'] = this.pincodeId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;

    data['fullAddress'] = this.fullAddress;
    // data['isDelete'] = this.isDelete;

    data['version'] = this.version;
    // data['delete'] = this.delete;
    return data;
  }
}
