class GetAllCustomerInventoryListRes {
  dynamic responseCode;
  dynamic responseMessage;
  dynamic data;
  List<CustomerInventoryDataList>? dataList;
  dynamic excelDataList;
  dynamic totalRecords;
  dynamic pageRecords;
  dynamic currentPageNumber;
  dynamic totalPages;

  GetAllCustomerInventoryListRes(
      {this.responseCode,
      this.responseMessage,
      this.data,
      this.dataList,
      this.excelDataList,
      this.totalRecords,
      this.pageRecords,
      this.currentPageNumber,
      this.totalPages});

  GetAllCustomerInventoryListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <CustomerInventoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new CustomerInventoryDataList.fromJson(v));
      });
    }
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
    data['data'] = this.data;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = this.excelDataList;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    return data;
  }
}

class CustomerInventoryDataList {
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
  String? removeRequestStatus;
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
  dynamic generateRemoveRequest;
  dynamic teamHierarchyMappingId;
  String? assigneeName;
  List<InOutWardMACMapping>? inOutWardMACMapping;
  dynamic previousApproveId;
  dynamic serviceId;
  dynamic custPackId;
  dynamic itemId;
  dynamic custInventoryListId;
  String? serviceName;
  dynamic currentPlan;
  String? itemType;
  String? warranty;
  bool? itemAssemblyflag;
  dynamic itemAssemblyStatus;
  String? itemAssemblyName;
  int? itemAssemblyId;
  dynamic itemAssembly;
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
  dynamic identityKey;
  dynamic filename;
  dynamic uniquename;
  dynamic fileDetails;

  CustomerInventoryDataList({
    this.id,
    this.qty,
    this.productId,
    this.customerId,
    this.staffId,
    this.inwardId,
    this.assignedDateTime,
    this.isDeleted,
    this.mvnoId,
    this.status,
    this.removeRequestStatus,
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
    this.generateRemoveRequest,
    this.teamHierarchyMappingId,
    this.assigneeName,
    this.inOutWardMACMapping,
    this.previousApproveId,
    this.serviceId,
    this.custPackId,
    this.itemId,
    this.custInventoryListId,
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
    this.identityKey,
    this.filename,
    this.uniquename,
    this.fileDetails,
  });

  CustomerInventoryDataList.fromJson(Map<String, dynamic> json) {
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
    removeRequestStatus = json['removeRequestStatus'];
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
    generateRemoveRequest = json['generateRemoveRequest'];
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
    custInventoryListId = json['custInventoryListId'];
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
    identityKey = json['identityKey'];
    filename = json['filename'];
    uniquename = json['uniquename'];
    fileDetails = json['fileDetails'];
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
    data['removeRequestStatus'] = this.removeRequestStatus;
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
    data['generateRemoveRequest'] = this.generateRemoveRequest;
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
    data['custInventoryListId'] = this.custInventoryListId;
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
    data['identityKey'] = this.identityKey;
    data['filename'] = this.filename;
    data['uniquename'] = this.uniquename;
    data['fileDetails'] = this.fileDetails;
    return data;
  }
}

class InOutWardMACMapping {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  dynamic createdById;
  dynamic lastModifiedById;
  dynamic id;
  dynamic inwardId;
  dynamic outwardId;
  String? status;
  String? macAddress;
  bool? isDeleted;
  int? custInventoryMappingId;
  String? serialNumber;
  dynamic currentApproveId;
  dynamic entityId;
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
  dynamic deleteFlag;
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
      this.currentApproveId,
      this.entityId,
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
    currentApproveId = json['currentApproveId'];
    entityId = json['entityId'];
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
    data['currentApproveId'] = this.currentApproveId;
    data['entityId'] = this.entityId;
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
