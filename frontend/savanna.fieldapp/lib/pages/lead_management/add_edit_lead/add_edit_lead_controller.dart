import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/individual_plan_data.dart';
import 'package:savbill/pages/customer/model/response/PincodeToAreaData.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/bill_to_res.dart';
import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/customer_basic_details_update_res.dart';
import 'package:savbill/pages/customer/model/response/customer_category_res.dart';
import 'package:savbill/pages/customer/model/response/customer_department_list.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/inside_outside_valley_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer/model/response/new_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_detail_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer/model/response/valley_type_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/lead_approval/assigne_lead/pa_assign_lead_controller.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/create_lead_res.dart';
import 'package:savbill/pages/lead_management/model/generate_lead_no_res.dart';
import 'package:savbill/pages/lead_management/model/lead_customer_gender_type_res.dart';
import 'package:savbill/pages/lead_management/model/lead_fesibility_res.dart';
import 'package:savbill/pages/lead_management/model/lead_master_details_res.dart';
import 'package:savbill/pages/lead_management/model/lead_origin_category_res.dart';
import 'package:savbill/pages/lead_management/model/lead_save_req.dart';
import 'package:savbill/pages/lead_management/model/lead_service_type_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_branch_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_customer_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_partner_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_service_area_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_source_staff_user_crms_res.dart';
import 'package:savbill/pages/lead_management/model/lead_to_caf_response.dart';
import 'package:savbill/pages/lead_management/model/lead_type_res.dart';
import 'package:savbill/pages/lead_management/model/pincode_to_area_by_id_res.dart';
import 'package:savbill/pages/lead_management/model/plan_detail_model.dart';
import 'package:savbill/pages/lead_management/model/require_service_type_res.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class AddEditLeadController extends GetxController {
  bool isLoading = false, checkBtnClickEvent = false;
  GetStorage getStorage = GetStorage();
  List<int> data = [
    1,
    2,
  ];
  int activeStep = 0;
  int dotCount = 14;
  TextEditingController leadNoController = TextEditingController();
  TextEditingController remarksController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();

  TextEditingController firstNameController = TextEditingController();
  TextEditingController lastNameController = TextEditingController();
  TextEditingController mobileController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController parentCustomerController = TextEditingController();
  TextEditingController panController = TextEditingController();
  TextEditingController vatController = TextEditingController();
  TextEditingController landmarkController = TextEditingController();
  TextEditingController streetNameController = TextEditingController();
  TextEditingController houseNumberController = TextEditingController();
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();

  TextEditingController planOfferPriceController = TextEditingController();
  TextEditingController planNewPriceController = TextEditingController();
  TextEditingController planValidityController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController newOfferPricePlanController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController previousAmountController = TextEditingController();
  TextEditingController packDurationController = TextEditingController();
  TextEditingController expiryController = TextEditingController();
  TextEditingController currentPayController = TextEditingController();
  TextEditingController customerFeedbackPayController = TextEditingController();
  TextEditingController contactPersonPayController = TextEditingController();
  TextEditingController cafNoController = TextEditingController();
  TextEditingController userNameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController leadCustomerSubSectorController =
      TextEditingController();
  final dropDownKey = GlobalKey<DropdownSearchState>();
  TextEditingController landlineNumberController = TextEditingController();
  TextEditingController secondaryEmailController = TextEditingController();
  TextEditingController secondaryPhoneController1 = TextEditingController();
  TextEditingController secondaryPhoneController2 = TextEditingController();
  TextEditingController secondaryPhoneController3 = TextEditingController();
  TextEditingController secondaryPhoneController4 = TextEditingController();
  TextEditingController secondaryPhoneController5 = TextEditingController();

  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  ParentCustomerDetail? selectedParentCustomer;
  String countryCode = Strings.defaultCountryCode;
  DateTime? selectedFollowUpDate;
  String? followUpScheduleDate;
  String? followUpScheduleTime;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiTimeFormat = DateFormat(Constant.TIME_FORMAT_24);
  DateFormat dateFormat =
      DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  UserDetail? userDetail;
  String from = Strings.add;
  LeadMasterListData? leadViewContentData;

  // LeadMaster? lead
  ServiceAreaDetailData? areaDetail;
  String? selectedChargeType,
      serviceAreaName,
      approveRejectStatus,
      assigneeName;
  num offerPrice = 0, discountOfferPrice = 0;
  bool businessPromotionFlag = false;
  List<IndividualPlanData>? individualPlanList = [];
  int? serviceAreaId, planServiceID, billableToCustomerId;
  int? partnerIdValue;

  bool readOnlyDiscountPrice = true,
      showDiscountPrice = true,
      showInvoiceTag = false,
      trialPlan = false,
      billToReadOnly = false;

  bool? isVisiblePassword = false;

  LocationDetail? selectedLocation;
  LocationLatLong? locationData;

  //Customer Category
  List<CustomerCategoryDetail>? custCategoryList = [];
  CustomerCategoryDetail? selectedCustCategory;

  // Lead Customer Type
  List<DropdownDetail>? leadCustomerTypeList = [];
  DropdownDetail? selectedCustomerLeadType;

  // Lead Customer Sector
  List<CustomerSectorData>? custSectorList = [];
  CustomerSectorData? selectedCustSector;

  // Lead Require Service Type
  List<String>? requireServiceTypeList = [];
  String? selectedRequireServiceType;

  // Lead Type
  List<String>? leadTypeList = [];
  String? selectedLeadType;

  // Lead Category
  List<DropdownDetail>? leadCategoryList = [];
  DropdownDetail? selectedLeadCategory;

  // Lead Origin Type
  List<String>? leadOriginTypeList = [];
  String? selectedLeadOriginType;

  // Lead Origin Type
  List<LeadSourceList>? leadSourceList = [];
  LeadSourceList? selectedLeadSource;

  // Lead Feasibility Type
  List<String>? leadFeasibilityList = [];
  String? selectedLeadFeasibility;

  // Department
  List<DepartmentListData>? allDepartmentDataList = [];
  DepartmentListData? selectAllDepartmentData;

  //Service Area
  // List<ServicesAreaDetail>? servicesAreaList = [];
  // ServicesAreaDetail? selPresentServiceArea;

  //New Service Area
  List<NewServiceDataList>? newServicesAreaList = [];
  NewServiceDataList? selectNewServiceArea;

  // Branch By Service Id
  List<BranchesByServiceAreaDataList>? branchesByServiceAreaList = [];
  BranchesByServiceAreaDataList? selectBranchesByServiceAreaData;

  // Lead Customer Gender Type
  List<String>? leadCustomerGenderList = [];
  String? selectedLeadCustomerGender;

  // PinCode
  List<PincodeDetail>? pincodeList = [];
  PincodeDetail? selPresentPincode;

  List<PincodeAreaDetail>? areaList = [];
  PincodeAreaDetail? selPresentArea;

  List<CityDetail>? cityList = [];
  CityDetail? selPresentCity;

  List<StateDetail>? stateList = [];
  StateDetail? selPresentState;

  List<CountryDetail>? countryList = [];
  CountryDetail? selPresentCountry;

  List<ValleyType>? valleyTypeList = [];
  ValleyType? selectedValleyType;

  List<InsideOutsideValleyData>? insideValleyList = [];
  InsideOutsideValleyData? selectedInsideValley;

  List<InsideOutsideValleyData>? outsideValleyList = [];
  InsideOutsideValleyData? selectedOutsideValley;

  List<DropdownDetail>? planCategoryList = [];
  DropdownDetail? selPlanCategory;

  List<ServicesByServiceAreaDataList>? servicesByServiceAreaDataList = [];
  ServicesByServiceAreaDataList? selectServicesByServiceAreaData;

  List<DropdownDetail>? invoiceToOrgList = [];
  DropdownDetail? selectedInvoiceToOrg;

  List<PlanGroupDetail>? planGroupList = [];
  PlanGroupDetail? selPlanGroup;

  List<ServiceAreaPlanPostpaidplanList>? serviceAreaAllPlanList = [];
  List<ServiceAreaPlanPostpaidplanList>? selectedServiceAreaPlanList = [];
  ServiceAreaPlanPostpaidplanList? serviceAreaPlanPostpaidData;

  List<PostpaidPlanDetail>? allPlanList = [];
  List<PostpaidPlanDetail>? planList = [];
  PostpaidPlanDetail? selPlan;

  List<PlanServiceDetail>? planServiceList = [];
  List<PlanServiceDetail>? selectedPlanServiceList = [];
  PlanServiceDetail? selPlanService;

  List<PlanGroupMappingDetail>? planGroupMappingList = [];
  PlanGroupMappingDetail? selectPlanGroupMappingData;

  List<BillToDetail>? billToList = [];
  BillToDetail? selectedBillTo; // 857 customer, 856 ORGANIZATION

  List<DropdownDetail>? discountTypeList = [];
  DropdownDetail? selDiscountType;

  List<DropdownDetail>? calenderTypeList = [];
  DropdownDetail? selectedCalenderType;
  List<DropdownDetail>? monthList = [];
  DropdownDetail? selectMonth;

  // Lead Service Type
  List<String>? leadServiceTypeList = [];
  String? selectedLeadServiceType;

  String? selectDurationUnit;
  //String? selectCustomerTitleCAF;

  String? type = Strings.prepaid;

  DateTime? selectedExpiryDate;

  CustomersBasicDetail? customerDetail;

  // CustomerBasicLeadCustomers? customerDetail;

  bool? ifReadonlyExistingInput = false;

  PlanMappingList? existingCustPlan;

  // PlanMappingLeadList? existingCustPlan;
  int? branchId;

  bool? myViewFlag = false;
  List<LeadSubSourceDtoList>? leadSubSourceArr = [];
  LeadSubSourceDtoList? selectedLeadSubSourceArr;
  String? leadSourceTitle;
  List<PartnerList>? leadSourcePartnerList = [];
  PartnerList? selectedLeadSourcePartner;
  List<StaffUserList>? leadSourceStaffUserList = [];
  StaffUserList? selectedLeadSourceStaffUser;
  List<ServiceAreaList>? leadSourceServiceAreaList = [];
  ServiceAreaList? selectedLeadSourceServiceArea;
  List<BranchList>? leadSourceBranchList = [];
  BranchList? selectedLeadSourceBranch;
  List<CustomersList>? leadSourceCustomerList = [];
  CustomersList? selectedLeadSourceCustomer;

  PincodeToAreaByIdData? pinCodeToAreaByIdData;

  List<String>? agentArr = [];
  String? selectAgentArr;

  LeadMaster? leadMaster;

  List<int>? billDayList = [];
  int? selectedBillDay;

  List<int> blockNoOptions = [];
  int? selectedBlockNo;
  int? pinCode;

  LeadToCAFCustomer? leadToCAFCustomer;

  @override
  void onInit() {
    super.onInit();

    getArgumentData();

    billDayList!.clear();
    for (int i = 1; i <= 31; i++) {
      billDayList!.add(i);
    }

    leadCustomerTypeList!.clear();
    leadCustomerTypeList!.add(DropdownDetail(
        id: Strings.prepaid,
        text: Strings.prepaid,
        type: Strings.customer_type));
    leadCustomerTypeList!.add(DropdownDetail(
        id: Strings.postpaid,
        text: Strings.postpaid,
        type: Strings.customer_type));

    selectedCustomerLeadType = leadCustomerTypeList![0];

    leadCategoryList!.clear();
    leadCategoryList!.add(DropdownDetail(
        id: Strings.new_lead,
        text: Strings.new_lead,
        type: Strings.lead_category));
    leadCategoryList!.add(DropdownDetail(
        id: Strings.existing_customer,
        text: Strings.existing_customer,
        type: Strings.lead_category));

    selectedLeadCategory = leadCategoryList![0];

    planCategoryList!.clear();
    planCategoryList!.add(DropdownDetail(
        id: Strings.individual,
        text: Strings.individual,
        type: Strings.plan_category));
    planCategoryList!.add(DropdownDetail(
        id: Strings.plan_group,
        text: Strings.plan_group,
        type: Strings.plan_category));

    selPlanCategory = planCategoryList![0];

    invoiceToOrgList!.clear();
    invoiceToOrgList!.add(DropdownDetail(
        id: Strings.yes, text: Strings.yes, type: Strings.invoice_to_org));
    invoiceToOrgList!.add(DropdownDetail(
        id: Strings.no, text: Strings.no, type: Strings.invoice_to_org));
    selectedInvoiceToOrg = invoiceToOrgList![1];

    selectDurationUnit = Utils.getDurationUnits()![1];

    discountTypeList!.clear();
    discountTypeList!.add(DropdownDetail(
        id: Strings.onetime,
        text: Strings.onetime,
        type: Strings.discount_type));
    discountTypeList!.add(DropdownDetail(
        id: Strings.recurring,
        text: Strings.recurring,
        type: Strings.discount_type));

    selDiscountType = discountTypeList![0];

    calenderTypeList!.clear();
    calenderTypeList!.add(DropdownDetail(
        id: Strings.english,
        text: Strings.english,
        type: Strings.calendar_type));
    calenderTypeList!.add(DropdownDetail(
        id: Strings.nepali, text: Strings.nepali, type: Strings.calendar_type));

    selectedCalenderType = calenderTypeList![0];

    monthList!.clear();
    monthList!.add(DropdownDetail(
        id: Strings.January.toUpperCase(),
        text: Strings.January,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.February.toUpperCase(),
        text: Strings.February,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.March.toUpperCase(),
        text: Strings.March,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.April.toUpperCase(),
        text: Strings.April,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.May.toUpperCase(), text: Strings.May, type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.June.toUpperCase(),
        text: Strings.June,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.July.toUpperCase(),
        text: Strings.July,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.August.toUpperCase(),
        text: Strings.August,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.September.toUpperCase(),
        text: Strings.September,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.October.toUpperCase().toUpperCase(),
        text: Strings.October,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.November.toUpperCase().toUpperCase(),
        text: Strings.November,
        type: Strings.month));
    monthList!.add(DropdownDetail(
        id: Strings.December.toUpperCase(),
        text: Strings.December,
        type: Strings.month));
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.LEAD_DETAIL] != null) {
        leadViewContentData = arguments[Constant.LEAD_DETAIL];
      }
      if (arguments[Constant.LEAD_STATUS] != null) {
        approveRejectStatus = arguments[Constant.LEAD_STATUS];
      }
    }
    update();
    // setTicketDetail();
    initPlatformState();
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
    if (from.equalsIgnoreCase(Strings.add)) {
      // getGenerateLeadNoData();
      getCustomerCategory();
    } else if (from.equalsIgnoreCase(Strings.edit)) {
      getLeadDetailData(leadViewContentData!.id!);
    } else if (from.equalsIgnoreCase(Strings.lead_caf)) {
      getLeadDetailData(leadViewContentData!.id!);
    }
  }

  // getGenerateLeadNoData() {
  //   isLoading = true;
  //   update();
  //   LeadSystemProvider().generateLeadNo(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             GenerateLeadNoRes responseData = GenerateLeadNoRes.fromJson(map);
  //             if (responseData.responseCode != null &&
  //                     responseData.responseCode == 200 ||
  //                 responseData.status != null && responseData.status == 200) {
  //               leadNoController.text = responseData.leadNo!;
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
  //       isLoading = false;
  //       update();
  //       getCustomerCategory();
  //     },
  //     onError: (ResponseModel error) {
  //       getCustomerCategory();
  //       handleApiError(error);
  //     },
  //   );
  // }

  getCustomerCategory() {
    selectedCustCategory = null;
    custCategoryList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerCategory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerCategoryRes responseData =
                  CustomerCategoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  custCategoryList?.addAll(responseData.dataList!);
                  selectedCustCategory = custCategoryList![0];
                  if (leadMaster != null &&
                      leadMaster!.dunningCategory != null) {
                    for (CustomerCategoryDetail element in custCategoryList!) {
                      if (element.value!
                          .equalsIgnoreCase(leadMaster!.dunningCategory!)) {
                        selectedCustCategory = element;
                      }
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
        getCustomerSector();
      },
      onError: (ResponseModel error) {
        getCustomerSector();
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

                  if (leadMaster != null &&
                      leadMaster!.leadCustomerSector != null) {
                    for (CustomerSectorData element in custSectorList!) {
                      if (element.value!
                          .equalsIgnoreCase(leadMaster!.leadCustomerSector!)) {
                        selectedCustSector = element;
                      }
                    }
                  } else {
                    selectedCustSector = custSectorList![3];
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
        getRequireServiceType();
      },
      onError: (ResponseModel error) {
        getRequireServiceType();
        _handleApiError(error);
      },
    );
  }

  getRequireServiceType() {
    selectedRequireServiceType = null;
    requireServiceTypeList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getRequireServiceType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RequireServiceTypeRes responseData =
                  RequireServiceTypeRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.requireServiceTypeList != null &&
                    responseData.requireServiceTypeList!.isNotEmpty) {
                  requireServiceTypeList =
                      responseData.requireServiceTypeList?[0].split(",");
                  if (leadMaster != null &&
                      leadMaster!.requireServiceType != null) {
                    for (String element in requireServiceTypeList!) {
                      if (element
                          .equalsIgnoreCase(leadMaster!.requireServiceType!)) {
                        selectedRequireServiceType = element;
                      }
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
        getLeadTypeApi();
      },
      onError: (ResponseModel error) {
        getLeadTypeApi();
        _handleApiError(error);
      },
    );
  }

  getLeadTypeApi() {
    selectedLeadType = null;
    leadTypeList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadTypeCall(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadTypeRes responseData = LeadTypeRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.leadTypeList != null &&
                    responseData.leadTypeList!.isNotEmpty) {
                  leadTypeList = responseData.leadTypeList?[0].split(",");

                  if (leadMaster != null && leadMaster!.leadType != null) {
                    for (String element in leadTypeList!) {
                      if (element.equalsIgnoreCase(leadMaster!.leadType!)) {
                        selectedLeadType = element;
                      }
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
        getLeadOriginTypes();
      },
      onError: (ResponseModel error) {
        getLeadOriginTypes();
        _handleApiError(error);
      },
    );
  }

  getLeadOriginTypes() {
    selectedLeadOriginType = null;
    leadOriginTypeList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().leadOriginTypes(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadOriginTypeRes responseData = LeadOriginTypeRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.leadOriginTypeList != null &&
                    responseData.leadOriginTypeList!.isNotEmpty) {
                  leadOriginTypeList =
                      responseData.leadOriginTypeList?[0].split(",");

                  if (leadMaster != null &&
                      leadMaster!.leadOriginType != null) {
                    for (String element in leadOriginTypeList!) {
                      if (element
                          .equalsIgnoreCase(leadMaster!.leadOriginType!)) {
                        selectedLeadOriginType = element;
                      }
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
        getLeadSourceList();
      },
      onError: (ResponseModel error) {
        getLeadSourceList();
        _handleApiError(error);
      },
    );
  }

  getLeadSourceList() {
    selectedLeadSource = null;
    leadSourceList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().leadSourceType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourceRes responseData = LeadSourceRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.leadSourceList != null &&
                    responseData.leadSourceList!.isNotEmpty) {
                  leadSourceList = responseData.leadSourceList!
                      .where((element) =>
                          element.status!.equalsIgnoreCase("Active"))
                      .toList();

                  if (leadMaster != null) {
                    if (leadMaster!.leadSourceName != null) {
                      for (LeadSourceList element in leadSourceList!) {
                        if (element.leadSourceName!
                            .equalsIgnoreCase(leadMaster!.leadSourceName!)) {
                          selectedLeadSource = element;
                          break;
                        }
                      }
                    }

                    if (leadMaster!.leadSourceId != null) {
                      selectLeadSource(leadMaster!.leadSourceId);
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
        getLeadFeasibilityList();
      },
      onError: (ResponseModel error) {
        getLeadFeasibilityList();
        _handleApiError(error);
      },
    );
  }

  getLeadFeasibilityList() {
    selectedLeadFeasibility = null;
    leadFeasibilityList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().leadFeasibility(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadFesibilityRes responseData = LeadFesibilityRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.feasibility != null &&
                    responseData.feasibility!.isNotEmpty) {
                  leadFeasibilityList = responseData.feasibility?[0].split(",");
                  selectedLeadFeasibility = leadFeasibilityList![0];
                  if (leadMaster != null && leadMaster!.feasibility != null) {
                    for (String element in leadFeasibilityList!) {
                      if (element.equalsIgnoreCase(leadMaster!.feasibility!)) {
                        selectedLeadFeasibility = element;
                      }
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
        getDepartmentListAPI();
      },
      onError: (ResponseModel error) {
        getDepartmentListAPI();
        _handleApiError(error);
      },
    );
  }

  getDepartmentListAPI() {
    isLoading = true;
    allDepartmentDataList!.clear();
    selectAllDepartmentData = null;
    update();
    CustomerProvider().getCustomerDepartmentList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustometDeparmentListRes responseData =
                  CustometDeparmentListRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.departmentList != null &&
                    responseData.departmentList!.isNotEmpty) {
                  allDepartmentDataList!.addAll(responseData.departmentList!);

                  if (leadMaster != null &&
                      leadMaster!.leadDepartment != null) {
                    for (DepartmentListData element in allDepartmentDataList!) {
                      if (element.name == leadMaster!.leadDepartment) {
                        selectAllDepartmentData = element;
                      }
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
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        if (getStorage.hasData(Constant.LEAD_GENDER_TYPE_DATA) == false) {
          getLeadCustomerGenderTypes();
        } else {
          List<String> genderList = getGenderTypeList();
          for (var element in genderList) {
            leadCustomerGenderList!.add(element);
          }
        }

        if (getStorage.hasData(Constant.LEAD_SERVICE_AREA_DATA) == false) {
          getNewServiceArea();
        } else if (from.equalsIgnoreCase(Strings.edit)) {
          getNewServiceArea();
        } else {
          List<NewServiceDataList> storedList = getNewServicesAreaList();
          for (var service in storedList) {
            newServicesAreaList!.add(service);
          }
        }
      },
      onError: (ResponseModel error) {
        // getServiceArea();
        if (getStorage.hasData(Constant.LEAD_GENDER_TYPE_DATA) == false) {
          getLeadCustomerGenderTypes();
        } else {
          List<String> genderList = getGenderTypeList();
          for (var element in genderList) {
            leadCustomerGenderList!.add(element);
          }
        }
        if (getStorage.hasData(Constant.LEAD_SERVICE_AREA_DATA) == false) {
          getNewServiceArea();
        } else {
          List<NewServiceDataList> storedList = getNewServicesAreaList();
          for (var service in storedList) {
            newServicesAreaList!.add(service);
          }
        }
        _handleApiError(error);
      },
    );
  }

  // getServiceArea() {
  //   isLoading = true;
  //   selPresentServiceArea = null;
  //   servicesAreaList!.clear();
  //   update();
  //   CustomerProvider().getServiceAreaData(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 servicesAreaList!.addAll(responseData.dataList!);
  //
  //                 if (leadMaster != null && leadMaster!.serviceareaid != null) {
  //                   serviceAreaId = leadMaster!.serviceareaid;
  //                   for (ServicesAreaDetail element in servicesAreaList!) {
  //                     if (element.id == leadMaster!.serviceareaid) {
  //                       selPresentServiceArea = element;
  //                     }
  //                   }
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
  //       isLoading = false;
  //       update();
  //       getLeadCustomerGenderTypes();
  //     },
  //     onError: (ResponseModel error) {
  //       getLeadCustomerGenderTypes();
  //       _handleApiError(error);
  //     },
  //   );
  // }

  getNewServiceArea() {
    isLoading = true;
    selectNewServiceArea = null;
    newServicesAreaList!.clear();
    update();
    CustomerProvider().getNewServiceAreaData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              NewServicesAreaRes responseData =
                  NewServicesAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  newServicesAreaList!.addAll(responseData.dataList!);
                  if (leadMaster != null && leadMaster!.serviceareaid != null) {
                    serviceAreaId = leadMaster!.serviceareaid;
                    for (NewServiceDataList element in newServicesAreaList!) {
                      if (element.id == leadMaster!.serviceareaid) {
                        selectNewServiceArea = element;
                      }
                    }
                  }

                  getStorage.write(Constant.LEAD_SERVICE_AREA_DATA,
                      jsonEncode(newServicesAreaList));
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

  List<NewServiceDataList> getNewServicesAreaList() {
    String? jsonString =
        getStorage.read(Constant.LEAD_SERVICE_AREA_DATA); // Read stored JSON

    if (jsonString == null || jsonString.isEmpty) {
      return []; // Return an empty list if data is null or empty
    }
    try {
      List<dynamic> jsonData = json.decode(jsonString); // Decode JSON string
      return jsonData
          .map((e) => NewServiceDataList.fromJson(e))
          .toList(); // Convert to List<ServicesAreaDetail>
    } catch (e) {
      print("Error decoding services data: $e");
      return []; // Return empty list if an error occurs
    }
  }

  getAllBranchesByServiceAreaData(
    List<int>? serviceAreaID,
  ) {
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
                  branchesByServiceAreaList!.addAll(responseData.dataList!);
                  if (branchesByServiceAreaList!.isNotEmpty) {
                    selectBranchesByServiceAreaData =
                        branchesByServiceAreaList![0];
                  }
                  if (leadMaster != null && leadMaster!.branchName != null) {
                    for (BranchesByServiceAreaDataList element
                        in branchesByServiceAreaList!) {
                      if (element.name!
                          .equalsIgnoreCase(leadMaster!.branchName!)) {
                        selectBranchesByServiceAreaData = element;
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
        // getAllServicesByServiceAreaIdData(serviceAreaID);
      },
      onError: (ResponseModel error) {
        // getAllServicesByServiceAreaIdData(serviceAreaID);
        _handleApiError(error);
      },
    );
  }

  getLeadCustomerGenderTypes() {
    selectedLeadCustomerGender = null;
    leadCustomerGenderList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().leadCustomerGenderType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadCustomerGenderRes responseData =
                  LeadCustomerGenderRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.leadCustomerGender!.isNotEmpty) {
                  leadCustomerGenderList =
                      responseData.leadCustomerGender?[0].split(",");
                  selectedLeadCustomerGender = leadCustomerGenderList![0];
                  if (leadMaster != null && leadMaster!.gender != null) {
                    for (String element in leadCustomerGenderList!) {
                      if (element.equalsIgnoreCase(leadMaster!.gender!) ||
                          element == leadMaster!.gender) {
                        selectedLeadCustomerGender = element;
                      }
                    }
                  }
                  getStorage.write(Constant.LEAD_GENDER_TYPE_DATA,
                      jsonEncode(leadCustomerGenderList));
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
        // getValleyTypeDetail();
      },
      onError: (ResponseModel error) {
        // getValleyTypeDetail();
        _handleApiError(error);
      },
    );
  }

  List<String> getGenderTypeList() {
    String? jsonString =
        getStorage.read(Constant.LEAD_GENDER_TYPE_DATA); // Read stored JSON
    if (jsonString == null || jsonString.isEmpty) {
      return []; // Return an empty list if data is null or empty
    }
    try {
      List<dynamic> jsonData = json.decode(jsonString); // Decode JSON string
      return jsonData
          .map((e) => e.toString())
          .toList(); // Convert to List<String>
    } catch (e) {
      print("Error decoding services data: $e");
      return []; // Return empty list if an error occurs
    }
  }

  getServiceAreaDetail(int? serviceAraId) {
    isLoading = true;
    selectedBlockNo = null;
    blockNoOptions.clear();
    update();
    CustomerProvider().getServiceAreaDetail(
      id: serviceAraId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServiceAreaDetailRes responseData =
                  ServiceAreaDetailRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 0)) {
                areaDetail = responseData.data;
                if (areaDetail!.blockNo != null &&
                    int.tryParse(areaDetail!.blockNo!) != null) {
                  int maxBlockNo = int.parse(areaDetail!.blockNo!);
                  blockNoOptions = List<int>.generate(maxBlockNo, (i) => i + 1);
                  log("blockNoOptions ::::: $blockNoOptions");
                } else {
                  blockNoOptions = []; // Clear options if invalid
                }
                if (areaDetail!.pincodes!.isNotEmpty) {
                  pinCode = areaDetail!.pincodes![0];
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
        getPincodeData();
        _handleApiError(error);
      },
    );
  }

  getPincodeData() {
    isLoading = true;
    selPresentPincode = null;
    selPresentArea = null;
    selPresentCity = null;
    selPresentState = null;
    selPresentCountry = null;
    pincodeList!.clear();
    areaList!.clear();
    cityList!.clear();
    stateList!.clear();
    countryList!.clear();
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
                  for (var element in responseData.dataList!) {
                    if (element.status != null &&
                        element.status!.equalsIgnoreCase("Active")) {
                      if (areaDetail!.pincodes != null &&
                          areaDetail!.pincodes!.isNotEmpty) {
                        for (int value in areaDetail!.pincodes!) {
                          if (element.pincodeid == value) {
                            pincodeList!.add(element);
                            break;
                          }
                        }
                      }
                      // if (element.pincodeid == areaDetail!.pincodeId) {
                      //   selPresentPincode = element;
                      //   if (selPresentPincode!.areaList != null &&
                      //       selPresentPincode!.areaList!.isNotEmpty) {
                      //     areaList!.addAll(selPresentPincode!.areaList!);
                      //     selPresentPincode!.areaList!.forEach((areaItem) {
                      //       if (areaItem.id == areaDetail!.id) {
                      //         selPresentArea = areaItem;
                      //       }
                      //     });
                      //   }
                      // }
                    }
                  }
                  if (customerDetail != null &&
                      customerDetail!.addressList!.isNotEmpty) {
                    getPinCodeToAreaList(
                        customerDetail!.addressList![0].pincodeId!);
                  }

                  if (leadMaster != null &&
                      leadMaster!.addressList!.isNotEmpty) {
                    if (leadMaster!.addressList![0].pincodeId != null) {
                      getPinCodeToAreaList(
                          leadMaster!.addressList![0].pincodeId!);
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

        /*  if (cityList == null || cityList!.isEmpty) {
          getAllCity();
        }*/
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

                  if (leadMaster != null) {
                    if (leadMaster!.planMappingList!.isNotEmpty) {
                      for (var planMappingElement
                          in leadMaster!.planMappingList!) {
                        for (var element in servicesByServiceAreaDataList!) {
                          if (element.name!
                              .equalsIgnoreCase(planMappingElement.service)) {
                            selectServicesByServiceAreaData = element;
                            break;
                          }
                        }
                        individualPlanList!.add(IndividualPlanData(
                            type: (planMappingElement.billTo == "CUSTOMER") ==
                                    false
                                ? 1
                                : 2,
                            planService: selectServicesByServiceAreaData,
                            // planDetail: addLeadController.selPlan,
                            planDetail: serviceAreaPlanPostpaidData,
                            discount:
                                planMappingElement.discount.toString() ?? "0",
                            discountType: planMappingElement.discountType,
                            newOfferPrice:
                                planMappingElement.newAmount.toString(),
                            // planOfferPrice: addLeadController.selPlan!.offerprice!
                            planOfferPrice: serviceAreaPlanPostpaidData!
                                .offerprice!
                                .toString(),
                            trialPlan: planMappingElement.istrialplan));
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
        getAllBranchesByServiceAreaData(serviceAreaID);
      },
      onError: (ResponseModel error) {
        getAllBranchesByServiceAreaData(serviceAreaID);
        _handleApiError(error);
      },
    );
  }

  getValleyTypeDetail() {
    selectedValleyType = null;
    valleyTypeList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getValleyType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ValleyTypeRes responseData = ValleyTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  valleyTypeList?.addAll(responseData.dataList!);

                  if (leadMaster != null && leadMaster!.valleyType != null) {
                    for (ValleyType element in valleyTypeList!) {
                      if (element.text!
                          .equalsIgnoreCase(leadMaster!.valleyType!)) {
                        selectedValleyType = element;
                        break;
                      }
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
        getInsideValleyDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getInsideValleyDetail();
      },
    );
  }

  getInsideValleyDetail() {
    selectedInsideValley = null;
    insideValleyList!.clear();
    isLoading = true;
    update();
    CustomerProvider().insideValleyData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InsideOutsideValleyRes responseData =
                  InsideOutsideValleyRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  insideValleyList?.addAll(responseData.dataList!);

                  if (leadMaster != null && leadMaster!.insideValley != null) {
                    for (InsideOutsideValleyData element in insideValleyList!) {
                      if (element.text!
                          .equalsIgnoreCase(leadMaster!.insideValley!)) {
                        selectedInsideValley = element;
                        break;
                      }
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
        getOutsideValleyDetail();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getOutsideValleyDetail();
      },
    );
  }

  getOutsideValleyDetail() {
    selectedOutsideValley = null;
    outsideValleyList!.clear();
    isLoading = true;
    update();
    CustomerProvider().outsideValleyData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InsideOutsideValleyRes responseData =
                  InsideOutsideValleyRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  outsideValleyList?.addAll(responseData.dataList!);

                  if (leadMaster != null && leadMaster!.outsideValley != null) {
                    for (InsideOutsideValleyData element
                        in outsideValleyList!) {
                      if (element.text!
                          .equalsIgnoreCase(leadMaster!.outsideValley!)) {
                        selectedOutsideValley = element;
                        break;
                      }
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
        // getAllPop();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getAllPop();
      },
    );
  }

  Future<void> getPlanDetailFromPlanId(int customerPlanId) async {
    isLoading = true;
    update();
    ChargeManagementProvider().getCustomerPlanDetail(
      planId: customerPlanId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanDetailsModel responseData = PlanDetailsModel.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.postPaidPlan != null) {
                  serviceAreaPlanPostpaidData = responseData.postPaidPlan;
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

  getPinCodeToAreaData(int id, String type) {
    //, String from
    // if (type.equalsIgnoreCase("Present")) {
    //   // selPresentArea = null;
    //   // selPresentCity = null;
    //   // selPresentState = null;
    //   // selPresentCountry = null;
    //   // areaList!.clear();
    //   // cityList!.clear();
    //   // stateList!.clear();
    //   // countryList!.clear();
    // }

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
                  if (type.equalsIgnoreCase("Present")) {
                    areaList!.addAll(responseData.areaList!);

                    if (customerDetail != null &&
                        customerDetail!.addressList!.isNotEmpty) {
                      for (PincodeAreaDetail element in areaList!) {
                        if (element.id ==
                            customerDetail!.addressList![0].areaId) {
                          selPresentArea = element;
                        }
                      }
                    }

                    if (leadMaster != null &&
                        leadMaster!.addressList!.isNotEmpty) {
                      if (leadMaster!.addressList![0].areaId != null) {
                        for (PincodeAreaDetail element in areaList!) {
                          if (element.id ==
                              leadMaster!.addressList![0].areaId) {
                            selPresentArea = element;
                          }
                        }
                      }
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

  getAreaDetail(int? areaId, String type) {
    if (type.equalsIgnoreCase("Present")) {
      selPresentCity = null;
      selPresentState = null;
      selPresentCountry = null;
      cityList!.clear();
      stateList!.clear();
      countryList!.clear();
    }

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
                if (responseData.data != null) {
                  if (type.equalsIgnoreCase("Present")) {
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

  calculatePlanGroupDiscountPrice(String fromId, String value) {
    if (fromId.equalsIgnoreCase(Strings.discount)) {
      num planGrpOfferPrice = 0;
      if (offerPrice > 0 && value.isNotEmpty) {
        double perValue = double.parse(value);
        planGrpOfferPrice = (offerPrice * perValue) / 100;
      }
      discountOfferPrice = offerPrice - planGrpOfferPrice;
      planNewPriceController.text = discountOfferPrice.toStringAsFixed(2);
    }

    if (fromId.equalsIgnoreCase(Strings.new_price_with_discount)) {
      num planDiscount = 0, discountValue = 0;
      if (offerPrice > 0 && value.isNotEmpty) {
        discountValue = double.parse(value);
        planDiscount = 100 - ((discountValue * 100) / offerPrice);
      }
      discountOfferPrice = discountValue;
      discountController.text = planDiscount.toStringAsFixed(2);
    }
    update();
  }

  calculatePlanDiscountPrice(String fromId, String value) {
    if (fromId.equalsIgnoreCase(Strings.add) ||
        fromId.equalsIgnoreCase(Strings.delete)) {
      num planOfferPrice = 0, newDiscountPrice = 0;
      for (IndividualPlanData element in individualPlanList!) {
        if (element.planOfferPrice != null &&
            element.planOfferPrice!.isNotEmpty) {
          num offerPrice = double.parse(element.planOfferPrice!);
          num planDiscountPrice = 0;
          if (element.discount != null && element.discount!.isNotEmpty) {
            num discount = double.parse(element.discount!);
            planDiscountPrice = offerPrice * discount / 100;
          }
          planOfferPrice = planOfferPrice + offerPrice;
          newDiscountPrice = newDiscountPrice + planDiscountPrice;
        }
      }
      offerPrice = planOfferPrice;
      discountOfferPrice = offerPrice - newDiscountPrice;
      planNewPriceController.text = discountOfferPrice.toStringAsFixed(2);
      planOfferPriceController.text = offerPrice.toStringAsFixed(2);
    }

    if (fromId.equalsIgnoreCase(Strings.new_price_with_discount)) {
      num planDiscountPer = 0, discountValue = 0;
      if (offerPrice > 0 && value.isNotEmpty) {
        discountValue = double.parse(value);
        planDiscountPer = 100 - ((discountValue * 100) / offerPrice);
      }
      for (IndividualPlanData element in individualPlanList!) {
        element.discount = planDiscountPer.toStringAsFixed(2);
      }
    }
    update();
  }

  getPlanGroupDetail() {
    isLoading = true;
    planGroupList!.clear();
    update();
    CustomerProvider().getPlanGroup(
      planMode: " ",
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanGroupRes responseData = PlanGroupRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.planGroupList != null &&
                    responseData.planGroupList!.isNotEmpty) {
                  planGroupList!.addAll(responseData.planGroupList!);
                }
              } else if (responseData.status != null &&
                  responseData.status == 404) {
                if (responseData.msg != null && responseData.msg!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.msg,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        }
        update();
        if (allPlanList == null || allPlanList!.isEmpty) {
          getPlanDetail();
        }
        // if (planServiceList == null || planServiceList!.isEmpty) {
        //   getPlanServicesDetail();
        // }
      },
      onError: (ResponseModel error) {
        if (allPlanList == null || allPlanList!.isEmpty) {
          getPlanDetail();
        }
        // if (planServiceList == null || planServiceList!.isEmpty) {
        //   getPlanServicesDetail();
        // }
        _handleApiErrorPlanGroupCustom(error);
      },
    );
  }

  getPlanServicesDetail() {
    isLoading = true;
    planServiceList!.clear();
    // selectedPlanServiceList!.clear();
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
                  planServiceList = responseData.serviceList
                      ?.where((element) =>
                          element.id == selectServicesByServiceAreaData!.id)
                      .toList();

                  if (planServiceList!.isNotEmpty) {
                    planServiceID = planServiceList![0].id;
                    selectedServiceAreaPlanList = serviceAreaAllPlanList!
                        .where((element) =>
                            element.serviceId == planServiceID &&
                            (element.planGroup!
                                    .equalsIgnoreCase("Registration") ||
                                element.planGroup!.equalsIgnoreCase(
                                    "Registration and Renewal")))
                        .toList();
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
        // getServicePlanModeServiceAreaAPI();
        getLeadServiceTypeList();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getServicePlanModeServiceAreaAPI();
        getLeadServiceTypeList();
      },
    );
  }

  getPlanDetail() {
    isLoading = true;
    allPlanList!.clear();
    update();
    CustomerProvider().getPostpaidPlan(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PostpaidPlanListRes responseData =
                  PostpaidPlanListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.postpaidplanList != null &&
                    responseData.postpaidplanList!.isNotEmpty) {
                  allPlanList!.addAll(responseData.postpaidplanList!);
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

  getServicePlanModeServiceAreaAPI() {
    isLoading = true;
    serviceAreaAllPlanList!.clear();
    selectedServiceAreaPlanList!.clear();
    serviceAreaPlanPostpaidData = null;
    update();

    CustomerProvider().getServicePlanModeServiceAreaList(
      serviceAreaId: selectNewServiceArea!.id,
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
                  serviceAreaAllPlanList = responseData.postpaidplanList!
                      .where((element) =>
                          element.plantype!.equalsIgnoreCase(type!))
                      .toList();
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
        // getLeadServiceTypeList();
        getPlanServicesDetail();
      },
      onError: (ResponseModel error) {
        // getLeadServiceTypeList();
        getPlanServicesDetail();
        _handleApiError(error);
      },
    );
  }

  getPlanGroupToPlanListData() {
    isLoading = true;
    planGroupMappingList!.clear();
    update();
    ChargeManagementProvider().getCustomerPlanGroupToPlan(
      cusPlanGroupId: selPlanGroup!.planGroupId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanGroupMappingRes responseData =
                  PlanGroupMappingRes.fromJson(map);
              num planGrpPrice = 0;
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.planGroupMappingList != null &&
                    responseData.planGroupMappingList!.isNotEmpty) {
                  planGroupMappingList!
                      .addAll(responseData.planGroupMappingList!);

                  for (PlanGroupMappingDetail element
                      in responseData.planGroupMappingList!) {
                    if (element.plan != null) {
                      if (element.plan!.newOfferPrice != null &&
                          element.plan!.newOfferPrice! > 0) {
                        planGrpPrice =
                            planGrpPrice + element.plan!.newOfferPrice!;
                      } else {
                        if (element.plan!.offerprice != null) {
                          planGrpPrice =
                              planGrpPrice + element.plan!.offerprice!;
                        }
                      }
                    }
                  }
                }
              }
              offerPrice = planGrpPrice;
              discountOfferPrice = offerPrice;
              planOfferPriceController.text = offerPrice.toStringAsFixed(2);
              planNewPriceController.text =
                  discountOfferPrice.toStringAsFixed(2);
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          /* if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }*/
        }
        // getFindPlanGroupByIdData();
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        // getFindPlanGroupByIdData();
        _handleApiError(error);
      },
    );
  }

  getBillToDetail() {
    isLoading = true;
    selectedBillTo = null;
    billToList!.clear();
    update();
    CustomerProvider().getBillToData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BillToRes responseData = BillToRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  billToList!.addAll(responseData.dataList!);

                  for (BillToDetail element in billToList!) {
                    if (element.id == 857) {
                      selectedBillTo = element;
                      break;
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
        getPlanGroupDetail();
      },
      onError: (ResponseModel error) {
        getPlanGroupDetail();
        _handleApiError(error);
      },
    );
  }

  manageDiscountVisibility() {
    if (selectedBillTo != null) {
      if (selectedBillTo!.id == 857) {
        showDiscountPrice = false;
        showInvoiceTag = true;
        businessPromotionFlag = true;
      } else {
        showDiscountPrice = true;
        showInvoiceTag = false;
        businessPromotionFlag = false;
      }
    }
    update();
  }

  manageThePlanGroupSelection() {
    if (selPlanGroup != null &&
        selPlanGroup!.category != null &&
        selPlanGroup!.category!.equalsIgnoreCase("Business Promotion")) {
      showDiscountPrice = false;
      showInvoiceTag = true;
      readOnlyDiscountPrice = true;
      billToReadOnly = true;
      selectedBillTo = null;
      for (BillToDetail element in billToList!) {
        if (element.id == 224) {
          selectedBillTo = element;
          break;
        }
      }
    }
    if (selPlanGroup != null &&
        selPlanGroup!.category != null &&
        !selPlanGroup!.category!.equalsIgnoreCase("Business Promotion")) {
      showDiscountPrice = true;
      showInvoiceTag = false;
      readOnlyDiscountPrice = false;
      billToReadOnly = false;
    }
    /*num planGrpPrice = 0;
    if (selPlanGroup!.planMappingList != null &&
        selPlanGroup!.planMappingList!.isNotEmpty) {
      for (PlanMappingDetail element in selPlanGroup!.planMappingList!) {
        if (element.plan != null && element.plan!.offerprice != null) {
          print("offer price :- ${element.plan!.offerprice!}");
          planGrpPrice = planGrpPrice + element.plan!.offerprice!;
        }
      }
    }
    offerPrice = planGrpPrice;
    planOfferPriceController.text = offerPrice.toString();
    update();*/
    getPlanGroupToPlanListData();
  }

  getLeadServiceTypeList() {
    selectedLeadServiceType = null;
    leadServiceTypeList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().leadServiceType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadServiceTypeRes responseData =
                  LeadServiceTypeRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.servicerTypeList != null &&
                    responseData.servicerTypeList!.isNotEmpty) {
                  leadServiceTypeList =
                      responseData.servicerTypeList?[0].split(",");

                  if (leadMaster != null && leadMaster!.servicerType != null) {
                    for (String element in leadServiceTypeList!) {
                      if (element == leadMaster!.servicerType) {
                        selectedLeadServiceType = element;
                      }
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

  getCustomerDetailById(int customerId) {
    isLoading = true;
    update();
    CustomerProvider().getCustomerDetail(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        // isFirstCall = false;
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

                if (customerDetail != null) {
                  ifReadonlyExistingInput = true;
                  if (customerDetail!.custtype != null) {
                    for (var element in leadCustomerTypeList!) {
                      if (element.id!
                          .equalsIgnoreCase(customerDetail!.custtype!)) {
                        selectedCustomerLeadType = element;
                        type = selectedCustomerLeadType!.text;
                      }
                    }
                  }

                  if (customerDetail!.branch != null) {
                    if (customerDetail!.branchId != null) {
                      branchId = customerDetail!.branchId!;
                      getFindServiceAreaByBranchIdData(branchId);
                    }
                  }

                  // if (customerDetail!.serviceareaid != null) {
                  //   getServiceAreaDetail(customerDetail!.serviceareaid);
                  // }

                  if (customerDetail!.leadSourceId != null) {
                    selectLeadSource(customerDetail!.leadSourceId);
                  }

                  List<int> ids = [];
                  ids.add(customerDetail!.serviceareaid);
                  getAllBranchesByServiceAreaData(ids);

                  firstNameController.text = customerDetail!.firstname!;
                  lastNameController.text = customerDetail!.lastname!;

                  countryCode = customerDetail!.countryCode != null
                      ? customerDetail!.countryCode!
                      : Strings.defaultCountryCode;
                  mobileController.text = customerDetail!.mobile!;
                  emailController.text = customerDetail!.email!.trim();

                  for (NewServiceDataList element in newServicesAreaList!) {
                    if (element.id == customerDetail!.serviceareaid) {
                      selectNewServiceArea = element;
                      serviceAreaId = selectNewServiceArea!.id;
                    }
                  }

                  for (BranchesByServiceAreaDataList element
                      in branchesByServiceAreaList!) {
                    if (element.id == customerDetail!.branch) {
                      selectBranchesByServiceAreaData = element;
                    }
                  }

                  //Address
                  if (customerDetail!.addressList!.isNotEmpty) {
                    if (customerDetail!.addressList![0].addressType != null) {
                      getAreaDetail(
                          customerDetail!.addressList![0].areaId, "Present");
                      landmarkController.text =
                          customerDetail!.addressList![0].landmark!;
                    }
                  }

                  //CAF Title

                  // for (String element in Utils.getTitle()) {
                  //   if (element == customerDetail!.title) {
                  //     selectCustomerTitleCAF = element;
                  //   }
                  // }

                  // Calendar Type

                  for (DropdownDetail element in calenderTypeList!) {
                    if (element.id == customerDetail!.calendarType) {
                      selectedCalenderType = element;
                    }
                  }

                  contactPersonPayController.text =
                      customerDetail!.contactperson ?? "";
                  cafNoController.text = customerDetail!.cafno ?? "";
                  userNameController.text = customerDetail!.username ?? "";
                  passwordController.text = customerDetail!.password ?? "";
                }

                // serviceAreabaseData(customerDetail!.serviceareaid);

                // fNameController.text = customerDetail!.firstname!;
                // lastNameController.text = customerDetail!.lastname!;
                // userNameController.text = customerDetail!.username!;

                // secondMobileNumberController.text =
                // customerDetail!.altmobile != null
                //     ? customerDetail!.altmobile!
                //     : "";
                // telephoneController.text = customerDetail!.altphone != null
                //     ? customerDetail!.altphone!
                //     : "";
                // faxController.text =
                // customerDetail!.fax != null ? customerDetail!.fax! : "";
                // emailController.text =
                // customerDetail!.email != null ? customerDetail!.email! : "";
                // panNumberController.text =
                // customerDetail!.pan != null ? customerDetail!.pan! : "";
                // contactPersonController.text =
                // customerDetail!.contactperson != null
                //     ? customerDetail!.contactperson!
                //     : "";
                // cafNumberController.text =
                // customerDetail!.cafno != null ? customerDetail!.cafno! : "";
                // salesMarkController.text = customerDetail!.salesremark != null
                //     ? customerDetail!.salesremark!
                //     : "";
                //
                // for (CustomerTitle element in bdTypeList!) {
                //   if (element.text == customerDetail!.title) {
                //     selectedBDType = element;
                //   }
                // }
                // for (CustomerCategoryDetail element in custCategoryList!) {
                //   if (element.value == customerDetail!.dunningCategory) {
                //     selectedCustCategory = element;
                //   }
                // }
                // for (CustomerTypeData element in custTypeList!) {
                //   if (element.value == customerDetail!.dunningType) {
                //     selectedCustType = element;
                //   }
                // }
                // for (CustomerSubType element in customerSubTypeList!) {
                //   if (element.text == customerDetail!.dunningSubType) {
                //     selectedCustomerSubType = element;
                //   }
                // }
                // for (DepartmentListData element in allDepartmentDataList!) {
                //   if (element.name == customerDetail!.department) {
                //     selectAllDepartmentData = element;
                //   }
                // }

                // for (CustomerSectorData element in custSectorList!) {
                //   if (element.value == customerDetail!.dunningSector) {
                //     selectedCustSector = element;
                //   }
                // }
                // customerSectorType.text =
                // customerDetail!.dunningSubSector != null
                //     ? customerDetail!.dunningSubSector!
                //     : "";
                // customerSubType.text = customerDetail!.dunningSubType ?? "";
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
        // isFirstCall = false;
        _handleApiError(error);
      },
    );
  }

  getFindServiceAreaByBranchIdData(int? serviceAreaID) {
    isLoading = true;
    update();
    branchesByServiceAreaList!.clear();
    selectBranchesByServiceAreaData = null;
    CustomerProvider().getFindServiceAreaByBranchId(
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
                  branchesByServiceAreaList!.addAll(responseData.dataList!);
                  if (branchesByServiceAreaList!.isNotEmpty) {
                    selectBranchesByServiceAreaData =
                        branchesByServiceAreaList![0];
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  selectLeadSource(int? selectedLeadSourceId) {
    myViewFlag = false;
    for (int i = 0; i < leadSourceList!.length; i++) {
      if (leadSourceList![i].id == selectedLeadSourceId) {
        leadSourceTitle = leadSourceList![i].leadSourceName;
        break;
      }
    }

    if (leadSourceTitle!.equalsIgnoreCase("Customer")) {
      myViewFlag = false;
      if (leadMaster != null && leadMaster!.leadCustomerName != null) {
        getLeadSourceCustomerCRMSList();
      }
      return;
    }

    if (leadSourceTitle!.equalsIgnoreCase("Partner")) {
      myViewFlag = false;
      if (leadMaster != null && leadMaster!.leadPartnerName != null) {
        getLeadSourcePartnerCRMSList();
      }
      return;
    }

    if (leadSourceTitle!.equalsIgnoreCase("Staff")) {
      myViewFlag = false;
      if (leadMaster != null && leadMaster!.leadStaffName != null) {
        getLeadSourceStaffUserCRMSList();
      }
      return;
    }

    if (leadSourceTitle!.equalsIgnoreCase("Outlet/ SA")) {
      myViewFlag = false;
      if (leadMaster != null && leadMaster!.leadServiceAreaName != null) {
        getLeadSourceServiceAreaCRMSList();
      }
      return;
    }

    if (leadSourceTitle!.equalsIgnoreCase("Branch")) {
      myViewFlag = false;
      if (leadMaster != null && leadMaster!.leadBranchName != null) {
        getLeadSourceBranchCRMSList();
      }
      return;
    }

    if (leadSourceList!.isNotEmpty) {
      for (int i = 0; i < leadSourceList!.length; i++) {
        if (!(leadSourceList![i].view!)) {
          if (leadSourceList![i].leadSubSourceDtoList!.isNotEmpty) {
            for (int j = 0;
                j < leadSourceList![i].leadSubSourceDtoList!.length;
                j++) {
              if (leadSourceList![i].leadSubSourceDtoList![j].leadSourceId ==
                  selectedLeadSourceId) {
                leadSubSourceArr!
                    .add(leadSourceList![i].leadSubSourceDtoList![j]);
                myViewFlag = true;
              }
            }
          }
        }
      }
    }

    if (leadSubSourceArr!.isEmpty) {
      myViewFlag = true;
    }
  }

  getLeadSourcePartnerCRMSList() {
    leadSourcePartnerList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadSourcePartner(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourcePartnerCRMRes responseData =
                  LeadSourcePartnerCRMRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.partnerList != null &&
                    responseData.partnerList!.isNotEmpty) {
                  leadSourcePartnerList!.addAll(responseData.partnerList!);

                  if (leadMaster != null &&
                      leadMaster!.leadPartnerName != null) {
                    for (PartnerList element in leadSourcePartnerList!) {
                      if (element.name!
                          .equalsIgnoreCase(leadMaster!.leadPartnerName!)) {
                        selectedLeadSourcePartner = element;
                      }
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

  getPinCodeToAreaList(int id) {
    isLoading = true;
    update();
    CustomerProvider().getPincodeToAreaData(
      id: id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PincodeToAreaByIdRes responseData =
                  PincodeToAreaByIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null) {
                  pinCodeToAreaByIdData = responseData.data!;

                  for (PincodeDetail element in pincodeList!) {
                    if (element.pincode == pinCodeToAreaByIdData!.pincode) {
                      selPresentPincode = element;
                      getPinCodeToAreaData(
                          selPresentPincode!.pincodeid!, "Present");
                    }
                  }
                }
              } /*else {
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getLeadSourceStaffUserCRMSList() {
    leadSourceStaffUserList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadSourceStaffUsers(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourceStaffUserCRMRes responseData =
                  LeadSourceStaffUserCRMRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.staffUserList!.isNotEmpty) {
                  for (var element in responseData.staffUserList!) {
                    leadSourceStaffUserList!.add(element);
                  }
                  // leadSourceStaffUserList!.addAll(responseData.staffUserList!);

                  if (leadMaster != null && leadMaster!.leadStaffName != null) {
                    for (StaffUserList element in leadSourceStaffUserList!) {
                      if (element.firstname!
                          .equalsIgnoreCase(leadMaster!.leadStaffName!)) {
                        selectedLeadSourceStaffUser = element;
                      }
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

  getLeadSourceServiceAreaCRMSList() {
    leadSourceServiceAreaList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadSourceServiceArea(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourceServiceAreaCRMRes responseData =
                  LeadSourceServiceAreaCRMRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.serviceAreaList != null &&
                    responseData.serviceAreaList!.isNotEmpty) {
                  leadSourceServiceAreaList!
                      .addAll(responseData.serviceAreaList!);

                  if (leadMaster != null &&
                      leadMaster!.leadServiceAreaName != null) {
                    for (ServiceAreaList element
                        in leadSourceServiceAreaList!) {
                      if (element.name!
                          .equalsIgnoreCase(leadMaster!.leadServiceAreaName!)) {
                        selectedLeadSourceServiceArea = element;
                      }
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

  getLeadSourceBranchCRMSList() {
    leadSourceBranchList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadSourceBranch(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourceBranchCRMRes responseData =
                  LeadSourceBranchCRMRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.branchList != null &&
                    responseData.branchList!.isNotEmpty) {
                  leadSourceBranchList!.addAll(responseData.branchList!);

                  if (leadMaster != null &&
                      leadMaster!.leadBranchName != null) {
                    for (BranchList element in leadSourceBranchList!) {
                      if (element.name!
                              .equalsIgnoreCase(leadMaster!.leadBranchName!) ||
                          element.name == leadMaster!.leadBranchName!) {
                        selectedLeadSourceBranch = element;
                      }
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

  getLeadSourceCustomerCRMSList() {
    leadSourceCustomerList!.clear();
    isLoading = true;
    update();
    LeadSystemProvider().getLeadSourceCustomer(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadSourceCustomerCRMRes responseData =
                  LeadSourceCustomerCRMRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.customersList != null &&
                    responseData.customersList!.isNotEmpty) {
                  leadSourceCustomerList!.addAll(responseData.customersList!);

                  if (leadMaster != null &&
                      leadMaster!.leadCustomerName != null) {
                    for (CustomersList element in leadSourceCustomerList!) {
                      if (element.firstname == leadMaster!.leadCustomerName) {
                        selectedLeadSourceCustomer = element;
                      }
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

  checkCustomerExist() {
    isLoading = true;
    update();
    LeadSystemProvider().checkLeadExistByName(
      username: userNameController.text,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (activeStep < dotCount - 1) {
                  activeStep++;
                  autoValidateMode = AutovalidateMode.disabled;
                  update();
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

  getLeadDetailData(int eventId) {
    isLoading = true;
    update();
    LeadSystemProvider().getLeadDetailsById(
      eventId: eventId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadMasterDetailsRes responseData =
                  LeadMasterDetailsRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                leadMaster = responseData.leadMaster;

                if (leadMaster != null) {
                  //Basic Lead Details
                  if (leadMaster!.leadCustomerType != null) {
                    for (DropdownDetail element in leadCustomerTypeList!) {
                      if (element.text!
                          .equalsIgnoreCase(leadMaster!.leadCustomerType!)) {
                        selectedCustomerLeadType = element;
                      }
                    }
                  }

                  if (leadMaster!.blockNo != null) {
                    selectedBlockNo = int.parse(leadMaster!.blockNo.toString());
                  }

                  if (selectedCustomerLeadType != null) {
                    type = selectedCustomerLeadType?.text;
                  }

                  if (leadMaster!.leadCategory != null) {
                    for (DropdownDetail element in leadCategoryList!) {
                      if (element.text!
                          .equalsIgnoreCase(leadMaster!.leadCategory!)) {
                        selectedLeadCategory = element;
                      }
                    }
                  }
                  if (leadMaster!.heardAboutSubisuFrom != null) {
                    remarksController.text = leadMaster!.heardAboutSubisuFrom;
                  }

                  if (leadMaster!.assigneeName != null) {
                    assigneeName = leadMaster!.assigneeName;
                  } else {
                    assigneeName = null;
                  }
                  if (leadMaster!.feasibilityRemark != null) {
                    remarksController.text = leadMaster!.feasibilityRemark;
                  }

                  //Basic Customer Details
                  if (leadMaster!.firstname != null) {
                    firstNameController.text = leadMaster!.firstname;
                  }

                  if (leadMaster!.lastname != null) {
                    lastNameController.text = leadMaster!.lastname;
                  }
                  if (leadMaster!.mobile != null) {
                    mobileController.text = leadMaster!.mobile;
                  }

                  if (leadMaster!.countryCode != null) {
                    countryCode = leadMaster!.countryCode != null
                        ? leadMaster!.countryCode!
                        : Strings.defaultCountryCode;
                  }
                  if (leadMaster!.email != null) {
                    emailController.text = leadMaster!.email;
                  }
                  if (leadMaster!.gender != null) {
                    selectedLeadCustomerGender = leadMaster!.gender;
                  }

                  if (leadMaster!.pan != null) {
                    panController.text = leadMaster!.pan;
                  }
                  if (leadMaster!.tinNo != null) {
                    vatController.text = leadMaster!.tinNo;
                  }

                  if (leadMaster!.addressList!.isNotEmpty) {
                    if (leadMaster!.addressList?[0].landmark != null) {
                      landmarkController.text =
                          leadMaster!.addressList?[0].landmark!;
                    }
                    // if (leadMaster!.addressList![0].addressType != null) {
                    //   getAreaDetail(
                    //       leadMaster!.addressList![0].areaId, "Present");
                    // }

                    if (leadMaster!.addressList![0].streetName != null) {
                      streetNameController.text =
                          leadMaster!.addressList![0].streetName!;
                    }

                    if (leadMaster!.addressList![0].houseNo != null) {
                      houseNumberController.text =
                          leadMaster!.addressList![0].houseNo!;
                    }
                  }

                  if (leadMaster!.latitude != null) {
                    latController.text = leadMaster!.latitude!;
                  }
                  if (leadMaster!.longitude != null) {
                    longController.text = leadMaster!.longitude!;
                  }

                  // if (leadMaster!.branchId != null) {
                  //     branchId = leadMaster!.branchId!;
                  //     getFindServiceAreaByBranchIdData(serviceAreaId);
                  // }

                  // Plan Details
                  if (leadMaster!.planMappingList!.isNotEmpty) {
                    planOfferPriceController.text =
                        leadMaster!.planMappingList![0].offerPrice.toString();
                    newOfferPricePlanController.text =
                        leadMaster!.planMappingList![0].newAmount.toString();

                    getPlanDetailFromPlanId(
                        leadMaster!.planMappingList![0].planId);
                  }

                  if (leadMaster!.serviceareaid != null) {
                    // getServiceAreaDetail(leadMaster!.serviceareaid);
                    List<int>? serviceAreaId = [];
                    serviceAreaId.add(leadMaster!.serviceareaid!);
                    if (leadMaster != null) {
                      getAllServicesByServiceAreaIdData(serviceAreaId);
                    } else {
                      getAllBranchesByServiceAreaData(serviceAreaId);
                    }
                  }

                  // if (!leadMaster!.serviceareaid!.isNullOrEmpty()) {
                  //   getServicePlanModeServiceAreaAPI();
                  // }

                  //Competitor Pack Details
                  if (leadMaster!.previousAmount != null) {
                    previousAmountController.text =
                        leadMaster!.previousAmount.toString();
                  }
                  if (leadMaster!.previousMonth != null) {
                    for (DropdownDetail element in monthList!) {
                      if (element.id!
                          .toUpperCase()
                          .equalsIgnoreCase(leadMaster!.previousMonth)) {
                        selectMonth = element;
                      }
                    }
                  }

                  if (leadMaster!.competitorDuration != null) {
                    String? competitorPackDuration =
                        leadMaster!.competitorDuration;
                    List<String> splitString =
                        competitorPackDuration!.split(" ");
                    String? packDuration = splitString[0];
                    String? packUnit = splitString[1];

                    packDurationController.text = packUnit;
                    if (packDuration.isNotEmpty) {
                      for (String element in Utils.getDurationUnits()) {
                        if (element.equalsIgnoreCase(packDuration)) {
                          selectDurationUnit = element;
                        }
                      }
                    }
                  }
                  if (leadMaster!.expiry != null) {
                    expiryController.text = apiDateFormat
                        .format(DateTime.parse(leadMaster!.expiry!));
                  }
                  if (leadMaster!.amount != null) {
                    currentPayController.text = leadMaster!.amount.toString();
                  }
                  if (leadMaster!.feedback != null) {
                    customerFeedbackPayController.text = leadMaster!.feedback;
                  }

                  //Basic CAF Details
                  // if (leadMaster!.title != null) {
                  //   for (String element in Utils.getTitle()) {
                  //     if (element.equalsIgnoreCase(leadMaster!.title!)) {
                  //       selectCustomerTitleCAF = element;
                  //     }
                  //   }
                  // }

                  if (leadMaster!.contactperson != null) {
                    contactPersonPayController.text = leadMaster!.contactperson;
                  }
                  if (leadMaster!.cafno != null) {
                    cafNoController.text = leadMaster!.cafno;
                  }
                  if (leadMaster!.calendarType != null) {
                    for (DropdownDetail element in calenderTypeList!) {
                      if (element.id!
                          .equalsIgnoreCase(leadMaster!.calendarType!)) {
                        selectedCalenderType = element;
                      }
                    }
                  }
                  if (leadMaster!.username != null) {
                    userNameController.text = leadMaster!.username!;
                  }
                  if (leadMaster!.password != null) {
                    passwordController.text = leadMaster!.password!;
                  }
                  //Secondary Contact Details
                  if (leadMaster!.landlineNumber != null) {
                    landlineNumberController.text = leadMaster!.landlineNumber;
                  }
                  if (leadMaster!.secondaryEmail != null) {
                    secondaryEmailController.text = leadMaster!.secondaryEmail;
                  }
                  if (leadMaster!.secondaryPhone != null) {
                    secondaryPhoneController1.text = leadMaster!.secondaryPhone;
                  }
                  if (leadMaster!.altmobile1 != null) {
                    secondaryPhoneController2.text = leadMaster!.altmobile1;
                  }
                  if (leadMaster!.altmobile2 != null) {
                    secondaryPhoneController3.text = leadMaster!.altmobile2;
                  }
                  if (leadMaster!.altmobile3 != null) {
                    secondaryPhoneController4.text = leadMaster!.altmobile3;
                  }
                  if (leadMaster!.altmobile4 != null) {
                    secondaryPhoneController5.text = leadMaster!.altmobile4;
                  }
                }
              } else {
                // if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
                // }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        // getGenerateLeadNoData();
        getCustomerCategory();
      },
      onError: (ResponseModel error) {
        // getGenerateLeadNoData();
        getCustomerCategory();
        _handleApiError(error);
      },
    );
  }

  createLead() {
    isLoading = true;
    update();
    if (userDetail!.partnerId == null) {
      partnerIdValue = 1;
    } else {
      partnerIdValue = userDetail!.partnerId;
    }

    dynamic leadStaffId;
    if (selectedLeadSourceCustomer != null) {
      leadStaffId = selectedLeadSourceCustomer!.id;
    } else if (selectedLeadSourceBranch != null) {
      leadStaffId = selectedLeadSourceBranch!.id;
    } else if (selectedLeadSourcePartner != null) {
      leadStaffId = selectedLeadSourcePartner!.id;
    } else if (selectedLeadSourceStaffUser != null) {
      leadStaffId = selectedLeadSourceStaffUser!.id;
    } else if (selectedLeadSourceServiceArea != null) {
      leadStaffId = selectedLeadSourceServiceArea!.id;
    } else if (selectedLeadSubSourceArr != null) {
      leadStaffId = selectedLeadSubSourceArr!.id;
    } else if (selectAgentArr != null) {
      leadStaffId = selectAgentArr;
    }

    List<LeadAddressList>? leadAddressList = [];
    leadAddressList.add(LeadAddressList(
        landmark: "",
        pincodeId: 0,
        areaId: 0,
        cityId: 0,
        stateId: 0,
        countryId: 0,
        streetName: "",
        houseNo: "",
        addressType: "null"));

    List<LeadSavePlanMappingList>? leadPlanMappingList = [];

    if (individualPlanList != null && individualPlanList!.isNotEmpty) {
      for (var element in individualPlanList!) {
        dynamic discount, newAmt, offerAmt;
        if (element.discount != null && element.discount!.isNotEmpty) {
          discount = double.parse(element.discount!);
        } else {
          discount = 0;
        }
        if (element.newOfferPrice != null &&
            element.newOfferPrice!.isNotEmpty) {
          newAmt = double.parse(element.newOfferPrice!).toString();
        } else {
          newAmt = "";
        }
        if (element.planOfferPrice != null &&
            element.planOfferPrice!.isNotEmpty) {
          offerAmt = double.parse(element.planOfferPrice!);
        } else {
          offerAmt = 0;
        }

        leadPlanMappingList.add(LeadSavePlanMappingList(
            planId: element.planDetail!.id,
            service: element.planService!.id,
            validity: double.tryParse(element.planDetail!.validity.toString()),
            discount: double.tryParse(discount.toString()),
            billTo: selectedBillTo != null ? selectedBillTo!.value : "CUSTOMER",
            billableCustomerId: billableToCustomerId,
            isInvoiceToOrg: selectedInvoiceToOrg != null &&
                    selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
                ? true
                : false,
            istrialplan: element.trialPlan,
            newAmount: double.tryParse(newAmt),
            discountType:
                selDiscountType != null ? selDiscountType!.text : "One-time",
            offerPrice: double.tryParse(offerAmt.toString())));
      }
    }

    LeadSaveReq saveReq = LeadSaveReq(
      //Basic Lead Details
      aadhar: "",
      dunningCategory: selectedCustCategory!.value ?? "",
      leadCustomerType: selectedCustomerLeadType!.text ?? "",
      leadCustomerSector: selectedCustSector!.value ?? "",
      requireServiceType: selectedRequireServiceType ?? "",
      leadType: selectedLeadType ?? "",
      leadCategory: selectedLeadCategory!.text ?? "",
      leadOriginType: selectedLeadOriginType ?? "",
      leadSourceId: selectedLeadSource!.id,
      feasibility: selectedLeadFeasibility ?? "",
      feasibilityRemark:
          remarksController.text.isNotEmpty ? remarksController.text : null,
      department: selectAllDepartmentData?.name,
      leadDepartment: selectAllDepartmentData?.name,
      leadSubSourceId: null,
      leadStaffId: leadStaffId,
      blockNo: selectedBlockNo,
      leadCustomerSubSector: leadCustomerSubSectorController.text.isNotEmpty
          ? leadCustomerSubSectorController.text
          : null,
      // heardAboutSubisuFrom:
      //     remarksController.text.isNotEmpty ? remarksController.text : null,
      billTo: selectedBillTo != null ? selectedBillTo!.value : "CUSTOMER",
      isCustCaf: "yes",
      branchId: selectBranchesByServiceAreaData?.id,
      custtype: type,
      isInvoiceToOrg: selectedInvoiceToOrg != null &&
              selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
          ? true
          : false,
      isLeadFromCWSC: false,
      isLeadQuickInv: false,
      istrialplan: false,
      presentCheckForPayment: false,
      presentCheckForPermanent: false,
      custlabel: "customer",
      leadIdentity: "retail",
      failcount: 0,
      partnerid: partnerIdValue,
      passportNo: "",
      phone: "",

      //Basic Customer Details

      firstname: firstNameController.text.trim(),
      lastname: lastNameController.text.trim(),
      countryCode: countryCode,
      mobile: mobileController.text.trim(),
      email: emailController.text.trim().isNotEmpty
          ? emailController.text.trim()
          : "",
      parentCustomerId: selectedParentCustomer?.id,
      serviceareaid: selectNewServiceArea?.id,
      leadPartnerId: selectedLeadSourcePartner?.id,
      gender: selectedLeadCustomerGender,
      pan: panController.text.isNotEmpty ? panController.text : "",
      tinNo: vatController.text.isNotEmpty ? vatController.text : null,
      didno: "",
      gst: "",
      salesremark: "",
      voicesrvtype: "",

      // Present Address Details

      addressList: leadAddressList,
      valleyType: selectedValleyType?.text,
      insideValley: selectedInsideValley?.text,
      outsideValley: selectedOutsideValley?.text,
      latitude: latController.text.isNotEmpty ? latController.text : null,
      longitude: longController.text.isNotEmpty ? longController.text : null,

      //Plan Details
      planMappingList: leadPlanMappingList,
      overChargeList: [],
      custMacMapppingList: [],
      servicetype: "",

      //Competitor Pack Details

      servicerType: selectedLeadServiceType,
      previousAmount: double.tryParse(previousAmountController.text),
      previousMonth: selectMonth?.id,
      competitorDuration: packDurationController.text.isNotEmpty
          ? "${packDurationController.text} $selectDurationUnit"
          : null,
      durationUnits: selectDurationUnit,
      expiry: expiryController.text.isNotEmpty ? expiryController.text : null,
      amount: currentPayController.text.isNotEmpty
          ? currentPayController.text
          : null,
      feedback: customerFeedbackPayController.text.isNotEmpty
          ? customerFeedbackPayController.text
          : null,

      // Basic CAF Details
      title: "",//selectCustomerTitleCAF,
      contactperson: contactPersonPayController.text.isNotEmpty
          ? contactPersonPayController.text
          : null,
      cafno: cafNoController.text.isNotEmpty ? cafNoController.text : null,
      calendarType: selectedCalenderType!.text,
      username:
          userNameController.text.isNotEmpty ? userNameController.text : null,
      password:
          passwordController.text.isNotEmpty ? passwordController.text : null,

      //Secondary Contact Details

      landlineNumber: landlineNumberController.text.isNotEmpty
          ? landlineNumberController.text
          : null,
      secondaryEmail: secondaryEmailController.text.isNotEmpty
          ? secondaryEmailController.text
          : null,
      altmobile1: secondaryPhoneController1.text.isNotEmpty
          ? secondaryPhoneController1.text
          : null,
      altmobile2: secondaryPhoneController2.text.isNotEmpty
          ? secondaryPhoneController2.text
          : null,
      altmobile3: secondaryPhoneController3.text.isNotEmpty
          ? secondaryPhoneController3.text
          : null,
      altmobile4: secondaryPhoneController4.text.isNotEmpty
          ? secondaryPhoneController4.text
          : null,
    );

    log("saveCreateLead===>>>${jsonEncode(saveReq)}");

    LeadSystemProvider().saveCreateLead(
      request: saveReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreateLeadRes responseData = CreateLeadRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
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

  leadToCAFConvertCustomer() {
    isLoading = true;
    update();
    if (userDetail!.partnerId == null) {
      partnerIdValue = 1;
    } else {
      partnerIdValue = userDetail!.partnerId;
    }
    dynamic leadStaffId;
    if (selectedLeadSourceCustomer != null) {
      leadStaffId = selectedLeadSourceCustomer!.id;
    } else if (selectedLeadSourceBranch != null) {
      leadStaffId = selectedLeadSourceBranch!.id;
    } else if (selectedLeadSourcePartner != null) {
      leadStaffId = selectedLeadSourcePartner!.id;
    } else if (selectedLeadSourceStaffUser != null) {
      leadStaffId = selectedLeadSourceStaffUser!.id;
    } else if (selectedLeadSourceServiceArea != null) {
      leadStaffId = selectedLeadSourceServiceArea!.id;
    } else if (selectedLeadSubSourceArr != null) {
      leadStaffId = selectedLeadSubSourceArr!.id;
    } else if (selectAgentArr != null) {
      leadStaffId = selectAgentArr;
    }

    List<LeadAddressList>? leadAddressList = [];
    leadAddressList.add(LeadAddressList(
        landmark: landmarkController.text,
        pincodeId: selPresentPincode!.pincodeid!,
        areaId: selPresentArea!.id,
        cityId: selPresentCity!.id,
        stateId: selPresentState!.id,
        countryId: selPresentCountry!.id,
        streetName: streetNameController.text,
        houseNo: houseNumberController.text,
        addressType: "Present"));

    List<LeadSavePlanMappingList>? leadPlanMappingList = [];

    if (individualPlanList != null && individualPlanList!.isNotEmpty) {
      for (var element in individualPlanList!) {
        dynamic discount, newAmt, offerAmt;
        if (element.discount != null && element.discount!.isNotEmpty) {
          discount = double.parse(element.discount!);
        } else {
          discount = 0;
        }
        if (element.newOfferPrice != null &&
            element.newOfferPrice!.isNotEmpty) {
          newAmt = double.parse(element.newOfferPrice!).toString();
        } else {
          newAmt = null;
        }
        if (element.planOfferPrice != null &&
            element.planOfferPrice!.isNotEmpty) {
          offerAmt = double.parse(element.planOfferPrice!);
        } else {
          offerAmt = 0;
        }

        leadPlanMappingList.add(LeadSavePlanMappingList(
            planId: element.planDetail!.id,
            service: element.planService!.id,
            validity: double.tryParse(element.planDetail!.validity.toString()),
            discount: double.tryParse(discount.toString()),
            billTo: selectedBillTo != null ? selectedBillTo!.value : "",
            billableCustomerId: billableToCustomerId,
            isInvoiceToOrg: selectedInvoiceToOrg != null &&
                    selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
                ? true
                : false,
            istrialplan: element.trialPlan,
            newAmount: double.tryParse(newAmt),
            discountType:
                selDiscountType != null ? selDiscountType!.text : "One-time",
            offerPrice: double.tryParse(offerAmt.toString())));
      }
    }

    LeadSaveReq saveReq = LeadSaveReq(
      //Basic Lead Details
      aadhar: "",
      dunningCategory: selectedCustCategory!.value ?? "",
      leadCustomerType: selectedCustomerLeadType!.text ?? "",
      leadCustomerSector: selectedCustSector!.value ?? "",
      requireServiceType: selectedRequireServiceType ?? "",
      leadType: selectedLeadType ?? "",
      leadCategory: selectedLeadCategory!.text ?? "",
      leadOriginType: selectedLeadOriginType ?? "",
      leadSourceId: selectedLeadSource!.id,
      feasibility: selectedLeadFeasibility ?? "",
      department: selectAllDepartmentData?.name,
      leadDepartment: selectAllDepartmentData?.name,
      leadSubSourceId: null,
      leadSourceName: leadMaster!.leadSourceName,
      leadCustomerSubSector: leadCustomerSubSectorController.text.isNotEmpty
          ? leadCustomerSubSectorController.text
          : null,
      // heardAboutSubisuFrom: remarksController.text.isNotEmpty ? remarksController.text : null,
      billTo: selectedBillTo != null ? selectedBillTo!.value : "",
      isCustCaf: "yes",
      branchId: selectBranchesByServiceAreaData?.id,
      branch: selectBranchesByServiceAreaData?.id,
      assigneeName: assigneeName,
      feasibilityRemark:
          leadMaster != null ? leadMaster!.feasibilityRemark : "",
      custtype: type,
      isInvoiceToOrg: selectedInvoiceToOrg != null &&
              selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
          ? true
          : false,
      isLeadQuickInv: false,
      leadStaffId: leadStaffId,
      istrialplan: false,
      presentCheckForPayment: false,
      presentCheckForPermanent: false,
      custlabel: "customer",
      leadIdentity: null,
      failcount: 0,
      partnerid: partnerIdValue,
      passportNo: "",
      phone: "",
      // leadNo: leadNoController.text,
      blockNo: selectedBlockNo,
      leadId: leadMaster!.id,
      billday: selectedBillDay,

      nextApproveStaffId: leadMaster!.nextApproveStaffId,
      nextTeamMappingId: leadMaster!.nextTeamMappingId,
      flatAmount: double.tryParse(planNewPriceController.text),
      //Basic Customer Details

      firstname: firstNameController.text,
      lastname: lastNameController.text,
      countryCode: countryCode,
      mobile: mobileController.text,
      email: emailController.text.trim().isNotEmpty
          ? emailController.text.trim()
          : "",
      parentCustomerId: selectedParentCustomer?.id,
      serviceareaid: selectNewServiceArea?.id,
      leadPartnerId: selectedLeadSourcePartner?.id,
      gender: selectedLeadCustomerGender,
      pan: panController.text.isNotEmpty ? panController.text : "",
      tinNo: vatController.text.isNotEmpty ? vatController.text : null,
      didno: "",
      gst: "",
      salesremark: "",
      voicesrvtype: "",
      leadStatus: "Inquiry",

      // Present Address Details

      addressList: leadAddressList,
      valleyType: selectedValleyType?.text,
      insideValley: selectedInsideValley?.text,
      outsideValley: selectedOutsideValley?.text,
      latitude: latController.text.isNotEmpty ? latController.text : null,
      longitude: longController.text.isNotEmpty ? longController.text : null,

      //Plan Details
      planMappingList: leadPlanMappingList,
      overChargeList: [],
      custMacMapppingList: [],
      servicetype: "",

      //Competitor Pack Details

      servicerType: selectedLeadServiceType,
      previousAmount: double.tryParse(previousAmountController.text),
      previousMonth: selectMonth?.id,
      competitorDuration: packDurationController.text.isNotEmpty
          ? "${packDurationController.text} $selectDurationUnit"
          : null,
      durationUnits: selectDurationUnit,
      expiry: expiryController.text.isNotEmpty ? expiryController.text : null,
      amount: currentPayController.text.isNotEmpty
          ? currentPayController.text
          : null,
      feedback: customerFeedbackPayController.text.isNotEmpty
          ? customerFeedbackPayController.text
          : null,

      // Basic CAF Details
      title: "",//selectCustomerTitleCAF,
      contactperson: contactPersonPayController.text.isNotEmpty
          ? contactPersonPayController.text
          : null,
      cafno: cafNoController.text.isNotEmpty ? cafNoController.text : null,
      calendarType: selectedCalenderType!.text,
      username:
          userNameController.text.isNotEmpty ? userNameController.text : null,
      password:
          passwordController.text.isNotEmpty ? passwordController.text : null,

      //Secondary Contact Details

      landlineNumber: landlineNumberController.text.isNotEmpty
          ? landlineNumberController.text
          : null,
      secondaryEmail: secondaryEmailController.text.isNotEmpty
          ? secondaryEmailController.text
          : null,
      altmobile1: secondaryPhoneController1.text.isNotEmpty
          ? secondaryPhoneController1.text
          : null,
      altmobile2: secondaryPhoneController2.text.isNotEmpty
          ? secondaryPhoneController2.text
          : null,
      altmobile3: secondaryPhoneController3.text.isNotEmpty
          ? secondaryPhoneController3.text
          : null,
      altmobile4: secondaryPhoneController4.text.isNotEmpty
          ? secondaryPhoneController4.text
          : null,
    );

    log("saveCreateLead===>>>${jsonEncode(saveReq)}");

    LeadSystemProvider().leadToCAFConvertCustomer(
      request: saveReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          log("message=>>${responseModel.statusCode}");
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadToCAFRes responseData = LeadToCAFRes.fromJson(map);
              log("responseData===>${jsonEncode(responseData)}");
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                leadToCAFCustomer = responseData.customer;
                updateLead(responseData.customer!.leadId,
                    responseData.customer!.id, true);
              } else {
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
        /*if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
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
        }*/
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  updateLead(int? leadId, int? customerId, bool? isLeadToCAF) {
    isLoading = true;
    update();
    if (userDetail!.partnerId == null) {
      partnerIdValue = 1;
    } else {
      partnerIdValue = userDetail!.partnerId;
    }

    List<LeadAddressList>? leadAddressList = [];
    leadAddressList.add(LeadAddressList(
        landmark: landmarkController.text,
        pincodeId: selPresentPincode?.pincodeid ?? 0,
        areaId: selPresentArea?.id ?? 0,
        cityId: selPresentCity?.id ?? 0,
        stateId: selPresentState?.id ?? 0,
        countryId: selPresentCountry?.id ?? 0,
        streetName: streetNameController.text,
        houseNo: houseNumberController.text,
        addressType: "Present"));

    List<LeadSavePlanMappingList>? leadPlanMappingList = [];

    if (individualPlanList != null && individualPlanList!.isNotEmpty) {
      for (var element in individualPlanList!) {
        dynamic discount, newAmt, offerAmt;
        if (element.discount != null && element.discount!.isNotEmpty) {
          discount = double.parse(element.discount!);
        } else {
          discount = 0;
        }
        if (element.newOfferPrice != null &&
            element.newOfferPrice!.isNotEmpty) {
          newAmt = double.parse(element.newOfferPrice!).toString();
        } else {
          newAmt = "";
        }
        if (element.planOfferPrice != null &&
            element.planOfferPrice!.isNotEmpty) {
          offerAmt = double.parse(element.planOfferPrice!);
        } else {
          offerAmt = 0;
        }

        leadPlanMappingList.add(LeadSavePlanMappingList(
            planId: element.planDetail?.id,
            service: element.planService!.id,
            validity: double.tryParse(element.planDetail!.validity.toString()),
            discount: double.tryParse(discount.toString()),
            billTo: selectedBillTo != null ? selectedBillTo!.value : "",
            billableCustomerId: billableToCustomerId,
            isInvoiceToOrg: selectedInvoiceToOrg != null &&
                    selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
                ? true
                : false,
            istrialplan: element.trialPlan,
            newAmount: double.tryParse(newAmt),
            discountType:
                selDiscountType != null ? selDiscountType!.text : "One-time",
            offerPrice: double.tryParse(offerAmt.toString())));
      }
    }
    LeadSaveReq saveReq;

    if (isLeadToCAF == true) {
      // saveReq = LeadSaveReq(
      //   approveMvnoId: userDetail?.mvnoId,
      //   approveStaffId: userDetail?.userId,
      //   approveCurrentLoggedInStaffId: userDetail?.userId,
      //   approveStatus: "Approved",
      //   approverNextLeadApprover: userDetail?.userId,
      //   approveFirstname:firstNameController.text,
      //   approveUsername:userNameController.text,
      //   approveServiceareaid:selPresentServiceArea?.id,
      //   leadStatus: "Converted",
      //   isCustomerCafeIsUpdated: true,
      //   customerId: customerId,
      //   assigneeName: null,
      // );
      saveReq = LeadSaveReq(
        //Basic Lead Details
        aadhar: "",
        dunningCategory: selectedCustCategory!.value ?? "",
        leadCustomerType: selectedCustomerLeadType!.text ?? "",
        leadCustomerSector: selectedCustSector!.value ?? "",
        requireServiceType: selectedRequireServiceType ?? "",
        leadType: selectedLeadType ?? "",
        leadCategory: selectedLeadCategory!.text ?? "",
        leadOriginType: selectedLeadOriginType ?? "",
        leadSourceId: selectedLeadSource!.id,
        feasibility: selectedLeadFeasibility ?? "",
        feasibilityRemark: remarksController.text,
        department: selectAllDepartmentData?.name,
        leadDepartment: selectAllDepartmentData?.name,
        leadSubSourceId: null,
        leadCustomerSubSector: leadCustomerSubSectorController.text.isNotEmpty
            ? leadCustomerSubSectorController.text
            : null,
        heardAboutSubisuFrom:
            remarksController.text.isNotEmpty ? remarksController.text : null,
        billTo: selectedBillTo != null ? selectedBillTo!.value : "",
        isCustCaf: "yes",
        branchId: selectBranchesByServiceAreaData?.id,
        custtype: type,
        isInvoiceToOrg: selectedInvoiceToOrg != null &&
                selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
            ? true
            : false,
        isLeadFromCWSC: false,
        isLeadQuickInv: false,
        istrialplan: false,
        presentCheckForPayment: false,
        presentCheckForPermanent: false,
        custlabel: "customer",
        leadIdentity: leadMaster!.leadIdentity,
        leadNo: leadMaster!.leadNo,
        blockNo: selectedBlockNo,
        leadSourceName: leadMaster!.leadSourceName,
        failcount: 0,
        partnerid: partnerIdValue,
        passportNo: "",
        phone: "",
        id: leadId,
        leadId: leadId,
        billday: leadToCAFCustomer?.billday,
        branch: leadToCAFCustomer?.branch,
        flatAmount: double.tryParse(planNewPriceController.text),
        //
        approveMvnoId: userDetail?.mvnoId,
        approveStaffId: userDetail?.userId,
        approveCurrentLoggedInStaffId: userDetail?.userId,
        approveStatus: "Approved",
        approverNextLeadApprover: userDetail?.userId,
        approveFirstname: firstNameController.text,
        approveUsername: userNameController.text,
        approveServiceareaid: selectNewServiceArea?.id,
        leadStatus: "Converted",
        isCustomerCafeIsUpdated: true,
        customerId: customerId,
        assigneeName: null,
        nextTeamMappingId: leadMaster!.nextTeamMappingId,
        nextApproveStaffId: leadMaster!.nextApproveStaffId,
        discountType:
            selDiscountType != null ? selDiscountType!.text : "One-time",

        //Basic Customer Details

        firstname: firstNameController.text.trim(),
        lastname: lastNameController.text.trim(),
        countryCode: countryCode,
        mobile: mobileController.text.trim(),
        email: emailController.text.trim().isNotEmpty
            ? emailController.text.trim()
            : "",
        parentCustomerId: selectedParentCustomer?.id,
        serviceareaid: selectNewServiceArea?.id,
        leadPartnerId: selectedLeadSourcePartner?.id,
        gender: "Male",
        pan: panController.text.isNotEmpty ? panController.text : "",
        tinNo: vatController.text.isNotEmpty ? vatController.text : null,
        didno: "",
        gst: "",
        salesremark: "",
        voicesrvtype: "",

        // Present Address Details

        addressList: leadAddressList,
        valleyType: selectedValleyType?.text,
        insideValley: selectedInsideValley?.text,
        outsideValley: selectedOutsideValley?.text,
        latitude: latController.text.isNotEmpty ? latController.text : null,
        longitude: longController.text.isNotEmpty ? longController.text : null,

        //Plan Details
        planMappingList: leadPlanMappingList,
        overChargeList: [],
        custMacMapppingList: [],
        servicetype: "",

        //Competitor Pack Details

        servicerType: selectedLeadServiceType,
        previousAmount: double.tryParse(previousAmountController.text),
        previousMonth: selectMonth?.id,
        competitorDuration: packDurationController.text.isNotEmpty
            ? "${packDurationController.text} $selectDurationUnit"
            : null,
        durationUnits: selectDurationUnit,
        expiry: expiryController.text.isNotEmpty ? expiryController.text : null,
        amount: currentPayController.text.isNotEmpty
            ? currentPayController.text
            : null,
        feedback: customerFeedbackPayController.text.isNotEmpty
            ? customerFeedbackPayController.text
            : null,

        // Basic CAF Details
        title: "", //selectCustomerTitleCAF,
        contactperson: contactPersonPayController.text.isNotEmpty
            ? contactPersonPayController.text
            : null,
        cafno: cafNoController.text.isNotEmpty ? cafNoController.text : null,
        calendarType: selectedCalenderType != null
            ? selectedCalenderType!.text
            : "English",
        username:
            userNameController.text.isNotEmpty ? userNameController.text : null,
        password:
            passwordController.text.isNotEmpty ? passwordController.text : null,

        //Secondary Contact Details

        landlineNumber: landlineNumberController.text.isNotEmpty
            ? landlineNumberController.text
            : null,
        secondaryEmail: secondaryEmailController.text.isNotEmpty
            ? secondaryEmailController.text
            : null,
        altmobile1: secondaryPhoneController1.text.isNotEmpty
            ? secondaryPhoneController1.text
            : null,
        altmobile2: secondaryPhoneController2.text.isNotEmpty
            ? secondaryPhoneController2.text
            : null,
        altmobile3: secondaryPhoneController3.text.isNotEmpty
            ? secondaryPhoneController3.text
            : null,
        altmobile4: secondaryPhoneController4.text.isNotEmpty
            ? secondaryPhoneController4.text
            : null,
      );
    } else {
      saveReq = LeadSaveReq(
        //Basic Lead Details
        aadhar: "",
        dunningCategory: selectedCustCategory!.value ?? "",
        leadCustomerType: selectedCustomerLeadType!.text ?? "",
        leadCustomerSector: selectedCustSector!.value ?? "",
        requireServiceType: selectedRequireServiceType ?? "",
        leadType: selectedLeadType ?? "",
        leadCategory: selectedLeadCategory!.text ?? "",
        leadOriginType: selectedLeadOriginType ?? "",
        leadSourceId: selectedLeadSource!.id,
        feasibility: selectedLeadFeasibility ?? "",
        feasibilityRemark:
            remarksController.text.isNotEmpty ? remarksController.text : null,
        department: selectAllDepartmentData?.name,
        leadDepartment: selectAllDepartmentData?.name,
        leadSubSourceId: null,
        leadCustomerSubSector: leadCustomerSubSectorController.text.isNotEmpty
            ? leadCustomerSubSectorController.text
            : null,
        heardAboutSubisuFrom:
            remarksController.text.isNotEmpty ? remarksController.text : null,
        billTo: selectedBillTo != null ? selectedBillTo!.value : "CUSTOMER",
        isCustCaf: "no",
        branchId: selectBranchesByServiceAreaData?.id,
        custtype: type,
        isInvoiceToOrg: selectedInvoiceToOrg != null &&
                selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
            ? true
            : false,
        isLeadFromCWSC: false,
        isLeadQuickInv: false,
        istrialplan: false,
        presentCheckForPayment: false,
        presentCheckForPermanent: false,
        custlabel: "customer",
        leadIdentity: leadMaster!.leadIdentity,
        leadNo: leadMaster!.leadNo,
        blockNo: selectedBlockNo,
        leadSourceName: leadMaster!.leadSourceName,
        failcount: 0,
        partnerid: partnerIdValue,
        passportNo: "",
        phone: "",
        id: leadId,
        leadId: leadId,
        leadStatus: leadMaster!.leadStatus,
        customerId: customerId,
        assigneeName: leadMaster != null ? leadMaster!.assigneeName : "",
        nextTeamMappingId: leadMaster!.nextTeamMappingId,
        nextApproveStaffId: leadMaster!.nextApproveStaffId,
        flatAmount: double.tryParse(planNewPriceController.text),

        //Basic Customer Details

        firstname: firstNameController.text.trim(),
        lastname: lastNameController.text.trim(),
        countryCode: countryCode,
        mobile: mobileController.text,
        email: emailController.text.trim().isNotEmpty
            ? emailController.text.trim()
            : "",
        parentCustomerId: selectedParentCustomer?.id,
        serviceareaid: selectNewServiceArea?.id,
        leadPartnerId: selectedLeadSourcePartner?.id,
        gender: "Male",
        pan: panController.text.isNotEmpty ? panController.text : "",
        tinNo: vatController.text.isNotEmpty ? vatController.text : null,
        didno: "",
        gst: "",
        salesremark: "",
        voicesrvtype: "",

        // Present Address Details

        addressList: leadAddressList,
        valleyType: selectedValleyType?.text,
        insideValley: selectedInsideValley?.text,
        outsideValley: selectedOutsideValley?.text,
        latitude: latController.text.isNotEmpty ? latController.text : null,
        longitude: longController.text.isNotEmpty ? longController.text : null,

        //Plan Details
        planMappingList: leadPlanMappingList,
        overChargeList: [],
        custMacMapppingList: [],
        servicetype: "",

        //Competitor Pack Details

        servicerType: selectedLeadServiceType,
        previousAmount: double.tryParse(previousAmountController.text),
        previousMonth: selectMonth?.id,
        competitorDuration: packDurationController.text.isNotEmpty
            ? "${packDurationController.text} $selectDurationUnit"
            : null,
        durationUnits: selectDurationUnit,
        expiry: expiryController.text.isNotEmpty ? expiryController.text : null,
        amount: currentPayController.text.isNotEmpty
            ? currentPayController.text
            : null,
        feedback: customerFeedbackPayController.text.isNotEmpty
            ? customerFeedbackPayController.text
            : null,

        // Basic CAF Details
        title: "", // selectCustomerTitleCAF,
        contactperson: contactPersonPayController.text.isNotEmpty
            ? contactPersonPayController.text
            : null,
        cafno: cafNoController.text.isNotEmpty ? cafNoController.text : null,
        calendarType: selectedCalenderType!.text,
        username:
            userNameController.text.isNotEmpty ? userNameController.text : null,
        password:
            passwordController.text.isNotEmpty ? passwordController.text : null,

        //Secondary Contact Details

        landlineNumber: landlineNumberController.text.isNotEmpty
            ? landlineNumberController.text
            : null,
        secondaryEmail: secondaryEmailController.text.isNotEmpty
            ? secondaryEmailController.text
            : null,
        altmobile1: secondaryPhoneController1.text.isNotEmpty
            ? secondaryPhoneController1.text
            : null,
        altmobile2: secondaryPhoneController2.text.isNotEmpty
            ? secondaryPhoneController2.text
            : null,
        altmobile3: secondaryPhoneController3.text.isNotEmpty
            ? secondaryPhoneController3.text
            : null,
        altmobile4: secondaryPhoneController4.text.isNotEmpty
            ? secondaryPhoneController4.text
            : null,
      );
    }

    log("updateCreateLead===>>>${jsonEncode(saveReq)}");

    LeadSystemProvider().updateCreateLead(
      request: saveReq,
      leadId: leadId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CreateLeadRes responseData = CreateLeadRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                Get.back(result: true);
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
              } else {
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
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

  handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 400) {
      Utils.showSnackbar(Strings.INFO, Strings.badRequest, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
    update();
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    if (error.statusCode == Constant.STATUS_CODE_NOT_RECORD_FOUND) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(Strings.INFO, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
    update();
  }

  _handleApiErrorPlanGroupCustom(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    /*if (error.statusCode == Constant.STATUS_CODE_NOT_RECORD_FOUND) {
      Utils.showSnackbar(Strings.INFO, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(Strings.INFO, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }*/
    update();
  }
}
