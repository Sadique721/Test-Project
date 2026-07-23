import 'package:savbill/webservices/base_response.dart';

class ChangePlanListResponse extends BaseResponse {
  List<PlanData>? data;

  ChangePlanListResponse({responseCode, responseMessage, this.data});

  ChangePlanListResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['data'] != null) {
      data = <PlanData>[];
      json['data'].forEach((v) {
        data!.add(new PlanData.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.data != null) {
      data['data'] = this.data!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class PlanData {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? displayName;
  String? planGroup;
  String? code;
  String? desc;
  String? category;
  String? startDate;
  String? endDate;
  num? quota;
  String? quotaUnit;
  bool? allowOverUsage;
  String? status;
  int? serviceId;
  String? plantype;
  num? dbr;
  num? validity;
  String? maxconcurrentsession;
  String? quotaunittime;
  num? quotatime;
  String? quotatype;
  num? offerprice;
  num? taxamount;
  String? serviceName;

  PlanData(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.displayName,
      this.planGroup,
      this.code,
      this.desc,
      this.category,
      this.startDate,
      this.endDate,
      this.quota,
      this.quotaUnit,
      this.allowOverUsage,
      this.status,
      this.serviceId,
      this.plantype,
      this.dbr,
      this.validity,
      this.maxconcurrentsession,
      this.quotaunittime,
      this.quotatime,
      this.quotatype,
      this.offerprice,
      this.taxamount,
      this.serviceName});

  PlanData.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    displayName = json['displayName'];
    planGroup = json['planGroup'];
    code = json['code'];
    desc = json['desc'];
    category = json['category'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    quota = json['quota'];
    quotaUnit = json['quotaUnit'];
    allowOverUsage = json['allowOverUsage'];
    status = json['status'];
    serviceId = json['serviceId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    validity = json['validity'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    taxamount = json['taxamount'];
    serviceName = json['serviceName'];
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
    data['displayName'] = this.displayName;
    data['planGroup'] = this.planGroup;
    data['code'] = this.code;
    data['desc'] = this.desc;
    data['category'] = this.category;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['quota'] = this.quota;
    data['quotaUnit'] = this.quotaUnit;
    data['allowOverUsage'] = this.allowOverUsage;
    data['status'] = this.status;
    data['serviceId'] = this.serviceId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    data['validity'] = this.validity;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['taxamount'] = this.taxamount;
    data['serviceName'] = this.serviceName;
    return data;
  }
}
