import 'dart:convert';
import 'dart:developer';
import 'package:get_storage/get_storage.dart';
import 'package:savbill/pages/change_discount/response/change_discount_list.dart';
import 'package:savbill/pages/change_plan/add_direct_charge/add_direct_charge.dart';
import 'package:savbill/pages/change_plan/change_plan_provicer.dart';
import 'package:savbill/pages/change_plan/plan_detail_dialog.dart';
import 'package:savbill/pages/change_plan/request/change_plan_group_screen.dart';
import 'package:savbill/pages/change_plan/request/change_plan_req.dart';
import 'package:savbill/pages/change_plan/request/charge_override_req.dart';
import 'package:savbill/pages/change_plan/request/cust_get_plan_filter_req.dart';
import 'package:savbill/pages/change_plan/request/plan_start_end_date_req.dart';
import 'package:savbill/pages/change_plan/response/change_plan_date_res.dart';
import 'package:savbill/pages/change_plan/response/child_cust_change_plan_res.dart';
import 'package:savbill/pages/change_plan/response/chk_prime_customer_res.dart';
import 'package:savbill/pages/change_plan/response/customer_add_on_plans_res.dart';
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/change_plan/response/customer_pojo.dart';
import 'package:savbill/pages/change_plan/response/deactive_plan_cust_res.dart';
import 'package:savbill/pages/change_plan/response/plan_date_detail_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/charge_data.dart';
import 'package:savbill/pages/customer/model/response/cust_plan_detail.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer/model/response/payment_owner_list_resp.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/model/response/promise_to_pay_res.dart';
import 'package:savbill/pages/customer/model/response/special_plan_group_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/customer_charge/response/add_charge_plan_detail.dart';
import 'package:savbill/pages/dashboard/model/response/final_amount_tax_res.dart';
import 'package:savbill/pages/enum/enum.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
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

import '../../util/acl_constant.dart';
import '../../util/permission_service.dart';
import '../customer/model/response/billing_cycle_res.dart';
import '../customer/model/response/change_plan_type_res.dart';
import '../dashboard/model/response/payment_configuration_res.dart';
import '../dashboard/payment_provider.dart';
import '../login/model/response/user_detail.dart';
import 'add_direct_charge/show_direct_charge_list_dialog.dart';

class ChangePlanController extends GetxController {
  bool isLoading = false, isCustomerPrime = false;
  int? page = 1;

  int customerId = 0;
  String customerName = "",
      customerType = "",
      quotaType = "",
      dataQuota = "",
      timeQuota = "",
      validity = "",
      price = "",
      finalPayAmt = "";
  int? custPlanGrpId, serviceAreaId;

  num discount = 0;
  DateTime? selectedStartDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  DateFormat apiDateAMPMFormat = DateFormat(Constant.DATE_NEW_TIME_FORMAT);
  int? planGroupId, serviceConnectionId, custServiceMappingId;
  String? planGroupName;
  String? allowChangePlan;

  TextEditingController remarksController = TextEditingController();
  TextEditingController externalRemarksController = TextEditingController();
  SingingCharacter? paymentTypeSelection = SingingCharacter.no;

  List<PlanTypeDetail>? planTypeDetail = [];
  PlanTypeDetail? selectedPlanType;

  List<ChangePlanTypeList>? changePlanTypeList = [];
  ChangePlanTypeList? selectedChangePlanList;

  List<BillingCycleList>? billingCycleList = [];
  BillingCycleList? selectedBillingCycle;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;
  CustomerDetail? customerDetail;
  List<PaymentOwnerDataList> paymentOwnerList = [];
  PaymentOwnerDataList? paymentOwnerData;

  List<PostpaidPlanDetail>? postpaidPlanAllData = [];
  List<PostpaidPlanDetail>? premierePlanAllData = [];

  List<PostpaidPlanDetail>? postpaidPlanList = [];
  List<PostpaidPlanDetail>? selectPlanList = [];
  PostpaidPlanDetail? selectedPlan;

  List<PostpaidPlanDetail> selectedPlanList = [];

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

  List<PlanGroupDetail>? planGroupList = [];
  List<PostpaidplanList>? specialPlanGroupList = [];
  PostpaidplanList? specialPlanGroup;

  PlanGroupDetail? selPlanGroup;
  List<ChangePlanGroupScreen> changePlanGroup = [];
  ChangePlanGroupScreen? selectPlanGroup;
  GetStorage getStorage = GetStorage();
  DateTime? selectedAddonDate, selectedPaymentDate, selectedChequeDate;
  String? selectedAddonApi = "",
      selectedPaymentDateApi = "",
      selectedChequeDateApi = "",
      currentDate = "",
      planStartDate = "",
      planEndDate = "";

  bool addDirectCharge = false;

  List<ChargeData>? chargeDataList = [];

  List<PlanMappingDetail> planMappingList = [];
  PlanMappingDetail? selectedActivePlan;

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

  // dynamic planGroupData;
  List<ChangePlanDateDataList>? changePlanDateList = [];

  ChangePlanDateDataList? selectChangePlanDate;
  String? changePLanDateSelection;

  // var planGroupData = <int, List<PlanMappingGroupDetail>>{}.obs;
  Map<String, List<PlanMappingGroupDetail>> planGroupData = {};

  dynamic selectPlanGroupValueData;

  // Map<String, dynamic> planGroupData = {};
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

  dynamic newPlanGroupId;

  double? planDiscount, finalOfferPrice, offerPrice;
  bool ifPlanSelectChanePlan = false, showAddDirectCharge = false;

  // dynamic selPlanData;

  List<Map<String, dynamic>> plansForChargeByCust = [];

  Map<String, dynamic> selPlanData = {};

  // int? billableToCustomerId;
  // ParentCustomerDetail? selectedParentCustomer;

  List<dynamic> chargeData = [];
  bool? ifPlanGroup = false;
  bool subisuChange = false;

  List<Map<String, dynamic>> plansArray = [];
  List<Map<String, dynamic>> plansForCharge = [];
  List<dynamic> overChargeListFromArray = [];

  dynamic addedChargeList = [];
  List<dynamic> filteredCharge = [];

  @override
  void onInit() async {
    super.onInit();
    payMode.add(DropdownDetail(
        id: Strings.cash, text: Strings.cash, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.cheque, text: Strings.cheque, type: Strings.payment_mode));
    payMode.add(DropdownDetail(
        id: Strings.online, text: Strings.online, type: Strings.payment_mode));
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

    // selPlanData = {
    //   'quotatype': '',
    //   'quotatime': '',
    //   'quota': '',
    //   'quotaUnit': '',
    //   'quotaunittime': '',
    //   'validity': '',
    //   'offerprice': '',
    //   'taxamount': '',
    //   'activationDate': '',
    //   'expiryDate': '',
    //   'finalAmount': '',
    // };
    String strUserData = "";

    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (strUserData.trim().isNotEmpty) {
      final userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      custPaymentOwnerId = userDetail.userId;
    }

    getArgumentData();
    resetPlanSummary();
    // getChangePlanTypeList();
    getBillingCycleAPI();
    getSystemConfigurationData();
  }

  getArgumentData() {
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

      if (arguments[Constant.CUSTOMER_PLAN_MAP] != null) {
        planMappingList = arguments[Constant.CUSTOMER_PLAN_MAP];
      }

      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
        customerId = customerDetail!.id!;
        getCustomerPlanType();
      }
    }
    // update();

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
    price = "-";
    finalPayAmt = "-";
    update();
  }

  setPlanSummary() {
    if (selectedPlanType != null &&
        selectedPlanType!.text != null &&
        (selectedPlanType!.text!.equalsIgnoreCase("Renew") ||
            selectedPlanType!.text!.equalsIgnoreCase("Change Plan"))) {
      quotaType = "-";
      dataQuota = "-";
      timeQuota = "-";
      validity = "-";
      // price = "-";
      //  finalPayAmt = "-";

      num price = 0, finalPrice = 0;

      if (selectedPlanList.isNotEmpty) {
        for (PostpaidPlanDetail element in selectedPlanList) {
          if (element.offerprice != null) {
            price = price + element.offerprice!;
            if (element.taxamount != null) {
              finalPrice =
                  finalPrice + element.offerprice! + element.taxamount!;
            } else {
              finalPrice = finalPrice + element.offerprice!;
            }
          } else {
            if (element.taxamount != null) {
              finalPrice = finalPrice + element.taxamount!;
            }
          }
        }
      }
      this.price = price.toStringAsFixed(2);
      finalPayAmt = finalPrice.toStringAsFixed(2);
    }

    if (custPlanGrpId == null ||
        selectedPlanType != null &&
            selectedPlanType!.text != null &&
            selectedPlanType!.text!.equalsIgnoreCase("Addon")) {
      if (selectedPlan != null) {
        quotaType = selectedPlan!.quotatype!;
        if (selectedPlan!.quotatype!.equalsIgnoreCase(Strings.api_data) ||
            selectedPlan!.quotatype!.equalsIgnoreCase(Strings.api_both)) {
          dataQuota = "${selectedPlan!.quota} ${selectedPlan!.quotaUnit!}";
        } else {
          dataQuota = "-";
        }

        if (selectedPlan!.quotatype!.equalsIgnoreCase(Strings.api_time) ||
            selectedPlan!.quotatype!.equalsIgnoreCase(Strings.api_both)) {
          timeQuota =
              "${selectedPlan!.quotatime!.toInt()} ${selectedPlan!.quotaunittime!}";
        } else {
          timeQuota = "-";
        }

        if (selectedPlan!.newOfferPrice != null &&
            selectedPlan!.newOfferPrice! > 0) {
          price = selectedPlan!.newOfferPrice!.toStringAsFixed(2);
        } else {
          price = selectedPlan!.offerprice!.toStringAsFixed(2);
        }

        validity =
            "${selectedPlan!.validity!.toInt()} ${selectedPlan!.unitsOfValidity!}";
        // price = selectedPlan!.offerprice!.toStringAsFixed(2);
        // price = selectedPlan!.offerprice!.toStringAsFixed(2);
        // finalPayAmt = (selectedPlan!.offerprice! + selectedPlan!.taxamount!).toStringAsFixed(2);
        finalPayAmt = (selectedPlan!.offerprice!).toStringAsFixed(2);
      } else {
        quotaType = "-";
        dataQuota = "-";
        timeQuota = "-";
        validity = "-";
        price = "-";
        finalPayAmt = "-";
      }
    }

    update();
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
                        if (element.text!.equalsIgnoreCase("Change Plan")) {
                          if (PermissionService().hasAclPermission([
                                AclPostCustConstants
                                    .POST_CUST_CHANGE_PLAN_DROPDOWN
                              ]) ==
                              true) {
                            planTypeDetail!.add(element);
                          }
                        } else {
                          planTypeDetail!.add(element);
                        }
                      }
                    } else {
                      if (element.text != null &&
                          !element.text!.equalsIgnoreCase("New") &&
                          !element.text!.equalsIgnoreCase("Upgrade")) {
                        if (element.text!.equalsIgnoreCase("Change Plan")) {
                          if (PermissionService().hasAclPermission([
                                AclPreCustConstants
                                    .PRE_CUST_CHANGE_PLAN_DROPDOWN
                              ]) ==
                              true) {
                            planTypeDetail!.add(element);
                          }
                        } else {
                          planTypeDetail!.add(element);
                        }
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
        // chkCustomerIsPrime();
        // getPlanGroupNormalDetail();
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        getPlanServiceData();
        _handleApiError(error);
        // chkCustomerIsPrime();
        // getPlanGroupNormalDetail();
      },
    );
  }

  chkCustomerIsPrime() {
    isLoading = true;
    update();
    ChangePlanProvider().chkCustomerPrime(
      custId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChkPrimeCustomerRes responseData =
                  ChkPrimeCustomerRes.fromJson(map);
              if (responseData.status == 200 &&
                  responseData.isCustomerPrime == true) {
                isCustomerPrime = true;
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
        // if (isCustomerPrime) {
        //   getCustomerPremierePlan();
        // } else {
        //   /// planGroupMappings with planCategory & Cust Id
        //   getCustomerPlan();
        // }

        getPlanGroupNormalDetail();
        /* if (custPlanGrpId == null) {
          getCustomerPlan();
        } else {
          getPlanGroupDetail();
        }*/
      },
      onError: (ResponseModel error) {
        _handleApiError(error);

        getPlanGroupNormalDetail();
        // if (isCustomerPrime) {
        //   getCustomerPremierePlan();
        // } else {
        //   /// planGroupMappings with planCategory & Cust Id
        //   getCustomerPlan();
        // }
        /* if (custPlanGrpId == null) {
          getCustomerPlan();
        } else {
          getPlanGroupDetail();
        }*/
      },
    );
  }

  filterPlanAsPerPlanType() {
    selectedPlan = null;
    selPlanGroup = null;
    selectedPlanList.clear();
    postpaidPlanList!.clear();
    if (selectedPlanType != null &&
        selectedPlanType!.text != null &&
        postpaidPlanAllData != null &&
        postpaidPlanAllData!.isNotEmpty) {
      for (PostpaidPlanDetail element in postpaidPlanAllData!) {
        if (element.plantype!.isNotEmpty &&
            element.plantype!.equalsIgnoreCase(customerType)) {
          if (element.planGroup != null && element.planGroup!.isNotEmpty) {
            if (selectedPlanType!.text!.equalsIgnoreCase("Renew") ||
                element.planGroup!
                    .equalsIgnoreCase("Registration and Renewal")) {
              selectPlanList!.add(element);
            }

            if (selectedPlanType!.text!.equalsIgnoreCase("Renew") &&
                element.planGroup!
                    .equalsIgnoreCase("Registration and Renewal")) {
              postpaidPlanList!.add(element);
            }

            if ((selectedPlanType!.text!.equalsIgnoreCase("Addon")) &&
                element.planGroup!.equalsIgnoreCase("Volume Booster")) {
              postpaidPlanList!.add(element);
            }

            if (selectedPlanType!.text!.equalsIgnoreCase("Change Plan")) {
              postpaidPlanList!.add(element);
            }
          }
        }
      }
    }

    if (isCustomerPrime &&
        premierePlanAllData != null &&
        premierePlanAllData!.isNotEmpty) {
      for (PostpaidPlanDetail premiereElement in premierePlanAllData!) {
        // selectPlanList?.add(premiereElement);

        if (selectPlanList != null && selectPlanList!.isNotEmpty) {
          PostpaidPlanDetail? stuff = selectPlanList?.firstWhereOrNull(
              (element) => (element.id == premiereElement.id));
          if (stuff == null) {
            selectPlanList?.add(premiereElement);
          }
        } else {
          selectPlanList?.add(premiereElement);
        }

        if (postpaidPlanList != null && postpaidPlanList!.isNotEmpty) {
          PostpaidPlanDetail? stuff = postpaidPlanList?.firstWhereOrNull(
              (element) => (element.id == premiereElement.id));
          if (stuff == null) {
            postpaidPlanList?.add(premiereElement);
          }
        } else {
          postpaidPlanList?.add(premiereElement);
        }
      }
    }
    update();
  }

  getPlanGroupDetail() {
    isLoading = true;
    planGroupList!.clear();
    update();
    CustomerProvider().getChangePlanGroupLst(
      custId: customerId,
      planMode: 'NORMAL',
      planCategory: 'Normal',
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanGroupRes responseData = PlanGroupRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.planGroupList != null &&
                    responseData.planGroupList!.isNotEmpty) {
                  if (customerType.isNotEmpty) {
                    for (PlanGroupDetail element
                        in responseData.planGroupList!) {
                      if (element.plantype!.equalsIgnoreCase(customerType)) {
                        planGroupList!.add(element);
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
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanServiceData();
      },
    );
  }

  getPlanGroupNormalDetail() {
    isLoading = true;
    planGroupList!.clear();
    update();
    CustomerProvider().getPlanGroup(
      planMode: 'NORMAL',
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanGroupRes responseData = PlanGroupRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.planGroupList != null &&
                    responseData.planGroupList!.isNotEmpty) {
                  if (customerType.isNotEmpty) {
                    for (PlanGroupDetail element
                        in responseData.planGroupList!) {
                      if (element.plantype!.equalsIgnoreCase(customerType)) {
                        planGroupList!.add(element);
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
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanServiceData();
      },
    );
  }

  getCustomerPremierePlan() {
    premierePlanAllData!.clear();
    isLoading = true;
    update();
    ChangePlanProvider().getPremierePlan(
      serviceAreaId: serviceAreaId!,
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PostpaidPlanListRes responseData =
                  PostpaidPlanListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  // premierePlanAllData!.addAll(responseData.postpaidplanList!);
                  responseData.postpaidplanList?.forEach((element) {
                    if (element.serviceId == serviceConnectionId) {
                      if (element.planGroup!
                              .equalsIgnoreCase("Registration and Renewal") ||
                          element.planGroup!.equalsIgnoreCase("Registration")) {
                        premierePlanAllData!.add(element);
                      }
                    }
                  });
                  // premierePlanAllData!.addAll(responseData.postpaidplanList!);
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

        /// planGroupMappings with normal
        // getCustomerPlan();
        // getPlanGroupDetail();
        // getPlanGroupNormalDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getCustomerPlan();
        // getPlanGroupDetail();
        // getPlanGroupNormalDetail();
      },
    );
  }

  getCustomerPlan() {
    postpaidPlanAllData!.clear();
    isLoading = true;
    update();
    ChangePlanProvider().getServiceAreaToPlan(
      serviceAreaId: serviceAreaId!,
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PostpaidPlanListRes responseData =
                  PostpaidPlanListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  postpaidPlanAllData!.addAll(responseData.postpaidplanList!);
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
        getPlanGroupDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanGroupDetail();
      },
    );
  }

  // deActivePlanReq() {
  //   DeactivatePlanReq request = DeactivatePlanReq(
  //       custId: customerId,
  //       planGroupChange: custPlanGrpId != null ? true : false,
  //       planGroupFullyChanged: custPlanGrpId != null ? true : false,
  //       deactivatePlanReqModels: [
  //         DeactivatePlanReqDetail(
  //             newPlanGroupId:
  //                 selPlanGroup != null ? selPlanGroup!.planGroupId : null,
  //             planGroupId: custPlanGrpId,
  //             newPlanId: selectedPlan != null ? selectedPlan!.id : null,
  //             planId:
  //                 selectedActivePlan != null ? selectedActivePlan!.id : null)
  //       ]);
  //   isLoading = true;
  //   update();
  //   ChangePlanProvider().deactivatePlan(
  //     request: request,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             BaseResponse responseData = BaseResponse.fromJson(map);
  //             if ((responseData.responseCode != null &&
  //                     responseData.responseCode == 200) ||
  //                 (responseData.status != null && responseData.status == 200)) {
  //               showApiResponsePopup();
  //             } else {
  //               if (responseData.ERROR!.isNotEmpty) {
  //                 Utils.showSnackbar(Strings.ERROR, responseData.message!,
  //                     AppTheme.colorWhite, AppTheme.colorRed);
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
  //       _handleApiError(error);
  //     },
  //   );
  // }

  getPlanDateDetailReq() {
    PlanStartEndDateReq request =
        PlanStartEndDateReq(changePlanRequestDTOList: [
      PlanStartEndDateDetailReq(
          purchaseType: selectedPlanType!.value,
          planId: selectedPlan!.id,
          isPaymentReceived: "false",
          remarks: "",
          addonStartDate: selectedAddonApi,
          isAdvRenewal: false,
          custId: customerId,
          isRefund: false,
          discount: 0)
    ]);

    isLoading = true;
    update();
    ChangePlanProvider().planStartEndDateReq(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanDateDetailRes responseData = PlanDateDetailRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.data != null &&
                    responseData.data!.startDate != null &&
                    responseData.data!.startDate!.isNotEmpty) {
                  DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
                      .parse(responseData.data!.startDate!);
                  planStartDate =
                      DateFormat(Constant.API_DATE_TIME_FORMAT).format(date);
                }
                if (responseData.data != null &&
                    responseData.data!.endDate != null &&
                    responseData.data!.endDate!.isNotEmpty) {
                  DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
                      .parse(responseData.data!.endDate!);
                  planEndDate =
                      DateFormat(Constant.API_DATE_TIME_FORMAT).format(date);
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

  changePlanReq() {
    List<PlanBindWithOldPlans> planBindWithOldPlans = [];
    List<int> newPlanList = [];
    if (selPlanGroup != null && selectedPlanList.isNotEmpty) {
      for (PostpaidPlanDetail element in selectedPlanList) {
        planBindWithOldPlans.add(PlanBindWithOldPlans(oldPlanId: element.id));
        newPlanList.add(element.id!);
      }
    }
    ChangePlanRequestDTOList data = ChangePlanRequestDTOList(
        purchaseType: selectedPlanType != null ? selectedPlanType!.text : "",
        planGroupId: selPlanGroup?.planGroupId,
        planId: selectedPlan?.id,
        isPaymentReceived:
            paymentTypeSelection == SingingCharacter.yes ? "true" : "false",
        remarks: remarksController.text,
        paymentOwner: paymentOwnerData!.fullName,
        recordPaymentDTO: paymentTypeSelection == SingingCharacter.yes
            ? RecordPaymentDTO(
                paymentAmount: amountController.text,
                paymentDate: selectedPaymentDateApi,
                paymentMode:
                    selectedPayMode != null ? selectedPayMode!.text : "",
                referenceNo: referenceNoController.text,
                bankName: bankNameController.text,
                branch: branchController.text,
                remarks: paymentRemarkController.text,
                isTdsDeducted: false,
                custId: customerId,
                chequeNo: chequeNoController.text,
                chequeDate: selectedChequeDateApi)
            : null,
        addonStartDate: selectedAddonApi,
        isAdvRenewal: false,
        custId: customerId,
        isRefund: false,
        discount: discount,
        planBindWithOldPlans:
            selPlanGroup != null ? planBindWithOldPlans : null,
        newPlanList: selPlanGroup != null ? newPlanList : null,
        planMappingList: null,
        custServiceMappingId: custServiceMappingId);

    List<ChangePlanRequestDTOList> arrayData = [];
    arrayData.add(data);
    ChangePlanReq request = ChangePlanReq(changePlanRequestDTOList: arrayData);
    isLoading = true;
    update();
    ChangePlanProvider().changePlan(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (addDirectCharge &&
                    chargeDataList != null &&
                    chargeDataList!.isNotEmpty) {
                  changeOverRideReq();
                } else {
                  showApiResponsePopup();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  changeOverRideReq() {
    List<CustChargeDetails> data = [];
    for (ChargeData element in chargeDataList!) {
      double price = 0;
      if (element.price != null && element.price!.isNotEmpty) {
        price = double.parse(element.price!);
      }
      data.add(CustChargeDetails(
          type: element.chargeType,
          chargeid: element.chargeDetail!.id,
          validity: element.planDetail!.validity,
          price: price,
          actualprice: element.chargeDetail!.price,
          chargeDate: currentDate,
          planid: element.planDetail!.id,
          unitsOfValidity: element.planDetail!.unitsOfValidity,
          billingCycle: element.recMonth));
    }
    ChargeOverrideReq request =
        ChargeOverrideReq(custid: customerId, custChargeDetailsPojoList: data);
    isLoading = true;
    update();
    ChangePlanProvider().customerOverrideCharge(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                showApiResponsePopup();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  showApiResponsePopup() {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: Strings.app_name,
            message: "Plan Change Successfully.",
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              Get.back();
              Get.back();
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  discountApplyDialog(
      int? customerPlanId, CustomerPlanServiceDetail? custServiceMapping) {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: "Change Discount Confirmation",
            message:
                "Do you want to apply ${custServiceMapping!.discount} % of  Discount?",
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              planDiscount = custServiceMapping.discount;
              updateDiscountFromService(customerPlanId);
              custServiceMapping.newDiscount = custServiceMapping.discount;
              Get.back();
            },
            negativeBtnClick: () {
              Get.back();
            });
      },
    );
  }

  changePlanConfirmDialog({String? title, String? textMsg}) {
    showDialog(
      context: Get.context!,
      builder: (BuildContext context) {
        return AlertDialogHelper(
            title: title,
            message: textMsg,
            positiveBtnText: Strings.ok,
            positiveBtnClick: () {
              subisuChange = true;
              plansArray.add({
                'planId': [selPlanData['id']],
                'name': [selPlanData['displayName']],
                'service': [selPlanData['serviceId']],
                'validity': [selPlanData['validity']],
                'discount': [selPlanData['discount']],
                'billTo': ['ORGANIZATION'],
                'offerPrice': [selPlanData['offerprice']],
                'newAmount': [
                  selPlanData['newAmount'] ?? selPlanData['offerprice']
                ],
                'chargeName': [selPlanData['chargeList'][0]['charge']['name']],
                'isInvoiceToOrg': [customerDetail!.isInvoiceToOrg],
                'istrialplan': [customerDetail!.istrialplan],
              });
              Get.back();
            },
            negativeBtnClick: () {
              subisuChange = false;
              Utils.showSnackbar(Strings.INFO, "You have rejected",
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
              Get.back();
            });
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

                  log("custServiceData===>${jsonEncode(custServiceData)}");

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
        // getPaymentOwnerDataApi();
        getChildCustomersForChangePlan();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getPaymentOwnerDataApi();
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
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
                    getCustomerDiscountDetail(element.id, "");
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
          //plantype: selectedChangePlanList?.value?.toLowerCase(),
          plantype: "upgrade",
          currPlanId: data.planId ?? 0);
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

  openChargeDetails(int? custId, BuildContext context) {
    var filteredCharge = chargeData
        .firstWhere((item) => item['custid'] == custId, orElse: () => null);
    if (filteredCharge != null) {
      addedChargeList = filteredCharge['custChargeDetailsPojoList'];
    }
    showChargeDetails = true;
    showDirectChargeDialog(addedChargeList, showChargeDetails, context);
  }

  modalOpenDetails(
    int? newPlanId,
    String connectionNo,
    int custId,
    String selectedPlanCategory,
    BuildContext context,
  ) {
    if (selectedPlanCategory == "groupPlan") {
      final plans = planGroupData[custId];
      if (plans != null) {
        for (var e in plans) {
          if (e.plan!.id == newPlanId) {
            planDetails = e.plan;
            break;
          }
        }
      }
    } else {
      final plans = newPlanData[connectionNo];
      if (plans != null) {
        for (var e in plans) {
          if (e['id'] == newPlanId) {
            planDetails = e;
            break;
          }
        }
      }
    }
    displayPlanDetails = true;
    update();
    showPlanDetailDialog(
      planDetails,
      0.0,
      displayPlanDetails,
      context,
    );
  }

  showPlanDetailDialog(dynamic planDetails, double? discount,
      bool? displayPlanDetails, BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return PlanDetailsDialog(
            planDetails: planDetails,
            planDiscount: discount,
            displayPlanDetails: displayPlanDetails,
          );
        });
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

  onDirectChargeChange(bool? value, int? custId) {
    plansForCharge =
        plansForChargeByCust.where((item) => item['custId'] == custId).toList();

    if (value == true) {
      showAddDirectCharge = true;
      filteredCharge =
          chargeData.where((item) => item['custid'] == custId).toList();

      if (filteredCharge.isNotEmpty) {
        for (var element in filteredCharge) {
          overChargeListFromArray.add({
            'type': element['type'],
            'custid': element['custId'],
            'chargeid': element['chargeid'],
            'validity': element['validity'],
            'price': element['price'],
            'actualprice': element['actualprice'],
            'charge_date': element['charge_date'],
            'planid': element['planid'],
            'planName': element['planName'],
            'unitsOfValidity': element['unitsOfValidity'],
            'billingCycle': element['billingCycle'],
            'discount': element['discount'],
            'staticIPAdrress': element['staticIPAdrress'],
          });
        }
      }
    } else {
      var index = chargeData.indexWhere((item) => item['custid'] == custId);
      if (index != -1) chargeData.removeAt(index);
      addedChargeList = [];
    }

    if (showAddDirectCharge == true) {
      openAddDirectChargeScreen(plansForCharge);
    }
  }

  bool isPlanSelected(int? custId) {
    plans =
        plansForChargeByCust.where((element) => element['custId'] == custId);
    if (plans != null) {
      return false;
    }
    return true;
  }

  openAddDirectChargeScreen(List<Map<String, dynamic>> plansForCharge) async {
    var result = await Get.to(AddDirectCharge(), arguments: {
      Constant.CUSTOMER_DETAIL: customerDetail,
      Constant.PLAN_FOR_CHARGE_DATA: plansForCharge,
    });
    List<dynamic> chargeList = [];
    if (result != null) {
      chargeList = result;
      log("openAddDirectChargeScreen:::${jsonEncode(chargeList)}");

      chargeData.add({
        'custChargeDetailsPojoList': chargeList,
        'custid': customerDetail!.id,
        'parentId': customerDetail!.id,
        'billableCustomerId': customerDetail!.id,
        'paymentOwnerId': custPaymentOwnerId,
      });

      log("openAddDirectChargeChargeData11:::${jsonEncode(chargeData)}");

      // List<ChargeData> selectedList = result;
      // if (selectedList.isNotEmpty) {
      //   chargeDataList!.clear();
      //   chargeDataList!.addAll(selectedList);
      //   addDirectCharge = true;
      // }
    }
    update();
  }

  prepareChangePlanPayload(dynamic recordPaymentPojo, BuildContext? context) {
    if (selectedPlanType!.text!.equalsIgnoreCase("Renew")) {
      List<Map<String, dynamic>> planBindWithOldPlans = [];
      List<String> planList = [];
      List<Map<String, dynamic>> changePlanRequestDTOList = [];
      Map<String, dynamic> pojo = {};

      if (selectPlanGroup!.planGroupName!.equalsIgnoreCase("groupPlan")) {
        pojo = {
          'purchaseType': "Renew",
          'isPaymentReceived':
              paymentTypeSelection == SingingCharacter.no ? true : false,
          'remarks': remarksController.text,
          'paymentOwnerId': custPaymentOwnerId,
          'billableCustomerId': billableCustList![0].id,
          'addonStartDate': null,
          'ChangePlanCategory': "",
          'isAdvRenewal': false,
          'custId': customerId,
          'recordPaymentDTO': {},
          'isRefund': false,
          'planBindWithOldPlans': planBindWithOldPlans,
          'newPlanList': planList,
          'planMappingList': null,
          'isParent': true,
        };

        for (var element in custServiceData) {
          if (element.newPlanSelection != null) {
            Map<String, dynamic> data = {
              'newPlanId': element.newPlanSelection,
              'custServiceMappingId': element.customerServiceMappingId,
              'oldPlanId': element.planId,
              'discount': element.newDiscount,
            };
            planList.add(element.newPlanSelection);
            planBindWithOldPlans.add(data);
            pojo['planGroupId'] = newPlanGroupId;
            pojo['planBindWithOldPlans'] = planBindWithOldPlans;
            pojo['custServiceMappingId'] = element.customerServiceMappingId;
            pojo['newPlanList'] = planList;
            pojo['planId'] = element.newPlanSelection;
          }
        }
        changePlanRequestDTOList.add(pojo);
      } else if (selectPlanGroup!.planGroupName!
          .equalsIgnoreCase("individual")) {
        for (var element in custServiceData) {
          if (element.newPlanSelection != null) {
            Map<String, dynamic> pojo = {
              'purchaseType': "Renew",
              'isPaymentReceived':
                  paymentTypeSelection == SingingCharacter.no ? false : true,
              'remarks': remarksController.text,
              'paymentOwnerId': custPaymentOwnerId,
              'billableCustomerId': billableCustList![0].id,
              'addonStartDate': null,
              'ChangePlanCategory': "",
              'isAdvRenewal': false,
              'custId': billableCustList![0].id,
              'recordPaymentDTO': {},
              'isRefund': false,
              'planBindWithOldPlans': planBindWithOldPlans,
              'newPlanList': null,
              'planMappingList': null,
              'isParent': true,
            };
            pojo['discount'] = element.newDiscount ?? 0;
            pojo['planId'] = element.newPlanSelection;
            pojo['custServiceMappingId'] = element.customerServiceMappingId;
            changePlanRequestDTOList.add(pojo);
          }
        }
      }
      if (childCustList!.isNotEmpty) {
        for (var childCust in childCustList!) {
          List<Map<String, dynamic>> childPlanBindWithOldPlans = [];
          List<String> childPlanList = [];
          if (childCust.serviceMappingData!.isNotEmpty) {
            if (selectPlanGroup!.planGroupName != null &&
                selectPlanGroup!.planGroupName!.isNotEmpty) {
              if (selectPlanGroup!.planGroupName!
                  .equalsIgnoreCase("groupPlan")) {
                Map<String, dynamic> pojo = {
                  'purchaseType': "Renew",
                  'isPaymentReceived':
                      paymentTypeSelection == SingingCharacter.no
                          ? true
                          : false,
                  'remarks': remarksController.text,
                  'paymentOwnerId': custPaymentOwnerId,
                  'billableCustomerId': billableCustList![0].id,
                  'addonStartDate': null,
                  'ChangePlanCategory': "",
                  'isAdvRenewal': false,
                  'custId': childCust.id,
                  'recordPaymentDTO': {},
                  'isRefund': false,
                  'planBindWithOldPlans': childPlanBindWithOldPlans,
                  'newPlanList': childPlanList,
                  'planMappingList': null,
                  'isParent': true,
                };
                for (var element in childCust.serviceMappingData!) {
                  if (element.newPlanSelection != null) {
                    Map<String, dynamic> data = {
                      "newPlanId": element.newPlanSelection,
                      // "custServiceMappingId": element.customerServiceMappingId,
                      // "oldPlanId": element.planId,
                      // "discount": element.newDiscount,
                    };

                    childPlanList.add(element.newPlanSelection);
                    childPlanBindWithOldPlans.add(data);
                    pojo["planGroupId"] = childCust.newPlanGroupId;
                    pojo["planBindWithOldPlans"] = childPlanBindWithOldPlans;
                    // pojo["custServiceMappingId"] = element.customerServiceMappingId;
                    pojo["newPlanList"] = childPlanList;
                    pojo["planId"] = element.newPlanSelection;
                  }
                }
                changePlanRequestDTOList.add(pojo);
                // Additional logic for child plans would go here
              } else if (selectPlanGroup!.planGroupName!
                  .equalsIgnoreCase("individual")) {
                childCust.serviceMappingData!.forEach((element) {
                  if (element.newPlanSelection != null) {
                    Map<String, dynamic> pojo = {
                      "purchaseType": "Renew",
                      "isPaymentReceived":
                          paymentTypeSelection == SingingCharacter.no
                              ? true
                              : false,
                      "remarks": remarksController.text,
                      "paymentOwnerId": custPaymentOwnerId,
                      "billableCustomerId": billableCustList![0].id,
                      "addonStartDate": null,
                      "ChangePlanCategory": "",
                      "isAdvRenewal": false,
                      "custId": childCust.id,
                      "recordPaymentDTO": {},
                      "isRefund": false,
                      "planBindWithOldPlans": childPlanBindWithOldPlans,
                      "newPlanList": null,
                      "planMappingList": null,
                      "isParent": false,
                    };
                    // pojo["discount"] = element.newDiscount;
                    pojo["planId"] = element.newPlanSelection;
                    // pojo["custServiceMappingId"] = element.customerServiceMappingId;
                    changePlanRequestDTOList.add(pojo);
                  }
                });
              }
            }
          }
        }
      }

      Map<String, dynamic> finalRenewData = {
        'changePlanRequestDTOList': changePlanRequestDTOList,
        'custChargeDetailsList': chargeData,
        'recordPayment': null,
      };
      if (recordPaymentPojo != null) {
        finalRenewData['recordPayment'] = recordPaymentPojo;
      }
      if (allowChangePlan != null && allowChangePlan!.equalsIgnoreCase("Yes")) {
        isLoading = true;
        update();
        Map<String, dynamic> finalData = {
          'custId': customerDetail!.id,
          'changePlanBillingCycle': selectedBillingCycle?.text ?? "",
          'purchaseType': "Renew",
        };
        for (var element in custServiceData) {
          if (element.newPlanSelection != null) {
            finalData.addAll({
              'newPlanId': element.newPlanSelection,
              'custPackRelId': element.planmapid,
              'oldPlanId': element.planId,
            });
          }
        }
        ChangePlanProvider().getCustomerChangePlanDueAmount(
          finalData: finalData,
          onSuccess: (ResponseModel responseModel) {
            if (responseModel.statusCode == 200) {
              if (responseModel.result != null) {
                try {
                  Map<String, dynamic> map = responseModel.result;
                  double amount = (map["Amount"] ?? 0).toDouble();
                  if (amount == 0) {
                    RenewPlans(finalRenewData);
                  } else {
                    print("Amount is not zero: $amount");
                    showDialog(
                        context: context!,
                        barrierDismissible: true,
                        builder: (BuildContext context) {
                          return AlertDialogHelper(
                              title: "Insufficient Balance",
                              message:
                              "Please top up customer wallet balance to change plan.",
                              positiveBtnText: Strings.ok,
                              negativeBtnText: Strings.cancel,
                              positiveBtnClick: () {
                                Get.back();
                                Get.back();
                                Get.back();
                              },
                              negativeBtnClick: () {
                                Get.back();
                              });
                        });
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
      } else {
        RenewPlans(finalRenewData);
      }
    } else if (selectedPlanType!.text!.equalsIgnoreCase("Addon")) {
      List<dynamic> changePlanRequestDTOList = [];
      for (var element in custServiceData) {
        if (element.newPlanSelection != null) {
          Map<String, dynamic> addonPojo = {
            'connectionNo': element.connectionNo,
            'serviceName': element.planName,
            'serviceNickName': element.nickname,
            'purchaseType': "Addon",
            'planId': element.newPlanSelection,
            // 'planGroupId': newPlanGroupId,
            'isPaymentReceived':
                paymentTypeSelection == SingingCharacter.yes ? "true" : "false",
            'remarks': remarksController.text,
            'paymentOwnerId': custPaymentOwnerId,
            'billableCustomerId': billableCustList![0].id,
            'addonStartDate': DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .format(DateTime.now())
                .toString(),
            'ChangePlanCategory': "",
            'isAdvRenewal': false,
            'custId': customerDetail!.id,
            'recordPaymentDTO': {},
            'isRefund': false,
            'discount': element.newDiscount,
            'planBindWithOldPlans': [],
            'newPlanList': null,
            'planMappingList': null,
            'custServiceMappingId': element.customerServiceMappingId,
            'isParent': true,
          };
          if (customerDetail!.planGroupId != null) {
            addonPojo["planGroupId"] = customerDetail!.planGroupId;
          }
          changePlanRequestDTOList.add(addonPojo);
        }
      }
      Map<String, dynamic> finalAddonData = {
        'changePlanRequestDTOList': changePlanRequestDTOList,
        'recordPayment': null,
      };

      if (recordPaymentPojo != null) {
        finalAddonData['recordPayment'] = recordPaymentPojo;
      }
      addOnPlans(finalAddonData);
    } else {
      if (allowChangePlan != null && allowChangePlan!.equalsIgnoreCase("Yes")) {
        isLoading = true;
        update();
        Map<String, dynamic> finalData = {
          'custId': customerDetail!.id,
          'changePlanBillingCycle': selectedBillingCycle?.text ?? "",
          'purchaseType': "Change Plan",
        };
        for (var element in custServiceData) {
          if (element.newPlanSelection != null) {
            finalData.addAll({
              'newPlanId': element.newPlanSelection,
              'custPackRelId': element.planmapid,
              'oldPlanId': element.planId,
            });
          }
        }
        ChangePlanProvider().getCustomerChangePlanDueAmount(
          finalData: finalData,
          onSuccess: (ResponseModel responseModel) {
            if (responseModel.statusCode == 200) {
              if (responseModel.result != null) {
                try {
                  Map<String, dynamic> map = responseModel.result;
                  double amount = (map["Amount"] ?? 0).toDouble();
                  if (amount == 0) {
                    submitChangePlan(recordPaymentPojo);
                  } else {
                    print("Amount is not zero: $amount");
                    showDialog(
                        context: context!,
                        barrierDismissible: true,
                        builder: (BuildContext context) {
                          return AlertDialogHelper(
                              title: "Insufficient Balance",
                              message:
                                  "Please top up customer wallet balance to change plan.",
                              positiveBtnText: Strings.ok,
                              negativeBtnText: Strings.cancel,
                              positiveBtnClick: () {
                                Get.back();
                                Get.back();
                                Get.back();
                              },
                              negativeBtnClick: () {
                                Get.back();
                              });
                        });
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
      } else {
        submitChangePlan(recordPaymentPojo);
      }
    }
  }

  submitChangePlan(dynamic recordPaymentPojo) {
    List<dynamic> deactivatePlanReqModels = [];
    Map<String, dynamic> finalData = {
      'deactivatePlanReqDTOS': [],
      'recordPayment': null,
    };

    log("planGroupName==>${selectPlanGroup!.planGroupName}");

    if (selectPlanGroup!.planGroupName != null &&
        selectPlanGroup!.planGroupName!.isNotEmpty &&
        selectPlanGroup!.planGroupName != '') {
      log("custServiceData==>${jsonEncode(custServiceData)}");

      for (var element in custServiceData) {
        if (element.newPlanSelection != null) {
          Map<String, dynamic> data = {
            'billToOrg': false,
            'newPlanGroupId': newPlanGroupId,
            'planGroupId': customerDetail!.planGroupId,
            'newPlanId': element.newPlanSelection,
            'custServiceMappingId': element.customerServiceMappingId,
            'discount': element.newDiscount,
          };
          deactivatePlanReqModels.add(data);
        }
      }

      log("deactivatePlanReqModels==>${jsonEncode(deactivatePlanReqModels)}");

      if (deactivatePlanReqModels.isNotEmpty) {
        finalData['deactivatePlanReqDTOS'].add({
          'custId': customerDetail!.id,
          'deactivatePlanReqModels': deactivatePlanReqModels,
          'planGroupChange':
              selectPlanGroup!.planGroupName!.equalsIgnoreCase("groupPlan"),
          'planGroupFullyChanged':
              selectPlanGroup!.planGroupName!.equalsIgnoreCase("groupPlan"),
          'paymentOwner': 'yogesh Patil',
          'paymentOwnerId': custPaymentOwnerId,
          'billableCustomerId': billableCustList![0].id,
          'isParent': true,
          'remark': remarksController.text,
          'changePlanDate': changePLanDateSelection,
          'changePlanBillingCycle': selectedBillingCycle?.text ?? ""
        });
      }
    }
    for (var childCust in childCustList!) {
      // if (childCust.selectedPlanCategory != null &&
      //     childCust.selectedPlanCategory.isNotEmpty) {
      List<dynamic> deactivatePlanReqModelsChild = [];
      childCust.serviceMappingData!.forEach((element) {
        if (element.newPlanSelection != null) {
          Map<String, dynamic> changeDetails = {
            'billToOrg': false,
            'newPlanGroupId': childCust.newPlanGroupId,
            'planGroupId': childCust.plangroupid,
            'newPlanId': element.newPlanSelection,
            'discount': 0.0
            // 'custServiceMappingId': element.customerServiceMappingId,
            // 'discount': element.newDiscount,
          };
          deactivatePlanReqModelsChild.add(changeDetails);
        }
      });
      if (deactivatePlanReqModelsChild.isNotEmpty) {
        Map<String, dynamic> childPojo = {
          'custId': childCust.id,
          'deactivatePlanReqModels': deactivatePlanReqModelsChild,
          // 'planGroupChange': childCust.selectedPlanCategory == "groupPlan",
          // 'planGroupFullyChanged': childCust.selectedPlanCategory == "groupPlan",
          'paymentOwner': "yogesh Patil",
          'paymentOwnerId': custPaymentOwnerId,
          'billableCustomerId': billableCustList![0].id,
          'isParent': false,
          'remark': remarksController.text,
          'changePlanBillingCycle': selectedBillingCycle?.text ?? ""
        };
        finalData['deactivatePlanReqDTOS'].add(childPojo);
        // finalData.deactivatePlanReqDTOS.add(childPojo);
      }
      // }
    }
    finalData['skipQuotaUpdate'] = false;
    if (recordPaymentPojo != null) {
      finalData['recordPayment'] = recordPaymentPojo;
    }
    changePlans(finalData);
  }

  getCustomerChangePlanDueAmount(dynamic recordPaymentPojo) {}

  changePlans(Map<String, dynamic> finalData) {
    isLoading = true;
    update();
    ChangePlanProvider().customerDeActivePlanInBluk(
      finalData: finalData,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DeActivatePlanCustRes responseData =
                  DeActivatePlanCustRes.fromJson(map);
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

  RenewPlans(Map<String, dynamic> finalAddonData) {
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

  addChargeDataInRenew(dynamic finalRenewData) {
    log("finalRenewDatafinalRenewDatafinalRenewDatafinalRenewData:::${jsonEncode(finalRenewData)}");
    // this.renewPlans(finalRenewData);
  }

  bool hasNullValue(List<dynamic> items, String fieldName) {
    return items.any((item) => item[fieldName] == null);
  }

  bool hasNonNullValue(
      List<dynamic> items, String fieldCheckbox, String fieldName) {
    return items.any((item) => item[fieldName] != null);
  }

  selectNewPlan(int? event, CustomerPlanServiceDetail? custServiceData) {
    planDiscount = 0;
    ifPlanSelectChanePlan = true;
    getPlanDetailByPlanId(event, custServiceData);
  }

  getPlanDetailByPlanId(int? customerPlanId,
      CustomerPlanServiceDetail? custServiceMapping) async {
    isLoading = true;
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

              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                // selPlanData = responseData.postPaidPlan as Map<String, dynamic>;

                if (responseData.postPaidPlan != null) {
                  log("postPaidPlanpostPaidPlan::::${jsonEncode(responseData.postPaidPlan)}");

                  selPlanData['quotatype'] =
                      responseData.postPaidPlan!.quotatype;
                  selPlanData['quotatime'] =
                      responseData.postPaidPlan!.quotatime;
                  selPlanData['quota'] = responseData.postPaidPlan!.quota;
                  selPlanData['quotaUnit'] = responseData.postPaidPlan!.quota;
                  selPlanData['quotaunittime'] =
                      responseData.postPaidPlan!.quotaunittime;
                  selPlanData['validity'] =
                      responseData.postPaidPlan!.validity!.toInt();
                  selPlanData['offerprice'] =
                      responseData.postPaidPlan!.offerprice;
                  selPlanData['taxamount'] =
                      responseData.postPaidPlan!.taxamount;
                  // selPlanData['activationDate'] = responseData.postPaidPlan!.ac.toString();
                  selPlanData['expiryDate'] =
                      responseData.postPaidPlan!.expiryDate;
                  selPlanData['id'] = responseData.postPaidPlan!.id;
                  selPlanData['name'] = responseData.postPaidPlan!.name;
                  selPlanData['unitsOfValidity'] =
                      responseData.postPaidPlan!.unitsOfValidity;

                  final DateTime date = DateTime.now();
                  selPlanData['activationDate'] =
                      DateFormat('dd-MM-yyyy').format(date);
                  selPlanData['expiryDate'] = date
                      .add(Duration(days: selPlanData['validity']))
                      .millisecondsSinceEpoch;
                  selPlanData['expiryDate'] = DateFormat('dd-MM-yyyy').format(
                      DateTime.fromMillisecondsSinceEpoch(
                          selPlanData['expiryDate']));
                  selPlanData['finalAmount'] =
                      selPlanData['offerprice'] + selPlanData['taxamount'];

                  if (discountList!.isNotEmpty) {
                    discountData = discountList!.firstWhere((element) =>
                        element.custId == custServiceMapping!.custId);
                    if (discountData != null &&
                        discountData['discountType'] == "Recurring" &&
                        DateTime.parse(discountData['discountExpiryDate'])
                            .isAfter(DateTime.now()) &&
                        (discountData['discount'] > 0 ||
                            discountData['discount'] < 0)) {
                      discountApplyDialog(customerPlanId, custServiceMapping);
                    } else if (discountData != null &&
                        discountData['discountType'] == "Recurring" &&
                        DateTime.parse(discountData['discountExpiryDate'])
                            .isAfter(DateTime.now()) &&
                        (discountData['discount'] < 0)) {
                      planDiscount = discountData.discount;
                      custServiceMapping!.newDiscount = 0;
                      updateDiscountFromService(customerPlanId);
                    } else {
                      planDiscount = 0;
                      custServiceMapping!.newDiscount = 0;
                      updateDiscountFromService(customerPlanId);
                    }
                  }
                  if (plansForChargeByCust != null &&
                      plansForChargeByCust.isNotEmpty) {
                    int index = plansForChargeByCust.indexWhere(
                      (item) =>
                          item['connection_no'] ==
                          custServiceMapping!.connectionNo,
                    );

                    if (index != -1) {
                      plansForChargeByCust.removeRange(
                          index, plansForChargeByCust.length);
                    }
                  }

                  plansForChargeByCust.add({
                    'connection_no': custServiceMapping!.connectionNo,
                    'custId': custServiceMapping.custId,
                    'planId': selPlanData['id'],
                    'planName': selPlanData['name'],
                    'unitsOfValidity': selPlanData['unitsOfValidity'],
                    'validity': selPlanData['validity'],
                    'discount': custServiceMapping.discount,
                    'discountExpiryDate': custServiceMapping.discountExpiryDate,
                    'discountType': custServiceMapping.discountType,
                  });

                  log("selPlanDataselPlanData::::${jsonEncode(plansForChargeByCust)}");
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

  updateDiscountFromService(int? customerPlanId) {
    if (ifPlanGroup! && selectedPlanType!.text!.equalsIgnoreCase("Addon")) {
      custServiceData
          .firstWhere((element) => element.newPlan == customerPlanId)
          .discount = planDiscount;
      finalOfferPrice = 0.0;

      for (var element in custServiceData) {
        if (element.newPlan) {
          getOfferPriceWithTax(customerPlanId, element.discount);
        }
      }
      offerPrice = 0;
    } else {
      offerPrice = 0;
      discount = planDiscount!;
      finalOfferPrice = 0;
      // offerPrice
      offerPrice = (offerPrice! + double.parse(selPlanData['offerprice']));
      getOfferPriceWithTax(customerPlanId, planDiscount);
    }

    if (customerDetail!.planMappingList![0].billTo!
            .equalsIgnoreCase("ORGANIZATION") ||
        customerDetail!.planMappingList![0].billTo!
            .equalsIgnoreCase("Organization")) {
      changePlanConfirmDialog(
          title: "Change Plan Confirmation",
          textMsg:
              "The customer is bill_to organization, do you want to continue?");
    }
  }

  getOfferPriceWithTax(int? planId, double? discount) {
    isLoading = true;
    update();
    CustomerProvider().getOfferPriceWithTax(
      planId: planId,
      discount: discount,
      planGroupId: "",
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              FinalAmountTaxRes responseData = FinalAmountTaxRes.fromJson(map);
              if (responseData.result!.finalAmount != null) {
                finalOfferPrice = (finalOfferPrice! +
                    double.parse(
                        responseData.result!.finalAmount!.toStringAsFixed(3)));
              } else {
                finalOfferPrice = 0.0;
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

  getBillingCycleAPI() {
    //isLoading = true;
    billingCycleList!.clear();
    billingCycleList = null;

    //update();
    CustomerProvider().getBillingCycleList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BillingCycleRes responseData = BillingCycleRes.fromJson(map);
              if (responseData.billingCycleList != null &&
                  responseData.billingCycleList!.isNotEmpty) {
                billingCycleList = responseData.billingCycleList;
                if (billingCycleList!.length > 0) {
                  selectedBillingCycle = billingCycleList!.first;
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
              ChangePlanTypeRes responseData = ChangePlanTypeRes.fromJson(map);
              if (responseData.changePlanTypeList != null &&
                  responseData.changePlanTypeList!.isNotEmpty) {
                changePlanTypeList =
                    responseData.changePlanTypeList?.reversed.toList();
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

  showDirectChargeDialog(
      dynamic addedChargeList, bool displayShowCharge, BuildContext context) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return ShowDirectChargeDialog(
            showChargeDetails: displayShowCharge,
            addedChargeList: addedChargeList,
            controller: ChangePlanController(),
          );
        });
  }

  getSystemConfigurationData() {
    PaymentProvider().getSystemConfiguration(
      type: Strings.allowChangePlanWhenEnoughBalance,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentConfigurationRes responseData =
                  PaymentConfigurationRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  if (responseData.data!.name!.isNotEmpty &&
                      responseData.data!.name!.equalsIgnoreCase(
                          Strings.allowChangePlanWhenEnoughBalance)) {
                    allowChangePlan = responseData.data!.value;
                    print("allowChangePlan: $allowChangePlan");
                  }
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        }
      },
      onError: (ResponseModel error) {},
    );
  }
}
