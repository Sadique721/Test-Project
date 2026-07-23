import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/warranty_status_req_item.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class InventoryWarrantyChangeController extends GetxController {
  bool isLoading = false;

  List<InventoryListDetail>? inventoryList = [];

  List<WarrantyStatusDetail>? warrantyStatusList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.INVENTORY_ITEMS] != null) {
        inventoryList = arguments[Constant.INVENTORY_ITEMS];
      }
    }
    update();
    getWarrantyStatusData();
  }

  getWarrantyStatusData() {
    isLoading = true;
    warrantyStatusList!.clear();
    update();
    if (Utils.warrantyStatusListForFilter != null &&
        Utils.warrantyStatusListForFilter!.isNotEmpty) {
      warrantyStatusList?.addAll(Utils.warrantyStatusListForFilter!);
      isLoading = false;
      update();
    } else {
      InventoryManagementProvider().getWarrantyStatusList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                WarrantyStatusRes responseData =
                    WarrantyStatusRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    warrantyStatusList?.addAll(responseData.dataList!);
                    Utils.warrantyStatusListForFilter!
                        .addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
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
  }

  warrantyItemRequest() {
    List<WarrantyStatusReqItem> listData = [];
    inventoryList!.forEach((element) {
      listData.add(WarrantyStatusReqItem(
          id: element.id, warranty: element.selectedWarranty!.value));
    });

    isLoading = true;
    update();
    InventoryManagementProvider().changeWarrantyStatusReq(
      request: listData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                Get.back(result: true);
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.responseMessage!,
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
