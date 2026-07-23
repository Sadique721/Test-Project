class ActivePartnerListRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<ActivePartnerDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  ActivePartnerListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  ActivePartnerListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <ActivePartnerDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(ActivePartnerDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ActivePartnerDataList {
  String? createdate;
  String? updatedate;
  dynamic createdByName;
  dynamic lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  int? id;
  String? name;
  String? status;
  String? commtype;
  double? commrelvalue;
  double? balance;
  dynamic commdueday;
  dynamic nextbilldate;
  dynamic lastbilldate;
  dynamic taxid;
  dynamic credit;
  dynamic addresstype;
  dynamic address1;
  dynamic address2;
  dynamic city;
  dynamic state;
  dynamic country;
  dynamic pincode;
  dynamic mobile;
  dynamic countryCode;
  dynamic prcode;
  dynamic partnerType;
  dynamic email;
  dynamic parentpartnerid;
  bool? isDelete;
  String? cityName;
  String? countryName;
  String? stateName;
  dynamic taxName;
  String? parentPartnerName;
  dynamic pricebookId;
  dynamic pricebookname;
  dynamic cpName;
  dynamic cname;
  dynamic panName;
  double? outcomeBalance;
  dynamic totalCustomerCount;
  dynamic renewCustomerCount;
  dynamic newCustomerCount;
  dynamic calendarType;
  dynamic resetDate;
  dynamic commissionShareType;
  int? mvnoId;
  dynamic buId;
  dynamic creditConsume;
  int? displayId;
  String? displayName;
  dynamic region;
  dynamic branch;
  dynamic bussinessvertical;
  int? identityKey;

  ActivePartnerDataList(
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
        this.parentpartnerid,
        this.isDelete,
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
        this.bussinessvertical,
        this.identityKey});

  ActivePartnerDataList.fromJson(Map<String, dynamic> json) {
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
    parentpartnerid = json['parentpartnerid'];
    isDelete = json['isDelete'];
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
    identityKey = json['identityKey'];
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

    data['parentpartnerid'] = this.parentpartnerid;
    data['isDelete'] = this.isDelete;

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
    data['identityKey'] = this.identityKey;
    return data;
  }
}
