import 'dart:convert';
import 'dart:developer';

import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/customer/customer_provider.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/response/services_area_res.dart';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/case_assign.dart';
import 'package:savbill/pages/dashboard/model/request/case_followup_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_assign_staff_lst.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/task_management/model/response/get_all_team_list_res.dart';
import 'package:savbill/pages/task_management/model/response/task_category_management_list_res.dart';
import 'package:savbill/pages/task_management/model/response/view_task_detail_response.dart';
import 'package:savbill/pages/task_management/model/response/view_task_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/create_task/create_task_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_assign/task_case_assign.dart';
import 'package:savbill/pages/task_management/task_system_provider.dart';
import 'package:savbill/pages/ticket_system/model/request/edit_ticket_request.dart';
import 'package:savbill/pages/ticket_system/model/response/approve_reject_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/check_reassign_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_assign_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_detail_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:dio/dio.dart' as dia;
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class ViewTaskMgmtController extends GetxController implements TicketAssignAction{
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  ScrollController? scrollController;
  int page = 1;

  GetStorage getStorage = GetStorage();
  List<ViewTaskDataList>? taskList = [];
  ViewTaskResponse? viewTicketResponse;

  List<TaskCategoryMgmtDataList>? problemDomainList = [];
  TaskCategoryMgmtDataList? selProblemDomain;
  // List<ProblemDomainDetail>? problemDomainList = [];
  // ProblemDomainDetail? selProblemDomain;

  List<ServicesAreaDetail>? servicesAreaList = [];
  ServicesAreaDetail? selectedServicesArea;

  List<CaseStatusDetail>? caseStatusList = [];
  CaseStatusDetail? selectedCaseStatus;

  UserDetail? userDetail;

  TextEditingController remarksController = TextEditingController();
  TextEditingController searchController = TextEditingController();

  TextEditingController searchDetailController = TextEditingController();

  ViewTaskDataList? selectedTicket;

  List<TicketPriority>? ticketPriorityList = [];
  int? assignStaffParentId;

  List<AllTeamDataList>? allTeamList = [];
  AllTeamDataList? selectedTeam;

  List<PlanTypeDetail>? ticketSearchOptionList = [];
  PlanTypeDetail? selectSearchOption;

  List<ApproveRejectTicketList>? approveRejectTicketList = [];
  int? selectedCaseId ;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    scrollController = ScrollController();
    scrollController?.addListener(() {
      double? extentAfter = scrollController?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewTicketResponse != null &&
            viewTicketResponse!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          viewTickets();
        }
      }
    });
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
    getTicketSearchOption();
  }

  // addEditTicketScreen(String from, ViewTaskDataList? item) async {
  //   var result = await Get.to(CreateTicket(),
  //       arguments: {Constant.FROM: from, Constant.TICKET_DETAIL: item});
  //
  //   if (result != null && result == true) {
  //     clearFilter();
  //   }
  // }

  applyFilter() {
    // if (selProblemDomain == null &&
    //     selectedServicesArea == null &&
    //     selectedCaseStatus == null) {
    //   isFilterApply = false;
    //   update();
    //   Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
    //       AppTheme.colorWhite, AppTheme.colorRed);
    //   return;
    // }

    if (selectSearchOption == null &&
        searchDetailController.text.isEmpty) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    update();
    viewTickets();
  }

  clearFilter() {
    // selProblemDomain = null;
    // selectedServicesArea = null;
    // selectedCaseStatus = null;
    selectSearchOption = null;
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    update();
    viewTickets();
  }

  getTicketSearchOption() {
    isLoading = true;
    ticketSearchOptionList?.clear();
    update();
    TicketSystemProvider().getTicketManagementSearchOption(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CustomerPlanTypeRes responseData =
              CustomerPlanTypeRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList!.isNotEmpty) {
                  for (var element in responseData.dataList!) {
                    if (element.text != null &&
                        !element.text!.equalsIgnoreCase("New") &&
                        !element.text!.equalsIgnoreCase("Upgrade")) {
                      ticketSearchOptionList!.add(element);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        getAllProblemDomain();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getAllProblemDomain();
      },
    );
  }

  getAllProblemDomain() {
    isLoading = true;
    problemDomainList!.clear();
    update();
    TaskSystemProvider().getAllActiveReasonCategory(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              // ProblemDomainListRes responseData =
              // ProblemDomainListRes.fromJson(map);
              TaskCategoryMgmtRes responseData =
              TaskCategoryMgmtRes.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  problemDomainList!.addAll(responseData.dataList!);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getServiceArea();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getServiceArea();
      },
    );
  }

  getServiceArea() {
    isLoading = true;
    servicesAreaList!.clear();
    update();
    CustomerProvider().getServiceAreaData(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ServicesAreaRes responseData = ServicesAreaRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  servicesAreaList!.addAll(responseData.dataList!);
                  update();
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getCaseStatusListData();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getCaseStatusListData();
      },
    );
  }

  getCaseStatusListData() {
    isLoading = true;
    caseStatusList?.clear();
    update();
    TaskSystemProvider().getTaskCaseStatusList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStatusResponse responseData =
              CaseStatusResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  caseStatusList?.addAll(responseData.dataList!);
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        getTicketPriority();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        getTicketPriority();
      },
    );
  }

  getTicketPriority() {
    isLoading = true;
    ticketPriorityList!.clear();
    update();
    TicketSystemProvider().getTicketPriority(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketPriorityRes responseData = TicketPriorityRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketPriorityList!.addAll(responseData.dataList!);
                }
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        isLoading = false;
        update();
        viewTickets();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        viewTickets();
      },
    );
  }

  viewTickets() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
      /*if (selectedServicesArea != null) {
        filters.add(Filters(
            filterColumn: "servicearea_id",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: selectedServicesArea!.id.toString()));
      }
      if (selProblemDomain != null) {
        filters.add(Filters(
            filterColumn: "ticketReasonCategoryId",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: selProblemDomain!.id.toString()));
      }
      if (selectedCaseStatus != null) {
        filters.add(Filters(
            filterColumn: "caseStatus",
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: selectedCaseStatus!.value));
      }*/
      if (selectSearchOption != null) {
        filters.add(Filters(
            filterColumn: selectSearchOption!.value,
            filterCondition: "",
            filterDataType: "",
            filterOperator: "",
            filterValue: searchDetailController.text.trim()));
      }

      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
      searchReq.sortBy = "createdate";
      searchReq.sortOrder = 0;
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }
    TaskSystemProvider().viewTaskList(
      isSearch: isFilterApply,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTaskResponse responseData =
              ViewTaskResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewTicketResponse = responseData;
                if (page == 1) {
                  taskList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  taskList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  taskList?.clear();
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
            taskList?.clear();
          }
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        // if (assignStaffParentId != null) {
        //   getTicketStaffDetail(assignStaffParentId!);
        // }
      },
      onError: (ResponseModel error) {
        // if (assignStaffParentId != null) {
        //   getTicketStaffDetail(assignStaffParentId!);
        // }
        if (page == 1) {
          taskList?.clear();
        }
        handleApiError(error);
      },
    );
  }

  void caseFollowUpApiCall(ViewTaskDataList? caseDetail, String remarks) {
    isLoading = true;
    update();
    DateTime now = DateTime.now();
    String currentDate = DateFormat(Constant.API_DATE_TIME_FORMAT).format(now);
    CaseFollowupReq caseFollowupReq = CaseFollowupReq(
        caseId: caseDetail?.caseId,
        remark: remarks,
        remarkDate: currentDate,
        staffId: caseDetail?.currentAssigneeId,
        custId: caseDetail?.customersId);

    SavbillCareProvider().caseFollowupRequest(
      caseFollowupReq: caseFollowupReq,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode == 200 ||
                responseData.responseCode == 0) {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
              }

              clearFilter();
            } else {
              if (responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  assignTicket(int staffId) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectTicket(
      entityId: selectedTicket!.caseId!,
      eventName: "CASE",
      approveReject: true,
      assignId: staffId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.message != null &&
                    responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                selectedTicket = null;
                clearFilter();
              } else {
                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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

  getTicketStaffDetail(int staffId) {
    isLoading = true;
    update();
    TicketSystemProvider().getTicketStaffDetail(
      staffId: staffId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketStaffDetailRes responseData =
              TicketStaffDetailRes.fromJson(map);
              if (responseData.status != null && responseData.status == 200) {
                if (responseData.staff != null) {
                  assignStaffParentId = responseData.staff!.parentStaffId;
                  ticketStaffDetailDialog(Get.context!, responseData.staff!);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  pickTicket(String remarks) {
    isLoading = true;
    update();
    int? staffId;
    if (selectedTicket!.ticketAssignStaffMappings != null &&
        selectedTicket!.ticketAssignStaffMappings!.isNotEmpty) {
      selectedTicket!.ticketAssignStaffMappings!.forEach((element) {
        if (element.staffId == userDetail!.userId) {
          staffId = element.staffId;
        }
      });
    }
    TaskSystemProvider().pickTask(
      remarks: remarks,
      caseId: selectedTicket!.caseId!,
      staffId: staffId!,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                }
                selectedTicket = null;
                clearFilter();
              } else {
                if (responseData.ERROR != null &&
                    responseData.ERROR!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.ERROR,
                      AppTheme.colorWhite, AppTheme.colorRed);
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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

  changePriorityTicket(TicketPriority priority, TaskDetail taskDetail) {
    EditTicketRequest editRequest = EditTicketRequest(
      ticketId: taskDetail.caseId,
      status: taskDetail.caseStatus!,
      caseType: taskDetail.caseType,
      assignee: taskDetail.currentAssigneeId,
      priority: priority.value,
      attachment: "",
      filename: "",
      helperName: taskDetail.helperName,
      finalResolutionId: taskDetail.finalResolutionId,
      remarkType: "",
      groupReasonId: taskDetail.groupReasonId,
      // reasonSubCategoryId: taskDetail.reasonSubCategoryId!,
      // ticketReasonCategoryId: taskDetail.ticketReasonCategoryId!,
      caseTitle: taskDetail.caseTitle,
      rootCauseReasonId: taskDetail.rootCauseReasonId,
      source: taskDetail.source,
      subSource: taskDetail.subSource,
      // customerAdditionalMobileNumber:
      // ticketDetail.customerAdditionalMobileNumber,
      // customerAdditionalEmail: ticketDetail.customerAdditionalEmail,
    );
    Map<String, dynamic> map = {};
    map["caseUpdate"] = jsonEncode(editRequest);
    dia.FormData formData = dia.FormData.fromMap(map);
    isLoading = true;
    update();
    TicketSystemProvider().addEditCaseTicketsRequest(
      isAdd: false,
      formData: formData,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if (responseData.responseCode != null &&
                responseData.responseCode == 200) {
              clearFilter();
            } else {
              if (responseData.responseMessage != null &&
                  responseData.responseMessage!.isNotEmpty) {
                Utils.showSnackbar(Strings.ERROR, responseData.responseMessage,
                    AppTheme.colorWhite, AppTheme.colorRed);
              }
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  checkTaskReAssign(TaskDetail? taskDetail) {
    isLoading = true;
    update();
    TicketSystemProvider().checkReAssignTicket(
      caseId: taskDetail?.caseId,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CheckReassignTicketRes responseData =
              CheckReassignTicketRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null &&
                      responseData.responseCode == 200)) {
                if (responseData.data != null &&
                    responseData.data!.isNotEmpty &&
                    responseData.data!.equalsIgnoreCase("true")) {
                  // openChangeProblemDomainScreen(ticketDetail);
                } else {
                  Utils.showSnackbar(
                      Strings.INFO,
                      "Not eligible to change problem domain..",
                      AppTheme.colorBlack,
                      AppTheme.colorBlueRView);
                }
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
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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

  // openChangeProblemDomainScreen(ViewTaskDataList? ticketDetail) async {
  //   var result = Get.to(() => TicketChangeProblemDomain(), arguments: {
  //     Constant.TICKET_DETAIL: ticketDetail,
  //   });
  //   if (result != null && result == true) {
  //     clearFilter();
  //   }
  // }



  getAllTeamList(int? taskId, TaskDetail? taskDetail ) {
    if(!(taskDetail!.caseStatus!.equalsIgnoreCase("Closed"))) {
      isLoading = true;
      allTeamList!.clear();
      update();
      SavbillCareProvider().getAllTeamListRequest(
        onSuccess: (ResponseModel responseModel) {
          if (responseModel.statusCode == 200) {
            if (responseModel.result != null) {
              try {
                Map<String, dynamic> map = responseModel.result;
                GetAllTeamListRes responseData = GetAllTeamListRes.fromJson(
                    map);
                if (responseData.responseCode != null &&
                    responseData.responseCode == 200) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    allTeamList!.addAll(responseData.dataList!);

                    log("assignStaffParentId==>${assignStaffParentId}");

                    openCaseAssignScreen(taskDetail:taskDetail ,teamDetail: allTeamList,assignStaffParentId: assignStaffParentId);
                  } else {
                    Utils.showSnackbar(
                        Strings.INFO,
                        "No staff available to assign..",
                        AppTheme.colorWhite,
                        AppTheme.colorBlueRView);
                  }
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
            if (responseModel.message!.isNotEmpty &&
                responseModel.message != Strings.something_wrong) {
              Utils.showSnackbar(
                  Strings.ERROR, responseModel.message!.isNotEmpty,
                  AppTheme.colorWhite, AppTheme.colorRed);
            }
          }
          isLoading = false;
          update();
        },
        onError: (ResponseModel error) {
          handleApiError(error);
        },
      );
    }else{
      Utils.showSnackbar(
          Strings.INFO,
          "Can not assign close tickets.",
          AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    }
  }


  approveRejectTicket({required String? status, required String? remark,required int? caseId, required BuildContext context}) {
    isLoading = true;
    update();
    PendingApprovalsProvider().approveRejectTickets(
      caseId: caseId,
      isApproveRequest: status!.equalsIgnoreCase(Strings.approve.toLowerCase())? true : false,
      remark: remark,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ApproveRejectTicketRes responseData = ApproveRejectTicketRes.fromJson(map);
              if ((responseData.status != null && responseData.status == 200) ||
                  (responseData.responseCode != null && responseData.responseCode == 200)) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  approveRejectTicketList?.clear();
                  approveRejectTicketList?.addAll(responseData.dataList!);
                  showAssignStaffDialog(responseData.dataList!,status,context,caseId);
                }
                else {
                  if (status.equalsIgnoreCase(Strings.reject)) {
                    Utils.showSnackbar(Strings.SUCCESS,
                        "Reject Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  } else {
                    Utils.showSnackbar(
                        Strings.SUCCESS,
                        "Approved Successfully.",
                        AppTheme.colorWhite,
                        AppTheme.colorGreen);
                  }
                  // Get.back(result: true);
                  // getCreditNoteListData();
                }
              }
            } on Exception catch (e) {
              print(e.toString());
            }
          }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
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



  openCaseAssignScreen(
      {TaskDetail? taskDetail,
        List<AllTeamDataList>? teamDetail,int? assignStaffParentId}) async {
    bool chkRefresh = await Get.to(() => TaskCaseAssign(), arguments: {
      Constant.TASK_DETAIL: taskDetail,
      Constant.TEAM_DETAIL: teamDetail,
      Constant.ASSIGN_STAFF_PARENT_ID: assignStaffParentId,
    });
    if (chkRefresh) {
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


  showAssignStaffDialog(List<ApproveRejectTicketList> item,String? staffStatus,BuildContext context,int? caseId) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this,
              itemsOrgLst: item,
              entityId: caseId,
              staffStatus:  staffStatus,
              controller: searchController,);
        });
  }

  @override
  void ticketAssignBtnAction({ApproveRejectTicketList? selectedItem, bool? isStaffSelected,int? entityId, String? approveRejectStatus}) {
    Get.back();

    log("entityIdselectedItem==>${entityId}");
    if(isStaffSelected == true){
      // log("Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)){
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignStaffCreditNote(entityId,selectedItem!.id,true);
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignStaffCreditNote(entityId,selectedItem!.id,false);
      }
    }else{
      // log("Not Staff is selected");
      if(approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Not Staff is selected!!!!!!!!=>${Strings.approve}");
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: true);
      }else if(approveRejectStatus.equalsIgnoreCase(Strings.reject)){
        // log("Not Staff is selected!!!!!!!!=>${Strings.reject}");
        assignEveryStaffCreditNote(entityId: entityId, isApprovedRequest: false);
      }
    }

  }


  assignStaffCreditNote(int? entityId,int? nextAssignStaff,bool? isApproveRequest) {
    String apiUrl =
        "${UrlConstants.assignFromStaffTicketList}?entityId=$entityId&eventName=CASE&nextAssignStaff=$nextAssignStaff&isApproveRequest=$isApproveRequest";
    isLoading = true;
    update();
    TicketSystemProvider().assignTicketEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          getTicketSearchOption();
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


  addEditTaskScreen(String from, TicketDetail? item) async {
    var result = await Get.to(CreateTaskScreen(),
        arguments: {Constant.FROM: from, Constant.TASK_DETAIL: item,});

    if (result != null && result == true) {
      clearFilter();
    }
  }

  assignEveryStaffCreditNote({int? entityId,bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.assignFromEveryStaffTicketList}?caseId=$entityId&remark=assign to everyone from list.&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    TicketSystemProvider().assignTicketEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          // if (responseModel.result != null) {
          getTicketSearchOption();
          try {
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }


}
