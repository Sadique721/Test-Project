import 'package:savbill/webservices/base_response.dart';

class IpManagementListRes extends BaseResponse{
  dynamic responseCode;
  String? responseMessage;
  dynamic data;
  List<IpManagementDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  IpManagementListRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  IpManagementListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <IpManagementDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new IpManagementDataList.fromJson(v));
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

class IpManagementDataList {
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  int? poolId;
  String? poolName;
  String? displayName;
  String? poolType;
  String? ipRange;
  String? poolCategory;
  String? netMask;
  String? networkIp;
  String? broadcastIp;
  String? firstHost;
  String? lastHost;
  int? totalHost;
  bool? isDelete;
  bool? isStaticIpPool;
  bool? defaultPoolFlag;
  String? status;
  String? remark;
  int? mvnoId;
  int? displayId;
  String? displayPoolName;

  IpManagementDataList(
      {this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.poolId,
        this.poolName,
        this.displayName,
        this.poolType,
        this.ipRange,
        this.poolCategory,
        this.netMask,
        this.networkIp,
        this.broadcastIp,
        this.firstHost,
        this.lastHost,
        this.totalHost,
        this.isDelete,
        this.isStaticIpPool,
        this.defaultPoolFlag,
        this.status,
        this.remark,
        this.mvnoId,
        this.displayId,
        this.displayPoolName});

  IpManagementDataList.fromJson(Map<String, dynamic> json) {
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    poolId = json['poolId'];
    poolName = json['poolName'];
    displayName = json['displayName'];
    poolType = json['poolType'];
    ipRange = json['ipRange'];
    poolCategory = json['poolCategory'];
    netMask = json['netMask'];
    networkIp = json['networkIp'];
    broadcastIp = json['broadcastIp'];
    firstHost = json['firstHost'];
    lastHost = json['lastHost'];
    totalHost = json['totalHost'];
    isDelete = json['isDelete'];
    isStaticIpPool = json['isStaticIpPool'];
    defaultPoolFlag = json['defaultPoolFlag'];
    status = json['status'];
    remark = json['remark'];
    mvnoId = json['mvnoId'];
    displayId = json['displayId'];
    displayPoolName = json['displayPoolName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['poolId'] = this.poolId;
    data['poolName'] = this.poolName;
    data['displayName'] = this.displayName;
    data['poolType'] = this.poolType;
    data['ipRange'] = this.ipRange;
    data['poolCategory'] = this.poolCategory;
    data['netMask'] = this.netMask;
    data['networkIp'] = this.networkIp;
    data['broadcastIp'] = this.broadcastIp;
    data['firstHost'] = this.firstHost;
    data['lastHost'] = this.lastHost;
    data['totalHost'] = this.totalHost;
    data['isDelete'] = this.isDelete;
    data['isStaticIpPool'] = this.isStaticIpPool;
    data['defaultPoolFlag'] = this.defaultPoolFlag;
    data['status'] = this.status;
    data['remark'] = this.remark;
    data['mvnoId'] = this.mvnoId;
    data['displayId'] = this.displayId;
    data['displayPoolName'] = this.displayPoolName;
    return data;
  }
}
