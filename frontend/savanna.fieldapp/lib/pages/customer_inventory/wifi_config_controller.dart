import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/other_inventory_assign_req.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/product_plan_service_inventory_res.dart';
import 'package:savbill/pages/customer/model/response/update_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/response/all_inventory_spec_by_item_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_non_trackable_product_qty_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_item_delete_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/product_non_trackable_product_category_res.dart';
import 'package:savbill/pages/customer_inventory/response/wifi_config.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/request/cust_inventory_new_req.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

import '../../webservices/url_constants.dart';
import '../customer/model/response/inventory_job_type.dart';
import '../customer/model/response/nature.dart';

class WifiConfigController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool checkBtnClickEvent = false;
  TextEditingController userController = TextEditingController();
  TextEditingController passwordController = TextEditingController();

  List<DropdownDetail>? frequencyList = [];
  DropdownDetail? selectedFrequency;
  final CustomerInventoryDataList? item;

  WifiConfigController({this.item});

  @override
  void onInit() {
    super.onInit();
    frequencyList!.clear();
    frequencyList!.add(DropdownDetail(id: "0", text: "2.4G", type: ""));
    frequencyList!.add(DropdownDetail(id: "1", text: "5G", type: ""));
    // selectedFrequency ??= frequencyList![1];
    initPlatformState();
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
    getWifiConfig();
  }

  getWifiConfig() {
    isLoading = true;
    update();
    Map<String, dynamic> request = {
      "customerId": item?.customerId,
      "custInvenId": item?.id,
      "itemId": item?.itemId,
      "serialNumber": item!.inOutWardMACMapping?[0].serialNumber
    };
    CustomerProvider().getWifiConfig(
      data: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WifiConfigResponse responseData =
                  WifiConfigResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  if (responseData.data?.ssidUsername != null) {
                    userController.text = responseData.data!.ssidUsername!;
                  }
                  if (responseData.data?.ssidPassword != null) {
                    passwordController.text = responseData.data!.ssidPassword!;
                  }
                  if (responseData.data?.workingFrequency != null) {
                    if (responseData.data!.workingFrequency == "0") {
                      selectedFrequency ??= frequencyList![0];
                    } else {
                      selectedFrequency ??= frequencyList![1];
                    }
                  }
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
      onError: (ResponseModel error) {},
    );
  }

  saveNMSWifiConfig() {
    isLoading = true;
    update();
    Map<String, dynamic> request = {
      "customerId": item?.customerId,
      "custInvenId": item?.id,
      "itemId": item?.itemId,
      "serialNumber": item!.inOutWardMACMapping?[0].serialNumber,
      "ssidPassword": passwordController.text.trim(),
      "ssidUsername": userController.text.trim(),
      "workingFrequency": selectedFrequency?.id,
    };
    CustomerProvider().saveNMSWifiConfig(
      data: request,
      onSuccess: (ResponseModel responseModel) async {
        Map<String, dynamic> map = responseModel.result;
        final int responseCode = map['responseCode'] ?? 0;
        final String responseMessage = map['responseMessage'] ?? '';
        if (responseCode == 200 &&
            responseMessage != "resource does not exist") {
          Get.back();
          Utils.showSnackbar(Strings.SUCCESS, responseMessage,
              AppTheme.colorWhite, AppTheme.colorGreen);
        } else {
          Utils.showSnackbar(Strings.ERROR, responseMessage,
              AppTheme.colorWhite, AppTheme.colorRed);
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

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }
}
