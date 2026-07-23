
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_provider.dart';
import 'package:savbill/pages/customer_caf/followup/model/caf_remark_follow_up_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

class RemarkFollowUpController extends GetxController {
  bool isLoading = false;
  // ScrollController? controller;
  // int page = 1;
  // bool isShowLoadMore = false;
  List<RemarkFollowUpDataList>? remarkFollowUpDataList = [];
  CafRemarkFollowUpRes? remarkFollowUpDataRes;
  TextEditingController remarkController = TextEditingController();
  CustomerDetail? customerDetail;
  int? followUpId;
  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {

      if(arguments[Constant.CUSTOMER_DETAIL] != null){
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      if(arguments[Constant.FOLLOW_UP_ID] != null){
        followUpId = arguments[Constant.FOLLOW_UP_ID];
      }

      getCafFollowUpRemark(followUpId);
    }
    update();
  }

  getCafFollowUpRemark(int? followUpId) {
    remarkFollowUpDataList!.clear();
    isLoading = true;
    update();
    CafFollowUpProvider().getCafFollowUpRemark(
      followUpId: followUpId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CafRemarkFollowUpRes responseData =
              CafRemarkFollowUpRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  remarkFollowUpDataList?.addAll(responseData.dataList!);
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

  addRemarkFollowUp(String? remarks) {
      isLoading = true;
    update();
    CafFollowUpProvider().addRemarkFollowUp(
      cafFollowUpId: followUpId,
      remark : remarks,
      mvnoId: customerDetail!.mvnoId,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorGreen);
                remarkController.clear();
                getCafFollowUpRemark(followUpId);
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
        isLoading = false;
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