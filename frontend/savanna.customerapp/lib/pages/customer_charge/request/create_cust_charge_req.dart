class CreateCustChargeReq {
  List<CustChargeDetailsPojoList>? custChargeDetailsPojoList;
  int? custid;
  int? billableCustomerId;
  int? paymentOwnerId;

  CreateCustChargeReq(
      {this.custChargeDetailsPojoList,
        this.custid,
        this.billableCustomerId,
        this.paymentOwnerId});

  CreateCustChargeReq.fromJson(Map<String, dynamic> json) {
    if (json['custChargeDetailsPojoList'] != null) {
      custChargeDetailsPojoList = <CustChargeDetailsPojoList>[];
      json['custChargeDetailsPojoList'].forEach((v) {
        custChargeDetailsPojoList!
            .add(new CustChargeDetailsPojoList.fromJson(v));
      });
    }
    custid = json['custid'];
    billableCustomerId = json['billableCustomerId'];
    paymentOwnerId = json['paymentOwnerId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.custChargeDetailsPojoList != null) {
      data['custChargeDetailsPojoList'] =
          this.custChargeDetailsPojoList!.map((v) => v.toJson()).toList();
    }
    data['custid'] = this.custid;
    data['billableCustomerId'] = this.billableCustomerId;
    data['paymentOwnerId'] = this.paymentOwnerId;
    return data;
  }
}

class CustChargeDetailsPojoList {
  String? type;
  int? chargeid;
  dynamic validity;
  double? price;
  double? actualprice;
  String? chargeDate;
  int? planid;
  String? planName;
  String? unitsOfValidity;
  dynamic billingCycle;
  int? paymentOwnerId;
  double? discount;
  String? staticIPAdrress;
  String? expiry;
  String? expiryDate;
  String? connectionNo;
  String? chargeName;
  int? custId;

  CustChargeDetailsPojoList(
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
        this.connectionNo,
        this.chargeName,
        this.custId
      });

  CustChargeDetailsPojoList.fromJson(Map<String, dynamic> json) {
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
    connectionNo = json['connection_no'];
    chargeName = json['chargeName'];
    custId = json['custid'];
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
    data['connection_no'] = this.connectionNo;
    data['chargeName'] = this.chargeName;
    data['custid'] = this.custId;
    return data;
  }
}
