import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_list_response.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ExistingCustomerController extends GetxController {
  bool isFilterApply = false, filterViewOpen = false;
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  // List<ParentCustomerDetail>? parentCustomerList = [];
  // ParentCustomerRes? customerListResponse;
  CustomerDetail? customerDetail;
  String type = Strings.prepaid;
  String? pageRoute;
  TextEditingController searchController = TextEditingController();
  List<DropdownDetail>? customerTypeList = [];
  DropdownDetail? selectedCustomerType;

  List<DropdownDetail>? searchOptionList = [];
  DropdownDetail? selectedSearchOption;

  List<CustomerDetail>? customerList = [];
  CustomerListResponse? customerListResponse;

  bool checkBtnClickEvent = false;

  @override
  void onInit() {
    super.onInit();

    customerTypeList!.clear();
    customerTypeList!.add(DropdownDetail(
        id: Strings.prepaid,
        text: Strings.prepaid,
        type: Strings.customer_type));
    customerTypeList!.add(DropdownDetail(
        id: Strings.postpaid,
        text: Strings.postpaid,
        type: Strings.customer_type));
    selectedCustomerType = customerTypeList![0];

    searchOptionList!.clear();
    searchOptionList!.add(DropdownDetail(
        id: Strings.user_name,
        text: Strings.username.toLowerCase(),
        type: Strings.select_search_option));
    searchOptionList!.add(DropdownDetail(
        id: Strings.email,
        text: Strings.email.toLowerCase(),
        type: Strings.select_search_option));
    searchOptionList!.add(DropdownDetail(
        id: Strings.tin_no,
        text: Strings.tin_no.toLowerCase(),
        type: Strings.select_search_option));
    searchOptionList!.add(DropdownDetail(
        id: Strings.mobile_number,
        text: Strings.mobile.toLowerCase(),
        type: Strings.select_search_option));

    // getArgumentData();
    getCustomerListData();
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

  applyFilter() {
    if (selectedCustomerType == null) {
      isFilterApply = false;
      filterViewOpen = true;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please select or enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    getCustomerListData();
  }

  clearFilter() {
    selectedCustomerType = null;
    searchController.clear();
    page = 1;
    type = Strings.prepaid;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    getCustomerListData();
  }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );

    if (selectedCustomerType != null) {
      type = selectedCustomerType!.text!;
    }
    List<Filters>? filters = [];
    if (isFilterApply) {
      filters.add(Filters(
          filterColumn: selectedSearchOption?.text ?? "",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text.trim()));
      customerReq.filters = filters;
    } else {
      filters.add(Filters(
          filterColumn: "",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: ""));
      customerReq.filters = filters;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    LeadSystemProvider().getExistingCustomerList(
      type: type,
      isSearch: isFilterApply,
      customerListRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerListResponse responseData =
                  CustomerListResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  customerList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  customerList?.addAll(responseData.customerList!);
                }
              } else {
                if (page == 1) {
                  customerList?.clear();
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
            customerList?.clear();
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
          customerList?.clear();
        }
        _handleApiErrorSearchData(error);
      },
    );
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
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
