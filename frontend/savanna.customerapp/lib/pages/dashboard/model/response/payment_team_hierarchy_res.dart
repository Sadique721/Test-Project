import 'package:savbill/webservices/base_response.dart';

class PaymentTeamHierarchyRes extends BaseResponse {
  List<TeamHierarchyDetail>? dataList;

  PaymentTeamHierarchyRes({responseCode, responseMessage, this.dataList});

  PaymentTeamHierarchyRes.fromJson(Map<String, dynamic> json) {
    responseCode = json['responseCode'];
    responseMessage = json['responseMessage'];
    if (json['dataList'] != null) {
      dataList = <TeamHierarchyDetail>[];
      json['dataList'].forEach((v) {
        dataList!.add(new TeamHierarchyDetail.fromJson(v));
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

class TeamHierarchyDetail {
  int? teamsId;
  String? status;
  int? parentTeamsId;
  String? teamName;

  TeamHierarchyDetail(
      {this.teamsId, this.status, this.parentTeamsId, this.teamName});

  TeamHierarchyDetail.fromJson(Map<String, dynamic> json) {
    teamsId = json['teamsId'];
    status = json['status'];
    parentTeamsId = json['parentTeamsId'];
    teamName = json['teamName'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['teamsId'] = this.teamsId;
    data['status'] = this.status;
    data['parentTeamsId'] = this.parentTeamsId;
    data['teamName'] = this.teamName;
    return data;
  }
}
