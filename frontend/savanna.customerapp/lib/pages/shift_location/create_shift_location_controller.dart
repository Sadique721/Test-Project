import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/cust_wallet_bal_req.dart';
import 'package:savbill/pages/customer/model/response/PincodeToAreaData.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/cust_address_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/customer_wallet_bal_res.dart';
import 'package:savbill/pages/customer/model/response/network_devices_by_device_type_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_detail_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer_caf/response/building_mgmt_number_res.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/revenue_report/model/cust_revenue_report_res.dart';
import 'package:savbill/pages/revenue_report/revenue_report_provider.dart';
import 'package:savbill/pages/shift_location/request/shift_cust_location_req.dart';
import 'package:savbill/pages/shift_location/response/balance_comm_for_shift_location_res.dart';
import 'package:savbill/pages/shift_location/response/charge_by_type_res.dart';
import 'package:savbill/pages/shift_location/response/partner_service_res.dart';
import 'package:savbill/pages/shift_location/shift_location_provider.dart';
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
import 'package:intl/intl.dart';

class CreateShiftLocationController extends GetxController {
  bool isLoading = false;

  List<PartnerServiceDetail>? partnerList = [];
  PartnerServiceDetail? selectedPartner;

  CustomerDetail? customerDetail;

  List<ServicesAreaDetail>? servicesAreaList = [];
  ServicesAreaDetail? selPresentServiceArea;
  ServiceAreaDetailData? areaDetail;

  List<PincodeDetail>? pincodeList = [];
  PincodeDetail? selPresentPincode;

  List<PincodeAreaDetail>? areaList = [];
  PincodeAreaDetail? selPresentArea;

  List<CityDetail>? cityList = [];
  CityDetail? selPresentCity;

  List<StateDetail>? stateList = [];
  StateDetail? selPresentState;
  List<int>? serviceAreaIds = [];

  List<CountryDetail>? countryList = [];
  CountryDetail? selPresentCountry;

  List<DropdownDetail> chargeTypeList = [];
  DropdownDetail? selectedChargeTypeList;

  List<Chargelist>? chargeList = [];
  Chargelist? selectedChargeList;

  PopDetail? selectedPop;
  List<NetworkDevicesByDeviceDataList>? oltNetworkDevicesByDeviceList = [];
  NetworkDevicesByDeviceDataList? selectedOLTDevice;
  List<BranchesByServiceAreaDataList>? branchesByServiceAreaList = [];
  BranchesByServiceAreaDataList? selectBranchesByServiceAreaData;

  TextEditingController presentAddController = TextEditingController();
  TextEditingController requesterByController = TextEditingController();
  TextEditingController walletAmountController = TextEditingController();
  TextEditingController prepaidAmountController = TextEditingController();
  TextEditingController dueAmountController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController paymentOwnerController = TextEditingController();
  TextEditingController actualPriceController = TextEditingController();
  TextEditingController newPriceController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  ParentStaffUserlist? selectedParentStaff;
  ParentStaffUserlist? selectedPaymentOwner;
  CustRevenueReportRes? responseData;
  int? requesterId = 0, billableCustomerId = 0, paymentOwnerId = 0;
  double? walletValue = 0.0, prepaidValue = 0.0;
  double? actualPrice = 0.0;
  CustAddressDetail? presentAddress;
  bool checkServiceArea = false,
      checkPinCode = false,
      checkArea = false,
      chkCity = false,
      chkState = false,
      chkCountry = false,
      samePaymentAdd = false,
      samePermanentAdd = false,
      isBranchShiftLocation = false;

  bool? isInvoiceClear;

  double? transferBalance = 0.0, transferCommission = 0.0;

  String currentDate = "", newCurrentDate = "",newCurrentWithDateMonth= "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat newApiDateFormat = DateFormat(Constant.DATE_FORMAT);
  DateFormat newApiDateFormatWithMonth = DateFormat(Constant.DATE_FORMAT_MONTH);
  ParentCustomerDetail? selectedParentCustomer;
  String? customerType = Strings.prepaid;
  String? customerTypeData ="";

  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  List<SubAreaDataList>? subAreaDataList = [];
  SubAreaDataList? selectedSubAreaData;
  List<String>? buildingNumberList = [];
  String? selectedBuildingNumber;

  List<BuildingManagementDataList>? buildingManagementDataList = [];
  BuildingManagementDataList? selectedBuildingManagementData;

  @override
  void onInit() {
    super.onInit();
    currentDate = apiDateFormat.format(DateTime.now());
    newCurrentDate = newApiDateFormat.format(DateTime.now());

    newCurrentWithDateMonth =newApiDateFormatWithMonth.format(DateTime.now());

    chargeTypeList.add(DropdownDetail(
        id: Strings.onetime, text: Strings.onetime, type: Strings.charge_type));

    chargeTypeList.add(DropdownDetail(
        id: Strings.recurring,
        text: Strings.recurring,
        type: Strings.charge_type));
    selectedChargeTypeList = chargeTypeList[0];

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        if (checkServiceArea != null && customerDetail!.serviceAreaId != null) {
          checkServiceArea = true;
        }
        if (customerDetail != null &&
            customerDetail!.addressList != null &&
            customerDetail!.addressList!.isNotEmpty) {
          for (CustAddressDetail element in customerDetail!.addressList!) {
            if (element.addressType != null &&
                element.addressType!.isNotEmpty &&
                element.addressType!.equalsIgnoreCase("Present")) {
              presentAddress = element;
            }
          }
        }
        if (presentAddress != null) {
          if (presentAddress!.pincodeId != null) {
            checkPinCode = true;
          }
          if (presentAddress!.areaId != null) {
            checkArea = true;
          }
          if (presentAddress!.cityId != null) {
            chkCity = true;
          }
          if (presentAddress!.stateId != null) {
            chkState = true;
          }
          if (presentAddress!.countryId != null) {
            chkCountry = true;
          }
          presentAddController.text = presentAddress!.landmark!;
        }
        getCustomerDiscountDetail();
      }

      if(arguments[Constant.CUSTOMER_TYPE] != null){
        customerType =  arguments[Constant.CUSTOMER_TYPE];
      }

      if(arguments[Constant.CUST_TYPE] != null){
        customerTypeData =  arguments[Constant.CUST_TYPE];
      }
    }
    update();
  }

  getCustomerDiscountDetail() {
    isLoading = true;
    partnerList!.clear();
    update();
    ShiftLocationProvider().getPartnerServiceList(
      id: customerDetail!.serviceAreaId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PartnerServiceRes response = PartnerServiceRes.fromJson(map);
              if (response.status == 200) {
                if (response.partnerList != null &&
                    response.partnerList!.isNotEmpty) {
                  for (var element in response.partnerList!) {
                    if (element.id != 1) {
                      partnerList!.add(element);
                    }
                  }
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
        getServiceArea();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getServiceArea();
      },
    );
  }

  getAllBranchesByServiceAreaData(List<int>? serviceAreaID) {
    isLoading = true;
    update();
    branchesByServiceAreaList!.clear();
    selectBranchesByServiceAreaData = null;
    CustomerProvider().getAllBranchesByServiceAreaId(
      serviceAreaId: serviceAreaID,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // BaseResponse responseData = BaseResponse.fromJson(map);
              BranchesByServiceAreaRes responseData =
              BranchesByServiceAreaRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  isBranchShiftLocation = true;
                  branchesByServiceAreaList!.addAll(responseData.dataList!);
                } else {
                  isBranchShiftLocation = false;
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
        getCustomerWalletBal();
      },
      onError: (ResponseModel error) {
        getCustomerWalletBal();
        _handleApiError(error);
      },
    );
  }

  getServiceArea() {
    isLoading = true;
    selPresentServiceArea = null;
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
                  if (checkServiceArea) {
                    servicesAreaList!.forEach((element) {
                      if (presentAddress != null &&
                          customerDetail!.serviceAreaId == element.id) {
                        selPresentServiceArea = element;
                        serviceAreaIds!.add(element.id!);
                        checkServiceArea = false;
                      }
                    });
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
        getPincodeData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPincodeData();
      },
    );
  }

  getPincodeData() {
    isLoading = true;
    selPresentPincode = null;
    pincodeList!.clear();
    areaList!.clear();
    update();
    CustomerProvider().getPincodeData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PincodeListRes responseData = PincodeListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  // responseData.dataList!.forEach((element) {
                  //   if (element.status != null &&
                  //       element.status!.equalsIgnoreCase("Active")) {
                  //     pincodeList!.add(element);
                      // if (element.pincodeid == presentAddress!.pincodeId) {
                      //   selPresentPincode = element;
                      // }

                  for (var element in responseData.dataList!) {
                        if (element.status != null &&
                            element.status!.equalsIgnoreCase("Active")) {
                          if (selPresentServiceArea!.pincodes != null &&
                              selPresentServiceArea!.pincodes!.isNotEmpty) {
                            for (int value in selPresentServiceArea!.pincodes!) {
                              if (element.pincodeid == value) {
                                pincodeList!.add(element);
                                break;
                              }
                            }
                          }

                          /*if (element.pincodeid == areaDetail!.pincodeId) {
                        selPresentPincode = element;
                        if (selPresentPincode!.areaList != null &&
                            selPresentPincode!.areaList!.isNotEmpty) {
                          areaList!.addAll(selPresentPincode!.areaList!);
                          selPresentPincode!.areaList!.forEach((areaItem) {
                            if (areaItem.id == areaDetail!.id) {
                              selPresentArea = areaItem;
                            }
                          });
                        }
                      }*/
                    }
                  }
                  for(var element in pincodeList!){
                   if( element.pincodeid == presentAddress!.pincodeId){
                        selPresentPincode = element;
                   }
                  }
                  // }
                  // });
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
        // getAreaDetail();
        getPinCodeToAreaData(presentAddress!.pincodeId!);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getAreaDetail();
        getPinCodeToAreaData(presentAddress!.pincodeId!);
      },
    );
  }

  getPinCodeToAreaData(int id) {
    selPresentArea = null;
    selPresentCity = null;
    selPresentState = null;
    selPresentCountry = null;

    areaList!.clear();
    cityList!.clear();
    stateList!.clear();
    countryList!.clear();

    isLoading = true;
    update();
    CustomerProvider().getPincodeToArea(
      pincodeid: id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PinCodeToAreaData responseData = PinCodeToAreaData.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.areaList != null &&
                    responseData.areaList!.isNotEmpty) {
                  areaList!.addAll(responseData.areaList!);
                  if (checkArea) {
                    areaList!.forEach((element) {
                      if (element.id == presentAddress!.areaId) {
                        selPresentArea = element;
                        checkArea = false;
                      }
                    });
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
        getAllPop();
        // if (chkCity || chkState || chkCountry) {
        //   getAreaDetail(selPresentArea!.id);
        // }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllPop();
        /* if (chkCity || chkState || chkCountry) {
          getAreaDetail(selPresentArea!.id);
        }*/
      },
    );
  }

  getAreaDetail(int? areaId) {
    selPresentCity = null;
    selPresentState = null;
    selPresentCountry = null;

    cityList!.clear();
    stateList!.clear();
    countryList!.clear();

    isLoading = true;
    update();
    CustomerProvider().getAreaDetail(
      areaId: areaId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddressDetailResponse responseData =
              AddressDetailResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (chkCity || chkState || chkCountry) {
                  chkCity = false;
                  chkState = false;
                  chkCountry = false;
                }
                if (responseData.data != null) {
                  if (responseData.data!.cityId != null &&
                      responseData.data!.cityName != null) {
                    cityList!.add(CityDetail(
                        id: responseData.data!.cityId,
                        name: responseData.data!.cityName));
                    selPresentCity = cityList![0];
                  }
                  if (responseData.data!.stateId != null &&
                      responseData.data!.stateName != null) {
                    stateList!.add(StateDetail(
                        id: responseData.data!.stateId,
                        name: responseData.data!.stateName));
                    selPresentState = stateList![0];
                  }
                  if (responseData.data!.countryId != null &&
                      responseData.data!.countryName != null) {
                    countryList!.add(CountryDetail(
                        id: responseData.data!.countryId,
                        name: responseData.data!.countryName));
                    selPresentCountry = countryList![0];
                  }
                }
                update();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  /* getAreaDetail() {
    selPresentArea = null;
    areaList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getServiceAllAreaDetail(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AreaListRes responseData = AreaListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  areaList!.addAll(responseData.dataList!);

                  areaList!.forEach((element) {
                    if (element.id == presentAddress!.areaId) {
                      selPresentArea = element;
                      checkArea = false;
                    }
                  });
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
        getAllCity();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllCity();
      },
    );
  }*/

  getAllCity() {
    isLoading = true;
    selPresentCity = null;
    cityList!.clear();
    update();
    CustomerProvider().getAllCity(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CityListRes responseData = CityListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.cityList != null &&
                    responseData.cityList!.isNotEmpty) {
                  cityList!.addAll(responseData.cityList!);
                  cityList!.forEach((element) {
                    if (element.id == presentAddress!.cityId) {
                      selPresentCity = element;
                    }
                  });
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

        getAllState();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getAllState() {
    isLoading = true;
    selPresentState = null;
    stateList!.clear();
    update();
    CustomerProvider().getAllState(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StateListRes responseData = StateListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.stateList != null &&
                    responseData.stateList!.isNotEmpty) {
                  stateList!.addAll(responseData.stateList!);

                  stateList!.forEach((element) {
                    if (element.id == presentAddress!.stateId) {
                      selPresentState = element;
                    }
                  });
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

        getAllCountry();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getAllCountry() {
    isLoading = true;
    selPresentCountry = null;
    countryList!.clear();
    update();
    CustomerProvider().getAllCountry(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CountryListRes responseData = CountryListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.countryList != null &&
                    responseData.countryList!.isNotEmpty) {
                  countryList!.addAll(responseData.countryList!);
                  countryList!.forEach((element) {
                    if (element.id == presentAddress!.countryId) {
                      selPresentCountry = element;
                    }
                  });
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

  updateShiftLocation() {
    List<CustChargeDetailsPojoList>? custChargeDetails = [];

    if (customerTypeData!.equalsIgnoreCase("CUSTOMER_CAF")) {
      custChargeDetails.add(CustChargeDetailsPojoList(
        actualprice: null,
        billableCustomerId: null,
        chargeDate: null,
        chargeid: null,
        discount: null,
        paymentOwnerId: null,
        price: null,
        type: null,
        billingCycle: null,
      ));
    } else {
      custChargeDetails.add(CustChargeDetailsPojoList(
        actualprice: double.parse(actualPriceController.text.toString()),
        billableCustomerId: billableCustomerId,
        chargeDate: currentDate,
        chargeid: selectedChargeList?.id ?? null,
        discount: discountController.text.isNotEmpty
            ? double.parse(discountController.text)
            : null,
        paymentOwnerId: paymentOwnerId,
        price: newPriceController.text.isNotEmpty
            ? double.parse(newPriceController.text.toString())
            : null,
        type: selectedChargeTypeList!.text,
        billingCycle:
            selectedChargeTypeList!.text!.equalsIgnoreCase(Strings.recurring)
                ? 1
                : null,
      ));
    }

    ShiftCustomerLocationReq request = ShiftCustomerLocationReq(
      addressDetails: AddressDetails(
          addressType: "Present",
          landmark: presentAddController.text,
          areaId: selPresentArea != null ? selPresentArea!.id : null,
          pincodeId:
              selPresentPincode != null ? selPresentPincode!.pincodeid : null,
          building_mgmt_id: selectedBuildingManagementData!= null ? selectedBuildingManagementData!.buildingMgmtId : null,
          buildingNumber: selectedBuildingNumber!= null ? selectedBuildingNumber : null,
          cityId: selPresentCity != null ? selPresentCity!.id : null,
          stateId: selPresentState != null ? selPresentState!.id : null,
          countryId: selPresentCountry != null ? selPresentCountry!.id : null,
        subareaId: selectedSubAreaData != null ? selectedSubAreaData!.id : null
      ),
      branchID: isBranchShiftLocation == true
          ? selectBranchesByServiceAreaData!.id
          : null,
      custChargeOverrideDTO: CustChargeOverrideDTO(
          billableCustomerId: billableCustomerId,
          custChargeDetailsPojoList: custChargeDetails,
          custid: customerDetail!.id,
          paymentOwnerId: paymentOwnerId),
      isInvoiceCleared: (customerTypeData!.equalsIgnoreCase("CUSTOMER_CAF"))
          ? true
          : isInvoiceClear,
      isPaymentAddresSame: samePaymentAdd,
      isPermanentAddress: samePermanentAdd,
      oltid: selectedOLTDevice != null ? selectedOLTDevice!.id: null,
      popid: selectedPop != null  ? selectedPop!.id : null,
      requestedById: requesterId,
      shiftPartnerid: selectedPartner != null ? selectedPartner!.id : 1,
      transferableBalance: transferBalance,
      newShiftbranchID: selectBranchesByServiceAreaData!.id,
      transferableCommission: transferCommission,
      serviceareaid:
          selPresentServiceArea != null ? selPresentServiceArea!.id : null,
      updateAddressServiceAreaId:
          selPresentServiceArea != null ? selPresentServiceArea!.id : null,
    );

    isLoading = true;
    update();
    ShiftLocationProvider().updateCustomerAddress(
      customerId: customerDetail!.id!,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                showApiResponsePopup();
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else{
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

  showApiResponsePopup() {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: "Shift Location Change Successfully.",
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              Get.back(result: true);
              Get.back(result: true);
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  ///Pop
  getAllPop() {
    isLoading = true;
    Utils.popList?.clear();
    update();
    InventoryManagementProvider().getAllPop(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewPopListRes responseData = ViewPopListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.popList?.addAll(responseData.dataList!);
                    for (var element in Utils.popList!) {
                      if (customerDetail!.popid == element.id) {
                        selectedPop = element;
                      }
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
        getNetworkDevicesByDeviceTypeAPI(Strings.olt);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getNetworkDevicesByDeviceTypeAPI(Strings.olt);
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
        update();
        getAreaDetail(areaId);
      },
      onError: (ResponseModel error) {
        getAreaDetail(areaId);
        _handleApiError(error);
      },
    );
  }

  getBuildingMgmtCall({int? entityId, String? entryName}) {
    isLoading = true;
    update();
    CustomerProvider().getBuildingMgmt(
      entityId: entityId,
      entityName: entryName,
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

  /// Get Network Devices ByDeviceType
  getNetworkDevicesByDeviceTypeAPI(String? networkDeviceType) {
    isLoading = true;
    update();
    ChargeManagementProvider().getNetworkDevicesByDeviceType(
      networkDeviceType: networkDeviceType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NetworkDevicesByDeviceTypeRes responseData =
              NetworkDevicesByDeviceTypeRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  if (networkDeviceType!.equalsIgnoreCase(Strings.olt)) {
                    oltNetworkDevicesByDeviceList!.addAll(responseData.dataList!);
                    for (var element in oltNetworkDevicesByDeviceList!) {
                      if (customerDetail!.oltid == element.id) {
                        selectedOLTDevice = element;
                      }
                    }
                  } /*else if (networkDeviceType
                      .equalsIgnoreCase(Strings.master_db)) {
                    masterDBNetworkDevicesByDeviceList!
                        .addAll(responseData.dataList!);
                  } else if (networkDeviceType
                      .equalsIgnoreCase(Strings.splitter_db)) {
                    splitterDBNetworkDevicesByDeviceList!
                        .addAll(responseData.dataList!);
                  }*/
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
        getAllBranchesByServiceAreaData(serviceAreaIds);
        update();
      },
      onError: (ResponseModel error) {
        getAllBranchesByServiceAreaData(serviceAreaIds);
        _handleApiError(error);
      },
    );
  }

  /// Wallet Amount
  getCustomerWalletBal() {
    CustomerWalletReq request = CustomerWalletReq(custId: customerDetail!.id);
    isLoading = true;
    update();
    CustomerProvider().getCustomerWalletBal(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        dueAmountController.clear();
        walletAmountController.clear();
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerWalletRes responseData = CustomerWalletRes.fromJson(map);
              if (responseData.status == 200) {
                walletValue = responseData.customerWalletDetails;
                walletAmountController.text = walletValue.toString();
                if (walletValue! >= 0) {
                  dueAmountController.text = 0.toString();
                } else {
                  dueAmountController.text =
                      responseData.customerWalletDetails!.abs().toString();
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
        getRevenueReportDetail(currentDate, currentDate);
      },
      onError: (ResponseModel error) {
        getRevenueReportDetail(currentDate, currentDate);
        _handleApiError(error);
      },
    );
  }

  /// DBR
  getRevenueReportDetail(String? startDate, String? endDate) {
    isLoading = true;
    update();
    RevenueReportProvider().getRevenueReport(
      custId: customerDetail!.id,
      startDate: startDate,
      endDate: endDate,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              responseData = CustRevenueReportRes.fromJson(map);
              if (responseData!.customerDBRPojos != null &&
                  responseData!.customerDBRPojos != null &&
                  responseData!.customerDBRPojos!.isNotEmpty) {
                for (var element in responseData!.customerDBRPojos!) {

                  if (element.month!.trim().equalsIgnoreCase(newCurrentDate.trim())) {
                    prepaidValue = (prepaidValue! + element.pendingamt!);
                    log("prepaidValue==>>${prepaidValue}");
                  }else if(element.month!.trim().equalsIgnoreCase(newCurrentWithDateMonth)){
                    prepaidValue = (prepaidValue! + element.pendingamt!);
                  }
                }
                prepaidAmountController.text = prepaidValue!.toStringAsFixed(2);
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
        if (chkCity || chkState || chkCountry) {
          getAreaDetail(selPresentArea!.id);
        }
        getChargeByType();
      },
      onError: (ResponseModel error) {
        if (chkCity || chkState || chkCountry) {
          getAreaDetail(selPresentArea!.id);
        }
        getChargeByType();
        _handleApiError(error);
      },
    );
  }

  /// BalanceAndCommissionInfoForShiftLocation
  getBalanceCommissionForShiftLocation() {
    isLoading = true;
    update();
    RevenueReportProvider().getBalanceAndCommissionInfoForShiftLocation(
      custId: customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BalanceCommissionForShiftLocationRes responseData =
              BalanceCommissionForShiftLocationRes.fromJson(map);
              if (responseData.balanceAndCommissionInfo != null) {
                isInvoiceClear =
                    responseData.balanceAndCommissionInfo!.isInvoiceClear;
                transferBalance =
                    responseData.balanceAndCommissionInfo!.transferBalance;
                transferCommission =
                    responseData.balanceAndCommissionInfo!.transferCommission;
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

  ///Charge

  getChargeByType() {
    chargeList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getChargeByType(
      type: Constant.CUSTOMER_DIRECT,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChargeByTypeRes responseData = ChargeByTypeRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.chargelist != null &&
                    responseData.chargelist!.isNotEmpty) {
                  chargeList?.addAll(responseData.chargelist!);
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
        // getSubAreaFromAreaCall(selPresentServiceArea!.id);
      },
      onError: (ResponseModel error) {
        // getSubAreaFromAreaCall(selPresentServiceArea!.id);
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

  handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }if(error.statusCode == 500){
      Utils.showSnackbar(Strings.INFO, "Invoices must be cleared for location shift",
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }
}
