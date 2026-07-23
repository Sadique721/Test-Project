import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/network_management/ip/create_ip_screen.dart';
import 'package:savbill/pages/network_management/model/response/delete_ip_management_res.dart';
import 'package:savbill/pages/network_management/model/response/get_ip_management_list_res.dart';
import 'package:savbill/pages/network_management/model/response/ip_management_list_res.dart';
import 'package:savbill/pages/network_management/network_management_provider.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';

class IpPoolController extends GetxController {
  bool isLoading = false,
      isShowLoadMore = false,
      isLoadFilterData = false;

  ScrollController? controller;
  int page = 1;
  GetStorage getStorage = GetStorage();
  IpManagementListRes? ipManagementListRes;

  List<IpManagementDataList>? ipManagementList = [];
  IpManagementDataList? selectedIpManagementData;

  IpManagementData? ipManagementData;



  @override
  void onInit() {
    super.onInit();
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (ipManagementListRes != null && ipManagementListRes!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getDeviceListData();
        }
      }
    });
    getDeviceListData();
    // getDeviceType();
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }



  clearFilter() {
    selectedIpManagementData = null;
    ipManagementList!.clear();
    page = 1;
    update();
    getDeviceListData();
  }


  getDeviceListData() {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    NetworkManagementProvider().getIPPoolListManagement(
      requestNormal: PageRequest(
        page: page,
        pageSize: Constant.RECENT_LIMIT,
      ),
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              IpManagementListRes responseData = IpManagementListRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                ipManagementListRes = responseData;
                if (page == 1) {
                  ipManagementList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ipManagementList?.addAll(responseData.dataList!);
                }
              }else if(responseData.responseCode != null &&
                  responseData.responseCode == 404){
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              } else {
                if (page == 1) {
                  ipManagementList?.clear();
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
            ipManagementList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.INFO, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorBlueRView);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          ipManagementList?.clear();
        }
        handleApiError(error);
      },
    );
  }

  getIpListData(int? ipPoolId) {
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    NetworkManagementProvider().getIPPoolListWithId(
      ipPoolId: ipPoolId,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              GetIpManagementListRes responseData = GetIpManagementListRes.fromJson(map);
              if (responseData.responseCode != null && responseData.responseCode == 200) {
                if(responseData.data !=null){
                  ipManagementData = responseData.data;
                }
                openCreateIpManagementScreen(Strings.update_text,ipManagementData);
              }else if (responseData.responseCode != null && responseData.responseCode == 400) {
                Utils.showSnackbar(
                    Strings.INFO,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorBlueRView);
              }
              else {
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
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.INFO, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorBlueRView);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          ipManagementList?.clear();
        }
        handleApiError(error);
      },
    );
  }


  deleteDevice(IpManagementDataList item, int index) {
    isLoading = true;
    update();
    NetworkManagementProvider().deleteIpManagement(
      request: item,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DeleteIpManagementRes responseData = DeleteIpManagementRes.fromJson(map);
              if (responseData.responseCode != null &&
                      responseData.responseCode == 200) {
                ipManagementList!.removeAt(index);
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
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


  openCreateIpManagementScreen(String from, IpManagementData? item) async {
    var result = await Get.to(CreateIpScreen(), arguments: {
      Constant.FROM: from, Constant.IM_DETAIL: item
    });
    if (result != null && result == true) {
      clearFilter();
    }
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
}