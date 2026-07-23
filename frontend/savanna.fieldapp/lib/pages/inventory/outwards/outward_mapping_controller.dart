import 'dart:developer';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/pages/inventory/module/response/view_outward_list_res.dart';
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

class ViewOutwardMappingController extends GetxController {
  bool isLoading = false, changeData = false;
  int page = 1;
  bool isShowLoadMore = false;
  ScrollController? controller;
  GetStorage getStorage = GetStorage();

  InwardMacSerialItemRes? inwardMacSerialItemRes;
  List<InwardMacSerialDataList>? inwardMacMapList = [];
  List<InwardMacSerialDataList>? inwardMacMapListOrg = [];
  OutwardDetail? outwardsDetail;

  TextEditingController serialNumberController = TextEditingController();
  TextEditingController macAddressController = TextEditingController();
  TextEditingController searchController = TextEditingController();
  int? outwardId, inwardId;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (inwardMacSerialItemRes != null &&
            inwardMacSerialItemRes?.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getOutwardMacMappingData(
            ownerId: outwardsDetail!.sourceId,
            ownerType: outwardsDetail!.sourceType,
            productId: outwardsDetail!.productId!.id,
          );
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.IM_DETAIL] != null) {
        outwardsDetail = arguments[Constant.IM_DETAIL];
        if (outwardsDetail != null) {
          if (outwardsDetail!.id != null) {
            outwardId = outwardsDetail!.id;
          }
          if (outwardsDetail!.inwardId != null &&
              outwardsDetail!.inwardId!.id != null) {
            inwardId = outwardsDetail!.inwardId!.id;
          }
        }
      }
    }
    update();
    getOutwardMacMappingData(
      ownerId: outwardsDetail!.sourceId,
      ownerType: outwardsDetail!.sourceType,
      productId: outwardsDetail!.productId!.id,
    );
  }

  searchData(String value) {
    inwardMacMapList!.clear();
    if (value.isEmpty) {
      inwardMacMapList!.addAll(inwardMacMapListOrg!);
    } else {

      // for (InwardMacMapDetail detail in inwardMacMapListOrg!) {
      for (InwardMacSerialDataList detail in inwardMacMapListOrg!) {
        final input = value.toLowerCase();

        if (detail.serialNumber != null &&
            detail.serialNumber!.toLowerCase().startsWith(input)) {
          inwardMacMapList!.add(detail);
        } else if (detail.id.toString().toLowerCase().startsWith(input)) {
          inwardMacMapList!.add(detail);
        } else if (detail.macAddress != null &&
            detail.macAddress!.toLowerCase().startsWith(input)) {
          inwardMacMapList!.add(detail);
        } else if (detail.condition != null &&
            detail.condition!.toLowerCase().startsWith(input)) {
          inwardMacMapList!.add(detail);
        }
      }
    }
    update();
  }

  clearData() {
    searchController.clear();
    inwardMacMapList!.clear();
    inwardMacMapList!.addAll(inwardMacMapListOrg!);
    update();
  }

  getOutwardMacMappingData({int? ownerId, int? productId, String? ownerType}) {
    // inwardMacMapList!.clear();
    // isLoading = true;
    update();
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }

    PageRequest pageRequest = PageRequest(page: page, pageSize: 20);
    InventoryManagementProvider().getItemForOutwardMacSerialNo(
      ownerId: ownerId,
      ownerType: ownerType,
      productId: productId,
      pageRequest: pageRequest,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InwardMacSerialItemRes responseData =
                  InwardMacSerialItemRes.fromJson(map);

              if ((responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
                inwardMacSerialItemRes = responseData;
                if (page == 1) {
                  inwardMacMapList?.clear();
                  // inwardMacMapNewList?.clear();
                }

                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inwardMacMapList?.addAll(responseData.dataList!);
                  inwardMacMapListOrg!.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  inwardMacMapList?.clear();
                }
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
          if (page == 1) {
            inwardMacMapList?.clear();
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
          inwardMacMapList?.clear();
        }
        _handleApiError(error);
      },
    );
  }

  // getOutwardMacMappingByInwards() {
  //   if (inwardId == null) {
  //     return;
  //   }
  //   isLoading = true;
  //   update();
  //   InventoryManagementProvider().viewOutwardsMacMapByInwards(
  //     inwardId: inwardId!,
  //     onSuccess: (ResponseModel responseModel) {
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             InwardMacMapListRes responseData =
  //             InwardMacMapListRes.fromJson(map);
  //             if ((responseData.status != null && responseData.status == 200) ||
  //                 (responseData.responseCode != null &&
  //                     responseData.responseCode == 200)) {
  //               if (responseData.dataList != null &&
  //                   responseData.dataList!.isNotEmpty) {
  //                 inwardMacMapList?.addAll(responseData.dataList!);
  //                 inwardMacMapListOrg!.addAll(responseData.dataList!);
  //               }
  //             } else {
  //               if (responseData.responseMessage!=null &&responseData.responseMessage!.isNotEmpty) {
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
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       _handleApiError(error);
  //     },
  //   );
  // }

  int getSelectedMac() {
    if (inwardMacMapList == null || inwardMacMapList!.isEmpty) {
      return 0;
    }

    return inwardMacMapList!
        .where((element) => element.selected == true)
        .length;
  }

  void addOutwardMacMapApiCall() {
    searchController.clear();
    searchData("");
    List<InwardMacSerialDataList> inwardMacMapSelected = [];
    if (inwardMacMapList != null && inwardMacMapList!.isNotEmpty) {
      inwardMacMapList!.forEach((element) {
        if (element.selected == true) {
          element.outwardId = outwardsDetail!.id;
          inwardMacMapSelected.add(element);
        }
      });
    }

    if (inwardMacMapSelected.isEmpty) {
      Utils.showSnackbar(
          Strings.ERROR,
          "Please select at-lease one item of the list.!",
          AppTheme.colorWhite,
          AppTheme.colorRed);
      return;
    }
    isLoading = true;
    update();
    InventoryManagementProvider().outwardMacMapReq(
      request: inwardMacMapSelected,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              changeData = true;
              Get.back(result: true);
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              } else {
                Utils.showSnackbar(
                    Strings.ERROR,
                    "Something wrong, get empty message from the server side.!",
                    AppTheme.colorWhite,
                    AppTheme.colorRed);
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
