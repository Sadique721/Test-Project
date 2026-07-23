import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/item_status_req_item.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class InventoryChangeStatusController extends GetxController {
  bool isLoading = false;

  List<InventoryListDetail>? inventoryList = [];

  List<StatusDetail>? itemStatusList = [];

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
    getItemStatusData();
  }


  getItemStatusData() {
    isLoading = true;
    itemStatusList!.clear();
    update();
    if (Utils.statusListForFilter != null &&
        Utils.statusListForFilter!.isNotEmpty) {
      itemStatusList?.addAll(Utils.statusListForFilter!);
      isLoading = false;
      update();
    } else {
      InventoryManagementProvider().getItemStatusList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                StatusRes responseData = StatusRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    itemStatusList?.addAll(responseData.dataList!);
                    Utils.statusListForFilter!.addAll(responseData.dataList!);
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

  itemStatusRequest() {
    List<ItemStatusReqItem> listData = [];
    inventoryList!.forEach((element) {
      listData.add(ItemStatusReqItem(
          id: element.id, itemStatus: element.selectedItemStatus!.value));
    });

    isLoading = true;
    update();
    InventoryManagementProvider().changeItemStatusReq(
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
