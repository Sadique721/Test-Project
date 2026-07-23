import 'dart:async';
import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/cust_address_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/customer_caf_invoice/customer_caf_mobile_number_dialog.dart';
import 'package:savbill/pages/customer_caf/customer_caf_invoice/payment_gat_status_caf_dialog.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/pages/customer_invoice/customer_invoice_provider.dart';
import 'package:savbill/pages/customer_invoice/payment_status_dialog.dart';
import 'package:savbill/pages/customer_invoice/request/airtel_payment_req.dart';
import 'package:savbill/pages/customer_invoice/request/get_payment_req.dart';
import 'package:savbill/pages/customer_invoice/request/momo_pay_request.dart';
import 'package:savbill/pages/customer_invoice/request/selcom_pay_request.dart';
import 'package:savbill/pages/customer_invoice/response/airtel_payment_res.dart';
import 'package:savbill/pages/customer_invoice/response/get_payment_status_res.dart';
import 'package:savbill/pages/customer_invoice/response/momo_pay_res.dart';
import 'package:savbill/pages/customer_invoice/response/payment_config_res.dart';
import 'package:savbill/pages/customer_invoice/response/selcom_payment_res.dart';
import 'package:savbill/pages/customer_invoice/response/void_invoice_add_mark_res.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:file_utils/file_utils.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:path/path.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:uuid/uuid.dart';

class CustomerCafInvoiceController extends GetxController
    implements PaymentCAFStatusAction {
  bool isLoading = false,
      isFilterApply = false,
      checkBtnClickEvent = false,
      filterViewOpen = false;
  String? currencySymbol;

  // List<InvoiceDetail>? invoiceList = [];
  List<Invoicesearchlist>? invoiceList = [];
  int customerId = 0;
  int? invoiceId = 0;
  String customerName = "";
  CustomerDetail? customerDetail;
  String billId = "", document = "", billFrom = "", billTo = "";

  TextEditingController documentNoController = TextEditingController();
  TextEditingController invoiceFormDateController = TextEditingController();
  TextEditingController invoiceToDateController = TextEditingController();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  TextEditingController mobileNumberController = TextEditingController();
  DateTime? selectedInvoiceFromDate, selectedInvoiceToDate;
  String? pdfUrls;
  String? documentId;
  bool displayDialog = false;

  //String? selectedInvoiceFromDateApi = "", selectedInvoiceToDateApi = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);

  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  List<ActivePaymentConfig>? activePaymentConfig = [];
  List<ActivePaymentConfig>? savedConfig = [];
  Map<String, dynamic> paymentkeyValuePairs = {};
  String? presentFullAddress;
  AddressData? customerServiceAreaData;

  Invoicesearchlist? selectedPlan;
  int paymentstatusCount = 1;
  Timer? timer;
  bool? transactionStatus = false;
  String? transactionId;


  CustAddressDetail? presentAddress;
  /*___downloading code___*/

  late String localPath;
  late bool permissionReady;
  late TargetPlatform? platform;
  GetStorage getStorage = GetStorage();

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    if (Platform.isAndroid) {
      platform = TargetPlatform.android;
    } else {
      platform = TargetPlatform.iOS;
    }
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        /*   if (customerListResponse != null &&
            customerListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getCustomerListData();
        }*/
      }
    });
  }

  Future<bool> checkPermission() async {
    if (platform == TargetPlatform.android) {
      final status = await Permission.storage.status;
      if (status != PermissionStatus.granted) {
        final result = await Permission.storage.request();
        if (result == PermissionStatus.granted) {
          return true;
        }
      } else {
        return true;
      }
    } else {
      return true;
    }
    return false;
  }

  Future<void> prepareSaveDir() async {
    localPath = (await _findLocalPath())!;

    print(localPath);
    final savedDir = Directory(localPath);
    bool hasExisted = await savedDir.exists();
    if (!hasExisted) {
      savedDir.create();
    }
  }

  Future<String?> _findLocalPath() async {
    if (platform == TargetPlatform.android) {
      return "/sdcard/download/";
    } else {
      var directory = await getApplicationDocumentsDirectory();
      return directory.path + Platform.pathSeparator + 'Download';
    }
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_AREA_DETAIAL] != null) {
        presentAddress = arguments[Constant.CUSTOMER_AREA_DETAIAL];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        //customerId = 125;
        getCustomerInvoiceDetail();
      }
    }

    if (customerDetail != null) {
      mobileNumberController.text = customerDetail!.mobile!;
    }
    update();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  applyFilter() {
    isFilterApply = true;
    filterViewOpen = false;
    document = documentNoController.text;
    page = 1;
    update();
    getCustomerInvoiceDetail();
  }

  clearFilter() {
    selectedInvoiceFromDate = null;
    selectedInvoiceToDate = null;
    document = "";
    billFrom = "";
    billTo = "";
    documentNoController.clear();
    invoiceFormDateController.clear();
    invoiceToDateController.clear();

    isFilterApply = false;
    filterViewOpen = false;
    page = 1;
    update();
    getCustomerInvoiceDetail();
  }

  getCustomerInvoiceDetail() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    update();
    String url =
        "customerid=$customerId&billrunid=$billId&docnumber=$document&billfromdate=$billFrom&billtodate=$billTo&custmobile=&isInvoiceVoid=true";

    CustomerInvoiceProvider().getCafCustomerInvoiceList(
      request: PageRequest(page: page, pageSize: 10),
      url: url,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          invoiceList?.clear();
        }
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCafInvoiceDetailRes responseData =
                  CustomerCafInvoiceDetailRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.invoicesearchlist != null &&
                    responseData.invoicesearchlist!.isNotEmpty) {
                  invoiceList!.addAll(responseData.invoicesearchlist!);
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
          paymentConfigurationCall();
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          invoiceList?.clear();
        }
        paymentConfigurationCall();
        _handleApiError(error);
      },
    );
  }

  paymentConfigurationCall() {
    isLoading = true;
    update();
    PaymentProvider().paymentConfig(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentConfigRes responseData = PaymentConfigRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                activePaymentConfig = responseData.activePaymentConfig;
                savedConfig = activePaymentConfig;
                Map<String, dynamic> keyValuePairs = {};
                for (var config in savedConfig!) {
                  for (var mappingItem in config.paymentConfigMappingList!) {
                    keyValuePairs[mappingItem.paymentParameterName!] =
                        mappingItem.paymentParameterValue;
                  }
                }
                paymentkeyValuePairs = keyValuePairs;
              } else {
                if (responseData.message != null && responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(
                    Strings.ERROR,
                    responseData.message!,
                    AppTheme.colorWhite,
                    AppTheme.colorRed,
                  );
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
        getSystemConfigurationData(Strings.currency_payment);
      },
      onError: (ResponseModel error) {
        getSystemConfigurationData(Strings.currency_payment);
        _handleApiError(error);
      },
    );
  }

  getSystemConfigurationData (String type) {
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

  cancelAndRegenerateAPI(
      int? invoiceId, TextEditingController textEditingController) {
    isLoading = true;
    update();
    String url =
        "$invoiceId?isCaf=true&invoiceCancelRemarks=${textEditingController.text}";
    log("cancelAndRegenerateAPI>>> ${url}");

    CustomerInvoiceProvider().cancelAndRegenerateInvoice(
      url: url,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              VoidInvoiceRemarkRes responseData =
                  VoidInvoiceRemarkRes.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                Future.delayed(const Duration(seconds: 2)).then((val) {
                  Get.back();
                });
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
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          invoiceList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getVoidInvoiceListAPI(
      int? invoiceId, TextEditingController textEditingController) {
    isLoading = true;
    update();
    String url =
        "invoiceId=$invoiceId&invoiceCancelRemarks=${textEditingController.text}";
    CustomerInvoiceProvider().getVoidInvoiceList(
      url: url,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              VoidInvoiceRemarkRes responseData =
                  VoidInvoiceRemarkRes.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                Future.delayed(const Duration(seconds: 2)).then((val) {
                  Get.back();
                });
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
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
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          invoiceList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  generatePdfInvoiceAPI(int? invoiceId) {
    isLoading = true;
    update();
    CustomerInvoiceProvider().generateTrialPdfByInvoiceId(
      id: invoiceId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == "200") {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                  getCustomerInvoiceDetail();
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
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        _handleApiError(error);
      },
    );
  }

  String convertCurrentDateTimeToString() {
    String formattedDateTime =
        DateFormat('yyyyMMdd_kkmmss').format(DateTime.now()).toString();
    return "bill_" + formattedDateTime;
  }

  downloadFile() async {
    try {
      String fileName = convertCurrentDateTimeToString();
      final directory = await getApplicationDocumentsDirectory();
      String dirloc = '${directory.path}/';
      FileUtils.mkdir([dirloc]);
      downloadGenerateBillCall(
          UrlConstants.trialInvoiceDownloadUrl + documentId!.toString(),
          "$dirloc$fileName.pdf");
    } catch (e) {
      print("$e");
    }
  }

  downloadGenerateBillCall(String url, String savePath) {
    isLoading = true;
    update();
    CustomerInvoiceProvider().generateInvoiceBill(
      fileUrl: url,
      savePath: savePath,
      onSuccess: (ResponseModel responseModel) async {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (!savePath.isNullOrEmpty()) {}
          createFileOfPdfUrl();
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  Future<File?> downloadFileNew(String? docNumber) async {
    Completer<File> completer = Completer();
    isLoading = true;
    String token = "";
    if (getStorage.hasData(Constant.USER_TOKEN)) {
      token = await getStorage.read(Constant.USER_TOKEN);
    }
    Map<String, String> headers = {
      'Content-type': 'application/json; charset=UTF-8',
      'Accept': 'application/json',
      'Authorization': 'Bearer $token'
    };
    String fileName = convertCurrentDateTimeToString();
    final response = await http.get(
        Uri.parse(
            UrlConstants.trial_invoice_receipt_url + docNumber.toString()),
        headers: headers);
    final bytes = response.bodyBytes;

    var dir = await getApplicationDocumentsDirectory();
    var file = File('${dir.path}/$fileName.pdf');
    await file.writeAsBytes(bytes, flush: true);
    // pFile = file;
    completer.complete(file);
    isLoading = false;
    // return pFile;
  }

  Future<File> createFileOfPdfUrl() async {
    Completer<File> completer = Completer();
    print("Start download file from internet!");
    try {
      final url =
          "http://192.168.24.31:30080/api/v1/Revenue/regeneratepdfsub/160";
      final filename = url.substring(url.lastIndexOf("/") + 1);
      var request = await HttpClient().getUrl(Uri.parse(url));
      var response = await request.close();
      var bytes = await consolidateHttpClientResponseBytes(response);
      var dir = await getApplicationDocumentsDirectory();
      print("Download files");
      print("${dir.path}/$filename");
      File file = File("${dir.path}/$filename.pdf");
      await file.writeAsBytes(bytes, flush: true);
      // completer.complete(file);
    } catch (e) {
      throw Exception('Error parsing asset file!');
    }

    return completer.future;
  }

  ///open Payment Gateways
  openPaymentGateways(
      {Invoicesearchlist? plan, required BuildContext context}) {
    displayDialog = false;
    if (savedConfig!.isEmpty) {
      Utils.showSnackbar(Strings.INFO, Strings.payment_gateway_config_not_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (savedConfig!.length == 1) {
      switch (savedConfig![0].paymentConfigName) {
        case "PAYTM":
          // spinner.show();
          // addPayment(plan);
          break;
        case "FLUTTERWAVE":
          // spinner.show();
          // renewActivePlan(plan);
          break;
        case "RAZORPAY":
          // spinner.show();
          // buyPlanWithRazorpay(plan);
          break;
        case "MoMo Pay":
          // spinner.show();
          // buyMomoPayPlan(plan);
          break;
        case "AIRTEL":
          // spinner.show();
          // airtelPayPlan(plan);
          break;
        default:
          // spinner.show();
          selectedPlan = plan;
      }
    } else if (savedConfig!.length > 1) {
      selectedPlan = plan;
      showPaymentGatewayDialog(plan, savedConfig!, true);
      update();
    }
  }

  showPaymentGatewayDialog(Invoicesearchlist? plan,
      List<ActivePaymentConfig> item, bool? displayDialog) {
    showDialog(
        context: Get.overlayContext!,
        barrierDismissible: true,
        builder: (_) {
          return PaymentGetCAFStatusDialog(
            paymentGatewayAction: this,
            displayDialog: true,
            savedConfig: savedConfig!,
            plan: plan,
          );
        });
  }

   fileDownloading(
      String? pageTitle, String? networkPathUrl, String? customerName) async {
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

  @override
  void paymentCAFStatusBtnAction(
      {Invoicesearchlist? plan,
      ActivePaymentConfig? selectedItem,
      BuildContext? context}) {
    if (selectedItem!.paymentConfigName!.equalsIgnoreCase("PAYTM")) {
      Get.back();
      Utils.showSnackbar(Strings.INFO, Strings.under_development,
          AppTheme.colorWhite, AppTheme.colorGreen);
    } else if (selectedItem.paymentConfigName!
        .equalsIgnoreCase("FLUTTERWAVE")) {
      Get.back();
      Utils.showSnackbar(Strings.INFO, Strings.under_development,
          AppTheme.colorWhite, AppTheme.colorGreen);
    } else if (selectedItem.paymentConfigName!.equalsIgnoreCase("RAZORPAY")) {
      Get.back();
      Utils.showSnackbar(Strings.INFO, Strings.under_development,
          AppTheme.colorWhite, AppTheme.colorGreen);
    } else if (selectedItem.paymentConfigName!.equalsIgnoreCase("MoMo Pay")) {
      Get.back();
      showMobileNumberDialog(plan: plan, selectedData: selectedItem, context: Get.context,);
      // momoPayRequestApiCall(plan: plan, selectedData: selectedItem, context: Get.context);
    } else if (selectedItem.paymentConfigName!.equalsIgnoreCase("AIRTEL")) {
      Get.back();
      showMobileNumberDialog(plan: plan, selectedData: selectedItem, context: Get.context,);
      // airtelPayApiCall(plan: plan, selectedData: selectedItem, context: Get.context);
    } else if (selectedItem.paymentConfigName!.equalsIgnoreCase("SELCOM")) {
      Get.back();
      buySelcomPayApiCall(plan: plan, selectedData: selectedItem);
    } else {
      selectedPlan = plan;
    }
  }

  showMobileNumberDialog({Invoicesearchlist? plan,
    ActivePaymentConfig? selectedData,
    BuildContext? context})async {
    showDialog(
      barrierDismissible: false,
      context: context!,
      builder: (BuildContext context) {
        return CustomerCAFMobileNumberDialog(
          title: Strings.mobile_number,
          mobileController: mobileNumberController,
          plan: plan,
          selectedItem: selectedData,
        );
      },
    );
  }

  momoPayRequestApiCall(
      {Invoicesearchlist? plan,
      ActivePaymentConfig? selectedData,
      BuildContext? context,String? mobileNumber,String? countryCode}) {
    double? lastAmount = 0.0;
    if (plan != null && plan.adjustedAmount == null) {
      lastAmount = plan.totalamount! - 0;
    } else {
      lastAmount = plan!.totalamount! - plan.adjustedAmount!;
    }

    isLoading = true;
    update();
    MomoPayRequest request = MomoPayRequest(
        customerId: customerDetail!.id,
        planId: plan.id,
        amount: lastAmount.toString(),
        isFromCaptive: false,
        merchantName: Constant.MOMO_PAY_MERCHANT,
        customerUUID: Uuid().v4(),
        customerUserName: customerDetail!.firstname,
        mvnoId: customerDetail!.mvnoId,
        invoiceId: plan.id,
        mobileNumber: (countryCode ?? "").replaceAll("+", "") +
            (mobileNumber ?? ""),
        partnerId: customerDetail!.partnerid,
        accountNumber: customerDetail!.acctno ?? "");
    PaymentProvider().requestOfMomoPay(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              MomoPayRes responseData = MomoPayRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.data != null) {
                  getStorage.write(Constant.TRANSACATION_ID,
                      responseData.data!.data!.orderId);
                  timer = Timer.periodic(const Duration(seconds: 1), (timer) {
                    if (paymentstatusCount > 0) {
                      paymentstatusCount -= 1;
                      // getStatusSuccessByMomo("SUCCESSFUL", context);
                      paymentConfirmationDialog(context);
                      if (transactionStatus == true) {
                        timer.cancel();
                      }
                    }
                    if (paymentstatusCount == 0) {
                      timer.cancel();
                    }
                  });
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

  buySelcomPayApiCall(
      {Invoicesearchlist? plan, ActivePaymentConfig? selectedData}) {
    isLoading = true;
    update();
    double? lastAmount = 0.0;
    if (plan != null && plan.adjustedAmount == null) {
      lastAmount = plan.totalamount! - 0;
    } else {
      lastAmount = plan!.totalamount! - plan.adjustedAmount!;
    }

    CustomerPaymentDTO customerPaymentDTO = CustomerPaymentDTO(
      customerId: customerDetail!.id,
      buid: customerDetail!.buId,
      planId: customerDetail!.planMappingList![0].planId,
      amount: (plan.totalamount! - (plan.adjustedAmount != null ? plan.adjustedAmount!  : 0)).toString(),
      isBuyPlan: true,
      isFromCaptive: true,
      merchantName: "SELCOM",
      customerUserName: customerDetail!.username ?? "",
      customerUUID: Uuid().v4(),
      mvnoId: customerDetail!.mvnoId,
      mobileNumber: customerDetail!.countryCode!.replaceAll("+", "") +
          customerDetail!.mobile!,
      orderId: null,
      invoiceId: plan.id,
      partnerId: customerDetail!.partnerid,
      partnerPaymentId: customerDetail!.partnerPaymentId ?? null,
      status: customerDetail!.status,
      custServiceMappingId:
          customerDetail!.planMappingList![0].custServiceMappingId,
      requestFor: customerDetail!.requestFor ?? null,
    );

    SelcomPayPayment selcomPayPayment = SelcomPayPayment(
        vendor: "",
        orderId: null,
        buyerEmail: customerDetail!.email,
        buyerName: customerDetail!.username,
        buyerPhone: customerDetail!.countryCode!.replaceAll("+", "") +
            customerDetail!.mobile!,
        gatewayBuyerUuid: "",
        amount: lastAmount.toString(),
        currency: "",
        paymentMethods: "",
        billingFirstname: customerDetail!.firstname ?? "",
        billingLastname: customerDetail!.lastname ?? "",
        billingAddress1: customerDetail!.addressList![0].landmark ?? "",
        billingCity: presentAddress != null
            ? presentAddress!.cityName
            : "",
        billingStateOrRegion: presentAddress != null
            ? presentAddress!.stateName
            : "",
        billingCountry: presentAddress != null
            ? presentAddress!.countryName
            : "",
        billingPhone: customerDetail!.countryCode!.replaceAll("+", "") +
            customerDetail!.mobile!,

        noOfItems: 1,
        webhook: "");

    SelcomPayRequest request = SelcomPayRequest(
        selcomPayPayment: selcomPayPayment,
        customerPaymentDTO: customerPaymentDTO);

    PaymentProvider().requestBuyPlanUsingSelcom(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SelcomPaymentRes responseData = SelcomPaymentRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.data != null) {
                  _launchUrl(Uri.parse(responseData.data!.data!));
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        _handleSelcomApiError(error);
      },
    );
  }

  Future<void> _launchUrl(Uri _url) async {
    if (!await launchUrl(_url)) {
      throw Exception('Could not launch $_url');
    }
  }

  airtelPayApiCall(
      {Invoicesearchlist? plan,
      ActivePaymentConfig? selectedData,
      BuildContext? context,String? mobileNumber}) {
    double? lastAmount = 0.0;
    if (plan != null && plan.adjustedAmount == null) {
      lastAmount = plan.totalamount! - 0;
    } else {
      lastAmount = plan!.totalamount! - plan.adjustedAmount!;
    }
    isLoading = true;
    update();
    ArtelPaymentReq request = ArtelPaymentReq(
        customerId: customerDetail!.id,
        planId: null,
        amount: lastAmount.toString(),
        isFromCaptive: false,
        merchantName: "AIRTEL",
        customerUserName: customerDetail!.username,
        mvnoId: customerDetail!.mvnoId,
        mobileNumber: mobileNumber,
        invoiceId: plan.id,
        partnerId: customerDetail!.partnerid,
        accountNumber: customerDetail!.acctno ?? "");
    PaymentProvider().airtelPaymentRequest(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ArtelPaymentRes responseData = ArtelPaymentRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.data != null) {
                  getStorage.write(Constant.TRANSACATION_ID,
                      responseData.data!.data!.transaction!.id);
                  timer = Timer.periodic(const Duration(seconds: 1), (timer) {
                    if (paymentstatusCount > 0) {
                      paymentstatusCount -= 1;
                      // getStatusSuccessByMomo("SUCCESSFUL", context);
                      paymentConfirmationDialog(context);
                      if (transactionStatus == true) {
                        timer.cancel();
                      }
                    }
                    if (paymentstatusCount == 0) {
                      timer.cancel();
                    }
                  });
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        _handleSelcomApiError(error);
      },
    );
  }

  getStatusSuccessByMomo(String? status, BuildContext? context) {
    if (getStorage.read(Constant.TRANSACATION_ID) != null) {
      transactionId = getStorage.read(Constant.TRANSACATION_ID);
    }
    GetPaymentStatusReq request =
        GetPaymentStatusReq(orderId: transactionId, status: status);

    PaymentProvider().paymentStatusRequest(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetPaymentStatusRes responseData =
                  GetPaymentStatusRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                // if (responseData.data!.istransactionsuccess == "true")) {
                paymentConfirmationDialog(context);
                // }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        _handleSelcomApiError(error);
      },
    );
  }

  paymentConfirmationDialog(BuildContext? context) {
    showDialog(
        context: context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return const PaymentStatusDialog(
              titleMsg: Strings.paymentConfirmationMsg);
        });
  }

  _handleSelcomApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == 417) {
      Utils.showSnackbar(Strings.ERROR, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    } else if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
