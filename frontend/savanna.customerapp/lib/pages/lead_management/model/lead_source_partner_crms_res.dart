import 'package:savbill/webservices/base_response.dart';

class LeadSourcePartnerCRMRes  extends BaseResponse{
  List<PartnerList>? partnerList;
  String? timestamp;
  int? status;

  LeadSourcePartnerCRMRes({this.partnerList, this.timestamp, this.status});

  LeadSourcePartnerCRMRes.fromJson(Map<String, dynamic> json) {
    if (json['partnerList'] != null) {
      partnerList = <PartnerList>[];
      json['partnerList'].forEach((v) {
        partnerList!.add(new PartnerList.fromJson(v));
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

class PartnerList {
  int? id;
  String? name;
  String? status;
  String? commtype;
  double? commrelvalue;
  double? balance;
  int? commdueday;
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
  int? parentPartnerId;
  dynamic priceBookId;
  bool? isDelete;
  int? mvnoId;
  String? commissionShareType;
  dynamic buId;
  dynamic newCustomerCount;
  dynamic renewCustomerCount;
  dynamic totalCustomerCount;

  PartnerList(
      {this.id,
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
        this.parentPartnerId,
        this.priceBookId,
        this.isDelete,
        this.mvnoId,
        this.commissionShareType,
        this.buId,
        this.newCustomerCount,
        this.renewCustomerCount,
        this.totalCustomerCount});

  PartnerList.fromJson(Map<String, dynamic> json) {
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
    parentPartnerId = json['parentPartnerId'];
    priceBookId = json['priceBookId'];
    isDelete = json['isDelete'];
    mvnoId = json['mvnoId'];
    commissionShareType = json['commissionShareType'];
    buId = json['buId'];
    newCustomerCount = json['newCustomerCount'];
    renewCustomerCount = json['renewCustomerCount'];
    totalCustomerCount = json['totalCustomerCount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['parentPartnerId'] = this.parentPartnerId;
    data['priceBookId'] = this.priceBookId;
    data['isDelete'] = this.isDelete;
    data['mvnoId'] = this.mvnoId;
    data['commissionShareType'] = this.commissionShareType;
    data['buId'] = this.buId;
    data['newCustomerCount'] = this.newCustomerCount;
    data['renewCustomerCount'] = this.renewCustomerCount;
    data['totalCustomerCount'] = this.totalCustomerCount;
    return data;
  }
}
