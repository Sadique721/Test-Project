class ExternalGroupAddEditReq {
  int? id;
  int? productId;
  int? qty;
  String? ownershipType;
  int? ownerId;
  String? status;
  String? externalItemGroupNumber;
  String? inTransitQty;
  int? mvnoId;
  int? usedQty;
  int? unusedQty;
  String? rejectedQty;
  ExternalGroupServiceArea? serviceAreaId;
  int? totalMacSerial;

  ExternalGroupAddEditReq(
      {this.id,
        this.productId,
        this.qty,
        this.ownershipType,
        this.ownerId,
        this.status,
        this.externalItemGroupNumber,
        this.inTransitQty,
        this.mvnoId,
        this.usedQty,
        this.unusedQty,
        this.rejectedQty,
        this.serviceAreaId,
        this.totalMacSerial});

  ExternalGroupAddEditReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    productId = json['productId'];
    qty = json['qty'];
    ownershipType = json['ownershipType'];
    ownerId = json['ownerId'];
    status = json['status'];
    externalItemGroupNumber = json['externalItemGroupNumber'];
    inTransitQty = json['inTransitQty'];
    mvnoId = json['mvnoId'];
    usedQty = json['usedQty'];
    unusedQty = json['unusedQty'];
    rejectedQty = json['rejectedQty'];
    serviceAreaId = json['serviceAreaId'] != null
        ? new ExternalGroupServiceArea.fromJson(json['serviceAreaId'])
        : null;
    totalMacSerial = json['totalMacSerial'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['productId'] = this.productId;
    data['qty'] = this.qty;
    data['ownershipType'] = this.ownershipType;
    data['ownerId'] = this.ownerId;
    data['status'] = this.status;
    data['externalItemGroupNumber'] = this.externalItemGroupNumber;
    data['inTransitQty'] = this.inTransitQty;
    data['mvnoId'] = this.mvnoId;
    data['usedQty'] = this.usedQty;
    data['unusedQty'] = this.unusedQty;
    data['rejectedQty'] = this.rejectedQty;
    if (this.serviceAreaId != null) {
      data['serviceAreaId'] = this.serviceAreaId!.toJson();
    }
    data['totalMacSerial'] = this.totalMacSerial;
    return data;
  }
}

class ExternalGroupServiceArea{
  int? id;

  ExternalGroupServiceArea({this.id});

  ExternalGroupServiceArea.fromJson(Map<String, dynamic> json) {
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    return data;
  }
}