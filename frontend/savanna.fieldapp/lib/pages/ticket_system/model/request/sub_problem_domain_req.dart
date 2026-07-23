
import '../response/sub_problem_domain_list_res.dart';

class SubProblemDomainListReq {
  int? id;
  String? subCategoryName;
  String? status;
  ParentCategory? parentCategory;
  List<TicketSubCategoryGroupReasonMappingList>?ticketSubCategoryGroupReasonMappingList;
  List<TicketSubCategoryTatMappingList>? ticketSubCategoryTatMappingList;
  List<TicketSubCategoryReasonCategoryMappingList>? ticketSubCategoryReasonCategoryMappingList;


  SubProblemDomainListReq(
      {this.subCategoryName,
        this.status,
        this.parentCategory,
        this.id,
        this.ticketSubCategoryGroupReasonMappingList,
        this.ticketSubCategoryTatMappingList,
      this.ticketSubCategoryReasonCategoryMappingList});

  SubProblemDomainListReq.fromJson(Map<String, dynamic> json) {
    subCategoryName = json['subCategoryName'];
    status = json['status'];
    parentCategory = json['parentCategory'] != null
        ? ParentCategory.fromJson(json['parentCategory'])
        : null;
    id = json['id'];
    if (json['ticketSubCategoryGroupReasonMappingList'] != null) {
      ticketSubCategoryGroupReasonMappingList =
      <TicketSubCategoryGroupReasonMappingList>[];
      json['ticketSubCategoryGroupReasonMappingList'].forEach((v) {
        ticketSubCategoryGroupReasonMappingList!
            .add(TicketSubCategoryGroupReasonMappingList.fromJson(v));
      });
    }
    if (json['ticketSubCategoryTatMappingList'] != null) {
      ticketSubCategoryTatMappingList = <TicketSubCategoryTatMappingList>[];
      json['ticketSubCategoryTatMappingList'].forEach((v) {
        ticketSubCategoryTatMappingList!
            .add(TicketSubCategoryTatMappingList.fromJson(v));
      });
    }
    if (json['ticketSubCategoryReasonCategoryMappingList'] != null) {
      ticketSubCategoryReasonCategoryMappingList = <TicketSubCategoryReasonCategoryMappingList>[];
      json['ticketSubCategoryReasonCategoryMappingList'].forEach((v) {
        ticketSubCategoryReasonCategoryMappingList!
            .add(TicketSubCategoryReasonCategoryMappingList.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['subCategoryName'] = subCategoryName;
    data['status'] = status;
    if (parentCategory != null) {
      data['parentCategory'] = parentCategory!.toJson();
    }
    data['id'] = id;
    if (ticketSubCategoryGroupReasonMappingList != null) {
      data['ticketSubCategoryGroupReasonMappingList'] = ticketSubCategoryGroupReasonMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    if (ticketSubCategoryTatMappingList != null) {
      data['ticketSubCategoryTatMappingList'] =
          ticketSubCategoryTatMappingList!.map((v) => v.toJson()).toList();
    }
    if (ticketSubCategoryReasonCategoryMappingList != null) {
      data['ticketSubCategoryReasonCategoryMappingList'] =
          ticketSubCategoryReasonCategoryMappingList!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class ParentCategory {
  int? id;

  ParentCategory({this.id});

  ParentCategory.fromJson(Map<String, dynamic> json) {
    id = json['id'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = Map<String, dynamic>();
    data['id'] = id;
    return data;
  }
}
