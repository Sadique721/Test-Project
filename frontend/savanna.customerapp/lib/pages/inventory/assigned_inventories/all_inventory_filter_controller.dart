import 'dart:convert';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/active_product_res.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/active_partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/bulk_consumption_inward_res.dart';
import 'package:savbill/pages/inventory/module/response/item_type_res.dart';
import 'package:savbill/pages/inventory/module/response/ownership_res.dart';
import 'package:savbill/pages/inventory/module/response/partner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/staff_user_list_res.dart';
import 'package:savbill/pages/inventory/module/response/status_res.dart';
import 'package:savbill/pages/inventory/module/response/view_pop_list_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/inventory/module/response/warranty_status_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class AllInventoriesFilterController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;

  List<DropdownDetail>? ownerTypeList = [];
  DropdownDetail? selectedOwnerType;

  List<DropdownDetail>? ownerList = [];
  DropdownDetail? selectedOwner;

  List<ProductDetail>? productList = [];
  ProductDetail? selectedProduct;

  List<BulkConsumptionInward>? inwardList = [];
  BulkConsumptionInward? selectedInward;

  List<OwnershipDetail>? ownershipList = [];
  OwnershipDetail? selectedOwnership;

  List<StatusDetail>? statusList = [];
  StatusDetail? selectedStatus;

  List<ItemTypeDetail>? itemTypeList = [];
  ItemTypeDetail? selectedItemType;

  List<WarrantyStatusDetail>? warrantyStatusList = [];
  WarrantyStatusDetail? selectedWarrantyStatus;
  TextEditingController serialNumberController = TextEditingController();

  @override
  void onInit() {
    super.onInit();
    ownerTypeList!.add(DropdownDetail(
        id: Strings.ware_house,
        text: Strings.ware_house,
        type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.staff, text: Strings.staff, type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.partner, text: Strings.partner, type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.pop, text: Strings.pop, type: Strings.owner_type));
    ownerTypeList!.add(DropdownDetail(
        id: Strings.service_area,
        text: Strings.service_area,
        type: Strings.owner_type));
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

    if (Utils.productListForFilter != null &&
        Utils.productListForFilter!.isNotEmpty) {
      productList?.addAll(Utils.productListForFilter!);
      isLoading = false;
      update();
      getInwardsData();
    } else {
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
                    Utils.productListForFilter!.addAll(responseData.dataList!);
                    /* if (bulkConsumptionDetail != null &&
                      bulkConsumptionDetail!.productId != null) {
                    for (ProductDetail element in productList!) {
                      if (element.id != null &&
                          element.id == bulkConsumptionDetail!.productId) {
                        selectedProduct = element;
                        break;
                      }
                    }
                  }*/
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
              Utils.showSnackbar(
                  Strings.INFO,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorBlueRView);
            }
          }
          isLoading = false;
          update();
          getInwardsData();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
          getInwardsData();
        },
      );
    }
  }

  getInwardsData() {
    isLoading = true;
    selectedInward = null;
    inwardList?.clear();
    update();

    if (Utils.inwardListForFilter != null &&
        Utils.inwardListForFilter!.isNotEmpty) {
      inwardList?.addAll(Utils.inwardListForFilter!);
      isLoading = false;
      update();
      getOwnerShipData();
    } else {
      InventoryManagementProvider().getAllInwardsList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                BulkConsumptionInwardRes responseData =
                    BulkConsumptionInwardRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    inwardList?.addAll(responseData.dataList!);
                    Utils.inwardListForFilter!.addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
          getOwnerShipData();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
          getOwnerShipData();
        },
      );
    }
  }

  getOwnerShipData() {
    isLoading = true;
    selectedOwnership = null;
    ownershipList!.clear();
    update();

    if (Utils.ownershipListForFilter != null &&
        Utils.ownershipListForFilter!.isNotEmpty) {
      ownershipList?.addAll(Utils.ownershipListForFilter!);
      isLoading = false;
      update();
      getItemStatusData();
    } else {
      InventoryManagementProvider().getOwnershipList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                OwnershipRes responseData = OwnershipRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    ownershipList?.addAll(responseData.dataList!);
                    Utils.ownershipListForFilter!
                        .addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
          getItemStatusData();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
          getItemStatusData();
        },
      );
    }
  }

  getItemStatusData() {
    isLoading = true;
    selectedStatus = null;
    statusList!.clear();
    update();

    if (Utils.statusListForFilter != null &&
        Utils.statusListForFilter!.isNotEmpty) {
      statusList?.addAll(Utils.statusListForFilter!);
      isLoading = false;
      update();
      getItemTypeData();
    } else {
      InventoryManagementProvider().getItemStatusList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                StatusRes responseData = StatusRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    statusList?.addAll(responseData.dataList!);
                    Utils.statusListForFilter!.addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
          getItemTypeData();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
          getItemTypeData();
        },
      );
    }
  }

  getItemTypeData() {
    isLoading = true;
    selectedItemType = null;
    itemTypeList!.clear();
    update();
    if (Utils.itemTypeListForFilter != null &&
        Utils.itemTypeListForFilter!.isNotEmpty) {
      itemTypeList?.addAll(Utils.itemTypeListForFilter!);
      isLoading = false;
      update();
      getWarrantyStatusData();
    } else {
      InventoryManagementProvider().getItemTypeList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                ItemTypeRes responseData = ItemTypeRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    itemTypeList?.addAll(responseData.dataList!);
                    Utils.itemTypeListForFilter!.addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
          getWarrantyStatusData();
        },
        onError: (ResponseModel error) {
          _handleApiError(error);
          getWarrantyStatusData();
        },
      );
    }
  }

  getWarrantyStatusData() {
    isLoading = true;
    selectedWarrantyStatus = null;
    warrantyStatusList!.clear();
    update();
    if (Utils.warrantyStatusListForFilter != null &&
        Utils.warrantyStatusListForFilter!.isNotEmpty) {
      warrantyStatusList?.addAll(Utils.warrantyStatusListForFilter!);
      isLoading = false;
      update();
    } else {
      InventoryManagementProvider().getWarrantyStatusList(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                WarrantyStatusRes responseData =
                    WarrantyStatusRes.fromJson(map);
                if (responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    warrantyStatusList?.addAll(responseData.dataList!);
                    Utils.warrantyStatusListForFilter!
                        .addAll(responseData.dataList!);
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
              Utils.showSnackbar(
                  Strings.ERROR,
                  responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite,
                  AppTheme.colorRed);
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
  }

  manageOwner() {
    if (selectedOwnerType != null &&
        selectedOwnerType!.id != null &&
        selectedOwnerType!.id!.equalsIgnoreCase(Strings.ware_house)) {
      if(Utils.wareHouseList!=null && Utils.wareHouseList!.isNotEmpty){
        for (WareHouseDetail element in Utils.wareHouseList!) {
          ownerList!.add(DropdownDetail(id:element.id!.toString(), text:element.name,type:Strings.ware_house));
        }
      }else{
        getAllWareHouse();
      }
    }

    if (selectedOwnerType != null &&
        selectedOwnerType!.id != null &&
        selectedOwnerType!.id!.equalsIgnoreCase(Strings.staff)) {
      if(Utils.staffUserList!=null && Utils.staffUserList!.isNotEmpty){
        for (StaffUserDetail element in Utils.staffUserList!) {
          ownerList!.add(DropdownDetail(id:element.id!.toString(), text:element.username,type:Strings.staff));
        }
      }else{
        getAllStaffUser();
      }
    }

    if (selectedOwnerType != null &&
        selectedOwnerType!.id != null &&
        selectedOwnerType!.id!.equalsIgnoreCase(Strings.partner)) {
      if(Utils.activePartnerList!=null && Utils.activePartnerList!.isNotEmpty){
        for (ActivePartnerDataList element in Utils.activePartnerList!) {
          ownerList!.add(DropdownDetail(id:element.id!.toString(), text:element.name,type:Strings.partner));
        }
      }else{
        getAllPartnerUser();
      }
    }

    if (selectedOwnerType != null &&
        selectedOwnerType!.id != null &&
        selectedOwnerType!.id!.equalsIgnoreCase(Strings.pop)) {
      if(Utils.popList!=null && Utils.popList!.isNotEmpty){
        for (PopDetail element in Utils.popList!) {
          ownerList!.add(DropdownDetail(id:element.id!.toString(), text:element.name,type:Strings.pop));
        }
      }else{
        getAllPop();
      }
    }

    if (selectedOwnerType != null &&
        selectedOwnerType!.id != null &&
        selectedOwnerType!.id!.equalsIgnoreCase(Strings.service_area)) {
      if(Utils.servicesAreaList!=null && Utils.servicesAreaList!.isNotEmpty){
        for (ServicesAreaDetail element in Utils.servicesAreaList!) {
          ownerList!.add(DropdownDetail(id:element.id!.toString(), text:element.name,type:Strings.service_area));
        }
      }else{
        getServiceArea();
      }
    }
    update();
  }

  /*getAllWareHouse() {
    isLoading = true;
    Utils.wareHouseList?.clear();
    update();
    InventoryManagementProvider().getAllWareHouseList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseListRes responseData = WareHouseListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.wareHouseList?.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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
  }*/


  getAllWareHouse() {
    isLoading = true;
    // wareHouseList?.clear();
    Utils.wareHouseList?.clear();
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
                  // wareHouseList?.addAll(responseData.dataList!);
                  Utils.wareHouseList?.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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
    Utils.staffUserList?.clear();
    update();
    InventoryManagementProvider().getAllActiveStaffUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              StaffUserListRes responseData = StaffUserListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.staffUserList?.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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
    Utils.activePartnerList?.clear();
    update();
    InventoryManagementProvider().getAllActivePartnerUser(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ActivePartnerListRes responseData = ActivePartnerListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.activePartnerList?.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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

  getAllPop() {
    isLoading = true;
    Utils.popList?.clear();
    update();
    InventoryManagementProvider().getAllPop(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewPopListRes responseData = ViewPopListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.popList?.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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

  getServiceArea() {
    isLoading = true;
    Utils.servicesAreaList!.clear();
    update();
    InventoryManagementProvider().getAllStaffServiceArea(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  Utils.servicesAreaList!.addAll(responseData.dataList!);
                  update();
                  manageOwner();
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
