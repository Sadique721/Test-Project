class ChargeOverrideReq {
  List<CustChargeDetails>? custChargeDetailsPojoList;
  int? custid;

  ChargeOverrideReq({this.custChargeDetailsPojoList, this.custid});

  ChargeOverrideReq.fromJson(Map<String, dynamic> json) {
    if (json['custChargeDetailsPojoList'] != null) {
      custChargeDetailsPojoList = <CustChargeDetails>[];
      json['custChargeDetailsPojoList'].forEach((v) {
        custChargeDetailsPojoList!.add(CustChargeDetails.fromJson(v));
      });
    }
    custid = json['custid'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.custChargeDetailsPojoList != null) {
      data['custChargeDetailsPojoList'] =
          this.custChargeDetailsPojoList!.map((v) => v.toJson()).toList();
    }
    data['custid'] = this.custid;
    return data;
  }
}

class CustChargeDetails {
  String? type;
  int? chargeid;
  num? validity;
  num? price;
  num? actualprice;
  String? chargeDate;
  int? planid;
  String? planName;
  String? unitsOfValidity;
  int? billingCycle;
  int? paymentOwnerId;
  int? discount;
  String? staticIPAdrress;
  String? expiry;
  String? expiryDate;
  String? connection_no;

  CustChargeDetails(
      {this.type,
      this.chargeid,
      this.validity,
      this.price,
      this.actualprice,
      this.chargeDate,
      this.planid,
      this.planName,
      this.unitsOfValidity,
      this.billingCycle,
      this.paymentOwnerId,
      this.discount,
      this.staticIPAdrress,
      this.expiry,
      this.expiryDate,
      this.connection_no});

  CustChargeDetails.fromJson(Map<String, dynamic> json) {
    type = json['type'];
    chargeid = json['chargeid'];
    validity = json['validity'];
    price = json['price'];
    actualprice = json['actualprice'];
    chargeDate = json['charge_date'];
    planid = json['planid'];
    planName = json['planName'];
    unitsOfValidity = json['unitsOfValidity'];
    billingCycle = json['billingCycle'];
    paymentOwnerId = json['paymentOwnerId'];
    discount = json['discount'];
    staticIPAdrress = json['staticIPAdrress'];
    expiry = json['expiry'];
    expiryDate = json['expiryDate'];
    connection_no = json['connection_no'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['type'] = this.type;
    data['chargeid'] = this.chargeid;
    data['validity'] = this.validity;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['charge_date'] = this.chargeDate;
    data['planid'] = this.planid;
    data['planName'] = this.planName;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['billingCycle'] = this.billingCycle;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['discount'] = this.discount;
    data['staticIPAdrress'] = this.staticIPAdrress;
    data['expiry'] = this.expiry;
    data['expiryDate'] = this.expiryDate;
    data['connection_no'] = this.connection_no;
    return data;
  }
}
