import 'dart:developer';

import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/customer/model/customer_search_data.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ActiveCustomerListController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<CustomerCreditList>? parentCustomerList = [];
  List<CustomerCreditList>? filterParentCustomerList = [];
  TextEditingController searchController = TextEditingController();
  CustomerCreditNoteRes? customerListResponse;
  String type = Strings.prepaid;
  bool isFilterApply = false, filterViewOpen = false;

  List<CustomerSearchData>? searchCategory = [];
  CustomerSearchData? selectedSearchCategory;

  @override
  void onInit() {
    super.onInit();

    searchCategory?.clear();
    searchCategory?.add(CustomerSearchData(
        text: Strings.firstname, value: Strings.name.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.username, value: Strings.username.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.fullname, value: Strings.fullname.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.email, value: Strings.email.toLowerCase()));
    searchCategory?.add(
        CustomerSearchData(text: 'Phone', value: Strings.mobile.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.service, value: Strings.service.toLowerCase()));
    searchCategory?.add(CustomerSearchData(
        text: Strings.plan, value: Strings.plan.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'Plan Group', value: 'planGroup'));
    searchCategory?.add(
        CustomerSearchData(text: 'Service Area', value: 'serviceareaName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Mac Address', value: 'macaddress'));
    searchCategory?.add(CustomerSearchData(
        text: Strings.status, value: Strings.status.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'CAF Status', value: 'cafStatus'));
    searchCategory?.add(CustomerSearchData(
        text: Strings.any, value: Strings.any.toLowerCase()));
    searchCategory
        ?.add(CustomerSearchData(text: 'PartnerName', value: 'partnerName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Branch', value: 'branchName'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Customer Type', value: 'custtype'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Circuit Name', value: 'circuitName'));
    searchCategory?.add(CustomerSearchData(
        text: 'Current Assigned Staff', value: 'currentAssigneeName'));
    searchCategory?.add(CustomerSearchData(
        text: 'Current Assigned Team', value: 'currentAssignedTeam'));
    searchCategory?.add(
        CustomerSearchData(text: 'CAF Created Date', value: 'cafCreatedDate'));
    searchCategory?.add(CustomerSearchData(text: 'CAF Number', value: 'cafNo'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Static IPr', value: 'staticIp'));
    searchCategory?.add(CustomerSearchData(
        text: 'Inventory Serial Number', value: 'inventorySerial'));
    searchCategory?.add(
        CustomerSearchData(text: 'Plan Expiry Date', value: 'expiryDate'));
    searchCategory?.add(CustomerSearchData(
        text: 'Framed_Ip_Address', value: 'framedIpAddress'));
    searchCategory?.add(CustomerSearchData(
        text: 'Subscription Mode', value: 'subscriptionMode'));
    searchCategory?.add(CustomerSearchData(text: 'Param1', value: 'param1'));
    searchCategory?.add(CustomerSearchData(text: 'Param2', value: 'param2'));
    searchCategory?.add(CustomerSearchData(text: 'Param3', value: 'param3'));
    searchCategory?.add(CustomerSearchData(text: 'Param4', value: 'param4'));
    searchCategory
        ?.add(CustomerSearchData(text: 'Account No', value: 'accountNumber'));

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
          getActiveCustomerListData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    getActiveCustomerListData();
    /* if (arguments != null) {
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
        getCustomerListData();
      }
    }*/
  }

  applyFilter() {
    if (selectedSearchCategory == null ||
        searchController.text.isNullOrEmpty() ){
      isFilterApply = false;
      filterViewOpen = true;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please select or enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    filterParentCustomerList!.clear();
    parentCustomerList!.clear();
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    getCustomerListData();
  }

  clearFilter() {
    // selectedSearchCategory = null;
    // selectedStatusList = null;
    // selectedCustomerAllStaffList = null;
    searchController.clear();
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    getActiveCustomerListData();
  }

  getActiveCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    ViewCreditNoteProvider().getActiveCustomerList(
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCreditNoteRes responseData =
                  CustomerCreditNoteRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  parentCustomerList?.addAll(responseData.customerList!);
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
        isLoading = false;
        if (page == 1) {
          parentCustomerList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: selectedSearchCategory?.value,
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
    parentCustomerList!.clear();
    ViewCreditNoteProvider().getCustomerBothList(
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCreditNoteRes responseData =
              CustomerCreditNoteRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (page == 1) {
                  parentCustomerList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  // filterParentCustomerList?.addAll(responseData.customerList!);
                  parentCustomerList?.addAll(responseData.customerList!);
                }
              } else if (responseData.status == 204) {
                if (page == 1) {
                  parentCustomerList?.clear();
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
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          parentCustomerList?.clear();
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
