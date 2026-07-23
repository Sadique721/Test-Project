class UpdateCustomerAddress {
  AddressDetailsReq? addressDetails;
  int? updateAddressServiceAreaId;
  bool? isPaymentAddresSame;
  bool? isPermanentAddress;
  int? shiftPartnerid;

  UpdateCustomerAddress(
      {this.addressDetails,
      this.updateAddressServiceAreaId,
      this.isPaymentAddresSame,
      this.isPermanentAddress,
      this.shiftPartnerid});

  UpdateCustomerAddress.fromJson(Map<String, dynamic> json) {
    addressDetails = json['addressDetails'] != null
        ? new AddressDetailsReq.fromJson(json['addressDetails'])
        : null;
    updateAddressServiceAreaId = json['updateAddressServiceAreaId'];
    isPaymentAddresSame = json['isPaymentAddresSame'];
    isPermanentAddress = json['isPermanentAddress'];
    shiftPartnerid = json['shiftPartnerid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.addressDetails != null) {
      data['addressDetails'] = this.addressDetails!.toJson();
    }
    data['updateAddressServiceAreaId'] = this.updateAddressServiceAreaId;
    data['isPaymentAddresSame'] = this.isPaymentAddresSame;
    data['isPermanentAddress'] = this.isPermanentAddress;
    data['shiftPartnerid'] = this.shiftPartnerid;
    return data;
  }
}

class AddressDetailsReq {
  String? addressType;
  String? landmark;
  int? areaId;
  int? pincodeId;
  int? cityId;
  int? stateId;
  int? countryId;

  AddressDetailsReq(
      {this.addressType,
      this.landmark,
      this.areaId,
      this.pincodeId,
      this.cityId,
      this.stateId,
      this.countryId});

  AddressDetailsReq.fromJson(Map<String, dynamic> json) {
    addressType = json['addressType'];
    landmark = json['landmark'];
    areaId = json['areaId'];
    pincodeId = json['pincodeId'];
    cityId = json['cityId'];
    stateId = json['stateId'];
    countryId = json['countryId'];
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
    return data;
  }
}
