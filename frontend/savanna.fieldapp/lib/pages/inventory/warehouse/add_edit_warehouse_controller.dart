import 'dart:convert';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_area_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_warehouse_req.dart';
import 'package:savbill/pages/inventory/module/response/branch_service_area_list_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_team_based_inventory_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_detail_res.dart';
import 'package:savbill/pages/inventory/module/response/warehouse_type_res.dart';
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

class AddEditWareHouseController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController warehouseNameController = TextEditingController();
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();
  TextEditingController parentServicesAreaController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();
  TextEditingController servicesAreaController = TextEditingController();
  TextEditingController address1Controller = TextEditingController();
  TextEditingController address2Controller = TextEditingController();
  TextEditingController warehouseCodeController = TextEditingController();
  TextEditingController teamController = TextEditingController();

  UserDetail? userDetail;

  String from = Strings.add;
  WareHouseData? wareHouseData;

  List<WareHouseTypeDetail>? typeList = [];
  WareHouseTypeDetail? selectedType;

  List<int> selectedParentServiceArea = [];
  List<StaffServiceAreaDetail>? parentServiceAreaList = [];

  List<int> selectedServiceArea = [];
  List<StaffServiceAreaDetail>? serviceAreaList = [];

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  LocationDetail? selectedLocation;
  bool checkBtnClickEvent = false, firstTime = false;

  List<PincodeDetail>? pincodeList = [];
  PincodeDetail? selPincode;

  List<CityDetail>? cityList = [];
  CityDetail? selCity;

  List<StateDetail>? stateList = [];
  StateDetail? selState;

  List<CountryDetail>? countryList = [];
  CountryDetail? selCountry;

  List<BranchServiceAreaDetail>? branchList = [];
  BranchServiceAreaDetail? selBranch;

  List<AllTeamDataList>? allTeamBasedInventoryList = [];
  // AllTeamDataList? selectedTeamInventoryData;

  List<int>? selectedAllTeamInventoryList = [];


  PincodeDetail? pincodeDetail;

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
        wareHouseData = arguments[Constant.IM_DETAIL];
      }
      if (wareHouseData != null) {
        firstTime = true;
        if (wareHouseData!.name != null && wareHouseData!.name!.isNotEmpty) {
          warehouseNameController.text = wareHouseData!.name!;
        }
        //new
        if (wareHouseData!.warehouseCode != null && wareHouseData!.warehouseCode!.isNotEmpty) {
          warehouseCodeController.text = wareHouseData!.warehouseCode!;
        }
        if (wareHouseData!.description != null &&
            wareHouseData!.description!.isNotEmpty) {
          descriptionController.text = wareHouseData!.description!;
        }
        if (wareHouseData!.latitude != null &&
            wareHouseData!.latitude!.isNotEmpty) {
          latController.text = wareHouseData!.latitude!;
        }
        if (wareHouseData!.longitude != null &&
            wareHouseData!.longitude!.isNotEmpty) {
          longController.text = wareHouseData!.longitude!;
        }

        if (wareHouseData!.address1 != null &&
            wareHouseData!.address1!.isNotEmpty) {
          address1Controller.text = wareHouseData!.address1!;
        }
        if (wareHouseData!.address2 != null &&
            wareHouseData!.address2!.isNotEmpty) {
          address2Controller.text = wareHouseData!.address2!;
        }

        if (wareHouseData!.status != null) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(wareHouseData!.status!)) {
              selectedStatus = element;
              break;
            }
          }
        }


        if (wareHouseData!.warehouseType != null) {
          for (WareHouseTypeDetail element in typeList!) {
            if (element.value!.equalsIgnoreCase(wareHouseData!.warehouseType!)) {
              selectedType = element;
              break;
            }
          }
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
    if (wareHouseData != null) {
      warehouseToParentServiceArea();
    } else {
      getWareHouseType();
    }
  }

  getWareHouseType() {
    isLoading = true;
    typeList!.clear();
    update();
    InventoryManagementProvider().getWareHouseType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseTypeRes responseData = WareHouseTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  typeList?.addAll(responseData.dataList!);
                  if (wareHouseData != null &&
                      wareHouseData!.warehouseType != null &&
                      wareHouseData!.warehouseType!.isNotEmpty) {
                    for (WareHouseTypeDetail element in typeList!) {
                      if (element.text!.equalsIgnoreCase(wareHouseData!.warehouseType!)) {
                        selectedType = element;
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
        getAllParentServiceAreaData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllParentServiceAreaData();
      },
    );
  }
  getAllParentServiceAreaData() {
    isLoading = true;
    parentServiceAreaList!.clear();
    selectedParentServiceArea.clear();
    update();
    InventoryManagementProvider().getAllParentServiceArea(
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
                  parentServiceAreaList?.addAll(responseData.dataList!);
                  if (wareHouseData != null &&
                      wareHouseData!.parentServiceAreaNameList != null &&
                      wareHouseData!.parentServiceAreaNameList!.isNotEmpty) {
                    String serviceAreaName = "";
                    for (StaffServiceAreaDetail element
                    in parentServiceAreaList!) {
                      for (StaffServiceAreaDetail value
                      in wareHouseData!.parentServiceAreaNameList!) {
                        if (value.id == element.id) {
                          selectedParentServiceArea.add(element.id!);
                          serviceAreaName =
                          "$serviceAreaName${element.name!}, ";
                          element.selected = true;
                        }
                      }
                    }
                    if (!serviceAreaName.isNullOrEmpty() &&
                        serviceAreaName.contains(",") &&
                        serviceAreaName.length >= 2) {
                      serviceAreaName = serviceAreaName.substring(
                          0, serviceAreaName.length - 2);
                    }
                    parentServicesAreaController.text = serviceAreaName;
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
        getAllTeamBasedInventory();

      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllTeamBasedInventory();

      },
    );
  }

  getAllTeamBasedInventory() {
    isLoading = true;

    allTeamBasedInventoryList?.clear();
    selectedAllTeamInventoryList?.clear();

    update();

    InventoryManagementProvider().getAllTeamInventory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;

              GetAllTeamBasedInventoryRes responseData =
              GetAllTeamBasedInventoryRes.fromJson(map);

              if (responseData.responseCode == 200) {
                final List<AllTeamDataList> dataList =
                    responseData.dataList ?? [];

                if (dataList.isNotEmpty) {
                  allTeamBasedInventoryList?.addAll(dataList);

                  final teamIds = wareHouseData?.teamsIdsList ?? [];
                  String teamName = "";

                  if (teamIds.isNotEmpty) {
                    for (AllTeamDataList element in allTeamBasedInventoryList ?? []) {
                      final elementId = element.id ?? -1;

                      if (teamIds.contains(elementId)) {
                        selectedAllTeamInventoryList?.add(elementId);

                        final display = element.name ?? "";
                        if (display.isNotEmpty) {
                          teamName += "$display, ";
                        }

                        element.selected = true;
                      }
                    }

                    if (teamName.isNotEmpty && teamName.endsWith(", ")) {
                      teamName = teamName.substring(0, teamName.length - 2);
                    }

                    teamController.text = teamName;
                  }
                }
              } else {
                final message = responseData.responseMessage ?? "";
                if (message.isNotEmpty) {
                  Utils.showSnackbar(
                    Strings.ERROR,
                    message,
                    AppTheme.colorWhite,
                    AppTheme.colorRed,
                  );
                }
              }
            } catch (e) {
              print("Parsing Error: $e");
            }
          }
        } else {
          final msg = responseModel.message ?? "";
          if (msg.isNotEmpty) {
            Utils.showSnackbar(
              Strings.ERROR,
              msg,
              AppTheme.colorWhite,
              AppTheme.colorRed,
            );
          }
        }

        isLoading = false;
        update();

        getStaffServiceAreaData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getStaffServiceAreaData();
      },
    );
  }


  // for address detail Service Area
  getStaffServiceAreaData() {
    isLoading = true;
    serviceAreaList!.clear();
    selectedServiceArea.clear();
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
                  String serviceAreaName = "";

                  if (wareHouseData != null &&
                      wareHouseData!.serviceAreaNameList != null &&
                      wareHouseData!.serviceAreaNameList!.isNotEmpty) {
                    for (StaffServiceAreaDetail element in serviceAreaList!) {
                      for (StaffServiceAreaDetail value
                      in wareHouseData!.serviceAreaNameList!) {
                        if (value.id == element.id) {
                          selectedServiceArea.add(element.id!);
                          serviceAreaName =
                          "$serviceAreaName${element.name!}, ";
                          element.selected = true;
                        }
                      }
                    }
                    if (!serviceAreaName.isNullOrEmpty() &&
                        serviceAreaName.contains(",") &&
                        serviceAreaName.length >= 2) {
                      serviceAreaName = serviceAreaName.substring(
                          0, serviceAreaName.length - 2);
                    }
                    servicesAreaController.text = serviceAreaName;
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

        if (wareHouseData != null &&
            selectedServiceArea.isNotEmpty) {
          getPinCodeFromArea();
        }

      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (wareHouseData != null &&
            selectedServiceArea.isNotEmpty) {
          getPinCodeFromArea();
        }
      },
    );
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  //service area to pin-code
  getPinCodeFromArea() {
    isLoading = true;
    selPincode = null;
    pincodeList!.clear();
    selCity = null;
    selState = null;
    selCountry = null;
    cityList!.clear();
    stateList!.clear();
    countryList!.clear();
    update();
    InventoryManagementProvider().getServiceAreaToPinCode(
      saId: selectedServiceArea,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PincodeListRes responseData = PincodeListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  pincodeList?.addAll(responseData.dataList!);
                  if (firstTime &&
                      wareHouseData != null &&
                      wareHouseData!.pincode != null &&
                      wareHouseData!.pincode!.isNotEmpty) {
                    print(wareHouseData!.pincode!);
                    for (PincodeDetail element in pincodeList!) {
                      if (element.id!
                          .toString()
                          .equalsIgnoreCase(wareHouseData!.pincode!)) {
                        selPincode = element;
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
        if (firstTime && selPincode != null) {
          getPinCodeToAreaList(selPincode!.id!);
          firstTime = false;
          update();
        }
        if(wareHouseData != null){
          getBranchServiceArea(selectedServiceArea);
        }

      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (firstTime) {
          getPinCodeToAreaList(selPincode!.id!);
          firstTime = false;
          update();
        }

        if(wareHouseData != null){
          getBranchServiceArea(selectedServiceArea);
        }
      },
    );
  }

  // pin-code id to address
  getPinCodeToAreaList(int id) {
    isLoading = true;
    selCity = null;
    selState = null;
    selCountry = null;
    cityList!.clear();
    stateList!.clear();
    countryList!.clear();
    update();
    CustomerProvider().getPincodeToAreaData(
      id: id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PincodeToAreaRes responseData = PincodeToAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  pincodeDetail = responseData.data;
                  if (responseData.data!.cityId != null &&
                      responseData.data!.cityName != null) {
                    cityList!.add(CityDetail(
                        id: responseData.data!.cityId,
                        name: responseData.data!.cityName));
                    selCity = cityList![0];
                  }

                  if (responseData.data!.stateId != null) {
                    stateList!.add(StateDetail(
                        id: responseData.data!.stateId,
                        name: responseData.data!.stateName));
                    selState = stateList![0];
                  }

                  if (responseData.data!.countryId != null) {
                    countryList!.add(CountryDetail(
                        id: responseData.data!.countryId,
                        name: responseData.data!.countryName));
                    selCountry = countryList![0];
                  }
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
        _handleApiError(error);
      },
    );
  }

  warehouseToParentServiceArea() {
    isLoading = true;
    update();
    InventoryManagementProvider().warehouseToParentServiceArea(
      wId: wareHouseData!.id!,
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
                  wareHouseData!.parentServiceAreaNameList = [];
                  wareHouseData!.parentServiceAreaNameList!
                      .addAll(responseData.dataList!);
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
        // getBranchServiceArea(selectedServiceArea);
        if (wareHouseData != null) {
          getWareHouseType();
        }else {
          getPinCodeFromArea();
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getBranchServiceArea(selectedServiceArea);
        if (wareHouseData != null) {
          getWareHouseType();
        }else {
          getPinCodeFromArea();
        }
      },
    );
  }

  getBranchServiceArea(List<int>? serviceAreaIds) {
    isLoading = true;
    branchList!.clear();
    update();
    CustomerProvider().getAllBranchesByServiceAreaId(
      serviceAreaId: serviceAreaIds,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BranchServiceAreaListRes responseData =
                  BranchServiceAreaListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  branchList?.addAll(responseData.dataList!);
                  if (wareHouseData != null &&
                      wareHouseData!.branchId != null) {
                    for (BranchServiceAreaDetail element in branchList!) {
                      if (element.id! == wareHouseData!.branchId) {
                        selBranch = element;
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

        if (wareHouseData == null) {
          getPinCodeFromArea();
        }

      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (wareHouseData == null) {
          getPinCodeFromArea();
        }
      },
    );
  }

  void addEditWareHouseApiCall() {
    isLoading = true;
    update();
    AddEditWareHouseReq request = AddEditWareHouseReq(
      id: wareHouseData != null ? wareHouseData!.id!.toString() : null,
      name: warehouseNameController.text,
      description: descriptionController.text,
      address1: address1Controller.text,
      address2: address2Controller.text,
      latitude: latController.text,
      longitude: longController.text,
      status: selectedStatus != null ? selectedStatus!.id : null,
      pincode: selPincode != null ? selPincode!.id : null,
      city: selCity != null ? selCity!.id : null,
      state: selState != null ? selState!.id : null,
      country: selCountry != null ? selCountry!.id : null,
      mvnoId: userDetail != null ? userDetail!.mvnoId : null,
      parentServiceAreaIdsList: selectedParentServiceArea,
      serviceAreaIdsList: selectedServiceArea,
      warehouseType: selectedType != null ? selectedType!.value : null,
      branchId: selBranch != null ? selBranch!.id : null,
      warehouseCode: warehouseCodeController.text,
      teamsIdsList: selectedAllTeamInventoryList,
    );

    InventoryManagementProvider().addEditWareHouse(
      isAdd: wareHouseData != null ? false : true,
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
