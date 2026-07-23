import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/approve_inventory_owner_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ViewPopInventoryController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();

  List<PopInventoryDetail>? popInventoryList = [];
  // PopInventoryDetail? selectedPopInventoryDetail;
  ViewPopInventoryRes? viewPopInventoryRes;
  int? popId,ownerID;
  String? custName,ownerType;

  @override
  void onInit() {
    super.onInit();

    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewPopInventoryRes != null &&
            viewPopInventoryRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewPopInventoryData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.ID] != null) {
        popId = arguments[Constant.ID];
      }

      if (arguments[Constant.CUST_USERNAME] != null) {
        custName = arguments[Constant.CUST_USERNAME];
      }
    }
    update();
    if (popId != null) {
      viewPopInventoryData();
    }
  }

  viewPopInventoryData() {
    CustomerListRequest customerReq = CustomerListRequest(
        page: page,
        pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
        sortBy: "createdate",
        sortOrder: 0,
        filters: [
          Filters(filterColumn: "pop", filterValue: popId!.toString())
        ]);

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewPopInventoryList(
      request: customerReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewPopInventoryRes responseData =
                  ViewPopInventoryRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewPopInventoryRes = responseData;
                if (page == 1) {
                  popInventoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  popInventoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  popInventoryList?.clear();
                }
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
          if (page == 1) {
            popInventoryList?.clear();
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
          popInventoryList?.clear();
        }
        _handleApiError(error);
      },
    );
  }



  viewPopDetail(int? inventoryMappingId, String? inventoryRemarks,bool? isApproveReq) {
    isLoading = true;
    update();
    InventoryManagementProvider().approveInventoryFromOwner(
        inventoryRemark: inventoryRemarks,
        inventoryMappingId : inventoryMappingId,
      isApproveRequest: isApproveReq,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApproveInventoryFromOwnerRes responseData = ApproveInventoryFromOwnerRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null) {
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                  viewPopInventoryData();
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
