class GetCustomerInventoryHistoryRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<InventoryHistoryDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetCustomerInventoryHistoryRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetCustomerInventoryHistoryRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <InventoryHistoryDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new InventoryHistoryDataList.fromJson(v));
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

class InventoryHistoryDataList {
  int? id;
  String? itemStatus;
  int? customerId;
  dynamic serviceAreaId;
  dynamic popId;
  int? itemId;
  String? event;
  String? startDate;
  String? endDate;
  String? condition;
  String? macAddress;
  String? serialNumber;
  dynamic externalItemGroupNumber;
  dynamic approvalRemark;
  String? postPaidPlanName;
  String? serviceName;
  String? connectionNo;
  dynamic bulkConsumptionId;

  InventoryHistoryDataList(
      {this.id,
        this.itemStatus,
        this.customerId,
        this.serviceAreaId,
        this.popId,
        this.itemId,
        this.event,
        this.startDate,
        this.endDate,
        this.condition,
        this.macAddress,
        this.serialNumber,
        this.externalItemGroupNumber,
        this.approvalRemark,
        this.postPaidPlanName,
        this.serviceName,
        this.connectionNo,
        this.bulkConsumptionId});

  InventoryHistoryDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    itemStatus = json['itemStatus'];
    customerId = json['customerId'];
    serviceAreaId = json['serviceAreaId'];
    popId = json['popId'];
    itemId = json['itemId'];
    event = json['event'];
    startDate = json['startDate'];
    endDate = json['endDate'];
    condition = json['condition'];
    macAddress = json['macAddress'];
    serialNumber = json['serialNumber'];
    externalItemGroupNumber = json['externalItemGroupNumber'];
    approvalRemark = json['approvalRemark'];
    postPaidPlanName = json['postPaidPlanName'];
    serviceName = json['serviceName'];
    connectionNo = json['connectionNo'];
    bulkConsumptionId = json['bulkConsumptionId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['itemStatus'] = this.itemStatus;
    data['customerId'] = this.customerId;
    data['serviceAreaId'] = this.serviceAreaId;
    data['popId'] = this.popId;
    data['itemId'] = this.itemId;
    data['event'] = this.event;
    data['startDate'] = this.startDate;
    data['endDate'] = this.endDate;
    data['condition'] = this.condition;
    data['macAddress'] = this.macAddress;
    data['serialNumber'] = this.serialNumber;
    data['externalItemGroupNumber'] = this.externalItemGroupNumber;
    data['approvalRemark'] = this.approvalRemark;
    data['postPaidPlanName'] = this.postPaidPlanName;
    data['serviceName'] = this.serviceName;
    data['connectionNo'] = this.connectionNo;
    data['bulkConsumptionId'] = this.bulkConsumptionId;
    return data;
  }
}
