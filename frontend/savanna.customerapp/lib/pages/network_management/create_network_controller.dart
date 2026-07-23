import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/model/network_inward_product_res.dart';
import 'package:savbill/pages/network_management/model/request/network_add_device_req.dart';
import 'package:savbill/pages/network_management/model/response/device_list_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_product_res.dart';
import 'package:savbill/pages/network_management/model/response/network_device_type_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
import 'package:savbill/routes/app_routes.dart';
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

class CreateNetworkController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false,
      checkBtnClickEvent = false,
      isLoadFilterData = false;

  TextEditingController nameNetworkController = TextEditingController();
  TextEditingController totalPortsController = TextEditingController();
  TextEditingController latitudeController = TextEditingController();
  TextEditingController longitudeController = TextEditingController();
  TextEditingController servicesAreaController = TextEditingController();

  ScrollController? controller;
  int? page = 1, productId, inwardProductId;
  GetStorage getStorage = GetStorage();

  List<DeviceDetail>? deviceList = [];
  DeviceListRes? deviceListRes;

  List<NetworkDeviceType>? deviceTypeList = [];
  NetworkDeviceType? selectedDeviceType;

  List<NetworkDeviceProduct>? deviceProductList = [];
  NetworkDeviceProduct? selectedDeviceProduct;

  List<NetworkInwardProudctDataList>? inwardProductList = [];
  NetworkInwardProudctDataList? selectedInwardProduct;

  List<ServicesAreaDetail>? servicesAreaList = [];
  List<ServicesAreaDetail>? selectedServicesArea = [];

  // ServicesAreaDetail? selectedServicesArea;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String? availablePorts = "";

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

    // controller = ScrollController();
    // controller?.addListener(() {
    //   double? extentAfter = controller?.position.extentAfter;
    //   if (extentAfter! < 300 && !isShowLoadMore) {
    //     if (deviceListRes != null && deviceListRes!.totalPages != page) {
    //       isShowLoadMore = true;
    //       page = page + 1;
    //       update();
    //       getDeviceListData();
    //     }
    //   }
    // });
    // getDeviceType();
    // getDeviceType();
    getDeviceProductList();
  }

  getDeviceProductList() {
    isLoading = true;
    selectedDeviceProduct = null;
    deviceProductList!.clear();
    update();
    NetworkManagementProvider().getNetworkDeviceProduct(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NetworkDeviceProductRes responseData =
                  NetworkDeviceProductRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  deviceProductList?.addAll(responseData.dataList!);
                  getDeviceType();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        // getServiceArea();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        // getServiceArea();
      },
    );
  }

  getNetworkInwardProductList(int? productId) {
    isLoading = true;
    selectedInwardProduct = null;
    inwardProductList!.clear();
    update();
    NetworkManagementProvider().getNetworkInwardProduct(
      productId: productId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NetworkInwardProductRes responseData =
                  NetworkInwardProductRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inwardProductList?.addAll(responseData.dataList!);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        // getServiceArea();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  getServiceArea() {
    isLoading = true;
    // selectedServicesArea = null;
    servicesAreaList!.clear();
    update();
    CustomerProvider().getServiceAreaData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesAreaList!.addAll(responseData.dataList!);
                  update();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        if (isLoadFilterData == false) {
          isLoadFilterData = true;
          filterViewOpen = true;
        }
        update();
      },
      onError: (ResponseModel error) {
        if (isLoadFilterData == false) {
          isLoadFilterData = true;
          filterViewOpen = true;
        }
        update();
        handleApiError(error);
      },
    );
  }

  getDeviceType() {
    isLoading = true;
    selectedDeviceType = null;
    deviceTypeList!.clear();
    update();
    NetworkManagementProvider().getNetworkDeviceType(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NetworkDeviceTypeRes responseData =
                  NetworkDeviceTypeRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  deviceTypeList?.addAll(responseData.dataList!);
                  getServiceArea();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
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

  // getDeviceListData() {
  //   if (!isShowLoadMore) {
  //     isLoading = true;
  //     update();
  //   }
  //
  //   NetworkManagementProvider().getDeviceList(
  //     isSearch: isFilterApply,
  //     deviceName: isFilterApply ? deviceNameController.text : "",
  //     deviceType: (isFilterApply && selectedDeviceType != null)
  //         ? selectedDeviceType!.text
  //         : "",
  //     deviceProductName: (isFilterApply && selectedDeviceProduct != null)
  //         ? selectedDeviceProduct!.name
  //         : "",
  //     serviceName: (isFilterApply && selectedServiceArea != null)
  //         ? selectedServiceArea!.name
  //         : "",
  //     status:
  //     (isFilterApply && selectedStatus != null) ? selectedStatus!.text : "",
  //     searchRequest: CustomerListRequest(
  //         page: page,
  //         pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
  //         sortBy: "Id",
  //         sortOrder: 0),
  //     requestNormal: PageRequest(
  //       page: page,
  //       pageSize: Constant.PAGE_LOAD_DATA_LIMIT,
  //     ),
  //     onSuccess: (ResponseModel responseModel) {
  //       isShowLoadMore = false;
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             DeviceListRes responseData = DeviceListRes.fromJson(map);
  //
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               deviceListRes = responseData;
  //               if (page == 1) {
  //                 deviceList?.clear();
  //               }
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 deviceList?.addAll(responseData.dataList!);
  //               }
  //             } else {
  //               if (page == 1) {
  //                 deviceList?.clear();
  //               }
  //               if (responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (page == 1) {
  //           deviceList?.clear();
  //         }
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       if (page == 1) {
  //         deviceList?.clear();
  //       }
  //       handleApiError(error);
  //     },
  //   );
  // }

  // deleteDevice(DeviceDetail item, int index) {
  //   isLoading = true;
  //   update();
  //   NetworkManagementProvider().deleteDevice(
  //     request: item,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             BaseResponse responseData = BaseResponse.fromJson(map);
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               deviceList!.removeAt(index);
  //             } else {
  //               if (responseData.responseMessage != null &&
  //                   responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       handleApiError(error);
  //     },
  //   );
  // }
  //
  // getDeviceDetail(int deviceId, String from) {
  //   isLoading = true;
  //   update();
  //   NetworkManagementProvider().getDeviceDetail(
  //     deviceId: deviceId,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             DeviceListRes responseData = DeviceListRes.fromJson(map);
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               if (responseData.data != null) {
  //                 if (from.equalsIgnoreCase(Strings.update_device_location)) {
  //                   openDeviceLocationUpdateScreen(responseData.data!);
  //                 } else if (from
  //                     .equalsIgnoreCase(Strings.parent_device_mapping)) {
  //                   openDevicePortBindScreen(responseData.data!);
  //                 } else if (from.equalsIgnoreCase(Strings.device_detail)) {
  //                   openDeviceDetailScreen(responseData.data!);
  //                 }
  //               }
  //             } else {
  //               if (responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       handleApiError(error);
  //     },
  //   );
  // }
  //
  // openDeviceLocationUpdateScreen(DeviceDetail data) async {
  //   var result = await Get.to(DeviceLocationUpdate(),
  //       arguments: {Constant.DEVICE_DETAIL: data});
  //
  //   if (result != null && result == true) {
  //     clearFilter();
  //   }
  // }
  //
  // openDevicePortBindScreen(DeviceDetail data) async {
  //   var result = await Get.to(DevicePortBind(),
  //       arguments: {Constant.DEVICE_DETAIL: data});
  //
  //   if (result != null && result == true) {
  //     clearFilter();
  //   }
  // }
  //
  // openDeviceDetailScreen(DeviceDetail data) async {
  //   Get.to(DeviceDetailScreen(), arguments: {Constant.DEVICE_DETAIL: data});
  // }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }

  addNetworkDeviceDetail() {
    isLoading = true;
    update();
    List<int> serviceAreaId = [];
    if (selectedServicesArea != null && selectedServicesArea!.isNotEmpty) {
      selectedServicesArea!.forEach((element) {
        serviceAreaId.add(element.id!);
      });
    }
    NetworkAddDeviceReq request = NetworkAddDeviceReq(
      id: "",
      name: nameNetworkController.text,
      status:  selectedStatus!.text,
      productId: productId,
      staffId: null,
      inwardId: selectedInwardProduct!.id,
      latitude: latitudeController.text,
      longitude: longitudeController.text,
      isDeleted: false,
      devicetype: selectedDeviceType!.text,
      serviceAreaIdsList: serviceAreaId,
      availableInPorts: "",
      availableOutPorts: "",
      totalInPorts: "",
      totalOutPorts: "",
      totalPorts: int.parse(totalPortsController.text.toString()),
      availablePorts: int.parse(totalPortsController.text.toString()),
    );
    NetworkManagementProvider().addNetworkDevice(
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
