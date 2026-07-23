import 'dart:convert';
import 'dart:developer';
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/credit_note/credit_note_provider.dart';
import 'package:savbill/pages/customer/model/request/filters.dart';
import 'package:savbill/pages/lead_management/create_lead_screen.dart';
import 'package:savbill/pages/lead_management/lead_approve_reject_dialog.dart';
import 'package:savbill/pages/lead_management/lead_staff_assign_dialog.dart';
import 'package:savbill/pages/lead_management/lead_system_provider.dart';
import 'package:savbill/pages/lead_management/model/lead_all_rejected_reason_res.dart';
import 'package:savbill/pages/lead_management/model/lead_approve_reject_req.dart';
import 'package:savbill/pages/lead_management/model/lead_approve_reject_staff_res.dart';
import 'package:savbill/pages/lead_management/model/view_lead_response.dart';
import 'package:savbill/pages/lead_management/model/view_lead_search_response.dart';
import 'package:savbill/pages/login/model/response/user_detail.dart';
import 'package:savbill/pages/model/dropdown_detail.dart';
import 'package:savbill/pages/model/page_request.dart';
import 'package:savbill/routes/app_routes.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/extensions.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/base_response.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/webservices/url_constants.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:get_storage/get_storage.dart';
import 'package:intl/intl.dart';

class ViewLeadController extends GetxController
    implements LeadApproveRejectBtnAction, LeadAssignAction {
  bool isLoading = false,
      isShowLoadMore = false,
      isFilterApply = false,
      filterViewOpen = false;
  ScrollController? controller;
  int page = 1;

  GetStorage getStorage = GetStorage();

  // List<LeadViewContentList>? leadViewContentList = [];

  List<LeadMasterListData>? leadMasterList = [];

  int? leadListDataTotalRecords;

  // ViewLeadRes ? viewLeadResponse;
  UserDetail? userDetail;
  TextEditingController searchDetailController = TextEditingController();
  TextEditingController leadConvertedDateController = TextEditingController();
  TextEditingController leadCreditDateController = TextEditingController();
  TextEditingController lastUpdateDateController = TextEditingController();
  TextEditingController searchController = TextEditingController();
  DateFormat dateFormat = DateFormat(Constant.DATE_FORMAT);
  String? selectedLeadFromDateApi = "",
      selectedLeadToDateApi = "",
      selectedLastUpdateDateApi = "";
  DateFormat apiDateFormat = DateFormat(Constant.API_DATE_FORMAT);

  List<PlanTypeDetail>? ticketSearchOptionList = [];
  ViewLeadRes? viewLeadRes;

  ViewLeadSearchRes? viewLeadSearchRes;

  // PlanTypeDetail? selectSearchOption;

  List<DropdownDetail>? leadSearchOptionList = [];
  DropdownDetail? selectSearchOption;

  List<DropdownDetail>? leadStatusOptionList = [];
  DropdownDetail? selectStatusOption;

  List<RejectReasonList>? rejectedReasonList = [];
  RejectReasonList? selectedRejectedReason;

  List<ApproveRejectStaffLeadList>? approveRejectStaffLeadList = [];

  DateTime? selectedLeadFromDate, selectedLeadToDate, selectedLastUpdateDate;

  @override
  void onInit() {
    super.onInit();
    initPlatformState();
    leadConvertedDateController.text = dateFormat.format(DateTime.now());
    leadCreditDateController.text = dateFormat.format(DateTime.now());

    selectedLeadFromDateApi = apiDateFormat.format(DateTime.now());
    selectedLeadToDateApi = apiDateFormat.format(DateTime.now());

    selectedLeadToDate = DateTime.now();

    leadSearchOptionList!.clear();
    leadSearchOptionList!.add(DropdownDetail(
        id: "Customer Name", text: "Customer Name", type: "name"));
    leadSearchOptionList!
        .add(DropdownDetail(id: "Mobile", text: "Mobile", type: "mobile"));
    leadSearchOptionList!.add(DropdownDetail(
        id: "Created By", text: "Created By", type: "createdBy"));
    leadSearchOptionList!.add(DropdownDetail(
        id: "Last Modified On",
        text: "Last Modified On",
        type: "lastUpdateOn"));
    leadSearchOptionList!.add(
        DropdownDetail(id: "Lead Status", text: "Lead Status", type: "status"));
    leadSearchOptionList!.add(DropdownDetail(
        id: "Lead Source", text: "Lead Source", type: "leadSourceName"));
    leadSearchOptionList!.add(DropdownDetail(
        id: "serviceArea ", text: "serviceArea ", type: "serviceArea "));

    leadSearchOptionList!.add(DropdownDetail(
        id: "Lead Assigne Name ",
        text: "Lead Assigne Name ",
        type: "Lead Assigne Name "));

    leadSearchOptionList!
        .add(DropdownDetail(id: "Branch ", text: "Branch ", type: "Branch "));

    leadSearchOptionList!.add(
        DropdownDetail(id: "Partner ", text: "Partner ", type: "Partner "));

    leadSearchOptionList!.add(DropdownDetail(
        id: "plangroupname ", text: "plangroupname ", type: "plangroupname "));

    leadStatusOptionList!.clear();
    leadStatusOptionList!.add(
        DropdownDetail(id: "LeadStatus", text: "Inquiry", type: "Inquiry"));
    leadStatusOptionList!.add(
        DropdownDetail(id: "LeadStatus", text: "Converted", type: "Converted"));
    leadStatusOptionList!.add(
        DropdownDetail(id: "LeadStatus", text: "Rejected", type: "Rejected"));
    leadStatusOptionList!.add(DropdownDetail(
        id: "LeadStatus", text: "Re-Inquiry", type: "Re-Inquiry"));

    controller = ScrollController();
    controller?.addListener(() {
      double? extentAfter = controller?.position.extentAfter;
      if (extentAfter! < 300 && !isShowLoadMore) {
        if (viewLeadRes != null &&
            viewLeadRes!.pageDetails!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getLeadManagement();
        } else if (viewLeadSearchRes != null &&
            viewLeadSearchRes!.leadMasterList!.totalPages != page) {
          isShowLoadMore = true;
          page = page + 1;
          update();
          getLeadManagement();
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
    getLeadManagement();
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

    if (selectSearchOption == null && searchDetailController.text.isEmpty) {
      isFilterApply = false;
      update();
      Utils.showSnackbar(Strings.ERROR, "Please enter filter option.",
          AppTheme.colorWhite, AppTheme.colorRed);
      return;
    }
    isFilterApply = true;
    filterViewOpen = false;
    page = 1;
    leadMasterList?.clear();
    update();
    viewLeadList();
    // viewLeads();
  }

  clearFilter() {
    // selProblemDomain = null;
    // selectedServicesArea = null;
    // selectedCaseStatus = null;
    selectSearchOption = null;
    page = 1;
    isFilterApply = false;
    filterViewOpen = false;
    leadMasterList?.clear();
    searchDetailController.text = "";
    selectedLeadFromDateApi = "";
    selectedLeadToDateApi = "";
    selectedLastUpdateDateApi = "";
    leadConvertedDateController.clear();
    leadCreditDateController.clear();
    lastUpdateDateController.clear();
    update();
    getLeadManagement();
    // viewLeads();
  }

  getLeadManagement() {
    isLoading = true;
    leadMasterList!.clear();
    ticketSearchOptionList?.clear();
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    update();
    LeadSystemProvider().viewLeadManagementList(
      requestNormal: normalRequest,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              ViewLeadRes responseData = ViewLeadRes.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                if (responseData.leadMasterList!.isNotEmpty) {
                  // leadViewContentList = responseData.leadMasterList!.content;
                  leadMasterList = responseData.leadMasterList;
                  leadListDataTotalRecords =
                      responseData.pageDetails!.totalRecords;
                  Get.back(result: true);
                }
              } else {
                if (responseData.message!.isNotEmpty) {
                  Utils.showSnackbar(Strings.ERROR, responseData.message,
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
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  // viewLeadList() {
  //   PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
  //   CustomerNewListRequest searchReq = CustomerNewListRequest();
  //   if (isFilterApply) {
  //     List<Filters>? filters = [];
  //     String? filterValue="";
  //
  //     if(selectSearchOption!.type!.equalsIgnoreCase("status")){
  //       filterValue = selectStatusOption!.type;
  //     }else{
  //       filterValue = searchDetailController.text.trim();
  //     }
  //     if (selectSearchOption != null) {
  //       filters.add(Filters(
  //           filterColumn: selectSearchOption!.type,
  //           filterCondition: "and",
  //           filterDataType: "",
  //           filterOperator: "equalto",
  //           filterValue: filterValue));
  //     }
  //
  //     searchReq.filters = filters;
  //     searchReq.page = page;
  //     searchReq.pageSize = 10;
  //     // searchReq.sortBy = "";
  //     searchReq.filterBy = "";
  //     searchReq.sortOrder = "";
  //   }
  //   if (!isShowLoadMore) {
  //     isLoading = true;
  //     update();
  //   }
  //   LeadSystemProvider().viewLeadSearchList(
  //     isSearch: isFilterApply,
  //     requestNormal: normalRequest,
  //     requestSearch: searchReq,
  //     onSuccess: (ResponseModel responseModel) {
  //       isShowLoadMore = false;
  //       isLoading = false;
  //       update();
  //       if (responseModel.statusCode == 200) {
  //         if (responseModel.result != null) {
  //           try {
  //             Map<String, dynamic> map = responseModel.result;
  //             ViewLeadSearchRes responseData = ViewLeadSearchRes.fromJson(map);
  //             if (responseData.responseCode == 200 ||
  //                 responseData.status == 200) {
  //               viewLeadSearchRes = responseData;
  //               if (page == 1) {
  //                 leadMasterList?.clear();
  //               }
  //               if (responseData.leadMasterList!.content!.isNotEmpty) {
  //                 leadMasterList!.addAll(responseData.leadMasterList!.content!);
  //               }
  //             } else {
  //               if (page == 1) {
  //                 leadMasterList?.clear();
  //               }
  //               if (responseData.responseMessage != null &&
  //                   responseData.responseMessage!.isNotEmpty) {
  //                 Utils.showSnackbar(
  //                     Strings.ERROR,
  //                     responseData.responseMessage,
  //                     AppTheme.colorWhite,
  //                     AppTheme.colorRed);
  //               }
  //             }
  //           } on Exception catch (e) {
  //             print(e.toString());
  //           }
  //         }
  //       } else {
  //         if (page == 1) {
  //           leadMasterList?.clear();
  //         }
  //         if (responseModel.message!.isNotEmpty) {
  //           Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
  //               AppTheme.colorWhite, AppTheme.colorRed);
  //         }
  //       }
  //       update();
  //     },
  //     onError: (ResponseModel error) {
  //       if (page == 1) {
  //         leadMasterList?.clear();
  //       }
  //       handleApiError(error);
  //     },
  //   );
  // }

  viewLeadList() {
    String apiUrl = "${UrlConstants.lead_master_list}/search";
    PageRequest normalRequest = PageRequest(page: page, pageSize: 10);
    CustomerNewListRequest searchReq = CustomerNewListRequest();
    if (isFilterApply) {
      List<Filters>? filters = [];
      String? filterValue = "";

      if (selectStatusOption != null &&
          selectStatusOption!.text!.equalsIgnoreCase("Converted")) {
        apiUrl =
            "${apiUrl}?fromConvertedDate=$selectedLeadFromDateApi&toConvertedDate=$selectedLeadToDateApi";
      }

      if (selectSearchOption!.type!.equalsIgnoreCase("status")) {
        filterValue = selectStatusOption!.type;
      } else if (selectSearchOption!.type!.equalsIgnoreCase("lastUpdateOn")) {
        filterValue = selectedLastUpdateDateApi;
      } else {
        filterValue = searchDetailController.text.trim();
      }
      if (selectSearchOption != null) {
        filters.add(Filters(
            filterColumn: selectSearchOption!.type,
            filterCondition: "and",
            filterDataType: "",
            filterOperator: "equalto",
            filterValue: filterValue));
      }

      searchReq.filters = filters;
      searchReq.page = page;
      searchReq.pageSize = 10;
      // searchReq.sortBy = "";
      searchReq.filterBy = "";
      searchReq.sortOrder = "";
    }
    if (!isShowLoadMore) {
      isLoading = true;
      update();
    }

    update();
    LeadSystemProvider().viewLeadSearchStatusList(
      url: apiUrl,
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
              ViewLeadSearchRes responseData = ViewLeadSearchRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                viewLeadSearchRes = responseData;
                if (page == 1) {
                  leadMasterList?.clear();
                }
                if (responseData.leadMasterList!.content!.isNotEmpty) {
                  leadMasterList!.addAll(responseData.leadMasterList!.content!);
                }
              } else {
                if (page == 1) {
                  leadMasterList?.clear();
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
            leadMasterList?.clear();
          }
          if (responseModel.message!.isNotEmpty &&
              responseModel.message != Strings.something_wrong) {
            Utils.showSnackbar(Strings.ERROR, responseModel.message!.isNotEmpty,
                AppTheme.colorWhite, AppTheme.colorRed);
          }
        }
        update();
      },
      onError: (ResponseModel error) {
        if (page == 1) {
          leadMasterList?.clear();
        }
        handleApiLeadSearchError(error);
      },
    );
  }

  addMasterLeadNotes(int? leadMasterId, String? notes) {
    isLoading = true;
    ticketSearchOptionList?.clear();
    update();
    LeadSystemProvider().addNotesLeadMaster(
      leadMasterId: leadMasterId,
      notes: notes,
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
                getLeadManagement();
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  getLeadAllRejectedReason(BuildContext context, String? status,
      LeadMasterListData? item, ViewLeadController? controller) {
    isLoading = true;
    rejectedReasonList?.clear();
    selectedRejectedReason = null;
    update();
    LeadSystemProvider().allLeadRejectedReason(
      onSuccess: (ResponseModel responseModel) {
        isLoading = false;
        update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              LeadAllRejectedReasonRes responseData =
                  LeadAllRejectedReasonRes.fromJson(map);
              if ((responseData.responseCode != null &&
                      responseData.responseCode == 200) ||
                  (responseData.status != null && responseData.status == 200)) {
                if (responseData.rejectReasonList!.isNotEmpty) {
                  rejectedReasonList = responseData.rejectReasonList;
                }
                if (status!.equalsIgnoreCase(Strings.approve)) {
                  addRemarkLeadDialog(
                      context, Strings.approve, item, controller);
                } else {
                  addRemarkLeadDialog(
                      context, Strings.reject, item, controller);
                }
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
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

  approveRejectStaffLead(
      {required String? status,
      required String? remark,
      required BuildContext context,
      LeadMasterListData? item}) {
    isLoading = true;
    update();
    LeadApproveRejectReq request = LeadApproveRejectReq(
      approveRequest: status!.equalsIgnoreCase(Strings.approve) ? true : false,
      buId: item!.buId,
      currentLoggedInStaffId: userDetail!.userId,
      firstname: item.firstname,
      id: item.id,
      mvnoId: userDetail!.mvnoId,
      remark: remark,
      serviceareaid: item.serviceareaid,
      flag: status.equalsIgnoreCase(Strings.approve) ? "Approve" : "Reject",
      nextTeamMappingId: item.nextTeamMappingId,
      status: item.leadStatus,
      username: item.username,
      rejectedReasonMasterId: status.equalsIgnoreCase(Strings.reject)
          ? selectedRejectedReason!.id
          : "",
    );

    log("LeadApproveRejectReq>>>>${jsonEncode(request)}");

    LeadSystemProvider().approveRejectLeads(
      request: request,
      onSuccess: (responseModel) {
        isLoading = false;
        update();
        // print("ResponseModel Data ==> ${jsonEncode(responseModel.data)}");
        try {
          // Map<String, dynamic> map = responseModel.data;
          LeadApproveRejectStaffRes responseData =
              LeadApproveRejectStaffRes.fromJson(responseModel);
          if ((responseData.status != null && responseData.status == 200) ||
              (responseData.responseCode != null &&
                  responseData.responseCode == 200)) {
            if (responseData.data != null &&
                responseData.data == "FINAL_APPROVED") {
              openCreateAddLeadScreen(Strings.lead_caf, item, status);
            } else if (responseData.data != null &&
                responseData.data == "FINAL_REJECTED") {
              Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                  AppTheme.colorWhite, AppTheme.colorGreen);
              clearFilter();
            } else if (responseData.data == null) {
              if (responseData.dataList != null &&
                  responseData.dataList!.isNotEmpty) {
                approveRejectStaffLeadList?.clear();
                approveRejectStaffLeadList?.addAll(responseData.dataList!);
                showAssignStaffDialog(responseData.dataList!, status, request);
                // showAssignStaffDialog(
                //     responseData.dataList!, status, request);
              } else {
                if (status.equalsIgnoreCase(Strings.reject)) {
                  Utils.showSnackbar(Strings.SUCCESS, "Reject Successfully.",
                      AppTheme.colorWhite, AppTheme.colorGreen);
                  clearFilter();
                } else if (status.equalsIgnoreCase(Strings.approve)) {
                  Utils.showSnackbar(Strings.SUCCESS, "Approved Successfully.",
                      AppTheme.colorWhite, AppTheme.colorGreen);
                }
                // Get.back(result: true);
                // getCreditNoteListData();
              }
            }
          }
        } on Exception catch (e) {
          print(e.toString());
        }
        update();
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  assignLeadStaffNote(ApproveRejectStaffLeadList? selectedItem,
      bool? isApproveRequest, LeadApproveRejectReq? request) {
    LeadApproveRejectReq req = LeadApproveRejectReq();
    if (selectedRejectedReason != null) {
      req.rejectedReasonMasterId = selectedRejectedReason!.id;
    } else {
      req.rejectedReasonMasterId = null;
    }
    req = request!;

    log("LeadApproveRejectReq>>>>>>${jsonEncode(req)}");

    String apiUrl =
        "${UrlConstants.assignFromStaffListForLead}?eventName=LEAD&nextAssignStaff=${selectedItem!.id}";

    log("assignFromStaffListForLead==>$apiUrl");
    isLoading = true;
    update();
    LeadSystemProvider().assignLeadStaff(
      url: apiUrl,
      request: req,
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
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage!,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                getLeadManagement();
              } else {
                Utils.showSnackbar(Strings.INFO, responseData.responseMessage!,
                    AppTheme.colorWhite, AppTheme.colorBlueRView);
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

  assignEveryStaffLead({int? entityId, bool? isApprovedRequest}) {
    String apiUrl =
        "${UrlConstants.creditNote_assign_every_staff}?entityId=$entityId&eventName=LEAD&isApproveRequest=$isApprovedRequest";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.result != null) {
          try {
            Map<String, dynamic> map = responseModel.result;
            BaseResponse responseData = BaseResponse.fromJson(map);
            if ((responseData.status != null && responseData.status == 200) ||
                (responseData.responseCode != null &&
                    responseData.responseCode == 200)) {
              Utils.showSnackbar(Strings.SUCCESS, responseData.responseMessage!,
                  AppTheme.colorWhite, AppTheme.colorGreen);
            } else {
              Utils.showSnackbar(Strings.INFO, responseData.responseMessage!,
                  AppTheme.colorWhite, AppTheme.colorBlueRView);
            }
            getLeadManagement();
          } on Exception catch (e) {
            print(e.toString());
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

  getReOpenLead(int? leadId) {
    isLoading = true;
    update();
    LeadSystemProvider().reOpenLead(
      leadId: leadId,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200 ||
                  responseData.status == 200) {
                Utils.showSnackbar(Strings.SUCCESS, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorGreen);
                getLeadManagement();
              } else {
                Utils.showSnackbar(Strings.ERROR, responseData.message,
                    AppTheme.colorWhite, AppTheme.colorRed);
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
      },
      onError: (ResponseModel error) {
        handleApiError(error);
      },
    );
  }

  pickUpLead(int? entityId) {
    String apiUrl =
        "${UrlConstants.creditNote_pick_up_flow}?entityId=$entityId&eventName=LEAD";
    isLoading = true;
    update();
    ViewCreditNoteProvider().assignCreditNoteEveryStaff(
      url: apiUrl,
      onSuccess: (ResponseModel responseModel) {
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              BaseResponse responseData = BaseResponse.fromJson(map);
              if (responseData.responseCode == 200) {
                Utils.showSnackbar(
                    Strings.SUCCESS,
                    responseData.responseMessage,
                    AppTheme.colorWhite,
                    AppTheme.colorGreen);
                getLeadManagement();
              } else {
                if (responseData.responseMessage != null &&
                    responseData.responseMessage!.isNotEmpty) {
                  Utils.showSnackbar(
                      Strings.ERROR,
                      responseData.responseMessage,
                      AppTheme.colorWhite,
                      AppTheme.colorRed);
                }
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
        isLoading = false;
        update();
      },
      onError: (ResponseModel error) {
        _handleApiError(error);
      },
    );
  }

  void moveToDashboard() async {
    Get.offAllNamed(AppRoutes.DASHBOARD);
  }

  void showSnackBarDialog() {
    Utils.showSnackbar(Strings.INFO, Strings.under_development,
        AppTheme.colorWhite, AppTheme.colorGreen);
  }

  addRemarkLeadDialog(BuildContext context, String? pageName,
      LeadMasterListData? item, ViewLeadController? controller) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return LeadApproveRejectDialog(
              pageName: pageName,
              leadApproveRejectBtnAction: this,
              item: item,
              controller: controller);
        });
  }

  showAssignStaffDialog(List<ApproveRejectStaffLeadList> item,
      String? staffStatus, LeadApproveRejectReq? requestData) {
    showDialog(
        context: Get.context!,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return LeadAssignDialog(
            leadAssignAction: this,
            itemsOrgLst: item,
            request: requestData,
            // entityId: caseId,
            staffStatus: staffStatus,
            searchController: searchController,
          );
        });
  }

  @override
  void leadApproveRejectStatus(
      {String? identifier,
      TextEditingController? remarkController,
      int? caseId,
      BuildContext? context,
      LeadMasterListData? item}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      approveRejectStaffLead(
          status: Strings.approve,
          remark: remarkController!.text,
          context: context!,
          item: item);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      approveRejectStaffLead(
          status: Strings.reject,
          remark: remarkController!.text,
          context: context!,
          item: item);
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

  handleApiLeadSearchError(ResponseModel error) {
    isLoading = false;
    isShowLoadMore = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    } else if (error.statusCode == 417) {
      Utils.showSnackbar(Strings.INFO, error.message, AppTheme.colorWhite,
          AppTheme.colorBlueRView);
    } else if (!error.message!.isNullOrEmpty()) {
      Utils.showSnackbar(
          Strings.ERROR, error.message, AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  _handleApiError(ResponseModel error) {
    isLoading = false;
    if (error.statusCode == Constant.CODE_NO_INTERNET_CONNECTION) {
      Utils.showSnackbar(Strings.ERROR, Strings.no_internet,
          AppTheme.colorWhite, AppTheme.colorRed);
    }
    update();
  }

  @override
  void leadAssignBtnAction(
      {ApproveRejectStaffLeadList? selectedItem,
      bool? isStaffSelected,
      LeadApproveRejectReq? request,
      String? approveRejectStatus}) {
    Get.back();
    if (isStaffSelected == true) {
      // log("Staff is selected");
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        // log("Staff is selected!!!!!!!!=>${Strings.approve}");
        assignLeadStaffNote(selectedItem, true, request);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        // log("Staff is selected!!!!!!!!=>${Strings.reject}");
        assignLeadStaffNote(selectedItem, false, request);
      }
    } else {
      if (approveRejectStatus!.equalsIgnoreCase(Strings.approve)) {
        assignEveryStaffLead(entityId: request!.id, isApprovedRequest: true);
      } else if (approveRejectStatus.equalsIgnoreCase(Strings.reject)) {
        assignEveryStaffLead(entityId: request!.id, isApprovedRequest: false);
      }
    }
  }

  openCreateAddLeadScreen(String? from, LeadMasterListData? leadViewContentData,
      String? approveRejectStatus) async {
    var result = await Get.to(CreateLeadScreen(), arguments: {
      Constant.FROM: from,
      Constant.LEAD_DETAIL: leadViewContentData,
      Constant.LEAD_STATUS: approveRejectStatus,
    });
    if (result != null && result == true) {
      clearFilter();
    }
  }
}

class CustomerNewListRequest {
  int? page;
  int? pageSize;
  String? sortOrder; // 1 for ascending, 0 for descending
  String? filterBy;
  List<Filters>? filters;

  CustomerNewListRequest(
      {this.page, this.pageSize, this.sortOrder, this.filterBy, this.filters});

  CustomerNewListRequest.fromJson(Map<String, dynamic> json) {
    page = json['page'];
    pageSize = json['pageSize'];
    sortOrder = json['sortOrder'];
    filterBy = json['filterBy'];
    if (json['filters'] != null) {
      filters = <Filters>[];
      json['filters'].forEach((v) {
        filters!.add(new Filters.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['page'] = this.page;
    data['pageSize'] = this.pageSize;
    data['sortOrder'] = this.sortOrder;
    data['filterBy'] = this.filterBy;
    if (this.filters != null) {
      data['filters'] = this.filters!.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
