class GetAllTeamBasedInventoryRes {
  int? responseCode;
  String? responseMessage;
  Null? data;
  List<AllTeamDataList>? dataList;
  Null? excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllTeamBasedInventoryRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  GetAllTeamBasedInventoryRes.fromJson(Map<String, dynamic> json) {
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
  bool? isDeleted;
  int? partnerid;
  String? partnername;
  Null? lcoId;
  List<int>? staffUserIds;
  List<String>? staffNameList;
  Null? parentteamid;
  String? parentTeamName;
  int? mvnoId;
  int? displayId;
  String? displayName;
  bool? selected;

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
        this.isDeleted,
        this.partnerid,
        this.partnername,
        this.lcoId,
        this.staffUserIds,
        this.staffNameList,
        this.parentteamid,
        this.parentTeamName,
        this.mvnoId,
        this.displayId,
        this.displayName,
      this.selected});

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
    isDeleted = json['isDeleted'];
    partnerid = json['partnerid'];
    partnername = json['partnername'];
    lcoId = json['lcoId'];
    staffUserIds = json['staffUserIds'].cast<int>();
    staffNameList = json['staffNameList'].cast<String>();
    parentteamid = json['parentteamid'];
    parentTeamName = json['parentTeamName'];
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
    data['isDeleted'] = this.isDeleted;
    data['partnerid'] = this.partnerid;
    data['partnername'] = this.partnername;
    data['lcoId'] = this.lcoId;
    data['staffUserIds'] = this.staffUserIds;
    data['staffNameList'] = this.staffNameList;
    data['parentteamid'] = this.parentteamid;
    data['parentTeamName'] = this.parentTeamName;
    data['mvnoId'] = this.mvnoId;
    data['displayId'] = this.displayId;
    data['displayName'] = this.displayName;
    return data;
  }
}
