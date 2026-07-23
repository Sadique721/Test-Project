import 'dart:convert';
import 'dart:core';
import 'dart:developer';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/util/constant.dart';
import 'package:get/get.dart';

class TaskCategoryDetailsController extends GetxController {
  bool isLoading = false;

  TaskCategoryMgmtDataList? taskCategoryMgmtDataList;
  List<CaseCategoryTatMappingList>? caseCategoryTatMappingList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.TCM_DETAIL] != null) {
        taskCategoryMgmtDataList = arguments[Constant.TCM_DETAIL];


        log("taskCategoryMgmtDataList===>>>${jsonEncode(taskCategoryMgmtDataList)}");
      }
    }
    if (taskCategoryMgmtDataList != null) {
      if (taskCategoryMgmtDataList!.caseCategoryTatMappingList != null && taskCategoryMgmtDataList!.caseCategoryTatMappingList!.isNotEmpty) {
        caseCategoryTatMappingList!
            .addAll(taskCategoryMgmtDataList!.caseCategoryTatMappingList!);
      }
    }
    update();
  }
}
