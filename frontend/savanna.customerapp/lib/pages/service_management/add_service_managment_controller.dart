import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/service_management/request/add_service_req.dart';
import 'package:savbill/pages/service_management/response/add_new_service_res.dart';
import 'package:savbill/pages/service_management/response/get_plan_by_service_id_res.dart';
import 'package:savbill/pages/service_management/service_management_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

import '../customer/model/response/cust_plan_detail.dart';

class AddServiceManagementController extends GetxController {
  bool isLoading = false;
  CustomerDetail? customerDetail;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  TextEditingController billToController = TextEditingController();
  TextEditingController planCategoryNameController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController expiryDateController = TextEditingController();
  TextEditingController enterValidityController = TextEditingController();
  TextEditingController offerPriceController = TextEditingController();
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  String? customerType ;
  ParentCustomerDetail? selectedParentCustomer;
  int? billableCustomerId = 0, planServiceID;
  String? serviceNameValue;
  DateTime? selectedExpiryDateTime;
  List<CustomerPlanServiceDetail>? customerServiceList = [];
  DateFormat apiDateFormatChange = DateFormat(Constant.DATE_FORMAT);
  int? planId;
  num? validityTime;


  // PlanMappingDetail? selectedPlanMappingDetails;
  List<PlanMappingDetail>? selectedPlanMappingList = [];
  List<PlanByServiceId>? planByServiceIdList = [];
  PlanByServiceId? selectedPlanByServiceId;
  List<ServicesByServiceAreaDataList>? servicesByServiceAreaDataList = [];
  ServicesByServiceAreaDataList? selectServicesByServiceAreaData;
  List<PlanServiceDetail>? allPlanServiceList = [];
  List<PlanServiceDetail>? selectedPlanServiceList = [];
  List<int>? serviceAreaId = [];
  List<PostpaidPlanDetail>? planList = [];
  List<DropdownDetail>? discountTypeList = [];
  DropdownDetail? selectedDiscountType;
  String? expiryDiscountDateFormat, unitOfValidity;
  bool? isTrial = false;
  AddServiceReq? addServiceReq;

  @override
  void onInit() {
    super.onInit();
    discountTypeList!.add(DropdownDetail(
        id: Strings.onetime, text: Strings.onetime, type: Strings.charge_type));
    discountTypeList!.add(DropdownDetail(
        id: Strings.recurring,
        text: Strings.recurring,
        type: Strings.charge_type));
    // selectedDiscountType = discountTypeList![0];
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
    getArgumentData();
  }

  getArgumentData() {
    if (serviceAreaId!.isNotEmpty) {
      serviceAreaId!.clear();
    }
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
    }

    log("customerDetailcustomerDetail==>>>${jsonEncode(customerDetail)}");
    serviceAreaId!.add(customerDetail!.serviceAreaId!);
    planCategoryNameController.text = customerDetail!.planPurchaseType!;
    selectedPlanMappingList = customerDetail!.planMappingList!
        .where((element) =>
            element.custPlanStatus!.equalsIgnoreCase(Strings.active))
        .toList();
    billToController.text = selectedPlanMappingList![0].billTo!;
    getAllServicesByServiceAreaIdData(serviceAreaId);
    update();
  }

  getPlanServiceData(int customerId) {
    // customerServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerServiceManagement(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServiceByCustomerRes responseData =
                  PlanServiceByCustomerRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  customerServiceList?.addAll(responseData.dataList!);
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

  getAllServicesByServiceAreaIdData(List<int>? serviceAreaID) {
    isLoading = true;
    update();
    servicesByServiceAreaDataList!.clear();
    selectServicesByServiceAreaData = null;
    CustomerProvider().getAllServicesByServiceAreaId(
      serviceAreaId: serviceAreaID,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllServicesByServiceAreaRes responseData =
                  GetAllServicesByServiceAreaRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesByServiceAreaDataList!.addAll(responseData.dataList!);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getPlanServicesDetail() {
    isLoading = true;
    allPlanServiceList!.clear();
    selectedPlanByServiceId = null;
    selectedPlanServiceList!.clear();
    planServiceID = 0;
    update();
    CustomerProvider().getPlanService(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServicesRes responseData = PlanServicesRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.serviceList != null &&
                    responseData.serviceList!.isNotEmpty) {
                  allPlanServiceList!.addAll(responseData.serviceList!);
                  selectedPlanServiceList!.addAll(allPlanServiceList!.where(
                      (element) =>
                          element.name!.equalsIgnoreCase(serviceNameValue!)));
                  if (selectedPlanServiceList!.isNotEmpty) {
                    planServiceID = selectedPlanServiceList![0].id;
                    getPlanByServiceId(planServiceID);
                  }
                  log("serviceAreaAllPlanList>> ${json.encode(selectedPlanServiceList)}");
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

  getPlanByServiceId(int? serviceId) {
    isLoading = true;
    update();
    ServiceManagementProvider().getPlanByServiceId(
      serviceId: serviceId!,
      customerType: customerType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetPlanByServiceIdRes response =
                  GetPlanByServiceIdRes.fromJson(map);
              if (response.status == 200) {
                if (response.postPaidPlan!.isNotEmpty) {
                  planByServiceIdList?.clear();
                  planByServiceIdList?.addAll(response.postPaidPlan!);
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

  getPlanDetailFromPlanId(int customerPlanId) {
    isLoading = true;
    planList!.clear();
    update();
    ChargeManagementProvider().getCustomerPlanDetail(
      planId: customerPlanId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddChargePlanDetail responseData =
                  AddChargePlanDetail.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postPaidPlan != null) {
                  validityTime = responseData.postPaidPlan!.validity;
                  unitOfValidity = responseData.postPaidPlan!.unitsOfValidity;
                  enterValidityController.text =
                      "${responseData.postPaidPlan!.validity} ${responseData.postPaidPlan!.unitsOfValidity}";
                  offerPriceController.text =
                      "${responseData.postPaidPlan!.offerprice}";
                  // planList?.add(responseData.postPaidPlan!);
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
        return true;
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        return true;
      },
    );
  }

  void addServiceApi() {
    List<PlanMappingList>? planMappingList = [];
    planMappingList.add(PlanMappingList(
        discount: discountController.text.isNotEmpty ? double.parse(discountController.text) : null,
        planId: selectedPlanByServiceId != null
            ? selectedPlanByServiceId!.id
            : null,
        service: selectServicesByServiceAreaData != null
            ? selectServicesByServiceAreaData!.name
            : "",
        serviceId: selectServicesByServiceAreaData != null
            ? selectServicesByServiceAreaData!.id
            : null,
        validity: double.parse(validityTime.toString()),
        offerPrice: double.parse(offerPriceController.text),
        validityUnit: unitOfValidity,
        istrialplan: isTrial,
        discountType:
        selectedDiscountType != null ? selectedDiscountType!.text : "",
        discountExpiryDate: selectedDiscountType != null &&
            selectedDiscountType!.text!.equalsIgnoreCase(Strings.recurring)
            ? expiryDateController.text
            : null,
        planCategory: planCategoryNameController.text.isNotEmpty ? planCategoryNameController.text : null,
        billTo: billToController.text.isNotEmpty ? billToController.text : null,
        billableCustomerId: billableCustomerId,
        newAmount: offerPriceController.text.isNotEmpty ? double.parse(offerPriceController.text) : null,
        isInvoiceToOrg: customerDetail!.planMappingList!.isNotEmpty
            ? customerDetail!.planMappingList![0].isInvoiceToOrg
            : false));
    customerServiceList!.clear();
    isLoading = true;
    update();
    addServiceReq = AddServiceReq(
      id: customerDetail!.id,
      failcount: customerDetail!.failcount,
      custtype: customerDetail!.custtype,
      countryCode: customerDetail!.countryCode,
      cafno: customerDetail!.cafno,
      calendarType: customerDetail!.calendarType,
      partnerid: customerDetail!.partnerid,
      serviceareaid: customerDetail!.serviceAreaId,
      status: customerDetail!.status,
      billableCustomerId: billableCustomerId,
      planMappingList: planMappingList,
      addressList: customerDetail!.addressList,
      dunningCategory: customerDetail!.dunningCategory,
    );
    ServiceManagementProvider().addNewService(
      request: addServiceReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddNewServiceRes responseData = AddNewServiceRes.fromJson(map);
              if (responseData.status == 200) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    Strings.successfully,
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
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          log("ResponseModelError==>");
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        getPlanServiceData(customerDetail!.id!);
        update();
      },
      onError: (ResponseModel error) {
        log("ResponseModelError==>${error}");
        _handleApiErrorCustom(error);
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
  _handleApiErrorCustom(ResponseModel error) {

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
