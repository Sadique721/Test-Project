import 'package:savbill/webservices/base_response.dart';

import '../../../dashboard/model/response/cust_plan_detail_res.dart';

class PlanServiceByCustomerRes extends BaseResponse {
  List<CustomerPlanServiceDetail>? dataList;

  PlanServiceByCustomerRes({responseCode, responseMessage, this.dataList});

  PlanServiceByCustomerRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <CustomerPlanServiceDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerPlanServiceDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class CustomerPlanServiceDetail {
  int? planId;
  String? planName;
  int? serviceId;
  int? planmapid;
  String? qosPolicyName;
  int? qosPolicyId;
  int? custId;
  dynamic validity;
  String? quotaType;
  String? volTotalQuota;
  String? volUsedQuota;
  String? volQuotaUnit;
  String? timeTotalQuota;
  String? timeUsedQuota;
  String? timeQuotaUnit;
  String? startDate;
  String? endDate;
  String? expiryDate;
  String? startDateString;
  String? expiryDateString;
  String? plangroup;
  dynamic maxsession;
  String? service;
  String? planstage;
  dynamic childValidity;
  dynamic plangroupid;
  String? planGroupName;
  bool? istrialplan;
  String? custPlanStatus;
  bool? isinvoicestop;
  String? nickname;
  String? connectionNo;
  dynamic custPlanMapppingId;
  String? stopServiceDate;
  String? createbyname;
  String? createdate;
  bool? isdeleteforVoid;
  dynamic remarks;
  bool? isinvoicestopinpackrel;
  String? billTo;
  double? discount;
  String? custPlanCategory;
  String? discountType;
  bool? isDtv;
  bool? isChildExists;
  bool? isQosv;
  dynamic invoiceType;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  List<CustomerInventorySerialnumber>? customerInventorySerialnumberDtos;
  dynamic customerServiceMappingId;
  String? discountExpiryDate;
  String? serviceEndDate;
  String? serviceHoldDate;
  String? serviceResumeDate;
  String? serviceHoldBy;
  String? serviceResumeBy;
  String? serviceHoldRemarks;
  String? serviceResumeRemarks;
  String? custServMappingStatus;
  String? dbStartDate;
  String? dbEndDate;
  String? dbExpiryDate;
  bool? isPromiseToPayTaken;
  bool? isSelectedPlan = false;
  bool? changeFlag;
  dynamic newPlanSelection;
  dynamic newPlan;
  dynamic newDiscount;
  CustomerPlanServiceDetail(
      {this.planId,
      this.planName,
      this.serviceId,
      this.planmapid,
      this.qosPolicyName,
      this.qosPolicyId,
      this.custId,
      this.validity,
      this.quotaType,
      this.volTotalQuota,
      this.volUsedQuota,
      this.volQuotaUnit,
      this.timeTotalQuota,
      this.timeUsedQuota,
      this.timeQuotaUnit,
      this.startDate,
      this.endDate,
      this.expiryDate,
      this.startDateString,
      this.expiryDateString,
      this.plangroup,
      this.maxsession,
      this.service,
      this.planstage,
      this.childValidity,
      this.plangroupid,
      this.planGroupName,
      this.istrialplan,
      this.custPlanStatus,
      this.isinvoicestop,
      this.nickname,
      this.connectionNo,
      this.custPlanMapppingId,
      this.stopServiceDate,
      this.createbyname,
      this.createdate,
      this.isdeleteforVoid,
      this.remarks,
      this.isinvoicestopinpackrel,
      this.billTo,
      this.discount,
      this.custPlanCategory,
      this.discountType,
      this.isDtv,
      this.isChildExists,
      this.isQosv,
      this.invoiceType,
      this.nextTeamHierarchyMappingId,
      this.nextStaff,
        this.customerInventorySerialnumberDtos,
      this.customerServiceMappingId,
      this.discountExpiryDate,
      this.serviceEndDate,
      this.serviceHoldDate,
      this.serviceResumeDate,
      this.serviceHoldBy,
      this.serviceResumeBy,
      this.serviceResumeRemarks,
      this.serviceHoldRemarks,
      this.custServMappingStatus,
        this.dbStartDate,
        this.dbEndDate,
        this.dbExpiryDate,
        this.isPromiseToPayTaken,
        this.isSelectedPlan,
        this.changeFlag,
        this.newPlanSelection,
        this.newPlan,
        this.newDiscount,


      });

  CustomerPlanServiceDetail.fromJson(Map<String, dynamic> json) {
    planId = json['planId'];
    planName = json['planName'];
    serviceId = json['serviceId'];
    planmapid = json['planmapid'];
    qosPolicyName = json['qosPolicyName'];
    qosPolicyId = json['qosPolicyId'];
    custId = json['custId'];
    validity = json['validity'];
    quotaType = json['quotaType'];
    volTotalQuota = json['volTotalQuota'];
    volUsedQuota = json['volUsedQuota'];
    volQuotaUnit = json['volQuotaUnit'];
    timeTotalQuota = json['timeTotalQuota'];
    timeUsedQuota = json['timeUsedQuota'];
    timeQuotaUnit = json['timeQuotaUnit'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    expiryDate = json['expiryDate'];
    startDateString = json['startDateString'];
    expiryDateString = json['expiryDateString'];
    plangroup = json['plangroup'];
    maxsession = json['maxsession'];
    service = json['service'];
    planstage = json['planstage'];
    childValidity = json['childValidity'];
    plangroupid = json['plangroupid'];
    planGroupName = json['planGroupName'];
    istrialplan = json['istrialplan'];
    custPlanStatus = json['custPlanStatus'];
    isinvoicestop = json['isinvoicestop'];
    nickname = json['nickname'];
    connectionNo = json['connection_no'];
    custPlanMapppingId = json['custPlanMapppingId'];
    stopServiceDate = json['stopServiceDate'];
    createbyname = json['createbyname'];
    createdate = json['createdate'];
    isdeleteforVoid = json['isdeleteforVoid'];
    remarks = json['remarks'];
    isinvoicestopinpackrel = json['isinvoicestopinpackrel'];
    billTo = json['billTo'];
    discount = json['discount'];
    custPlanCategory = json['custPlanCategory'];
    discountType = json['discountType'];
    isDtv = json['is_dtv'];
    isChildExists = json['isChildExists'];
    isQosv = json['is_qosv'];
    invoiceType = json['invoiceType'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    if (json['customerInventorySerialnumberDtos'] != null) {
      customerInventorySerialnumberDtos = <CustomerInventorySerialnumber>[];
      json['customerInventorySerialnumberDtos'].forEach((v) {
        customerInventorySerialnumberDtos!.add(CustomerInventorySerialnumber.fromJson(v));
      });
    }
    customerServiceMappingId = json['customerServiceMappingId'];
    discountExpiryDate = json['discountExpiryDate'];
    serviceEndDate = json['serviceEndDate'];
    serviceHoldDate = json['serviceHoldDate'];
    serviceResumeDate = json['serviceResumeDate'];
    serviceHoldBy = json['serviceHoldBy'];
    serviceResumeBy = json['serviceResumeBy'];
    serviceHoldRemarks = json['serviceHoldRemarks'];
    serviceHoldRemarks = json['serviceHoldRemarks'];
    custServMappingStatus = json['custServMappingStatus'];
    dbStartDate = json['dbStartDate'];
    dbEndDate = json['dbEndDate'];
    dbExpiryDate = json['dbExpiryDate'];
    isPromiseToPayTaken = json['isPromiseToPayTaken'];
    changeFlag = json['changeFlag'];
    newPlanSelection = json['newPlanSelection'];
    newPlan = json['newplan'];
    newDiscount = json['newDiscount'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['planId'] = this.planId;
    data['planName'] = this.planName;
    data['serviceId'] = this.serviceId;
    data['planmapid'] = this.planmapid;
    data['qosPolicyName'] = this.qosPolicyName;
    data['qosPolicyId'] = this.qosPolicyId;
    data['custId'] = this.custId;
    data['validity'] = this.validity;
    data['quotaType'] = this.quotaType;
    data['volTotalQuota'] = this.volTotalQuota;
    data['volUsedQuota'] = this.volUsedQuota;
    data['volQuotaUnit'] = this.volQuotaUnit;
    data['timeTotalQuota'] = this.timeTotalQuota;
    data['timeUsedQuota'] = this.timeUsedQuota;
    data['timeQuotaUnit'] = this.timeQuotaUnit;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['expiryDate'] = this.expiryDate;
    data['startDateString'] = this.startDateString;
    data['expiryDateString'] = this.expiryDateString;
    data['plangroup'] = this.plangroup;
    data['maxsession'] = this.maxsession;
    data['service'] = this.service;
    data['planstage'] = this.planstage;
    data['childValidity'] = this.childValidity;
    data['plangroupid'] = this.plangroupid;
    data['planGroupName'] = this.planGroupName;
    data['istrialplan'] = this.istrialplan;
    data['custPlanStatus'] = this.custPlanStatus;
    data['isinvoicestop'] = this.isinvoicestop;
    data['nickname'] = this.nickname;
    data['connection_no'] = this.connectionNo;
    data['custPlanMapppingId'] = this.custPlanMapppingId;
    data['stopServiceDate'] = this.stopServiceDate;
    data['createbyname'] = this.createbyname;
    data['createdate'] = this.createdate;
    data['isdeleteforVoid'] = this.isdeleteforVoid;
    data['remarks'] = this.remarks;
    data['isinvoicestopinpackrel'] = this.isinvoicestopinpackrel;
    data['billTo'] = this.billTo;
    data['discount'] = this.discount;
    data['custPlanCategory'] = this.custPlanCategory;
    data['discountType'] = this.discountType;
    data['is_dtv'] = this.isDtv;
    data['isChildExists'] = this.isChildExists;
    data['is_qosv'] = this.isQosv;
    data['invoiceType'] = this.invoiceType;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    if (this.customerInventorySerialnumberDtos != null) {
      data['customerInventorySerialnumberDtos'] = this
          .customerInventorySerialnumberDtos!
          .map((v) => v.toJson())
          .toList();
    }
    data['customerServiceMappingId'] = this.customerServiceMappingId;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['serviceEndDate'] = this.serviceEndDate;
    data['serviceHoldDate'] = this.serviceHoldDate;
    data['serviceResumeDate'] = this.serviceResumeDate;
    data['serviceHoldBy'] = this.serviceHoldBy;
    data['serviceResumeBy'] = this.serviceResumeBy;
    data['serviceHoldRemarks'] = this.serviceHoldRemarks;
    data['serviceResumeRemarks'] = this.serviceResumeRemarks;
    data['custServMappingStatus'] = this.custServMappingStatus;
    data['dbStartDate'] = this.dbStartDate;
    data['dbEndDate'] = this.dbEndDate;
    data['dbExpiryDate'] = this.dbExpiryDate;
    data['isPromiseToPayTaken'] = this.isPromiseToPayTaken;
    data['changeFlag'] = this.changeFlag;
    data['newPlanSelection'] = this.newPlanSelection;
    data['newplan'] = this.newPlan;
    data['newDiscount'] = this.newDiscount;
    return data;
  }
}
