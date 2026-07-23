import 'dart:convert';
import 'dart:core';
import 'dart:developer';
import 'package:savbill/pages/task_management/model/response/task_sub_category_mgmt_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';

class TaskSubCategoryDetailsController extends GetxController {
  bool isLoading = false;

  TaskSubCategoryDataList? taskSubCategoryDataList;
  List<CaseSubCategoryCategoryMappingList>? caseCategoryTatMappingList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TSCM_DETAIL] != null) {
        taskSubCategoryDataList = arguments[Constant.TSCM_DETAIL];
      }
    }
    if (taskSubCategoryDataList != null) {
      if (taskSubCategoryDataList!.caseSubCategoryCategoryMappingList != null && taskSubCategoryDataList!.caseSubCategoryCategoryMappingList!.isNotEmpty) {
        caseCategoryTatMappingList!
            .addAll(taskSubCategoryDataList!.caseSubCategoryCategoryMappingList!);
      }
    }
    update();
  }
}
