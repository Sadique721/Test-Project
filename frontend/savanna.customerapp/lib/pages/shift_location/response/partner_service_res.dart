import 'package:savbill/webservices/base_response.dart';

class PartnerServiceRes extends BaseResponse {
  List<PartnerServiceDetail>? partnerList;

  PartnerServiceRes({this.partnerList, timestamp, status});

  PartnerServiceRes.fromJson(Map<String, dynamic> json) {
    if (json['partnerList'] != null) {
      partnerList = <PartnerServiceDetail>[];
      json['partnerList'].forEach((v) {
        partnerList!.add(new PartnerServiceDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.partnerList != null) {
      data['partnerList'] = this.partnerList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class PartnerServiceDetail {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? status;
  String? commtype;
  num? commrelvalue;
  num? balance;
  num? commdueday;
  String? nextbilldate;
  String? lastbilldate;
  int? taxid;
  String? addresstype;
  String? address1;
  String? address2;
  int? city;
  int? state;
  int? country;
  String? pincode;
  String? mobile;
  String? countryCode;
  String? email;
  List<int>? serviceAreaIds;
  int? parentpartnerid;
  bool? isDelete;
  List<String>? serviceAreaNameList;
  String? cityName;
  String? countryName;
  String? stateName;
  String? taxName;
  String? parentPartnerName;
  int? pricebookId;
  String? pricebookname;
  num? outcomeBalance;
  String? calendarType;
  String? commissionShareType;
  int? mvnoId;

  PartnerServiceDetail(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.status,
      this.commtype,
      this.commrelvalue,
      this.balance,
      this.commdueday,
      this.nextbilldate,
      this.lastbilldate,
      this.taxid,
      this.addresstype,
      this.address1,
      this.address2,
      this.city,
      this.state,
      this.country,
      this.pincode,
      this.mobile,
      this.countryCode,
      this.email,
      this.serviceAreaIds,
      this.parentpartnerid,
      this.isDelete,
      this.serviceAreaNameList,
      this.cityName,
      this.countryName,
      this.stateName,
      this.taxName,
      this.parentPartnerName,
      this.pricebookId,
      this.pricebookname,
      this.outcomeBalance,
      this.calendarType,
      this.commissionShareType,
      this.mvnoId});

  PartnerServiceDetail.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    status = json['status'];
    commtype = json['commtype'];
    commrelvalue = json['commrelvalue'];
    balance = json['balance'];
    commdueday = json['commdueday'];
    nextbilldate = json['nextbilldate'];
    lastbilldate = json['lastbilldate'];
    taxid = json['taxid'];
    addresstype = json['addresstype'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    pincode = json['pincode'];
    mobile = json['mobile'];
    countryCode = json['countryCode'];
    email = json['email'];
    serviceAreaIds = json['serviceAreaIds'].cast<int>();
    parentpartnerid = json['parentpartnerid'];
    isDelete = json['isDelete'];
    serviceAreaNameList = json['serviceAreaNameList'].cast<String>();
    cityName = json['cityName'];
    countryName = json['countryName'];
    stateName = json['stateName'];
    taxName = json['taxName'];
    parentPartnerName = json['parentPartnerName'];
    pricebookId = json['pricebookId'];
    pricebookname = json['pricebookname'];
    outcomeBalance = json['outcomeBalance'];
    calendarType = json['calendarType'];
    commissionShareType = json['commissionShareType'];
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
    data['commtype'] = this.commtype;
    data['commrelvalue'] = this.commrelvalue;
    data['balance'] = this.balance;
    data['commdueday'] = this.commdueday;
    data['nextbilldate'] = this.nextbilldate;
    data['lastbilldate'] = this.lastbilldate;
    data['taxid'] = this.taxid;
    data['addresstype'] = this.addresstype;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['pincode'] = this.pincode;
    data['mobile'] = this.mobile;
    data['countryCode'] = this.countryCode;
    data['email'] = this.email;
    data['serviceAreaIds'] = this.serviceAreaIds;
    data['parentpartnerid'] = this.parentpartnerid;
    data['isDelete'] = this.isDelete;
    data['serviceAreaNameList'] = this.serviceAreaNameList;
    data['cityName'] = this.cityName;
    data['countryName'] = this.countryName;
    data['stateName'] = this.stateName;
    data['taxName'] = this.taxName;
    data['parentPartnerName'] = this.parentPartnerName;
    data['pricebookId'] = this.pricebookId;
    data['pricebookname'] = this.pricebookname;
    data['outcomeBalance'] = this.outcomeBalance;
    data['calendarType'] = this.calendarType;
    data['commissionShareType'] = this.commissionShareType;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
