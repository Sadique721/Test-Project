import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/plan_service_by_customer_res.dart';
import 'package:savbill/pages/customer_inventory/document/cust_doc_view_list.dart';
import 'package:savbill/pages/customer_inventory/inventory_assign_dialog.dart';
import 'package:savbill/pages/customer_inventory/inventory_provider.dart';
import 'package:savbill/pages/customer_inventory/request/approve_inventory_req.dart';
import 'package:savbill/pages/customer_inventory/request/inventory_list_req.dart';
import 'package:savbill/pages/customer_inventory/request/replace_inventory_customer_req.dart';
import 'package:savbill/pages/customer_inventory/response/approve_reject_inventory_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_all_customer_inventory_list_res.dart';
import 'package:savbill/pages/customer_inventory/response/get_approve_inventory_req_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_documentList_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_item_delete_id_res.dart';
import 'package:savbill/pages/customer_inventory/response/inventory_list_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class InventoryDetailController extends GetxController
    implements InventoryAssignAction {
  bool isLoading = false;
  List<DocumentLis>? documentList = [];
  List<InventoryDetail>? inventoryDetail = [];
  int customerId = 0;
  int serviceAreaId = 0;
  String customerName = "",customerFirstName="", type = "";
  bool checkBtnClickEvent = false;
  bool? editInventory = false;
  bool? editSTBCradInventory = false;
  bool? removeRemarkSubmitted = false;
  bool? approveRemoveFlag = false;
  bool? rejectRemoveFlag = false;
  int? removeItemId, removeCustInventoryId, removeId;

  ScrollController? controller;
  int page = 1;
  bool isShowLoadMore = false;
  InventoryListRes? inventoryListRes;
  GetAllCustomerInventoryListRes? getAllCustomerInventoryListRes;
  List<int>? approveIdList = [];

  List<CustomerInventoryDataList>? inventoryDataList = [];

  List<ApproveRejectInventory>? approveRejectInventoryList = [];

  CustomerInventoryDataList? customerInventoryDataList;

  ApproveInventoryData? approveInventoryData;

  InventoryItemData? inventoryItemData;
  GetStorage getStorage = GetStorage();
  UserDetail? userDetail;
  List<ReplaceInventoryReq> replaceInventory = [];
  List<CustomerPlanServiceDetail>? planServiceList = [];
  bool? downloadDocumentId = true;

  double? actualProductPrice, newProductPrice;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        // if (inventoryListRes != null && inventoryListRes!.totalPages != page) {
        if (getAllCustomerInventoryListRes != null &&
            getAllCustomerInventoryListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          // getCustomerInventoryDetail();
          getAllCustomerInventoryListApi();
          update();
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
      update();
    }
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_NAME] != null) {
        customerName = arguments[Constant.CUSTOMER_NAME];
      }
      if (arguments[Constant.CUSTOMER_FIRST_NAME] != null) {
        customerFirstName = arguments[Constant.CUSTOMER_FIRST_NAME];
      }
      if (arguments[Constant.CUSTOMER_ID] != null) {
        customerId = arguments[Constant.CUSTOMER_ID];
        // getCustomerInventoryDetail();
        getAllCustomerInventoryListApi();
      }
      if (arguments[Constant.SERVICE_AREA_ID] != null) {
        serviceAreaId = arguments[Constant.SERVICE_AREA_ID];
      }

      if (arguments[Constant.CUSTOMER_TYPE] != null) {
        type = arguments[Constant.CUSTOMER_TYPE];
      }
    }
    update();
  }

  getAllCustomerInventoryListApi() {
    inventoryDataList!.clear();
    isLoading = true;
    update();
    CustomerProvider().getAllCustomerInventoryList(
      custId: customerId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetAllCustomerInventoryListRes responseData =
                  GetAllCustomerInventoryListRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inventoryDataList?.addAll(responseData.dataList!);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        getPlanServiceData();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getPlanServiceData();
      },
    );
  }

  getApproveReqInventoryApi(
      {bool? approveRequest,
      String? remark,
      required String? status,
      required BuildContext context,
      required int? itemId}) {
    isLoading = true;
    update();
    ApproveInventoryReq approveInventoryReq = ApproveInventoryReq(
        approveReq: approveRequest, requestApproveId: approveIdList);
    CustomerProvider().getApproveRequestInventory(
      nextStaffId: userDetail!.userId,
      remark: remark,
      request: approveInventoryReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApproveRejectInventoryRes responseData =
                  ApproveRejectInventoryRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  approveRejectInventoryList!.addAll(responseData.dataList!);
                  showAssignStaffDialog(
                      responseData.dataList!, status, context, itemId);
                  // Utils.showSnackbar(
                  //     Strings.successfully,
                  //     responseData.responseMessage,
                  //     AppTheme.colorWhite,
                  //     AppTheme.colorGreen);
                  // isLoading = true;
                  // getAllCustomerInventoryListApi();
                } else {
                  if (status!.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                        AppTheme.colorWhite, AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  }
                  getAllCustomerInventoryListApi();
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  showAssignStaffDialog(List<ApproveRejectInventory> item, String? staffStatus,
      BuildContext context, int? itemId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return InventoryAssignDialog(
            itemsOrgLst: item,
            entityId: itemId,
            staffStatus: staffStatus,
            inventoryAssignAction: this,
          );
        });
  }

  approveReplaceInventoryApi(
      bool? approveRequest, CustomerInventoryDataList item) {
    ReplaceInventoryReq replaceInventoryReq = ReplaceInventoryReq(
      oldMacMappingId: item.inOutWardMACMapping![0].id,
      newMacMappingId: item.inOutWardMACMapping![1].id,
    );
    replaceInventory.add(replaceInventoryReq);
    isLoading = true;
    update();
    InventoryProvider().customerApproveReplaceInventory(
      isApproveRequest: approveRequest,
      isbillAble: true,
      request: replaceInventory,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetApproveInventoryRequestRes responseData =
                  GetApproveInventoryRequestRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null &&
                    !responseData.data!.isNullOrEmpty()) {
                  Utils.showSnackbar(
                      Strings.successfully,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                  isLoading = true;
                  getAllCustomerInventoryListApi();
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  getCustomerInventoryDetail() {
    InventoryFilters filter =
        InventoryFilters(filterValue: customerId, filterColumn: "customerId");
    List<InventoryFilters> filters = [filter];
    InventoryListReq request = InventoryListReq(
        filters: filters,
        page: page,
        pageSize: 10,
        sortBy: "createdate",
        sortOrder: 0);

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    //inventoryDetail!.clear();

    InventoryProvider().getCustomerInventoryList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          inventoryDetail?.clear();
        }
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              inventoryListRes = InventoryListRes.fromJson(map);

              if (inventoryListRes!.responseCode == 200) {
                if (inventoryListRes!.dataList != null &&
                    inventoryListRes!.dataList!.isNotEmpty) {
                  inventoryDetail!.addAll(inventoryListRes!.dataList!);
                }
              } else {
                if (inventoryListRes!.responseMessage != null &&
                    inventoryListRes!.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      inventoryListRes!.responseMessage,
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
        getAllCustomerInventoryListApi();
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          inventoryDetail?.clear();
        }
        _handleApiError(error);
        getAllCustomerInventoryListApi();
      },
    );
  }

  inventoryItemIdCall(
      {required int itemId,
      required String remark,
      required int customerId,
      required int macMappingId,
      required int customerInventoryId,
      required bool isFlag}) {
    isLoading = true;
    update();
    InventoryProvider().getCustomerInventoryItemId(
      itemId: itemId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryItemDeleteIdRes responseData =
                  InventoryItemDeleteIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null &&
                    !responseData.data!.isNullOrEmpty()) {
                  inventoryItemData = responseData.data!;
                  // inventoryItemDeleteCall(
                  //     remark: remark,
                  //     macMappingId: macMappingId,
                  //     customerId: customerId,
                  //     customerInventoryId: customerInventoryId,
                  //     isFlag: isFlag);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        getAllCustomerInventoryListApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllCustomerInventoryListApi();
      },
    );
  }

  inventoryItemDeleteCall({
    required String remark,
    required int macMappingId,
    required int customerId,
    required int customerInventoryId,
    required bool isApprove,
    required int nextStaffId,
  }) {
    isLoading = true;
    update();
    InventoryProvider().getCustomerInventoryItemDelete(
      remark: remark,
      macMappingId: macMappingId,
      customerId: customerId,
      customerInventoryId: customerInventoryId,
      nextStaffId: nextStaffId,
      isApprove: isApprove,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryItemDeleteIdRes responseData =
                  InventoryItemDeleteIdRes.fromJson(map);
              if (responseData.responseCode == 0 || responseData.status == 0) {
                if (responseData.data != null &&
                    !responseData.data!.isNullOrEmpty()) {
                  inventoryItemData = responseData.data!;
                  getAllCustomerInventoryListApi();
                  Utils.showSnackbar(
                      Strings.successfully,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  inventoryRemoveById({required int itemId, required int customerInventoryId}) {
    isLoading = true;
    update();
    InventoryProvider().getRemoveInventoryById(
      itemId: itemId,
      customerInventoryId: customerInventoryId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryItemDeleteIdRes responseData =
                  InventoryItemDeleteIdRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.data != null &&
                    !responseData.data!.isNullOrEmpty()) {
                  inventoryItemData = responseData.data!;
                  if (inventoryItemData!.refundFlag == true) {
                    if (inventoryItemData!.warranty!
                            .equalsIgnoreCase("InWarranty") ||
                        inventoryItemData!.warranty!
                            .equalsIgnoreCase("Expired")) {
                      actualProductPrice =
                          inventoryItemData!.productRefundAmount;
                      newProductPrice = inventoryItemData!.productRefundAmount;
                    } else {
                      acceptRemoveItem();
                    }
                  } else {
                    acceptRemoveItem();
                  }
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        // getAllCustomerInventoryListApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getAllCustomerInventoryListApi();
      },
    );
  }

  generateRemoveInventoryRequestApiCall(
      {required int? macMappingId,
      required int? customerId,
      required int? customerInventoryId,
      required bool isFlag,
      required String? reviseCharge}) {
    isLoading = true;
    update();
    InventoryProvider().generateRemoveInventoryRequestCall(
      macMappingId: macMappingId,
      customerId: customerId,
      customerInventoryId: customerInventoryId,
      isFlag: isFlag,
      revisedCharge: reviseCharge,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                approveRemoveFlag = true;
                rejectRemoveFlag = true;
                getAllCustomerInventoryListApi();
              } else {
                approveRemoveFlag = false;
                rejectRemoveFlag = false;
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  getPlanServiceData() {
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

              if (responseData.responseCode == 200 ||
                  responseData.responseCode == 0) {
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
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        // getPaymentOwnerDataApi();
        // getPaymentOwnerStaffDataApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        // getPaymentOwnerDataApi();
        // getPaymentOwnerStaffDataApi();
      },
    );
  }

  assignNonSerializedItemToCustomerApi() {
    InventoryFilters filter =
        InventoryFilters(filterValue: customerId, filterColumn: "customerId");
    List<InventoryFilters> filters = [filter];
    InventoryListReq request = InventoryListReq(
        filters: filters,
        page: page,
        pageSize: 10,
        sortBy: "createdate",
        sortOrder: 0);

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    //inventoryDetail!.clear();

    InventoryProvider().getCustomerInventoryList(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        if (page == 1) {
          inventoryDetail?.clear();
        }
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              inventoryListRes = InventoryListRes.fromJson(map);

              if (inventoryListRes!.responseCode == 200) {
                if (inventoryListRes!.dataList != null &&
                    inventoryListRes!.dataList!.isNotEmpty) {
                  inventoryDetail!.addAll(inventoryListRes!.dataList!);
                }
              } else {
                if (inventoryListRes!.responseMessage != null &&
                    inventoryListRes!.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      inventoryListRes!.responseMessage,
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
        getAllCustomerInventoryListApi();
      },
      onError: (ResponseModel error) {
        isShowLoadMore = false;
        if (page == 1) {
          inventoryDetail?.clear();
        }
        _handleApiError(error);
        getAllCustomerInventoryListApi();
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

  // void changeCustomerStatusUpPopup(int index) {}

  inventoryReactiveBoxApi() {
    isLoading = true;
    update();
    ApproveInventoryReq approveInventoryReq =
        ApproveInventoryReq(requestApproveId: approveIdList);
    CustomerProvider().getInventoryReactivateBox(
      request: approveInventoryReq,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.successfully,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
        getAllCustomerInventoryListApi();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getAllCustomerInventoryListApi();
      },
    );
  }

  setBtnClickEvent(bool status) {
    checkBtnClickEvent = status;
    update();
  }

  void acceptRemoveItem() {
    editInventory = false;
    editSTBCradInventory = false;
    removeRemarkSubmitted = true;
    generateRemoveInventoryRequestApiCall(
        macMappingId: removeId,
        customerInventoryId: removeCustInventoryId,
        customerId: customerId,
        isFlag: false,
        reviseCharge: "");
  }

  void downloadDocument(CustomerInventoryDataList? inventoryFileData) {
    // Assign inventory ID and file data
    // var inventoryFileData = Map<String, dynamic>.from(inventory);

    // Split filename and unique name lists
    List<String> filenameList =
        (inventoryFileData!.filename as String).split(',');
    List<String> uniqueNamesList =
        (inventoryFileData.uniquename as String).split(',');

    log("filenameList==>>>${filenameList.length}");
    log("uniqueNamesList==>>>${uniqueNamesList.length}");

    // Check for mismatch in lengths
    if (filenameList.length != uniqueNamesList.length) {
      print("The number of filenames and unique names do not match!");
      return;
    }
    // Initialize fileDetails as an empty list
    inventoryFileData.fileDetails = [];
    // Populate fileDetails
    for (int i = 0; i < filenameList.length; i++) {
      inventoryFileData.fileDetails.add({
        'filename': filenameList[i].trim(),
        'uniquename': uniqueNamesList[i].trim(),
      });
    }
    // Set downloadDocumentId to true
    downloadDocumentId = true;

    if (inventoryFileData.filename != null &&
        inventoryFileData.uniquename != null) {
      openCustomerDocViewInventoryScreen(
          downloadDocumentId: inventoryFileData.id,
          inventoryFileData: inventoryFileData);
    }

    print("File task_catg_detail_screen processed: ${inventoryFileData.fileDetails}");
  }

  closeDownloadDocumentId() {
    downloadDocumentId = false;
    Get.back();
    Get.back();
    // this.getCustomerAssignedList();
  }

  void downloadDoc(String filename, int id, String uniqueName) {
    print("Downloading $filename with ID $id and Unique Name $uniqueName");
  }

  void viewDoc(String filename, int id, String uniqueName) {
    print("Viewing $filename with ID $id and Unique Name $uniqueName");
  }

  void deleteDoc(String filename, int id, String uniqueName) {
    print("Deleting $filename with ID $id and Unique Name $uniqueName");
  }

  void closeDialog() {
    downloadDocumentId = false;
    update();
  }

  getInventoryDocumentList(int? ID) {
    documentList?.clear();
    isLoading = true;
    update();
    InventoryProvider().getInventoryDocumentViewCall(
      inventoryId: ID!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InventoryDocumentListRes responseData =
              InventoryDocumentListRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  documentList?.addAll(responseData.dataList!);
                }
              } else if (responseData.responseCode == 204) {
              } else if (responseData.responseCode == 404) {
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

  assignStaffInventory(
      int? entityId, int? nextAssignStaff, bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignInventoryFromStaffList}?entityId=$entityId&eventName=CUSTOMER_INVENTORY_ASSIGN&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest&isAssignPairItem=false";
    isLoading = true;
    update();
    InventoryProvider().assignInventoryFromStaffList(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          getAllCustomerInventoryListApi();
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  handleApiError(ResponseModel error) {
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

  openCustomerDocViewInventoryScreen(
      {int? downloadDocumentId,
      CustomerInventoryDataList? inventoryFileData}) async {
    var result = await Get.to(CustDocViewList(), arguments: {
      Constant.CUSTOMER_DOCUMENT_VIEW_ID: downloadDocumentId,
      Constant.INVENTORY_FILE_DATA: inventoryFileData,
    });
    if (result != null && result == true) {
      getAllCustomerInventoryListApi();
      update();
    }
  }

  assignInventoryEveryStaff({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.assignInventoryEveryStaff}?entityId=$entityId&eventName=CUSTOMER_INVENTORY_ASSIGN&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    InventoryProvider().assignInventoryEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          getAllCustomerInventoryListApi();
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  @override
  void inventoryAssignBtnAction(
      {ApproveRejectInventory? selectedItem,
      bool? isStaffSelected,
      int? entityId,
      String? approveRejectStatus}) {
    Get.back();
    if (isStaffSelected == true) {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignStaffInventory(entityId, selectedItem!.id, true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffInventory(entityId, selectedItem!.id, false);
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignInventoryEveryStaff(entityId: entityId, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignInventoryEveryStaff(entityId: entityId, isApprovedRequest: false);
      }
    }
  }
}
