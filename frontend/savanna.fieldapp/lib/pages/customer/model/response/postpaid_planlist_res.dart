import 'package:savbill/webservices/base_response.dart';

class PostpaidPlanListRes extends BaseResponse {
  List<PostpaidPlanDetail>? postpaidplanList;

  PostpaidPlanListRes({this.postpaidplanList, timestamp, error, status});

  PostpaidPlanListRes.fromJson(Map<String, dynamic> json) {
    if (json['postpaidplanList'] != null) {
      postpaidplanList = <PostpaidPlanDetail>[];
      json['postpaidplanList'].forEach((v) {
        postpaidplanList!.add(new PostpaidPlanDetail.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    error = json['error'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.postpaidplanList != null) {
      data['postpaidplanList'] =
          this.postpaidplanList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['error'] = this.error;
    data['status'] = this.status;
    return data;
  }
}

class PostpaidPlanDetail {
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
  String? expiryDate;

  //bool? allowOverUsage;
  String? quotaUnit;
  num? quota;
  int? mvnoId;
  String? status;
  int? serviceId;
  String? plantype;
  num? dbr;
  String? planGroup;
  num? validity;
  String? maxconcurrentsession;
  String? quotatype;
  num? offerprice;
  num? newOfferPrice;
  int? qospolicyid;
  String? qospolicyName;
  bool? isDelete;
  num? taxamount;
  String? quotaResetInterval;
  String? mode;
  String? unitsOfValidity;
  int? buId;
  String? quotaunittime;
  num? quotatime;

  bool? selected = false;

  PostpaidPlanDetail(
      {this.createdById,
      this.lastModifiedById,
      this.id,
      this.name,
      this.displayName,
      this.code,
      this.desc,
      this.category,
      this.startDate,
      this.endDate,
      this.expiryDate,
      //this.allowOverUsage,
      this.quotaUnit,
      this.quota,
      this.mvnoId,
      this.status,
      this.serviceId,
      this.plantype,
      this.dbr,
      this.planGroup,
      this.validity,
      this.maxconcurrentsession,
      this.quotatype,
      this.offerprice,
      this.newOfferPrice,
      this.qospolicyid,
      this.qospolicyName,
      this.isDelete,
      this.taxamount,
      this.quotaResetInterval,
      this.mode,
      this.unitsOfValidity,
      this.buId,
      this.quotaunittime,
      this.quotatime,
      this.selected});

  PostpaidPlanDetail.fromJson(Map<String, dynamic> json) {
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
    expiryDate = json['expiryDate'];
    // allowOverUsage = json['allowOverUsage'];
    quotaUnit = json['quotaUnit'];
    quota = json['quota'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    serviceId = json['serviceId'];
    plantype = json['plantype'];
    dbr = json['dbr'];
    planGroup = json['planGroup'];
    validity = json['validity'];
    maxconcurrentsession = json['maxconcurrentsession'];
    quotatype = json['quotatype'];
    offerprice = json['offerprice'];
    newOfferPrice = json['newOfferPrice'];
    qospolicyid = json['qospolicyid'];
    qospolicyName = json['qospolicyName'];
    isDelete = json['isDelete'];
    taxamount = json['taxamount'];
    quotaResetInterval = json['quotaResetInterval'];
    mode = json['mode'];
    unitsOfValidity = json['unitsOfValidity'];
    buId = json['buId'];
    quotaunittime = json['quotaunittime'];
    quotatime = json['quotatime'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
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
    data['expiryDate'] = this.expiryDate;
    //data['allowOverUsage'] = this.allowOverUsage;
    data['quotaUnit'] = this.quotaUnit;
    data['quota'] = this.quota;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['serviceId'] = this.serviceId;
    data['plantype'] = this.plantype;
    data['dbr'] = this.dbr;
    data['planGroup'] = this.planGroup;
    data['validity'] = this.validity;
    data['maxconcurrentsession'] = this.maxconcurrentsession;
    data['quotatype'] = this.quotatype;
    data['offerprice'] = this.offerprice;
    data['newOfferPrice'] = this.newOfferPrice;
    data['qospolicyid'] = this.qospolicyid;
    data['qospolicyName'] = this.qospolicyName;
    data['isDelete'] = this.isDelete;
    data['taxamount'] = this.taxamount;
    data['quotaResetInterval'] = this.quotaResetInterval;
    data['mode'] = this.mode;
    data['unitsOfValidity'] = this.unitsOfValidity;
    data['buId'] = this.buId;
    data['quotaunittime'] = this.quotaunittime;
    data['quotatime'] = this.quotatime;
    return data;
  }
}
