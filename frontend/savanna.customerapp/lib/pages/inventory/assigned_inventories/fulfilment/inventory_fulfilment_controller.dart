import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/customer/model/save_all_inventory_outward_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/active_staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/available_qty_product_des_res.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
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

class InventoryFulFilMentController extends GetxController {
  bool isLoading = false, isReadOnly = false;
  GetStorage getStorage = GetStorage();

  TextEditingController inventoryRequestNameController =
      TextEditingController();
  TextEditingController inventorySourceTypeController = TextEditingController();
  TextEditingController inventorySelectSourceController =
      TextEditingController();

  TextEditingController outwardDateController = TextEditingController();
  TextEditingController fulfilmentQtyController = TextEditingController();
  DateFormat apiDateStandardFormat = DateFormat(Constant.DATE_TIME_FORMAT_API_US);
  UserDetail? userDetail;

  //Select Destination
  List<DropdownDetail>? destinationTypeList = [];
  DropdownDetail? selectedDestinationType;

  List<FulfilmentProductMappings>? fulfilmentProductMapping = [];

  // // product list
  // List<ProductDetail>? productList = [];
  // ProductDetail? selectedProduct;
  //
  // // source type
  // List<DropdownDetail>? sourceTypeList = [];
  // DropdownDetail? selectedSourceType;
  //
  // // source
  // List<DropdownDetail>? sourceList = [];
  // DropdownDetail? selectedSource;
  //
  // // inward list
  // List<OutwardInwardDetail>? inwardList = [];
  // OutwardInwardDetail? selectedInward;
  //
  // // destination type
  // List<DropdownDetail>? destinationTypeList = [];
  // DropdownDetail? selectedDestinationType;
  //
  // // source  & destination 1
  List<WareHouseDetail>? wareHouseList = [];
  WareHouseDetail? selectedWareHouseData;

  //
  // // source & destination 2
  // List<StaffUserDetail>? staffUserList = [];
  List<StaffUserDataList>? staffUserList = [];
  StaffUserDataList? selectedStaffUserData;

  List<AvailableQtyDataList>? availableQtyProductList = [];
  AvailableQtyDataList? selectedAvailableQty;

  //
  // // source & destination 3
  // List<PartnerDetail>? partnerList = [];
  //
  // //Select Destination
  // List<DropdownDetail>? destinationList = [];
  // DropdownDetail? selectedDestination;
  //
  // // destination type - 1 pop while  Source Type != Warehouse
  // List<PopDetail>? popList = [];
  int? availableQty = 0;

  //
  // // destination type - 2 service area  Source Type != Warehouse
  // List<ServicesAreaDetail>? servicesAreaList = [];
  //
  // List<DropdownDetail>? statusList = [];
  // DropdownDetail? selectedStatus;
  //
  // String from = Strings.add;
  // OutwardDetail? outwardDetail;

  FulfilmentData? fulfillmentData;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;

    if (arguments != null) {
      if (arguments[Constant.FUL_FILL_MENT] != null) {
        fulfillmentData = arguments[Constant.FUL_FILL_MENT];
      }

      if (fulfillmentData != null) {
        isReadOnly = true;
        // set selected qty
        inventoryRequestNameController.text =
            fulfillmentData!.requestInventoryName!.toString();
        inventorySourceTypeController.text = "Warehouse";
        inventorySelectSourceController.text =
            fulfillmentData!.requestToName.toString();
        if (fulfillmentData!.onBehalfOf!.equalsIgnoreCase("WareHouse")) {
          destinationTypeList!.add(DropdownDetail(
              id: Strings.ware_house.toUpperCase(),
              text: Strings.ware_house,
              type: Strings.destination_type));
        } else {
          destinationTypeList!.add(DropdownDetail(
              id: Strings.staff.toUpperCase(),
              text: Strings.staff,
              type: Strings.destination_type));
        }
      } else {
        isReadOnly = false;
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

    getFulfilmentAllWareHouses(fulfillmentData!.id);
  }

  // getProductsData() {
  //   isLoading = true;
  //   productList?.clear();
  //   update();
  //   InventoryManagementProvider().getAllProductList(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ActiveProductRes responseData = ActiveProductRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 productList?.addAll(responseData.dataList!);
  //                 if (outwardDetail != null &&
  //                     outwardDetail!.productId != null &&
  //                     outwardDetail!.productId!.id != null) {
  //                   for (ProductDetail element in productList!) {
  //                     if (element.id != null &&
  //                         element.id == outwardDetail!.productId!.id) {
  //                       selectedProduct = element;
  //                       break;
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
  //       getAllWareHouse();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //       getAllWareHouse();
  //     },
  //   );
  // }

  // getActiveProductsData() {
  //   isLoading = true;
  //   productList?.clear();
  //   update();
  //   CustomerProvider().getActiveProductList(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ActiveProductRes responseData = ActiveProductRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 productList?.addAll(responseData.dataList!);
  //                 if (outwardDetail != null &&
  //                     outwardDetail!.productId != null &&
  //                     outwardDetail!.productId!.id != null) {
  //                   for (ProductDetail element in productList!) {
  //                     if (element.id != null &&
  //                         element.id == outwardDetail!.productId!.id) {
  //                       selectedProduct = element;
  //                       break;
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
  //       // getAllWareHouse();
  //       getAllActiveWareHouse();
  //       // getAllStaffUser();
  //     },
  //     onError: (ResponseModel error) {
  //       getAllWareHouse();
  //       // getAllActiveWareHouse();
  //       // getAllStaffUser();
  //       _handleApiError(error);
  //     },
  //   );
  // }

  // getAllWareHouse() {
  //   isLoading = true;
  //   wareHouseList?.clear();
  //   update();
  //   InventoryManagementProvider().getAllWareHouseList(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             WareHouseListRes responseData = WareHouseListRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 wareHouseList?.addAll(responseData.dataList!);
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
  //       getAllStaffUser();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //       getAllStaffUser();
  //     },
  //   );
  // }

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
                  availableQtyProductDestination();
                  // if (outwardDetail != null &&
                  //     outwardDetail!.destinationType != null &&
                  //     outwardDetail!.destinationType!.isNotEmpty) {
                  //   for (WareHouseDetail element in wareHouseList!) {
                  //     if (element.name != null &&
                  //         element.name == outwardDetail!.destinationType) {
                  //       selectedWareHouseData = element;
                  //       break;
                  //     }
                  //   }
                  // }
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

  getAllStaffUser() {
    isLoading = true;
    staffUserList?.clear();
    update();
    InventoryManagementProvider().getAllActiveStaffUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ActiveStaffUserListRes responseData =
                  ActiveStaffUserListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  staffUserList?.addAll(responseData.dataList!);
                  availableQtyProductDestination();
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
        // setFillDetail();
      },
      onError: (ResponseModel error) {
        // setFillDetail();
        _handleApiError(error);
      },
    );
  }

  availableQtyProductDestination() {
    isLoading = true;
    availableQtyProductList?.clear();
    update();
    InventoryManagementProvider().getAvailableQtyDetailsByProductAndDestination(
      productId: fulfillmentData!.requestInvenotryProductMappings![0].productId,
      ownerId: fulfillmentData!.requestToWarehouseId,
      ownerType: selectedDestinationType != null
          ? selectedDestinationType!.text
          : "Warehouse",
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AvailableQtyProductDesRes responseData =
                  AvailableQtyProductDesRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  availableQtyProductList?.addAll(responseData.dataList!);
                  for (var element in availableQtyProductList!) {
                    if (element.unusedQty != null) {
                      availableQty = element.unusedQty;
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

  void addSaveAllInventoryOutwardApiCall() {
    isLoading = true;
    update();
    List<SaveAllInventoryOutwardRes> requestOutwardList = [];
    SaveAllInventoryOutwardRes request = SaveAllInventoryOutwardRes(
      id: "",
      productId: fulfillmentData != null
          ? fulfillmentData!.requestInvenotryProductMappings![0].productId
          : null,
      qty: "",
      outwardDateTime: apiDateStandardFormat.format(DateTime.now()),
      sourceId: fulfillmentData != null
          ? fulfillmentData!.requestToWarehouseId
          : null,
      source: "",
      sourceType: inventorySourceTypeController.text.isNotEmpty
          ? inventorySourceTypeController.text
          : "",
      status: "ACTIVE",
      outwardNumber: "",
      destinationId:
          selectedStaffUserData != null ? selectedStaffUserData!.id : null,
      destinationType: selectedDestinationType != null
          ? selectedDestinationType!.text
          : null,
      mvnoId: null,
      usedQty: 0,
      unusedQty: availableQty,
      inTransitQty: int.parse(fulfilmentQtyController.text.toString()) ?? 0,
      outTransitQty: "",
      rejectedQty: 0,
      requestInventoryId: fulfillmentData!
          .requestInvenotryProductMappings![0].inventoryRequestId,
      requestInventoryProductId:
          fulfillmentData!.requestInvenotryProductMappings![0].id,
      selectedItems: 0,
    );
    requestOutwardList.add(request);

    log("SaveAllInventoryOutwardReq==>${jsonEncode(requestOutwardList)}");

    InventoryManagementProvider().saveAllInventoryRequestOutward(
      request: requestOutwardList,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
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

        log("ErrrorResoponseModel==>${error}");
        _handleApiError(error);
      },
    );
  }

  getFulfilmentAllWareHouses(int? assignedInventoryId) {
    isLoading = true;
    fulfilmentProductMapping?.clear();
    update();
    InventoryManagementProvider().fulfilmentInventoryRequest(
      fulFilmentId: assignedInventoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            RequestInventoryFulfilmentRes responseData =
                RequestInventoryFulfilmentRes.fromJson(map);
            if (responseData.responseCode != null &&
                responseData.responseCode == 200) {
              fulfillmentData = responseData.data!;
              if (fulfillmentData!
                  .requestInvenotryProductMappings!.isNotEmpty) {
                for (var element
                    in fulfillmentData!.requestInvenotryProductMappings!) {
                  fulfilmentProductMapping!.add(element);
                }
              }
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage!,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message != null &&
              responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        availableQtyProductDestination();
      },
      onError: (ResponseModel error) {
        availableQtyProductDestination();
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
