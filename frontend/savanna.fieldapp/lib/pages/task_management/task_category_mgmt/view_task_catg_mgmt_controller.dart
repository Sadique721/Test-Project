
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/task_category_mgmt/add_edit_task_catg/add_edit_task_catg_mgmt.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';


class ViewTaskCategoryManagementController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  int page = 1;
  ScrollController? controller;
  TextEditingController searchController = TextEditingController();


  GetStorage getStorage = GetStorage();
  List<TaskCategoryMgmtDataList>? taskCategoryList = [];
  TaskCategoryMgmtRes? taskCategoryManagementListRes;


  @override
  void onInit() {
    super.onInit();
    viewTaskCategoryData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (taskCategoryManagementListRes != null &&
            taskCategoryManagementListRes!.totalPages != page) {
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
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();
    if (isFilterApply) {
      List<Filters>? filters = [];
      if (!searchController.text.isNullOrEmpty()) {
        filters.add(Filters(
            filterColumn: "name",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: searchController.text.trim()));
      }

      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
      searchReq.sortBy = "createdate";
      searchReq.sortOrder = 0;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TaskSystemProvider().viewTaskCategoryList(
      isSearch: isFilterApply,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TaskCategoryMgmtRes responseData =
              TaskCategoryMgmtRes.fromJson(map);

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                taskCategoryManagementListRes = responseData;
                if (page == 1) {
                  taskCategoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskCategoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  taskCategoryList?.clear();
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
            taskCategoryList?.clear();
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
          taskCategoryList?.clear();
        }
        _handleApiError(error);
      },
    );

  }

  addEditTaskCategoryScreen(String from, TaskCategoryMgmtDataList? item) async {
    var result = await Get.to(AddEditTaskCategoryMgmt(),
        arguments: {Constant.FROM: from, Constant.TCM_DETAIL: item});

    if (result != null && result == true) {
      clearFilter();
    }
  }


  deleteTaskCategory(TaskCategoryMgmtDataList item, int index) {
    isLoading = true;
    update();
    TaskSystemProvider().deleteTaskCategory(
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
                taskCategoryList!.removeAt(index);
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
