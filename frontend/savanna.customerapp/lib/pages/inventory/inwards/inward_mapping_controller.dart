import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/request/add_inward_mac_map_req.dart';
import 'package:savbill/pages/inventory/module/request/save_manual_mac_serial_req.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_map_list_res.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
import 'package:savbill/pages/inventory/module/response/view_inwards_list_res.dart';
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

class ViewInwardMappingController extends GetxController {
  bool isLoading = false, showMacAddress = false, changeData = false;

  GetStorage getStorage = GetStorage();
  ScrollController? controller;
  bool isShowLoadMore = false;
  int page = 1;

  // List<InwardMacMapDetail>? inwardMacMapList = [];
  List<InwardMacSerialDataList>? inwardMacMapList = [];
  InwardMacSerialItemRes? inwardMacSerialItemRes;

  List<InwardMacSerialDataList>? inwardMacMapNewList = [];
  InwardMacMapDetail? selectedInwardMacSerialNo;
  List<MacSerialListDTOList> macSerialListReq = [];
  InwardsDetail? inwardsDetail;

  TextEditingController macAddController = TextEditingController();
  TextEditingController serialNoController = TextEditingController();
  TextEditingController searchController = TextEditingController();

  final addInwardMapFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  String from = Strings.view;

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
          getInwardMacMappingData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }

      if (arguments[Constant.IM_DETAIL] != null) {
        inwardsDetail = arguments[Constant.IM_DETAIL];
        if (inwardsDetail != null &&
            inwardsDetail!.productId != null &&
            inwardsDetail!.productId!.productCategory != null) {
          if (inwardsDetail!.productId!.productCategory!.hasMac != null &&
              inwardsDetail!.productId!.productCategory!.hasMac == true) {
            showMacAddress = true;
          }
        }
      }
    }
    update();
    getInwardMacMappingData();
  }

  getInwardMacMappingData() {
    // inwardMacMapList!.clear();
    // isLoading = true;
    update();

    PageRequest pageRequest =
        PageRequest(page: page, pageSize: 20);

    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().getMacSerialNumberItemForInward(
      pageRequest: pageRequest,
      inwardId: inwardsDetail!.id!,
      productId: inwardsDetail!.productId!.id,
      ownerId: inwardsDetail!.destinationId,
      ownerType: inwardsDetail!.destinationType,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        isShowLoadMore = false;

        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              InwardMacSerialItemRes responseData =
                  InwardMacSerialItemRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                inwardMacSerialItemRes = responseData;
                if (page == 1) {
                  inwardMacMapList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inwardMacMapList?.addAll(responseData.dataList!);
                  inwardMacMapNewList?.addAll(responseData.dataList!);
                  // for (var element in inwardMacMapList!) {
                  //   macSerialListReq.add(MacSerialListDTOList(
                  //       macAddress: element.macAddress ?? "",
                  //       serialNumber: element.serialNumber ?? ""));
                  // }
                }
              }
              else {
                if (page == 1) {
                  inwardMacMapList?.clear();
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

  deleteInwardsMapData(InwardMacSerialDataList item, int index) {
    isLoading = true;
    update();
    InventoryManagementProvider().deleteInwardMacMap(
      id: item.id!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                changeData = true;
                getInwardMacMappingData();
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

  // void addInwardMacMapApiCall() {
  //   isLoading = true;
  //   update();
  //   AddInwardMacMapReq request = AddInwardMacMapReq(
  //       id: null,
  //       inwardId: inwardsDetail!.id!,
  //       macAddress: macAddController.text,
  //       serialNumber: serialNoController.text,
  //       outwardId: null,
  //       status: "ACTIVE");
  //   InventoryManagementProvider().addInwardMacMap(
  //     request: request,
  //     onSuccess: (ResponseModel responseModel) {
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           Map<String, dynamic> map = responseModel.result;
  //           BaseResponse responseData = BaseResponse.fromJson(map);
  //           if (responseData.responseCode == 200) {
  //             changeData = true;
  //             macAddController.clear();
  //             serialNoController.clear();
  //             autoValidateMode = AutovalidateMode.disabled;
  //             getInwardMacMappingData();
  //           } else {
  //             if (responseData.responseMessage!.isNotEmpty) {
  //               Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
  //                   AppTheme.colorWhite, AppTheme.colorRed);
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
  void saveManualMacSerialCall() {
    isLoading = true;
    update();
    // macSerialListReq.clear();
    // if(inwardMacMapList != null){
      // for (var element in inwardMacMapList!) {
        // macSerialListReq.add(MacSerialListDTOList(macAddress: element.macAddress,
        // serialNumber: element.serialNumber));
      // }
    // }
    // AddInwardMacMapReq request = AddInwardMacMapReq(
    //     id: null,
    //     inwardId: inwardsDetail!.id!,
    //     macAddress: macAddController.text,
    //     serialNumber: serialNoController.text,
    //     outwardId: null,
    //     status: "ACTIVE");

    // macSerialListReq.add(MacSerialListDTOList(
    //     macAddress: macAddController.text,
    //     serialNumber: serialNoController.text));


    log("SaveManualMacSerialReq==>>${jsonEncode(macSerialListReq)}");


    SaveManualMacSerialReq request = SaveManualMacSerialReq(
      inwardId: inwardsDetail!.id!,
      macSerialListDTOList: macSerialListReq,
    );

    log("InventoryManagementProvider===>${jsonEncode(request)}");

    InventoryManagementProvider().saveManualMacSerial(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              Get.back(result: true);
              Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorGreen);
              // changeData = true;
              // macAddController.clear();
              // serialNoController.clear();
              // autoValidateMode = AutovalidateMode.disabled;
              // getInwardMacMappingData();
            } else if (responseData.responseCode == 406) {
              changeData = true;
              macAddController.clear();
              serialNoController.clear();
              autoValidateMode = AutovalidateMode.disabled;
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
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

  void updateManualMacSerialCall(InwardMacSerialDataList? item, int? index) {
    // isLoading = true;
    update();
    macSerialListReq.clear();

    inwardMacMapList!.removeAt(index!);

    for (var element in inwardMacMapList!) {
      macSerialListReq.add(MacSerialListDTOList(macAddress: element.macAddress,serialNumber: element.serialNumber));
    }

      SaveManualMacSerialReq ? request = SaveManualMacSerialReq(
        inwardId: inwardsDetail!.id!,
        macSerialListDTOList: macSerialListReq,
      );


      log("SaveManualMacSerialReq==>${jsonEncode(request)}");

    InventoryManagementProvider().saveManualMacSerial(
      request: request,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200) {
              changeData = true;
              macAddController.clear();
              serialNoController.clear();
              autoValidateMode = AutovalidateMode.disabled;
              getInwardMacMappingData();
            }
            else if (responseData.responseCode == 406) {
              // changeData = true;
              // macAddController.clear();
              // serialNoController.clear();
              // autoValidateMode = AutovalidateMode.disabled;
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
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


  searchData(String value) {
    inwardMacMapList!.clear();
    if (value.isEmpty) {
      inwardMacMapList!.addAll(inwardMacMapNewList!);
    } else {
      // for (InwardMacMapDetail detail in inwardMacMapListOrg!) {
      for (InwardMacSerialDataList detail in inwardMacMapNewList!) {

        if (detail.serialNumber!.containsIgnoreCase(value)) {
          inwardMacMapList!.add(detail);
        }else if(detail.id.toString().containsIgnoreCase(value)){
          inwardMacMapList!.add(detail);
        }else if(detail.macAddress!.containsIgnoreCase(value)){
          inwardMacMapList!.add(detail);
        }else if(detail.condition!.containsIgnoreCase(value)){
          inwardMacMapList!.add(detail);
        }
      }
    }
    update();
  }

  clearData() {
    searchController.clear();
    inwardMacMapNewList!.clear();
    // inwardMacMapList!.addAll(inwardMacMapNewList!);
    update();
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
