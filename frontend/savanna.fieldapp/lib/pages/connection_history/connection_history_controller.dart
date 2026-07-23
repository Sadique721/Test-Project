import 'dart:developer';

import 'package:savbill/pages/connection_history/connection_history_provider.dart';
import 'package:savbill/pages/connection_history/response/connection_history_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class ConnectionHistoryController extends GetxController {
  bool isLoading = false, isFilterApply = false, filterViewOpen = false;

  List<Content>? contentData = [];

  int customerId = 0, mvId = 0, page = 1;
  String customerName = "";
  String frameIp = "", fromDate = "", toDate = "", custUsername = "";

  TextEditingController frameIpController = TextEditingController();
  TextEditingController formDateController = TextEditingController();
  TextEditingController toDateController = TextEditingController();

  DateTime? selectedFromDate, selectedToDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);

  bool isShowLoadMore = false;
  ConnectionHistoryRes? responseData;
  ScrollController? controller;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (responseData != null &&
            responseData!.acctCdr != null &&
            responseData!.acctCdr!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getConnectionHistoryDetail();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
      if (arguments[Constant.MV_ID] != null) {
        mvId = arguments[Constant.MV_ID];
      }
      if (arguments[Constant.CUST_USERNAME] != null) {
        custUsername = arguments[Constant.CUST_USERNAME];
      }
      update();
      getConnectionHistoryDetail();
    }
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    frameIp = frameIpController.text;
    page = 1;
    update();
    getConnectionHistoryDetail();
  }

  clearFilter() {
    selectedFromDate = null;
    selectedToDate = null;
    toDate = "";
    fromDate = "";
    frameIpController.clear();
    formDateController.clear();
    toDateController.clear();
    isFilterApply = false;
    filterViewOpen = false;
    page = 1;
    update();
    getConnectionHistoryDetail();
  }

  getConnectionHistoryDetail() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    // String apiUrl =
    //     "findAcctCdrByUserName?mvnoId=$mvId&page=$page&size=10&userName=$custUsername&framedIpAddress=$frameIp&fromDate=$fromDate&toDate=$toDate";
    String apiUrl = "findAcctCdrByUserName?mvnoId=$mvId";
    Map<String, dynamic> request = {
      "mvnoId": mvId,
      "page": page,
      "size": 10,
      "userName": custUsername,
      "framedIpAddress": frameIp,
      "fromDate": fromDate,
      "toDate": toDate
    };
    ConnectionHistoryProvider().getConnectionHistory(
      requestData: request,
      apiUrl: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          contentData?.clear();
        }
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              responseData = ConnectionHistoryRes.fromJson(map);
              if (responseData != null && responseData!.status == 200) {
                if (responseData!.acctCdr != null &&
                    responseData!.acctCdr!.content != null &&
                    responseData!.acctCdr!.content!.isNotEmpty) {
                  contentData!.addAll(responseData!.acctCdr!.content!);
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
        isLoading = false;
        _handleApiError(error);
        isShowLoadMore = false;
        if (page == 1) {
          contentData?.clear();
        }
        update();
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    log("_handleApiError===><${error.statusCode}");
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
