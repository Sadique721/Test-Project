import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/connection_history/response/connection_history_res.dart';
import 'package:savbill/pages/revenue_report/model/cust_revenue_report_res.dart';
import 'package:savbill/pages/revenue_report/revenue_report_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class RevenueReportController extends GetxController {
  bool isLoading = false, isFilterApply = false, filterViewOpen = false;
  List<CustomerDBRPojos>? customerDBRList= [];
  bool isShowLoadMore = false;
  int customerId = 0, mvId = 0, page = 1;
  String customerName = "",customerType="";
  String frameIp = "", fromDate = "", toDate = "", custUsername = "";

  TextEditingController formDateController = TextEditingController();
  TextEditingController toDateController = TextEditingController();

  DateTime? selectedFromDate, selectedToDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);

  CustRevenueReportRes? responseData;
  String? fromDateFormat ="",toDateFormat ="";
  String? currentDate,previousDate;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
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
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
      if (arguments[Constant.CUST_USERNAME] != null) {
        custUsername = arguments[Constant.CUST_USERNAME];
      }
      update();
      currentDate = dateFormat.format(DateTime.now());
      var nextMonth = DateTime.now().add(const Duration(days: 30));
      previousDate = dateFormat.format(nextMonth);

      formDateController.text = currentDate.toString();
      toDateController.text = previousDate.toString();

      fromDateFormat = apiDateFormat.format(DateTime.now());
      toDateFormat = apiDateFormat.format(nextMonth);
      getRevenueReportDetail(fromDateFormat,toDateFormat);
    }
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    DateTime start = dateFormat.parse(formDateController.text);
    DateTime end = dateFormat.parse(toDateController.text);

    getRevenueReportDetail(apiDateFormat.format(start),apiDateFormat.format(end));
  }

  clearFilter() {
    selectedFromDate = null;
    selectedToDate = null;
    toDate = previousDate!;
    fromDate = currentDate!;
    formDateController.clear();
    formDateController.text = currentDate.toString();
    toDateController.clear();
    toDateController.text = previousDate.toString();
    isFilterApply = false;
    filterViewOpen = false;
    page = 1;
    update();
    var nextMonth = DateTime.now().add(const Duration(days: 30));
    previousDate = dateFormat.format(nextMonth);
    formDateController.text = currentDate.toString();
    toDateController.text = previousDate.toString();
    fromDateFormat = apiDateFormat.format(DateTime.now());
    toDateFormat = apiDateFormat.format(nextMonth);
    getRevenueReportDetail(fromDateFormat,toDateFormat);
  }

  getRevenueReportDetail(String? startDate,String? endDate) {
    isLoading = true;
    update();
    RevenueReportProvider().getRevenueReport(
      custId: customerId,
      startDate: startDate,
      endDate: endDate,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              responseData = CustRevenueReportRes.fromJson(map);
                if (responseData!.customerDBRPojos != null &&
                    responseData!.customerDBRPojos != null &&
                    responseData!.customerDBRPojos!.isNotEmpty) {
                  customerDBRList!.addAll(responseData!.customerDBRPojos!);


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
        update();
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
