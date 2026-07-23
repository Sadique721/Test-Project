import 'dart:convert';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/task_management/model/request/add_edit_tat_task_req.dart';
import 'package:savbill/pages/task_management/model/response/tat_task_list_res.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
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

import '../../../dashboard/model/response/show_tat_details_res.dart';

class AddEditTatTaskController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController taskNameController = TextEditingController();
  TextEditingController responseTimeController = TextEditingController();

  TextEditingController slaTimeP1Controller = TextEditingController();
  TextEditingController slaTimeP2Controller = TextEditingController();
  TextEditingController slaTimeP3Controller = TextEditingController();

  TextEditingController tatTimeP1Controller = TextEditingController();
  TextEditingController tatTimeP2Controller = TextEditingController();
  TextEditingController tatTimeP3Controller = TextEditingController();

  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  String from = Strings.add;
  TatTaskListDetails? tatTaskDetail;

  List<String>? unitList = [Strings.day, Strings.hour, Strings.minutes];
  List<String>? actionList = [
    Strings.notification,
    Strings.reassign,
    Strings.both
  ];

  String? selectedUnit,
      selectedSLAUnitP1,
      selectedSLAUnitP2,
      selectedSLAUnitP3,
      selectedTATUnit,
      selectedAction;

  List<TatMatrixMappings>? tatMatrixMappings = [];
  int orderId = 1;

  @override
  void onInit() {
    super.onInit();
    statusList!.add(DropdownDetail(
        id: Strings.active.toUpperCase(),
        text: Strings.active,
        type: Strings.status));
    statusList!.add(DropdownDetail(
        id: Strings.in_active.toUpperCase(),
        text: Strings.in_active,
        type: Strings.status));
    getArgumentData();
  }

  getArgumentData() {
    var arguments = Get.arguments;
    if (arguments != null) {
      if (arguments[Constant.FROM] != null) {
        from = arguments[Constant.FROM];
      }
      if (arguments[Constant.TAT_TASK_DETAIL] != null) {
        tatTaskDetail = arguments[Constant.TAT_TASK_DETAIL];
      }
      if (tatTaskDetail != null) {
        taskNameController.text = tatTaskDetail!.name!;
        if (tatTaskDetail!.rtime != null) {
          responseTimeController.text = tatTaskDetail!.rtime!.toString();
        }
        if (tatTaskDetail!.slaTimep1 != null) {
          slaTimeP1Controller.text = tatTaskDetail!.slaTimep1!.toString();
        }
        if (tatTaskDetail!.slaTimep2 != null) {
          slaTimeP2Controller.text = tatTaskDetail!.slaTimep2!.toString();
        }
        if (tatTaskDetail!.slaTime3 != null) {
          slaTimeP3Controller.text = tatTaskDetail!.slaTime3!.toString();
        }

        for (String item in unitList!) {
          if (tatTaskDetail!.sunitp1 != null &&
              tatTaskDetail!.sunitp1!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTaskDetail!.sunitp1!)) {
              selectedSLAUnitP1 = item;
            }
          }
          if (tatTaskDetail!.sunitp2 != null &&
              tatTaskDetail!.sunitp2!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTaskDetail!.sunitp2!)) {
              selectedSLAUnitP2 = item;
            }
          }

          if (tatTaskDetail!.sunitp3 != null &&
              tatTaskDetail!.sunitp3!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTaskDetail!.sunitp3!)) {
              selectedSLAUnitP3 = item;
            }
          }

          if (tatTaskDetail!.runit != null &&
              tatTaskDetail!.runit!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTaskDetail!.runit!)) {
              selectedUnit = item;
            }
          }
        }

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(tatTaskDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }

        if (tatTaskDetail!.tatMatrixMappings != null &&
            tatTaskDetail!.tatMatrixMappings!.isNotEmpty) {
          tatMatrixMappings!.addAll(tatTaskDetail!.tatMatrixMappings!);
          orderId=tatMatrixMappings!.length+1;
        }
      }
    }
    update();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    String strUserData = "";
    if (getStorage.hasData(Constant.USER_DATA)) {
      strUserData = await getStorage.read(Constant.USER_DATA);
    }

    if (!strUserData.isNullOrEmpty()) {
      userDetail = UserDetail.fromJson(jsonDecode(strUserData));
      update();
    }
  }

  void addEditTatTaskApiCall() {
    isLoading = true;
    update();
    num slaTime1 = int.parse(slaTimeP1Controller.text);
    num slaTime2 = int.parse(slaTimeP2Controller.text);
    num slaTime3 = int.parse(slaTimeP3Controller.text);
    num rTime = int.parse(responseTimeController.text);

    AddEditTatTaskReq request = AddEditTatTaskReq(
        name: taskNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        slaTimep1: slaTime1,
        slaTimep2: slaTime2,
        slaTime3: slaTime3,
        sunitp1: selectedSLAUnitP1,
        sunitp2: selectedSLAUnitP2,
        sunitp3: selectedSLAUnitP3,
        rtime: rTime,
        runit: selectedUnit,
        id: tatTaskDetail != null ? tatTaskDetail!.id : null,
        tatMatrixMappings: tatMatrixMappings);

    TaskSystemProvider().addEditTatForTicket(
      isAdd: tatTaskDetail != null ? false : true,
      request: request,
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
