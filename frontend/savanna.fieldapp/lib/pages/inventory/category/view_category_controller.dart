import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/category_search_req.dart';
import 'package:savbill/pages/inventory/module/response/category_list_res.dart';
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

class ViewCategoryController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  List<CategoryDetail>? categoryList = [];
  CategoryListRes? categoryListRes;

  TextEditingController searchController = TextEditingController();
  bool isFilterApply = false,filterViewOpen = false;

  List<DropdownDetail>? categorySearchOptionList = [];
  DropdownDetail? selectedCategorySearchOption;

  @override
  void onInit() {
    super.onInit();
    categorySearchOptionList!.add(DropdownDetail(
        id: Strings.product_category_name.toUpperCase(),
        text: Strings.product_category_name,
        type: Strings.name));
    categorySearchOptionList!.add(DropdownDetail(
        id: Strings.product_category_type.toUpperCase(),
        text: Strings.product_category_type,
        type: Strings.type));

    getCategoryData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (categoryListRes != null && categoryListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getCategoryData();
        }
      }
    });
  }

  applyFilter() {
    if (searchController.text.isNullOrEmpty()) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    getCategoryData();
  }

  clearFilter() {
    selectedCategorySearchOption = null;
    searchController.clear();
    page = 1;
    isFilterApply = false;
    update();
    getCategoryData();
  }

  getCategoryData() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CategorySearchReq searchReq = CategorySearchReq();

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: selectedCategorySearchOption != null ? selectedCategorySearchOption!.type : "",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text.trim()));
      searchReq.filter = filters;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().getProductCategory(
      isSearch: isFilterApply,
      pageNo: page,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CategoryListRes responseData = CategoryListRes.fromJson(map);

              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                categoryListRes = responseData;
                if (page == 1) {
                  categoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  categoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  categoryList?.clear();
                }
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
          if (page == 1) {
            categoryList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          categoryList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  deleteCategoryData(CategoryDetail item, int index) {
    isLoading = true;
    update();

    InventoryManagementProvider().deleteProductCategory(
      request: item,
        productID:item.id,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                categoryList!.removeAt(index);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
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
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
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
