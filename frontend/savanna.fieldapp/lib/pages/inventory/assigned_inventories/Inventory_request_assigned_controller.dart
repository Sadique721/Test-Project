import 'dart:developer';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/all_ware_house_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_new_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../util/strings.dart';

class InventoryRequestAssignedController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  String? selectProductCategoriesValue;
  int? assignedInventoryId;
  List<FulfilmentProductMappings>? fulfilmentProductMapping = [];
  FulfilmentData? fulfilmentData;

  String? selectedRequesterTo;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.ID] != null) {
        assignedInventoryId = arguments[Constant.ID];
        update();
      }
    }
    getFulfilmentAllWareHouses(assignedInventoryId);

  }

  getFulfilmentAllWareHouses(int? assignedInventoryId) {
    isLoading = true;
    fulfilmentProductMapping?.clear();
    update();
    InventoryManagementProvider().fulfilmentInventoryRequest(
      fulFilmentId: assignedInventoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            RequestInventoryFulfilmentRes responseData =
            RequestInventoryFulfilmentRes.fromJson(map);
            if (responseData.responseCode != null &&
                responseData.responseCode == 200) {
              fulfilmentData = responseData.data!;
              if (fulfilmentData!.requestInvenotryProductMappings!.isNotEmpty) {
                for (var element in fulfilmentData!.requestInvenotryProductMappings!) {
                  fulfilmentProductMapping!.add(element);
                }
              }
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
