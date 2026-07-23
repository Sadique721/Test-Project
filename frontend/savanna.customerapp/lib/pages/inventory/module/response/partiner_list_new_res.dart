class PartnerListNewRes {
  List<Partnerlist>? partnerlist;
  String? timestamp;
  int? status;

  PartnerListNewRes({this.partnerlist, this.timestamp, this.status});

  PartnerListNewRes.fromJson(Map<String, dynamic> json) {
    if (json['partnerlist'] != null) {
      partnerlist = <Partnerlist>[];
      json['partnerlist'].forEach((v) {
        partnerlist!.add(new Partnerlist.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.partnerlist != null) {
      data['partnerlist'] = this.partnerlist!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['status'] = this.status;
    return data;
  }
}

class Partnerlist {
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
  dynamic commrelvalue;
  dynamic balance;
  dynamic commdueday;
  String? nextbilldate;
  String? lastbilldate;
  dynamic taxid;
  dynamic credit;
  String? addresstype;
  String? address1;
  String? address2;
  int? city;
  int? state;
  int? country;
  String? pincode;
  String? mobile;
  String? countryCode;
  dynamic prcode;
  String? partnerType;
  String? email;
  List<int>? serviceAreaIds;
  dynamic parentpartnerid;
  bool? isDelete;
  List<String>? serviceAreaNameList;
  String? cityName;
  String? countryName;
  String? stateName;
  String? taxName;
  String? parentPartnerName;
  dynamic pricebookId;
  String? pricebookname;
  dynamic cpName;
  dynamic cname;
  dynamic panName;
  dynamic outcomeBalance;
  dynamic totalCustomerCount;
  dynamic renewCustomerCount;
  dynamic newCustomerCount;
  String? calendarType;
  dynamic resetDate;
  String? commissionShareType;
  int? mvnoId;
  dynamic buId;
  dynamic creditConsume;
  int? displayId;
  String? displayName;
  dynamic region;
  dynamic branch;
  dynamic bussinessvertical;

  Partnerlist(
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
        this.credit,
        this.addresstype,
        this.address1,
        this.address2,
        this.city,
        this.state,
        this.country,
        this.pincode,
        this.mobile,
        this.countryCode,
        this.prcode,
        this.partnerType,
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
        this.cpName,
        this.cname,
        this.panName,
        this.outcomeBalance,
        this.totalCustomerCount,
        this.renewCustomerCount,
        this.newCustomerCount,
        this.calendarType,
        this.resetDate,
        this.commissionShareType,
        this.mvnoId,
        this.buId,
        this.creditConsume,
        this.displayId,
        this.displayName,
        this.region,
        this.branch,
        this.bussinessvertical});

  Partnerlist.fromJson(Map<String, dynamic> json) {
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
    credit = json['credit'];
    addresstype = json['addresstype'];
    address1 = json['address1'];
    address2 = json['address2'];
    city = json['city'];
    state = json['state'];
    country = json['country'];
    pincode = json['pincode'];
    mobile = json['mobile'];
    countryCode = json['countryCode'];
    prcode = json['prcode'];
    partnerType = json['partnerType'];
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
    cpName = json['cpName'];
    cname = json['cname'];
    panName = json['panName'];
    outcomeBalance = json['outcomeBalance'];
    totalCustomerCount = json['totalCustomerCount'];
    renewCustomerCount = json['renewCustomerCount'];
    newCustomerCount = json['newCustomerCount'];
    calendarType = json['calendarType'];
    resetDate = json['ResetDate'];
    commissionShareType = json['commissionShareType'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    creditConsume = json['creditConsume'];
    displayId = json['displayId'];
    displayName = json['displayName'];
    region = json['region'];
    branch = json['branch'];
    bussinessvertical = json['bussinessvertical'];
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
    data['credit'] = this.credit;
    data['addresstype'] = this.addresstype;
    data['address1'] = this.address1;
    data['address2'] = this.address2;
    data['city'] = this.city;
    data['state'] = this.state;
    data['country'] = this.country;
    data['pincode'] = this.pincode;
    data['mobile'] = this.mobile;
    data['countryCode'] = this.countryCode;
    data['prcode'] = this.prcode;
    data['partnerType'] = this.partnerType;
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
    data['cpName'] = this.cpName;
    data['cname'] = this.cname;
    data['panName'] = this.panName;
    data['outcomeBalance'] = this.outcomeBalance;
    data['totalCustomerCount'] = this.totalCustomerCount;
    data['renewCustomerCount'] = this.renewCustomerCount;
    data['newCustomerCount'] = this.newCustomerCount;
    data['calendarType'] = this.calendarType;
    data['ResetDate'] = this.resetDate;
    data['commissionShareType'] = this.commissionShareType;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['creditConsume'] = this.creditConsume;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    data['region'] = this.region;
    data['branch'] = this.branch;
    data['bussinessvertical'] = this.bussinessvertical;
    data['resetDate'] = this.resetDate;
    return data;
  }
}
