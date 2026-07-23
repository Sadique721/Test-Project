import 'dart:convert';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/payment_owner_list_resp.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_inventory/request/external_inventory_plan_req.dart';
import 'package:savbill/pages/customer_inventory/response/all_plan_inventory_plan_res.dart';
import 'package:savbill/pages/customer_inventory/response/external_inv_product_customer_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_externalItem_product_staff_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_item_based_on_product_type_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_mac_mapping_external_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_by_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_category_by_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_mapping_details_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class ExternalInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  TextEditingController connectionNumberController = TextEditingController();
  TextEditingController planCategoryController = TextEditingController();
  TextEditingController productCategoryController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController oldOfferPriceController = TextEditingController();
  TextEditingController newOfferPriceController = TextEditingController();
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController assignMacController = TextEditingController();
  DateTime? selectedInwordDateTime;
  String inwardDateTime = "";
  ParentCustomerDetail? selectedParentCustomer;
  String? productByPlanId, productByPlanGroupId,customerType;
  int? productId = 0,externalItemId=0;
  int? qtyValue,billableCustomerId;
  String? selectProductUnit;
  String? selectServices;
  bool? serviceVisible = false,
      productCategoryFlag = false,
      productPlanVisible = false,
     externalItemGroupVisible = false,
      macNoAndSerialNoFlag = false,
      hasMacFlag=false,
      hasSerialFlag = false,
      billToDiscountFlag = false,
      oldOfferAndNewOfferPriceFlag= false;
  int availableQtyPics = 0;
  int? serviceAreaId = 0;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;
  List<ExternalInvProductsDataList>? activePlanList = [];
  ExternalInvProductsDataList? selectedPlanDetail;

  List<GetAllExternalItemProductDataList>? externalItemProductList = [];
  GetAllExternalItemProductDataList? selectedExternalItemProductDetails;

  List<MACMappingExternalData>? externalMacMappingExternalList = [];
  MACMappingExternalData? externalMacMappingExternalDetails;

  List<MACMappingExternalData>? selectExternalMacExternalList = [];
  // MACMappingExternalData? selectedExternalMacDetails;

  List<ProductCategoryDataList>? productCategoryDataList = [];
  ProductCategoryDataList? selectProductCategoryList;

  List<ProductByPlanDataList>? productByPlanDataList = [];
  ProductByPlanDataList? selectProductByPlanDataList;

  List<ProductMacDataList>? productMacAddressList = [];
  ProductMacDataList? productMacAddressData;

  List<InwardsDetail>? inwardList = [];
  InwardsDetail? selectedInward;

  List<PaymentOwnerDataList> paymentOwnerList = [];
  PaymentOwnerDataList? paymentOwnerData;

  List<AllPlanInventoryIdOnPlan> getAllPlanInventoryIdOnPlanList = [];
  AllPlanInventoryIdOnPlan? selectAllPlanInventoryIdOnPlan;

  List<DropdownDetail>? itemCondition = [];
  DropdownDetail? selectedItemCondition;

  // List<DropdownDetail>? statusList = [];
  // DropdownDetail? selectedStatus;
  int customerId = 0;
  int? paymentOwnerId = 0,productItemId;
  DropdownDetail? selectedItemType;

  List<DropdownDetail>? billToList = [];
  DropdownDetail? selectedBillToDetail;

  List<ProductTypDataList>? productTypeDataList = [];
  ProductTypDataList? selectProductTypeData;

  List<ProductMappingDataList>? productMappingDataList = [];
  ProductMappingDataList? selectProductMappingData;

  List<DropdownDetail>? conditionTypeList = [];
  DropdownDetail? selectedConditionType;

  List<ProductTypDataList>? selectedSerialNumberList = [];

  AssignInventoryReq? assignInventoryReq;

  DateFormat dateFormat = DateFormat(Constant.DATE_TIME_FORMAT);
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API);

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiTimeFormat = DateFormat(Constant.API_DATE_FORMAT);

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
    DateTime now = DateTime.now();
    outwardDateController.text = dateFormat.format(now);
    inwardDateTime = apiDateTimeFormat.format(now);



    // statusList?.clear();
    // statusList?.add(DropdownDetail(
    //     id: Strings.active.toUpperCase(),
    //     text: Strings.active,
    //     type: Strings.status));
    // statusList?.add(DropdownDetail(
    //     id: Strings.in_active.toUpperCase(),
    //     text: Strings.in_active,
    //     type: Strings.status));



  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
      if (arguments[Constant.SERVICE_AREA_ID] != null) {
        serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      }
      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        customerType = arguments[Constant.CUSTOMER_TYPE];
      }
    }
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
    getPlanServiceData();
  }

  getPlanServiceData() {
    selectedPlanService = null;
    planServiceList!.clear();
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
              if (responseData.responseCode == 200|| responseData.responseCode==0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  planServiceList?.addAll(responseData.dataList!);
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
        // getActiveProductData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getActiveProductData();
      },
    );
  }

  getProductByCustomerOwnerData() {
    selectedPlanDetail = null;
    selectedExternalItemProductDetails =null;
    activePlanList!.clear();
    externalItemProductList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getExternalItemGroupByCustomer(
      customerId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ExternalInvProductsCustomerRes responseData =
              ExternalInvProductsCustomerRes.fromJson(map);

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  macNoAndSerialNoFlag = false;
                  activePlanList?.addAll(responseData.dataList!);
                }
              }else if (responseData.responseCode == 404){
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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
        // getPaymentOwnerDataApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getPaymentOwnerDataApi();
      },
    );
  }

  getAllExternalItemProductStaffCall(int? productId) {
    selectedExternalItemProductDetails = null;
    externalItemProductList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getAllExternalItemProductAndStaff(
      customerId: customerId,
      productId: productId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllExternalItemProductRes responseData =
              GetAllExternalItemProductRes.fromJson(map);

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  externalItemProductList?.addAll(responseData.dataList!);
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

  getAllMACMappingByExternalIdCall(int? externalId) {
    externalMacMappingExternalDetails = null;
    externalMacMappingExternalList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getAllMACMappingByExternalId(
      externalId: externalId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              MACMappingExternalRes responseData =
              MACMappingExternalRes.fromJson(map);

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  externalMacMappingExternalList?.addAll(responseData.dataList!);
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

  assignPlanInventoryByPlanCallApi() {
    String strQty = "1";
    int qty = 0;
    if (strQty.isNotEmpty) {
      qty = int.parse(strQty);
    }
    ExternalInventoryPlanReq request = ExternalInventoryPlanReq(
        id: "",
        qty: qty.toString(),
        productId: productId,
        serviceId: selectedPlanService!.serviceId,
        customerId: customerId,
        staffId: userDetail!.userId.toString(),
        assignedDateTime: inwardDateTime,
        // status: selectedStatus!.id,
        externalItemId: externalItemId,
        itemId: productItemId,
        connectionNo: connectionNumberController.text,
        inOutWardMACMapping: selectExternalMacExternalList
    );
    isLoading = true;
    update();
    CustomerProvider().externalPlanInventoryByPlanCall(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
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