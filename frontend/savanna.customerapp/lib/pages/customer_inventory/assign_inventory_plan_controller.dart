import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/payment_owner_list_resp.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/update_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/request/assign_plan_inventory_by_plan_req.dart';
import 'package:savbill/pages/customer_inventory/response/all_plan_inventory_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_item_based_on_product_type_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_by_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_category_by_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_mapping_details_res.dart';
import 'package:savbill/pages/dashboard/model/response/plan_detail_response.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';
import '../customer_plan/customer_plan_provider.dart';

class AssignInventoryPlanController extends GetxController {
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
  TextEditingController macAddressController = TextEditingController();
  TextEditingController serialNumberController = TextEditingController();
  TextEditingController billToController = TextEditingController();
  TextEditingController paymentOwnerStaffController = TextEditingController();
  DateTime? selectedInwordDateTime;
  String inwardDateTime = "";
  ParentCustomerDetail? selectedParentCustomer;
  ParentStaffUserlist? selectedParentStaff;
  String? productByPlanId, productByPlanGroupId,customerType;
  int? productId = 0;
  int page = 1;
  int? qtyValue,billableCustomerId;
  String? selectProductUnit;
  String? selectServices, connectionNumber;
  bool? serviceVisible = false,
      productCategoryFlag = false,
      productPlanVisible = false,
      planInventoryVisible = false,
      macNoAndSerialNoFlag = false,
      hasMacFlag=false,
      hasSerialFlag = false,
      billToDiscountFlag = false,
      oldOfferAndNewOfferPriceFlag= false;
  int availableQtyPics = 0;
  int? serviceAreaId = 0;
  int? productPlanMappingId;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;
  List<PlanDetail>? activePlanList = [];
  PlanDetail? selectedPlanDetail;

  List<ProductCategoryDataList>? productCategoryDataList = [];
  ProductCategoryDataList? selectProductCategoryList;

  List<ProductByPlanDataList>? productByPlanDataList = [];
  ProductByPlanDataList? selectProductByPlanDataList;

  List<ProductMacDataList>? productMacAddressList = [];
  ProductMacDataList? productMacAddressData;

  // List<InwardsDetail>? inwardList = [];
  // InwardsDetail? selectedInward;

  List<PaymentOwnerDataList> paymentOwnerList = [];
  PaymentOwnerDataList? paymentOwnerData;

  List<AllPlanInventoryDataList> getAllPlanInventoryIdOnPlanList = [];
  AllPlanInventoryDataList? selectAllPlanInventoryIdOnPlan;

  List<DropdownDetail>? itemCondition = [];
  DropdownDetail? selectedItemCondition;

  List<DropdownDetail>? assemblyType = [];
  DropdownDetail? selectedAssemblyType;

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

  List<ProductTypDataList>? selectedMacAddressList = [];

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

    itemCondition?.clear();
    itemCondition?.add(DropdownDetail(
        id: Strings.key_new,
        text: Strings.key_new,
        type: Strings.item_condition));
    itemCondition?.add(DropdownDetail(
        id: Strings.refurbished,
        text: Strings.refurbished,
        type: Strings.item_condition));



    assemblyType?.clear();
    assemblyType?.add(DropdownDetail(
        id: Strings.single_item_type,
        text: Strings.single_item_type,
        type: Strings.assembly_type));
    assemblyType?.add(DropdownDetail(
        id: Strings.pair_item_type,
        text: Strings.pair_item_type,
        type: Strings.assembly_type));


    // statusList?.clear();
    // statusList?.add(DropdownDetail(
    //     id: Strings.active.toUpperCase(),
    //     text: Strings.active,
    //     type: Strings.status));
    // statusList?.add(DropdownDetail(
    //     id: Strings.in_active.toUpperCase(),
    //     text: Strings.in_active,
    //     type: Strings.status));

    billToList?.clear();
    billToList?.add(DropdownDetail(
        id: Strings.subisu.toUpperCase(),
        text: Strings.subisu.toUpperCase(),
        type: Strings.bill_to));
    billToList?.add(DropdownDetail(
        id: Strings.customer.toUpperCase(),
        text: Strings.customer.toUpperCase(),
        type: Strings.bill_to));


    conditionTypeList?.clear();
    conditionTypeList?.add(DropdownDetail(
      id: Strings.key_new,
      text: Strings.key_new,
      type: Strings.condition_type,
    ));
    conditionTypeList?.add(DropdownDetail(
      id: Strings.refurbished,
      text: Strings.refurbished,
      type: Strings.condition_type,
    ));

    discountController.text = '0.0';
    oldOfferPriceController.text = '0.0';
    newOfferPriceController.text = '0.0';
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

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
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
        // getPaymentOwnerDataApi();
        // getPaymentOwnerStaffDataApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getPaymentOwnerDataApi();
        // getPaymentOwnerStaffDataApi();
      },
    );
  }

  getActivePlanListData(int? serviceId) {
    String apiUrl = UrlConstants.get_active_plan;
    apiUrl = "${apiUrl}/${customerId.toString()}?serviceId=$serviceId";
    selectedPlanDetail = null;
    activePlanList!.clear();
    isLoading = true;
    update();
    CustomerPlanProvider().getActivePlanList(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanDetailResponse responseData =
                  PlanDetailResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  activePlanList?.clear();
                  activePlanList?.addAll(responseData.dataList!.reversed);


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
        // if (isCallAllApi) {
        //   getFuturePlanListData();
        // }
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // if (isCallAllApi) {
        //   getFuturePlanListData();
        // }
      },
    );
  }

  getAllPlanInventoryIdOnPlanId(int? planId) {
    isLoading = true;
    getAllPlanInventoryIdOnPlanList.clear();
    selectAllPlanInventoryIdOnPlan = null;
    update();
    CustomerProvider().getAllPlanInventoryIdApi(
      planId: planId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AllPlanInventoryPlanIdRes responseData =
              AllPlanInventoryPlanIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  getAllPlanInventoryIdOnPlanList.clear();
                  getAllPlanInventoryIdOnPlanList.addAll(responseData.dataList!);
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

  getItemBasedOnProductTypeApiCall({required String? ownerId, required String? productId, required String? productCategoryId}) {
    isLoading = true;
    productTypeDataList!.clear();
    selectProductTypeData = null;
    update();
    String productPlanGroupId = "";

    if (productByPlanGroupId != null) {
      productPlanGroupId = productByPlanGroupId!;
    } else {
      productPlanGroupId = "";
    }
    CustomerProvider().getItemBasedOnProductType(
      ownerId: ownerId,
      planId: productByPlanId.toString(),
      planGroupId: productPlanGroupId,
      productId: productId,
      productCategoryId: productCategoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetItemBasedOnProductTypeRes responseData =
                  GetItemBasedOnProductTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  macNoAndSerialNoFlag = true;
                  billToDiscountFlag = true;
                  oldOfferAndNewOfferPriceFlag = true;
                  productTypeDataList?.clear();
                  productTypeDataList?.addAll(responseData.dataList!);

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
        getProductMappingDetailsApiCall(productId: productId, productCategoryId: productCategoryId,);
        update();
      },
      onError: (ResponseModel error) {
        getProductMappingDetailsApiCall(productId: productId, productCategoryId: productCategoryId);
        _handleApiError(error);
      },
    );
  }

  getProductMappingDetailsApiCall({required String? productId, required String? productCategoryId }) {
    isLoading = true;
    productMappingDataList!.clear();
    selectProductMappingData = null;
    update();
    String productPlanGroupId = "";

    if (productByPlanGroupId != null) {
      productPlanGroupId = productByPlanGroupId!;
    } else {
      productPlanGroupId = "";
    }
    CustomerProvider().getProductMappingDetailsCall(
      planId: productByPlanId.toString(),
      planGroupId: productPlanGroupId,
      productId: productId,
      productCategoryId: productCategoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetProductMappingDetailsRes responseData =
              GetProductMappingDetailsRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productMappingDataList?.clear();
                  productMappingDataList?.addAll(responseData.dataList!);
                  oldOfferPriceController.text = responseData.dataList![0].revisedCharge!.toString();
                  newOfferPriceController.text = responseData.dataList![0].revisedCharge!.toString();
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

  getProductCategoryByPlanIdApi(int? categoryId) {
    isLoading = true;
    productCategoryDataList!.clear();
    selectProductCategoryList = null;
    update();
    CustomerProvider().getProductCategoryByPlanIdCall(
      categoryId: categoryId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetProductCategoryByPlanIdRes responseData =
                  GetProductCategoryByPlanIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productCategoryDataList?.clear();
                  productCategoryDataList?.addAll(responseData.dataList!);
                  productCategoryController.text =
                      productCategoryDataList![0].name!;
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
        //getOutwardsData();
        // getPaymentOwnerDataApi();
        getProductByPlanIdApiCall(categoryId);
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getOutwardsData();
        // getPaymentOwnerDataApi();
        getProductByPlanIdApiCall(categoryId);
      },
    );
  }

  getProductByPlanIdApiCall(int? mappingId) {
    isLoading = true;
    productByPlanDataList!.clear();
    selectProductByPlanDataList = null;
    update();
    CustomerProvider().getProductByPlanIdCall(
      mappingId: mappingId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetProductByPlanIdRes responseData =
                  GetProductByPlanIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productByPlanDataList?.clear();
                  productByPlanDataList?.addAll(responseData.dataList!);
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

  // assignInventory() {
  //   String strQty = qtyPicsController.text;
  //   int qty = 0;
  //   if (strQty.isNotEmpty) {
  //     qty = int.parse(strQty);
  //   }
  //   assignInventoryReq = AssignInventoryReq(
  //       id: null,
  //       qty: qty,
  //       connectionNo: connectionNumberController.text,
  //       productId: productId,
  //       customerId: selectedPlanService!.custId,
  //       serviceId: selectedPlanService!.serviceId,
  //       custPackId: null,
  //       staffId: userDetail!.userId,
  //       inwardId: null,
  //       itemId: productId,
  //       itemTypeFlag: selectedItemType!.text,
  //       assignedDateTime: inwardDateTime,
  //       status: selectedStatus!.id,
  //       paymentOwnerId: paymentOwnerId,
  //       nonSerializedQty: qty,
  //       billTo: selectedBillToDetail!.text,
  //       discount: discountController.text,
  //       isRequiredApproval: false,
  //       isFree: false); //selectedMacList
  //   isLoading = true;
  //   update();
  //   InventoryProvider().assignNonSerializedItemToCustomerApi(
  //     request: assignInventoryReq,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             BaseResponse responseData = BaseResponse.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               Get.back(result: true);
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
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  assignPlanInventoryByPlanCallApi() {

    AssignPlanInventoryByPlanReq request = AssignPlanInventoryByPlanReq(
        id: "",
        qty: 1,
        productId: productId,
        serviceId: selectedPlanService!.serviceId,
        customerId: customerId,
        staffId: userDetail!.userId.toString(),
        assignedDateTime: inwardDateTime,
        // status: selectedStatus!.id,
        paymentOwnerId: paymentOwnerId,
        itemId: productItemId,
        itemType: selectedItemCondition!.text,
        connectionNo: connectionNumberController.text,
        planId: int.parse(productByPlanId.toString()),
        billTo: billToController.text.toString(),
        offerPrice: int.parse(oldOfferPriceController.text.toString()),
        newAmount: int.parse(newOfferPriceController.text.toString()),
        productPlanMappingId: productPlanMappingId,
        billabecustId: billableCustomerId,
        itemAssemblyflag: false,
        inOutWardMACMapping: selectedMacAddressList); //selectedMacList
    isLoading = true;
    update();
    log("AssignPlanInventoryByPlanReq===>>>> ${json.encode(request)}");
    isLoading = false;
    CustomerProvider().assignPlanInventoryByPlanCall(
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

  updateMacAndSerialNumber(
      int? itemId, String? serialNumber, String? macAddress) {
    isLoading = true;
    update();
    UpdateMacSerialReq request = UpdateMacSerialReq(
        itemId: itemId, macAddress: macAddress, serialNumber: serialNumber);
    CustomerProvider().updateMacSerial(
      updateMacSerialReq: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              UpdateMacSerialRes responseData =
                  UpdateMacSerialRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.successfully,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorAccent);
                update();
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }


  /// Get Staff User by Service Area Id
  getPaymentOwnerDataApi() {
    paymentOwnerList.clear();
    paymentOwnerData = null;
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


