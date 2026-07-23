import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/category_search_req.dart';
import 'package:savbill/pages/inventory/module/response/all_inventory_list_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_detail_res.dart';
import 'package:savbill/pages/inventory/module/response/filter_data.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_customer_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_pop_res.dart';
import 'package:savbill/pages/inventory/module/response/inventory_assigned_service_area_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class AssignedInventoriesController extends GetxController with WidgetsBindingObserver{
  bool isLoading = false;
  ScrollController? controller, controllerAllInventory;
  ScrollController? controllerAssigned, controllerAssignedCustomer,controllerPop,controllerServiceArea;
  int page = 1, pageAllInventory = 1,pageNumber = 1;
  bool isShowLoadMore = false;
  bool isFilterApply = false,
      isFilterAllInventory = false,
      filterViewOpen = false;

  GetStorage getStorage = GetStorage();
  // AssignedCustomerDetailsRes? assignedCustomerDetailsRes;
  List<InventoryAssignedCustomerDetail>? assignedCustomerList = [];
  List<InventoryAssignedCustomerDetail>? assignedCustomerListOrg = [];

  List<InventoryAssignedPopDataList>? assignedPopList =[];
  List<InventoryAssignedPopDataList>? assignedPopListOrg = [];

  List<AssignedServiceAreaDataList>? assignedServiceList =[];
  List<AssignedServiceAreaDataList>? assignedServiceListOrg =[];


  UserDetail? userDetail;
  bool isCallAllApi = true;

  int tabIndex = 0;
  RxInt subTabIndex = 0.obs;
  // RxInt subTabIndex = 0.obs;
  TextEditingController searchController = TextEditingController();
  AssignedInventoryDetailRes? assignedInventoryDetailRes;
  InventoryAssignedCustomerRes? assignedCustomerRes;
  InventoryAssignedPopRes? assignedPopRes;
  InventoryAssignedServiceAreaRes? assignedServiceAreaRes;
  List<AssignedInventoryDetail>? assignedInventoryList = [];

  AllInventoryListRes? allInventoryListRes;
  List<InventoryListDetail>? allInventoryList = [];

  FilterData? filterData;


  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    controller = ScrollController();
    controllerAllInventory = ScrollController();
    controllerAssigned = ScrollController();
    controllerPop = ScrollController();
    controllerServiceArea = ScrollController();

    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignedInventoryDetailRes != null &&
            assignedInventoryDetailRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewAssignedInventoryList();
        }
      }
    });

    controllerAllInventory?.addListener(() {
      double? extentAfter = controllerAllInventory?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (allInventoryListRes != null &&
            allInventoryListRes?.totalPages != pageAllInventory) {
          isShowLoadMore = true;
          pageAllInventory = pageAllInventory + 1;
          update();
          viewAllInventoryList();
        }
      }
    });

    controllerAssigned?.addListener(() {
      double? extentAfter = controllerAssigned?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignedCustomerRes != null &&
            assignedCustomerRes?.totalPages != pageNumber) {
          isShowLoadMore = true;
          pageNumber = pageNumber + 1;
          update();
          getAssignedInventoryCustomerList(
              subTabIndex.value == 0 ? true : false);
        }
      }
    });

    controllerPop?.addListener(() {
      double? extentAfter = controllerPop?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignedPopRes != null &&
            assignedPopRes?.totalPages != pageNumber) {
          isShowLoadMore = true;
          pageNumber = pageNumber + 1;
          update();
          getAssignedInventoryPopList(subTabIndex.value == 0 ? true :false);
        }
      }
    });

    controllerServiceArea?.addListener(() {
      double? extentAfter = controllerServiceArea?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (assignedServiceAreaRes != null &&
            assignedServiceAreaRes?.totalPages != pageNumber) {
          isShowLoadMore = true;
          pageNumber = pageNumber + 1;
          update();
          getAssignedInventoryServiceAreaList(subTabIndex.value == 0 ? true : false);
        }
      }
    });

  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));

      getAssignedInventoryCustomerList(subTabIndex.value == 0 ? true :false);
      update();
    }
  }

  applyFilter() {
    if (tabIndex == 0) {
      viewAllInventoryList();
    }
    else if(tabIndex == 1){
      if (searchController.text.isNullOrEmpty()) {
        isFilterApply = false;
        filterViewOpen = true;
        update();
        Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
            AppTheme.colorWhite, AppTheme.colorRed);
        return;
      }
      isFilterApply = true;
      filterViewOpen = false;
      page = 1;
      update();
      viewAssignedInventoryList();
    }
    else{
      // isFilterApply = true;
      // filterViewOpen = false;
      pageNumber = 1;
      update();
      getAssignedInventoryCustomerList(subTabIndex.value == 0 ? true :false);
    }
  }

  clearFilter() {
    if (tabIndex == 0) {
      pageAllInventory = 1;
      filterData=null;
      update();
      viewAllInventoryList();
    } else if(tabIndex==1) {
      searchController.clear();
      page = 1;
      isFilterApply = false;
      filterViewOpen = false;
      update();
      viewAssignedInventoryList();
    }else{
      // isFilterApply = true;
      // filterViewOpen = false;
      pageNumber = 1;
      update();
      getAssignedInventoryCustomerList(subTabIndex.value == 0 ? true :false);
    }
  }

  getAssignedInventoryCustomerList(bool isSerialized) {
    PageRequest normalRequest = PageRequest(page: pageNumber, pageSize: 10);
    // isLoading = true;
    // assignedCustomerList?.clear();
    // assignedCustomerListOrg?.clear();
    // update();
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewAssignedInventoryCustomerList(
      staffId: userDetail!.userId!,
      isSerialized: isSerialized,
      requestNormal: normalRequest,
      pageNumber: pageNumber,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (pageNumber == 1) {
            assignedCustomerList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryAssignedCustomerRes responseData =
                  InventoryAssignedCustomerRes.fromJson(map);
              if (responseData.responseCode == 200) {
                assignedCustomerRes = responseData;
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  assignedCustomerList?.addAll(responseData.dataList!);
                  assignedCustomerListOrg?.addAll(responseData.dataList!);
                }
              } else {
                if (pageNumber == 1) {
                  assignedCustomerList?.clear();
                  // assignedCustomerListOrg?.clear();
                }
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (pageNumber == 1) {
            assignedCustomerList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        // viewAssignedInventoryList();
        getAssignedInventoryPopList(isSerialized);
      },
      onError: (ResponseModel error) {
        if (pageNumber == 1) {
          assignedCustomerList?.clear();
          // assignedCustomerListOrg?.clear();
        }
        _handleApiError(error);
        // viewAssignedInventoryList();
        getAssignedInventoryPopList(isSerialized);
      },
    );
  }

  getAssignedInventoryPopList(bool isSerialized) {
    PageRequest normalRequest = PageRequest(page: pageNumber, pageSize: 10);
    // isLoading = true;
    // assignedCustomerList?.clear();
    // assignedCustomerListOrg?.clear();
    // update();
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewAssignedInventoryPopList(
      staffId: userDetail!.userId!,
      isSerialized: isSerialized,
      requestNormal: normalRequest,
      pageNumber: pageNumber,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (pageNumber == 1) {
            assignedPopList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryAssignedPopRes responseData =
              InventoryAssignedPopRes.fromJson(map);
              if (responseData.responseCode == 200) {
                assignedPopRes = responseData;
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  assignedPopList?.addAll(responseData.dataList!);
                  assignedPopListOrg?.addAll(responseData.dataList!);
                }
              } else {
                if (pageNumber == 1) {
                  assignedPopList?.clear();
                  // assignedCustomerListOrg?.clear();
                }
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (pageNumber == 1) {
            assignedPopList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getAssignedInventoryServiceAreaList(isSerialized);
      },
      onError: (ResponseModel error) {
        if (pageNumber == 1) {
          assignedPopList?.clear();
          // assignedCustomerListOrg?.clear();
        }
        _handleApiError(error);
        getAssignedInventoryServiceAreaList(isSerialized);
      },
    );
  }

  getAssignedInventoryServiceAreaList(bool isSerialized) {
    PageRequest normalRequest = PageRequest(page: pageNumber, pageSize: 10);
    // isLoading = true;
    // assignedCustomerList?.clear();
    // assignedCustomerListOrg?.clear();
    // update();
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewAssignedInventoryServiceAreaList(
      staffId: userDetail!.userId!,
      isSerialized: isSerialized,
      requestNormal: normalRequest,
      pageNumber: pageNumber,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (pageNumber == 1) {
            assignedServiceList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryAssignedServiceAreaRes responseData =
              InventoryAssignedServiceAreaRes.fromJson(map);
              if (responseData.responseCode == 200) {
                assignedServiceAreaRes = responseData;
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  assignedServiceList?.addAll(responseData.dataList!);
                  assignedServiceListOrg?.addAll(responseData.dataList!);
                }
              } else {
                if (pageNumber == 1) {
                  assignedServiceList?.clear();
                  // assignedCustomerListOrg?.clear();
                }
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (pageNumber == 1) {
            assignedServiceList?.clear();
            // assignedCustomerListOrg?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        viewAssignedInventoryList();
      },
      onError: (ResponseModel error) {
        if (pageNumber == 1) {
          assignedServiceList?.clear();
          // assignedCustomerListOrg?.clear();
        }
        _handleApiError(error);
        viewAssignedInventoryList();
      },
    );
  }

  viewAllInventoryList() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewAllInventoryList(
      inwardId: filterData != null && filterData!.inwardId != null
          ? filterData!.inwardId
          : null,
      itemStatus: filterData != null && filterData!.status != null
          ? filterData!.status
          : "",
      itemType: filterData != null && filterData!.itemType != null
          ? filterData!.itemType
          : "",
      ownerId: filterData != null && filterData!.owner != null
          ? filterData!.owner
          : "",
      ownerType: filterData != null && filterData!.ownerType != null
          ? filterData!.ownerType
          : "",
      ownership: filterData != null && filterData!.ownership != null
          ? filterData!.ownership
          : "",
      productId: filterData != null && filterData!.productId != null
          ? filterData!.productId
          : null,
      warrantyStatus: filterData != null && filterData!.warrantyStatus != null
          ? filterData!.warrantyStatus
          : "",
      serialNumber: filterData != null && filterData!.serialNumber != null
          ? filterData!.serialNumber
          : "",
      requestNormal: PageRequest(page: pageAllInventory, pageSize: 10),
      onSuccess: (ResponseModel responseModel) {
        isCallAllApi = false;
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              AllInventoryListRes responseData =
              AllInventoryListRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                allInventoryListRes = responseData;

                if (pageAllInventory == 1) {
                  allInventoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  for (var element in responseData.dataList!) {
                    element.selected=false;
                  }
                  allInventoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (pageAllInventory == 1) {
                  allInventoryList?.clear();
                }
                if (responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.INFO,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorBlueRView);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (pageAllInventory == 1) {
            allInventoryList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getAssignedInventoryPopList(subTabIndex.value == 0 ? true : false);
      },
      onError: (ResponseModel error) {
        isCallAllApi = false;
        if (pageAllInventory == 1) {
          allInventoryList?.clear();
        }
        getAssignedInventoryPopList(subTabIndex.value == 0 ? true : false);
        _handleApiError(error);
      },
    );
  }

  viewAssignedInventoryList() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CategorySearchReq searchReq = CategorySearchReq();

    if (isFilterApply) {
      List<Filters>? filters = [];
      filters.add(Filters(
          filterColumn: "any",
          filterCondition: "and",
          filterDataType: "",
          filterOperator: "equalto",
          filterValue: searchController.text));
      searchReq.filter = filters;
    }

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewAssignedInventoryList(
      staffId: userDetail!.userId!,
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
              AssignedInventoryDetailRes responseData =
                  AssignedInventoryDetailRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                assignedInventoryDetailRes = responseData;
                if (page == 1) {
                  assignedInventoryList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  assignedInventoryList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  assignedInventoryList?.clear();
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
            assignedInventoryList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        if (isCallAllApi) {
          viewAllInventoryList();
        }
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          assignedInventoryList?.clear();
        }
        _handleApiError(error);

        if (isCallAllApi) {
          viewAllInventoryList();
        }
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
