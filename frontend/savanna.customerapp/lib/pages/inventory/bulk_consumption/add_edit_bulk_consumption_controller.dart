import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_bulk_consumption_req.dart';
import 'package:savbill/pages/inventory/module/response/active_staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/add_edit_bulk_consumption_res.dart';
import 'package:savbill/pages/inventory/module/response/available_qty_product_des_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_product_based_item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_serialized_item_base_on_product_res.dart';
import 'package:savbill/pages/inventory/module/response/partiner_list_new_res.dart';
import 'package:savbill/pages/inventory/module/response/view_bulk_consumption_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_inventory_res.dart';
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

class AddEditBulkConsumptionController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController nameController = TextEditingController();

  UserDetail? userDetail;

  List<ProductDetail>? productList = [];
  ProductDetail? selectedProduct;

  String from = Strings.add;
  BulkConsumptionDetail? bulkConsumptionDetail;
  TextEditingController searchController = TextEditingController();

  List<InOutWardMACMapping>? inOutWardMACMapping = [];
  List<InOutWardMACMapping>? inOutWardMACMappingOrg = [];
  List<ProductBasedItemTypeDataList>? serializedNonProductDataList = [];
  ProductBasedItemTypeDataList? selectSerializedProductData;
  List<DropdownDetail>? itemTypeList = [];
  DropdownDetail? selectedItemType;

  List<DropdownDetail>? ownerTypeList = [];
  DropdownDetail? selectedOwnerType;

  List<WareHouseDetail>? wareHouseList = [];
  WareHouseDetail? selectedWarehouse;

  List<StaffUserDataList>? staffUserList = [];
  StaffUserDataList? selectedStaffUserDetail;

  List<Partnerlist>? partnerList = [];
  Partnerlist? selectedPartnerData;

  List<AvailableQtyDataList>? availableQtyProductList = [];
  AvailableQtyDataList? selectedAvailableQty;
  TextEditingController assignQuantityController = TextEditingController();


  List<SerializedItemBaseDataList>? serializedItemBaseDataList = [];
  SerializedItemBaseDataList? selectedSerializedItemBaseData;
  int? availableQty = 0, ownerId = 0;
  bool? isSerializedItem = false, isNonSerializedItem = false;

  @override
  void onInit() {
    super.onInit();
    itemTypeList!.add(DropdownDetail(
        id: Strings.serialized_item.toUpperCase(),
        text: Strings.serialized_item,
        type: Strings.item_type));
    itemTypeList!.add(DropdownDetail(
        id: Strings.non_serialized_item.toUpperCase(),
        text: Strings.non_serialized_item,
        type: Strings.item_type));

    ownerTypeList!.add(DropdownDetail(
        id: Strings.ware_house.toUpperCase(),
        text: Strings.ware_house,
        type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.staff.toUpperCase(),
        text: Strings.staff,
        type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.partner.toUpperCase(),
        text: Strings.partner,
        type: Strings.owner_type));

    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        bulkConsumptionDetail = arguments[Constant.IM_DETAIL];
      }

      if (bulkConsumptionDetail != null) {
        if (bulkConsumptionDetail!.bulkConsumptionName != null) {
          nameController.text =
              bulkConsumptionDetail!.bulkConsumptionName!.toString();
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
    // getActiveProductsData();
  }

  searchItem(String value) {
    inOutWardMACMapping!.clear();
    if (value.isEmpty) {
      inOutWardMACMapping!.addAll(inOutWardMACMappingOrg!);
    } else {
      for (InOutWardMACMapping detail in inOutWardMACMappingOrg!) {
        if (detail.serialNumber != null &&
            detail.serialNumber!.containsIgnoreCase(value) ||
            detail.macAddress != null &&
                detail.macAddress!.containsIgnoreCase(value)) {
          inOutWardMACMapping!.add(detail);
        }
      }
    }
    update();
  }

  getAllProductBasedOnItemType(String? productItemType) {
    isLoading = true;
    serializedNonProductDataList?.clear();
    selectSerializedProductData = null;
    selectedOwnerType = null;
    selectedWarehouse = null;
    update();

    InventoryManagementProvider().getAllProductBasedOnItemType(
      itemType: productItemType,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllProductBasedItemTypeRes responseData =
              GetAllProductBasedItemTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  serializedNonProductDataList?.addAll(responseData.dataList!);
                  // for (var element in serializedProductDataList!) {
                  //   if(element.unusedQty != null ) {
                  //     availableQty = element.unusedQty;
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
                  // if (inwardsDetail != null &&
                  //     inwardsDetail!.destinationId != null) {
                  //   for (WareHouseDetail element in wareHouseList!) {
                  //     if (element.id != null &&
                  //         element.id == inwardsDetail!.destinationId) {
                  //       selectedWarehouse = element;
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
                  for (var element in responseData.dataList!) {
                    if (element.id == userDetail!.userId) {
                      staffUserList?.add(element);
                    }
                  }
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

  getAllPartnerUser() {
    isLoading = true;
    partnerList?.clear();
    update();
    InventoryManagementProvider().getAllNewPartnerUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PartnerListNewRes responseData = PartnerListNewRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.partnerlist != null &&
                    responseData.partnerlist!.isNotEmpty) {
                  partnerList?.addAll(responseData.partnerlist!);
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
                  if (bulkConsumptionDetail != null &&
                      bulkConsumptionDetail!.productId != null) {
                    for (ProductDetail element in productList!) {
                      if (element.id != null &&
                          element.id == bulkConsumptionDetail!.productId) {
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
        // getInwardsData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getInwardsData();
      },
    );
  }


  availableQtyProductDestination() {
    isLoading = true;
    availableQtyProductList?.clear();
    update();
    InventoryManagementProvider().getAvailableQtyDetailsByProductAndDestination(
      productId: selectSerializedProductData!.id,
      ownerId: ownerId,
      ownerType: selectedOwnerType!.text,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AvailableQtyProductDesRes responseData = AvailableQtyProductDesRes
                  .fromJson(map);
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



  getAllSerializedItemBaseOnProduct() {
    isLoading = true;
    serializedItemBaseDataList?.clear();
    update();
    InventoryManagementProvider().getAllSerializedItemBaseOnProduct(
      productId: selectSerializedProductData!.id,
      ownerId: ownerId,
      ownerType: selectedOwnerType!.text,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllSerializedItemBaseOnProductRes responseData = GetAllSerializedItemBaseOnProductRes
                  .fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  serializedItemBaseDataList?.addAll(responseData.dataList!);
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

  // getInwardsData() {
  //   if (selectedProduct == null || selectedProduct!.id == null) {
  //     return;
  //   }
  //   isLoading = true;
  //   selectedInward = null;
  //   inwardList!.clear();
  //   inOutWardMACMapping!.clear();
  //   inOutWardMACMappingOrg!.clear();
  //   update();
  //   InventoryManagementProvider().getBulkConsumptionInwardsList(
  //     productId: selectedProduct!.id!,
  //     staffId: userDetail!.userId!,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             BulkConsumptionInwardRes responseData =
  //                 BulkConsumptionInwardRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 inwardList?.addAll(responseData.dataList!);
  //                 if (bulkConsumptionDetail != null &&
  //                     bulkConsumptionDetail!.inwardId != null) {
  //                   for (BulkConsumptionInward element in inwardList!) {
  //                     if (element.id != null &&
  //                         element.id == bulkConsumptionDetail!.inwardId) {
  //                       selectedInward = element;
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
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }
  //
  // getInwardMappingData() {
  //   if (selectedInward == null || selectedInward!.id == null) {
  //     return;
  //   }
  //   isLoading = true;
  //   inOutWardMACMapping!.clear();
  //   inOutWardMACMappingOrg!.clear();
  //   update();
  //   InventoryManagementProvider().inoutwardMappingDetail(
  //     id: selectedInward!.id,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             InOutwardMacMapRes responseData =
  //                 InOutwardMacMapRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 inOutWardMACMapping?.addAll(responseData.dataList!);
  //                 inOutWardMACMappingOrg!.addAll(responseData.dataList!);
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
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  void addEditBulkConsumptionApiCall() {
    List<SerializedItemBaseDataList> inOutWardMACMappingSelected = [];
    List<int> macMap = [];
    if (serializedItemBaseDataList != null && serializedItemBaseDataList!.isNotEmpty) {
      for (var element in serializedItemBaseDataList!) {
        if (element.selected == true) {
          // element.outwardId = outwardsDetail!.id;
          macMap.add(element.id!);
          inOutWardMACMappingSelected.add(element);
        }
      }
    }

    if (inOutWardMACMappingSelected.isEmpty) {
      Utils.showSnackbar(
          Strings.ERROR,
          "Please select at-lease one item of the list.!",
          AppTheme.colorWhite,
          AppTheme.colorRed);
      return;
    }

    // isLoading = true;
    update();
    // List<int> macMap = [];
    // if (inOutWardMACMapping != null && inOutWardMACMapping!.isNotEmpty) {
    //   for (InOutWardMACMapping element in inOutWardMACMapping!) {
    //     if (element.selected != null && element.selected == true) {
    //       macMap.add(element.id!);
    //     }
    //   }
    // }

    int assignQty = 0;
    if(assignQuantityController.text.isEmpty){
      assignQty = inOutWardMACMappingSelected.length;
    }else{
      assignQty=int.parse(assignQuantityController.text);
    }
    AddEditBulkConsumptionReq request = AddEditBulkConsumptionReq(
        id: bulkConsumptionDetail != null ? bulkConsumptionDetail!.id : null,
        productId: selectSerializedProductData != null ? selectSerializedProductData!.id : null,
        itemListLongId: macMap,
        bulkConsumptionName: nameController.text,
        // inwardId: selectedInward != null ? selectedInward!.id : null,
        isDeleted: false,
        qty: assignQty ,
        ownerId: ownerId,
        ownerType: selectedOwnerType!.text,
        nonSerializedQty: assignQuantityController.text.isNotEmpty ? int.parse(assignQuantityController.text) : 0,
        itemType: selectedItemType!.text,
      inOutWardMACMappings: inOutWardMACMappingSelected,
    );

    log("AddEditBulkConsumptionReq==>>>>${jsonEncode(request)}");
    InventoryManagementProvider().addEditBulkConsumption(
      isAdd: bulkConsumptionDetail != null ? false : true,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            // BaseResponse responseData = BaseResponse.fromJson(map);
            AddEditBulkConsumptionRes responseData = AddEditBulkConsumptionRes.fromJson(map);
            if (responseData.responseCode == 200 ) {
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
