import 'package:savbill/pages/customer_inventory/response/get_mac_mapping_external_res.dart';
import '../response/get_item_based_on_product_type_res.dart';

class AssignPlanInventoryByPlanReq {
  String? id;
  int? qty;
  int? productId;
  int? customerId;
  int? serviceId;
  String? inventoryType;
  String? staffId;
  String? inwardId;
  String? assignedDateTime;
  String? status;
  int? paymentOwnerId;
  String? mvnoId;
  String? externalItemId;
  int? itemId;
  String? itemType;
  String? connectionNo;
  int? planId;
  bool? isInvoiceToOrg;
  String? billTo;
  int? discount;
  int? offerPrice;
  int? newAmount;
  String? chargeId;
  String? planGroupId;
  String? planGroupName;
  bool? isRequiredApproval;
  bool? isFree;
  bool? itemAssemblyflag;
  String? itemAssemblyId;

  int? billabecustId;
  String? parentCustomerId;
  int? productPlanMappingId;
  // List<InOutWardMACMapping>? inOutWardMACMapping;
  List<ProductTypDataList>? inOutWardMACMapping;
  List<MACMappingExternalData>? inOutWardSerialMapping;
  String? itemAssemblyStatus;

  AssignPlanInventoryByPlanReq(
      {this.id,
        this.qty,
        this.productId,
        this.customerId,
        this.serviceId,
        this.inventoryType,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.status,
        this.paymentOwnerId,
        this.mvnoId,
        this.externalItemId,
        this.itemId,
        this.itemType,
        this.connectionNo,
        this.planId,
        this.isInvoiceToOrg,
        this.billTo,
        this.discount,
        this.offerPrice,
        this.newAmount,
        this.chargeId,
        this.planGroupId,
        this.planGroupName,
        this.isRequiredApproval,
        this.isFree,
        this.itemAssemblyflag,
        this.itemAssemblyId,
        this.billabecustId,
        this.parentCustomerId,
        this.productPlanMappingId,
        this.inOutWardMACMapping,
        this.inOutWardSerialMapping,
        this.itemAssemblyStatus});

  AssignPlanInventoryByPlanReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    customerId = json['customerId'];
    serviceId = json['serviceId'];
    inventoryType = json['inventoryType'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    status = json['status'];
    paymentOwnerId = json['paymentOwnerId'];
    mvnoId = json['mvnoId'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    itemType = json['itemType'];
    connectionNo = json['connectionNo'];
    planId = json['planId'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    billTo = json['billTo'];
    discount = json['discount'];
    offerPrice = json['offerPrice'];
    newAmount = json['newAmount'];
    chargeId = json['chargeId'];
    planGroupId = json['planGroupId'];
    planGroupName = json['planGroupName'];
    isRequiredApproval = json['isRequiredApproval'];
    isFree = json['isFree'];
    itemAssemblyflag = json['itemAssemblyflag'];
    itemAssemblyId = json['itemAssemblyId'];
    billabecustId = json['billabecustId'];
    parentCustomerId = json['parentCustomerId'];
    productPlanMappingId = json['productPlanMappingId'];
    if (json['inOutWardMACMapping'] != null) {
      // inOutWardMACMapping = <InOutWardMACMapping>[];
      inOutWardMACMapping = <ProductTypDataList>[];
      json['inOutWardMACMapping'].forEach((v) {
        // inOutWardMACMapping!.add(new InOutWardMACMapping.fromJson(v));
        inOutWardMACMapping!.add(new ProductTypDataList.fromJson(v));
      });
    }
    if (json['inOutWardMACMapping'] != null) {
      // inOutWardMACMapping = <InOutWardMACMapping>[];
      inOutWardSerialMapping = <MACMappingExternalData>[];
      json['inOutWardMACMapping'].forEach((v) {
        // inOutWardMACMapping!.add(new InOutWardMACMapping.fromJson(v));
        inOutWardSerialMapping!.add(new MACMappingExternalData.fromJson(v));
      });
    }
    itemAssemblyStatus = json['itemAssemblyStatus'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['customerId'] = this.customerId;
    data['serviceId'] = this.serviceId;
    data['inventoryType'] = this.inventoryType;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['status'] = this.status;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['mvnoId'] = this.mvnoId;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['itemType'] = this.itemType;
    data['connectionNo'] = this.connectionNo;
    data['planId'] = this.planId;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['billTo'] = this.billTo;
    data['discount'] = this.discount;
    data['offerPrice'] = this.offerPrice;
    data['newAmount'] = this.newAmount;
    data['chargeId'] = this.chargeId;
    data['planGroupId'] = this.planGroupId;
    data['planGroupName'] = this.planGroupName;
    data['isRequiredApproval'] = this.isRequiredApproval;
    data['isFree'] = this.isFree;
    data['itemAssemblyflag'] = this.itemAssemblyflag;
    data['itemAssemblyId'] = this.itemAssemblyId;
    data['billabecustId'] = this.billabecustId;
    data['parentCustomerId'] = this.parentCustomerId;
    data['productPlanMappingId'] = this.productPlanMappingId;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
    if (this.inOutWardSerialMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardSerialMapping!.map((v) => v.toJson()).toList();
    }
    data['itemAssemblyStatus'] = this.itemAssemblyStatus;
    return data;
  }
}

class InOutWardMACMapping {
  int? id;
  int? inwardId;
  int? outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  Null? custInventoryMappingId;
  String? serialNumber;
  Null? mvnoId;
  Null? currentApproverId;
  Null? previousApproverId;
  Null? teamHierarchyMappingId;
  Null? inwardIdOfOutward;
  int? isForwarded;
  Null? remark;
  Null? externalItemId;
  int? itemId;
  Null? inventoryMappingId;
  Null? bulkConsumptionId;
  Null? itemRemaingDays;
  int? isReturned;
  Null? nonSerializedItemId;
  String? condition;
  String? productName;
  bool? hasMac;
  bool? hasSerial;
  Null? ownerShip;
  int? identityKey;

  InOutWardMACMapping(
      {this.id,
        this.inwardId,
        this.outwardId,
        this.status,
        this.macAddress,
        this.isDeleted,
        this.custInventoryMappingId,
        this.serialNumber,
        this.mvnoId,
        this.currentApproverId,
        this.previousApproverId,
        this.teamHierarchyMappingId,
        this.inwardIdOfOutward,
        this.isForwarded,
        this.remark,
        this.externalItemId,
        this.itemId,
        this.inventoryMappingId,
        this.bulkConsumptionId,
        this.itemRemaingDays,
        this.isReturned,
        this.nonSerializedItemId,
        this.condition,
        this.productName,
        this.hasMac,
        this.hasSerial,
        this.ownerShip,
        this.identityKey});

  InOutWardMACMapping.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    inwardId = json['inwardId'];
    outwardId = json['outwardId'];
    status = json['status'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    currentApproverId = json['currentApproverId'];
    previousApproverId = json['previousApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    inwardIdOfOutward = json['inwardIdOfOutward'];
    isForwarded = json['isForwarded'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    itemRemaingDays = json['itemRemaingDays'];
    isReturned = json['isReturned'];
    nonSerializedItemId = json['nonSerializedItemId'];
    condition = json['condition'];
    productName = json['productName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    ownerShip = json['ownerShip'];
    identityKey = json['identityKey'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['currentApproverId'] = this.currentApproverId;
    data['previousApproverId'] = this.previousApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['inwardIdOfOutward'] = this.inwardIdOfOutward;
    data['isForwarded'] = this.isForwarded;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['itemRemaingDays'] = this.itemRemaingDays;
    data['isReturned'] = this.isReturned;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['condition'] = this.condition;
    data['productName'] = this.productName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['ownerShip'] = this.ownerShip;
    data['identityKey'] = this.identityKey;
    return data;
  }
}
