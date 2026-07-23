class DiscountUpdateData {
  int? id;
  int? custId;
  String? connectionNo;
  String? serviceName;
  int? serviceId;
  String? invoiceType;
  String? discount;
  String? newDiscount;
  String? remarks;
  String? status;
  String? discountType;
  String? newDiscountType;
  String? discountExpiryDate;
  String? newDiscountExpiryDate;

  DiscountUpdateData(
      {this.id,
        this.custId,
        this.connectionNo,
        this.serviceName,
        this.serviceId,
        this.invoiceType,
        this.discount,
        this.newDiscount,
        this.remarks,
        this.status,
        this.discountType,
        this.newDiscountType,
        this.discountExpiryDate,
        this.newDiscountExpiryDate});

  DiscountUpdateData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    custId = json['custId'];
    connectionNo = json['connectionNo'];
    serviceName = json['serviceName'];
    serviceId = json['serviceId'];
    invoiceType = json['invoiceType'];
    discount = json['discount'];
    newDiscount = json['newDiscount'];
    remarks = json['remarks'];
    status = json['status'];
    discountType = json['discountType'];
    newDiscountType = json['newDiscountType'];
    discountExpiryDate = json['discountExpiryDate'];
    newDiscountExpiryDate = json['newDiscountExpiryDate'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['custId'] = this.custId;
    data['connectionNo'] = this.connectionNo;
    data['serviceName'] = this.serviceName;
    data['serviceId'] = this.serviceId;
    data['invoiceType'] = this.invoiceType;
    data['discount'] = this.discount;
    data['newDiscount'] = this.newDiscount;
    data['remarks'] = this.remarks;
    data['status'] = this.status;
    data['discountType'] = this.discountType;
    data['newDiscountType'] = this.newDiscountType;
    data['discountExpiryDate'] = this.discountExpiryDate;
    data['newDiscountExpiryDate'] = this.newDiscountExpiryDate;
    return data;
  }
}
