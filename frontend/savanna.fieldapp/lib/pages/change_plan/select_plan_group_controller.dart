import 'package:savbill/pages/change_plan/change_plan_provicer.dart';
import 'package:savbill/pages/change_plan/response/plan_group_plan_res.dart';
import 'package:savbill/pages/customer/model/response/postpaid_planlist_res.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class SelectPlanController extends GetxController {
  bool isLoading = false;

  int planGroupId = 0;
  List<PostpaidPlanDetail>? planList = [];

  @override
  void onInit() {
    super.onInit();
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.PLAN_GROUP_ID] != null) {
        planGroupId = arguments[Constant.PLAN_GROUP_ID];
      }
    }
    update();
    planGroupToPlan();
  }

  planGroupToPlan() {
    planList!.clear();
    isLoading = true;
    update();
    ChangePlanProvider().planGroupToPlan(
      planGroupId: planGroupId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanGroupPlanListRes responseData =
                  PlanGroupPlanListRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.planList != null &&
                    responseData.planList!.isNotEmpty) {
                  planList!.addAll(responseData.planList!);
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
