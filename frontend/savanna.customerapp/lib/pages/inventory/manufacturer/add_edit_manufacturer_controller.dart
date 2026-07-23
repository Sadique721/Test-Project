import 'dart:convert';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_update_manufacturer_req.dart';
import 'package:savbill/pages/inventory/module/response/add_update_manufacturer_res.dart';
import 'package:savbill/pages/inventory/module/response/manufacture_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class AddEditManufacturerController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController manufactureNameController = TextEditingController();

  UserDetail? userDetail;
  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;
  bool macAddress = false,
      serialNo = false,
      hasTrackable = false,
      hasPort = false,
      hasCas = false;
  String from = Strings.add;
  ManufacturerDetail? manufacturerDetail;

  @override
  void onInit() {
    super.onInit();
    statusList!.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));

    initPlatformState();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;

    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        manufacturerDetail = arguments[Constant.IM_DETAIL];
      }


      if (manufacturerDetail != null) {
        if (manufacturerDetail!.name != null && manufacturerDetail!.name!.isNotEmpty) {
          manufactureNameController.text = manufacturerDetail!.name!;
        }

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(manufacturerDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }
      }

    }
    update();
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
    // getCaseTypeListData();
  }

  void addEditManufacturerApiCall() {
    isLoading = true;
    update();
    AddUpdateManufactureReq request = AddUpdateManufactureReq(
        id: manufacturerDetail != null ? manufacturerDetail!.id : null,
        name: manufactureNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        mvnoId:manufacturerDetail != null ? manufacturerDetail!.mvnoId : null,
        isDelete: manufacturerDetail != null ? manufacturerDetail!.deleted : false,
        );

    InventoryManagementProvider().addEditManufactureManagement(
      isAdd: manufacturerDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              Get.back(result: true);
              Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorGreen);
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