import 'package:savbill/pages/customer/model/response/customer_detail_response.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/caf_follow_up_provider.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/customer_caf_follow_up_res.dart';
import 'package:savbill/pages/customer_caf/followup/caf_follow_up/model/reschedule_follow_up_req.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:intl/intl.dart';

class ReScheduleFollowUpController extends GetxController {
  bool isLoading = false;
  TextEditingController remarkController = TextEditingController();
  TextEditingController followupNameController = TextEditingController();
  TextEditingController followupDateTimeController = TextEditingController();
  DateTime? selectedFollowUpDate;
  AutovalidateMode autoValidateMode = AutovalidateMode.disabled;
  CustomerDetail? customerDetail;

  DateFormat dateFormat = DateFormat("${Constant.DATE_FORMAT} ${Constant.APP_TIME_FORMAT}");
  DateFormat apiDateTimeFormat = DateFormat(Constant.API_DATE_TIME_FORMAT);
  String? followUpScheduleDateTime;
  List<DropdownDetail>? rescheduleRemarkList = [];
  DropdownDetail? selectedRescheduleRemark;
  CafFollowUpDataList? cafFollowUpDataItem;

  // int? followUpId;

  @override
  void onInit() {
    super.onInit();
    rescheduleRemarkList!.clear();
    rescheduleRemarkList!.add(DropdownDetail(
        id: Strings.confirmLetter,
        text: Strings.confirmLetter,
        type: Strings.reschedule));
    rescheduleRemarkList!.add(DropdownDetail(
        id: Strings.doNotCall,
        text: Strings.doNotCall,
        type: Strings.reschedule));
    rescheduleRemarkList!.add(DropdownDetail(
        id: Strings.expensivePackage,
        text: Strings.expensivePackage,
        type: Strings.reschedule));
    rescheduleRemarkList!.add(DropdownDetail(
        id: Strings.callRejectedClient,
        text: Strings.callRejectedClient,
        type: Strings.reschedule));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CUSTOMER_DETAIL] != null) {
        customerDetail = arguments[Constant.CUSTOMER_DETAIL];
      }

      if (arguments[Constant.FOLLOW_UP_DATA] != null) {
        cafFollowUpDataItem = arguments[Constant.FOLLOW_UP_DATA];
      }

      if (cafFollowUpDataItem != null &&
          cafFollowUpDataItem!.followUpName!.isNotEmpty) {
        followupNameController.text = cafFollowUpDataItem!.followUpName!;
      }

    }
    update();
  }
  rescheduleFollowUpCreate() {
    isLoading = true;
    update();
    RescheduleFollowUpReq request = RescheduleFollowUpReq(
        customersId: cafFollowUpDataItem!.customersId,
        followUpDatetime: followUpScheduleDateTime,
        followUpName: cafFollowUpDataItem!.followUpName,
        isMissed: cafFollowUpDataItem!.isMissed,
        isSend: cafFollowUpDataItem!.isSend,
        mvnoId: cafFollowUpDataItem!.mvnoId,
        remarks: selectedRescheduleRemark!.text,
        remarksTemp: remarkController.text,
        staffUserId: cafFollowUpDataItem!.staffUserId,
        status: cafFollowUpDataItem!.status);
    CafFollowUpProvider().rescheduleFollowUpCreate(
      cafFollowUpId: cafFollowUpDataItem!.id,
      remark: remarkController.text,
      request: request,
      onSuccess: (ResponseModel responseModel) {
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Get.back(result: true);
                Utils.showSnackbar(
                    Strings.SUCCESS,
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
