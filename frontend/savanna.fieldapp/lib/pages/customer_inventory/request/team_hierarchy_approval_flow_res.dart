class TeamHierarchyApprovalFlowRes {
  int? responseCode;
  String? responseMessage;
  dynamic data;
  List<TeamHierarchyDataList>? dataList;
  dynamic excelDataList;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;

  TeamHierarchyApprovalFlowRes(
      {this.responseCode,
        this.responseMessage,
        this.data,
        this.dataList,
        this.excelDataList,
        this.totalRecords,
        this.pageRecords,
        this.currentPageNumber,
        this.totalPages});

  TeamHierarchyApprovalFlowRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    data = json['data'];
    if (json['dataList'] != null) {
      dataList = <TeamHierarchyDataList>[];
      json['dataList'].forEach((v) {
        dataList!.add(TeamHierarchyDataList.fromJson(v));
      });
    }
    excelDataList = json['excelDataList'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['responseCode'] = responseCode;
    data['responseMessage'] = responseMessage;
    data['data'] = this.data;
    if (dataList != null) {
      data['dataList'] = dataList!.map((v) => v.toJson()).toList();
    }
    data['excelDataList'] = excelDataList;
    data['totalRecords'] = totalRecords;
    data['pageRecords'] = pageRecords;
    data['currentPageNumber'] = currentPageNumber;
    data['totalPages'] = totalPages;
    return data;
  }
}

class TeamHierarchyDataList {
  int? teamsId;
  String? status;
  int? parentTeamsId;
  String? teamName;

  TeamHierarchyDataList({this.teamsId, this.status, this.parentTeamsId, this.teamName});

  TeamHierarchyDataList.fromJson(Map<String, dynamic> json) {
    teamsId = json['teamsId'];
    status = json['status'];
    parentTeamsId = json['parentTeamsId'];
    teamName = json['teamName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['teamsId'] = teamsId;
    data['status'] = status;
    data['parentTeamsId'] = parentTeamsId;
    data['teamName'] = teamName;
    return data;
  }
}
