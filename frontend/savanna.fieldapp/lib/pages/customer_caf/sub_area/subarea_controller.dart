import 'dart:developer';

import 'package:savbill/util/Extensions.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../../theme/app_theme.dart';
import '../../../util/constant.dart';
import '../../../util/strings.dart';
import '../../../util/utils.dart';
import '../../../webservices/response_model.dart';
import '../../customer/customer_provider.dart';
import '../../model/page_request.dart';
import '../response/get_sub_area_res.dart';

class SubareaController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  TextEditingController searchController = TextEditingController();
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<SubAreaDataList>? parentCustomerList = [];
  GetSubAreaRes? customerListResponse;
  bool isFilterApply = false;
  int? serviceArea;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListResponse != null &&
            customerListResponse!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          if (isFilterApply) {
            searchListData(area: searchController.text);
          } else {
            getCustomerListData();
          }
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.SERVICE_AREA] != null) {
        serviceArea = arguments[Constant.SERVICE_AREA];
        print("ServiceArea: $serviceArea");
      }
    }
    getCustomerListData();
  }

  getCustomerListData() {
    PageRequest request =
    PageRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getSubAreaNew(
      serviceArea: serviceArea,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSubAreaRes responseData = GetSubAreaRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  parentCustomerList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  parentCustomerList?.clear();
                }
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
          if (page == 1) {
            parentCustomerList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        log("else CustomerCreditNoteRes$error");
        isShowLoadMore = false;
        if (page == 1) {
          parentCustomerList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  applyFilter() {
    if (searchController.text.isNullOrEmpty()) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter at-list one character.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    page = 1;
    update();
    searchListData(area: searchController.text);
  }

  clearFilter() {
    if (isFilterApply) {
      searchController.clear();
      page = 1;
      isFilterApply = false;
      update();
      getCustomerListData();
    }
  }

  searchListData({required String area}) {
    List<Map<String, dynamic>> filters = [
      {
        "filterDataType": "",
        "filterValue": area,
        "filterColumn": "any",
        "filterOperator": "equalto",
        "filterCondition": "and",
      },
      {
        "filterDataType": "",
        "filterValue": "Active",
        "filterColumn": "Status",
        "filterOperator": "equalto",
        "filterCondition": "and",
      },
    ];

    if (serviceArea != null) {
      filters.add({
        "filterDataType": "",
        "filterValue": serviceArea, // <-- Your dynamic id
        "filterColumn": "Area",
        "filterOperator": "equalto",
        "filterCondition": "and",
      });
    }

    Map<String, dynamic> body = {
      "filters": filters,
      "page": page,
      "pageSize": Constant.PAGE_LOAD_DATA_LIMIT,
    };
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().searchSubAreaNew(
      body: body,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSubAreaRes responseData = GetSubAreaRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  parentCustomerList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  parentCustomerList?.clear();
                }
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
          if (page == 1) {
            parentCustomerList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        log("else CustomerCreditNoteRes$error");
        isShowLoadMore = false;
        if (page == 1) {
          parentCustomerList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
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
