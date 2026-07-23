import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/response/customer_status_list_res.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_edit_product_management_req.dart';
import 'package:savbill/pages/inventory/module/response/case_packege_res.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_product_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_taxes_all_res.dart';
import 'package:savbill/pages/inventory/module/response/product_charge_list_res.dart';
import 'package:savbill/pages/inventory/module/response/product_manufacturer_list_Res.dart';
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

class AddEditProductController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  RxBool ifSplitterCASDropdownShow = false.obs;
  TextEditingController productNameController = TextEditingController();
  TextEditingController descriptionController = TextEditingController();
  TextEditingController refundInAmountController = TextEditingController();
  TextEditingController refundPostAmountController = TextEditingController();

  // TextEditingController inPortController = TextEditingController();
  // TextEditingController outPortController = TextEditingController();
  // TextEditingController availableInPortController = TextEditingController();
  // TextEditingController availableOutPortController = TextEditingController();
  // TextEditingController expiryTimeController = TextEditingController();
  TextEditingController productIdController = TextEditingController();
  TextEditingController ledgerIdController = TextEditingController();
  TextEditingController warrantyTimeController = TextEditingController();
  TextEditingController actualPriceController = TextEditingController();

  TextEditingController refurActualPriceController = TextEditingController();
  TextEditingController refurRefundInAmountController = TextEditingController();
  TextEditingController refurRefundPostAmountController =
      TextEditingController();

  UserDetail? userDetail;

  List<CategoryDetail>? categoryList = [];
  CategoryDetail? selectedCategory;

  List<CategoryDetail>? selectedCategoryData = [];

  // List<ProductChargeDetail>? chargeList = [];
  // ProductChargeDetail? selectedCharge;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<DropdownDetail>? expiryTimeUnitList = [];

  DropdownDetail? selectedExpiryTimeUnit;

  String from = Strings.add;
  ProductDetail? productDetail;

  //int? availableInPort, availableOutPort;

  List<CustomerStatusDetail>? warrantyTimeUnitList = [];
  CustomerStatusDetail? selectedWarrantyTimeUnitData;

  List<ManufacturerDataList>? manufacturerDataList = [];
  ManufacturerDataList? selectedManufacturerData;

  List<Taxlist>? taxList = [];
  Taxlist? selectedTaxData;
  Taxlist? selectedRefurTaxData;

  List<CASDataList>? casDataList = [];
  CASDataList? selectedCASData;

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

    expiryTimeUnitList!.add(DropdownDetail(
        id: Strings.day, text: Strings.day, type: Strings.expiry_time_unit));
    expiryTimeUnitList!.add(DropdownDetail(
        id: Strings.month,
        text: Strings.month,
        type: Strings.expiry_time_unit));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.IM_DETAIL] != null) {
        productDetail = arguments[Constant.IM_DETAIL];
      }

      if (productDetail != null) {
        productNameController.text = productDetail!.name!;
        descriptionController.text = productDetail!.description!;
        // refundAmountController.text = productDetail!.refundAmount!;

        if (productDetail!.newProductRefAmountInWarranty != null) {
          refundInAmountController.text =
              productDetail!.newProductRefAmountInWarranty!.toString();
        } else {
          refundInAmountController.text = "0.0";
        }

        if (productDetail!.newProductRefAmountPostWarranty != null) {
          refundPostAmountController.text =
              productDetail!.newProductRefAmountPostWarranty!.toString();
        } else {
          refundPostAmountController.text = "0.0";
        }

        if (productDetail!.refurburshiedProductRefAmountInWarranty != null) {
          refurRefundInAmountController.text = productDetail!
              .refurburshiedProductRefAmountInWarranty!
              .toString();
        } else {
          refurRefundInAmountController.text = "0.0";
        }

        if (productDetail!.refurburshiedProductRefAmountPostWarranty != null) {
          refurRefundPostAmountController.text = productDetail!
              .refurburshiedProductRefAmountPostWarranty!
              .toString();
        } else {
          refurRefundPostAmountController.text = "0.0";
        }

        if (productDetail!.productId != null &&
            productDetail!.productId!.isNotEmpty) {
          productIdController.text = productDetail!.productId!;
        }
        if (productDetail!.navLedgerId != null &&
            productDetail!.navLedgerId!.isNotEmpty) {
          ledgerIdController.text = productDetail!.navLedgerId!;
        }

        if (productDetail!.expiryTime != null) {
          warrantyTimeController.text = productDetail!.expiryTime.toString();
        }

        if (productDetail!.actualpricenewProduct != null) {
          actualPriceController.text =
              productDetail!.actualpricenewProduct.toString();
        }

        if (productDetail!.actualpricerefurbishedProduct != null) {
          refurActualPriceController.text =
              productDetail!.actualpricerefurbishedProduct.toString();
        }

        // if (productDetail!.expiryTime != null) {
        //   expiryTimeController.text = productDetail!.expiryTime!.toString();
        // }
        // if (productDetail!.expiryTime != null) {
        //   inPortController.text = productDetail!.expiryTime!.toString();
        // }
        // if (productDetail!.totalInPorts != null) {
        //   inPortController.text = productDetail!.totalInPorts!.toString();
        // }
        // if (productDetail!.availableInPorts != null) {
        //   availableInPortController.text =
        //       productDetail!.availableInPorts!.toString();
        // }
        // if (productDetail!.totalOutPorts != null) {
        //   outPortController.text = productDetail!.totalOutPorts!.toString();
        // }
        // if (productDetail!.availableOutPorts != null) {
        //   availableOutPortController.text =
        //       productDetail!.availableOutPorts!.toString();
        // }
        if (productDetail!.expiryTimeUnit != null &&
            productDetail!.expiryTimeUnit!.isNotEmpty) {
          for (DropdownDetail element in expiryTimeUnitList!) {
            if (element.id!.equalsIgnoreCase(productDetail!.expiryTimeUnit!)) {
              selectedExpiryTimeUnit = element;
              break;
            }
          }
        }

        if (productDetail!.status != null &&
            productDetail!.status!.isNotEmpty) {
          for (DropdownDetail element in statusList!) {
            if (element.id!.equalsIgnoreCase(productDetail!.status!)) {
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
    // getProductCategoryList();
    getAllActiveProductCategoryList();
  }

  getProductCategoryList() {
    isLoading = true;
    categoryList?.clear();
    update();
    InventoryManagementProvider().getAllProductCategory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CategoryListRes responseData = CategoryListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  categoryList?.addAll(responseData.dataList!);
                  if (categoryList != null &&
                      productDetail != null &&
                      productDetail!.productCategory != null) {
                    for (CategoryDetail element in categoryList!) {
                      if (element.id != null &&
                          element.id == productDetail!.productCategory!.id!) {
                        selectedCategory = element;
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
        // getChargeList();
        getWarrantyTimeUnitList();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getChargeList();
        getWarrantyTimeUnitList();
      },
    );
  }

  getAllActiveProductCategoryList() {
    isLoading = true;
    categoryList?.clear();
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
                  categoryList?.addAll(responseData.dataList!);
                  if (categoryList != null &&
                      productDetail != null &&
                      productDetail!.productCategory != null) {
                    for (CategoryDetail element in categoryList!) {
                      if (element.id != null &&
                          element.id == productDetail!.productCategory!.id) {
                        selectedCategory = element;
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
        // getChargeList();
        getWarrantyTimeUnitList();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getChargeList();
        getWarrantyTimeUnitList();
      },
    );
  }

  getWarrantyTimeUnitList() {
    isLoading = true;
    warrantyTimeUnitList?.clear();
    update();
    InventoryManagementProvider().getwarrantyTimeUnit(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerStatusListRes responseData =
                  CustomerStatusListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  warrantyTimeUnitList?.addAll(responseData.dataList!);
                  if (categoryList != null &&
                      productDetail != null &&
                      productDetail!.expiryTimeUnit != null &&
                      productDetail!.expiryTimeUnit != "") {
                    for (CustomerStatusDetail element
                        in warrantyTimeUnitList!) {
                      if (element.id != null &&
                          element.value == productDetail!.expiryTimeUnit) {
                        selectedWarrantyTimeUnitData = element;
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
        getProductManufacturerData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getProductManufacturerData();
      },
    );
  }

  getProductManufacturerData() {
    isLoading = true;
    manufacturerDataList?.clear();
    update();
    InventoryManagementProvider().getProductManufacturerList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProductManufacturerListRes responseData =
                  ProductManufacturerListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  manufacturerDataList?.addAll(responseData.dataList!);

                  if (categoryList != null &&
                      productDetail != null &&
                      productDetail!.vendorId != null &&
                      productDetail!.vendorId != 0) {
                    for (ManufacturerDataList element
                        in manufacturerDataList!) {
                      if (element.id != null &&
                          element.id == productDetail!.vendorId) {
                        selectedManufacturerData = element;
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
        getTaxesDataList();
      },
      onError: (ResponseModel error) {
        getTaxesDataList();
        _handleApiError(error);
      },
    );
  }

  getTaxesDataList() {
    isLoading = true;
    taxList?.clear();
    update();
    InventoryManagementProvider().getTaxesDataList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventroyTaxesListRes responseData =
                  InventroyTaxesListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.taxlist != null &&
                    responseData.taxlist!.isNotEmpty) {
                  taxList?.addAll(responseData.taxlist!);

                  if (categoryList != null &&
                      productDetail != null &&
                      productDetail!.newProductTaxName != null &&
                      productDetail!.newProductTaxName!.isNotEmpty) {
                    for (Taxlist element in taxList!) {
                      if (element.id != null &&
                          element.name == productDetail!.newProductTaxName) {
                        selectedTaxData = element;
                        // break;
                      } else if (element.id != null &&
                          element.name ==
                              productDetail!.refurburshiedProductTaxName) {
                        selectedRefurTaxData = element;
                        // break;
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
        // getChargeList();
        getCASPackageCASData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getChargeList();
        getCASPackageCASData();
      },
    );
  }

  getCASPackageCASData() {
    isLoading = true;
    casDataList?.clear();
    update();
    InventoryManagementProvider().getCASPackage(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetCasePackageRes responseData = GetCasePackageRes.fromJson(map);
              if (responseData.status == 200 ||
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  casDataList?.addAll(responseData.dataList!);
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
        // getChargeList();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getChargeList();
      },
    );
  }

  // getChargeList() {
  //   isLoading = true;
  //   chargeList?.clear();
  //   update();
  //   InventoryManagementProvider().getProductCharge(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ProductChargeListRes responseData =
  //                 ProductChargeListRes.fromJson(map);
  //             if (responseData.responseCode == 200) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 chargeList?.addAll(responseData.dataList!);
  //                 if (chargeList != null &&
  //                     productDetail != null &&
  //                     productDetail!.chargeId != null) {
  //                   for (ProductChargeDetail element in chargeList!) {
  //                     if (element.id != null &&
  //                         element.id == productDetail!.chargeId!) {
  //                       selectedCharge = element;
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
  //       getWarrantyTimeUnitList();
  //     },
  //     onError: (ResponseModel error) {
  //       getWarrantyTimeUnitList();
  //       _handleApiError(error);
  //     },
  //   );
  // }

  productDeviceTypeEvent(CategoryDetail categoryDetail) {
    int? Id = categoryDetail.id;
    selectedCategoryData!.clear();
    // categoryList!.forEach((element) {
    //   log("categoryListId=>${element.id}");
    //   if(element.id == Id){
    //     selectedCategoryData!.add(element);
    //   }
    // });
    selectedCategoryData!
        .addAll(categoryList!.where((element) => element.id == Id));

    if (selectedCategoryData!.isNotEmpty) {
      if (selectedCategoryData![0].hasCas == true) {
        ifSplitterCASDropdownShow.value = true;
      } else {
        ifSplitterCASDropdownShow.value = false;
      }
    }
    update();
  }

  void addEditProductApiCall() {
    isLoading = true;
    update();

    int? totalInPort = -1,
        totalOutPort = -1,
        availableInPort = -1,
        availableOutPort = -1,
        expTime;

    double? actualPriceNewProduct,
        newProductRefAmountInWarranty,
        newProductRefAmountPostWarranty,
        actualPriceRefurbishProduct,
        refurProductRefAmountInWarranty,
        refurProductRefAmountPostWarranty;
    if (actualPriceController.text.isNotEmpty) {
      actualPriceNewProduct = double.parse(actualPriceController.text);
    } else {
      actualPriceNewProduct = 0.0;
    }

    if (refurActualPriceController.text.isNotEmpty) {
      actualPriceRefurbishProduct =
          double.parse(refurActualPriceController.text);
    } else {
      actualPriceRefurbishProduct = 0.0;
    }
    if (refundInAmountController.text.isNotEmpty) {
      newProductRefAmountInWarranty =
          double.parse(refundInAmountController.text);
    } else {
      newProductRefAmountInWarranty = 0.0;
    }

    if (refundPostAmountController.text.isNotEmpty) {
      newProductRefAmountPostWarranty =
          double.parse(refundPostAmountController.text);
    } else {
      newProductRefAmountPostWarranty = 0.0;
    }

    if (refurRefundInAmountController.text.isNotEmpty) {
      refurProductRefAmountInWarranty =
          double.parse(refurRefundInAmountController.text);
    } else {
      refurProductRefAmountInWarranty = 0.0;
    }

    if (refundPostAmountController.text.isNotEmpty) {
      refurProductRefAmountPostWarranty =
          double.parse(refundPostAmountController.text);
    } else {
      refurProductRefAmountPostWarranty = 0.0;
    }

    if (warrantyTimeController.text.isNotEmpty) {
      expTime = int.parse(warrantyTimeController.text);
    } else {
      expTime = 0;
    }

    AddEditProductManagementReq request = AddEditProductManagementReq(
      id: productDetail != null ? productDetail!.id : null,
      name: productNameController.text,
      productId:
          productIdController.text.isNotEmpty ? productIdController.text : null,
      navLedgerId:
          ledgerIdController.text.isNotEmpty ? ledgerIdController.text : null,
      description: descriptionController.text,
      productCategory: selectedCategory != null ? selectedCategory!.id : null,
      status: selectedStatus != null ? selectedStatus!.id : null,
      actualpricenewProduct: actualPriceNewProduct,
      newProductTax: selectedTaxData != null ? selectedTaxData!.id : null,
      newProductRefAmountInWarranty: newProductRefAmountInWarranty,
      newProductRefAmountPostWarranty: newProductRefAmountPostWarranty,
      actualpricerefurbishedProduct: actualPriceRefurbishProduct,
      refurburshiedProductTax:
          selectedRefurTaxData != null ? selectedRefurTaxData!.id : null,
      refurburshiedProductRefAmountInWarranty: refurProductRefAmountInWarranty,
      refurburshiedProductRefAmountPostWarranty:
          refurProductRefAmountPostWarranty,
      expiryTime: expTime,
      expiryTimeUnit: selectedWarrantyTimeUnitData != null
          ? selectedWarrantyTimeUnitData!.value
          : "",
      vendorId: selectedManufacturerData != null
          ? selectedManufacturerData!.id
          : null,
      totalInPorts: totalInPort,
      totalOutPorts: totalOutPort,
      availableInPorts: availableInPort,
      caseId:
          ifSplitterCASDropdownShow.value == true ? selectedCASData!.id : null,
      availableOutPorts: availableOutPort,
      hasAssetConsider: false,
      hasOEMConsider: false,
    );

    log("AddEditProductReq => " + json.encode(request));

    InventoryManagementProvider().addEditProduct(
      isAdd: productDetail != null ? false : true,
      request: request,
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
