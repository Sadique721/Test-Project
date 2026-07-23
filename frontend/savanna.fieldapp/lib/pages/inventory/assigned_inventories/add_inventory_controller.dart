import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/all_active_product_categories_by_res.dart';
import 'package:savbill/pages/inventory/module/response/all_ware_house_res.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/get_all_active_products_by_product_category_res.dart';
import 'package:savbill/pages/inventory/module/response/on_behalf_of_res.dart';
import 'package:savbill/pages/inventory/module/response/save_inventory_request_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_list_res.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../util/strings.dart';
import '../module/response/add_product_inventory_request.dart';
import '../module/response/ware_house_new_list_res.dart';

class AddInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  String from = Strings.add;

  String? selectedOnBehalfValue;
  String? wareHouseValue;
  String? selectProductCategoriesValue;

  List<DropdownDetail> productType = [];
  DropdownDetail? selectedProductType;

  List<RequestInvenotryProductMappings>? addProductDetails = [];

  List<OnBehalfDataList>? onBehalfOfList = [];
  // List<ProductCategoriesList>? productCategoriesList = [];

  // List<GetAllWareHousesRes>? allWareHouseList = [];
  List<WareHouseDataList>? allWareHouseList = [];
  WareHouseDataList? selectedWareHouseData;

  // List<GetAllProductByCategroyIdRes>? allCategoryProductIdList = [];
  OnBehalfDataList? onBehalfData;
  GetAllWareHousesRes? wareHousesRes;

  // GetAllProductByCategroyIdRes? allCategoryProductIdRes;
  // ProductCategoriesList? productCategoriesData;
  List<AllActiveProductsByProductData>? allCategoryProductIdCategoryList = [];
  AllActiveProductsByProductData? allActiveProductsByProductData;
  TextEditingController qtyProductController = TextEditingController();
  TextEditingController reasonController = TextEditingController();
  GetAllActiveProductsByProductCategoryRes?
      getAllActiveProductsByProductCategoryRes;


  List<WareHouseDetail>? wareHouseList = [];
  WareHouseDetail? selectedWarehouse;

  List<CategoryDetail>? productCategoryList = [];
  List<CategoryDetail>? filterProductCategoryList = [];
  CategoryDetail? selectedProductCategory;


  @override
  void onInit() {
    super.onInit();
    productType.add(DropdownDetail(
        id: Strings.new_txt, text: Strings.new_txt, type: Strings.type));
    productType.add(DropdownDetail(
        id: Strings.refurbished,
        text: Strings.refurbished,
        type: Strings.type));
    // getAllWareHouses();
    // getAllActiveProductCategoriesByCB();
    getAllActiveProductCategoryList();
  }

  getOnBehalfOfList(String selectedOnBehalf) {
    isLoading = true;
    onBehalfOfList?.clear();
    productCategoryList?.clear();
    onBehalfData = null;
    update();
    InventoryManagementProvider().getOnBehalfOffReq(
      onBehalfRequest: selectedOnBehalf,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              OnBehalfOfRes responseData = OnBehalfOfRes.fromJson(map);
              if (responseData.responseCode == 0) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  if(selectedOnBehalf.equalsIgnoreCase("Pop") || selectedOnBehalf.equalsIgnoreCase("ServiceArea")){
                    for (var element in filterProductCategoryList!) {
                      if(!element.type!.equalsIgnoreCase("CustomerBind")){
                        productCategoryList!.add(element);
                      }
                    }
                  }else{
                    productCategoryList!.addAll(filterProductCategoryList!);
                  }

                  onBehalfOfList?.addAll(responseData.dataList!);

                  if (onBehalfOfList != null &&
                      onBehalfData != null &&
                      onBehalfData!.name != null) {
                    for (OnBehalfDataList element in onBehalfOfList!) {
                      if (element.id != null &&
                          element.id == onBehalfData!.id!) {
                        selectedOnBehalfValue = element.name;
                        break;
                      }
                    }
                  }
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "${Strings.data_not_available}.",
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
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

  getAllWareHouses() {
    isLoading = true;
    allWareHouseList?.clear();
    update();
    InventoryManagementProvider().getAllWareHousesReq(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              WareHouseListNewRes responseData =
                  WareHouseListNewRes.fromJson(map);
              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                for (var element in responseData.dataList!) {
                  if (onBehalfData!.id != element.id) {
                    allWareHouseList!.add(element);
                  }
                }
                if(allWareHouseList!.isEmpty || allWareHouseList ==null){
                  Utils.showSnackbar(Strings.INFO, "${Strings.request_to} ${Strings.data_not_available}!!",
                      AppTheme.colorWhite, AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
            // getAllActiveProductCategoriesByCB();
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

  // getAllActiveProductCategoriesByCB() {
  //   isLoading = true;
  //   productCategoriesList?.clear();
  //   productCategoriesData = null;
  //   update();
  //   InventoryManagementProvider().getAllActiveProductCategoriesByReq(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             GetAllActiveProductCategoriesByRes responseData =
  //                 GetAllActiveProductCategoriesByRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 productCategoriesList?.addAll(responseData.dataList!);
  //                 if (productCategoriesList != null &&
  //                     productCategoriesData != null &&
  //                     productCategoriesData!.name != null) {
  //                   for (ProductCategoriesList element
  //                       in productCategoriesList!) {
  //                     if (element.id != null &&
  //                         element.id == productCategoriesData!.id!) {
  //                       selectProductCategoriesValue = element.name;
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

  getCategoryByProductId(int categoryByProductId) {
    isLoading = true;
    allCategoryProductIdCategoryList?.clear();
    allActiveProductsByProductData = null;
    update();
    InventoryManagementProvider().getAllCategoryByProductIdReq(
      productCategoryId: categoryByProductId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllActiveProductsByProductCategoryRes responseData =
                  GetAllActiveProductsByProductCategoryRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                getAllActiveProductsByProductCategoryRes = responseData;
                if (responseData.dataList != null ||
                    responseData.dataList!.isNotEmpty) {
                  allCategoryProductIdCategoryList = responseData.dataList;
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
              // allCategoryProductIdList = jsonListValue.map((map) => GetAllProductByCategroyIdRes.fromJson(map)).toList();
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

  saveRequestInventoryApi() {
    AddProductInventoryRequest request = AddProductInventoryRequest(
      onBehalfOf: selectedOnBehalfValue,
      requestNameId: onBehalfData!.id,
      requestToWarehouseId: selectedWareHouseData!.id,
      reason: reasonController.text,
      status: Strings.pending,
      requestInvenotryProductMappings: addProductDetails,
    );
    log("saveRequestInventoryApi=>> ${jsonEncode(request)}");
    isLoading = true;
    update();
    InventoryManagementProvider().saveRequestInventory(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              SaveRequestInventoryRes responseData =
              SaveRequestInventoryRes.fromJson(map);
              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                // Utils.showSnackbar(
                //     Strings.SUCCESS,
                //     responseData.responseMessage,
                //     AppTheme.colorWhite,
                //     AppTheme.colorGreen);
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

  getAllActiveProductCategoryList() {
    isLoading = true;
    productCategoryList?.clear();
    filterProductCategoryList?.clear();
    update();
    InventoryManagementProvider().getAllActiveProductCategories(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CategoryListRes responseData = CategoryListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  productCategoryList?.addAll(responseData.dataList!);
                  filterProductCategoryList?.addAll(responseData.dataList!);
                  // if (categoryList != null &&
                  //     productDetail != null &&
                  //     productDetail!.productCategory != null) {
                  //   for (CategoryDetail element in categoryList!) {
                  //     if (element.id != null &&
                  //         element.id == productDetail!.productCategory!.id) {
                  //       selectedCategory = element;
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
