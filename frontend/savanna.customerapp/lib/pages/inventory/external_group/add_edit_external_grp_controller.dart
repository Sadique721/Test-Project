import 'dart:convert';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/external_group_add_edit_req.dart';
import 'package:savbill/pages/inventory/module/response/external_group_list_res.dart';
import 'package:savbill/pages/inventory/module/response/external_group_owner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/external_partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_service_area_res.dart';
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

class AddEditExternalGrpController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController qtyController = TextEditingController();
  TextEditingController ownerController = TextEditingController();

  UserDetail? userDetail;

  List<ProductDetail>? productList = [];
  ProductDetail? selectedProduct;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<DropdownDetail>? typeList = [];
  DropdownDetail? selectedType;

  String from = Strings.add;
  ExternalGroupDetail? externalGroupDetail;

  List<StaffServiceAreaDetail>? serviceAreaList = [];
  StaffServiceAreaDetail? selectedServiceArea;

  ExternalOwnerDataList? selectedOwnerCustomer;

  ExternalPartnerDataList? selectedOwnerPartner;

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
        id: Strings.customer_owned,
        text: Strings.customer_owned,
        type: Strings.type));
    typeList!.add(DropdownDetail(
        id: Strings.partner_owned,
        text: Strings.partner_owned,
        type: Strings.type));

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        externalGroupDetail = arguments[Constant.IM_DETAIL];
      }

      if (externalGroupDetail != null) {
        if (externalGroupDetail!.inTransitQty != null) {
          qtyController.text = externalGroupDetail!.inTransitQty!.toString();
        }
        if (externalGroupDetail!.ownershipType != null &&
            externalGroupDetail!.ownershipType!.isNotEmpty) {
          for (DropdownDetail element in typeList!) {
            if (element.id!
                .equalsIgnoreCase(externalGroupDetail!.ownershipType!)) {
              selectedType = element;
              break;
            }
          }
        }

        if (externalGroupDetail!.ownerId != null) {
          ownerController.text = externalGroupDetail!.ownerId!.toString();
        }

        if (externalGroupDetail!.status != null &&
            externalGroupDetail!.status!.isNotEmpty) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(externalGroupDetail!.status!)) {
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
              ActiveProductRes responseData = ActiveProductRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productList?.addAll(responseData.dataList!);
                  if (externalGroupDetail != null &&
                      externalGroupDetail!.productId != null &&
                      externalGroupDetail!.productId!.id != null) {
                    for (ProductDetail element in productList!) {
                      if (element.id != null &&
                          element.id ==
                              externalGroupDetail!.productId!.id) {
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
        getStaffServiceAreaData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getStaffServiceAreaData();
      },
    );
  }

  getStaffServiceAreaData() {
    isLoading = true;
    serviceAreaList!.clear();
    update();
    InventoryManagementProvider().getAllStaffServiceArea(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StaffServiceAreaRes responseData =
                  StaffServiceAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  serviceAreaList?.addAll(responseData.dataList!);
                  if (externalGroupDetail != null &&
                      externalGroupDetail!.serviceAreaId != null &&
                      externalGroupDetail!.serviceAreaId!.id != null) {
                    for (StaffServiceAreaDetail element in serviceAreaList!) {
                      if (element.id != null &&
                          element.id ==
                              externalGroupDetail!.serviceAreaId!.id) {
                        selectedServiceArea = element;
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

  void addEditExternalGroupApiCall() {
    isLoading = true;
    update();
    int qty = 0;
    if (qtyController.text.isNotEmpty) {
      qty = int.parse(qtyController.text);
    }
    int? externalOwnerId;
    if(selectedType!.text!.equalsIgnoreCase(Strings.customer_owned)){
      if(selectedOwnerCustomer != null){
        externalOwnerId = selectedOwnerCustomer!.id;
      }else{
        externalOwnerId = null;
      }
    }else if(selectedType!.text!.equalsIgnoreCase(Strings.partner_owned)){
      if(selectedOwnerPartner != null){
        externalOwnerId = selectedOwnerPartner!.id;
      }else{
        externalOwnerId = null;
      }
    }


    ExternalGroupAddEditReq request = ExternalGroupAddEditReq(
        id: externalGroupDetail != null ? externalGroupDetail!.id : null,
        productId: selectedProduct != null ? selectedProduct!.id : null,
        qty: 0,
        inTransitQty: qtyController.text,
        ownerId: externalOwnerId,
        usedQty:
            externalGroupDetail != null ? externalGroupDetail!.usedQty : null,
        unusedQty:
            externalGroupDetail != null ? externalGroupDetail!.unusedQty : null,
        rejectedQty: externalGroupDetail != null
            ? externalGroupDetail!.rejectedQty!.toString()
            : null,
        externalItemGroupNumber: externalGroupDetail != null
            ? externalGroupDetail!.externalItemGroupNumber
            : "",
        ownershipType: selectedType != null ? selectedType!.id : "",
        status: selectedStatus != null ? selectedStatus!.id : "",
        totalMacSerial: externalGroupDetail != null
            ? externalGroupDetail!.totalMacSerial
            : null,
        serviceAreaId: selectedServiceArea != null
            ? ExternalGroupServiceArea(id: selectedServiceArea!.id)
            : null);

    InventoryManagementProvider().addEditExternalGroup(
      isAdd: externalGroupDetail != null ? false : true,
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
}
