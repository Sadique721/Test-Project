import 'package:savbill/webservices/base_response.dart';

class TeamListResponse extends BaseResponse {
  List<TeamDetail>? dataList;

  TeamListResponse({responseCode, responseMessage, this.dataList});

  TeamListResponse.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TeamDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TeamDetail.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['responseCode'] = this.responseCode;
    data['responseMessage'] = this.responseMessage;
    if (this.dataList != null) {
      data['dataList'] = this.dataList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class TeamDetail {
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
  int? parentteamid;
  String? parentTeamName;
  int? mvnoId;



  TeamDetail(
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
      this.parentteamid,
      this.parentTeamName,
      this.mvnoId});

  TeamDetail.fromJson(Map<String, dynamic> json) {
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
    parentteamid = json['parentteamid'];
    parentTeamName = json['parentTeamName'];
    mvnoId = json['mvnoId'];
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
    data['parentteamid'] = this.parentteamid;
    data['parentTeamName'] = this.parentTeamName;
    data['mvnoId'] = this.mvnoId;
    return data;
  }
}
