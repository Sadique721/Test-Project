import 'dart:convert';

import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_tat_ticket_req.dart';
import 'package:savbill/pages/ticket_system/model/response/tat_ticket_list_res.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
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

import '../../dashboard/model/response/show_tat_details_res.dart';

class AddEditTatTicketController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController ticketNameController = TextEditingController();
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
  TatTicketDetail? tatTicketDetail;

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
      if (arguments[Constant.TAT_DETAIL] != null) {
        tatTicketDetail = arguments[Constant.TAT_DETAIL];
      }
      if (tatTicketDetail != null) {
        ticketNameController.text = tatTicketDetail!.name!;
        if (tatTicketDetail!.rtime != null) {
          responseTimeController.text = tatTicketDetail!.rtime!.toString();
        }
        if (tatTicketDetail!.slaTimep1 != null) {
          slaTimeP1Controller.text = tatTicketDetail!.slaTimep1!.toString();
        }
        if (tatTicketDetail!.slaTimep2 != null) {
          slaTimeP2Controller.text = tatTicketDetail!.slaTimep2!.toString();
        }
        if (tatTicketDetail!.slaTime3 != null) {
          slaTimeP3Controller.text = tatTicketDetail!.slaTime3!.toString();
        }

        for (String item in unitList!) {
          if (tatTicketDetail!.sunitp1 != null &&
              tatTicketDetail!.sunitp1!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTicketDetail!.sunitp1!)) {
              selectedSLAUnitP1 = item;
            }
          }
          if (tatTicketDetail!.sunitp2 != null &&
              tatTicketDetail!.sunitp2!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTicketDetail!.sunitp2!)) {
              selectedSLAUnitP2 = item;
            }
          }

          if (tatTicketDetail!.sunitp3 != null &&
              tatTicketDetail!.sunitp3!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTicketDetail!.sunitp3!)) {
              selectedSLAUnitP3 = item;
            }
          }

          if (tatTicketDetail!.runit != null &&
              tatTicketDetail!.runit!.isNotEmpty) {
            if (item.equalsIgnoreCase(tatTicketDetail!.runit!)) {
              selectedUnit = item;
            }
          }
        }

        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(tatTicketDetail!.status!)) {
            selectedStatus = element;
            break;
          }
        }

        if (tatTicketDetail!.tatMatrixMappings != null &&
            tatTicketDetail!.tatMatrixMappings!.isNotEmpty) {
          tatMatrixMappings!.addAll(tatTicketDetail!.tatMatrixMappings!);
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

  void addEditTatTicketApiCall() {
    isLoading = true;
    update();
    num slaTime1 = int.parse(slaTimeP1Controller.text);
    num slaTime2 = int.parse(slaTimeP2Controller.text);
    num slaTime3 = int.parse(slaTimeP3Controller.text);
    num rTime = int.parse(responseTimeController.text);

    AddEditTatTicketReq request = AddEditTatTicketReq(
        name: ticketNameController.text,
        status: selectedStatus != null ? selectedStatus!.text : "",
        slaTimep1: slaTime1,
        slaTimep2: slaTime2,
        slaTime3: slaTime3,
        sunitp1: selectedSLAUnitP1,
        sunitp2: selectedSLAUnitP2,
        sunitp3: selectedSLAUnitP3,
        rtime: rTime,
        runit: selectedUnit,
        id: tatTicketDetail != null ? tatTicketDetail!.id : null,
        tatMatrixMappings: tatMatrixMappings);

    TicketSystemProvider().addEditTatForTicket(
      isAdd: tatTicketDetail != null ? false : true,
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
