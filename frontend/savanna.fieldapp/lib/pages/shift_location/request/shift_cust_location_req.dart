class ShiftCustomerLocationReq {
  AddressDetails? addressDetails;
  int? updateAddressServiceAreaId;
  int? serviceareaid;
  int? newShiftbranchID;
  bool? isPaymentAddresSame;
  bool? isPermanentAddress;
  int? shiftPartnerid;
  int? popid;
  int? oltid;
  int? requestedById;
  int? branchID;
  CustChargeOverrideDTO? custChargeOverrideDTO;
  bool? isInvoiceCleared;
  double? transferableCommission;
  double? transferableBalance;

  ShiftCustomerLocationReq(
      {this.addressDetails,
        this.updateAddressServiceAreaId,
        this.serviceareaid,
        this.newShiftbranchID,
        this.isPaymentAddresSame,
        this.isPermanentAddress,
        this.shiftPartnerid,
        this.popid,
        this.oltid,
        this.requestedById,
        this.branchID,
        this.custChargeOverrideDTO,
        this.isInvoiceCleared,
        this.transferableCommission,
        this.transferableBalance});

  ShiftCustomerLocationReq.fromJson(Map<String, dynamic> json) {
    addressDetails = json['addressDetails'] != null
        ? new AddressDetails.fromJson(json['addressDetails'])
        : null;
    updateAddressServiceAreaId = json['updateAddressServiceAreaId'];
    serviceareaid = json['serviceareaid'];
    newShiftbranchID = json['newShiftbranchID'];
    isPaymentAddresSame = json['isPaymentAddresSame'];
    isPermanentAddress = json['isPermanentAddress'];
    shiftPartnerid = json['shiftPartnerid'];
    popid = json['popid'];
    oltid = json['oltid'];
    requestedById = json['requestedById'];
    branchID = json['branchID'];
    custChargeOverrideDTO = json['custChargeOverrideDTO'] != null
        ? new CustChargeOverrideDTO.fromJson(json['custChargeOverrideDTO'])
        : null;
    isInvoiceCleared = json['isInvoiceCleared'];
    transferableCommission = json['transferableCommission'];
    transferableBalance = json['transferableBalance'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.addressDetails != null) {
      data['addressDetails'] = this.addressDetails!.toJson();
    }
    data['updateAddressServiceAreaId'] = this.updateAddressServiceAreaId;
    data['serviceareaid'] = this.serviceareaid;
    data['newShiftbranchID'] = this.newShiftbranchID;
    data['isPaymentAddresSame'] = this.isPaymentAddresSame;
    data['isPermanentAddress'] = this.isPermanentAddress;
    data['shiftPartnerid'] = this.shiftPartnerid;
    data['popid'] = this.popid;
    data['oltid'] = this.oltid;
    data['requestedById'] = this.requestedById;
    data['branchID'] = this.branchID;
    if (this.custChargeOverrideDTO != null) {
      data['custChargeOverrideDTO'] = this.custChargeOverrideDTO!.toJson();
    }
    data['isInvoiceCleared'] = this.isInvoiceCleared;
    data['transferableCommission'] = this.transferableCommission;
    data['transferableBalance'] = this.transferableBalance;
    return data;
  }
}

class AddressDetails {
  String? addressType;
  String? landmark;
  int? areaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;
  int? subareaId;
  int? building_mgmt_id;
  String? buildingNumber;
  dynamic landmark1;

  AddressDetails(
      {this.addressType,
        this.landmark,
        this.areaId,
        this.pincodeId,
        this.cityId,
        this.stateId,
        this.countryId,
        this.subareaId,
        this.buildingNumber,
        this.building_mgmt_id,
        this.landmark1});

  AddressDetails.fromJson(Map<String, dynamic> json) {
    addressType = json['addressType'];
    landmark = json['landmark'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
    subareaId = json['subareaId'];
    building_mgmt_id = json['building_mgmt_id'];
    buildingNumber = json['buildingNumber'];
    landmark1 = json['landmark1'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['addressType'] = this.addressType;
    data['landmark'] = this.landmark;
    data['areaId'] = this.areaId;
    data['pincodeId'] = this.pincodeId;
    data['cityId'] = this.cityId;
    data['stateId'] = this.stateId;
    data['countryId'] = this.countryId;
    data['subareaId'] = this.subareaId;
    data['building_mgmt_id'] = this.building_mgmt_id;
    data['buildingNumber'] = this.buildingNumber;
    data['landmark1'] = this.landmark1;
    return data;
  }
}

class CustChargeOverrideDTO {
  int? billableCustomerId;
  List<CustChargeDetailsPojoList>? custChargeDetailsPojoList;
  int? custid;
  int? paymentOwnerId;

  CustChargeOverrideDTO(
      {this.billableCustomerId,
        this.custChargeDetailsPojoList,
        this.custid,
        this.paymentOwnerId});

  CustChargeOverrideDTO.fromJson(Map<String, dynamic> json) {
    billableCustomerId = json['billableCustomerId'];
    if (json['custChargeDetailsPojoList'] != null) {
      custChargeDetailsPojoList = <CustChargeDetailsPojoList>[];
      json['custChargeDetailsPojoList'].forEach((v) {
        custChargeDetailsPojoList!
            .add(new CustChargeDetailsPojoList.fromJson(v));
      });
    }
    custid = json['custid'];
    paymentOwnerId = json['paymentOwnerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['billableCustomerId'] = this.billableCustomerId;
    if (this.custChargeDetailsPojoList != null) {
      data['custChargeDetailsPojoList'] =
          this.custChargeDetailsPojoList!.map((v) => v.toJson()).toList();
    }
    data['custid'] = this.custid;
    data['paymentOwnerId'] = this.paymentOwnerId;
    return data;
  }
}

class CustChargeDetailsPojoList {
  int? chargeid;
  double? price;
  double? actualprice;
  String? chargeDate;
  String? type;
  double? discount;
  int? billingCycle;
  int? id;
  int? billableCustomerId;
  int? paymentOwnerId;

  CustChargeDetailsPojoList(
      {this.chargeid,
        this.price,
        this.actualprice,
        this.chargeDate,
        this.type,
        this.discount,
        this.billingCycle,
        this.id,
        this.billableCustomerId,
        this.paymentOwnerId});

  CustChargeDetailsPojoList.fromJson(Map<String, dynamic> json) {
    chargeid = json['chargeid'];
    price = json['price'];
    actualprice = json['actualprice'];
    chargeDate = json['charge_date'];
    type = json['type'];
    discount = json['discount'];
    billingCycle = json['billingCycle'];
    id = json['id'];
    billableCustomerId = json['billableCustomerId'];
    paymentOwnerId = json['paymentOwnerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['chargeid'] = this.chargeid;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['charge_date'] = this.chargeDate;
    data['type'] = this.type;
    data['discount'] = this.discount;
    data['billingCycle'] = this.billingCycle;
    data['id'] = this.id;
    data['billableCustomerId'] = this.billableCustomerId;
    data['paymentOwnerId'] = this.paymentOwnerId;
    return data;
  }
}
