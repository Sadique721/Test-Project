import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';

class AddEditRootCauseReq {
  int? id;
  String? name;
  String? status;
  List<RootCauseResolutionMapping>? rootCauseResolutionMappingList;
  List<ResoSubCategoryMappingList>? rootCauseSubProblemList;

  AddEditRootCauseReq(
      {this.name, this.status, this.rootCauseResolutionMappingList, this.id,this.rootCauseSubProblemList});

  AddEditRootCauseReq.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    name = json['name'];
    status = json['status'];
    if (json['rootCauseResolutionMappingList'] != null) {
      rootCauseResolutionMappingList = <RootCauseResolutionMapping>[];
      json['rootCauseResolutionMappingList'].forEach((v) {
        rootCauseResolutionMappingList!
            .add(RootCauseResolutionMapping.fromJson(v));
      });
    }

    if (json['resoSubCategoryMappingList'] != null) {
      rootCauseSubProblemList = <ResoSubCategoryMappingList>[];
      json['resoSubCategoryMappingList'].forEach((v) {
        rootCauseSubProblemList!
            .add(ResoSubCategoryMappingList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['name'] = this.name;
    data['status'] = this.status;
    if (this.rootCauseResolutionMappingList != null) {
      data['rootCauseResolutionMappingList'] =
          this.rootCauseResolutionMappingList!.map((v) => v.toJson()).toList();
    }
    if (this.rootCauseSubProblemList != null) {
      data['resoSubCategoryMappingList'] =
          this.rootCauseSubProblemList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
