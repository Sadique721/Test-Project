import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/charge_data.dart';
import 'package:savbill/pages/customer/model/individual_plan_data.dart';
import 'package:savbill/pages/customer/model/response/PincodeToAreaData.dart';
import 'package:savbill/pages/customer/model/response/address_detail_response.dart';
import 'package:savbill/pages/customer/model/response/bill_to_res.dart';
import 'package:savbill/pages/customer/model/response/branch_by_service_area_id_res.dart';
import 'package:savbill/pages/customer/model/response/charge_list_res.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/customer/model/response/country_list_res.dart';
import 'package:savbill/pages/customer/model/response/customer_category_res.dart';
import 'package:savbill/pages/customer/model/response/customer_department_list.dart';
import 'package:savbill/pages/customer/model/response/customer_exist_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sector_res.dart';
import 'package:savbill/pages/customer/model/response/customer_status_res.dart';
import 'package:savbill/pages/customer/model/response/customer_sub_type_res.dart';
import 'package:savbill/pages/customer/model/response/customer_title_res.dart';
import 'package:savbill/pages/customer/model/response/customer_type_res.dart';
import 'package:savbill/pages/customer/model/response/get_all_services_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/inside_outside_valley_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer/model/response/network_devices_by_device_type_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/partner_list_res.dart';
import 'package:savbill/pages/customer/model/response/payment_mode_list_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_area_res.dart';
import 'package:savbill/pages/customer/model/response/pincode_list_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_mapping_res.dart';
import 'package:savbill/pages/customer/model/response/plan_group_res.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/customer/model/response/plans_by_plan_group_id_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_detail_res.dart';
import 'package:savbill/pages/customer/model/response/service_area_plan_mode_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/customer/model/response/staffs_by_service_area_res.dart';
import 'package:savbill/pages/customer/model/response/state_list_res.dart';
import 'package:savbill/pages/customer/model/response/valley_type_res.dart';
import 'package:savbill/pages/customer_caf/response/building_mgmt_number_res.dart';
import 'package:savbill/pages/customer_caf/response/building_refence_all_res.dart';
import 'package:savbill/pages/customer_caf/response/get_area_all_res.dart';
import 'package:savbill/pages/customer_caf/response/get_building_management_res.dart';
import 'package:savbill/pages/customer_caf/response/get_pincode_all_res.dart';
import 'package:savbill/pages/customer_caf/response/get_sub_area_res.dart';
import 'package:savbill/pages/customer_caf/response/simple_data_res.dart';
import 'package:savbill/pages/customer_charge/charge_management_provider.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:dropdown_search/dropdown_search.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../../routes/app_routes.dart';
import '../../util/logger.dart';
import '../service_management/request/add_service_req.dart';
import 'response/add_edit_customer_caf_req.dart';

class AddEditCafCustomerController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  String? action, customerId, type;
  UserDetail? userData;
  List<CustomerTitle>? bdTypeList = [];
  CustomerTitle? selectedBDType;

  List<ValleyType>? valleyTypeList = [];
  ValleyType? selectedValleyType;

  List<InsideOutsideValleyData>? insideValleyList = [];
  InsideOutsideValleyData? selectedInsideValley;

  List<InsideOutsideValleyData>? outsideValleyList = [];
  InsideOutsideValleyData? selectedOutsideValley;

  List<BranchesByServiceAreaDataList>? branchesByServiceAreaList = [];

  List<ServicesByServiceAreaDataList>? servicesByServiceAreaDataList = [];
  ServicesByServiceAreaDataList? selectServicesByServiceAreaData;

  BranchesByServiceAreaDataList? selectBranchesByServiceAreaData;
  List<PopDetail>? popList = [];
  PopDetail? selectedPop;
  final pincodeDropDownKey = GlobalKey<DropdownSearchState>();
  final serviceAreaDropDownKey = GlobalKey<DropdownSearchState>();
  final areaDropDownKey = GlobalKey<DropdownSearchState>();
  final subAreaDropDownKey = GlobalKey<DropdownSearchState>();
  final buildingNameDropDownKey = GlobalKey<DropdownSearchState>();
  final buildingNoDropDownKey = GlobalKey<DropdownSearchState>();
  TextEditingController fnameController = TextEditingController();
  TextEditingController lnameController = TextEditingController();
  TextEditingController contactPersonController = TextEditingController();
  TextEditingController cafNoController = TextEditingController();
  TextEditingController usernameController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController aaaUserNameController = TextEditingController();
  TextEditingController aaaPasswordController = TextEditingController();
  TextEditingController parentCustomerController = TextEditingController();

  List<int>? billDayList = [];
  int? selectedBillDay;
  TextEditingController gstController = TextEditingController();
  TextEditingController faxNumberController = TextEditingController();
  TextEditingController vatController = TextEditingController();
  TextEditingController nationalIdController = TextEditingController();
  TextEditingController passportController = TextEditingController();
  TextEditingController tinController = TextEditingController();
  TextEditingController feasibilityRemarkController = TextEditingController();

  TextEditingController mobileController = TextEditingController();
  TextEditingController secondaryMobileController = TextEditingController();
  TextEditingController telephoneController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  String countryCode = "+256";

  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();

  List<DropdownDetail>? calenderTypeList = [];
  DropdownDetail? selectedCalenderType;

  List<DropdownDetail>? feasibilityList = [];
  DropdownDetail? selectedFeasibilityData;

  List<CustomerStatusDetail>? statusList = [];
  CustomerStatusDetail? selectedStatus;

  List<DropdownDetail>? parentExperienceList = [];
  DropdownDetail? selectedParentExperience;

  //List<DropdownDetail>? parentCustomerList = [];
  ParentCustomerDetail? selectedParentCustomer;

  List<DropdownDetail>? invoiceTypeList = [];
  DropdownDetail? selectedInvoiceType;

  List<CustomerCategoryDetail>? custCategoryList = [];
  CustomerCategoryDetail? selectedCustCategory;

  List<CustomerTypeData>? custTypeList = [];
  CustomerTypeData? selectedCustType;

  List<CustomerSectorData>? custSectorList = [];
  CustomerSectorData? selectedCustSector;

  TextEditingController saleRemarkController = TextEditingController();
  TextEditingController renewPlanLimitController = TextEditingController();
  List<PartnerDetail>? partnerList = [];
  PartnerDetail? selectedPartner;

  TextEditingController amountController = TextEditingController();
  TextEditingController referenceNoController = TextEditingController();
  TextEditingController paymentDateController = TextEditingController();
  TextEditingController dobDateController = TextEditingController();
  List<PaymentModeDetail>? payModeList = [];
  PaymentModeDetail? selectedPayMode;

  TextEditingController presentAddController = TextEditingController();
  TextEditingController billableToController = TextEditingController();

//List<>? dataList;
  List<ServicesAreaDetail>? servicesAreaList = [];
  List<dynamic>? servicesAreaList11 = [];
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

  List<CountryDetail>? countryList = [];
  CountryDetail? selPresentCountry;

  bool paymentSameAs = false;
  TextEditingController paymentAddController = TextEditingController();

  List<PincodeDetail>? paymentPincodeList = [];
  PincodeDetail? selPaymentPincode;

  List<PincodeAreaDetail>? paymentAreaList = [];
  PincodeAreaDetail? selPaymentArea;

  List<CityDetail>? paymentCityList = [];
  CityDetail? selPaymentCity;

  List<StateDetail>? paymentStateList = [];
  StateDetail? selPaymentState;

  List<CountryDetail>? paymentCountryList = [];
  CountryDetail? selPaymentCountry;

  bool permanentSameAs = false;
  TextEditingController permanentAddController = TextEditingController();

  List<PincodeDetail>? permanentPincodeList = [];
  PincodeDetail? selPermanentPincode;

  List<PincodeAreaDetail>? permanentAreaList = [];
  PincodeAreaDetail? selPermanentArea;

  List<CityDetail>? permanentCityList = [];
  CityDetail? selPermanentCity;

  List<StateDetail>? permanentStateList = [];
  StateDetail? selPermanentState;

  List<CountryDetail>? permanentCountryList = [];
  CountryDetail? selPermanentCountry;

  List<DropdownDetail>? planCategoryList = [];
  DropdownDetail? selPlanCategory;

  List<DropdownDetail>? discountTypeList = [];
  DropdownDetail? selDiscountType;

  List<DropdownDetail>? parentCustTypeList = [];
  DropdownDetail? selectParentCustType;

  List<PlanGroupDetail>? planGroupList = [];
  PlanGroupDetail? selPlanGroup;

  List<BillToDetail>? billToList = [];
  BillToDetail? selectedBillTo; // 223 customer, 224 subisu

  List<PlanServiceDetail>? planServiceList = [];
  List<PlanServiceDetail>? selectedPlanServiceList = [];
  PlanServiceDetail? selPlanService;

  List<PostpaidPlanDetail>? allPlanList = [];
  List<PostpaidPlanDetail>? planList = [];
  PostpaidPlanDetail? selPlan;
  List<IndividualPlanData>? individualPlanList = [];

  List<DropdownDetail>? invoiceToOrgList = [];
  DropdownDetail? selectedInvoiceToOrg;

  List<ServiceAreaPlanPostpaidplanList>? serviceAreaAllPlanList = [];
  List<ServiceAreaPlanPostpaidplanList>? selectedServiceAreaPlanList = [];
  ServiceAreaPlanPostpaidplanList? serviceAreaPlanPostpaidData;

  List<StaffsByServiceAreaData>? staffsByServiceAreaList = [];
  StaffsByServiceAreaData? selectStaffsByServiceAreaData;

  List<PlansByPlanGroupIdPlanList>? plansByPlanGroupIdList = [];
  PlansByPlanGroupIdPlanList? selectPlanByPlanGroupList;

  List<NetworkDevicesByDeviceDataList>? oltNetworkDevicesByDeviceList = [];
  List<NetworkDevicesByDeviceDataList>? masterDBNetworkDevicesByDeviceList = [];
  List<NetworkDevicesByDeviceDataList>? splitterDBNetworkDevicesByDeviceList =
      [];
  NetworkDevicesByDeviceDataList? selectedOltNetworkDeviceList;
  NetworkDevicesByDeviceDataList? selectedMasterDBNetworkDeviceList;
  NetworkDevicesByDeviceDataList? selectedSplitterDBNetworkDeviceList;

  List<PlanGroupMappingDetail>? planGroupMappingList = [];
  PlanGroupMappingDetail? selectPlanGroupMappingData;

  List<DepartmentListData>? allDepartmentDataList = [];
  DepartmentListData? selectAllDepartmentData;

  List<SubAreaDataList>? subAreaDataList = [];
  SubAreaDataList? selectedSubAreaData;

  List<BuildingManagementDataList>? buildingManagementDataList = [];
  BuildingManagementDataList? selectedBuildingManagementData;

  List<String>? buildingNumberList = [];
  String? selectedBuildingNumber;

  List<BuildingReferenceDataList>? buildingReferenceDataList = [];
  BuildingReferenceDataList? selectedBuildingReferenceData;

  List<PinCodeAllDataList>? newPinCodeAllList = [];
  PinCodeAllDataList? selectedNewPinCodeAll;

  List<AreaAllDataList>? newAreaAllList = [];
  AreaAllDataList? selectedNewAreaAllData;

  int? idData;

  String? selectedMappingFrom = "";

  bool businessPromotionFlag = false;

  bool readOnlyDiscountPrice = true,
      showDiscountPrice = true,
      showInvoiceTag = false,
      trialPlan = false,
      isParentLocation = false,
      billToReadOnly = false;

  num offerPrice = 0, discountOfferPrice = 0;

  TextEditingController planOfferPriceController = TextEditingController();
  TextEditingController planNewPriceController = TextEditingController();
  TextEditingController planValidityController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController newOfferPricePlanController = TextEditingController();

  TextEditingController voiceServiceTypeController = TextEditingController();
  TextEditingController didNoController = TextEditingController();

  TextEditingController macAddressController = TextEditingController();

  List<String>? macAddressList = [];

  List<ChargeDetail>? chargeList = [];
  ChargeDetail? selCharge;

  // TextEditingController validityController = TextEditingController();
  // TextEditingController priceController = TextEditingController();
  TextEditingController newPriceController = TextEditingController();

  //TextEditingController chargeDateController = TextEditingController();

  List<ChargeData>? chargeDataList = [];

  LocationDetail? selectedLocation;
  LocationLatLong? locationData;

  DateTime? selectedChargeDate, selectedPaymentDate, selectedDOBDate;
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  int activeStep = 0;
  int dotCount = 14;

  // List<int> data = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];
  List<int> data = [1, 2, 3];
  bool checkBtnClickEvent = false;

  List<String> chargeTypeLst = [Strings.onetime, Strings.recurring];
  String? selectedChargeType, serviceAreaName;

  List<int> recurringMonthLst = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
  int? selectedRecurringMonth;
  List<int> blockNoOptions = [];
  int? selectedBlockNo;

  List<IndividualPlanData>? chargePlanList = [];
  IndividualPlanData? selectedChargePlan;

  TextEditingController staticIPController = TextEditingController();
  TextEditingController nasIpController = TextEditingController();
  TextEditingController frameIpController = TextEditingController();
  TextEditingController nasPort = TextEditingController();
  TextEditingController ipPoolNameController = TextEditingController();
  TextEditingController maxConcurrentController = TextEditingController();
  TextEditingController framedIP = TextEditingController();
  TextEditingController customerSectorType = TextEditingController();
  TextEditingController customerSubType = TextEditingController();

  List<CustomerSubType>? customerSubTypeList = [];
  CustomerSubType? selectedCustomerSubType;
  bool custSubTypeDDl = false;
  bool isCredentialMatchWithAccountNo = false;
  int? serviceAreaId, planServiceID, billableToCustomerId;

  List<DropdownDetail>? parentQuotaTypeList = [];
  DropdownDetail? selectParentQuotaType;

  @override
  void onInit() {
    super.onInit();
    /*fnameController.text = "Test";
    lnameController.text = "Hp";
    contactPersonController.text = "Test Hp";
    cafNoController.text = "1234";
    usernameController.text = "test.hp";
    passwordController.text = "1234";*/

    billDayList!.clear();
    for (int i = 1; i <= 31; i++) {
      billDayList!.add(i);
    }
    calenderTypeList!.clear();
    calenderTypeList!.add(DropdownDetail(
        id: Strings.english,
        text: Strings.english,
        type: Strings.calendar_type));
    calenderTypeList!.add(DropdownDetail(
        id: Strings.nepali, text: Strings.nepali, type: Strings.calendar_type));

    invoiceToOrgList!.clear();
    invoiceToOrgList!.add(DropdownDetail(
        id: Strings.yes, text: Strings.yes, type: Strings.invoice_to_org));
    invoiceToOrgList!.add(DropdownDetail(
        id: Strings.no, text: Strings.no, type: Strings.invoice_to_org));
    selectedInvoiceToOrg = invoiceToOrgList![1];
    selectedCalenderType = calenderTypeList![0];

    planCategoryList!.clear();
    planCategoryList!.add(DropdownDetail(
        id: Strings.individual,
        text: Strings.individual,
        type: Strings.plan_category));
    planCategoryList!.add(DropdownDetail(
        id: Strings.plan_group,
        text: Strings.plan_group,
        type: Strings.plan_category));

    invoiceTypeList!.clear();
    invoiceTypeList!.add(DropdownDetail(
        id: Strings.group, text: Strings.group, type: Strings.invoice_type));
    invoiceTypeList!.add(DropdownDetail(
        id: Strings.independent,
        text: Strings.independent,
        type: Strings.invoice_type));

    discountTypeList!.clear();
    discountTypeList!.add(DropdownDetail(
        id: Strings.onetime,
        text: Strings.onetime,
        type: Strings.discount_type));
    discountTypeList!.add(DropdownDetail(
        id: Strings.recurring,
        text: Strings.recurring,
        type: Strings.discount_type));

    parentCustTypeList!.clear();
    parentCustTypeList!.add(DropdownDetail(
        id: Strings.customer,
        text: Strings.customer,
        type: Strings.customer_type));
    parentCustTypeList!.add(DropdownDetail(
        id: Strings.organization,
        text: Strings.organization,
        type: Strings.customer_type));

    parentExperienceList!.clear();
    parentExperienceList!.add(DropdownDetail(
        id: Strings.single,
        text: Strings.single,
        type: Strings.parent_experience));
    parentExperienceList!.add(DropdownDetail(
        id: Strings.actual,
        text: Strings.actual,
        type: Strings.parent_experience));

    feasibilityList!.clear();
    feasibilityList!.add(DropdownDetail(
        id: Strings.na,
        text: Strings.not_service_ready_nsr,
        type: Strings.feasibility));
    feasibilityList!.add(DropdownDetail(
        id: Strings.feasibilityBooking,
        text: Strings.maintenance,
        type: Strings.feasibility));

    parentQuotaTypeList!.clear();
    parentQuotaTypeList!.add(DropdownDetail(
        id: Strings.shareable,
        text: Strings.shareable,
        type: Strings.parent_quota));
    parentQuotaTypeList!.add(DropdownDetail(
        id: Strings.individual.toLowerCase(),
        text: Strings.individual.toLowerCase(),
        type: Strings.parent_quota));

    selectedCalenderType = calenderTypeList![0];
    selectParentCustType = parentCustTypeList![0];
    selDiscountType = discountTypeList![0];
    selPlanCategory = planCategoryList![0];

    getArgumentData();
    initPlatformState();
  }

  getArgumentData() async {
    var arguments = Get.arguments;

    if (arguments != null) {
      if (arguments[Constant.ACTION] != null) {
        action = arguments[Constant.ACTION];
      }

      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
      }
      /* if (arguments[Constant.CONTACT_CODE] != null) {
        contactCode = arguments[Constant.CONTACT_CODE];
      }*/
    }

    if (businessPromotionFlag == true) {
      if (selectedBillTo!.text != null && selectedBillTo!.text!.isNotEmpty) {
        for (BillToDetail element in billToList!) {
          if (element.text!.equalsIgnoreCase("ORGANIZATION")) {
            selectedBillTo = element;
            break;
          } else if (element.text!.equalsIgnoreCase("CUSTOMER")) {
            selectedBillTo = element;
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
      userData = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
    getCustomerTitle();

    // else{
    //   List<CustomerTitle> titleList = getCustTitleList();
    //   for (var element in titleList) {
    //     bdTypeList!.add(element);
    //   }
    //   selectedBDType = bdTypeList![0];
    //   getCustomerStatus();
    // }
    // getCustomerTitle();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
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
                  bdTypeList?.addAll(responseData.dataList!.reversed);
                  selectedBDType = bdTypeList![0];
                  // getStorage.write(Constant.CUST_TITLE, jsonEncode(bdTypeList));
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
        getCustomerStatus();
      },
      onError: (ResponseModel error) {
        getCustomerStatus();
        _handleApiError(error);
      },
    );
  }

  getCustomerStatus() {
    statusList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerStatus(
      onSuccess: (ResponseModel responseModel) async {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerStatusRes responseData = CustomerStatusRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  statusList?.addAll(responseData.dataList!);
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
        getServiceArea();

        // if (getStorage.hasData(Constant.SERVICE_AREA_DATA) == false) {
        //   getServiceArea();
        // }else{
        //   List<ServicesAreaDetail> storedList = getServicesAreaList();
        //   for (var service in storedList) {
        //     servicesAreaList!.add(service);
        //   }
        // }
      },
      onError: (ResponseModel error) async {
        _handleApiError(error);
        // getValleyTypeDetail();
        getServiceArea();
        // if (getStorage.hasData(Constant.SERVICE_AREA_DATA) == false) {
        //
        // }else{
        //   List<ServicesAreaDetail> storedList = getServicesAreaList();
        //   for (var service in storedList) {
        //     servicesAreaList!.add(service);
        //   }
        // }
      },
    );
  }

  // List<ServicesAreaDetail> getServicesAreaList() {
  //   String? jsonString = getStorage.read(Constant.SERVICE_AREA_DATA); // Read stored JSON
  //
  //   if (jsonString == null || jsonString.isEmpty) {
  //     return []; // Return an empty list if data is null or empty
  //   }
  //   try {
  //     List<dynamic> jsonData = json.decode(jsonString); // Decode JSON string
  //     return jsonData.map((e) => ServicesAreaDetail.fromJson(e)).toList(); // Convert to List<ServicesAreaDetail>
  //   } catch (e) {
  //     print("Error decoding services data: $e");
  //     return []; // Return empty list if an error occurs
  //   }
  // }

  // List<CustomerTitle> getCustTitleList() {
  //   String? jsonString = getStorage.read(Constant.CUST_TITLE); // Read stored JSON
  //
  //   if (jsonString == null || jsonString.isEmpty) {
  //     return []; // Return an empty list if data is null or empty
  //   }
  //   try {
  //     List<dynamic> jsonData = json.decode(jsonString); // Decode JSON string
  //     return jsonData.map((e) => CustomerTitle.fromJson(e)).toList(); // Convert to List<ServicesAreaDetail>
  //   } catch (e) {
  //     print("Error decoding services data: $e");
  //     return []; // Return empty list if an error occurs
  //   }
  // }

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
        if (servicesAreaList == null || servicesAreaList!.isEmpty) {
          getServiceArea();
        }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        if (servicesAreaList == null || servicesAreaList!.isEmpty) {
          getServiceArea();
        }
        // getAllPop();
      },
    );
  }

  getAllPop() {
    isLoading = true;
    selectedPop = null;
    popList?.clear();
    update();
    InventoryManagementProvider().getAllPop(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewPopListRes responseData = ViewPopListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList!.isNotEmpty) {
                  popList?.addAll(responseData.dataList!);
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
        if (oltNetworkDevicesByDeviceList!.isEmpty) {
          getNetworkDevicesByDeviceTypeAPI(Strings.olt);
        }
        update();
      },
      onError: (ResponseModel error) {
        isLoading = false;
        if (oltNetworkDevicesByDeviceList!.isEmpty) {
          getNetworkDevicesByDeviceTypeAPI(Strings.olt);
        }
        _handleApiCafCustomerError(error);
      },
    );
  }

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
        _handleApiError(error);
        getCustomerType();
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
        // getCustomerSector();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getCustomerSector();
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
        if (allDepartmentDataList == null || allDepartmentDataList!.isEmpty) {
          getDepartmentListAPI();
        }
      },
      onError: (ResponseModel error) {
        if (allDepartmentDataList == null || allDepartmentDataList!.isEmpty) {
          getDepartmentListAPI();
        }
        _handleApiError(error);
      },
    );
  }

  manageCustomerSubType() {
    customerSubType.clear();
    if (selectedCustType != null &&
        selectedCustType!.value != null &&
        selectedCustType!.value!.isNotEmpty) {
      if (selectedCustType!.value!.equalsIgnoreCase("barter")) {
        custSubTypeDDl = false;
      } else {
        custSubTypeDDl = true;
      }
      update();
    }
    if (custSubTypeDDl) {
      getCustomerSubType();
    }
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
        _handleApiSubTypeError(error);
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

  getActivePartner() {
    isLoading = true;
    selectedPartner = null;
    partnerList?.clear();
    update();
    CustomerProvider().getAllPartner(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PartnerListRes responseData = PartnerListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.partnerlist != null &&
                    responseData.partnerlist!.isNotEmpty) {
                  partnerList?.addAll(responseData.partnerlist!);
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

  getPaymentMode() {
    isLoading = true;
    selectedPayMode = null;
    payModeList?.clear();
    update();
    CustomerProvider().getPaymentMode(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PaymentModeListRes responseData =
                  PaymentModeListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  payModeList?.addAll(responseData.dataList!);
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

  // getServiceArea() {
  //   isLoading = true;
  //   selPresentServiceArea = null;
  //   servicesAreaList!.clear();
  //   update();
  //   CustomerProvider().getServiceAreaData(
  //     onSuccess: (ResponseModel responseModel) async{
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 for (var element in responseData.dataList!) {
  //                   // if (element.createdById == userData!.userId) {
  //                   // log("getServiceAreaDataIf>>> ${json.encode(element)}");
  //                   servicesAreaList!.add(element);
  //                   // } else {
  //                   // var ab = json.decode(userData!.serviceAreaIdList!).toList();
  //                   // for (var serviceAreaId in ab) {
  //                   //   if (element.id == serviceAreaId) {
  //                   //     servicesAreaList!.add(element);
  //                   //   }
  //                   // }
  //                   // }
  //                 }
  //                 // getStorage.write(Constant.SERVICE_AREA_DATA, jsonEncode(servicesAreaList));
  //                 // servicesAreaList!.addAll(responseData.dtaList!);
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
  //       // getCustomerSector();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //       // getCustomerSector();
  //     },
  //   );
  // }
  getServiceArea() {
    isLoading = true;
    selPresentServiceArea = null;
    servicesAreaList!.clear();
    update();
    CustomerProvider().getNewServiceAreaCAFData(
      onSuccess: (ResponseModel responseModel) async {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  for (var element in responseData.dataList!) {
                    // if (element.createdById == userData!.userId) {
                    // log("getServiceAreaDataIf>>> ${json.encode(element)}");
                    servicesAreaList!.add(element);
                    // } else {
                    // var ab = json.decode(userData!.serviceAreaIdList!).toList();
                    // for (var serviceAreaId in ab) {
                    //   if (element.id == serviceAreaId) {
                    //     servicesAreaList!.add(element);
                    //   }
                    // }
                    // }
                  }
                  // getStorage.write(Constant.SERVICE_AREA_DATA, jsonEncode(servicesAreaList));
                  // servicesAreaList!.addAll(responseData.dtaList!);
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
        // getCustomerSector();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getCustomerSector();
      },
    );
  }

  getServiceAreaDetail(int? serviceId, bool? isCheck) {
    isLoading = true;
    blockNoOptions.clear();
    selectedBlockNo = null;
    update();
    CustomerProvider().getServiceAreaDetail(
      // id: selPresentServiceArea!.id!,
      id: serviceId!,
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
                List<int>? serviceAreaId = [];
                areaDetail = responseData.data;
                log("getServiceAreaDetail===>>>${jsonEncode(areaDetail)}");

                if (servicesAreaList!.isNotEmpty) {
                  for (var element in servicesAreaList!) {
                    if (element.id == responseData.data!.id) {
                      selPresentServiceArea = element;
                      serviceAreaId.add(selPresentServiceArea!.id!);
                    }
                  }
                  getStaffsByServiceAreaAPI(selPresentServiceArea!.id, false);
                  getAllBranchesByServiceAreaData(serviceAreaId, false);
                }

                if (areaDetail!.blockNo != null &&
                    int.tryParse(areaDetail!.blockNo!) != null) {
                  int maxBlockNo = int.parse(areaDetail!.blockNo!);
                  blockNoOptions = List<int>.generate(maxBlockNo, (i) => i + 1);
                } else {
                  blockNoOptions = [];
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
        // if (isCheck == false) {
        getPincodeData();
        // }
      },
      onError: (ResponseModel error) {
        // if (isCheck == false) {
        getPincodeData();
        // }
        _handleApiError(error);
      },
    );
  }

  getAllBranchesByServiceAreaData(List<int>? serviceAreaID, bool? isCheck) {
    isLoading = true;
    update();
    branchesByServiceAreaList!.clear();
    selectBranchesByServiceAreaData = null;
    CustomerProvider().getAllBranchesByServiceAreaId(
      serviceAreaId: serviceAreaID,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          isLoading = false;
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
          isLoading = false;
        }

        update();
        getAllServicesByServiceAreaIdData(serviceAreaID, isCheck);
      },
      onError: (ResponseModel error) {
        getAllServicesByServiceAreaIdData(serviceAreaID, isCheck);
        _handleApiError(error);
      },
    );
  }

  getPinCodeToAreaData(int id, String type) {
    //, String from
    if (type.equalsIgnoreCase("Present")) {
      selPresentArea = null;
      selPresentCity = null;
      selPresentState = null;
      selPresentCountry = null;

      areaList!.clear();
      cityList!.clear();
      stateList!.clear();
      countryList!.clear();
    }
    if (type.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentArea = null;
      selPaymentCity = null;
      selPaymentState = null;
      selPaymentCountry = null;

      paymentAreaList!.clear();
      paymentCityList!.clear();
      paymentStateList!.clear();
      paymentCountryList!.clear();
    }

    if (type.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentArea = null;
      permanentAreaList!.clear();

      selPermanentCity = null;
      permanentCityList!.clear();

      selPermanentState = null;
      permanentStateList!.clear();

      selPermanentCountry = null;
      permanentCountryList!.clear();
    }

    isLoading = true;
    newAreaAllList!.clear();
    selectedNewAreaAllData = null;
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
                  // newAreaAllList = responseData.areaList;

                  for (var element in responseData.areaList!) {
                    newAreaAllList!.add(
                        AreaAllDataList(id: element.id, name: element.name));
                  }

                  if (type.equalsIgnoreCase("Present")) {
                    areaList!.addAll(responseData.areaList!);
                  }
                  if (type.equalsIgnoreCase(Strings.payment_address_details)) {
                    paymentAreaList!.addAll(responseData.areaList!);
                  }
                  if (type
                      .equalsIgnoreCase(Strings.permanent_address_details)) {
                    permanentAreaList!.addAll(responseData.areaList!);
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

  getAreaDetail({int? subAreaId, int? areaId, String? type}) {
    if (type!.equalsIgnoreCase("Present")) {
      selPresentCity = null;
      selPresentState = null;
      selPresentCountry = null;

      cityList!.clear();
      stateList!.clear();
      countryList!.clear();
    }

    if (type.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentCity = null;
      paymentCityList!.clear();

      selPaymentState = null;
      paymentStateList!.clear();

      selPaymentCountry = null;
      paymentCountryList!.clear();
    }

    if (type.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentCity = null;
      permanentCityList!.clear();

      selPermanentState = null;
      permanentStateList!.clear();

      selPermanentCountry = null;
      permanentCountryList!.clear();
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
                  if (newAreaAllList!.isNotEmpty) {
                    for (var element in newAreaAllList!) {
                      if (element.id == responseData.data!.id) {
                        selectedNewAreaAllData = element;
                      }
                    }
                  }

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
                  if (type.equalsIgnoreCase(Strings.payment_address_details)) {
                    if (responseData.data!.cityId != null &&
                        responseData.data!.cityName != null) {
                      paymentCityList!.add(CityDetail(
                          id: responseData.data!.cityId,
                          name: responseData.data!.cityName));
                      selPaymentCity = paymentCityList![0];
                    }

                    if (responseData.data!.stateId != null &&
                        responseData.data!.stateName != null) {
                      paymentStateList!.add(StateDetail(
                          id: responseData.data!.stateId,
                          name: responseData.data!.stateName));
                      selPaymentState = paymentStateList![0];
                    }

                    if (responseData.data!.countryId != null &&
                        responseData.data!.countryName != null) {
                      paymentCountryList!.add(CountryDetail(
                          id: responseData.data!.countryId,
                          name: responseData.data!.countryName));
                      selPaymentCountry = paymentCountryList![0];
                    }
                  }

                  if (type
                      .equalsIgnoreCase(Strings.permanent_address_details)) {
                    if (responseData.data!.cityId != null &&
                        responseData.data!.cityName != null) {
                      permanentCityList!.add(CityDetail(
                          id: responseData.data!.cityId,
                          name: responseData.data!.cityName));
                      selPermanentCity = permanentCityList![0];
                    }

                    if (responseData.data!.stateId != null &&
                        responseData.data!.stateName != null) {
                      permanentStateList!.add(StateDetail(
                          id: responseData.data!.stateId,
                          name: responseData.data!.stateName));
                      selPermanentState = permanentStateList![0];
                    }

                    if (responseData.data!.countryId != null &&
                        responseData.data!.countryName != null) {
                      permanentCountryList!.add(CountryDetail(
                          id: responseData.data!.countryId,
                          name: responseData.data!.countryName));
                      selPermanentCountry = permanentCountryList![0];
                    }
                  }
                }
                if (selectedMappingFrom!.equalsIgnoreCase("Pin Code")) {
                  idData = responseData.data!.pincodeId;
                } else if (selectedMappingFrom!.equalsIgnoreCase("Area")) {
                  idData = areaId;
                } else {
                  idData = subAreaId;
                }
                getServicAreaIdByPincodeCAll(responseData.data!.pincodeId);
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

  getPinCodeToAreaList(int id, String from) {
    isLoading = true;
    if (from.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentArea = null;
      paymentAreaList!.clear();
      selPaymentCity = null;
      selPaymentState = null;
      selPaymentCountry = null;
    }
    if (from.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentArea = null;
      permanentAreaList!.clear();
      selPermanentCity = null;
      selPermanentState = null;
      selPermanentCountry = null;
    }
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
                if (responseData.data != null &&
                    responseData.data!.areaList != null &&
                    responseData.data!.areaList!.isNotEmpty) {
                  if (from.equalsIgnoreCase(Strings.payment_address_details)) {
                    paymentAreaList!.addAll(responseData.data!.areaList!);
                  }
                  if (from
                      .equalsIgnoreCase(Strings.permanent_address_details)) {
                    permanentAreaList!.addAll(responseData.data!.areaList!);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  getPincodeData() {
    isLoading = true;
    // selPresentPincode = null;
    // selPresentArea = null;
    // selPresentCity = null;
    // selPresentState = null;
    // selPresentCountry = null;
    // pincodeList!.clear();
    // areaList!.clear();
    // cityList!.clear();
    // stateList!.clear();
    // countryList!.clear();
    update();
    CustomerProvider().getPincodeData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // PincodeListRes responseData = PincodeListRes.fromJson(map);
              GetPinCodeAllRes responseData = GetPinCodeAllRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  log("areaDetail===>>>${jsonEncode(areaDetail)}");
                  newPinCodeAllList!.clear();

                  for (var element in responseData.dataList!) {
                    if (element.status != null &&
                        element.status!.equalsIgnoreCase("Active")) {
                      if (areaDetail!.pincodes != null &&
                          areaDetail!.pincodes!.isNotEmpty) {
                        for (int value in areaDetail!.pincodes!) {
                          if (element.pincodeid == value) {
                            newPinCodeAllList!.add(element);
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
                  log("areaDetail===>>>${jsonEncode(newPinCodeAllList)}");
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

  sameAsPresentAddress(String type) {
    if (type.equalsIgnoreCase(Strings.payment_address_details)) {
      paymentAddController.clear();
      selPaymentPincode = null;
      selPaymentArea = null;
      selPaymentCity = null;
      selPaymentState = null;
      selPaymentCountry = null;
      if (paymentSameAs) {
        paymentAddController.text = presentAddController.text;
        // for pincode
        for (var element in paymentPincodeList!) {
          if (element.pincodeid == selPresentPincode!.pincodeid) {
            selPaymentPincode = element;
          }
        }
        // for area
        paymentAreaList!.clear();
        paymentAreaList!.addAll(areaList!);
        selPaymentArea = selPresentArea;

        // for city
        paymentCityList!.clear();
        paymentCityList!.addAll(cityList!);
        selPaymentCity = selPresentCity;

        // for state
        paymentStateList!.clear();
        paymentStateList!.addAll(stateList!);
        selPaymentState = selPresentState;

        // for Country
        paymentCountryList!.clear();
        paymentCountryList!.addAll(countryList!);
        selPaymentCountry = selPresentCountry;
      }
    } else {
      permanentAddController.clear();
      selPermanentPincode = null;
      selPermanentArea = null;
      selPermanentCity = null;
      selPermanentState = null;
      selPermanentCountry = null;

      if (permanentSameAs) {
        permanentAddController.text = presentAddController.text;
        // for pincode
        for (var element in permanentPincodeList!) {
          if (element.pincodeid == selPresentPincode!.pincodeid) {
            selPermanentPincode = element;
          }
        }
        // for area
        permanentAreaList!.clear();
        permanentAreaList!.addAll(areaList!);
        selPermanentArea = selPresentArea;

        // for city
        permanentCityList!.clear();
        permanentCityList!.addAll(cityList!);
        selPermanentCity = selPresentCity;

        // for state
        permanentStateList!.clear();
        permanentStateList!.addAll(stateList!);
        selPermanentState = selPresentState;

        // for Country
        permanentCountryList!.clear();
        permanentCountryList!.addAll(countryList!);
        selPermanentCountry = selPresentCountry;
      }
    }
  }

  setCityData(String from) {
    if (from.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentCity = null;
      paymentCityList!.clear();
      if (cityList != null && cityList!.isNotEmpty) {
        paymentCityList!.addAll(cityList!);
        for (var element in paymentCityList!) {
          if (selPaymentArea!.cityId == element.id) {
            selPaymentCity = element;
          }
        }
      }
    }

    if (from.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentCity = null;
      permanentCityList!.clear();
      if (cityList != null && cityList!.isNotEmpty) {
        permanentCityList!.addAll(cityList!);
        for (var element in permanentCityList!) {
          if (selPermanentArea!.cityId == element.id) {
            selPermanentCity = element;
          }
        }
      }
    }
    update();
    setStateData(from);
  }

  setStateData(String from) {
    if (from.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentState = null;
      paymentStateList!.clear();
      if (stateList != null && stateList!.isNotEmpty) {
        paymentStateList!.addAll(stateList!);
        for (var element in paymentStateList!) {
          if (selPaymentArea!.stateId == element.id) {
            selPaymentState = element;
          }
        }
      }
    }
    if (from.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentState = null;
      permanentStateList!.clear();
      if (stateList != null && stateList!.isNotEmpty) {
        permanentStateList!.addAll(stateList!);
        for (var element in permanentStateList!) {
          if (selPermanentArea!.stateId == element.id) {
            selPermanentState = element;
          }
        }
      }
    }
    update();
    setCountryData(from);
  }

  setCountryData(String from) {
    if (from.equalsIgnoreCase(Strings.payment_address_details)) {
      selPaymentCountry = null;
      paymentCountryList!.clear();
      if (countryList != null && countryList!.isNotEmpty) {
        paymentCountryList!.addAll(countryList!);
        for (var element in paymentCountryList!) {
          if (selPaymentArea!.countryId == element.id) {
            selPaymentCountry = element;
          }
        }
      }
    }

    if (from.equalsIgnoreCase(Strings.permanent_address_details)) {
      selPermanentCountry = null;
      permanentCountryList!.clear();
      if (countryList != null && countryList!.isNotEmpty) {
        permanentCountryList!.addAll(countryList!);
        for (var element in permanentCountryList!) {
          if (selPermanentArea!.countryId == element.id) {
            selPermanentCountry = element;
          }
        }
      }
    }
    update();
  }

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

                  if (areaDetail != null && areaDetail!.cityId != null) {
                    for (var element in cityList!) {
                      if (element.id == areaDetail!.cityId) {
                        selPresentCity = element;
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
        if (stateList == null || stateList!.isEmpty) {
          getAllState();
        }
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

                  if (areaDetail != null && areaDetail!.stateId != null) {
                    for (var element in stateList!) {
                      if (element.id == areaDetail!.stateId) {
                        selPresentState = element;
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
        if (countryList == null || countryList!.isEmpty) {
          getAllCountry();
        }
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
                  if (areaDetail != null && areaDetail!.countryId != null) {
                    for (var element in countryList!) {
                      if (element.id == areaDetail!.countryId) {
                        selPresentCountry = element;
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
      },
      onError: (ResponseModel error) {
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
                  selectedBillTo = billToList![0];
                  for (BillToDetail element in billToList!) {
                    if (element.text!.equalsIgnoreCase("CUSTOMER")) {
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
        // getPlanGroupDetail();
      },
      onError: (ResponseModel error) {
        // getPlanGroupDetail();
        _handleApiError(error);
      },
    );
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

  manageDiscountVisibility() {
    if (selectedBillTo != null) {
      if (selectedBillTo!.id == 224) {
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
        if (element.text!.equalsIgnoreCase("ORGANIZATION")) {
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
                    oltNetworkDevicesByDeviceList!
                        .addAll(responseData.dataList!);
                  } else if (networkDeviceType
                      .equalsIgnoreCase(Strings.master_db)) {
                    masterDBNetworkDevicesByDeviceList!
                        .addAll(responseData.dataList!);
                  } else if (networkDeviceType
                      .equalsIgnoreCase(Strings.splitter_db)) {
                    splitterDBNetworkDevicesByDeviceList!
                        .addAll(responseData.dataList!);
                  }
                }
              } else if (responseData.responseCode != null &&
                  responseData.responseCode == 404) {
                // Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                //     AppTheme.colorWhite, AppTheme.colorBlueRView);
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
        _handleApiCafNetworkDevicesError(error);
      },
    );
  }

  getFindPlanGroupByIdData() {
    isLoading = true;
    update();
    ChargeManagementProvider().getFindPlanGroupById(
      cusPlanGroupId: selPlanGroup!.planGroupId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlansByPlanGroupIdRes responseData =
                  PlansByPlanGroupIdRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.planList != null &&
                    responseData.planList!.isNotEmpty) {
                  plansByPlanGroupIdList!.addAll(responseData.planList!);
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

  getPlanGroupDetail() {
    isLoading = true;
    planGroupList!.clear();
    update();
    CustomerProvider().getPlanGroup(
      planMode: '',
      onSuccess: (ResponseModel responseModel) {
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
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          // if (responseModel.message != null &&
          //     responseModel.message!.isNotEmpty) {
          //   Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
          //       AppTheme.colorWhite, AppTheme.colorRed);
          // }
        }
        isLoading = false;
        update();
        if (allPlanList == null || allPlanList!.isEmpty) {
          getPlanDetail();
        }
        // if (planServiceList == null || planServiceList!.isEmpty) {
        //   getPlanServicesDetail();
        // }
      },
      onError: (ResponseModel error) {
        isLoading = false;
        update();
        if (allPlanList == null || allPlanList!.isEmpty) {
          getPlanDetail();
        }
        // debugPrint("Request Data ==> ${error.statusCode}", wrapWidth: 1024);
        // if (planServiceList == null || planServiceList!.isEmpty) {
        //   getPlanServicesDetail();
        // }
        _handleApiError(error);
      },
    );
  }

  getPlanServicesDetail() {
    isLoading = true;
    planServiceList!.clear();
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
                  planServiceList!.addAll(responseData.serviceList!);
                  selectedPlanServiceList!.addAll(planServiceList!.where(
                      (element) =>
                          element.name!.equalsIgnoreCase(serviceAreaName!)));
                  if (selectedPlanServiceList!.isNotEmpty) {
                    planServiceID = selectedPlanServiceList![0].id;
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
        getServicePlanModeServiceAreaAPI();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getServicePlanModeServiceAreaAPI();
      },
    );
  }

  setPlanData() {
    //type Strings.prepaid
    selPlan = null;
    planList!.clear();
    if (allPlanList != null && allPlanList!.isNotEmpty) {
      for (var element in allPlanList!) {
        //serviceId , plantype
        if (element.serviceId == selPlanService!.id &&
            element.plantype!.equalsIgnoreCase(type!)) {
          planList!.add(element);
        }
      }
    }
    update();
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
      serviceAreaId: serviceAreaId,
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
                  for (var element in serviceAreaAllPlanList!) {
                    if (element.serviceId == planServiceID) {
                      if (element.planGroup!.equalsIgnoreCase("Registration") ||
                          element.planGroup!
                              .equalsIgnoreCase("Registration and Renewal")) {
                        selectedServiceAreaPlanList!.add(element);
                      }
                    }
                  }
                  if (selectedServiceAreaPlanList!.isEmpty) {
                    Utils.showSnackbar(
                        Strings.note,
                        "Plan not available for this customer type and service",
                        AppTheme.colorWhite,
                        AppTheme.colorBlueRView);
                  }
                  log("getServicePlanModeServiceAreaAPI>> ${json.encode(selectedServiceAreaPlanList)}");
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

  getAllServicesByServiceAreaIdData(List<int>? serviceAreaID, bool? isCheck) {
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
        if (isCheck == true) {
          getServiceAreaDetail(serviceAreaID![0], false);
        }
      },
      onError: (ResponseModel error) {
        if (isCheck == true) {
          getServiceAreaDetail(serviceAreaID![0], false);
        }
        _handleApiError(error);
      },
    );
  }

  getStaffsByServiceAreaAPI(int? serviceAreaId, bool? isCheck) {
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
          isLoading = false;
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
          isLoading = false;
        }
        update();
      },
      onError: (ResponseModel error) {
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
        // if (servicesAreaList == null || servicesAreaList!.isEmpty) {
        //   getServiceArea();
        // }
        getCustomerType();
        update();
      },
      onError: (ResponseModel error) {
        // if (servicesAreaList == null || servicesAreaList!.isEmpty) {
        //   getServiceArea();
        // }
        getCustomerType();
        _handleApiError(error);
      },
    );
  }

  getChargeList() {
    isLoading = true;
    selCharge = null;
    chargeList!.clear();
    update();
    CustomerProvider().getAllCharge(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ChargeListRes responseData = ChargeListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.chargelist != null &&
                    responseData.chargelist!.isNotEmpty) {
                  for (var element in responseData.chargelist!) {
                    if (element.chargetype != null &&
                        element.chargetype!
                            .equalsIgnoreCase("CUSTOMER_DIRECT")) {
                      chargeList!.add(element);
                    }
                  }
                  //chargeList!.addAll(responseData.chargelist!);
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

  checkCustomerExist() {
    isLoading = true;
    update();
    CustomerProvider().checkCustomerExist(
      username: usernameController.text,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerExistRes responseData = CustomerExistRes.fromJson(map);

              if (responseData.status == 200) {
                if (responseData.isAlreadyExists == true) {
                  Utils.showSnackbar(Strings.ERROR, "User is already exist!",
                      AppTheme.colorWhite, AppTheme.colorRed);
                } else {
                  if (activeStep < dotCount - 1) {
                    activeStep++;
                    autoValidateMode = AutovalidateMode.disabled;
                    update();
                  }
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
        getSubAreaCall();
      },
      onError: (ResponseModel error) {
        getSubAreaCall();
        _handleApiError(error);
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
        getBuildingReferenceAllCall();
      },
      onError: (ResponseModel error) {
        getBuildingReferenceAllCall();
        _handleApiError(error);
      },
    );
  }

  getBuildingReferenceAllCall() {
    buildingReferenceDataList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getBuildingReferenceAll(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BuildingReferenceRes responseData =
                  BuildingReferenceRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  buildingReferenceDataList!.addAll(responseData.dataList!);
                  selectedMappingFrom =
                      buildingReferenceDataList![0].mappingFrom;
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "Please Select First Building Reference Management",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
        getPinCodeAllCall();
      },
      onError: (ResponseModel error) {
        getPinCodeAllCall();
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
                      responseData.responseCode == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 0)) {
                if (responseData != null && responseData.dataList!.isNotEmpty) {
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

  getPinCodeAllCall() {
    isLoading = true;
    newPinCodeAllList!.clear();
    update();
    CustomerProvider().getPinCodeALl(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetPinCodeAllRes responseData = GetPinCodeAllRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  newPinCodeAllList!.addAll(responseData.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
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
        getAreaAllCall();
      },
      onError: (ResponseModel error) {
        getAreaAllCall();
        _handleApiError(error);
      },
    );
  }

  getAreaAllCall() {
    isLoading = true;
    newAreaAllList!.clear();
    update();
    CustomerProvider().getAreaAll(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAreaAllRes responseData = GetAreaAllRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  newAreaAllList!.addAll(responseData.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
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

  createCustomerApiCall() {
    dynamic phone, mobile;
    String? cafNo, didNo, feasibilityRemark;
    double? amount, discount;

    List<PlanMappingList>? planMapping = [];
    List<AddressList> addressList = [];
    List<OverChargesDetails>? overChargeList = [];
    List<CustMacMapppingList>? custMacMapppingList = [];

    if (amountController.text.isNotEmpty) {
      amount = double.parse(amountController.text);
    }
    PaymentDetails paymentDetails = PaymentDetails(
        amount: amount ?? 0.0,
        paymode: selectedPayMode != null ? selectedPayMode!.value : null,
        referenceno: referenceNoController.text.isNotEmpty
            ? referenceNoController.text
            : null,
        paymentdate: paymentDateController.text.isNotEmpty
            ? paymentDateController.text
            : null);

    if (telephoneController.text.isNotEmpty) {
      phone = int.parse(telephoneController.text);
    }

    if (mobileController.text.isNotEmpty) {
      mobile = int.parse(mobileController.text);
    }
    if (cafNoController.text.isNotEmpty) {
      // cafNo = int.parse(cafNoController.text);
      cafNo = cafNoController.text;
    } else {
      cafNo = "";
    }
    if (didNoController.text.isNotEmpty) {
      // didNo = int.parse(didNoController.text);
      didNo = didNoController.text;
    }

    if (feasibilityRemarkController.text.isNotEmpty) {
      feasibilityRemark = feasibilityRemarkController.text.trim();
    }

    if (discountController.text.isNotEmpty) {
      discount = double.parse(discountController.text);
    }

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

        planMapping.add(PlanMappingList(
            billableCustomerId: billableToCustomerId,
            planId: element.planDetail!.id,
            service: element.planService!.name,
            validity: element.planDetail!.validity,
            discount: discount,
            billTo: selectedBillTo != null ? selectedBillTo!.value : "",
            isInvoiceToOrg: selectedInvoiceToOrg != null &&
                    selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
                ? true
                : false,
            istrialplan: element.trialPlan,
            newAmount: null,
            discountType:
                selDiscountType != null ? selDiscountType!.text : "One-time",
            invoiceType: "Independent",
            serviceId: element.planDetail!.serviceId,
            offerPrice: offerAmt));
      }
    }

    if (!presentAddController.text.isNullOrEmpty()) {
      addressList.add(AddressList(
          addressType: "Present",
          landmark: presentAddController.text,
          areaId: selectedNewAreaAllData != null
              ? selectedNewAreaAllData!.id
              : null,
          pincodeId: selectedNewPinCodeAll != null
              ? selectedNewPinCodeAll!.pincodeid
              : null,
          cityId: selPresentCity != null ? selPresentCity!.id : null,
          stateId: selPresentState != null ? selPresentState!.id : null,
          countryId: selPresentCountry != null ? selPresentCountry!.id : null,
          building_mgmt_id:
              selectedBuildingManagementData?.buildingMgmtId ?? null,
          buildingNumber: selectedBuildingNumber ?? "",
          subareaId: selectedSubAreaData?.id ?? null,
          landmark1: null,
          version: "New"));
    }

    /* if (!paymentAddController.text.isNullOrEmpty()) {
      addressList.add(AddressList(
          addressType: "Payment",
          landmark: paymentAddController.text,
          areaId: selPaymentArea != null ? selPaymentArea!.id : null,
          pincodeId:
              selPaymentPincode != null ? selPaymentPincode!.pincodeid : null,
          cityId: selPaymentCity != null ? selPaymentCity!.id : null,
          stateId: selPaymentState != null ? selPaymentState!.id : null,
          countryId: selPaymentCountry != null ? selPaymentCountry!.id : null,
          version: "NEW"));
    }

    if (!permanentAddController.text.isNullOrEmpty()) {
      addressList.add(AddressList(
          addressType: "Permanent",
          landmark: permanentAddController.text,
          areaId: selPermanentArea != null ? selPermanentArea!.id : null,
          pincodeId: selPermanentPincode != null
              ? selPermanentPincode!.pincodeid
              : null,
          cityId: selPermanentCity != null ? selPermanentCity!.id : null,
          stateId: selPermanentState != null ? selPermanentState!.id : null,
          countryId:
              selPermanentCountry != null ? selPermanentCountry!.id : null,
          version: "NEW"));
    }*/

    String todayDt = apiDateFormat.format(DateTime.now());
    if (chargeDataList != null && chargeDataList!.isNotEmpty) {
      for (var element in chargeDataList!) {
        overChargeList.add(OverChargesDetails(
            type: element.chargeType,
            chargeid:
                element.chargeDetail != null ? element.chargeDetail!.id : null,
            validity: element.chargePlan != null &&
                    element.chargePlan!.planDetail != null
                ? element.chargePlan!.planDetail!.validity
                : null,
            price: element.price != null ? int.parse(element.price!) : null,
            actualprice: element.chargeDetail != null
                ? element.chargeDetail!.price
                : null,
            chargeDate: todayDt,
            planid: element.chargePlan != null &&
                    element.chargePlan!.planDetail != null
                ? element.chargePlan!.planDetail!.id
                : null,
            unitsOfValidity: element.chargePlan != null &&
                    element.chargePlan!.planDetail != null
                ? element.chargePlan!.planDetail!.unitsOfValidity
                : null));
      }
    }

    if (macAddressList != null && macAddressList!.isNotEmpty) {
      for (var element in macAddressList!) {
        custMacMapppingList.add(CustMacMapppingList(macAddress: element));
      }
    }

    String strFlatPrice = discountOfferPrice.toStringAsFixed(2);
    double flatPrice = double.parse(strFlatPrice);
    String customerArea = "";
    if (selectedValleyType != null &&
        selectedValleyType!.id == Constant.INSIDE_VALLEY &&
        selectedInsideValley != null) {
      customerArea = selectedInsideValley!.value!;
    }

    if (selectedValleyType != null &&
        selectedValleyType!.id == Constant.OUTSIDE_VALLEY &&
        selectedOutsideValley != null) {
      customerArea = selectedOutsideValley!.value!;
    }

    AddEditCustomerCafReq request = AddEditCustomerCafReq(
        username: aaaUserNameController.text.isNotEmpty
            ? aaaUserNameController.text
            : null,
        password: aaaPasswordController.text.isNotEmpty
            ? aaaPasswordController.text
            : null,
        loginUsername:
            usernameController.text.isNotEmpty ? usernameController.text : null,
        loginPassword:
            passwordController.text.isNotEmpty ? passwordController.text : null,
        firstname: fnameController.text.trim(),
        lastname: lnameController.text.trim(),
        email: emailController.text.trim(),
        title: selectedBDType != null ? selectedBDType!.value : "",
        pan: vatController.text.trim(),
        gst: gstController.text.trim().isNotEmpty
            ? gstController.text.trim()
            : "",
        aadhar: nationalIdController.text.trim().isNotEmpty
            ? nationalIdController.text.trim()
            : "",
        passportNo: passportController.text.trim().isNotEmpty
            ? passportController.text.trim()
            : "",
        tinNo: tinController.text.trim().isNotEmpty
            ? tinController.text.trim()
            : null,
        contactperson: contactPersonController.text.trim().isNotEmpty
            ? contactPersonController.text.trim()
            : "",
        failcount: 0,
        blockNo: selectedBlockNo,
        macProvision: true,
        isCredentialMatchWithAccountNo: isCredentialMatchWithAccountNo,
        custtype: type,
        custlabel: 'customer',
        phone: phone ?? "",
        mobile: mobile,
        secondaryMobile: int.tryParse(secondaryMobileController.text.trim()),
        // fax: faxNumberController.text
        //     .trim()
        //     .isNotEmpty
        //     ? int.parse(faxNumberController.text.trim())
        //     : "",
        dateOfBirth: dobDateController.text.trim().isNotEmpty
            ? dobDateController.text.trim()
            : null,
        countryCode: countryCode,
        feasibilityRemark: feasibilityRemark,
        // feasibilityRequired: selectedFeasibilityData?.text,
        feasibilityRequired: selectedFeasibilityData?.id,
        parentExperience: selectedParentExperience?.text,
        dunningType:
            selectedCustCategory != null ? selectedCustCategory!.value : "",
        dunningSubType: custSubTypeDDl
            ? selectedCustomerSubType != null
                ? selectedCustomerSubType!.value
                : ""
            : customerSubType.text,
        dunningSector:
            selectedCustSector != null ? selectedCustSector!.value : "",
        dunningSubSector: customerSectorType.text ?? "",
        cafno: cafNo ?? "",
        voicesrvtype: voiceServiceTypeController.text,
        didno: didNo ?? "",
        calendarType:
            selectedCalenderType != null ? selectedCalenderType!.text : "",
        partnerid: selectedPartner != null ? selectedPartner!.id : 1,
        salesremark: saleRemarkController.text.trim(),
        renewPlanLimit: renewPlanLimitController.text.isNotEmpty
            ? renewPlanLimitController.text.trim()
            : null,
        servicetype: "",
        serviceareaid: selPresentServiceArea?.id,
        status:
            selectedStatus != null ? selectedStatus!.value : "NewActivation",
        parentCustomerId: selectedParentCustomer?.id,
        parentQuotaType: selectParentQuotaType?.text,
        latitude: latController.text.trim(),
        longitude: longController.text.trim(),
        billTo: selectedBillTo != null ? selectedBillTo!.value : "",
        billableCustomerId: billableToCustomerId,
        isInvoiceToOrg: selectedInvoiceToOrg != null &&
                selectedInvoiceToOrg!.text!.equalsIgnoreCase(Strings.yes)
            ? true
            : false,
        istrialplan: trialPlan,
        popid: selectedPop?.id,
        // staffId: selectStaffsByServiceAreaData?.displayId,
        staffId: userData?.userId,
        discount: discount ?? 0,
        flatAmount: flatPrice.toString(),
        plangroupid: selPlanGroup?.planGroupId.toString(),
        discountType: null,
        discountExpiryDate: null,
        planMappingList: planMapping,
        addressList: addressList,
        overChargeList: overChargeList,
        custMacMapppingList: custMacMapppingList,
        branch: selectBranchesByServiceAreaData != null
            ? selectBranchesByServiceAreaData!.id
            : 0,
        oltid: selectedOltNetworkDeviceList?.id,
        masterdbid: selectedMasterDBNetworkDeviceList?.id,
        splitterid: selectedSplitterDBNetworkDeviceList?.id,
        nasPort: nasPort.text.trim().isNotEmpty ? nasPort.text.trim() : null,
        // framedIp: framedIP.text.isNotEmpty ? framedIP.text : null,
        framedIp: frameIpController.text.trim().isNotEmpty
            ? frameIpController.text.trim()
            : null,
        framedIpv6Address: ipPoolNameController.text.trim().isNotEmpty
            ? ipPoolNameController.text.trim()
            : null,
        // ipPoolNameBind: ipPoolNameController.text.trim().isNotEmpty
        //     ? ipPoolNameController.text.trim()
        //     : null,
        nasIpAddress: nasIpController.text.trim().isNotEmpty
            ? nasIpController.text.trim()
            : "",
        VLANID: staticIPController.text.trim().isNotEmpty
            ? staticIPController.text.trim()
            : null,
        maxconcurrentsession: maxConcurrentController.text.trim().isNotEmpty
            ? maxConcurrentController.text.trim()
            : null,
        valleyType: selectedValleyType?.value,
        customerArea: customerArea.isNotEmpty ? customerArea : null,
        // customerArea: "City",
        paymentDetails: paymentDetails,
        isCustCaf: "yes",
        dunningCategory: selectedCustCategory?.value,
        billday: selectedBillDay,
        department: selectAllDepartmentData?.name,
        invoiceType: selectedInvoiceType?.text,
        planPurchaseType: selPlanCategory?.text!.toLowerCase(),
        isParentLocation: isParentLocation);

    log("Request Data ==> ${jsonEncode(request)}");

    /* debugPrint("Request Data ==> ${jsonEncode(request)}");
    return;*/
    isLoading = true;
    update();
    CustomerProvider().addCafCustomerRequest(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  Get.snackbar(
                    "Success",
                    "CAF Created Successfully.",
                    backgroundColor: Colors.green,
                    colorText: Colors.white,
                    snackPosition: SnackPosition.BOTTOM,
                  );
                  Get.offAllNamed(AppRoutes.DASHBOARD);
                });
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
      },
      onError: (ResponseModel error) {
        _handleApiCafCustomerError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiSubTypeError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiCafCustomerError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.message != null && error.message!.isNotEmpty) {
      Utils.showSnackbar(Strings.ERROR, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    } else if (error.statusCode == Constant.CODE_NO_TRY_CATCH) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_data_found,
          AppTheme.colorWhite, AppTheme.colorBlueRView);
    }
    update();
  }

  _handleApiCafNetworkDevicesError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  getAreaIdFromSubAreaIdCall(int? subAreaId, String? type) {
    isLoading = true;
    update();
    CustomerProvider().getAreaIdFromSubAreaId(
      subAreaId: subAreaId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SimpleDataRes responseData = SimpleDataRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null) {
                  getAreaDetail(
                      subAreaId: subAreaId,
                      areaId: responseData.data,
                      type: type!);
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

  getServicAreaIdByPincodeCAll(int? pinCodeId) {
    isLoading = true;
    // selPresentPincode = null;
    // selPresentArea = null;
    // selPresentCity = null;
    // selPresentState = null;
    // selPresentCountry = null;
    // pincodeList!.clear();
    // areaList!.clear();
    // cityList!.clear();
    // stateList!.clear();
    // countryList!.clear();
    update();
    CustomerProvider().getServicAreaIdByPincode(
      pinCodeId: pinCodeId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SimpleDataRes responseData = SimpleDataRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.data != null) {
                  serviceAreaId = responseData.data;
                  if (newPinCodeAllList!.isNotEmpty) {
                    for (var element in newPinCodeAllList!) {
                      if (element.pincodeid == responseData.data) {
                        selectedNewPinCodeAll = element;
                      }
                    }
                  }
                  getServiceAreaDetail(serviceAreaId, true);
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
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();

        getBuildingMgmtCall();
        /*  if (cityList == null || cityList!.isEmpty) {
          getAllCity();
        }*/
      },
      onError: (ResponseModel error) {
        getBuildingMgmtCall();
        _handleApiError(error);
      },
    );
  }

  // onChangeBuildingArea(String? value) {
  //   buildingNumberList!.clear();
  //   buildingNumberList!.addAll(value);
  // }

  getBuildingMgmtCall() {
    isLoading = true;
    update();
    CustomerProvider().getBuildingMgmt(
      entityId: idData,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }
}
