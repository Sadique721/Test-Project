import 'dart:convert';
import 'dart:developer';
import 'dart:io';

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
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'package:path/path.dart';

class CustomerPaymentListController extends GetxController {
  bool isLoading = false;

  int customerId = 0;
  String customerName = "";
  List<PaymentDetail>? paymentDetail = [];
  CustomerDetail? customerDetail;
  UserDetail? userDetail;
  GetStorage getStorage = GetStorage();
  String? currencySymbol;
  String? customerType;
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

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      // customerDetail = CustomerDetail(id: customerId, name: customerName);
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




  custDownloadInvoice(String? pageTitle, String? networkPathUrl,String? customerName) async {
    isLoading = true;
    var url = "$networkPathUrl";
    final filename = basename(url);
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };
    log("Url===>>>$url");
    final response = await http.get(Uri.parse(url), headers: headers);
    if(response.statusCode == 200) {
      final bytes = response.bodyBytes;

      Directory directory;
      if (Platform.isIOS) {
        directory = await getApplicationDocumentsDirectory();
      } else {
        directory = Directory("/storage/emulated/0/Download");
      }

      var file = File('${directory.path}/$customerName$filename.pdf');
      await file.writeAsBytes(bytes, flush: true);

      if (Platform.isIOS) {
        Utils.showSnackbar(Strings.SUCCESS, "File saved to → On My iPhone → ${Strings.app_name} folder.",
            AppTheme.colorWhite, AppTheme.colorGreen);
      } else {
        Utils.showSnackbar(Strings.SUCCESS, "File Downloaded Successfully Please Open Download Folder!!",
            AppTheme.colorWhite, AppTheme.colorGreen);
      }


      isLoading = false;
      // return pFile;
    }else if (response.statusCode == 404){
      isLoading = false;
      Utils.showSnackbar(Strings.INFO,
          "File not found!!",
          AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

}
