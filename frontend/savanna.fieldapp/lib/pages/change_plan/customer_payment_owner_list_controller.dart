import 'package:savbill/pages/change_plan/response/customer_payment_owner_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../customer/model/request/filters.dart';

class CustomerPaymentOwnerListController extends GetxController {

  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  TextEditingController searchController = TextEditingController();
  bool isFilterApply = false, filterViewOpen = false;
  List<StaffUserlist>? staffUserlist = [];
  CustomerPaymentOwnerRes? customerListResponse;
  String type = Strings.prepaid;
  String? pageRoute;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListResponse != null &&
            customerListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getPaymentOwnerCustomerListData(Constant.PRODUCT_TYPE);
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
        getPaymentOwnerCustomerListData(Constant.PRODUCT_TYPE);
      }
    }
  }
  applyFilter() {
    if (
    searchController.text.isNullOrEmpty() ){
      isFilterApply = false;
      filterViewOpen = true;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please select or enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    staffUserlist!.clear();
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    getStaffUserSearchCallApi();
  }

  clearFilter() {
    searchController.clear();
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    getPaymentOwnerCustomerListData(Constant.PRODUCT_TYPE);
  }

  getPaymentOwnerCustomerListData(String type) {
    PageRequest pageRequest = PageRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getPaymentCustomerOwnerList(
      type: type,
      pageRequest: pageRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerPaymentOwnerRes responseData = CustomerPaymentOwnerRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                customerListResponse = responseData;
                if (page == 1) {
                  staffUserlist?.clear();
                }
                if (responseData.staffUserlist != null &&
                    responseData.staffUserlist!.isNotEmpty) {
                  staffUserlist?.addAll(responseData.staffUserlist!);
                }
              } else {
                if (page == 1) {
                  staffUserlist?.clear();
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
            staffUserlist?.clear();
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
        isShowLoadMore = false;
        if (page == 1) {
          staffUserlist?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  // getStaffUserSearch();

  getStaffUserSearchCallApi() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: "any",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text.trim()));
      customerReq.filters = filters;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getStaffUserSearch(
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerPaymentOwnerRes responseData =
              CustomerPaymentOwnerRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (page == 1) {
                  staffUserlist?.clear();
                }
                if (responseData.staffUserlist != null &&
                    responseData.staffUserlist!.isNotEmpty) {
                  staffUserlist?.addAll(responseData.staffUserlist!);
                }
              } else if (responseData.status == 204) {
                if (page == 1) {
                  staffUserlist?.clear();
                }
              } else {
                if (page == 1) {
                  staffUserlist?.clear();
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
            staffUserlist?.clear();
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
          staffUserlist?.clear();
        }
        _handleApiErrorSearchData(error);
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


  _handleApiErrorSearchData(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 500) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}