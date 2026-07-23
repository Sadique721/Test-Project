import 'package:savbill/webservices/base_response.dart';

class GetAllTeamListRes extends BaseResponse{
  String? responseMessage;
  dynamic data;
  List<AllTeamDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllTeamListRes(
      {
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllTeamListRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <AllTeamDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(new AllTeamDataList.fromJson(v));
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

class AllTeamDataList {
  int? id;
  String? createdate;
  String? updatedate;
  String? createdByName;
  String? lastModifiedByName;
  int? createdById;
  int? lastModifiedById;
  String? name;
  String? status;
  dynamic teamType;
  bool? isDeleted;
  int? partnerid;
  dynamic partnername;
  dynamic lcoId;
  List<int>? staffUserIds;
  List<String>? staffNameList;
  dynamic parentteamid;
  String? parentTeamName;
  String? product;
  int? mvnoId;
  int? displayId;
  String? displayName;

  AllTeamDataList(
      {this.id,
        this.createdate,
        this.updatedate,
        this.createdByName,
        this.lastModifiedByName,
        this.createdById,
        this.lastModifiedById,
        this.name,
        this.status,
        this.teamType,
        this.isDeleted,
        this.partnerid,
        this.partnername,
        this.lcoId,
        this.staffUserIds,
        this.staffNameList,
        this.parentteamid,
        this.parentTeamName,
        this.product,
        this.mvnoId,
        this.displayId,
        this.displayName});

  AllTeamDataList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    createdate = json['createdate'];
    updatedate = json['updatedate'];
    createdByName = json['createdByName'];
    lastModifiedByName = json['lastModifiedByName'];
    createdById = json['createdById'];
    lastModifiedById = json['lastModifiedById'];
    name = json['name'];
    status = json['status'];
    teamType = json['teamType'];
    isDeleted = json['isDeleted'];
    partnerid = json['partnerid'];
    partnername = json['partnername'];
    lcoId = json['lcoId'];
    staffUserIds = json['staffUserIds'].cast<int>();
    staffNameList = json['staffNameList'].cast<String>();
    parentteamid = json['parentteamid'];
    parentTeamName = json['parentTeamName'];
    product = json['product'];
    mvnoId = json['mvnoId'];
    displayId = json['displayId'];
    displayName = json['displayName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['createdate'] = this.createdate;
    data['updatedate'] = this.updatedate;
    data['createdByName'] = this.createdByName;
    data['lastModifiedByName'] = this.lastModifiedByName;
    data['createdById'] = this.createdById;
    data['lastModifiedById'] = this.lastModifiedById;
    data['name'] = this.name;
    data['status'] = this.status;
    data['teamType'] = this.teamType;
    data['isDeleted'] = this.isDeleted;
    data['partnerid'] = this.partnerid;
    data['partnername'] = this.partnername;
    data['lcoId'] = this.lcoId;
    data['staffUserIds'] = this.staffUserIds;
    data['staffNameList'] = this.staffNameList;
    data['parentteamid'] = this.parentteamid;
    data['parentTeamName'] = this.parentTeamName;
    data['product'] = this.product;
    data['mvnoId'] = this.mvnoId;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
