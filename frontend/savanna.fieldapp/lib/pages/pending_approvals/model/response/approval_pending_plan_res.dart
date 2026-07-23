import 'package:savbill/webservices/base_response.dart';

class ApprovalPendingPlanRes extends BaseResponse {
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  List<ApprovalPendingPlan>? dataList;

  ApprovalPendingPlanRes(
      {responseCode,
      responseMessage,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages,
      this.dataList});

  ApprovalPendingPlanRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    if (json['dataList'] != null) {
      dataList = <ApprovalPendingPlan>[];
      json['dataList'].forEach((v) {
        dataList!.add(new ApprovalPendingPlan.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ApprovalPendingPlan {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? id;
  String? name;
  String? displayName;
  String? code;
  String? desc;
  String? category;
  String? startDate;
  String? endDate;
  bool? allowOverUsage;
  int? mvnoId;
  String? status;
  int? serviceId;
  String? plantype;
  num? dbr;
  String? planGroup;
  num? validity;
  String? maxconcurrentsession;
  String? quotaunittime;
  num? quotatime;
  String? quotatype;
  num? offerprice;
  int? qospolicyid;
  String? qospolicyName;
  bool? isDelete;
  String? createDateString;
  String? updateDateString;
  num? taxamount;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  int? buId;
  int? nextStaff;
  num? newOfferPrice;

  ApprovalPendingPlan(
      {this.createdate,
      this.updatedate,
      this.createdByName,
      this.lastModifiedByName,
      this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.displayName,
      this.code,
      this.desc,
      this.category,
      this.startDate,
      this.endDate,
      this.allowOverUsage,
      this.mvnoId,
      this.status,
      this.serviceId,
      this.plantype,
      this.dbr,
      this.planGroup,
      this.validity,
      this.maxconcurrentsession,
      this.quotaunittime,
      this.quotatime,
      this.quotatype,
      this.offerprice,
      this.qospolicyid,
      this.qospolicyName,
      this.isDelete,
      this.createDateString,
      this.updateDateString,
      this.taxamount,
      this.quotaResetInterval,
      this.mode,
      this.unitsOfValidity,
      this.buId,
      this.nextStaff,
      this.newOfferPrice});

  ApprovalPendingPlan.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    name = json['name'];
    displayName = json['displayName'];
    code = json['code'];
    desc = json['desc'];
    category = json['category'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    allowOverUsage = json['allowOverUsage'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    serviceId = json['serviceId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    planGroup = json['planGroup'];
    validity = json['validity'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    qospolicyid = json['qospolicyid'];
    qospolicyName = json['qospolicyName'];
    isDelete = json['isDelete'];
    createDateString = json['createDateString'];
    updateDateString = json['updateDateString'];
    taxamount = json['taxamount'];
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    nextStaff = json['nextStaff'];
    newOfferPrice = json['newOfferPrice'];
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
    data['code'] = this.code;
    data['desc'] = this.desc;
    data['category'] = this.category;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['allowOverUsage'] = this.allowOverUsage;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['serviceId'] = this.serviceId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['qospolicyid'] = this.qospolicyid;
    data['qospolicyName'] = this.qospolicyName;
    data['isDelete'] = this.isDelete;
    data['createDateString'] = this.createDateString;
    data['updateDateString'] = this.updateDateString;
    data['taxamount'] = this.taxamount;
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['nextStaff'] = this.nextStaff;
    data['newOfferPrice'] = this.newOfferPrice;
    return data;
  }
}
