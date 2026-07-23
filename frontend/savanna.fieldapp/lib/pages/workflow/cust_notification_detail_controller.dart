import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/response/customer_basic_details_update_res.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/workflow/model/cust_notification_detail_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class NotificationDetailController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  bool? isCheckNotificationStatus = false;
  List<NotificationContent>? notificationContentList = [];
  CustNotificationDetailRes? custNotificationDetailRes;
  CustomerDetail? customerDetail;
  CustomersBasicDetail? customersBasicDetail;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (custNotificationDetailRes != null &&
            custNotificationDetailRes!
                    .customerNotificationHistory!.totalPages !=
                page) {
          if (custNotificationDetailRes!
                  .customerNotificationHistory!.totalPages !=
              page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            notificationDetailsApiCall(customerDetail!.username);
          }
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      // notificationDetailsApiCall(customerDetail!.username);
      getCustomerBasicDetail();
    }
    update();
  }

  getCustomerBasicDetail() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: customerDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerBasicDetailsUpdateRes responseData =
                  CustomerBasicDetailsUpdateRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customersBasicDetail = responseData.customers;

                isCheckNotificationStatus = customersBasicDetail!.isNotificationEnable!;
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
        notificationDetailsApiCall(customerDetail!.username);
      },
      onError: (ResponseModel error) {
        notificationDetailsApiCall(customerDetail!.username);
        _handleApiError(error);
      },
    );
  }

  changeNotificationStatus(bool dunningStatus) {
    isLoading = true;
    update();
    CustomerProvider().notificationStatusChange(
      custId: customerDetail!.id,
      notificationStatus: dunningStatus,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                Utils.showSnackbar(Strings.SUCCESS, responseData.msg,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.msg != null && responseData.msg!.isNotEmpty) {
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  notificationDetailsApiCall(String? customerName) {
    isShowLoadMore = true;
    if (page == 1) {
      isLoading = true;
      notificationContentList?.clear();
    }
    update();
    List<Filters>? filters = [];
    filters.add(Filters(
        filterColumn: "customer",
        filterCondition: "",
        filterDataType: "",
        filterOperator: "",
        filterValue: customerName));
    CustomerProvider().customerNotificationDetail(
      request: CustomerListRequest(
          page: page,
          pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
          filters: filters),
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            notificationContentList?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustNotificationDetailRes responseData =
                  CustNotificationDetailRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customerNotificationHistory!.content != null &&
                    responseData
                        .customerNotificationHistory!.content!.isNotEmpty) {
                  notificationContentList?.addAll(
                      responseData.customerNotificationHistory!.content!);
                }
              } else {
                if (page == 1) {
                  notificationContentList?.clear();
                }
                Utils.showSnackbar(
                    Strings.ERROR,
                    responseData.customerNotificationHistory,
                    AppTheme.colorWhite,
                    AppTheme.colorRed);
              }
            } on Exception catch (e) {
              log("Exception:-${e.toString()}");
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        // getCustomerBasicDetail();
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          notificationContentList?.clear();
        }
        // getCustomerBasicDetail();
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
