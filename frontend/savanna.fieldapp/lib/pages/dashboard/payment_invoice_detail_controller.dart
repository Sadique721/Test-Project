import 'package:savbill/pages/dashboard/model/response/payment_invoice_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class PaymentInvoiceDetailController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  List<PaymentInvoice>? paymentInvoice = [];

  int? paymentId;
  num adjustedAmount = 0;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.ID] != null) {
        paymentId = arguments[Constant.ID];
      }
    }
    update();
    if (paymentId != null) {
      getPaymentInvoiceData();
    }
  }

  getPaymentInvoiceData() {
    paymentInvoice!.clear();
    adjustedAmount = 0;
    isLoading = true;
    update();
    PaymentProvider().paymentInvoiceData(
      id: paymentId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentInvoiceRes responseData = PaymentInvoiceRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.invoicelist != null &&
                    responseData.invoicelist!.isNotEmpty) {
                  paymentInvoice!.addAll(responseData.invoicelist!);
                  for (PaymentInvoice element in paymentInvoice!) {
                    if (element.adjustedAmount != null) {
                      adjustedAmount = adjustedAmount + element.adjustedAmount!;
                    }
                  }
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
