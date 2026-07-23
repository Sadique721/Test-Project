import 'package:savbill/webservices/base_response.dart';

class CustomerChargeListRes extends BaseResponse {
  List<CustChargeOverrideDetail>? custChargeOverrideList;

  CustomerChargeListRes({this.custChargeOverrideList, timestamp, status});

  CustomerChargeListRes.fromJson(Map<String, dynamic> json) {
    if (json['custChargeOverrideList'] != null) {
      custChargeOverrideList = <CustChargeOverrideDetail>[];
      json['custChargeOverrideList'].forEach((v) {
        custChargeOverrideList!.add(new CustChargeOverrideDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.custChargeOverrideList != null) {
      data['custChargeOverrideList'] =
          this.custChargeOverrideList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class CustChargeOverrideDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  num? validity;
  int? planid;
  int? chargeid;
  String? chargetype;
  String? chargeName;
  double? price;
  double? actualprice;
  dynamic staticIPAdrress;
  String? remarks;
  String? chargeDate;
  String? startdate;
  String? enddate;
  num? taxamount;
  bool? isReversed;
  bool? isUsed;
  String? type;
  num? planValidity;
  String? unitsOfValidity;
  int? taxId;
  int? custPlanMapppingId;
  bool? isDeleted;
  num? billingCycle;

  CustChargeOverrideDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.validity,
      this.planid,
      this.chargeid,
      this.chargetype,
      this.chargeName,
      this.price,
      this.actualprice,
      this.staticIPAdrress,
      this.remarks,
      this.chargeDate,
      this.startdate,
      this.enddate,
      this.taxamount,
      this.isReversed,
      this.isUsed,
      this.type,
      this.planValidity,
      this.unitsOfValidity,
      this.taxId,
      this.custPlanMapppingId,
      this.isDeleted,
      this.billingCycle

      });

  CustChargeOverrideDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    validity = json['validity'];
    planid = json['planid'];
    chargeid = json['chargeid'];
    chargetype = json['chargetype'];
    chargeName = json['charge_name'];
    price = json['price'];
    actualprice = json['actualprice'];
    staticIPAdrress = json['staticIPAdrress'];
    remarks = json['remarks'];
    chargeDate = json['charge_date'];
    startdate = json['startdate'];
    enddate = json['enddate'];
    taxamount = json['taxamount'];
    isReversed = json['is_reversed'];
    isUsed = json['isUsed'];
    type = json['type'];
    planValidity = json['planValidity'];
    unitsOfValidity = json['unitsOfValidity'];
    taxId = json['taxId'];
    custPlanMapppingId = json['custPlanMapppingId'];
    isDeleted = json['isDeleted'];
    billingCycle = json['billingCycle'];
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
    data['validity'] = this.validity;
    data['planid'] = this.planid;
    data['chargeid'] = this.chargeid;
    data['chargetype'] = this.chargetype;
    data['charge_name'] = this.chargeName;
    data['price'] = this.price;
    data['actualprice'] = this.actualprice;
    data['staticIPAdrress'] = this.staticIPAdrress;
    data['remarks'] = this.remarks;
    data['charge_date'] = this.chargeDate;
    data['startdate'] = this.startdate;
    data['enddate'] = this.enddate;
    data['taxamount'] = this.taxamount;
    data['is_reversed'] = this.isReversed;
    data['isUsed'] = this.isUsed;
    data['type'] = this.type;
    data['planValidity'] = this.planValidity;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['taxId'] = this.taxId;
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['isDeleted'] = this.isDeleted;
    data['billingCycle'] = this.billingCycle;
    return data;
  }
}
