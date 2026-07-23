import 'dart:convert';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/external_item_details_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ExternalItemManagementController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  UserDetail? userDetail;
  int? externalId;
  String? externalItemName;
  ExternalItemDetailsData? externalItemDetailsData;


  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.EXTERNAL_ID] != null) {
        externalId = arguments[Constant.EXTERNAL_ID];
      }
      if (arguments[Constant.EXTERNAL_ITEM_NAME] != null) {
        externalItemName = arguments[Constant.EXTERNAL_ITEM_NAME];
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
      update();
    }
    if (externalId != null) {
      getExternalItemDetails(externalId);
    }
  }

  getExternalItemDetails(int? inwardId) {
    isLoading = true;
    update();
    InventoryManagementProvider().externalItemDetailsById(
      externalId: inwardId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ExternalItemDetailsRes responseData = ExternalItemDetailsRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  externalItemDetailsData = responseData.data;
                }
              } else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRedRView);
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
