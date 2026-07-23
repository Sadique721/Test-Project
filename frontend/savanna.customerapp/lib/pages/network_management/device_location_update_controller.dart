import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/model/request/device_location_update_req.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/alert_dialog.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class DeviceLocationUpdateController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false;
  GetStorage getStorage = GetStorage();
  DeviceDetail? deviceDetail;
  String? product_Name;

  List<ServicesAreaDetail>? servicesAreaList = [];
  List<ServicesAreaDetail>? selectedServicesArea = [];

  TextEditingController servicesAreaController = TextEditingController();
  TextEditingController latitudeController = TextEditingController();
  TextEditingController longitudeController = TextEditingController();
  TextEditingController nameController = TextEditingController();

  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;


  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

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
      if (arguments[Constant.DEVICE_DETAIL] != null) {
        deviceDetail = arguments[Constant.DEVICE_DETAIL];
        if (deviceDetail != null) {
          if (deviceDetail!.serviceAreaNameList != null &&
              deviceDetail!.serviceAreaNameList!.isNotEmpty) {
            selectedServicesArea!.addAll(deviceDetail!.serviceAreaNameList!);
            String strServiceArea = "";
            for (var element in selectedServicesArea!) {
              strServiceArea = "$strServiceArea${element.name!}, ";
            }
            if (!strServiceArea.isNullOrEmpty() &&
                strServiceArea.contains(",") &&
                strServiceArea.length >= 2) {
              strServiceArea =
                  strServiceArea.substring(0, strServiceArea.length - 2);
            }
            servicesAreaController.text = strServiceArea;
          }



          if(deviceDetail!.name !=null && deviceDetail!.name!.isNotEmpty){
            nameController.text = deviceDetail!.name!;
          }
          if (deviceDetail!.latitude != null &&
              deviceDetail!.latitude!.isNotEmpty) {
            latitudeController.text = deviceDetail!.latitude!;
          }
          if (deviceDetail!.longitude != null &&
              deviceDetail!.longitude!.isNotEmpty) {
            longitudeController.text = deviceDetail!.longitude!;
          }

          if (deviceDetail!.name != null &&
              deviceDetail!.name!.isNotEmpty) {
            var productName = deviceDetail!.name;
            var parts = productName!.split(' - ');
            product_Name = parts[0].trim();
          }
        }
      }
    }
    update();
    getServiceArea();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  getServiceArea() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    CustomerProvider().getServiceAreaData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesAreaList!.addAll(responseData.dataList!);
                  update();
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  updateDeviceDetail() {
    isLoading = true;
    update();
    List<int> serviceAreaId = [];
    if (selectedServicesArea != null && selectedServicesArea!.isNotEmpty) {
      selectedServicesArea!.forEach((element) {
        serviceAreaId.add(element.id!);
      });
    }
    DeviceLocationUpdateReq request = DeviceLocationUpdateReq(
        id: deviceDetail!.id,
        name: (deviceDetail!.name != null && deviceDetail!.name!.isNotEmpty)
            ? deviceDetail!.name
            : "",
        status: selectedStatus!.text,
        productId:
            (deviceDetail!.productId != null) ? deviceDetail!.productId : null,
        staffId: null,
        inwardId:
            (deviceDetail!.inwardId != null) ? deviceDetail!.inwardId : null,
        latitude: latitudeController.text,
        longitude: longitudeController.text,
        isDeleted: false,
        devicetype: (deviceDetail!.devicetype != null &&
                deviceDetail!.devicetype!.isNotEmpty)
            ? deviceDetail!.devicetype
            : "",
        serviceAreaIdsList: serviceAreaId,
        availableInPorts: (deviceDetail!.availableInPorts != null)
            ? deviceDetail!.availableInPorts
            : null,
        availableOutPorts: (deviceDetail!.availableOutPorts != null)
            ? deviceDetail!.availableOutPorts
            : null,
        totalInPorts: (deviceDetail!.totalInPorts != null)
            ? deviceDetail!.totalInPorts
            : null,
        totalOutPorts: (deviceDetail!.totalOutPorts != null)
            ? deviceDetail!.totalOutPorts
            : null
    );
    NetworkManagementProvider().updateDeviceDetail(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                          responseData.responseCode == 200 ||
                      responseData.responseCode == 0)) {
                showDialog(
                  context: Get.context!,
                  builder: (BuildContext context) {
                    return AlertDialogHelper(
                        title: Strings.INFO,
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
