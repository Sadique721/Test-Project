// class GetApproveInventoryRequestRes {
//   dynamic responseCode;
//   String? responseMessage;
//   ApproveInventoryData? data;
//   List<int>? dataList;
//   dynamic excelDataList;
//   dynamic totalRecords;
//   dynamic pageRecords;
//   dynamic currentPageNumber;
//   dynamic totalPages;
//
//   GetApproveInventoryRequestRes(
//       {this.responseCode,
//         this.responseMessage,
//         this.data,
//         this.dataList,
//         this.excelDataList,
//         this.totalRecords,
//         this.pageRecords,
//         this.currentPageNumber,
//         this.totalPages});
//
//   GetApproveInventoryRequestRes.fromJson(Map<String, dynamic> json) {
//     responseCode = json['responseCode'];
//     responseMessage = json['responseMessage'];
//     data = json['data'] != null ? ApproveInventoryData.fromJson(json['data']) : null;
//     dataList = json['dataList'].cast<int>();
//     excelDataList = json['excelDataList'];
//     totalRecords = json['totalRecords'];
//     pageRecords = json['pageRecords'];
//     currentPageNumber = json['currentPageNumber'];
//     totalPages = json['totalPages'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['responseCode'] = this.responseCode;
//     data['responseMessage'] = this.responseMessage;
//     if (this.data != null) {
//       data['data'] = this.data!.toJson();
//     }
//     data['dataList'] = this.dataList;
//     data['excelDataList'] = this.excelDataList;
//     data['totalRecords'] = this.totalRecords;
//     data['pageRecords'] = this.pageRecords;
//     data['currentPageNumber'] = this.currentPageNumber;
//     data['totalPages'] = this.totalPages;
//     return data;
//   }
// }
//
// class ApproveInventoryData {
//   dynamic id;
//   dynamic qty;
//   dynamic productId;
//   dynamic customerId;
//   dynamic staffId;
//   dynamic inwardId;
//   String? assignedDateTime;
//   bool? isDeleted;
//   dynamic mvnoId;
//   String? status;
//   String? expiryDateTime;
//   dynamic inwardNumber;
//   dynamic externalItemNumber;
//   dynamic externalItemId;
//   String? productName;
//   String? customerName;
//   bool? hasMac;
//   bool? hasSerial;
//   bool? hasTrackable;
//   bool? hasPort;
//   bool? hasCas;
//   dynamic nextApproverId;
//   dynamic teamHierarchyMappingId;
//   String? assigneeName;
//   dynamic previousApproveId;
//   dynamic serviceId;
//   dynamic custPackId;
//   dynamic itemId;
//  dynamic serviceName;
//  dynamic currentPlan;
//  dynamic itemType;
//  dynamic warranty;
//  dynamic itemAssemblyflag;
//  dynamic itemAssemblyStatus;
//  dynamic itemAssemblyName;
//  dynamic itemAssemblyId;
//  dynamic itemAssembly;
//   String? connectionNo;
//   bool? isInvoiceCreated;
//   dynamic planId;
//   dynamic mappingRefId;
//   dynamic approvalRemark;
//   dynamic customerFirstName;
//   dynamic customerLastName;
//   dynamic itemwarranty;
//   dynamic expDate;
//   dynamic serviceAreaName;
//   dynamic dtvCategory;
//   dynamic flag;
//   dynamic identityKey;
//
//   ApproveInventoryData(
//       {this.id,
//         this.qty,
//         this.productId,
//         this.customerId,
//         this.staffId,
//         this.inwardId,
//         this.assignedDateTime,
//         this.isDeleted,
//         this.mvnoId,
//         this.status,
//         this.expiryDateTime,
//         this.inwardNumber,
//         this.externalItemNumber,
//         this.externalItemId,
//         this.productName,
//         this.customerName,
//         this.hasMac,
//         this.hasSerial,
//         this.hasTrackable,
//         this.hasPort,
//         this.hasCas,
//         this.nextApproverId,
//         this.teamHierarchyMappingId,
//         this.assigneeName,
//         this.previousApproveId,
//         this.serviceId,
//         this.custPackId,
//         this.itemId,
//         this.serviceName,
//         this.currentPlan,
//         this.itemType,
//         this.warranty,
//         this.itemAssemblyflag,
//         this.itemAssemblyStatus,
//         this.itemAssemblyName,
//         this.itemAssemblyId,
//         this.itemAssembly,
//         this.connectionNo,
//         this.isInvoiceCreated,
//         this.planId,
//         this.mappingRefId,
//         this.approvalRemark,
//         this.customerFirstName,
//         this.customerLastName,
//         this.itemwarranty,
//         this.expDate,
//         this.serviceAreaName,
//         this.dtvCategory,
//         this.flag,
//         this.identityKey});
//
//   ApproveInventoryData.fromJson(Map<String, dynamic> json) {
//     id = json['id'];
//     qty = json['qty'];
//     productId = json['productId'];
//     customerId = json['customerId'];
//     staffId = json['staffId'];
//     inwardId = json['inwardId'];
//     assignedDateTime = json['assignedDateTime'];
//     isDeleted = json['isDeleted'];
//     mvnoId = json['mvnoId'];
//     status = json['status'];
//     expiryDateTime = json['expiryDateTime'];
//     inwardNumber = json['inwardNumber'];
//     externalItemNumber = json['externalItemNumber'];
//     externalItemId = json['externalItemId'];
//     productName = json['productName'];
//     customerName = json['customerName'];
//     hasMac = json['hasMac'];
//     hasSerial = json['hasSerial'];
//     hasTrackable = json['hasTrackable'];
//     hasPort = json['hasPort'];
//     hasCas = json['hasCas'];
//     nextApproverId = json['nextApproverId'];
//     teamHierarchyMappingId = json['teamHierarchyMappingId'];
//     assigneeName = json['assigneeName'];
//
//     previousApproveId = json['previousApproveId'];
//     serviceId = json['serviceId'];
//     custPackId = json['custPackId'];
//     itemId = json['itemId'];
//     serviceName = json['serviceName'];
//     currentPlan = json['currentPlan'];
//     itemType = json['itemType'];
//     warranty = json['warranty'];
//     itemAssemblyflag = json['itemAssemblyflag'];
//     itemAssemblyStatus = json['itemAssemblyStatus'];
//     itemAssemblyName = json['itemAssemblyName'];
//     itemAssemblyId = json['itemAssemblyId'];
//     itemAssembly = json['itemAssembly'];
//     connectionNo = json['connectionNo'];
//     isInvoiceCreated = json['isInvoiceCreated'];
//     planId = json['planId'];
//     mappingRefId = json['mapping_ref_id'];
//     approvalRemark = json['approvalRemark'];
//     customerFirstName = json['customerFirstName'];
//     customerLastName = json['customerLastName'];
//     itemwarranty = json['itemwarranty'];
//     expDate = json['expDate'];
//     serviceAreaName = json['serviceAreaName'];
//     dtvCategory = json['dtvCategory'];
//     flag = json['flag'];
//     identityKey = json['identityKey'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['id'] = this.id;
//     data['qty'] = this.qty;
//     data['productId'] = this.productId;
//     data['customerId'] = this.customerId;
//     data['staffId'] = this.staffId;
//     data['inwardId'] = this.inwardId;
//     data['assignedDateTime'] = this.assignedDateTime;
//     data['isDeleted'] = this.isDeleted;
//     data['mvnoId'] = this.mvnoId;
//     data['status'] = this.status;
//     data['expiryDateTime'] = this.expiryDateTime;
//     data['inwardNumber'] = this.inwardNumber;
//     data['externalItemNumber'] = this.externalItemNumber;
//     data['externalItemId'] = this.externalItemId;
//     data['productName'] = this.productName;
//     data['customerName'] = this.customerName;
//     data['hasMac'] = this.hasMac;
//     data['hasSerial'] = this.hasSerial;
//     data['hasTrackable'] = this.hasTrackable;
//     data['hasPort'] = this.hasPort;
//     data['hasCas'] = this.hasCas;
//     data['nextApproverId'] = this.nextApproverId;
//     data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
//     data['assigneeName'] = this.assigneeName;
//     data['previousApproveId'] = this.previousApproveId;
//     data['serviceId'] = this.serviceId;
//     data['custPackId'] = this.custPackId;
//     data['itemId'] = this.itemId;
//     data['serviceName'] = this.serviceName;
//     data['currentPlan'] = this.currentPlan;
//     data['itemType'] = this.itemType;
//     data['warranty'] = this.warranty;
//     data['itemAssemblyflag'] = this.itemAssemblyflag;
//     data['itemAssemblyStatus'] = this.itemAssemblyStatus;
//     data['itemAssemblyName'] = this.itemAssemblyName;
//     data['itemAssemblyId'] = this.itemAssemblyId;
//     data['itemAssembly'] = this.itemAssembly;
//     data['connectionNo'] = this.connectionNo;
//     data['isInvoiceCreated'] = this.isInvoiceCreated;
//     data['planId'] = this.planId;
//     data['mapping_ref_id'] = this.mappingRefId;
//     data['approvalRemark'] = this.approvalRemark;
//     data['customerFirstName'] = this.customerFirstName;
//     data['customerLastName'] = this.customerLastName;
//     data['itemwarranty'] = this.itemwarranty;
//     data['expDate'] = this.expDate;
//     data['serviceAreaName'] = this.serviceAreaName;
//     data['dtvCategory'] = this.dtvCategory;
//     data['flag'] = this.flag;
//     data['identityKey'] = this.identityKey;
//     return data;
//   }
// }


class GetApproveInventoryRequestRes {
  dynamic responseCode;
  String? responseMessage;
  ApproveInventoryData? data;
  dynamic dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  GetApproveInventoryRequestRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetApproveInventoryRequestRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'] != null ? new ApproveInventoryData.fromJson(json['data']) : null;
    dataList = json['dataList'];
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
    if (this.data != null) {
      data['data'] = this.data!.toJson();
    }
    data['dataList'] = this.dataList;
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class ApproveInventoryData {
  dynamic id;
  dynamic qty;
  dynamic productId;
  dynamic customerId;
  dynamic staffId;
  dynamic inwardId;
  String? assignedDateTime;
  bool? isDeleted;
  dynamic mvnoId;
  String? status;
  String? expiryDateTime;
  dynamic inwardNumber;
  dynamic externalItemNumber;
  dynamic externalItemId;
  String? productName;
  String? customerName;
  bool? hasMac;
  bool? hasSerial;
  bool? hasTrackable;
  bool? hasPort;
  bool? hasCas;
  dynamic nextApproverId;
  dynamic teamHierarchyMappingId;
  String? assigneeName;
  List<InOutWardMACMapping>? inOutWardMACMapping;
  dynamic previousApproveId;
  dynamic serviceId;
  dynamic custPackId;
  dynamic itemId;
  dynamic  serviceName;
  dynamic  currentPlan;
  dynamic  itemType;
  dynamic  warranty;
  dynamic  itemAssemblyflag;
  dynamic  itemAssemblyStatus;
  dynamic  itemAssemblyName;
  dynamic  itemAssemblyId;
  dynamic  itemAssembly;
  String? connectionNo;
  bool? isInvoiceCreated;
  dynamic planId;
  dynamic mappingRefId;
  dynamic approvalRemark;
  dynamic customerFirstName;
  dynamic customerLastName;
  dynamic itemwarranty;
  dynamic expDate;
  dynamic serviceAreaName;
  dynamic dtvCategory;
  dynamic flag;
  dynamic discount;
  String? billTo;
  double? newAmount;
  dynamic offerPrice;
  bool? isInvoiceToOrg;
  bool? isRequiredApproval;
  dynamic chargeId;
  dynamic planGroupId;
  dynamic isFree;
  dynamic paymentOwnerId;
  dynamic ezyBillStockId;
  dynamic billabecustId;
  dynamic replacementReason;
  dynamic revisedCharge;
  dynamic removeRequestStatus;
  dynamic pairStatus;
  dynamic productType;
  dynamic productCategoryName;
  dynamic productPlanMappingId;
  dynamic nonSerializedItemRemark;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic identityKey;
  bool? generateRemoveRequest;

  ApproveInventoryData(
      {this.id,
        this.qty,
        this.productId,
        this.customerId,
        this.staffId,
        this.inwardId,
        this.assignedDateTime,
        this.isDeleted,
        this.mvnoId,
        this.status,
        this.expiryDateTime,
        this.inwardNumber,
        this.externalItemNumber,
        this.externalItemId,
        this.productName,
        this.customerName,
        this.hasMac,
        this.hasSerial,
        this.hasTrackable,
        this.hasPort,
        this.hasCas,
        this.nextApproverId,
        this.teamHierarchyMappingId,
        this.assigneeName,
        this.inOutWardMACMapping,
        this.previousApproveId,
        this.serviceId,
        this.custPackId,
        this.itemId,
        this.serviceName,
        this.currentPlan,
        this.itemType,
        this.warranty,
        this.itemAssemblyflag,
        this.itemAssemblyStatus,
        this.itemAssemblyName,
        this.itemAssemblyId,
        this.itemAssembly,
        this.connectionNo,
        this.isInvoiceCreated,
        this.planId,
        this.mappingRefId,
        this.approvalRemark,
        this.customerFirstName,
        this.customerLastName,
        this.itemwarranty,
        this.expDate,
        this.serviceAreaName,
        this.dtvCategory,
        this.flag,
        this.discount,
        this.billTo,
        this.newAmount,
        this.offerPrice,
        this.isInvoiceToOrg,
        this.isRequiredApproval,
        this.chargeId,
        this.planGroupId,
        this.isFree,
        this.paymentOwnerId,
        this.ezyBillStockId,
        this.billabecustId,
        this.replacementReason,
        this.revisedCharge,
        this.removeRequestStatus,
        this.pairStatus,
        this.productType,
        this.productCategoryName,
        this.productPlanMappingId,
        this.nonSerializedItemRemark,
        this.createdById,
        this.lastModifiedById,
        this.identityKey,
        this.generateRemoveRequest});

  ApproveInventoryData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    qty = json['qty'];
    productId = json['productId'];
    customerId = json['customerId'];
    staffId = json['staffId'];
    inwardId = json['inwardId'];
    assignedDateTime = json['assignedDateTime'];
    isDeleted = json['isDeleted'];
    mvnoId = json['mvnoId'];
    status = json['status'];
    expiryDateTime = json['expiryDateTime'];
    inwardNumber = json['inwardNumber'];
    externalItemNumber = json['externalItemNumber'];
    externalItemId = json['externalItemId'];
    productName = json['productName'];
    customerName = json['customerName'];
    hasMac = json['hasMac'];
    hasSerial = json['hasSerial'];
    hasTrackable = json['hasTrackable'];
    hasPort = json['hasPort'];
    hasCas = json['hasCas'];
    nextApproverId = json['nextApproverId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    assigneeName = json['assigneeName'];
    if (json['inOutWardMACMapping'] != null) {
      inOutWardMACMapping = <InOutWardMACMapping>[];
      json['inOutWardMACMapping'].forEach((v) {
        inOutWardMACMapping!.add(new InOutWardMACMapping.fromJson(v));
      });
    }
    previousApproveId = json['previousApproveId'];
    serviceId = json['serviceId'];
    custPackId = json['custPackId'];
    itemId = json['itemId'];
    serviceName = json['serviceName'];
    currentPlan = json['currentPlan'];
    itemType = json['itemType'];
    warranty = json['warranty'];
    itemAssemblyflag = json['itemAssemblyflag'];
    itemAssemblyStatus = json['itemAssemblyStatus'];
    itemAssemblyName = json['itemAssemblyName'];
    itemAssemblyId = json['itemAssemblyId'];
    itemAssembly = json['itemAssembly'];
    connectionNo = json['connectionNo'];
    isInvoiceCreated = json['isInvoiceCreated'];
    planId = json['planId'];
    mappingRefId = json['mapping_ref_id'];
    approvalRemark = json['approvalRemark'];
    customerFirstName = json['customerFirstName'];
    customerLastName = json['customerLastName'];
    itemwarranty = json['itemwarranty'];
    expDate = json['expDate'];
    serviceAreaName = json['serviceAreaName'];
    dtvCategory = json['dtvCategory'];
    flag = json['flag'];
    discount = json['discount'];
    billTo = json['billTo'];
    newAmount = json['newAmount'];
    offerPrice = json['offerPrice'];
    isInvoiceToOrg = json['isInvoiceToOrg'];
    isRequiredApproval = json['isRequiredApproval'];
    chargeId = json['chargeId'];
    planGroupId = json['planGroupId'];
    isFree = json['isFree'];
    paymentOwnerId = json['paymentOwnerId'];
    ezyBillStockId = json['ezyBillStockId'];
    billabecustId = json['billabecustId'];
    replacementReason = json['replacementReason'];
    revisedCharge = json['revisedCharge'];
    removeRequestStatus = json['removeRequestStatus'];
    pairStatus = json['pairStatus'];
    productType = json['productType'];
    productCategoryName = json['productCategoryName'];
    productPlanMappingId = json['productPlanMappingId'];
    nonSerializedItemRemark = json['nonSerializedItemRemark'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    identityKey = json['identityKey'];
    generateRemoveRequest = json['generateRemoveRequest'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['qty'] = this.qty;
    data['productId'] = this.productId;
    data['customerId'] = this.customerId;
    data['staffId'] = this.staffId;
    data['inwardId'] = this.inwardId;
    data['assignedDateTime'] = this.assignedDateTime;
    data['isDeleted'] = this.isDeleted;
    data['mvnoId'] = this.mvnoId;
    data['status'] = this.status;
    data['expiryDateTime'] = this.expiryDateTime;
    data['inwardNumber'] = this.inwardNumber;
    data['externalItemNumber'] = this.externalItemNumber;
    data['externalItemId'] = this.externalItemId;
    data['productName'] = this.productName;
    data['customerName'] = this.customerName;
    data['hasMac'] = this.hasMac;
    data['hasSerial'] = this.hasSerial;
    data['hasTrackable'] = this.hasTrackable;
    data['hasPort'] = this.hasPort;
    data['hasCas'] = this.hasCas;
    data['nextApproverId'] = this.nextApproverId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['assigneeName'] = this.assigneeName;
    if (this.inOutWardMACMapping != null) {
      data['inOutWardMACMapping'] =
          this.inOutWardMACMapping!.map((v) => v.toJson()).toList();
    }
    data['previousApproveId'] = this.previousApproveId;
    data['serviceId'] = this.serviceId;
    data['custPackId'] = this.custPackId;
    data['itemId'] = this.itemId;
    data['serviceName'] = this.serviceName;
    data['currentPlan'] = this.currentPlan;
    data['itemType'] = this.itemType;
    data['warranty'] = this.warranty;
    data['itemAssemblyflag'] = this.itemAssemblyflag;
    data['itemAssemblyStatus'] = this.itemAssemblyStatus;
    data['itemAssemblyName'] = this.itemAssemblyName;
    data['itemAssemblyId'] = this.itemAssemblyId;
    data['itemAssembly'] = this.itemAssembly;
    data['connectionNo'] = this.connectionNo;
    data['isInvoiceCreated'] = this.isInvoiceCreated;
    data['planId'] = this.planId;
    data['mapping_ref_id'] = this.mappingRefId;
    data['approvalRemark'] = this.approvalRemark;
    data['customerFirstName'] = this.customerFirstName;
    data['customerLastName'] = this.customerLastName;
    data['itemwarranty'] = this.itemwarranty;
    data['expDate'] = this.expDate;
    data['serviceAreaName'] = this.serviceAreaName;
    data['dtvCategory'] = this.dtvCategory;
    data['flag'] = this.flag;
    data['discount'] = this.discount;
    data['billTo'] = this.billTo;
    data['newAmount'] = this.newAmount;
    data['offerPrice'] = this.offerPrice;
    data['isInvoiceToOrg'] = this.isInvoiceToOrg;
    data['isRequiredApproval'] = this.isRequiredApproval;
    data['chargeId'] = this.chargeId;
    data['planGroupId'] = this.planGroupId;
    data['isFree'] = this.isFree;
    data['paymentOwnerId'] = this.paymentOwnerId;
    data['ezyBillStockId'] = this.ezyBillStockId;
    data['billabecustId'] = this.billabecustId;
    data['replacementReason'] = this.replacementReason;
    data['revisedCharge'] = this.revisedCharge;
    data['removeRequestStatus'] = this.removeRequestStatus;
    data['pairStatus'] = this.pairStatus;
    data['productType'] = this.productType;
    data['productCategoryName'] = this.productCategoryName;
    data['productPlanMappingId'] = this.productPlanMappingId;
    data['nonSerializedItemRemark'] = this.nonSerializedItemRemark;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['identityKey'] = this.identityKey;
    data['generateRemoveRequest'] = this.generateRemoveRequest;
    return data;
  }
}

class InOutWardMACMapping {
  dynamic createdate;
  String? updatedate;
  dynamic createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic inwardId;
  dynamic outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  dynamic custInventoryMappingId;
  String? serialNumber;
  dynamic mvnoId;
  dynamic currentApproveId;
  dynamic previousApproveId;
  dynamic teamHierarchyMappingId;
  dynamic usedCount;
  dynamic inwardIdOfOutward;
  dynamic isForwarded;
  dynamic isReturned;
  dynamic remark;
  dynamic externalItemId;
  dynamic itemId;
  dynamic inventoryMappingId;
  dynamic bulkConsumptionId;
  dynamic nonSerializedItemId;
  dynamic itemStatus;
  bool? deleteFlag;
  dynamic primaryKey;

  InOutWardMACMapping(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.id,
        this.inwardId,
        this.outwardId,
        this.status,
        this.macAddress,
        this.isDeleted,
        this.custInventoryMappingId,
        this.serialNumber,
        this.mvnoId,
        this.currentApproveId,
        this.previousApproveId,
        this.teamHierarchyMappingId,
        this.usedCount,
        this.inwardIdOfOutward,
        this.isForwarded,
        this.isReturned,
        this.remark,
        this.externalItemId,
        this.itemId,
        this.inventoryMappingId,
        this.bulkConsumptionId,
        this.nonSerializedItemId,
        this.itemStatus,
        this.deleteFlag,
        this.primaryKey});

  InOutWardMACMapping.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    id = json['id'];
    inwardId = json['inwardId'];
    outwardId = json['outwardId'];
    status = json['status'];
    macAddress = json['macAddress'];
    isDeleted = json['isDeleted'];
    custInventoryMappingId = json['custInventoryMappingId'];
    serialNumber = json['serialNumber'];
    mvnoId = json['mvnoId'];
    currentApproveId = json['currentApproveId'];
    previousApproveId = json['previousApproveId'];
    teamHierarchyMappingId = json['teamHierarchyMappingId'];
    usedCount = json['usedCount'];
    inwardIdOfOutward = json['inwardIdOfOutward'];
    isForwarded = json['isForwarded'];
    isReturned = json['isReturned'];
    remark = json['remark'];
    externalItemId = json['externalItemId'];
    itemId = json['itemId'];
    inventoryMappingId = json['inventoryMappingId'];
    bulkConsumptionId = json['bulkConsumptionId'];
    nonSerializedItemId = json['nonSerializedItemId'];
    itemStatus = json['itemStatus'];
    deleteFlag = json['deleteFlag'];
    primaryKey = json['primaryKey'];
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
    data['inwardId'] = this.inwardId;
    data['outwardId'] = this.outwardId;
    data['status'] = this.status;
    data['macAddress'] = this.macAddress;
    data['isDeleted'] = this.isDeleted;
    data['custInventoryMappingId'] = this.custInventoryMappingId;
    data['serialNumber'] = this.serialNumber;
    data['mvnoId'] = this.mvnoId;
    data['currentApproveId'] = this.currentApproveId;
    data['previousApproveId'] = this.previousApproveId;
    data['teamHierarchyMappingId'] = this.teamHierarchyMappingId;
    data['usedCount'] = this.usedCount;
    data['inwardIdOfOutward'] = this.inwardIdOfOutward;
    data['isForwarded'] = this.isForwarded;
    data['isReturned'] = this.isReturned;
    data['remark'] = this.remark;
    data['externalItemId'] = this.externalItemId;
    data['itemId'] = this.itemId;
    data['inventoryMappingId'] = this.inventoryMappingId;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    data['nonSerializedItemId'] = this.nonSerializedItemId;
    data['itemStatus'] = this.itemStatus;
    data['deleteFlag'] = this.deleteFlag;
    data['primaryKey'] = this.primaryKey;
    return data;
  }
}
