import 'package:savbill/pages/customer_ledger/customer_ledger_provider.dart';
import 'package:savbill/pages/customer_ledger/request/customer_ledger_req.dart';
import 'package:savbill/pages/customer_ledger/response/customer_ledger_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class CustomerLedgerController extends GetxController {
  bool isLoading = false, isFilterApply = false, filterViewOpen = false;

  int customerId = 0;
  String customerName = "";
  String fromDate = "", toDate = "";

  TextEditingController fromDateController = TextEditingController();
  TextEditingController toDateController = TextEditingController();

  DateTime? selectedFromDate, selectedToDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  String? currencySymbol;
  CustomerLedgerDtls? ledgerDetail;
  List<LedgerDebitCreditDetail>? debitCreditDetail = [];
  String openingAmount = "0", closingBalance = "0";

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
        getCustomerLedgerDetail();
      }
    }
    update();
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    update();
    getCustomerLedgerDetail();
  }

  clearFilter() {
    selectedFromDate = null;
    selectedToDate = null;

    fromDate = "";
    toDate = "";
    fromDateController.clear();
    toDateController.clear();

    isFilterApply = false;
    filterViewOpen = false;
    update();
    getCustomerLedgerDetail();
  }

  getCustomerLedgerDetail() {
    isLoading = true;
    debitCreditDetail!.clear();
    update();
    CustomerLedgerReq request = CustomerLedgerReq(
        cREATEDATE: fromDate,
        eNDDATE: toDate,
        id: "",
        amount: "",
        balAmount: "",
        custId: customerId,
        description: "",
        refNo: "",
        transcategory: "",
        transtype: "");
    CustomerLedgerProvider().getCustomerLedgerDetail(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerLedgerRes responseData = CustomerLedgerRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customerLedgerDtls != null) {
                  ledgerDetail = responseData.customerLedgerDtls;
                  if (ledgerDetail!.customerLedgerInfoPojo != null) {
                    openingAmount = ledgerDetail!
                        .customerLedgerInfoPojo!.openingAmount!
                        .toString();
                    closingBalance = ledgerDetail!
                        .customerLedgerInfoPojo!.closingBalance!
                        .toString();
                  }
                  if (ledgerDetail!.customerLedgerInfoPojo != null &&
                      ledgerDetail!.customerLedgerInfoPojo!.debitCreditDetail !=
                          null &&
                      ledgerDetail!.customerLedgerInfoPojo!.debitCreditDetail!
                          .isNotEmpty) {
                    debitCreditDetail!.addAll(ledgerDetail!
                        .customerLedgerInfoPojo!.debitCreditDetail!);
                  }
                }
              } else {
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorRed);
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
        getSystemConfigurationData(Strings.currency_payment);
      },
      onError: (ResponseModel error) {
        getSystemConfigurationData(Strings.currency_payment);
        _handleApiError(error);
      },
    );
  }

  getSystemConfigurationData(String type) {
    isLoading = true;
    update();
    PaymentProvider().getSystemConfiguration(
      type: type,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentConfigurationRes responseData =
                  PaymentConfigurationRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.data != null) {
                  if (responseData.data!.name!.isNotEmpty &&
                      type.equalsIgnoreCase(Strings.currency_payment)) {
                    currencySymbol = responseData.data!.value;
                  }
                }
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
