import 'dart:convert';
import 'dart:developer';
import 'dart:io';
import 'package:savbill/pages/change_plan/change_plan_controller.dart';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/credit_note/response/credit_invoice_list_res.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/payment_mode_list_res.dart';
import 'package:savbill/pages/dashboard/model/request/get_all_case_request.dart';
import 'package:savbill/pages/dashboard/model/request/record_payment_req.dart';
import 'package:savbill/pages/dashboard/model/response/bank_list_res.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/pages/dashboard/payment_tab_controller.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/model/file_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class RecordPaymentController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false, tds = false, abbs = false;

  List<TextEditingController> textEditControllersAmount = [];
  List<TextEditingController> textEditControllersTDS = [];
  List<TextEditingController> textEditControllersABBS = [];

  Map<String, String> quantities = {};

  TextEditingController amountController = TextEditingController();
  TextEditingController barterAmountController = TextEditingController();
  TextEditingController paymentDateController = TextEditingController();
  TextEditingController chequeNoController = TextEditingController();
  TextEditingController chequeDateController = TextEditingController();
  TextEditingController transactionDateController = TextEditingController();
  TextEditingController refNoController = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  TextEditingController paymentRefNoController = TextEditingController();
  TextEditingController bankController = TextEditingController();
  TextEditingController sourceBankController = TextEditingController();
  TextEditingController branchController = TextEditingController();
  TextEditingController receiptNoController = TextEditingController();
  TextEditingController tdsController = TextEditingController();
  TextEditingController abbsController = TextEditingController();
  TextEditingController invoiceController = TextEditingController();

  TextEditingController createCustomerController = TextEditingController();

  DateTime? selectedPaymentDate, selectedChequeDate, selectedTransactionDate;
  String? selectedPaymentDateApi = "",
      selectedChequeDateApi = "",
      selectedTransactionDateApi = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);

  List<CustomerDetail>? customerList = [];
  CustomerDetail? selectedCustomer;

  // List<CustListDetails>? customerList = [];
  // CustListDetails? selectedCustomer;
  List<CustomerCreditList> newCustomerList = [];
  CustomerCreditList? newSelectedCustomer;

  List<CreditInvoiceList>? custNewList = [];
  CreditInvoiceList? selectedNewCustDetails;

  List<PaymentModeDetail>? paymentModeList = [];
  PaymentModeDetail? selectedPayMode;

  List<InvoiceDetail>? invoiceList = [];
  InvoiceDetail? selectedInvoices;

  List<int> selectedTdsAmount = [];
  List<int> selectedABBSAmount = [];

  List<int> selectedInvoice = [];

  List<PaymentListPojos> paymentListPojo = [];
  PaymentListPojos paymentPojo = PaymentListPojos();
  bool? isInvoiceListEmpty = false;

  List<BankDetail>? sourceBankList = [];
  List<BankDetail>? destinationBankList = [];
  BankDetail? selectedDestinationBank;
  BankDetail? selectedSourceBank;
  ConfigurationDetail? tdsConfigurationDetail, abbsConfigurationDetail;

  List<StatusDetail>? paymentModeSourceStatusList = [];
  StatusDetail? selectPaymentModeSource;

  RxDouble? tdsValue = 0.0.obs;
  RxDouble? abbsValue = 0.0.obs;

  FileDetail? fileDetail;
  String? form;

  RxBool isDestinationBank = false.obs,
      isOnline = false.obs,
      isChequeMode = false.obs,
      isNEFTRTGS = false.obs,
      isDirectPayment = false.obs,
      isSourceTypeDisable = false.obs;

  // chequeDate = false.obs,
  // receiptNo = false.obs,
  // branch = false.obs,
  // destinationBank = false.obs,
  // bankManagement = false.obs,
  // chequeNo = false.obs;

  @override
  void onInit() {
    super.onInit();
    selectedPaymentDateApi = apiDateFormat.format(DateTime.now());

    initPlatformState();
  }

  Future<void> initPlatformState() async {
    getArgumentData();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      // if (arguments[Constant.CUSTOMER_LIST] != null) {
      //   customerList = arguments[Constant.CUSTOMER_LIST];
      // }
      //
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        CustomerDetail cd = arguments[Constant.CUSTOMER_DETAIL];
        // customerList!.add(cd);
        selectedCustomer = cd;
      }

      if (arguments[Constant.FROM] != null) {
        form = arguments[Constant.FROM];
      }
      update();
    }

    log("formType===>${form}");

    update();

    if (form!.equalsIgnoreCase("Customer Payment")) {
      log("selectedCustomer===>${selectedCustomer!.id}");
      log("selectedCustomer===>${jsonEncode(selectedCustomer)}");
      selectedInvoice = [];
      invoiceController.clear();
      getInvoiceListData(selectedCustomer!.id);
      createCustomerController.text =
          "${selectedCustomer?.title} ${selectedCustomer?.firstname} ${selectedCustomer?.lastname}";
      update();
    }
    getCustomerListData();
    // getPaymentMode();
  }

  getCreditInvoiceListData(int creditInvoiceId) {
    String apiUrl = UrlConstants.credit_invoice_list;
    isLoading = true;
    update();
    ViewCreditNoteProvider().getCreditInvoiceList(
      url: apiUrl,
      invoiceId: creditInvoiceId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreditInvoiceListRes responseData =
                  CreditInvoiceListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.invoiceList != null &&
                    responseData.invoiceList!.isNotEmpty) {
                  custNewList?.clear();
                  custNewList?.addAll(responseData.invoiceList!);
                } else {
                  invoiceList?.clear();
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  calculateABBSTDS(String? amountValue, int index) {
    if (tds && tdsConfigurationDetail != null) {
      // double amt = double.parse(amountController.text);
      double amt = double.parse(amountValue!);
      double tdsPer = 0, tdsAmt = 0;
      if (tdsConfigurationDetail!.value != null &&
          tdsConfigurationDetail!.value!.isNotEmpty) {
        tdsPer = double.parse(tdsConfigurationDetail!.value!);
      }
      if (tdsPer > 0) {
        tdsAmt = amt * tdsPer / 100;
      }
      textEditControllersTDS[index].text = tdsAmt.toString();
    } else {
      tdsController.text = "0";
      textEditControllersTDS[index].text = "0";
    }

    if (abbs && abbsConfigurationDetail != null) {
      // double amt = double.parse(amountController.text);
      double amt = double.parse(amountValue!);
      double abbsPer = 0, abbsAmt = 0;
      if (abbsConfigurationDetail!.value != null &&
          abbsConfigurationDetail!.value!.isNotEmpty) {
        abbsPer = double.parse(abbsConfigurationDetail!.value!);
      }
      if (abbsPer > 0) {
        abbsAmt = amt * abbsPer / 100;
      }
      textEditControllersABBS[index].text = abbsAmt.toString();
    } else {
      textEditControllersABBS[index].text = "0";
    }

    update();
  }

  getPaymentMode() {
    isLoading = true;
    selectedPayMode = null;
    paymentModeList?.clear();
    update();
    CustomerProvider().getPaymentMode(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentModeListRes responseData =
                  PaymentModeListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  paymentModeList?.addAll(responseData.dataList!);
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
        getBankListData("other");
        // getSystemConfigurationData(Strings.TDS);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getBankListData("other");
        // getSystemConfigurationData(Strings.TDS);
      },
    );
  }

  getPaymentModeSourceType(String? paymentModeSourceType) {
    isLoading = true;
    paymentModeSourceStatusList!.clear();
    selectPaymentModeSource = null;
    update();
    PaymentProvider().getPaymentModeSourceStatus(
      paymentStatus: paymentModeSourceType ?? "",
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StatusRes responseData = StatusRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  paymentModeSourceStatusList?.addAll(responseData.dataList!);
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
      },
      onError: (ResponseModel error) {
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
                  if (type.isNotEmpty && type.equalsIgnoreCase(Strings.TDS)) {
                    tdsConfigurationDetail = responseData.data;
                  }
                  if (type.isNotEmpty && type.equalsIgnoreCase(Strings.ABBS)) {
                    abbsConfigurationDetail = responseData.data;
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
        if (type.isNotEmpty && type.equalsIgnoreCase(Strings.TDS)) {
          getSystemConfigurationData(Strings.ABBS);
        }
        if (type.isNotEmpty && type.equalsIgnoreCase(Strings.ABBS)) {
          // getBankListData();
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (type.isNotEmpty && type.equalsIgnoreCase(Strings.TDS)) {
          getSystemConfigurationData(Strings.ABBS);
        }
        if (type.isNotEmpty && type.equalsIgnoreCase(Strings.ABBS)) {
          // getBankListData();
          // getBankListData("operator");
        }
      },
    );
  }

  getBankListData(String? operatorType) {
    isLoading = true;
    if (operatorType!.equalsIgnoreCase("other")) {
      sourceBankList?.clear();
    } else {
      destinationBankList?.clear();
    }
    update();
    PaymentProvider().getBankListData(
      bankType: operatorType,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BankListRes responseData = BankListRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  if (operatorType.equalsIgnoreCase("other")) {
                    sourceBankList?.addAll(responseData.dataList!);
                  } else {
                    destinationBankList?.addAll(responseData.dataList!);
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
        getSystemConfigurationData(Strings.TDS);
      },
      onError: (ResponseModel error) {
        getSystemConfigurationData(Strings.TDS);
        _handleApiError(error);
      },
    );
  }

// get invoice list by customer
  getInvoiceListData(int? selectedCustomerId) {
    isLoading = true;
    invoiceList?.clear();
    update();
    PaymentProvider().getInvoiceListOfCustomer(
      customerId: selectedCustomerId.toString(),
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InvoiceListResponse responseData =
                  InvoiceListResponse.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.invoiceList != null &&
                    responseData.invoiceList!.isNotEmpty) {
                  invoiceList?.addAll(responseData.invoiceList!);
                  isInvoiceListEmpty = true;
                } else {
                  invoiceList?.clear();
                  isInvoiceListEmpty = false;
                  invoiceList?.add(InvoiceDetail(
                      id: 0,
                      docnumber: Strings.advance,
                      tax: 0.0,
                      totalamount: 0.0,
                      adjustedAmount: 0.0,
                      pendingAmt: 0.0,
                      refundAbleAmount: 0.0,
                      subtotal: 0.0,
                      tdsCheck: false,
                      abbsCheck: false));
                }

                invoiceList?.forEach((element) {
                  // element['tdsCheck'] = 0;
                  // element['abbsCheck'] = 0;
                  // element['includeTds'] = false;
                  // element['includeAbbs'] = false;
                  // element['isSelected'] = false;
                  element.tdsCheck = false;
                  element.abbsCheck = false;
                  element.selected = false;
                  if (element.adjustedAmount != null) {
                    element.testamount =
                        (element.totalamount! - element.adjustedAmount!);
                  } else {
                    element.testamount = element.totalamount;
                  }
                });
              } else {
                invoiceList?.clear();
                invoiceList
                    ?.add(InvoiceDetail(id: 0, docnumber: Strings.advance));
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
          invoiceList?.clear();
          invoiceList?.add(InvoiceDetail(id: 0, docnumber: Strings.advance));
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        invoiceList?.clear();
        invoiceList?.add(InvoiceDetail(id: 0, docnumber: Strings.advance));
        _handleApiError(error);
      },
    );
  }

  getCustomerListData() {
    isLoading = true;
    update();
    GetAllCaseRequest getAllCaseRequest =
        GetAllCaseRequest(page: 1, pageSize: 10);
    PaymentProvider().getCustomerList(
      getAllCaseRequest: getAllCaseRequest,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // CustomerListRes responseData = CustomerListRes.fromJson(map);
              CustomerCreditNoteRes responseData =
                  CustomerCreditNoteRes.fromJson(map);
              // CustomerDetailResponse responseData = CustomerDetailResponse.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  newCustomerList.clear();
                  newCustomerList.addAll(responseData.customerList!);
                  // log("customerListLengtht${customerList!.length}");
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
        getPaymentMode();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        update();
        getPaymentMode();
      },
    );
  }

  void callRecordPaymentApiCall() async {
    String? chequeNo;
    num amount = 0;
    if (chequeNoController.text.isNotEmpty) {
      chequeNo = chequeNoController.text;
    }
    if (amountController.text.isNotEmpty) {
      amount = double.parse(amountController.text);
    }
    String payType = "invoice";
    if (selectedInvoice.isNotEmpty &&
        selectedInvoice.length == 1 &&
        selectedInvoice[0] == 0) {
      payType = "advance";
    }

    double abbsAmt = 0, tdsAmt = 0;

    if (tdsController.text.isNotEmpty) {
      tdsAmt = double.parse(tdsController.text);
    }
    String? onlineMode;
    if (abbsController.text.isNotEmpty) {
      abbsAmt = double.parse(abbsController.text);
    }

    if (selectPaymentModeSource != null &&
        selectPaymentModeSource!.value != null) {
      onlineMode = selectPaymentModeSource!.value;
    } else {
      onlineMode = "";
    }
    isLoading = true;
    update();

    RecordPaymentReq recordPaymentReq = RecordPaymentReq(
        amount: amount,
        chequedate: selectedChequeDateApi,
        chequeno: chequeNo,
        // customerid: selectedCustomer!.id,
        customerid: newSelectedCustomer != null
            ? newSelectedCustomer!.id
            : selectedCustomer!.id,
        invoiceId: selectedInvoice,
        paymentdate: selectedPaymentDateApi,
        onlinesource: onlineMode,
        paymentListPojos: paymentListPojo,
        paymode: selectedPayMode!.value,
        referenceno:
            refNoController.text.isNotEmpty ? refNoController.text : null,
        remark:
            remarksController.text.isNotEmpty ? remarksController.text : null,
        bank: bankController.text.isNotEmpty ? bankController.text : null,
        branch: branchController.text.isNotEmpty ? branchController.text : null,
        payReferenceNo: paymentRefNoController.text.isNotEmpty
            ? paymentRefNoController.text
            : null,
        type: "Payment",
        paytype: payType,
        filename: "",
        reciptNo: receiptNoController.text,
        abbsAmount: abbsValue!.value,
        tdsAmount: tdsValue!.value,
        destinationBank: selectedDestinationBank != null
            ? selectedDestinationBank!.id.toString()
            : null,
        bankManagement: selectedSourceBank != null
            ? selectedSourceBank!.id.toString()
            : null);

    // print("Request Data ==> ${jsonEncode(recordPaymentReq)}");

    Map<String, dynamic> map = {};
    if (fileDetail != null &&
        fileDetail!.filePathLocal != null &&
        fileDetail!.filePathLocal!.isNotEmpty) {
      File f = File(fileDetail!.filePathLocal!);
      String fileName = f.path.split('/').last;
      dia.MultipartFile multipartFile =
          await dia.MultipartFile.fromFile(f.path, filename: fileName);
      map["file"] = multipartFile;
      recordPaymentReq.filename = fileName;
    }
    // print("Request Data ==> ${jsonEncode(recordPaymentReq)}");
    map["spojo"] = jsonEncode(recordPaymentReq);
    dia.FormData formData = dia.FormData.fromMap(map);

    PaymentProvider().recordPaymentRequest(
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if ((responseData.status != null && responseData.status == 200) ||
                (responseData.responseCode != null &&
                    responseData.responseCode == 200)) {

              // getCustomerListData();
              // final paymentController = Get.find<PaymentTabController>();
              // paymentController.isFilterApply = false;
              // paymentController.isFirstTime = true;
              Get.back(result: true);
            } else {
              if (responseData.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
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

  void updateControllerValue(int index, String value) {
    textEditControllersAmount[index].text = value;
    // Update the UI when value changes
    update();
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
