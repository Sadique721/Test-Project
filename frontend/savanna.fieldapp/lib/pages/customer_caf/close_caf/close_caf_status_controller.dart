import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_provider.dart';
import 'package:savbill/pages/customer_caf/response/reject_reason_caf_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';

class CloseCafStatusController extends GetxController {
  bool isLoading = false;
  TextEditingController remarkController = TextEditingController();
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  CustomerDetail? customerDetail;

  List<CloseCafContentList>? closeCafContentList = [];
  List<RejectSubReasonDtoList>? rejectSubReasonDtoList = [];
  RejectSubReasonDtoList? selectedRejectedSubReason;
  RxBool isRejectedSubReason = false .obs;
  CloseCafContentList? selectRejectedReason;

  // int? followUpId;

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }
      getCloseRejectReasonCaf();

    }
    update();
  }
  getCloseRejectReasonCaf() {
    isLoading = true;
    closeCafContentList!.clear();
    CustomerProvider().getRejectReasonCaf(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              RejectReasonCafRes responseData = RejectReasonCafRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.rejectReasonList != null &&
                    responseData.rejectReasonList!.content!.isNotEmpty) {
                  closeCafContentList!.addAll(responseData.rejectReasonList!.content!);
                }
              } else {
                if (responseData.error != null &&
                    responseData.error!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.error,
                      AppTheme.colorWhite, AppTheme.colorRed);
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


  postCloseCAFCall() {
    isLoading = true;
    update();
    CafFollowUpProvider().postCloseCAF(
      cafID: customerDetail!.id,
      rejectReasonId: selectRejectedReason!.id,
      rejectSubReasonId: selectedRejectedSubReason!.id,
      remark: remarkController.text,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.status == 200) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.msg,
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
