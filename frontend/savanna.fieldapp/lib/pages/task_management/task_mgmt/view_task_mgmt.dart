
import 'package:savbill/pages/change_plan/response/customer_plan_type_res.dart';
import 'package:savbill/pages/customer/customer_selection.dart';
import 'package:savbill/pages/dashboard/model/response/view_ticket_response.dart';
import 'package:savbill/pages/pending_approvals/model/response/ticket_assign_staff_res.dart';
import 'package:savbill/pages/pending_approvals/pending_approvals_provider.dart';
import 'package:savbill/pages/pending_approvals/tickets/ticket_assign_dialog.dart';
import 'package:savbill/pages/task_management/model/response/view_task_response.dart';
import 'package:savbill/pages/task_management/task_mgmt/task_detail/task_detail_screen.dart';
import 'package:savbill/pages/task_management/task_mgmt/view_task_item.dart';
import 'package:savbill/pages/task_management/task_mgmt/view_task_mgmt_controller.dart';
import 'package:savbill/pages/ticket_system/model/response/ticket_priority_res.dart';
import 'package:savbill/pages/ticket_system/ticket_management/change_ticket_priority_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_pick_dialog.dart';
import 'package:savbill/pages/ticket_system/ticket_management/ticket_status_approve_reject_dialog.dart';
import 'package:savbill/theme/app_theme.dart';
import 'package:savbill/util/Extensions.dart';
import 'package:savbill/util/constant.dart';
import 'package:savbill/util/resources.dart';
import 'package:savbill/util/strings.dart';
import 'package:savbill/util/utils.dart';
import 'package:savbill/webservices/response_model.dart';
import 'package:savbill/widgets/coustom_text.dart';
import 'package:savbill/widgets/dynamic_appbar.dart';
import 'package:savbill/widgets/input_textfield.dart';
import 'package:savbill/widgets/no_data_found.dart';
import 'package:savbill/widgets/progress_bar.dart';
import 'package:savbill/widgets/simple_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:get/get.dart';

class ViewTaskMgmt extends StatefulWidget {

  const ViewTaskMgmt({super.key});

  @override
  State<ViewTaskMgmt> createState() => _ViewTaskMgmtState();
}

class _ViewTaskMgmtState extends State<ViewTaskMgmt>
    implements
        AddFollowUpBtnAction,
        TicketAssignAction,
        TicketPickBtnAction,
        TicketPriorityBtnAction,
        TicketApproveRejectBtnAction {
  final viewTaskMgmtController = Get.put(ViewTaskMgmtController());

  Future<bool> _onWillPop() async {
    return (await _backScreen()) ?? false;
  }

  _backScreen() {
    Get.back();
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onWillPop,
      child: GetBuilder<ViewTaskMgmtController>(builder: (controller) {
        return Stack(children: [
          Scaffold(
            backgroundColor: AppTheme.colorBG,
            appBar: _appBar(),
            body: _body(),
          ),
          ProgressBar(isLoader: viewTaskMgmtController.isLoading),
        ]);
      }),
    );
  }

  _body() {
    return GestureDetector(
      onTap: () {
        FocusScope.of(context).requestFocus(FocusNode());
      },
      child: Container(
        width: MediaQuery.of(context).size.width,
        child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(
                height: Constant.SCREEN_PADDING,
              ),
              Padding(
                padding: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Row(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      CustomText(
                          title: Strings.task,
                          colors: AppTheme.colorBlack,
                          textAlign: TextAlign.start,
                          fontSize: AppTheme.medium + 1,
                          fontWeight: FontWeight.w500),
                      InkWell(
                        onTap: () {
                          if (viewTaskMgmtController.filterViewOpen) {
                            viewTaskMgmtController.filterViewOpen = false;
                          } else {
                            viewTaskMgmtController.filterViewOpen = true;
                          }
                          viewTaskMgmtController.update();
                        },
                        child: Container(
                            height: 38,
                            margin: const EdgeInsets.only(right: 0), //
                            child: Icon(
                              Icons.filter_alt_rounded,
                              color: viewTaskMgmtController.isFilterApply
                                  ? AppTheme.colorPrimary
                                  : AppTheme.colorBlack,
                              size: 32,
                            )),
                      ),
                    ]),
              ),
              const SizedBox(
                height: Constant.SMALL_PADDING,
              ),
              viewTaskMgmtController.filterViewOpen
                  ? Container(
                width: MediaQuery.of(context).size.width,
                margin: const EdgeInsets.symmetric(
                    horizontal: Constant.SCREEN_PADDING),
                child: Material(
                  color: AppTheme.colorWhite,
                  elevation: 1.5,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(
                          Constant.BTN_ROUNDED_CORNER - 2)),
                  child: Padding(
                    padding: const EdgeInsets.all(Constant.SMALL_PADDING),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        DropdownButtonHideUnderline(
                          child: DropdownButtonFormField(
                            icon: SvgPicture.asset(
                              downArrowSvg,
                              height: Constant.DROP_DOWN_ARROW_W_H,
                              width: Constant.DROP_DOWN_ARROW_W_H,
                              color: AppTheme.colorBlack,
                              fit: BoxFit.fill,
                            ),
                            decoration: Utils.ddlDecoration(),
                            hint: Align(
                              alignment: Alignment.centerLeft,
                              child: Text(
                                Strings.select_search_option,
                                style: TextStyle(
                                  fontSize: AppTheme.medium,
                                  color: AppTheme.colorIconGrey,
                                  fontFamily: AppTheme.appFontName,
                                ),
                              ),
                            ),
                            style: AppTheme.dropdownTextStyle,
                            isExpanded: true,
                            isDense: true,
                            value:
                            viewTaskMgmtController.selectSearchOption,
                            items: viewTaskMgmtController
                                .ticketSearchOptionList!
                                .map((PlanTypeDetail value) {
                              return DropdownMenuItem<PlanTypeDetail>(
                                value: value,
                                child: Text(value.text!),
                              );
                            }).toList(),
                            onChanged: (value) {
                              viewTaskMgmtController.selectSearchOption =
                              value as PlanTypeDetail?;
                              viewTaskMgmtController.update();
                            },
                            validator: (value) {
                              return null;
                            },
                          ),
                        ),
                        // DropdownButtonHideUnderline(
                        //   child: DropdownButtonFormField(
                        //     icon: SvgPicture.asset(
                        //       downArrowSvg,
                        //       height: Constant.DROP_DOWN_ARROW_W_H,
                        //       width: Constant.DROP_DOWN_ARROW_W_H,
                        //       color: AppTheme.colorBlack,
                        //       fit: BoxFit.fill,
                        //     ),
                        //     decoration: Utils.ddlDecoration(),
                        //     hint: Align(
                        //       alignment: Alignment.centerLeft,
                        //       child: Text(
                        //         Strings.ticket_problem_domain,
                        //         style: TextStyle(
                        //           fontSize: AppTheme.medium,
                        //           color: AppTheme.colorIconGrey,
                        //           fontFamily: AppTheme.appFontName,
                        //         ),
                        //       ),
                        //     ),
                        //     style: AppTheme.dropdownTextStyle,
                        //     isExpanded: true,
                        //     isDense: true,
                        //     value: viewTaskMgmtController.selProblemDomain,
                        //     items: viewTaskMgmtController.problemDomainList!
                        //         .map((ProblemDomainDetail value) {
                        //       return DropdownMenuItem<
                        //           ProblemDomainDetail>(
                        //         value: value,
                        //         child: Text(value.categoryName!),
                        //       );
                        //     }).toList(),
                        //     onChanged: (value) {
                        //       viewTaskMgmtController.selProblemDomain =
                        //           value as ProblemDomainDetail?;
                        //       viewTaskMgmtController.update();
                        //     },
                        //     validator: (value) {
                        //       return null;
                        //     },
                        //   ),
                        // ),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        CoustomTextField(
                            labelText: Strings.enter_search_detail,
                            hintColor: AppTheme.colorIconGrey,
                            textEditingController: viewTaskMgmtController
                                .searchDetailController,
                            borderEnableColors: AppTheme.colorIconGrey,
                            borderFocusColors: AppTheme.colorIconGrey,
                            textColor: AppTheme.colorBlack,
                            keyboardType: TextInputType.emailAddress,
                            fontSize: AppTheme.small,
                            textInputAction: TextInputAction.next,
                            fontWeight: FontWeight.w500,
                            contentPadding: const EdgeInsets.symmetric(
                                horizontal: Constant.MEDIUM_PADDING,
                                vertical: Constant.MEDIUM_PADDING),
                            borderCorner: Constant.BTN_ROUNDED_CORNER,
                            onTextValidator: (String? value) {
                              if (value!.isEmpty) {
                                return Strings.please_enter_value;
                              }
                              return null;
                            },
                            onTextFiledOnTap: () {},
                            readOnly: false),

                        // DropdownButtonHideUnderline(
                        //   child: DropdownButtonFormField(
                        //     icon: SvgPicture.asset(
                        //       downArrowSvg,
                        //       height: Constant.DROP_DOWN_ARROW_W_H,
                        //       width: Constant.DROP_DOWN_ARROW_W_H,
                        //       color: AppTheme.colorBlack,
                        //       fit: BoxFit.fill,
                        //     ),
                        //     decoration: Utils.ddlDecoration(),
                        //     hint: Align(
                        //       alignment: Alignment.centerLeft,
                        //       child: Text(
                        //         Strings.service_area,
                        //         style: TextStyle(
                        //           fontSize: AppTheme.medium,
                        //           color: AppTheme.colorIconGrey,
                        //           fontFamily: AppTheme.appFontName,
                        //         ),
                        //       ),
                        //     ),
                        //     style: AppTheme.dropdownTextStyle,
                        //     isExpanded: true,
                        //     isDense: true,
                        //     value:
                        //         viewTaskMgmtController.selectedServicesArea,
                        //     items: viewTaskMgmtController.servicesAreaList!
                        //         .map((ServicesAreaDetail value) {
                        //       return DropdownMenuItem<ServicesAreaDetail>(
                        //         value: value,
                        //         child: Text(value.name!),
                        //       );
                        //     }).toList(),
                        //     onChanged: (value) {
                        //       viewTaskMgmtController.selectedServicesArea =
                        //           value as ServicesAreaDetail?;
                        //       viewTaskMgmtController.update();
                        //     },
                        //     validator: (value) {
                        //       return null;
                        //     },
                        //   ),
                        // ),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        // DropdownButtonHideUnderline(
                        //   child: DropdownButtonFormField(
                        //     icon: SvgPicture.asset(
                        //       downArrowSvg,
                        //       height: Constant.DROP_DOWN_ARROW_W_H,
                        //       width: Constant.DROP_DOWN_ARROW_W_H,
                        //       color: AppTheme.colorBlack,
                        //       fit: BoxFit.fill,
                        //     ),
                        //     decoration: Utils.ddlDecoration(),
                        //     hint: Align(
                        //       alignment: Alignment.centerLeft,
                        //       child: Text(
                        //         Strings.case_status,
                        //         style: TextStyle(
                        //           fontSize: AppTheme.medium,
                        //           color: AppTheme.colorIconGrey,
                        //           fontFamily: AppTheme.appFontName,
                        //         ),
                        //       ),
                        //     ),
                        //     style: AppTheme.dropdownTextStyle,
                        //     isExpanded: true,
                        //     isDense: true,
                        //     value:
                        //         viewTaskMgmtController.selectedCaseStatus,
                        //     items: viewTaskMgmtController.caseStatusList!
                        //         .map((CaseStatusDetail value) {
                        //       return DropdownMenuItem<CaseStatusDetail>(
                        //         value: value,
                        //         child: Text(value.text!),
                        //       );
                        //     }).toList(),
                        //     onChanged: (value) {
                        //       viewTaskMgmtController.selectedCaseStatus =
                        //           value as CaseStatusDetail?;
                        //       viewTaskMgmtController.update();
                        //     },
                        //     validator: (value) {
                        //       return null;
                        //     },
                        //   ),
                        // ),
                        const SizedBox(
                          height: Constant.SMALL_PADDING,
                        ),
                        Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Expanded(
                                child: SimpleButton(
                                  onTap: () {
                                    viewTaskMgmtController.applyFilter();
                                  },
                                  radius: Constant.BTN_HEIGHT_M,
                                  height: Constant.BTN_HEIGHT_M,
                                  bgColors: AppTheme.colorPrimary,
                                  child: CustomText(
                                    title: Strings.apply,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                              const SizedBox(
                                width: Constant.LARGE_PADDING,
                              ),
                              Expanded(
                                child: SimpleButton(
                                  onTap: () {
                                    viewTaskMgmtController.clearFilter();
                                  },
                                  radius: Constant.BTN_HEIGHT_M,
                                  height: Constant.BTN_HEIGHT_M,
                                  bgColors: AppTheme.colorBlack,
                                  borderColors: AppTheme.colorBlack,
                                  child: CustomText(
                                    title: Strings.clear,
                                    fontSize: AppTheme.medium,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                              ),
                            ]),
                      ],
                    ),
                  ),
                ),
              )
                  : Container(),
              viewTaskMgmtController.filterViewOpen
                  ? const SizedBox(
                height: Constant.MEDIUM_PADDING,
              )
                  : Container(),
              Expanded(
                flex: 1,
                child: (viewTaskMgmtController.taskList != null &&
                    viewTaskMgmtController.taskList!.isNotEmpty)
                    ? Padding(
                  padding: const EdgeInsets.symmetric(
                      horizontal: Constant.SCREEN_PADDING),
                  child: ListView.builder(
                      controller: viewTaskMgmtController.scrollController,
                      scrollDirection: Axis.vertical,
                      itemCount:
                      viewTaskMgmtController.taskList!.length + 1,
                      itemBuilder: (context, index) {
                        if (index ==
                            viewTaskMgmtController.taskList?.length) {
                          if (viewTaskMgmtController.isShowLoadMore) {
                            return Padding(
                              padding: const EdgeInsets.all(
                                  Constant.SMALL_PADDING),
                              child: Center(
                                child: SizedBox(
                                  width: Constant.SCREEN_PADDING,
                                  height: Constant.SCREEN_PADDING,
                                  child: CircularProgressIndicator(
                                    strokeWidth: 2.5,
                                    valueColor:
                                    AlwaysStoppedAnimation<Color>(
                                        AppTheme.colorProgress),
                                    backgroundColor:
                                    AppTheme.colorProgressBg,
                                  ),
                                ),
                              ),
                            );
                          } else {
                            return Container();
                          }
                        } else {
                          ViewTaskDataList item =
                          viewTaskMgmtController.taskList![index];
                          viewTaskMgmtController.assignStaffParentId =
                              item.currentAssigneeId;
                          return InkWell(
                            // onTap: () {
                            //   openTicketDetailScreen(item.caseId);
                            // },
                            child: ViewTaskItem(
                              item: item,
                              showActionBtn: true,
                              forSelection: false,
                              controller: viewTaskMgmtController,
                              userid: viewTaskMgmtController
                                  .userDetail!.userId!,
                              onTapTicketDetail: () {
                                openTaskDetailScreen(item.caseId);
                              },
                              onTapCustomerDetail: () {
                                openTaskDetailScreen(item.caseId);
                                // ticketCustomerDetail(item.customersId);
                              },
                              onTapStaffDetail: () {
                                if (item.currentAssigneeId != null) {
                                  viewTaskMgmtController
                                      .getTicketStaffDetail(
                                      item.currentAssigneeId!);
                                }
                              },
                              onTapEdit: () {
                                // viewTaskMgmtController.addEditTicketScreen(
                                //     Strings.edit, item);
                              },

                              onTapPick: () {
                                showTicketPickDialog(item);
                              },

                            ),
                          );
                        }
                      }),
                )
                    : noDataFound(),
              ),
              Row(
                children: [
                  Expanded(
                      child: SimpleButton(
                        onTap: () {
                          viewTaskMgmtController.addEditTaskScreen(
                              Strings.add, null);
                        },
                        radius: 0,
                        height: Constant.BOTTOM_BTN_HEIGHT,
                        bgColors: AppTheme.colorPrimary,
                        borderColors: AppTheme.colorPrimary,
                        child: CustomText(
                          title: Strings.create_task,
                          fontSize: AppTheme.medium,
                          fontWeight: FontWeight.w400,
                        ),
                      ))
                ],
              )
            ]),
      ),
    );
  }

  // openCaseAssignScreen(TicketDetail ticketDetail) async {
  //   bool chkRefresh = await Get.to(CaseAssign(), arguments: {
  //     Constant.TICKET_DETAIL: ticketDetail,
  //   });
  //
  //   if (chkRefresh) {
  //     viewTaskMgmtController.clearFilter();
  //   }
  // }

  openTaskDetailScreen(int? taskId) async {
    Get.to(TaskDetailScreen(), arguments: {
      Constant.TASK_ID: taskId,
    });
  }
  //
  // ticketCustomerDetail(int? customerId) async {
  //   Get.to(TicketCustomerDetail(), arguments: {
  //     Constant.CUSTOMER_DETAIL: customerId,
  //   });
  // }

  // showFollowUpPopup(ViewTaskDataList ticketDetail) {
  //   String title = "Ticket Comment (${ticketDetail.caseNumber})";
  //   viewTaskMgmtController.remarksController.clear();
  //   viewTaskMgmtController.update();
  //   showDialog(
  //       context: context,
  //       barrierDismissible: false,
  //       builder: (BuildContext context) {
  //         return AddFollowUpDialog(
  //             addFollowUpBtnAction: this,
  //             caseDetail: ticketDetail,
  //             title: title);
  //       });
  // }

  showTicketPickDialog(ViewTaskDataList ticketDetail) {
    bool showPopup = false;
    if (ticketDetail.ticketAssignStaffMappings != null &&
        ticketDetail.ticketAssignStaffMappings!.isNotEmpty) {
      ticketDetail.ticketAssignStaffMappings!.forEach((element) {
        if (element.staffId == viewTaskMgmtController.userDetail!.userId) {
          showPopup = true;
        }
      });
    }
    if (!showPopup) {
      Utils.showSnackbar(
          Strings.INFO,
          "You are not eligible to pick this ticket..",
          AppTheme.colorBlack,
          AppTheme.colorBlueRView);
      return;
    }

    viewTaskMgmtController.selectedTicket = ticketDetail;
    viewTaskMgmtController.update();
    showDialog(
        context: context,
        barrierDismissible: false,
        builder: (BuildContext context) {
          return TicketPickDialog(
            ticketPickBtnAction: this,
            from: Strings.pick_ticket,
          );
        });
  }

  // showTicketPriorityDialog(ViewTaskDataList? ticketDetail) {
  //   if (viewTaskMgmtController.ticketPriorityList != null &&
  //       viewTaskMgmtController.ticketPriorityList!.isNotEmpty) {
  //     showDialog(
  //         context: context,
  //         barrierDismissible: false,
  //         builder: (BuildContext context) {
  //           return ChangeTicketPriorityDialog(
  //               ticketPriorityBtnAction: this,
  //               ticketDetail: ticketDetail,
  //               priorityList: viewTaskMgmtController.ticketPriorityList!);
  //         });
  //   }
  // }

  // showSLATimeCounterDialog(ViewTaskDataList ticketDetail) {
  //   showDialog(
  //       context: context,
  //       barrierDismissible: false,
  //       builder: (BuildContext context) {
  //         return TicketSLATimeCounterDialog(ticketDetail: ticketDetail);
  //       });
  // }

  // openLinkTicketScreen(TicketDetail ticketDetail) async {
  //   var result = Get.to(LinkTicket(), arguments: {
  //     Constant.TICKET_DETAIL: ticketDetail,
  //   });
  //   if (result != null && result == true) {
  //     viewTaskMgmtController.clearFilter();
  //   }
  // }

  // openTicketDocumentUploadScreen(TicketDetail ticketDetail) async {
  //   Get.to(UploadDocumentScreen(), arguments: {
  //     Constant.TICKET_ID: ticketDetail.caseId,
  //   });
  // }

  /*openTicketChangeStatusScreen(ViewTaskDataList ticketDetail) async {
    var result = await Get.to(TicketChangeStatusScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });

    if (result != null && result == true) {
      viewTaskMgmtController.clearFilter();
    }
  }

  onTicketETRScreen(ViewTaskDataList ticketDetail) async {
    var result = Get.to(TicketETRScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      viewTaskMgmtController.getAllProblemDomain();
    }
  }

  onTicketRemarkScreen(ViewTaskDataList ticketDetail) async {
    var result = Get.to(TicketRemarkScreen(), arguments: {
      Constant.TICKET_DETAIL: ticketDetail,
    });
    if (result != null && result == true) {
      viewTaskMgmtController.getAllProblemDomain();
    }
  }*/

  noDataFound() {
    return const NoDataFound();
  }

  _appBar() {
    return DynamicAppBar(
        Strings.task_management,
        '',
        AppTheme.colorPrimary,
        false,
        _backScreen,
        [],
        AppBar().preferredSize.height);
  }

  // @override
  // void followUpBtnAction(
  //     {String? identifier, ViewTaskDataList? caseDetail, String? remarks}) {
  //   Get.back();
  //   viewTaskMgmtController.caseFollowUpApiCall(caseDetail, remarks!);
  // }

  getTicketStaff(ViewTaskDataList ticketDetail, bool isApproveRequest) {
    viewTaskMgmtController.isLoading = true;
    viewTaskMgmtController.update();
    PendingApprovalsProvider().getAssignTicketStaff(
      caseId: ticketDetail.caseId!,
      isApproveRequest: isApproveRequest,
      onSuccess: (ResponseModel responseModel) {
        viewTaskMgmtController.isLoading = false;
        viewTaskMgmtController.update();
        if (responseModel.statusCode == 200) {
          if (responseModel.result != null) {
            try {
              Map<String, dynamic> map = responseModel.result;
              TicketAssignStaffRes responseData =
              TicketAssignStaffRes.fromJson(map);
              if (isApproveRequest) {
                if ((responseData.status != null &&
                    responseData.status == 200) ||
                    (responseData.responseCode != null &&
                        responseData.responseCode == 200)) {
                  if (responseData.dataList != null &&
                      responseData.dataList!.isNotEmpty) {
                    viewTaskMgmtController.selectedTicket = ticketDetail;
                    viewTaskMgmtController.update();
                    showAssignStaffDialog(responseData.dataList!);
                  } else {
                    Utils.showSnackbar(Strings.INFO, "Staff data not available",
                        AppTheme.colorWhite, AppTheme.colorBlueRView);
                  }
                }
              } else {
                if ((responseData.status != null &&
                    responseData.status == 200) ||
                    (responseData.responseCode != null &&
                        (responseData.responseCode == 200 ||
                            responseData.responseCode == 0))) {
                  Utils.showSnackbar(
                      Strings.SUCCESS,
                      "Ticket reject successfully.",
                      AppTheme.colorWhite,
                      AppTheme.colorGreen);
                  viewTaskMgmtController.clearFilter();
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
        viewTaskMgmtController.update();
      },
      onError: (ResponseModel error) {
        viewTaskMgmtController.handleApiError(error);
      },
    );
  }

  showAssignStaffDialog(List<TicketAssignStaff> item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketAssignDialog(
              ticketAssignAction: this, itemsOrgLst: item);
        });
  }

  @override
  // void ticketAssignBtnAction({TicketAssignStaff? selectedItem}) {
  void ticketAssignBtnAction(
      {TicketAssignStaff? selectedItem,
        bool? isStaffSelected,
        String? approveRejectStatus}) {
    Get.back();
    if (selectedItem != null && viewTaskMgmtController.selectedTicket != null) {
      viewTaskMgmtController.assignTicket(selectedItem.id!);
    }
  }

  @override
  void ticketPickBtnAction({String? identifier, String? remark}) {
    Get.back();
    if (remark != null &&
        remark.isNotEmpty &&
        viewTaskMgmtController.selectedTicket != null) {
      viewTaskMgmtController.pickTicket(remark.trim());
    }
  }

  // @override
  // void ticketPriorityBtnAction(
  //     {TicketPriority? priority, ViewTaskDataList? ticketDetail}) {
  //   Get.back();
  //
  //   if (priority != null && ticketDetail != null) {
  //     // call update api call
  //     viewTaskMgmtController.changePriorityTicket(priority, ticketDetail);
  //   }
  // }

  addRemarkTicketDialog(
      BuildContext context, String? pageName, ViewTaskDataList item) {
    showDialog(
        context: context,
        barrierDismissible: true,
        builder: (BuildContext context) {
          return TicketApproveRejectDialog(
            pageName: pageName,
            ticketApproveRejectBtnAction: this,
            caseId: item.caseId,
          );
        });
  }

  @override
  void ticketApproveRejectStatus(
      {String? identifier,
        TextEditingController? remarkController,
        int? caseId}) {
    Get.back();
    if (identifier != null && identifier.equalsIgnoreCase(Strings.approve)) {
      viewTaskMgmtController.approveRejectTicket(
          status: Strings.approve.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    } else if (identifier != null &&
        identifier.equalsIgnoreCase(Strings.reject)) {
      viewTaskMgmtController.approveRejectTicket(
          status: Strings.reject.toLowerCase(),
          remark: remarkController!.text,
          caseId: caseId,
          context: context);
    }
  }

  @override
  void followUpBtnAction({String? identifier, TicketDetail? caseDetail, String? remarks}) {
    // TODO: implement followUpBtnAction
  }

  @override
  void ticketPriorityBtnAction({TicketPriority? priority, TicketDetail? ticketDetail}) {
    // TODO: implement ticketPriorityBtnAction
  }
}