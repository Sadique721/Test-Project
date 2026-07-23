import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';

class AddEditProblemDomainReq {
  int? id;
  String? categoryName;
  String? status;
  String? department;
  int? slaTimeP1;
  int? slaTimeP2;
  int? slaTimeP3;
  String? slaUnitP1;
  String? slaUnitP2;
  String? slaUnitP3;
  Service? service;
  List<TicketReasonCategoryTATMapping>? ticketReasonCategoryTATMappingList;

  AddEditProblemDomainReq(
      {this.id,
      this.categoryName,
      this.status,
      this.department,
      this.slaTimeP1,
      this.slaTimeP2,
      this.slaTimeP3,
      this.slaUnitP1,
      this.slaUnitP2,
      this.slaUnitP3,
      this.service,
      this.ticketReasonCategoryTATMappingList});

  AddEditProblemDomainReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    categoryName = json['categoryName'];
    status = json['status'];
    department = json['department'];
    slaTimeP1 = json['slaTimeP1'];
    slaTimeP2 = json['slaTimeP2'];
    slaTimeP3 = json['slaTimeP3'];
    slaUnitP1 = json['slaUnitP1'];
    slaUnitP2 = json['slaUnitP2'];
    slaUnitP3 = json['slaUnitP3'];
    service =
        json['service'] != null ? new Service.fromJson(json['service']) : null;
    if (json['ticketReasonCategoryTATMappingList'] != null) {
      ticketReasonCategoryTATMappingList = <TicketReasonCategoryTATMapping>[];
      json['ticketReasonCategoryTATMappingList'].forEach((v) {
        ticketReasonCategoryTATMappingList!
            .add(new TicketReasonCategoryTATMapping.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['categoryName'] = this.categoryName;
    data['status'] = this.status;
    data['department'] = this.department;
    data['slaTimeP1'] = this.slaTimeP1;
    data['slaTimeP2'] = this.slaTimeP2;
    data['slaTimeP3'] = this.slaTimeP3;
    data['slaUnitP1'] = this.slaUnitP1;
    data['slaUnitP2'] = this.slaUnitP2;
    data['slaUnitP3'] = this.slaUnitP3;
    if (this.service != null) {
      data['service'] = this.service!.toJson();
    }
    if (this.ticketReasonCategoryTATMappingList != null) {
      data['ticketReasonCategoryTATMappingList'] = this
          .ticketReasonCategoryTATMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    return data;
  }
}
