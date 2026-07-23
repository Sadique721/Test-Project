import 'dart:convert';

import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/response/plan_services_res.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/ticket_system/model/request/add_edit_problem_domain_req.dart';
import 'package:savbill/pages/ticket_system/model/response/department_type_res.dart';
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
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

class AddEditProblemDomainController extends GetxController {
  bool isLoading = false;
  GetStorage getStorage = GetStorage();

  TextEditingController problemDomainNameController = TextEditingController();

  UserDetail? userDetail;

  List<DropdownDetail>? statusList = [];
  DropdownDetail? selectedStatus;

  List<PlanServiceDetail>? planServiceList = [];
  PlanServiceDetail? selPlanService;

  List<DepartmentType>? departmentTypeList = [];
  DepartmentType? selectedDepartment;

  String from = Strings.add;
  ProblemDomainDetail? problemDomainDetail;
  bool chkDepartment = false, chkService = false;

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
      if (arguments[Constant.PD_DETAIL] != null) {
        problemDomainDetail = arguments[Constant.PD_DETAIL];
      }
      if (problemDomainDetail != null) {
        problemDomainNameController.text = problemDomainDetail!.categoryName!;

        if (problemDomainDetail!.department != null &&
            problemDomainDetail!.department!.isNotEmpty) {
          chkDepartment = true;
        }
        if (problemDomainDetail!.service != null &&
            problemDomainDetail!.service!.id != null) {
          chkService = true;
        }
        for (DropdownDetail element in statusList!) {
          if (element.id!.equalsIgnoreCase(problemDomainDetail!.status!)) {
            selectedStatus = element;
            break;
          }
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
    getPlanServicesDetail();
  }

  getPlanServicesDetail() {
    isLoading = true;
    planServiceList!.clear();
    update();
    CustomerProvider().getPlanService(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              PlanServicesRes responseData = PlanServicesRes.fromJson(map);
              if (responseData.status == 200) {
                if (responseData.serviceList != null &&
                    responseData.serviceList!.isNotEmpty) {
                  planServiceList!.addAll(responseData.serviceList!);
                  if (chkService) {
                    for (PlanServiceDetail element in planServiceList!) {
                      if (element.id == problemDomainDetail!.service!.id) {
                        selPlanService = element;
                        chkService = false;
                        break;
                      }
                    }
                  }
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
        getDepartmentType();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
        getDepartmentType();
      },
    );
  }

  getDepartmentType() {
    isLoading = true;
    departmentTypeList!.clear();
    update();
    TicketSystemProvider().getDepartmentType(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              DepartmentTypeRes responseData = DepartmentTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  departmentTypeList!.addAll(responseData.dataList!);
                  if (chkDepartment) {
                    for (DepartmentType element in departmentTypeList!) {
                      if (element.value != null && element.value!.isNotEmpty) {
                        if (element.value!.equalsIgnoreCase(
                            problemDomainDetail!.department!)) {
                          selectedDepartment = element;
                          chkDepartment = false;
                          break;
                        }
                      }
                    }
                  }
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

  void addEditProblemDomainApiCall() {
    isLoading = true;
    update();
    AddEditProblemDomainReq request = AddEditProblemDomainReq(
        categoryName: problemDomainNameController.text,
        department:
            selectedDepartment != null ? selectedDepartment!.value : null,
        service:
            selPlanService != null ? Service(id: selPlanService!.id) : null,
        status: selectedStatus != null ? selectedStatus!.text : "",
        id: problemDomainDetail != null ? problemDomainDetail!.id : null,
        slaTimeP1: 1,
        slaTimeP2: 1,
        slaTimeP3: 1,
        slaUnitP1: "Day",
        slaUnitP2: "Day",
        slaUnitP3: "Day",
        ticketReasonCategoryTATMappingList: [
          TicketReasonCategoryTATMapping(
              orderNumber: 1,
              time: 1,
              timeUnit: "Day",
              action: "Notification",
              mappingId: null,
              escalatedTime: 1,
              mediumTime: 1,
              level: "Level 1")
        ]);
    TicketSystemProvider().addEditProblemDomain(
      isAdd: problemDomainDetail != null ? false : true,
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
