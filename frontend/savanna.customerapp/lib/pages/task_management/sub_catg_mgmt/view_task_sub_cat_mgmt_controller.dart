import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/task_management/model/response/task_sub_category_mgmt_res.dart';
import 'package:savbill/pages/task_management/sub_catg_mgmt/add_edit_sub_category/add_edit_task_sub_category_mgmt.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../../util/strings.dart';
import '../../../webservices/response_model.dart';
import '../model/response/task_category_management_list_res.dart';

class ViewTaskSubCategoryManagementController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  int page = 1;
  ScrollController? controller;
  TextEditingController searchController = TextEditingController();

  GetStorage getStorage = GetStorage();
  List<TaskSubCategoryDataList>? taskSubCategoryList = [];
  TaskSubCategoryMgmtRes? taskSubCategoryMgmtListRes;
  List<TaskCategoryMgmtDataList>? allActiveReasonCategoryList = [];
  TaskCategoryMgmtDataList? selectedActiveReasonCategory;
  @override
  void onInit() {
    super.onInit();
    getAllActiveReasonCategory();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (taskSubCategoryMgmtListRes != null &&
            taskSubCategoryMgmtListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewTaskCategoryData();
        }
      }
    });
  }

  applyFilter() {
    if (searchController.text.isNullOrEmpty()) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    viewTaskCategoryData();
  }

  clearFilter() {
    searchController.clear();
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    viewTaskCategoryData();
  }

  viewTaskCategoryData() {
    // PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();
    if (isFilterApply) {
      List<Filters>? filters = [];
      if (!searchController.text.isNullOrEmpty()) {
        filters.add(Filters(
            filterColumn: "name",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: searchController.text));
      }

      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
      searchReq.sortBy = "createdate";
      searchReq.sortOrder = 0;
    } else {
      searchReq.page = page;
      searchReq.pageSize = 10;
      searchReq.sortBy = "createdate";
      searchReq.sortOrder = 0;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TaskSystemProvider().viewTaskSubCategoryList(
      isSearch: isFilterApply,
      // requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskSubCategoryMgmtRes responseData =
              TaskSubCategoryMgmtRes.fromJson(map);

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                taskSubCategoryMgmtListRes = responseData;
                if (page == 1) {
                  taskSubCategoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskSubCategoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  taskSubCategoryList?.clear();
                }
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            taskSubCategoryList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          taskSubCategoryList?.clear();
        }
        _handleApiError(error);
      },
    );
  }
  getAllActiveReasonCategory() {
    isLoading = true;
    TaskSystemProvider().getAllActiveReasonCategory(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskCategoryMgmtRes responseData =
              TaskCategoryMgmtRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                allActiveReasonCategoryList?.addAll(responseData.dataList!);
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        viewTaskCategoryData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        viewTaskCategoryData();
      },
    );
  }


  addEditTaskCategoryScreen(String from, TaskSubCategoryDataList? item) async {
    var result = await Get.to(AddEditTaskSubCategoryMgmt(),
        arguments: {Constant.FROM: from, Constant.TSCM_DETAIL: item});

    if (result != null && result == true) {
      clearFilter();
    }
  }

  deleteTaskCategory(TaskSubCategoryDataList? item, int index) {
    isLoading = true;
    update();
    TaskSystemProvider().deleteTaskSubCategory(
      request: item,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      (responseData.responseCode == 200 ||
                          responseData.responseCode == 0))) {
                taskSubCategoryList!.removeAt(index);
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
