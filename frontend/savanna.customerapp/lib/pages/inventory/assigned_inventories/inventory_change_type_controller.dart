import 'dart:convert';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/inventory_item_type_req.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class InventoryChangeTypeController extends GetxController {
  bool isLoading = false;

  List<InventoryListDetail>? inventoryList = [];

  List<ItemTypeDetail>? typeList = [];
  List<DropdownDetail>? remarkTypeList = [];

  @override
  void onInit() {
    super.onInit();
    remarkTypeList!.add(DropdownDetail(
        id: Strings.lost, text: Strings.lost, type: Strings.remarks));
    remarkTypeList!.add(DropdownDetail(
        id: Strings.theft, text: Strings.theft, type: Strings.remarks));
    remarkTypeList!.add(DropdownDetail(
        id: Strings.other, text: Strings.other, type: Strings.remarks));
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
    getItemTypeData();
  }

  getItemTypeData() {
    isLoading = true;
    typeList!.clear();
    update();
    if (Utils.itemTypeListForFilter != null &&
        Utils.itemTypeListForFilter!.isNotEmpty) {
      typeList?.addAll(Utils.itemTypeListForFilter!);
      isLoading = false;
      update();
    } else {
      InventoryManagementProvider().getItemTypeList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                ItemTypeRes responseData = ItemTypeRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    typeList?.addAll(responseData.dataList!);
                    Utils.itemTypeListForFilter!.addAll(responseData.dataList!);
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

  changeItemTypeRequest() {
    List<InventoryItemTypeReq> listData = [];
    inventoryList!.forEach((element) {
      listData.add(InventoryItemTypeReq(
          itemId: element.id,
          condition: element.selectedItemType != null
              ? element.selectedItemType!.value
              : "",
          remarks: element.selectedRemarkType != null
              ? element.selectedRemarkType!.id
              : "",
          otherreason: element.changeTypeRemarks ?? "",
          filename: ""));
    });
    Map<String, dynamic> map = {};
    map["entityDTOs"] = jsonEncode(listData);
   // map["entityDTOs"] =  [{"condition":"New","remarks":"Other","otherreason":"Test","itemId":"766","filename":""}];
    isLoading = true;
    update();
    InventoryManagementProvider().updateItemTypeReq(
      request: map,
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
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!,
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
