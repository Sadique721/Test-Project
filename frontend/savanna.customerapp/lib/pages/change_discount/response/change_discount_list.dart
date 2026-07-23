/*
class ChangeDiscountList {
  List<DiscountDetails>? discountDetails;

  ChangeDiscountList({this.discountDetails});

  ChangeDiscountList.fromJson(Map<String, dynamic> json) {
    if (json['discountDetails'] != null) {
      discountDetails = <DiscountDetails>[];
      json['discountDetails'].forEach((v) {
        discountDetails!.add(new DiscountDetails.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.discountDetails != null) {
      data['discountDetails'] =
          this.discountDetails!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class DiscountDetails {
  int? id;
  int? custId;
  int? planId;
  num? oldDiscount;
  num? newDiscount;
  String? startDate;
  String? endDate;
  String? planName;
  String? strNewDiscount;

  DiscountDetails(
      {this.id,
      this.custId,
      this.planId,
      this.oldDiscount,
      this.newDiscount,
      this.startDate,
      this.endDate,
      this.planName});

  DiscountDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    planId = json['planId'];
    oldDiscount = json['oldDiscount'];
    newDiscount = json['newDiscount'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    planName = json['planName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['planId'] = this.planId;
    data['oldDiscount'] = this.oldDiscount;
    data['newDiscount'] = this.newDiscount;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['planName'] = this.planName;
    return data;
  }
}

*/

class ChangeDiscountList {
  List<DiscountDetails>? discountDetails;

  ChangeDiscountList({this.discountDetails});

  ChangeDiscountList.fromJson(Map<String, dynamic> json) {
    if (json['discountDetails'] != null) {
      discountDetails = <DiscountDetails>[];
      json['discountDetails'].forEach((v) {
        discountDetails!.add(new DiscountDetails.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.discountDetails != null) {
      data['discountDetails'] =
          this.discountDetails!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class DiscountDetails {
  int? id;
  int? custId;
  int? serviceId;
  String? serviceName;
  String? connectionNo;
  dynamic nickName;
  dynamic discountType;
  dynamic discount;
  String? discountExpiryDate;
  dynamic newDiscount;
  dynamic newDiscountType;
  String? newDiscountExpiryDate;
  dynamic remarks;
  dynamic nextTeamHierarchyMappingId;
  dynamic nextStaff;
  String? invoiceType;
  String? status;
  dynamic discountFlowInProcess;

  DiscountDetails(
      {this.id,
        this.custId,
        this.serviceId,
        this.serviceName,
        this.connectionNo,
        this.nickName,
        this.discountType,
        this.discount,
        this.discountExpiryDate,
        this.newDiscount,
        this.newDiscountType,
        this.newDiscountExpiryDate,
        this.remarks,
        this.nextTeamHierarchyMappingId,
        this.nextStaff,
        this.invoiceType,
        this.status,
        this.discountFlowInProcess});

  DiscountDetails.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    serviceId = json['serviceId'];
    serviceName = json['serviceName'];
    connectionNo = json['connectionNo'];
    nickName = json['nickName'];
    discountType = json['discountType'];
    discount = json['discount'];
    discountExpiryDate = json['discountExpiryDate'];
    newDiscount = json['newDiscount'];
    newDiscountType = json['newDiscountType'];
    newDiscountExpiryDate = json['newDiscountExpiryDate'];
    remarks = json['remarks'];
    nextTeamHierarchyMappingId = json['nextTeamHierarchyMappingId'];
    nextStaff = json['nextStaff'];
    invoiceType = json['invoiceType'];
    status = json['status'];
    discountFlowInProcess = json['discountFlowInProcess'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['serviceId'] = this.serviceId;
    data['serviceName'] = this.serviceName;
    data['connectionNo'] = this.connectionNo;
    data['nickName'] = this.nickName;
    data['discountType'] = this.discountType;
    data['discount'] = this.discount;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['newDiscount'] = this.newDiscount;
    data['newDiscountType'] = this.newDiscountType;
    data['newDiscountExpiryDate'] = this.newDiscountExpiryDate;
    data['remarks'] = this.remarks;
    data['nextTeamHierarchyMappingId'] = this.nextTeamHierarchyMappingId;
    data['nextStaff'] = this.nextStaff;
    data['invoiceType'] = this.invoiceType;
    data['status'] = this.status;
    data['discountFlowInProcess'] = this.discountFlowInProcess;
    return data;
  }
}
