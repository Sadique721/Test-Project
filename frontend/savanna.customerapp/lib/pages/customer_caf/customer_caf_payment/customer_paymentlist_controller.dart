import 'dart:convert';

import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_payment/customer_payment_provider.dart';
import 'package:savbill/pages/customer_payment/response/customer_payment_list_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_list_response.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CustomerCAFPaymentListController extends GetxController {
  bool isLoading = false;

  int customerId = 0;
  String customerName = "";
  List<PaymentDetail>? paymentDetail = [];
  CustomerDetail? customerDetail;
  UserDetail? userDetail;
  GetStorage getStorage = GetStorage();
  String? currencySymbol;
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
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        // customerId = 17;
        getCustomerInvoiceDetail();
      }
      customerDetail = CustomerDetail(id: customerId, name: customerName);
    }
    update();
  }

  getCustomerInvoiceDetail() {
    isLoading = true;
    paymentDetail!.clear();
    update();
    CustomerPaymentProvider().getCustomerPaymentList(
      id: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerPaymentListRes responseData =
                  CustomerPaymentListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  paymentDetail!.addAll(responseData.dataList!);
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
              if (responseData.responseCode == 200) {
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
