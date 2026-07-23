import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_inwards.dart';
import 'package:savbill/pages/inventory/module/response/get_all_active_products_by_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
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

class AddEditInwardsController extends GetxController {
  bool isLoading = false, hasOEMConsider = false;
  GetStorage getStorage = GetStorage();

  TextEditingController qtyController = TextEditingController();
  TextEditingController inwardsDateController = TextEditingController();
  TextEditingController warrantyStartDateController = TextEditingController();
  TextEditingController warrantyEndDateController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();

  UserDetail? userDetail;

  List<AllActiveProductsByProductData>? productList = [];
  AllActiveProductsByProductData? selectedProduct;
  String? unitType = "";
int? durationMonth = 0;
  List<WareHouseDetail>? wareHouseList = [];
  WareHouseDetail? selectedWarehouse;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<DropdownDetail>? typeList = [];
  DropdownDetail? selectedType;

  String from = Strings.add;
  InwardsDetail? inwardsDetail;
  String inwardsDateTime = "";
  String startDate = "";
  String endDate = "";
  DateTime? selectedDateTime;
  DateFormat dateFormat =
      DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat apiDateTimeFormat = DateFormat(Constant.DATE_TIME_FORMAT_API_US);

  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  @override
  void onInit() {
    super.onInit();
    statusList!.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));
    typeList!.add(DropdownDetail(
        id: Strings.key_new, text: Strings.key_new, type: Strings.type));
    typeList!.add(DropdownDetail(
        id: Strings.refurbished,
        text: Strings.refurbished,
        type: Strings.type));
    typeList!.add(DropdownDetail(
        id: Strings.damage, text: Strings.damage, type: Strings.type));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        inwardsDetail = arguments[Constant.IM_DETAIL];
      }

      if (inwardsDetail != null) {
        if (inwardsDetail!.inTransitQty != null) {
          qtyController.text = inwardsDetail!.inTransitQty!.toString();
        }

        if (inwardsDetail!.inTransitQty != null) {
          descriptionController.text = inwardsDetail!.description ?? "";
        }


        if(inwardsDetail!.inwardDateTime != null) {
          DateTime date = DateFormat(Constant.DATE_TIME_FORMAT_API)
              .parse(inwardsDetail!.inwardDateTime!);
          selectedDateTime = date;
          inwardsDateController.text = dateFormat.format(date);
          inwardsDateTime = apiDateTimeFormat.format(date);
        }

        if (inwardsDetail!.type != null && inwardsDetail!.type!.isNotEmpty) {
          for (DropdownDetail element in typeList!) {
            if (element.id!.equalsIgnoreCase(inwardsDetail!.type!)) {
              selectedType = element;
              break;
            }
          }
        }
        if (inwardsDetail!.status != null &&
            inwardsDetail!.status!.isNotEmpty) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(inwardsDetail!.status!)) {
              selectedStatus = element;
              break;
            }
          }
        }
      }
    }
    update();

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
    getActiveProductsData();
  }

  getActiveProductsData() {
    isLoading = true;
    productList?.clear();
    update();
    CustomerProvider().getActiveProductList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllActiveProductsByProductCategoryRes responseData =
                  GetAllActiveProductsByProductCategoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productList?.addAll(responseData.dataList!);
                  if (inwardsDetail != null &&
                      inwardsDetail!.productId != null &&
                      inwardsDetail!.productId!.id != null) {
                    for (AllActiveProductsByProductData element
                        in productList!) {
                      if (element.id != null &&
                          element.id == inwardsDetail!.productId!.id) {
                        selectedProduct = element;
                        break;
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
        getAllActiveWareHouse();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllActiveWareHouse();
      },
    );
  }

  getAllActiveWareHouse() {
    isLoading = true;
    wareHouseList?.clear();
    update();
    InventoryManagementProvider().getActiveWareHouse(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseListRes responseData = WareHouseListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  wareHouseList?.addAll(responseData.dataList!);
                  if (inwardsDetail != null &&
                      inwardsDetail!.destinationId != null) {
                    for (WareHouseDetail element in wareHouseList!) {
                      if (element.id != null &&
                          element.id == inwardsDetail!.destinationId) {
                        selectedWarehouse = element;
                        break;
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

  void addEditInwardsApiCall() {
    /*    Destination Type
    selectedWarehouse != null &&
        selectedWarehouse!.warehouseType != null &&
        selectedWarehouse!.warehouseType!.isNotEmpty
        ? selectedWarehouse!.warehouseType
        : null*/
    isLoading = true;
    update();
    AddEditInwards request = AddEditInwards(
      id: inwardsDetail != null ? inwardsDetail!.id : null,
      productId: selectedProduct != null ? selectedProduct!.id : null,
      qty: inwardsDetail != null ? inwardsDetail!.qty : null,
      inwardDateTime: inwardsDateTime,
      startDateTime: startDate,
      expiryDateTime:endDate,
      destinationId: selectedWarehouse != null ? selectedWarehouse!.id : null,
      destinationType: "Warehouse",
      inwardNumber: inwardsDetail != null ? inwardsDetail!.inwardNumber : "",
      inTransitQty: int.parse(qtyController.text.toString()),
      mvnoId: null,
      usedQty: inwardsDetail != null && inwardsDetail!.usedQty != null
          ? inwardsDetail!.usedQty
          : null,
      unusedQty: inwardsDetail != null && inwardsDetail!.unusedQty != null
          ? inwardsDetail!.unusedQty
          : null,
      type: selectedType != null ? selectedType!.id : "",
      status: selectedStatus != null ? selectedStatus!.id : "",
      outTransitQty:
          inwardsDetail != null && inwardsDetail!.outTransitQty != null
              ? inwardsDetail!.outTransitQty
              : null,
      rejectedQty: inwardsDetail != null && inwardsDetail!.rejectedQty != null
          ? inwardsDetail!.rejectedQty
          : null,
      totalMacSerial:
          inwardsDetail != null && inwardsDetail!.totalMacSerial != null
              ? inwardsDetail!.totalMacSerial
              : null,
      description: descriptionController.text,
      specificationParametersDTOList: [],
    );
    InventoryManagementProvider().addEditInwards(
      isAdd: inwardsDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200 ||
                responseData.responseCode == 0) {
              Get.back(result: true);
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
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

  durationCalculate(int? id) {
    for (AllActiveProductsByProductData element in productList!) {
      if (element.id != null && element.id == selectedProduct!.id) {
        unitType = element.expiryTimeUnit;
        durationMonth = element.expiryTime;
        hasOEMConsider = element.hasOEMConsider!;
        break;
      }
    }
    update();
  }

   calculateMonth(DateTime? startDate) {

     DateTime? pickDate;
     if(unitType!.equalsIgnoreCase(Strings.month)) {
       pickDate = DateTime(
         startDate!.year,
         startDate.month + durationMonth!,
         startDate.day,
       );
     }else{
       pickDate = DateTime(
         startDate!.year,
         startDate.month ,
         startDate.day+ durationMonth!,
       );
     }

     warrantyEndDateController.text =
         apiDateFormat.format(pickDate);
     endDate =
         apiDateFormat.format(pickDate);
   }
}
