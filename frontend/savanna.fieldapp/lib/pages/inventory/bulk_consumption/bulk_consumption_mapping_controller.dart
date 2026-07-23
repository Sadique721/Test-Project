import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/bulk_consumption_mapping_res.dart';
import 'package:savbill/pages/inventory/module/response/view_bulk_consumption_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ViewBulkConsumptionMappingController extends GetxController {
  bool isLoading = false, showMacAddress = true, changeData = false;

  GetStorage getStorage = GetStorage();
  List<BulkConsumptionMappingDetail>? bulkConsumptionMacMapList = [];
  BulkConsumptionDetail? bulkConsumptionDetail;

  TextEditingController macAddController = TextEditingController();
  TextEditingController serialNoController = TextEditingController();

  final addBulkConsumptionMapFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  String from = Strings.view;

  // UserDetail? userDetail;

  @override
  void onInit() {
    super.onInit();
    // initPlatformState();
    getArgumentData();
  }

  Future<void> initPlatformState() async {
    /* String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
    getArgumentData();*/
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }

      if (arguments[Constant.IM_DETAIL] != null) {
        bulkConsumptionDetail = arguments[Constant.IM_DETAIL];
      }
    }
    update();
    if (bulkConsumptionDetail != null &&
        bulkConsumptionDetail!.productId != null) {
      getBulkConsumptionMacMappingData();
    }
  }

  getBulkConsumptionMacMappingData() {
    bulkConsumptionMacMapList!.clear();
    isLoading = true;
    update();
    InventoryManagementProvider().viewBulkConsumptionMacMap(
      id: bulkConsumptionDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BulkConsumptionMappingRes responseData =
                  BulkConsumptionMappingRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  bulkConsumptionMacMapList?.addAll(responseData.dataList!);
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
