import 'dart:convert';

import 'package:savbill/pages/inventory_dashboard/model/get_product_qty_staff_res.dart';
import 'package:savbill/pages/inventory_dashboard/product_qty_staff_provider.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class GetProductQtyStaffController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<ProductQtyStaffDataList>? productQtyStaffList = [];
  GetProductQtyStaffRes? productQtyStaffRes;
  UserDetail? userDetail;
  String? inventoryType;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (productQtyStaffRes != null && productQtyStaffRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getProductQtyStaffApi();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.INVENTORY_TYPE] != null) {
        inventoryType = arguments[Constant.INVENTORY_TYPE];
      }
    }
    update();
    initPlatformState();
  }
  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }
    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
    }
    update();
    if(inventoryType!.equalsIgnoreCase(Strings.staff)) {
      getProductQtyStaffApi();
    }else{
      getProductQtyWarehouseApi();
    }
  }

  getProductQtyStaffApi() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    ProductQtyStaffProvider().getProductQtyStaff(
      mvNoId: userDetail!.mvnoId,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetProductQtyStaffRes responseData = GetProductQtyStaffRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                productQtyStaffRes = responseData;
                if (page == 1) {
                  productQtyStaffList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productQtyStaffList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  productQtyStaffList?.clear();
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
            productQtyStaffList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          productQtyStaffList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getProductQtyWarehouseApi() {
    PageRequest request = PageRequest(page: page, pageSize: 10);
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    ProductQtyStaffProvider().getProductQtyByWarehouse(
      mvNoId: userDetail!.mvnoId,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetProductQtyStaffRes responseData = GetProductQtyStaffRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                productQtyStaffRes = responseData;
                if (page == 1) {
                  productQtyStaffList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productQtyStaffList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  productQtyStaffList?.clear();
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
            productQtyStaffList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          productQtyStaffList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
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
