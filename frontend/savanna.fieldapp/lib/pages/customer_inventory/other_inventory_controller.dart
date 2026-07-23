import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/other_inventory_assign_req.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/parent_customer_res.dart';
import 'package:savbill/pages/customer/model/response/parent_staff_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/product_plan_service_inventory_res.dart';
import 'package:savbill/pages/customer/model/response/update_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/response/all_inventory_spec_by_item_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_non_trackable_product_qty_res.dart';
import 'package:savbill/pages/customer_inventory/response/product_non_trackable_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/task_management/model/request/cust_inventory_new_req.dart';
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
import 'package:intl/intl.dart';

import '../../webservices/url_constants.dart';
import '../customer/model/response/inventory_job_type.dart';
import '../customer/model/response/nature.dart';

class OtherInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  ScrollController? controller;
  bool isShowLoadMore = false;
  int page = 1;

  TextEditingController connectionNumberController = TextEditingController();

  TextEditingController qtyPicsController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController oldOfferPriceController = TextEditingController();
  TextEditingController newOfferPriceController = TextEditingController();
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController assignMacController = TextEditingController();
  TextEditingController macAddressController = TextEditingController();
  TextEditingController serialNumberController = TextEditingController();
  TextEditingController billableToController = TextEditingController();
  TextEditingController paymentOwnerStaffController = TextEditingController();
  TextEditingController searchController = TextEditingController();
  DateTime? selectedInwordDateTime;
  String inwardDateTime = "";
  ParentCustomerDetail? selectedParentCustomer;
  ParentStaffUserlist? selectedParentStaff;
  int? productId = 0, billableCustomerId;
  int? qtyValue;
  String? selectProductUnit, getLatLongToAddress;

  String? selectServices, connectionNumber, customerType, customerFirstName;

  bool? serviceVisible = false,
      serializedItemVisible,
      productItemVisible = false,
      nonSerializedQtyVisible = false,
      assemblyOldNewOfferPriceFlag = false,
      latLongFlag = false,
      oldOfferAndNewOfferPriceFlag = false;

  var availableQtyPics = 0;
  int? serviceAreaId = 0, macItemId;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  List<ProductInventoryServiceList>? productPlanServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;
  ProductInventoryServiceList? selectedProductPlanService;

  CustomerPlanServiceDetail? selectedConnection;

  List<ProductDetail>? productList = [];
  List<ProductTrackableDataList>? productTrackableDataList = [];
  ProductTrackableDataList? selectedProductTrackableDataService;
  ProductDetail? selectedProduct;

  List<NonTrackableProductDataList> nonTrackableProductDataList = [];
  NonTrackableProductDataList? selectNonTrackableProductData;
  int? indexValue = 0;

  List<InwardsDetail>? inwardList = [];
  InwardsDetail? selectedInward;

  ProductMacAddressDataRes? productMacAddressDataRes;
  List<ProductMacDataList>? productMacAddressList = [];
  List<ProductMacDataList>? productMacAddressNewList = [];
  ProductMacDataList? productMacAddressData;

  List<AllInventorySpecByItemDataList>? allInventorySpecByItemIdDataList = [];
  List<AllInventorySpecByItemDataList>? allInventorySpecByItemIdList = [];
  AllInventorySpecByItemDataList? selectAllInventorySpecByItemIdData;

  int customerId = 0;
  int? paymentOwnerId = 0;

  List<InventoryTypeDataList> inventoryTypeList = [];
  InventoryTypeDataList? selectedInventoryType;

  List<NatureDataList> natureList = [];
  NatureDataList? selectedNature;

  List<DropdownDetail>? itemTypeList = [];
  DropdownDetail? selectedItemType;

  List<DropdownDetail>? billToList = [];
  DropdownDetail? selectedBillToDetail;

  List<DropdownDetail>? invoiceToOrgList = [];
  DropdownDetail? selectedInvoiceToOrg;

  List<DropdownDetail>? assemblyTypeList = [];
  DropdownDetail? selectedAssemblyType;

  List<DropdownDetail>? conditionTypeList = [];
  DropdownDetail? selectedConditionType;

  List<InwardMacMapDetail>? inventoryMacList = [];
  List<InwardMacMapDetail>? selectedMacList = [];
  List<ProductMacDataList>? selectedMacAddressList = [];

  OtherInventoryAssignReq? assignInventoryReq;

  DateFormat dateFormat = DateFormat(Constant.DATE_TIME_FORMAT);

  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API);

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  DateFormat apiTimeFormat = DateFormat(Constant.API_DATE_FORMAT);

  bool? productConditionType, checkBtnClickEvent = false;

  TextEditingController latLonController = TextEditingController();

  List<CustInvParams>? custInvParams = [];

  List<String>? selectedSerialNumber = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
    // getProductMacAddressData(selectedProductPlanService!.id);
    // update();
    //
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (productMacAddressDataRes != null &&
            productMacAddressDataRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getProductMacAddressData(selectedProductPlanService!.id);
        }
      }
    });

    DateTime now = DateTime.now();
    outwardDateController.text = dateFormat.format(now);
    inwardDateTime = apiDateTimeFormat.format(now);

    billToList?.clear();
    billToList?.add(DropdownDetail(
        id: Strings.subisu.toUpperCase(),
        text: Strings.subisu.toUpperCase(),
        type: Strings.bill_to));
    billToList?.add(DropdownDetail(
        id: Strings.customer.toUpperCase(),
        text: Strings.customer.toUpperCase(),
        type: Strings.bill_to));

    invoiceToOrgList?.clear();
    invoiceToOrgList?.add(DropdownDetail(
        id: Strings.yes.toUpperCase(),
        text: Strings.yes.toUpperCase(),
        type: Strings.invoice_to_org));
    invoiceToOrgList?.add(DropdownDetail(
        id: Strings.no.toUpperCase(),
        text: Strings.no.toUpperCase(),
        type: Strings.invoice_to_org));

    itemTypeList?.clear();
    itemTypeList?.add(DropdownDetail(
        id: Strings.serialized_item,
        text: Strings.serialized_item,
        type: Strings.item_type));
    itemTypeList?.add(DropdownDetail(
        id: Strings.non_serialized_item,
        text: Strings.non_serialized_item,
        type: Strings.item_type));

    assemblyTypeList?.clear();
    assemblyTypeList?.add(DropdownDetail(
        id: Strings.single_item_type,
        text: Strings.single_item_type,
        type: Strings.assembly_type));
    assemblyTypeList?.add(DropdownDetail(
        id: Strings.pair_item_type,
        text: Strings.pair_item_type,
        type: Strings.assembly_type));

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

    for (DropdownDetail element in billToList!) {
      if (element.id!.equalsIgnoreCase("customer")) {
        selectedBillToDetail = element;
        break;
      }
    }
  }

  getArgumentData() async {
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
      if (arguments[Constant.CUSTOMER_FIRST_NAME] != null) {
        customerFirstName = arguments[Constant.CUSTOMER_FIRST_NAME];
      }
    }

    if (customerFirstName != null && customerId != null) {
      paymentOwnerStaffController.text = customerFirstName!;
      paymentOwnerId = customerId;
    }
    String strUserData = "";

    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (strUserData.trim().isNotEmpty) {
      final userDetail = UserDetail.fromJson(jsonDecode(strUserData));

      final fullName = userDetail.fullName?.trim() ?? "";
      final firstName = fullName.isNotEmpty ? fullName.split(" ").first : "";

      paymentOwnerStaffController.text = firstName;
      paymentOwnerId = userDetail.userId;
      latLongFlag = false;
      update();
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
    getInventoryType();
    getNature();
  }

  clearData() {
    searchController.clear();
    productMacAddressList!.clear();
    productMacAddressList!.addAll(productMacAddressNewList!);
    // inwardMacMapList!.addAll(inwardMacMapNewList!);
    update();
  }

  searchData(String value) {
    productMacAddressList!.clear();
    if (value.isEmpty) {
      productMacAddressList!.addAll(productMacAddressNewList!);
    } else {
      // for (InwardMacMapDetail detail in inwardMacMapListOrg!) {
      for (ProductMacDataList detail in productMacAddressNewList!) {
        if (detail.serialNumber!.containsIgnoreCase(value)) {
          productMacAddressList!.add(detail);
        } else if (detail.itemId.toString().containsIgnoreCase(value)) {
          productMacAddressList!.add(detail);
        } else if (detail.macAddress!.containsIgnoreCase(value)) {
          productMacAddressList!.add(detail);
        } else if (detail.condition!.containsIgnoreCase(value)) {
          productMacAddressList!.add(detail);
        }
      }
    }
    update();
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
        getActiveProductData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getActiveProductData();
      },
    );
  }

  getActiveProductData() {
    isLoading = true;
    update();
    CustomerProvider().getActiveProductList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ActiveProductRes responseData = ActiveProductRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productList?.clear();
                  productList?.addAll(responseData.dataList!);
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getOutwardsData();
      },
    );
  }

  getInventoryType() {
    isLoading = true;
    update();
    CustomerProvider().getInventoryType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryJobType responseData = InventoryJobType.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inventoryTypeList?.clear();
                  inventoryTypeList?.addAll(responseData.dataList!);
                  for (var item in inventoryTypeList) {
                    if (item.displayName == "New Installation") {
                      selectedInventoryType = item;
                      break; // stop loop once found
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
        //getOutwardsData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getOutwardsData();
      },
    );
  }

  getNature() {
    isLoading = true;
    update();
    CustomerProvider().getNature(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              Nature responseData = Nature.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  natureList?.clear();
                  natureList?.addAll(responseData.dataList!);
                  for (var item in natureList) {
                    if (item.displayName == "Sales Conversion") {
                      selectedNature = item;
                      break; // stop loop once found
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
        //getOutwardsData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getOutwardsData();
      },
    );
  }

  getAllProductForNonTrackableProductCategory() {
    isLoading = true;
    productTrackableDataList!.clear();
    selectedProductTrackableDataService = null;
    selectProductUnit = "";
    update();
    CustomerProvider().getAllProductForNonTrackableProductCategory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductNonTrackableProductCategoryRes responseData =
                  ProductNonTrackableProductCategoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productTrackableDataList?.clear();
                  productTrackableDataList?.addAll(responseData.dataList!);
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

  getNonTrackableProductQtyApi(int? productId) {
    isLoading = true;
    nonTrackableProductDataList.clear();
    qtyValue = 0;
    update();
    CustomerProvider().getNonTrackableProductQty(
      productId: productId!,
      ownerId: userDetail!.userId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetNonTrackableProductQtyRes responseData =
                  GetNonTrackableProductQtyRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  nonTrackableProductDataList.clear();
                  nonTrackableProductDataList.addAll(responseData.dataList!);
                  for (var element in nonTrackableProductDataList) {
                    qtyValue = element.unusedQty;
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

  getProductMacAddressData(int? selectProductId) {
    update();

    PageRequest pageRequest = PageRequest(page: page, pageSize: 10);
    CustAssigninwardNewReq request = CustAssigninwardNewReq(
      productId: selectProductId,
      ownerId: userDetail!.userId!.toString(),
      ownerType: "Staff",
      paginationRequestDTO: pageRequest,
    );

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    CustomerProvider().getCustProductMacAddressList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        isShowLoadMore = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductMacAddressDataRes responseData =
                  ProductMacAddressDataRes.fromJson(map);
              if (responseData.responseCode == 200) {
                productMacAddressDataRes = responseData;

                if (page == 1) {
                  productMacAddressList?.clear();
                }

                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productMacAddressList?.addAll(responseData.dataList!);
                  productMacAddressNewList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 404) {
                if (page == 1) {
                  productMacAddressList?.clear();
                }
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
              } else {
                if (page == 1) {
                  productMacAddressList?.clear();
                }
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
          if (page == 1) {
            productMacAddressList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        // isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          productMacAddressList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getProductInventoryList(int? serviceId) {
    selectedProductPlanService = null;
    productPlanServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getAllProductByServiceIdInventory(
      serviceId: serviceId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductPlanServiceInventoryRes responseData =
                  ProductPlanServiceInventoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productPlanServiceList?.addAll(responseData.dataList!);
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

  getAllInventorySpecByItemIdApiCall(int? itemID) {
    selectAllInventorySpecByItemIdData = null;
    allInventorySpecByItemIdDataList!.clear();
    allInventorySpecByItemIdList!.clear();
    isLoading = true;
    update();
    InventoryProvider().getAllInventorySpecByItemIdCall(
      itemId: itemID!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AllInventorySpecByItemIdRes responseData =
                  AllInventorySpecByItemIdRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  allInventorySpecByItemIdDataList
                      ?.addAll(responseData.dataList!);
                  for (var element in allInventorySpecByItemIdDataList!) {
                    if (element.isMultiValueParam == false) {
                      allInventorySpecByItemIdList!.add(element);
                    }
                  }
                }
              } else if (responseData.responseCode == 404) {
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

  assignInventory() {
    String strQty = qtyPicsController.text;
    int? qty;
    if (strQty.isNotEmpty) {
      qty = int.parse(strQty);
    }

    double? newOfferPrice, newProductAmount;

    if (newOfferPriceController.text.isNotEmpty) {
      newOfferPrice = double.tryParse(newOfferPriceController.text.toString());
    } else {
      newOfferPrice = null;
    }

    if (selectedProductPlanService?.newProductAmount != null) {
      newProductAmount = double.tryParse(
          selectedProductPlanService!.newProductAmount.toString());
    } else {
      newProductAmount = null;
    }

    for (var element in allInventorySpecByItemIdList!) {
      custInvParams!.add(CustInvParams(
          paramName: element.paramName, paramValue: element.paramValue));
    }

    if (selectedItemType!.text!.equalsIgnoreCase(Strings.serialized_item)) {
      if (selectedSerialNumber!.isEmpty) {
        Utils.showSnackbar(Strings.INFO, Strings.select_at_list_one_item,
            AppTheme.colorWhite, AppTheme.colorBlueRView);
        return;
      } else {
        custInvParams!.add(CustInvParams(
          paramName: "ONT SN",
          paramValue: selectedSerialNumber![0],
        ));
      }
    }

    //   if (selectedSerialNumber!.isEmpty) {
    //   Utils.showSnackbar(Strings.INFO, Strings.select_at_list_one_item,
    //       AppTheme.colorWhite, AppTheme.colorBlueRView);
    // } else {
    //   custInvParams!.add(CustInvParams(
    //     paramName: "ONT SN",
    //     paramValue: selectedSerialNumber![0],
    //   ));

    assignInventoryReq = OtherInventoryAssignReq(
        id: null,
        qty: qty ?? 1,
        connectionNo: connectionNumberController.text,
        productId: productId,
        inventoryJobType: selectedInventoryType!.value,
        nature: selectedNature!.value,
        customerId: selectedPlanService?.custId,
        serviceId: selectedPlanService?.serviceId,
        custPackId: null,
        staffId: userDetail!.userId,
        inwardId: null,
        itemId: macItemId,
        itemType: selectedConditionType?.text,
        itemTypeFlag: selectedItemType?.text,
        assignedDateTime: inwardDateTime,
        nonSerializedQty: qty,
        billTo: selectedBillToDetail?.text,
        discount: discountController.text,
        isRequiredApproval: false,
        billabecustId: billableCustomerId,
        itemAssemblyStatus: "Pending",
        newAmount: newOfferPrice,
        offerPrice: newProductAmount,
        isFree: false,
        inOutWardMACMapping: selectedMacAddressList,
        custServiceMapId: selectedPlanService?.custPlanMapppingId,
        custInvParams: custInvParams); //selectedMacList
    isLoading = true;
    update();
    log("assignNonSerializedItemToCustomerApi==>>>${jsonEncode(assignInventoryReq)}");
    final url = selectedItemType?.text?.trim().toLowerCase() ==
            Strings.serialized_item.toLowerCase()
        ? UrlConstants.assign_inventory
        : UrlConstants.assignNonSerializedItemToCustomer;
    InventoryProvider().assignNonSerializedItemToCustomerApi(
      url: url,
      request: assignInventoryReq,
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

  //}

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

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }
}
