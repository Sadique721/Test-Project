
import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/location_data_res.dart';
import 'package:savbill/pages/customer/model/response/location_lat_long_res.dart';
import 'package:savbill/pages/customer/model/response/payment_owner_list_resp.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer/model/response/product_plan_service_inventory_res.dart';
import 'package:savbill/pages/customer/model/response/update_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/assign_inventory_end_owner.dart';
import 'package:savbill/pages/customer_inventory/response/get_non_trackable_product_qty_res.dart';
import 'package:savbill/pages/customer_inventory/response/product_non_trackable_product_category_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/assign_non_serialize_item_req.dart';
import 'package:savbill/pages/inventory/module/response/get_serialize_product_item_res.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/product_mac_address_data_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
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

class PopAssignInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  TextEditingController connectionNumberController = TextEditingController();

  TextEditingController qtyPicsController = TextEditingController();
  TextEditingController discountController = TextEditingController();
  TextEditingController oldOfferPriceController = TextEditingController();
  TextEditingController newOfferPriceController = TextEditingController();
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController assignMacController = TextEditingController();
  TextEditingController macAddressController = TextEditingController();
  TextEditingController serialNumberController = TextEditingController();
  TextEditingController latController = TextEditingController();
  TextEditingController longController = TextEditingController();
  DateTime? selectedInwordDateTime;
  String inwardDateTime = "";
  LocationDetail? selectedLocation;

  int? productId = 0;

  int? qtyValue;
  String? selectProductUnit,getLatLongToAddress;

  String? selectServices, connectionNumber;

  bool? serviceVisible = false,
      serializedItemVisible,
      productItemVisible = false,
      nonSerializedQtyVisible = false,
      assemblyOldNewOfferPriceFlag = false,
      latLongFlag = false;

  int availableQtyPics = 0;
  int? serviceAreaId = 0;

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
  int page = 1;

  List<PaymentOwnerDataList> paymentOwnerList = [];
  PaymentOwnerDataList? paymentOwnerData;

  List<InwardsDetail>? inwardList = [];
  InwardsDetail? selectedInward;

  List<ProductMacDataList>? productMacAddressList = [];
  ProductMacDataList? productMacAddressData;

  List<InwardMacMapDetail>? inwardMacMapDetailList =[];

  int ownerId = 0;
  String ownerType = "";
  int? paymentOwnerId = 0;

  List<DropdownDetail>? itemTypeList = [];
  DropdownDetail? selectedItemType;

  List<DropdownDetail>? billToList = [];
  DropdownDetail? selectedBillToDetail;

  List<DropdownDetail>? assemblyTypeList = [];
  DropdownDetail? selectedAssemblyType;


  List<DropdownDetail>? conditionTypeList = [];
  DropdownDetail? selectedConditionType;

  List<InwardMacMapDetail>? inventoryMacList = [];
  List<InwardMacMapDetail>? selectedMacList = [];
  List<ProductMacDataList>? selectedMacAddressList = [];

  AssignInventoryEndOwnerReq? assignInventoryReq;

  DateFormat dateFormat = DateFormat(Constant.DATE_TIME_FORMAT);
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API);

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiTimeFormat = DateFormat(Constant.API_DATE_FORMAT);

  bool? productConditionType,checkBtnClickEvent=false;
  TextEditingController latLonController = TextEditingController();


  List<SerializedDataList>? nonSerializeProductList= [];
  SerializedDataList? selectedNonSerializeProductData;

  List<SerializedDataList>? serializeProductList= [];
  SerializedDataList? selectedSerializeProductData;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    initPlatformState();
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

  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.OWNER_ID] != null) {
        ownerId = arguments[Constant.OWNER_ID];
      }
      if (arguments[Constant.OWNER_TYPE] != null) {
        ownerType = arguments[Constant.OWNER_TYPE];
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
  }

  getNonSerializedProductDataList() {
    isLoading = true;
    nonSerializeProductList!.clear();
    update();
    InventoryManagementProvider().nonSerializeProductList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSerializedProductDataRes responseData = GetSerializedProductDataRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  nonSerializeProductList?.clear();
                  nonSerializeProductList?.addAll(responseData.dataList!);
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

  getSerializedProductDataList() {
    isLoading = true;
    serializeProductList!.clear();
    update();
    InventoryManagementProvider().serializeProductList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetSerializedProductDataRes responseData = GetSerializedProductDataRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  serializeProductList?.clear();
                  serializeProductList?.addAll(responseData.dataList!);
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

  /*getPlanServiceData() {
    selectedPlanService = null;
    planServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getCustomerService(
      customerId: 1,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServiceByCustomerRes responseData =
              PlanServiceByCustomerRes.fromJson(map);
              if (responseData.responseCode == 200|| responseData.responseCode ==0) {
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
  }*/

  /*getActiveProductData() {
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
        getPaymentOwnerDataApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        //getOutwardsData();
        getPaymentOwnerDataApi();
      },
    );
  }*/

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
    productMacAddressData = null;
    productMacAddressList?.clear();
    isLoading = true;
    update();
    PageRequest pageRequest = PageRequest(page: page, pageSize: 20);
    CustAssigninwardNewReq request = CustAssigninwardNewReq(
        productId: selectProductId,
        ownerId: userDetail!.userId!.toString(),
        ownerType: "Staff",
      paginationRequestDTO: pageRequest
    );

    CustomerProvider().getProductMacAddressList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductMacAddressDataRes responseData =
              ProductMacAddressDataRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productMacAddressList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode  == 404){
                Utils.showSnackbar(
                    Strings.INFO,
                    "Product MAC address not available",
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }else {
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

  getProductInventoryList(int? serviceId) {
    selectedProductPlanService = null;
    productPlanServiceList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getProductByProductAssignInventory(
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

  assignInventory() {
    assignInventoryReq = AssignInventoryEndOwnerReq(
        id: null,
        qty: 1,
        productId: productId,
        staffId: userDetail!.userId,
        inwardId: null,
        itemId: productMacAddressData!.itemId ,
        itemTypeFlag: selectedItemType!.text,
        assignedDateTime: inwardDateTime,
        status: "",
        mvnoId: null,
        ownerId: ownerId,
        ownerType: "pop",
        outInWardMACMapping: selectedMacAddressList!,
        nonSerializedQty: null,
        latitude: latController.text,
        longitude: longController.text,
        // billTo: selectedBillToDetail!.text,
        // discount: discountController.text,
        // isRequiredApproval:false,
        // isFree:false
    ); //selectedMacList
    isLoading = true;
    update();


    InventoryProvider().assignInventoryToEndOwner(
      request: assignInventoryReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // BaseResponse responseData = BaseResponse.fromJson(map);
              ViewPopInventoryRes responseData =
              ViewPopInventoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: responseData);
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


  assignNonSerializeInventoryPop() {
    String strQty = qtyPicsController.text;
    int qty = 0;
    if (strQty.isNotEmpty) {
      qty = int.parse(strQty);
    }
    AssignNonSerializedItemReq assignNonSerializedItemReq = AssignNonSerializedItemReq(
        id: null,
        qty: qty,
        productId: productId,
        staffId: userDetail!.userId,
        inwardId: null,
        itemId: productId ,
        itemTypeFlag: selectedItemType!.text,
        assignedDateTime: inwardDateTime,
        status: null,
        nonSerializedQty: qty,
      itemAssemblyStatus:"Pending",
      ownerId: ownerId,
      ownerType: ownerType,
      mvnoId: null,

    ); //selectedMacList
    isLoading = true;
    update();
    InventoryProvider().assignNonSerializedItemToEndOwnerApi(
      request: assignNonSerializedItemReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // BaseResponse responseData = BaseResponse.fromJson(map);
              ViewPopInventoryRes responseData =
              ViewPopInventoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: responseData);
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

                  log("latitude===>>>${responseData.location!.latitude!}");
                  log("longitude===>>>${responseData.location!.longitude!}");
                  // locationData = responseData.location;
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
              log("Exception$e");
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

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }



}
