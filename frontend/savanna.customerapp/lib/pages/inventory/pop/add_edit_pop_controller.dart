import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_pop_req.dart';
import 'package:savbill/pages/inventory/module/response/pop_detail_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class AddEditPopController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController popNameController = TextEditingController();
  TextEditingController popCodeController = TextEditingController();
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();
  TextEditingController servicesAreaController = TextEditingController();

  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String from = Strings.add;
  PopDetailData? popDetail;

  List<int> selectedServiceAreaIds = [];
  List<String> selectedServiceArea = [];
  List<StaffServiceAreaDetail>? serviceAreaList = [];

  LocationDetail? selectedLocation;
  bool checkBtnClickEvent = false;

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
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        popDetail = arguments[Constant.IM_DETAIL];
      }
      if (popDetail != null) {
        popNameController.text = popDetail!.name.toString();
        popCodeController.text = popDetail!.popCode.toString();
        latController.text = popDetail!.latitude!;
        longController.text = popDetail!.longitude!;
        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(popDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }

        if (popDetail!.serviceAreaNameList != null &&
            popDetail!.serviceAreaNameList!.isNotEmpty) {
          selectedServiceArea.clear();
          selectedServiceAreaIds.clear();
          String serviceAreaName = "";
          for (String element in popDetail!.serviceAreaNameList!) {
            serviceAreaName = "$serviceAreaName$element, ";
            selectedServiceArea.add(element);
          }

          for (int element in popDetail!.serviceAreaIdsList!) {
            selectedServiceAreaIds.add(element);
          }

          if (!serviceAreaName.isNullOrEmpty() &&
              serviceAreaName.contains(",") &&
              serviceAreaName.length >= 2) {
            serviceAreaName =
                serviceAreaName.substring(0, serviceAreaName.length - 2);
          }
          servicesAreaController.text = serviceAreaName;
        }
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
    getStaffServiceAreaData();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  getStaffServiceAreaData() {
    isLoading = true;
    serviceAreaList!.clear();
    // selectedServiceArea.clear();
    update();
    InventoryManagementProvider().getAllStaffServiceArea(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StaffServiceAreaRes responseData =
                  StaffServiceAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  serviceAreaList?.addAll(responseData.dataList!);
                  if (popDetail != null &&
                      popDetail!.serviceAreaNameList != null &&
                      popDetail!.serviceAreaNameList!.isNotEmpty) {
                    for (StaffServiceAreaDetail element in serviceAreaList!) {
                      for (String value
                          in popDetail!.serviceAreaNameList!) {
                        if (value == element.name) {
                          element.selected = true;
                          selectedServiceArea.add(element.name!);
                          break;
                        }
                      }
                    }
                  }
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
              log("Exception$e");
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

  void addEditPopApiCall() {
    isLoading = true;
    update();

    AddEditPopReq request = AddEditPopReq(
      id: popDetail != null ? popDetail!.id : null,
      name: popNameController.text,
      popCode: popCodeController.text,
      latitude: latController.text,
      longitude: longController.text,
      status: selectedStatus != null ? selectedStatus!.text : "",
      isDeleted: false,
      mvnoId: userDetail != null ? userDetail!.mvnoId : null,
      createdById: popDetail != null ? popDetail!.createdById : null,
      lastModifiedById: popDetail != null ? popDetail!.lastModifiedById : null,
      serviceAreaIdsList: selectedServiceAreaIds,
    );

    InventoryManagementProvider().addEditPop(
      isAdd: popDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              Get.back(result: true);
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

  getLocationToLatLong() {
    isLoading = true;
    update();
    CustomerProvider().getLocationToLatLong(
      placeId: selectedLocation!.placeId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LocationLatLongRes responseData =
                  LocationLatLongRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.location != null) {
                  // locationData = responseData.location;
                  latController.text = responseData.location!.latitude!;
                  longController.text = responseData.location!.longitude!;
                }
              } else {
                if (responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.error,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception$e");
              // print(e.toString());
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
