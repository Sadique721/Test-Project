import 'package:savbill/webservices/base_response.dart';

class LeadAuditTrialRes extends BaseResponse {
  List<LeadAuditList>? leadAuditList;
  String? timestamp;
  int? totalRecords;
  int? pageRecords;
  int? currentPageNumber;
  int? totalPages;
  int? status;

  LeadAuditTrialRes({this.leadAuditList, this.timestamp, this.status,this.totalRecords,
    this.pageRecords,
    this.currentPageNumber,
    this.totalPages});

  LeadAuditTrialRes.fromJson(Map<String, dynamic> json) {
    if (json['leadAuditList'] != null) {
      leadAuditList = <LeadAuditList>[];
      json['leadAuditList'].forEach((v) {
        leadAuditList!.add(new LeadAuditList.fromJson(v));
      });
    }
    timestamp = json['timestamp'];
    totalRecords = json['totalRecords'];
    pageRecords = json['pageRecords'];
    currentPageNumber = json['currentPageNumber'];
    totalPages = json['totalPages'];
    status = json['status'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.leadAuditList != null) {
      data['leadAuditList'] =
          this.leadAuditList!.map((v) => v.toJson()).toList();
    }
    data['timestamp'] = this.timestamp;
    data['totalRecords'] = this.totalRecords;
    data['pageRecords'] = this.pageRecords;
    data['currentPageNumber'] = this.currentPageNumber;
    data['totalPages'] = this.totalPages;
    data['status'] = this.status;
    return data;
  }
}

class LeadAuditList {
  int? id;
  String? auditName;
  String? name;
  String? staffName;
  String? createdOn;
  int? leadMasterId;

  LeadAuditList(
      {this.id,
        this.auditName,
        this.name,
        this.staffName,
        this.createdOn,
        this.leadMasterId});

  LeadAuditList.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    auditName = json['auditName'];
    name = json['name'];
    staffName = json['staffName'];
    createdOn = json['createdOn'];
    leadMasterId = json['leadMasterId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['auditName'] = this.auditName;
    data['name'] = this.name;
    data['staffName'] = this.staffName;
    data['createdOn'] = this.createdOn;
    data['leadMasterId'] = this.leadMasterId;
    return data;
  }
}
