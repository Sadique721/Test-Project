import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/ticket_system/model/response/root_cause_list_res.dart';
import 'package:savbill/pages/ticket_system/root_cause/add_edit_root_cause.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../model/response/root_cause_sub_problem_res.dart';

class ViewRootCauseController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  GetStorage getStorage = GetStorage();
  List<RootCauseDetail>? rootCauseList = [];
  RootCauseListRes? rootCauseListRes;

  TextEditingController searchController = TextEditingController();
  bool isFilterApply = false;

  @override
  void onInit() {
    super.onInit();
    viewRootCauseData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (rootCauseListRes != null && rootCauseListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewRootCauseData();
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
    page = 1;
    update();
    viewRootCauseData();
  }

  clearFilter() {
    searchController.clear();
    page = 1;
    isFilterApply = false;
    update();
    viewRootCauseData();
  }

  viewRootCauseData() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: "any",
          filterCondition: "",
          filterDataType: "",
          filterOperator: "",
          filterValue: searchController.text.trim()));
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
    TicketSystemProvider().viewRootCauseList(
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
              RootCauseListRes responseData = RootCauseListRes.fromJson(map);

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                rootCauseListRes = responseData;
                if (page == 1) {
                  rootCauseList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  rootCauseList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  rootCauseList?.clear();
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
            rootCauseList?.clear();
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
          rootCauseList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  addEditRootCauseScreen(String from, RootCauseDetail? item) async {
     var result = await Get.to(AddEditRootCause(),
        arguments: {Constant.FROM: from, Constant.ROOT_CAUSE_DETAIL: item});

    if (result != null && result == true) {
      clearFilter();
    }
  }

  deleteRootCause(RootCauseDetail item, int index) {
    isLoading = true;
    update();
    TicketSystemProvider().deleteRootCause(
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
                rootCauseList!.removeAt(index);
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
