import 'dart:developer';

import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/model/request/save_ip_management_req.dart';
import 'package:savbill/pages/network_management/model/response/get_ip_management_list_res.dart';
import 'package:savbill/pages/network_management/model/response/save_ip_management_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class CreateIpController extends GetxController {
  bool isLoading = false, isShowLoadMore = false;
  TextEditingController networkIpController = TextEditingController();
  TextEditingController displayNameController = TextEditingController();
  TextEditingController ipRangeController = TextEditingController();
  TextEditingController poolNameController = TextEditingController();
  TextEditingController poolCategoryController = TextEditingController();
  TextEditingController broadcastIpController = TextEditingController();
  TextEditingController firstHostController = TextEditingController();
  TextEditingController lastHostController = TextEditingController();
  TextEditingController totalHostController = TextEditingController();
  TextEditingController netMaskController = TextEditingController();
  TextEditingController remarkController = TextEditingController();

  ScrollController? controller;
  int? page = 1, productId, inwardProductId;
  GetStorage getStorage = GetStorage();

  List<DropdownDetail>? poolTypeList = [];
  DropdownDetail? selectedPoolType;

  List<DropdownDetail>? defaultPoolFlagList = [];
  DropdownDetail? selectedDefaultPoolFlag;

  List<DropdownDetail>? staticIpPoolList = [];
  DropdownDetail? selectedStaticIpPool;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String from = Strings.add;

  IpManagementData? ipManagementData;
  int? poolIpId;

  @override
  void onInit() {
    super.onInit();

    poolTypeList!.add(DropdownDetail(
        id: Strings.public, text: Strings.public, type: Strings.pool_type));
    poolTypeList!.add(DropdownDetail(
        id: Strings.private, text: Strings.private, type: Strings.pool_type));

    defaultPoolFlagList!.add(DropdownDetail(
        id: Strings.yes, text: Strings.yes, type: Strings.default_pool_flag));
    defaultPoolFlagList!.add(DropdownDetail(
        id: Strings.no, text: Strings.no, type: Strings.default_pool_flag));

    staticIpPoolList!.add(DropdownDetail(
        id: Strings.yes, text: Strings.yes, type: Strings.static_ip_pool));
    staticIpPoolList!.add(DropdownDetail(
        id: Strings.no, text: Strings.no, type: Strings.static_ip_pool));

    statusList!.add(DropdownDetail(
        id: Strings.active, text: Strings.active, type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active, text: Strings.in_active, type: Strings.status));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        ipManagementData = arguments[Constant.IM_DETAIL];
      }

      if (ipManagementData != null) {
        networkIpController.text = ipManagementData!.networkIp!;
        displayNameController.text = ipManagementData!.displayName!;
        ipRangeController.text = ipManagementData!.ipRange!;
        poolNameController.text = ipManagementData!.poolName!;
        poolCategoryController.text = ipManagementData!.poolCategory!;
        broadcastIpController.text = ipManagementData!.broadcastIp!;
        firstHostController.text = ipManagementData!.firstHost!;
        lastHostController.text = ipManagementData!.lastHost!;
        totalHostController.text = ipManagementData!.totalHost.toString();
        netMaskController.text = ipManagementData!.networkIp!;
        remarkController.text = ipManagementData!.remark!;

        poolIpId = ipManagementData!.poolId;

        if (ipManagementData!.status != null &&
            ipManagementData!.status!.isNotEmpty) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(ipManagementData!.status!)) {
              selectedStatus = element;
              break;
            }
          }
        }

        if (ipManagementData!.poolType != null &&
            ipManagementData!.poolType!.isNotEmpty) {
          for (DropdownDetail element in poolTypeList!) {
            if (element.id!.equalsIgnoreCase(ipManagementData!.poolType!)) {
              selectedPoolType = element;
              break;
            }
          }
        }

        if (ipManagementData!.isStaticIpPool != null &&
            !(ipManagementData!.isStaticIpPool!.isNullOrEmpty())) {
          String isStaticIpValue = "";
          if (ipManagementData!.isStaticIpPool == true) {
            isStaticIpValue = Strings.yes;
          } else {
            isStaticIpValue = Strings.no;
          }
          for (DropdownDetail element in staticIpPoolList!) {
            if (element.id!.equalsIgnoreCase(isStaticIpValue)) {
              selectedStaticIpPool = element;
              break;
            }
          }
        }

        if (ipManagementData!.defaultPoolFlag != null &&
            !(ipManagementData!.defaultPoolFlag!.isNullOrEmpty())) {
          String elementValue = "";
          if (ipManagementData!.defaultPoolFlag == true) {
            elementValue = Strings.yes;
          } else {
            elementValue = Strings.no;
          }
          for (DropdownDetail element in defaultPoolFlagList!) {
            if (element.id!.equalsIgnoreCase(elementValue)) {
              selectedDefaultPoolFlag = element;
              break;
            }
          }
        }
      }
    }
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }

// save ip management list
  saveIpManagementApiCall() {
    isLoading = true;
    update();
    SaveIpManagmentReq request = SaveIpManagmentReq(
      broadcastIp: broadcastIpController.text,
      defaultPoolFlag:
          selectedDefaultPoolFlag!.text!.equalsIgnoreCase(Strings.yes)
              ? true
              : false,
      displayName: displayNameController.text,
      remark: remarkController.text,
      firstHost: firstHostController.text,
      ipRange: ipRangeController.text,
      isStaticIpPool: selectedStaticIpPool!.text!.equalsIgnoreCase(Strings.yes)
          ? true
          : false,
      lastHost: lastHostController.text,
      netMask: netMaskController.text,
      networkIp: networkIpController.text,
      poolCategory: poolCategoryController.text,
      poolName: poolNameController.text,
      poolType: selectedPoolType!.text,
      status: selectedStatus!.text,
      totalHost: totalHostController.text,
    );
    NetworkManagementProvider().saveIpManagement(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SaveIpManagementRes responseData =
                  SaveIpManagementRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                showDialog(
                  context: Get.context!,
                  builder: (BuildContext context) {
                    return AlertDialogHelper(
                        title: Strings.SUCCESS,
                        message: Strings.successfully,
                        positiveBtnText: Strings.ok,
                        negativeBtnText: "",
                        positiveBtnClick: () {
                          Get.back(result: true);
                          Get.back(result: true);
                        },
                        negativeBtnClick: () {
                          Get.back();
                        });
                  },
                );
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
        handleApiError(error);
      },
    );
  }

  // update ip management list
  updateIpManagementApiCall() {
    isLoading = true;
    update();
    SaveIpManagmentReq request = SaveIpManagmentReq(
      broadcastIp: broadcastIpController.text,
      defaultPoolFlag:
          selectedDefaultPoolFlag!.text!.equalsIgnoreCase(Strings.yes)
              ? true
              : false,
      displayName: displayNameController.text,
      remark: remarkController.text,
      firstHost: firstHostController.text,
      ipRange: ipRangeController.text,
      isStaticIpPool: selectedStaticIpPool!.text!.equalsIgnoreCase(Strings.yes)
          ? true
          : false,
      lastHost: lastHostController.text,
      netMask: netMaskController.text,
      networkIp: networkIpController.text,
      poolCategory: poolCategoryController.text,
      poolName: poolNameController.text,
      poolType: selectedPoolType!.text,
      status: selectedStatus!.text,
      totalHost: totalHostController.text,
      poolId: poolIpId,
    );
    NetworkManagementProvider().updateIpManagement(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SaveIpManagementRes responseData =
                  SaveIpManagementRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                showDialog(
                  context: Get.context!,
                  builder: (BuildContext context) {
                    return AlertDialogHelper(
                        title: Strings.SUCCESS,
                        message: Strings.successfully,
                        positiveBtnText: Strings.ok,
                        negativeBtnText: "",
                        positiveBtnClick: () {
                          Get.back(result: true);
                          Get.back(result: true);
                        },
                        negativeBtnClick: () {
                          Get.back();
                        });
                  },
                );
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
        handleApiError(error);
      },
    );
  }

  handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
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
