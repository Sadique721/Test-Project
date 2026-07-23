import 'package:savbill/pages/task_management/model/response/task_sub_category_mgmt_res.dart';

class AddEditTaskSubCategoryReq {
  String? subCategoryName;
  List<CaseSubCategoryCategoryMappingList>? caseSubCategoryCategoryMappingList;
  String? status;
  dynamic subCategoryId;
  String? discription;
  String? mvnoId;
  dynamic buId;
  bool? isDeleted;

  AddEditTaskSubCategoryReq(
      {this.subCategoryName,
        this.caseSubCategoryCategoryMappingList,
        this.status,
        this.subCategoryId,
        this.discription,
        this.mvnoId,
        this.buId,
        this.isDeleted});

  AddEditTaskSubCategoryReq.fromJson(Map<String, dynamic> json) {
    subCategoryName = json['subCategoryName'];
    if (json['caseSubCategoryCategoryMappingList'] != null) {
      caseSubCategoryCategoryMappingList =
      <CaseSubCategoryCategoryMappingList>[];
      json['caseSubCategoryCategoryMappingList'].forEach((v) {
        caseSubCategoryCategoryMappingList!
            .add(new CaseSubCategoryCategoryMappingList.fromJson(v));
      });
    }
    status = json['status'];
    subCategoryId = json['subCategoryId'];
    discription = json['discription'];
    mvnoId = json['mvnoId'];
    buId = json['buId'];
    isDeleted = json['isDeleted'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['subCategoryName'] = this.subCategoryName;
    if (this.caseSubCategoryCategoryMappingList != null) {
      data['caseSubCategoryCategoryMappingList'] = this
          .caseSubCategoryCategoryMappingList!
          .map((v) => v.toJson())
          .toList();
    }
    data['status'] = this.status;
    data['subCategoryId'] = this.subCategoryId;
    data['discription'] = this.discription;
    data['mvnoId'] = this.mvnoId;
    data['buId'] = this.buId;
    data['isDeleted'] = this.isDeleted;
    return data;
  }
}

// class CaseSubCategoryCategoryMappingList {
//   int? caseCategoryId;
//   String? caseSubCategoryId;
//
//   CaseSubCategoryCategoryMappingList(
//       {this.caseCategoryId, this.caseSubCategoryId});
//
//   CaseSubCategoryCategoryMappingList.fromJson(Map<String, dynamic> json) {
//     caseCategoryId = json['caseCategoryId'];
//     caseSubCategoryId = json['caseSubCategoryId'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['caseCategoryId'] = this.caseCategoryId;
//     data['caseSubCategoryId'] = this.caseSubCategoryId;
//     return data;
//   }
// }
