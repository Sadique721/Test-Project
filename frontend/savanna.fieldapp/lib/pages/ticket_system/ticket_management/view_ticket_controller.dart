import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/customer/model/request/custmer_list_request.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/customer/model/response/city_list_res.dart';
import 'package:savbill/pages/dashboard/savbill_care_provider.dart';
import 'package:savbill/pages/dashboard/model/request/case_followup_req.dart';
import 'package:savbill/pages/dashboard/model/response/case_status_response.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/dashboard/ticket_detail_controller.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/ticket_system/model/request/edit_ticket_request.dart';
import 'package:savbill/pages/ticket_system/model/response/approve_reject_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/check_reassign_ticket_res.dart';
import 'package:savbill/pages/ticket_system/model/response/olt_list_res.dart' hide CityDetail;
import 'package:savbill/pages/ticket_system/model/response/problem_domain_list_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_staff_detail_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/create_ticket.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_change_problem_domain.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_assign_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_staff_detail_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_system_provider.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:dio/dio.dart' as dia;
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

import '../../dashboard/case_assign.dart';
import '../../dashboard/model/response/case_assign_staff_lst.dart';
import '../../dashboard/savbill_caretab_controller.dart';

class ViewTicketController extends GetxController implements TicketAssignAction{
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  ScrollController? controller;
  int page = 1;

  int tabIndex = 0;

  GetStorage getStorage = GetStorage();
  List<TicketDetail>? ticketList = [];
  ViewTicketResponse? viewTicketResponse;

  List<TicketDetail>? ticketAssignMeList = [];
  ViewTicketResponse? viewTicketAssignMeResponse;

  List<ProblemDomainDetail>? problemDomainList = [];
  ProblemDomainDetail? selProblemDomain;

  List<CaseStatusDetail>? caseStatusList = [];
  CaseStatusDetail? selectedCaseStatus;

  List<CityDetailList>? OLTList = [];
  CityDetailList? selectedOLT;

  UserDetail? userDetail;

  TextEditingController remarksController = TextEditingController();

  TextEditingController searchDetailController = TextEditingController();

  TicketDetail? selectedTicket;

  List<TicketPriority>? ticketPriorityList = [];
  int? assignStaffParentId;

  List<CaseStaffDetail>? staffList = [];
  CaseStaffDetail? selectedStaff;

  List<PlanTypeDetail>? ticketSearchOptionList = [];
  PlanTypeDetail? selectSearchOption;
  TicketDetailController? ticketDetailController;
  List<ApproveRejectTicketList>? approveRejectTicketList = [];


  int? selectedCaseId ;
  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    ticketDetailController = Get.put(TicketDetailController());
    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
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
    getOLTList();
  }

  addEditTicketScreen(String from, TicketDetail? item) async {
    var result = await Get.to(CreateTicket(),
        arguments: {Constant.FROM: from, Constant.TICKET_DETAIL: item});

    if (result != null && result == true) {
      clearFilter();
    }
  }

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

    if((selectSearchOption !=
        null &&
        selectSearchOption!.value!
            .equalsIgnoreCase(
            "OLT"))) {
      searchDetailController.text = selectedOLT?.name ?? "";
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
          if (responseModel.message!.isNotEmpty) {
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
    TicketSystemProvider().getAllProductDomain(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ProblemDomainListRes responseData =
                  ProblemDomainListRes.fromJson(map);
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
          if (responseModel.message!.isNotEmpty) {
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
    SavbillCareProvider().getCaseStatusList(
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
          if (responseModel.message!.isNotEmpty) {
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
          if (responseModel.message!.isNotEmpty) {
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
    TicketSystemProvider().viewTicketsList(
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
              ViewTicketResponse responseData =
                  ViewTicketResponse.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewTicketResponse = responseData;
                if (page == 1) {
                  ticketList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  ticketList?.clear();
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
            ticketList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }


        if (assignStaffParentId != null) {
           // getTicketStaffDetail(assignStaffParentId!);
        }
        assignedToMeTicket();
        update();
      },
      onError: (ResponseModel error) {
        if (assignStaffParentId != null) {
          // getTicketStaffDetail(assignStaffParentId!);
        }
        if (page == 1) {
          ticketList?.clear();
        }
        assignedToMeTicket();
        handleApiError(error);
      },
    );
  }

  assignedToMeTicket() {
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerListRequest searchReq = CustomerListRequest();

    if (isFilterApply) {
      List<Filters>? filters = [];
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
      // isLoading = true;
      update();
    }
    TicketSystemProvider().assignedToMeTicket(
      isSearch: !isFilterApply,
      requestNormal: normalRequest,
      requestSearch: searchReq,
      onSuccess: (ResponseModel responseModel) {
        isShowLoadMore = false;
        // isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewTicketResponse responseData =
                  ViewTicketResponse.fromJson(map);
              log("assignedToMeTicketResponse==>${jsonEncode(responseData)}");

              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                viewTicketAssignMeResponse = responseData;
                if (page == 1) {
                  ticketAssignMeList?.clear();
                }
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  ticketAssignMeList?.addAll(responseData.dataList!);
                }
              } else {
                if (page == 1) {
                  ticketAssignMeList?.clear();
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
            ticketAssignMeList?.clear();
          }
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
        if (assignStaffParentId != null) {
           // getTicketStaffDetail(assignStaffParentId!);
        }
      },
      onError: (ResponseModel error) {
        if (assignStaffParentId != null) {
          // getTicketStaffDetail(assignStaffParentId!);
        }
        if (page == 1) {
          ticketAssignMeList?.clear();
        }
        handleApiError(error);
      },
    );
  }

  void caseFollowUpApiCall(TicketDetail? caseDetail, String remarks) {
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
          if (responseModel.message!.isNotEmpty) {
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
                  ticketStaffDetailDialog(Get.context!, responseData.staff!);
                  assignStaffParentId = responseData.staff!.parentStaffId;


                  log("assignStaffParentId==>${assignStaffParentId}");
                  log("assignStaffParentId2==>${responseData.staff!.parentStaffId}");

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
    TicketSystemProvider().pickTicket(
      remarks: remarks.replaceAll(" ", "%20"),
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

  changePriorityTicket(TicketPriority priority, TicketDetail ticketDetail) {
    EditTicketRequest editRequest = EditTicketRequest(
      ticketId: ticketDetail.caseId,
      status: ticketDetail.caseStatus!,
      caseType: ticketDetail.caseType,
      assignee: ticketDetail.currentAssigneeId,
      priority: priority.value,
      attachment: "",
      filename: "",
      helperName: ticketDetail.helperName,
      finalResolutionId: ticketDetail.finalResolutionId,
      remarkType: "",
      groupReasonId: ticketDetail.groupReasonId,
      reasonSubCategoryId: ticketDetail.reasonSubCategoryId!,
      ticketReasonCategoryId: ticketDetail.ticketReasonCategoryId!,
      caseTitle: ticketDetail.caseTitle,
      rootCauseReasonId: ticketDetail.rootCauseReasonId,
      source: ticketDetail.source,
      subSource: ticketDetail.subSource,
      customerAdditionalMobileNumber:
          ticketDetail.customerAdditionalMobileNumber,
      customerAdditionalEmail: ticketDetail.customerAdditionalEmail,
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
          if (responseModel.message!.isNotEmpty) {
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

  checkTicketReAssign(TicketDetail? ticketDetail) {
    isLoading = true;
    update();
    TicketSystemProvider().checkReAssignTicket(
      caseId: ticketDetail?.caseId,
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
                  openChangeProblemDomainScreen(ticketDetail);
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

  openChangeProblemDomainScreen(TicketDetail? ticketDetail) async {
    var result = Get.to(() => TicketChangeProblemDomain(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      clearFilter();
    }
  }

  getStaffListData(int? itemCaseId, TicketDetailController ticketDetailController) {
    isLoading = true;
    staffList!.clear();
    update();
    SavbillCareProvider().getTicketStaffLst(
      ticketId: itemCaseId!,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              CaseStaffListRes responseData = CaseStaffListRes.fromJson(map);
              if (responseData.responseCode != null &&
                  responseData.responseCode == 200) {
                if (responseData.dataList != null &&
                    responseData.dataList!.isNotEmpty) {
                  staffList!.addAll(responseData.dataList!);
                  openCaseAssignScreen(ticketDetailController, staffList);
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
          if (responseModel.message!.isNotEmpty) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
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



  openCaseAssignScreen(
      TicketDetailController ticketDetailController, List<CaseStaffDetail>? staffDetail) async {
    bool chkRefresh = await Get.to(() => CaseAssign(), arguments: {
      Constant.TICKET_DETAIL: ticketDetailController.ticketDetail,
      Constant.STAFF_DETAIL: staffList,
    });

    if (chkRefresh) {
      ticketDetailController.getTicketListData(ticketDetailController.ticketDetail?.caseId ?? 0);
      if (Get.isRegistered<SavbillCareTabController>()) {
        final savbillCareTabController = Get.find<SavbillCareTabController>();
        savbillCareTabController.page = 1;
        savbillCareTabController.update();
        savbillCareTabController.initPlatformState();
      }
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
              staffStatus:  staffStatus);
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
          // getTicketSearchOption();
          try {
            ticketDetailController!.getTicketListData(entityId ?? 0);
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);

          } on Exception catch (e) {
            print(e.toString());
          }

          // }
        } else {
          if (responseModel.message!.isNotEmpty) {
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
          // getTicketSearchOption();
          try {
            ticketDetailController!.getTicketListData(entityId ?? 0);
            Utils.showSnackbar(Strings.SUCCESS, Strings.successfully,
                AppTheme.colorWhite, AppTheme.colorGreen);
          } on Exception catch (e) {
            print(e.toString());
          }
          // }
        } else {
          if (responseModel.message!.isNotEmpty) {
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

  getOLTList() {
    //isLoading = true;
    OLTList!.clear();
    update();
    TicketSystemProvider().getCityList(
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              OLTListRes responseData = OLTListRes.fromJson(map);
              // if (responseData.responseCode != null &&
              //     responseData.responseCode == 200) {
                if (responseData.cityList != null &&
                    responseData.cityList!.isNotEmpty) {
                  //Iterable<CityDetailList> iterableCityList = responseData.cityList! as Iterable<CityDetailList>;
                  OLTList!.addAll(responseData.cityList!);
                }
              //}
              else {
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
        // isLoading = false;
        // update();
        //viewTickets();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
        //viewTickets();
      },
    );
  }

}
