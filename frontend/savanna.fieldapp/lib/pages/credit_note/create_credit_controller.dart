import 'dart:convert';

import 'package:savbill/pages/credit_note/response/credit_invoice_list_res.dart';
import 'package:savbill/pages/credit_note/response/customer_credit_res.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:dio/dio.dart' as dia;
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../util/strings.dart';
import '../../util/utils.dart';
import '../../webservices/base_response.dart';
import '../../webservices/response_model.dart';
import '../../webservices/url_constants.dart';
import '../dashboard/model/request/record_payment_req.dart';
import '../dashboard/payment_provider.dart';
import 'credit_note_provider.dart';

class CreateCreditController extends GetxController{
  bool isLoading = false,isLoadFilterData = false,
  isLoadingProgress = false,filterViewOpen = false;
  double? pendingAmount;

  List<CustomerCreditList>? customerCreditList = [];

  // List<CreditInvoiceList>? invoiceList =[];
  CustomerCreditList? selectedCustomer;
  int page =1;
  String? type = "CUSTOMER_TYPE";
  ScrollController? controller;
  CustomerDetail? customerDetail;
  bool isShowLoadMore = false;
  CustomerCreditNoteRes? customerListResponse;
  TextEditingController createCustomerController = TextEditingController();
  TextEditingController invoiceController = TextEditingController();
  TextEditingController referenceNumberController = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  DateTime? selectedPaymentDate, selectedChequeDate;
  List<CreditInvoiceList>? invoiceList =[];
  CreditInvoiceList? selectedInvoice;
  var creditAmount = "".obs;
  String? selectedPaymentDateApi = "";
  List<int> selectedInvoiceIds = [];
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  List<int>? selectedAllTeamInventoryList = [];

  @override
  void onInit(){
    super.onInit();
    getArgumentData();
    selectedPaymentDateApi = apiDateFormat.format(DateTime.now());
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (customerListResponse != null &&
            customerListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getCustomerListData();
        }
      }
    });
  }
  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        selectedInvoice = null;
        invoiceList!.clear();
        createCustomerController.text = customerDetail!.username!;
        getCreditInvoiceListData(customerDetail!.id!);
        update();
      }
    }
  }

  getCustomerListData() {
    isLoadingProgress = true;
    update();
    CustomerListRequest getAllCaseRequest =
    CustomerListRequest(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT);
    ViewCreditNoteProvider().getCustomerList(
      customerListRequest: getAllCaseRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCreditNoteRes responseData =
              CustomerCreditNoteRes.fromJson(map);

              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerListResponse = responseData;
                if (page == 1) {
                  customerCreditList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  customerCreditList?.addAll(responseData.customerList!);
                }
              } else {
                if (page == 1) {
                  customerCreditList?.clear();
                }
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
          if (page == 1) {
            customerCreditList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          customerCreditList?.clear();
        }
        _handleApiError(error);
      },
    );
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
                  invoiceList?.clear();
                  invoiceList?.addAll(responseData.invoiceList!);
                } else {
                  invoiceList?.clear();
                }
              } else {
                if (responseData.message!.isNotEmpty) {
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  void callRecordPaymentApiCall() async {
    String? chequeNo = "";
    num amount = 0;
    if (creditAmount.isNotEmpty) {
      amount = double.parse(creditAmount.value);
    }
    String paymode = "Credit Note";
    double abbsAmt = 0, tdsAmt = 0;
    isLoading = true;
    update();

    RecordPaymentReq recordPaymentReq = RecordPaymentReq(
        amount: amount,
        chequeno: chequeNo,
        customerid: customerDetail== null ? selectedCustomer!.id : customerDetail!.id,
        invoiceId: selectedInvoiceIds,
        paymentdate: selectedPaymentDateApi,
        paymode: paymode,
        referenceno: referenceNumberController.text.isNotEmpty ? referenceNumberController.text : null,
        remark: remarksController.text.isNotEmpty ? remarksController.text : null,
        type: "creditnote",
        paytype: "creditnote",
        );
    print("Request Data ==> ${jsonEncode(recordPaymentReq)}");
    Map<String, dynamic> map = {};
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
              Get.back(result: true);
            } else {
              if (responseData.message!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        }
        else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }

        // if(responseModel.statusCode == 200){
        //   Get.back(result: true);
        // }
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
    isLoadingProgress = false;
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