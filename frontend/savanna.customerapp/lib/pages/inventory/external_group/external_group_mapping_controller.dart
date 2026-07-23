import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/external_lite_mac_mapping_req.dart';
import 'package:savbill/pages/inventory/module/response/external_group_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_external_lite_mac_mapping_res.dart';
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

class ViewExternalGrpMappingController extends GetxController {
  bool isLoading = false, showMacAddress = false, changeData = false;

  GetStorage getStorage = GetStorage();
  List<ExternalLiteMacMappingDetail>? externalGrpMacMapList = [];
  ExternalGroupDetail? externalGroupDetail;

  TextEditingController macAddController = TextEditingController();
  TextEditingController serialNoController = TextEditingController();

  final addExternalGrpMapFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  String from = Strings.view;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }

      if (arguments[Constant.IM_DETAIL] != null) {
        externalGroupDetail = arguments[Constant.IM_DETAIL];
        if (externalGroupDetail != null &&
            externalGroupDetail!.productId != null &&
            externalGroupDetail!.productId!.productCategory != null) {
          if (externalGroupDetail!.productId!.productCategory!.hasMac != null &&
              externalGroupDetail!.productId!.productCategory!.hasMac == true) {
            showMacAddress = true;
          }
        }
      }
    }
    update();
    getExternalGrpMacMappingData();
  }

  getExternalGrpMacMappingData() {
    externalGrpMacMapList!.clear();
    isLoading = true;
    update();
    InventoryManagementProvider().viewExternalGroupMacMap(
      id: externalGroupDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewExternalLiteMacMappingRes responseData =
                  ViewExternalLiteMacMappingRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  externalGrpMacMapList?.addAll(responseData.dataList!);
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

  void addExternalGrpMacMappingData() {
    isLoading = true;
    update();
    ExternalLiteMacMappingReq request = ExternalLiteMacMappingReq(
        id: null,
        externalItemId: externalGroupDetail!.id!,
        macAddress: macAddController.text,
        serialNumber: serialNoController.text,
        status: "ACTIVE");
    InventoryManagementProvider().addExternalGroupMacMap(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              changeData = true;
              macAddController.clear();
              serialNoController.clear();
              autoValidateMode = AutovalidateMode.disabled;
              getExternalGrpMacMappingData();
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
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
