import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/update_mac_serial_req.dart';
import 'package:savbill/pages/customer/model/response/update_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/replace_inventory_customer_req.dart';
import 'package:savbill/pages/customer_inventory/response/all_inventory_spec_by_item_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/all_plan_inventory_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_item_based_on_product_type_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_product_by_plan_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/product_mac_serial_res.dart';
import 'package:savbill/pages/customer_inventory/response/replacement_mac_address_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
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

class InventoryReplaceController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  bool isShowLoadMore = false;
  ScrollController? controller;
  int? macItemId = 0;
  List<String>? selectedSerialNumber = [];

  List<DropdownDetail>? replacementList = [];
  DropdownDetail? selectReplacementItem;
  int macMappingId = 0, customerId = 0,itemId = 0;
  int planId = 0;
  List<ReplacementMacAddressList>? selectedMacAddressList = [];
  var availableQtyPics = 0;

  List<DropdownDetail>? replacementReasonList = [];
  DropdownDetail? selectReplacementReason;

  TextEditingController remarksController = TextEditingController();

  List<ProductMacSerialDataList>? productMacSerialDataList = [];
  ProductMacSerialDataList? selectProductMacSerialData;

  List<ReplacementMacAddressList> replacementMacAddressList = [];
  ReplacementMacAddressList? macAddressData;
  ReplacementMacAddressListRes? replacementMacAddressListRes;
  ReplacementMacAddressList? productMacAddressData;

  List<ProductTypDataList>? productTypeDataList = [];
  ProductTypDataList? selectProductTypeData;
  bool? macNoAndSerialNoFlag = false;
  bool? otherInventoryReplaceFlag = false;
  bool? planInventoryReplaceFlag = false;
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController assignMacController = TextEditingController();
  TextEditingController macAddressController = TextEditingController();
  TextEditingController serialNumberController = TextEditingController();
  List<ReplaceInventoryReq> replaceInventory = [];
  DateTime? selectedInwordDateTime;
  String replacementDateTime = "";
  int? newMacMappingId = 0;
  int page = 1;

  String? selectReplacementValue, selectReplacementReasonValue;

  List<ProductByPlanDataList>? productByPlanDataList = [];
  ProductByPlanDataList? selectProductByPlanDataList;

  List<AllPlanInventoryDataList> getAllPlanInventoryIdOnPlanList = [];
  AllPlanInventoryDataList? selectAllPlanInventoryIdOnPlan;

  DateFormat dateFormat = DateFormat(Constant.DATE_TIME_FORMAT);
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API);

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);
  DateFormat apiTimeFormat = DateFormat(Constant.API_DATE_FORMAT);

  bool? hasMac,hasSerial;

  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (replacementMacAddressListRes != null &&
            replacementMacAddressListRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getReplacementMacAddressListApi(selectProductMacSerialData!.id);
        }
      }
    });
    getArgumentData();
    initPlatformState();
    DateTime now = DateTime.now();
    outwardDateController.text = dateFormat.format(now);
    replacementDateTime = apiDateTimeFormat.format(now);

    replacementList?.clear();
    replacementList?.add(DropdownDetail(
        id: Strings.temporary_replacement.toUpperCase(),
        text: Strings.temporary_replacement,
        type: Strings.replacement));
    replacementList?.add(DropdownDetail(
        id: Strings.parmanant_replacement.toUpperCase(),
        text: Strings.parmanant_replacement,
        type: Strings.replacement));

    replacementReasonList?.add(DropdownDetail(
        id: Strings.defective.toUpperCase(),
        text: Strings.defective,
        type: Strings.replacement_reason));
    replacementReasonList?.add(DropdownDetail(
        id: Strings.upgrade.toUpperCase(),
        text: Strings.upgrade,
        type: Strings.replacement_reason));
    replacementReasonList?.add(DropdownDetail(
        id: Strings.surrender.toUpperCase(),
        text: Strings.surrender,
        type: Strings.replacement_reason));
    replacementReasonList?.add(DropdownDetail(
        id: Strings.others.toUpperCase(),
        text: Strings.other,
        type: Strings.replacement_reason));
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
      }
      if (arguments[Constant.MAC_MAPPING_ID] != null) {
        macMappingId = arguments[Constant.MAC_MAPPING_ID];
      }
      if (arguments[Constant.ITEM_ID] != null) {
        itemId = arguments[Constant.ITEM_ID];
      }
      if (arguments[Constant.PLAN_ID] != null) {
        planId = arguments[Constant.PLAN_ID];
      }

      getAllPlanInventoryIdOnPlanId(planId);
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

  getProductMacSerialNumberApi() {
    productMacSerialDataList!.clear();
    isLoading = true;
    update();
    InventoryProvider().productByMacSerialNumber(
      macMappingId: macMappingId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductMacSerialNumberRes responseData =
                  ProductMacSerialNumberRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productMacSerialDataList?.addAll(responseData.dataList!);
                  otherInventoryReplaceFlag = true;
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
                  getAllPlanInventoryIdOnPlanList
                      .addAll(responseData.dataList!);
                  getProductByPlanIdApiCall(
                      getAllPlanInventoryIdOnPlanList[0].id);
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
        getProductMacSerialNumberApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getProductMacSerialNumberApi();
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
                  planInventoryReplaceFlag = true;
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

  getItemBasedOnProductTypeApiCall(
      {required String? ownerId,
      required String? productId,
      required String? productCategoryId}) {
    isLoading = true;
    productTypeDataList!.clear();
    selectProductTypeData = null;
    update();
    String productPlanGroupId = "";

    CustomerProvider().getItemBasedOnProductType(
      ownerId: ownerId,
      planId: planId.toString(),
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
                  // macNoAndSerialNoFlag = true;
                  // billToDiscountFlag = true;
                  // oldOfferAndNewOfferPriceFlag = true;
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

  replaceInventoryCustomer() {
    replaceInventory.clear();
    ReplaceInventoryReq replaceInventoryReq = ReplaceInventoryReq(
      oldMacMappingId: macMappingId,
      newMacMappingId: newMacMappingId,
    );
    replaceInventory.add(replaceInventoryReq);

    isLoading = true;
    update();
    InventoryProvider().replaceInventoryCustomerInventory(
      customerId: customerId,
      inventoryType: selectReplacementItem!.text,
      replacementReason: selectReplacementReason!.text,
      approvalRemark: remarksController.text,
      request: replaceInventory,
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



  getReplacementMacAddressListApi(int? productId) {
    List<ProductMacSerialDataList>? product = [];
    product= productMacSerialDataList!.where((element) => element.id == productId).toList();
    PageRequest request = PageRequest(page: page,pageSize: 20);
    for (var element in product) {
      hasMac = element.productCategory!.hasMac;
      hasSerial = element.productCategory!.hasSerial;
    }

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }

    InventoryProvider().replacementProductMacAddress(
      productId: productId,
      itemId: itemId,
      ownerId: userDetail!.userId,
      pagerRequest: request,
      replacement:selectReplacementItem!.text,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        isShowLoadMore = false;
        update();
        if (responseModel.statusCode == 200 && responseModel.result != null) {
          try {
            Map<String, dynamic> map = responseModel.result;
            ReplacementMacAddressListRes responseData =
            ReplacementMacAddressListRes.fromJson(map);
            if (responseData.responseCode == 200) {
              replacementMacAddressListRes = responseData;
              if (page == 1) {
                replacementMacAddressList.clear();
              }

              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                replacementMacAddressList.addAll(responseData.dataList!);
              }
            } else if (responseData.responseCode == 404) {
              if (page == 1) {
                replacementMacAddressList.clear();
              }
              Utils.showSnackbar(
                Strings.INFO,
                "Assign does not have a product",
                AppTheme.colorWhite,
                AppTheme.colorBlueRView,
              );
            } else {
              if (page == 1) {
                replacementMacAddressList.clear();
              }
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(
                  Strings.ERROR,
                  responseData.responseMessage,
                  AppTheme.colorWhite,
                  AppTheme.colorRed,
                );
              }
            }
          } on Exception catch (e) {
            print(e.toString());
          }
        } else {
          if (page == 1) {
            replacementMacAddressList.clear();
          }
          if (responseModel.message != null && responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(
              Strings.ERROR,
              responseModel.message,
              AppTheme.colorWhite,
              AppTheme.colorRed,
            );
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          replacementMacAddressList.clear();
        }
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
