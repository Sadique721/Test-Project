import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';

class OtherInventoryAssignReq {
  int? id;
  int? qty;
  int? productId;
  int? customerId;
  int? serviceId;
  int? inventoryType;
  int? staffId;
  int? inwardId;
  String? assignedDateTime;
  String? status;
  int? paymentOwnerId;
  int? mvnoId;
  int? ownerId;
  String? ownerType;
  int? externalItemId;
  int? itemId;
  dynamic itemType;
  String? connectionNo;
  int? planId;
  bool? isInvoiceToOrg;
  String? billTo;
  String? inventoryJobType;
  String? nature;
  dynamic discount;
  double? offerPrice;
  double? newAmount;
  int? chargeId;
  dynamic planGroupId;
  String? planGroupName;
  bool? isRequiredApproval;
  bool? isFree;
  int? billabecustId;
  dynamic parentCustomerId;
  int? custPackId;
  String? itemAssemblyId;
  bool? itemAssemblyflag;
  String? itemTypeFlag;
  dynamic nonSerializedQty;
  List<ProductMacDataList>? inOutWardMACMapping;
  String? itemAssemblyStatus;
  List<CustInvParams>? custInvParams;
  int? custServiceMapId;

  OtherInventoryAssignReq({
    this.id,
    this.qty,
    this.productId,
    this.serviceId,
    this.custPackId,
    this.customerId,
    this.staffId,
    this.inwardId,
    this.assignedDateTime,
    this.mvnoId,
    this.ownerId,
    this.ownerType,
    this.status,
    this.externalItemId,
    this.itemId,
    this.itemAssemblyId,
    this.itemAssemblyflag,
    this.itemTypeFlag,
    this.inventoryType,
    this.nonSerializedQty,
    this.connectionNo,
    this.planId,
    this.inOutWardMACMapping,
    this.itemAssemblyStatus,
    this.paymentOwnerId,
    this.isInvoiceToOrg,
    this.billTo,
    this.inventoryJobType,
    this.nature,
    this.discount,
    this.offerPrice,
    this.newAmount,
    this.chargeId,
    this.isRequiredApproval,
    this.isFree,
    this.itemType,
    this.billabecustId,
    this.parentCustomerId,
    this.custInvParams,
    this.custServiceMapId
  });

  OtherInventoryAssignReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    serviceId = json['serviceId'];
    custPackId = json['custPackId'];
    customerId = json['customerId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    mvnoId = json['mvnoId'];
    ownerId = json['ownerId'];
    ownerType = json['ownerType'];
    status = json['status'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    itemAssemblyId = json['itemAssemblyId'];
    itemAssemblyflag = json['itemAssemblyflag'];
    itemTypeFlag = json['itemTypeFlag'];
    inventoryType = json['inventoryType'];
    nonSerializedQty = json['nonSerializedQty'];
    planId = json['planId'];
    connectionNo = json['connectionNo'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <ProductMacDataList>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(ProductMacDataList.fromJson(v));
      });
    }

    itemAssemblyStatus = json['itemAssemblyStatus'];
    paymentOwnerId = json['paymentOwnerId'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    billTo = json['billTo'];
    inventoryJobType = json['inventoryJobType'];
    nature = json['nature'];
    discount = json['discount'];
    offerPrice = json['offerPrice'];
    newAmount = json['newAmount'];
    chargeId = json['chargeId'];
    isRequiredApproval = json['isRequiredApproval'];
    isFree = json['isFree'];
    itemType = json['itemType'];
    billabecustId = json['billabecustId'];
    parentCustomerId = json['parentCustomerId'];
    if (json['custInvParams'] != null) {
      custInvParams = <CustInvParams>[];
      json['custInvParams'].forEach((v) {
        custInvParams!.add(new CustInvParams.fromJson(v));
      });
    }
    custServiceMapId = json['custServiceMapId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['serviceId'] = this.serviceId;
    data['custPackId'] = this.custPackId;
    data['customerId'] = this.customerId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['mvnoId'] = this.mvnoId;
    data['ownerId'] = this.ownerId;
    data['ownerType'] = this.ownerType;
    data['status'] = this.status;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['itemAssemblyId'] = this.itemAssemblyId;
    data['itemAssemblyflag'] = this.itemAssemblyflag;
    data['itemTypeFlag'] = this.itemTypeFlag;
    data['inventoryType'] = this.inventoryType;
    data['nonSerializedQty'] = this.nonSerializedQty;
    data['connectionNo'] = this.connectionNo;
    data['planId'] = this.planId;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }

    data['itemAssemblyStatus'] = this.itemAssemblyStatus;

    data['paymentOwnerId'] = paymentOwnerId;
    data['isInvoiceToOrg'] = isInvoiceToOrg;
    data['billTo'] = billTo;
    data['inventoryJobType'] = inventoryJobType;
    data['nature'] = nature;
    data['discount'] = discount;
    data['offerPrice'] = offerPrice;
    data['newAmount'] = newAmount;
    data['chargeId'] = chargeId;
    data['isRequiredApproval'] = isRequiredApproval;
    data['isFree'] = isFree;
    data['itemType'] = itemType;
    data['billabecustId'] = billabecustId;
    data['parentCustomerId'] = parentCustomerId;
    if (this.custInvParams != null) {
      data['custInvParams'] =
          this.custInvParams!.map((v) => v.toJson()).toList();
    }
    data['custServiceMapId'] = this.custServiceMapId;
    return data;
  }
}


class CustInvParams {
  String? paramName;
  String? paramValue;

  CustInvParams({this.paramName, this.paramValue});

  CustInvParams.fromJson(Map<String, dynamic> json) {
    paramName = json['paramName'];
    paramValue = json['paramValue'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['paramName'] = this.paramName;
    data['paramValue'] = this.paramValue;
    return data;
  }
}
