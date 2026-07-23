import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/external_group_owner_list_res.dart';
import 'package:savbill/pages/inventory/module/response/external_partner_list_res.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class ExternalOwnerItemController extends GetxController {
  bool isLoading = false;
  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  GetStorage getStorage = GetStorage();
  bool isFilterApply = false;
  TextEditingController searchController = TextEditingController();
  List<ExternalOwnerDataList>? externalOwnerDataList = [];
  ExternalOwnerListRes? externalOwnerListRes;

  List<ExternalPartnerDataList>? externalPartnerDataList = [];
  ExternalPartnerListRes? externalPartnerListRes;
  String? type;
  int? serviceId;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if(type!.equalsIgnoreCase(Strings.customer_owned) && serviceId != null) {
          if (externalOwnerListRes != null &&
              externalOwnerListRes?.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            getExternalOwnerList(serviceId);
          }
        }else if(type!.equalsIgnoreCase(Strings.partner_owned) && serviceId != null){
          if (externalPartnerListRes != null &&
              externalPartnerListRes?.totalPages != page) {
            isShowLoadMore = true;
            page = page + 1;
            update();
            getExternalPartnerList(serviceId);
          }
        }
      }
    });
  }

  // applyFilter() {
  //   if (searchController.text.isNullOrEmpty()) {
  //     isFilterApply = false;
  //     update();
  //     Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
  //         AppTheme.colorWhite, AppTheme.colorRed);
  //     return;
  //   }
  //   isFilterApply = true;
  //   page = 1;
  //   update();
  //   // getExternalOwnerList(serviceId);
  //   if(type!.equalsIgnoreCase(Strings.customer_owned) && serviceId != null){
  //     getExternalOwnerList(serviceId);
  //   }else if(type!.equalsIgnoreCase(Strings.partner_owned) && serviceId != null){
  //     getExternalOwnerList(serviceId);
  //   }
  // }
  //
  // clearFilter() {
  //   searchController.clear();
  //   page = 1;
  //   isFilterApply = false;
  //   update();
  //   // getExternalOwnerList(serviceId);
  //   if(type!.equalsIgnoreCase(Strings.customer_owned) && serviceId != null){
  //     getExternalOwnerList(serviceId);
  //   }else if(type!.equalsIgnoreCase(Strings.partner_owned) && serviceId != null){
  //     getExternalOwnerList(serviceId);
  //   }
  // }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.OWNER_TYPE] != null) {
        type = arguments[Constant.OWNER_TYPE];
      }
      if (arguments[Constant.ID] != null) {
        serviceId = arguments[Constant.ID];
      }

      if(type!.equalsIgnoreCase(Strings.customer_owned) && serviceId != null){
        getExternalOwnerList(serviceId);
      }else if(type!.equalsIgnoreCase(Strings.partner_owned) && serviceId != null){
        getExternalPartnerList(serviceId);
      }
    }
  }

  getExternalOwnerList(int? serviceAreaId) {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: "any",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text));
      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewOwnerExternalList(
      serviceAreaId: serviceAreaId,
      isSearch: isFilterApply,
      pageNo: page,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ExternalOwnerListRes responseData = ExternalOwnerListRes.fromJson(map);
              if (responseData.responseCode != null &&
                      responseData.responseCode == 200) {
                externalOwnerListRes = responseData;
                if (page == 1) {
                  externalOwnerDataList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  externalOwnerDataList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  externalOwnerDataList?.clear();
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
          // }
        } else {
          if (page == 1) {
            externalOwnerDataList?.clear();
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
          externalOwnerDataList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  getExternalPartnerList(int? serviceAreaId) {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: "any",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text));
      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewPartnerExternalList(
      serviceAreaId: serviceAreaId,
      isSearch: isFilterApply,
      pageNo: page,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ExternalPartnerListRes responseData = ExternalPartnerListRes.fromJson(map);
              if (responseData.responseCode != null &&
                      responseData.responseCode == 200) {
                externalPartnerListRes = responseData;
                if (page == 1) {
                  externalPartnerDataList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  externalPartnerDataList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  externalPartnerDataList?.clear();
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
          // }
        } else {
          if (page == 1) {
            externalPartnerDataList?.clear();
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
          externalPartnerDataList?.clear();
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
