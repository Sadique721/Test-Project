import 'dart:developer';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/all_ware_house_res.dart';
import 'package:savbill/pages/inventory/module/response/assigned_inventory_request_list_res.dart';
import 'package:savbill/pages/inventory/module/response/request_inventory_fulfilment_res.dart';
import 'package:savbill/pages/inventory/module/response/ware_house_new_list_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import '../../../util/strings.dart';

class ForwardInventoryController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();
  String from = Strings.add;
  String? selectProductCategoriesValue;
  AssignedInventoryDataList? item;
  int? forwardToReqId;

  ForwardInventoryController(this.item);
  // ForwardInventoryController();

  // GetAllWareHousesRes? wareHousesRes;
  // List<GetAllWareHousesRes>? allWareHouseList = [];

  List<WareHouseDataList> allWareHouseList =[];
  WareHouseDataList? wareHousesRes;

  List<FulfilmentProductMappings>? fulfilmentProductMapping = [];
  FulfilmentData? fulfilmentData;

  String? selectedRequesterTo;
  TextEditingController remarkReasonController = TextEditingController();
  TextEditingController requestInventoryNameController =
      TextEditingController();
  TextEditingController onBehalfOfController = TextEditingController();
  TextEditingController requesterController = TextEditingController();
  TextEditingController reasonController = TextEditingController();

  @override
  void onInit() {
    super.onInit();
    // getFulfilmentAllWareHouses();

    getArgumentData();
    // getAllWareHouses();
    // requestInventoryNameController.text = item!.requestInventoryName!;
    // onBehalfOfController.text = item!.onBehalfOf!;
    // requesterController.text = item!.requesterName!;
    // reasonController.text = item!.reason!;
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.IM_DETAIL] != null) {
        item = arguments[Constant.IM_DETAIL];
      }

    }
    update();

    if(item != null){
      if(item!.requestInventoryName!.isNotEmpty){
        requestInventoryNameController.text = item!.requestInventoryName!;
      }

      if(item!.onBehalfOf!.isNotEmpty){
        onBehalfOfController.text = item!.onBehalfOf!;
      }

      if(item!.requesterName!.isNotEmpty){
        requesterController.text = item!.requesterName!;
      }

      if(item!.reason!.isNotEmpty){
        reasonController.text = item!.reason!;
      }


      if (item!.requestToName != null) {
        for (WareHouseDataList element in allWareHouseList) {
          if (element.name!.equalsIgnoreCase(item!.requestToName!)) {
            wareHousesRes = element;
            break;
          }
        }
      }



    }

    // getFulfilmentAllWareHouses();
    getAllWareHouses();
  }

  // getAllWareHouses() {
  //   isLoading = true;
  //   allWareHouseList?.clear();
  //   update();
  //   InventoryManagementProvider().getAllWareHousesReq(
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           final jsonList = responseModel.result as List;
  //           allWareHouseList = jsonList
  //               .map((map) => GetAllWareHousesRes.fromJson(map))
  //               .toList();
  //           for (GetAllWareHousesRes element in allWareHouseList!) {
  //             if (element.name!.equalsIgnoreCase(item!.requestToName!)) {
  //               wareHousesRes = element;
  //               forwardToReqId = element.id;
  //               break;
  //             }
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

  getAllWareHouses() {
    isLoading = true;
    allWareHouseList.clear();
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
                allWareHouseList.addAll(responseData.dataList!);
                // for (var element in responseData.dataList!) {
                //   // if (onBehalfData!.id != element.id) {
                //     allWareHouseList.add(element);
                //   // }
                //
                //
                //
                // }


                if (item != null &&
                    item!.requestToName != null &&
                    item!.requestToName!.isNotEmpty) {
                  for (WareHouseDataList element in allWareHouseList) {
                    if (element.name!.equalsIgnoreCase(item!.requestToName!)) {
                      wareHousesRes = element;
                      break;
                    }
                  }
                }
                if(allWareHouseList.isEmpty || allWareHouseList == null){
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

  getFulfilmentAllWareHouses() {
    isLoading = true;
    fulfilmentProductMapping?.clear();
    fulfilmentData = null;
    update();
    log("fulFilmentId==>>${item!.id}");
    InventoryManagementProvider().fulfilmentInventoryRequest(
      fulFilmentId: item!.id,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            RequestInventoryFulfilmentRes responseData =
                RequestInventoryFulfilmentRes.fromJson(map);
            if (responseData.responseCode != null &&
                responseData.responseCode == 200) {
              fulfilmentData = responseData.data;

              requestInventoryNameController.text = fulfilmentData!.requestInventoryName!;
              onBehalfOfController.text = fulfilmentData!.onBehalfOf!;
              requesterController.text = fulfilmentData!.requesterName!;
              reasonController.text = fulfilmentData!.reason!;
              remarkReasonController.text = fulfilmentData!.remarks.toString() ?? "-";

              if (fulfilmentData!.requestInvenotryProductMappings!.isNotEmpty) {
                for (var element in fulfilmentData!.requestInvenotryProductMappings!) {
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
        getAllWareHouses();
      },
      onError: (ResponseModel error) {
        getAllWareHouses();
        _handleApiError(error);
      },
    );
  }

  void forwardInventoryRequestApiCall() {
    isLoading = true;
    update();

    InventoryManagementProvider().saveForwardInventory(
      reqId: item != null ? item!.id! : null,
      forwardToReqId: forwardToReqId,
      remarks: remarkReasonController.text.isNotEmpty
          ? remarkReasonController.text
          : null,
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
