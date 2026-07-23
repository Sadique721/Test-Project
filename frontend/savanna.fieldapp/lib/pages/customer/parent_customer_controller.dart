import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
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

class ParentCustomerController extends GetxController {

  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  List<ParentCustomerDetail>? parentCustomerList = [];
  ParentCustomerRes? customerListResponse;
  CustomerDetail? customerDetail;
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
          getCustomerListData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
        getCustomerListData();
      }
      if (arguments[Constant.SHIFT_LOCATION] != null) {
        pageRoute = arguments[Constant.SHIFT_LOCATION];
      }
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
    }
  }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );
    /*if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: selectedSearchCategory?.value,
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text));
      customerReq.filters = filters;
    }*/
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getParentCustomerList(
      type: type,
      isSearch: false,
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ParentCustomerRes responseData = ParentCustomerRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                customerListResponse = responseData;
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.parentCustomerList != null &&
                    responseData.parentCustomerList!.isNotEmpty) {
                  if(pageRoute != null && pageRoute!.equalsIgnoreCase(Strings.shift_location)){
                    for (var element in responseData.parentCustomerList!) {
                      if(element.id != customerDetail!.id){
                        parentCustomerList!.add(element);
                      }
                    }
                  }else {
                    parentCustomerList?.addAll(
                        responseData.parentCustomerList!);
                  }
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
              log("Exception *****> ${e}");
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            parentCustomerList?.clear();
          }
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        log("ParentCustomerControllerList #### >> $error");
        isShowLoadMore = false;
        if (page == 1) {
          parentCustomerList?.clear();
        }
        _handleApiError(error);
      },
    );
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
              ParentCustomerRes responseData = ParentCustomerRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                customerListResponse = responseData;
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.parentCustomerList != null &&
                    responseData.parentCustomerList!.isNotEmpty) {
                  if(pageRoute != null && pageRoute!.equalsIgnoreCase(Strings.shift_location)){
                    for (var element in responseData.parentCustomerList!) {
                      if(element.id != customerDetail!.id){
                        parentCustomerList!.add(element);
                      }
                    }
                  }else {
                    parentCustomerList?.addAll(
                        responseData.parentCustomerList!);
                  }
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
              log("Exception *****> ${e}");
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
        log("ParentCustomerControllerList #### >> $error");
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