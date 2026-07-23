import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/response/customer_caf_invoice_details_res.dart';
import 'package:savbill/pages/customer_invoice/cust_invoice_detail/model/cust_invoice_detail_res.dart';
import 'package:savbill/pages/dashboard/model/response/invoice_list_response.dart';
import 'package:savbill/pages/dashboard/model/response/payment_configuration_res.dart';
import 'package:savbill/pages/dashboard/payment_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class CustomerInvoiceDetailController extends GetxController {
  bool isLoading = false;
  int customerId = 0;
  bool isShowLoadMore = false;
  int page = 1;
  Dio dio = Dio();
  GetStorage getStorage = GetStorage();
  BuildContext? context;
  CustomerDetail? customerDetail;
  InvoiceDetails? custInvoiceDetails;

  // Invoicesearchlist? invoiceDetail;
  InvoiceDetail? invoiceDetail;
  bool? displayTaxDetails;

  String? currencySymbol;

  // List<DebitDocumentTAXReels>? debitDocumentTAXRels = [];
  // List<DebitDocumentTAXRelDtos>? debitDocumentTAXRelDtos = [];

  List<DebitDocumentTAXReels>? debitDocumentTAXRels = [];
  List<DebitDocDetail>? debitDocDetails =[];
  List<DebitDocumentTAXRelDtos>? debitDocumentTAXRelDtos =[];

  List<DebitDocumentTAXReels>? taxData = [];
  List<DebitDocumentTAXRelDtos>? taxTotalData = [];

  DateFormat apiDateFormat = DateFormat(Constant.DATE_FORMAT);
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      if (arguments[Constant.INVOICE_DETAIL] != null) {
        invoiceDetail = arguments[Constant.INVOICE_DETAIL];
      }

      if(invoiceDetail != null) {
        getCustomerInvoiceDetail(invoiceDetail?.custid!, invoiceDetail?.id!);
      }
    }
    context = Get.key.currentContext;
  }

  getCustomerInvoiceDetail(int? customerId,int? id) {
    isLoading = true;
    update();
    CustomerProvider().getCustomerInvoiceDetail(
      customerId: customerId!,
      id: id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerInvoiceDetailRes responseData =
              CustomerInvoiceDetailRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {

                custInvoiceDetails = responseData.invoiceDetails;
                debitDocDetails!.addAll(responseData.debitDocDetails!);
                debitDocumentTAXRels!.addAll(responseData.debitDocumentTAXRels!);
                debitDocumentTAXRelDtos!.addAll(responseData.debitDocumentTAXRelDtos!);
                // customerDetail = responseData.customers;

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


  openTaxModel(int? debitDocumentId,String? chargeType){
    if(chargeType!.equalsIgnoreCase("charge")) {
      taxData =
          debitDocumentTAXRels!.where((element) => element.documentDetailId ==
              debitDocumentId).toList();
    }else{
      taxData = debitDocumentTAXRels;
    }

    if(taxData!.isNotEmpty){
      displayTaxDetails = true;
    }else{
      Utils.showSnackbar(Strings.INFO, "Tax Data Not Found!",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }

  }

  openTotalTaxModel(){
     taxTotalData = debitDocumentTAXRelDtos;
     if(taxTotalData!.isNotEmpty){
       displayTaxDetails = true;
     }else{
       Utils.showSnackbar(Strings.INFO, "Tax Data Not Found!",
           AppTheme.colorWhite, AppTheme.colorBlueRView);
     }
  }

}