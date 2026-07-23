import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_category_req.dart';
import 'package:savbill/pages/inventory/module/response/add_edit_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/category_type_res.dart';
import 'package:savbill/pages/inventory/module/response/get_dtv_category_res.dart';
import 'package:savbill/pages/inventory/module/response/inventroy_uom_data_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/network_management/model/response/network_device_type_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class AddEditCategoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController categoryNameController = TextEditingController();
  TextEditingController productIdController = TextEditingController();
  TextEditingController uomController = TextEditingController();
  TextEditingController categoryTypeController = TextEditingController();

  UserDetail? userDetail;

  List<CategoryType>? catTypeList = [];
  CategoryType? selectedCatType;
  List<int> selectedCategoryTypeList = [];

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<DtvCategoryDataList>? dtvCategoryDataList = [];
  DtvCategoryDataList? selectedDtvCategoryData;
  RxBool isDeviceTypeVisible = false.obs;
  List<UOMDataList>? uomDataList = [];
  UOMDataList? selectedUomData;

  bool macAddress = false,
      serialNo = false,
      hasTrackable = false,
      hasPort = false,
      hasCas = false;

  String from = Strings.add;
  CategoryDetail? categoryDetail;

  List<NetworkDeviceType>? deviceTypeList = [];
  NetworkDeviceType? selectedDeviceType;

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
        categoryDetail = arguments[Constant.IM_DETAIL];
      }

      if (categoryDetail != null) {
        if (categoryDetail!.name != null && categoryDetail!.name!.isNotEmpty) {
          categoryNameController.text = categoryDetail!.name!;
        }

        if (categoryDetail!.productId != null &&
            categoryDetail!.productId!.isNotEmpty) {
          productIdController.text = categoryDetail!.productId!;
        }

        if (categoryDetail!.type != null && categoryDetail!.type!.isNotEmpty) {
          categoryTypeController.text = categoryDetail!.type! ?? "";
        }

        if (categoryDetail!.hasSerial != null &&
            categoryDetail!.hasSerial == true) {
          serialNo = true;
          hasTrackable = true;
          macAddress = false;
        } else {
          hasTrackable = false;
          macAddress = false;
          serialNo = false;
        }

        if (categoryDetail!.hasMac != null && categoryDetail!.hasMac == true) {
          macAddress = true;
          serialNo = true;
          hasTrackable = true;
        } else {
          macAddress = false;
          serialNo = false;
          hasTrackable = false;
        }

        if (categoryDetail!.hasTrackable != null &&
            categoryDetail!.hasTrackable == true) {
          hasTrackable = true;
          macAddress = true;
          serialNo = true;
        } else {
          hasTrackable = false;
          macAddress = false;
          serialNo = false;
        }

        if (categoryDetail!.hasPort != null &&
            categoryDetail!.hasPort! == true) {
          hasPort = true;
        } else {
          hasPort = false;
        }

        if (categoryDetail!.hasCas != null && categoryDetail!.hasCas! == true) {
          hasCas = true;
        } else {
          hasCas = false;
        }

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(categoryDetail!.status!)) {
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
    getCaseTypeListData();
  }

  getCaseTypeListData() {
    isLoading = true;
    catTypeList?.clear();
    selectedCategoryTypeList.clear();
    update();
    InventoryManagementProvider().getCategoryType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CategoryTypeRes responseData = CategoryTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  catTypeList?.addAll(responseData.dataList!);
                  // if (categoryDetail != null &&
                  //     categoryDetail!.type != null &&
                  //     categoryDetail!.type!.isNotEmpty) {
                  //   for (CategoryType element in catTypeList!) {
                  //     if (element.text != null &&
                  //         element.text!.isNotEmpty &&
                  //         element.text!
                  //             .equalsIgnoreCase(categoryDetail!.type!)) {
                  //       selectedCatType = element;
                  //       break;
                  //     }
                  //   }
                  // }
                  // if (categoryDetail != null &&
                  //     categoryDetail!.type != null &&
                  //     categoryDetail!.type!.isNotEmpty) {
                  //   String selectCategoryTypeName = "";
                  //   for (CategoryType element in catTypeList!) {
                  //       if (categoryDetail!.id == element.id) {
                  //         selectedCategoryTypeList.add(element.id!);
                  //         selectCategoryTypeName = "$selectCategoryTypeName${element.text!}, ";
                  //         element.selected = true;
                  //     }
                  //   }
                  //   if (!selectCategoryTypeName.isNullOrEmpty() &&
                  //       selectCategoryTypeName.contains(",") &&
                  //       selectCategoryTypeName.length >= 2) {
                  //     selectCategoryTypeName = selectCategoryTypeName.substring(
                  //         0, selectCategoryTypeName.length - 2);
                  //   }
                  //   categoryTypeController.text = selectCategoryTypeName;
                  // }
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
        getDtvCategoryDataList();
      },
      onError: (ResponseModel error) {
        getDtvCategoryDataList();
        _handleApiError(error);
      },
    );
  }

  getDtvCategoryDataList() {
    isLoading = true;
    dtvCategoryDataList!.clear();
    update();
    InventoryManagementProvider().getDtvCategoryList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          try {
            Map<String, dynamic> map = responseModel.result;
            GetDtvCategoryDataRes responseData =
                GetDtvCategoryDataRes.fromJson(map);
            if (responseData.responseCode == 200) {
              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                dtvCategoryDataList!.addAll(responseData.dataList!);
                // if (categoryDetail != null &&
                //     categoryDetail!.type != null &&
                //     categoryDetail!.type!.isNotEmpty) {
                //   for (DtvCategoryDataList element in dtvCategoryDataList!) {
                //     if (element.text != null &&
                //         element.text!.isNotEmpty &&
                //         element.text!
                //             .equalsIgnoreCase(categoryDetail!.dtvCategory!)) {
                //       selectedDtvCategoryData = element;
                //       break;
                //     }
                //   }
                // }
              }
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          } on Exception catch (e) {
            print("ExceptionExceptionException" + e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getUomInventoryDataList();
      },
      onError: (ResponseModel error) {
        log("getDtvCategoryDataList:-$error");
        _handleApiError(error);
        getUomInventoryDataList();
      },
    );
  }

  getUomInventoryDataList() {
    isLoading = true;
    uomDataList!.clear();
    update();
    InventoryManagementProvider().getUomInventoryData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryUOMDataRes responseData =
                  InventoryUOMDataRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  uomDataList!.addAll(responseData.dataList!);

                  if (categoryDetail != null &&
                      categoryDetail!.type != null &&
                      categoryDetail!.type!.isNotEmpty) {
                    for (UOMDataList element in uomDataList!) {
                      if (element.displayName != null &&
                          element.displayName!.isNotEmpty &&
                          element.displayName!
                              .equalsIgnoreCase(categoryDetail!.unit!)) {
                        selectedUomData = element;
                        break;
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
        getDeviceType();
      },
      onError: (ResponseModel error) {
        getDeviceType();
        _handleApiError(error);
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
        _handleApiError(error);
      },
    );
  }

  void addEditCategoryApiCall() {
    isLoading = true;
    update();
    String? dtvCategoryValue;
    if (hasCas == true) {
      if (selectedDtvCategoryData != null) {
        dtvCategoryValue = selectedDtvCategoryData!.displayName;
      } else {
        dtvCategoryValue = "";
      }
    } else {
      dtvCategoryValue = "";
    }



    AddEditCategory request = AddEditCategory(
        id: categoryDetail != null ? categoryDetail!.id : null,
        name: categoryNameController.text,
        unit: selectedUomData!.displayName,
        // type: selectedCatType != null ? selectedCatType!.text : "",
        type: categoryTypeController.text ?? "",
        status: selectedStatus != null ? selectedStatus!.id : "",
        specificationParametersDTOList:[],
        hasMac: macAddress,
        hasSerial: serialNo,
        hasTrackable: hasTrackable,
        hasPort: hasPort,
        hasCas: hasCas.isNullOrEmpty() ? null : hasCas,
        productId: productIdController.text,
        expiryTime: null,
        expiryTimeUnit: null,
        dtvCategory: dtvCategoryValue!.isNotEmpty ? dtvCategoryValue : null,
    deviceType: selectedDeviceType != null ? selectedDeviceType!.text : null);

    InventoryManagementProvider().addEditProductCategory(
      isAdd: categoryDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          Map<String, dynamic> map = responseModel.result;
          // BaseResponse responseData = BaseResponse.fromJson(map);
          AddEditCategoryRes responseData = AddEditCategoryRes.fromJson(map);
          if (responseData.responseCode == 417) {
            Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                AppTheme.colorWhite, AppTheme.colorBlueRView);
          } else if (responseData.responseCode == 200) {
            if (responseData.data!.responseCode == 200) {
              Get.back(result: true);
              Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorGreen);
            }
          } else {
            if (responseData.responseMessage!.isNotEmpty) {
              Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorRed);
            }
          }
          // }
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
