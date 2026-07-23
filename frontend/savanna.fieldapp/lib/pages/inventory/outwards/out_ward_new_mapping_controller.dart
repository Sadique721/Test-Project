import 'package:savbill/pages/inventory/inventory_management_provider.dart';
import 'package:savbill/pages/inventory/module/response/inward_mac_serial_item_res.dart';
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

import '../module/response/view_outward_list_res.dart';

class OutwardMappingController extends GetxController {
  bool isLoading = false, showMacAddress = false, changeData = false;
int page = 1;
  bool isShowLoadMore = false;
  ScrollController? controller;
  GetStorage getStorage = GetStorage();
  List<InwardMacSerialDataList>? inwardMacMapList = [];
  InwardMacSerialItemRes? inwardMacSerialItemRes;

  List<InwardMacSerialDataList>? inwardMacMapNewList = [];
  InwardMacSerialDataList? selectedOutwardMacSerialNo;

  OutwardDetail? outwardsDetail;

  TextEditingController macAddController = TextEditingController();
  TextEditingController serialNoController = TextEditingController();
  TextEditingController searchController = TextEditingController();

  final addInwardMapFormKey = GlobalKey<FormState>();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;

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
          page = page+1;
          update();
          getOutwardMacMappingData();
        }
      }
    });
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      // if (arguments[Constant.FROM] != null) {
      //   from = arguments[Constant.FROM];
      // }

      if (arguments[Constant.IM_DETAIL] != null) {
        outwardsDetail = arguments[Constant.IM_DETAIL];
        if (outwardsDetail != null &&
            outwardsDetail!.productId != null &&
            outwardsDetail!.productId!.productCategory != null) {
          if (outwardsDetail!.productId!.productCategory!.hasMac != null &&
              outwardsDetail!.productId!.productCategory!.hasMac == true) {
            showMacAddress = true;
          }
        }
      }
    }
    update();
    getOutwardMacMappingData();

  }

  getOutwardMacMappingData() {
    update();
    PageRequest pageRequest = PageRequest(
      page: page,
      pageSize: 15,
    );
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    InventoryManagementProvider().viewOutwardsMacMap(
      pageRequest: pageRequest,
      ownerId: outwardsDetail!.destinationId!,
      ownerType: outwardsDetail!.destinationType!,
      productId: outwardsDetail!.productId!.id!,
      outwardId: outwardsDetail!.id!,
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

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                inwardMacSerialItemRes = responseData;

                if (page == 1) {
                  inwardMacMapList?.clear();
                  // inwardMacMapNewList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  inwardMacMapList?.addAll(responseData.dataList!);
                  inwardMacMapNewList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  inwardMacMapList?.clear();
                  // inwardMacMapNewList?.clear();
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
            // inwardMacMapNewList?.clear();
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
          // inwardMacMapNewList?.clear();
        }
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
    inwardMacMapList!.clear();
    inwardMacMapList!.addAll(inwardMacMapNewList!);
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
