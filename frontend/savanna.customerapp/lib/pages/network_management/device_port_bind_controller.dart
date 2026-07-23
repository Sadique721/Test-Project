import 'dart:developer';

import 'package:savbill/pages/network_management/model/request/device_port_bind_req.dart';
import 'package:savbill/pages/network_management/model/response/bind_port_device_res.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/pages/network_management/model/response/port_availability_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
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

class DevicePortBindController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false, isDataChange = false;
  GetStorage getStorage = GetStorage();
  DeviceDetail? deviceDetail;

  AutovalidateMode autoValidateModeIn = AutovalidateMode.disabled;
  AutovalidateMode autoValidateModeOut = AutovalidateMode.disabled;
  final inPortFormKey = GlobalKey<FormState>();
  final outPortFormKey = GlobalKey<FormState>();

  // for form
  List<String>? inPortList = [];
  List<String>? inPortForBindList = [];
  List<String>? outPortList = [];
  List<String>? outPortForBindList = [];
  String? selectedInPort,
      selectedInPortForBind,
      selectedOutPort,
      selectedOutPortForBind;

  List<DeviceDetail>? inParentDeviceList = [];
  List<DeviceDetail>? outParentDeviceList = [];
  DeviceDetail? selectedInParentDevice, selectedOutParentDevice;

  // for list
  List<BindPortDeviceDetail>? inPortBindData = [];
  List<BindPortDeviceDetail>? outPortBindData = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.DEVICE_DETAIL] != null) {
        deviceDetail = arguments[Constant.DEVICE_DETAIL];
      }
    }
    update();
    checkInPortAvailability(1, "IN");
  }

  // 1 for in port, 2 for in bind port, 3 for out port, 4 for out bind port

  checkInPortAvailability(int identify, String type) {
    if (identify == 1 && type.equalsIgnoreCase("IN")) {
      selectedInPort = null;
      inPortList!.clear();
    }
    if (identify == 2 && type.equalsIgnoreCase("OUT")) {
      selectedInPortForBind = null;
      inPortForBindList!.clear();
    }
    if (identify == 3 && type.equalsIgnoreCase("OUT")) {
      selectedOutPort = null;
      outPortList!.clear();
    }
    if (identify == 4 && type.equalsIgnoreCase("IN")) {
      selectedOutPortForBind = null;
      outPortForBindList!.clear();
    }
    isLoading = true;
    update();
    int parentDeviceId = deviceDetail!.id!;
    if (identify == 2) {
      parentDeviceId = selectedInParentDevice!.id!;
    }
    if (identify == 4) {
      parentDeviceId = selectedOutParentDevice!.id!;
    }
    NetworkManagementProvider().getDevicePortAvailabilityData(
      parentDeviceId: parentDeviceId,
      parentPortType: type,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PortAvailabilityRes responseData =
                  PortAvailabilityRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  if (identify == 1 && type.equalsIgnoreCase("IN")) {
                    inPortList!.addAll(responseData.dataList!);
                  }
                  if (identify == 3 && type.equalsIgnoreCase("OUT")) {
                    outPortList!.addAll(responseData.dataList!);
                  }
                  if (identify == 2 && type.equalsIgnoreCase("OUT")) {
                    inPortForBindList!.addAll(responseData.dataList!);
                  }
                  if (identify == 4 && type.equalsIgnoreCase("IN")) {
                    outPortForBindList!.addAll(responseData.dataList!);
                  }
                  update();
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
            log("responseModel_message>> ${responseModel.message}");
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
          log("responseModel_message>>");
        }
        if (identify == 1 && type.equalsIgnoreCase("IN")) {
          checkInPortAvailability(3, "OUT");
        }

        if (identify == 3 && type.equalsIgnoreCase("OUT")) {
          getParentDeviceDetail();
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        if (identify == 1 && type.equalsIgnoreCase("IN")) {
          checkInPortAvailability(3, "OUT");
        }

        if (identify == 3 && type.equalsIgnoreCase("OUT")) {
          getParentDeviceDetail();
        }
      },
    );
  }

  getParentDeviceDetail() {
    isLoading = true;
    selectedInParentDevice = null;
    selectedOutParentDevice = null;
    inParentDeviceList!.clear();
    outParentDeviceList!.clear();
    update();
    NetworkManagementProvider().getParentDeviceForBindPort(
      parentDeviceId: deviceDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DeviceListRes responseData = DeviceListRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inParentDeviceList!.addAll(responseData.dataList!);
                  outParentDeviceList!.addAll(responseData.dataList!);
                  update();
                }
              } /*else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
              }*/
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
        getPortBindData();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getPortBindData();
      },
    );
  }

  getPortBindData() {
    isLoading = true;
    inPortBindData!.clear();
    outPortBindData!.clear();
    update();
    NetworkManagementProvider().getBindPortDeviceData(
      parentDeviceId: deviceDetail!.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BindPortDeviceRes responseData = BindPortDeviceRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  for (BindPortDeviceDetail element in responseData.dataList!) {
                    if (element.portType != null &&
                        element.portType!.isNotEmpty) {
                      if (element.portType!.equalsIgnoreCase("in")) {
                        inPortBindData!.add(element);
                      }
                      if (element.portType!.equalsIgnoreCase("out")) {
                        outPortBindData!.add(element);
                      }
                    }
                  }
                  update();
                }
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
        } /*else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }*/
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  updateDevicePortBind(String type, InOutPortDevices item) {
    List<InOutPortDevices> inPortDevices = [];
    List<InOutPortDevices> outPortDevices = [];

    if (inPortBindData != null && inPortBindData!.isNotEmpty) {
      for (BindPortDeviceDetail element in inPortBindData!) {
        inPortDevices.add(InOutPortDevices(
            inBind: element.inBind,
            flag: true,
            outBind: element.outBind,
            parentDeviceId: element.parentDeviceId));
      }
    }

    if (outPortBindData != null && outPortBindData!.isNotEmpty) {
      for (BindPortDeviceDetail element in outPortBindData!) {
        outPortDevices.add(InOutPortDevices(
            inBind: element.inBind,
            flag: true,
            outBind: element.outBind,
            parentDeviceId: element.parentDeviceId));
      }
    }

    if (type.equalsIgnoreCase("in")) {
      inPortDevices.add(item);
    }

    if (type.equalsIgnoreCase("out")) {
      outPortDevices.add(item);
    }

    isLoading = true;
    update();
    DevicePortBindReq request = DevicePortBindReq(
        deviceId: deviceDetail!.id,
        inPortDevices: inPortDevices,
        outPortDevices: outPortDevices);

    NetworkManagementProvider().updateDevicePortBind(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  (responseData.responseCode == 200 ||
                      responseData.responseCode == 0)) {
                reloadScreen();
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
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
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  reloadScreen() {
    isDataChange = true;
    autoValidateModeIn = AutovalidateMode.disabled;
    autoValidateModeOut = AutovalidateMode.disabled;

    selectedInPort = null;
    selectedInPortForBind = null;
    selectedOutPort = null;
    selectedOutPortForBind = null;
    selectedInParentDevice = null;
    selectedOutParentDevice = null;
    inPortList!.clear();
    inPortForBindList!.clear();
    outPortList!.clear();
    outPortForBindList!.clear();
    inParentDeviceList!.clear();
    outParentDeviceList!.clear();
    inPortBindData!.clear();
    outPortBindData!.clear();
    update();
    checkInPortAvailability(1, "IN");
  }

  handleApiError(ResponseModel error) {
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
