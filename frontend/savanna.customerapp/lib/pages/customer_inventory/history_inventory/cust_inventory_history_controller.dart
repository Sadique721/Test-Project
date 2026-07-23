import 'dart:convert';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer_inventory/response/get_customer_inventory_details_history_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CustInventoryHistoryController extends GetxController{
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  int customerId = 0;
  int? serviceAreaId = 0;
  String? customerType;

  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;

  List<InventoryHistoryDataList>? inventoryHistoryDataList=[];
  InventoryHistoryDataList? inventoryHistoryDataDetails;
  GetCustomerInventoryHistoryRes? getCustomerInventoryHistoryRes;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        // if (inventoryListRes != null && inventoryListRes!.totalPages != page) {
        if (GetCustomerInventoryHistoryRes != null &&
            getCustomerInventoryHistoryRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          getAllMACMappingByExternalIdCall();
          update();
        }
      }
    });
  }


  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
      if (arguments[Constant.SERVICE_AREA_ID] != null) {
        serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      }
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
    }
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
    getAllMACMappingByExternalIdCall();
  }


  getAllMACMappingByExternalIdCall() {
    inventoryHistoryDataList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getAllCustomerInventoryDetailsHistory(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetCustomerInventoryHistoryRes responseData =
              GetCustomerInventoryHistoryRes.fromJson(map);

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inventoryHistoryDataList?.addAll(responseData.dataList!);
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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