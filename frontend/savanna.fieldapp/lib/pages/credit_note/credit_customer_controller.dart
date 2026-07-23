import 'dart:developer';

import 'package:savbill/util/Extensions.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

import '../../theme/app_theme.dart';
import '../../util/constant.dart';
import '../../util/strings.dart';
import '../../util/utils.dart';
import '../../webservices/response_model.dart';
import '../customer/model/request/custmer_list_request.dart';
import 'credit_note_provider.dart';
import 'response/customer_credit_res.dart';

class CreditCustomerController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<CustomerCreditList>? parentCustomerList = [];
  CustomerCreditNoteRes? customerListResponse;
  String type = Strings.prepaid;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
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
    getCustomerListData();
   /* if (arguments != null) {
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
        getCustomerListData();
      }
    }*/
  }

  getCustomerListData() {
    CustomerListRequest customerReq = CustomerListRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    ViewCreditNoteProvider().getCustomerList(
      customerListRequest: customerReq,
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
                  parentCustomerList?.clear();
                }
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  parentCustomerList?.addAll(responseData.customerList!);
                }
              } else {
                if (page == 1) {
                  parentCustomerList?.clear();
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
            parentCustomerList?.clear();
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
        log("else CustomerCreditNoteRes$error");
        isShowLoadMore = false;
        if (page == 1) {
          parentCustomerList?.clear();
        }
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