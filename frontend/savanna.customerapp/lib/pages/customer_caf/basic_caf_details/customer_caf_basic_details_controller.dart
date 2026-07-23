
import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/add_edit_customer_req.dart';
import 'package:savbill/pages/customer/model/response/customer_basic_details_update_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_area_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/staffs_by_service_area_res.dart';
import 'package:savbill/pages/customer_caf/customer_caf_list_controller.dart';
import 'package:savbill/pages/customer_caf/response/building_mgmt_number_res.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/pages/customer_caf/response/simple_data_res.dart';
import 'package:savbill/pages/service_management/request/add_service_req.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

import '../../login/model/response/user_detail.dart';

class CustomerCAFBasicDetailController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  TextEditingController fNameController = TextEditingController();
  TextEditingController lastNameController = TextEditingController();
  TextEditingController userNameController = TextEditingController();
  TextEditingController primaryMobileNumberController = TextEditingController();
  TextEditingController secondMobileNumberController = TextEditingController();
  TextEditingController telephoneController = TextEditingController();
  TextEditingController faxController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController panNumberController = TextEditingController();
  TextEditingController contactPersonController = TextEditingController();
  TextEditingController customerSectorController = TextEditingController();
  TextEditingController cafNumberController = TextEditingController();
  TextEditingController salesMarkController = TextEditingController();
  TextEditingController renewPlanLimitController = TextEditingController();
  TextEditingController customerSubType = TextEditingController();
  TextEditingController customerSectorType = TextEditingController();
  TextEditingController dobDateController = TextEditingController();
  TextEditingController serviceAreaController = TextEditingController();
  TextEditingController unitNoController = TextEditingController();
  TextEditingController branchController = TextEditingController();
  TextEditingController addressController = TextEditingController();
  TextEditingController landmarkController = TextEditingController();
  TextEditingController pincodeController = TextEditingController();
  TextEditingController areaController = TextEditingController();
  TextEditingController cityController = TextEditingController();
  TextEditingController stateController = TextEditingController();
  TextEditingController countryController = TextEditingController();
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();

  final subAreaDropDownKey = GlobalKey<DropdownSearchState>();
  final buildingNameDropDownKey = GlobalKey<DropdownSearchState>();
  final buildingNumberDropDownKey = GlobalKey<DropdownSearchState>();
  final customerCafListController = Get.find<CustomerCafListController>();
  bool checkBtnClickEvent = false;

  DateTime? selectedDOBDate;
  String? customerDob;
  DateFormat apiDateFormat = DateFormat(Constant.DATE_TIME_FORMAT_API_US);
  DateFormat apiCustDateFormat = DateFormat(Constant.DATE_FORMAT);

  CustomersBasicDetail? customerDetail;

  List<CustomerTitle>? bdTypeList = [];

  CustomerTitle? selectedBDType;
  String countryCode = "+256";

  List<CustomerTypeData>? custTypeList = [];
  CustomerTypeData? selectedCustType;

  bool custSubTypeDDl = false, isFirstCall = true;

  List<CustomerSubType>? customerSubTypeList = [];
  CustomerSubType? selectedCustomerSubType;

  List<CustomerSectorData>? custSectorList = [];
  CustomerSectorData? selectedCustSector;

  List<StaffsByServiceAreaData>? staffsByServiceAreaList = [];
  StaffsByServiceAreaData? selectStaffsByServiceAreaData;

  PincodeDetail? selPresentArea;

  List<SubAreaDataList>? subAreaDataList = [];
  SubAreaDataList? selectedSubAreaData;

  // int? idData;
  String? selectedMappingFrom = Strings.sub_area;

  List<BuildingManagementDataList>? buildingManagementDataList = [];
  BuildingManagementDataList? selectedBuildingManagementData;

  List<String>? buildingNumberList = [];
  String? selectedBuildingNumber;

  LocationDetail? selectedLocation;
  LocationLatLong? locationData;

  int customerId = 0;
  int? custServiceAreaId;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
    // DateTime now = DateTime.now();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
    }
  }
  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
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
    getCustomerTitle();
  }

  getCustomerTitle() {
    bdTypeList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerTitle(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerTitleRes responseData = CustomerTitleRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  bdTypeList?.addAll(responseData.dataList!);
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
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getCustomerType();
      },
      onError: (ResponseModel error) {
        getCustomerType();
        _handleApiError(error);
      },
    );
  }


  getCustomerType() {
    selectedCustType = null;
    custTypeList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerTypeRes responseData = CustomerTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  custTypeList?.addAll(responseData.dataList!);
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
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getCustomerSector();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getCustomerSector();
      },
    );
  }

  manageCustomerSubType() {
    customerSubType.clear();
    if (selectedCustType != null &&
        selectedCustType!.value != null &&
        selectedCustType!.value!.isNotEmpty) {
      if (selectedCustType!.value!.equalsIgnoreCase("barter")) {
        customerSubType.text = customerDetail!.dunningSubType ?? "";
        custSubTypeDDl = false;
      } else {
        custSubTypeDDl = true;
      }
      update();
      // getCustomerSubType();
    }
    if (custSubTypeDDl) {
      getCustomerSubType();
    }
    update();
  }

  getCustomerSubType() {
    selectedCustomerSubType = null;
    customerSubTypeList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerSubType(
      type: selectedCustType!.value!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerSubTypeData responseData =
                  CustomerSubTypeData.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  customerSubTypeList?.addAll(responseData.dataList!);
                  for (CustomerSubType element in customerSubTypeList!) {
                    if (element.text == customerDetail!.customerSubType) {
                      selectedCustomerSubType = element;
                    }
                  }
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

  getCustomerSector() {
    selectedCustSector = null;
    custSectorList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerSector(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerSectorRes responseData = CustomerSectorRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  custSectorList?.addAll(responseData.dataList!);
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
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getCustomerBasicDetail();
      },
      onError: (ResponseModel error) {
        getCustomerBasicDetail();
        _handleApiError(error);
      },
    );
  }

  getStaffsByServiceAreaAPI(int? serviceAreaId) {
    isLoading = true;
    staffsByServiceAreaList!.clear();
    selectStaffsByServiceAreaData = null;
    update();
    CustomerProvider().getStaffsByServiceAreaId(
      serviceAreaID: serviceAreaId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StaffsByServiceAreaIdRes responseData =
                  StaffsByServiceAreaIdRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  staffsByServiceAreaList!.addAll(responseData.dataList!);
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
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getPinCodeToAreaList(custServiceAreaId);
        // getSubAreaCall();
      },
      onError: (ResponseModel error) {
        // getCustomerBasicDetail();
        getPinCodeToAreaList(custServiceAreaId);
        _handleApiError(error);
        // getSubAreaCall();
      },
    );
  }
  getSubAreaCall() {
    isLoading = true;
    subAreaDataList!.clear();
    update();
    CustomerProvider().getSubArea(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSubAreaRes responseData = GetSubAreaRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  subAreaDataList!.addAll(responseData.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        update();

        // getPinCodeToAreaList(custServiceAreaId);
      },
      onError: (ResponseModel error) {

        // getPinCodeToAreaList(custServiceAreaId);
        _handleApiError(error);
      },
    );
  }

  getPinCodeToAreaList(int? id) {
    isLoading = true;
    // if (from.equalsIgnoreCase(Strings.payment_address_details)) {
      // selPaymentArea = null;
      // paymentAreaList!.clear();
      // selPaymentCity = null;
      // selPaymentState = null;
      // selPaymentCountry = null;
    // }
    // if (from.equalsIgnoreCase(Strings.permanent_address_details)) {
      // selPermanentArea = null;
      // permanentAreaList!.clear();
      // selPermanentCity = null;
      // selPermanentState = null;
      // selPermanentCountry = null;
    // }
    update();
    CustomerProvider().getPincodeToAreaData(
      id: id!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PincodeToAreaRes responseData = PincodeToAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if(responseData.data !=null){
                  selPresentArea = responseData.data;

                  pincodeController.text = selPresentArea!.pincode ?? '';
                  areaController.text = selPresentArea!.areas ?? '';
                  cityController.text = selPresentArea!.cityName ?? '';
                  stateController.text = selPresentArea!.stateName ?? '';
                  countryController.text = selPresentArea!.countryName ?? '';


                }
                // if (responseData.data != null &&
                //     responseData.data!.areaList != null &&
                //     responseData.data!.areaList!.isNotEmpty) {
                  // if (from.equalsIgnoreCase(Strings.payment_address_details)) {
                  //   paymentAreaList!.addAll(responseData.data!.areaList!);
                  // }
                  // if (from
                  //     .equalsIgnoreCase(Strings.permanent_address_details)) {
                  //   permanentAreaList!.addAll(responseData.data!.areaList!);
                  // }
                // }
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

        getSubAreaFromAreaCall(custServiceAreaId);
        update();
      },
      onError: (ResponseModel error) {

        getSubAreaFromAreaCall(custServiceAreaId);
        _handleApiError(error);
      },
    );
  }

  getSubAreaFromAreaCall(int? areaId) {
    isLoading = true;
    subAreaDataList!.clear();
    selectedSubAreaData = null;
    update();
    CustomerProvider().getSubAreaFromArea(
      areaId: areaId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSubAreaRes responseData = GetSubAreaRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  subAreaDataList!.addAll(responseData.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        getBuildingMgmtCall(custServiceAreaId);
        update();
      },
      onError: (ResponseModel error) {
        getBuildingMgmtCall(custServiceAreaId);
        _handleApiError(error);
      },
    );
  }

  getBuildingMgmtCall(int? id) {
    buildingManagementDataList!.clear();
    selectedBuildingNumber = null;
    isLoading = true;
    update();
    CustomerProvider().getBuildingMgmt(
      entityId: id,
      entityName: selectedMappingFrom,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetBuildingManagementRes responseData =
              GetBuildingManagementRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 0)) {
                if (responseData.buildingNumberList != null &&
                    responseData.buildingNumberList!.isNotEmpty) {
                  buildingManagementDataList!
                      .addAll(responseData.buildingNumberList!);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getBuildingMgmtNumbersCall(int? buildingMgmtId) {
    buildingNumberList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getBuildingMgmtNumbers(
      buildingMgmtId: buildingMgmtId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BuildingMgmtNumbersRes responseData =
              BuildingMgmtNumbersRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200) ||(responseData.responseCode != null &&
                  responseData.responseCode == 0)) {
                if (responseData != null &&
                    responseData.dataList!.isNotEmpty) {
                  buildingNumberList!.addAll(responseData.dataList!);
                  log("NumberList==>${jsonEncode(buildingNumberList)}");
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }



  getCustomerBasicDetail() {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        isFirstCall = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerBasicDetailsUpdateRes responseData =
                  CustomerBasicDetailsUpdateRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                customerDetail = responseData.customers;

                custServiceAreaId = customerDetail?.serviceareaid;

                fNameController.text = customerDetail?.firstname!;
                lastNameController.text = customerDetail?.lastname!;
                userNameController.text = customerDetail?.username!;

                serviceAreaController.text = customerDetail?.serviceareaName ?? "";
                 unitNoController.text =  customerDetail?.blockNo ?? "";
                 branchController.text =  customerDetail?.branchName ?? "";
                 addressController.text = customerDetail?.landmark ?? "";
                 pincodeController.text = customerDetail?.pincode ?? "";
                 areaController.text =  customerDetail?.area ?? "";

                countryCode = customerDetail!.countryCode != null
                    ? customerDetail!.countryCode!
                    : "+256";
                primaryMobileNumberController.text = customerDetail?.mobile!;
                secondMobileNumberController.text =
                    customerDetail!.altmobile != null
                        ? customerDetail?.altmobile!
                        : "";
                telephoneController.text = customerDetail!.altphone != null
                    ? customerDetail!.altphone!
                    : "";
                faxController.text =
                    customerDetail?.fax != null ? customerDetail?.fax! : "";
                emailController.text =
                    customerDetail?.email != null ? customerDetail?.email! : "";
                panNumberController.text =
                    customerDetail?.pan != null ? customerDetail?.pan! : "";
                contactPersonController.text =
                    customerDetail?.contactperson != null
                        ? customerDetail?.contactperson!
                        : "";
                cafNumberController.text = customerDetail?.cafno != null ? customerDetail?.cafno! : "";
                salesMarkController.text = customerDetail?.salesremark != null
                    ? customerDetail?.salesremark!
                    : "";
                if(customerDetail?.renewPlanLimit != null){
                  renewPlanLimitController.text = customerDetail!.renewPlanLimit.toString();
                }

                if (customerDetail?.birthDate != null) {
                  DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
                      .parse(customerDetail?.birthDate);

                  selectedDOBDate = date;
                  customerDob = apiDateFormat.format(date);
                  dobDateController.text = apiCustDateFormat.format(date);
                }

                for (CustomerTitle element in bdTypeList!) {
                  if (element.text == customerDetail?.title) {
                    selectedBDType = element;
                  }
                }

                for (CustomerTypeData element in custTypeList!) {
                  if (element.value == customerDetail?.customerType) {
                    selectedCustType = element;
                    manageCustomerSubType();
                    update();
                  }
                }

                for (StaffsByServiceAreaData element
                    in staffsByServiceAreaList!) {
                  if (element.id == customerDetail?.staffId) {
                    selectStaffsByServiceAreaData = element;
                  }
                }
                for (CustomerSectorData element in custSectorList!) {
                  if (element.value == customerDetail?.customerSector) {
                    selectedCustSector = element;
                  }
                }
                customerSectorType.text =
                    customerDetail?.customerSubSector != null
                        ? customerDetail?.customerSubSector!
                        : "";

                // log("dunningSubType==>${customerDetail!.customerSubType}");
                // log("dunningSubTypeList==>${jsonEncode(customerSubTypeList)}");

                for (CustomerSubType element in customerSubTypeList!) {
                  if (element.text == customerDetail?.customerSubType) {
                    selectedCustomerSubType = element;
                  }
                }
                customerSubType.text = customerDetail?.customerSubType ?? "";
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
        getStaffsByServiceAreaAPI(custServiceAreaId);
        update();
      },
      onError: (ResponseModel error) {
        getStaffsByServiceAreaAPI(custServiceAreaId);
        isFirstCall = false;
        _handleApiError(error);
      },
    );
  }

  updateCustomerDetailsApiCall() {
    int? phone, mobileNumber, alternateNumber, faxNumber;
    String? billTo;
    List<PlanMappingList> planMapping = [];
    List<AddressList>? addressList = [];
    List<OverChargesDetails>? overChargesList = [];
    List<CustMacMapppingList>? custMacMappingList = [];

    if (telephoneController.text.isNotEmpty) {
      phone = int.parse(telephoneController.text.trim());
    }

    if (primaryMobileNumberController.text.isNotEmpty) {
      mobileNumber = int.parse(primaryMobileNumberController.text.trim());
    }

    if (secondMobileNumberController.text.isNotEmpty) {
      alternateNumber = int.parse(secondMobileNumberController.text.trim());
    }

    if (faxController.text.isNotEmpty) {
      faxNumber = int.parse(faxController.text.trim());
    }

    for (var element in customerDetail!.planMappingList!) {
      planMapping.add(element);
      billTo = element.billTo;
      break;
    }

    for (var element in customerDetail!.addressList!) {
      addressList.add(element);
      break;
    }

    String todayDt = apiDateFormat.format(DateTime.now());

    countryCode = customerDetail!.countryCode != null
        ? customerDetail!.countryCode!
        : "+256";

    PaymentDetails paymentDetails = PaymentDetails(
      amount: 0,
      paymode: "",
      referenceno: "",
      paymentdate: "",
    );

    AddEditCustomerReq request = AddEditCustomerReq(
      username: userNameController.text,
      password: customerDetail!.password,
      firstname: fNameController.text,
      lastname: lastNameController.text,
      email: emailController.text,
      title: selectedBDType != null ? selectedBDType!.value : "",
      pan: panNumberController.text,
      gst: customerDetail!.gst,
      aadhar: customerDetail!.aadhar,
      isCredentialMatchWithAccountNo: customerDetail?.isCredentialMatchWithAccountNo == true ? true : false,
      passportNo: customerDetail!.passportNo,
      tinNo: customerDetail!.tinNo,
      contactperson: customerDetail!.contactperson,
      failcount: customerDetail!.failcount,
      custtype: customerDetail!.custtype,
      custlabel: customerDetail!.custlabel,
      phone: phone ?? "",
      mobile: mobileNumber.toString(),
      altmobile: alternateNumber,
      vlan_id: "",
      fax: faxNumber ?? "",
      birthDate: customerDob ?? "",
      countryCode: countryCode,
      customerType: customerDetail != null ? customerDetail!.customerType : "",
      customerSubType: custSubTypeDDl
          ? selectedCustomerSubType != null
              ? selectedCustomerSubType!.value
              : ""
          : customerSubType.text,
      customerSector:
          selectedCustSector != null ? selectedCustSector!.value : "",
      customerSubSector: customerSectorType.text,
      cafno: customerDetail!.cafno,
      voicesrvtype: customerDetail!.voicesrvtype,
      didno: customerDetail!.didno,
      calendarType: customerDetail!.calendarType,
      partnerid: customerDetail!.partnerid,
      salesremark: salesMarkController.text,
      renewPlanLimit: renewPlanLimitController.text.isNotEmpty ? int.parse(renewPlanLimitController.text.toString()) : null ,
      servicetype: customerDetail!.servicetype,
      serviceareaid: customerDetail!.serviceareaid,
      status: customerDetail!.status,
      parentCustomerId: customerDetail!.parentCustomerId,
      parentQuotaType: customerDetail!.parentQuotaType ?? "",
      latitude: customerDetail!.latitude,
      longitude: customerDetail!.longitude,
      billTo: customerDetail!.planMappingList!.isNotEmpty ? customerDetail!.planMappingList![0].billTo : "",
      billableCustomerId: customerDetail?.billableCustomerId,
      isInvoiceToOrg: customerDetail!.isinvoicestop,
      istrialplan: customerDetail!.istrialplan,
      popid: customerDetail!.popid,
      staffId: selectStaffsByServiceAreaData?.displayId,
      locations: customerDetail!.locations ?? [],
      discount: customerDetail!.discount,
      flatAmount: customerDetail!.flatAmount ?? 0,
      plangroupid: customerDetail!.plangroupid,
      discountType: customerDetail!.discountType,
      discountExpiryDate: customerDetail!.discountExpiryDate,
      planMappingList: customerDetail!.planMappingList!.isNotEmpty ? customerDetail!.planMappingList! : null,
      addressList: addressList,
      branch: customerDetail!.branch,
      oltid: customerDetail?.oltid,
      masterdbid: customerDetail!.masterdbid,
      splitterid: customerDetail!.splitterid,
      nasIpAddress: "",
      nasPort: customerDetail!.nasPort ?? "",
      framedIp: customerDetail!.framedIp,
      framedIpBind: customerDetail!.framedIpBind,
      ipPoolNameBind: customerDetail!.ipPoolNameBind,
      valleyType: customerDetail!.valleyType,
      customerArea: customerDetail!.customerArea,
      paymentDetails: customerDetail?.paymentDetails,
      framedIpv6Address: "",
      isCustCaf: "yes",
      dunningCategory: customerDetail?.dunningCategory,
      billday: null,
      department: customerDetail?.department,
      mac_auth_enable: true,
      mac_provision: true,
      // macRetentionUnit: "DAY",
      // macRetentionPeriod: 1,
      skipQuotaUpdate: false,
      primaryDNS: "",
      primaryIPv6DNS: "",
      secondaryDNS: "",
      secondaryIPv6DNS: "",
      acctno: customerDetail!.acctno ?? "",

    );

    log("Request Data Update==> ${jsonEncode(request)}");
    isLoading = true;
    update();
    CustomerProvider().updateCustomerBasicDetailsRequest(
      id: customerId,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) || (responseData.responseCode != null && responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    "Customer Update Successfully",
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);

              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }

                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
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
        customerCafListController.getCustomerListData();
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
                  locationData = responseData.location;
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

}
