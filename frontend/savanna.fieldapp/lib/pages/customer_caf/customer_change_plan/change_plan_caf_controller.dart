import 'dart:convert';
import 'dart:developer';
import 'package:get_storage/get_storage.dart';
import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
import 'package:savbill/pages/change_plan/change_plan_provicer.dart';
import 'package:savbill/pages/change_plan/request/change_plan_group_screen.dart';
import 'package:savbill/pages/change_plan/request/cust_get_plan_filter_req.dart';
import 'package:savbill/pages/change_plan/response/change_plan_date_res.dart';
import 'package:savbill/pages/change_plan/response/child_cust_change_plan_res.dart';
import 'package:savbill/pages/change_plan/response/customer_add_on_plans_res.dart';
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/change_plan/response/customer_pojo.dart';
import 'package:savbill/pages/change_plan/response/deactive_plan_cust_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/charge_data.dart';
import 'package:savbill/pages/customer/model/response/billing_cycle_res.dart';
import 'package:savbill/pages/customer/model/response/change_plan_type_res.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/payment_owner_list_resp.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/model/response/promise_to_pay_res.dart';
import 'package:savbill/pages/customer/model/response/special_plan_group_res.dart';
import 'package:savbill/pages/customer_caf/response/request/cust_change_plan_caf_req.dart';
import 'package:savbill/pages/customer_charge/response/active_plan_list_res.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/enum/enum.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

import '../../customer/model/response/service_area_plan_mode_res.dart';
import '../../login/model/response/user_detail.dart';

class ChangePlanCAFController extends GetxController {
  bool isLoading = false;
  int? page = 1;
  GetStorage getStorage = GetStorage();
  int customerId = 0;
  String customerName = "",
      customerType = "",
      quotaType = "",
      dataQuota = "",
      timeQuota = "",
      validity = "",
      finalPayAmt = "";
  int? custPlanGrpId,
      serviceAreaId,
      planServiceID,
      planId,
      newPlanId,
      planDataLength = 0;

  num discount = 0;
  DateTime? selectedStartDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  DateFormat apiDateAMPMFormat = DateFormat(Constant.DATE_NEW_TIME_FORMAT);
  int? planGroupId, serviceConnectionId, custServiceMappingId;
  String? planGroupName;

  TextEditingController remarksController = TextEditingController();
  SingingCharacter? paymentTypeSelection = SingingCharacter.no;

  List<PlanTypeDetail>? planTypeDetail = [];
  PlanTypeDetail? selectedPlanType;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;
  CustomerDetail? customerDetail;
  List<PaymentOwnerDataList> paymentOwnerList = [];
  PaymentOwnerDataList? paymentOwnerData;

  PostpaidPlanDetail? selectedPlan;

  List<PostpaidPlanDetail>? selectedPlanListDetails = [];
  PostpaidPlanDetail? postpaidPlanDetail;

  TextEditingController addOnStartDateController = TextEditingController();

  TextEditingController amountController = TextEditingController();
  TextEditingController paymentDateController = TextEditingController();
  TextEditingController paymentRemarkController = TextEditingController();

  TextEditingController bankNameController = TextEditingController();
  TextEditingController branchController = TextEditingController();
  TextEditingController referenceNoController = TextEditingController();
  TextEditingController chequeDateController = TextEditingController();
  TextEditingController chequeNoController = TextEditingController();
  TextEditingController activePlanController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController paymentOwnerController = TextEditingController();

  List<DropdownDetail> payMode = [];
  DropdownDetail? selectedPayMode;

  List<ActivePlanListDataList>? activePlanList = [];
  ActivePlanListDataList? activePlanListDetail;

  List<PostpaidplanList>? specialPlanGroupList = [];

  List<ChangePlanGroupScreen> changePlanGroup = [];
  ChangePlanGroupScreen? selectPlanGroup;

  DateTime? selectedAddonDate, selectedPaymentDate, selectedChequeDate;
  String? selectedAddonApi = "",
      selectedPaymentDateApi = "",
      selectedChequeDateApi = "",
      currentDate = "",
      planStartDate = "",
      planEndDate = "";

  List<ChargeData>? chargeDataList = [];

  // List<PlanMappingDetail> planMappingList = [];
  // PlanMappingDetail? selectedActivePlan;

  // change plan
  bool changePlanSubmitted = false;
  bool isAddCharge = false;
  bool isShowConnection = true;

  int? customerCurrentPlanListdatatotalRecords;
  List<CustomerPojo>? billableCustList = [];

  List<CustomerPlanServiceDetail> custServiceData = [];
  List<Map<String, dynamic>> serviceSerialNumbers = [];

  List<PlanGroupDetail>? planGroupFilterList = [];
  PlanGroupDetail? selPlanGroupFilter;

  List<PlanMappingGroupDetail>? planMappingListData = [];

  List<ChangePlanDateDataList>? changePlanDateList = [];
  ChangePlanDateDataList? selectChangePlanDate;

  String? changePLanDateSelection;

  List<ServiceAreaPlanPostpaidplanList>? serviceAreaAllPlanList = [];
  ServiceAreaPlanPostpaidplanList? selectedServiceAreaPlanList;

  List<ChangePlanTypeList>? changePlanTypeList = [];
  ChangePlanTypeList? selectedChangePlanList;

  List<CustomerPlanServiceDetail>? customerPlanServiceDetailList = [];
  CustomerPlanServiceDetail? customerPlanServiceDetail;
  Map<String, List<PlanMappingGroupDetail>> planGroupData = {};

  dynamic selectPlanGroupValueData;
  bool chargeAvailable = false;

  bool showChargeDetails = false;

  dynamic discountData;

  dynamic plans;

  List<ChildCustList>? childCustList = [];

  List<DiscountDetails>? discountList = [];
  Map<String, dynamic> newPlanData = {};
  dynamic newPlanSelection;
  dynamic planDetails;
  bool displayPlanDetails = false;
  int? custPaymentOwnerId;
  ParentStaffUserlist? selectedPaymentOwner;

  dynamic newPlanGroupId;

  double? planDiscount, finalOfferPrice, offerPrice;
  bool ifPlanSelectChanePlan = false, showAddDirectCharge = false;

  // dynamic selPlanData;

  List<Map<String, dynamic>> plansForChargeByCust = [];

  @override
  void onInit() {
    super.onInit();
    payMode.add(DropdownDetail(
        id: Strings.EFTs, text: Strings.EFTs, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.cash, text: Strings.cash, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.online, text: Strings.online, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.cheque, text: Strings.cheque, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.NEFT_RTGS,
        text: Strings.NEFT_RTGS,
        type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.TDS, text: Strings.TDS, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.barter, text: Strings.barter, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.direct_deposit,
        text: Strings.direct_deposit,
        type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.vat_receiveable,
        text: Strings.vat_receiveable,
        type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.pos_adjustment,
        text: Strings.pos_adjustment,
        type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.opg_adjustment,
        text: Strings.opg_adjustment,
        type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.ABBS, text: Strings.ABBS, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.other,
        text: Strings.other_adjustment,
        type: Strings.payment_mode));
    selectedPayMode = payMode[0];
    currentDate = apiDateFormat.format(DateTime.now());

    changePlanGroup.add(ChangePlanGroupScreen(
        planGroupName: Strings.individual,
        groupId: 1,
        planGroupValue: "individual"));
    changePlanGroup.add(ChangePlanGroupScreen(
        planGroupName: Strings.plan_group,
        groupId: 2,
        planGroupValue: "groupPlan"));

    getArgumentData();
    resetPlanSummary();
  }

  getArgumentData() async{
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_PLAN_GRP_ID] != null) {
        custPlanGrpId = arguments[Constant.CUSTOMER_PLAN_GRP_ID];
      }
      if (arguments[Constant.DISCOUNT] != null) {
        discount = arguments[Constant.DISCOUNT];
      }
      if (arguments[Constant.SERVICE_AREA_ID] != null) {
        serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      }
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }

      // if (arguments[Constant.CUSTOMER_PLAN_MAP] != null) {
      //   planMappingList = arguments[Constant.CUSTOMER_PLAN_MAP];
      // }

      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        customerId = customerDetail!.id!;
        getPlanServiceDataNoChangePlan(customerId);
       // getChangePlanTypeList();
      }
      String strUserData = "";

      if (getStorage.hasData(Constant.USER_DATA)) {
        strUserData = await getStorage.read(Constant.USER_DATA);
      }

      if (strUserData.trim().isNotEmpty) {
        final userDetail = UserDetail.fromJson(jsonDecode(strUserData));
        custPaymentOwnerId = userDetail.userId;
      }
    }
    update();

    if (customerDetail != null) {
      billableCustList!.add(CustomerPojo(
          id: customerDetail!.id,
          name: '${customerDetail!.title} ${customerDetail!.custname}'));
    }

    if (billableCustList!.isNotEmpty) {
      billableToController.text = billableCustList![0].name!;
    }
    update();
  }

  resetPlanSummary() {
    quotaType = "-";
    dataQuota = "-";
    timeQuota = "-";
    validity = "-";
    finalPayAmt = "-";
    update();
  }

  getPlanServiceDataNoChangePlan(int customerId) {
    isLoading = true;
    planId = null;
    update();
    CustomerProvider().getCustomerServiceManagementCaf(
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
                  customerPlanServiceDetailList?.addAll(responseData.dataList!);
                  planId = customerPlanServiceDetailList!.first.planId;
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
        getCustomerPlanType();
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getCustomerPlanType();
      },
    );
  }

  getCustomerPlanType() {
    isLoading = true;
    planTypeDetail?.clear();
    update();
    ChangePlanProvider().getCustomerPlanType(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerPlanTypeRes responseData =
                  CustomerPlanTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList!.isNotEmpty) {
                  for (var element in responseData.dataList!) {
                    if (customerType.equalsIgnoreCase("Postpaid")) {
                      if (element.text != null &&
                          !element.text!.equalsIgnoreCase("New") &&
                          !element.text!.equalsIgnoreCase("Renew") &&
                          !element.text!.equalsIgnoreCase("Upgrade")) {
                        planTypeDetail!.add(element);
                      }
                    } else {
                      if (element.text != null &&
                          !element.text!.equalsIgnoreCase("New") &&
                          !element.text!.equalsIgnoreCase("Upgrade")) {
                        planTypeDetail!.add(element);
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
        update();
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanServiceData();
      },
    );
  }

  getChangePlanTypeList() {
    //isLoading = true;
    changePlanTypeList!.clear();
    changePlanTypeList = null;
    //update();
    CustomerProvider().getAllChangeTypeList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangePlanTypeRes responseData =
              ChangePlanTypeRes.fromJson(map);
                if (responseData.changePlanTypeList != null &&
                    responseData.changePlanTypeList!.isNotEmpty) {
                  changePlanTypeList = responseData.changePlanTypeList?.reversed.toList();
                  selectedChangePlanList = changePlanTypeList!.first;
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

  getServicePlanModeServiceAreaAPI(bool isFirstTime) {
    isLoading = true;
    serviceAreaAllPlanList!.clear();
    selectedServiceAreaPlanList = null;
    // serviceAreaPlanPostpaidData = null;
    update();
    CustomerProvider().getServicePlanModeServiceAreaListPlanCategory(
      serviceAreaId: serviceAreaId,
      custId: customerId,
      //plantype: selectedChangePlanList?.value?.toLowerCase(),
      plantype: "upgrade",
      currPlanId : activePlanListDetail?.planId ?? 0,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServiceAreaPlanModeRes responseData =
                  ServiceAreaPlanModeRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  serviceAreaAllPlanList!
                      .addAll(responseData.postpaidplanList!);
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
        if(isFirstTime) {
          getActivePlanListApi();
        }
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if(isFirstTime) {
          getActivePlanListApi();
        }
      },
    );
  }

  getCustomerPlanDetailAPI(int? planId) {
    isLoading = true;
    selectedPlanListDetails!.clear();
    postpaidPlanDetail = null;
    update();
    CustomerProvider().getCustomerPlanDetail(
      planId: planId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AddChargePlanDetail responseData =
                  AddChargePlanDetail.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                // selPlanData = responseData.postPaidPlan as Map<String, dynamic>;

                if (responseData.postPaidPlan != null) {
                  selectedPlanListDetails!.add(responseData.postPaidPlan!);
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

  getPlanServiceData() {
    planServiceList!.clear();
    custServiceData.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerService(
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
                  planServiceList?.addAll(responseData.dataList!);
                  bool keepGoing = false;
                  custServiceData.clear();
                  for (var service in responseData.dataList!) {
                    if (!custServiceData.any((element) =>
                        element.connectionNo!
                            .equalsIgnoreCase(service.connectionNo!) &&
                        !service.custPlanStatus!
                            .toLowerCase()
                            .equalsIgnoreCase("newactivation") &&
                        (service.custPlanStatus == null ||
                            service.invoiceType == '' ||
                            service.invoiceType == "Independent"))) {
                      custServiceData.add(service);
                    }
                  }

                  if (custServiceData.isNotEmpty) {
                    serviceSerialNumbers = [];
                    for (var item in custServiceData) {
                      if (!keepGoing) {
                        var filteredItem = item
                            .customerInventorySerialnumberDtos!
                            .where((item) => item.primary!)
                            .toList();
                        if (filteredItem.isNotEmpty) {
                          isShowConnection = false;
                          serviceSerialNumbers.add({
                            'serialNumber': filteredItem[0].serialNumber,
                            'custPlanMapppingId': item.custPlanMapppingId,
                          });
                        } else {
                          isShowConnection = true;
                          serviceSerialNumbers = [];
                          keepGoing = true;
                        }
                      }
                    }
                  }

                  log("serviceSerialNumbers===>${jsonEncode(serviceSerialNumbers)}");

                  List<CustomerPlanServiceDetail> data = custServiceData;
                  custServiceData = [];
                  for (var element in data) {
                    if (!element.custPlanStatus!
                        .toLowerCase()
                        .equalsIgnoreCase("terminate")) {
                      custServiceData.add(element);
                    }
                  }
                  customerCurrentPlanListdatatotalRecords =
                      custServiceData.length;
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
              log("Exception==>${e.toString()}");
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
        getChildCustomersForChangePlan();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getChildCustomersForChangePlan();
      },
    );
  }

  getPromiseToRemarkApi(TextEditingController promiseRemark) {
    isLoading = true;
    update();
    CustomerProvider().getPromiseToPayRemarks(
      graceDays: 0,
      promiseToRemarks: promiseRemark.text,
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetPromiseRemarkRes responseData =
                  GetPromiseRemarkRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  Get.back();
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
              log("Exception==>${e.toString()}");
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
        getArgumentData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getArgumentData();
      },
    );
  }

  /// Get Staff User by Service Area Id
  getPaymentOwnerDataApi() {
    paymentOwnerList.clear();
    isLoading = true;
    update();
    CustomerProvider().getPaymentOwnerListService(
      id: serviceAreaId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentOwnerResp responseData = PaymentOwnerResp.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  paymentOwnerList.addAll(responseData.dataList!);
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
              log("Exception==>${e.toString()}");
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        getServicePlanModeServiceAreaAPI(true);
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getServicePlanModeServiceAreaAPI(true);
      },
    );
  }

  getSpecialPlanGroupDetail() {
    isLoading = true;
    specialPlanGroupList!.clear();
    update();
    ChangePlanProvider().getSpecialPlanGroup(
      customerId: customerId,
      serviceAreaId: serviceAreaId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SpecialPlanGroupRes responseData =
                  SpecialPlanGroupRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  if (customerType.isNotEmpty) {
                    for (PostpaidplanList element
                        in responseData.postpaidplanList!) {
                      if (element.plantype!.equalsIgnoreCase(customerType)) {
                        specialPlanGroupList!.add(element);
                      }
                    }
                  }
                }
              } else {
                if (responseData.errorMsg!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.errorMsg,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
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
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanServiceData();
      },
    );
  }

  onChangePlanType(PlanTypeDetail? planTypeEvent) {
    changePlanSubmitted = false;
    newPlanGroupId = null;
    isAddCharge = false;

    if (custServiceData.isNotEmpty &&
        custServiceData.length == 1 &&
        !planTypeEvent!.value!.equalsIgnoreCase("Addon")) {
      selectPlanGroup = changePlanGroup[0];
    } else {
      selectPlanGroup = null;
    }

    for (var element in custServiceData) {
      element.changeFlag = false;
      element.newPlanSelection = null;
    }
    update();
  }

  selectPlanCategory(
    ChangePlanGroupScreen? value,
    int childIdx,
  ) {
    if (value!.planGroupValue!.equalsIgnoreCase("individual")) {
      if (childIdx != -1) {
        childCustList![childIdx].newPlanGroupId = null;
        childCustList![childIdx].isAddCharge = false;
      } else {
        newPlanGroupId = null;
        isAddCharge = false;
      }
    }

    if (childIdx != -1) {
      childCustList![childIdx].serviceMappingData =
          childCustList![childIdx].serviceMappingData!.map((item) {
        item.changeFlag = false;
        item.newPlanSelection = null;
        return item;
      }).toList();
    } else {
      custServiceData.map((item) {
        item.changeFlag = false;
        item.newPlanSelection = null;
        return item;
      }).toList();
    }
    if (value.planGroupValue!.equalsIgnoreCase("groupPlan")) {
      getPlanChangeGroup(customerDetail);
    }
    update();
  }

//getPlanChangeGroup

  getPlanChangeGroup(CustomerDetail? customerDetail) {
    isLoading = true;
    planGroupFilterList!.clear();
    update();
    ChangePlanProvider().getChangePlanGroup(
      customerId: customerId,
      changePlanType: selectedPlanType!.value!.toLowerCase(),
      planGroupId: customerDetail?.planGroupId,
      custServiceMappingID:
          customerDetail!.planMappingList![0].custServiceMappingId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              List<dynamic> body = responseModel.result;
              planGroupFilterList = body
                  .map((dynamic item) => PlanGroupDetail.fromJson(item))
                  .toList();

              log("planGroupFilterList::::=>>${jsonEncode(planGroupFilterList)}");

              // if (responseData.status == 200) {
              //   if (responseData.planGroupList != null &&
              //       responseData.planGroupList!.isNotEmpty) {
              //     planGroupFilterList!.addAll(responseModel.result);
              //   }
              // } else {
              //   if (responseData.ERROR!.isNotEmpty) {
              //     Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
              //         AppTheme.colorWhite, AppTheme.colorRed);
              //   }
              // }

              //planGroupFilterList=json.decode(responseModel.result);
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
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

  selectPlanGroupType(PlanGroupDetail? planTypeDetail, int childIndex) {
    selPlanGroupFilter = null;
    for (var element in planGroupFilterList!) {
      if (planTypeDetail!.planGroupId == element.planGroupId) {
        selPlanGroupFilter = element;
      }
    }

    for (var element in selPlanGroupFilter!.planMappingList!) {
      planMappingListData!.add(element);
    }
    if (childIndex != -1) {
      planGroupData[childCustList![childIndex].id.toString()] =
          planMappingListData!;
    } else {
      planGroupData[customerDetail!.id.toString()] = planMappingListData!;
    }
  }

  filterPlanGroup(String? service, int childIdx) {
    log("filterPlanGroup:::$service");
    log("filterPlanGroup::childIdx:::$childIdx");

    if (childIdx != -1) {
      List<PlanMappingGroupDetail>? planGroup =
          planGroupData[childCustList![childIdx].id.toString()];
      if (planGroup != null) {
        for (var element in planGroup) {
          if (element.service!.equalsIgnoreCase(service!)) {
            element.inactive = false;
          } else {
            element.inactive = true;
          }
        }
      }
    } else {
      log("planGroupData123::::${jsonEncode(planGroupData)}");
      List<PlanMappingGroupDetail>? planGroup =
          planGroupData[customerDetail!.id.toString()];
      log("PlanMappingGroupDetail::::${jsonEncode(planGroup)}");
      if (planGroup != null) {
        planGroup.forEach((element) {
          log("planGroup:::::${element.service}");
          if (element.service!.equalsIgnoreCase(service!)) {
            element.inactive = false;
            log("planGroup11:::::${element.inactive}");
          } else {
            element.inactive = true;
            log("planGroup22:::::${element.inactive}");
          }
        });
      }
    }
  }

  getChangePlanDate() {
    isLoading = true;
    planGroupFilterList!.clear();
    update();
    ChangePlanProvider().customerChangePlanDate(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangePlanDateRes responseData = ChangePlanDateRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  changePlanDateList!.addAll(responseData.dataList!);
                  if (customerType.equalsIgnoreCase("Postpaid")) {
                    changePLanDateSelection = changePlanDateList![0].value;
                  }
                }
              } else {
                if (responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
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
        getCustomerDiscountDetail(customerId, "");
      },
      onError: (ResponseModel error) {
        getCustomerDiscountDetail(customerId, "");
        _handleApiError(error);
      },
    );
  }

  getChildCustomersForChangePlan() {
    isLoading = true;
    childCustList!.clear();
    PageRequest request = PageRequest(page: page, pageSize: 10);
    update();
    ChangePlanProvider().childCustomersChangePlan(
      pageRequest: request,
      customerId: customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChildCustChangePlanRes responseData =
                  ChildCustChangePlanRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.customerList != null &&
                    responseData.customerList!.isNotEmpty) {
                  childCustList!.addAll(responseData.customerList!);
                  for (var element in childCustList!) {
                    if (element.indiChargeList!.isEmpty) {
                      chargeAvailable = false;
                    } else {
                      chargeAvailable = true;
                    }
                    // getCustomerDiscountDetail(element.id, "");
                  }
                }
              } else {
                if (responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
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
        getChangePlanDate();
      },
      onError: (ResponseModel error) {
        getChangePlanDate();
        _handleApiError(error);
      },
    );
  }

  getCustomerDiscountDetail(int? custId, String? discountType) {
    isLoading = true;
    discountList!.clear();
    int custDiscountIndex = 0;
    update();
    ChangePlanProvider().getCustomerDiscountList(
      id: custId,
      discountType: discountType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChangeDiscountList responseData =
                  ChangeDiscountList.fromJson(map);
              if (responseData.discountDetails != null &&
                  responseData.discountDetails!.isNotEmpty) {
                for (var element in responseData.discountDetails!) {
                  if (element.newDiscount != null) {
                    element.newDiscount = element.newDiscount.toString();
                  }
                }
                discountList!.addAll(responseData.discountDetails!);

                while (custDiscountIndex < discountList!.length) {
                  if (discountList![custDiscountIndex].discount == null ||
                      discountList![custDiscountIndex].discount == "") {
                    discountList![custDiscountIndex].discount = 0;
                  }
                  discountList![custDiscountIndex].discount =
                      discountList![custDiscountIndex].discount;

                  if (discountList![custDiscountIndex].newDiscount == null ||
                      discountList![custDiscountIndex].newDiscount == "") {
                    discountList![custDiscountIndex].newDiscount = 0;
                  }
                  discountList![custDiscountIndex].newDiscount =
                      discountList![custDiscountIndex].newDiscount;

                  if (discountList![custDiscountIndex].discountType == null ||
                      discountList![custDiscountIndex].discountType == "") {
                    discountList![custDiscountIndex].discountType = "One-time";
                  }

                  if (discountList![custDiscountIndex].newDiscountType ==
                          null ||
                      discountList![custDiscountIndex].newDiscountType == "") {
                    discountList![custDiscountIndex].newDiscountType =
                        "One-time";
                  }

                  if (discountList![custDiscountIndex].discountExpiryDate !=
                          null ||
                      discountList![custDiscountIndex]
                          .discountExpiryDate!
                          .isNotEmpty) {
                    discountList![custDiscountIndex].discountExpiryDate =
                        DateTime.parse(
                      discountList![custDiscountIndex]
                          .discountExpiryDate
                          .toString(),
                    ).toUtc().toString();
                  }

                  if (discountList![custDiscountIndex].newDiscountExpiryDate !=
                          null ||
                      discountList![custDiscountIndex]
                          .newDiscountExpiryDate!
                          .isNotEmpty) {
                    discountList![custDiscountIndex].newDiscountExpiryDate =
                        DateTime.parse(
                      discountList![custDiscountIndex]
                          .newDiscountExpiryDate
                          .toString(),
                    ).toUtc().toString();
                  }

                  custDiscountIndex++;
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
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
        getPaymentOwnerDataApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPaymentOwnerDataApi();
      },
    );
  }

  changePlanSelection(
      {bool? isSelectedPlan,
      CustomerPlanServiceDetail? data,
      int? index,
      bool? isChildPlan,
      int? childIdx}) {
    // planList!.clear();
    if (isSelectedPlan == true) {
      isLoading = true;
      newPlanData.clear();
      CustGetPlanByFiltersReq payLoadRequest = CustGetPlanByFiltersReq(
        changePlanType: selectedPlanType!.value!.toLowerCase(),
        custId: customerId,
        serviceId: data!.serviceId,
        customerServiceMappingID: data.customerServiceMappingId,
      );
      log("CustGetPlanByFiltersReq===>${jsonEncode(payLoadRequest)}");
      update();
      ChangePlanProvider().getPlanByFilters(
        request: payLoadRequest,
        onSuccess: (ResponseModel responseModel) {
          isLoading = false;
          update();
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                // List<CustGetPlansByFiltersRes> users = (responseModel.result as List)
                //     .map((data) => CustGetPlansByFiltersRes.fromJson(data))
                //     .toList();
                newPlanData[data.connectionNo!] =
                    (responseModel.result as List<dynamic>)
                        .where((item) => item['plantype']
                            .toString()
                            .equalsIgnoreCase(customerType))
                        .toList();

                newPlanData[data.connectionNo!].forEach((e) {
                  if (e['plantype'] == "Postpaid") {
                    e['label'] = e['name'];
                  } else {
                    if (e['planGroup'] != "Bandwidthbooster") {
                      if (e['quotatype'] == "Data") {
                        e['label'] =
                            "${e['name']} (${data.isQosv! ? e['quota'].toString() + ' ' + e['quotaUnit'] : ''} ${e['quotaResetInterval'] == 'Total' ? '' : '/' + e['quotaResetInterval'] + ' - '} ${e['validity']} ${e['unitsOfValidity']} ${e['qospolicyName'] != null ? '-' + e['qospolicyName'] : ''})";
                      } else if (e['quotatype'] == "Time") {
                        e['label'] =
                            "${e['name']} (${e['quotatime'].toString()} ${e['quotaunittime']} ${e['quotaResetInterval'] == 'Total' ? '' : '/' + e['quotaResetInterval'] + ' - '}${e['validity']} ${e['unitsOfValidity']} ${e['qospolicyName'] != null ? '-' + e['qospolicyName'] : ''})";
                      } else if (e['quotatype'] == "Both") {
                        e['label'] =
                            "${e['name']} (${data.isQosv! ? e['quota'].toString() + ' ' + e['quotaUnit'] : ''} ${e['quotaResetInterval'] == 'Total' ? '' : '/' + e['quotaResetInterval'] + ' and '}${e['quotatime'].toString()} ${e['quotaunittime']}${e['quotaResetInterval'] == 'Total' ? '' : '/' + e['quotaResetInterval']}  - ${e['validity']} ${e['unitsOfValidity']} ${e['qospolicyName'] != null ? '-' + e['qospolicyName'] : ''})";
                      } else {
                        e['label'] = e['name'];
                      }
                    } else {
                      e['label'] = e['name'];
                    }
                  }
                  log("newPlanDataLable===>${e['label']}");
                });
              } on Exception catch (e) {
                Utils.showSnackbar(Strings.ERROR, e.toString(),
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          } else {
            if (responseModel.message!.isNotEmpty) {
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          update();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
        },
      );
    } else {
      if (isChildPlan == true) {
        childCustList![childIdx!].serviceMappingData![index!].changeFlag =
            false;
        childCustList![childIdx].serviceMappingData![index].newPlanSelection =
            null;
      } else {
        custServiceData[index!].changeFlag = false;
        custServiceData[index].newPlanSelection = null;
      }
    }
  }

  closeDisplayPlanDetails() {
    displayPlanDetails = false;
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

  _handleApiChangePlanError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.STATUS_CODE_NOT_RECORD_FOUND) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  getActivePlanListApi() {
    isLoading = true;
    custServiceMappingId = null;
    activePlanList?.clear();
    activePlanListDetail = null;
    update();
    CustomerProvider().getCustomerActivePlanList(
      planId: customerDetail!.id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ActivePlanListRes responseData = ActivePlanListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  for (ActivePlanListDataList element
                      in responseData.dataList!) {
                    custServiceMappingId = element.customerServiceMappingId;
                    planId = element.planId;
                    activePlanList?.add(element);
                    // activePlanListDetail!.connectionNo = activePlanListDetail!.connectionNo;
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  changePlans() {
    List<CustDeactivatePlanReqModel> deactivatePlanReqModelsChild = [
      CustDeactivatePlanReqModel(
          planId: customerDetail!.planMappingList![planDataLength!].planId,
          newPlanId: newPlanId,
          custServiceMappingId: custServiceMappingId,
          discount: "0.00",
          newPlanGroupId: "",
          planGroupId: "")
    ];

    CustChangePlanCafReq request = CustChangePlanCafReq(
        custId: customerDetail!.id,
        deactivatePlanReqModels: deactivatePlanReqModelsChild,
        billableCustomerId: billableCustList![0].id,
        paymentOwner: (paymentOwnerData?.fullName != null)
            ? paymentOwnerData!.fullName
            : "",
        paymentOwnerId: custPaymentOwnerId,
        planGroupChange: custPlanGrpId != null ? true : false,
        planGroupFullyChanged: custPlanGrpId != null ? true : false);
       // changePlanBillingCycle: selectedBillingCycle?.text ?? "");

    isLoading = true;
    update();
    CustomerProvider().customerCafDeActivePlan(
      custChangePlanReq: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DeActivatePlanCustRes responseData =
                  DeActivatePlanCustRes.fromJson(map);
              log("DeActivatePlanCustRes=>${responseData}");
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
            }
          }
        } else {
          Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
              AppTheme.colorWhite, AppTheme.colorGreen);
        }
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiChangePlanError(error);
      },
    );
  }

  addOnPlans(Map<String, dynamic> finalAddonData) {
    isLoading = true;
    update();
    ChangePlanProvider().customerAddOnPlan(
      finalAddonData: finalAddonData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerAddOnPlanRes responseData =
                  CustomerAddOnPlanRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              log("Exception==>${e.toString()}");
            }
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
