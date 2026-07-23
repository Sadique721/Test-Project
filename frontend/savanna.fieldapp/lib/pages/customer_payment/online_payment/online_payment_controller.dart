import 'dart:convert';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_payment/customer_payment_provider.dart';
import 'package:savbill/pages/customer_payment/response/add_wallet_order_id_res.dart';
import 'package:savbill/pages/customer_payment/response/online_payment_audit_res.dart';
import 'package:savbill/pages/customer_payment/response/online_payment_retry_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class OnlinePaymentController extends GetxController {
  bool isLoading = false;
  // List<PaymentDetail>? customerList = [];
  List<OnlineAuditData>? onlineAuditList = [];
  TextEditingController transactionNumberController = TextEditingController();
  CustomerDetail? customerDetail;
  UserDetail? userDetail;
  GetStorage getStorage = GetStorage();
  String? currencySymbol;
  String? form;


  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    getArgumentData();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        CustomerDetail cd = arguments[Constant.CUSTOMER_DETAIL];
        customerDetail = cd;
      }
      if (arguments[Constant.FROM] != null) {
        form = arguments[Constant.FROM];
      }
    }
    update();
    getOnlinePaymentList();
  }

  getOnlinePaymentList() {
    isLoading = true;
    onlineAuditList!.clear();
    update();
    CustomerPaymentProvider().getOnlinePaymentList(
      id: customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              OnlinePaymentAuditRes responseData =
              OnlinePaymentAuditRes.fromJson(map);
              if (responseData.responseCode != null && responseData.responseCode == 200) {
                if (responseData.onlineAuditData != null &&
                    responseData.onlineAuditData!.isNotEmpty) {
                  onlineAuditList!.addAll(responseData.onlineAuditData!);
                }
              } else if(responseData.responseCode != null && responseData.responseCode == 404) {
                Utils.showSnackbar(
                    Strings.INFO,
                    " No Record Found !!",
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }else{
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.message,
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


  retryPayment(int? orderId) {
    isLoading = true;
    update();
    CustomerPaymentProvider().onlinePaymentRetry(
      orderId: orderId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              OnlinePaymentRetryRes responseData =
              OnlinePaymentRetryRes.fromJson(map);
              if(responseData.statusCode == 200) {
                if (responseData.status != null &&
                    responseData.status!.equalsIgnoreCase("Success")) {
                  getOnlinePaymentList();
                }
              }else if (responseData.statusCode == 204){
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.message,
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              } else{
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.message,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
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
        _handleApiRetryError(error);
      },
    );
  }

  addToWalletAPI(int? orderId,String? transactionNo) {
    isLoading = true;
    update();
    CustomerPaymentProvider().addWalletByOrderId(
      orderId: orderId,
      transactionNo: transactionNo,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddToWalletByOrderIdRes responseData =
              AddToWalletByOrderIdRes.fromJson(map);
              if (responseData.responseCode != null && responseData.responseCode == 200) {
                transactionNumberController.clear();
                getOnlinePaymentList();
              } else if (responseData.responseCode== 405 ||responseData.responseCode== 406 || responseData.responseCode == 417 || responseData.responseCode == 415) {
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.data,
                    AppTheme.colorWhite,
                    AppTheme.colorRed);
              }else{
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.data,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
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


  _handleApiRetryError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }else if (error.statusCode == 500){
      Utils.showSnackbar(Strings.ERROR, error.message,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }






}
