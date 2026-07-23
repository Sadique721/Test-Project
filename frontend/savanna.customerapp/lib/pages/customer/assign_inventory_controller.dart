import 'dart:convert';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/assign_inventory_req.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
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

class AssignInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  TextEditingController qtyPicsController = TextEditingController();
  TextEditingController outwardDateController = TextEditingController();
  TextEditingController assignMacController = TextEditingController();
  DateTime? selectedInwordDateTime;
  String inwardDateTime = "";
  int availableQtyPics = 0;

  List<CustomerPlanServiceDetail>? planServiceList = [];
  CustomerPlanServiceDetail? selectedPlanService;

  List<ProductDetail>? productList = [];
  ProductDetail? selectedProduct;

  List<InwardsDetail>? inwardList = [];
  InwardsDetail? selectedInward;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;
  int customerId = 0;

  List<InwardMacMapDetail>? inventoryMacList = [];
  List<InwardMacMapDetail>? selectedMacList = [];
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
    statusList?.clear();
    statusList?.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList?.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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
          if (responseModel.message != Strings.something_wrong) {
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

  getInwardsData() {
    selectedInward = null;
    inwardList?.clear();
    isLoading = true;
    update();
    CustomerProvider().getInwardsList(
      productId: selectedProduct!.id!,
      userId: userDetail!.userId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewInwardsListRes responseData =
                  ViewInwardsListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inwardList?.addAll(responseData.dataList!);
                }
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
          if (responseModel.message != Strings.something_wrong) {
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

  getMacFromInward() {
    isLoading = true;
    inventoryMacList?.clear();
    selectedMacList?.clear();
    update();
    InventoryManagementProvider().inoutwardMappingDetail(
      id: selectedInward!.id!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InwardMacMapListRes responseData =
                  InwardMacMapListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  /*responseData.dataList!.forEach((element) {
                    if (element.custInventoryMappingId == null &&
                        element.customerId == null) {
                      element.isAvailable = true;
                      availableQtyPics = availableQtyPics + 1;
                    } else {
                      element.isAvailable = false;
                    }
                  });*/
                  inventoryMacList?.addAll(responseData.dataList!);
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
          if (responseModel.message != Strings.something_wrong) {
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

  assignInventory() {
    String strQty = qtyPicsController.text;
    int qty = 0;
    if (strQty.isNotEmpty) {
      qty = int.parse(strQty);
    }

    AssignInventoryReq request = AssignInventoryReq(
        id: null,
        qty: qty,
        productId: selectedProduct!.id,
        serviceId: selectedPlanService!.serviceId,
        custPackId: null,
        customerId: customerId,
        staffId: userDetail!.userId,
        inwardId: selectedInward!.id,
        assignedDateTime: inwardDateTime,
        status: selectedStatus!.id,
        inOutWardMACMapping: selectedMacList
    ); //selectedMacList

    isLoading = true;
    update();
    CustomerProvider().assignInventory(
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
          if (responseModel.message != Strings.something_wrong) {
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
