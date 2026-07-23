import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/workflow/model/cust_audit_detail_req.dart';
import 'package:savbill/pages/workflow/model/cust_audit_details_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';

class AuditDetailController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  List<AuditDetailList>? custAuditDetailsList = [];
  CustAuditDetailRes? custAuditDetailRes;
  CustomerDetail? customerDetail;
  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (custAuditDetailRes != null &&
            custAuditDetailRes!.totalPages != page) {
          if (custAuditDetailRes!.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            auditDetailsApiCall(customerDetail!.id);
          }
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {

      if(arguments[Constant.CUSTOMER_DETAIL] != null){
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      auditDetailsApiCall(customerDetail!.id);
    }
    update();
  }

  auditDetailsApiCall(int? customerId) {
    isShowLoadMore = true;
    if (page == 1) {
      isLoading = true;
      custAuditDetailsList?.clear();
    }
    update();
    CustomerProvider().customerAuditDetail(
      custId: customerId,
      request: CustAuditDetailReq(page: page, pageSize: Constant.PAGE_LOAD_DATA_LIMIT,sortBy: "id",sortOrder: 0),
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (page == 1) {
            custAuditDetailsList?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustAuditDetailRes responseData = CustAuditDetailRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  custAuditDetailsList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  custAuditDetailsList?.clear();
                }
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            } on Exception catch (e) {
              log("Exception:-${e.toString()}");
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
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          custAuditDetailsList?.clear();
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