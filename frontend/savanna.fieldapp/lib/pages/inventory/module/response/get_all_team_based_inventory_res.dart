class GetAllTeamBasedInventoryRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<AllTeamDataList> dataList;   // Always non-null
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  GetAllTeamBasedInventoryRes({
    this.responseCode,
    this.responseMessage,
    this.data,
    List<AllTeamDataList>? dataList,
    this.excelDataList,
    this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages,
  }) : dataList = dataList ?? [];

  GetAllTeamBasedInventoryRes.fromJson(Map<String, dynamic> json)
      : responseCode = json['responseCode'],
        responseMessage = json['responseMessage'],
        data = json['data'],
        dataList = (json['dataList'] as List?)
            ?.map((v) => AllTeamDataList.fromJson(v))
            .toList() ??
            [],
        excelDataList = json['excelDataList'],
        totalRecords = json['totalRecords'],
        pageRecords = json['pageRecords'],
        currentPageNumber = json['currentPageNumber'],
        totalPages = json['totalPages'];

  Map<String, dynamic> toJson() {
    return {
      'responseCode': responseCode,
      'responseMessage': responseMessage,
      'data': data,
      'dataList': dataList.map((v) => v.toJson()).toList(),
      'excelDataList': excelDataList,
      'totalRecords': totalRecords,
      'pageRecords': pageRecords,
      'currentPageNumber': currentPageNumber,
      'totalPages': totalPages,
    };
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
  dynamic lcoId;
  List<int> staffUserIds;       // Always non-null
  List<String> staffNameList;   // Always non-null
  dynamic parentteamid;
  String? parentTeamName;
  int? mvnoId;
  int? displayId;
  String? displayName;
  bool? selected;

  AllTeamDataList({
    this.id,
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
    List<int>? staffUserIds,
    List<String>? staffNameList,
    this.parentteamid,
    this.parentTeamName,
    this.mvnoId,
    this.displayId,
    this.displayName,
    this.selected,
  })  : staffUserIds = staffUserIds ?? [],
        staffNameList = staffNameList ?? [];

  AllTeamDataList.fromJson(Map<String, dynamic> json)
      : id = json['id'],
        createdate = json['createdate'],
        updatedate = json['updatedate'],
        createdByName = json['createdByName'],
        lastModifiedByName = json['lastModifiedByName'],
        createdById = json['createdById'],
        lastModifiedById = json['lastModifiedById'],
        name = json['name'],
        status = json['status'],
        isDeleted = json['isDeleted'],
        partnerid = json['partnerid'],
        partnername = json['partnername'],
        lcoId = json['lcoId'],
        staffUserIds = (json['staffUserIds'] as List?)
            ?.map((e) => e as int)
            .toList() ??
            [],
        staffNameList = (json['staffNameList'] as List?)
            ?.map((e) => e as String)
            .toList() ??
            [],
        parentteamid = json['parentteamid'],
        parentTeamName = json['parentTeamName'],
        mvnoId = json['mvnoId'],
        displayId = json['displayId'],
        displayName = json['displayName'],
        selected = json['selected'];

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'createdate': createdate,
      'updatedate': updatedate,
      'createdByName': createdByName,
      'lastModifiedByName': lastModifiedByName,
      'createdById': createdById,
      'lastModifiedById': lastModifiedById,
      'name': name,
      'status': status,
      'isDeleted': isDeleted,
      'partnerid': partnerid,
      'partnername': partnername,
      'lcoId': lcoId,
      'staffUserIds': staffUserIds,
      'staffNameList': staffNameList,
      'parentteamid': parentteamid,
      'parentTeamName': parentTeamName,
      'mvnoId': mvnoId,
      'displayId': displayId,
      'displayName': displayName,
      'selected': selected,
    };
  }
}
