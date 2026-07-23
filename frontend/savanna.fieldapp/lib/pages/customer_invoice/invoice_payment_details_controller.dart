import 'dart:developer';

import 'package:savbill/pages/customer_invoice/customer_invoice_provider.dart';
import 'package:savbill/pages/customer_invoice/request/invoice_payment_adjust_req.dart';
import 'package:savbill/pages/customer_invoice/response/invoice_payment_adjust_res.dart';
import 'package:savbill/pages/customer_invoice/response/invoice_payment_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class InvoicePaymentListController extends GetxController{
  bool isLoading = false,isShowLoadMore = false;
  int? customerId = 0,
      invoiceId =0;
  String? customerName;
  double? remaningAmount;
  List<InvoicePaymentList>? invoicePaymentList =[];
  List<InvoicePaymentList>? selectedInvoicePaymentList =[];

  List<CreditDocumentList>? creditDocumentList = [];

  InvoicePaymentAdjustReq? invoicePaymentAdjustReq;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    // getInvoicePaymentListApi();
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
      if(arguments[Constant.INVOICE_ID] != null){
        invoiceId =arguments[Constant.INVOICE_ID];
        getInvoicePaymentListApi();
      }
    }
    update();
  }

  getInvoicePaymentListApi() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    update();
    CustomerInvoiceProvider().getInvoicePaymentList(
      invoiceId: invoiceId ?? 0,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        invoicePaymentList?.clear();
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InvoicePaymentListRes responseData = InvoicePaymentListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.paymentlist != null &&
                    responseData.paymentlist!.isNotEmpty) {
                  invoicePaymentList!.addAll(responseData.paymentlist!);
                }
              } /*else {
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }*/
            } on Exception catch (e) {
              print("EceptionInvoicePayment>> ${e.toString()}");
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        invoicePaymentList?.clear();
        _handleApiError(error);
      },
    );
  }

  double? remaningAmountV(double? totalAmount, double? remainingAmountValue){
    remaningAmount = (double.parse(totalAmount.toString())-double.parse(remainingAmountValue.toString()));
    return remaningAmount;
  }

  invoicePaymentAdjustApi() {
    invoicePaymentAdjustReq = InvoicePaymentAdjustReq(
      invoiceId: invoiceId,
      creditDocumentList:creditDocumentList,
    );
    isLoading = true;
    update();
    log("invoicePaymentAdjustReq>>> ${invoicePaymentAdjustReq}");
    CustomerInvoiceProvider().invoicePaymentAdjustApi(
      request: invoicePaymentAdjustReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InvoicePaymentAdjustRes responseData = InvoicePaymentAdjustRes.fromJson(map);
              if (responseData.status == 200) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.invoicePamentAdjust,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);

              } else {
                if (responseData.invoicePamentAdjust!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.invoicePamentAdjust,
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