import 'package:savbill/pages/ticket_system/model/response/condition_res.dart';
import 'package:savbill/pages/ticket_system/model/response/sub_problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:get/get.dart';

class AddConditionController extends GetxController {
  bool isLoading = false;
  List<ConditionDetail>? fieldList = [];

  List<TatQueryFieldMappingList>? tatQueryFieldMappingList = [];

  List<String>? operatorList = [];

  List<String>? conditionList = [];
  int uId = 1;

  @override
  void onInit() {
    super.onInit();
    operatorList!.add(Strings.equal_to);
    operatorList!.add(Strings.less_than_or_equal_to);
    operatorList!.add(Strings.greater_than_or_equal_to);
    operatorList!.add(Strings.less_than);
    operatorList!.add(Strings.greater_than);
    operatorList!.add(Strings.not_equal_to);

    conditionList!.add(Strings.and);
    conditionList!.add(Strings.or);
    update();
    getAllCaseCondition();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.CONDITION] != null) {
        List<TatQueryFieldMappingList>? mappingList =
            arguments[Constant.CONDITION];
        if (mappingList != null && mappingList.isNotEmpty) {
          tatQueryFieldMappingList!.addAll(mappingList);
          uId = tatQueryFieldMappingList!.length + 1;
        }
      }
    }
    update();

  }

  addConditionItem(String from) {
    bool isValid = true;
    String message = "";
    if (tatQueryFieldMappingList != null &&
        tatQueryFieldMappingList!.isNotEmpty) {
      int i = 0;
      for (TatQueryFieldMappingList element in tatQueryFieldMappingList!) {
        i++;
        if (i == tatQueryFieldMappingList!.length) {
          if (element.selectedField == null ||
              element.selectedOperator == null ||
              (element.queryValue == null || element.queryValue!.isEmpty)) {
            message = "Please Enter Details.";
            isValid = false;
            break;
          }
        } else {
          if (element.selectedField == null ||
              element.selectedOperator == null ||
              (element.queryValue == null || element.queryValue!.isEmpty) ||
              element.selectedCondition == null) {
            if (element.selectedCondition != null) {
              message = "Please select condition.";
            } else {
              message = "Please Enter Details.";
            }
            isValid = false;
            break;
          }
        }
      }
    } else {
      isValid = false;
    }
    if (isValid) {
      if (from.equalsIgnoreCase(Strings.add)) {
        uId = uId + 1;
        tatQueryFieldMappingList!.add(TatQueryFieldMappingList(uId: uId));
        update();
      } else {
        Get.back(result: tatQueryFieldMappingList);
      }
    } else {
      Utils.showSnackbar(
          Strings.ERROR, message, AppTheme.colorWhite, AppTheme.colorRed);
    }
  }

  getAllCaseCondition() {
    isLoading = true;
    fieldList!.clear();
    update();
    TicketSystemProvider().viewAllCaseCondition(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ConditionRes responseData = ConditionRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  fieldList!.addAll(responseData.dataList!);
                  //getArgumentData();
                  tatQueryFieldMappingList!
                      .add(TatQueryFieldMappingList(uId: uId));
                }
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
