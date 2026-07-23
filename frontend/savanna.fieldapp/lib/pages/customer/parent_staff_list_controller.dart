import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ParentStaffListController extends GetxController {

  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  List<ParentStaffUserlist> parentStaffList =[];
  ParentStaffRes? staffListResponse;
  String type = Strings.prepaid;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (staffListResponse != null &&
            staffListResponse?.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getStaffListData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    getStaffListData();
  }

  getStaffListData() {
    PageRequest customerReq = PageRequest(
      page: page,
      pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
    );
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getParentStaffList(
      pageRequest: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ParentStaffRes responseData = ParentStaffRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                staffListResponse = responseData;
                if (page == 1) {
                  parentStaffList.clear();
                }
                if (responseData.staffUserlist != null &&
                    responseData.staffUserlist!.isNotEmpty) {
                  parentStaffList.addAll(responseData.staffUserlist!);
                }
              } else {
                if (page == 1) {
                  parentStaffList.clear();
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
              log("Exception *****> ${e}");
              print(e.toString());
            }
          }
        } else {
          if (page == 1) {
            parentStaffList.clear();
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
        log("ParentCustomerControllerList #### >> $error");
        isShowLoadMore = false;
        if (page == 1) {
          parentStaffList.clear();
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